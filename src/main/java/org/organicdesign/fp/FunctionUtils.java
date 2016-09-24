// Copyright 2014-02-09 PlanBase Inc. & Glen Peterson
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

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;

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

/**
 A dumping ground for utility functions that aren't useful enough to belong in StaticImports.

 The unmod___() methods are an alternative to Collections.unmodifiable____().  They provide
 unmodifiable wrappers to protect mutable collections for sharing.  Except for the Iterators,
 the returned classes are Serializable.  These will never return null, the closest they get is to
 return an empty unmodifiable collection.  The unmodifiable interfaces they return have deprecated
 the modification methods so that any attempt to use those methods causes a warning in your IDE and
 compiler.
 */
public class FunctionUtils {

    // I don't want any instances of this class.
    private FunctionUtils() {
        throw new UnsupportedOperationException("No instantiation");
    }

    public static String stringify(Object o) {
        if (o == null) { return "null"; }
        if (o instanceof String) { return "\"" + o + "\""; }
        return o.toString();
    }

    // Not Needed in Java 8.
//    /** Returns a String showing the type and first few elements of a map */
//    public static <A,B> String mapToString(Map<A,B> map) {
//        if (map == null) {
//            return "null";
//        }
//        StringBuilder sB = new StringBuilder();
//
//        sB.append(map.getClass().getSimpleName());
//        sB.append("(");
//
//        int i = 0;
//        for (Map.Entry<A,B> item : map.entrySet()) {
//            if (i > 4) {
//                sB.append(",...");
//                break;
//            } else if (i > 0) {
//                sB.append(",");
//            }
//            sB.append("Entry(").append(String.valueOf(item.getKey())).append(",");
//            sB.append(String.valueOf(item.getValue())).append(")");
//            i++;
//        }
//
//        sB.append(")");
//        return sB.toString();
//    }
//
//    /** Returns a String showing the type and first few elements of an array */
//    public static String arrayToString(Object[] as) {
//        if (as == null) {
//            return "null";
//        }
//        StringBuilder sB = new StringBuilder();
//        sB.append("Array");
//
//        if ( (as.length > 0) && (as[0] != null) ) {
//            sB.append("<");
//            sB.append(as[0].getClass().getSimpleName());
//            sB.append(">");
//        }
//
//        sB.append("(");
//
//        int i = 0;
//        for (Object item : as) {
//            if (i > 4) {
//                sB.append(",...");
//                break;
//            } else if (i > 0) {
//                sB.append(",");
//            }
//        }
//
//        sB.append(")");
//        return sB.toString();
//    }

//    public static String truncateIfNecessary(String in, int maxLen) {
//        if ( (in == null) || (in.length() <= maxLen) ) {
//            return in;
//        }
//        return in.substring(0, maxLen);
//    }
//
//    public static Date earliestOrNull(Date... dates) {
//        if ( (dates == null) || (dates.length < 1) ) {
//            return null;
//        }
//        Date earliest = null;
//        for (Date date : dates) {
//            if (earliest == null) {
//                earliest = date;
//            } else if ((date != null) && (date.before(earliest)) ) {
//                earliest = date;
//            }
//        }
//        return earliest;
//    }
//
//    public enum EnglishListType {
//        AND("and"),
//        OR("or");
//        public final String word;
//        EnglishListType(String s) {
//            word = s;
//        }
//    }
//
//    public static String unsafeEnglishList(Collection<?> rips, EnglishListType type) {
//        if ( (rips == null) || (rips.size() < 1) ) {
//            return "";
//        }
//        StringBuilder sB = new StringBuilder();
//        int i = 0;
//        for (Object rip : rips) {
//            i++;
//            if (i > 1) {
//                if (rips.size() > 2) {
//                    // if there are three or more rips, print with commas
//                    // between all but the last two - they get ", and "
//                    if (i < rips.size()) {
//                        sB.concat(", ");
//                    } else {
//                        // The serial comma!
//                        sB.concat(", ");
//                        sB.concat(type.word);
//                        sB.concat(" ");
//                    }
//                } else if ( (rips.size() == 2) && (i == 2) ) {
//                    // If there are two rips, print with " and " inbetween
//                    sB.concat(" ");
//                    sB.concat(type.word);
//                    sB.concat(" ");
//                }
//            }
//            // print it.  This is safe because these strings are hard-coded
//            // above, do not come from the user, and are HTML-safe.
//            sB.concat(rip.toString());
//        }
//        return sB.toString();
//    }
//
//    public static String commaSepList(Iterable<?> is) {
//        StringBuilder sB = new StringBuilder();
//        boolean isFirst = true;
//        for (Object o : is) {
//            if (isFirst) {
//                isFirst = false;
//            } else {
//                sB.concat(", ");
//            }
//            sB.concat(String.valueOf(o));
//        }
//        return sB.toString();
//    }

