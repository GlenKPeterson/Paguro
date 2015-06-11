/**
 Copyright (c) Rich Hickey. All rights reserved. The use and distribution terms for this software are covered by the
 Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php) which can be found in the file epl-v10.html
 at the root of this distribution. By using this software in any fashion, you are agreeing to be bound by the terms of
 this license. You must not remove this notice, or any other, from this software.
 */

/* rich Mar 3, 2008 */

package org.organicdesign.fp.collections;

import org.organicdesign.fp.Option;
import org.organicdesign.fp.permanent.Sequence;

import java.util.Comparator;
import java.util.SortedSet;

/**
 A wrapper that turns a PersistentTreeMap into a set.
 */
public class PersistentTreeSet<E> implements ImSetOrdered<E> {

    /**
     Be extremely careful with this because it uses the default comparator, which only works for items that implement
     Comparable (have a "natural ordering").  An attempt to use it with other items will blow up at runtime.  Either
     a withComparator() method will be added, or this will be removed.
     */
    // TODO: Should really require a comparator.
    @SuppressWarnings("unchecked")
    static public final PersistentTreeSet EMPTY = new PersistentTreeSet(PersistentTreeMap.EMPTY);

    /**
     Be extremely careful with this because it uses the default comparator, which only works for items that implement
     Comparable (have a "natural ordering").  An attempt to use it with other items will blow up at runtime.  Either
     a withComparator() method will be added, or this will be removed.
     */
    @SuppressWarnings("unchecked")
    static public <T extends Comparable<T>> PersistentTreeSet<T> empty() { return EMPTY; }

    private final ImMapOrdered<E,?> impl;

//    static public <T> PersistentTreeSet<T> create(ISeq<T> items) {
//        PersistentTreeSet<T> ret = emptyTreeSet();
//        for (; items != null; items = items.next()) {
//            ret = ret.cons(items.head());
//        }
//        return ret;
//    }
//
//    static public <T> PersistentTreeSet<T> create(Comparator<? super T> comp, ISeq<T> items) {
//        PersistentTreeSet<T> ret = new PersistentTreeSet<>(null, new PersistentTreeMap<>(null, comp));
//        for (; items != null; items = items.next()) {
//            ret = ret.cons(items.head());
//        }
//        return ret;
//    }

    private PersistentTreeSet(ImMapOrdered<E,?> i) { impl = i; }

    /**
     Returns a new PersistentTreeSet of the given comparator.  Always use this instead of starting with empty() because
     there is no way to assign a comparator later on.
     */
    public static <T> PersistentTreeSet<T> ofComp(Comparator<? super T> comp) {
        return new PersistentTreeSet<>(PersistentTreeMap.empty(comp));
    }

    /** Returns a new PersistentTreeSet of the given comparator and items. */
    @SafeVarargs
    public static <T> PersistentTreeSet<T> ofComp(Comparator<? super T> comp, T... items) {
        PersistentTreeSet<T> ret = new PersistentTreeSet<>(PersistentTreeMap.empty(comp));
        if ( (items == null) || (items.length < 1) ) { return ret; }
        for (T item : items) {
            ret = ret.put(item);
        }
        return ret;
    }

    /** Returns a new PersistentTreeSet of the given comparable items. */
    @SafeVarargs
    public static <T extends Comparable<T>> PersistentTreeSet<T> of(T... items) {
        // empty() uses default comparator
        if ( (items == null) || (items.length < 1) ) { return empty(); }
        PersistentTreeSet<T> ret = empty();
        for (T item : items) {
            ret = ret.put(item);
        }
        return ret;
    }

    /**
     Returns a new PersistentTreeSet of the keys and comparator in the given map.  Since PersistentTreeSet is just a
     wrapper for a PersistentTreeMap, this can be a very cheap operation.
     */
    public static <T> PersistentTreeSet<T> ofMap(ImMapOrdered<T,?> i) { return new PersistentTreeSet<>(i); }

    /**
     Returns the comparator used to order the items in this set, or null if it uses Function2.DEFAULT_COMPARATOR
     (for compatibility with java.util.SortedSet).
     */
    @Override public Comparator<? super E> comparator() { return impl.comparator(); }

