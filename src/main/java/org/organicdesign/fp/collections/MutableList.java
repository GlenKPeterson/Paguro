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
 A mutate-in-place interface using copy-on-write methods so that you can treat mutable and
 immutable lists the same.  Being mutable, this is inherently NOT thread-safe.
 */
public interface MutableList<E> extends BaseList<E> {
    /** {@inheritDoc} */
    @Override MutableList<E> append(E val);

    /** Returns a immutable version of this mutable list. */
    ImList<E> immutable();

    /** {@inheritDoc} */
    @Override default MutableList<E> concat(Iterable<? extends E> es) {
        for (E e : es) {
            this.append(e);
        }
        return this;
    }

    /** {@inheritDoc} */
    @Override MutableList<E> replace(int idx, E e);

    /** {@inheritDoc} */
    @Override default MutableList<E> reverse() {
        MutableList<E> ret = PersistentVector.emptyMutable();
        UnmodListIterator<E> iter = listIterator(size());
        while (iter.hasPrevious()) {
            ret.append(iter.previous());
        }
        return ret;
    }
}
