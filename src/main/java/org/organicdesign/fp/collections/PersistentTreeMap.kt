/*
 Copyright (c) Rich Hickey. All rights reserved. The use and distribution terms for this software
 are covered by the Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
 which can be found in the file epl-v10.html at the root of this distribution. By using this
 software in any fashion, you are agreeing to be bound by the terms of this license. You must not
 remove this notice, or any other, from this software.
 */

/* rich May 20, 2006 */
package org.organicdesign.fp.collections

import java.io.IOException
import java.io.InvalidObjectException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable
import java.util.ArrayDeque
import java.util.Comparator
import java.util.NoSuchElementException
import java.util.Stack

import org.organicdesign.fp.oneOf.Option
import org.organicdesign.fp.tuple.Tuple2

import org.organicdesign.fp.FunctionUtils.stringify
import kotlin.collections.MutableMap.MutableEntry

/**
 * Persistent Red Black Tree. Note that instances of this class are constant values
 * i.e. add/remove etc return new values.
 *
 * See Okasaki, Kahrs, Larsen et al
 *
 * This file is a derivative work based on a Clojure collection licensed under the Eclipse Public
 * License 1.0 Copyright Rich Hickey
 *
 * @author Rich Hickey: Original author
 * @author Glen Peterson: Added generic types, static factories, custom serialization, and made Nodes
 * extend Tuple2.  All errors are Glen's.
 */