    public static String ordinal(final int origI) {
        final int i = (origI < 0) ? -origI : origI;
        final int modTen = i % 10;
        if ( (modTen < 4) && (modTen > 0)) {
            int modHundred = i % 100;
            if ( (modHundred < 21) && (modHundred > 3) ) {
                return Integer.toString(origI) + "th";
            }
            switch (modTen) {
                case 1: return Integer.toString(origI) + "st";
                case 2: return Integer.toString(origI) + "nd";
                case 3: return Integer.toString(origI) + "rd";
            }
        }
        return Integer.toString(origI) + "th";
    }

// EqualsWhichDoesntCheckParameterClass Note:
// http://codereview.stackexchange.com/questions/88333/is-one-sided-equality-more-helpful-or-more-confusing-than-quick-failure
// "There is no one-sided equality. If it is one-sided, that is it's asymmetric, then it's just
// wrong."  Which is a little ironic because with inheritance, there are many cases in Java
// where equality is one-sided.

    // ========================================== Classes ==========================================

    // The point of these classes existing at all are to wrap mutable collections for safe
    // sharing.  Use them either to retrofit existing Java code, or to wrap mutable collections
    // you may use for performance reasons.
    //
    // These are true, named classes instead of anonymous implementations so that they can properly
    // implement Serializable.
    //
    // These classes seem to have to be public in order to compile without
    // "remove() in org.organicdesign.fp.collections.UnmodIterator is defined in an inaccessible class or interface"
    //
    // They belong here, instead of being a static class in the Unmod___ interfacies, so that they
    // don't overshadow same-named static classes in sub-interfaces, prevent use in method
    // references (due to overloading), make overloading in subclasses onerous, or generally cause\
    // confusion.


    /**
     Wraps an iterator.  Not Serializable.  You probably want to use this by calling
     {@link #unmodIterator(Iterator)}.
     */
    @SuppressWarnings("WeakerAccess")
    public static class UnmodifiableIterator<E> implements UnmodIterator<E> {
        // Iterators are not serializable (today) because they aren't in Java.
        // I'm assuming Java had a good reason for that, but I really don't know.
//        , Serializable {
//        // For serializable.  Make sure to change whenever internal data format changes.
//        private static final long serialVersionUID = 20160903174100L;

        private final Iterator<E> iter;
        private UnmodifiableIterator(Iterator<E> i) { iter = i; }

        @Override public boolean hasNext() { return iter.hasNext(); }
        @Override public E next() { return iter.next(); }

        // Defining equals and hashcode makes no sense because can't call them without changing
        // the iterator which both makes it useless, and changes the equals and hashcode
        // results.
//            @Override public int hashCode() { return iter.hashCode(); }
//            @SuppressWarnings("EqualsWhichDoesntCheckParameterClass") // See Note above.
//            @Override public boolean equals(Object o) { return iter.equals(o); }
    }

    /**
     Wraps an ordered iterator.  Not Serializable.  You probably want to use this by calling
     {@link #unmodSortedIterator(Iterator)}.
     */
    @SuppressWarnings("WeakerAccess")
    public static class UnmodifiableSortedIterator<E> implements UnmodSortedIterator<E> {
        // Iterators are not serializable (today) because they aren't in Java.
        // I'm assuming Java had a good reason for that, but I really don't know.
//        , Serializable {
//        // For serializable.  Make sure to change whenever internal data format changes.
//        private static final long serialVersionUID = 20160903174100L;

        private final Iterator<E> iter;
        private UnmodifiableSortedIterator(Iterator<E> i) { iter = i; }

        @Override public boolean hasNext() { return iter.hasNext(); }
        @Override public E next() { return iter.next(); }

        // Defining equals and hashcode makes no sense because can't call them without changing
        // the iterator which both makes it useless, and changes the equals and hashcode
        // results.
//            @Override public int hashCode() { return iter.hashCode(); }
//            @SuppressWarnings("EqualsWhichDoesntCheckParameterClass") // See Note above.
//            @Override public boolean equals(Object o) { return iter.equals(o); }
    }

