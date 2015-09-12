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

package org.organicdesign.fp.xform;

import org.organicdesign.fp.collections.ImList;
import org.organicdesign.fp.collections.ImMap;
import org.organicdesign.fp.collections.ImSet;
import org.organicdesign.fp.collections.ImSortedMap;
import org.organicdesign.fp.collections.ImSortedSet;
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
     Add items to the end of this Transformable (precat() adds to the beginning)
     @param list the items to add
     @return a new Transformable with the items added.
     */
    Transformable<T> concat(Iterable<? extends T> list);

    /**
     Ignore the first n items and return only those that come after.
     The Xform API is designed to allow dropping items with a single pointer addition if the data
     source is a List, but that feature is not implemented yet.  For best results, drop as early in
     your chain of functions as practical.
     @param numItems the number of items at the beginning of this Transformable to ignore
     @return a Transformable with the specified number of items ignored.
     */
    Transformable<T> drop(long numItems);

    /**
     Return only the items for which the given predicate returns true.
     @return a Transformable of only the filtered items.
     @param predicate a function that returns true for items to keep, false for items to drop
     */
    Transformable<T> filter(Function1<? super T,Boolean> predicate);

    /**
     Transform each item into zero or more new items using the given function.
     One of the two higher-order functions that can produce more output items than input items.
     foldLeft is the other, but flatMap is lazy while foldLeft is eager.
     @return a lazily evaluated collection which is expected to be larger than the input
     collection.  For a collection that's the same size, map() is more efficient.  If the expected
     return is smaller, use filter followed by map if possible, or vice versa if not.
     @param f yields a Transformable of 0 or more results for each input item.
     */
    <U> Transformable<U> flatMap(Function1<? super T,Iterable<U>> f);

    /**
     Apply the function to each item, accumulating the result in u.  Other transformations can be
     implemented with just this one function, but it is clearer (and allows lazy evaluation) to use
     the most specific transformations that meet your needs.  Still, sometimes you need the
     flexibility foldLeft provides.  This implementation follows the convention that foldLeft
     processes items *in order* unless those items are a linked list, and in this case, they are
     not.

     FoldLeft is one of the two higher-order functions that can produce more output items than input
     items (when u is a collection). FlatMap is the other, but foldLeft is eager while flatMap is
     lazy. FoldLeft can also produce a single (scalar) value.  In that form, it is often called
     reduce().

     @param u the accumulator and starting value.  This will be passed to the function on the
     first iteration to be combined with the first member of the underlying data source.  For some
     operations you'll need to pass an identity, e.g. for a sum, pass 0, for a product, pass 1 as
     this parameter.
     @param fun combines each value in the list with the result so far.  The initial result is u.
     @return an eagerly evaluated result which could be a single value like a sum, or a collection.
     */
    <U> U foldLeft(U u, Function2<U,? super T,U> fun);

    /**
     Normally you want to terminate by doing a take(), drop(), or takeWhile() before you get to the
     fold, but if you need to terminate based on the complete result so far, you can  provide your
     own termination condition to this version of foldLeft().

     If foldLeft replaces a loop, and return is a more general form of break, then this function can
     do anything a loop can do.

     @param u the accumulator and starting value.  This will be passed to the function on the
     first iteration to be combined with the first member of the underlying data source.  For some
     operations you'll need to pass an identity, e.g. for a sum, pass 0, for a product, pass 1 as
     this parameter.
     @param fun combines each value in the list with the result so far.  The initial result is u.
     @param terminateWhen returns true when the termination condition is reached and will stop
     processing the input at that time, returning the latest u.
     @return an eagerly evaluated result which could be a single value like a sum, or a collection.
     */
    <U> U foldLeft(U u, Function2<U,? super T,U> fun, Function1<? super U,Boolean> terminateWhen);

    /**
     Transform each item into exactly one new item using the given function.
     @param func a function that returns a new value for any value in the input
     @return a Transformable of the same size as the input (may contain duplicates) containing the
     return values of the given function in the same order as the input values.
     */
    <U> Transformable<U> map(Function1<? super T,? extends U> func);

    /**
     Add items to the beginning of this Transformable ("precat" is a PREpending version of conCAT).
     @param list the items to add
     @return a new Transformable with the items added.
     */
    Transformable<T> precat(Iterable<? extends T> list);

    /**
     Return only the first n items.
     @param numItems the maximum number of items in the returned view.
     @return a Transformable containing no more than the specified number of items.
     */
    Transformable<T> take(long numItems);

    /**
     Return items from the beginning until the given predicate returns false.
     @param predicate the predicate (test function)
     @return a lazy transformable containing the longest un-interrupted run of items, from the
     beginning of the transformable, that satisfy the given predicate.  This could be 0 items to
     the entire transformable.
     */
    Transformable<T> takeWhile(Function1<? super T,Boolean> predicate);

    /** {@inheritDoc} */
    @Override default List<T> toMutableList() {
        return foldLeft(new ArrayList<>(), (ts, t) -> {
            ts.add(t);
            return ts;
        });
    }

    /** {@inheritDoc} */
    @Override default ImList<T> toImList() {
        return foldLeft(PersistentVector.empty(), (ts, t) -> ts.append(t));
    }

    /** {@inheritDoc} */
    @Override default <U,V> Map<U,V> toMutableMap(final Function1<? super T,Map.Entry<U,V>> f1) {
        return foldLeft(new HashMap<>(), (ts, t) -> {
            Map.Entry<U,V> entry = f1.apply(t);
            ts.put(entry.getKey(), entry.getValue());
            return ts;
        });
    }

    /** {@inheritDoc} */
    @Override default <U,V> SortedMap<U,V>
    toMutableSortedMap(final Function1<? super T,Map.Entry<U,V>> f1) {
        return foldLeft(new TreeMap<>(), (ts, t) -> {
            Map.Entry<U,V> entry = f1.apply(t);
            ts.put(entry.getKey(), entry.getValue());
            return ts;
        });
    }

    /** {@inheritDoc} */
    @Override
    default <U,V> ImMap<U,V> toImMap(Function1<? super T,Map.Entry<U,V>> f1) {
        return foldLeft((ImMap<U, V>) PersistentHashMap.<U, V>empty(),
                        (ts, t) -> ts.assoc(f1.apply(t)));
    }

    /** {@inheritDoc} */
    @Override
    default <U,V> ImSortedMap<U,V> toImSortedMap(Comparator<? super U> comp,
                                                 Function1<? super T,Map.Entry<U,V>> f1) {
        return foldLeft((ImSortedMap<U, V>) PersistentTreeMap.<U, V>empty(comp),
                        (ts, t) -> ts.assoc(f1.apply(t)));
    }

    /** {@inheritDoc} */
    @Override default SortedSet<T> toMutableSortedSet(Comparator<? super T> comparator) {
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
    @Override default ImSortedSet<T> toImSortedSet(Comparator<? super T> comparator) {
        return foldLeft(PersistentTreeSet.ofComp(comparator), (accum, t) -> accum.put(t));
    }

    /** {@inheritDoc} */
    @Override default Set<T> toMutableSet() {
        return foldLeft(new HashSet<>(), (ts, t) -> {
            ts.add(t);
            return ts;
        });
    }
}
