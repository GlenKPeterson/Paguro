package org.organicdesign.fp.experiments.collections;

public interface UnIterable<T> extends Iterable<T> {
    /** {@inheritDoc} */
    @Override UnIterator<T> iterator();
}
