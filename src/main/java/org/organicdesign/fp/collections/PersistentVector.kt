/*
 *   Copyright (c) Rich Hickey. All rights reserved.
 *   The use and distribution terms for this software are covered by the
 *   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
 *   which can be found in the file epl-v10.html at the root of this distribution.
 *   By using this software in any fashion, you are agreeing to be bound by
 * 	 the terms of this license.
 *   You must not remove this notice, or any other, from this software.
 */

/* rich Jul 5, 2007 */
package org.organicdesign.fp.collections

import java.io.IOException
import java.io.InvalidObjectException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable
import java.util.NoSuchElementException
import java.util.concurrent.atomic.AtomicReference

/**
 * This started out as Rich Hickey's PersistentVector class from Clojure in late 2014.  Glen added
 * generic types, tried to make it a little more pure-Java friendly, and removed dependencies on other
 * Clojure stuff.  In 2018, Glen converted it to Kotlin.
 *
 * This file is a derivative work based on a Clojure collection licensed under the Eclipse Public
 * License 1.0 Copyright Rich Hickey
 *
 * @author Rich Hickey (Primary author)
 * @author Glen Peterson (Java/Kotlin-centric editor)
 */
class PersistentVector<E>
private constructor(override val size: Int, // The number of items in this Vector.
                    private val shift: Int,
                    @field:Transient private val root: Node,
                    private val tail: Array<E?>) : UnmodList.Companion.AbstractUnmodList<E>(), ImList<E>, Serializable {

    // Java shift operator review:
    // The signed left shift operator "<<" shifts a bit pattern to the left, and
    // the signed right shift operator ">>" shifts a bit pattern to the right.
    // The bit pattern is given by the left-hand operand, and the
    // number of positions to shift by the right-hand operand.
    // The unsigned right shift operator ">>>" shifts a zero into the leftmost position,
    // while the leftmost position after ">>" depends on sign extension.
    //
    // The bitwise & operator performs a bitwise AND operation.
    //
    // The bitwise ^ operator performs a bitwise exclusive OR operation.
    //
    // The bitwise | operator performs a bitwise inclusive OR operation

    private class Node(
        // The same data structure backs both the mutable and immutable vector.  The immutable
        // one uses copy-on-write for all operations.  The mutable one still uses copy-on-write
        // for the tree, but not for the tail.  Instead of creating a new tail one bigger after
        // each append, it creates a STRICT_NODE_SIZE tail and inserts items into it in place.
        //
        // The reason we need this AtomicReference is that some mutable vector could still have
        // a pointer to the Tail array that's in the tree.  When first mutating, the current thread
        // is placed in here.  After the mutable structure is made immutable, a null is placed in
        // here.  Subsequent attempts to mutate anything check and if they find the null, they
        // throw an exception.
        @Transient
        val edit: AtomicReference<Thread>,

        // This is either the data in the node (for a leaf node), or it's pointers to sub-nodes (for
        // a branch node).  We could probably have two separate classes: NodeLeaf and NodeBranch
        // where NodeLeaf has T[] and NodeBranch has Node<T>[].
        val array: Array<Any?>) {

        internal constructor(e: AtomicReference<Thread>) : this (e, arrayOfNulls(MAX_NODE_LENGTH))
    }

    // Check out Josh Bloch Item 78, p. 312 for an explanation of what's going on here.
    private class SerializationProxy<E>(@Transient
                                        private var vector: ImList<E>?) : Serializable {

        private val size: Int = vector!!.size


        // Taken from Josh Bloch Item 75, p. 298
        @Throws(IOException::class)
        private fun writeObject(s: ObjectOutputStream) {
            s.defaultWriteObject()
            // Write out all elements in the proper order
            for (entry in vector!!) {
                s.writeObject(entry)
            }
        }

        @Throws(IOException::class, ClassNotFoundException::class)
        private fun readObject(s: ObjectInputStream) {
            s.defaultReadObject()
            val temp = emptyMutable<E>()
            for (i in 0 until size) {
                @Suppress("UNCHECKED_CAST")
                temp.append(s.readObject() as E)
            }
            vector = temp.immutable()
        }

        private fun readResolve(): Any? = vector

        companion object {
            // For serializable.  Make sure to change whenever internal data format changes.
            private const val serialVersionUID = 20160904155600L
        }
    }

    private fun writeReplace(): Any = SerializationProxy(this)

    @Throws(IOException::class, ClassNotFoundException::class)
    @Suppress("UNUSED_PARAMETER")
    private fun readObject(ignore: java.io.ObjectInputStream) {
        throw InvalidObjectException("Proxy required")
    }

    // ===================================== Instance Methods =====================================

    // IEditableCollection has this return ITransientCollection<E>,
    // not MutVector<E> as this originally returned.
    //    @Override
    // We could make this public some day, maybe.
    override fun mutable(): MutVector<E> = MutVector(this)

    // Returns the high (gt 5) bits of the index of the last item.
    // I think this is the index of the start of the last array in the tree.
    // ((size - 1) / 32) * 32
    // (Size - 1) is an index into an array because size starts counting from 1 and array
    //            indices start from 0.
    // /32 *32 zeroes out the low 5 bits.
    // Note: Identical to other tailoff() function.
    private fun tailoff(): Int =
            if (size < MAX_NODE_LENGTH) {
                0
            } else {
                (size - 1).ushr(NODE_LENGTH_POW_2) shl NODE_LENGTH_POW_2
            }

    /** Returns the array (of type E) from the leaf node indicated by the given index.  */
    private fun leafNodeArrayFor(i: Int): Array<E?> {
        // i is the index into this vector.  Each 5 bits represent an index into an array.  The
        // highest 5 bits (that are less than the shift value) are the index into the top-level
        // array. The lowest 5 bits index the the leaf.  The guts of this method indexes into the
        // array at each level, finally indexing into the leaf node.

        if (i in 0..(size - 1)) {
            if (i >= tailoff()) {
                return tail
            }
            var node = root
            var level = shift
            while (level > 0) {
                node = node.array[i.ushr(level) and LOW_BITS] as Node
                level -= NODE_LENGTH_POW_2
            }
            @Suppress("UNCHECKED_CAST")
            return node.array as Array<E?>
        }
        throw IndexOutOfBoundsException()
    }

    /** Returns the item specified by the given index.  */
    override fun get(index: Int): E = leafNodeArrayFor(index)[index and LOW_BITS]!!

    override fun replace(index: Int, item: E): PersistentVector<E> {
        if (index in 0..(size - 1)) {
            if (index >= tailoff()) {
                @Suppress("UNCHECKED_CAST")
                val newTail: Array<E?> = arrayOfNulls<Any>(tail.size) as Array<E?>
                System.arraycopy(tail, 0, newTail, 0, tail.size)
                newTail[index and LOW_BITS] = item

                return PersistentVector(size, shift, root, newTail)
            }

            return PersistentVector(size, shift, doAssoc(shift, root, index, item), tail)
        }
        if (index == size) {
            return append(item)
        }
        throw IndexOutOfBoundsException()
    }

    /**
     * Inserts a new item at the end of the Vecsicle.
     * @param item the value to insert
     * @return a new Vecsicle with the additional item.
     */
    override fun append(item: E): PersistentVector<E> {
        //room in tail?
        //	if(tail.length < MAX_NODE_LENGTH)
        if (size - tailoff() < MAX_NODE_LENGTH) {
            @Suppress("UNCHECKED_CAST")
            val newTail = arrayOfNulls<Any>(tail.size + 1) as Array<E?>
            System.arraycopy(tail, 0, newTail, 0, tail.size)
            newTail[tail.size] = item
            return PersistentVector(size + 1, shift, root, newTail)
        }
        //full tail, push into tree
        val newroot: Node
        @Suppress("UNCHECKED_CAST")
        val tailnode = Node(root.edit, tail as Array<Any?>)
        var newshift = shift
        //overflow root?
        if (size.ushr(NODE_LENGTH_POW_2) > 1 shl shift) {
            newroot = Node(root.edit)
            newroot.array[0] = root
            newroot.array[1] = newPath(root.edit, shift, tailnode)
            newshift += NODE_LENGTH_POW_2
        } else {
            newroot = pushTail(shift, root, tailnode)
        }
        return PersistentVector(size + 1, newshift, newroot, singleElementArray(item))
    }

    /**
     * Efficiently adds items to the end of this PersistentVector.
     * @param iterable the values to insert
     * @return a new PersistentVector with the additional items at the end.
     */
    override fun concat(iterable: Iterable<E>): PersistentVector<E> =
            mutable().concat(iterable).immutable() as PersistentVector<E>

    private fun pushTail(level: Int, parent: Node, tailnode: Node): Node {
        //if parent is leaf, insert node,
        // else does it map to an existing child? -> nodeToInsert = pushNode one more level
        // else alloc new path
        //return  nodeToInsert placed in copy of parent
        val subidx = (size - 1).ushr(level) and LOW_BITS
        val ret = Node(parent.edit, parent.array.clone())
        val nodeToInsert: Node =
                if (level == NODE_LENGTH_POW_2) {
                    tailnode
                } else {
                    val child = parent.array[subidx] as Node?
                    if (child == null)
                        newPath(root.edit, level - NODE_LENGTH_POW_2, tailnode)
                    else
                        pushTail(level - NODE_LENGTH_POW_2, child, tailnode)
                }
        ret.array[subidx] = nodeToInsert
        return ret
    }

    override fun listIterator(index: Int): UnmodListIterator<E> {
        if (index < 0 || index > size) {
            // To match ArrayList and other java.util.List expectations
            throw IndexOutOfBoundsException("Index: $index")
        }
        return object : UnmodListIterator<E> {
            private var i = index
            private var base = i - i % MAX_NODE_LENGTH
            private var array: Array<E?>? = if (index < size) leafNodeArrayFor(i) else null

            override fun hasNext(): Boolean = i < size

            override fun hasPrevious(): Boolean = i > 0

            override fun next(): E {
                if (i >= size) {
                    // To match ArrayList and other java.util.List expectations
                    // If we didn't catch this, it would be an ArrayIndexOutOfBoundsException.
                    throw NoSuchElementException()
                }
                if (i - base == MAX_NODE_LENGTH) {
                    array = leafNodeArrayFor(i)
                    base += MAX_NODE_LENGTH
                }
                return array!![i++ and LOW_BITS]!!
            }

            override fun nextIndex(): Int = i

            override fun previous(): E {
                // To match contract of ListIterator and implementation of ArrayList
                if (i < 1) {
                    // To match ArrayList and other java.util.List expectations.
                    throw NoSuchElementException()
                }
                if (i - base == 0) {
                    //                    System.out.println("i - base was zero");
                    array = leafNodeArrayFor(i - 1)
                    base -= MAX_NODE_LENGTH
                } else if (i == size) {
                    // Can start with index past array.
                    array = leafNodeArrayFor(i - 1)
                    base = i - i % MAX_NODE_LENGTH
                }
                return array!![--i and LOW_BITS]!!
            }
        }
    }

    //    public static class Reduced<A> {
    //        public final A val;
    //        private Reduced(A a) { val = a; }
    //    }
    //
    //    /**
    //     * This is an early exit indicator for reduce operations.  Return one of these when you want
    //     * the reduction to end. It uses types, but not in a "traditional" way.
    //     */
    //    public static <A> Reduced<A> done(A a) { return new Reduced<>(a); }

    // Implements Counted through ITransientVector<E> -> Indexed<E> -> Counted.
    class MutVector<F> private constructor(
            private var sz: Int, // The number of items in this Vector.
            private var shift: Int, // The root node of the data tree inside this vector.
            private var root: Node,
            private var tail: Array<F?>) : UnmodList.Companion.AbstractUnmodList<F>(),
                                           MutList<F> {

        override val size: Int
            get() {
                ensureEditable()
                return sz
            }

        constructor(v: PersistentVector<F>) : this(v.size,
                                                   v.shift,
                                                   editableRoot(v.root),
                                                   arrayCopy(v.tail, MAX_NODE_LENGTH, null))

        private fun ensureEditable(node: Node): Node =
                if (node.edit === root.edit) {
                    node
                } else {
                    Node(root.edit, node.array.clone())
                }

        private fun ensureEditable() {
            if (root.edit.get() == null) {
                throw IllegalAccessError("Mutable used after immutable! call")
            }
            //		root = editableRoot(root);
            //		tail = editableTail(tail);
        }

        override fun immutable(): PersistentVector<F> {
            ensureEditable()
            //		Thread owner = root.edit.get();
            //		if(owner != null && owner != Thread.currentThread())
            //			{
            //			throw new IllegalAccessError("Mutation release by non-owner thread");
            //			}
            root.edit.set(null)
            @Suppress("UNCHECKED_CAST")
            val trimmedTail = arrayOfNulls<Any>(sz - tailoff()) as Array<F?>
            System.arraycopy(tail, 0, trimmedTail, 0, trimmedTail.size)
            return PersistentVector(sz, shift, root, trimmedTail)
        }

        override fun append(item: F): MutList<F> {
            ensureEditable()
            //room in tail?
            if (sz - tailoff() < MAX_NODE_LENGTH) {
                tail[sz and LOW_BITS] = item
                ++sz
                return this
            }
            //full tail, push into tree
            val newroot: Node
            @Suppress("UNCHECKED_CAST")
            val tailnode = Node(root.edit, tail as Array<Any?>)
            @Suppress("UNCHECKED_CAST")
            tail = arrayOfNulls<Any>(MAX_NODE_LENGTH) as Array<F?>
            tail[0] = item
            var newshift = shift
            //overflow root?
            if (sz.ushr(NODE_LENGTH_POW_2) > 1 shl shift) {
                newroot = Node(root.edit)
                newroot.array[0] = root
                newroot.array[1] = newPath(root.edit, shift, tailnode)
                newshift += NODE_LENGTH_POW_2
            } else
                newroot = pushTail(shift, root, tailnode)
            root = newroot
            shift = newshift
            ++sz
            return this
        }

        // TODO: are these all node<F> or could this return a super-type of F?
        private fun pushTail(level: Int, parent: Node, tailnode: Node): Node {
            //if parent is leaf, insert node,
            // else does it map to an existing child? -> nodeToInsert = pushNode one more level
            // else alloc new path
            //return  nodeToInsert placed in parent
            val par = ensureEditable(parent)
            val subidx = (sz - 1).ushr(level) and LOW_BITS
            val nodeToInsert: Node =
                    if (level == NODE_LENGTH_POW_2) {
                        tailnode
                    } else {
                        val child = par.array[subidx] as Node?
                        if (child != null) {
                            pushTail(level - NODE_LENGTH_POW_2, child, tailnode)
                        } else {
                            newPath(root.edit, level - NODE_LENGTH_POW_2, tailnode)
                        }
                    }
            par.array[subidx] = nodeToInsert
            return par
        }

        // Returns the high (gt 5) bits of the index of the last item.
        // I think this is the index of the start of the last array in the tree.
        // ((size - 1) / 32) * 32
        // (Size - 1) is an index into an array because size starts counting from 1 and array
        //            indices start from 0.
        // /32 *32 zeroes out the low 5 bits.
        // Note: Identical to other tailoff() function.
        private fun tailoff(): Int =
            if (sz < MAX_NODE_LENGTH) {
                0
            } else {
                (sz - 1).ushr(NODE_LENGTH_POW_2) shl NODE_LENGTH_POW_2
            }

        //        @SuppressWarnings("unchecked")
        //        private F[] leafNodeArrayFor(int i) {
        //            if (i >= 0 && i < size) {
        //                if (i >= tailoff()) {
        //                    return tail;
        //                }
        //                Node node = root;
        //                for (int level = shift; level > 0; level -= NODE_LENGTH_POW_2) {
        //                    node = (Node) node.array[(i >>> level) & LOW_BITS];
        //                }
        //                return (F[]) node.array;
        //            }
        //            throw new IndexOutOfBoundsException();
        //        }

        private fun editableArrayFor(i: Int): Array<F?> {
            if (i in 0..(sz - 1)) {
                if (i >= tailoff()) {
                    return tail
                }
                var node = root
                var level = shift
                while (level > 0) {
                    node = ensureEditable(node.array[i.ushr(level) and LOW_BITS] as Node)
                    level -= NODE_LENGTH_POW_2
                }
                @Suppress("UNCHECKED_CAST")
                return node.array as Array<F?>
            }
            throw IndexOutOfBoundsException()
        }

        override fun get(index: Int): F {
            ensureEditable()
            val node = editableArrayFor(index)
            return node[index and LOW_BITS]!!
        }

        override fun replace(index: Int, item: F): MutList<F> {
            ensureEditable()
            val node = editableArrayFor(index)
            node[index and LOW_BITS] = item
            return this
        }

    } // end inner static class MutVector

    companion object {

        // There's bit shifting going on here because it's a very fast operation.
        // Shifting right by 5 is aeons faster than dividing by 32.
        private val NODE_LENGTH_POW_2 = 5

        // 0b00000000000000000000000000100000 = 0x20 = 32
        private val MAX_NODE_LENGTH = 1 shl NODE_LENGTH_POW_2
        // 0b00000000000000000000000000011111 = 0x1f
        private val LOW_BITS = MAX_NODE_LENGTH - 1

        private val NOEDIT = AtomicReference<Thread>(null)

        private val EMPTY_NODE = Node(NOEDIT, arrayOfNulls(MAX_NODE_LENGTH))

        val EMPTY: PersistentVector<*> = PersistentVector(0, NODE_LENGTH_POW_2, EMPTY_NODE, arrayOf<Any?>())

        /** Returns the empty ImList (there only needs to be one)  */
        @Suppress("UNCHECKED_CAST")
        fun <T> empty(): PersistentVector<T> = EMPTY as PersistentVector<T>

        /**
         * Returns a new mutable vector.  For some reason calling empty().mutable() sometimes requires
         * an explicit type parameter in Java, so this convenience method works around that.
         */
        fun <T> emptyMutable(): MutVector<T> = empty<T>().mutable()

        /**
         * Public static factory method to create a vector from an Iterable.  A varargs version of this
         * method is: [org.organicdesign.fp.vec].
         */
        fun <T> ofIter(items: Iterable<T>): PersistentVector<T> {
            val ret = emptyMutable<T>()
            for (item in items) {
                ret.append(item)
            }
            return ret.immutable()
        }

        // ======================================= Serialization =======================================
        // This class has a custom serialized form designed to be as small as possible.  It does not
        // have the same internal structure as an instance of this class.

        // For serializable.  Make sure to change whenever internal data format changes.
        private const val serialVersionUID = 20160904160500L

        //    Iterator<E> rangedIterator(final int start, final int end) {
        //        return new Iterator<E>() {
        //            int i = start;
        //            int base = i - (i % MAX_NODE_LENGTH);
        //            E[] array = (start < size()) ? leafNodeArrayFor(i) : null;
        //
        //            @Override
        //            public boolean hasNext() {
        //                return i < end;
        //            }
        //
        //            @Override
        //            public E next() {
        //                if (i - base == MAX_NODE_LENGTH) {
        //                    array = leafNodeArrayFor(i);
        //                    base += MAX_NODE_LENGTH;
        //                }
        //                return array[i++ & LOW_BITS];
        //            }
        //
        //            @Override
        //            public void remove() {
        //                throw new UnsupportedOperationException();
        //            }
        //        };
        //    }

        //    public UnmodIterator<E> iterator() {
        //        return rangedIterator(0, size());
        //    }

        //    @SuppressWarnings("unchecked")
        //    public <U> U reduce(Fn2<U, E, U> f, U init) {
        //        int step = 0;
        //        for (int i = 0; i < size; i += step) {
        //            E[] array = leafNodeArrayFor(i);
        //            for (int j = 0; j < array.length; ++j) {
        //                init = f.invoke(init, array[j]);
        //
        //                if ( (init != null) && (init instanceof Reduced) ) {
        //                    return ((Reduced<U>) init).val;
        //                }
        //            }
        //            step = array.length;
        //        }
        //        return init;
        //    }


        //    @Override public IPersistentCollection<E> empty(){
        //    	return emptyPersistentCollection(meta());
        //    }

        //    @SuppressWarnings("unchecked")
        //    public ImVectorImpl<E> pop() {
        //        if (size == 0)
        //            throw new IllegalStateException("Can't pop empty vector");
        //        if (size == 1)
        //            return empty();
        //        //if(tail.length > 1)
        //        if (size - tailoff() > 1) {
        //            E[] newTail = (E[]) new Object[tail.length - 1];
        //            System.arraycopy(tail, 0, newTail, 0, newTail.length);
        //            return new ImVectorImpl<>(size - 1, shift, root, newTail);
        //        }
        //        E[] newtail = leafNodeArrayFor(size - 2);
        //
        //        Node newroot = popTail(shift, root);
        //        int newshift = shift;
        //        if (newroot == null) {
        //            newroot = EMPTY_NODE;
        //        }
        //        if (shift > NODE_LENGTH_POW_2 && newroot.array[1] == null) {
        //            newroot = (Node) newroot.array[0];
        //            newshift -= NODE_LENGTH_POW_2;
        //        }
        //        return new ImVectorImpl<>(size - 1, newshift, newroot, newtail);
        //    }

        //    private Node popTail(int level, Node node) {
        //        int subidx = ((size - 2) >>> level) & LOW_BITS;
        //        if (level > NODE_LENGTH_POW_2) {
        //            Node newchild = popTail(level - NODE_LENGTH_POW_2, (Node) node.array[subidx]);
        //            if (newchild == null && subidx == 0)
        //                return null;
        //            else {
        //                Node ret = new Node(root.edit, node.array.clone());
        //                ret.array[subidx] = newchild;
        //                return ret;
        //            }
        //        } else if (subidx == 0)
        //            return null;
        //        else {
        //            Node ret = new Node(root.edit, node.array.clone());
        //            ret.array[subidx] = null;
        //            return ret;
        //        }
        //    }

        private fun <Z> doAssoc(level: Int, node: Node, i: Int, item: Z): Node {
            val ret = Node(node.edit, node.array.clone())
            if (level == 0) {
                ret.array[i and LOW_BITS] = item
            } else {
                val subidx = i.ushr(level) and LOW_BITS
                ret.array[subidx] = doAssoc(level - NODE_LENGTH_POW_2,
                                            node.array[subidx] as Node,
                                            i, item)
            }
            return ret
        }

        private fun newPath(edit: AtomicReference<Thread>, level: Int, node: Node): Node {
            if (level == 0) {
                return node
            }
            val ret = Node(edit)
            ret.array[0] = newPath(edit, level - NODE_LENGTH_POW_2, node)
            return ret
        }

        private fun editableRoot(node: Node): Node =
                Node(AtomicReference(Thread.currentThread()), node.array.clone())
    }
}
