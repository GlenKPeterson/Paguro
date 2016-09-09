package org.organicdesign.fp.collections;

/**
 Replaced with {@link ImUnsortMap} and {@link ImUnsortMapTrans}.
 */
@Deprecated
public interface ImMapTrans<K,V> extends ImUnsortMapTrans<K,V> {
    ImUnsortMapTrans<K,V> asTransient();

    /** Returns the Equator used by this map for equals comparisons and hashCodes */
    Equator<K> equator();

    /** Returns a persistent/immutable version of this transient map. */
    ImUnsortMap<K,V> persistent();

    /** {@inheritDoc} */
    @Override default ImSet<K> keySet() { return PersistentHashSet.ofMap(this); }

    /** {@inheritDoc} */
    @Override ImUnsortMapTrans<K,V> assoc(K key, V val);

    /** {@inheritDoc} */
    @Override ImUnsortMapTrans<K,V> without(K key);
}