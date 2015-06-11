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

import org.organicdesign.fp.collections.ImList;
import org.organicdesign.fp.collections.ImMap;
import org.organicdesign.fp.collections.ImMapOrdered;
import org.organicdesign.fp.collections.ImSet;
import org.organicdesign.fp.collections.ImSetOrdered;
import org.organicdesign.fp.collections.PersistentHashMap;
import org.organicdesign.fp.collections.PersistentHashSet;
import org.organicdesign.fp.collections.PersistentTreeMap;
import org.organicdesign.fp.collections.PersistentTreeSet;
import org.organicdesign.fp.collections.PersistentVector;
import org.organicdesign.fp.function.Function1;
import org.organicdesign.fp.function.Function2;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 Represents transformations to be carried out on a collection.  This class also implements the
 methods defined in Realizable so that sub-classes can just implement foldLeft and not have to
 worry about any Realizable functions.
 @param <T>
 */
public interface Transformable<T> extends Realizable<T> {
    /**
     Removes the first numItems from the beginning of this Transformable.
     Note that dropped items will be evaluated as they are dropped and any side effects
     (including delays) caused by evaluating these items will be incurred.  For this reason,
     you should always drop as early in your chain of functions as practical.
     @param numItems the number of items at the beginning of this Transformable to ignore
     @return a Transformable with the specified number of items ignored.
     */
    Transformable<T> drop(long numItems);

    /**
     Lazily applies the given function to each item in the underlying data source, and returns
     a Transformable with one item for each result.
     @return a Transformable of the same size as the input (may contain duplicates) containing the
     return values of the given function in the same order as the input values.
      * @param func a function that returns a new value for any value in the input
     */
    <U> Transformable<U> map(Function1<? super T,? extends U> func);

    /**
     Lazily applies the filter function to the underlying data source and returns a new Transformable
     containing only the items for which the filter returned true
     @return a Transformable of only the filtered items.
      * @param predicate a function that returns true for items to keep, false for items to drop
     */
    Transformable<T> filter(Function1<? super T,Boolean> predicate);

    /**
     Eagerly processes the entire data source for side effects.
     @param consumer the function to do the processing
     @return the unmodified sequence you started with (for chaining).
     */
    Transformable<T> forEach(Function1<? super T,?> consumer);

//    /**
//     Deprecated: use filter(...).head() instead.
//     Eagerly returns the first item matching the given predicate.
//     @param pred the test that the item needs to pass
//     @return the first item that passes the test, or null if no such item is found
//     */
//    @Deprecated
//    Option<T> firstMatching(Predicate<T> pred);

    /**
     Shorten this Transformable to contain no more than the specified number of items.
     @param numItems the maximum number of items in the returned view.
     @return a Transformable containing no more than the specified number of items.
     */
    Transformable<T> take(long numItems);

    /**
     Shorten this transformable to contain all items from the beginning so long as they satisfy the
     predicate.
     @return a lazy transformable containing the longest un-interrupted run of items, from the
     beginning of the transformable, that satisfy the given predicate.  This could be 0 items to
     the entire transformable.
      * @param predicate the test.
     */
    Transformable<T> takeWhile(Function1<? super T,Boolean> predicate);

// View and Sequence cannot inherit from these because because function arguments are contravariant.  It's OK for
// View to return a View and Sequence to return a Sequence because they are subclasses of Transformable and if
// a method returns a T, then sub-classes can return a T or "? extends T".
// But if a method takes an argument of T, then a sub-class can only take an argument of T or "? super T".
// Transformable does not provide a way to get to the first argument or the "rest".  It seems kind of a useless
// interface if the persistent Sequence is fast enough to get rid of the View.
//    /** Add the given Transformable after the end of this one. */
//    Transformable<T> concat(Transformable<T> other);
//
//    /** Add the given Transformable before the beginning of this one. */
//    Transformable<T> precat(Transformable<T> other);


    // TODO: You can always use foldLeft for this operation.  Does having reduceLeft add more clarity to the underlying code, or does it provide some useful additional functionality?
//    /**
//     Eagerly process entire data source.  This is an extremely powerful method, being the only one
//     that currently can produce more output items than input items (flatMap would do that too
//     if implemented).
//     @return
//     @param fun Starting with the first two elements of the list, combines each value in the list with the result so far.  The initial result is u.
//     */
//    T reduceLeft(BiFunction<T, T, T> fun);

