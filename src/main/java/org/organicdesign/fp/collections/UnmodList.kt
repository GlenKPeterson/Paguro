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

/**
 Formalizes the return type of {@link java.util.Collections#unmodifiableList(List)}, deprecating
 mutator methods and implementing them to throw exceptions.  You could think of this as
 "clearing the slate" to a point where immutable, functional, fluent interfaces can be built again.
 */
@PurelyImplements("java.util.List")
interface UnmodList<E>: List<E>, UnmodSortedCollection<E> {

    /** Not allowed - this is supposed to be unmodifiable  */
    @JvmDefault
    @Deprecated("Not allowed - this is supposed to be unmodifiable")
    override fun add(element: E): Boolean = throw UnsupportedOperationException("Modification attempted")

    /** Not allowed - this is supposed to be unmodifiable  */
    @JvmDefault
    @Deprecated("Not allowed - this is supposed to be unmodifiable")
    fun add(index:Int, element:E) { throw UnsupportedOperationException("Modification attempted") }

    /** Not allowed - this is supposed to be unmodifiable  */
    @JvmDefault
    @Deprecated("Not allowed - this is supposed to be unmodifiable")
    override fun addAll(elements: Collection<E>): Boolean = throw UnsupportedOperationException("Modification attempted")

    /** Not allowed - this is supposed to be unmodifiable  */
    @JvmDefault
    @Deprecated("Not allowed - this is supposed to be unmodifiable")
    fun addAll(index:Int, c:Collection<E>): Boolean = throw UnsupportedOperationException("Modification attempted")

    /** Not allowed - this is supposed to be unmodifiable  */
    @JvmDefault
    @Deprecated("Not allowed - this is supposed to be unmodifiable")
    override fun clear() { throw UnsupportedOperationException("Modification attempted") }

    /**
     This method is deprecated because implementing it on a List has O(n) performance.  It will
     never go away because it's declared on java.util.Collection which List extends.  It still
     shouldn't be used.

     If you need repeated or fast contains() tests, use a Set instead instead of a List.
     SortedSet.contains() has O(log2 n) performance.  HashSet.contains() has O(1) performance!
     If you truly need a one-shot contains test, iterate the list manually, or override the
     deprecation warning, but include a description of why you need to use a List instead of some
     kind of Set or Map!
     */
    @JvmDefault
    @Deprecated("O(n) performance - use a Set or SortedSet instead if your collection is large.")
    override fun contains(element: E):Boolean {
        for (item in this) {
            if (item == element) { return true }
        }
        return false
    }

    /**
     The default implementation of this method has O(this.size() + that.size()) or O(n) performance.
     So even though contains() is impossible to implement efficiently for Lists, containsAll()
     has a decent implementation (brute force would be O(this.size() * that.size()) or O(n^2) ).
     */
    @JvmDefault
    override fun containsAll(elements: Collection<E>): Boolean = UnmodCollection.containsAll(this, elements)

//boolean	equals(Object o)
//E	get(int index)
//int	hashCode()

    /**
     The default implementation of this method has O(this.size()) performance.  If you call this
     much, you probably want to use a Map&lt;Integer,T&gt; instead for O(1) performance.
     */
    @JvmDefault
    override fun indexOf(element: E): Int {
        for (i in this.indices) {
            if (get(i) == element) { return i }
        }
        return -1
    }

    /** A convenience method to check if size is 0 */
    @JvmDefault
    override fun isEmpty(): Boolean = size == 0

    /** A convenience method to get a listIterator. */
    @JvmDefault
    override fun iterator(): UnmodSortedIterator<E> = listIterator(0)

    /** The default implementation of this method has O(this.size()) performance. */
    @JvmDefault
    override fun lastIndexOf(element: E): Int {
        for (i in size() - 1 downTo 0) {
            if (get(i) == element) { return i }
        }
        return -1
    }

    @JvmDefault
    override fun listIterator(): UnmodListIterator<E> = listIterator(0)


    /** {@inheritDoc}  Subclasses should override this when they can do so more efficiently. */
    @JvmDefault
    override fun listIterator(index: Int): UnmodListIterator<E> {
        if ( (index < 0) || (index > size) ) {
            throw IndexOutOfBoundsException("Expected an index between 0 and " + size +
                                                " but found: " + index)
        }
        class Impl(private var idx:Int): UnmodListIterator<E> {
            //, Serializable {
            // For serializable.  Make sure to change whenever internal data format changes.
            // private static final long serialVersionUID = 20160903104400L;

            override fun hasNext(): Boolean = idx < size

            override fun next(): E {
                // I think this temporary variable i gets compiled to a register access
                // Load memory value from idx to register.  This is the index we will use against
                // our internal data.
                val i = idx
                // Throw based on value in register
                if (i >= size) { throw NoSuchElementException() }
                // Store incremented register value back to memory.  Note that this is the
                // next index value we will access.
                idx = i + 1
                // call get() using the old value of idx (before our increment).
                // i should still be in the register, not in memory.
                return get(i)
            }

            override fun hasPrevious(): Boolean = idx > 0

            override fun previous(): E {
                // I think this temporary variable i gets compiled to a register access
                // retrieve idx, subtract 1, leaving result in register.  The JVM only has one
                // register.
                val i = idx - 1
                // throw if item in register is < 0
                if (i < 0) { throw NoSuchElementException() }
                // Write register to memory location
                idx = i
                // retrieve item at the index in the register.
                return get(i)
            }

            override fun nextIndex(): Int = idx
        }

        return Impl(index)
    }

