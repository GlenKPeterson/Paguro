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
public interface UnSetOrdered<E> extends UnSet<E>, SortedSet<E>, UnCollectionOrdered<E> {
    // ==================================================== Static ====================================================
    UnSet<Object> EMPTY = new UnSetOrdered<Object>() {
        @Override public boolean contains(Object o) { return false; }
        @Override public int size() { return 0; }
        @Override public boolean isEmpty() { return true; }
        @Override public UnIteratorOrdered<Object> iterator() { return UnIteratorOrdered.empty(); }
        @Override public Comparator<? super Object> comparator() { return null; }
        @Override public UnSetOrdered<Object> subSet(Object fromElement, Object toElement) { return this; }
        @Override public Object first() { throw new NoSuchElementException("Empty set"); }
        @Override public Object last() { throw new NoSuchElementException("Empty set"); }
    };
    @SuppressWarnings("unchecked")
    static <T> UnSetOrdered<T> empty() { return (UnSetOrdered<T>) EMPTY; }

    // =================================================== Instance ===================================================
    /** {@inheritDoc} */
    @Override default UnSetOrdered<E> headSet(E toElement) { return subSet(first(), toElement); }

    /**
     Iterates over contents in a guaranteed order.
     {@inheritDoc}
     */
    @Override UnIteratorOrdered<E> iterator();

    /** {@inheritDoc} */
    @Override
    UnSetOrdered<E> subSet(E fromElement, E toElement);

    /** {@inheritDoc} */
    @Override default UnSetOrdered<E> tailSet(E fromElement) { return subSet(fromElement, last()); }
}
