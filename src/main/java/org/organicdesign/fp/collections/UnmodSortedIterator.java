package org.organicdesign.fp.collections;

import java.io.Serializable;
import java.util.Iterator;

/** This represents an iterator with a guaranteed ordering. */
public interface UnmodSortedIterator<E> extends UnmodIterator<E> {
    class Implementation<E> implements UnmodSortedIterator<E>, Serializable {
        // For serializable.  Make sure to change whenever internal data format changes.
        private static final long serialVersionUID = 20160903174100L;

        private final Iterator<E> iter;
        Implementation(Iterator<E> i) { iter = i; }

        @Override public boolean hasNext() { return iter.hasNext(); }
        @Override public E next() { return iter.next(); }
    }
}
