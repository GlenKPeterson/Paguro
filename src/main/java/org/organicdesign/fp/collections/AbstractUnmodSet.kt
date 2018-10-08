package org.organicdesign.fp.collections

/**
 * Implements equals and hashCode() methods compatible with java.util.Set (which ignores order)
 * to make defining unmod sets easier, especially for implementing Map.keySet() and such.
 */
abstract class AbstractUnmodSet<T> : AbstractUnmodIterable<T>(), UnmodSet<T> {

    override fun equals(other: Any?): Boolean =
            if (this === other) {
                true
            } else {
                (other is Set<*>) &&
                size == other.size &&
                containsAll(other)
            }

    override fun hashCode(): Int = UnmodIterable.hash(this)

    /** To avoid inherited declaration clash */
    abstract override val size: kotlin.Int
}
