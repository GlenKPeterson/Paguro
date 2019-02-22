package org.organicdesign.fp.collections;

import java.util.Set;

import org.jetbrains.annotations.NotNull;

public interface UnmodSortedCollection<E> extends UnmodCollection<E>, UnmodSortedIterable<E> {

    /** An unmodifiable ordered iterator {@inheritDoc} */
    @NotNull
    @Override
    UnmodSortedIterator<E> iterator();
}
