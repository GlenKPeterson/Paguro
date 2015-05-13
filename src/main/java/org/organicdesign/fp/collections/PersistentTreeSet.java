/**
 Copyright (c) Rich Hickey. All rights reserved. The use and distribution terms for this software are covered by the
 Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php) which can be found in the file epl-v10.html
 at the root of this distribution. By using this software in any fashion, you are agreeing to be bound by the terms of
 this license. You must not remove this notice, or any other, from this software.
 */

/* rich Mar 3, 2008 */

package org.organicdesign.fp.collections;

import java.util.Comparator;
import java.util.Objects;

import org.organicdesign.fp.Option;
import org.organicdesign.fp.permanent.Sequence;

public class PersistentTreeSet<E> implements ImSetSorted<E> {

    /**
     Be extremely careful with this because it uses the default comparator, which only works for items that implement
     Comparable (have a "natural ordering").  An attempt to use it with other items will blow up at runtime.  Either
     a withComparator() method will be added, or this will be removed.
     */
    @SuppressWarnings("unchecked")
    static public final PersistentTreeSet EMPTY = new PersistentTreeSet(PersistentTreeMap.EMPTY);

    /**
     Be extremely careful with this because it uses the default comparator, which only works for items that implement
     Comparable (have a "natural ordering").  An attempt to use it with other items will blow up at runtime.  Either
     a withComparator() method will be added, or this will be removed.
     */
    @SuppressWarnings("unchecked")
    static public <T> PersistentTreeSet<T> empty() { return EMPTY; }

    /** {@inheritDoc} */
    @Override public boolean contains(Object o) { return impl.containsKey(o); }

    /** {@inheritDoc} */
    @Override public int size() { return impl.size(); }

    /** {@inheritDoc} */
    @Override public boolean isEmpty() { return impl.isEmpty(); }

//    /** {@inheritDoc} */
//    @Override
//    public UnIterator<E> iterator() {
//        return iterator();
//    }

    @Override public boolean equals(Object other) {
        return (other != null) &&
               (other instanceof ImSetSorted) &&
               (this.size() == ((ImSetSorted) other).size()) &&
               Objects.equals(comparator(), ((ImSetSorted) other).comparator()) &&
               UnIterable.equals(this, (ImSetSorted) other);
    }

    @Override public int hashCode() { return (size() == 0) ? 0 : UnIterable.hashCode(this); }

    final ImMapSorted<E,?> impl;

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

    private PersistentTreeSet(ImMapSorted<E,?> i) { impl = i; }

    public static <T> PersistentTreeSet<T> ofComp(Comparator<? super T> comp) {
        return new PersistentTreeSet<>(PersistentTreeMap.ofComp(comp));
    }

    @SafeVarargs
    public static <T> PersistentTreeSet<T> ofComp(Comparator<? super T> comp, T... items) {
        PersistentTreeSet<T> ret = new PersistentTreeSet<>(PersistentTreeMap.ofComp(comp));
        if ( (items == null) || (items.length < 1) ) { return ret; }
        for (T item : items) {
            ret = ret.put(item);
        }
        return ret;
    }

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

    public static <T> PersistentTreeSet<T> ofMap(ImMapSorted<T,?> i) { return new PersistentTreeSet<>(i); }

    @Override public PersistentTreeSet<E> put(E e) {
        return (impl.containsKey(e)) ? this
                                     : new PersistentTreeSet<>(impl.assoc(e, null));
    }

    @Override public PersistentTreeSet<E> disjoin(E key) {
        return (impl.containsKey(key)) ? new PersistentTreeSet<>(impl.without(key))
                                       : this;
    }

    @Override public Comparator<? super E> comparator() { return impl.comparator(); }

    @Override public ImSetSorted<E> subSet(E fromElement, E toElement) {
        return PersistentTreeSet.ofMap(impl.subMap(fromElement, toElement));
    }

    @Override public E first() { return impl.firstKey(); }

    @Override public Option<E> head() { return size() > 0 ? Option.of(impl.firstKey()) : Option.none(); }

    // TODO: Ensure that KeySet is sorted.
    @Override public Sequence<E> tail() { return impl.without(first()).keySet(); }

    @Override public String toString() { return UnIterable.toString("PersistentTreeSet", this); }

    @Override public E last() { return impl.lastKey(); }

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
