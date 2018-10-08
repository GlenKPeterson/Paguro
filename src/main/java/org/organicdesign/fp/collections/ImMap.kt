package org.organicdesign.fp.collections

import kotlin.collections.Map.Entry

/** An immutable map with no guarantees about its ordering.  */
interface ImMap<K, V> : BaseUnsortedMap<K, V> {

    /** Returns a new map with the given key/value added  */
    override fun assoc(key: K, value: V): ImMap<K, V>

    /** Returns a new map with an immutable copy of the given entry added  */
    @JvmDefault
    override fun assoc(entry: Entry<K, V>): ImMap<K, V> = assoc(entry.key, entry.value)

    /** Returns a new map with the given key/value removed  */
    override fun without(key: K): ImMap<K, V>

    /**
     * Returns a view of the mappings contained in this map.  The set should actually contain
     * UnmodMap.Entry items, but that return signature is illegal in Java, so you'll just have to
     * remember.
     */
    @JvmDefault
    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() = super.entries

    /** Returns an immutable view of the keys contained in this map.  */
    @JvmDefault
    override val keys: ImSet<K>
        get() = mutable().keys.immutable()

    /** Returns a mutable version of this mutable map.  */
    fun mutable(): MutMap<K, V>

    @JvmDefault
    override val size: Int
}
