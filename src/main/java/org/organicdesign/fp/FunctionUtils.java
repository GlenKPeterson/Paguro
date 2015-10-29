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
 A dumping ground for utility functions that aren't useful enough to belong in StaticImports.

 The unmod___() methods are an alternative to Collections.unmodifiable____() for building immutable
 collections.  These will never return null, the closest they get is to return an empty immutable
 collection (the same one is reused).  Also, the unmodifiable interfaces they return have deprecated
 the modification methods so that any attempt to use those methods causes a warning in your IDE and
 compiler.
 */
public class FunctionUtils {

    // I don't want any instances of this class.
    private FunctionUtils() {}


    /** Returns a String showing the type and first few elements of a map */
    public static <A,B> String toString(Map<A,B> map) {
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
    public static String toString(Object[] as) {
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

    /** Returns an unmodifiable version of the given iterable. */
    // TODO: Test this.
    public static <T> UnmodIterable<T> unmodIterable(Iterable<T> iterable) {
        if (iterable == null) { return () -> UnmodIterator.empty(); }
        if (iterable instanceof UnmodIterable) { return (UnmodIterable<T>) iterable; }
        return () -> new UnmodIterator<T>() {
            private final Iterator<T> iter = iterable.iterator();
            @Override public boolean hasNext() { return iter.hasNext(); }
            @Override public T next() { return iter.next(); }
            // Defining equals and hashcode makes no sense because can't call them without changing
            // the iterator which both makes it useless, and changes the equals and hashcode
            // results.
//            @Override public int hashCode() { return iter.hashCode(); }
//            @SuppressWarnings("EqualsWhichDoesntCheckParameterClass") // See Note above.
//            @Override public boolean equals(Object o) { return iter.equals(o); }
        };
    }

    /** Returns an unmodifiable version of the given iterator. */
    // Never make this public.  We can't trust an iterator that we didn't get
    // brand new ourselves, because iterators are inherently unsafe to share.
    private static <T> UnmodIterator<T> unmodIterator(Iterator<T> iter) {
        if (iter == null) { return UnmodIterator.empty(); }
        if (iter instanceof UnmodIterator) { return (UnmodIterator<T>) iter; }
        return new UnmodIterator<T>() {
            @Override public boolean hasNext() { return iter.hasNext(); }
            @Override public T next() { return iter.next(); }
            // Defining equals and hashcode makes no sense because can't call them without changing
            // the iterator which both makes it useless, and changes the equals and hashcode
            // results.
//            @Override public int hashCode() { return iter.hashCode(); }
//            @SuppressWarnings("EqualsWhichDoesntCheckParameterClass") // See Note above.
//            @Override public boolean equals(Object o) { return iter.equals(o); }
        };
    }

    /**
     Returns an unmodifiable version of the given listIterator.  This is private because sharing
     iterators is bad.
     */
    private static <T> UnmodListIterator<T> unmodListIterator(ListIterator<T> iter) {
        if (iter == null) { return UnmodListIterator.empty(); }
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

    /** Returns an unmodifiable version of the given list. */
    public static <T> UnmodList<T> unmodList(List<T> inner) {
        if (inner == null) { return UnmodList.empty(); }
        if (inner instanceof UnmodList) { return (UnmodList<T>) inner; }
        if (inner.size() < 1) { return UnmodList.empty(); }
        return new UnmodList<T>() {
            @Override public int size() { return inner.size(); }
            @Override public T get(int index) { return inner.get(index); }
            @Override public int hashCode() { return inner.hashCode(); }
            @SuppressWarnings("EqualsWhichDoesntCheckParameterClass") // See Note above.
            @Override public boolean equals(Object o) { return inner.equals(o); }
        };
    }

    /** Returns an unmodifiable version of the given set. */
    public static <T> UnmodSet<T> unmodSet(Set<T> set) {
        if (set == null) { return UnmodSet.empty(); }
        if (set instanceof UnmodSet) { return (UnmodSet<T>) set; }
        if (set.size() < 1) { return UnmodSet.empty(); }
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

    /** Returns an unmodifiable version of the given set. */
    public static <T> UnmodSortedSet<T> unmodSortedSet(SortedSet<T> set) {
        if (set == null) { return UnmodSortedSet.empty(); }
        if (set instanceof UnmodSortedSet) { return (UnmodSortedSet<T>) set; }
        if (set.size() < 1) { return UnmodSortedSet.empty(); }
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

    /** Returns an unmodifiable version of the given map. */
    public static <K,V> UnmodMap<K,V> unmodMap(Map<K,V> map) {
        if (map == null) { return UnmodMap.empty(); }
        if (map instanceof UnmodMap) { return (UnmodMap<K,V>) map; }
        if (map.size() < 1) { return UnmodMap.empty(); }
        return new UnmodMap<K,V>() {
            /** {@inheritDoc} */
            @Override
            public UnmodIterator<UnEntry<K,V>> iterator() {
                return UnEntry.wrap(map.entrySet().iterator());
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
            @Override public UnmodCollection<V> values() { return unmodCollection(map.values()); }
            @Override public int hashCode() { return map.hashCode(); }
            @SuppressWarnings("EqualsWhichDoesntCheckParameterClass") // See Note above.
            @Override public boolean equals(Object o) { return map.equals(o); }
        };
    }

    /** Returns an unmodifiable version of the given sorted map. */
    public static <K,V> UnmodSortedMap<K,V> unmodSortedMap(SortedMap<K,V> map) {
        if (map == null) { return UnmodSortedMap.empty(); }
        if (map instanceof UnmodSortedMap) { return (UnmodSortedMap<K,V>) map; }
        if (map.size() < 1) { return UnmodSortedMap.empty(); }
        return new UnmodSortedMap<K,V>() {
            // TODO: Test this.
            @Override public UnmodSortedSet<Entry<K,V>> entrySet() {
                return new UnmodSortedSet<Entry<K,V>>() {
                    Set<Entry<K,V>> entrySet = map.entrySet();
                    @Override public UnmodSortedIterator<Entry<K,V>> iterator() {
                        return new UnmodSortedIterator<Entry<K,V>>() {
                            Iterator<Entry<K,V>> iter = entrySet.iterator();
                            @Override public boolean hasNext() { return iter.hasNext(); }
                            @Override public Entry<K,V> next() { return iter.next(); }
                        };
                    }
                    @Override public UnmodSortedSet<Entry<K,V>> subSet(Entry<K,V> fromElement,
                                                                       Entry<K,V> toElement) {
                        // This is recursive.  I hope it's not an infinite loop 'cause I don't want
                        // to write this all out again.
                        return unmodSortedMap(map.subMap(fromElement.getKey(), toElement.getKey()))
                                .entrySet();
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
            @Override public UnmodSet<K> keySet() { return unmodSet(map.keySet()); }
            @Override public Comparator<? super K> comparator() { return map.comparator(); }
            @Override public UnmodSortedMap<K,V> subMap(K fromKey, K toKey) {
                return unmodSortedMap(map.subMap(fromKey, toKey));
            }
            @Override public UnmodSortedMap<K,V> tailMap(K fromKey) {
                return unmodSortedMap(map.tailMap(fromKey));
            }
            @Override public K firstKey() { return map.firstKey(); }
            @Override public K lastKey() { return map.lastKey(); }
            @Override public UnmodCollection<V> values() { return unmodCollection(map.values()); }
            @Override public int hashCode() { return map.hashCode(); }
            @SuppressWarnings("EqualsWhichDoesntCheckParameterClass") // See Note above.
            @Override public boolean equals(Object o) { return map.equals(o); }
        };
    }

    /** Returns an unmodifiable version of the given collection. */
    public static <T> UnmodCollection<T> unmodCollection(Collection<T> coll) {
        if (coll == null) { return UnmodCollection.empty(); }
        if (coll instanceof UnmodCollection) { return (UnmodCollection<T>) coll; }
        if (coll.size() < 1) { return UnmodCollection.empty(); }
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
