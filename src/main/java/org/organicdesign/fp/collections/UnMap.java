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

import org.organicdesign.fp.tuple.Tuple2;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

/** An unmodifiable map */
public interface UnMap<K,V> extends Map<K,V>, UnIterable<UnMap.UnEntry<K,V>> {
    // ==================================================== Static ====================================================
    /**
     * A map entry (key-value pair).  The <tt>UnMap.entrySet</tt> method returns
     * a collection-view of the map, whose elements are of this class.  The
     * <i>only</i> way to obtain a reference to a map entry is from the
     * iterator of this collection-view.
     *
     * @see UnMap#entrySet()
     */
    interface UnEntry<K,V> extends Map.Entry<K,V> {
        /** Not allowed - this is supposed to be unmodifiable */
        @SuppressWarnings("deprecation")
        @Override @Deprecated default V setValue(V value) {
            throw new UnsupportedOperationException("Modification attempted");
        }

        static <K,V> UnEntry<K,V> wrap(Map.Entry<K,V> entry) {
            return Tuple2.of(entry.getKey(), entry.getValue());
        }

        static <K,V> UnIterator<UnEntry<K,V>> wrap(Iterator<Entry<K,V>> innerIter) {
            return new UnIterator<UnEntry<K, V>>() {
                @Override public boolean hasNext() { return innerIter.hasNext(); }
                @Override public UnEntry<K, V> next() { return UnMap.UnEntry.wrap(innerIter.next()); }
            };
        }

        static <K,V> UnIteratorOrdered<UnEntry<K,V>> wrap(UnIteratorOrdered<Map.Entry<K,V>> innerIter) {
            return new UnIteratorOrdered<UnEntry<K, V>>() {
                @Override public boolean hasNext() { return innerIter.hasNext(); }
                @Override public UnEntry<K, V> next() { return UnMap.UnEntry.wrap(innerIter.next()); }
            };
        }

        static <K,V> UnEntry<K,V> of(K k, V v) { return Tuple2.of(k, v); }
//
//        class Impl<K,V> implements UnEntry<K,V> {
//            private final K key;
//            private final V val;
//            private Impl(K k, V v) { key = k; val = v; }
//            @Override public K getKey() { return key; }
//            @Override public V getValue() { return val; }
//            @Override
//            public boolean equals(Object other) {
//                if (this == other) { return true; }
//                if ((other == null) || !(other instanceof UnEntry)) { return false; }
//
//                UnEntry that = (UnEntry) other;
//                return Objects.equals(this.key, that.getKey()) && Objects.equals(this.getValue(), that.getValue());
//            }
//
//            @Override
//            public int hashCode() {
//                int ret = 0;
//                if (key != null) { ret = key.hashCode(); }
//                if (val != null) { return ret ^ val.hashCode(); }
//                // If it's uninitialized, it's equal to every other uninitialized instance.
//                return ret;
//            }
//
//            @Override public String toString() {
//                return "UnEntry(" + key + "," + val + ")";
//            }
//        };
    }

    UnMap<Object,Object> EMPTY = new UnMap<Object,Object>() {
        @Override public UnSet<Map.Entry<Object,Object>> entrySet() { return UnSet.empty(); }
        @Override public UnSet<Object> keySet() { return UnSet.empty(); }
        @Override public UnCollection<Object> values() { return UnSet.empty(); }
        @Override public int size() { return 0; }
        @Override public boolean isEmpty() { return true; }
        @Override public UnIterator<UnEntry<Object,Object>> iterator() { return UnIterator.empty(); }
        @Override public boolean containsKey(Object key) { return false; }
        @Override public boolean containsValue(Object value) { return false; }
        @Override public Object get(Object key) { return null; }
    };
    @SuppressWarnings("unchecked")
    static <T,U> UnMap<T,U> empty() { return (UnMap<T,U>) EMPTY; }

    // =================================================== Instance ===================================================

