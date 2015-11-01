package org.organicdesign.fp.collections;

import java.util.NoSuchElementException;

/** This represents an iterator with a guaranteed ordering. */
public interface UnmodSortedIterator<E> extends UnmodIterator<E> {
    // ========================================== Static ==========================================
    UnmodSortedIterator<Object> EMPTY = new UnmodSortedIterator<Object>() {
        @Override public boolean hasNext() { return false; }
        @Override public Object next() { throw new NoSuchElementException(); }
    };
    @SuppressWarnings("unchecked")
    static <T> UnmodSortedIterator<T> empty() { return (UnmodSortedIterator<T>) EMPTY; }
}
