package org.organicdesign.fp.collections

/**
 * Implements equals and hashCode() methods compatible with all java.util collections (this
 * algorithm is not order-dependent) and toString which takes the name of the sub-class.
 * You must override equals!
 */
abstract class AbstractUnmodIterable<T> : UnmodIterable<T> {
    abstract override fun equals(other: Any?): Boolean

    override fun hashCode(): Int = UnmodIterable.hash(this)

    override fun toString(): String = UnmodIterable.toString(javaClass.simpleName, this)
}
