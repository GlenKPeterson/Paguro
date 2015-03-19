package org.organicdesign.fp.experiments.collections;

import java.util.Collection;
import java.util.function.Predicate;

public interface UnCollection<E> extends Collection<E>, UnIterable<E> {

    /** {@inheritDoc} */
    @Override UnIterator<E> iterator();

    /** Not allowed - we aren't making mutable defensive, non-typed copies! */
    @Override @Deprecated default Object[] toArray() {
        throw new UnsupportedOperationException("remove");
    }

    /** Not allowed - we aren't making mutable defensive, non-typed copies! */
    @Override @Deprecated default <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException("remove");
    }

    // Modification Operations

    /** Not allowed - this is supposed to be unmodifiable */
    @Override @Deprecated default boolean add(E e) {
        throw new UnsupportedOperationException("Modification attempted");
    }

    /** Not allowed - this is supposed to be unmodifiable */
    @Override @Deprecated default boolean remove(Object o) {
        throw new UnsupportedOperationException("Modification attempted");
    }

    /** Not allowed - this is supposed to be unmodifiable */
    @Override @Deprecated default boolean addAll(Collection<? extends E> c) {
        throw new UnsupportedOperationException("Modification attempted");
    }

    /** Not allowed - this is supposed to be unmodifiable */
    @Override @Deprecated default boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("Modification attempted");
    }

    /** Not allowed - this is supposed to be unmodifiable */
    @Override @Deprecated default boolean removeIf(Predicate<? super E> filter) {
        throw new UnsupportedOperationException("Modification attempted");
    }

    /** Not allowed - this is supposed to be unmodifiable */
    @Override @Deprecated default boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("Modification attempted");
    }

    /** Not allowed - this is supposed to be unmodifiable */
    @Override @Deprecated default void clear() {
        throw new UnsupportedOperationException("Modification attempted");
    }
}
