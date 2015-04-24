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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import org.organicdesign.fp.collections.PersistentTreeSet;
import org.organicdesign.fp.collections.UnCollection;
import org.organicdesign.fp.collections.UnIterator;
import org.organicdesign.fp.collections.UnList;
import org.organicdesign.fp.collections.UnListIterator;
import org.organicdesign.fp.collections.UnMap;
import org.organicdesign.fp.collections.UnMap.UnEntry;
import org.organicdesign.fp.collections.UnSet;
import org.organicdesign.fp.collections.UnSetSorted;

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

    /** Returns an unmodifiable version of the given listiterator. */
    public static <T> UnIterator<T> un(Iterator<T> inner) {
        if (inner == null) { return UnIterator.empty(); }
        if (inner instanceof UnIterator) { return (UnIterator<T>) inner; }
        return new UnIterator<T>() {
            @Override public boolean hasNext() { return inner.hasNext(); }
            @Override public T next() { return inner.next(); }

            @Override public int hashCode() { return inner.hashCode(); }
            @Override public boolean equals(Object o) { return inner.equals(o); }
        };
    }

    /** Returns an unmodifiable version of the given listiterator. */
    public static <T> UnListIterator<T> un(ListIterator<T> inner) {
        if (inner == null) { return UnListIterator.empty(); }
        if (inner instanceof UnListIterator) { return (UnListIterator<T>) inner; }
        return new UnListIterator<T>() {
            @Override public boolean hasNext() { return inner.hasNext(); }
            @Override public T next() { return inner.next(); }
            @Override public boolean hasPrevious() { return inner.hasPrevious(); }
            @Override public T previous() { return inner.previous(); }
            @Override public int nextIndex() { return inner.nextIndex(); }
            @Override public int previousIndex() { return inner.previousIndex(); }

            @Override public int hashCode() { return inner.hashCode(); }
            @Override public boolean equals(Object o) { return inner.equals(o); }
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
            @Override public boolean equals(Object o) { return inner.equals(o); }
        };
    }

    /** Returns an unmodifiable version of the given set. */
    public static <T> UnSet<T> un(Set<T> inner) {
        if (inner == null) { return UnSet.empty(); }
        if (inner instanceof UnSet) { return (UnSet<T>) inner; }
        if (inner.size() < 1) { return UnSet.empty(); }
        return new UnSet<T>() {
            @Override public boolean contains(Object o) { return inner.contains(o); }
            @Override public int size() { return inner.size(); }
            @Override public boolean isEmpty() { return inner.isEmpty(); }
            @Override public UnIterator<T> iterator() { return un(inner.iterator()); }

            @Override public int hashCode() { return inner.hashCode(); }
            @Override public boolean equals(Object o) { return inner.equals(o); }
        };
    }

    /** Returns an unmodifiable version of the given set. */
    public static <T> UnSetSorted<T> un(SortedSet<T> set) {
        if (set == null) { return PersistentTreeSet.empty(); }
        if (set instanceof UnSetSorted) { return (UnSetSorted<T>) set; }
        if (set.size() < 1) { return PersistentTreeSet.empty(); }
        class UnSetSortedWrapper<E> implements UnSetSorted<E> {
            private final SortedSet<E> inner;
            private UnSetSortedWrapper(SortedSet<E> i) { inner = i; }
            @Override public Comparator<? super E> comparator() { return inner.comparator(); }
            @Override public UnSetSorted<E> subSet(E fromElement, E toElement) {
                return un(inner.subSet(fromElement, toElement));
            }
            @Override public UnSetSorted<E> headSet(E toElement) { return un(inner.headSet(toElement)); }
            @Override public UnSetSorted<E> tailSet(E fromElement) { return un(inner.tailSet(fromElement)); }
            @Override public E first() { return inner.first(); }
            @Override public E last() { return inner.last(); }
            @Override public boolean contains(Object o) { return inner.contains(o); }
            @Override public int size() { return inner.size(); }
            @Override public boolean isEmpty() { return inner.isEmpty(); }
            @Override public UnIterator<E> iterator() { return un(inner.iterator()); }
            @Override public int hashCode() { return inner.hashCode(); }
            @Override public boolean equals(Object o) {
                return inner.equals((o instanceof UnSetSortedWrapper) ? ((UnSetSortedWrapper) o).inner : o); }
        };
        return new UnSetSortedWrapper<>(set);
    }

    /** Returns an unmodifiable version of the given list. */
    public static <K,V> UnMap<K,V> un(Map<K,V> inner) {
        if (inner == null) { return UnMap.empty(); }
        if (inner instanceof UnMap) { return (UnMap<K,V>) inner; }
        if (inner.size() < 1) { return UnMap.empty(); }
        return new UnMap<K,V>() {
            /** {@inheritDoc} */
            @Override public UnIterator<UnEntry<K,V>> iterator() {
                return UnMap.UnEntry.wrap(entrySet().iterator());
            }
            @Override public UnSet<Map.Entry<K,V>> entrySet() { return un(inner.entrySet()); }
            @Override public int size() { return inner.size(); }
            @Override public boolean isEmpty() { return inner.isEmpty(); }
            @Override public boolean containsKey(Object key) { return inner.containsKey(key); }
            @Override public boolean containsValue(Object value) { return inner.containsValue(value); }
            @Override public V get(Object key) { return inner.get(key); }
            @Override public UnSet<K> keySet() { return un(inner.keySet()); }
            @Override public UnCollection<V> values() { return un(inner.values()); }

            @Override public int hashCode() { return inner.hashCode(); }
            @Override public boolean equals(Object o) { return inner.equals(o); }
        };
    }

    /** Returns an unmodifiable version of the given list. */
    public static <T> UnCollection<T> un(Collection<T> inner) {
        if (inner == null) { return UnCollection.empty(); }
        if (inner instanceof UnCollection) { return (UnCollection<T>) inner; }
        if (inner.size() < 1) { return UnCollection.empty(); }
        return new UnCollection<T>() {
            @Override public boolean contains(Object o) { return inner.contains(o); }
            @Override public int size() { return inner.size(); }
            @Override public boolean isEmpty() { return inner.isEmpty(); }
            @Override public UnIterator<T> iterator() { return un(inner.iterator()); }

            @Override public int hashCode() { return inner.hashCode(); }
            @Override public boolean equals(Object o) { return inner.equals(o); }
        };
    }

    /** Returns an unmodifiable Map containing all passed pairs (including null keys/values/values). */
    public static <K,V> Map<K,V> unMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5,
                                       K k6, V v6, K k7, V v7, K k8, V v8, K k9, V v9, K k10, V v10,
                                       K k11, V v11, K k12, V v12, K k13, V v13, K k14, V v14,
                                       K k15, V v15, K k16, V v16, K k17, V v17, K k18, V v18,
                                       K k19, V v19, K k20, V v20) {
        Map<K,V> m = new HashMap<>(20);
        m.put(k1, v1); m.put(k2, v2); m.put(k3, v3); m.put(k4, v4); m.put(k5, v5); m.put(k6, v6);
        m.put(k7, v7); m.put(k8, v8); m.put(k9, v9); m.put(k10, v10); m.put(k11, v11);
        m.put(k12, v12); m.put(k13, v13); m.put(k14, v14); m.put(k15, v15); m.put(k16, v16);
        m.put(k17, v17); m.put(k18, v18); m.put(k19, v19); m.put(k20, v20);
        return un(m);
    }
    /** Returns an unmodifiable Map containing all passed pairs (including null keys/values). */
    public static <K,V> Map<K,V> unMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5,
                                       K k6, V v6, K k7, V v7, K k8, V v8, K k9, V v9, K k10, V v10,
                                       K k11, V v11, K k12, V v12, K k13, V v13, K k14, V v14,
                                       K k15, V v15, K k16, V v16, K k17, V v17, K k18, V v18,
                                       K k19, V v19) {
        Map<K,V> m = new HashMap<>(20);
        m.put(k1, v1); m.put(k2, v2); m.put(k3, v3); m.put(k4, v4); m.put(k5, v5); m.put(k6, v6);
        m.put(k7, v7); m.put(k8, v8); m.put(k9, v9); m.put(k10, v10); m.put(k11, v11);
        m.put(k12, v12); m.put(k13, v13); m.put(k14, v14); m.put(k15, v15); m.put(k16, v16);
        m.put(k17, v17); m.put(k18, v18); m.put(k19, v19);
        return un(m);
    }
    /** Returns an unmodifiable Map containing all passed pairs (including null keys/values). */
    public static <K,V> Map<K,V> unMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5,
                                       K k6, V v6, K k7, V v7, K k8, V v8, K k9, V v9, K k10, V v10,
                                       K k11, V v11, K k12, V v12, K k13, V v13, K k14, V v14,
                                       K k15, V v15, K k16, V v16, K k17, V v17, K k18, V v18) {
        Map<K,V> m = new HashMap<>(20);
        m.put(k1, v1); m.put(k2, v2); m.put(k3, v3); m.put(k4, v4); m.put(k5, v5); m.put(k6, v6);
        m.put(k7, v7); m.put(k8, v8); m.put(k9, v9); m.put(k10, v10); m.put(k11, v11);
        m.put(k12, v12); m.put(k13, v13); m.put(k14, v14); m.put(k15, v15); m.put(k16, v16);
        m.put(k17, v17); m.put(k18, v18);
        return un(m);
    }
    /** Returns an unmodifiable Map containing all passed pairs (including null keys/values). */
    public static <K,V> Map<K,V> unMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5,
                                       K k6, V v6, K k7, V v7, K k8, V v8, K k9, V v9, K k10, V v10,
                                       K k11, V v11, K k12, V v12, K k13, V v13, K k14, V v14,
                                       K k15, V v15, K k16, V v16, K k17, V v17) {
        Map<K,V> m = new HashMap<>(20);
        m.put(k1, v1); m.put(k2, v2); m.put(k3, v3); m.put(k4, v4); m.put(k5, v5); m.put(k6, v6);
        m.put(k7, v7); m.put(k8, v8); m.put(k9, v9); m.put(k10, v10); m.put(k11, v11);
        m.put(k12, v12); m.put(k13, v13); m.put(k14, v14); m.put(k15, v15);
        m.put(k16, v16);
        m.put(k17, v17);
        return un(m);
    }
    /** Returns an unmodifiable Map containing all passed pairs (including null keys/values). */
    public static <K,V> Map<K,V> unMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5,
                                       K k6, V v6, K k7, V v7, K k8, V v8, K k9, V v9, K k10, V v10,
                                       K k11, V v11, K k12, V v12, K k13, V v13, K k14, V v14,
                                       K k15, V v15, K k16, V v16) {
        Map<K,V> m = new HashMap<>(20);
        m.put(k1, v1); m.put(k2, v2); m.put(k3, v3); m.put(k4, v4); m.put(k5, v5); m.put(k6, v6);
        m.put(k7, v7); m.put(k8, v8); m.put(k9, v9); m.put(k10, v10); m.put(k11, v11);
        m.put(k12, v12); m.put(k13, v13); m.put(k14, v14); m.put(k15, v15); m.put(k16, v16);
        return un(m);
    }
    /** Returns an unmodifiable Map containing all passed pairs (including null keys/values). */
    public static <K,V> Map<K,V> unMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5,
                                       K k6, V v6, K k7, V v7, K k8, V v8, K k9, V v9, K k10, V v10,
                                       K k11, V v11, K k12, V v12, K k13, V v13, K k14, V v14,
                                       K k15, V v15) {
        Map<K,V> m = new HashMap<>(20);
        m.put(k1, v1); m.put(k2, v2); m.put(k3, v3); m.put(k4, v4); m.put(k5, v5); m.put(k6, v6);
        m.put(k7, v7); m.put(k8, v8); m.put(k9, v9); m.put(k10, v10); m.put(k11, v11);
        m.put(k12, v12); m.put(k13, v13); m.put(k14, v14); m.put(k15, v15);
        return un(m);
    }
    /** Returns an unmodifiable Map containing all passed pairs (including null keys/values). */
    public static <K,V> Map<K,V> unMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5,
                                       K k6, V v6, K k7, V v7, K k8, V v8, K k9, V v9, K k10, V v10,
                                       K k11, V v11, K k12, V v12, K k13, V v13, K k14, V v14) {
        Map<K,V> m = new HashMap<>(20);
        m.put(k1, v1); m.put(k2, v2); m.put(k3, v3); m.put(k4, v4); m.put(k5, v5); m.put(k6, v6);
        m.put(k7, v7); m.put(k8, v8); m.put(k9, v9); m.put(k10, v10); m.put(k11, v11);
        m.put(k12, v12); m.put(k13, v13); m.put(k14, v14);
        return un(m);
    }
    /** Returns an unmodifiable Map containing all passed pairs (including null keys/values). */
    public static <K,V> Map<K,V> unMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5,
                                       K k6, V v6, K k7, V v7, K k8, V v8, K k9, V v9, K k10, V v10,
                                       K k11, V v11, K k12, V v12, K k13, V v13) {
        Map<K,V> m = new HashMap<>(20);
        m.put(k1, v1); m.put(k2, v2); m.put(k3, v3); m.put(k4, v4); m.put(k5, v5); m.put(k6, v6);
        m.put(k7, v7); m.put(k8, v8); m.put(k9, v9); m.put(k10, v10); m.put(k11, v11);
        m.put(k12, v12); m.put(k13, v13);
        return un(m);
    }
    /** Returns an unmodifiable Map containing all passed pairs (including null keys/values). */
    public static <K,V> Map<K,V> unMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5,
                                       K k6, V v6, K k7, V v7, K k8, V v8, K k9, V v9, K k10, V v10,
                                       K k11, V v11, K k12, V v12) {
        Map<K,V> m = new HashMap<>(20);
        m.put(k1, v1); m.put(k2, v2); m.put(k3, v3); m.put(k4, v4); m.put(k5, v5); m.put(k6, v6);
        m.put(k7, v7); m.put(k8, v8); m.put(k9, v9); m.put(k10, v10);
        m.put(k11, v11);
        m.put(k12, v12);
        return un(m);
    }
    /** Returns an unmodifiable Map containing all passed pairs (including null keys/values). */
    public static <K,V> Map<K,V> unMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5,
                                       K k6, V v6, K k7, V v7, K k8, V v8, K k9, V v9, K k10, V v10,
                                       K k11, V v11) {
        Map<K,V> m = new HashMap<>(20);
        m.put(k1, v1); m.put(k2, v2); m.put(k3, v3); m.put(k4, v4); m.put(k5, v5); m.put(k6, v6);
        m.put(k7, v7); m.put(k8, v8); m.put(k9, v9); m.put(k10, v10); m.put(k11, v11);
        return un(m);
    }
    /** Returns an unmodifiable Map containing all passed pairs (including null keys/values). */
    public static <K,V> Map<K,V> unMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5,
                                       K k6, V v6, K k7, V v7, K k8, V v8, K k9, V v9,
                                       K k10, V v10) {
        Map<K,V> m = new HashMap<>(20);
        m.put(k1, v1); m.put(k2, v2); m.put(k3, v3); m.put(k4, v4); m.put(k5, v5); m.put(k6, v6);
        m.put(k7, v7); m.put(k8, v8); m.put(k9, v9); m.put(k10, v10);
        return un(m);
    }
    /** Returns an unmodifiable Map containing all passed pairs (including null keys/values). */
    public static <K,V> Map<K,V> unMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5,
                                       K k6, V v6, K k7, V v7, K k8, V v8, K k9, V v9) {
        Map<K,V> m = new HashMap<>(9);
        m.put(k1, v1); m.put(k2, v2); m.put(k3, v3); m.put(k4, v4); m.put(k5, v5); m.put(k6, v6);
        m.put(k7, v7); m.put(k8, v8); m.put(k9, v9);
        return un(m);
    }
    /** Returns an unmodifiable Map containing all passed pairs (including null keys/values). */
    public static <K,V> Map<K,V> unMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5,
                                       K k6, V v6, K k7, V v7, K k8, V v8) {
        Map<K,V> m = new HashMap<>(8);
        m.put(k1, v1); m.put(k2, v2); m.put(k3, v3); m.put(k4, v4); m.put(k5, v5); m.put(k6, v6);
        m.put(k7, v7); m.put(k8, v8);
        return un(m);
    }
    /** Returns an unmodifiable Map containing all passed pairs (including null keys/values). */
    public static <K,V> Map<K,V> unMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5,
                                       K k6, V v6, K k7, V v7) {
        Map<K,V> m = new HashMap<>(7);
        m.put(k1, v1); m.put(k2, v2); m.put(k3, v3); m.put(k4, v4); m.put(k5, v5); m.put(k6, v6);
        m.put(k7, v7);
        return un(m);
    }
    /** Returns an unmodifiable Map containing all passed pairs (including null keys/values). */
    public static <K,V> Map<K,V> unMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5,
                                       K k6, V v6) {
        Map<K,V> m = new HashMap<>(6);
        m.put(k1, v1); m.put(k2, v2); m.put(k3, v3); m.put(k4, v4); m.put(k5, v5); m.put(k6, v6);
        return un(m);
    }
    /** Returns an unmodifiable Map containing all passed pairs (including null keys/values). */
    public static <K,V> Map<K,V> unMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5) {
        Map<K,V> m = new HashMap<>(5);
        m.put(k1, v1); m.put(k2, v2); m.put(k3, v3); m.put(k4, v4); m.put(k5, v5);
        return un(m);
    }
    /** Returns an unmodifiable Map containing all passed pairs (including null keys/values). */
    public static <K,V> Map<K,V> unMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
        Map<K,V> m = new HashMap<>(4); m.put(k1, v1); m.put(k2, v2); m.put(k3, v3); m.put(k4, v4);
        return un(m);
    }
    /** Returns an unmodifiable Map containing all passed pairs (including null keys/values). */
    public static <K,V> Map<K,V> unMap(K k1, V v1, K k2, V v2, K k3, V v3) {
        Map<K,V> m = new HashMap<>(3); m.put(k1, v1); m.put(k2, v2); m.put(k3, v3);
        return un(m);
    }
    /** Returns an unmodifiable Map containing all passed pairs (including null keys/values). */
    public static <K,V> Map<K,V> unMap(K k1, V v1, K k2, V v2) {
        Map<K,V> m = new HashMap<>(2); m.put(k1, v1); m.put(k2, v2);
        return un(m);
    }
    /** Returns an unmodifiable Map containing all passed pairs (including null keys/values). */
    public static <K,V> Map<K,V> unMap(K k1, V v1) {
        Map<K,V> m = new HashMap<>(1); m.put(k1, v1); return un(m);
    }

    /** Returns an unmodifiable Map containing any non-null passed items. */
    public static <K,V> Map<K,V> unMapSkipNull(UnEntry<K,V> t1, UnEntry<K,V> t2, UnEntry<K,V> t3, UnEntry<K,V> t4,
                                               UnEntry<K,V> t5, UnEntry<K,V> t6, UnEntry<K,V> t7, UnEntry<K,V> t8,
                                               UnEntry<K,V> t9, UnEntry<K,V> t10, UnEntry<K,V> t11, UnEntry<K,V> t12,
                                               UnEntry<K,V> t13, UnEntry<K,V> t14, UnEntry<K,V> t15, UnEntry<K,V> t16,
                                               UnEntry<K,V> t17, UnEntry<K,V> t18, UnEntry<K,V> t19, UnEntry<K,V> t20) {
        Map<K,V> m = new HashMap<>();
        if (t1 != null) { m.put(t1.getKey(), t1.getValue()); }
        if (t2 != null) { m.put(t2.getKey(), t2.getValue()); }
        if (t3 != null) { m.put(t3.getKey(), t3.getValue()); }
        if (t4 != null) { m.put(t4.getKey(), t4.getValue()); }
        if (t5 != null) { m.put(t5.getKey(), t5.getValue()); }
        if (t6 != null) { m.put(t6.getKey(), t6.getValue()); }
        if (t7 != null) { m.put(t7.getKey(), t7.getValue()); }
        if (t8 != null) { m.put(t8.getKey(), t8.getValue()); }
        if (t9 != null) { m.put(t9.getKey(), t9.getValue()); }
        if (t10 != null) { m.put(t10.getKey(), t10.getValue()); }
        if (t11 != null) { m.put(t11.getKey(), t11.getValue()); }
        if (t12 != null) { m.put(t12.getKey(), t12.getValue()); }
        if (t13 != null) { m.put(t13.getKey(), t13.getValue()); }
        if (t14 != null) { m.put(t14.getKey(), t14.getValue()); }
        if (t15 != null) { m.put(t15.getKey(), t15.getValue()); }
        if (t16 != null) { m.put(t16.getKey(), t16.getValue()); }
        if (t17 != null) { m.put(t17.getKey(), t17.getValue()); }
        if (t18 != null) { m.put(t18.getKey(), t18.getValue()); }
        if (t19 != null) { m.put(t19.getKey(), t19.getValue()); }
        if (t20 != null) { m.put(t20.getKey(), t20.getValue()); }
        return un(m);
    }
    /** Returns an unmodifiable Map containing any non-null passed items. */
    public static <K,V> Map<K,V> unMapSkipNull(UnEntry<K,V> t1, UnEntry<K,V> t2, UnEntry<K,V> t3, UnEntry<K,V> t4,
                                               UnEntry<K,V> t5, UnEntry<K,V> t6, UnEntry<K,V> t7, UnEntry<K,V> t8,
                                               UnEntry<K,V> t9, UnEntry<K,V> t10, UnEntry<K,V> t11, UnEntry<K,V> t12,
                                               UnEntry<K,V> t13, UnEntry<K,V> t14, UnEntry<K,V> t15, UnEntry<K,V> t16,
                                               UnEntry<K,V> t17, UnEntry<K,V> t18, UnEntry<K,V> t19) {
        Map<K,V> m = new HashMap<>();
        if (t1 != null) { m.put(t1.getKey(), t1.getValue()); }
        if (t2 != null) { m.put(t2.getKey(), t2.getValue()); }
        if (t3 != null) { m.put(t3.getKey(), t3.getValue()); }
        if (t4 != null) { m.put(t4.getKey(), t4.getValue()); }
        if (t5 != null) { m.put(t5.getKey(), t5.getValue()); }
        if (t6 != null) { m.put(t6.getKey(), t6.getValue()); }
        if (t7 != null) { m.put(t7.getKey(), t7.getValue()); }
        if (t8 != null) { m.put(t8.getKey(), t8.getValue()); }
        if (t9 != null) { m.put(t9.getKey(), t9.getValue()); }
        if (t10 != null) { m.put(t10.getKey(), t10.getValue()); }
        if (t11 != null) { m.put(t11.getKey(), t11.getValue()); }
        if (t12 != null) { m.put(t12.getKey(), t12.getValue()); }
        if (t13 != null) { m.put(t13.getKey(), t13.getValue()); }
        if (t14 != null) { m.put(t14.getKey(), t14.getValue()); }
        if (t15 != null) { m.put(t15.getKey(), t15.getValue()); }
        if (t16 != null) { m.put(t16.getKey(), t16.getValue()); }
        if (t17 != null) { m.put(t17.getKey(), t17.getValue()); }
        if (t18 != null) { m.put(t18.getKey(), t18.getValue()); }
        if (t19 != null) { m.put(t19.getKey(), t19.getValue()); }
        return un(m);
    }
    /** Returns an unmodifiable Map containing any non-null passed items. */
    public static <K,V> Map<K,V> unMapSkipNull(UnEntry<K,V> t1, UnEntry<K,V> t2, UnEntry<K,V> t3, UnEntry<K,V> t4,
                                               UnEntry<K,V> t5, UnEntry<K,V> t6, UnEntry<K,V> t7, UnEntry<K,V> t8,
                                               UnEntry<K,V> t9, UnEntry<K,V> t10, UnEntry<K,V> t11, UnEntry<K,V> t12,
                                               UnEntry<K,V> t13, UnEntry<K,V> t14, UnEntry<K,V> t15, UnEntry<K,V> t16,
                                               UnEntry<K,V> t17, UnEntry<K,V> t18) {
        Map<K,V> m = new HashMap<>();
        if (t1 != null) { m.put(t1.getKey(), t1.getValue()); }
        if (t2 != null) { m.put(t2.getKey(), t2.getValue()); }
        if (t3 != null) { m.put(t3.getKey(), t3.getValue()); }
        if (t4 != null) { m.put(t4.getKey(), t4.getValue()); }
        if (t5 != null) { m.put(t5.getKey(), t5.getValue()); }
        if (t6 != null) { m.put(t6.getKey(), t6.getValue()); }
        if (t7 != null) { m.put(t7.getKey(), t7.getValue()); }
        if (t8 != null) { m.put(t8.getKey(), t8.getValue()); }
        if (t9 != null) { m.put(t9.getKey(), t9.getValue()); }
        if (t10 != null) { m.put(t10.getKey(), t10.getValue()); }
        if (t11 != null) { m.put(t11.getKey(), t11.getValue()); }
        if (t12 != null) { m.put(t12.getKey(), t12.getValue()); }
        if (t13 != null) { m.put(t13.getKey(), t13.getValue()); }
        if (t14 != null) { m.put(t14.getKey(), t14.getValue()); }
        if (t15 != null) { m.put(t15.getKey(), t15.getValue()); }
        if (t16 != null) { m.put(t16.getKey(), t16.getValue()); }
        if (t17 != null) { m.put(t17.getKey(), t17.getValue()); }
        if (t18 != null) { m.put(t18.getKey(), t18.getValue()); }
        return un(m);
    }
    /** Returns an unmodifiable Map containing any non-null passed items. */
    public static <K,V> Map<K,V> unMapSkipNull(UnEntry<K,V> t1, UnEntry<K,V> t2, UnEntry<K,V> t3, UnEntry<K,V> t4,
                                               UnEntry<K,V> t5, UnEntry<K,V> t6, UnEntry<K,V> t7, UnEntry<K,V> t8,
                                               UnEntry<K,V> t9, UnEntry<K,V> t10, UnEntry<K,V> t11, UnEntry<K,V> t12,
                                               UnEntry<K,V> t13, UnEntry<K,V> t14, UnEntry<K,V> t15, UnEntry<K,V> t16,
                                               UnEntry<K,V> t17) {
        Map<K,V> m = new HashMap<>();
        if (t1 != null) { m.put(t1.getKey(), t1.getValue()); }
        if (t2 != null) { m.put(t2.getKey(), t2.getValue()); }
        if (t3 != null) { m.put(t3.getKey(), t3.getValue()); }
        if (t4 != null) { m.put(t4.getKey(), t4.getValue()); }
        if (t5 != null) { m.put(t5.getKey(), t5.getValue()); }
        if (t6 != null) { m.put(t6.getKey(), t6.getValue()); }
        if (t7 != null) { m.put(t7.getKey(), t7.getValue()); }
        if (t8 != null) { m.put(t8.getKey(), t8.getValue()); }
        if (t9 != null) { m.put(t9.getKey(), t9.getValue()); }
        if (t10 != null) { m.put(t10.getKey(), t10.getValue()); }
        if (t11 != null) { m.put(t11.getKey(), t11.getValue()); }
        if (t12 != null) { m.put(t12.getKey(), t12.getValue()); }
        if (t13 != null) { m.put(t13.getKey(), t13.getValue()); }
        if (t14 != null) { m.put(t14.getKey(), t14.getValue()); }
        if (t15 != null) { m.put(t15.getKey(), t15.getValue()); }
        if (t16 != null) { m.put(t16.getKey(), t16.getValue()); }
        if (t17 != null) { m.put(t17.getKey(), t17.getValue()); }
        return un(m);
    }
    /** Returns an unmodifiable Map containing any non-null passed items. */
    public static <K,V> Map<K,V> unMapSkipNull(UnEntry<K,V> t1, UnEntry<K,V> t2, UnEntry<K,V> t3, UnEntry<K,V> t4,
                                               UnEntry<K,V> t5, UnEntry<K,V> t6, UnEntry<K,V> t7, UnEntry<K,V> t8,
                                               UnEntry<K,V> t9, UnEntry<K,V> t10, UnEntry<K,V> t11, UnEntry<K,V> t12,
                                               UnEntry<K,V> t13, UnEntry<K,V> t14, UnEntry<K,V> t15, UnEntry<K,V> t16) {
        Map<K,V> m = new HashMap<>();
        if (t1 != null) { m.put(t1.getKey(), t1.getValue()); }
        if (t2 != null) { m.put(t2.getKey(), t2.getValue()); }
        if (t3 != null) { m.put(t3.getKey(), t3.getValue()); }
        if (t4 != null) { m.put(t4.getKey(), t4.getValue()); }
        if (t5 != null) { m.put(t5.getKey(), t5.getValue()); }
        if (t6 != null) { m.put(t6.getKey(), t6.getValue()); }
        if (t7 != null) { m.put(t7.getKey(), t7.getValue()); }
        if (t8 != null) { m.put(t8.getKey(), t8.getValue()); }
        if (t9 != null) { m.put(t9.getKey(), t9.getValue()); }
        if (t10 != null) { m.put(t10.getKey(), t10.getValue()); }
        if (t11 != null) { m.put(t11.getKey(), t11.getValue()); }
        if (t12 != null) { m.put(t12.getKey(), t12.getValue()); }
        if (t13 != null) { m.put(t13.getKey(), t13.getValue()); }
        if (t14 != null) { m.put(t14.getKey(), t14.getValue()); }
        if (t15 != null) { m.put(t15.getKey(), t15.getValue()); }
        if (t16 != null) { m.put(t16.getKey(), t16.getValue()); }
        return un(m);
    }
    /** Returns an unmodifiable Map containing any non-null passed items. */
    public static <K,V> Map<K,V> unMapSkipNull(UnEntry<K,V> t1, UnEntry<K,V> t2, UnEntry<K,V> t3, UnEntry<K,V> t4,
                                               UnEntry<K,V> t5, UnEntry<K,V> t6, UnEntry<K,V> t7, UnEntry<K,V> t8,
                                               UnEntry<K,V> t9, UnEntry<K,V> t10, UnEntry<K,V> t11, UnEntry<K,V> t12,
                                               UnEntry<K,V> t13, UnEntry<K,V> t14, UnEntry<K,V> t15) {
        Map<K,V> m = new HashMap<>();
        if (t1 != null) { m.put(t1.getKey(), t1.getValue()); }
        if (t2 != null) { m.put(t2.getKey(), t2.getValue()); }
        if (t3 != null) { m.put(t3.getKey(), t3.getValue()); }
        if (t4 != null) { m.put(t4.getKey(), t4.getValue()); }
        if (t5 != null) { m.put(t5.getKey(), t5.getValue()); }
        if (t6 != null) { m.put(t6.getKey(), t6.getValue()); }
        if (t7 != null) { m.put(t7.getKey(), t7.getValue()); }
        if (t8 != null) { m.put(t8.getKey(), t8.getValue()); }
        if (t9 != null) { m.put(t9.getKey(), t9.getValue()); }
        if (t10 != null) { m.put(t10.getKey(), t10.getValue()); }
        if (t11 != null) { m.put(t11.getKey(), t11.getValue()); }
        if (t12 != null) { m.put(t12.getKey(), t12.getValue()); }
        if (t13 != null) { m.put(t13.getKey(), t13.getValue()); }
        if (t14 != null) { m.put(t14.getKey(), t14.getValue()); }
        if (t15 != null) { m.put(t15.getKey(), t15.getValue()); }
        return un(m);
    }
    /** Returns an unmodifiable Map containing any non-null passed items. */
    public static <K,V> Map<K,V> unMapSkipNull(UnEntry<K,V> t1, UnEntry<K,V> t2, UnEntry<K,V> t3, UnEntry<K,V> t4,
                                               UnEntry<K,V> t5, UnEntry<K,V> t6, UnEntry<K,V> t7, UnEntry<K,V> t8,
                                               UnEntry<K,V> t9, UnEntry<K,V> t10, UnEntry<K,V> t11, UnEntry<K,V> t12,
                                               UnEntry<K,V> t13, UnEntry<K,V> t14) {
        Map<K,V> m = new HashMap<>();
        if (t1 != null) { m.put(t1.getKey(), t1.getValue()); }
        if (t2 != null) { m.put(t2.getKey(), t2.getValue()); }
        if (t3 != null) { m.put(t3.getKey(), t3.getValue()); }
        if (t4 != null) { m.put(t4.getKey(), t4.getValue()); }
        if (t5 != null) { m.put(t5.getKey(), t5.getValue()); }
        if (t6 != null) { m.put(t6.getKey(), t6.getValue()); }
        if (t7 != null) { m.put(t7.getKey(), t7.getValue()); }
        if (t8 != null) { m.put(t8.getKey(), t8.getValue()); }
        if (t9 != null) { m.put(t9.getKey(), t9.getValue()); }
        if (t10 != null) { m.put(t10.getKey(), t10.getValue()); }
        if (t11 != null) { m.put(t11.getKey(), t11.getValue()); }
        if (t12 != null) { m.put(t12.getKey(), t12.getValue()); }
        if (t13 != null) { m.put(t13.getKey(), t13.getValue()); }
        if (t14 != null) { m.put(t14.getKey(), t14.getValue()); }
        return un(m);
    }
    /** Returns an unmodifiable Map containing any non-null passed items. */
    public static <K,V> Map<K,V> unMapSkipNull(UnEntry<K,V> t1, UnEntry<K,V> t2, UnEntry<K,V> t3, UnEntry<K,V> t4,
                                               UnEntry<K,V> t5, UnEntry<K,V> t6, UnEntry<K,V> t7, UnEntry<K,V> t8,
                                               UnEntry<K,V> t9, UnEntry<K,V> t10, UnEntry<K,V> t11, UnEntry<K,V> t12,
                                               UnEntry<K,V> t13) {
        Map<K,V> m = new HashMap<>();
        if (t1 != null) { m.put(t1.getKey(), t1.getValue()); }
        if (t2 != null) { m.put(t2.getKey(), t2.getValue()); }
        if (t3 != null) { m.put(t3.getKey(), t3.getValue()); }
        if (t4 != null) { m.put(t4.getKey(), t4.getValue()); }
        if (t5 != null) { m.put(t5.getKey(), t5.getValue()); }
        if (t6 != null) { m.put(t6.getKey(), t6.getValue()); }
        if (t7 != null) { m.put(t7.getKey(), t7.getValue()); }
        if (t8 != null) { m.put(t8.getKey(), t8.getValue()); }
        if (t9 != null) { m.put(t9.getKey(), t9.getValue()); }
        if (t10 != null) { m.put(t10.getKey(), t10.getValue()); }
        if (t11 != null) { m.put(t11.getKey(), t11.getValue()); }
        if (t12 != null) { m.put(t12.getKey(), t12.getValue()); }
        if (t13 != null) { m.put(t13.getKey(), t13.getValue()); }
        return un(m);
    }
    /** Returns an unmodifiable Map containing any non-null passed items. */
    public static <K,V> Map<K,V> unMapSkipNull(UnEntry<K,V> t1, UnEntry<K,V> t2, UnEntry<K,V> t3, UnEntry<K,V> t4,
                                               UnEntry<K,V> t5, UnEntry<K,V> t6, UnEntry<K,V> t7, UnEntry<K,V> t8,
                                               UnEntry<K,V> t9, UnEntry<K,V> t10, UnEntry<K,V> t11, UnEntry<K,V> t12) {
        Map<K,V> m = new HashMap<>();
        if (t1 != null) { m.put(t1.getKey(), t1.getValue()); }
        if (t2 != null) { m.put(t2.getKey(), t2.getValue()); }
        if (t3 != null) { m.put(t3.getKey(), t3.getValue()); }
        if (t4 != null) { m.put(t4.getKey(), t4.getValue()); }
        if (t5 != null) { m.put(t5.getKey(), t5.getValue()); }
        if (t6 != null) { m.put(t6.getKey(), t6.getValue()); }
        if (t7 != null) { m.put(t7.getKey(), t7.getValue()); }
        if (t8 != null) { m.put(t8.getKey(), t8.getValue()); }
        if (t9 != null) { m.put(t9.getKey(), t9.getValue()); }
        if (t10 != null) { m.put(t10.getKey(), t10.getValue()); }
        if (t11 != null) { m.put(t11.getKey(), t11.getValue()); }
        if (t12 != null) { m.put(t12.getKey(), t12.getValue()); }
        return un(m);
    }
    /** Returns an unmodifiable Map containing any non-null passed items. */
    public static <K,V> Map<K,V> unMapSkipNull(UnEntry<K,V> t1, UnEntry<K,V> t2, UnEntry<K,V> t3, UnEntry<K,V> t4,
                                               UnEntry<K,V> t5, UnEntry<K,V> t6, UnEntry<K,V> t7, UnEntry<K,V> t8,
                                               UnEntry<K,V> t9, UnEntry<K,V> t10, UnEntry<K,V> t11) {
        Map<K,V> m = new HashMap<>();
        if (t1 != null) { m.put(t1.getKey(), t1.getValue()); }
        if (t2 != null) { m.put(t2.getKey(), t2.getValue()); }
        if (t3 != null) { m.put(t3.getKey(), t3.getValue()); }
        if (t4 != null) { m.put(t4.getKey(), t4.getValue()); }
        if (t5 != null) { m.put(t5.getKey(), t5.getValue()); }
        if (t6 != null) { m.put(t6.getKey(), t6.getValue()); }
        if (t7 != null) { m.put(t7.getKey(), t7.getValue()); }
        if (t8 != null) { m.put(t8.getKey(), t8.getValue()); }
        if (t9 != null) { m.put(t9.getKey(), t9.getValue()); }
        if (t10 != null) { m.put(t10.getKey(), t10.getValue()); }
        if (t11 != null) { m.put(t11.getKey(), t11.getValue()); }
        return un(m);
    }
    /** Returns an unmodifiable Map containing any non-null passed items. */
    public static <K,V> Map<K,V> unMapSkipNull(UnEntry<K,V> t1, UnEntry<K,V> t2, UnEntry<K,V> t3, UnEntry<K,V> t4,
                                               UnEntry<K,V> t5, UnEntry<K,V> t6, UnEntry<K,V> t7, UnEntry<K,V> t8,
                                               UnEntry<K,V> t9, UnEntry<K,V> t10) {
        Map<K,V> m = new HashMap<>();
        if (t1 != null) { m.put(t1.getKey(), t1.getValue()); }
        if (t2 != null) { m.put(t2.getKey(), t2.getValue()); }
        if (t3 != null) { m.put(t3.getKey(), t3.getValue()); }
        if (t4 != null) { m.put(t4.getKey(), t4.getValue()); }
        if (t5 != null) { m.put(t5.getKey(), t5.getValue()); }
        if (t6 != null) { m.put(t6.getKey(), t6.getValue()); }
        if (t7 != null) { m.put(t7.getKey(), t7.getValue()); }
        if (t8 != null) { m.put(t8.getKey(), t8.getValue()); }
        if (t9 != null) { m.put(t9.getKey(), t9.getValue()); }
        if (t10 != null) { m.put(t10.getKey(), t10.getValue()); }
        return un(m);
    }
    /** Returns an unmodifiable Map containing any non-null passed items. */
    public static <K,V> Map<K,V> unMapSkipNull(UnEntry<K,V> t1, UnEntry<K,V> t2, UnEntry<K,V> t3, UnEntry<K,V> t4,
                                               UnEntry<K,V> t5, UnEntry<K,V> t6, UnEntry<K,V> t7, UnEntry<K,V> t8,
                                               UnEntry<K,V> t9) {
        Map<K,V> m = new HashMap<>();
        if (t1 != null) { m.put(t1.getKey(), t1.getValue()); }
        if (t2 != null) { m.put(t2.getKey(), t2.getValue()); }
        if (t3 != null) { m.put(t3.getKey(), t3.getValue()); }
        if (t4 != null) { m.put(t4.getKey(), t4.getValue()); }
        if (t5 != null) { m.put(t5.getKey(), t5.getValue()); }
        if (t6 != null) { m.put(t6.getKey(), t6.getValue()); }
        if (t7 != null) { m.put(t7.getKey(), t7.getValue()); }
        if (t8 != null) { m.put(t8.getKey(), t8.getValue()); }
        if (t9 != null) { m.put(t9.getKey(), t9.getValue()); }
        return un(m);
    }
    /** Returns an unmodifiable Map containing any non-null passed items. */
    public static <K,V> Map<K,V> unMapSkipNull(UnEntry<K,V> t1, UnEntry<K,V> t2, UnEntry<K,V> t3, UnEntry<K,V> t4,
                                               UnEntry<K,V> t5, UnEntry<K,V> t6, UnEntry<K,V> t7, UnEntry<K,V> t8) {
        Map<K,V> m = new HashMap<>();
        if (t1 != null) { m.put(t1.getKey(), t1.getValue()); }
        if (t2 != null) { m.put(t2.getKey(), t2.getValue()); }
        if (t3 != null) { m.put(t3.getKey(), t3.getValue()); }
        if (t4 != null) { m.put(t4.getKey(), t4.getValue()); }
        if (t5 != null) { m.put(t5.getKey(), t5.getValue()); }
        if (t6 != null) { m.put(t6.getKey(), t6.getValue()); }
        if (t7 != null) { m.put(t7.getKey(), t7.getValue()); }
        if (t8 != null) { m.put(t8.getKey(), t8.getValue()); }
        return un(m);
    }
    /** Returns an unmodifiable Map containing any non-null passed items. */
    public static <K,V> Map<K,V> unMapSkipNull(UnEntry<K,V> t1, UnEntry<K,V> t2, UnEntry<K,V> t3, UnEntry<K,V> t4,
                                               UnEntry<K,V> t5, UnEntry<K,V> t6, UnEntry<K,V> t7) {
        Map<K,V> m = new HashMap<>();
        if (t1 != null) { m.put(t1.getKey(), t1.getValue()); }
        if (t2 != null) { m.put(t2.getKey(), t2.getValue()); }
        if (t3 != null) { m.put(t3.getKey(), t3.getValue()); }
        if (t4 != null) { m.put(t4.getKey(), t4.getValue()); }
        if (t5 != null) { m.put(t5.getKey(), t5.getValue()); }
        if (t6 != null) { m.put(t6.getKey(), t6.getValue()); }
        if (t7 != null) { m.put(t7.getKey(), t7.getValue()); }
        return un(m);
    }
    /** Returns an unmodifiable Map containing any non-null passed items. */
    public static <K,V> Map<K,V> unMapSkipNull(UnEntry<K,V> t1, UnEntry<K,V> t2, UnEntry<K,V> t3, UnEntry<K,V> t4,
                                               UnEntry<K,V> t5, UnEntry<K,V> t6) {
        Map<K,V> m = new HashMap<>();
        if (t1 != null) { m.put(t1.getKey(), t1.getValue()); }
        if (t2 != null) { m.put(t2.getKey(), t2.getValue()); }
        if (t3 != null) { m.put(t3.getKey(), t3.getValue()); }
        if (t4 != null) { m.put(t4.getKey(), t4.getValue()); }
        if (t5 != null) { m.put(t5.getKey(), t5.getValue()); }
        if (t6 != null) { m.put(t6.getKey(), t6.getValue()); }
        return un(m);
    }
    /** Returns an unmodifiable Map containing any non-null passed items. */
    public static <K,V> Map<K,V> unMapSkipNull(UnEntry<K,V> t1, UnEntry<K,V> t2, UnEntry<K,V> t3, UnEntry<K,V> t4,
                                              UnEntry<K,V> t5) {
        Map<K,V> m = new HashMap<>();
        if (t1 != null) { m.put(t1.getKey(), t1.getValue()); }
        if (t2 != null) { m.put(t2.getKey(), t2.getValue()); }
        if (t3 != null) { m.put(t3.getKey(), t3.getValue()); }
        if (t4 != null) { m.put(t4.getKey(), t4.getValue()); }
        if (t5 != null) { m.put(t5.getKey(), t5.getValue()); }
        return un(m);
    }
    /** Returns an unmodifiable Map containing any non-null passed items. */
    public static <K,V> Map<K,V> unMapSkipNull(UnEntry<K,V> t1, UnEntry<K,V> t2, UnEntry<K,V> t3, UnEntry<K,V> t4) {
        Map<K,V> m = new HashMap<>();
        if (t1 != null) { m.put(t1.getKey(), t1.getValue()); }
        if (t2 != null) { m.put(t2.getKey(), t2.getValue()); }
        if (t3 != null) { m.put(t3.getKey(), t3.getValue()); }
        if (t4 != null) { m.put(t4.getKey(), t4.getValue()); }
        return un(m);
    }
    /** Returns an unmodifiable Map containing any non-null passed items. */
    public static <K,V> Map<K,V> unMapSkipNull(UnEntry<K,V> t1, UnEntry<K,V> t2, UnEntry<K,V> t3) {
        Map<K,V> m = new HashMap<>();
        if (t1 != null) { m.put(t1.getKey(), t1.getValue()); }
        if (t2 != null) { m.put(t2.getKey(), t2.getValue()); }
        if (t3 != null) { m.put(t3.getKey(), t3.getValue()); }
        return un(m);
    }
    /** Returns an unmodifiable Map containing any non-null passed items. */
    public static <K,V> Map<K,V> unMapSkipNull(UnEntry<K,V> t1, UnEntry<K,V> t2) {
        Map<K,V> m = new HashMap<>();
        if (t1 != null) { m.put(t1.getKey(), t1.getValue()); }
        if (t2 != null) { m.put(t2.getKey(), t2.getValue()); }
        return un(m);
    }
    /** Returns an unmodifiable Map containing any non-null passed items. */
    public static <K,V> Map<K,V> unMapSkipNull(UnEntry<K,V> t1) {
        Map<K,V> m = new HashMap<>();
        if (t1 != null) { m.put(t1.getKey(), t1.getValue()); }
        return un(m);
    }

    /** Returns an unmodifiable Set containing all passed items (including null items). */
    @SuppressWarnings("unchecked")
    @SafeVarargs
    public static <T> UnSet<T> unSet(T... ts) {
        return ( (ts == null) || (ts.length < 1) )
                ? UnSet.empty()
                : un(new HashSet<>(Arrays.asList(ts)));
    }

    /** Returns an unmodifiable Set containing any non-null passed items. */
    @SuppressWarnings("unchecked")
    @SafeVarargs
    public static <T> UnSet<T> unSetSkipNull(T... ts) {
        if ( (ts == null) || (ts.length < 1) ) {
            return UnSet.empty();
        }
        Set<T> s = new HashSet<>();
        for (T t : ts) {
            if (t != null) {
                s.add(t);
            }
        }
        return (s.size() > 0) ? un(s) : UnSet.empty();
    }

    /** Returns an unmodifiable List containing all passed items (including null items). */
    @SuppressWarnings("unchecked")
    @SafeVarargs
    public static <T> UnList<T> unList(T... ts) {
        return ( (ts == null) || (ts.length < 1) )
                ? UnList.empty()
                : un(Arrays.asList(ts));
    }

    /** Returns an unmodifiable List containing any non-null passed items. */
    @SuppressWarnings("unchecked")
    @SafeVarargs
    public static <T> UnList<T> unListSkipNull(T... ts) {
        if ( (ts == null) || (ts.length < 1) ) {
            return UnList.empty();
        }
        List<T> s = new ArrayList<>();
        for (T t : ts) {
            if (t != null) {
                s.add(t);
            }
        }
        return (s.size() > 0) ? un(s) : UnList.empty();
    }

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
