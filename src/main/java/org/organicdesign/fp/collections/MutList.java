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

    /** Returns a immutable version of this mutable list. */
    ImList<E> immutable();

    /** {@inheritDoc} */
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
