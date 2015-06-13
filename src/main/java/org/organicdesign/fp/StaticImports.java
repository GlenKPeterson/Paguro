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

import org.organicdesign.fp.collections.ImList;
import org.organicdesign.fp.collections.ImMap;
import org.organicdesign.fp.collections.ImSet;
import org.organicdesign.fp.collections.ImSortedMap;
import org.organicdesign.fp.collections.ImSortedSet;
import org.organicdesign.fp.collections.PersistentHashMap;
import org.organicdesign.fp.collections.PersistentHashSet;
import org.organicdesign.fp.collections.PersistentTreeMap;
import org.organicdesign.fp.collections.PersistentTreeSet;
import org.organicdesign.fp.collections.PersistentVector;
import org.organicdesign.fp.collections.UnmodCollection;
import org.organicdesign.fp.collections.UnmodIterable;
import org.organicdesign.fp.collections.UnmodIterator;
import org.organicdesign.fp.collections.UnmodList;
import org.organicdesign.fp.collections.UnmodListIterator;
import org.organicdesign.fp.collections.UnmodMap;
import org.organicdesign.fp.collections.UnmodSet;
import org.organicdesign.fp.collections.UnmodSortedIterator;
import org.organicdesign.fp.collections.UnmodSortedMap;
import org.organicdesign.fp.collections.UnmodSortedSet;
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
 Statically importing the functions in this file is like building a mini language in Java.  Whether that is a good or
 bad idea remains to be seen.  As with any usage of import *, there will probably be issues if you import 2 different
 versions of this file in your classpath.

 Contains methods for building immutable collections.  These will never return null, the closest they get is to return
 an empty immutable collection (the same one is reused).  The skipNull versions aid immutable programming since
 you can build a map of unknown size as follows:
 <pre><code>imMapSkipNull(Tuple2.of("hello", 33),
              Tuple2.of("there", 44),
              currUser.isFred ? Tuple2.of("Fred", 55) : null);</code></pre>
 */
@SuppressWarnings("UnusedDeclaration")
public class StaticImports {
    // Prevent instantiation
    private StaticImports() { throw new UnsupportedOperationException("No instantiation"); }

    /** Returns a new PersistentVector of the given items. */
    @SafeVarargs
    static public <T> ImList<T> imList(T... items) { return PersistentVector.of(items); }

    /** Returns a new PersistentVector of the given items, omitting any nulls. */
    @SafeVarargs
    public static <T> ImList<T> imListSkipNull(T... items) { return PersistentVector.ofSkipNull(items); }

    /** Returns a new PersistentHashMap of the given keys and their paired values. */
    public static <K,V> ImMap<K,V> imMap(K k1, V v1) {
        return PersistentHashMap.of(k1, v1);
    }

    /** Returns a new PersistentHashMap of the given keys and their paired values. */
    public static <K,V>
    ImMap<K,V> imMap(K k1, V v1, K k2, V v2) {
        return PersistentHashMap.of(k1, v1, k2, v2);
    }

    /** Returns a new PersistentHashMap of the given keys and their paired values. */
    public static <K,V>
    ImMap<K,V> imMap(K k1, V v1, K k2, V v2, K k3, V v3) {
        return PersistentHashMap.of(k1, v1, k2, v2, k3, v3);
    }

