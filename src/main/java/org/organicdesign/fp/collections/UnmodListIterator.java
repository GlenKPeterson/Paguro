package org.organicdesign.fp.collections;

import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

/** An unmodifiable ListIterator */
public interface UnmodListIterator<E> extends ListIterator<E>, UnmodSortedIterator<E> {

    /** Not allowed - this is supposed to be unmodifiable */
    @Override @Deprecated default void add(E element) {
        throw new UnsupportedOperationException("Modification attempted");
    }

// boolean	hasNext()
// boolean	hasPrevious()
// E	next()
// int	nextIndex()
// E	previous()
// int	previousIndex()

    /** Not allowed - this is supposed to be unmodifiable */
    @SuppressWarnings("deprecation")
    @Override @Deprecated default void remove() { throw new UnsupportedOperationException("Modification attempted"); }

    /** Not allowed - this is supposed to be unmodifiable */
    @Override @Deprecated default void set(E element) {
        throw new UnsupportedOperationException("Modification attempted");
    }

    // ==================================================== Static ====================================================
    UnmodListIterator<Object> EMPTY = new UnmodListIterator<Object>() {
        @Override public boolean hasNext() { return false; }
        @Override public Object next() { throw new NoSuchElementException(); }
        @Override public void forEachRemaining(Consumer<? super Object> action) { }
        @Override public boolean hasPrevious() { return false; }
        @Override public Object previous() { throw new NoSuchElementException(); }
        @Override public int nextIndex() { return 0; }
        @Override public int previousIndex() { return -1; }
    };
    @SuppressWarnings("unchecked")
    static <T> UnmodListIterator<T> empty() { return (UnmodListIterator<T>) EMPTY; }

// Methods inherited from interface java.util.Iterator
// forEachRemaining
}
