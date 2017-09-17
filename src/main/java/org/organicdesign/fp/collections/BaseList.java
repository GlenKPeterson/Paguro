// Copyright 2017 PlanBase Inc. & Glen Peterson
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

import org.organicdesign.fp.oneOf.Option;

/**
 Adds copy-on-write, "fluent interface" methods to {@link UnmodList}.
 Lowest common ancestor of {@link MutList} and {@link ImList}.
 */
public interface BaseList<E> extends UnmodList<E> {
    /**
     Adds one item to the end of the ImList.

     @param e the value to insert
     @return a new ImList with the additional item at the end.
     */
    BaseList<E> append(E e);

    /**
     Efficiently adds items to the end of this ImList.

     @param es the values to insert
     @return a new ImList with the additional items at the end.
     */
    @Override BaseList<E> concat(Iterable<? extends E> es);

    // I don't know if this is a good idea or not and I don't want to have to support it if not.
//    /**
//     * Returns the item at this index, but takes any Number as an argument.
//     * @param n the zero-based index to get from the vector.
//     * @return the value at that index.
//     */
//    default E get(Number n) { return get(n.intValue()); }

    /**
     * Returns the item at this index.
     * @param i the zero-based index to get from the vector.
     * @param notFound the value to return if the index is out of bounds.
     * @return the value at that index, or the notFound value.
     */
    default E get(int i, E notFound) {
        if (i >= 0 && i < size())
            return get(i);
        return notFound;
    }

    /** {@inheritDoc} */
    @Override default Option<E> head() {
        return size() > 0 ? Option.some(get(0)) : Option.none();
    }

    /**
     Replace the item at the given index.  Note: i.replace(i.size(), o) used to be equivalent to
     i.concat(o), but it probably won't be for the RRB tree implementation, so this will change too.

     @param idx the index where the value should be stored.
     @param e the value to store
     @return a new ImList with the replaced item
     */
    // TODO: Don't make i.replace(i.size(), o) equivalent to i.concat(o)
    BaseList<E> replace(int idx, E e);

    /** Returns a reversed copy of this list. */
    BaseList<E> reverse();
}
