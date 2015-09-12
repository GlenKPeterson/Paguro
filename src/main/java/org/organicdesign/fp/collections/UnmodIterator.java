package org.organicdesign.fp.collections;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 A one-time use, mutable, not-thread-safe way to get each value of the underling collection in
 turn. I experimented with various thread-safe alternatives, but the JVM is optimized around
 iterators so this is the lowest common denominator of collection iteration, even though
 iterators are inherently mutable.

 This is called "Unmod" in the sense that it doesn't modify the underlying collection.  Iterators
 are inherently mutable.  The only safe way to handle them is to pass around IteraBLEs so that
 the ultimate client gets its own, unshared iteraTOR.  Order is not guaranteed.
 */
public interface UnmodIterator<E> extends Iterator<E> {
    // ========================================== Static ==========================================
    UnmodIterator<Object> EMPTY = new UnmodIterator<Object>() {
        @Override public boolean hasNext() { return false; }
        @Override public Object next() { throw new NoSuchElementException(); }
    };
    @SuppressWarnings("unchecked")
    static <T> UnmodIterator<T> empty() { return (UnmodIterator<T>) EMPTY; }

    // ========================================= Instance =========================================
//default void forEachRemaining(Consumer<? super E> action)
//boolean hasNext()
//E next()

    /** Not allowed - this is supposed to be unmodifiable */
    @Override @Deprecated default void remove() {
        throw new UnsupportedOperationException("Modification attempted");
    }
}
