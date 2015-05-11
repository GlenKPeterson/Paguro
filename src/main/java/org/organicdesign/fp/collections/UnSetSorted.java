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

import java.util.SortedSet;

/** An unmodifiable SortedSet. */
public interface UnSetSorted<E> extends UnSet<E>, SortedSet<E> {
    /** {@inheritDoc} */
    @Override default UnSetSorted<E> headSet(E toElement) { return subSet(first(), toElement); }

    /** {@inheritDoc} */
    @Override UnSetSorted<E> subSet(E fromElement, E toElement);

    /** {@inheritDoc} */
    @Override default UnSetSorted<E> tailSet(E fromElement) { return subSet(fromElement, last()); }
}
