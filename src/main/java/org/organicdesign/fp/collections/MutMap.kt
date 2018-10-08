// Copyright 2016 PlanBase Inc. & Glen Peterson
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

import kotlin.collections.Map

/**
 * Interface for mutable (hash) map builder.
 */
interface MutMap<K, V> : BaseUnsortedMap<K, V> {
    override fun assoc(key: K, value: V): MutMap<K, V>

    @JvmDefault
    override fun assoc(entry: Map.Entry<K, V>): MutMap<K, V> {
        return assoc(entry.key, entry.value)
    }

    @JvmDefault
    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() = super.entries

    /** Returns a mutable view of the keys contained in this map.  */
    @JvmDefault
    override val keys: MutSet<K>
        get() = map{ e -> (e as Map.Entry<K, V>).key }.toMutSet()

    /** Returns an immutable version of this mutable map.  */
    fun immutable(): ImMap<K, V>

    @JvmDefault
    override val size: Int

    override fun without(key: K): MutMap<K, V>
}