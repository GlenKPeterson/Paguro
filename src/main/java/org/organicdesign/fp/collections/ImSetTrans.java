package org.organicdesign.fp.collections;

/**
 You could think of this as a builder for a PersistentHashSet.  It builds a little faster than the
 persistent one.  This is inherently NOT thread-safe.
 */
public interface ImSetTrans<E> extends ImSet<E> {

    /** {@inheritDoc} */
    @Override  ImSetTrans<E> put(E val);

    /** {@inheritDoc} */
    @Override ImSetTrans<E> without(E key);

    /** Returns a persistent/immutable version of this transient map. */
    ImSet<E> persistent();
}
