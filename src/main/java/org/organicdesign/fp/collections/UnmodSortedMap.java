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

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

/** An unmodifiable SortedMap. */
public interface UnmodSortedMap<K,V> extends UnmodMap<K,V>, SortedMap<K,V>, UnmodSortedIterable<UnmodMap.UnEntry<K,V>> {

    // ========================================= Instance =========================================

// public Comparator<? super K>	comparator()

    /**
     Returns a view of the mappings contained in this map.  The set should actually contain
     UnmodMap.Entry items, but that return signature is illegal in Java, so you'll just have to
     remember.
     */
    @Override default UnmodSortedSet<Entry<K,V>> entrySet() {
        UnmodSortedMap<K,V> parentMap = this;
        return new UnmodSortedSet<Entry<K,V>>() {
            @Override public int size() { return parentMap.size(); }

            @SuppressWarnings("unchecked")
            @Override public boolean contains(Object o) {
                if ( !(o instanceof Entry) ) { return false; }
                return containsKey(((Entry<K, V>) o).getKey());
            }

            @SuppressWarnings("unchecked")
            @Override public UnmodSortedIterator<Entry<K,V>> iterator() {
                // Converting from
                // UnmodSortedIterator<UnEntry<K,V>> to
                // UnmodSortedIterator<Entry<K,V>>
                // Is a totally legal widening conversion (at runtime) because UnEntry extends
                // (is an) Entry.  But Java's type system doesn't know that because (I think)
                // it's a higher kinded type.  Thanks to type erasure, we can forget about all
                // that and cast it to a base type then suppress the unchecked warning.
                return (UnmodSortedIterator) parentMap.iterator();
            }

            @SuppressWarnings("unchecked")
            @Override public Comparator<Entry<K,V>> comparator() {
                if (parentMap.comparator() == null) {
                    return (a, b) -> Equator.ComparisonContext.DEFAULT_COMPARATOR
                                                        .compare((Comparable) a.getKey(),
                                                                 (Comparable) b.getKey());
                    // This may be more flexible, but from what I can tell, nothing else in the
                    // chain is this flexible and it's just going to be unused code that can't be
                    // tested.  For an unsorted Map, this may be appropriate.
//                    .compare((a == null) ? null : (Comparable) a.getKey(),
//                             (b == null) ? null : (Comparable) b.getKey());

                }
                return (o1, o2) -> parentMap.comparator().compare(o1.getKey(), o2.getKey());
            }

            @Override public UnmodSortedSet<Entry<K,V>> subSet(Entry<K,V> fromElement,
                                                               Entry<K,V> toElement) {
                return parentMap.subMap(fromElement.getKey(), toElement.getKey()).entrySet();
            }

            @Override public UnmodSortedSet<Entry<K,V>> tailSet(Entry<K,V> fromElement) {
                return parentMap.tailMap(fromElement.getKey()).entrySet();
            }

            @Override public Entry<K,V> first() {
                K key = parentMap.firstKey();
                return Tuple2.of(key, parentMap.get(key));
            }

            @Override public Entry<K,V> last() {
                K key = parentMap.lastKey();
                return Tuple2.of(key, parentMap.get(key));
            }

            @Override public int hashCode() { return UnmodIterable.hashCode(this); }

            @SuppressWarnings("unchecked")
            @Override public boolean equals(Object o) {
                if (this == o) { return true; }

                // java.util.SortedMap.entrySet() returns just a Set, not a SortedSet, even though
                // the order is guaranteed to be the same as the SortedMap it came from as
                // guaranteed by the comparator (which seems more like a SortedSet, but no-one
                // asked me).  So we have to accept a Set here for equals 'cause Java might
                // hand us one.  All of this could have been avoided if SortedMap extended
                // Collection<Map.Entry<K,V>> which is essentially an Iterable with a size().
                if ( !(o instanceof Set) ) { return false; }

                // If you're using UncleJim, then you should have passed us a sortedSet.
                if ( (o instanceof UnmodSet) &&
                     !(o instanceof UnmodSortedSet) ) {
                    return false;
                }

                Set<Map.Entry<K,V>> that = (Set<Map.Entry<K,V>>) o;
                if (that.size() != this.size()) { return false; }

                // Here we are again.  The default implementation of EntrySet.equals() is in
                // java.util.AbstractSet and it ignores order, even though this has a guaranteed
                // order.  I sort of think that's wrong, but lacking absolute certainty, I choose
                // compatibility.  If you want a better equals test, use an Equator.
                // TODO: Test vs. TreeMap!

//                try {
                    return containsAll(that);

                // I was unable to write a test for this.  With an unsorted map, sure,
                // but you can't put an un-castable class or a null into a sorted map.
                // When we can write a test to prove this code is used, we'll bring it back.
//                } catch (ClassCastException ignore)   {
//                    return false;
//                } catch (NullPointerException ignore) {
//                    return false;
//                }
            }

            @Override public String toString() {
                return UnmodIterable.toString("UnmodSortedMap.entrySet", this);
            }
        };
    }

// public  K	firstKey()