class PersistentTreeMap<K, V>
private constructor(
        private val comp: Comparator<in K>,
        @field:Transient private val tree: Node<K, V>?,
        override val size: Int
) : AbstractUnmodMap<K, V>(), ImSortedMap<K, V>, Serializable {

    /**
     * This is a throw-away class used internally by PersistentTreeMap and PersistentHashMap like
     * a mutable Option class to hold either null, or some value.  I don't want to remove this
     * without checking the effect on performance.
     */
    internal class Box<E>(var value: E?)

    /**
     * This would be private, except that PersistentTreeSet needs to check that the wrapped
     * comparator is serializable.
     */
    private class KeyComparator<T>(
            val wrappedComparator: Comparator<in T>
    ) : Comparator<MutableEntry<T, *>>, Serializable {

        override fun compare(a: MutableEntry<T, *>, b: MutableEntry<T, *>): Int =
                wrappedComparator.compare(a.key, b.key)

        override fun toString(): String = "KeyComparator($wrappedComparator)"

        companion object {
            private const val serialVersionUID = 20160827174100L
        }
    }

    // Check out Josh Bloch Item 78, p. 312 for an explanation of what's going on here.
    private class SerializationProxy<K, V>(@field:Transient private var theMap: PersistentTreeMap<K, V>?) :
            Serializable {

        private val comparator: Comparator<in K>
        private val size: Int

        init {
            val tempMap = theMap!!
            comparator = tempMap.comp
            if (comparator !is Serializable) {
                throw IllegalStateException("Comparator must equal serializable." +
                                            "  Instead it was " + comparator)
            }
            size = tempMap.size
        }

        // Taken from Josh Bloch Item 75, p. 298
        @Throws(IOException::class)
        private fun writeObject(s: ObjectOutputStream) {
            s.defaultWriteObject()

            // Serializing in iteration-order yields a worst-case deserialization because
            // without re-balancing (rotating nodes) such an order yields an completely unbalanced
            // linked list internal structure.
            //       4
            //      /
            //     3
            //    /
            //   2
            //  /
            // 1
            //
            // That seems unnecessary since before Serialization we might have something like this
            // which, while not perfect, requires no re-balancing:
            //
            //                    11
            //            ,------'  `----.
            //           8                14
            //        ,-' `-.            /  \
            //       4       9         13    15
            //    ,-' `-.     \       /        \
            //   2       6     10   12          16
            //  / \     / \
            // 1   3   5   7
            //
            // If we serialize the middle value (n/2) first.  Then the n/4 and 3n/4,
            // followed by n/8, 3n/8, 5n/8, 7n/8, then n/16, 3n/16, etc.  Finally, the odd-numbered
            // values last.  That gives us the order:
            // 8, 4, 12, 2, 6, 10, 14, 1, 3, 5, 7, 9, 11, 13, 15
            //
            // Deserializing in that order yields an ideally balanced tree without any shuffling:
            //               8
            //        ,-----' `-------.
            //       4                 12
            //    ,-' `-.          ,--'  `--.
            //   2       6       10          14
            //  / \     / \     /  \        /  \
            // 1   3   5   7   9    11    13    15
            //
            // That would be ideal, but I don't see how to do that without a significant
            // intermediate data structure.
            //
            // A good improvement could be made by serializing breadth-first instead of depth first
            // to at least yield a tree no worse than the original without requiring shuffling.
            //
            // This improvement does not change the serialized form, or break compatibility.
            // But it has a superior ordering for deserialization without (or with minimal)
            // rotations.

            //            System.out.println("Serializing tree map...");
            if (theMap!!.tree != null) {
                val queue = ArrayDeque<Node<K, V>>()
                queue.add(theMap!!.tree)
                while (queue.peek() != null) {
                    val node = queue.remove()
                    //                    System.out.println("Node: " + node);
                    s.writeObject(node.key)
                    s.writeObject(node.value)
                    var child = node.left()
                    if (child != null) {
                        queue.add(child)
                    }
                    child = node.right()
                    if (child != null) {
                        queue.add(child)
                    }
                }
            }
            //            for (UnEntry<K,V> entry : theMap) {
            //                s.writeObject(entry.getKey());
            //                s.writeObject(entry.getValue());
            //            }
        }

        @Throws(IOException::class, ClassNotFoundException::class)
        private fun readObject(s: ObjectInputStream) {
            s.defaultReadObject()
            theMap = PersistentTreeMap(comparator, null, 0)
            for (i in 0 until size) {
                @Suppress("UNCHECKED_CAST")
                theMap = theMap!!.assoc(s.readObject() as K, s.readObject() as V)
            }
        }

        private fun readResolve(): Any? {
            return theMap
        }

        companion object {
            // For serializable.  Make sure to change whenever internal data format changes.
            private const val serialVersionUID = 20160904095000L
        }
    }

    private fun writeReplace(): Any {
        return SerializationProxy(this)
    }

    @Throws(IOException::class, ClassNotFoundException::class)
    @Suppress("UNUSED_PARAMETER") // We need to match a java method that takes this parameter.
    private fun readObject(ignore: java.io.ObjectInputStream) {
        throw InvalidObjectException("Proxy required")
    }

    // ===================================== Instance Methods =====================================
    /**
     * Returns a view of the mappings contained in this map.  The set should actually contain
     * UnmodMap.UnEntry items, but that return signature is illegal in Java, so you'll just have to
     * remember.
     */
    override val entries: MutableSet<MutableEntry<K, V>>
        // This is the pretty way to do it.
        @Suppress("UNCHECKED_CAST")
        get() = this.toImSortedSet(KeyComparator(comp)).toMutSet() as MutableSet<MutableEntry<K, V>>


    //    public static final Equator<SortedMap> EQUATOR = new Equator<SortedMap>() {
    //        @Override
    //        public int hash(SortedMap kvSortedMap) {
    //            return UnmodIterable.hashCode(kvSortedMap.entrySet());
    //        }
    //
    //        @Override
    //        public boolean eq(SortedMap o1, SortedMap o2) {
    //            if (o1 == o2) { return true; }
    //            if ( o1.size() != o2.size() ) { return false; }
    //            return UnmodSortedIterable.equals(UnmodSortedIterable.castFromSortedMap(o1),
    //                                              UnmodSortedIterable.castFromSortedMap(o2));
    //        }
    //    };

    //    /** Returns a view of the keys contained in this map. */
    //    @Override public ImSet<K> keySet() { return PersistentTreeSet.ofMap(this); }

    /** {@inheritDoc}  */
    override fun subMap(fromKey: K, toKey: K): ImSortedMap<K, V> {
        val diff = comp.compare(fromKey, toKey)

        if (diff > 0) {
            throw IllegalArgumentException("fromKey is greater than toKey")
        }
        val last = last()
        val lastKey = last!!.key
        val compFromKeyLastKey = comp.compare(fromKey, lastKey)

        // If no intersect, return empty. We aren't checking the toKey vs. the firstKey() because
        // that's a single pass through the iterator loop which is probably as cheap as checking
        // here.
        if (diff == 0 || compFromKeyLastKey > 0) {
            return PersistentTreeMap(comp, null, 0)
        }
        // If map is entirely contained, just return it.
        if (comp.compare(fromKey, firstKey()) <= 0 && comp.compare(toKey, lastKey) > 0) {
            return this
        }
        // Don't iterate through entire map for only the last item.
        if (compFromKeyLastKey == 0) {
            return ofComp(comp, listOf<MutableEntry<K, V>>(last))
        }

        var ret: ImSortedMap<K, V> = PersistentTreeMap(comp, null, 0)
        val iter = this.iterator()
        while (iter.hasNext()) {
            val next = iter.next()
            val key = next.key
            if (comp.compare(toKey, key) <= 0) {
                break
            }
            if (comp.compare(fromKey, key) > 0) {
                continue
            }
            ret = ret.assoc(key, next.value)
        }
        return ret
    }

    //    String debugStr() {
    //        return "PersistentTreeMap(size=" + size +
    //               " comp=" + comp +
    //               " tree=" + tree + ")";
    //    }

    //    /** {@inheritDoc} */
    //    @Override public UnmodCollection<V> values() {
    //        class ValueColl<B,Z> implements UnmodCollection<B>, UnmodSortedIterable<B> {
    //            private final Fn0<UnmodSortedIterator<UnEntry<Z,B>>> iterFactory;
    //            private ValueColl(Fn0<UnmodSortedIterator<UnEntry<Z, B>>> f) { iterFactory = f; }
    //
    //            @Override public int size() { return size; }
    //
    //            @Override public UnmodSortedIterator<B> iterator() {
    //                final UnmodSortedIterator<UnmodMap.UnEntry<Z,B>> iter = iterFactory.invoke();
    //                return new UnmodSortedIterator<B>() {
    //                    @Override public boolean hasNext() { return iter.hasNext(); }
    //                    @Override public B next() { return iter.next().getValue(); }
    //                };
    //            }
    //            @Override public int hashCode() { return UnmodIterable.hashCode(this); }
    //            @Override public boolean equals(Object o) {
    //                if (this == o) { return true; }
    //                if ( !(o instanceof UnmodSortedIterable) ) { return false; }
    //                return UnmodSortedIterable.equals(this, (UnmodSortedIterable) o);
    //            }
    //            @Override public String toString() {
    //                return UnmodSortedIterable.toString("ValueColl", this);
    //            }
    //        }
    //        return new ValueColl<>(() -> this.iterator());
    //    }

    /** {@inheritDoc}  */
    override fun head(): Option<UnmodMap.UnEntry<K, V>> {
        var t = tree
        if (t != null) {
            while (t!!.left() != null) {
                t = t.left()
            }
        }
        return Option.some(t)
    }

    /** {@inheritDoc}  */
    override fun tailMap(fromKey: K): ImSortedMap<K, V> {
        val last = last()
        val lastKey = last!!.key
        val compFromKeyLastKey = comp.compare(fromKey, lastKey)

        // If no intersect, return empty. We aren't checking the toKey vs. the firstKey() because
        // that's a single pass through the iterator loop which is probably as cheap as checking
        // here.
        if (compFromKeyLastKey > 0) {
            return PersistentTreeMap(comp, null, 0)
        }
        // If map is entirely contained, just return it.
        if (comp.compare(fromKey, firstKey()) <= 0) {
            return this
        }
        // Don't iterate through entire map for only the last item.
        if (compFromKeyLastKey == 0) {
            return ofComp(comp, listOf<MutableEntry<K, V>>(last))
        }

        var ret: ImSortedMap<K, V> = PersistentTreeMap(comp, null, 0)
        val iter = this.iterator()
        while (iter.hasNext()) {
            val next = iter.next()
            val key = next.key
            if (comp.compare(fromKey, key) > 0) {
                continue
            }
            ret = ret.assoc(key, next.value)
        }
        return ret
    }

    //    /** {@inheritDoc} */
    //    @Override public Sequence<UnEntry<K,V>> tail() {
    //        if (size() > 1) {
    //            return without(firstKey());
    ////            // The iterator is designed to do this quickly.  It also prevents an infinite loop.
    ////            UnmodIterator<UnEntry<K,V>> iter = this.iterator();
    ////            // Drop the head
    ////            iter.next();
    ////            return tailMap(iter.next().getKey());
    //        }
    //        return Sequence.emptySequence();
    //    }

    //    @SuppressWarnings("unchecked")
    //    static public <S, K extends S, V extends S> PersistentTreeMap<K,V> create(ISeq<S> items) {
    //        PersistentTreeMap<K,V> ret = empty();
    //        for (; items != null; items = items.next().next()) {
    //            if (items.next() == null)
    //                throw new IllegalArgumentException(String.format("No value supplied for key: %s",
    //                                                                 items.head()));
    //            ret = ret.assoc((K) items.head(), (V) RT.second(items));
    //        }
    //        return ret;
    //    }

    //    @SuppressWarnings("unchecked")
    //    static public <S, K extends S, V extends S>
    //    PersistentTreeMap<K,V> create(Comparator<? super K> comp, ISeq<S> items) {
    //        PersistentTreeMap<K,V> ret = new PersistentTreeMap<>(comp);
    //        for (; items != null; items = items.next().next()) {
    //            if (items.next() == null)
    //                throw new IllegalArgumentException(String.format("No value supplied for key: %s",
    //                                                                 items.head()));
    //            ret = ret.assoc((K) items.head(), (V) RT.second(items));
    //        }
    //        return ret;
    //    }

    /**
     * Returns the comparator used to order the keys in this map, or null if it uses
     * Fn2.DEFAULT_COMPARATOR (for compatibility with java.util.SortedMap).
     */
    override fun comparator(): Comparator<in K> = comp

    //    /** Returns true if the map contains the given key. */
    //    @SuppressWarnings("unchecked")
    //    @Override public boolean containsKey(Object key) {
    //        return entryAt((K) key) != null;
    //    }

    //    /** Returns the value associated with the given key. */
    //    @SuppressWarnings("unchecked")
    //    @Override
    //    public V get(Object key) {
    //        if (key == null) { return null; }
    //        MutableEntry<K,V> entry = entryAt((K) key);
    //        if (entry == null) { return null; }
    //        return entry.getValue();
    //    }

    // public PersistentTreeMap<K,V> assocEx(K key, V val) {
    // Inherits default implementation of assocEx from IPersistentMap

    /** {@inheritDoc}  */
    override fun assoc(key: K, value: V): PersistentTreeMap<K, V> {
        val found = Box<Node<K, V>>(null)
        val t = add(tree, key, value, found)
        //null == already contains key
        if (t == null) {
            val foundNode = found.value

            //note only get same collection on identity of val, not equals()
            return if (foundNode!!.value === value) {
                this
            } else PersistentTreeMap(comp, replace(tree!!, key, value), size)
        }
        return PersistentTreeMap(comp, t.blacken(), size + 1)
    }

    /** {@inheritDoc}  */
    override fun without(key: K): PersistentTreeMap<K, V> {
        val found = Box<Node<K, V>>(null)
        val t = remove(tree, key, found)
                ?: //null == doesn't contain key
                return if (found.value == null) {
                    this
                } else PersistentTreeMap(comp, null, 0)
        //empty
        return PersistentTreeMap(comp, t.blacken(), size - 1)
    }

    //    @Override
    //    public ISeq<Map.MutableEntry<K,V>> seq() {
    //        if (size > 0)
    //            return Iter.create(tree, true, size);
    //        return null;
    //    }
    //
    //    @Override
    //    public ISeq<Map.MutableEntry<K,V>> rseq() {
    //        if (size > 0)
    //            return Iter.create(tree, false, size);
    //        return null;
    //    }

    //    @Override
    //    public Object entryKey(Map.MutableEntry<K,V> entry) {
    //        return entry.getKey();
    //    }

    //    // This lets you make a sequence of map entries from this HashMap.
    //// The other methods on Sorted seem to care only about the key, and the implementations of them
    //// here work that way.  This one, however, returns a sequence of Map.MutableEntry<K,V> or Node<K,V>
    //// If I understood why, maybe I could do better.
    //    @SuppressWarnings("unchecked")
    //    @Override
    //    public ISeq<Map.MutableEntry<K,V>> seq(boolean ascending) {
    //        if (size > 0)
    //            return Iter.create(tree, ascending, size);
    //        return null;
    //    }

    //    @SuppressWarnings("unchecked")
    //    @Override
    //    public ISeq<Map.MutableEntry<K,V>> seqFrom(Object key, boolean ascending) {
    //        if (size > 0) {
    //            ISeq<Node<K,V>> stack = null;
    //            Node<K,V> t = tree;
    //            while (t != null) {
    //                int c = doCompare((K) key, t.key);
    //                if (c == 0) {
    //                    stack = RT.cons(t, stack);
    //                    return new Iter<>(stack, ascending);
    //                } else if (ascending) {
    //                    if (c < 0) {
    //                        stack = RT.cons(t, stack);
    //                        t = t.left();
    //                    } else
    //                        t = t.right();
    //                } else {
    //                    if (c > 0) {
    //                        stack = RT.cons(t, stack);
    //                        t = t.right();
    //                    } else
    //                        t = t.left();
    //                }
    //            }
    //            if (stack != null)
    //                return new Iter<>(stack, ascending);
    //        }
    //        return null;
    //    }

    /** {@inheritDoc}  */
    override fun iterator(): UnmodSortedIterator<UnmodMap.UnEntry<K, V>> =
            NodeIterator(tree as Node<K, V>, true)

    //    public NodeIterator<K,V> reverseIterator() { return new NodeIterator<>(tree, false); }

    /** Returns the first key in this map or throws a NoSuchElementException if the map is empty.  */
    override fun firstKey(): K =
            if (size < 1) {
                throw NoSuchElementException("this map is empty")
            } else {
                head().get().key
            }

    /** Returns the last key in this map or throws a NoSuchElementException if the map is empty.  */
    override fun lastKey(): K {
        val max = last() ?: throw NoSuchElementException("this map is empty")
        return max.key
    }

    /** Returns the last key/value pair in this map, or null if the map is empty.  */
    fun last(): UnmodMap.UnEntry<K, V>? {
        var t = tree
        if (t != null) {
            while (t!!.right() != null)
                t = t.right()
        }
        return t
    }

    //    public int depth() {
    //        return depth(tree);
    //    }

    //    int depth(Node<K,V> t) {
    //        if (t == null)
    //            return 0;
    //        return 1 + Math.max(depth(t.left()), depth(t.right()));
    //    }

    // public Object valAt(Object key){
    // Default implementation now inherited from ILookup

    /**
     * Returns an Option of the key/value pair matching the given key, or Option.none() if the key is
     * not found.
     */
    override fun entry(key: K): Option<UnmodMap.UnEntry<K, V>> {
        var t = tree
        while (t != null) {
            val c = comp.compare(key, t.key)
            if (c == 0)
                return Option.some(t)
            else if (c < 0)
                t = t.left()
            else
                t = t.right()
        }
        return Option.none() // t; // t is always null
    }

    //    // In TreeMap, this is final MutableEntry<K,V> getEntry(Object key)
    //    /** Returns the key/value pair matching the given key, or null if the key is not found. */
    //    public UnEntry<K,V> entryAt(K key) {
    //        Node<K,V> t = tree;
    //        while (t != null) {
    //            int c = comp.compare(key, t.key);
    //            if (c == 0)
    //                return t;
    //            else if (c < 0)
    //                t = t.left();
    //            else
    //                t = t.right();
    //        }
    //        return null; // t; // t is always null
    //    }

    private fun add(t: Node<K, V>?, key: K, value: V, found: Box<Node<K, V>>): Node<K, V>? {
        if (t == null) {
            //            if (val == null)
            //                return new Red<>(key);
            return Red(key, value)
        }
        val c = comp.compare(key, t.key)
        if (c == 0) {
            found.value = t
            return null
        }
        val ins = add(if (c < 0) t.left() else t.right(),
                      key, value, found) ?: //found below
                  return null
        return if (c < 0) t.addLeft(ins) else t.addRight(ins)
    }

    private fun remove(t: Node<K, V>?, key: K, found: Box<Node<K, V>>): Node<K, V>? {
        if (t == null)
            return null //not found indicator
        val c = comp.compare(key, t.key)
        if (c == 0) {
            found.value = t
            return append(t.left(), t.right())
        }
        val del = remove(if (c < 0) t.left() else t.right(),
                         key, found)
        if (del == null && found.value == null)
        //not found below
            return null
        if (c < 0) {
            return if (t.left() is PersistentTreeMap.Black<*, *>)
                balanceLeftDel(t.key, t.value, del, t.right())
            else
                red(t.key, t.value, del, t.right())
        }
        return if (t.right() is PersistentTreeMap.Black<*, *>) balanceRightDel(t.key,
                                                                               t.value,
                                                                               t.left(),
                                                                               del) else red(t.key,
                                                                                             t.value,
                                                                                             t.left(),
                                                                                             del)
//		return t.removeLeft(del);
        //	return t.removeRight(del);
    }

    private fun replace(t: Node<K, V>, key: K, value: V): Node<K, V> {
        val c = comp.compare(key, t.key)
        return t.replace(t.key,
                         if (c == 0) value else t.value,
                         if (c < 0) replace(t.left()!!, key, value) else t.left(),
                         if (c > 0) replace(t.right()!!, key, value) else t.right())
    }

    //    public static class Reduced<A> {
    //        public final A val;
    //        private Reduced(A a) { val = a; }
    //    }

    private abstract class Node<K, V>(key: K, value: V) : Tuple2<K, V>(key, value) {

        open fun left(): Node<K, V>? = null

        open fun right(): Node<K, V>? = null

        abstract fun addLeft(ins: Node<K, V>): Node<K, V>

        abstract fun addRight(ins: Node<K, V>): Node<K, V>

        abstract fun removeLeft(del: Node<K, V>): Node<K, V>

        abstract fun removeRight(del: Node<K, V>): Node<K, V>

        abstract fun blacken(): Node<K, V>

        abstract fun redden(): Node<K, V>

        open fun balanceLeft(parent: Node<K, V>): Node<K, V> =
                black(parent._1, parent._2, this, parent.right())

        open fun balanceRight(parent: Node<K, V>): Node<K, V> =
                black(parent._1, parent._2, parent.left(), this)

        abstract fun replace(key: K, value: V, left: Node<K, V>?, right: Node<K, V>?): Node<K, V>

        override fun toString(): String = stringify(_1) + "=" + stringify(_2)

        //        public <R> R kvreduce(Fn3<R,K,V,R> f, R init) {
        //            if (left() != null) {
        //                init = left().kvreduce(f, init);
        //                if (init instanceof Reduced)
        //                    return init;
        //            }
        //            init = f.invoke(init, key(), val());
        //            if (init instanceof Reduced)
        //                return init;
        //
        //            if (right() != null) {
        //                init = right().kvreduce(f, init);
        //            }
        //            return init;
        //        }
    } // end class Node.

    private open class Black<K, V> internal constructor(key: K, value: V) : Node<K, V>(key, value) {

        override fun addLeft(ins: Node<K, V>): Node<K, V> = ins.balanceLeft(this)

        override fun addRight(ins: Node<K, V>): Node<K, V> = ins.balanceRight(this)

        override fun removeLeft(del: Node<K, V>): Node<K, V> = balanceLeftDel(_1, _2, del, right())

        override fun removeRight(del: Node<K, V>): Node<K, V> = balanceRightDel(_1, _2, left(), del)

        override fun blacken(): Node<K, V> = this

        override fun redden(): Node<K, V> = Red(_1, _2)

        override fun replace(key: K, value: V, left: Node<K, V>?, right: Node<K, V>?): Node<K, V> =
                black(key, value, left, right)
    }

    private class BlackBranch<K, V>(
            key: K,
            value: V,
            @field:Transient internal val left: Node<K, V>,
            @field:Transient internal val right: Node<K, V>
    ) : Black<K, V>(key, value) {

        override fun left(): Node<K, V>? = left

        override fun right(): Node<K, V>? = right

        override fun redden(): Node<K, V> = RedBranch(_1, _2, left, right)
    }

    private open class Red<K, V>(key: K, value: V) : Node<K, V>(key, value) {

        override fun addLeft(ins: Node<K, V>): Node<K, V> = red(_1, _2, ins, right())

        override fun addRight(ins: Node<K, V>): Node<K, V> = red(_1, _2, left(), ins)

        override fun removeLeft(del: Node<K, V>): Node<K, V> = red(_1, _2, del, right())

        override fun removeRight(del: Node<K, V>): Node<K, V> = red(_1, _2, left(), del)

        override fun blacken(): Node<K, V> = Black(_1, _2)

        override fun redden(): Node<K, V> = throw UnsupportedOperationException("Invariant violation")

        override fun replace(key: K, value: V, left: Node<K, V>?, right: Node<K, V>?): Node<K, V> =
                red(key, value, left, right)
    }

    private class RedBranch<K, V> internal constructor(key: K,
                                                       value: V, @field:Transient internal val left: Node<K, V>, @field:Transient internal val right: Node<K, V>) :
            Red<K, V>(key, value) {

        override fun left(): Node<K, V>? = left

        override fun right(): Node<K, V>? = right

        override fun balanceLeft(parent: Node<K, V>): Node<K, V> {
            return if (left is PersistentTreeMap.Red<*, *>)
                red(_1, _2, left.blacken(),
                    black(parent.key, parent.value, right, parent.right()))
            else if (right is PersistentTreeMap.Red<*, *>)
                red(right.key, right.value, black(_1, _2, left, right.left()),
                    black(parent.key, parent.value, right.right(), parent.right()))
            else
                super.balanceLeft(parent)

        }

        override fun balanceRight(parent: Node<K, V>): Node<K, V> {
            return if (right is PersistentTreeMap.Red<*, *>)
                red(_1, _2,
                    black(parent.key, parent.value, parent.left(), left),
                    right.blacken())
            else if (left is PersistentTreeMap.Red<*, *>)
                red(left.key, left.value,
                    black(parent.key, parent.value, parent.left(), left.left()),
                    black(_1, _2, left.right(), right))
            else
                super.balanceRight(parent)
        }

        override fun blacken(): Node<K, V> {
            return BlackBranch(_1, _2, left, right)
        }
    }


    //    static public class Iter<K, V> extends ASeq<Map.MutableEntry<K,V>> {
    //        final ISeq<Node<K,V>> stack;
    //        final boolean asc;
    //        final int cnt;
    //
    //        public Iter(ISeq<Node<K,V>> stack, boolean asc) {
    //            this.stack = stack;
    //            this.asc = asc;
    //            this.cnt = -1;
    //        }
    //
    //        public Iter(ISeq<Node<K,V>> stack, boolean asc, int cnt) {
    //            this.stack = stack;
    //            this.asc = asc;
    //            this.cnt = cnt;
    //        }
    //
    //        Iter(ISeq<Node<K,V>> stack, boolean asc, int cnt) {
    //            super();
    //            this.stack = stack;
    //            this.asc = asc;
    //            this.cnt = cnt;
    //        }
    //
    //        static <K, V> Iter<K,V> create(Node<K,V> t, boolean asc, int cnt) {
    //            return new Iter<>(push(t, null, asc), asc, cnt);
    //        }
    //
    //        static <K, V> ISeq<Node<K,V>> push(Node<K,V> t, ISeq<Node<K,V>> stack, boolean asc) {
    //            while (t != null) {
    //                stack = RT.cons(t, stack);
    //                t = asc ? t.left() : t.right();
    //            }
    //            return stack;
    //        }
    //
    //        @Override
    //        public Node<K,V> head() {
    //            return stack.head();
    //        }
    //
    //        @Override
    //        public ISeq<Map.MutableEntry<K,V>> next() {
    //            Node<K,V> t = stack.head();
    //            ISeq<Node<K,V>> nextstack = push(asc ? t.right() : t.left(), stack.next(), asc);
    //            if (nextstack != null) {
    //                return new Iter<>(nextstack, asc, cnt - 1);
    //            }
    //            return null;
    //        }
    //
    //        @Override
    //        public int count() {
    //            if (cnt < 0)
    //                return super.count();
    //            return cnt;
    //        }
    //    }

    /**
     * This currently returns chunks of the inner tree structure that implement Map.MutableEntry.
     * They are not serializable and should not be made so.  I can alter this to return nice,
     * neat, Tuple2 objects which are serializable, but we've made it this far without so...
     */
    private class NodeIterator<K, V> internal constructor(t: Node<K, V>, private val asc: Boolean) :
            UnmodSortedIterator<UnmodMap.UnEntry<K, V>> {
        //, Serializable {
        // For serializable.  Make sure to change whenever internal data format changes.
        // private static final long serialVersionUID = 20160827174100L;

        private val stack = Stack<Node<K, V>>()

        init {
            push(t)
        }

        private fun push(t: Node<K, V>?) {
            var t = t
            while (t != null) {
                stack.push(t)
                t = if (asc) t.left() else t.right()
            }
        }

        override fun hasNext(): Boolean {
            return !stack.isEmpty()
        }

        override fun next(): UnmodMap.UnEntry<K, V> {
            val t = stack.pop()
            push(if (asc) t.right() else t.left())

            return Tuple2.of(t)
        }
    }

    companion object {

        /**
         * Returns a new PersistentTreeMap of the given comparable keys and their paired values, skipping
         * any null Entries.
         */
        fun <K : Comparable<K>, V> of(es: Iterable<Map.Entry<K, V>?>?): PersistentTreeMap<K, V> {
            if (es == null) {
                return empty()
            }
            var map: PersistentTreeMap<K, V> = PersistentTreeMap(Equator.defaultComparator(), null, 0)
            for (entry in es) {
                if (entry != null) {
                    map = map.assoc(entry.key, entry.value)
                }
            }
            return map
        }

        /**
         * Returns a new PersistentTreeMap of the specified comparator and the given key/value pairs.
         *
         * @param comp A comparator (on the keys) that defines the sort order inside the new map.  This
         * becomes a permanent part of the map and all sub-maps or appended maps derived from it.  If you
         * want to use a null key, make sure the comparator treats nulls correctly in all circumstances!
         * @param kvPairs Key/value pairs (to go into the map).  In the case of a duplicate key, later
         * values in the input list overwrite the earlier ones.  The resulting map can contain zero or
         * one null key (if your comparator knows how to sort nulls) and any number of null values.  Null
         * k/v pairs will be silently ignored.
         * @return a new PersistentTreeMap of the specified comparator and the given key/value pairs
         */
        fun <K, V> ofComp(comp: Comparator<in K>, kvPairs: Iterable<Map.Entry<K, V>?>?): PersistentTreeMap<K, V> {
            if (kvPairs == null) {
                return PersistentTreeMap(comp, null, 0)
            }
            var map: PersistentTreeMap<K, V> = PersistentTreeMap(comp, null, 0)
            for (entry in kvPairs) {
                if (entry != null) {
                    map = map.assoc(entry.key, entry.value)
                }
            }
            return map
        }

        /**
         * Be extremely careful with this because it uses the default comparator, which only works for
         * items that implement Comparable (have a "natural ordering").  An attempt to use it with other
         * items will blow up at runtime.  Either a withComparator() method will be added, or this will
         * be removed.
         */
        private val EMPTY: PersistentTreeMap<*, *> = PersistentTreeMap<Any, Any>(Equator.defaultComparator(), null, 0)

        /**
         * Be extremely careful with this because it uses the default comparator, which only works for
         * items that implement Comparable (have a "natural ordering").  An attempt to use it with other
         * items will blow up at runtime.  Either a withComparator() method will be added, or this will
         * be removed.
         */
        @Suppress("UNCHECKED_CAST")
        fun <K : Comparable<K>, V> empty(): PersistentTreeMap<K, V> = EMPTY as PersistentTreeMap<K, V>

        /** Returns a new empty PersistentTreeMap that will use the specified comparator.  */
        fun <K, V> empty(c: Comparator<in K>): PersistentTreeMap<K, V> {
            return PersistentTreeMap(c, null, 0)
        }

        //    /** Returns a new PersistentTreeMap of the given comparable keys and their paired values. */
        //    public static <K extends Comparable<K>,V> PersistentTreeMap<K,V> of() {
        //        return empty();
        //    }

        // ======================================= Serialization =======================================
        // This class has a custom serialized form designed to be as small as possible.  It does not
        // have the same internal structure as an instance of this class.

        // For serializable.  Make sure to change whenever internal data format changes.
        private const val serialVersionUID = 20160904095000L

        //static <K,V, K1 extends K, K2 extends K, V1 extends V, V2 extends V>
        //Node<K,V> concat(Node<K1,V1> left, Node<K2,V2> right){
        private fun <K, V> append(left: Node<out K, out V>?,
                                  right: Node<out K, out V>?): Node<K, V> {
            if ( (left == null) && (right == null) )
                throw java.lang.IllegalStateException("Both params can't be null")
            else if (left == null)
                @Suppress("UNCHECKED_CAST")
                return right as Node<K, V>
            else if (right == null)
                @Suppress("UNCHECKED_CAST")
                return left as Node<K, V>
            else if (left is PersistentTreeMap.Red<*, *>) {
                if (right is PersistentTreeMap.Red<*, *>) {
                    val app = append(left.right(), right.left())
                    return if (app is PersistentTreeMap.Red<*, *>)
                        red(app.key, app.value,
                            red(left.key, left.value, left.left(), app.left()),
                            red(right.key, right.value, app.right(), right.right()))
                    else
                        red(left.key, left.value, left.left(),
                            red(right.key, right.value, app, right.right()))
                } else
                    return red(left.key, left.value, left.left(), append(left.right(), right))
            } else if (right is PersistentTreeMap.Red<*, *>)
                return red(right.key, right.value, append(left, right.left()), right.right())
            else
            //black/black
            {
                val app = append(left.right(), right.left())
                return if (app is PersistentTreeMap.Red<*, *>)
                    red(app.key, app.value,
                        black(left.key, left.value, left.left(), app.left()),
                        black(right.key, right.value, app.right(), right.right()))
                else
                    balanceLeftDel(left.key, left.value, left.left(),
                                   black(right.key, right.value, app, right.right()))
            }
        }

        private fun <K, V, K1 : K, V1 : V> balanceLeftDel(key: K1, value: V1,
                                                          del: Node<out K, out V>?,
                                                          right: Node<out K, out V>?): Node<K, V> {
            return if (del is PersistentTreeMap.Red<*, *>)
                red(key, value, del.blacken(), right)
            else if (right is PersistentTreeMap.Black<*, *>)
                rightBalance(key, value, del, right.redden())
            else if (right is PersistentTreeMap.Red<*, *> && right.left() is PersistentTreeMap.Black<*, *>)
                red(right.left()!!.key, right.left()!!.value,
                    black(key, value, del, right.left()!!.left()),
                    rightBalance(right.key, right.value, right.left()!!.right(),
                                 right.right()!!.redden()))
            else
                throw UnsupportedOperationException("Invariant violation")
        }

        private fun <K, V, K1 : K, V1 : V> balanceRightDel(key: K1, value: V1,
                                                           left: Node<out K, out V>?,
                                                           del: Node<out K, out V>?): Node<K, V> {
            return if (del is PersistentTreeMap.Red<*, *>)
                red(key, value, left, del.blacken())
            else if (left is PersistentTreeMap.Black<*, *>)
                leftBalance(key, value, left.redden(), del)
            else if (left is PersistentTreeMap.Red<*, *> && left.right() is PersistentTreeMap.Black<*, *>)
                red(left.right()!!.key, left.right()!!.value,
                    leftBalance(left.key, left.value, left.left()!!.redden(), left.right()!!.left()),
                    black(key, value, left.right()!!.right(), del))
            else
                throw UnsupportedOperationException("Invariant violation")
        }

        private fun <K, V, K1 : K, V1 : V> leftBalance(key: K1, value: V1,
                                                       ins: Node<out K, out V>,
                                                       right: Node<out K, out V>?): Node<K, V> {
            return if (ins is PersistentTreeMap.Red<*, *> && ins.left() is PersistentTreeMap.Red<*, *>)
                red(ins.key, ins.value, ins.left()!!.blacken(),
                    black(key, value, ins.right(), right))
            else if (ins is PersistentTreeMap.Red<*, *> && ins.right() is PersistentTreeMap.Red<*, *>)
                red(ins.right()!!.key, ins.right()!!.value,
                    black(ins.key, ins.value, ins.left(), ins.right()!!.left()),
                    black(key, value, ins.right()!!.right(), right))
            else
                black(key, value, ins, right)
        }


        private fun <K, V, K1 : K, V1 : V> rightBalance(key: K1, value: V1,
                                                        left: Node<out K, out V>?,
                                                        ins: Node<out K, out V>): Node<K, V> {
            return if (ins is PersistentTreeMap.Red<*, *> && ins.right() is PersistentTreeMap.Red<*, *>)
                red(ins.key, ins.value, black(key, value, left, ins.left()),
                    ins.right()!!.blacken())
            else if (ins is PersistentTreeMap.Red<*, *> && ins.left() is PersistentTreeMap.Red<*, *>)
                red(ins.left()!!.key, ins.left()!!.value,
                    black(key, value, left, ins.left()!!.left()),
                    black(ins.key, ins.value, ins.left()!!.right(), ins.right()))
            else
                black(key, value, left, ins)
        }

        private fun <K, V, K1 : K, V1 : V> red(key: K1, value: V1,
                                               left: Node<out K, out V>?,
                                               right: Node<out K, out V>?): Red<K, V> {
            return if (left == null && right == null) {
                //            if (val == null)
                //                return new Red<K,V>(key, val);
                Red(key, value)
            } else {
                @Suppress("UNCHECKED_CAST")
                RedBranch(key as K, value as V, left as Node<K, V>, right as Node<K, V>)
            }
            //        if (val == null)
            //            return new RedBranch<K,V>((K) key, (Node<K,V>) left, (Node<K,V>) right);
        }

        private fun <K, V, K1 : K, V1 : V> black(key: K1, value: V1,
                                                 left: Node<out K, out V>?,
                                                 right: Node<out K, out V>?): Black<K, V> {
            return if (left == null && right == null) {
                //            if (val == null)
                //                return new Black<>(key);
                Black(key, value)
            } else {
                @Suppress("UNCHECKED_CAST")
                BlackBranch(key as K, value as V, left as Node<K, V>, right as Node<K, V>)
            }
            //        if (val == null)
            //            return new BlackBranch<K,V>((K) key, (Node<K,V>) left, (Node<K,V>) right);
        }
    }

    //    static class KeyIterator<K> implements Iterator<K> {
    //        NodeIterator<K,?> it;
    //
    //        KeyIterator(NodeIterator<K,?> it) {
    //            this.it = it;
    //        }
    //
    //        @Override
    //        public boolean hasNext() {
    //            return it.hasNext();
    //        }
    //
    //        @Override
    //        public K next() {
    //            return it.next().getKey();
    //        }
    //
    //        @Override
    //        public void remove() {
    //            throw new UnsupportedOperationException();
    //        }
    //    }
    //
    //    static class ValIterator<V> implements Iterator<V> {
    //        NodeIterator<?,V> it;
    //
    //        ValIterator(NodeIterator<?,V> it) {
    //            this.it = it;
    //        }
    //
    //        @Override
    //        public boolean hasNext() {
    //            return it.hasNext();
    //        }
    //
    //        @Override
    //        public V next() {
    //            return it.next().getValue();
    //        }
    //
    //        @Override
    //        public void remove() {
    //            throw new UnsupportedOperationException();
    //        }
    //    }
}
