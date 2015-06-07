package org.organicdesign.fp.collections;

import org.organicdesign.fp.Option;
import org.organicdesign.fp.permanent.Sequence;

import java.util.Map;

/** An immutable map with no guarantees about its ordering. */
public interface ImMap<K,V> extends UnMap<K,V> {
    Option<UnMap.UnEntry<K,V>> entry(K key);

    Sequence<UnEntry<K,V>> seq();

    /** Returns a new map with the given key/value added */
    ImMap<K,V> assoc(K key, V val);

    /** Returns a new map with the given key/value removed */
    ImMap<K,V> without(K key);

    /**
     * Returns a view of the mappings contained in this map.  The set should actually contain UnMap.Entry items, but
     * that return signature is illegal in Java, so you'll just have to remember. */
    @Override default ImSet<Map.Entry<K,V>> entrySet() {
        return seq().map(e -> (Map.Entry<K,V>) e)
                .toImSet();
    }

    /** Returns a view of the keys contained in this map. */
    @Override ImSet<K> keySet();

    @SuppressWarnings("unchecked")
    @Override default boolean containsKey(Object key) { return entry((K) key).isSome(); }

    @SuppressWarnings("unchecked")
    @Override default V get(Object key) {
        Option<UnEntry<K,V>> entry = entry((K) key);
        return entry.isSome() ? entry.get().getValue() : null;
    }

    default V getOrElse(K key, V notFound) {
        Option<UnEntry<K,V>> entry = entry(key);
        return entry.isSome() ? entry.get().getValue() : notFound;
    }

    @Override default UnCollection<V> values() { return seq().map(e -> e.getValue()).toImSet(); }

    @Override default UnIterator<UnEntry<K,V>> iterator() { return seq().iterator(); }

    /** Returns a new map with an immutable copy of the given entry added */
    default ImMap<K,V> assoc(Map.Entry<K,V> entry) { return assoc(entry.getKey(), entry.getValue()); }
}
