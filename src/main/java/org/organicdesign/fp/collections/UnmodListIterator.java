package org.organicdesign.fp.collections;

import java.util.ListIterator;

/** An unmodifiable ListIterator */
public interface UnmodListIterator<E> extends ListIterator<E>, UnmodSortedIterator<E> {

    // ========================================= Instance =========================================
    /** Not allowed - this is supposed to be unmodifiable */
    @Override @Deprecated default void add(E element) {
        throw new UnsupportedOperationException("Modification attempted");
    }

// boolean	hasNext()
// boolean	hasPrevious()
// E	next()
// int	nextIndex()
// E	previous()

    // I think this is the only valid implementation of this method. You can override it if you
    // think otherwise.
    /** {@inheritDoc} */
    @Override default int previousIndex() { return nextIndex() - 1; }

    /** Not allowed - this is supposed to be unmodifiable */
    @SuppressWarnings("deprecation")
    @Override @Deprecated default void remove() {
        throw new UnsupportedOperationException("Modification attempted");
    }

    /** Not allowed - this is supposed to be unmodifiable */
    @Override @Deprecated default void set(E element) {
        throw new UnsupportedOperationException("Modification attempted");
    }

// Methods inherited from interface java.util.Iterator
// forEachRemaining
}
