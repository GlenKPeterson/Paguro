/**
 *   Copyright (c) Rich Hickey. All rights reserved.
 *   The use and distribution terms for this software are covered by the
 *   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
 *   which can be found in the file epl-v10.html at the root of this distribution.
 *   By using this software in any fashion, you are agreeing to be bound by
 * 	 the terms of this license.
 *   You must not remove this notice, or any other, from this software.
 **/

/* rich Mar 3, 2008 */
/* glen added types 2015-06-06 any errors are now mine. */

package org.organicdesign.fp.collections;

import java.util.Set;

/**
 A wrapper that turns a PersistentTreeMap into a set.

 This file is a derivative work based on a Clojure collection licensed under the Eclipse Public
 License 1.0 Copyright Rich Hickey
*/
public class PersistentHashSet<E> implements ImSet<E> {

//    public static final PersistentHashSet<Object> EMPTY = new PersistentHashSet<>(PersistentHashMap.EMPTY);

    @SuppressWarnings("unchecked")
    public static <E> PersistentHashSet<E> empty() { return (PersistentHashSet<E>) new PersistentHashSet<>(PersistentHashMap.empty()); }

    public static <E> PersistentHashSet<E> empty(Equator<E> eq) { return new PersistentHashSet<>(PersistentHashMap.empty(eq)); }

    public static <E>  PersistentHashSet<E> of(Iterable<E> init) {
        PersistentHashSet<E> empty = empty();
        TransientHashSet<E> ret = empty.asTransient();
        for (E e : init) {
            ret = ret.put(e);
        }
        return ret.persistent();
    }

    public static <E>  PersistentHashSet<E> ofEq(Equator<E> eq, Iterable<E> init) {
        PersistentHashSet<E> empty = empty(eq);
        TransientHashSet<E> ret = empty.asTransient();
        for (E e : init) {
            ret = ret.put(e);
        }
        return ret.persistent();
    }

    @SuppressWarnings("unchecked")
    public static <E> PersistentHashSet<E> ofMap(ImMapTrans<E,?> map) {
        return new PersistentHashSet<>((ImMapTrans<E,E>) map);
    }

    private final ImMapTrans<E,E> impl;

    private PersistentHashSet(ImMapTrans<E,E> i) { impl = i; }

    @Override public boolean contains(Object key) {
        //noinspection SuspiciousMethodCalls
        return impl.containsKey(key);
    }

    /** Returns the Equator used by this set for equals comparisons and hashCodes */
    public Equator<E> equator() { return impl.equator(); }

    @Override public PersistentHashSet<E> without(E key) {
        if (contains(key))
            return new PersistentHashSet<>(impl.without(key));
        return this;
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

    @Override public int hashCode() { return UnmodIterable.hashCode(this); }

    @Override public String toString() { return UnmodIterable.toString("PersistentHashSet", this); }

    @Override public PersistentHashSet<E> put(E o) {
        if (contains(o))
            return this;
        return new PersistentHashSet<>(impl.assoc(o, o));
    }

//    @Override public Sequence<E> seq() { return impl.seq().map(e -> e.getKey()); }

    @Override public UnmodIterator<E> iterator() { return impl.map(e -> e.getKey()).iterator(); }

    @Override public int size() { return impl.size(); }

    private TransientHashSet<E> asTransient() {
        return new TransientHashSet<>(impl.asTransient());
    }

    static final class TransientHashSet<E> implements ImSet<E> {
        ImMapTrans<E,E> impl;

        TransientHashSet(ImMapTrans<E,E> impl) {
            this.impl = impl;
        }

        @Override public int size() { return impl.size(); }

        @Override public TransientHashSet<E> put(E val) {
            ImMapTrans<E,E> m = impl.assoc(val, val);
            if (m != impl) this.impl = m;
            return this;
        }

//        @Deprecated
//        @Override public Sequence<E> seq() { return impl.keySet().seq(); }

        @Override
        public UnmodIterator<E> iterator() { return impl.map(e -> e.getKey()).iterator(); }

        @SuppressWarnings("unchecked")
        @Override public boolean contains(Object key) {
            return impl.entry((E) key).isSome();
        }

        /**
         This is a convenience method inherited from Collection that returns true if size() == 0 (if this set contains no
         elements).
         */
        @Override public boolean isEmpty() { return impl.isEmpty(); }

        @Override public TransientHashSet<E> without(E key) {
            ImMapTrans<E,E> m = impl.without(key);
            if (m != impl) this.impl = m;
            return this;
        }

        public PersistentHashSet<E> persistent() {
            return new PersistentHashSet<>(impl.persistent());
        }
    }

}
