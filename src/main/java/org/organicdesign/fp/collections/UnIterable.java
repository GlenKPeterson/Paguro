package org.organicdesign.fp.collections;

import java.util.Objects;

/** An unmodifiable Iterable */
public interface UnIterable<T> extends Iterable<T> {
    /** {@inheritDoc} */
    @Override UnIterator<T> iterator();

    // ==================================================== Static ====================================================
    /** This is correct, but O(n) */
    static int hashCode(UnIterable is) {
        int ret = 0;
        for (Object t : is) {
            if (t != null) {
                ret = ret + t.hashCode();
            }
        }
        return ret;
    }

    /** This is correct, but O(n) */
    static boolean equals(UnIterable a, UnIterable b) {
        // Cheapest operation first...
        if (a == b) { return true; }

        if ((a == null) || (b == null)) {
            return false;
        }
        UnIterator as = a.iterator();
        UnIterator bs = b.iterator();
        while (as.hasNext() && bs.hasNext()) {
            if (!Objects.equals(as.next(), bs.next())) {
                return false;
            }
        }
        return !as.hasNext() && !bs.hasNext();
    }

//    /** Lets underlying compareTo method handle comparing nulls to non-null values. */
//    static <E extends Comparable<E>> int compareHelper(E e1, E e2) {
//        if (e1 == e2) { return 0; }
//        if (e1 == null) {
//            //noinspection ConstantConditions
//            return -e2.compareTo(e1);
//        }
//        return e1.compareTo(e2);
//    }
//
//    /** A default comparator for UnIterables comparable */
//    static <F extends Comparable<F>,E extends UnIterable<F>> Comparator<E> iterableComparator() {
//        return new Comparator<E>() {
//            @Override
//            public int compare(E o1, E o2) {
//                if (o1 == null) {
//                    if (o2 == null) {
//                        return 0;
//                    } else {
//                        //noinspection ConstantConditions
//                        return -compare(o2, o1);
//                    }
//                }
//                UnIterator<F> as = o1.iterator();
//                UnIterator<F> bs = o2.iterator();
//                while (as.hasNext() && bs.hasNext()) {
//                    int ret = compareHelper(as.next(), bs.next());
//                    if (ret != 0) {
//                        return ret;
//                    }
//                }
//                // If we run out of items in one, the longer one is considered greater, just like ordering words in a
//                // dictionary.
//                if (as.hasNext()) { return -1; }
//                if (bs.hasNext()) { return 1; }
//                // All items compare 0 and same number of items - these are sorted the same (and probably equal)
//                return 0;
//            }
//        };
//    }
}
