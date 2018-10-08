// Copyright 2017 PlanBase Inc. & Glen Peterson
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

import org.organicdesign.fp.oneOf.Option

/**
 Adds copy-on-write, "fluent interface" methods to {@link UnmodMap}.
 Lowest common ancestor of {@link BaseUnsortedMap}, and {@link ImSortedMap}.
 */
interface BaseMap<K,V>: UnmodMap<K,V> {
    /** Returns an option of the key/value pair associated with this key */
    fun entry(key: K): Option<UnmodMap.UnEntry<K, V>>

    /** Returns a new map with the given key/value added */
    fun assoc(key: K, value: V): BaseMap<K,V>

    // Overrides kotlin.collections.Collection.size
    override val size: kotlin.Int

    @JvmDefault
    override fun size(): Int = size

    /** Returns a new map with an immutable copy of the given entry added */
    @JvmDefault
    fun assoc(entry: Map.Entry<K,V>):  BaseMap<K,V> =
            assoc(entry.key, entry.value)

    /** Returns a new map with the given key/value removed */
    fun without(key: K): BaseMap<K,V>

    @JvmDefault
    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() = super.entries
//        return map(e -> (Map.Entry<K,V>) e)
//                .toImSet();
//    }

    /** Returns a view of the keys contained in this map. */
    @JvmDefault
    override val keys: BaseSet<K>

    @JvmDefault
    override fun containsKey(key: K): Boolean = entry(key).isSome

    @JvmDefault
    override fun get(key: K): V? = getOrDefault(key, null)

    @JvmDefault
    override fun getOrDefault(key: K, defaultValue: V): V {
        val entry:Option<UnmodMap.UnEntry<K,V>> = entry(key)
        return if (entry.isSome) { entry.get().value } else { defaultValue }
    }
}
