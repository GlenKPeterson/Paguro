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

import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 A mutate-in-place interface using the same copy-on-write methods as {@link BaseList} and
 {@link ImList} so that you can treat mutable and immutable lists the same.
 You could think of this as a builder for an ImList, or just a stand-alone MutList
 that behaves similarly (extends {@link org.organicdesign.fp.xform.Transformable}).
 Being mutable, this is inherently NOT thread-safe.
 */
public interface MutList<E> extends BaseList<E> {
    /** {@inheritDoc} */
    @Override
    MutList<E> append(E val);

    // TODO: Is this a good idea?  Kotlin does this...
    // I'm concerned that we cannot provide good implementations for all these methods.
    // Will implementing some be a benefit?
    // Or just a temptation to use other deprecated methods?
    // I'm going to try this with a few easy methods to see how it goes.
    // These are all technically "optional" operations anyway.
    /**
     Ensures that this collection contains the specified element (optional operation).
     Returns true if this collection changed as a result of the call.
     */
    @SuppressWarnings("deprecation")
    @Override default boolean add(E val) {
        append(val);
        return true;
    }

    /**
     * Appends all the elements in the specified collection to the end of this list,
     * in the order that they are returned by the specified collection's iterator.
     */
    @SuppressWarnings("deprecation")
    @Override default boolean addAll(@NotNull Collection<? extends E> c) {
        concat(c);
        return true;
    }

    /** Returns a immutable version of this mutable list. */
    ImList<E> immutable();

    /** {@inheritDoc} */
    @NotNull
    @Override default MutList<E> concat(Iterable<? extends E> es) {
        for (E e : es) {
            this.append(e);
        }
        return this;
    }

    /** {@inheritDoc} */
    @Override
    MutList<E> replace(int idx, E e);

    /** {@inheritDoc} */
    @Override default MutList<E> reverse() {
        MutList<E> ret = PersistentVector.emptyMutable();
        UnmodListIterator<E> iter = listIterator(size());
        while (iter.hasPrevious()) {
            ret.append(iter.previous());
        }
        return ret;
    }
}
