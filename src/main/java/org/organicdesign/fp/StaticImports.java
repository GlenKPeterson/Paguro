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

package org.organicdesign.fp;

import org.organicdesign.fp.collections.UnCollection;
import org.organicdesign.fp.collections.UnIterable;
import org.organicdesign.fp.collections.UnIterator;
import org.organicdesign.fp.collections.UnIteratorOrdered;
import org.organicdesign.fp.collections.UnList;
import org.organicdesign.fp.collections.UnListIterator;
import org.organicdesign.fp.collections.UnMap;
import org.organicdesign.fp.collections.UnMapOrdered;
import org.organicdesign.fp.collections.UnSet;
import org.organicdesign.fp.collections.UnSetOrdered;
import org.organicdesign.fp.tuple.Tuple2;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;

/**
 Contains methods for building immutable collections.  These will never return null, the closest they get is to return
 an empty immutable collection (the same one is reused).  The skipNull versions aid immutable programming since
 you can build a map of unknown size as follows:
 <pre><code>unMapSkipNull(Tuple2.of("hello", 33),
             Tuple2.of("there", 44),
             currUser.isFred ? Tuple2.of("Fred", 55) : null);</code></pre>
 */
@SuppressWarnings("UnusedDeclaration")
public class StaticImports {
    // Prevent instantiation
    private StaticImports() { throw new UnsupportedOperationException("No instantiation"); }

    // EqualsWhichDoesntCheckParameterClass Note:
    // http://codereview.stackexchange.com/questions/88333/is-one-sided-equality-more-helpful-or-more-confusing-than-quick-failure
    // "There is no one-sided equality. If it is one-sided, that is it's asymmetric, then it's just wrong."
    // Which is a little ironic because with inheritance, there are many cases in Java where equality is one-sided.

    /** Returns an unmodifiable version of the given iterable. */
    // TODO: Test this.
    public static <T> UnIterable<T> un(Iterable<T> iterable) {
        if (iterable == null) { return () -> UnIterator.empty(); }
        if (iterable instanceof UnIterable) { return (UnIterable<T>) iterable; }
        return () -> new UnIterator<T>() {
            private final Iterator<T> iter = iterable.iterator();
            @Override public boolean hasNext() { return iter.hasNext(); }
            @Override public T next() { return iter.next(); }
            // Defining equals and hashcode makes no sense because can't call them without changing the iterator
            // which both makes it useless, and changes the equals and hashcode results.
//            @Override public int hashCode() { return iter.hashCode(); }
//            @SuppressWarnings("EqualsWhichDoesntCheckParameterClass") // See Note above.
//            @Override public boolean equals(Object o) { return iter.equals(o); }
        };
    }

    /** Returns an unmodifiable version of the given iterator. */
    // TODO: Never make this public.  We can't trust an iterator that we didn't get
    // brand new ourselves, because iterators are inherently unsafe to share.
    private static <T> UnIterator<T> un(Iterator<T> iter) {
        if (iter == null) { return UnIterator.empty(); }
        if (iter instanceof UnIterator) { return (UnIterator<T>) iter; }
        return new UnIterator<T>() {
            @Override public boolean hasNext() { return iter.hasNext(); }
            @Override public T next() { return iter.next(); }
            // Defining equals and hashcode makes no sense because can't call them without changing the iterator
            // which both makes it useless, and changes the equals and hashcode results.
//            @Override public int hashCode() { return iter.hashCode(); }
//            @SuppressWarnings("EqualsWhichDoesntCheckParameterClass") // See Note above.
//            @Override public boolean equals(Object o) { return iter.equals(o); }
        };
    }