    // Don't put this on UnmodIterator - see reasons in UnmodifiableIterator above.
//    public static class UnmodifiableIteratorEmpty<T> extends UnmodifiableIterator<T> {
//        // Not serializable, so we don't have to defend against deserialization.
//        private static final UnmodifiableIteratorEmpty<?> INSTANCE =
//                new UnmodifiableIteratorEmpty<>(Collections.emptyIterator());
//        private UnmodifiableIteratorEmpty(Iterator<T> i) { super(i); }
//    }

    // I don't see the need for this and I'm concerned about confusing SortedIterable with Iterable.
//    /**
//     Wraps an iterable.  The result will be serializable to the extent that the wrapped
//     iterable is.  You probably want to use this by calling
//     {@link #unmodIterable(Iterable)}.
//     */
//    @SuppressWarnings("WeakerAccess")
//    public static class UnmodifiableIterable<T> implements UnmodIterable<T>, Serializable {
//        private final Iterable<T> iterable;
//        private UnmodifiableIterable(Iterable<T> is) { iterable = is; }
//
//        // For serializable.  Make sure to change whenever internal data format changes.
//        private static final long serialVersionUID = 20160918033000L;
//
//        @Override public UnmodIterator<T> iterator() {
//            return new UnmodifiableIterator<>(iterable.iterator());
//        }
//    }

//    private static final class UnmodifiableIterableEmpty<T> extends UnmodifiableIterable<T> {
//        private static final UnmodifiableIterableEmpty<?> INSTANCE =
//                new UnmodifiableIterableEmpty<>(Collections.emptySet());
//        private UnmodifiableIterableEmpty(Iterable<T> is) { super(is); }
//        // For serializable.  Make sure to change whenever internal data format changes.
//        private static final long serialVersionUID = 20160918033000L;
//
//        // This enforces the singleton property in the face of deserialization.
//        private Object readResolve() { return INSTANCE; }
//
//        @Override public UnmodifiableIteratorEmpty<T> iterator() { return emptyUnmodIterator(); }
//    }

    /**
     Wraps a list iterator.  The is NOT serializable.  You probably want to use this by calling
     {@link #unmodListIterator(ListIterator)}.
     */
    @SuppressWarnings("WeakerAccess")
    public static class UnmodifiableListIterator<T> implements UnmodListIterator<T>, Serializable {
        private final ListIterator<T> iter;
        private UnmodifiableListIterator(ListIterator<T> is) { iter = is; }

        // For serializable.  Make sure to change whenever internal data format changes.
        private static final long serialVersionUID = 20160918033000L;

        @Override public boolean hasNext() { return iter.hasNext(); }
        @Override public T next() { return iter.next(); }
        @Override public boolean hasPrevious() { return iter.hasPrevious(); }
        @Override public T previous() { return iter.previous(); }
        @Override public int nextIndex() { return iter.nextIndex(); }
        // Defining equals and hashcode makes no sense because can't call them without changing
        // the iterator which both makes it useless, and changes the equals and hashcode
        // results.
//            @Override public int hashCode() { return iter.hashCode(); }
//            @SuppressWarnings("EqualsWhichDoesntCheckParameterClass") // See Note above.
//            @Override public boolean equals(Object o) { return iter.equals(o); }
    }

    /**
     Wraps a list.  The result will be serializable to the extent that the wrapped
     list is.  You probably want to use this by calling
     {@link #unmodList(List)}.
     */
    @SuppressWarnings("WeakerAccess")
    public static class UnmodifiableList<T> implements UnmodList<T>, Serializable {
        private final List<T> inner;
        UnmodifiableList(List<T> ls) { inner = ls; }

        // For serializable.  Make sure to change whenever internal data format changes.
        private static final long serialVersionUID = 20160918033000L;

        @Override public int size() { return inner.size(); }
        @Override public T get(int index) { return inner.get(index); }
        @Override public int hashCode() { return inner.hashCode(); }
        @SuppressWarnings("EqualsWhichDoesntCheckParameterClass") // See Note above.
        @Override public boolean equals(Object o) { return inner.equals(o); }
        @Override public UnmodListIterator<T> listIterator(int idx) {
            return unmodListIterator(inner.listIterator(idx));
        }
        @Override public UnmodListIterator<T> listIterator() {
            return unmodListIterator(inner.listIterator());
        }
        @Override public UnmodSortedIterator<T> iterator() {
            return unmodSortedIterator(inner.iterator());
        }
        @Override public String toString() {
            return UnmodIterable.toString("UnmodList", inner);
        }
    }

