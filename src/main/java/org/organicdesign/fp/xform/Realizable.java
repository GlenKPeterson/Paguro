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

package org.organicdesign.fp.xform;

import org.organicdesign.fp.collections.ImList;
import org.organicdesign.fp.collections.ImMap;
import org.organicdesign.fp.collections.ImSet;
import org.organicdesign.fp.collections.ImSortedMap;
import org.organicdesign.fp.collections.ImSortedSet;
import org.organicdesign.fp.function.Function1;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;

/**
 Calling any of these methods forces eager evaluation of the underlying collection.  Infinite
 collections are not Realizable.
 */
public interface Realizable<T> {
    /**
     Returns an Object[] for backward compatibility
     */
    @SuppressWarnings("unchecked")
    default Object[] toArray() {
        return toMutableList().toArray();
//        return al.toArray((T[]) new Object[al.size()]);
    }

    /**
     Realize a thread-safe immutable list to access items quickly O(log32 n) by index.
     */
    ImList<T> toImList();

    /**
     Realize an unordered immutable hash map to very quickly O(1) look up values by key, but don't
     care about ordering.

     @param f1 Maps each item in this collection to a key/value pair.  If the collection is composed
               of Map.Entries, you can pass Function1.identity() here.
     @return An unmodifiable map
     */
    <U,V> ImMap<U,V> toImMap(Function1<? super T,Map.Entry<U,V>> f1);

    /**
     Realize an unordered immutable hash set to remove duplicates or very quickly O(1) tell whether
     the set contains various items, but don't care about ordering.

     @return An unmodifiable set (with duplicates removed)
     */
    ImSet<T> toImSet();

    /**
     Realize an immutable, ordered (tree) map to quickly O(log2 n) look up values by key, but still
     retrieve entries in key order.

     @param comp Determines the ordering.  If U implements Comparable, you can pass
                 Function2.defaultComparator() here.
     @param f1 Maps each item in this collection to a key/value pair.  If the collection is composed
               of Map.Entries, you can pass Function1.identity() here.
     @return An immutable map
     */
    <U,V> ImSortedMap<U,V> toImSortedMap(Comparator<? super U> comp,
                                         Function1<? super T,Map.Entry<U,V>> f1);

    /**
     Realize an immutable, sorted (tree) set to quickly O(log2 n) test it contains items, but still
     retrieve entries in order.

     @param comp Determines the ordering.  If T implements Comparable, you can pass
                 Function2.defaultComparator() here.
     @return An immutable set (with duplicates removed)
     */
    ImSortedSet<T> toImSortedSet(Comparator<? super T> comp);

    /** Realize a mutable list.  Use toImList unless you need to modify the list in-place. */
    List<T> toMutableList();

    /**
     Realize a mutable hash map.  Use toImMap() unless you need to modify the map in-place.

     @param f1 Maps keys to values

     @return A map with the keys from the given set, mapped to values using the given function.
     */
    <U,V> Map<U,V> toMutableMap(final Function1<? super T,Map.Entry<U,V>> f1);

    /**
     Realize a mutable tree map.  Use toImSortedMap() unless you need to modify the map in-place.

     @param f1 Maps keys to values
     @return A map with the keys from the given set, mapped to values using the given function.
     */
    <U,V> SortedMap<U,V> toMutableSortedMap(final Function1<? super T,Map.Entry<U,V>> f1);

    /**
     Realize a mutable hash set. Use toImSet() unless you need to modify the set in-place.

     @return A mutable set (with duplicates removed)
     */
    Set<T> toMutableSet();

    /**
     Returns a mutable tree set. Use toImSortedSet unless you need to modify the set in-place.

     @param comp Determines the ordering.  If T implements Comparable, you can pass
                 Function2.defaultComparator() here.
     @return A mutable sorted set
     */
    SortedSet<T> toMutableSortedSet(Comparator<? super T> comp);

}
