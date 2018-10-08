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

import kotlin.collections.Map.Entry
import org.organicdesign.fp.tuple.Tuple2
import java.io.Serializable

/**
 An unmodifiable map.
 This cannot extend Collection because the remove() method would then be inherited
 from both Collection and Map and Collection.remove() returns a boolean while Map.remove() returns
 a V (the type of the value in the key/value pair).  Maybe an UnmodSizedIterable is called for?
 */
@PurelyImplements("java.util.Map")
interface UnmodMap<K,V> : Map<K,V>, UnmodIterable<UnmodMap.UnEntry<K,V>>, Sized {
    // ========================================== Static ==========================================

    /**
     * A map entry (key-value pair).  The <tt>UnmodMap.entrySet</tt> method returns
     * a collection-view of the map, whose elements are of this class.  The
     * <i>only</i> way to obtain a reference to a map entry is from the
     * iterator of this collection-view.
     *
     * @see UnmodMap#entries
     */
    interface UnEntry<K,V> : Entry<K,V> {
        open class EntryToUnEntryIter<K,V>(private val innerIter:Iterator<Entry<K,V>>) : UnmodIterator<UnEntry<K,V>> {
            //, Serializable {
            // For serializable.  Make sure to change whenever internal data format changes.
            // private static final long serialVersionUID = 20160903082500L;

            override fun hasNext(): Boolean = innerIter.hasNext()

            override fun next(): UnEntry<K,V> = Tuple2.of(innerIter.next())
        }

        class EntryToUnEntrySortedIter<K,V>(iter:Iterator<Entry<K,V>>) : EntryToUnEntryIter<K,V>(iter),
                                                                         UnmodSortedIterator<UnEntry<K,V>> {
            //, Serializable {
            // For serializable.  Make sure to change whenever internal data format changes.
//            private static final long serialVersionUID = 20160903082500L;
        }

        open class UnmodKeyIter<K,V>(private val iter:Iterator<Entry<K,V>>): UnmodIterator<K> {
            //, Serializable {
            // For serializable.  Make sure to change whenever internal data format changes.
            // private static final long serialVersionUID = 20160903174100L;

            override fun hasNext(): Boolean = iter.hasNext()

            override fun next(): K = iter.next().key
        }

        class UnmodSortedKeyIter<K,V>(iter:Iterator<Entry<K,V>>) : UnmodKeyIter<K,V>(iter), UnmodSortedIterator<K> {
            // , Serializable {
            // For serializable.  Make sure to change whenever internal data format changes.
//            private static final long serialVersionUID = 20160903174100L;
        }

        open class UnmodValIter<K,V>(private val iter:Iterator<Entry<K,V>>): UnmodIterator<V> {
            //, Serializable {
            // For serializable.  Make sure to change whenever internal data format changes.
            // private static final long serialVersionUID = 20160903174100L;

            override fun hasNext(): Boolean = iter.hasNext()

            override fun next(): V = iter.next().value
        }

        class UnmodSortedValIter<K,V>(iter:Iterator<Entry<K,V>>): UnmodValIter<K,V>(iter), UnmodSortedIterator<V> {
                // , Serializable {
            // For serializable.  Make sure to change whenever internal data format changes.
//            private static final long serialVersionUID = 20160903174100L;
        }

        companion object {
            fun <K,V> entryIterToUnEntryUnIter(innerIter: Iterator<Entry<K,V>>):UnmodIterator<UnEntry<K,V>> =
                    EntryToUnEntryIter(innerIter)

            fun <K,V> entryIterToUnEntrySortedUnIter(innerIter: Iterator<Entry<K,V>>): UnmodSortedIterator<UnEntry<K,V>> =
                    EntryToUnEntrySortedIter(innerIter)

            // This should be done with a cast, not with code.
//        static <K,V> UnmodSortedIterator<UnEntry<K,V>> unSortIterEntToUnSortIterUnEnt(
//                UnmodSortedIterator<Entry<K,V>> innerIter) {
//            return new UnmodSortedIterator<UnEntry<K, V>>() {
//                @Override public boolean hasNext() { return innerIter.hasNext(); }
//                @Override public UnEntry<K, V> next() {
//                    return UnmodMap.UnEntry.entryToUnEntry(innerIter.next());
//                }
//            };
//        }
//
        }

        /**
         Not compatible with immutability - use
         {@link ImMap#assoc(Object, Object)}
         instead because it returns a new map.
         */
        @Deprecated("Not allowed - this is supposed to be unmodifiable")
        @JvmDefault
        fun setValue(value: V):V = throw UnsupportedOperationException("Modification attempted")
    }

    // ========================================= Instance =========================================

    // Modification Operations

