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

import java.io.Serializable
import java.util.SortedMap
import kotlin.collections.Map.Entry

/**
 * An unmodifiable SortedMap.
 *
This cannot extend Collection because the remove() method would then be inherited
from both Collection and Map and Collection.remove() returns a boolean while Map.remove() returns
a V (the type of the value in the key/value pair).  Maybe an UnmodSizedIterable is called for?

 */
@PurelyImplements("java.util.SortedMap")
interface UnmodSortedMap<K, V> : UnmodMap<K, V>, SortedMap<K, V>, UnmodSortedIterable<UnmodMap.UnEntry<K, V>> {
    @JvmDefault
    override fun put(key: K, value: V): V = throw UnsupportedOperationException("Modification attempted")

    @JvmDefault
    @Deprecated("Not allowed - this is supposed to be unmodifiable")
    override fun putAll(from: Map<out K, V>) { throw UnsupportedOperationException("Modification attempted") }

    @JvmDefault
    override fun replace(key: K, oldValue: V, newValue: V): Boolean =
            throw UnsupportedOperationException("Modification attempted")

    @JvmDefault
    override fun replace(key: K, value: V): V = throw UnsupportedOperationException("Modification attempted")

    @JvmDefault
    override fun clear() { throw UnsupportedOperationException("Modification attempted") }

    @Deprecated("Modification not allowed", ReplaceWith("Use java.util.SortedMap instead"))
    @JvmDefault
    override fun computeIfAbsent(key: K, mappingFunction: (K) -> V): V =
            throw UnsupportedOperationException("Modification attempted")

    @JvmDefault
    override fun putIfAbsent(key: K, value: V): V = throw UnsupportedOperationException("Modification attempted")

    override fun containsKey(key: K): Boolean

    override fun get(key: K): V?

    override val size: Int

    /**
     * Returns a view of the mappings contained in this map.  The set should actually contain
     * UnmodMap.Entry items, but that return signature is illegal in Java, so you'll just have to
     * remember.
     */
    @JvmDefault
    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        @Suppress("UNCHECKED_CAST")
        get() = SortedEntrySet(this) as MutableSet<MutableMap.MutableEntry<K, V>>


//    @JvmDefault
//    override fun keySet(): UnmodSortedSet<K> = SortedKeySet(this)

    /** Returns a view of the keys contained in this map.  */
    @JvmDefault
    override val keys: UnmodSortedSet<K>
        get() = SortedKeySet(this)

    /**
     * This method is deprecated on UnmodMap because equals() and hashCode() cannot be implemented
     * on the resulting collection, but the guaranteed order of the result in a SortedMap makes this
     * able to return a List.  It's still an unnecessary convenience method and you should use
     * this map as an Iterable instead for consistency in dealing with all maps.
     *
     * <pre>`mySortedMap.map((UnEntry<K,V> entry) -> entry.getValue())
     * .toImList();`</pre>
     *
     * {@inheritDoc}
     */
    @Deprecated("It is impossible to implement equals() or hashCode() on the returned collection.  " +
                "Calling this method is probably at least a missed opportunity, if not an outright error.  " +
                "Use an UnmodMap as an UnmodIterable<UnmodMap.UnEntry> instead.",
                replaceWith = ReplaceWith("iterator"))
    @JvmDefault
    override val values:MutableCollection<V>
        @Suppress("UNCHECKED_CAST")
        get() = SortedValues(this) as MutableCollection<V>


//    @JvmDefault
//    @Suppress("UNCHECKED_CAST")
//    override val values: MutableCollection<V>
//        get() = SortedValues(this)

    //        return map((UnEntry<K,V> entry) -> entry.getValue())
    //                .toImList();
//    @Suppress("OverridingDeprecatedMember")
//    @JvmDefault
//    override val values: UnmodSortedCollection<V>
//        get() = SortedValues(this)


    @JvmDefault
    override fun iterator(): UnmodSortedIterator<UnmodMap.UnEntry<K, V>>

    override fun lastKey(): K

    override fun comparator(): Comparator<in K>

    override fun firstKey(): K

    // ========================================= Instance =========================================

    // public Comparator<? super K>	comparator()

    // public  K	firstKey()

    @JvmDefault
    override fun headMap(toKey: K): UnmodSortedMap<K, V> = subMap(firstKey(), toKey)

    //    /** {@inheritDoc} */
    //    @Override default UnmodSortedIterator<UnEntry<K,V>> iterator() {
    //        return UnmodMap.UnEntry.unSortIterEntToUnSortIterUnEnt(entrySet().iterator());
    //    }

    // public  K	lastKey()

//    override fun remove(key: K): V?

//    override fun remove(var1: Any, var2: Any): Boolean // : V

    /** Not allowed - this is supposed to be unmodifiable */
//    @JvmDefault
//    @Deprecated("Not allowed - this is supposed to be unmodifiable")
//    override fun remove(key: Any?): V = throw UnsupportedOperationException("Modification attempted")

