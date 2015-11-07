// Copyright 2015 PlanBase Inc. & Glen Peterson
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package org.organicdesign.fp.collections;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.function.Predicate;

/**
 * A collection is an {@link java.lang.Iterable} with a size (a size() method).  UnmodCollection is
 * an unmodifiable version of {@link java.util.Collection} which formalizes the return type of
 * Collections.unmodifiableCollection()
 *
 * {@inheritDoc}
 */
public interface UnmodCollection<E> extends Collection<E>, UnmodIterable<E> {

    // ========================================== Static ==========================================
    UnmodCollection<Object> EMPTY = new UnmodCollection<Object>() {
        @Override public boolean contains(Object o) { return false; }
        @Override public int size() { return 0; }
        @Override public boolean isEmpty() { return true; }
        @Override public UnmodIterator<Object> iterator() { return UnmodIterator.empty(); }
    };
    @SuppressWarnings("unchecked")
    static <T> UnmodCollection<T> empty() { return (UnmodCollection<T>) EMPTY; }

    /**
     Implements equals and hashCode() methods to make defining unmod sets easier, especially for
     implementing Map.keySet() and such.
     */
    abstract class AbstractUnmodCollection<T> implements UnmodCollection<T> {
        @Override public boolean equals(Object other) {
            if (this == other) { return true; }
            if ( !(other instanceof Collection) ) { return false; }
            Collection that = (Collection) other;
            return (size() == that.size()) &&
                   containsAll(that);
        }

        @Override public int hashCode() { return UnmodIterable.hashCode(this); }
    }
    // ========================================= Instance =========================================
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

// I don't think that this should be implemented here.  It's a core function so each implementation
// of the interface should implement it
//    /**
//     This is quick for sets O(1) or O(log n), but slow for Lists O(n).
//
//     {@inheritDoc}
//     */
//    @Override default boolean contains(Object o) {
//        for (Object item : this) {
//            if (Objects.equals(item, o)) { return true; }
//        }
//        return false;
//    }

    /**
     The default implementation of this method has O(this.size() + that.size()) performance.

     {@inheritDoc}
     */
    @Override default boolean containsAll(Collection<?> c) {
        // Faster to create a HashSet and call containsAll on that because it's
        // O(this size PLUS that size), whereas looping through both would be
        // O(this size TIMES that size).
        return  ( (c == null) || (c.size() < 1) ) ? true :
                (size() < 1) ? false :
//                (ts instanceof Set) ? ((Set) ts).containsAll(c) :
//                (ts instanceof Map) ? ((Map) ts).entrySet().containsAll(c) :
                new HashSet<>(this).containsAll(c);
    }

//boolean	equals(Object o)
//int	hashCode()

    /** {@inheritDoc} */
    @Override default boolean isEmpty() { return size() == 0; }

    /** An unmodifiable iterator {@inheritDoc} */
    @Override
    UnmodIterator<E> iterator();

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
        return this.toArray(new Object[size()]);
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
        if (as.length < size()) {
            as = (T[]) new Object[size()];
        }
        Iterator<E> iter = iterator();
        for (int i = 0; i < size(); i++) {
            as[i] = (T) iter.next();
        }
        if (size() < as.length) {
            Arrays.fill(as, size(), as.length, null);
        }
        return as;
    }
}
