// Copyright 2014-02-15 PlanBase Inc. & Glen Peterson
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.organicdesign.fp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.organicdesign.fp.function.BiFunction;
import org.organicdesign.fp.function.Consumer;
import org.organicdesign.fp.function.Function;
import org.organicdesign.fp.function.Predicate;

/**
 Represents transformations to be carried out on a collection.  This class also implements the
 methods defined in Realizable so that sub-classes can just implement foldLeft and not have to
 worry about any Realizable functions.
 @param <T>
 */
public abstract class Transformable<T> implements Realizable<T> {

    /**
     Please do not call this method directly because this abstract class may be turned into an
     interface.
     */
    protected Transformable() {};

    /**
     Lazily applies the given function to each item in the underlying data source, and returns
     a View with one item for each result.
     @param func a function that returns a new value for any value in the input
     @return a lazy view of the same size as the input (may contain duplicates) containing the
     return values of the given function in the same order as the input values.
     */
    public abstract <U> Transformable<U> map(Function<T,U> func);

    /**
     Lazily applies the filter function to the underlying data source and returns a new view
     containing only the items for which the filter returned true
     @param func a function that returns true for items to keep, false for items to drop
     @return a lazy view of only the filtered items.
     */
    public abstract Transformable<T> filter(Predicate<T> func);

    /**
     Eagerly processes the entire data source for side effects.
     @param se the function to do the processing
     */
    public abstract void forEach(Consumer<T> se);

    /**
     Eagerly returns the first item matching the given predicate.
     @param pred the test that the item needs to pass
     @return the first item that passes the test, or null if no such item is found
     */
    public abstract T firstMatching(Predicate<T> pred);

    // TODO: You can always use foldLeft for this operation.  Does having reduceLeft add more clarity to the underlying code, or does it provide some useful additional functionality?
//    /**
//     Eagerly process entire data source.  This is an extremely powerful method, being the only one
//     that currently can produce more output items than input items (flatMap would do that too
//     if implemented).
//     @return
//     @param fun Starting with the first two elements of the list, combines each value in the list with the result so far.  The initial result is u.
//     */
//    public T reduceLeft(BiFunction<T, T, T> fun);

    /**
     One of the two higher-order functions that can produce more output items than input items
     (when u is a collection). FlatMap is the other, but foldLeft is eager while flatMap is lazy.

     @return an eagerly evaluated result which could be a single value like a sum, or a collection.
     @param u the accumulator and starting value.  This will be passed to the function on the
     first iteration to be combined with the first member of the underlying data source.  For some
     operations you'll need to pass an identity, e.g. for a sum, pass 0, for a product, pass 1 as
     this parameter.
     @param fun combines each value in the list with the result so far.  The initial result is u.
     */
    public abstract <U> U foldLeft(U u, BiFunction<U, T, U> fun);


    // Sub-classes cannot inherit from this because the function that you pass in has to know the actal return type.
    // Have to implement this independently on sub-classes.
//    /**
//     One of the two higher-order functions that can produce more output items than input items.
//     foldLeft is the other, but flatMap is lazy while foldLeft is eager.
//     @return a lazily evaluated collection which is expected to be larger than the input
//     collection.  For a collection that's the same size, map() is more efficient.  If the expected
//     return is smaller, use filter followed by map if possible, or vice versa if not.
//     @param fun yields a Transformable of 0 or more results for each input item.
//     */
//    public <U> Transformable<U> flatMap(Function<T,? extends Transformable<U>> func);

    @Override
    public ArrayList<T> toJavaArrayList() {
        return foldLeft(new ArrayList<T>(), new BiFunction<ArrayList<T>, T, ArrayList<T>>() {
            @Override
            public ArrayList<T> apply(ArrayList<T> ts, T t) throws Exception {
                ts.add(t);
                return ts;
            }
        });
    }

    @Override
    public List<T> toJavaUnmodList() {
        return Collections.unmodifiableList(toJavaArrayList());
    }

    @Override
    public <U> HashMap<T,U> toJavaHashMap(final Function<T,U> f1) {
        return foldLeft(new HashMap<T, U>(), new BiFunction<HashMap<T, U>, T, HashMap<T, U>>() {
            @Override
            public HashMap<T, U> apply(HashMap<T, U> ts, T t) throws Exception {
                ts.put(t, f1.apply(t));
                return ts;
            }
        });
    }

    @Override
    public <U> Map<T,U> toJavaUnmodMap(Function<T,U> f1) {
        return Collections.unmodifiableMap(toJavaHashMap(f1));
    }

    @Override
    public <U> HashMap<U,T> toReverseJavaHashMap(final Function<T, U> f1) {
        return foldLeft(new HashMap<U, T>(), new BiFunction<HashMap<U, T>, T, HashMap<U, T>>() {
            @Override
            public HashMap<U, T> apply(HashMap<U, T> ts, T t) throws Exception {
                ts.put(f1.apply_(t), t);
                return ts;
            }
        });
    }

    @Override
    public <U> Map<U,T> toReverseJavaUnmodMap(Function<T,U> f1) {
        return Collections.unmodifiableMap(toReverseJavaHashMap(f1));
    }

    @Override
    public TreeSet<T> toJavaTreeSet(Comparator<? super T> comparator) {
        return foldLeft(new TreeSet<T>(comparator), new BiFunction<TreeSet<T>, T, TreeSet<T>>() {
            @Override
            public TreeSet<T> apply(TreeSet<T> ts, T t) throws Exception {
                ts.add(t);
                return ts;
            }
        });
    }
    @Override
    public TreeSet<T> toJavaTreeSet() { return toJavaTreeSet(null); }


    @Override
    public SortedSet<T> toJavaUnmodSortedSet(Comparator<? super T> comparator) {
        return Collections.unmodifiableSortedSet(toJavaTreeSet(comparator));
    }
    @Override
    public SortedSet<T> toJavaUnmodSortedSet() {
        return toJavaUnmodSortedSet(null);
    }

    @Override
    public HashSet<T> toJavaHashSet() {
        return foldLeft(new HashSet<T>(), new BiFunction<HashSet<T>, T, HashSet<T>>() {
            @Override
            public HashSet<T> apply(HashSet<T> ts, T t) throws Exception {
                ts.add(t);
                return ts;
            }
        });
    }

    @Override
    public Set<T> toJavaUnmodSet() {
        return Collections.unmodifiableSet(toJavaHashSet());
    }
}
