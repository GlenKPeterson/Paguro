package org.organicdesign.fp.collections;

/**
 Replaced with {@link ImUnsortedMap} and {@link MutableUnsortedMap}.
 */
@Deprecated
public interface ImMapTrans<K,V> extends MutableUnsortedMap<K,V> {
    MutableUnsortedMap<K,V> mutable();

    /** Returns the Equator used by this map for equals comparisons and hashCodes */
    Equator<K> equator();

    /** Returns a immutable version of this mutable map. */
    ImUnsortedMap<K,V> immutable();

    /** {@inheritDoc} */
    @Override default ImSet<K> keySet() { return PersistentHashSet.ofMap(this); }

    /** {@inheritDoc} */
    @Override
    MutableUnsortedMap<K,V> assoc(K key, V val);

    /** {@inheritDoc} */
    @Override
    MutableUnsortedMap<K,V> without(K key);
}