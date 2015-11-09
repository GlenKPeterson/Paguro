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

import org.organicdesign.fp.tuple.Tuple2;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 An unmodifiable map.
 This cannot extend Collection because the remove() method would then be inherited
 from both Collection and Map and Collection.remove() returns a boolean while Map.remove() returns
 a V (the type of the value in the key/value pair).  Maybe an UnmodSizedIterable is called for?
 */
public interface UnmodMap<K,V> extends Map<K,V>, UnmodIterable<UnmodMap.UnEntry<K,V>> {
    // ========================================== Static ==========================================
    /**
     * A map entry (key-value pair).  The <tt>UnmodMap.entrySet</tt> method returns
     * a collection-view of the map, whose elements are of this class.  The
     * <i>only</i> way to obtain a reference to a map entry is from the
     * iterator of this collection-view.
     *
     * @see UnmodMap#entrySet()
     */
    interface UnEntry<K,V> extends Map.Entry<K,V> {
        /** Not allowed - this is supposed to be unmodifiable */
        @SuppressWarnings("deprecation")
        @Override @Deprecated default V setValue(V value) {
            throw new UnsupportedOperationException("Modification attempted");
        }

        static <K,V> UnEntry<K,V> entryToUnEntry(Map.Entry<K,V> entry) {
            return Tuple2.of(entry.getKey(), entry.getValue());
        }

        static <K,V>
        UnmodIterator<UnEntry<K,V>> entryIterToUnEntryUnIter(Iterator<Entry<K,V>> innerIter) {
            return new UnmodIterator<UnEntry<K, V>>() {
                @Override public boolean hasNext() { return innerIter.hasNext(); }
                @Override public UnEntry<K, V> next() {
                    return UnmodMap.UnEntry.entryToUnEntry(innerIter.next());
                }
            };
        }

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
//        class Impl<K,V> implements UnEntry<K,V> {
//            private final K key;
//            private final V val;
//            private Impl(K k, V v) { key = k; val = v; }
//            @Override public K getKey() { return key; }
//            @Override public V getValue() { return val; }
//            @Override
//            public boolean equals(Object other) {
//                if (this == other) { return true; }
//                if ((other == null) || !(other instanceof UnEntry)) { return false; }
//
//                UnEntry that = (UnEntry) other;
//                return Objects.equals(this.key, that.getKey()) &&
//                       Objects.equals(this.getValue(), that.getValue());
//            }
//
//            @Override
//            public int hashCode() {
//                int ret = 0;
//                if (key != null) { ret = key.hashCode(); }
//                if (val != null) { return ret ^ val.hashCode(); }
//                // If it's uninitialized, it's equal to every other uninitialized instance.
//                return ret;
//            }
//
//            @Override public String toString() {
//                return "UnEntry(" + key + "," + val + ")";
//            }
//        };
    }

    UnmodMap<Object,Object> EMPTY = new UnmodMap<Object,Object>() {
        @Override public UnmodSet<Entry<Object,Object>> entrySet() { return UnmodSet.empty(); }
        @Override public UnmodSet<Object> keySet() { return UnmodSet.empty(); }
        @Override public UnmodCollection<Object> values() { return UnmodCollection.empty(); }
        @Override public int size() { return 0; }
        @Override public boolean isEmpty() { return true; }
        @Override public UnmodIterator<UnEntry<Object,Object>> iterator() {
            return UnmodIterator.empty();
        }
        @Override public boolean containsKey(Object key) { return false; }
        @Override public boolean containsValue(Object value) { return false; }
        @Override public Object get(Object key) { return null; }
    };
    @SuppressWarnings("unchecked")
    static <T,U> UnmodMap<T,U> empty() { return (UnmodMap<T,U>) EMPTY; }

    // ========================================= Instance =========================================

    // Modification Operations

    /** Not allowed - this is supposed to be unmodifiable */
    @Override @Deprecated default void clear() {
        throw new UnsupportedOperationException("Modification attempted");
    }

    /** Not allowed - this is supposed to be unmodifiable */
    @Override @Deprecated
    default V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        throw new UnsupportedOperationException("Modification attempted");
    }
    /** Not allowed - this is supposed to be unmodifiable */
    @Override @Deprecated
    default V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        throw new UnsupportedOperationException("Modification attempted");
    }
    /** Not allowed - this is supposed to be unmodifiable */
    @Override @Deprecated
    default V computeIfPresent(K key,
                               BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        throw new UnsupportedOperationException("Modification attempted");
    }
