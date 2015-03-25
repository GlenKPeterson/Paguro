package org.organicdesign.fp.experiments.collections;

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
     * The default implementation of this method has O(this.size() * that.size()) performance
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
     * Grudgingly provided for backward compatibility, but deprecated because a type-safe version of this method is
     * available (this method isn't going away).
     * If you're going to go against Josh Bloch's Item 25: "Prefer Lists to Arrays", at least use the type-safe version
     * of this method.
     *
     * {@inheritDoc}
     */
    @Deprecated @Override default Object[] toArray() {
        Object[] os = new Object[size()];
        UnIterator iter = iterator();
        for (int i = 0; i < size(); i++) {
            os[i] = iter.next();
        }
        return os;
    }

    /**
     * This method goes against Josh Bloch's Item 25: "Prefer Lists to Arrays", but is provided for backwards
     * compatibility.  Unlike the collections method this overrides, you can pass it null, though you'll want to cast
     * it to the appropriate type as in
     *
     * <code>String[] ss = c.toArray((String[]) null);</code>
     *
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override default <T> T[] toArray(T[] as) {
        if ( (as == null) || (as.length < size()) ) {
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
}
