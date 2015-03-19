package org.organicdesign.fp.experiments.collections;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public interface UnList<E> extends UnCollection<E>, List<E> {
    /** {@inheritDoc} */
    @Override UnIterator<E> iterator();

    /** Not allowed - we aren't making mutable defensive, non-typed copies! */
    @SuppressWarnings("deprecation")
    @Override @Deprecated default Object[] toArray() {
        throw new UnsupportedOperationException("remove");
    }

    /** Not allowed - we aren't making mutable defensive, non-typed copies! */
    @SuppressWarnings("deprecation")
    @Override @Deprecated default <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException("remove");
    }


    // Modification Operations

    /** Not allowed - this is supposed to be unmodifiable */
    @SuppressWarnings("deprecation")
    @Override @Deprecated default boolean add(E e) {
        throw new UnsupportedOperationException("Modification attempted");
    }

    /** Not allowed - this is supposed to be unmodifiable */
    @SuppressWarnings("deprecation")
    @Override @Deprecated default boolean remove(Object o) {
        throw new UnsupportedOperationException("Modification attempted");
    }

    /** Not allowed - this is supposed to be unmodifiable */
    @Override @Deprecated default boolean addAll(int index, Collection<? extends E> c) {
        throw new UnsupportedOperationException("Modification attempted");
    }
    /** Not allowed - this is supposed to be unmodifiable */
    @SuppressWarnings("deprecation")
    @Override @Deprecated default boolean addAll(Collection<? extends E> c) {
        throw new UnsupportedOperationException("Modification attempted");
    }

    /** Not allowed - this is supposed to be unmodifiable */
    @SuppressWarnings("deprecation")
    @Override @Deprecated default boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("Modification attempted");
    }

    /** Not allowed - this is supposed to be unmodifiable */
    @SuppressWarnings("deprecation")
    @Override @Deprecated default boolean removeIf(Predicate<? super E> filter) {
        throw new UnsupportedOperationException("Modification attempted");
    }

    /** Not allowed - this is supposed to be unmodifiable */
    @SuppressWarnings("deprecation")
    @Override @Deprecated default boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("Modification attempted");
    }

    /** Not allowed - this is supposed to be unmodifiable */
    @Override @Deprecated default void replaceAll(UnaryOperator<E> operator) {
        throw new UnsupportedOperationException("Modification attempted");
    }

    /** Not allowed - this is supposed to be unmodifiable */
    @Override @Deprecated default void sort(Comparator<? super E> c) {
        throw new UnsupportedOperationException("Modification attempted");
    }

    /** Not allowed - this is supposed to be unmodifiable */
    @SuppressWarnings("deprecation")
    @Override @Deprecated default void clear() {
        throw new UnsupportedOperationException("Modification attempted");
    }

    /** Not allowed - this is supposed to be unmodifiable */
    @Override @Deprecated default E set(int index, E element) {
        throw new UnsupportedOperationException("Modification attempted");
    }

    /** Not allowed - this is supposed to be unmodifiable */
    @Override @Deprecated default void add(int index, E element) {
        throw new UnsupportedOperationException("Modification attempted");
    }

    /** Not allowed - this is supposed to be unmodifiable */
    @Override @Deprecated default E remove(int index) {
        throw new UnsupportedOperationException("Modification attempted");
    }

    // List Iterators

    /** {@inheritDoc} */
    @Override UnListIterator<E> listIterator();

    /** {@inheritDoc} */
    @Override UnListIterator<E> listIterator(int index);

    // View

    /** {@inheritDoc} */
    @Override UnList<E> subList(int fromIndex, int toIndex);
}
