package org.organicdesign.fp.collections;

/**
 You could think of this as a builder for a transient map.  It builds a little faster than the
 persistent one.  This is inherently NOT thread-safe.
 */
public interface ImListTrans<E> extends ImList<E> {

    /** {@inheritDoc} */
    @Override ImListTrans<E> append(E val);

    /** Returns a persistent/immutable version of this transient vector/list. */
    ImList<E> persistent();

    /** {@inheritDoc} */
    @Override ImListTrans<E> replace(int idx, E e);
}
