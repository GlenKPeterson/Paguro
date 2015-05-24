package org.organicdesign.fp.experiments;

import java.util.Comparator;

/**
 An Equator represents an equality context in a way that is analgous to the java.util.Comparator interface.
 <a href="http://glenpeterson.blogspot.com/2013/09/object-equality-is-context-relative.html" target="_blank">Comparing Objects is Relative</a>
 This will need to be passed to Hash-based collections the way a Comparator is passed to tree-based ones.
 */
public interface Equator<T> {

    // ==================================================== Static ====================================================

    Equator<Object> DEFAULT_EQUATOR = new Equator<Object>() {
        @Override public int hash(Object o) { return (o == null) ? 0 : o.hashCode(); }
        @Override public boolean equalTo(Object o1, Object o2) {
            if (o1 == null) { return (o2 == null); }
            return o1.equals(o2);
        }
    };

    @SuppressWarnings("unchecked")
    static <T> Equator<T> defaultEquator() { return (Equator<T>) DEFAULT_EQUATOR; }

    /**
     Implement compare() and hash() and you get equalTo() for free.
    */
    interface ComparisonContext<T> extends Equator<T>, Comparator<T> {
        /** Returns true if the first object is less than the second. */
        default boolean lt(T o1, T o2) { return compare(o1, o2) < 0; }

        /** Returns true if the first object is less than or equal to the second. */
        default boolean lte(T o1, T o2) { return compare(o1, o2) <= 0; }

        /** Returns true if the first object is greater than the second. */
        default boolean gt(T o1, T o2) { return compare(o1, o2) > 0; }

        /** Returns true if the first object is greater than or equal to the second. */
        default boolean gte(T o1, T o2) { return compare(o1, o2) >= 0; }

        @Override default boolean equalTo(T o1, T o2) { return compare(o1, o2) == 0; }

        ComparisonContext<Comparable<Object>> DEFAULT_CONTEXT = new ComparisonContext<Comparable<Object>>() {
            @Override public int hash(Comparable<Object> o) { return (o == null) ? 0 : o.hashCode(); }
            @SuppressWarnings("ConstantConditions")
            @Override public int compare(Comparable<Object> o1, Comparable<Object> o2) {
                if (o1 == null) {
                    if (o2 == null) { return 0; }
                    return o2.compareTo(o1);
                }
                return o1.compareTo(o2);
            }
        };

        @SuppressWarnings("unchecked")
        static <T> ComparisonContext<T> defaultComparisonContext() {
            return (ComparisonContext<T>) DEFAULT_CONTEXT;
        }
    }

    // =================================================== Instance ===================================================
    /**
     An integer digest used for very quick "can-equal" testing.
     This method MUST return equal hash codes for equal objects.
     It should USUALLY return unequal hash codes for unequal objects.
     You should not change mutable objects while you rely on their hash codes.
     That said, if a mutable object's internal state changes, the hash code generally must change to reflect the new state.
     */
    int hash(T t);

    /**
     Determines whether two objects are equal.
     @return true if this Equator considers the two objects to be equal.
     */
    boolean equalTo(T o1, T o2);

}
