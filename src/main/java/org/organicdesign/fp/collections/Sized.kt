package org.organicdesign.fp.collections

interface Sized {
    /** The number of items in this collection or iterable.  */
    // Overrides kotlin.collections.Collection.size
    // TODO: Should this have an @get:JvmName("size") ?
    val size: kotlin.Int

    /** The number of items in this collection or iterable.  */
    @JvmDefault
    fun size(): Int = size
}