    /**
     Wraps a set.  The result will be serializable to the extent that the wrapped
     set is.  You probably want to use this by calling
     {@link #unmodSet(Set)}.
     */
    @SuppressWarnings("WeakerAccess")
    public static class UnmodifiableSet<T> implements UnmodSet<T>, Serializable {
        private final Set<T> set;
        private UnmodifiableSet(Set<T> s) { set = s; }

        // For serializable.  Make sure to change whenever internal data format changes.
        private static final long serialVersionUID = 20160918033000L;

        @Override public boolean contains(Object o) { return set.contains(o); }
        @Override public int size() { return set.size(); }
        @Override public boolean isEmpty() { return set.isEmpty(); }
        @Override public UnmodIterator<T> iterator() { return unmodIterator(set.iterator()); }
        @Override public int hashCode() { return set.hashCode(); }
        @SuppressWarnings("EqualsWhichDoesntCheckParameterClass") // See Note above.
        @Override public boolean equals(Object o) { return set.equals(o); }
        @Override public String toString() {
            return UnmodIterable.toString("UnmodSet", set);
        }
    }

    /**
     Wraps a sorted set.  The result will be serializable to the extent that the wrapped
     set is.  You probably want to use this by calling
     {@link #unmodSortedSet(SortedSet)}.
     */
    @SuppressWarnings("WeakerAccess")
    public static class UnmodifiableSortedSet<T> implements UnmodSortedSet<T>, Serializable {
        private final SortedSet<T> set;
        private UnmodifiableSortedSet(SortedSet<T> s) { set = s; }

        // For serializable.  Make sure to change whenever internal data format changes.
        private static final long serialVersionUID = 20160918033000L;

        @Override public Comparator<? super T> comparator() { return set.comparator(); }
        @Override public UnmodSortedSet<T> subSet(T fromElement, T toElement) {
            return unmodSortedSet(set.subSet(fromElement, toElement));
        }
        @Override public UnmodSortedSet<T> headSet(T toElement) {
            return unmodSortedSet(set.headSet(toElement));
        }
        @Override public UnmodSortedSet<T> tailSet(T fromElement) {
            return unmodSortedSet(set.tailSet(fromElement));
        }
        @Override public T first() { return set.first(); }
        @Override public T last() { return set.last(); }
        @Override public boolean contains(Object o) { return set.contains(o); }
        @Override public int size() { return set.size(); }
        @Override public boolean isEmpty() { return set.isEmpty(); }
        @Override public UnmodSortedIterator<T> iterator() {
            return unmodSortedIterator(set.iterator());
        }
        @Override public int hashCode() { return set.hashCode(); }
        @SuppressWarnings("EqualsWhichDoesntCheckParameterClass") // See Note above.
        @Override public boolean equals(Object o) { return set.equals(o); }
        @Override public String toString() {
            return UnmodIterable.toString("UnmodSortedSet", set);
        }
    }

    /**
     Wraps a map.  The result will be serializable to the extent that the wrapped
     map is.  You probably want to use this by calling
     {@link #unmodMap(Map)}.
     */
    @SuppressWarnings("WeakerAccess")
    public static class UnmodifiableMap<K,V> implements UnmodMap<K,V>, Serializable {
        private final Map<K,V> map;
        private UnmodifiableMap(Map<K,V> m) { map = m; }

        // For serializable.  Make sure to change whenever internal data format changes.
        private static final long serialVersionUID = 20160918033000L;

        /** {@inheritDoc} */
        @Override public UnmodIterator<UnmodMap.UnEntry<K,V>> iterator() {
            return UnmodMap.UnEntry.entryIterToUnEntryUnIter(map.entrySet().iterator());
        }

        @Override public UnmodSet<Map.Entry<K,V>> entrySet() { return unmodSet(map.entrySet()); }
        @Override public int size() { return map.size(); }
        @Override public boolean isEmpty() { return map.isEmpty(); }
        @Override public boolean containsKey(Object key) { return map.containsKey(key); }
        @Override public boolean containsValue(Object value) {
            return map.containsValue(value);
        }
        @Override public V get(Object key) { return map.get(key); }
        @Override public UnmodSet<K> keySet() { return unmodSet(map.keySet()); }
        @SuppressWarnings("deprecation")
        @Deprecated
        @Override public UnmodCollection<V> values() { return unmodCollection(map.values()); }
        @Override public int hashCode() { return map.hashCode(); }
        @SuppressWarnings("EqualsWhichDoesntCheckParameterClass") // See Note above.
        @Override public boolean equals(Object o) { return map.equals(o); }
        @Override public String toString() {
            return UnmodIterable.toString("UnmodMap", map.entrySet());
        }
    }

