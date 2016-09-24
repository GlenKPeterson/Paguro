package org.organicdesign.fp.collections;

import java.util.Set;

public interface UnmodSortedCollection<E> extends UnmodCollection<E>, UnmodSortedIterable<E> {

    /** An unmodifiable ordered iterator {@inheritDoc} */
    @Override
    UnmodSortedIterator<E> iterator();
}
