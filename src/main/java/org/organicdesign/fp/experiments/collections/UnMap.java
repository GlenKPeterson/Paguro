package org.organicdesign.fp.experiments.collections;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface UnMap<K,V> extends Map<K,V> {
    // Modification Operations

    /** Not allowed - this is supposed to be unmodifiable */
    @Override @Deprecated default V put(K key, V value) {
        throw new UnsupportedOperationException("Modification attempted");
    }

    /** Not allowed - this is supposed to be unmodifiable */
    @Override @Deprecated default V remove(Object key) {
        throw new UnsupportedOperationException("Modification attempted");
    }


    // Bulk Operations

    /** Not allowed - this is supposed to be unmodifiable */
    @Override @Deprecated default void putAll(Map<? extends K, ? extends V> m) {
        throw new UnsupportedOperationException("Modification attempted");
    }

    /** Not allowed - this is supposed to be unmodifiable */
    @Override @Deprecated default void clear() {
        throw new UnsupportedOperationException("Modification attempted");
    }


    // Views

    /** Returns a view of the keys contained in this map. */
    @Override UnSet<K> keySet();

    /** Returns a view of the values contained in this map. */
    @Override UnCollection<V> values();

    /**
     * Returns a view of the mappings contained in this map.  The set should actually contain UnMap.Entry items, but
     * that return signature is illegal in Java, so you'll just have to remember. */
    @Override UnSet<Map.Entry<K,V>> entrySet();

    /**
     * A map entry (key-value pair).  The <tt>UnMap.entrySet</tt> method returns
     * a collection-view of the map, whose elements are of this class.  The
     * <i>only</i> way to obtain a reference to a map entry is from the
     * iterator of this collection-view.
     *
     * @see UnMap#entrySet()
     */
    interface Entry<K,V> extends Map.Entry<K,V> {
        /** Not allowed - this is supposed to be unmodifiable */
        @SuppressWarnings("deprecation")
        @Override @Deprecated default V setValue(V value) {
            throw new UnsupportedOperationException("Modification attempted");
        }
    }

    // Defaultable methods

    /** Not allowed - this is supposed to be unmodifiable */
    @Override @Deprecated default void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        throw new UnsupportedOperationException("Modification attempted");
    }

    /** Not allowed - this is supposed to be unmodifiable */
    @Override @Deprecated default V putIfAbsent(K key, V value) {
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
    @Override @Deprecated default V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        throw new UnsupportedOperationException("Modification attempted");
    }
    /** Not allowed - this is supposed to be unmodifiable */
    @Override @Deprecated default V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        throw new UnsupportedOperationException("Modification attempted");
    }
    /** Not allowed - this is supposed to be unmodifiable */
    @Override @Deprecated default V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        throw new UnsupportedOperationException("Modification attempted");
    }
    /** Not allowed - this is supposed to be unmodifiable */
    @Override @Deprecated default V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        throw new UnsupportedOperationException("Modification attempted");
    }
}
