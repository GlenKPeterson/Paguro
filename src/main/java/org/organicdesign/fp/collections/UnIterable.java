package org.organicdesign.fp.collections;

/** An unmodifiable Iterable */
public interface UnIterable<T> extends Iterable<T> {
    /** {@inheritDoc} */
    @Override UnIterator<T> iterator();
}