    /** Not allowed - this is supposed to be unmodifiable  */
    @JvmDefault
    @Deprecated("Not allowed - this is supposed to be unmodifiable")
    fun clear() { throw UnsupportedOperationException("Modification attempted") }

    /** Not allowed - this is supposed to be unmodifiable  */
    @JvmDefault
    @Deprecated("Not allowed - this is supposed to be unmodifiable")
    fun compute(key: K, remappingFunction: (K, V) -> V): V  =
            throw UnsupportedOperationException("Modification attempted")

    /** Not allowed - this is supposed to be unmodifiable */
    @JvmDefault
    @Deprecated("Not allowed - this is supposed to be unmodifiable")
    fun computeIfAbsent(key: K, mappingFunction: (K) -> V): V =
            throw UnsupportedOperationException("Modification attempted")

    /** Not allowed - this is supposed to be unmodifiable */
    @JvmDefault
    @Deprecated("Not allowed - this is supposed to be unmodifiable")
    fun computeIfPresent(key: K, remappingFunction: (K, V) -> V): V  =
            throw UnsupportedOperationException("Modification attempted")
// boolean	containsKey(Object key)

    /**
    Most maps are not designed for this - the default implementation has O(n) performance.
    {@inheritDoc}
     */
    // This is the place to define this slow operation so that it can be used in
    // values().contains(), UnmodSortedMap.containsValue() and UnmodSortedMap.values().contains().
    @JvmDefault
    override fun containsValue(value: V): Boolean {
        for (item:UnEntry<K,V> in this) {
            if (item.value == value) { return true }
        }
        return false
    }

    /**
     Returns a view of the mappings contained in this map.  The set will contain UnmodMap.UnEntry
     items, but that return signature is illegal in Java, so you'll just have to remember. An
     UnmodMap is iterable, so this method is probably not nearly as useful as it once was.
     */
    @JvmDefault
    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        @Suppress("UNCHECKED_CAST")
        get() = EntrySet(this) as MutableSet<MutableMap.MutableEntry<K, V>>


// boolean	equals(Object o)

//    @Override default boolean equals(Object other) {
//        // Cheapest operation first...
//        if (this == other) { return true; }
//
//        if ( (other == null) ||
//                !(other instanceof Map) ||
//                (this.hashCode() != other.hashCode()) ) {
//            return false;
//        }
//        // Details...
//        final Map that = (Map) other;
//        if (this.size() != that.size()) {
//            return false;
//        }
//        return this.entrySet().containsAll(that.entrySet());
//    }

// default void	forEach(BiConsumer<? super K,? super V> action)
// V	get(Object key)
// default V	getOrDefault(Object key, V defaultValue)

//    @Override default int hashCode() {
//        if (size() == 0) { return 0; }
//        return Arrays.hashCode(entrySet().toArray());
//    };

    @JvmDefault
    override fun isEmpty(): Boolean = size == 0

    /**
    Returns a view of the keys contained in this map.  An UnmodMap is iterable, so this method
    is probably not nearly as useful as it once was.
     */
    @JvmDefault
    override val keys: UnmodSet<K>
        get() = KeySet(this)

    /** Not allowed - this is supposed to be unmodifiable */
    @JvmDefault
    @Deprecated("Not allowed - this is supposed to be unmodifiable")
    fun merge(key: K, value: V, remappingFunction: (K, V) -> V): V  =
            throw UnsupportedOperationException("Modification attempted")

    /**
     Not compatible with immutability - use
     {@link ImMap#assoc(Object, Object)}
     instead because it returns a new map.
     */
    /** Not allowed - this is supposed to be unmodifiable */
    @JvmDefault
    @Deprecated("Not allowed - this is supposed to be unmodifiable")
    fun put(key: K, value: V): V = throw UnsupportedOperationException("Modification attempted")

    /** Not allowed - this is supposed to be unmodifiable */
    @JvmDefault
    @Deprecated("Not allowed - this is supposed to be unmodifiable")
    fun putAll(from: Map<out K, V>) { throw UnsupportedOperationException("Modification attempted") }

    /** Not allowed - this is supposed to be unmodifiable */
    @JvmDefault
    @Deprecated("Not allowed - this is supposed to be unmodifiable")
    fun putIfAbsent(key: K, value: V): V = throw UnsupportedOperationException("Modification attempted")

//    /** Not allowed - this is supposed to be unmodifiable */
//    @JvmDefault
//    @Deprecated("Not allowed - this is supposed to be unmodifiable")
//    override fun remove(key: Any?): V = throw UnsupportedOperationException("Modification attempted")
//
//    /** Not allowed - this is supposed to be unmodifiable */
//    @JvmDefault
//    @Deprecated("Not allowed - this is supposed to be unmodifiable")
//    override fun remove(key: K, value: V): V = throw UnsupportedOperationException("Modification attempted")

