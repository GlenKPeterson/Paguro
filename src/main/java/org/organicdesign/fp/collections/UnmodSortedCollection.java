package org.organicdesign.fp.collections;

public interface UnmodSortedCollection<E> extends UnmodCollection<E>, UnmodSortedIterable<E> {
    /** An unmodifiable ordered iterator {@inheritDoc} */
    @Override
    UnmodSortedIterator<E> iterator();
}
