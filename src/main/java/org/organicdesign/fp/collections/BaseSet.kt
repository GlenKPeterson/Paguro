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
package org.organicdesign.fp.collections

/**
 * Adds copy-on-write, "fluent interface" methods to [UnmodSet].
 * Lowest common ancestor of [MutSet], [ImSet], and [ImSortedSet].
 */
interface BaseSet<E> : UnmodSet<E> {
    /**
     * Adds an element.
     * If the element already exists in this set, the new value overwrites the old one.  If the new
     * element is the same as an old element (based on the address of that item in memory, not an
     * equals test), the old set may be returned unchanged.
     *
     * @param item the element to add to this set
     * @return a new set with the element added (see note above about adding duplicate elements).
     */
    fun put(item: E): BaseSet<E>

    @JvmDefault
    override val size: kotlin.Int

    /** Returns a new set containing all the items.  */
    fun union(iter: Iterable<E>?): BaseSet<E>
    //    {
    //        return concat(iter).toImSet();
    //    }

    /** Removes this key from the set  */
    fun without(key: E): BaseSet<E>
}
