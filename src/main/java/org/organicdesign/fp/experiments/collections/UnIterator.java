package org.organicdesign.fp.experiments.collections;

import java.util.Iterator;

public interface UnIterator<E> extends Iterator<E> {
    /** Not allowed - this is supposed to be unmodifiable */
    @Override @Deprecated default void remove() {
        throw new UnsupportedOperationException("Modification attempted");
    }
}
