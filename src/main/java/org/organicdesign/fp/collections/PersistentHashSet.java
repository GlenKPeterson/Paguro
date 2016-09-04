/*
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

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 A wrapper that turns a PersistentTreeMap into a set.

 This file is a derivative work based on a Clojure collection licensed under the Eclipse Public
 License 1.0 Copyright Rich Hickey
*/
public class PersistentHashSet<E> implements ImSet<E>, Serializable {

    // If you don't put this here, it inherits EMPTY from UnmodSet, which does not have .equals()
    // defined.  UnmodSet.empty won't put() either.
    public static final PersistentHashSet<Object> EMPTY =
            new PersistentHashSet<>(PersistentHashMap.EMPTY);

    @SuppressWarnings("unchecked")
    public static <E> PersistentHashSet<E> empty() { return (PersistentHashSet<E>) EMPTY; }

    public static <E> PersistentHashSet<E> empty(Equator<E> eq) {
        return new PersistentHashSet<>(PersistentHashMap.empty(eq));
    }

    /**
     Returns a new PersistentHashSet of the values.  The vararg version of this method is
     {@link org.organicdesign.fp.StaticImports#set(Object...)}   If the input contains duplicate
     elements, later values overwrite earlier ones.

     @param elements The items to put into a vector.
     @return a new PersistentHashSet of the given elements.
     */
    public static <E> PersistentHashSet<E> of(Iterable<E> elements) {
        PersistentHashSet<E> empty = empty();
        ImSetTrans<E> ret = empty.asTransient();
        for (E e : elements) {
            ret.put(e);
        }
        return (PersistentHashSet<E>) ret.persistent();
    }

    public static <E> PersistentHashSet<E> ofEq(Equator<E> eq, Iterable<E> init) {
        PersistentHashSet<E> empty = empty(eq);
        ImSetTrans<E> ret = empty.asTransient();
        for (E e : init) {
            ret.put(e);
        }
        return (PersistentHashSet<E>) ret.persistent();
    }

    @SuppressWarnings("unchecked")
    public static <E> PersistentHashSet<E> ofMap(ImMapTrans<E,?> map) {
        return new PersistentHashSet<>((ImMapTrans<E,E>) map);
    }

    // ==================================== Instance Variables ====================================
    private final ImMapTrans<E,E> impl;

    // ======================================= Constructor =======================================
    private PersistentHashSet(ImMapTrans<E,E> i) { impl = i; }

    // ======================================= Serialization =======================================
    // This class has a custom serialized form designed to be as small as possible.  It does not
    // have the same internal structure as an instance of this class.

    // For serializable.  Make sure to change whenever internal data format changes.
    private static final long serialVersionUID = 20160904155600L;

    // Check out Josh Bloch Item 78, p. 312 for an explanation of what's going on here.
    private static class SerializationProxy<K> implements Serializable {
        // For serializable.  Make sure to change whenever internal data format changes.
        private static final long serialVersionUID = 20160904155600L;

        private final int size;
        private transient ImMapTrans<K,K> theMap;
        SerializationProxy(ImMapTrans<K,K> phm) {
            size = phm.size();
            theMap = phm;
        }

        // Taken from Josh Bloch Item 75, p. 298
        private void writeObject(ObjectOutputStream s) throws IOException {
            s.defaultWriteObject();
            // Write out all elements in the proper order
            for (Map.Entry<K,?> entry : theMap) {
                s.writeObject(entry.getKey());
            }
        }

        @SuppressWarnings("unchecked")
        private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
            s.defaultReadObject();
            theMap = PersistentHashMap.<K,K>empty().asTransient();
            for (int i = 0; i < size; i++) {
                K k = (K) s.readObject();
                theMap = theMap.assoc(k, k);
            }
        }

        private Object readResolve() { return PersistentHashSet.ofMap(theMap.persistent()); }
    }

    private Object writeReplace() { return new SerializationProxy<>(impl); }

    private void readObject(java.io.ObjectInputStream in) throws IOException,
            ClassNotFoundException {
        throw new InvalidObjectException("Proxy required");
    }

    // ===================================== Instance Methods =====================================
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

    public ImSetTrans<E> asTransient() {
        return new TransientHashSet<>(impl.asTransient());
    }

    private static final class TransientHashSet<E> implements ImSetTrans<E> {
        ImMapTrans<E,E> impl;

        TransientHashSet(ImMapTrans<E,E> impl) { this.impl = impl; }

        @Override public int size() { return impl.size(); }

        @Override public ImSetTrans<E> put(E val) {
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

        @Override public ImSetTrans<E> without(E key) {
            ImMapTrans<E,E> m = impl.without(key);
            if (m != impl) this.impl = m;
            return this;
        }

        @Override  public PersistentHashSet<E> persistent() {
            return new PersistentHashSet<>(impl.persistent());
        }
    }

}
