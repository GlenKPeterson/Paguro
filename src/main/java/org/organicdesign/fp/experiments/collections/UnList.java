package org.organicdesign.fp.experiments.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

/**
 * An unmodifiable version of {@link java.util.List} which formalizes the return type of
 * Collections.unmodifiableList()
 *
 * {@inheritDoc}
 */
public interface UnList<E> extends List<E>, UnCollection<E> {

    /** Not allowed - this is supposed to be unmodifiable */
    @SuppressWarnings("deprecation")
    @Override @Deprecated default boolean add(E e) {
        throw new UnsupportedOperationException("Modification attempted");
    }

    /** Not allowed - this is supposed to be unmodifiable */
    @Override @Deprecated default void add(int index, E element) {
        throw new UnsupportedOperationException("Modification attempted");
    }

    /** Not allowed - this is supposed to be unmodifiable */
    @SuppressWarnings("deprecation")
    @Override @Deprecated default boolean addAll(Collection<? extends E> c) {
        throw new UnsupportedOperationException("Modification attempted");
    }

    /** Not allowed - this is supposed to be unmodifiable */
    @Override @Deprecated default boolean addAll(int index, Collection<? extends E> c) {
        throw new UnsupportedOperationException("Modification attempted");
    }

    /** Not allowed - this is supposed to be unmodifiable */
    @SuppressWarnings("deprecation")
    @Override @Deprecated default void clear() {
        throw new UnsupportedOperationException("Modification attempted");
    }

    /**
     * The default implementation of this method has O(this.size()) performance.
     *
     * {@inheritDoc}
     */
    @Override default boolean contains(Object o) {
        UnIterator<E> iter = iterator();
        while (iter.hasNext()) {
            if (Objects.equals(iter.next(), o)) {
                return true;
            }
        }
        return false;
    }

    /**
     * The default implementation of this method has O(this.size() * that.size()) performance.
     *
     * {@inheritDoc}
     * */
    @Override default boolean containsAll(Collection<?> c) {
        Iterator iter = c.iterator();
        while (iter.hasNext()) {
            if (!contains(iter.next())) {
                return false;
            }
        }
        return true;
    }

//boolean	equals(Object o)
//E	get(int index)
//int	hashCode()

    /**
     * The default implementation of this method has O(this.size()) performance.
     *
     * {@inheritDoc}
     */
    @Override default int indexOf(Object o) {
        for (int i = 0; i < size(); i++) {
            if (Objects.equals(get(i), o)) {
                return i;
            }
        }
        return -1;
    }

    /** {@inheritDoc} */
    @Override default boolean isEmpty() { return size() == 0; }

    /** {@inheritDoc} */
    @Override default UnIterator<E> iterator() { return listIterator(0); }

    /**
     * The default implementation of this method has O(this.size()) performance.
     *
     * {@inheritDoc}
     */
    @Override default int lastIndexOf(Object o) {
        for (int i = size() - 1; i > -1; i--) {
            if (Objects.equals(get(i), o)) {
                return i;
            }
        }
        return -1;
    }

    /** {@inheritDoc} */
    @Override default UnListIterator<E> listIterator() { return listIterator(0); }


    /** {@inheritDoc} */
    @Override UnListIterator<E> listIterator(int index);

    /** Not allowed - this is supposed to be unmodifiable */
    @Override @Deprecated default E remove(int index) {
        throw new UnsupportedOperationException("Modification attempted");
    }

    /** Not allowed - this is supposed to be unmodifiable */
    @SuppressWarnings("deprecation")
    @Override @Deprecated default boolean remove(Object o) {
        throw new UnsupportedOperationException("Modification attempted");
    }

    /** Not allowed - this is supposed to be unmodifiable */
    @SuppressWarnings("deprecation")
    @Override @Deprecated default boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("Modification attempted");
    }

    /** Not allowed - this is supposed to be unmodifiable */
    @Override @Deprecated default void replaceAll(UnaryOperator<E> operator) {
        throw new UnsupportedOperationException("Modification attempted");
    }

    /** Not allowed - this is supposed to be unmodifiable */
    @SuppressWarnings("deprecation")
    @Override @Deprecated default boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("Modification attempted");
    }

    /** Not allowed - this is supposed to be unmodifiable */
    @Override @Deprecated default E set(int index, E element) {
        throw new UnsupportedOperationException("Modification attempted");
    }

//int	size()

    /** Not allowed - this is supposed to be unmodifiable */
    @Override @Deprecated default void sort(Comparator<? super E> c) {
        throw new UnsupportedOperationException("Modification attempted");
    }

//default Spliterator<E> spliterator()

    /** {@inheritDoc} */
    @Override default UnList<E> subList(int fromIndex, int toIndex) {
        if ( (fromIndex == 0) && (toIndex == (size() - 1)) ) {
            return this;
        }
        if (fromIndex == (size() - 1)) {
            return empty();
        }
        List<E> ls = new ArrayList<>();
        ls.addAll(this);
        return (UnList<E>) ls.subList(fromIndex, toIndex);
    }

    /** {@inheritDoc} */
    @Override default Object[] toArray() {
        Object[] os = new Object[size()];
        UnIterator iter = iterator();
        for (int i = 0; i < size(); i++) {
            os[i] = iter.next();
        }
        return os;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override default <T> T[] toArray(T[] as) {
        if (as == null) {
            throw new IllegalArgumentException("You can't pass null to this method.");
        }
        if ( (as.length < size()) ) {
            as = (T[]) new Object[size()];
        }
        UnIterator<E> iter = iterator();
        int i = 0;
        for (; i < size(); i++) {
            as[i] = (T) iter.next();
        }
        for (; i < size(); i++) {
            as[i] = null;
        }
        return as;
    }

//Methods inherited from interface java.util.Collection
//parallelStream, removeIf, stream

    /** Not allowed - this is supposed to be unmodifiable */
    @SuppressWarnings("deprecation")
    @Override @Deprecated default boolean removeIf(Predicate<? super E> filter) {
        throw new UnsupportedOperationException("Modification attempted");
    }

//Methods inherited from interface java.lang.Iterable
//forEach

    // ==================================================== STATIC ====================================================
    UnList<Object> EMPTY = new UnList<Object>() {
        @Override public UnListIterator<Object> listIterator(int index) { return UnListIterator.empty(); }
        @Override public int size() { return 0; }
        @Override public Object get(int index) { throw new IndexOutOfBoundsException(); }
    };

    @SuppressWarnings("unchecked")
    static <T> UnList<T> empty() { return (UnList<T>) EMPTY; }
}
