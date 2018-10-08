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
package org.organicdesign.fp.collections

import java.util.Spliterator
import java.util.Spliterators

import java.util.Spliterator.DISTINCT
import java.util.Spliterator.SIZED

/** An unmodifiable set */
@PurelyImplements("java.util.Set")
interface UnmodSet<E> : UnmodCollection<E>, Set<E> {

    /** Not allowed - this is supposed to be unmodifiable */
    @JvmDefault
    @Deprecated("Not allowed - this is supposed to be unmodifiable",
                ReplaceWith("Use a MutSet or ImSet and call .append()"))
    override fun add(element: E): Boolean = throw UnsupportedOperationException("Modification attempted")

    /** Not allowed - this is supposed to be unmodifiable  */
    @JvmDefault
    @Deprecated("Not allowed - this is supposed to be unmodifiable",
                ReplaceWith("Use a MutSet or ImSet and call .concat()"))
    override fun addAll(elements: Collection<E>): Boolean = throw UnsupportedOperationException("Modification attempted")

    /** Not allowed - this is supposed to be unmodifiable  */
    @JvmDefault
    @Deprecated("Not allowed - this is supposed to be unmodifiable",
                ReplaceWith("Make a new set"))
    override fun clear() { throw UnsupportedOperationException("Modification attempted") }

    /**
     Returns true if the set contains the given item.  This is the defining method of a set.
     Sets have to override this because the default implementation in UnmodCollection is O(n)
     whereas a sorted set should be O(log n) or O(1).
     */
    override fun contains(element: E): Boolean

    @JvmDefault
    override fun containsAll(elements: Collection<E>): Boolean {
        for (elem in elements) {
            if (!this.contains(elem)) { return false; }
        }
        return true
    }
// boolean	equals(Object o)
// int	hashCode()

    /**
     This is a convenience method inherited from Collection that returns true if size == 0 (if
     this set contains no elements).
     */
    @JvmDefault
    override fun isEmpty(): Boolean = size == 0

    /**
     Iterates over contents with no guarantees about their ordering.
     {@inheritDoc}
     */
    @JvmDefault
    override fun iterator(): UnmodIterator<E>

    /** Not allowed - this is supposed to be unmodifiable  */
    @Deprecated("Not allowed - this is supposed to be unmodifiable",
                ReplaceWith("Use a MutSet and call .remove()"))
    @JvmDefault
    override fun remove(element: E): Boolean = throw UnsupportedOperationException("Modification attempted")

    /** Not allowed - this is supposed to be unmodifiable  */
    @Deprecated("Not allowed - this is supposed to be unmodifiable",
                ReplaceWith("Make a new set"))
    @JvmDefault
    override fun removeAll(elements: Collection<E>): Boolean = throw UnsupportedOperationException("Modification attempted")

    /** Not allowed - this is supposed to be unmodifiable  */
    @Deprecated("Not allowed - this is supposed to be unmodifiable",
                ReplaceWith("Use .filter().toIm/MutSet"))
    @JvmDefault
    override fun removeIf(filter: (E) -> Boolean): Boolean = throw UnsupportedOperationException("Modification attempted")

    /** Not allowed - this is supposed to be unmodifiable  */
    @Deprecated("Not allowed - this is supposed to be unmodifiable",
                ReplaceWith("Use .filter(!collection.contains(it)).toIm/MutSet"))
    @JvmDefault
    override fun retainAll(elements: Collection<E>): Boolean = throw UnsupportedOperationException("Modification attempted")

    // Having a size forces a set to be defined as a collection by adding items.
    // A set without a size is just a contains() function.
    // Overrides kotlin.collections.Collection.size
    @JvmDefault
    override val size: kotlin.Int

//    @JvmDefault
//    override fun size(): Int = size

    /**
     * Overridden to avoid inheriting unrelated defaults between java.util.Collections and
     * kotlin.collections.Iterable. Copied implementation from Collections.spliterator() because
     * for a Collection the size is known.
     */
    @JvmDefault
    override fun spliterator(): Spliterator<E> = Spliterators.spliterator(this, SIZED or DISTINCT)

    /**
     * This method goes against Josh Bloch's Item 25: "Prefer Lists to Arrays", but is provided for backwards
     * compatibility in some performance-critical situations.  If you really need an array, consider using the somewhat
     * type-safe version of this method instead, but read the caveats first.
     */
    @JvmDefault
    override fun toArray(): Array<out Any?> = UnmodIterable.toArray(this, arrayOfNulls<Any>(size), size)

    /**
     * This method goes against Josh Bloch's Item 25: "Prefer Lists to Arrays", but is provided for backwards
     * compatibility in some performance-critical situations.  If you need to create an array (you almost always do)
     * then the best way to use this method is:
     *
     * `MyThing[] things = col.toArray(new MyThing\[coll.size]);`
     *
     * Calling this method any other way causes unnecessary work to be done - an extra memory allocation and potential
     * garbage collection if the passed array is too small, extra effort to fill the end of the array with nulls if it
     * is too large.
     */
    @JvmDefault
    override fun <T> toArray(elements: Array<T>): Array<out T> {
        @Suppress("UNCHECKED_CAST")
        return UnmodIterable.toArray(this as Iterable<T>, elements, size)
    }

// Methods inherited from interface java.util.Collection
// parallelStream, stream

// Methods inherited from interface java.lang.Iterable
// forEach
}
