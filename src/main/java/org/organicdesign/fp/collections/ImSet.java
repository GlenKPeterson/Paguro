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

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** An immutable set with no guarantees about its ordering */
public interface ImSet<E> extends BaseSet<E> {

    /** Returns a mutable version of this immutable set. */
    @NotNull MutSet<E> mutable();

    /**
     Adds an element, returning a modified version of the set (leaving the original set unchanged).
     If the element already exists in this set, the new value overwrites the old one.  If the new
     element is the same as an old element (based on the address of that item in memory, not an
     equals test), the old set is returned unchanged.

     @param e the element to add to this set
     @return a new set with the element added (see note above about adding duplicate elements).
     */
    @NotNull
    @Override ImSet<E> put(E e);

    @Contract(pure = true)
    default @NotNull ImSet<E> union(@Nullable Iterable<? extends E> iter) {
        return iter == null ? this
                            : mutable().union(iter).immutable();
    }

    /** {@inheritDoc} */
    @NotNull
    @Override ImSet<E> without(E key);
}