    /** Returns an unmodifiable version of the given listiterator. */
    public static <T> UnListIterator<T> un(ListIterator<T> iter) {
        if (iter == null) { return UnListIterator.empty(); }
        if (iter instanceof UnListIterator) { return (UnListIterator<T>) iter; }
        return new UnListIterator<T>() {
            @Override public boolean hasNext() { return iter.hasNext(); }
            @Override public T next() { return iter.next(); }
            @Override public boolean hasPrevious() { return iter.hasPrevious(); }
            @Override public T previous() { return iter.previous(); }
            @Override public int nextIndex() { return iter.nextIndex(); }
            @Override public int previousIndex() { return iter.previousIndex(); }
            // Defining equals and hashcode makes no sense because can't call them without changing the iterator
            // which both makes it useless, and changes the equals and hashcode results.
//            @Override public int hashCode() { return iter.hashCode(); }
//            @SuppressWarnings("EqualsWhichDoesntCheckParameterClass") // See Note above.
//            @Override public boolean equals(Object o) { return iter.equals(o); }
        };
    }

    /** Returns an unmodifiable version of the given list. */
    public static <T> UnList<T> un(List<T> inner) {
        if (inner == null) { return UnList.empty(); }
        if (inner instanceof UnList) { return (UnList<T>) inner; }
        if (inner.size() < 1) { return UnList.empty(); }
        return new UnList<T>() {
            @Override public UnListIterator<T> listIterator(int index) { return un(inner.listIterator(index)); }
            @Override public int size() { return inner.size(); }
            @Override public T get(int index) { return inner.get(index); }
            @Override public int hashCode() { return inner.hashCode(); }
            @SuppressWarnings("EqualsWhichDoesntCheckParameterClass") // See Note above.
            @Override public boolean equals(Object o) { return inner.equals(o); }
        };
    }

    /** Returns an unmodifiable version of the given set. */
    public static <T> UnSet<T> un(Set<T> set) {
        if (set == null) { return UnSet.empty(); }
        if (set instanceof UnSet) { return (UnSet<T>) set; }
        if (set.size() < 1) { return UnSet.empty(); }
        return new UnSet<T>() {
            @Override public boolean contains(Object o) { return set.contains(o); }
            @Override public int size() { return set.size(); }
            @Override public boolean isEmpty() { return set.isEmpty(); }
            @Override public UnIterator<T> iterator() { return un(set.iterator()); }
            @Override public int hashCode() { return set.hashCode(); }
            @SuppressWarnings("EqualsWhichDoesntCheckParameterClass") // See Note above.
            @Override public boolean equals(Object o) { return set.equals(o); }
        };
    }

    /** Returns an unmodifiable version of the given set. */
    public static <T> UnSetOrdered<T> un(SortedSet<T> set) {
        if (set == null) { return UnSetOrdered.empty(); }
        if (set instanceof UnSetOrdered) { return (UnSetOrdered<T>) set; }
        if (set.size() < 1) { return UnSetOrdered.empty(); }
        return new UnSetOrdered<T>() {
            @Override public Comparator<? super T> comparator() { return set.comparator(); }
            @Override public UnSetOrdered<T> subSet(T fromElement, T toElement) {
                return un(set.subSet(fromElement, toElement));
            }
            @Override public UnSetOrdered<T> headSet(T toElement) { return un(set.headSet(toElement)); }
            @Override public UnSetOrdered<T> tailSet(T fromElement) { return un(set.tailSet(fromElement)); }
            @Override public T first() { return set.first(); }
            @Override public T last() { return set.last(); }
            @Override public boolean contains(Object o) { return set.contains(o); }
            @Override public int size() { return set.size(); }
            @Override public boolean isEmpty() { return set.isEmpty(); }
            @Override public UnIteratorOrdered<T> iterator() {
                return new UnIteratorOrdered<T>() {
                    Iterator<T> iter = set.iterator();
                    @Override public boolean hasNext() { return iter.hasNext(); }
                    @Override public T next() { return iter.next(); }
                };
            }
            @Override public int hashCode() { return set.hashCode(); }
            @SuppressWarnings("EqualsWhichDoesntCheckParameterClass") // See Note above.
            @Override public boolean equals(Object o) { return set.equals(o); }
        };
    }

