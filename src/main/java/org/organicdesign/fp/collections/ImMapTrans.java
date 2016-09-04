package org.organicdesign.fp.collections;

/**
 You could think of this as a builder for a transient map.  It builds a little faster than the
 persistent one.

 Allows a map to be taken from transient to persistent.  This is NOT inherently thread-safe.
 This bridges that gap to let PersistentHashSet know about the asTransient() method for accessing
 PersistentHashMap's inner transient class, which in turn, allows this class to implement keySet
 for PersistentHashMap.
 */
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