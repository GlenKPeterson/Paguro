package org.organicdesign.fp.collections;

/**
 The super-interface of PersistentHashSet (excludes TreeSet).
 */
public interface ImUnsortSet<E> extends ImSet<E> {

    /**
     Returns a transient (mutable builder) version of this set that is not thread safe.
     */
    ImUnsortSetTrans<E> asTransient();

    /** {@inheritDoc} */
    @Override
    ImUnsortSet<E> put(E val);

    /** {@inheritDoc} */
    @Override
    ImUnsortSet<E> without(E key);
}
