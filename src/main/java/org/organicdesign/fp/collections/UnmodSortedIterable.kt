package org.organicdesign.fp.collections

import java.io.Serializable
import java.util.SortedMap
import java.util.SortedSet

/**
 * An unmodifiable Iterable, with guaranteed order.  The signature of this interface is nearly
 * identical to UnmodIterable, but implementing this interface represents a contract to always return
 * iterators that have the same ordering.
 */
interface UnmodSortedIterable<T> : UnmodIterable<T> {

    // ========================================= Instance =========================================
    /** Returns items in a guaranteed order.  */
    @JvmDefault
    override fun iterator(): UnmodSortedIterator<T>

    companion object {

        /** This is correct, but O(n).  This only works with an ordered iterable.  */
        @JvmStatic
        fun equal(c: UnmodSortedIterable<*>?, b: UnmodSortedIterable<*>?): Boolean {
            // Cheapest operation first...
            if (c === b) {
                return true
            }

            if (c == null || b == null) {
                return false
            }
            val cs = c.iterator()
            val bs = b.iterator()
            while (cs.hasNext() && bs.hasNext()) {
                if (cs.next() != bs.next()) {
                    return false
                }
            }
            return !cs.hasNext() && !bs.hasNext()
        }

        //    static <E> UnmodSortedIterable<E> castFromSortedSet(SortedSet<E> ss) {
        //        return () -> new UnmodSortedIterator<E>() {
        //            Iterator<E> iter = ss.iterator();
        //            @Override public boolean hasNext() { return iter.hasNext(); }
        //            @Override public E next() { return iter.next(); }
        //        };
        //    }

        @JvmStatic
        fun <E> castFromSortedSet(s: SortedSet<E>): UnmodSortedIterable<E> {
            class Implementation<S>(private val ss: SortedSet<S>) : UnmodSortedIterable<S>,
                                                                    Serializable {

                /** Returns items in a guaranteed order.  */
                override fun iterator(): UnmodSortedIterator<S> {
                    return UnmodSortedIterator.Companion.Wrapper(ss.iterator())
                }

//                companion object {
//                    // For serializable.  Make sure to change whenever internal data format changes.
//                    private const val serialVersionUID = 20160903174100L
//                }
            }
            return Implementation(s)
        }

        //    static <E> UnmodSortedIterable<E> castFromList(List<E> ss) {
        //        return () -> new UnmodSortedIterator<E>() {
        //            Iterator<E> iter = ss.iterator();
        //            @Override public boolean hasNext() { return iter.hasNext(); }
        //            @Override public E next() { return iter.next(); }
        //        };
        //    }

        @JvmStatic
        fun <E> castFromList(s: List<E>): UnmodSortedIterable<E> {
            class Implementation<S>(private val ss: List<S>) : UnmodSortedIterable<S>,
                                                               Serializable {

                /** Returns items in a guaranteed order.  */
                override fun iterator(): UnmodSortedIterator<S> {
                    return UnmodSortedIterator.Companion.Wrapper(ss.iterator())
                }

//                companion object {
//                    // For serializable.  Make sure to change whenever internal data format changes.
//                    private const val serialVersionUID = 20160903174100L
//                }
            }
            return Implementation(s)
        }

        //    static <U> UnmodSortedIterable<U> castFromTypedList(List<U> ss) {
        //        return () -> new UnmodSortedIterator<U>() {
        //            Iterator<U> iter = ss.iterator();
        //            @Override public boolean hasNext() { return iter.hasNext(); }
        //            @Override public U next() { return iter.next(); }
        //        };
        //    }
        //
        //    static <U> UnmodSortedIterable<U> castFromCollection(Collection<U> ss) {
        //        return () -> new UnmodSortedIterator<U>() {
        //            Iterator<U> iter = ss.iterator();
        //            @Override public boolean hasNext() { return iter.hasNext(); }
        //            @Override public U next() { return iter.next(); }
        //        };
        //    }

        //    static <K,V> UnmodSortedIterable<Map.Entry<K,V>> castFromSortedMap(SortedMap<K,V> sm) {
        //        return () -> new UnmodSortedIterator<Map.Entry<K,V>>() {
        //            Iterator<Map.Entry<K,V>> iter = sm.entrySet().iterator();
        //            @Override public boolean hasNext() { return iter.hasNext(); }
        //            @Override public Map.Entry<K,V> next() { return iter.next(); }
        //        };
        //    }

        @JvmStatic
        fun <K, V> castFromSortedMap(sm: SortedMap<K, V>): UnmodSortedIterable<UnmodMap.UnEntry<K, V>> {
            if (sm is UnmodSortedMap<*, *>) {
                return sm as UnmodSortedMap<K, V>
            }

            class Implementation<K1, V1>(private val m: SortedMap<K1, V1>) :
                    UnmodSortedIterable<UnmodMap.UnEntry<K1, V1>>, Serializable {

                /** Returns items in a guaranteed order.  */
                override fun iterator(): UnmodSortedIterator<UnmodMap.UnEntry<K1, V1>> {
                    return UnmodMap.UnEntry.EntryToUnEntrySortedIter(m.entries.iterator())
                }

//                companion object {
//                    // For serializable.  Make sure to change whenever internal data format changes.
//                    private const val serialVersionUID = 20160903174100L
//                }
            }
            return Implementation(sm)
        }
    }
}
