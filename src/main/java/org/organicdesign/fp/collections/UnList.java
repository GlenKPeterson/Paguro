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

import org.organicdesign.fp.function.Function2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import static org.organicdesign.fp.StaticImports.un;

/**
 * An unmodifiable version of {@link java.util.List} which formalizes the return type of
 * Collections.unmodifiableList()
 *
 * {@inheritDoc}
 */
public interface UnList<E> extends List<E>, UnCollection<E>, UnIterableOrdered<E> {

    // ==================================================== Static ====================================================

    /** The EMPTY list - a sentinel value for use in == comparisons. */
    UnList<Object> EMPTY = new UnList<Object>() {
        @Override public UnListIterator<Object> listIterator(int index) { return UnListIterator.empty(); }
        @Override public int size() { return 0; }
        @Override public Object get(int index) { throw new IndexOutOfBoundsException(); }
    };

    /** Returns a type-aware version of the EMPTY list. */
    @SuppressWarnings("unchecked")
    static <T> UnList<T> empty() { return (UnList<T>) EMPTY; }

    /**
     Apply the given function against all unique pairings of items in the list.  Does this belong on Function2 instead
     of List?
     */
    static <T> void permutations(List<T> items, Function2<? super T,? super T,?> f) {
        for (int i = 0; i < items.size(); i++) {
            for (int j = i + 1; j < items.size(); j++) {
                f.apply(items.get(i), items.get(j));
            }
        }
    }

    // =================================================== Instance ===================================================

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
     The default implementation of this method has O(this.size()) performance.

     {@inheritDoc}
     */
    @Override default boolean contains(Object o) { return UnCollection.contains(this, o); }

    /**
     The default implementation of this method has O(this.size() * that.size()) performance.

     {@inheritDoc}
     */
    @Override default boolean containsAll(Collection<?> c) { return UnCollection.containsAll(this, c); }

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
    @Override default UnIteratorOrdered<E> iterator() { return listIterator(0); }

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
        if ( (fromIndex == 0) && (toIndex == size()) ) {
            return this;
        }
// I think this should be the implementation in ImList.
//        PersistentVector<E> pv = PersistentVector.empty();
//        for (int i = fromIndex; i < toIndex; i++) {
//            pv = pv.appendOne(this.get(i));
//        }
//        return pv;
        List<E> ls = new ArrayList<>();
        for (int i = fromIndex; i < toIndex; i++) {
            ls.add(this.get(i));
        }
        return un(ls);
    }

    /**
     * This method goes against Josh Bloch's Item 25: "Prefer Lists to Arrays", but is provided for backwards
     * compatibility in some performance-critical situations.  If you really need an array, consider using the somewhat
     * type-safe version of this method instead, but read the caveats first.
     * {@inheritDoc}
     */
    @Override default Object[] toArray() { return UnCollection.toArray(this); }

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
    @Override default <T> T[] toArray(T[] as) { return UnCollection.toArray(this, as); }

//Methods inherited from interface java.util.Collection
//parallelStream, removeIf, stream

    /** Not allowed - this is supposed to be unmodifiable */
    @SuppressWarnings("deprecation")
    @Override @Deprecated default boolean removeIf(Predicate<? super E> filter) {
        throw new UnsupportedOperationException("Modification attempted");
    }

//Methods inherited from interface java.lang.Iterable
//forEach

}
