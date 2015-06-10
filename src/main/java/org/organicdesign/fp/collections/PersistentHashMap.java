/**
 *   Copyright (c) Rich Hickey. All rights reserved.
 *   The use and distribution terms for this software are covered by the
 *   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
 *   which can be found in the file epl-v10.html at the root of this distribution.
 *   By using this software in any fashion, you are agreeing to be bound by
 * 	 the terms of this license.
 *   You must not remove this notice, or any other, from this software.
 **/

package org.organicdesign.fp.collections;

import org.organicdesign.fp.FunctionUtils;
import org.organicdesign.fp.Option;
import org.organicdesign.fp.function.Function0;
import org.organicdesign.fp.function.Function1;
import org.organicdesign.fp.function.Function2;
import org.organicdesign.fp.function.Function3;
import org.organicdesign.fp.permanent.Sequence;
import org.organicdesign.fp.tuple.Tuple2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;

/**
 Rich Hickey's persistent rendition of Phil Bagwell's Hash Array Mapped Trie.

 Uses path copying for persistence,
 HashCollision leaves vs. extended hashing,
 Node polymorphism vs. conditionals,
 No sub-tree pools or root-resizing.
 Any errors are my own (said Rich, but now says Glen 2015-06-06).
 */
public class PersistentHashMap<K,V> implements ImMapTrans<K,V> {

    // TODO: Replace with Mutable.Ref, or make methods return Tuple2.
    private static class Box {
        public Object val;
        public Box(Object val) { this.val = val; }
    }

    private static final class Reduced {
        Object val;
        public Reduced(Object val) { this.val = val; }
//        public Object deref() { return val; }
    }

    private static boolean isReduced(Object r){
    	return (r instanceof Reduced);
    }

    private static int mask(int hash, int shift){
        //return ((hash << shift) >>> 27);// & 0x01f;
        return (hash >>> shift) & 0x01f;
    }

    // A method call is slow, but it keeps the cast localized.
    @SuppressWarnings("unchecked")
    private static <K> K k(Object[] array, int i) { return (K) array[i]; }

    // A method call is slow, but it keeps the cast localized.
    @SuppressWarnings("unchecked")
    private static <V> V v(Object[] array, int i) { return (V) array[i]; }

    // A method call is slow, but it keeps the cast localized.
    @SuppressWarnings("unchecked")
    private static <K,V> INode<K,V> iNode(Object[] array, int i) { return (INode<K,V>) array[i]; }


//    interface IFn {}

    final public static PersistentHashMap<Object,Object> EMPTY = new PersistentHashMap<>(0, null, false, null);

    @SuppressWarnings("unchecked")
    public static <K,V> PersistentHashMap<K,V> empty() { return (PersistentHashMap<K,V>) EMPTY; }

//    final private static Object NOT_FOUND = new Object();

    /** Returns a new PersistentHashMap of the given comparable keys and their paired values. */
    public static <K,V>
    PersistentHashMap<K,V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7,
                              K k8, V v8, K k9, V v9, K k10, V v10) {
        PersistentHashMap<K,V> empty = empty();
        return empty.assoc(k1, v1).assoc(k2, v2).assoc(k3, v3).assoc(k4, v4).assoc(k5, v5)
                .assoc(k6, v6).assoc(k7, v7).assoc(k8, v8).assoc(k9, v9).assoc(k10, v10);
    }

    /** Returns a new PersistentHashMap of the given comparable keys and their paired values. */
    public static <K,V>
    PersistentHashMap<K,V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7,
                              K k8, V v8, K k9, V v9) {
        PersistentHashMap<K,V> empty = empty();
        return empty.assoc(k1, v1).assoc(k2, v2).assoc(k3, v3).assoc(k4, v4).assoc(k5, v5)
                .assoc(k6, v6).assoc(k7, v7).assoc(k8, v8).assoc(k9, v9);
    }

    /** Returns a new PersistentHashMap of the given comparable keys and their paired values. */
    public static <K,V>
    PersistentHashMap<K,V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7,
                              K k8, V v8) {
        PersistentHashMap<K,V> empty = empty();
        return empty.assoc(k1, v1).assoc(k2, v2).assoc(k3, v3).assoc(k4, v4).assoc(k5, v5)
                .assoc(k6, v6).assoc(k7, v7).assoc(k8, v8);
    }

    /** Returns a new PersistentHashMap of the given comparable keys and their paired values. */
    public static <K,V>
    PersistentHashMap<K,V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7) {
        PersistentHashMap<K,V> empty = empty();
        return empty.assoc(k1, v1).assoc(k2, v2).assoc(k3, v3).assoc(k4, v4).assoc(k5, v5)
                .assoc(k6, v6).assoc(k7, v7);
    }

    /** Returns a new PersistentHashMap of the given comparable keys and their paired values. */
    public static <K,V>
    PersistentHashMap<K,V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6) {
        PersistentHashMap<K,V> empty = empty();
        return empty.assoc(k1, v1).assoc(k2, v2).assoc(k3, v3).assoc(k4, v4).assoc(k5, v5)
                .assoc(k6, v6);
    }

    /** Returns a new PersistentHashMap of the given comparable keys and their paired values. */
    public static <K,V>
    PersistentHashMap<K,V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5) {
        PersistentHashMap<K,V> empty = empty();
        return empty.assoc(k1, v1).assoc(k2, v2).assoc(k3, v3).assoc(k4, v4).assoc(k5, v5);
    }

    /** Returns a new PersistentHashMap of the given comparable keys and their paired values. */
    public static <K,V>
    PersistentHashMap<K,V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
        PersistentHashMap<K,V> empty = empty();
        return empty.assoc(k1, v1).assoc(k2, v2).assoc(k3, v3).assoc(k4, v4);
    }

    /** Returns a new PersistentHashMap of the given comparable keys and their paired values. */
    public static <K,V>
    PersistentHashMap<K,V> of(K k1, V v1, K k2, V v2, K k3, V v3) {
        PersistentHashMap<K,V> empty = empty();
        return empty.assoc(k1, v1).assoc(k2, v2).assoc(k3, v3);
    }

    /** Returns a new PersistentHashMap of the given comparable keys and their paired values. */
    public static <K,V>
    PersistentHashMap<K,V> of(K k1, V v1, K k2, V v2) {
        PersistentHashMap<K,V> empty = empty();
        return empty.assoc(k1, v1).assoc(k2, v2);
    }

    /** Returns a new PersistentHashMap of the given comparable keys and their paired values. */
    public static <K,V> PersistentHashMap<K,V> of(K k1, V v1) {
        PersistentHashMap<K,V> empty = empty();
        return empty.assoc(k1, v1);
    }


    /**
     Returns a new PersistentHashMap of the given comparable keys and their paired values, skipping any null Entries.
     */
    @SafeVarargs
    public static <K,V> PersistentHashMap<K,V>
    ofSkipNull(Map.Entry<K,V>... es) {
        if (es == null) { return empty(); }
        PersistentHashMap<K,V> map = empty();
        for (Map.Entry<K,V> entry : es) {
            if (entry != null) {
                map = map.assoc(entry.getKey(), entry.getValue());
            }
        }
        return map;
    }

