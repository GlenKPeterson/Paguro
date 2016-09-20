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
import java.util.NoSuchElementException;
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
import org.organicdesign.fp.tuple.Tuple2;

/**
 A dumping ground for utility functions that aren't useful enough to belong in StaticImports.

 The unmod___() methods are an alternative to Collections.unmodifiable____() for building immutable
 collections.  These will never return null, the closest they get is to return an empty immutable
 collection (the same one is reused).  Also, the unmodifiable interfaces they return have deprecated
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

    /** Returns a String showing the type and first few elements of a map */
    public static <A,B> String mapToString(Map<A,B> map) {
        if (map == null) {
            return "null";
        }
        StringBuilder sB = new StringBuilder();

        sB.append(map.getClass().getSimpleName());
        sB.append("(");

        int i = 0;
        for (Map.Entry<A,B> item : map.entrySet()) {
            if (i > 4) {
                sB.append(",...");
                break;
            } else if (i > 0) {
                sB.append(",");
            }
            sB.append("Entry(").append(String.valueOf(item.getKey())).append(",");
            sB.append(String.valueOf(item.getValue())).append(")");
            i++;
        }

        sB.append(")");
        return sB.toString();
    }

    /** Returns a String showing the type and first few elements of an array */
    public static String arrayToString(Object[] as) {
        if (as == null) {
            return "null";
        }
        StringBuilder sB = new StringBuilder();
        sB.append("Array");

        if ( (as.length > 0) && (as[0] != null) ) {
            sB.append("<");
            sB.append(as[0].getClass().getSimpleName());
            sB.append(">");
        }

        sB.append("(");

        int i = 0;
        for (Object item : as) {
            if (i > 4) {
                sB.append(",...");
                break;
            } else if (i > 0) {
                sB.append(",");
            }
            sB.append(String.valueOf(item));
            i++;
        }

        sB.append(")");
        return sB.toString();
    }

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

    // This seems to have to be public in order to compile without
    // remove() in org.organicdesign.fp.collections.UnmodIterator is defined in an inaccessible class or interface
    // As a static class in UnmodIterator, it can shadow same-named static classes in
    // sub-interfaces, preventing use in method references (due to overloading), making overloading
    // in subclasses onerous, and generally causing confusion.
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

    // Don't put this on UnmodIterator - see reasons in UnmodifiableIterator above.
    public static class UnmodifiableIteratorEmpty<T> extends UnmodifiableIterator<T> {
        // Not serializable, so we don't have to defend against deserialization.
        private static final UnmodifiableIteratorEmpty<?> INSTANCE =
                new UnmodifiableIteratorEmpty<>(Collections.emptyIterator());
        private UnmodifiableIteratorEmpty(Iterator<T> i) { super(i); }
    }

    /** Use {@link #emptyUnmodIterator()} instead. */
    @Deprecated
    public static final UnmodIterator<?> EMPTY_UNMOD_ITERATOR = UnmodifiableIteratorEmpty.INSTANCE;

    @SuppressWarnings("unchecked")
    public static <T> UnmodifiableIteratorEmpty<T> emptyUnmodIterator() {
        return (UnmodifiableIteratorEmpty<T>) UnmodifiableIteratorEmpty.INSTANCE;
    }

    private static class UnmodifiableIterable<T> implements UnmodIterable<T>, Serializable {
        private final Iterable<T> iterable;
        UnmodifiableIterable(Iterable<T> is) { iterable = is; }

        // For serializable.  Make sure to change whenever internal data format changes.
        private static final long serialVersionUID = 20160918033000L;

        @Override public UnmodIterator<T> iterator() {
            final Iterator<T> iter = iterable.iterator();
            return iter.hasNext() ? new UnmodifiableIterator<>(iter)
                                  : emptyUnmodIterator();
        }
    }

    private static final class UnmodifiableIterableEmpty<T> extends UnmodifiableIterable<T> {
        private static final UnmodifiableIterableEmpty<?> INSTANCE =
                new UnmodifiableIterableEmpty<>(Collections.emptySet());
        private UnmodifiableIterableEmpty(Iterable<T> is) { super(is); }
        // For serializable.  Make sure to change whenever internal data format changes.
        private static final long serialVersionUID = 20160918033000L;

        // This enforces the singleton property in the face of deserialization.
        private Object readResolve() { return INSTANCE; }

        @Override public UnmodifiableIteratorEmpty<T> iterator() { return emptyUnmodIterator(); }
    }

    @SuppressWarnings("unchecked")
    public static <E> UnmodIterable<E> emptyUnmodIterable() {
        return (UnmodIterable<E>) UnmodifiableIterableEmpty.INSTANCE;
    }

    /** Returns an unmodifiable version of the given iterable. */
    public static <T> UnmodIterable<T> unmodIterable(Iterable<T> iterable) {
        if (iterable == null) { return emptyUnmodIterable(); }
        if (iterable instanceof UnmodIterable) { return (UnmodIterable<T>) iterable; }
        return new UnmodifiableIterable<>(iterable);
    }

    /**
     Returns an unmodifiable version of the given iterator.  Note that while you could pass
     a partially used-up iterator to this method, that's probably something you want to avoid.
     */
    public static <T> UnmodIterator<T> unmodIterator(Iterator<T> iter) {
        if ( (iter == null) || !iter.hasNext() ) { return emptyUnmodIterator(); }
        if (iter instanceof UnmodIterator) { return (UnmodIterator<T>) iter; }
        return new UnmodifiableIterator<>(iter);
    }

    public static final UnmodListIterator<Object> EMPTY_UNMOD_LIST_ITERATOR =
            new UnmodListIterator<Object>() {
        @Override public boolean hasNext() { return false; }
        @Override public Object next() { throw new NoSuchElementException(); }
        @Override public boolean hasPrevious() { return false; }
        @Override public Object previous() { throw new NoSuchElementException(); }
        @Override public int nextIndex() { return 0; }
    };
    @SuppressWarnings("unchecked")
    public static <T> UnmodListIterator<T> emptyUnmodListIterator() {
        return (UnmodListIterator<T>) EMPTY_UNMOD_LIST_ITERATOR;
    }

    /**
     Returns an unmodifiable version of the given listIterator.  This is private because sharing
     iterators is bad.
     */
    private static <T> UnmodListIterator<T> unmodListIterator(ListIterator<T> iter) {
        if (iter == null) { return emptyUnmodListIterator(); }
        if (iter instanceof UnmodListIterator) { return (UnmodListIterator<T>) iter; }
        return new UnmodListIterator<T>() {
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
        };
    }

    private static class UnmodifiableList<T> implements UnmodList<T>, Serializable {
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
            Iterator<T> iter = inner.iterator();
            return new UnmodSortedIterator<T>() {
                @Override public boolean hasNext() { return iter.hasNext(); }
                @Override public T next() { return iter.next(); }
            };
        }
        @Override public String toString() {
            return UnmodIterable.toString("UnmodList", inner);
        }
    }

    // This is a separate class 1. to defend this singleton against serialization/deserialization
    // and I suppose 2. so that you can enforce the singleton property with the type system if you
    // want to.
    private static final class UnmodifiableListEmpty<T> extends UnmodifiableList<T> {
        private static final UnmodifiableListEmpty<?> INSTANCE = new UnmodifiableListEmpty();
        private UnmodifiableListEmpty() { super(Collections.emptyList()); }
        // For serializable.  Make sure to change whenever internal data format changes.
        private static final long serialVersionUID = 20160918033000L;
        // This enforces the singleton property in the face of deserialization.
        private Object readResolve() { return INSTANCE; }
    }

    /** Use {@link #emptyUnmodList()} instead */
    @Deprecated
    public static final UnmodList<?> EMPTY_UNMOD_LIST = UnmodifiableListEmpty.INSTANCE;

    /** Returns a type-aware version of the empty unmodifiable list. */
    @SuppressWarnings("unchecked")
    public static <T> UnmodList<T> emptyUnmodList() {
        return (UnmodList<T>) UnmodifiableListEmpty.INSTANCE;
    }

    /** Returns an unmodifiable version of the given list. */
    public static <T> UnmodList<T> unmodList(List<T> inner) {
        if (inner == null) { return emptyUnmodList(); }
        if (inner instanceof UnmodList) { return (UnmodList<T>) inner; }
        if (inner.size() < 1) { return emptyUnmodList(); }
        return new UnmodifiableList<>(inner);
    }

    // TODO: Make this behave like UnmodList (and empty same)
    public static UnmodSet<Object> EMPTY_UNMOD_SET = new UnmodSet<Object>() {
        @Override public boolean contains(Object o) { return false; }
        @Override public int size() { return 0; }
        @Override public boolean isEmpty() { return true; }
        @Override public UnmodIterator<Object> iterator() { return emptyUnmodIterator(); }
    };
    @SuppressWarnings("unchecked")
    public static <T> UnmodSet<T> emptyUnmodSet() { return (UnmodSet<T>) EMPTY_UNMOD_SET; }

    // TODO: Make this behave like UnmodList (and empty same)
    /** Returns an unmodifiable version of the given set. */
    public static <T> UnmodSet<T> unmodSet(Set<T> set) {
        if (set == null) { return emptyUnmodSet(); }
        if (set instanceof UnmodSet) { return (UnmodSet<T>) set; }
        if (set.size() < 1) { return emptyUnmodSet(); }
        return new UnmodSet<T>() {
            @Override public boolean contains(Object o) { return set.contains(o); }
            @Override public int size() { return set.size(); }
            @Override public boolean isEmpty() { return set.isEmpty(); }
            @Override public UnmodIterator<T> iterator() { return unmodIterator(set.iterator()); }
            @Override public int hashCode() { return set.hashCode(); }
            @SuppressWarnings("EqualsWhichDoesntCheckParameterClass") // See Note above.
            @Override public boolean equals(Object o) { return set.equals(o); }
        };
    }

    public static UnmodSortedIterator<Object> EMPTY_UNMOD_SORTED_ITERATOR =
            new UnmodSortedIterator<Object>() {
        @Override public boolean hasNext() { return false; }
        @Override public Object next() { throw new NoSuchElementException(); }
    };
    @SuppressWarnings("unchecked")
    public static <T> UnmodSortedIterator<T> emptyUnmodSortedIterator() {
        return (UnmodSortedIterator<T>) EMPTY_UNMOD_SORTED_ITERATOR;
    }

    // TODO: Make this behave like UnmodList (and empty same)
    public static UnmodSet<Object> EMPTY_UNMOD_SORTED_SET = new UnmodSortedSet<Object>() {
        @Override public boolean contains(Object o) { return false; }
        @Override public int size() { return 0; }
        @Override public boolean isEmpty() { return true; }
        @Override public UnmodSortedIterator<Object> iterator() {
            return emptyUnmodSortedIterator();
        }
        // Is this implementation a reason not to have an empty sorted set singleton?
        @Override public Comparator<? super Object> comparator() { return null; }
        @Override public UnmodSortedSet<Object> subSet(Object fromElement, Object toElement) {
            return this;
        }
        @Override public UnmodSortedSet<Object> tailSet(Object fromElement) { return this; }
        @Override public Object first() { throw new NoSuchElementException("Empty set"); }
        @Override public Object last() { throw new NoSuchElementException("Empty set"); }
    };
    @SuppressWarnings("unchecked")
    public static <T> UnmodSortedSet<T> emptyUnmodSortedSet() {
        return (UnmodSortedSet<T>) EMPTY_UNMOD_SORTED_SET;
    }

    /** Returns an unmodifiable version of the given set. */
    public static <T> UnmodSortedSet<T> unmodSortedSet(SortedSet<T> set) {
        if (set == null) { return emptyUnmodSortedSet(); }
        if (set instanceof UnmodSortedSet) { return (UnmodSortedSet<T>) set; }
        if (set.size() < 1) { return emptyUnmodSortedSet(); }
        return new UnmodSortedSet<T>() {
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

    // TODO: Make this behave like UnmodList (and empty same)
    public static UnmodMap<Object,Object> EMPTY_UNMOD_MAP = new UnmodMap<Object,Object>() {
        @Override public UnmodSet<Entry<Object,Object>> entrySet() { return emptyUnmodSet(); }
        @Override public UnmodSet<Object> keySet() { return emptyUnmodSet(); }
        @SuppressWarnings("deprecation")
        @Override public UnmodCollection<Object> values() { return emptyUnmodCollection(); }
        @Override public int size() { return 0; }
        @Override public boolean isEmpty() { return true; }
        @Override public UnmodIterator<UnEntry<Object,Object>> iterator() {
            return emptyUnmodIterator();
        }
        @Override public boolean containsKey(Object key) { return false; }
        @Override public boolean containsValue(Object value) { return false; }
        @Override public Object get(Object key) { return null; }
    };
    @SuppressWarnings("unchecked")
    public static <T,U> UnmodMap<T,U> emptyUnmodMap() { return (UnmodMap<T,U>) EMPTY_UNMOD_MAP; }

    // TODO: Make this behave like UnmodList (and empty same)
    /** Returns an unmodifiable version of the given map. */
    public static <K,V> UnmodMap<K,V> unmodMap(Map<K,V> map) {
        if (map == null) { return emptyUnmodMap(); }
        if (map instanceof UnmodMap) { return (UnmodMap<K,V>) map; }
        if (map.size() < 1) { return emptyUnmodMap(); }
        return new UnmodMap<K,V>() {
            /** {@inheritDoc} */
            @Override
            public UnmodIterator<UnEntry<K,V>> iterator() {
                return UnEntry.entryIterToUnEntryUnIter(map.entrySet().iterator());
            }

            @Override public UnmodSet<Entry<K,V>> entrySet() { return unmodSet(map.entrySet()); }
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
        };
    }

    // TODO: Make this behave like UnmodList (and empty same)
    static UnmodSortedMap<Object,Object> EMPTY_UNMOD_SORTED_MAP =
            new UnmodSortedMap<Object,Object>() {
        @Override public UnmodSortedSet<Entry<Object,Object>> entrySet() {
            return emptyUnmodSortedSet();
        }
        @Override public UnmodSortedSet<Object> keySet() { return emptyUnmodSortedSet(); }
        @Override public Comparator<? super Object> comparator() { return null; }
        @Override public UnmodSortedMap<Object,Object> subMap(Object fromKey, Object toKey) {
            return this;
        }
        @Override public UnmodSortedMap<Object,Object> tailMap(Object fromKey) { return this; }
        @Override public Object firstKey() { throw new NoSuchElementException("empty map"); }
        @Override public Object lastKey() { throw new NoSuchElementException("empty map"); }
        // I don't think I should need this suppression because it's not deprecated in
        // UnmodSortedMap.  My IDE doesn't warn me, but Java 1.8.0_60 does.
        @SuppressWarnings("deprecation")
        @Override public UnmodList<Object> values() { return emptyUnmodList(); }
        @Override public int size() { return 0; }
        @Override public boolean isEmpty() { return true; }
        @Override public UnmodSortedIterator<UnEntry<Object,Object>> iterator() {
            return emptyUnmodSortedIterator();
        }
        @Override public boolean containsKey(Object key) { return false; }
        @Override public boolean containsValue(Object value) { return false; }
        @Override public Object get(Object key) { return null; }
    };
    @SuppressWarnings("unchecked")
    static <T,U> UnmodSortedMap<T,U> emptyUnmodSortedMap() {
        return (UnmodSortedMap<T,U>) EMPTY_UNMOD_SORTED_MAP;
    }

    // TODO: Make this behave like UnmodList (and empty same)
    /** Returns an unmodifiable version of the given sorted map. */
    public static <K,V> UnmodSortedMap<K,V> unmodSortedMap(SortedMap<K,V> map) {
        if (map == null) { return emptyUnmodSortedMap(); }
        if (map instanceof UnmodSortedMap) { return (UnmodSortedMap<K,V>) map; }
        if (map.size() < 1) { return emptyUnmodSortedMap(); }
        return new UnmodSortedMap<K,V>() {
//            @Override public UnmodSortedSet<Entry<K,V>> entrySet() {
//                return new UnmodSortedSet<Entry<K,V>>() {
//                    Set<Entry<K,V>> entrySet = map.entrySet();
//                    @Override public UnmodSortedIterator<Entry<K,V>> iterator() {
//                        return new UnmodSortedIterator<Entry<K,V>>() {
//                            Iterator<Entry<K,V>> iter = entrySet.iterator();
//                            @Override public boolean hasNext() { return iter.hasNext(); }
//                            @Override public Entry<K,V> next() { return iter.next(); }
//                        };
//                    }
//                    @Override public UnmodSortedSet<Entry<K,V>> subSet(Entry<K,V> fromElement,
//                                                                       Entry<K,V> toElement) {
//                        // This is recursive.  I hope it's not an infinite loop 'cause I don't want
//                        // to write this all out again.
//                        return unmodSortedMap(map.subMap(fromElement.getKey(), toElement.getKey()))
//                                .entrySet();
//                    }
//
//                    @Override public UnmodSortedSet<Entry<K,V>> tailSet(Entry<K,V> fromElement) {
//                        // This is recursive.  I hope it's not an infinite loop 'cause I don't want
//                        // to write this all out again.
//                        return unmodSortedMap(map.tailMap(fromElement.getKey()))
//                                .entrySet();
//                    }
//
//                    @Override public Comparator<? super Entry<K,V>> comparator() {
//                        return (o1, o2) -> map.comparator().compare(o1.getKey(), o2.getKey());
//                    }
//                    @Override public Entry<K,V> first() {
//                        K key = map.firstKey();
//                        return Tuple2.of(key, map.get(key));
//                    }
//
//                    @Override public Entry<K,V> last() {
//                        K key = map.lastKey();
//                        return Tuple2.of(key, map.get(key));
//                    }
//                    @Override public boolean contains(Object o) { return entrySet.contains(o); }
//                    @Override public boolean isEmpty() { return entrySet.isEmpty(); }
//                    @Override public int size() { return entrySet.size(); }
//                    @Override public int hashCode() { return entrySet.hashCode(); }
//                    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass") // See Note above.
//                    @Override public boolean equals(Object o) { return entrySet.equals(o); }
//                };
//            }
            @Override public int size() { return map.size(); }
            @Override public boolean isEmpty() { return map.isEmpty(); }
            @Override public boolean containsKey(Object key) { return map.containsKey(key); }
            @Override public boolean containsValue(Object value) {
                return map.containsValue(value);
            }
            @Override public V get(Object key) { return map.get(key); }
//            @Override public UnmodSortedSet<K> keySet() { return unmodSet(map.keySet()); }
            @Override public Comparator<? super K> comparator() { return map.comparator(); }
            @Override public UnmodSortedMap<K,V> subMap(K fromKey, K toKey) {
                return unmodSortedMap(map.subMap(fromKey, toKey));
            }
            @Override public UnmodSortedMap<K,V> tailMap(K fromKey) {
                return unmodSortedMap(map.tailMap(fromKey));
            }
            @Override public K firstKey() { return map.firstKey(); }
            @Override public K lastKey() { return map.lastKey(); }
//            @Override public UnmodSortedCollection<V> values() {
//                return unmodSortedCollection(map.values());
//            }
            @Override public UnmodSortedIterator<UnEntry<K,V>> iterator() {
                return new UnmodSortedIterator<UnEntry<K,V>>() {
                    // Could have gone with values() instead.
                    Iterator<Entry<K,V>> iter = map.entrySet().iterator();
                    @Override public boolean hasNext() { return iter.hasNext(); }

                    @Override public UnEntry<K,V> next() {
                        return Tuple2.of(iter.next());
                    }
                };
            }
            @Override public int hashCode() { return map.hashCode(); }
            @SuppressWarnings("EqualsWhichDoesntCheckParameterClass") // See Note above.
            @Override public boolean equals(Object o) { return map.equals(o); }
        };
    }

    // TODO: Make this behave like UnmodList (and empty same)
    public static UnmodCollection<Object> EMPTY_UNMOD_COLLECTION = new UnmodCollection<Object>() {
        @Override public boolean contains(Object o) { return false; }
        @Override public int size() { return 0; }
        @Override public boolean isEmpty() { return true; }
        @Override public UnmodIterator<Object> iterator() { return emptyUnmodIterator(); }
    };

    @SuppressWarnings("unchecked")
    public static <T> UnmodCollection<T> emptyUnmodCollection() {
        return (UnmodCollection<T>) EMPTY_UNMOD_COLLECTION;
    }


    /**
     Returns an unmodifiable version of the given collection.  Collections shouldn't be instantiated
     because they are an abomination from the equals() point of view - neither List nor Set will
     call themselves equal to a Collection.
     */
    @Deprecated
    static <T> UnmodCollection<T> unmodCollection(Collection<T> coll) {
        if (coll == null) { return emptyUnmodCollection(); }
        if (coll instanceof UnmodCollection) { return (UnmodCollection<T>) coll; }
        if (coll.size() < 1) { return emptyUnmodCollection(); }
        return new UnmodCollection<T>() {
            @Override public boolean contains(Object o) { return coll.contains(o); }
            @Override public int size() { return coll.size(); }
            @Override public boolean isEmpty() { return coll.isEmpty(); }
            @Override public UnmodIterator<T> iterator() { return unmodIterator(coll.iterator()); }
            @Override public int hashCode() { return coll.hashCode(); }
            @SuppressWarnings("EqualsWhichDoesntCheckParameterClass") // See Note above.
            @Override public boolean equals(Object o) { return coll.equals(o); }
        };
    }

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
