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

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.organicdesign.fp.function.Fn0;
import org.organicdesign.fp.oneOf.Option;

/** Immutable copy-on-write list */
public interface ImList<E> extends BaseList<E> {
// Inherited correctly - there is no ImIterator.
// UnmodSortedIterator<E> iterator();
// UnmodListIterator<E> listIterator(int index);

// Inherited correctly and need to be implemented by the implementing class
// int size() {
// boolean equals(Object o) {
// int hashCode() {
// E get(int index) {

    /** {@inheritDoc} */
    @Override
    @NotNull ImList<E> append(E e);

    /** {@inheritDoc} */
    @Override
    @Contract(pure = true)
    default @NotNull ImList<E> appendSome(
            @NotNull Fn0<? extends @NotNull Option<E>> supplier
    ) {
        return supplier.apply().match(
                (it) -> append(it),
                () -> this
        );
    }

    /** {@inheritDoc} */
    @Override
    @Contract(pure = true)
    default @NotNull ImList<E> concat(@Nullable Iterable<? extends E> es) {
        return mutable().concat(es).immutable();
    }

    /** Returns a mutable list (builder) */
    @NotNull MutList<E> mutable();

    /** {@inheritDoc} */
    @Contract(pure = true)
    @NotNull ImList<E> replace(int idx, E e);

    /** {@inheritDoc} */
    default @NotNull ImList<E> reverse() {
        return mutable().reverse().immutable();
    }
}
