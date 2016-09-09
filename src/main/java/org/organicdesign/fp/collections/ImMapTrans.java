package org.organicdesign.fp.collections;

/**
 You could think of this as a builder for a PersistentHashMap.  It builds a little faster than the
 persistent one.  This is inherently NOT thread-safe.
 */
// TODO: Rename to ImUnSortMap
public interface ImMapTrans<K,V> extends ImMap<K,V> {
    ImMapTrans<K,V> asTransient();

    /** Returns the Equator used by this map for equals comparisons and hashCodes */
    Equator<K> equator();

    /** Returns a persistent/immutable version of this transient map. */
    ImMapTrans<K,V> persistent();

    /** {@inheritDoc} */
    @Override default ImSet<K> keySet() { return PersistentHashSet.ofMap(this); }

    /** {@inheritDoc} */
    @Override ImMapTrans<K,V> assoc(K key, V val);

    /** {@inheritDoc} */
    @Override ImMapTrans<K,V> without(K key);
}