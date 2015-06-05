package org.organicdesign.fp.collections;

import java.util.Iterator;
import java.util.NoSuchElementException;

/** An unmodifiable iterator.  Order is not guaranteed. */
public interface UnIterator<E> extends Iterator<E> {
    // ==================================================== Static ====================================================
    UnIterator<Object> EMPTY = new UnIterator<Object>() {
        @Override public boolean hasNext() { return false; }
        @Override public Object next() { throw new NoSuchElementException(); }
    };
    @SuppressWarnings("unchecked")
    static <T> UnIterator<T> empty() { return (UnIterator<T>) EMPTY; }

    // =================================================== Instance ===================================================
//default void forEachRemaining(Consumer<? super E> action)
//boolean hasNext()
//E next()

    /** Not allowed - this is supposed to be unmodifiable */
    @Override @Deprecated default void remove() {
        throw new UnsupportedOperationException("Modification attempted");
    }
}