    // Modification Operations

    /** Not allowed - this is supposed to be unmodifiable */
    @Override @Deprecated default void clear() {
        throw new UnsupportedOperationException("Modification attempted");
    }

    /** Not allowed - this is supposed to be unmodifiable */
    @Override @Deprecated default V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        throw new UnsupportedOperationException("Modification attempted");
    }
    /** Not allowed - this is supposed to be unmodifiable */
    @Override @Deprecated default V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        throw new UnsupportedOperationException("Modification attempted");
    }
    /** Not allowed - this is supposed to be unmodifiable */
    @Override @Deprecated default V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        throw new UnsupportedOperationException("Modification attempted");
    }
// boolean	containsKey(Object key)
// boolean	containsValue(Object value)

    /** Most maps are not designed for this - the default implementation has O(n) performance. {@inheritDoc} */
    @Override default boolean containsValue(Object value) {
        for (Entry<K,V> entry : this.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a view of the mappings contained in this map.  The set should actually contain UnMap.Entry items, but
     * that return signature is illegal in Java, so you'll just have to remember. */
    @Override UnSet<Map.Entry<K,V>> entrySet();

// boolean	equals(Object o)

//    @Override default boolean equals(Object other) {
//        // Cheapest operation first...
//        if (this == other) { return true; }
//
//        if ( (other == null) ||
//                !(other instanceof Map) ||
//                (this.hashCode() != other.hashCode()) ) {
//            return false;
//        }
//        // Details...
//        final Map that = (Map) other;
//        if (this.size() != that.size()) {
//            return false;
//        }
//        return this.entrySet().containsAll(that.entrySet());
//    }

// default void	forEach(BiConsumer<? super K,? super V> action)
// V	get(Object key)
// default V	getOrDefault(Object key, V defaultValue)

//    @Override default int hashCode() {
//        if (size() == 0) { return 0; }
//        return Arrays.hashCode(entrySet().toArray());
//    };

    /** {@inheritDoc} */
    @Override default boolean isEmpty() { return size() == 0; }

    /** Returns a view of the keys contained in this map. */
    @Override UnSet<K> keySet();

    /** Not allowed - this is supposed to be unmodifiable */
    @Override @Deprecated default V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        throw new UnsupportedOperationException("Modification attempted");
    }
    /** Not allowed - this is supposed to be unmodifiable */
    @Override @Deprecated default V put(K key, V value) {
        throw new UnsupportedOperationException("Modification attempted");
    }
    /** Not allowed - this is supposed to be unmodifiable */
    @Override @Deprecated default void putAll(Map<? extends K, ? extends V> m) {
        throw new UnsupportedOperationException("Modification attempted");
    }
    /** Not allowed - this is supposed to be unmodifiable */
    @Override @Deprecated default V putIfAbsent(K key, V value) {
        throw new UnsupportedOperationException("Modification attempted");
    }
    /** Not allowed - this is supposed to be unmodifiable */
    @Override @Deprecated default V remove(Object key) {
        throw new UnsupportedOperationException("Modification attempted");
    }
    /** Not allowed - this is supposed to be unmodifiable */
    @Override @Deprecated default boolean remove(Object key, Object value) {
        throw new UnsupportedOperationException("Modification attempted");
    }
    /** Not allowed - this is supposed to be unmodifiable */
    @Override @Deprecated default boolean replace(K key, V oldValue, V newValue) {
        throw new UnsupportedOperationException("Modification attempted");
    }
    /** Not allowed - this is supposed to be unmodifiable */
    @Override @Deprecated default V replace(K key, V value) {
        throw new UnsupportedOperationException("Modification attempted");
    }
    /** Not allowed - this is supposed to be unmodifiable */
    @Override @Deprecated default void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        throw new UnsupportedOperationException("Modification attempted");
    }

// int	size()

    /** Returns a view of the values contained in this map. */
    @Override UnCollection<V> values();
}
