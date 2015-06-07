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

import org.organicdesign.fp.Option;
import org.organicdesign.fp.permanent.Sequence;

import java.util.Collection;
import java.util.Map;

/** An immutable sorted map. */
public interface ImMapOrdered<K,V> extends UnMapOrdered<K,V>, Sequence<UnMap.UnEntry<K,V>> {

    Option<UnMap.UnEntry<K,V>> entry(K key);

    /**
     * Returns a view of the mappings contained in this map.  The set should actually contain UnMap.Entry items, but
     * that return signature is illegal in Java, so you'll just have to remember. */
    @Override
    ImSetOrdered<Entry<K,V>> entrySet();

// public  K	firstKey()

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

    /** Return the elements in this map up (but excluding) to the given element */
    @Override default ImMapOrdered<K,V> headMap(K toKey) { return subMap(firstKey(), toKey); }

    /**
     Returns an iterator over the UnEntries of this map in order.
     @return an Iterator.
     */
    @Override UnIteratorOrdered<UnEntry<K, V>> iterator();

    /** Returns a view of the keys contained in this map. */
    @Override default ImSetOrdered<K> keySet() { return PersistentTreeSet.ofMap(this); }

// public  K	lastKey()

    /** Return the elements in this map from the start element (inclusive) to the end element (exclusive) */
    @Override
    ImMapOrdered<K,V> subMap(K fromKey, K toKey);

    /** Return the elements in this from the given element to the end */
    @Override
    ImMapOrdered<K,V> tailMap(K fromKey);

    /** {@inheritDoc} */
    @Override default UnCollectionOrdered<V> values() {
        // We need values, but still ordered by their keys.
        final ImMapOrdered<K,V> inner = this;
        return new UnCollectionOrdered<V>() {
            @Override public UnIteratorOrdered<V> iterator() {
                return inner.entrySet().map(e -> e.getValue()).iterator();
            }
            @Override public int size() { return inner.size(); }

            @Override public int hashCode() { return UnIterable.hashCode(this); }

            @Override public boolean equals(Object o) {
                if (this == o) { return true; }
                if ( !(o instanceof Collection) ) { return false; }
                Collection that = (Collection) o;
                if (this.size() != that.size()) { return false; }
                return containsAll(that);
            }

            @Override public String toString() { return UnIterable.toString("ImMapOrd.values.UnCollectionOrd", this); }
        };
    }

    /** Returns a new map with the given key/value added */
    ImMapOrdered<K,V> assoc(K key, V val);

    /** Returns a new map with an immutable copy of the given entry added */
    default ImMapOrdered<K,V> assoc(Map.Entry<K,V> entry) { return assoc(entry.getKey(), entry.getValue()); }

    /** Returns a new map with the given key/value removed */
    ImMapOrdered<K,V> without(K key);
}