    /** Not allowed - this is supposed to be unmodifiable */
    @Deprecated("Not allowed - this is supposed to be unmodifiable")
    @JvmDefault
    fun remove(index: Int):E =
    throw UnsupportedOperationException("Modification attempted")

    /** Not allowed - this is supposed to be unmodifiable  */
    @Deprecated("Not allowed - this is supposed to be unmodifiable")
    @JvmDefault
    override fun remove(element: E): Boolean = throw UnsupportedOperationException("Modification attempted")

    /** Not allowed - this is supposed to be unmodifiable  */
    @Deprecated("Not allowed - this is supposed to be unmodifiable")
    @JvmDefault
    override fun removeAll(elements: Collection<E>): Boolean = throw UnsupportedOperationException("Modification attempted")

    /** Not allowed - this is supposed to be unmodifiable */
    @Deprecated("Not allowed - this is supposed to be unmodifiable")
    @JvmDefault
    override fun removeIf(filter: (E) -> Boolean):Boolean {
        throw UnsupportedOperationException("Modification attempted")
    }

    /** Not allowed - this is supposed to be unmodifiable */
    @Deprecated("Not allowed - this is supposed to be unmodifiable")
    @JvmDefault
    fun replaceAll(operator: (E) -> E) {
        throw UnsupportedOperationException("Modification attempted")
    }

    /** Not allowed - this is supposed to be unmodifiable  */
    @Deprecated("Not allowed - this is supposed to be unmodifiable")
    @JvmDefault
    override fun retainAll(elements: Collection<E>): Boolean = throw UnsupportedOperationException("Modification attempted")

    /** Not allowed - this is supposed to be unmodifiable */
    @Deprecated("Not allowed - this is supposed to be unmodifiable")
    @JvmDefault
    fun set(index: Int, element: E): E = throw UnsupportedOperationException("Modification attempted")

    // Overrides kotlin.collections.Collection.size
    @JvmDefault
    override val size: kotlin.Int

//    @JvmDefault
//    override fun size(): Int = size

    /** Not allowed - this is supposed to be unmodifiable */
    @Deprecated("Not allowed - this is supposed to be unmodifiable")
    @JvmDefault
    fun sort(c: Comparator<in E>) {
        throw UnsupportedOperationException("Modification attempted")
    }

    /**
     * Overridden to avoid inheriting unrelated defaults between java.util.Collections and
     * kotlin.collections.Iterable. Copied implementation from Collections.spliterator() because
     * for a Collection the size is known.
     */
    @JvmDefault
    override fun spliterator(): Spliterator<E> =
            Spliterators.spliterator(this, Spliterator.SIZED or Spliterator.ORDERED)

    @JvmDefault
    override fun subList(fromIndex:Int, toIndex:Int): UnmodList<E> {
        if ( (fromIndex == 0) && (toIndex == size()) ) {
            return this
        }
        // Note that this is an IllegalArgumentException, not IndexOutOfBoundsException in order to
        // match ArrayList.
        if (fromIndex > toIndex) {
            throw IllegalArgumentException("fromIndex($fromIndex) > toIndex($toIndex)")
        }
        // The text of this matches ArrayList
        if (fromIndex < 0) { throw IndexOutOfBoundsException("fromIndex = $fromIndex"); }
        if (toIndex > size()) { throw IndexOutOfBoundsException("toIndex = $toIndex"); }

        val parent:UnmodList<E> = this
        return object: UnmodList<E> {
            override val size = toIndex - fromIndex

            override fun get(index: Int): E = parent[index + fromIndex]
        }
    }

    @JvmDefault
    override fun toArray(): Array<out Any?> = UnmodIterable.toArray(this, arrayOfNulls<Any>(size), size())

    @JvmDefault
    override fun <T> toArray(elements: Array<T>): Array<out T> {
        @Suppress("UNCHECKED_CAST")
        return UnmodIterable.toArray(this as Iterable<T>, elements, size())
    }

//Methods inherited from interface java.util.Collection
//parallelStream, stream

//Methods inherited from interface java.lang.Iterable
//forEach


    companion object {

        /**
        Implements equals and hashCode() methods compatible with java.util.List (which ignores order)
        to make defining unmod lists easier.
         */
        @PurelyImplements("java.util.List")
        abstract class AbstractUnmodList<E> : AbstractUnmodIterable<E>(), UnmodList<E> {

            // Overrides kotlin.collections.Collection.size
            abstract override val size: kotlin.Int

            override fun equals(other: Any?): Boolean =
                    (other is List<*>) &&
                    (size == other.size) &&
                    UnmodSortedIterable.equal(this, UnmodSortedIterable.castFromList(other))

            /** This implementation is compatible with java.util.AbstractList but O(n). */
            override fun hashCode(): Int {
                var ret = 1
                for (item in this) {
                    ret *= 31
                    if (item != null) {
                        ret += item.hashCode()
                    }
                }
                return ret
            }

            // Apparently need this to tell it that we're a sorted iterator, not unsorted.
            override fun iterator(): UnmodSortedIterator<E> = listIterator(0)

            /** Not allowed - this is supposed to be unmodifiable  */
            @Deprecated("Not allowed - this is supposed to be unmodifiable")
            override fun retainAll(elements: Collection<E>): Boolean = throw UnsupportedOperationException("Modification attempted")

        }

        /**
        Apply the given function against all unique pairings of items in the list.  Does this belong on
        Fn2 instead of List?
         */
        fun <T> permutations(items: List<T>, f: (T, T) -> Any?) {
            for (i in items.indices) {
                for (j in i + 1 until items.size) {
                    f.invoke(items[i], items[j])
                }
            }
        }
    }
}
