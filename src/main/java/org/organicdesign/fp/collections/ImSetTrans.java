package org.organicdesign.fp.collections;

/**
 You could think of this as a builder for a transient set.  It builds a little faster than the
 persistent one.  This is NOT inherently thread-safe.  The goal of this is to expose as little of
 the TransientHashMap as possible.
 */
public interface ImSetTrans<E> extends ImSet<E> {

    /** {@inheritDoc} */
    @Override  ImSetTrans<E> put(E val);

    /** {@inheritDoc} */
    @Override ImSetTrans<E> without(E key);

    /** Returns a persistent/immutable version of this transient map. */
    ImSet<E> persistent();
}
