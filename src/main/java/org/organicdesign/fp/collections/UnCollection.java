package org.organicdesign.fp.collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * An unmodifiable version of {@link java.util.Collection} which formalizes the return type of
 * Collections.unmodifiableCollection()
 *
 * {@inheritDoc}
 */
public interface UnCollection<E> extends Collection<E> {

    // Methods are listed in the same order as the javadocs.

    /** Not allowed - this is supposed to be unmodifiable */
    @Override @Deprecated default boolean add(E e) {
        throw new UnsupportedOperationException("Modification attempted");
    }

    /** Not allowed - this is supposed to be unmodifiable */
    @Override @Deprecated default boolean addAll(Collection<? extends E> c) {
        throw new UnsupportedOperationException("Modification attempted");
    }

    /** Not allowed - this is supposed to be unmodifiable */
    @Override @Deprecated default void clear() {
        throw new UnsupportedOperationException("Modification attempted");
    }

    /**
     * This is quick for sets and maps, but it's slow for Lists.
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
     * This is quick for sets and maps, but it's slow for Lists.
     *
     * {@inheritDoc}
     */
    @Override default boolean containsAll(Collection<?> c) { return containsAll(this, c); }

//boolean	equals(Object o)
//int	hashCode()

    /** {@inheritDoc} */
    @Override default boolean isEmpty() { return size() == 0; }

    /** An unmodifiable iterator {@inheritDoc} */
    @Override UnIterator<E> iterator();

//default Stream<E> parallelStream()

    /** Not allowed - this is supposed to be unmodifiable */
    @Override @Deprecated default boolean remove(Object o) {
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

//int	size()
//default Spliterator<E> spliterator()
//default Stream<E>	stream()

    /**
     * This method goes against Josh Bloch's Item 25: "Prefer Lists to Arrays", but is provided for backwards
     * compatibility in some performance-critical situations.  If you really need an array, consider using the somewhat
     * type-safe version of this method instead, but read the caveats first.
     *
     * {@inheritDoc}
     */
    @Override default Object[] toArray() {
        Object[] os = new Object[size()];
        UnIterator iter = iterator();
        for (int i = 0; i < size(); i++) {
            os[i] = iter.next();
        }
        return os;
    }

    /**
     * This method goes against Josh Bloch's Item 25: "Prefer Lists to Arrays", but is provided for backwards
     * compatibility in some performance-critical situations.  If you need to create an array (you almost always do)
     * then the best way to use this method is:
     *
     * <code>MyThing[] things = col.toArray(new MyThing[coll.size()]);</code>
     *
     * Calling this method any other way causes unnecessary work to be done - an extra memory allocation and potential
     * garbage collection if the passed array is too small, extra effort to fill the end of the array with nulls if it
     * is too large.
     *
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override default <T> T[] toArray(T[] as) {
        if (as == null) {
            throw new IllegalArgumentException("You can't pass null to this method.");
        }
        if (as.length < size()) {
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

//forEach

    // ==================================================== Static ====================================================

    /**
     * This method goes against Josh Bloch's Item 25: "Prefer Lists to Arrays", but is provided for backwards
     * compatibility in some performance-critical situations.  If you really need an array, consider using the somewhat
     * type-safe version of this method instead, but read the caveats first.
     */
    static Object[] toArray(UnCollection uc) {
        Object[] os = new Object[uc.size()];
        UnIterator iter = uc.iterator();
        for (int i = 0; i < uc.size(); i++) {
            os[i] = iter.next();
        }
        return os;
    }

    /**
     * This method goes against Josh Bloch's Item 25: "Prefer Lists to Arrays", but is provided for backwards
     * compatibility in some performance-critical situations.  If you need to create an array (you almost always do)
     * then the best way to use this method is:
     *
     * <code>MyThing[] things = col.toArray(new MyThing[coll.size()]);</code>
     *
     * Calling this method any other way causes unnecessary work to be done - an extra memory allocation and potential
     * garbage collection if the passed array is too small, extra effort to fill the end of the array with nulls if it
     * is too large.
     */
    @SuppressWarnings("unchecked")
    static <T> T[] toArray(UnCollection uc, T[] as) {
        if (as == null) {
            throw new IllegalArgumentException("You can't pass null to this method.");
        }
        if (as.length < uc.size()) {
            as = (T[]) new Object[uc.size()];
        }
        UnIterator<T> iter = uc.iterator();
        int i = 0;
        for (; i < uc.size(); i++) {
            as[i] = iter.next();
        }
        for (; i < uc.size(); i++) {
            as[i] = null;
        }
        return as;
    }

    /**
     * This is quick for sets and maps, but it's slow for Lists.
     */
    static <T> boolean containsAll(Collection<T> ts, Collection<?> c) {
        Iterator iter = c.iterator();
        while (iter.hasNext()) {
            //noinspection SuspiciousMethodCalls
            if (!ts.contains(iter.next())) {
                return false;
            }
        }
        return true;
    }

    static UnCollection<Object> EMPTY = new UnCollection<Object>() {
        @Override public boolean contains(Object o) { return false; }
        @Override public int size() { return 0; }
        @Override public boolean isEmpty() { return true; }
        @Override public UnIterator<Object> iterator() { return UnIterator.empty(); }
    };
    @SuppressWarnings("unchecked")
    static <T> UnCollection<T> empty() { return (UnCollection<T>) EMPTY; }

}
