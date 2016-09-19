package org.organicdesign.fp.collections;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import static org.organicdesign.fp.FunctionUtils.stringify;

public class ImInsertOrderSet2<E> implements ImSet<E>, Serializable {

    // For serializable.  Make sure to change whenever internal data format changes.
    private static final long serialVersionUID = 20160915083900L;

    private final ImInsertOrderMap2<E,?> inner;

    private ImInsertOrderSet2(ImInsertOrderMap2<E,?> m) { inner = m; }

    private static final ImInsertOrderSet2 EMPTY = new ImInsertOrderSet2<>(ImInsertOrderMap2.empty());

    @SuppressWarnings("unchecked")
    public static <E> ImInsertOrderSet2<E> empty() { return EMPTY; }

    /** To keep constructor private. */
    static <E> ImInsertOrderSet2<E> ofMap(ImInsertOrderMap2<E,?> map) {
        return new ImInsertOrderSet2<>(map);
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    @Override public boolean contains(Object o) { return inner.containsKey(o); }

    @Override public UnmodSortedIterator<E> iterator() {
        return inner.map(Map.Entry::getKey)
                    .toImList().iterator();
    }

    @SuppressWarnings("unchecked")
    @Override public ImInsertOrderSet2<E> put(E e) {
        return new ImInsertOrderSet2<>(((ImInsertOrderMap2<E,E>) inner).assoc(e, e));
    }

    @Override public int size() { return inner.size(); }

    @Override public ImInsertOrderSet2<E> without(E key) {
        return new ImInsertOrderSet2<>(inner.without(key));
    }

    /**
     This is compatible with java.util.Map but that means it wrongly allows comparisons with SortedMaps, which are
     necessarily not commutative.
     @param other the other (hopefully unsorted) map to compare to.
     @return true if these maps contain the same elements, regardless of order.
     */
    @Override public boolean equals(Object other) {
        if (other == this) { return true; }
        if ( !(other instanceof Set) ) { return false; }
        Set that = (Set) other;
        if (that.size() != size()) { return false; }
        return containsAll(that);
    }

    @Override public int hashCode() { return UnmodIterable.hash(this); }

    @Override public String toString() {
        return inner.foldLeft(new StringBuilder("ImInsertOrderSet2("),
                              (sB, entry) -> sB.append(stringify(entry.getKey())))
                    .append(")")
                    .toString();
    }
}