//    /** Returns a new PersistentHashMap of the specified comparator and the given key/value pairs. */
//    public static <K,V> PersistentHashMap<K,V>
//    ofComp(Equator<? super K> c, K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7,
//           K k8, V v8, K k9, V v9, K k10, V v10) {
//        return new PersistentHashMap<K,V>(c, null, 0)
//                .assoc(k1, v1).assoc(k2, v2).assoc(k3, v3).assoc(k4, v4).assoc(k5, v5)
//                .assoc(k6, v6).assoc(k7, v7).assoc(k8, v8).assoc(k9, v9).assoc(k10, v10);
//    }
//
//    /** Returns a new PersistentHashMap of the specified comparator and the given key/value pairs. */
//    public static <K,V> PersistentHashMap<K,V>
//    ofComp(Equator<? super K> c, K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7,
//           K k8, V v8, K k9, V v9) {
//        return new PersistentHashMap<K,V>(c, null, 0)
//                .assoc(k1, v1).assoc(k2, v2).assoc(k3, v3).assoc(k4, v4).assoc(k5, v5)
//                .assoc(k6, v6).assoc(k7, v7).assoc(k8, v8).assoc(k9, v9);
//    }
//
//    /** Returns a new PersistentHashMap of the specified Equator and the given key/value pairs. */
//    public static <K,V> PersistentHashMap<K,V>
//    ofComp(Equator<? super K> c, K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7,
//           K k8, V v8) {
//        return new PersistentHashMap<K,V>(c, null, 0)
//                .assoc(k1, v1).assoc(k2, v2).assoc(k3, v3).assoc(k4, v4).assoc(k5, v5)
//                .assoc(k6, v6).assoc(k7, v7).assoc(k8, v8);
//    }
//
//    /** Returns a new PersistentHashMap of the specified Equator and the given key/value pairs. */
//    public static <K,V> PersistentHashMap<K,V>
//    ofComp(Equator<? super K> c, K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7) {
//        return new PersistentHashMap<K,V>(c, null, 0)
//                .assoc(k1, v1).assoc(k2, v2).assoc(k3, v3).assoc(k4, v4).assoc(k5, v5)
//                .assoc(k6, v6).assoc(k7, v7);
//    }
//
//    /** Returns a new PersistentHashMap of the specified Equator and the given key/value pairs. */
//    public static <K,V> PersistentHashMap<K,V>
//    ofComp(Equator<? super K> c, K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6) {
//        return new PersistentHashMap<K,V>(c, null, 0)
//                .assoc(k1, v1).assoc(k2, v2).assoc(k3, v3).assoc(k4, v4).assoc(k5, v5)
//                .assoc(k6, v6);
//    }
//
//    /** Returns a new PersistentHashMap of the specified Equator and the given key/value pairs. */
//    public static <K,V> PersistentHashMap<K,V>
//    ofComp(Equator<? super K> c, K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5) {
//        return new PersistentHashMap<K,V>(c, null, 0)
//                .assoc(k1, v1).assoc(k2, v2).assoc(k3, v3).assoc(k4, v4).assoc(k5, v5);
//    }
//
//    /** Returns a new PersistentHashMap of the specified Equator and the given key/value pairs. */
//    public static <K,V> PersistentHashMap<K,V>
//    ofComp(Equator<? super K> c, K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
//        return new PersistentHashMap<K,V>(c, null, 0)
//                .assoc(k1, v1).assoc(k2, v2).assoc(k3, v3).assoc(k4, v4);
//    }
//
//    /** Returns a new PersistentHashMap of the specified Equator and the given key/value pairs. */
//    public static <K,V> PersistentHashMap<K,V>
//    ofComp(Equator<? super K> c, K k1, V v1, K k2, V v2, K k3, V v3) {
//        return new PersistentHashMap<K,V>(c, null, 0)
//                .assoc(k1, v1).assoc(k2, v2).assoc(k3, v3);
//    }
//
//    /** Returns a new PersistentHashMap of the specified Equator and the given key/value pairs. */
//    public static <K,V> PersistentHashMap<K,V>
//    ofComp(Equator<? super K> c, K k1, V v1, K k2, V v2) {
//        return new PersistentHashMap<K,V>(c, null, 0)
//                .assoc(k1, v1).assoc(k2, v2);
//    }
//
//    /** Returns a new PersistentHashMap of the specified Equator and the given key/value pairs. */
//    public static <K,V> PersistentHashMap<K,V>
//    ofComp(Equator<? super K> c, K k1, V v1) {
//        return new PersistentHashMap<K,V>(c, null, 0)
//                .assoc(k1, v1);
//    }
//
//    /** Returns a new PersistentHashMap of the specified Equator and the given key/value pairs. */
//    @SafeVarargs
//    public static <K,V> PersistentHashMap<K,V>
//    ofCompSkipNull(Equator<? super K> c, Map.Entry<K,V>... es) {
//        if (es == null) { return empty(); }
//        PersistentHashMap<K,V> map = new PersistentHashMap<>(c, null, 0);
//        for (Map.Entry<K,V> entry : es) {
//            if (entry != null) {
//                map = map.assoc(entry.getKey(), entry.getValue());
//            }
//        }
//        return map;
//    }



//    static public <K,V> PersistentHashMap<K,V> create(Map<K,V> other) {
//        PersistentHashMap<K,V> empty = empty();
//        TransientHashMap<K,V> ret = empty.asTransient();
//        for(Entry<K,V> e : other.entrySet()) {
//            ret = ret.assoc(e.getKey(), e.getValue());
//        }
//        return ret.persistent();
//    }

//    /*
//     * @param init {key1,val1,key2,val2,...}
//     */
//    public static <K,V> PersistentHashMap<K,V> create(Object... init){
//        TransientHashMap<K,V> ret = empty().asTransient();
//        for(int i = 0; i < init.length; i += 2) {
//            ret = ret.assoc(init[i], init[i + 1]);
//        }
//        return (PersistentHashMap<K,V>) ret.persistent();
//    }
//
//    public static <K,V> PersistentHashMap<K,V> createWithCheck(Object... init){
//        TransientHashMap<K,V> ret = EMPTY.asTransient();
//        for(int i = 0; i < init.length; i += 2)
//        {
//            ret = ret.assoc(init[i], init[i + 1]);
//            if(ret.count() != i/2 + 1)
//                throw new IllegalArgumentException("Duplicate key: " + init[i]);
//        }
//        return (PersistentHashMap<K,V>) ret.persistent();
//    }

//    static public <K,V> PersistentHashMap<K,V> create(Iterable<Map.Entry<K,V>> items){
//        TransientHashMap<K,V> ret = empty().asTransient();
//        for(; items != null; items = items.next().next())
//        {
//            if(items.next() == null)
//                throw new IllegalArgumentException(String.format("No value supplied for key: %s", items.first()));
//            ret = ret.assoc(items.first(), RT.second(items));
//        }
//        return ret.persistent();
//    }

//    static public <K,V> PersistentHashMap<K,V> createWithCheck(ISeq items){
//        TransientHashMap<K,V> ret = empty().asTransient();
//        for(int i=0; items != null; items = items.next().next(), ++i)
//        {
//            if(items.next() == null)
//                throw new IllegalArgumentException(String.format("No value supplied for key: %s", items.first()));
//            ret = ret.assoc(items.first(), RT.second(items));
//            if(ret.count() != i + 1)
//                throw new IllegalArgumentException("Duplicate key: " + items.first());
//        }
//        return (PersistentHashMap<K,V>) ret.persistent();
//    }

//    /*
//     * @param init {key1,val1,key2,val2,...}
//     */
//    public static <S,K extends S, V extends S> PersistentHashMap<K,V> create(S... init){
//        return create(init);
//    }

    // =================================================== Instance ===================================================
    private final int count;
    private final INode<K,V> root;
    private final boolean hasNull;
    private final V nullValue;

    PersistentHashMap(int count, INode<K,V> root, boolean hasNull, V nullValue) {
        this.count = count;
        this.root = root;
        this.hasNull = hasNull;
        this.nullValue = nullValue;
    }

    /** Not sure I like this - could disappear. */
    boolean hasNull() { return hasNull; }

//    @SuppressWarnings("unchecked")
//    @Override public boolean containsKey(Object key){
//        if (key == null) {
//            return hasNull;
//        }
//        return (root != null) ? root.findVal(0, Objects.hashCode(key), (K) key, (V) NOT_FOUND) != NOT_FOUND : false;
//    }

