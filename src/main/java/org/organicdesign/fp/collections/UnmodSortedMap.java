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
public interface UnmodSortedMap<K,V> extends UnmodMap<K,V>, SortedMap<K,V>, UnmodSortedIterable<UnmodMap.UnEntry<K,V>> {

    // ==================================================== Static ====================================================
    UnmodSortedMap<Object,Object> EMPTY = new UnmodSortedMap<Object,Object>() {
        @Override public UnmodSortedSet<Entry<Object,Object>> entrySet() { return UnmodSortedSet.empty(); }
        @Override public UnmodSet<Object> keySet() { return UnmodSet.empty(); }
        @Override public Comparator<? super Object> comparator() { return null; }
        @Override public UnmodSortedMap<Object,Object> subMap(Object fromKey, Object toKey) { return this; }
        @Override public UnmodSortedMap<Object,Object> tailMap(Object fromKey) { return this; }
        @Override public Object firstKey() { throw new NoSuchElementException("empty map"); }
        @Override public Object lastKey() { throw new NoSuchElementException("empty map"); }
        @Override public UnmodCollection<Object> values() { return UnmodSet.empty(); }
        @Override public int size() { return 0; }
        @Override public boolean isEmpty() { return true; }
        @Override public UnmodSortedIterator<UnEntry<Object,Object>> iterator() { return UnmodSortedIterator.empty(); }
        @Override public boolean containsKey(Object key) { return false; }
        @Override public boolean containsValue(Object value) { return false; }
        @Override public Object get(Object key) { return null; }
    };
    @SuppressWarnings("unchecked")
    static <T,U> UnmodSortedMap<T,U> empty() { return (UnmodSortedMap<T,U>) EMPTY; }

    // =================================================== Instance ===================================================

// public Comparator<? super K>	comparator()

    /**
     * Returns a view of the mappings contained in this map.  The set should actually contain UnmodMap.Entry items, but
     * that return signature is illegal in Java, so you'll just have to remember. */
    @Override
    UnmodSortedSet<Entry<K,V>> entrySet();

// public  K	firstKey()

    /** {@inheritDoc} */
    @Override default UnmodSortedMap<K,V> headMap(K toKey) { return subMap(firstKey(), toKey); }

    /** {@inheritDoc} */
    @Override default UnmodSortedIterator<UnEntry<K,V>> iterator() { return UnmodMap.UnEntry.wrap(entrySet().iterator()); }

    /** Returns a view of the keys contained in this map. */
    @Override
    UnmodSet<K> keySet();

// public  K	lastKey()

    /** {@inheritDoc} */
    @Override
    UnmodSortedMap<K,V> subMap(K fromKey, K toKey);

    /** {@inheritDoc} */
    @Override
    UnmodSortedMap<K,V> tailMap(K fromKey);

    /** {@inheritDoc} */
    @Override
    UnmodCollection<V> values();

// Methods inherited from interface java.util.Map
// clear, compute, computeIfAbsent, computeIfPresent, containsKey, containsValue, equals, forEach, get, getOrDefault, hashCode, isEmpty, merge, put, putAll, putIfAbsent, remove, remove, replace, replace, replaceAll, size
}