    /**
     Wraps a sorted map.  The result will be serializable to the extent that the wrapped
     sorted map is.  You probably want to use this by calling
     {@link #unmodSortedMap(SortedMap)}.
     */
    @SuppressWarnings("WeakerAccess")
    public static class UnmodifiableSortedMap<K,V> implements UnmodSortedMap<K,V>, Serializable {
        private final SortedMap<K,V> map;
        private UnmodifiableSortedMap(SortedMap<K,V> m) { map = m; }

        // For serializable.  Make sure to change whenever internal data format changes.
        private static final long serialVersionUID = 20160918033000L;

        @Override public int size() { return map.size(); }
        @Override public boolean isEmpty() { return map.isEmpty(); }
        @Override public boolean containsKey(Object key) { return map.containsKey(key); }
        @Override public boolean containsValue(Object value) {
            return map.containsValue(value);
        }
        @Override public V get(Object key) { return map.get(key); }
        @Override public Comparator<? super K> comparator() { return map.comparator(); }
        @Override public UnmodSortedMap<K,V> subMap(K fromKey, K toKey) {
            return unmodSortedMap(map.subMap(fromKey, toKey));
        }
        @Override public UnmodSortedMap<K,V> tailMap(K fromKey) {
            return unmodSortedMap(map.tailMap(fromKey));
        }
        @Override public K firstKey() { return map.firstKey(); }
        @Override public K lastKey() { return map.lastKey(); }
        @Override public UnmodSortedIterator<UnEntry<K,V>> iterator() {
            return UnmodMap.UnEntry.entryIterToUnEntrySortedUnIter(map.entrySet().iterator());
        }
        @Override public int hashCode() { return map.hashCode(); }
        @SuppressWarnings("EqualsWhichDoesntCheckParameterClass") // See Note above.
        @Override public boolean equals(Object o) { return map.equals(o); }

        @Override public String toString() {
            return UnmodIterable.toString("UnmodSortedMap", map.entrySet());
        }
    }

    /**
     Wraps a collection.  The result will be serializable to the extent that the wrapped
     collection is.  You probably want to use this by calling
     {@link #unmodCollection(Collection)} (also see caveats there).
     */
    @SuppressWarnings("WeakerAccess")
    public static class UnmodifiableCollection<T> implements UnmodCollection<T>, Serializable {
        private final Collection<T> coll;
        private UnmodifiableCollection(Collection<T> c) { coll = c; }

        // For serializable.  Make sure to change whenever internal data format changes.
        private static final long serialVersionUID = 20160918033000L;

        @Override public boolean contains(Object o) { return coll.contains(o); }
        @Override public int size() { return coll.size(); }
        @Override public boolean isEmpty() { return coll.isEmpty(); }
        @Override public UnmodIterator<T> iterator() { return unmodIterator(coll.iterator()); }
        @Override public int hashCode() { return coll.hashCode(); }
        @SuppressWarnings("EqualsWhichDoesntCheckParameterClass") // See Note above.
        @Override public boolean equals(Object o) { return coll.equals(o); }
        @Override public String toString() {
            return UnmodIterable.toString("UnmodCollection", coll);
        }
    }

    // ========================================== Empties ==========================================

    // I had originally provided special implementations for empty collections and iterators.
    // I started to further enhance these with Serializable singletons with a defensive
    // readResolve method to defend against deserializing non-singleton instances.  But that
    // added a lot of complexity without adding a lot of real value.  It also increased the jar
    // file size and I'm increasingly finding that the JVM optimizes best with minimal code size
    // (and maximum reuse).  So I picked simple.  Some implementations are left commented out.

    /** Returns an empty unmodifiable iterator.  The result is not serializable. */
    public static <T> UnmodifiableIterator<T> emptyUnmodIterator() {
        return new UnmodifiableIterator<>(Collections.emptyIterator());
    }

//    /**
//     Returns an empty unmodifiable iterable.  The result is serializable, but deserialization will
//     produce a new, unique object.  For this reason, you should not compare the returned iterable
//     with the == operator.
//     */
//    @SuppressWarnings("WeakerAccess")
//    public static <E> UnmodIterable<E> emptyUnmodIterable() {
//        return new UnmodifiableIterable<>(Collections.emptySet());
//    }

