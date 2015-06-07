package org.organicdesign.fp.collections;

public interface UnCollectionOrdered<E> extends UnCollection<E>, UnIterableOrdered<E> {
    /** An unmodifiable ordered iterator {@inheritDoc} */
    @Override UnIteratorOrdered<E> iterator();
}
