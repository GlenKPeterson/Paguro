package org.organicdesign.fp.experiments.collections;

/**
 * Holds "mutation" methods that return a new ImList reflecting the modification while sharing as much with the previous
 * ImList as possible.
 */
public interface ImList<E> extends UnList<E> {
// Do we want to make an ImIterator thatis truly immutable - a Sequence?
//    /** {@inheritDoc} */
//    @Override default UnIterator<E> iterator() { return listIterator(0); }
//
//    /** {@inheritDoc} */
//    @Override default UnListIterator<E> listIterator() { return listIterator(0); }

// Inherited correctly - there is no ImIterator.
// UnListIterator<E> listIterator(int index) {

// Inherited correctly and need to be implemented by the implementing class
// int size() {
// boolean equals(Object o) {
// int hashCode() {
// E get(int index) {

    /**
     * Inserts a new item at the specified index.
     * @param i the zero-based index to insert at (pushes current item and all subsequent items up)
     * @param val the value to insert
     * @return a new Vecsicle with the additional item.
     */
    ImList<E> insert(int i, E val);

    /**
     * Adds items to the end of the ImList.
     * @param es the values to insert
     * @return a new ImList with the additional items at the end.
     */
    default ImList<E> append(E... es) {
        if ( (es == null) || (es.length < 1) ) { return this; }
        ImList<E> l = this;
        for (E e : es) {
            l = l.insert(l.size() - 1, e);
        }
        return l;
    }

    /**
     * Adds items to the end of the ImList.
     * @param es the values to insert
     * @return a new ImList with the additional items at the end.
     */
    default ImList<E> appendSkipNull(E... es) {
        if ( (es == null) || (es.length < 1) ) { return this; }
        ImList<E> l = this;
        for (E e : es) {
            if (e != null) {
                l = l.insert(l.size() - 1, e);
            }
        }
        return l;
    }

    /**
     * Returns the item at this index, but takes any Number as an argument.
     * @param n the zero-based index to get from the vector.
     * @return the value at that index.
     */
    default E get(Number n) { return get(n.intValue()); }

    /**
     * Returns the item at this index.
     * @param i the zero-based index to get from the vector.
     * @param notFound the value to return if the index is out of bounds.
     * @return the value at that index, or the notFound value.
     */
    default E get(int i, E notFound) {
        if (i >= 0 && i < size())
            return get(i);
        return notFound;
    }

    /**
     * Inserts items at the beginning of the ImList.
     * @param es the values to insert
     * @return a new ImList beginning with the additional items.
     */
    default ImList<E> prepend(E... es) {
        if ( (es == null) || (es.length < 1) ) { return this; }
        ImList<E> l = this;
        for (E e : es) {
            l = l.insert(0, e);
        }
        return l;
    }

    /**
     * Inserts items at the beginning of the ImList.
     * @param es the values to insert
     * @return a new ImList beginning with the additional items.
     */
    default ImList<E> prependSkipNull(E... es) {
        if ( (es == null) || (es.length < 1) ) { return this; }
        ImList<E> l = this;
        for (E e : es) {
            if (e != null) {
                l = l.insert(0, e);
            }
        }
        return l;
    }
}
