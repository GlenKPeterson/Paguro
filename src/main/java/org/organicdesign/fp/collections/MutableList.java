package org.organicdesign.fp.collections;

/**
 You could think of this as a builder for an immutable map.  It builds a little faster than the
 immutable one.  This is inherently NOT thread-safe.
 */
public interface MutableList<E> extends ImList<E> {
    /** {@inheritDoc} */
    @Override
    MutableList<E> append(E val);

    /** Just returns this list */
    @Override default MutableList<E> mutable() { return this; }

    /** {@inheritDoc} */
    @Override default MutableList<E> concat(Iterable<? extends E> es) {
        return (MutableList<E>) ImList.super.concat(es);
    }

    /** {@inheritDoc} */
    @Override
    MutableList<E> replace(int idx, E e);
}