//    /** {@inheritDoc} */
//    @SuppressWarnings("unchecked")
//    @Override public V get(Object key){
//        return getOrElse((K) key, null);
//    }
//
//    /**
//     Returns a view of the mappings contained in this map.  The set should actually contain UnMap.Entry items, but that
//     return signature is illegal in Java, so you'll just have to remember.
//     */
//    @Override public ImSet<Entry<K,V>> entrySet() {
//        return seq().toImSet();
//    }
//
//    /** Returns a view of the keys contained in this map. */
//    @Override public ImSet<K> keySet() {
//        return seq().map(e -> e.getKey()).toImSet();
//    }
//
//    /** Returns a view of the values contained in this map. */
//    @Override public UnCollection<V> values() {
//        return seq().map(e -> e.getValue()).toUnSet();
//    }

    @Override public PersistentHashMap<K,V> assoc(K key, V val) {
        if(key == null) {
            if (hasNull && (val == nullValue)) { return this; }
            return new PersistentHashMap<>(hasNull ? count : count + 1, root, true, val);
        }
        Box addedLeaf = new Box(null);
        INode<K,V> newroot = (root == null ? BitmapIndexedNode.empty() : root);
        newroot = newroot.assoc(0, Objects.hashCode(key), key, val, addedLeaf);
        if (newroot == root) {
            return this;
        }
        return new PersistentHashMap<>(addedLeaf.val == null ? count : count + 1, newroot, hasNull, nullValue);
    }

    @Override public ImMapTrans<K,V> asTransient() {
        return new TransientHashMap<>(this);
    }

    @Override public Option<UnMap.UnEntry<K,V>> entry(K key) {
        if (key == null) {
            return hasNull ? Option.of(Tuple2.of(null, nullValue)) : Option.none();
        }
        if (root == null) {
            return Option.none();
        }
        UnEntry<K,V> entry = root.find(0, Objects.hashCode(key), key);
        return Option.someOrNullNoneOf(entry);
    }

//    // Suppress unchecked warnings for the two places where we cast a map of T to a map of super T.
//    @SuppressWarnings("unchecked")
//    public static <SK, SV, K1 extends SK, K2 extends SK, V1 extends SV, V2 extends SV>
//    ImMap<SK,SV> assoc(PersistentHashMap<K1,V1> map, K2 key, V2 val) {
//        if(key == null) {
//            if(map.hasNull && val == map.nullValue) {
//                return (ImMap<SK, SV>) map; // cast a map of T to a map of super T
//            } else {
//                return new PersistentHashMap<>(map.hasNull ? map.count : map.count + 1, map.root, true, val);
//            }
//        }
//        Box addedLeaf = new Box(null);
//        INode<K,V> newroot = (map.root == null ? BitmapIndexedNode.empty() : map.root)
//                .assoc(0, Objects.hashCode(key), key, val, addedLeaf);
//        if(newroot == map.root)
//            return (ImMap<SK, SV>) map; // cast a map of T to a map of super T
//        return new PersistentHashMap<>(addedLeaf.val == null ? map.count : map.count + 1, newroot, map.hasNull, map.nullValue);
//    }

//    public static <SK, SV, K1 extends SK, K2 extends SK, V1 extends SV, V2 extends SV>
//    ImMap<SK,SV> assocEx(PersistentHashMap<K1,V1> map, K2 key, V2 val) {
//        //noinspection SuspiciousMethodCalls
//        if (map.containsKey(key)) {
//            throw new IllegalStateException("Key already present");
//        }
//        return assoc(map, key, val);
//    }

    /**
     This is compatible with java.util.Map but that means it wrongly allows comparisons with SortedMaps, which are
     necessarily not commutative.
     @param other the other (hopefully unsorted) map to compare to.
     @return true if these maps contain the same elements, regardless of order.
     */
    @Override public boolean equals(Object other) {
        if (other == this) { return true; }
        if ( !(other instanceof Map) ) { return false; }

        Map<?,?> that = (Map<?,?>) other;
        if (that.size() != size()) { return false; }

        try {
            for (Entry<K,V> e : entrySet()) {
                K key = e.getKey();
                V value = e.getValue();
                if (value == null) {
                    if (!(that.get(key)==null && that.containsKey(key))) {
                        return false;
                    }
                } else {
                    if (!value.equals(that.get(key))) {
                        return false;
                    }
                }
            }
        } catch (ClassCastException unused) {
            return false;
        } catch (NullPointerException unused) {
            return false;
        }

        return true;
    }

    @Override public int hashCode() { return UnIterable.hashCode(this); }

    @Override public UnIterator<UnMap.UnEntry<K,V>> iterator(){
        return seq().iterator();
    }

    @Override public final PersistentHashMap<K,V> persistent() { return this; }

//    public <R> R kvreduce(Function3<R,K,V,R> f, R init) {
//        init = hasNull ? f.apply(init, null, nullValue) : init;
//        if(RT.isReduced(init))
//            return ((IDeref)init).deref();
//        if(root != null){
//            init = root.kvreduce(f,init);
//            if(RT.isReduced(init))
//                return ((IDeref)init).deref();
//            else
//                return init;
//        }
//        return init;
//    }

//    public <R> R fold(long n, final Function2<R,R,R> combinef, final Function3<R,K,V,R> reducef,
//                      Function1<Function0<R>,R> fjinvoke, final Function1<Function0,R> fjtask,
//                      final Function1<R,Object> fjfork, final Function1<Object,R> fjjoin){
//        //we are ignoring n for now
//        Function0<R> top = () -> {
//            R ret = combinef.apply(null,null);
//            if(root != null)
//                ret = combinef.apply(ret, root.fold(combinef, reducef, fjtask, fjfork, fjjoin));
//            return hasNull ? combinef.apply(ret, reducef.apply(combinef.apply(null,null), null, nullValue))
//                           : ret;
//        };
//        return fjinvoke.apply(top);
//    }

    // TODO: This suppression isn't right.  s.prepend() should be totally happy.
    @SuppressWarnings("unchecked")
    @Override public Sequence<UnMap.UnEntry<K,V>> seq() {
//        System.out.println("root: " + root);
        Sequence<UnMap.UnEntry<K,V>> s = root != null ? root.nodeSeq() : Sequence.emptySequence();
        return hasNull ? s.prepend((UnMap.UnEntry<K,V>) Tuple2.of((K) null, nullValue)) : s;
    }

    /** {@inheritDoc} */
    @Override public int size() { return count; }

    /** {@inheritDoc} */
    @Override public String toString() { return UnIterable.toString("PersistentHashMap", this); }

    @SuppressWarnings("unchecked")
    @Override public PersistentHashMap<K,V> without(K key){
        if(key == null)
            return hasNull ? new PersistentHashMap<>(count - 1, root, false, null) : this;
        if(root == null)
            return this;
        INode<K,V> newroot = root.without(0, Objects.hashCode(key), key);
        if(newroot == root)
            return this;
        return new PersistentHashMap<>(count - 1, newroot, hasNull, nullValue);
    }

