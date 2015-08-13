package org.organicdesign.fp.collections;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.SortedMap;
import java.util.SortedSet;

/**
 An unmodifiable Iterable, with guaranteed order.  The signature of this interface is nearly identical to UnmodIterable,
 but implementing this interface represents a contract to always return iterators that have the same ordering.
 */
public interface UnmodSortedIterable<T> extends UnmodIterable<T> {
    // ==================================================== Static ====================================================

    /** This is correct, but O(n).  This only works with an ordered iterable. */
    static boolean equals(UnmodSortedIterable a, UnmodSortedIterable b) {
        // Cheapest operation first...
        if (a == b) { return true; }

        if ((a == null) || (b == null)) {
            return false;
        }
        Iterator as = a.iterator();
        Iterator bs = b.iterator();
        while (as.hasNext() && bs.hasNext()) {
            if (!Objects.equals(as.next(), bs.next())) {
                return false;
            }
        }
        return !as.hasNext() && !bs.hasNext();
    }

    //    static <E> UnmodSortedIterable<E> castFromSortedSet(SortedSet<E> ss) {
//        return () -> new UnmodSortedIterator<E>() {
//            Iterator<E> iter = ss.iterator();
//            @Override public boolean hasNext() { return iter.hasNext(); }
//            @Override public E next() { return iter.next(); }
//        };
//    }

    static UnmodSortedIterable castFromSortedSet(SortedSet ss) {
        return () -> new UnmodSortedIterator() {
            Iterator iter = ss.iterator();
            @Override public boolean hasNext() { return iter.hasNext(); }
            @Override public Object next() { return iter.next(); }
        };
    }

//    static <E> UnmodSortedIterable<E> castFromList(List<E> ss) {
//        return () -> new UnmodSortedIterator<E>() {
//            Iterator<E> iter = ss.iterator();
//            @Override public boolean hasNext() { return iter.hasNext(); }
//            @Override public E next() { return iter.next(); }
//        };
//    }

    static UnmodSortedIterable castFromList(List ss) {
        return () -> new UnmodSortedIterator() {
            Iterator iter = ss.iterator();
            @Override public boolean hasNext() { return iter.hasNext(); }
            @Override public Object next() { return iter.next(); }
        };
    }

//    static <K,V> UnmodSortedIterable<Map.Entry<K,V>> castFromSortedMap(SortedMap<K,V> sm) {
//        return () -> new UnmodSortedIterator<Map.Entry<K,V>>() {
//            Iterator<Map.Entry<K,V>> iter = sm.entrySet().iterator();
//            @Override public boolean hasNext() { return iter.hasNext(); }
//            @Override public Map.Entry<K,V> next() { return iter.next(); }
//        };
//    }

    static UnmodSortedIterable castFromSortedMap(SortedMap sm) {
        return () -> new UnmodSortedIterator() {
            Iterator iter = sm.entrySet().iterator();
            @Override public boolean hasNext() { return iter.hasNext(); }
            @Override public Object next() { return iter.next(); }
        };
    }

    // =================================================== Instance ===================================================
    /** Returns items in a guaranteed order. */
    @Override
    UnmodSortedIterator<T> iterator();
}