    /**
     Only use this where Sorted items are called for.  Returns an empty unmodifiable sorted
     iterator.  The result is serializable, but deserialization will produce a new, unique object.
     For this reason, you should not compare the returned iterator with the == operator.
     */
    public static <T> UnmodSortedIterator<T> emptyUnmodSortedIterator() {
        return new UnmodifiableSortedIterator<>(Collections.emptyIterator());
    }

    /** Returns an empty list iterator.  The result is NOT serializable. */
    public static <T> UnmodListIterator<T> emptyUnmodListIterator() {
        return new UnmodifiableListIterator<>(Collections.emptyListIterator());
    }

//    // This is a separate class 1. to defend this singleton against serialization/deserialization
//    // and I suppose 2. so that you can enforce the singleton property with the type system if you
//    // want to.
//    private static final class UnmodifiableListEmpty<T> extends UnmodifiableList<T> {
//        private static final UnmodifiableListEmpty<?> INSTANCE = new UnmodifiableListEmpty();
//        private UnmodifiableListEmpty() { super(Collections.emptyList()); }
//        // For serializable.  Make sure to change whenever internal data format changes.
//        private static final long serialVersionUID = 20160918033000L;
//        // This enforces the singleton property in the face of deserialization.
//        private Object readResolve() { return INSTANCE; }
//    }

    /**
     Returns an empty unmodifiable list.  The result is serializable, but deserialization will
     produce a new, unique object.  For this reason, you should not compare the returned list
     with the == operator.
     */
    @SuppressWarnings("WeakerAccess")
    public static <T> UnmodList<T> emptyUnmodList() {
        return new UnmodifiableList<>(Collections.emptyList());
    }

    /**
     Returns an empty unmodifiable set.  The result is serializable, but deserialization will
     produce a new, unique object.  For this reason, you should not compare the returned set
     with the == operator.
     */
    @SuppressWarnings("WeakerAccess")
    public static <T> UnmodSet<T> emptyUnmodSet() {
        return new UnmodifiableSet<>(Collections.emptySet());
    }

    /**
     Returns an empty unmodifiable sorted set.  The result is serializable, but deserialization will
     produce a new, unique object.  For this reason, you should not compare the returned set
     with the == operator.
     */
    @SuppressWarnings("WeakerAccess")
    public static <T> UnmodSortedSet<T> emptyUnmodSortedSet() {
        return new UnmodifiableSortedSet<>(Collections.emptySortedSet());
    }

    /**
     Returns an empty unmodifiable map.  The result is serializable, but deserialization will
     produce a new, unique object.  For this reason, you should not compare the returned map
     with the == operator.
     */
    @SuppressWarnings("WeakerAccess")
    public static <T,U> UnmodMap<T,U> emptyUnmodMap() {
        return new UnmodifiableMap<>(Collections.emptyMap());
    }

    /**
     Returns an empty unmodifiable sorted map.  This is serializable, but deserialization will
     produce a new, unique object.  For this reason, you should not compare the returned map
     with the == operator.
     */
    @SuppressWarnings("WeakerAccess")
    public static <T,U> UnmodSortedMap<T,U> emptyUnmodSortedMap() {
        return new UnmodifiableSortedMap<>(Collections.emptySortedMap());
    }

    /**
     Returns an empty unmodifiable Collection.  Avoid this when possible because there is no way
     to compare Collections for equality (they could be Lists or Sets).
     This is serializable, but deserialization will produce a new, unique object.  For this reason,
     you should not compare the returned collection with the == operator.
     */
    public static <T> UnmodCollection<T> emptyUnmodCollection() {
        return new UnmodifiableCollection<>(Collections.emptySet());
    }

    // ====================================== Wrapper Methods ======================================

//    /**
//     Returns an unmodifiable version of the given iterable.  The result is serializable to the
//     extent that the supplied iterable is serializable.
//     */
//    public static <T> UnmodIterable<T> unmodIterable(Iterable<T> iterable) {
//        return (iterable == null)                  ? emptyUnmodIterable() :
//               (iterable instanceof UnmodIterable) ? (UnmodIterable<T>) iterable :
//               new UnmodifiableIterable<>(iterable);
//    }