    /** Returns true if the set contains the given item in O(log n) time.  This is the defining method of a set. */
    @SuppressWarnings("SuspiciousMethodCalls")
    @Override public boolean contains(Object o) { return impl.containsKey(o); }

    /** {@inheritDoc} */
    @Override public PersistentTreeSet<E> disjoin(E key) {
        return (impl.containsKey(key)) ? new PersistentTreeSet<>(impl.without(key))
                                       : this;
    }

    /** {@inheritDoc} */
    @Override
    public UnIteratorOrdered<E> iterator() {
        return new UnIteratorOrdered<E>() {
            UnIteratorOrdered<? extends UnMap.UnEntry<E,?>> iter = impl.iterator();
            @Override public boolean hasNext() { return iter.hasNext(); }
            @Override public E next() {
                UnMap.UnEntry<E,?> e = iter.next();
                return e == null ? null : e.getKey();
            }
        };
    }

    /**
     This is designed to be correct, rather than fully compatible with TreeSet.equals().
     TreeSet.equals() does not take ordering into account and this does.

     You want better equality?  Define an Equator.  This is for Java@trade; interop.
     */
    @Override public boolean equals(Object other) {
        if (this == other) { return true; }
        if ( !(other instanceof SortedSet) ) { return false; }
        SortedSet that = ((SortedSet) other);

        if (size() != that.size()) { return false; }
        return UnIterableOrdered.equals(this, UnIterableOrdered.cast(that));
    }

    /**
     Use head() inherited from Sequence instead of this method which is inherited from SortedSet.  This method
     returns the first element if it exists, or throws a NoSuchElementException if the set is empty.

     head() returns an Option of the first element where as this method throws an exception if this set is empty.
     I had to rename the method on Sequence from first() to head() to work around this.  Also returning an Option is
     thread-safe for building a lazy sequence.  The alternative, examining the rest() of a sequence to see if it's
     == Sequence.empty() gets ugly very quickly and makes many transformations eager (especially flatMap).
     */
    @Override public E first() { return impl.firstKey(); }

    /** {@inheritDoc} */
    @Override public int hashCode() { return (size() == 0) ? 0 : UnIterable.hashCode(this); }

    /** {@inheritDoc} */
    @Override public Option<E> head() { return size() > 0 ? Option.of(impl.firstKey()) : Option.none(); }

    /** {@inheritDoc} */
    @Override public boolean isEmpty() { return impl.isEmpty(); }

    /**
     Inherited from SortedSet, returns the last item in this set, or throw an exception if this set is empty.
     Good luck with that.
     */
    @Override public E last() { return impl.lastKey(); }

    /** {@inheritDoc} */
    @Override public PersistentTreeSet<E> put(E e) {
        return (impl.containsKey(e)) ? this
                                     : new PersistentTreeSet<>(impl.assoc(e, null));
    }

    /** The size of this set. */
    @Override public int size() { return impl.size(); }

    /** {@inheritDoc} */
    @Override public ImSetOrdered<E> subSet(E fromElement, E toElement) {
        return PersistentTreeSet.ofMap(impl.subMap(fromElement, toElement));
    }

    // TODO: Ensure that KeySet is sorted.
    /** {@inheritDoc} */
    @Override public Sequence<E> tail() { return impl.without(first()).keySet().seq(); }

    /** Returns a string representation of this set. */
    @Override public String toString() { return UnIterable.toString("PersistentTreeSet", this); }

//    @Override
//    public ISeq<E> rseq() {
//        return APersistentMap.KeySeq.create(((Reversible<E>) impl).rseq());
//    }
//
//    @Override
//    public Comparator comparator() {
//        return ((Sorted) impl).comparator();
//    }

//    @Override
//    public Object entryKey(E entry) {
//        return entry;
//    }

//    @SuppressWarnings("unchecked")
//    @Override
//    public ISeq<E> seq(boolean ascending) {
//        PersistentTreeMap m = (PersistentTreeMap) impl;
//        return RT.keys(m.seq(ascending));
//    }
//
//    @SuppressWarnings("unchecked")
//    @Override
//    public ISeq<E> seqFrom(Object key, boolean ascending) {
//        PersistentTreeMap m = (PersistentTreeMap) impl;
//        return RT.keys(m.seqFrom(key, ascending));
//    }

}
