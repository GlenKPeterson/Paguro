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
package org.organicdesign.fp.experiments.collections;

import org.organicdesign.fp.function.Function2;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * This started out as Rich Hickey's PersistentVector class from Clojure in late 2014.  Glen added types, and tried
 * to make it a little more pure-Java friendly, and removed dependencies on other Clojure stuff.
 *
 * @author Rich Hickey (Primary author)
 * @author Glen Peterson (Java-centric editor)
 */
public class ImVectorImpl<E> { // TODO: implements UnList<E> {

    // There's bit shifting going on here because it's a very fast operation.
    // Shifting right by 5 is aeons faster than dividing by 32.
    private static final int NODE_LENGTH_POW_2 = 5;
    private static final int MAX_NODE_LENGTH = 1 << NODE_LENGTH_POW_2;// 0b00000000000000000000000000100000 = 0x20 = 32
    private static final int HIGH_BITS = -MAX_NODE_LENGTH;            // 0b11111111111111111111111111100000
    private static final int LOW_BITS = MAX_NODE_LENGTH - 1;          // 0b00000000000000000000000000011111 = 0x1f

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

    private static class Node implements Serializable {
        // Every node in a Vector (Transient or Persistent) shares a single atomic reference value.
        // I'm not sure why this is on the node instead of on the vector.  You know, if we do that, we don't need this
        // class at all and could just use arrays instead.
        transient public final AtomicReference<Thread> edit;

        // This is either the data in the node (for a leaf node), or it's pointers to sub-nodes (for a branch node).
        // We could probably have two separate classes: NodeLeaf and NodeBranch where NodeLeaf has T[] and NodeBranch
        // has Node<T>[].
        public final Object[] array;

        public Node(AtomicReference<Thread> edit, Object[] array) {
            this.edit = edit;
            this.array = array;
        }

        Node(AtomicReference<Thread> edit) {
            this.edit = edit;
            this.array = new Object[MAX_NODE_LENGTH];
        }
    }

    private final static AtomicReference<Thread> NOEDIT = new AtomicReference<>(null);

    private final static Node EMPTY_NODE = new Node(NOEDIT, new Object[MAX_NODE_LENGTH]);

    private final static ImVectorImpl<?> EMPTY = new ImVectorImpl<>(0, NODE_LENGTH_POW_2, EMPTY_NODE,
            new Object[]{});

    @SuppressWarnings("unchecked")
    public static final <T> ImVectorImpl<T> empty() { return (ImVectorImpl<T>) EMPTY; }

    @SuppressWarnings("unchecked")
    public static final <T> MutableVector<T> emptyTransientVector() {
        return (MutableVector<T>) EMPTY.asTransient();
    }

    // The number of items in this Vector.
    final int size;
    public final int shift;
    public final Node root;
    public final E[] tail;

    /** Constructor */
    private ImVectorImpl(int z, int shift, Node root, E[] tail) {
        size = z;
        this.shift = shift;
        this.root = root;
        this.tail = tail;
    }

    /** Public static factory method. */
    static public <T> ImVectorImpl<T> of(List<T> items) {
        MutableVector<T> ret = emptyTransientVector();
        for (T item : items) {
            ret = ret.append(item);
        }
        return ret.persistent();
    }

    /** Public static factory method. */
    @SafeVarargs
    static public <T> ImVectorImpl<T> of(T... items) {
        MutableVector<T> ret = emptyTransientVector();
        for (T item : items) {
            ret = ret.append(item);
        }
        return ret.persistent();
    }

    // IEditableCollection has this return ITransientCollection<E>,
    // not TransientVector<E> as this originally returned.
//    @Override
    public MutableVector<E> asTransient() {
        return new MutableVector<>(this);
    }

    // Returns the high (gt 5) bits of the index of the last item.
    // I think this is the index of the start of the last array in the tree.
    final private int tailoff() {
        // ((size - 1) / 32) * 32
        // (Size - 1) is an index into an array because size starts counting from 1 and array indicies start from 0.
        // /32 *32 zeroes out the low 5 bits.
        return (size < MAX_NODE_LENGTH)
                ? 0
                : ((size - 1) >>> NODE_LENGTH_POW_2) << NODE_LENGTH_POW_2;
        // Last line can be replaced with (size -1) & HIGH_BITS
    }

