package org.organicdesign.fp.collections;

import java.util.Map;

/** An immutable map with no guarantees about its ordering. */
public interface ImMap<K,V> extends BaseUnsortedMap<K,V> {

    /** Returns a new map with the given key/value added */
    @Override ImMap<K,V> assoc(K key, V val);

    /** Returns a new map with an immutable copy of the given entry added */
    @Override default ImMap<K,V> assoc(Map.Entry<K,V> entry) {
        return assoc(entry.getKey(), entry.getValue());
    }

    /** Returns a new map with the given key/value removed */
    @Override ImMap<K,V> without(K key);

    /**
     Returns a view of the mappings contained in this map.  The set should actually contain
     UnmodMap.Entry items, but that return signature is illegal in Java, so you'll just have to
     remember.
     */
    @Override default ImSet<Map.Entry<K,V>> entrySet() {
        return map(e -> (Map.Entry<K,V>) e)
                .toImSet();
    }

    /** Returns an immutable view of the keys contained in this map. */
    @Override default ImSet<K> keySet() {
        return mutable().keySet().immutable();
    }

    /** Returns a mutable version of this mutable map. */
    MutMap<K,V> mutable();

}
