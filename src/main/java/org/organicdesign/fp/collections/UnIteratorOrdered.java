package org.organicdesign.fp.collections;

import java.util.NoSuchElementException;

/** This represents an iterator with a guaranteed ordering. */
public interface UnIteratorOrdered<E> extends UnIterator<E> {
    // ==================================================== Static ====================================================
    UnIteratorOrdered<Object> EMPTY = new UnIteratorOrdered<Object>() {
        @Override public boolean hasNext() { return false; }
        @Override public Object next() { throw new NoSuchElementException(); }
    };
    @SuppressWarnings("unchecked")
    static <T> UnIteratorOrdered<T> empty() { return (UnIteratorOrdered<T>) EMPTY; }

    // =================================================== Instance ===================================================
}
