package org.organicdesign.fp.collections;

import java.util.Set;

/**
 Implements equals and hashCode() methods compatible with java.util.Set (which ignores order)
 to make defining unmod sets easier, especially for implementing Map.keySet() and such.
 */
public abstract class AbstractUnmodSet<T> extends AbstractUnmodIterable<T> implements UnmodSet<T> {
    @Override public boolean equals(Object other) {
        if (this == other) { return true; }
        if ( !(other instanceof Set) ) { return false; }
        Set that = (Set) other;
        return (size() == that.size()) &&
               containsAll(that);
    }
}
