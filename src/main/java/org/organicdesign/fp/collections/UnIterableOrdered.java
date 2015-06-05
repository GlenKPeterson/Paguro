package org.organicdesign.fp.collections;

import java.util.Iterator;
import java.util.Objects;

/**
 An unmodifiable Iterable, with guaranteed order.  The signature of this interface is nearly identical to UnIterable,
 but implementing this interface represents a contract to always return iterators that have the same ordering.
 */
public interface UnIterableOrdered<T> extends UnIterable<T> {
    // ==================================================== Static ====================================================
    /** This is correct, but O(n) */
    static int hashCode(Iterable is) {
        if (is == null) { throw new IllegalArgumentException("Can't have a null iteratable."); }
//        System.out.println("hashCode for: " + is);
        int ret = 0;
        for (Object t : is) {
            if (t != null) {
//                System.out.println("\tt: " + t + " hashCode: " + t.hashCode());
                ret = ret + t.hashCode();
            }
        }
        return ret;
    }

    /** This is correct, but O(n) */
    static boolean equals(Iterable a, Iterable b) {
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

    /** Computes a reasonable to-string. */
    static String toString(String name, Iterable iterable) {
        if (name == null) { throw new IllegalArgumentException("Can't have a null name."); }
        if (iterable == null) { throw new IllegalArgumentException("Can't have a null iteratable."); }
        StringBuilder sB = new StringBuilder();
        sB.append(name).append("(");
        int i = 0;
        Iterator iter = iterable.iterator();
        while (iter.hasNext()) {
            Object item = iter.next();
            if (i > 0) { sB.append(","); }
            if (i > 4) { break; }
            sB.append(item);
            i++;
        }
        if (iter.hasNext()) {
            sB.append("...");
        }
        return sB.append(")").toString();
    }

    // =================================================== Instance ===================================================
    /** Returns items in a guaranteed order. */
    @Override UnIteratorOrdered<T> iterator();
}