    /**
     Returns an unmodifiable version of the given iterator.  The result is NOT serializable.
     You could pass a partially used-up iterator to this method, but that's probably something you
     want to avoid.
     */
    public static <T> UnmodIterator<T> unmodIterator(Iterator<T> iter) {
        return ( (iter == null) || !iter.hasNext() ) ? emptyUnmodIterator() :
               (iter instanceof UnmodIterator)       ? (UnmodIterator<T>) iter :
               new UnmodifiableIterator<>(iter);
    }

    /**
     Returns an unmodifiable version of the given ordered Iterator.
     Only use where items are ordered/sorted such as from a List or SortedSet that guarantees
     order.  The result is NOT serializable.
     */
    @SuppressWarnings("WeakerAccess")
    public static <T> UnmodSortedIterator<T> unmodSortedIterator(Iterator<T> iter) {
        return ( (iter == null) || !iter.hasNext() ) ? emptyUnmodSortedIterator() :
               (iter instanceof UnmodSortedIterator) ? (UnmodSortedIterator<T>) iter :
               new UnmodifiableSortedIterator<>(iter);
    }

    /**
     Returns an unmodifiable version of the given listIterator.  The result is NOT serializable.
     */
    @SuppressWarnings("WeakerAccess")
    public static <T> UnmodListIterator<T> unmodListIterator(ListIterator<T> iter) {
        return (iter == null)                           ? emptyUnmodListIterator() :
               (iter instanceof UnmodListIterator)      ? (UnmodListIterator<T>) iter :
               (!iter.hasNext() && !iter.hasPrevious()) ? emptyUnmodListIterator() :
               new UnmodifiableListIterator<>(iter);
    }

    /**
     Returns an unmodifiable version of the given list.  The returned list is serializable to the
     extent that the given list is serializable.
     */
    public static <T> UnmodList<T> unmodList(List<T> inner) {
        return (inner == null)              ? emptyUnmodList() :
               (inner instanceof UnmodList) ? (UnmodList<T>) inner :
               (inner.size() < 1)           ? emptyUnmodList() :
               new UnmodifiableList<>(inner);
    }

    /**
     Returns an unmodifiable version of the given set.  The result is serializable to the extent
     that the given set is serializable.
     */
    public static <T> UnmodSet<T> unmodSet(Set<T> set) {
        return (set == null)             ? emptyUnmodSet() :
               (set instanceof UnmodSet) ? (UnmodSet<T>) set :
               (set.size() < 1)          ? emptyUnmodSet() :
               new UnmodifiableSet<>(set);
    }

    /**
     Returns an unmodifiable version of the given set.  The result is serializable to the extent
     that the given set is serializable.  A common serialization mistake is to define a comparator
     with an anonymous class or function, or otherwise rely on a comparator which cannot be
     serialized.  The resulting deserialized set will take the default ordering
     (or could throw an error).  Since comparators are usually singletons, an Enum is probably the
     best way to implement one.
     */
    public static <T> UnmodSortedSet<T> unmodSortedSet(SortedSet<T> set) {
        return (set == null)                   ? emptyUnmodSortedSet() :
               (set instanceof UnmodSortedSet) ? (UnmodSortedSet<T>) set :
               (set.size() < 1)                ? emptyUnmodSortedSet() :
               new UnmodifiableSortedSet<>(set);
    }

    /**
     Returns an unmodifiable version of the given map.  The result is serializable to the
     extent that the given map is serializable.
     */
    public static <K,V> UnmodMap<K,V> unmodMap(Map<K,V> map) {
        return (map == null)             ? emptyUnmodMap() :
               (map instanceof UnmodMap) ? (UnmodMap<K,V>) map :
               (map.size() < 1)          ? emptyUnmodMap() :
               new UnmodifiableMap<>(map);
    }

    /**
     Returns an unmodifiable version of the given sorted map.  The result is serializable to the
     extent that the given map is serializable.  A common serialization mistake is to define a
     comparator with an anonymous class or function, or otherwise rely on a comparator which cannot
     be serialized.  The resulting deserialized map will take the default ordering
     (or could throw an error).  Since comparators are usually singletons, an Enum is probably the
     best way to implement one.
     */
    public static <K,V> UnmodSortedMap<K,V> unmodSortedMap(SortedMap<K,V> map) {
        return (map == null)                   ? emptyUnmodSortedMap() :
               (map instanceof UnmodSortedMap) ? (UnmodSortedMap<K,V>) map :
               (map.size() < 1)                ? emptyUnmodSortedMap() :
               new UnmodifiableSortedMap<>(map);
    }