    @SuppressWarnings("unchecked")
    public E[] arrayFor(int i) {
        if (i >= 0 && i < size) {
            if (i >= tailoff())
                return tail;
            Node node = root;
            for (int level = shift; level > 0; level -= NODE_LENGTH_POW_2)
                node = (Node) node.array[(i >>> level) & LOW_BITS];
            return (E[]) node.array;
        }
        throw new IndexOutOfBoundsException();
    }

    /**
     * Returns the item at this index.
     * @param i the zero-based index to get from the vector.
     * @return the value at that index.
     */
    public E get(int i) {
        E[] node = arrayFor(i);
        return node[i & LOW_BITS];
    }

    /**
     * Returns the item at this index.
     * @param n the zero-based index to get from the vector.
     * @return the value at that index.
     */
    public E get(Number n) { return get(n.intValue()); }

    /**
     * Returns the item at this index.
     * @param i the zero-based index to get from the vector.
     * @param notFound the value to return if the index is out of bounds.
     * @return the value at that index, or the notFound value.
     */
    public E get(int i, E notFound) {
        if (i >= 0 && i < size)
            return get(i);
        return notFound;
    }

    /**
     * Inserts a new item at the specified index.
     * @param i the zero-based index to insert at (pushes current item and all subsequent items up)
     * @param val the value to insert
     * @return a new Vecsicle with the additional item.
     */
    @SuppressWarnings("unchecked")
    public ImVectorImpl<E> insert(int i, E val) {
        if (i >= 0 && i < size) {
            if (i >= tailoff()) {
                Object[] newTail = new Object[tail.length];
                System.arraycopy(tail, 0, newTail, 0, tail.length);
                newTail[i & LOW_BITS] = val;

                return new ImVectorImpl<>(size, shift, root, (E[]) newTail);
            }

            return new ImVectorImpl<>(size, shift, doAssoc(shift, root, i, val), tail);
        }
        if (i == size) {
            return append(val);
        }
        throw new IndexOutOfBoundsException();
    }

    public int size() { return size; }

// TODO: add prepend() that takes an array
// TODO: Make this take an array
    /**
     * Inserts a new item at the end of the Vecsicle.
     * @param val the value to insert
     * @return a new Vecsicle with the additional item.
     */
    @SuppressWarnings("unchecked")
    public ImVectorImpl<E> append(E val) {
        //room in tail?
        //	if(tail.length < MAX_NODE_LENGTH)
        if (size - tailoff() < MAX_NODE_LENGTH) {
            Object[] newTail = new Object[tail.length + 1];
            System.arraycopy(tail, 0, newTail, 0, tail.length);
            newTail[tail.length] = val;
            return new ImVectorImpl<>(size + 1, shift, root, (E[]) newTail);
        }
        //full tail, push into tree
        Node newroot;
        Node tailnode = new Node(root.edit, tail);
        int newshift = shift;
        //overflow root?
        if ((size >>> NODE_LENGTH_POW_2) > (1 << shift)) {
            newroot = new Node(root.edit);
            newroot.array[0] = root;
            newroot.array[1] = newPath(root.edit, shift, tailnode);
            newshift += NODE_LENGTH_POW_2;
        } else {
            newroot = pushTail(shift, root, tailnode);
        }
        return new ImVectorImpl<>(size + 1, newshift, newroot, (E[]) new Object[]{val});
    }

    private Node pushTail(int level, Node parent, Node tailnode) {
        //if parent is leaf, insert node,
        // else does it map to an existing child? -> nodeToInsert = pushNode one more level
        // else alloc new path
        //return  nodeToInsert placed in copy of parent
        int subidx = ((size - 1) >>> level) & LOW_BITS;
        Node ret = new Node(parent.edit, parent.array.clone());
        Node nodeToInsert;
        if (level == NODE_LENGTH_POW_2) {
            nodeToInsert = tailnode;
        } else {
            Node child = (Node) parent.array[subidx];
            nodeToInsert = (child == null)
                    ? newPath(root.edit, level - NODE_LENGTH_POW_2, tailnode)
                    : pushTail(level - NODE_LENGTH_POW_2, child, tailnode);
        }
        ret.array[subidx] = nodeToInsert;
        return ret;
    }

