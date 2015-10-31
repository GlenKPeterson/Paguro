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

/** An immutable sorted set interface */
public interface ImSortedSet<E> extends ImSet<E>, UnmodSortedSet<E> {
    /** {@inheritDoc} */
    @Override ImSortedSet<E> put(E e);

    /** {@inheritDoc} */
    @Override ImSortedSet<E> without(E key);

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override default ImSortedSet<E> headSet(E toElement) {
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
     Iterates over contents in a guaranteed order. {@inheritDoc}
     */
    @Override UnmodSortedIterator<E> iterator();

//    /**
//     * This method goes against Josh Bloch's Item 25: "Prefer Lists to Arrays", but is provided for
//     * backwards compatibility in some performance-critical situations.
//     * {@inheritDoc}
//     */
//    @Override default Object[] toArray() { return UnmodSortedSet.super.toArray(); }

//    /** A sequence of the items contained in this set in order.  Just returns this SetOrdered. */
//    @Override default Sequence<E> seq() { return this; }

    /**
     Return the elements in this set from the start element (inclusive) to the end element
     (exclusive)
     */
    @Override ImSortedSet<E> subSet(E fromElement, E toElement);

    /** {@inheritDoc} */
    // Note: there is no simple default implementation because subSet() is exclusive of the given
    // end element and there is no way to reliably find an element exactly larger than last().
    // Otherwise we could just return subSet(fromElement, last());
    @Override ImSortedSet<E> tailSet(E fromElement);

    @Override default ImSortedSet<E> union(Iterable<? extends E> iter) {
        ImSortedSet<E> ret = this;
        for (E e : iter) {
            if (!ret.contains(e)) { ret = ret.put(e); }
        }
        return ret;
    }
}
