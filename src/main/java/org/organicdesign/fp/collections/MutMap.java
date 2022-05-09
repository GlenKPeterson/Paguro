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

package org.organicdesign.fp.collections;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 Interface for mutable (hash) map builder.
 */
public interface MutMap<K,V> extends BaseUnsortedMap<K,V> {
    /** {@inheritDoc} */
    @Override
    @Contract(mutates = "this")
    @NotNull MutMap<K,V> assoc(K key, V val);

    /** {@inheritDoc} */
    @Override
    @Contract(mutates = "this")
    default @NotNull MutMap<K,V> assoc(@NotNull Map.Entry<K,V> entry) {
        return assoc(entry.getKey(), entry.getValue());
    }

    @Override
    default @NotNull MutSet<Entry<K,V>> entrySet() {
        return map(e -> (Map.Entry<K,V>) e).toMutSet();
    }

    /** Returns a mutable view of the keys contained in this map. */
    @Override
    default @NotNull MutSet<K> keySet() {
        return map(e -> ((Map.Entry<K,V>) e).getKey()).toMutSet();
    }

    /** Returns an immutable version of this mutable map. */
    @NotNull ImMap<K,V> immutable();

    /** {@inheritDoc} */
    @Override
    @Contract(mutates = "this")
    @NotNull MutMap<K,V> without(K key);

    /** {@inheritDoc} */
    @Override
    @Contract(mutates = "this")
    default @NotNull MutMap<K,V> concat(@Nullable Iterable<? extends UnmodMap.UnEntry<K,V>> items) {
        if (items != null) {
            for (Entry<K,V> item : items) {
                assoc(item.getKey(), item.getValue());
            }
        }
        return this;
    }

    /** {@inheritDoc} */
    @Override
    @Contract(mutates = "this")
    default @NotNull MutMap<K,V> precat(@Nullable Iterable<? extends UnmodMap.UnEntry<K,V>> items) {
        return concat(items);
    }
}