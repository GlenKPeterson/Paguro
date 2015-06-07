// Copyright 2014-01-08 PlanBase Inc. & Glen Peterson
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
import org.organicdesign.fp.collections.UnIterable;
import org.organicdesign.fp.collections.UnIterator;
import org.organicdesign.fp.function.Function1;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;

import static org.organicdesign.fp.StaticImports.un;

/**
 Calling any of these methods forces eager evaluation of the underlying collection.  Infinite collections are not
 Realizable.
 */
public interface Realizable<T> extends UnIterable<T> {
    /** A one-time use, not-thread-safe way to get each value of this Realizable in turn. */
    @Override default UnIterator<T> iterator() {
        // Maybe not so performant, but gives a chance to see if this is even a useful method.
        return un(toJavaList()).iterator();
    }

    /**
     The contents of this Realizable as a thread-safe immutable list.  Use this when you want to access items quickly
     O(log32 n) by index.
     */
    ImList<T> toImList();

    /**
     The contents of this Realizable as an thread-safe, immutable, sorted (tree) map.  Use this when you want to quickly
     O(log n) look up values by key, but still be able to retrieve Entries in key order.
     @return An immutable map
     @param comp Determines the ordering.  If U implements Comparable, you can pass Function2.defaultComparator() here.
     @param f1 Maps each item in this collection to a key/value pair.  If the collection is composed of Map.Entries,
     you can pass Function1.identity() here.
     */
    <U,V> ImMapOrdered<U,V> toImMapOrdered(Comparator<? super U> comp, Function1<? super T,Map.Entry<U,V>> f1);

    /**
     The contents of this Realizable presented as an immutable, sorted (tree) set.  Use this when you want to quickly
     O(log n) tell whether the set contains various items.
     @return An immutable set
     @param comp Determines the ordering.  If T implements Comparable, you can pass Function2.defaultComparator() here.
     */
    ImSetOrdered<T> toImSetOrdered(Comparator<? super T> comp);

    /** The contents copied to a mutable list.  Use toImList unless you need to modify the list in-place. */
    List<T> toJavaList();

    /**
     Returns the contents of this Realizable copied to a mutable hash map.  Use toImMapOrdered() unless you need to
     modify the map in-place.  Use toUnMap if you just need the fastest O(1) access speed without modifying it in place.
     @return A map with the keys from the given set, mapped to values using the given function.
     @param f1 Maps keys to values
     */
    <U,V> Map<U,V> toJavaMap(final Function1<? super T,Map.Entry<U,V>> f1);

    /**
     Returns the contents of this Realizable copied to a mutable tree map.  Use toImMapOrdered() unless you need to
     modify the map in-place.
     @return A map with the keys from the given set, mapped to values using the given function.
     @param f1 Maps keys to values
     */
    <U,V> SortedMap<U,V> toJavaMapSorted(final Function1<? super T,Map.Entry<U,V>> f1);

    /**
     Returns the contents of this Realizable copied to a mutable hash set.  Use toImSetOrdered() unless you need to
     modify the set in-place.  Use toUnSet if you just need the fastest O(1) access speed without modifying it in place.
     @return A map with the keys from the given set, mapped to values using the given function.
     */
    Set<T> toJavaSet();

    /**
     Returns the contents of this Realizable copied to a mutable tree set.  Use toImSetOrdered unless you need to modify
     the set in-place.
     @param comp Determines the ordering.  If T implements Comparable, you can pass Function2.defaultComparator() here.
     */
    SortedSet<T> toJavaSetSorted(Comparator<? super T> comp);

    /**
     Returns a type-safe version of toArray() that doesn't require that you pass an array of the proper type and size.
     */
    @SuppressWarnings("unchecked")
    default T[] toTypedArray() {
        List<T> al = toJavaList();
        return al.toArray((T[]) new Object[al.size()]);
    }

    /**
     This method will be replaced with toImMap() once a PersistentHashMap is added to this project.
     The contents of this Realizable as an unmodifiable hash map.  Use this when you want to very quickly O(1)
     look up values by key, and don't care about ordering.
     @return An unmodifiable map
     @param f1 Maps each item in this collection to a key/value pair.  If the collection is composed of Map.Entries,
     you can pass Function1.identity() here.
     */
    <U,V> ImMap<U,V> toImMap(Function1<? super T,Map.Entry<U,V>> f1);

    /**
     This method will be replaced with toImSet() once a PersistentHashSet is added to this project.
     The contents of this Realizable presented as an unmodifiable hash set.  Use this when you want to very quickly O(1)
     tell whether the set contains various items, but don't care about ordering.
     @return An unmodifiable set
     */
    ImSet<T> toImSet();

}