    /** Not allowed - this is supposed to be unmodifiable */
    @JvmDefault
    @Deprecated("Not allowed - this is supposed to be unmodifiable")
    fun replace(key: K, oldValue: V, newValue: V): Boolean = throw UnsupportedOperationException("Modification attempted")
    /** Not allowed - this is supposed to be unmodifiable */

    @JvmDefault
    @Deprecated("Not allowed - this is supposed to be unmodifiable")
    fun replace(key: K, value: V): V = throw UnsupportedOperationException("Modification attempted")

    /** Not allowed - this is supposed to be unmodifiable  */
    @JvmDefault
    @Deprecated("Not allowed - this is supposed to be unmodifiable")
    fun replaceAll(remappingFunction: (K, V) -> V) {
        throw UnsupportedOperationException("Modification attempted")
    }

    // Overrides kotlin.collections.Collection.size
    override val size: kotlin.Int

    @JvmDefault
    override fun size(): Int = size

    /**
     This method has been deprecated because it is impossible to implement equals() or hashCode()
     on the resulting collection, and calling this method is probably at least a missed opportunity,
     if not an outright error.  Use an UnmodMap as an UnmodIterable&lt;UnmodMap.UnEntry&gt; instead.

     If you don't care about eliminating duplicate values, and want a compatible return type call:
     <pre><code>myMap.map((UnEntry&lt;K,V&gt; entry) -&gt; entry.getValue())
             .toImSet();</code></pre>

     If you want to keep a count of duplicates, try something like this, but it has a different
     signature:
     <pre><code>ImMap&lt;V,Integer&gt; valueCounts = myMap.fold(PersistentHashMap.empty(),
                     (ImMap&lt;V,Integer&gt; accum, UnEntry&lt;K,V&gt; origEntry) -&gt; {
                             V inVal = origEntry.getValue();
                             return accum.assoc(inVal,
                                                accum.getOrElse(inVal, 0) + 1);
                         });</code></pre>

     You really shouldn't turn values() into a List, because a List has order and an unsorted Map
     is unordered by key, and especially unordered by value.  On a SortedMap, List is the proper
     return type.

     java.util.HashMap.values() returns an instance of java.util.HashMap.Values which does *not*
     have equals() or hashCode() defined.  This is because List.equals() and Set.equals() return
     not-equal when compared to a Collection.  There is no good way to implement a reflexive
     equals with both of those because they are just too different.  Ultimately, Collection just
     isn't specific enough to instantiate, but we do it anyway here for backward compatibility.
     We don't implement equals() or hashCode() either because the result could have duplicates.
     If the Map isn't sorted, the result could have random ordering.
     */
    @Deprecated("It is impossible to implement equals() or hashCode() on the returned collection.  " +
                "Calling this method is probably at least a missed opportunity, if not an outright error.  " +
                "Use an UnmodMap as an UnmodIterable<UnmodMap.UnEntry> instead.",
                replaceWith = ReplaceWith("iterator"))
    @JvmDefault
    override val values:MutableCollection<V>
        @Suppress("UNCHECKED_CAST")
        get() = Values(this) as MutableCollection<V>

    companion object {
        private class EntrySet<K,V>(private val parentMap: UnmodMap<K,V>) : AbstractUnmodSet<Entry<K,V>>(), Serializable {

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
            override fun iterator(): UnmodIterator<Entry<K, V>> =
                    parentMap.iterator() as UnmodIterator<Entry<K, V>>

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

        private class KeySet<K,V>(private val parentMap:UnmodMap<K,V>) : AbstractUnmodSet<K>(), Serializable {

            override fun contains(element: K): Boolean = parentMap.containsKey(element)

            override fun iterator(): UnmodIterator<K> = UnEntry.UnmodKeyIter(parentMap.iterator())

            override val size: Int = parentMap.size

            override fun toString(): String =
                    UnmodIterable.toString("UnmodMap.keySet", this)

            companion object {
                // For serializable.  Make sure to change whenever internal data format changes.
                private const val serialVersionUID = 20180617104400L
            }
        }

        private class Values<V>(private val parentMap: UnmodMap<*,V>): UnmodCollection<V>, Serializable {

            override fun contains(element: V): Boolean = parentMap.containsKey(element)

            override fun iterator(): UnmodIterator<V> = UnEntry.UnmodValIter(parentMap.iterator())

            override val size: Int = parentMap.size

            override fun toString(): String =
                    UnmodIterable.toString("UnmodMap.values", this)

            companion object {
                // For serializable.  Make sure to change whenever internal data format changes.
                private const val serialVersionUID = 20180617104400L
            }
        }
    }
}
