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
package org.organicdesign.fp.collections;

import org.organicdesign.fp.permanent.Sequence;

import java.util.Map;

/** An immutable sorted map. */
public interface ImMapSorted<K,V> extends UnMapSorted<K,V>, Sequence<UnMap.UnEntry<K,V>> {

    /**
     * Returns a view of the mappings contained in this map.  The set should actually contain UnMap.Entry items, but
     * that return signature is illegal in Java, so you'll just have to remember. */
    @Override ImSetSorted<Map.Entry<K,V>> entrySet();

// public  K	firstKey()

    /** Return the elements in this map up (but excluding) to the given element */
    @Override default ImMapSorted<K,V> headMap(K toKey) { return subMap(firstKey(), toKey); }

    /**
     Returns an iterator over the UnEntries of this map in order.
     @return an Iterator.
     */
    @Override UnIteratorOrdered<UnEntry<K, V>> iterator();

    /** Returns a view of the keys contained in this map. */
    @Override ImSet<K> keySet();

// public  K	lastKey()

    /** Return the elements in this map from the start element (inclusive) to the end element (exclusive) */
    @Override ImMapSorted<K,V> subMap(K fromKey, K toKey);

    /** Return the elements in this from the given element to the end */
    @Override ImMapSorted<K,V> tailMap(K fromKey);

    /** Returns a new map with the given key/value added */
    ImMapSorted<K,V> assoc(K key, V val);

    /** Returns a new map with an immutable copy of the given entry added */
    default ImMapSorted<K,V> assoc(Map.Entry<K,V> entry) { return assoc(entry.getKey(), entry.getValue()); }

    /** Returns a new map with the given key/value removed */
    ImMapSorted<K,V> without(K key);
}
