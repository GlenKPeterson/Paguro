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

import org.organicdesign.fp.collections.UnmodMap.UnEntry
import org.organicdesign.fp.oneOf.Option

/** An immutable sorted map. */
interface ImSortedMap<K,V> : UnmodSortedMap<K,V>, BaseMap<K,V> {

    override fun entry(key: K): Option<UnEntry<K,V>>

//    /**
//     Returns a view of the mappings contained in this map.  The set should actually contain
//     UnmodMap.Entry items, but that return signature is illegal in Java, so you'll just have to
//     remember.
//     */
//    @Override ImSortedSet<Entry<K,V>> entrySet();

// public  K	firstKey()

    @JvmDefault
    override fun containsKey(key: K): Boolean = entry(key).isSome

    @JvmDefault
    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() = super<UnmodSortedMap>.entries
//    override fun entrySet(): ImSortedSet<Entry<K,V>>

    @JvmDefault
    override fun get(key: K): V? {
        val entry:Option<UnEntry<K, V>> = entry(key)
        return if (entry.isSome) entry.get().value else null
//        return entry.match({ item: UnmodMap.UnEntry<K, V> -> item.value }, { null })
    }

//    @SuppressWarnings("unchecked")
//    @Override default V get(Object key) {
//        Option<UnEntry<K,V>> entry = entry((K) key);
//        return entry.isSome() ? entry.get().getValue() : null;
//    }

    @JvmDefault
    override fun getOrDefault(key: K, defaultValue: V): V {
        val entry:Option<UnEntry<K, V>> = entry(key)
        return if (entry.isSome) entry.get().value else defaultValue
    }

    /** Return the elements in this map up (but excluding) to the given element */
    @JvmDefault
    override fun headMap(toKey: K): ImSortedMap<K, V> = subMap(firstKey(), toKey)

    /**
    Returns an iterator over the UnEntries of this map in order.
    @return an Iterator.
     */
    @JvmDefault
    override fun iterator(): UnmodSortedIterator<UnEntry<K, V>>

    /** Returns a view of the keys contained in this map. */
    @JvmDefault
    override val keys: ImSortedSet<K>
        get() = PersistentTreeSet.ofMap(this)

// public  K	lastKey()

    // Overrides kotlin.collections.Collection.size
    override val size: kotlin.Int

    /**
     Return the elements in this map from the start element (inclusive) to the end element
     (exclusive)
     */
    @JvmDefault
    override fun subMap(fromKey: K, toKey: K): ImSortedMap<K, V>

    /** Return the elements in this from the given element to the end */
    @JvmDefault
    override fun tailMap(fromKey: K): ImSortedMap<K,V>

//    /** {@inheritDoc} */
//    @Override default UnmodSortedCollection<V> values() {
//        // We need values, but still ordered by their keys.
//        final ImSortedMap<K,V> parent = this;
//        return new UnmodSortedCollection<V>() {
//            @Override public UnmodSortedIterator<V> iterator() {
//                return new UnmodListIterator<V>() {}
//                return UnmodSortedIterable.castFromTypedList(parent.entrySet()
//                                                                   .map(e -> e.getValue())
//                                                                   .toMutList())
//                                          .iterator();
//            }
//            @Override public int size() { return parent.size(); }
//
//            @SuppressWarnings("SuspiciousMethodCalls")
//            @Override public boolean contains(Object o) { return parent.containsValue(o); }
//
//            @Override public int hashCode() { return UnmodIterable.hashCode(this); }
//
//            @Override public boolean equals(Object o) {
//                if (this == o) { return true; }
//                if ( !(o instanceof Collection) ) { return false; }
//                Collection that = (Collection) o;
//                if (this.size() != that.size()) { return false; }
//                return containsAll(that);
//            }
//
//            @Override public String toString() {
//                return UnmodIterable.toString("ImMapOrd.values.UnCollectionOrd", this);
//            }
//        };
//    }
    // TODO: Java to Kotlin conversion didn't work here.  Keep converting manually.


    /**
     Returns a new map with the given key/value added.  If the key exists in this map, the new value
     overwrites the old one.  If the key exists with the same value (based on the address of that
     value in memory, not an equals test), the old map is returned unchanged.

     @param key the key used to look up the value.  In the case of a duplicate key, later values
     overwrite the earlier ones.  The resulting map can contain zero or one null key (if your
     comparator knows how to sort nulls) and any number of null values.

     @param val the value to store in this key.

     @return a new PersistentTreeMap of the specified comparator and the given key/value pairs

     */
    override fun assoc(key: K, value: V): ImSortedMap<K, V>

    /** Returns a new map with an immutable copy of the given entry added */
    @JvmDefault
    override fun assoc(entry: kotlin.collections.Map.Entry<K,V>): ImSortedMap<K,V> = assoc(entry.key, entry.value)

    /** Returns a new map with the given key/value removed */
    override fun without(key: K): ImSortedMap<K, V>
}
