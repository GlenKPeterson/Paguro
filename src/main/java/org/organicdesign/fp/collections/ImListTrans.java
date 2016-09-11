package org.organicdesign.fp.collections;

/**
 You could think of this as a builder for a transient map.  It builds a little faster than the
 persistent one.  This is inherently NOT thread-safe.
 */
public interface ImListTrans<E> extends ImList<E> {
    /** {@inheritDoc} */
    @Override ImListTrans<E> append(E val);

    /** Just returns this list */
    @Override default ImListTrans<E> asTransient() { return this; }

    /** {@inheritDoc} */
    @Override default ImListTrans<E> concat(Iterable<? extends E> es) {
        return (ImListTrans<E>) ImList.super.concat(es);
    }

    /** {@inheritDoc} */
    @Override ImListTrans<E> replace(int idx, E e);
}