    /** {@inheritDoc} */
    @Override default UnmodSortedMap<K,V> headMap(K toKey) { return subMap(firstKey(), toKey); }

//    /** {@inheritDoc} */
//    @Override default UnmodSortedIterator<UnEntry<K,V>> iterator() {
//        return UnmodMap.UnEntry.unSortIterEntToUnSortIterUnEnt(entrySet().iterator());
//    }

    /** Returns a view of the keys contained in this map. */
    @Override default UnmodSortedSet<K> keySet() {
        UnmodSortedMap<K,V> parentMap = this;
        return new UnmodSortedSet<K>() {
            @SuppressWarnings("SuspiciousMethodCalls")
            @Override public boolean contains(Object o) { return parentMap.containsKey(o); }

            @Override public UnmodSortedIterator<K> iterator() {
                return new UnmodSortedIterator<K>() {
                    Iterator<UnEntry<K,V>> iter = parentMap.iterator();
                    @Override public boolean hasNext() { return iter.hasNext(); }
                    @Override public K next() { return iter.next().getKey(); }
                };
            }

            @Override public int size() { return parentMap.size(); }

            @Override public UnmodSortedSet<K> subSet(K fromElement, K toElement) {
                return parentMap.subMap(fromElement, toElement).keySet();
            }

            @Override public UnmodSortedSet<K> tailSet(K fromElement) {
                return parentMap.tailMap(fromElement).keySet();
            }

            @Override public Comparator<? super K> comparator() {
                return (parentMap.comparator() == null) ? Equator.defaultComparator()
                                                        : parentMap.comparator();
            }

            @Override public K first() {
                return parentMap.firstKey();
            }

            @Override
            public K last() {
                return parentMap.lastKey();
            }

            @Override public int hashCode() { return UnmodIterable.hashCode(this); }

            @SuppressWarnings("unchecked")
            @Override public boolean equals(Object o) {
                if (this == o) { return true; }

                // java.util.SortedMap.entrySet() returns just a Set, not a SortedSet, even though
                // the order is guaranteed to be the same as the SortedMap it came from as
                // guaranteed by the comparator (which seems more like a SortedSet, but no-one
                // asked me).  So we have to accept a Set here for equals 'cause Java might
                // hand us one.  All of this could have been avoided if SortedMap extended
                // Collection<Map.Entry<K,V>> which is essentially an Iterable with a size().
                if ( !(o instanceof Set) ) { return false; }

                // If you're using UncleJim, then you should have passed us a sortedSet.
                if ( (o instanceof UnmodSet) &&
                     !(o instanceof UnmodSortedSet) ) {
                    return false;
                }

                Set<K> that = (Set<K>) o;
                if (that.size() != this.size()) { return false; }

                // OK, this is a pain because Map.Entries may not know how to equal Tuples.
                // So this compares based on the interface, not on the implementation.
                // Hmm... Maybe this should ues the comparator instead?
                Iterator<K> as = this.iterator();
                Iterator<K> bs = that.iterator();
                Comparator<? super K> comp = comparator();
                assert comp != null;
                while (as.hasNext() && bs.hasNext()) {
                    K a = as.next();
                    K b = bs.next();
                    if (comp.compare(a, b) != 0) {
                        return false;
                    }
//                    if (aEntry == bEntry) {
//                        continue; // it's good, check the next.
//                    }
//                    if ( (aEntry == null) || (bEntry == null) ) {
//                        return false; // they are different.
//                    }
//                    if (!Objects.equals(aEntry.getKey(), bEntry.getKey())) {
//                        return false;
//                    }
//                    if (!Objects.equals(aEntry.getValue(), bEntry.getValue())) {
//                        return false;
//                    }
                }
                return !as.hasNext() && !bs.hasNext();
            }
            @Override public String toString() {
                return UnmodIterable.toString("UnmodSortedMap.entrySet", this);
            }

        };
    }

// public  K	lastKey()