    /**
     One of the two higher-order functions that can produce more output items than input items
     (when u is a collection). FlatMap is the other, but foldLeft is eager while flatMap is lazy.
     FoldLeft can also produce a single (scalar) value.  In that form, it is often called reduce().

     @return an eagerly evaluated result which could be a single value like a sum, or a collection.
      * @param u the accumulator and starting value.  This will be passed to the function on the
      first iteration to be combined with the first member of the underlying data source.  For some
      operations you'll need to pass an identity, e.g. for a sum, pass 0, for a product, pass 1 as
      this parameter.
     * @param fun combines each value in the list with the result so far.  The initial result is u.
     */
    <U> U foldLeft(U u, Function2<U,? super T,U> fun);

    /**
     A form of foldLeft() that handles early termination.
     If foldLeft replaces a loop, and return
     is a more general form of break, then this can do anything a loop can do.  If you want to
     terminate based on an input T value rather than an output U, make U = Tuple2(T,V) and have
     terminateWith(Tuple2(T,V) tv) { if tv._1()... }

     @return an eagerly evaluated result which could be a single value like a sum, or a collection.
     @param u the accumulator and starting value.  This will be passed to the function on the
     first iteration to be combined with the first member of the underlying data source.  For some
     operations you'll need to pass an identity, e.g. for a sum, pass 0, for a product, pass 1 as
     this parameter.
     @param fun combines each value in the list with the result so far.  The initial result is u.
     @param terminateWhen returns true when the termination condition is reached and will stop
     processing the input at that time, returning the latest u.
     */
    <U> U foldLeft(U u, Function2<U,? super T,U> fun, Function1<? super U,Boolean> terminateWhen);

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
//    <U> Transformable<U> flatMap(Function<T,? extends Transformable<U>> func);

    /** {@inheritDoc} */
    @Override default List<T> toJavaList() {
        return foldLeft(new ArrayList<>(), (ts, t) -> {
            ts.add(t);
            return ts;
        });
    }

    /** {@inheritDoc} */
    @Override default ImList<T> toImList() { return foldLeft(PersistentVector.empty(), (ts, t) -> ts.appendOne(t)); }

    /** {@inheritDoc} */
    @Override default <U,V> Map<U,V> toJavaMap(final Function1<? super T,Map.Entry<U,V>> f1) {
        return foldLeft(new HashMap<>(), (ts, t) -> {
            Map.Entry<U,V> entry = f1.apply(t);
            ts.put(entry.getKey(), entry.getValue());
            return ts;
        });
    }

    /** {@inheritDoc} */
    @Override default <U,V> SortedMap<U,V> toJavaMapSorted(final Function1<? super T,Map.Entry<U,V>> f1) {
        return foldLeft(new TreeMap<>(), (ts, t) -> {
            Map.Entry<U,V> entry = f1.apply(t);
            ts.put(entry.getKey(), entry.getValue());
            return ts;
        });
    }

//    @Override
//    default <U,V> ImMap<U,V> toImMap(Function1<T,Map.Entry<U,V>> f1) {
//        return un(toJavaHashMap(f1));
//    }
    /** {@inheritDoc} */
    @Override
    default <U,V> ImMap<U,V> toImMap(Function1<? super T,Map.Entry<U,V>> f1) {
        return foldLeft((ImMap<U, V>) PersistentHashMap.<U, V>empty(),
                        (ts, t) -> ts.assoc(f1.apply(t)));
    }

    /** {@inheritDoc} */
    @Override
    default <U,V> ImMapOrdered<U,V> toImMapOrdered(Comparator<? super U> comp, Function1<? super T,Map.Entry<U,V>> f1) {
        return foldLeft((ImMapOrdered<U, V>) PersistentTreeMap.<U, V>empty(comp),
                        (ts, t) -> ts.assoc(f1.apply(t)));
    }

    /** {@inheritDoc} */
    @Override default SortedSet<T> toJavaSetSorted(Comparator<? super T> comparator) {
        return foldLeft(new TreeSet<>(comparator), (ts, t) -> {
            ts.add(t);
            return ts;
        });
    }

    /** {@inheritDoc} */
    @Override default ImSet<T> toImSet() {
        return foldLeft(PersistentHashSet.empty(), (accum, t) -> accum.put(t));
    }

    /** {@inheritDoc} */
    @Override default ImSetOrdered<T> toImSetOrdered(Comparator<? super T> comparator) {
        return foldLeft(PersistentTreeSet.ofComp(comparator), (accum, t) -> accum.put(t));
    }

    /** {@inheritDoc} */
    @Override default Set<T> toJavaSet() {
        return foldLeft(new HashSet<>(), (ts, t) -> {
            ts.add(t);
            return ts;
        });
    }
}
