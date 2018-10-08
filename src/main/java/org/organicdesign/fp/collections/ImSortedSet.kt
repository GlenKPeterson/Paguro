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
package org.organicdesign.fp.collections

/** An immutable sorted set interface  */
interface ImSortedSet<E> : BaseSet<E>, UnmodSortedSet<E> {
    override fun put(item: E): ImSortedSet<E>

    override fun without(key: E): ImSortedSet<E>

    @JvmDefault
    override fun headSet(toElement: E): ImSortedSet<E> = subSet(first(), toElement)

    @JvmDefault
    override fun iterator(): UnmodSortedIterator<E>

    @JvmDefault
    override val size: Int

    /**
     * Return the elements in this set from the start element (inclusive) to the end element
     * (exclusive)
     */
    override fun subSet(fromElement: E, toElement: E): ImSortedSet<E>

    // Note: there is no simple default implementation because subSet() is exclusive of the given
    // end element and there is no way to reliably find an element exactly larger than last().
    // Otherwise we could just return subSet(fromElement, last());
    override fun tailSet(fromElement: E): ImSortedSet<E>

    @JvmDefault
    override fun union(iter: Iterable<E>?): ImSortedSet<E> =
            if (iter == null) {
                this
            } else {
                var ret = this
                for (e in iter) {
                    if (!ret.contains(e)) {
                        ret = ret.put(e)
                    }
                }
                ret
            }
}