    /** Returns an unmodifiable version of the given map. */
    public static <K,V> UnMap<K,V> un(Map<K,V> map) {
        if (map == null) { return UnMap.empty(); }
        if (map instanceof UnMap) { return (UnMap<K,V>) map; }
        if (map.size() < 1) { return UnMap.empty(); }
        return new UnMap<K,V>() {
            /** {@inheritDoc} */
            @Override
            public UnIterator<UnEntry<K,V>> iterator() { return UnMap.UnEntry.wrap(map.entrySet().iterator()); }

            @Override public UnSet<Map.Entry<K,V>> entrySet() { return un(map.entrySet()); }
            @Override public int size() { return map.size(); }
            @Override public boolean isEmpty() { return map.isEmpty(); }
            @Override public boolean containsKey(Object key) { return map.containsKey(key); }
            @Override public boolean containsValue(Object value) { return map.containsValue(value); }
            @Override public V get(Object key) { return map.get(key); }
            @Override public UnSet<K> keySet() { return un(map.keySet()); }
            @Override public UnCollection<V> values() { return un(map.values()); }
            @Override public int hashCode() { return map.hashCode(); }
            @SuppressWarnings("EqualsWhichDoesntCheckParameterClass") // See Note above.
            @Override public boolean equals(Object o) { return map.equals(o); }
        };
    }

    /** Returns an unmodifiable version of the given sorted map. */
    public static <K,V> UnMapOrdered<K,V> un(SortedMap<K,V> map) {
        if (map == null) { return UnMapOrdered.empty(); }
        if (map instanceof UnMapOrdered) { return (UnMapOrdered<K,V>) map; }
        if (map.size() < 1) { return UnMapOrdered.empty(); }
        return new UnMapOrdered<K,V>() {
            // TODO: Test this.
            @Override public UnSetOrdered<Entry<K,V>> entrySet() {
                return new UnSetOrdered<Entry<K,V>>() {
                    Set<Map.Entry<K,V>> entrySet = map.entrySet();
                    @Override public UnIteratorOrdered<Entry<K,V>> iterator() {
                        return new UnIteratorOrdered<Entry<K,V>>() {
                            Iterator<Entry<K,V>> iter = entrySet.iterator();
                            @Override public boolean hasNext() { return iter.hasNext(); }
                            @Override public Entry<K,V> next() { return iter.next(); }
                        };
                    }
                    @Override public UnSetOrdered<Entry<K,V>> subSet(Entry<K,V> fromElement, Entry<K,V> toElement) {
                        // This is recursive.  I hope it's not an infinite loop 'cause I don't want to write this
                        // all out again.
                        return un(map.subMap(fromElement.getKey(), toElement.getKey())).entrySet();
                    }
                    @Override public Comparator<? super Entry<K,V>> comparator() {
                        return (o1, o2) -> map.comparator().compare(o1.getKey(), o2.getKey());
                    }
                    @Override public Entry<K,V> first() {
                        K key = map.firstKey();
                        return Tuple2.of(key, map.get(key));
                    }

                    @Override public Entry<K,V> last() {
                        K key = map.lastKey();
                        return Tuple2.of(key, map.get(key));
                    }
                    @Override public boolean contains(Object o) { return entrySet.contains(o); }
                    @Override public boolean isEmpty() { return entrySet.isEmpty(); }
                    @Override public int size() { return entrySet.size(); }
                    @Override public int hashCode() { return entrySet.hashCode(); }
                    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass") // See Note above.
                    @Override public boolean equals(Object o) { return entrySet.equals(o); }
                };
            }
            @Override public int size() { return map.size(); }
            @Override public boolean isEmpty() { return map.isEmpty(); }
            @Override public boolean containsKey(Object key) { return map.containsKey(key); }
            @Override public boolean containsValue(Object value) { return map.containsValue(value); }
            @Override public V get(Object key) { return map.get(key); }
            @Override public UnSet<K> keySet() { return un(map.keySet()); }
            @Override public Comparator<? super K> comparator() { return map.comparator(); }
            @Override public UnMapOrdered<K,V> subMap(K fromKey, K toKey) { return un(map.subMap(fromKey, toKey)); }
            @Override public UnMapOrdered<K,V> tailMap(K fromKey) { return un(map.tailMap(fromKey)); }
            @Override public K firstKey() { return map.firstKey(); }
            @Override public K lastKey() { return map.lastKey(); }
            @Override public UnCollection<V> values() { return un(map.values()); }
            @Override public int hashCode() { return map.hashCode(); }
            @SuppressWarnings("EqualsWhichDoesntCheckParameterClass") // See Note above.
            @Override public boolean equals(Object o) { return map.equals(o); }
        };
    }

