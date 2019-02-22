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
package org.organicdesign.fp.collections;

import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.organicdesign.fp.oneOf.Option;

/**
 Adds copy-on-write, "fluent interface" methods to {@link UnmodMap}.
 Lowest common ancestor of {@link BaseUnsortedMap}, and {@link ImSortedMap}.
 */
public interface BaseMap<K,V> extends UnmodMap<K,V> {
    /** Returns an option of the key/value pair associated with this key */
    @NotNull Option<UnEntry<K,V>> entry(K key);

    /** Returns a new map with the given key/value added */
    @NotNull BaseMap<K,V> assoc(K key, V val);

    /** Returns a new map with an immutable copy of the given entry added */
    default @NotNull BaseMap<K,V> assoc(@NotNull Map.Entry<K,V> entry) {
        return assoc(entry.getKey(), entry.getValue());
    }

    /** Returns a new map with the given key/value removed */
    @NotNull BaseMap<K,V> without(K key);

    /**
     Returns a view of the mappings contained in this map.  The set should actually contain
     UnmodMap.Entry items, but that return signature is illegal in Java, so you'll just have to
     remember.
     */
    @Override @NotNull BaseSet<Entry<K,V>> entrySet();
//        return map(e -> (Map.Entry<K,V>) e)
//                .toImSet();
//    }

    /** Returns a view of the keys contained in this map. */
    @NotNull
    @Override BaseSet<K> keySet();

    @SuppressWarnings("unchecked")
    @Override default boolean containsKey(Object key) { return entry((K) key).isSome(); }

    @SuppressWarnings("unchecked")
    @Override default V get(Object key) {
        Option<UnEntry<K,V>> entry = entry((K) key);
        return entry.isSome() ? entry.get().getValue() : null;
    }

    default V getOrElse(K key, V notFound) {
        Option<UnEntry<K,V>> entry = entry(key);
        return entry.isSome() ? entry.get().getValue() : notFound;
    }
}
