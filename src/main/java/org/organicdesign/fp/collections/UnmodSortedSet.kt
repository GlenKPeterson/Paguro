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

import java.util.SortedSet
import java.util.Spliterator
import java.util.Spliterator.*
import java.util.Spliterators.spliterator
import java.util.function.Predicate

/** An unmodifiable SortedSet.  */
@PurelyImplements("java.util.SortedSet")
interface UnmodSortedSet<E> : UnmodSet<E>, SortedSet<E>, UnmodSortedCollection<E> {

    /** Not allowed - this is supposed to be unmodifiable */
    @JvmDefault
    @Deprecated("Not allowed - this is supposed to be unmodifiable")
    override fun add(element: E): Boolean = throw UnsupportedOperationException("Modification attempted")

    /** Not allowed - this is supposed to be unmodifiable  */
    @JvmDefault
    @Deprecated("Not allowed - this is supposed to be unmodifiable")
    override fun addAll(elements: Collection<E>): Boolean = throw UnsupportedOperationException("Modification attempted")

    /** Not allowed - this is supposed to be unmodifiable  */
    @JvmDefault
    @Deprecated("Not allowed - this is supposed to be unmodifiable")
    override fun clear() { throw UnsupportedOperationException("Modification attempted") }

    @JvmDefault
    override fun headSet(toElement: E): UnmodSortedSet<E> = subSet(first(), toElement)

    /**
     * Iterates over contents in a guaranteed order.
     * {@inheritDoc}
     */
    @JvmDefault
    override fun iterator(): UnmodSortedIterator<E>

    /** Not allowed - this is supposed to be unmodifiable  */
    @Deprecated("Not allowed - this is supposed to be unmodifiable")
    @JvmDefault
    override fun remove(element: E): Boolean = throw UnsupportedOperationException("Modification attempted")

    /** Not allowed - this is supposed to be unmodifiable  */
    @Deprecated("Not allowed - this is supposed to be unmodifiable")
    @JvmDefault
    override fun removeAll(elements: Collection<E>): Boolean = throw UnsupportedOperationException("Modification attempted")

    /** Not allowed - this is supposed to be unmodifiable  */
    @Deprecated("Not allowed - this is supposed to be unmodifiable")
    @JvmDefault
    override fun removeIf(filter: Predicate<in E>): Boolean = throw UnsupportedOperationException("Modification attempted")

    /** Not allowed - this is supposed to be unmodifiable  */
    @Deprecated("Not allowed - this is supposed to be unmodifiable")
    @JvmDefault
    override fun retainAll(elements: Collection<E>): Boolean = throw UnsupportedOperationException("Modification attempted")

    // Having a size forces a set to be defined as a collection by adding items.
    // A set without a size is just a contains() function.
    // Overrides kotlin.collections.Collection.size
    @JvmDefault
    override val size: kotlin.Int

    @JvmDefault
    override fun spliterator(): Spliterator<E> = spliterator(this, SIZED or DISTINCT or ORDERED)

    override fun subSet(fromElement: E, toElement: E): UnmodSortedSet<E>

    /** {@inheritDoc}  */
    // Note: there is no simple default implementation because subSet() is exclusive of the given
    // end element and there is no way to reliably find an element exactly larger than last().
    // Otherwise we could just return subSet(fromElement, last());
    override fun tailSet(fromElement: E): UnmodSortedSet<E>
}
