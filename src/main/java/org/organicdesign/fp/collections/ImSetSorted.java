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

/** An immutable sorted set interface */
public interface ImSetSorted<E> extends ImSet<E>, UnSetSorted<E> {
    /** {@inheritDoc} */
    @Override ImSetSorted<E> put(E e);

    /** {@inheritDoc} */
    @Override ImSetSorted<E> disjoin(E key);

    /** Return the elements in this set up (but excluding) to the given element */
    @Override default ImSetSorted<E> headSet(E toElement) { return subSet(first(), toElement); }

    /**
     Iterates over contents in a guaranteed order. {@inheritDoc}
     */
    @Override UnIteratorOrdered<E> iterator();

    /** Return the elements in this set from the start element (inclusive) to the end element (exclusive) */
    @Override ImSetSorted<E> subSet(E fromElement, E toElement);

    /** Return the elements in this from the given element to the end */
    @Override default ImSetSorted<E> tailSet(E fromElement) { return subSet(fromElement, last()); }

}
