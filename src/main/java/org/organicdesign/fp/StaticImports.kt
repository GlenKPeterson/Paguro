// Copyright 2014-09-22 PlanBase Inc. & Glen Peterson
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

package org.organicdesign.fp

import org.organicdesign.fp.collections.ImList
import org.organicdesign.fp.collections.ImMap
import org.organicdesign.fp.collections.ImSet
import org.organicdesign.fp.collections.ImSortedMap
import org.organicdesign.fp.collections.ImSortedSet
import org.organicdesign.fp.collections.MutList
import org.organicdesign.fp.collections.MutMap
import org.organicdesign.fp.collections.MutSet
import org.organicdesign.fp.collections.PersistentHashMap
import org.organicdesign.fp.collections.PersistentHashSet
import org.organicdesign.fp.collections.PersistentTreeMap
import org.organicdesign.fp.collections.PersistentTreeSet
import org.organicdesign.fp.collections.PersistentVector
import org.organicdesign.fp.collections.RrbTree
import org.organicdesign.fp.collections.RrbTree.ImRrbt
import org.organicdesign.fp.collections.RrbTree.MutRrbt
import org.organicdesign.fp.collections.UnmodIterable
import org.organicdesign.fp.collections.UnmodIterator
import org.organicdesign.fp.tuple.Tuple2
import org.organicdesign.fp.tuple.Tuple3
import org.organicdesign.fp.xform.Xform

import java.util.Arrays
import java.util.Comparator
import kotlin.collections.Map.Entry

/**
 Returns a new PersistentHashMap of the given keys and their paired values.  Use the
 [tup] method to define those key/value pairs briefly and
 easily.  This data definition method is one of the few methods in this project that support
 varargs.

 @param kvPairs Key/value pairs (to go into the map).  In the case of a duplicate key, later
 values in the input list overwrite the earlier ones.  The resulting map can contain zero or one
 null key and any number of null values.  Null k/v pairs will be silently ignored.
 @return a new PersistentHashMap of the given key/value pairs
 */
@SafeVarargs
fun <K, V> map(vararg kvPairs: Entry<K, V>): ImMap<K, V> =
        if (kvPairs.isEmpty()) {
            PersistentHashMap.empty()
        } else {
            PersistentHashMap.of(Arrays.asList<Entry<K, V>>(*kvPairs))
        }

/**
 Returns a new MutMap of the given keys and their paired values.  Use the
 [tup] method to define those key/value pairs briefly and
 easily.  This data definition method is one of the few methods in this project that support
 varargs.

 @param kvPairs Key/value pairs (to go into the map).  In the case of a duplicate key, later
 values in the input list overwrite the earlier ones.  The resulting map can contain zero or one
 null key and any number of null values.  Null k/v pairs will be silently ignored.
 @return a new MutMap of the given key/value pairs
 */
@SafeVarargs
fun <K, V> mutableMap(vararg kvPairs: Entry<K, V>): MutMap<K, V> {
    val ret = PersistentHashMap.emptyMutable<K, V>()
    for (me in kvPairs) {
        ret.assoc(me)
    }
    return ret
}

/**
 Returns a mutable RRB Tree [RrbTree] of the given items.
 The RRB Tree is a list-type data structure that supports random inserts, split, and join
 (the PersistentVector does not).  The mutable RRB Tree append() method is only about half
 as fast as the PersistentVector method of the same name.  If you build it entirely with random
 inserts, then the RRB tree get() method may be about 5x slower.  Otherwise, performance
 is about the same.
 This data definition method is one of the few methods in this project that support varargs.
 */
@SafeVarargs
fun <T> mutableRrb(vararg items: T): MutRrbt<T> =
        if (items.isEmpty()) {
            RrbTree.emptyMutable()
        } else {
            RrbTree.emptyMutable<T>()
                    .concat(Arrays.asList(*items))
        }

/**
 Returns a new MutSet of the values.  This data definition method is one of the few
 methods in this project that support varargs.  If the input contains duplicate elements, later
 values overwrite earlier ones.
 */
@SafeVarargs
fun <T> mutableSet(vararg items: T): MutSet<T> {
    val ret = PersistentHashSet.emptyMutable<T>()
    for (t in items) {
        ret.put(t)
    }
    return ret
}

/**
 Returns a MutVector of the given items.  This data definition method is one of the
 few methods in this project that support varargs.
 */
@SafeVarargs
fun <T> mutableVec(vararg items: T): MutList<T> {
    val ret = PersistentVector.emptyMutable<T>()
    for (t in items) {
        ret.append(t)
    }
    return ret
}

/**
 Returns a new immutable RRB Tree [ImRrbt] of the given items.  An RRB Tree
 is an immutable list that supports random inserts, split, and join (the PersistentVector does
 not).  If you build it entirely with random
 inserts, then the RRB tree get() method may be about 5x slower.  Otherwise, performance
 is about the same.

 This data definition method is one of the few methods in this project that support varargs.
 */
@SafeVarargs
fun <T> rrb(vararg items: T): ImRrbt<T> =
        if (items.isEmpty()) {
            RrbTree.empty()
        } else {
            mutableRrb(*items).immutable()
        }

