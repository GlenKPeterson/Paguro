package org.organicdesign.fp.collections;

public interface UnIterable<T> extends Iterable<T> {
    /** {@inheritDoc} */
    @Override UnIterator<T> iterator();
}
