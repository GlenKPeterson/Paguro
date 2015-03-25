package org.organicdesign.fp.experiments.collections;

import java.util.ListIterator;

public interface UnListIterator<E> extends ListIterator<E>, UnIterator<E> {
    /** Not allowed - this is supposed to be unmodifiable */
    @SuppressWarnings("deprecation")
    @Override @Deprecated default void remove() { throw new UnsupportedOperationException("Modification attempted"); }

    /** Not allowed - this is supposed to be unmodifiable */
    @Override @Deprecated default void set(E element) {
        throw new UnsupportedOperationException("Modification attempted");
    }

    /** Not allowed - this is supposed to be unmodifiable */
    @Override @Deprecated default void add(E element) {
        throw new UnsupportedOperationException("Modification attempted");
    }
}
