// Copyright 2016 PlanBase Inc. & Glen Peterson
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

/**
 Interface for mutable (hash) set builder.
 */
public interface MutSet<E> extends BaseSet<E> {

    /** Returns an immutable version of this immutable set. */
    ImSet<E> immutable();

    /** {@inheritDoc} */
    @NotNull
    @Override
    MutSet<E> put(E val);

    /** {@inheritDoc} */
    @Override
    @Contract(mutates = "this")
    default @NotNull MutSet<E> union(Iterable<? extends E> iter) {
        if (iter != null) {
            for (E item : iter) {
                put(item);
            }
        }
        return this;
    }

    /** {@inheritDoc} */
    @NotNull
    @Override
    MutSet<E> without(E key);

    /**
     * Efficiently adds items to this MutSet.  Ordering is ignored.
     * @param items the values to add
     * @return a new MutSet with the additional items.
     */
    @Override
    @Contract(mutates = "this")
    default @NotNull MutSet<E> concat(@Nullable Iterable<? extends E> items) {
        return union(items);
    }

    /**
     * Efficiently adds items to this MutSet.  Ordering is ignored.
     * @param items the values to add
     * @return a new MutSet with the additional items.
     */
    @Override
    @Contract(mutates = "this")
    default @NotNull MutSet<E> precat(@Nullable Iterable<? extends E> items) {
        return union(items);
    }
}