//    static abstract class ATransientMap<K,V> implements ImMap {
//    	abstract void ensureEditable();
//    	abstract ImMap<K,V> doAssoc(K key, V val);
//    	abstract ImMap<K,V> doWithout(K key);
//    	abstract V doValAt(K key, V notFound);
//    	abstract int doCount();
//    	abstract ImMap<K,V> doPersistent();
//
////    	public ImMap<K,V> conj(Map.Entry<K,V> e) {
////    		ensureEditable();
////            return assoc(e.getKey(), e.getValue());
////    	}
//
////    	public final Object invoke(Object arg1) {
////    		return valAt(arg1);
////    	}
////
////    	public final Object invoke(Object arg1, Object notFound) {
////    		return valAt(arg1, notFound);
////    	}
//
//    	public final V valAt(K key) {
//    		return valAt(key, null);
//    	}
//
//    	@Override public final ImMap<K,V> assoc(K key, V val) {
//    		ensureEditable();
//    		return doAssoc(key, val);
//    	}
//
//    	public final ImMap<K,V> without(K key) {
//    		ensureEditable();
//    		return doWithout(key);
//    	}
//
//    	public final ImMap<K,V> persistent() {
//    		ensureEditable();
//    		return doPersistent();
//    	}
//
//    	public final V valAt(K key, V notFound) {
//    		ensureEditable();
//    		return doValAt(key, notFound);
//    	}
//
//    	public final int size() {
//    		ensureEditable();
//    		return doCount();
//    	}
//    }

    static final class TransientHashMap<K,V> implements ImMapTrans<K,V> {
        AtomicReference<Thread> edit;
        INode<K,V> root;
        int count;
        boolean hasNull;
        V nullValue;
        final Box leafFlag = new Box(null);

        TransientHashMap(PersistentHashMap<K,V> m) {
            this(new AtomicReference<>(Thread.currentThread()), m.root, m.count, m.hasNull, m.nullValue);
        }

        TransientHashMap(AtomicReference<Thread> edit, INode<K,V> root, int count, boolean hasNull, V nullValue) {
            this.edit = edit;
            this.root = root;
            this.count = count;
            this.hasNull = hasNull;
            this.nullValue = nullValue;
        }

        private int doCount() { return count; }

        private TransientHashMap<K,V> doAssoc(K key, V val) {
            if (key == null) {
                if (this.nullValue != val)
                    this.nullValue = val;
                if (!hasNull) {
                    this.count++;
                    this.hasNull = true;
                }
                return this;
            }
//		Box leafFlag = new Box(null);
            leafFlag.val = null;
            INode<K,V> n = (root == null ? BitmapIndexedNode.empty() : root);
            n = n.assoc(edit, 0, Objects.hashCode(key), key, val, leafFlag);
            if (n != this.root)
                this.root = n;
            if(leafFlag.val != null) this.count++;
            return this;
        }

        @Override public TransientHashMap<K,V> assoc(K key, V val) {
       		ensureEditable();
       		return doAssoc(key, val);
       	}

        @Override public ImMapTrans<K,V> asTransient() { return this; }

        private TransientHashMap<K,V> doWithout(K key) {
            if (key == null) {
                if (!hasNull) return this;
                hasNull = false;
                nullValue = null;
                this.count--;
                return this;
            }
            if (root == null) return this;
//		Box leafFlag = new Box(null);
            leafFlag.val = null;
            INode<K,V> n = root.without(edit, 0, Objects.hashCode(key), key, leafFlag);
            if (n != root)
                this.root = n;
            if(leafFlag.val != null) this.count--;
            return this;
        }

        private PersistentHashMap<K,V> doPersistent() {
            edit.set(null);
            return new PersistentHashMap<>(count, root, hasNull, nullValue);
        }

//        private V doValAt(K key, V notFound) {
//            if (key == null)
//                if (hasNull)
//                    return nullValue;
//                else
//                    return notFound;
//            if (root == null)
//                return notFound;
//            return root.findVal(0, Objects.hashCode(key), key, notFound);
//        }

//        public final TransientHashMap<K,V> assoc(K key, V val) {
//       		ensureEditable();
//       		return doAssoc(key, val);
//       	}
//
//       	public final TransientHashMap<K,V> without(K key) {
//       		ensureEditable();
//       		return doWithout(key);
//       	}

//        public final PersistentHashMap<K,V> persistent() {
//       		ensureEditable();
//       		return doPersistent();
//       	}

//        int doCount() {
//            return count;
//        }

        private Option<UnEntry<K,V>> doEntry(K key) {
            if (key == null) {
                return hasNull ? Option.of(Tuple2.of(null, nullValue)) : Option.none();
            }
            if (root == null) {
                return Option.none();
            }
            UnEntry<K,V> entry = root.find(0, Objects.hashCode(key), key);
            return Option.someOrNullNoneOf(entry);
        }

        @Override public Option<UnEntry<K,V>> entry(K key) {
            ensureEditable();
            return doEntry(key);
        }
//        public final V valAt(K key) {
//       		return valAt(key, null);
//       	}

        @Override
        // TODO: This suppression isn't right.  s.prepend() should be totally happy.
        @SuppressWarnings("unchecked")
        public Sequence<UnMap.UnEntry<K,V>> seq() {
            Sequence<UnMap.UnEntry<K,V>> s = root != null ? root.nodeSeq() : Sequence.emptySequence();
            return hasNull ? s.prepend((UnMap.UnEntry<K,V>) Tuple2.of((K) null, nullValue)) : s;
        }

       	@Override public final TransientHashMap<K,V> without(K key) {
       		ensureEditable();
       		return doWithout(key);
       	}

        @Override public final PersistentHashMap<K,V> persistent() {
       		ensureEditable();
       		return doPersistent();
       	}

//       	public final V valAt(K key, V notFound) {
//       		ensureEditable();
//       		return doValAt(key, notFound);
//       	}

       	@Override public final int size() {
       		ensureEditable();
       		return doCount();
       	}

        void ensureEditable() {
            if(edit.get() == null)
                throw new IllegalAccessError("Transient used after persistent! call");
        }
    }

    interface INode<K,V> extends Serializable {
        INode<K,V> assoc(int shift, int hash, K key, V val, Box addedLeaf);

        INode<K,V> without(int shift, int hash, K key);

        UnEntry<K,V> find(int shift, int hash, K key);

        V findVal(int shift, int hash, K key, V notFound);

        Sequence<UnMap.UnEntry<K,V>> nodeSeq();

        INode<K,V> assoc(AtomicReference<Thread> edit, int shift, int hash, K key, V val, Box addedLeaf);

        INode<K,V> without(AtomicReference<Thread> edit, int shift, int hash, K key, Box removedLeaf);

        <R> R kvreduce(Function3<R,K,V,R> f, R init);

        <R> R fold(Function2<R,R,R> combinef, Function3<R,K,V,R> reducef, final Function1<Function0,R> fjtask,
                   final Function1<R,Object> fjfork, final Function1<Object,R> fjjoin);
    }

    final static class ArrayNode<K,V> implements INode<K,V> {
        int count;
        final INode<K,V>[] array;
        final AtomicReference<Thread> edit;

        ArrayNode(AtomicReference<Thread> edit, int count, INode<K,V>[] array){
            this.array = array;
            this.edit = edit;
            this.count = count;
        }

        @Override public INode<K,V> assoc(int shift, int hash, K key, V val, Box addedLeaf) {
            int idx = mask(hash, shift);
            INode<K,V> node = array[idx];
            if (node == null) {
                BitmapIndexedNode<K,V> e = BitmapIndexedNode.empty();
                INode<K,V> n = e.assoc(shift + 5, hash, key, val, addedLeaf);
                return new ArrayNode<>(null, count + 1, cloneAndSet(array, idx, n));
            }
            INode<K,V> n = node.assoc(shift + 5, hash, key, val, addedLeaf);
            if (n == node) {
                return this;
            }
            return new ArrayNode<>(null, count, cloneAndSet(array, idx, n));
        }

        @Override public INode<K,V> without(int shift, int hash, K key){
            int idx = mask(hash, shift);
            INode<K,V> node = array[idx];
            if(node == null)
                return this;
            INode<K,V> n = node.without(shift + 5, hash, key);
            if(n == node)
                return this;
            if (n == null) {
                if (count <= 8) {
                    // shrink
                    return pack(null, idx);
                }
                return new ArrayNode<>(null, count - 1, cloneAndSet(array, idx, null));
            } else
                return new ArrayNode<>(null, count, cloneAndSet(array, idx, n));
        }

        @Override public UnMap.UnEntry<K,V> find(int shift, int hash, K key) {
            int idx = mask(hash, shift);
            INode<K,V> node = array[idx];
            if(node == null)
                return null;
            return node.find(shift + 5, hash, key);
        }

        @Override public V findVal(int shift, int hash, K key, V notFound){
            int idx = mask(hash, shift);
            INode<K,V> node = array[idx];
            if(node == null)
                return notFound;
            return node.findVal(shift + 5, hash, key, notFound);
        }

        @Override public Sequence<UnMap.UnEntry<K,V>> nodeSeq(){ return Seq.create(array); }

        @Override public <R> R kvreduce(Function3<R,K,V,R> f, R init){
            for(INode<K,V> node : array){
                if(node != null){
                    init = node.kvreduce(f,init);
                    if(isReduced(init))
                        return init;
                }
            }
            return init;
        }
        @Override public <R> R fold(Function2<R,R,R> combinef, Function3<R,K,V,R> reducef,
                                    final Function1<Function0,R> fjtask, final Function1<R,Object> fjfork,
                                    final Function1<Object,R> fjjoin){
            List<Callable<R>> tasks = new ArrayList<>();
            for(final INode<K,V> node : array){
                if(node != null){
                    tasks.add(() -> node.fold(combinef, reducef, fjtask, fjfork, fjjoin));
                }
            }

            return foldTasks(tasks,combinef,fjtask,fjfork,fjjoin);
        }

        static public <R> R foldTasks(List<Callable<R>> tasks, final Function2<R,R,R> combinef,
                                      final Function1<Function0,R> fjtask, final Function1<R,Object> fjfork,
                                      final Function1<Object,R> fjjoin) {

            if(tasks.isEmpty())
                return combinef.apply(null,null);

            if(tasks.size() == 1){
                try {
                    return tasks.get(0).call();
                } catch (RuntimeException re) {
                    throw re;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            List<Callable<R>> t1 = tasks.subList(0,tasks.size()/2);
            final List<Callable<R>> t2 = tasks.subList(tasks.size()/2, tasks.size());

            Object forked = fjfork.apply(fjtask.apply(() -> foldTasks(t2, combinef, fjtask, fjfork, fjjoin)));

            return combinef.apply(foldTasks(t1, combinef, fjtask, fjfork, fjjoin), fjjoin.apply(forked));
        }


        private ArrayNode<K,V> ensureEditable(AtomicReference<Thread> edit){
            if(this.edit == edit)
                return this;
            return new ArrayNode<>(edit, count, this.array.clone());
        }

        private ArrayNode<K,V> editAndSet(AtomicReference<Thread> edit, int i, INode<K,V> n){
            ArrayNode<K,V> editable = ensureEditable(edit);
            editable.array[i] = n;
            return editable;
        }


        private INode<K,V> pack(AtomicReference<Thread> edit, int idx) {
            Object[] newArray = new Object[2*(count - 1)];
            int j = 1;
            int bitmap = 0;
            for(int i = 0; i < idx; i++)
                if (array[i] != null) {
                    newArray[j] = array[i];
                    bitmap |= 1 << i;
                    j += 2;
                }
            for(int i = idx + 1; i < array.length; i++)
                if (array[i] != null) {
                    newArray[j] = array[i];
                    bitmap |= 1 << i;
                    j += 2;
                }
            return new BitmapIndexedNode<>(edit, bitmap, newArray);
        }

        @Override public INode<K,V> assoc(AtomicReference<Thread> edit, int shift, int hash, K key, V val, Box addedLeaf){
            int idx = mask(hash, shift);
            INode<K,V> node = array[idx];
            if(node == null) {
                BitmapIndexedNode<K,V> en = BitmapIndexedNode.empty();
                ArrayNode<K,V> editable = editAndSet(edit, idx, en.assoc(edit, shift + 5, hash, key, val, addedLeaf));
                editable.count++;
                return editable;
            }
            INode<K,V> n = node.assoc(edit, shift + 5, hash, key, val, addedLeaf);
            if(n == node)
                return this;
            return editAndSet(edit, idx, n);
        }

        @Override
        public INode<K,V> without(AtomicReference<Thread> edit, int shift, int hash, K key, Box removedLeaf) {
            int idx = mask(hash, shift);
            INode<K,V> node = array[idx];
            if (node == null) {
                return this;
            }
            INode<K,V> n = node.without(edit, shift + 5, hash, key, removedLeaf);
            if (n == node) {
                return this;
            }
            if (n == null) {
                if (count <= 8) // shrink
                    return pack(edit, idx);
                ArrayNode<K,V> editable = editAndSet(edit, idx, null);
                editable.count--;
                return editable;
            }
            return editAndSet(edit, idx, n);
        }

        @Override public String toString() {
            return UnIterable.toString("ArrayNode", this.nodeSeq());
        }

        static class Seq<K,V> implements Sequence<UnMap.UnEntry<K,V>> {
            final INode<K,V>[] nodes;
            final int i;
            final Sequence<UnMap.UnEntry<K,V>> s;

            static <K,V> Sequence<UnMap.UnEntry<K,V>> create(INode<K,V>[] nodes) {
                return create(nodes, 0, null);
            }

            private static <K,V> Sequence<UnMap.UnEntry<K,V>> create(INode<K,V>[] nodes, int i, Sequence<UnMap.UnEntry<K,V>> s) {
                if ( (s != null) && (s != Sequence.EMPTY_SEQUENCE) ) { return new Seq<>(nodes, i, s); }

                for(int j = i; j < nodes.length; j++) {
                    if (nodes[j] != null) {
                        Sequence<UnMap.UnEntry<K,V>> ns = nodes[j].nodeSeq();
                        if (ns != null) {
                            return new Seq<>(nodes, j + 1, ns);
                        }
                    }
                }
                return Sequence.emptySequence();
            }

            private Seq(INode<K,V>[] nodes, int i, Sequence<UnMap.UnEntry<K,V>> s) {
                super();
                this.nodes = nodes;
                this.i = i;
                this.s = s;
            }

            @Override public Option<UnMap.UnEntry<K,V>> head() {
                return ( (s != null) && (s != Sequence.EMPTY_SEQUENCE) )
                       ? s.head()
                       : Option.none();
            }

            @Override public Sequence<UnMap.UnEntry<K,V>> tail() {
                if ( (s != null) && (s != Sequence.EMPTY_SEQUENCE) ) {
                    return create(nodes, i, s.tail());
                }
                return create(nodes, i, null);

            }
        }
    }

    @SuppressWarnings("unchecked")
    final static class BitmapIndexedNode<K,V> implements INode<K,V> {
        static final BitmapIndexedNode EMPTY = new BitmapIndexedNode(null, 0, new Object[0]);

        static final <K,V> BitmapIndexedNode<K,V> empty() { return (BitmapIndexedNode<K,V>) EMPTY; }

        int bitmap;
        // even numbered cells are key or null, odd are val or node.
        Object[] array;
        final AtomicReference<Thread> edit;

        @Override public String toString() {
            return "BitmapIndexedNode(" + bitmap + "," + FunctionUtils.toString(array) + "," + edit + ")";
        }

        final int index(int bit) { return Integer.bitCount(bitmap & (bit - 1)); }

        BitmapIndexedNode(AtomicReference<Thread> edit, int bitmap, Object[] array){
            this.bitmap = bitmap;
            this.array = array;
            this.edit = edit;
        }

        @Override public INode<K,V> assoc(int shift, int hash, K key, V val, Box addedLeaf){
            int bit = bitpos(hash, shift);
            int idx = index(bit);
            if((bitmap & bit) != 0) {
                Object keyOrNull = array[2*idx];
                Object valOrNode = array[2*idx+1];
                if(keyOrNull == null) {
                    INode<K,V> n = ((INode) valOrNode).assoc(shift + 5, hash, key, val, addedLeaf);
                    if(n == valOrNode)
                        return this;
                    return new BitmapIndexedNode<>(null, bitmap, cloneAndSet(array, 2*idx+1, n));
                }
                if(Objects.equals(key, keyOrNull)) {
                    if(val == valOrNode)
                        return this;
                    return new BitmapIndexedNode<>(null, bitmap, cloneAndSet(array, 2*idx+1, val));
                }
                addedLeaf.val = addedLeaf;
                return new BitmapIndexedNode<>(null, bitmap,
                                               cloneAndSet(array,
                                                           2*idx, null,
                                                           2*idx+1, createNode(shift + 5, keyOrNull, valOrNode, hash, key, val)));
            } else {
                int n = Integer.bitCount(bitmap);
                if(n >= 16) {
                    INode[] nodes = new INode[32];
                    int jdx = mask(hash, shift);
                    nodes[jdx] = empty().assoc(shift + 5, hash, key, val, addedLeaf);
                    int j = 0;
                    for(int i = 0; i < 32; i++)
                        if(((bitmap >>> i) & 1) != 0) {
                            if (array[j] == null)
                                nodes[i] = (INode) array[j+1];
                            else
                                nodes[i] = empty().assoc(shift + 5, Objects.hashCode(array[j]), array[j], array[j + 1], addedLeaf);
                            j += 2;
                        }
                    return new ArrayNode(null, n + 1, nodes);
                } else {
                    Object[] newArray = new Object[2*(n+1)];
                    System.arraycopy(array, 0, newArray, 0, 2*idx);
                    newArray[2*idx] = key;
                    addedLeaf.val = addedLeaf;
                    newArray[2*idx+1] = val;
                    System.arraycopy(array, 2*idx, newArray, 2*(idx+1), 2*(n-idx));
                    return new BitmapIndexedNode<>(null, bitmap | bit, newArray);
                }
            }
        }

        @Override public INode<K,V> without(int shift, int hash, K key){
            int bit = bitpos(hash, shift);
            if((bitmap & bit) == 0)
                return this;
            int idx = index(bit);
            K keyOrNull = (K) array[2*idx];
            Object valOrNode = array[2*idx+1];
            if(keyOrNull == null) {
                INode<K,V> n = ((INode) valOrNode).without(shift + 5, hash, key);
                if (n == valOrNode)
                    return this;
                if (n != null)
                    return new BitmapIndexedNode<>(null, bitmap, cloneAndSet(array, 2*idx+1, n));
                if (bitmap == bit)
                    return null;
                return new BitmapIndexedNode<>(null, bitmap ^ bit, removePair(array, idx));
            }
            if(Objects.equals(key, keyOrNull))
                // TODO: collapse
                return new BitmapIndexedNode<>(null, bitmap ^ bit, removePair(array, idx));
            return this;
        }

        @Override public UnEntry<K,V> find(int shift, int hash, K key){
            int bit = bitpos(hash, shift);
            if((bitmap & bit) == 0)
                return null;
            int idx = index(bit);
            Object keyOrNull = array[2*idx];
            Object valOrNode = array[2*idx+1];
            if(keyOrNull == null)
                return ((INode) valOrNode).find(shift + 5, hash, key);
            if(Objects.equals(key, keyOrNull))
                return Tuple2.of((K) keyOrNull, (V) valOrNode);
            return null;
        }

        @Override public V findVal(int shift, int hash, K key, V notFound) {
            int bit = bitpos(hash, shift);
            if ((bitmap & bit) == 0) {
                return notFound;
            }
            int idx = index(bit);
            K keyOrNull = k(array, 2 * idx);
            if (keyOrNull == null) {
                INode<K,V> n = iNode(array, 2 * idx + 1);
                return n.findVal(shift + 5, hash, key, notFound);
            }
            if (Objects.equals(key, keyOrNull)) {
                return v(array, 2 * idx + 1);
            }
            return notFound;
        }

        @Override public Sequence<UnMap.UnEntry<K,V>> nodeSeq() { return NodeSeq.create(array); }

        @Override public <R> R kvreduce(Function3<R,K,V,R> f, R init){
            return NodeSeq.kvreduce(array,f,init);
        }

        @Override public <R> R fold(Function2<R,R,R> combinef, Function3<R,K,V,R> reducef,
                                    final Function1<Function0,R> fjtask, final Function1<R,Object> fjfork,
                                    final Function1<Object,R> fjjoin){
            return NodeSeq.kvreduce(array, reducef, combinef.apply(null,null));
        }

        private BitmapIndexedNode<K,V> ensureEditable(AtomicReference<Thread> edit){
            if(this.edit == edit)
                return this;
            int n = Integer.bitCount(bitmap);
            Object[] newArray = new Object[n >= 0 ? 2*(n+1) : 4]; // make room for next assoc
            System.arraycopy(array, 0, newArray, 0, 2*n);
            return new BitmapIndexedNode<>(edit, bitmap, newArray);
        }

        private BitmapIndexedNode<K,V> editAndSet(AtomicReference<Thread> edit, int i, Object a) {
            BitmapIndexedNode editable = ensureEditable(edit);
            editable.array[i] = a;
            return editable;
        }

        private BitmapIndexedNode<K,V> editAndSet(AtomicReference<Thread> edit, int i, Object a, int j, Object b) {
            BitmapIndexedNode editable = ensureEditable(edit);
            editable.array[i] = a;
            editable.array[j] = b;
            return editable;
        }

        private BitmapIndexedNode<K,V> editAndRemovePair(AtomicReference<Thread> edit, int bit, int i) {
            if (bitmap == bit)
                return null;
            BitmapIndexedNode<K,V> editable = ensureEditable(edit);
            editable.bitmap ^= bit;
            System.arraycopy(editable.array, 2*(i+1), editable.array, 2*i, editable.array.length - 2*(i+1));
            editable.array[editable.array.length - 2] = null;
            editable.array[editable.array.length - 1] = null;
            return editable;
        }

        @Override public INode<K,V> assoc(AtomicReference<Thread> edit, int shift, int hash, K key, V val, Box addedLeaf){
            int bit = bitpos(hash, shift);
            int idx = index(bit);
            if((bitmap & bit) != 0) {
                Object keyOrNull = array[2*idx];
                Object valOrNode = array[2*idx+1];
                if(keyOrNull == null) {
                    INode<K,V> n = ((INode<K,V>) valOrNode).assoc(edit, shift + 5, hash, key, val, addedLeaf);
                    if(n == valOrNode)
                        return this;
                    return editAndSet(edit, 2*idx+1, n);
                }
                if(Objects.equals(key, keyOrNull)) {
                    if(val == valOrNode)
                        return this;
                    return editAndSet(edit, 2*idx+1, val);
                }
                addedLeaf.val = addedLeaf;
                return editAndSet(edit, 2*idx, null, 2*idx+1,
                                  createNode(edit, shift + 5, keyOrNull, valOrNode, hash, key, val));
            } else {
                int n = Integer.bitCount(bitmap);
                if(n*2 < array.length) {
                    addedLeaf.val = addedLeaf;
                    BitmapIndexedNode<K,V> editable = ensureEditable(edit);
                    System.arraycopy(editable.array, 2*idx, editable.array, 2*(idx+1), 2*(n-idx));
                    editable.array[2*idx] = key;
                    editable.array[2*idx+1] = val;
                    editable.bitmap |= bit;
                    return editable;
                }
                if(n >= 16) {
                    INode[] nodes = new INode[32];
                    int jdx = mask(hash, shift);
                    nodes[jdx] = empty().assoc(edit, shift + 5, hash, key, val, addedLeaf);
                    int j = 0;
                    for(int i = 0; i < 32; i++)
                        if(((bitmap >>> i) & 1) != 0) {
                            if (array[j] == null)
                                nodes[i] = (INode) array[j+1];
                            else
                                nodes[i] = empty().assoc(edit, shift + 5, Objects.hashCode(array[j]), array[j], array[j + 1], addedLeaf);
                            j += 2;
                        }
                    return new ArrayNode(edit, n + 1, nodes);
                } else {
                    Object[] newArray = new Object[2*(n+4)];
                    System.arraycopy(array, 0, newArray, 0, 2*idx);
                    newArray[2*idx] = key;
                    addedLeaf.val = addedLeaf;
                    newArray[2*idx+1] = val;
                    System.arraycopy(array, 2*idx, newArray, 2*(idx+1), 2*(n-idx));
                    BitmapIndexedNode<K,V> editable = ensureEditable(edit);
                    editable.array = newArray;
                    editable.bitmap |= bit;
                    return editable;
                }
            }
        }

        @Override public INode<K,V> without(AtomicReference<Thread> edit, int shift, int hash, K key, Box removedLeaf){
            int bit = bitpos(hash, shift);
            if((bitmap & bit) == 0)
                return this;
            int idx = index(bit);
            Object keyOrNull = array[2*idx];
            Object valOrNode = array[2*idx+1];
            if(keyOrNull == null) {
                INode<K,V> n = ((INode) valOrNode).without(edit, shift + 5, hash, key, removedLeaf);
                if (n == valOrNode)
                    return this;
                if (n != null)
                    return editAndSet(edit, 2*idx+1, n);
                if (bitmap == bit)
                    return null;
                return editAndRemovePair(edit, bit, idx);
            }
            if(Objects.equals(key, keyOrNull)) {
                removedLeaf.val = removedLeaf;
                // TODO: collapse
                return editAndRemovePair(edit, bit, idx);
            }
            return this;
        }
    }

    final static class HashCollisionNode<K,V> implements INode<K,V>{

        final int hash;
        int count;
        Object[] array;
        final AtomicReference<Thread> edit;

        HashCollisionNode(AtomicReference<Thread> edit, int hash, int count, Object... array){
            this.edit = edit;
            this.hash = hash;
            this.count = count;
            this.array = array;
        }

        @Override public INode<K,V> assoc(int shift, int hash, K key, V val, Box addedLeaf){
            if(hash == this.hash) {
                int idx = findIndex(key);
                if(idx != -1) {
                    if(array[idx + 1] == val)
                        return this;
                    return new HashCollisionNode<>(null, hash, count, cloneAndSet(array, idx + 1, val));
                }
                Object[] newArray = new Object[2 * (count + 1)];
                System.arraycopy(array, 0, newArray, 0, 2 * count);
                newArray[2 * count] = key;
                newArray[2 * count + 1] = val;
                addedLeaf.val = addedLeaf;
                return new HashCollisionNode<>(edit, hash, count + 1, newArray);
            }
            // nest it in a bitmap node
            return new BitmapIndexedNode<K,V>(null, bitpos(this.hash, shift), new Object[] {null, this})
                    .assoc(shift, hash, key, val, addedLeaf);
        }

        @Override public INode<K,V> without(int shift, int hash, K key){
            int idx = findIndex(key);
            if(idx == -1)
                return this;
            if(count == 1)
                return null;
            return new HashCollisionNode<>(null, hash, count - 1, removePair(array, idx/2));
        }

        @SuppressWarnings("unchecked")
        @Override public UnMap.UnEntry<K,V> find(int shift, int hash, Object key){
            int idx = findIndex(key);
            if(idx < 0)
                return null;
            if(Objects.equals(key, array[idx]))
                return (UnMap.UnEntry<K,V>) Tuple2.of(array[idx], array[idx + 1]);
            return null;
        }

        @SuppressWarnings("unchecked")
        @Override public V findVal(int shift, int hash, K key, V notFound){
            int idx = findIndex(key);
            if(idx < 0)
                return notFound;
            if (Objects.equals(key, array[idx])) {
                return (V) array[idx + 1];
            }
            return notFound;
        }

        @Override public Sequence<UnMap.UnEntry<K,V>> nodeSeq() { return NodeSeq.create(array); }

        @Override public <R> R kvreduce(Function3<R,K,V,R> f, R init){
            return NodeSeq.kvreduce(array,f,init);
        }

        @Override public <R> R fold(Function2<R,R,R> combinef, Function3<R,K,V,R> reducef,
                                    final Function1<Function0,R> fjtask, final Function1<R,Object> fjfork,
                                    final Function1<Object,R> fjjoin){
            return NodeSeq.kvreduce(array, reducef, combinef.apply(null,null));
        }

        public int findIndex(Object key){
            for(int i = 0; i < 2*count; i+=2)
            {
                if(Objects.equals(key, array[i]))
                    return i;
            }
            return -1;
        }

        private HashCollisionNode<K,V> ensureEditable(AtomicReference<Thread> edit){
            if(this.edit == edit)
                return this;
            Object[] newArray = new Object[2*(count+1)]; // make room for next assoc
            System.arraycopy(array, 0, newArray, 0, 2*count);
            return new HashCollisionNode<>(edit, hash, count, newArray);
        }

        private HashCollisionNode<K,V> ensureEditable(AtomicReference<Thread> edit, int count, Object[] array){
            if(this.edit == edit) {
                this.array = array;
                this.count = count;
                return this;
            }
            return new HashCollisionNode<>(edit, hash, count, array);
        }

        private HashCollisionNode<K,V> editAndSet(AtomicReference<Thread> edit, int i, Object a) {
            HashCollisionNode<K,V> editable = ensureEditable(edit);
            editable.array[i] = a;
            return editable;
        }

        private HashCollisionNode<K,V> editAndSet(AtomicReference<Thread> edit, int i, Object a, int j, Object b) {
            HashCollisionNode<K,V> editable = ensureEditable(edit);
            editable.array[i] = a;
            editable.array[j] = b;
            return editable;
        }


        @Override public INode<K,V> assoc(AtomicReference<Thread> edit, int shift, int hash, K key, V val, Box addedLeaf){
            if(hash == this.hash) {
                int idx = findIndex(key);
                if(idx != -1) {
                    if(array[idx + 1] == val)
                        return this;
                    return editAndSet(edit, idx+1, val);
                }
                if (array.length > 2*count) {
                    addedLeaf.val = addedLeaf;
                    HashCollisionNode<K,V> editable = editAndSet(edit, 2*count, key, 2*count+1, val);
                    editable.count++;
                    return editable;
                }
                Object[] newArray = new Object[array.length + 2];
                System.arraycopy(array, 0, newArray, 0, array.length);
                newArray[array.length] = key;
                newArray[array.length + 1] = val;
                addedLeaf.val = addedLeaf;
                return ensureEditable(edit, count + 1, newArray);
            }
            // nest it in a bitmap node
            return new BitmapIndexedNode<K,V>(edit, bitpos(this.hash, shift), new Object[] {null, this, null, null})
                    .assoc(edit, shift, hash, key, val, addedLeaf);
        }

        @Override public INode<K,V> without(AtomicReference<Thread> edit, int shift, int hash, K key, Box removedLeaf){
            int idx = findIndex(key);
            if(idx == -1)
                return this;
            removedLeaf.val = removedLeaf;
            if(count == 1)
                return null;
            HashCollisionNode<K,V> editable = ensureEditable(edit);
            editable.array[idx] = editable.array[2*count-2];
            editable.array[idx+1] = editable.array[2*count-1];
            editable.array[2*count-2] = editable.array[2*count-1] = null;
            editable.count--;
            return editable;
        }
    }

/*
public static void main(String[] args){
	try
		{
		ArrayList words = new ArrayList();
		Scanner s = new Scanner(new File(args[0]));
		s.useDelimiter(Pattern.compile("\\W"));
		while(s.hasNext())
			{
			String word = s.next();
			words.add(word);
			}
		System.out.println("words: " + words.size());
		ImMap map = PersistentHashMap.EMPTY;
		//ImMap map = new PersistentHashMap();
		//Map ht = new Hashtable();
		Map ht = new HashMap();
		Random rand;

		System.out.println("Building map");
		long startTime = System.nanoTime();
		for(Object word5 : words)
			{
			map = map.assoc(word5, word5);
			}
		rand = new Random(42);
		ImMap snapshotMap = map;
		for(int i = 0; i < words.size() / 200; i++)
			{
			map = map.without(words.get(rand.nextInt(words.size() / 2)));
			}
		long estimatedTime = System.nanoTime() - startTime;
		System.out.println("count = " + map.count() + ", time: " + estimatedTime / 1000000);

		System.out.println("Building ht");
		startTime = System.nanoTime();
		for(Object word1 : words)
			{
			ht.put(word1, word1);
			}
		rand = new Random(42);
		for(int i = 0; i < words.size() / 200; i++)
			{
			ht.remove(words.get(rand.nextInt(words.size() / 2)));
			}
		estimatedTime = System.nanoTime() - startTime;
		System.out.println("count = " + ht.size() + ", time: " + estimatedTime / 1000000);

		System.out.println("map lookup");
		startTime = System.nanoTime();
		int c = 0;
		for(Object word2 : words)
			{
			if(!map.contains(word2))
				++c;
			}
		estimatedTime = System.nanoTime() - startTime;
		System.out.println("notfound = " + c + ", time: " + estimatedTime / 1000000);
		System.out.println("ht lookup");
		startTime = System.nanoTime();
		c = 0;
		for(Object word3 : words)
			{
			if(!ht.containsKey(word3))
				++c;
			}
		estimatedTime = System.nanoTime() - startTime;
		System.out.println("notfound = " + c + ", time: " + estimatedTime / 1000000);
		System.out.println("snapshotMap lookup");
		startTime = System.nanoTime();
		c = 0;
		for(Object word4 : words)
			{
			if(!snapshotMap.contains(word4))
				++c;
			}
		estimatedTime = System.nanoTime() - startTime;
		System.out.println("notfound = " + c + ", time: " + estimatedTime / 1000000);
		}
	catch(FileNotFoundException e)
		{
		e.printStackTrace();
		}

}
*/

    private static <K,V> INode<K,V>[] cloneAndSet(INode<K,V>[] array, int i, INode<K,V> a) {
        INode<K,V>[] clone = array.clone();
        clone[i] = a;
        return clone;
    }

    private static Object[] cloneAndSet(Object[] array, int i, Object a) {
        Object[] clone = array.clone();
        clone[i] = a;
        return clone;
    }

    private static Object[] cloneAndSet(Object[] array, int i, Object a, int j, Object b) {
        Object[] clone = array.clone();
        clone[i] = a;
        clone[j] = b;
        return clone;
    }

    private static Object[] removePair(Object[] array, int i) {
        Object[] newArray = new Object[array.length - 2];
        System.arraycopy(array, 0, newArray, 0, 2*i);
        System.arraycopy(array, 2*(i+1), newArray, 2*i, newArray.length - 2*i);
        return newArray;
    }

    private static <K,V> INode<K,V> createNode(int shift, K key1, V val1, int key2hash, K key2, V val2) {
        int key1hash = Objects.hashCode(key1);
        if(key1hash == key2hash)
            return new HashCollisionNode<>(null, key1hash, 2, new Object[] {key1, val1, key2, val2});
        Box addedLeaf = new Box(null);
        AtomicReference<Thread> edit = new AtomicReference<>();
        return BitmapIndexedNode.<K,V>empty()
                .assoc(edit, shift, key1hash, key1, val1, addedLeaf)
                .assoc(edit, shift, key2hash, key2, val2, addedLeaf);
    }

    private static <K,V> INode<K,V> createNode(AtomicReference<Thread> edit, int shift, K key1, V val1, int key2hash, K key2, V val2) {
        int key1hash = Objects.hashCode(key1);
        if(key1hash == key2hash)
            return new HashCollisionNode<>(null, key1hash, 2, new Object[] {key1, val1, key2, val2});
        Box addedLeaf = new Box(null);
        return BitmapIndexedNode.<K,V>empty()
                .assoc(edit, shift, key1hash, key1, val1, addedLeaf)
                .assoc(edit, shift, key2hash, key2, val2, addedLeaf);
    }

    private static int bitpos(int hash, int shift){
        return 1 << mask(hash, shift);
    }

    static final class NodeSeq<K,V> implements Sequence<UnMap.UnEntry<K,V>> {
        private final Object[] array;
        private final int i;
        private final Sequence<UnMap.UnEntry<K,V>> s;

//        NodeSeq(Object[] array, int i) {
//            this(null, array, i, null);
//        }

        static <K,V> Sequence<UnMap.UnEntry<K,V>> create(Object[] array) {
            return create(array, 0, null);
        }

        static public <K, V, R> R kvreduce(Object[] array, Function3<R,K,V,R> f, R init) {
            for (int i = 0; i < array.length; i += 2) {
                if (array[i] != null) {
                    init = f.apply(init, k(array, i), v(array, i + 1));
                } else {
                    INode<K,V> node = iNode(array, i + 1);
                    if (node != null)
                        init = node.kvreduce(f, init);
                }
                if (isReduced(init)) {
                    return init;
                }
            }
            return init;
        }

        private static <K,V> Sequence<UnMap.UnEntry<K,V>> create(Object[] array, int i, Sequence<UnMap.UnEntry<K,V>> s) {
            if ( (s != null) && (s != Sequence.EMPTY_SEQUENCE) ) { return new NodeSeq<>(array, i, s); }

            for (int j = i; j < array.length; j += 2) {
                if (array[j] != null) { return new NodeSeq<>(array, j, null); }

                INode<K,V> node = iNode(array, j + 1);
                if (node != null) {
                    Sequence<UnMap.UnEntry<K,V>> nodeSeq = node.nodeSeq();

                    if (nodeSeq != null) { return new NodeSeq<>(array, j + 2, nodeSeq); }
                }
            }
            return Sequence.emptySequence();
        }

        private NodeSeq(Object[] array, int i, Sequence<UnMap.UnEntry<K,V>> s) {
            super();
            this.array = array;
            this.i = i;
            this.s = s;
        }

        @Override public Option<UnMap.UnEntry<K,V>> head() {
            return ( (s != null) && (s != Sequence.EMPTY_SEQUENCE) ) ? s.head() :
                   i < array.length - 1 ? Option.of(Tuple2.of(k(array, i), v(array, i+1))) :
                   Option.none();
        }

        @Override public Sequence<UnMap.UnEntry<K,V>> tail() {
            if ( (s != null) && (s != Sequence.EMPTY_SEQUENCE) ) {
                return create(array, i, s.tail());
            }
            return create(array, i + 2, null);
        }

//        static public <K, V, R> R kvreduce(Object[] array, Function3<R,K,V,R> f, R init) {
//            for (int i = 0; i < array.length; i += 2) {
//                if (array[i] != null) {
//                    init = f.apply(init, k(array, i), v(array, i + 1));
//                } else {
//                    INode<K,V> node = iNode(array, i + 1);
//                    if (node != null)
//                        init = node.kvreduce(f, init);
//                }
//                if (isReduced(init)) {
//                    return init;
//                }
//            }
//            return init;
//        }

//        private final LazyRef<Tuple2<Option<UnEntry<K,V>>,Sequence<UnEntry<K,V>>>> laz;
//
//        @SuppressWarnings("unchecked")
//        private NodeSeq(int i, Object[] array) {
//            laz = LazyRef.of(() -> Tuple2.of(Option.of(Tuple2.of(k(array, i), v(array, i+1))),
//                                             (i >= (array.length - 2))
//                                             ? Sequence.emptySequence()
//                                             : new NodeSeq<>(i + 2, array)));
//        }
//
//        @SuppressWarnings("unchecked")
//        static <K,V> Sequence<UnMap.UnEntry<K,V>> create(Object[] array, int startIdx, Sequence<UnMap.UnEntry<K,V>> s) {
//            if (startIdx < 0) { throw new IllegalArgumentException("Start index must be >= 0"); }
//            if ( (s != null) && (s != Sequence.EMPTY_SEQUENCE) ) {
//                return s.concat(create(array, startIdx, null));
//            }
//            if ( (array == null) || (array.length < 1) || (startIdx > (array.length - 2)) ) {
//                return Sequence.emptySequence();
//            }
//            return new NodeSeq<>(startIdx, array);
//        }
//
//        static <K,V> Sequence<UnMap.UnEntry<K,V>> create(Object[] array) { return create(array, 0, null); }
//
//        @Override public Option<UnMap.UnEntry<K,V>> head() { return laz.get()._1(); }
//
//        @Override public Sequence<UnMap.UnEntry<K,V>> tail() { return laz.get()._2(); }
//
        @Override public String toString() { return UnIterable.toString("NodeSeq", this); }

    } // end class NodeSeq
}