    Iterator<E> rangedIterator(final int start, final int end) {
        return new Iterator<E>() {
            int i = start;
            int base = i - (i % MAX_NODE_LENGTH);
            E[] array = (start < size()) ? arrayFor(i) : null;

            @Override
            public boolean hasNext() {
                return i < end;
            }

            @Override
            public E next() {
                if (i - base == MAX_NODE_LENGTH) {
                    array = arrayFor(i);
                    base += MAX_NODE_LENGTH;
                }
                return array[i++ & LOW_BITS];
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    public Iterator<E> iterator() {
        return rangedIterator(0, size());
    }

    @SuppressWarnings("unchecked")
    public <U> U reduce(Function2<U, E, U> f, U init) {
        int step = 0;
        for (int i = 0; i < size; i += step) {
            E[] array = arrayFor(i);
            for (int j = 0; j < array.length; ++j) {
                init = f.apply_(init, array[j]);

                if ( (init != null) && (init instanceof Reduced) ) {
                    return ((Reduced<U>) init).val;
                }
            }
            step = array.length;
        }
        return init;
    }


//    @Override public IPersistentCollection<E> empty(){
//    	return emptyPersistentCollection(meta());
//    }

    @SuppressWarnings("unchecked")
    public ImVectorImpl<E> pop() {
        if (size == 0)
            throw new IllegalStateException("Can't pop empty vector");
        if (size == 1)
            return empty();
        //if(tail.length > 1)
        if (size - tailoff() > 1) {
            E[] newTail = (E[]) new Object[tail.length - 1];
            System.arraycopy(tail, 0, newTail, 0, newTail.length);
            return new ImVectorImpl<>(size - 1, shift, root, newTail);
        }
        E[] newtail = arrayFor(size - 2);

        Node newroot = popTail(shift, root);
        int newshift = shift;
        if (newroot == null) {
            newroot = EMPTY_NODE;
        }
        if (shift > NODE_LENGTH_POW_2 && newroot.array[1] == null) {
            newroot = (Node) newroot.array[0];
            newshift -= NODE_LENGTH_POW_2;
        }
        return new ImVectorImpl<>(size - 1, newshift, newroot, newtail);
    }

    private Node popTail(int level, Node node) {
        int subidx = ((size - 2) >>> level) & LOW_BITS;
        if (level > NODE_LENGTH_POW_2) {
            Node newchild = popTail(level - NODE_LENGTH_POW_2, (Node) node.array[subidx]);
            if (newchild == null && subidx == 0)
                return null;
            else {
                Node ret = new Node(root.edit, node.array.clone());
                ret.array[subidx] = newchild;
                return ret;
            }
        } else if (subidx == 0)
            return null;
        else {
            Node ret = new Node(root.edit, node.array.clone());
            ret.array[subidx] = null;
            return ret;
        }
    }

    private static Node doAssoc(int level, Node node, int i, Object val) {
        Node ret = new Node(node.edit, node.array.clone());
        if (level == 0) {
            ret.array[i & LOW_BITS] = val;
        } else {
            int subidx = (i >>> level) & LOW_BITS;
            ret.array[subidx] = doAssoc(level - NODE_LENGTH_POW_2, (Node) node.array[subidx], i, val);
        }
        return ret;
    }

    private static Node newPath(AtomicReference<Thread> edit, int level, Node node) {
        if (level == 0) {
            return node;
        }
        Node ret = new Node(edit);
        ret.array[0] = newPath(edit, level - NODE_LENGTH_POW_2, node);
        return ret;
    }

    public static class Reduced<A> {
        public final A val;
        private Reduced(A a) { val = a; }
    }

    /**
     * This is an early exit indicator for reduce operations.  Return one of these when you want the reduction to end.
     * It uses types, but not in a "traditional" way.
     */
    public static <A> Reduced<A> done(A a) { return new Reduced<>(a); }

    // Implements Counted through ITransientVector<E> -> Indexed<E> -> Counted.
    private static final class MutableVector<F> {
        // The number of items in this Vector.
        int size;

        int shift;

        // The root node of the data tree inside this vector.
        Node root;

        F[] tail;

        private MutableVector(int c, int s, Node r, F[] t) { size = c; shift = s; root = r; tail = t; }

        private MutableVector(ImVectorImpl<F> v) { this(v.size, v.shift, editableRoot(v.root), editableTail(v.tail)); }

        private Node ensureEditable(Node node) {
            if (node.edit == root.edit)
                return node;
            return new Node(root.edit, node.array.clone());
        }

        private void ensureEditable() {
            if (root.edit.get() == null) {
                throw new IllegalAccessError("Transient used after persistent! call");
            }
            //		root = editableRoot(root);
            //		tail = editableTail(tail);
        }

        public int size() {
            ensureEditable();
            return size;
        }

        @SuppressWarnings("unchecked")
        public ImVectorImpl<F> persistent() {
            ensureEditable();
            //		Thread owner = root.edit.get();
            //		if(owner != null && owner != Thread.currentThread())
            //			{
            //			throw new IllegalAccessError("Mutation release by non-owner thread");
            //			}
            root.edit.set(null);
            F[] trimmedTail = (F[]) new Object[size - tailoff()];
            System.arraycopy(tail, 0, trimmedTail, 0, trimmedTail.length);
            return new ImVectorImpl<>(size, shift, root, trimmedTail);
        }

        @SuppressWarnings("unchecked")
        public MutableVector<F> append(F val) {
            ensureEditable();
            int i = size;
            //room in tail?
            if (i - tailoff() < MAX_NODE_LENGTH) {
                tail[i & LOW_BITS] = val;
                ++size;
                return this;
            }
            //full tail, push into tree
            Node newroot;
            Node tailnode = new Node(root.edit, tail);
            tail = (F[]) new Object[MAX_NODE_LENGTH];
            tail[0] = val;
            int newshift = shift;
            //overflow root?
            if ((size >>> NODE_LENGTH_POW_2) > (1 << shift)) {
                newroot = new Node(root.edit);
                newroot.array[0] = root;
                newroot.array[1] = newPath(root.edit, shift, tailnode);
                newshift += NODE_LENGTH_POW_2;
            } else
                newroot = pushTail(shift, root, tailnode);
            root = newroot;
            shift = newshift;
            ++size;
            return this;
        }

        // TODO: are these all node<F> or could this return a super-type of F?
        @SuppressWarnings("unchecked")
        private Node pushTail(int level, Node parent, Node tailnode) {
            //if parent is leaf, insert node,
            // else does it map to an existing child? -> nodeToInsert = pushNode one more level
            // else alloc new path
            //return  nodeToInsert placed in parent
            parent = ensureEditable(parent);
            int subidx = ((size - 1) >>> level) & LOW_BITS;
            Node ret = parent;
            Node nodeToInsert;
            if (level == NODE_LENGTH_POW_2) {
                nodeToInsert = tailnode;
            } else {
                Node child = (Node) parent.array[subidx];
                nodeToInsert = (child != null) ?
                        pushTail(level - NODE_LENGTH_POW_2, child, tailnode)
                        : newPath(root.edit, level - NODE_LENGTH_POW_2, tailnode);
            }
            ret.array[subidx] = nodeToInsert;
            return ret;
        }

        // Returns the high (gt 5) bits of the index of the last item.
        // I think this is the index of the start of the last array in the tree.
        final private int tailoff() {
            // ((size - 1) / 32) * 32
            // (Size - 1) is an index into an array because size starts counting from 1 and array indicies start from 0.
            // /32 *32 zeroes out the low 5 bits.
            return (size < MAX_NODE_LENGTH)
                    ? 0
                    : ((size - 1) >>> NODE_LENGTH_POW_2) << NODE_LENGTH_POW_2;
            // Last line can be replaced with (size -1) & HIGH_BITS
        }

        @SuppressWarnings("unchecked")
        private F[] arrayFor(int i) {
            if (i >= 0 && i < size) {
                if (i >= tailoff()) {
                    return tail;
                }
                Node node = root;
                for (int level = shift; level > 0; level -= NODE_LENGTH_POW_2) {
                    node = (Node) node.array[(i >>> level) & LOW_BITS];
                }
                return (F[]) node.array;
            }
            throw new IndexOutOfBoundsException();
        }

        @SuppressWarnings("unchecked")
        private F[] editableArrayFor(int i) {
            if (i >= 0 && i < size) {
                if (i >= tailoff())
                    return tail;
                Node node = root;
                for (int level = shift; level > 0; level -= NODE_LENGTH_POW_2)
                    node = ensureEditable((Node) node.array[(i >>> level) & LOW_BITS]);
                return (F[]) node.array;
            }
            throw new IndexOutOfBoundsException();
        }

        public F nth(int i) {
            ensureEditable();
            F[] node = arrayFor(i);
            return node[i & LOW_BITS];
        }

        public F nth(int i, F notFound) {
            if (i >= 0 && i < size())
                return nth(i);
            return notFound;
        }

        /** Convenience method for using any class that implements Number as a key. */
        public F nth(Number key) { return nth(key.intValue(), null); }

        /** Convenience method for using any class that implements Number as a key. */
        public F nth(Number key, F notFound) { return nth(key.intValue(), notFound); }

        public MutableVector<F> insertAt(int i, F val) {
            ensureEditable();
            if (i >= 0 && i < size) {
                if (i >= tailoff()) {
                    tail[i & LOW_BITS] = val;
                    return this;
                }

                root = doAssoc(shift, root, i, val);
                return this;
            } else if (i == size) {
                return append(val);
            }
            throw new IndexOutOfBoundsException();
        }

        public MutableVector<F> assoc(int key, F val) {
            //note - relies on ensureEditable in insertAt
            return insertAt(key, val);
        }

        public MutableVector<F> assoc(Number key, F val) {
            return insertAt(key.intValue(), val);
        }

        @SuppressWarnings("unchecked")
        private Node doAssoc(int level, Node node, int i, Object val) {
            node = ensureEditable(node);
            Node ret = node;
            if (level == 0) {
                ret.array[i & LOW_BITS] = val;
            } else {
                int subidx = (i >>> level) & LOW_BITS;
                ret.array[subidx] = doAssoc(level - NODE_LENGTH_POW_2, (Node) node.array[subidx], i, val);
            }
            return ret;
        }

        @SuppressWarnings("unchecked")
        public MutableVector<F> pop() {
            ensureEditable();
            if (size == 0)
                throw new IllegalStateException("Can't pop empty vector");
            if (size == 1) {
                size = 0;
                return this;
            }
            int i = size - 1;
            //pop in tail?
            if ((i & LOW_BITS) > 0) {
                --size;
                return this;
            }

            F[] newtail = editableArrayFor(size - 2);

            Node newroot = popTail(shift, root);
            int newshift = shift;
            if (newroot == null) {
                newroot = new Node(root.edit);
            }
            if (shift > NODE_LENGTH_POW_2 && newroot.array[1] == null) {
                newroot = ensureEditable((Node) newroot.array[0]);
                newshift -= NODE_LENGTH_POW_2;
            }
            root = newroot;
            shift = newshift;
            --size;
            tail = newtail;
            return this;
        }

        @SuppressWarnings("unchecked")
        private Node popTail(int level, Node node) {
            node = ensureEditable(node);
            int subidx = ((size - 2) >>> level) & LOW_BITS;
            if (level > NODE_LENGTH_POW_2) {
                Node newchild = popTail(level - NODE_LENGTH_POW_2, (Node) node.array[subidx]);
                if (newchild == null && subidx == 0)
                    return null;
                else {
                    node.array[subidx] = newchild;
                    return node;
                }
            } else if (subidx == 0)
                return null;
            else {
                node.array[subidx] = null;
                return node;
            }
        }

        static Node editableRoot(Node node) {
            return new Node(new AtomicReference<>(Thread.currentThread()), node.array.clone());
        }
        @SuppressWarnings("unchecked")
        static <T> T[] editableTail(T[] tl) {
            Object[] ret = new Object[MAX_NODE_LENGTH];
            System.arraycopy(tl, 0, ret, 0, tl.length);
            return (T[]) ret;
        }
    } // end inner static class TransientVector
}