    /** Not allowed - this is supposed to be unmodifiable */
    @JvmDefault
    @Deprecated("Modification not allowed", ReplaceWith("Use java.util.SortedMap instead"))
    override fun remove(key: K, value: V): Boolean = throw UnsupportedOperationException("Modification attempted")

    override fun subMap(fromKey: K, toKey: K): UnmodSortedMap<K, V>

    override fun tailMap(fromKey: K): UnmodSortedMap<K, V>

    // Methods inherited from interface java.util.Map
    // clear, compute, computeIfAbsent, computeIfPresent, containsKey, containsValue, equals, forEach, get, getOrDefault, hashCode, isEmpty, merge, put, putAll, putIfAbsent, remove, remove, replace, replace, replaceAll, size

    companion object {

        private class SortedEntrySet<K,V>(
                private val parentMap: UnmodSortedMap<K,V>
        ) : AbstractUnmodSet<Entry<K,V>>(), UnmodSortedSet<Entry<K,V>>, Serializable {
            override fun subSet(fromElement: Entry<K, V>, toElement: Entry<K, V>): UnmodSortedSet<Entry<K, V>> =
                    SortedEntrySet(parentMap.subMap(fromElement.key, toElement.key))

            override fun tailSet(fromElement: Entry<K, V>): UnmodSortedSet<Entry<K, V>> =
                    SortedEntrySet(parentMap.tailMap(fromElement.key))

            override fun first(): Entry<K, V> = parentMap.entries.first()

            override fun comparator(): Comparator<in Entry<K, V>> {
                val comp = parentMap.comparator()
                return kotlin.Comparator { e1, e2 -> comp.compare(e1.key, e2.key) }
            }


            override fun last(): Entry<K, V> = parentMap.entries.last()

//            @Override
//            public boolean contains(Object element) {
//                if ( !(element instanceof Entry) ) { return false; }
//                @SuppressWarnings("unchecked")
//                Entry<K,V> entry = (Entry<K,V>) element;
//                return contains(entry);
//            }

            override fun contains(element: Entry<K,V>): Boolean =
                    parentMap.containsKey(element.key) &&
                    element.value == parentMap[element.key]

            // Converting from
            // UnmodIterator<UnEntry<K,V>> to
            // UnmodIterator<Entry<K,V>>
            // Is a totally legal widening conversion (at runtime) because UnEntry extends
            // (is an) Entry.  But Java's type system doesn't know that because (I think)
            // it's a higher kinded type.  Thanks to type erasure, we can forget about all
            // that and cast it to a base type then suppress the unchecked warning.
            //
            // Hmm... It's possible for this to return an Entry if the wrapped collection
            // uses them...  Not sure how much that matters.
            @Suppress("UNCHECKED_CAST")
            override fun iterator(): UnmodSortedIterator<Entry<K, V>> =
                    parentMap.iterator() as UnmodSortedIterator<Entry<K, V>>

            // Having a size forces a set to be defined as a collection by adding items.
            // A set without a size is just a contains() function.
            // Overrides kotlin.collections.Collection.size
            override val size: kotlin.Int = parentMap.size

            override fun toString(): String = UnmodIterable.toString("UnmodMap.entrySet", this)

            companion object {
                // For serializable.  Make sure to change whenever internal data format changes.
                private const val serialVersionUID = 20180617104400L
            }
        }

        private class SortedKeySet<K>(private val parentMap:UnmodSortedMap<K,*>) : UnmodSortedSet<K>, AbstractUnmodSet<K>(), Serializable {
            override fun subSet(fromElement: K, toElement: K): SortedKeySet<K> =
                    SortedKeySet(parentMap.subMap(fromElement, toElement))

            override fun tailSet(fromElement: K): SortedKeySet<K> =
                    SortedKeySet(parentMap.tailMap(fromElement))

            override fun first(): K = parentMap.firstKey()

            override fun comparator(): Comparator<in K> = parentMap.comparator()

            override fun last(): K = parentMap.lastKey()

            override fun contains(element: K): Boolean = parentMap.containsKey(element)

            override fun iterator(): UnmodSortedIterator<K> = UnmodMap.UnEntry.UnmodSortedKeyIter(parentMap.iterator())

            override val size: Int = parentMap.size

            override fun toString(): String =
                    UnmodIterable.toString("UnmodSortedMap.keySet", this)

            companion object {
                // For serializable.  Make sure to change whenever internal data format changes.
                private const val serialVersionUID = 20180617104400L
            }
        }

        private class SortedValues<V>(private val parentMap: UnmodSortedMap<*,V>): UnmodSortedCollection<V>, Serializable {

            override fun contains(element: V): Boolean = parentMap.containsKey(element)

            override fun iterator(): UnmodSortedIterator<V> = UnmodMap.UnEntry.UnmodSortedValIter(parentMap.iterator())

            override val size: Int = parentMap.size

            override fun toString(): String =
                    UnmodIterable.toString("UnmodSortedMap.values", this)

            companion object {
                // For serializable.  Make sure to change whenever internal data format changes.
                private const val serialVersionUID = 20180617104400L
            }
        }


    }
}