    /** Returns a new PersistentHashMap of the given keys and their paired values. */
    public static <K,V>
    ImMap<K,V> imMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
        return PersistentHashMap.of(k1, v1, k2, v2, k3, v3, k4, v4);
    }

    /** Returns a new PersistentHashMap of the given keys and their paired values. */
    public static <K,V>
    ImMap<K,V> imMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5) {
        return PersistentHashMap.of(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5);
    }

    /** Returns a new PersistentHashMap of the given keys and their paired values. */
    public static <K,V>
    ImMap<K,V> imMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6) {
        return PersistentHashMap.of(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6);
    }

    /** Returns a new PersistentHashMap of the given keys and their paired values. */
    public static <K,V>
    ImMap<K,V> imMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7) {
        return PersistentHashMap.of(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6, k7, v7);
    }

    /** Returns a new PersistentHashMap of the given keys and their paired values. */
    public static <K,V>
    ImMap<K,V> imMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7,
                              K k8, V v8) {
        return PersistentHashMap.of(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6, k7, v7, k8, v8);
    }

    /** Returns a new PersistentHashMap of the given keys and their paired values. */
    public static <K,V>
    ImMap<K,V> imMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7,
                              K k8, V v8, K k9, V v9) {
        return PersistentHashMap.of(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5,
                                    k6, v6, k7, v7, k8, v8, k9, v9);
    }

    /** Returns a new PersistentHashMap of the given keys and their paired values. */
    public static <K,V>
    ImMap<K,V> imMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7,
                              K k8, V v8, K k9, V v9, K k10, V v10) {
        return PersistentHashMap.of(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5,
                                    k6, v6, k7, v7, k8, v8, k9, v9, k10, v10);
    }

    /**
     Returns a new PersistentHashMap of the given keys and their paired values, skipping any null Entries.
     */
    @SafeVarargs
    public static <K,V> ImMap<K,V> imMapSkipNull(Map.Entry<K,V>... es) {
        return PersistentHashMap.ofSkipNull(es);
    }

    /** Returns a new PersistentHashSet of the given items. */
    @SafeVarargs
    public static <T> ImSet<T> imSet(T... items) {
        return PersistentHashSet.of(items);
    }

    /** Returns a new PersistentHashSet of the given items. */
    @SafeVarargs
    public static <T> ImSet<T> imSetSkipNull(T... items) {
        return PersistentHashSet.ofSkipNull(items);
    }

    /** Returns a new PersistentTreeMap of the given comparable keys and their paired values. */
    public static <K extends Comparable<K>,V> ImSortedMap<K,V> imSortedMap(K k1, V v1) {
        return PersistentTreeMap.of(k1, v1);
    }

    /** Returns a new PersistentTreeMap of the given comparable keys and their paired values. */
    public static <K extends Comparable<K>,V>
    ImSortedMap<K,V> imSortedMap(K k1, V v1, K k2, V v2) {
        return PersistentTreeMap.of(k1, v1, k2, v2);
    }

    /** Returns a new PersistentTreeMap of the given comparable keys and their paired values. */
    public static <K extends Comparable<K>,V>
    ImSortedMap<K,V> imSortedMap(K k1, V v1, K k2, V v2, K k3, V v3) {
        return PersistentTreeMap.of(k1, v1, k2, v2, k3, v3);
    }

    /** Returns a new PersistentTreeMap of the given comparable keys and their paired values. */
    public static <K extends Comparable<K>,V>
    ImSortedMap<K,V> imSortedMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
        return PersistentTreeMap.of(k1, v1, k2, v2, k3, v3, k4, v4);
    }

    /** Returns a new PersistentTreeMap of the given comparable keys and their paired values. */
    public static <K extends Comparable<K>,V>
    ImSortedMap<K,V> imSortedMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5) {
        return PersistentTreeMap.of(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5);
    }

    /** Returns a new PersistentTreeMap of the given comparable keys and their paired values. */
    public static <K extends Comparable<K>,V>
    ImSortedMap<K,V> imSortedMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6) {
        return PersistentTreeMap.of(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6);
    }

    /** Returns a new PersistentTreeMap of the given comparable keys and their paired values. */
    public static <K extends Comparable<K>,V>
    ImSortedMap<K,V> imSortedMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7) {
        return PersistentTreeMap.of(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6, k7, v7);
    }

    /** Returns a new PersistentTreeMap of the given comparable keys and their paired values. */
    public static <K extends Comparable<K>,V>
    ImSortedMap<K,V> imSortedMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7,
                              K k8, V v8) {
        return PersistentTreeMap.of(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6, k7, v7, k8, v8);
    }

    /** Returns a new PersistentTreeMap of the given comparable keys and their paired values. */
    public static <K extends Comparable<K>,V>
    ImSortedMap<K,V> imSortedMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5,
                              K k6, V v6, K k7, V v7, K k8, V v8, K k9, V v9) {
        return PersistentTreeMap.of(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6, k7, v7, k8, v8, k9, v9);
    }

    /** Returns a new PersistentTreeMap of the given comparable keys and their paired values. */
    public static <K extends Comparable<K>,V>
    ImSortedMap<K,V> imSortedMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5,
                                       K k6, V v6, K k7, V v7, K k8, V v8, K k9, V v9, K k10, V v10) {
        return PersistentTreeMap.of(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6, k7, v7, k8, v8, k9, v9, k10, v10);
    }

    /**
     Returns a new PersistentTreeMap of the given comparable keys and their paired values, skipping any null Entries.
     */
    @SafeVarargs
    public static <K extends Comparable<K>,V> ImSortedMap<K,V> imSortedMapSkipNull(Map.Entry<K,V>... es) {
        return PersistentTreeMap.ofSkipNull(es);
    }

    /** Returns a new PersistentTreeMap of the specified comparator and the given key/value pairs. */
    public static <K,V> ImSortedMap<K,V>
    imSortedMapComp(Comparator<? super K> c, K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5,
                    K k6, V v6, K k7, V v7, K k8, V v8, K k9, V v9, K k10, V v10) {
        return PersistentTreeMap.ofComp(c, k1, v1, k2, v2, k3, v3, k4, v4, k5, v5,
                                        k6, v6, k7, v7, k8, v8, k9, v9, k10, v10);
    }

    /** Returns a new PersistentTreeMap of the specified comparator and the given key/value pairs. */
    public static <K,V> ImSortedMap<K,V>
    imSortedMapComp(Comparator<? super K> c, K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5,
                    K k6, V v6, K k7, V v7, K k8, V v8, K k9, V v9) {
        return PersistentTreeMap.ofComp(c, k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6, k7, v7, k8, v8, k9, v9);
    }

    /** Returns a new PersistentTreeMap of the specified comparator and the given key/value pairs. */
        public static <K,V> ImSortedMap<K,V>
    imSortedMapComp(Comparator<? super K> c, K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5,
                    K k6, V v6, K k7, V v7, K k8, V v8) {
            return PersistentTreeMap.ofComp(c, k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6, k7, v7, k8, v8);
    }

    /** Returns a new PersistentTreeMap of the specified comparator and the given key/value pairs. */
    public static <K,V> ImSortedMap<K,V>
    imSortedMapComp(Comparator<? super K> c, K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5,
                    K k6, V v6, K k7, V v7) {
        return PersistentTreeMap.ofComp(c, k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6, k7, v7);
    }

    /** Returns a new PersistentTreeMap of the specified comparator and the given key/value pairs. */
    public static <K,V> ImSortedMap<K,V>
    imSortedMapComp(Comparator<? super K> c, K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6) {
        return PersistentTreeMap.ofComp(c, k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6);
    }

    /** Returns a new PersistentTreeMap of the specified comparator and the given key/value pairs. */
    public static <K,V> ImSortedMap<K,V>
    imSortedMapComp(Comparator<? super K> c, K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5) {
        return PersistentTreeMap.ofComp(c, k1, v1, k2, v2, k3, v3, k4, v4, k5, v5);
    }

    /** Returns a new PersistentTreeMap of the specified comparator and the given key/value pairs. */
    public static <K,V> ImSortedMap<K,V>
    imSortedMapComp(Comparator<? super K> c, K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
        return PersistentTreeMap.ofComp(c, k1, v1, k2, v2, k3, v3, k4, v4);
    }

    /** Returns a new PersistentTreeMap of the specified comparator and the given key/value pairs. */
    public static <K,V> ImSortedMap<K,V>
    imSortedMapComp(Comparator<? super K> c, K k1, V v1, K k2, V v2, K k3, V v3) {
        return PersistentTreeMap.ofComp(c, k1, v1, k2, v2, k3, v3);
    }

    /** Returns a new PersistentTreeMap of the specified comparator and the given key/value pairs. */
    public static <K,V> ImSortedMap<K,V>
    imSortedMapComp(Comparator<? super K> c, K k1, V v1, K k2, V v2) {
        return PersistentTreeMap.ofComp(c, k1, v1, k2, v2);
    }

    /** Returns a new PersistentTreeMap of the specified comparator and the given key/value pairs. */
    public static <K,V> ImSortedMap<K,V>
    imSortedMapComp(Comparator<? super K> c, K k1, V v1) {
        return PersistentTreeMap.ofComp(c, k1, v1);
    }

    /** Returns a new PersistentTreeMap of the specified comparator and the given key/value pairs. */
    @SafeVarargs
    public static <K,V> ImSortedMap<K,V>
    imSortedMapCompSkipNull(Comparator<? super K> c, Map.Entry<K,V>... es) {
        return PersistentTreeMap.ofCompSkipNull(c, es);
    }

    /** Returns a new PersistentTreeSet of the given comparable items. */
    @SafeVarargs
    public static <T extends Comparable<T>> ImSortedSet<T> imSortedSet(T... items) {
        return PersistentTreeSet.of(items);
    }

    /**
     Returns a new PersistentTreeSet of the given comparator.  Always use this instead of starting with empty() because
     there is no way to assign a comparator later on.
     */
    public static <T> ImSortedSet<T> imSortedSetComp(Comparator<? super T> comp) {
        return PersistentTreeSet.ofComp(comp);
    }

    /** Returns a new PersistentTreeSet of the given comparator and items. */
    @SafeVarargs
    public static <T> ImSortedSet<T> imSortedSetComp(Comparator<? super T> comp, T... items) {
        return PersistentTreeSet.ofComp(comp, items);
    }

    // EqualsWhichDoesntCheckParameterClass Note:
    // http://codereview.stackexchange.com/questions/88333/is-one-sided-equality-more-helpful-or-more-confusing-than-quick-failure
    // "There is no one-sided equality. If it is one-sided, that is it's asymmetric, then it's just wrong."
    // Which is a little ironic because with inheritance, there are many cases in Java where equality is one-sided.

    /** Returns an unmodifiable version of the given iterable. */
    // TODO: Test this.
    public static <T> UnmodIterable<T> unmod(Iterable<T> iterable) {
        if (iterable == null) { return () -> UnmodIterator.empty(); }
        if (iterable instanceof UnmodIterable) { return (UnmodIterable<T>) iterable; }
        return () -> new UnmodIterator<T>() {
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
    // Never make this public.  We can't trust an iterator that we didn't get
    // brand new ourselves, because iterators are inherently unsafe to share.
    private static <T> UnmodIterator<T> unmod(Iterator<T> iter) {
        if (iter == null) { return UnmodIterator.empty(); }
        if (iter instanceof UnmodIterator) { return (UnmodIterator<T>) iter; }
        return new UnmodIterator<T>() {
            @Override public boolean hasNext() { return iter.hasNext(); }
            @Override public T next() { return iter.next(); }
            // Defining equals and hashcode makes no sense because can't call them without changing the iterator
            // which both makes it useless, and changes the equals and hashcode results.
//            @Override public int hashCode() { return iter.hashCode(); }
//            @SuppressWarnings("EqualsWhichDoesntCheckParameterClass") // See Note above.
//            @Override public boolean equals(Object o) { return iter.equals(o); }
        };
    }

    /** Returns an unmodifiable version of the given listIterator.  This is private because sharing iterators is bad. */
    private static <T> UnmodListIterator<T> unmod(ListIterator<T> iter) {
        if (iter == null) { return UnmodListIterator.empty(); }
        if (iter instanceof UnmodListIterator) { return (UnmodListIterator<T>) iter; }
        return new UnmodListIterator<T>() {
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
    public static <T> UnmodList<T> unmod(List<T> inner) {
        if (inner == null) { return UnmodList.empty(); }
        if (inner instanceof UnmodList) { return (UnmodList<T>) inner; }
        if (inner.size() < 1) { return UnmodList.empty(); }
        return new UnmodList<T>() {
            @Override public UnmodListIterator<T> listIterator(int index) { return unmod(inner.listIterator(index)); }
            @Override public int size() { return inner.size(); }
            @Override public T get(int index) { return inner.get(index); }
            @Override public int hashCode() { return inner.hashCode(); }
            @SuppressWarnings("EqualsWhichDoesntCheckParameterClass") // See Note above.
            @Override public boolean equals(Object o) { return inner.equals(o); }
        };
    }

    /** Returns an unmodifiable version of the given set. */
    public static <T> UnmodSet<T> unmod(Set<T> set) {
        if (set == null) { return UnmodSet.empty(); }
        if (set instanceof UnmodSet) { return (UnmodSet<T>) set; }
        if (set.size() < 1) { return UnmodSet.empty(); }
        return new UnmodSet<T>() {
            @Override public boolean contains(Object o) { return set.contains(o); }
            @Override public int size() { return set.size(); }
            @Override public boolean isEmpty() { return set.isEmpty(); }
            @Override public UnmodIterator<T> iterator() { return unmod(set.iterator()); }
            @Override public int hashCode() { return set.hashCode(); }
            @SuppressWarnings("EqualsWhichDoesntCheckParameterClass") // See Note above.
            @Override public boolean equals(Object o) { return set.equals(o); }
        };
    }

    /** Returns an unmodifiable version of the given set. */
    public static <T> UnmodSortedSet<T> unmod(SortedSet<T> set) {
        if (set == null) { return UnmodSortedSet.empty(); }
        if (set instanceof UnmodSortedSet) { return (UnmodSortedSet<T>) set; }
        if (set.size() < 1) { return UnmodSortedSet.empty(); }
        return new UnmodSortedSet<T>() {
            @Override public Comparator<? super T> comparator() { return set.comparator(); }
            @Override public UnmodSortedSet<T> subSet(T fromElement, T toElement) {
                return unmod(set.subSet(fromElement, toElement));
            }
            @Override public UnmodSortedSet<T> headSet(T toElement) { return unmod(set.headSet(toElement)); }
            @Override public UnmodSortedSet<T> tailSet(T fromElement) { return unmod(set.tailSet(fromElement)); }
            @Override public T first() { return set.first(); }
            @Override public T last() { return set.last(); }
            @Override public boolean contains(Object o) { return set.contains(o); }
            @Override public int size() { return set.size(); }
            @Override public boolean isEmpty() { return set.isEmpty(); }
            @Override public UnmodSortedIterator<T> iterator() {
                return new UnmodSortedIterator<T>() {
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
    public static <K,V> UnmodMap<K,V> unmod(Map<K,V> map) {
        if (map == null) { return UnmodMap.empty(); }
        if (map instanceof UnmodMap) { return (UnmodMap<K,V>) map; }
        if (map.size() < 1) { return UnmodMap.empty(); }
        return new UnmodMap<K,V>() {
            /** {@inheritDoc} */
            @Override
            public UnmodIterator<UnEntry<K,V>> iterator() { return UnmodMap.UnEntry.wrap(map.entrySet().iterator()); }

            @Override public UnmodSet<Entry<K,V>> entrySet() { return unmod(map.entrySet()); }
            @Override public int size() { return map.size(); }
            @Override public boolean isEmpty() { return map.isEmpty(); }
            @Override public boolean containsKey(Object key) { return map.containsKey(key); }
            @Override public boolean containsValue(Object value) { return map.containsValue(value); }
            @Override public V get(Object key) { return map.get(key); }
            @Override public UnmodSet<K> keySet() { return unmod(map.keySet()); }
            @Override public UnmodCollection<V> values() { return unmod(map.values()); }
            @Override public int hashCode() { return map.hashCode(); }
            @SuppressWarnings("EqualsWhichDoesntCheckParameterClass") // See Note above.
            @Override public boolean equals(Object o) { return map.equals(o); }
        };
    }

    /** Returns an unmodifiable version of the given sorted map. */
    public static <K,V> UnmodSortedMap<K,V> unmod(SortedMap<K,V> map) {
        if (map == null) { return UnmodSortedMap.empty(); }
        if (map instanceof UnmodSortedMap) { return (UnmodSortedMap<K,V>) map; }
        if (map.size() < 1) { return UnmodSortedMap.empty(); }
        return new UnmodSortedMap<K,V>() {
            // TODO: Test this.
            @Override public UnmodSortedSet<Entry<K,V>> entrySet() {
                return new UnmodSortedSet<Entry<K,V>>() {
                    Set<Map.Entry<K,V>> entrySet = map.entrySet();
                    @Override public UnmodSortedIterator<Entry<K,V>> iterator() {
                        return new UnmodSortedIterator<Entry<K,V>>() {
                            Iterator<Entry<K,V>> iter = entrySet.iterator();
                            @Override public boolean hasNext() { return iter.hasNext(); }
                            @Override public Entry<K,V> next() { return iter.next(); }
                        };
                    }
                    @Override public UnmodSortedSet<Entry<K,V>> subSet(Entry<K,V> fromElement, Entry<K,V> toElement) {
                        // This is recursive.  I hope it's not an infinite loop 'cause I don't want to write this
                        // all out again.
                        return unmod(map.subMap(fromElement.getKey(), toElement.getKey())).entrySet();
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
            @Override public UnmodSet<K> keySet() { return unmod(map.keySet()); }
            @Override public Comparator<? super K> comparator() { return map.comparator(); }
            @Override public UnmodSortedMap<K,V> subMap(K fromKey, K toKey) { return unmod(map.subMap(fromKey, toKey)); }
            @Override public UnmodSortedMap<K,V> tailMap(K fromKey) { return unmod(map.tailMap(fromKey)); }
            @Override public K firstKey() { return map.firstKey(); }
            @Override public K lastKey() { return map.lastKey(); }
            @Override public UnmodCollection<V> values() { return unmod(map.values()); }
            @Override public int hashCode() { return map.hashCode(); }
            @SuppressWarnings("EqualsWhichDoesntCheckParameterClass") // See Note above.
            @Override public boolean equals(Object o) { return map.equals(o); }
        };
    }

    /** Returns an unmodifiable version of the given collection. */
    public static <T> UnmodCollection<T> unmod(Collection<T> coll) {
        if (coll == null) { return UnmodCollection.empty(); }
        if (coll instanceof UnmodCollection) { return (UnmodCollection<T>) coll; }
        if (coll.size() < 1) { return UnmodCollection.empty(); }
        return new UnmodCollection<T>() {
            @Override public boolean contains(Object o) { return coll.contains(o); }
            @Override public int size() { return coll.size(); }
            @Override public boolean isEmpty() { return coll.isEmpty(); }
            @Override public UnmodIterator<T> iterator() { return unmod(coll.iterator()); }
            @Override public int hashCode() { return coll.hashCode(); }
            @SuppressWarnings("EqualsWhichDoesntCheckParameterClass") // See Note above.
            @Override public boolean equals(Object o) { return coll.equals(o); }
        };
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
