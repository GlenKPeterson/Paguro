package org.organicdesign.fp.collections

import java.util.NoSuchElementException

/**
 * A one-time use, mutable, not-thread-safe way to get each value of the underling collection in
 * turn. I experimented with various thread-safe alternatives, but the JVM is optimized around
 * iterators so this is the lowest common denominator of collection iteration, even though
 * iterators are inherently mutable.
 *
 * This is called "Unmod" in the sense that it doesn't modify the underlying collection.  Iterators
 * are inherently mutable.  The only safe way to handle them is to pass around IteraBLEs so that
 * the ultimate client gets its own, unshared iteraTOR.  Order is not guaranteed.
 */
@PurelyImplements("java.util.Iterator")
interface UnmodIterator<E> : MutableIterator<E> {
    // ========================================= Instance =========================================
    //default void forEachRemaining(Consumer<? super E> action)
    //boolean hasNext()
    //E next()

    /** Not allowed - this is supposed to be unmodifiable  */
    @Deprecated("We're not doing modification here.")
    @JvmDefault
    override fun remove() {
        throw UnsupportedOperationException("Not Implemented")
    }

    companion object {

        /** Instead of calling this directly, please use [.emptyUnmodIterator] instead  */
        private enum class UnIterator : UnmodIterator<Any> {
            EMPTY {

                override fun hasNext(): Boolean = false

                override fun next(): Any =
                        throw NoSuchElementException("Can't call next() on an empty iterator")
            }
        }

        /** Returns the empty unmodifiable iterator. */
        @Suppress("UNCHECKED_CAST")
        @JvmStatic
        fun <T> emptyUnmodIterator(): UnmodIterator<T> =
                UnIterator.EMPTY as UnmodIterator<T>
    }
}