/**
 Returns a new PersistentHashSet of the values.  This data definition method is one of the few
 methods in this project that support varargs.  If the input contains duplicate elements, later
 values overwrite earlier ones.
 */
@SafeVarargs
fun <T> set(vararg items: T): ImSet<T> =
        if (items.isEmpty()) {
            PersistentHashSet.empty()
        } else {
            PersistentHashSet.of(Arrays.asList(*items))
        }

/**
 Returns a new PersistentTreeMap of the specified comparator and the given key/value pairs.  Use
 the tup() method to define those key/value pairs briefly and easily.  The keys are sorted
 according to the comparator you provide.

 @param comp A comparator (on the keys) that defines the sort order inside the new map.  This
 becomes a permanent part of the map and all sub-maps or appended maps derived from it.  If you
 want to use a null key, make sure the comparator treats nulls correctly in all circumstances!
 @param kvPairs Key/value pairs (to go into the map).  In the case of a duplicate key, later
 values in the input list overwrite the earlier ones.  The resulting map can contain zero or one
 null key (if your comparator knows how to sort nulls) and any number of null values.  Null k/v
 pairs will be silently ignored.
 @return a new PersistentTreeMap of the specified comparator and the given key/value pairs
 */
fun <K, V> sortedMap(comp: Comparator<in K>, kvPairs: Iterable<Entry<K, V>>): ImSortedMap<K, V> =
        PersistentTreeMap.ofComp(comp, kvPairs)

/**
 * Returns a new PersistentTreeMap of the given comparable keys and their paired values, sorted in
 * the default ordering of the keys.  Use the tup() method to define those key/value pairs briefly
 * and easily.
 *
 * @param kvPairs Key/value pairs (to go into the map).  In the case of a duplicate key, later
 * values overwrite earlier ones.
 * @return a new PersistentTreeMap of the specified comparator and the given key/value pairs which
 * uses the default comparator defined on the element type.
 */
fun <K : Comparable<K>, V> sortedMap(kvPairs: Iterable<Entry<K, V>>): ImSortedMap<K, V> =
        PersistentTreeMap.of(kvPairs)

/**
 * Returns a new PersistentTreeSet of the given comparator and items.
 *
 * @param comp A comparator that defines the sort order of elements in the new set.  This
 * becomes part of the set (it's not for pre-sorting).
 * @param elements items to go into the set.  In the case of duplicates, later elements overwrite
 * earlier ones.
 * @return a new PersistentTreeSet of the specified comparator and the given elements
 */
fun <T> sortedSet(comp: Comparator<in T>, elements: Iterable<T>): ImSortedSet<T> =
        Xform.of(elements).toImSortedSet(comp)

/** Returns a new PersistentTreeSet of the given comparable items.  */
fun <T : Comparable<T>> sortedSet(items: Iterable<T>): ImSortedSet<T> =
        PersistentTreeSet.of(items)

/** Returns a new [Tuple2] of the given items.  */
fun <T, U> tup(t: T, u: U): Tuple2<T, U> = Tuple2(t, u)

/** Returns a new [Tuple3] of the given items.  */
fun <T, U, V> tup(t: T, u: U, v: V): Tuple3<T, U, V> =
        Tuple3.of(t, u, v)

/**
 Returns a new PersistentVector of the given items.  This data definition method is one of the
 few methods in this project that support varargs.
 */
@SafeVarargs
fun <T> vec(vararg items: T): ImList<T> =
        if (items.isEmpty()) {
            PersistentVector.empty()
        } else {
            mutableVec(*items).immutable()
        }

/**
 If you need to wrap a regular Java collection or other iterable outside this project to perform
 a transformation on it, this method is the most convenient, efficient way to do so.
 */
fun <T> xform(iterable: Iterable<T>): UnmodIterable<T> = Xform.of(iterable)

/**
 If you need to wrap a regular Java array outside this project to perform
 a transformation on it, this method is the most convenient, efficient way to do so.
 */
@SafeVarargs
fun <T> xformArray(vararg items: T): UnmodIterable<T> = Xform.of(Arrays.asList(*items))

// TODO: Enable this to make Maps, Strings, and StringBuilders work like other collections.
//    /** Wrap a Java.util.Map to perform a transformation on it. */
//    public static <K,V> UnmodIterable<Map.Entry<K,V>> xform(Map<K,V> map) {
//        return Xform.of(map.entrySet());
//    }
//
/** Wrap a String (or CharSequence) to perform a Character-by-Character transformation on it.  */
fun xformChars(seq: CharSequence): UnmodIterable<Char> {

    return object : UnmodIterable<Char> {
        override fun iterator(): UnmodIterator<Char> {
            return object : UnmodIterator<Char> {
                private var idx = 0
                override fun hasNext(): Boolean {
                    return idx < seq.length
                }

                override fun next(): Char? {
                    val nextIdx = idx + 1
                    val c = seq[idx]
                    idx = nextIdx
                    return c
                }
            }
        }
    }
}