// boolean	containsKey(Object key)
// boolean	containsValue(Object value)

    /**
     Most maps are not designed for this - the default implementation has O(n) performance.
     {@inheritDoc}
     */
    // This is the place to define this slow operation so that it can be used in
    // values().contains(), UnmodSortedMap.containsValue() and UnmodSortedMap.values().contains().
    @SuppressWarnings("SuspiciousMethodCalls")
    @Override default boolean containsValue(Object value) {
        for (UnEntry<K,V> item : this) {
            if (Objects.equals(item.getValue(), value)) { return true; }
        }
        return false;
    }

    /**
     Returns a view of the mappings contained in this map.  The set will contain UnmodMap.UnEntry
     items, but that return signature is illegal in Java, so you'll just have to remember. An
     UnmodMap is iterable, so this method is probably not nearly as useful as it once was.

     {@inheritDoc}
     */
    @Override default UnmodSet<Entry<K,V>> entrySet() {
        final UnmodMap<K,V> parent = this;
        return new UnmodSet.AbstractUnmodSet<Entry<K,V>>() {
            @SuppressWarnings("unchecked")
            @Override public boolean contains(Object o) {
                if ( !(o instanceof Entry) ) { return false; }
                Entry<K,V> entry = (Entry<K,V>) o;
                if (!parent.containsKey(entry.getKey())) { return false; }
                V value = parent.get(entry.getKey());
                return Objects.equals(entry.getValue(), value);
            }

            @SuppressWarnings("unchecked")
            @Override public UnmodIterator<Entry<K,V>> iterator() {
                // Converting from
                // UnmodIterator<UnEntry<K,V>> to
                // UnmodIterator<Entry<K,V>>
                // Is a totally legal widening conversion (at runtime) because UnEntry extends
                // (is an) Entry.  But Java's type system doesn't know that because (I think)
                // it's a higher kinded type.  Thanks to type erasure, we can forget about all
                // that and cast it to a base type then suppress the unchecked warning.
                return (UnmodIterator) parent.iterator();
            }

            @Override public int size() { return parent.size(); }

            @Override public String toString() {
                return UnmodIterable.toString("UnmodMap.entrySet", this);
            }
        };
    }


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

    /** {@inheritDoc} */
    @Override default boolean isEmpty() { return size() == 0; }

    /**
     Returns a view of the keys contained in this map.  An UnmodMap is iterable, so this method
     is probably not nearly as useful as it once was.

     {@inheritDoc}
     */
    @Override default UnmodSet<K> keySet() {
        final UnmodMap<K,V> parent = this;
        return new UnmodSet.AbstractUnmodSet<K>() {
            @SuppressWarnings("SuspiciousMethodCalls")
            @Override public boolean contains(Object o) { return parent.containsKey(o); }

            @Override public UnmodIterator<K> iterator() {
                final UnmodIterator<UnEntry<K,V>> iter = parent.iterator();
                return new UnmodIterator<K>() {
                    @Override public boolean hasNext() { return iter.hasNext(); }
                    @Override public K next() { return iter.next().getKey(); }
                };
            }
            @Override public int size() { return parent.size(); }

            @Override public String toString() {
                return UnmodIterable.toString("UnmodMap.keySet", this);
            }
        };
    }

    /** Not allowed - this is supposed to be unmodifiable */
    @Override @Deprecated
    default V merge(K key, V value,
                    BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        throw new UnsupportedOperationException("Modification attempted");
    }
    /** Not allowed - this is supposed to be unmodifiable */
    @Override @Deprecated default V put(K key, V value) {
        throw new UnsupportedOperationException("Modification attempted");
    }
    /** Not allowed - this is supposed to be unmodifiable */
    @Override @Deprecated default void putAll(Map<? extends K, ? extends V> m) {
        throw new UnsupportedOperationException("Modification attempted");
    }
    /** Not allowed - this is supposed to be unmodifiable */
    @Override @Deprecated default V putIfAbsent(K key, V value) {
        throw new UnsupportedOperationException("Modification attempted");
    }
    /** Not allowed - this is supposed to be unmodifiable */
    @Override @Deprecated default V remove(Object key) {
        throw new UnsupportedOperationException("Modification attempted");
    }
    /** Not allowed - this is supposed to be unmodifiable */
    @Override @Deprecated default boolean remove(Object key, Object value) {
        throw new UnsupportedOperationException("Modification attempted");
    }
    /** Not allowed - this is supposed to be unmodifiable */
    @Override @Deprecated default boolean replace(K key, V oldValue, V newValue) {
        throw new UnsupportedOperationException("Modification attempted");
    }
    /** Not allowed - this is supposed to be unmodifiable */
    @Override @Deprecated default V replace(K key, V value) {
        throw new UnsupportedOperationException("Modification attempted");
    }
    /** Not allowed - this is supposed to be unmodifiable */
    @Override @Deprecated
    default void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        throw new UnsupportedOperationException("Modification attempted");
    }

// int	size()

    /**
     An UnmodMap is iterable, so this method is probably not nearly as useful as it once was.
     Actually, this might deserve to be deprecated.

     Definitely don't call .equals() or .hashCode() on what this returns.  It could have duplicates.
     If the Map isn't sorted, it could have random ordering.  Collection just isn't specific enough
     to instantiate, but we do it anyway here for backward compatibility.

     Returns a view of the values contained in this map.  java.util.HashMap returns an instance of
     java.util.HashMap.Values() when you call this method which (Java 8) does *not* have equals()
     or hashCode() defined.  So it only does referential equality and there is no way be equal to
     that.

     {@inheritDoc}
     */
    @Override default UnmodCollection<V> values() {
        final UnmodMap<K,V> parent = this;
        return new UnmodCollection<V>() {
            @SuppressWarnings("SuspiciousMethodCalls")
            @Override public boolean contains(Object o) { return parent.containsValue(o); }
            @Override public UnmodIterator<V> iterator() {
                final UnmodIterator<UnEntry<K,V>> iter = parent.iterator();
                return new UnmodIterator<V>() {
                    @Override public boolean hasNext() { return iter.hasNext(); }
                    @Override public V next() { return iter.next().getValue(); }
                };
            }
            @Override public int size() { return parent.size(); }

            @Override public String toString() {
                return UnmodIterable.toString("UnmodMap.values", this);
            }
        };
    }
}
