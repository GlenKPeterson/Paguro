// Copyright 2015-04-13 PlanBase Inc. & Glen Peterson
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
package org.organicdesign.fp.collections;

import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.SortedMap;

/** An unmodifiable SortedMap. */
public interface UnMapOrdered<K,V> extends UnMap<K,V>, SortedMap<K,V>, UnIterableOrdered<UnMap.UnEntry<K,V>> {

    // ==================================================== Static ====================================================
    UnMapOrdered<Object,Object> EMPTY = new UnMapOrdered<Object,Object>() {
        @Override public UnSetOrdered<Entry<Object,Object>> entrySet() { return UnSetOrdered.empty(); }
        @Override public UnSet<Object> keySet() { return UnSet.empty(); }
        @Override public Comparator<? super Object> comparator() { return null; }
        @Override public UnMapOrdered<Object,Object> subMap(Object fromKey, Object toKey) { return this; }
        @Override public UnMapOrdered<Object,Object> tailMap(Object fromKey) { return this; }
        @Override public Object firstKey() { throw new NoSuchElementException("empty map"); }
        @Override public Object lastKey() { throw new NoSuchElementException("empty map"); }
        @Override public UnCollection<Object> values() { return UnSet.empty(); }
        @Override public int size() { return 0; }
        @Override public boolean isEmpty() { return true; }
        @Override public UnIteratorOrdered<UnEntry<Object,Object>> iterator() { return UnIteratorOrdered.empty(); }
        @Override public boolean containsKey(Object key) { return false; }
        @Override public boolean containsValue(Object value) { return false; }
        @Override public Object get(Object key) { return null; }
    };
    @SuppressWarnings("unchecked")
    static <T,U> UnMapOrdered<T,U> empty() { return (UnMapOrdered<T,U>) EMPTY; }

    // =================================================== Instance ===================================================

// public Comparator<? super K>	comparator()

    /**
     * Returns a view of the mappings contained in this map.  The set should actually contain UnMap.Entry items, but
     * that return signature is illegal in Java, so you'll just have to remember. */
    @Override
    UnSetOrdered<Entry<K,V>> entrySet();

// public  K	firstKey()

    /** {@inheritDoc} */
    @Override default UnMapOrdered<K,V> headMap(K toKey) { return subMap(firstKey(), toKey); }

    /** {@inheritDoc} */
    @Override default UnIteratorOrdered<UnEntry<K,V>> iterator() { return UnMap.UnEntry.wrap(entrySet().iterator()); }

    /** Returns a view of the keys contained in this map. */
    @Override UnSet<K> keySet();

// public  K	lastKey()

    /** {@inheritDoc} */
    @Override
    UnMapOrdered<K,V> subMap(K fromKey, K toKey);

    /** {@inheritDoc} */
    @Override
    UnMapOrdered<K,V> tailMap(K fromKey);

    /** {@inheritDoc} */
    @Override UnCollection<V> values();

// Methods inherited from interface java.util.Map
// clear, compute, computeIfAbsent, computeIfPresent, containsKey, containsValue, equals, forEach, get, getOrDefault, hashCode, isEmpty, merge, put, putAll, putIfAbsent, remove, remove, replace, replace, replaceAll, size
}
