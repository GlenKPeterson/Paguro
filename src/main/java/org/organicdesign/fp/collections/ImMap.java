package org.organicdesign.fp.collections;

import java.util.Map;

/** An immutable map with no guarantees about its ordering. */
public interface ImMap<K,V> extends UnMap<K,V> {
    /**
     * Returns a view of the mappings contained in this map.  The set should actually contain UnMap.Entry items, but
     * that return signature is illegal in Java, so you'll just have to remember. */
    @Override ImSet<Map.Entry<K,V>> entrySet();

    /** Returns a view of the keys contained in this map. */
    @Override ImSet<K> keySet();

    /** Returns a new map with the given key/value added */
    ImMap<K,V> assoc(K key, V val);

    /** Returns a new map with an immutable copy of the given entry added */
    default ImMap<K,V> assoc(Map.Entry<K,V> entry) { return assoc(entry.getKey(), entry.getValue()); }

    /** Returns a new map with the given key/value removed */
    ImMap<K,V> without(K key);
}