    /** Returns an unmodifiable version of the given collection. */
    public static <T> UnCollection<T> un(Collection<T> coll) {
        if (coll == null) { return UnCollection.empty(); }
        if (coll instanceof UnCollection) { return (UnCollection<T>) coll; }
        if (coll.size() < 1) { return UnCollection.empty(); }
        return new UnCollection<T>() {
            @Override public boolean contains(Object o) { return coll.contains(o); }
            @Override public int size() { return coll.size(); }
            @Override public boolean isEmpty() { return coll.isEmpty(); }
            @Override public UnIterator<T> iterator() { return un(coll.iterator()); }
            @Override public int hashCode() { return coll.hashCode(); }
            @SuppressWarnings("EqualsWhichDoesntCheckParameterClass") // See Note above.
            @Override public boolean equals(Object o) { return coll.equals(o); }
        };
    }

//    /** Returns an unmodifiable Map containing all passed pairs (including null keys/values). */
//    public static <K,V> UnMap<K,V> unMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5,
//                                         K k6, V v6, K k7, V v7, K k8, V v8, K k9, V v9,
//                                         K k10, V v10) {
//        Map<K,V> m = new HashMap<>(20);
//        m.put(k1, v1); m.put(k2, v2); m.put(k3, v3); m.put(k4, v4); m.put(k5, v5); m.put(k6, v6);
//        m.put(k7, v7); m.put(k8, v8); m.put(k9, v9); m.put(k10, v10);
//        return un(m);
//    }
//    /** Returns an unmodifiable Map containing all passed pairs (including null keys/values). */
//    public static <K,V> UnMap<K,V> unMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5,
//                                         K k6, V v6, K k7, V v7, K k8, V v8, K k9, V v9) {
//        Map<K,V> m = new HashMap<>(9);
//        m.put(k1, v1); m.put(k2, v2); m.put(k3, v3); m.put(k4, v4); m.put(k5, v5); m.put(k6, v6);
//        m.put(k7, v7); m.put(k8, v8); m.put(k9, v9);
//        return un(m);
//    }
//    /** Returns an unmodifiable Map containing all passed pairs (including null keys/values). */
//    public static <K,V> UnMap<K,V> unMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5,
//                                         K k6, V v6, K k7, V v7, K k8, V v8) {
//        Map<K,V> m = new HashMap<>(8);
//        m.put(k1, v1); m.put(k2, v2); m.put(k3, v3); m.put(k4, v4); m.put(k5, v5); m.put(k6, v6);
//        m.put(k7, v7); m.put(k8, v8);
//        return un(m);
//    }
//    /** Returns an unmodifiable Map containing all passed pairs (including null keys/values). */
//    public static <K,V> UnMap<K,V> unMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5,
//                                         K k6, V v6, K k7, V v7) {
//        Map<K,V> m = new HashMap<>(7);
//        m.put(k1, v1); m.put(k2, v2); m.put(k3, v3); m.put(k4, v4); m.put(k5, v5); m.put(k6, v6);
//        m.put(k7, v7);
//        return un(m);
//    }
//    /** Returns an unmodifiable Map containing all passed pairs (including null keys/values). */
//    public static <K,V> UnMap<K,V> unMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5,
//                                         K k6, V v6) {
//        Map<K,V> m = new HashMap<>(6);
//        m.put(k1, v1); m.put(k2, v2); m.put(k3, v3); m.put(k4, v4); m.put(k5, v5); m.put(k6, v6);
//        return un(m);
//    }
//    /** Returns an unmodifiable Map containing all passed pairs (including null keys/values). */
//    public static <K,V> UnMap<K,V> unMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5) {
//        Map<K,V> m = new HashMap<>(5);
//        m.put(k1, v1); m.put(k2, v2); m.put(k3, v3); m.put(k4, v4); m.put(k5, v5);
//        return un(m);
//    }
//    /** Returns an unmodifiable Map containing all passed pairs (including null keys/values). */
//    public static <K,V> UnMap<K,V> unMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
//        Map<K,V> m = new HashMap<>(4); m.put(k1, v1); m.put(k2, v2); m.put(k3, v3); m.put(k4, v4);
//        return un(m);
//    }
//    /** Returns an unmodifiable Map containing all passed pairs (including null keys/values). */
//    public static <K,V> UnMap<K,V> unMap(K k1, V v1, K k2, V v2, K k3, V v3) {
//        Map<K,V> m = new HashMap<>(3); m.put(k1, v1); m.put(k2, v2); m.put(k3, v3);
//        return un(m);
//    }
//    /** Returns an unmodifiable Map containing all passed pairs (including null keys/values). */
//    public static <K,V> UnMap<K,V> unMap(K k1, V v1, K k2, V v2) {
//        Map<K,V> m = new HashMap<>(2); m.put(k1, v1); m.put(k2, v2);
//        return un(m);
//    }
//    /** Returns an unmodifiable Map containing all passed pairs (including null keys/values). */
//    public static <K,V> UnMap<K,V> unMap(K k1, V v1) {
//        Map<K,V> m = new HashMap<>(1); m.put(k1, v1); return un(m);
//    }
//
//    /** Returns an unmodifiable Map containing any non-null passed items. */
//    @SafeVarargs
//    public static <K,V> UnMap<K,V> unMapSkipNull(Map.Entry<K,V>... es) {
//        if (es == null) { return UnMap.empty(); }
//        Map<K,V> m = new HashMap<>();
//        for (Map.Entry<K,V> entry : es) {
//            if (entry != null) {
//                m.put(entry.getKey(), entry.getValue());
//            }
//        }
//        return un(m);
//    }
//
//    /** Returns an unmodifiable Set containing all passed items (including null items). */
//    @SuppressWarnings("unchecked")
//    @SafeVarargs
//    public static <T> UnSet<T> unSet(T... ts) {
//        return ( (ts == null) || (ts.length < 1) )
//                ? UnSet.empty()
//                : un(new HashSet<>(Arrays.asList(ts)));
//    }
//
//    /** Returns an unmodifiable Set containing any non-null passed items. */
//    @SuppressWarnings("unchecked")
//    @SafeVarargs
//    public static <T> UnSet<T> unSetSkipNull(T... ts) {
//        if ( (ts == null) || (ts.length < 1) ) {
//            return UnSet.empty();
//        }
//        Set<T> s = new HashSet<>();
//        for (T t : ts) {
//            if (t != null) {
//                s.add(t);
//            }
//        }
//        return (s.size() > 0) ? un(s) : UnSet.empty();
//    }

//    /**
//     * Returns an int which is a unique and correct hash code for the objects passed.  This hashcode is recomputed on
//     * every call, so that if any of these objects change their hashCodes, this will always return the latest value.
//     * Of course, if you add something to a collection that uses a hashCode, then that hashCode changes, you're going
//     * to have problems!
//     */
//    public static int hashCoder(Object... ts) {
//        if (ts == null) {
//            return 0;
//        }
//        int ret = 0;
//        for (Object t : ts) {
//            if (t != null) {
//                ret = ret ^ t.hashCode();
//            }
//        }
//        return ret;
//    }
//
//    /**
//     * Use this only if you can guarantee to pass it immutable objects only!  Returns a LazyInt that will compute a
//     * unique and correct hash code on the first time it is called, then return that primitive int very quickly for all
//     * future calls.  If any of the hashCodes for the objects passed in change after that time, it will not affect the
//     * output of this function.  Of course, if you add something to a collection that uses a hashCode and then change
//     * its hashCode, the behavior is undefined, so changing things after that time is a bad idea anyway.  Still,
//     * correct is more important than fast, so make good decisions about when to use this.
//     */
//    public static Lazy.Int lazyHashCoder(Object... ts) {
//        if ( (ts == null) || (ts.length < 1) ) {
//            return Lazy.Int.ZERO;
//        }
//        return Lazy.Int.of(() -> {
//            int ret = 0;
//            for (Object t : ts) {
//                if (t != null) {
//                    ret = ret ^ t.hashCode();
//                }
//            }
//            return ret;
//        });
//    }
}