    /**
     Returns an unmodifiable version of the given collection.  This class is too vague to be
     compared for equality.  Both List and Set are collections, but neither consider themselves
     equal to any Collection, and indeed their notions of equality are incompatible.
     The returned collection is serializable to the extent that the wrapped collection is.
     */
    @SuppressWarnings("WeakerAccess")
    public static <T> UnmodCollection<T> unmodCollection(Collection<T> coll) {
        return (coll == null)                    ? emptyUnmodCollection() :
               (coll instanceof UnmodCollection) ? (UnmodCollection<T>) coll :
               (coll.size() < 1)                 ? emptyUnmodCollection() :
               new UnmodifiableCollection<>(coll);
    }

    // ========================================= To Delete =========================================

    /** Use {@link #emptyUnmodIterator()} instead. */
    @SuppressWarnings({"unused", "WeakerAccess"})
    @Deprecated
    public static final UnmodIterator<?> EMPTY_UNMOD_ITERATOR = emptyUnmodIterator();

    /** Use {@link #emptyUnmodListIterator()} instead. */
    @SuppressWarnings({"unused", "WeakerAccess"})
    @Deprecated
    public static final UnmodListIterator<Object> EMPTY_UNMOD_LIST_ITERATOR =
            emptyUnmodListIterator();

//    /** Use {@link #emptyUnmodIterable()} instead. */
//    @SuppressWarnings({"unused", "WeakerAccess"})
//    @Deprecated
//    // Thankfully, this was never public.
//    static final UnmodIterable<?> EMPTY_UNMOD_ITERABLE = emptyUnmodIterable();

    /** Use {@link #emptyUnmodList()} instead */
    @SuppressWarnings({"unused", "WeakerAccess"})
    @Deprecated
    public static final UnmodList<?> EMPTY_UNMOD_LIST = emptyUnmodList();

    /** Use {@link #emptyUnmodSet()} instead. */
    @SuppressWarnings({"unused", "WeakerAccess"})
    @Deprecated
    public static final UnmodSet<Object> EMPTY_UNMOD_SET = emptyUnmodSet();

    /** Use {@link #emptyUnmodSortedIterator()} instead */
    @SuppressWarnings({"unused", "WeakerAccess"})
    @Deprecated
    public static final UnmodSortedIterator<Object> EMPTY_UNMOD_SORTED_ITERATOR =
            emptyUnmodSortedIterator();

    /** Use {@link #emptyUnmodSortedSet()} instead */
    @SuppressWarnings({"unused", "WeakerAccess"})
    @Deprecated
    public static final UnmodSet<Object> EMPTY_UNMOD_SORTED_SET = emptyUnmodSortedSet();

    /** Use {@link #emptyUnmodMap()} instead. */
    @SuppressWarnings({"unused", "WeakerAccess"})
    @Deprecated
    public static final UnmodMap<Object,Object> EMPTY_UNMOD_MAP = emptyUnmodMap();

    /** Use {@link #emptyUnmodSortedMap()} Instead. */
    @SuppressWarnings({"unused", "WeakerAccess"})
    @Deprecated
    // Thankfully, this was never public
    static final UnmodSortedMap<Object,Object> EMPTY_UNMOD_SORTED_MAP = emptyUnmodSortedMap();

    /** Use {@link #emptyUnmodCollection()} instead */
    @SuppressWarnings({"unused", "WeakerAccess"})
    @Deprecated
    public static final UnmodCollection<Object> EMPTY_UNMOD_COLLECTION = emptyUnmodCollection();

//    /**
//     Returns an int which is a unique and correct hash code for the objects passed.  This
//     hashcode is recomputed on every call, so that if any of these objects change their hashCodes,
//     this will always return the latest value.  Of course, if you add something to a collection
//     that uses a hashCode, then that hashCode changes, you're going to have problems!
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
//     Use this only if you can guarantee to pass it immutable objects only!  Returns a LazyInt that
//     will compute a unique and correct hash code on the first time it is called, then return that
//     primitive int very quickly for all future calls.  If any of the hashCodes for the objects
//     passed in change after that time, it will not affect the output of this function.  Of course,
//     if you add something to a collection that uses a hashCode and then change its hashCode, the
//     behavior is undefined, so changing things after that time is a bad idea anyway.  Still,
//     correct is more important than fast, so make good decisions about when to use this.
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