    /** {@inheritDoc} */
    @Override UnmodSortedMap<K,V> subMap(K fromKey, K toKey);

    /** {@inheritDoc} */
    @Override UnmodSortedMap<K,V> tailMap(K fromKey);

    /**
     This method is deprecated on UnmodMap because equals() and hashCode() cannot be implemented
     on the resulting collection, but the guaranteed order of the result in a SortedMap makes this
     able to return a List.  It's still an unnecessary convenience method and you should use
     this map as an Iterable instead for consistency in dealing with all maps.

     <pre><code>mySortedMap.map((UnEntry&lt;K,V&gt; entry) -&gt; entry.getValue())
             .toImList();</code></pre>

     {@inheritDoc}
     */
    @SuppressWarnings("deprecation")
    @Override default UnmodList<V> values() {
        return map((UnEntry<K,V> entry) -> entry.getValue())
                .toImList();
//        UnmodSortedMap<K,V> parentMap = this;
//        return new UnmodSortedCollection<V>() {
//            @Override public UnmodSortedIterator<V> iterator() {
//                return new UnmodSortedIterator<V>() {
//                    Iterator<UnEntry<K,V>> iter = parentMap.iterator();
//                    @Override public boolean hasNext() { return iter.hasNext(); }
//                    @Override public V next() { return iter.next().getValue(); }
//                };
//            }
//            @Override public int size() { return parentMap.size(); }
//
//            @SuppressWarnings("SuspiciousMethodCalls")
//            @Override public boolean contains(Object o) { return parentMap.containsValue(o); }
//
//            @Override public int hashCode() { return UnmodIterable.hashCode(this); }
//
//            @SuppressWarnings("unchecked")
//            @Override public boolean equals(Object o) {
//                if (this == o) { return true; }
//
//                // java.util.SortedMap.entrySet() returns just a Set, not a SortedSet, even though
//                // the order is guaranteed to be the same as the SortedMap it came from as
//                // guaranteed by the comparator (which seems more like a SortedSet, but no-one
//                // asked me).  So we have to accept a Set here for equals 'cause Java might
//                // hand us one.  All of this could have been avoided if SortedMap extended
//                // Collection<Map.Entry<K,V>> which is essentially an Iterable with a size().
//                if ( !(o instanceof Collection) ) { return false; }
//
//                // If you're using UncleJim, then you should have passed us a sortedSet.
//                if ( (o instanceof UnmodCollection) &&
//                     !(o instanceof UnmodSortedCollection) ) {
//                    return false;
//                }
//
//                Collection<V> that = (Collection<V>) o;
//                if (that.size() != this.size()) { return false; }
//
//                return UnmodSortedIterable.equals(this,
//                                                  UnmodSortedIterable.castFromCollection(that));
//            }
//            @Override public String toString() {
//                return UnmodIterable.toString("UnmodSortedMap.entrySet", this);
//            }
//
//        };
    }

// Methods inherited from interface java.util.Map
// clear, compute, computeIfAbsent, computeIfPresent, containsKey, containsValue, equals, forEach, get, getOrDefault, hashCode, isEmpty, merge, put, putAll, putIfAbsent, remove, remove, replace, replace, replaceAll, size
}
