// Copyright 2015-04-13 PlanBase Inc. & Glen Peterson
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

import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.SortedSet;

/** An unmodifiable SortedSet. */
public interface UnmodSortedSet<E> extends UnmodSet<E>, SortedSet<E>, UnmodSortedCollection<E> {
    // ==================================================== Static ====================================================
    UnmodSet<Object> EMPTY = new UnmodSortedSet<Object>() {
        @Override public boolean contains(Object o) { return false; }
        @Override public int size() { return 0; }
        @Override public boolean isEmpty() { return true; }
        @Override public UnmodSortedIterator<Object> iterator() { return UnmodSortedIterator.empty(); }
        // Is this implementation a reason not to have an empty sorted set singleton?
        @Override public Comparator<? super Object> comparator() { return null; }
        @Override public UnmodSortedSet<Object> subSet(Object fromElement, Object toElement) { return this; }
        @Override public UnmodSortedSet<Object> tailSet(Object fromElement) { return this; }
        @Override public Object first() { throw new NoSuchElementException("Empty set"); }
        @Override public Object last() { throw new NoSuchElementException("Empty set"); }
    };
    @SuppressWarnings("unchecked")
    static <T> UnmodSortedSet<T> empty() { return (UnmodSortedSet<T>) EMPTY; }

    // =================================================== Instance ===================================================
    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override default UnmodSortedSet<E> headSet(E toElement) {
        // This is tricky because of the case where toElement > last()
        Comparator<? super E> comparator = comparator();
        if (comparator == null) {
            // By the rules of the constructor type signature, we either need a comparator,
            // or we need to accept only Comparable items into this collection, so this cast should
            // always work.
            Comparable<E> last = (Comparable<E>) last();
            if (last.compareTo(toElement) < 0) {
                return this;
            }
        } else if (comparator.compare(last(), toElement) < 0) {
            return this;
        }
        // All other cases are trivial.
        return subSet(first(), toElement);
    }

    /**
     Iterates over contents in a guaranteed order.
     {@inheritDoc}
     */
    @Override
    UnmodSortedIterator<E> iterator();

    /** {@inheritDoc} */
    @Override
    UnmodSortedSet<E> subSet(E fromElement, E toElement);

    /** {@inheritDoc} */
    // Note: there is no simple default implementation because subSet() is exclusive of the given
    // end element and there is no way to reliably find an element exactly larger than last().
    // Otherwise we could just return subSet(fromElement, last());
    @Override UnmodSortedSet<E> tailSet(E fromElement);
}
