// Copyright 2015 PlanBase Inc. & Glen Peterson
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.organicdesign.fp.collections;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 Hickey's PersistentVector is one of the wonders of the modern world.  I don't see how its get()
 method could be made significantly faster.  But its construction assumes single-item appends.
 NestingVector makes every effort to construct in 32-item chunks, which should theoretically be
 faster and might make a transient version unnecessary.
 */
public class NestingVector<E> implements ImList<E> {
    // There's bit shifting going on here because it's a very fast operation.
    // Shifting right by 5 is aeons faster than dividing by 32.
    private static final int BUCKET_LENGTH_POW_2 = 5;
    private static final int MAX_BUCKET_LENGTH = 1 << BUCKET_LENGTH_POW_2;// 0b00000000000000000000000000100000 = 0x20 = 32
//    private static final int HIGH_BITS = -MAX_BUCKET_LENGTH;            // 0b11111111111111111111111111100000
    private static final int LOW_BITS = MAX_BUCKET_LENGTH - 1;          // 0b00000000000000000000000000011111 = 0x1f

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

    @SuppressWarnings("unchecked")
    private static <T> T[] singletonArray(T t) { return (T[]) new Object[] { t }; }

    @SuppressWarnings("unchecked")
    private static <T> T[] arrayOfLength(int i) { return (T[]) new Object[i]; }

    /** Copy an array as quickly as possible, replacing the item at the specified index. */
    private static <T> T[] copyReplace(T[] ts, int idx, T t) {
        // Copy the old kids, then replace the one item.
        T[] result = Arrays.copyOf(ts, ts.length);
        result[idx] = t;
        return result;
    }

    private static <T> String abbrevArrayStr(T[] ts) {
        StringBuilder sB = new StringBuilder("[");
        if (ts.length > 0) {
            sB.append(String.valueOf(ts[0]));
        }
        if (ts.length > 1) {
            int lastIdx = ts.length - 1;
            sB.append("...")
              .append(lastIdx).append(":")
              .append(String.valueOf(ts[lastIdx]));
        }
        return sB.append("]").toString();
    }

    private interface LeafIterator<E> {
        E[] nextLeaf();
        E[] reqLeaf();
        E[] prevLeaf();
    }

    /**
     Vectors of less than 32 items are incredibly popular and often end up being created in inner
     loops with vec() from static imports.  They need to be as fast as possible, thus deserving
     their own class.
     */
    private static final class Nest0<E> implements ImList<E> {
        private final E[] tail;

        private Nest0(E[] es) { tail = es; }

        /** {@inheritDoc} */
        @Override public ImList<E> append(E e) {
            if (tail.length < (MAX_BUCKET_LENGTH - 1)) {
                E[] newTail = Arrays.copyOf(tail, tail.length + 1);
                newTail[tail.length] = e;
                return new Nest0<>(newTail);
            } else if (tail.length == MAX_BUCKET_LENGTH) {
                // tail fills first bucket
                // new tail contains appended item
                // New length is incremented.
                return new NestingVector<>(Node1.ofLeaf(tail),
                                           singletonArray(e),
                                           MAX_BUCKET_LENGTH + 1);
            } else {
                throw new IllegalStateException("Somehow tail got to be longer than" +
                                                " MAX_BUCKET_LENGTH");
            }
        }

        /** {@inheritDoc} */
        @Override public ImList<E> replace(int idx, E e) {
            return new Nest0<>(copyReplace(tail, idx, e));
        }

        /** {@inheritDoc} */
        @Override public int size() { return tail.length; }

        /** {@inheritDoc} */
        // Note: arrays correctly throw ArrayIndexOutOfBoundsExceptions to satisfy the contract for
        // this method.
        @Override public E get(int index) { return tail[index]; }

        /** {@inheritDoc} */
        @Override public UnmodListIterator<E> listIterator(final int index) {
            return new UnmodListIterator<E>() {
                int idx = index;

                @Override public boolean hasNext() { return idx < (tail.length - 1); }

                @Override public E next() {
                    E ret = tail[idx];
                    idx = idx + 1;
                    return ret;
                }

                @Override public boolean hasPrevious() { return idx >= 0; }

                @Override public E previous() {
                    idx = idx - 1;
                    return tail[idx];
                }

                @Override public int nextIndex() { return idx; }
            };
        }
    }

    // These are all actually branch nodes, the leaves are stored in simple arrays.
    private interface Node<T> {
        boolean isFull();
        Node<T> pushLeafArray(T[] leaf);
        Node<T> newChild(T[] leaf);
        Node<T> create(Node<T>[] kids);
        Node<T> fullPromote(T[] leaf);
        T get(int index);
        Node<T> replace(int idx, T e);
        LeafIterator<T> leafIteratorFor(int idx);

//        UnmodListIterator<T> listIterator(int index);
    }

    // There is no Node0 because it's just a wrapped array and we made a one-off wrapper class
    // called Nest0 above to handle that case.  A true NestingVector instance is only created
    // when there are more than 32 items.

    // Assumes each leaf is full.
    private static class Node1<T> implements Node<T> {
        // Index into higher-level array
        private static final int SHIFT = BUCKET_LENGTH_POW_2;
        private static final int HIGH_AND_MASK = LOW_BITS;

        private final T[][] tree;
        private Node1(T[][] t) { tree = t; }

        // Returns a new node of just one leaf.
        @SuppressWarnings("unchecked")
        public static <T> Node1<T> ofLeaf(T[] leaf) {
            return new Node1<>((T[][]) new Object[][] { leaf });
        }

        @Override public boolean isFull() {
            return tree.length == MAX_BUCKET_LENGTH;
        }

        @Override public Node<T> newChild(T[] leaf) {
            throw new UnsupportedOperationException("This level doesn't need this operation.");
        }

        @Override public Node<T> create(Node<T>[] kids) {
            throw new UnsupportedOperationException("This level can't support this operation.");
        }

        @SuppressWarnings("unchecked")
        @Override public Node2<T> fullPromote(T[] leaf) {
            return new Node2<>((Node1<T>[]) new Node1[] { this, Node1.ofLeaf(leaf) });
        }

        @Override public Node<T> pushLeafArray(T[] leaf) {
            if (isFull()) { return fullPromote(leaf); }
            // Allocate a new tree.
            // Copy all the old packed arrays of items to the new array
            T[][] newTree = Arrays.copyOf(tree, tree.length + 1);
            newTree[tree.length] = leaf;
            return new Node1<>(newTree);
        }

        @Override public T get(int index) {
            return tree[index >> SHIFT][index & HIGH_AND_MASK];
        }

        @Override public Node1<T> replace(int index, T e) {
            return new Node1<>(copyReplace(tree, index >> SHIFT, copyReplace(tree[index >> SHIFT], index & HIGH_AND_MASK, e
                                           )
            ));
        }

//        /** {@inheritDoc} */
//        @Override public UnmodListIterator<T> listIterator(final int index) {
//            return new UnmodListIterator<T>() {
//                // Max index is the size of all the full buckets, plus the partial bucket.
//                final int size = ((tree.length - 1) * MAX_BUCKET_LENGTH) +
//                                 tree[tree.length - 1].length;
//                int idx = index;
//                T[] leaf = tree[idx >> SHIFT];
//
//                @Override public boolean hasNext() { return idx < size; }
//
//                @Override public T next() {
//                    T ret = leaf[idx & HIGH_AND_MASK];
//                    idx = idx + 1;
//                    // Did we just roll into the next leaf?
//                    if ( (idx & HIGH_AND_MASK) == 0 ) {
//                        leaf = tree[idx >> SHIFT];
//                    }
//                    return ret;
//                }
//
//                @Override public boolean hasPrevious() { return idx >= 0; }
//
//                @Override public T previous() {
//                    int maskedIdx = idx & HIGH_AND_MASK;
//                    idx = idx - 1;
//                    // Did we just roll down to the previous leaf?
//                    if ( maskedIdx == HIGH_AND_MASK ) {
//                        leaf = tree[idx >> SHIFT];
//                    }
//                    return leaf[maskedIdx];
//                }
//
//                @Override public int nextIndex() { return idx; }
//            };
//        }

        @Override public LeafIterator<T> leafIteratorFor(int index) {
            return new LeafIterator<T>() {
                // Max index is the size of all the full buckets, plus the partial bucket.
                int leafIdx = index >> SHIFT;

                @Override public T[] nextLeaf() {
                    leafIdx = leafIdx + 1;
                    if (leafIdx >= tree.length) {
                        return null;
                    }
                    return tree[leafIdx];
                }

                @Override public T[] reqLeaf() {
                    return tree[leafIdx];
                }

                @Override public T[] prevLeaf() {
                    leafIdx = leafIdx - 1;
                    if (leafIdx < 0) {
                        return null;
                    }
                    return tree[leafIdx];
                }
            };
        }

        public String toString() {
            if (tree.length == 1) {
                T[] subTree = tree[0];
                return "Node1(subTree0" + abbrevArrayStr(subTree) + ")";
            }

            int lastSubTreeIdx = tree.length - 1;
            return "Node1(subTree0" + abbrevArrayStr(tree[0]) + "...subTree" +
                   lastSubTreeIdx + abbrevArrayStr(tree[lastSubTreeIdx]) + ")";
        }
    }

    private static class Node2<T> implements Node<T> {
        private static final int SHIFT = BUCKET_LENGTH_POW_2 * 2;
        private static final int HIGH_AND_MASK = (1 << SHIFT) - 1;

        final Node<T>[] nodes;
        private Node2(Node<T>[] ns) { nodes = ns; }

        // Returns a new node of just one leaf.
        @SuppressWarnings("unchecked")
        public static <T> Node2<T> ofLeaf(T[] leaf) {
            return new Node2<>((Node1<T>[]) new Node1[] { Node1.ofLeaf(leaf) });
        }

        @Override public boolean isFull() {
            return (nodes.length == MAX_BUCKET_LENGTH) &&
                   nodes[MAX_BUCKET_LENGTH - 1].isFull();
        }

        @Override public Node<T> newChild(T[] leaf) { return Node1.ofLeaf(leaf); }

        @Override public Node2<T> create(Node<T>[] kids) { return new Node2<>(kids); }

        @SuppressWarnings("unchecked")
        @Override public Node3<T> fullPromote(T[] leaf) {
            return new Node3<>((Node2<T>[]) new Node2[] { this, Node2.ofLeaf(leaf) });
        }

        @Override public Node<T> pushLeafArray(T[] leaf) {
            if (isFull()) { return fullPromote(leaf); }

//            System.out.println("Trying to add: " + Arrays.toString(leaf));
//            System.out.println("To: " + this);

            // Try to push to last existing node
            Node<T> appendNode = nodes[nodes.length - 1];
            if (appendNode.isFull()) {
                // Allocate a new, longer node list
                // Copy all the old nodes to the new array
                Node<T>[] newNodes = Arrays.copyOf(nodes, nodes.length + 1);
                // Make a new "right kind" of node here...
                newNodes[nodes.length] = newChild(leaf);
                return create(newNodes);
            } else {
                // Allocate a new node list (same length as the old one)
                // Copy all the unchanged nodes to the new array
                Node<T>[] newNodes = Arrays.copyOf(nodes, nodes.length);
                // replace the last node
                newNodes[nodes.length - 1] = appendNode.pushLeafArray(leaf);
                return create(newNodes);
            }
        }

        @Override public T get(int index) {
            return nodes[index >> SHIFT].get(index & HIGH_AND_MASK);
        }

        @Override public Node2<T> replace(int index, T e) {
            int nodeIdx = index >> SHIFT;
            return new Node2<>(copyReplace(nodes,
                                           nodeIdx, nodes[nodeIdx].replace(index & HIGH_AND_MASK, e)
            ));
        }

        @Override public LeafIterator<T> leafIteratorFor(int index) {
            return new LeafIterator<T>() {
                // Max index is the size of all the full buckets, plus the partial bucket.
                int nodeIdx = index >> SHIFT;

                LeafIterator<T> iter = nodes[nodeIdx].leafIteratorFor(index & HIGH_AND_MASK);

                @Override public T[] nextLeaf() {
                    // Presumably the caller won't keep calling after the first null.
//                    if (nodeIdx >= nodes.length) {
//                        return null;
//                    }
                    T[] ret = iter.nextLeaf();
                    if (ret == null) {
                        nodeIdx = nodeIdx + 1;
                        if (nodeIdx >= nodes.length) {
                            return null;
                        }
                        iter = nodes[nodeIdx].leafIteratorFor(index & HIGH_AND_MASK);
                        ret = iter.nextLeaf();
                    }
                    return ret;
                }

                @Override public T[] reqLeaf() {
                    return iter.reqLeaf();
                }

                @Override public T[] prevLeaf() {
                    nodeIdx = nodeIdx - 1;
                    if (nodeIdx <= 0) {
                        return null;
                    }
                    T[] ret = iter.prevLeaf();
                    if (ret == null) {
                        iter = nodes[nodeIdx].leafIteratorFor(index & HIGH_AND_MASK);
                        ret = iter.nextLeaf();
                        iter.prevLeaf();
                    }
                    return ret;
                }
            };
        }

        public String toString() {
            return "Node2("+ abbrevArrayStr(nodes) + ")";
        }
    }

    private static class Node3<T> extends Node2<T> {
        private static final int SHIFT = BUCKET_LENGTH_POW_2 * 3;
        private static final int HIGH_AND_MASK = (1 << SHIFT) - 1;

        private Node3(Node<T>[] ns) { super(ns); }

        // Returns a new node of just one leaf.
        @SuppressWarnings("unchecked")
        public static <T> Node3<T> ofLeaf(T[] leaf) {
            return new Node3<>((Node2<T>[]) new Node2[] { Node2.ofLeaf(leaf) });
        }

        @Override public Node<T> newChild(T[] leaf) { return Node2.ofLeaf(leaf); }

        @Override public Node3<T> create(Node<T>[] kids) { return new Node3<>(kids); }

        @SuppressWarnings("unchecked")
        @Override public Node4<T> fullPromote(T[] leaf) {
            return new Node4<>((Node3<T>[]) new Node3[] { this, Node3.ofLeaf(leaf) });
        }

        @Override public T get(int index) {
//            System.out.println("Get on: " + this);
            return super.nodes[index >> SHIFT].get(index & HIGH_AND_MASK);
        }

        @Override public Node3<T> replace(int index, T e) {
            int nodeIdx = index >> SHIFT;
            return new Node3<>(copyReplace(super.nodes,
                                           nodeIdx, super.nodes[nodeIdx].replace(index & HIGH_AND_MASK, e)
            ));
        }

        public String toString() {
            return "Node3("+ abbrevArrayStr(nodes) + ")";
        }
    }

    private static class Node4<T> extends Node3<T> {
        private static final int SHIFT = BUCKET_LENGTH_POW_2 * 4;
        private static final int HIGH_AND_MASK = (1 << SHIFT) - 1;

        private Node4(Node<T>[] ns) { super(ns); }

        // Returns a new node of just one leaf.
        @SuppressWarnings("unchecked")
        public static <T> Node4<T> ofLeaf(T[] leaf) {
            return new Node4<>((Node3<T>[]) new Node3[] { Node3.ofLeaf(leaf) });
        }

        @Override public Node<T> newChild(T[] leaf) { return Node3.ofLeaf(leaf); }

        @Override public Node4<T> create(Node<T>[] kids) { return new Node4<>(kids); }

        @SuppressWarnings("unchecked")
        @Override public Node5<T> fullPromote(T[] leaf) {
            return new Node5<>((Node4<T>[]) new Node4[] { this, Node4.ofLeaf(leaf) });
        }

        @Override public T get(int index) {
            return super.nodes[index >> SHIFT].get(index & HIGH_AND_MASK);
        }

        @Override public Node4<T> replace(int index, T e) {
            int nodeIdx = index >> SHIFT;
            return new Node4<>(copyReplace(super.nodes,
                                           nodeIdx, super.nodes[nodeIdx].replace(index & HIGH_AND_MASK, e)
            ));
        }

        public String toString() {
            return "Node4("+ abbrevArrayStr(nodes) + ")";
        }
    }

    private static class Node5<T> extends Node4<T> {
        private static final int SHIFT = BUCKET_LENGTH_POW_2 * 5;
        private static final int HIGH_AND_MASK = (1 << SHIFT) - 1;

        private Node5(Node<T>[] ns) { super(ns); }

        // Returns a new node of just one leaf.
        @SuppressWarnings("unchecked")
        public static <T> Node5<T> ofLeaf(T[] leaf) {
            return new Node5<>((Node4<T>[]) new Node4[] { Node4.ofLeaf(leaf) });
        }

        @Override public Node<T> newChild(T[] leaf) { return Node4.ofLeaf(leaf); }

        @Override public Node5<T> create(Node<T>[] kids) { return new Node5<>(kids); }

        @SuppressWarnings("unchecked")
        @Override public Node6<T> fullPromote(T[] leaf) {
            return new Node6<>((Node5<T>[]) new Node5[] { this, Node5.ofLeaf(leaf) });
        }


        @Override public T get(int index) {
            return super.nodes[index >> SHIFT].get(index & HIGH_AND_MASK);
        }

        @Override public Node5<T> replace(int index, T e) {
            int nodeIdx = index >> SHIFT;
            return new Node5<>(copyReplace(super.nodes,
                                           nodeIdx, super.nodes[nodeIdx].replace(index & HIGH_AND_MASK, e)
            ));
        }

        public String toString() {
            return "Node5("+ abbrevArrayStr(nodes) + ")";
        }
    }

    // Integer.MAX_VALUE is 2147483647.  Log base 32 of 2147483647 is only 6.2, so this seventh
    // level can barely be used.  Still, it can be used a little, so we have to include it.
    private static class Node6<T> extends Node5<T> {
        private static final int SHIFT = BUCKET_LENGTH_POW_2 * 6;
        private static final int HIGH_AND_MASK = (1 << SHIFT) - 1; // TODO: Did we shift off the end?

        private Node6(Node<T>[] ns) { super(ns); }

        // Returns a new node of just one leaf.
        @SuppressWarnings("unchecked")
        public static <T> Node6<T> ofLeaf(T[] leaf) {
            return new Node6<>((Node5<T>[]) new Node5[] { Node5.ofLeaf(leaf) });
        }

        @Override public Node<T> newChild(T[] leaf) { return Node5.ofLeaf(leaf); }

        @Override public Node6<T> create(Node<T>[] kids) { return new Node6<>(kids); }

        @Override public Node6<T> fullPromote(T[] leaf) {
            throw new UnsupportedOperationException("This should be unreachable.");
        }

        @Override public T get(int index) {
            return super.nodes[index >> SHIFT].get(index & HIGH_AND_MASK);
        }

        @Override public Node6<T> replace(int index, T e) {
            int nodeIdx = index >> SHIFT;
            return new Node6<>(copyReplace(super.nodes,
                                           nodeIdx, super.nodes[nodeIdx].replace(index & HIGH_AND_MASK, e)
            ));
        }

        public String toString() {
            return "Node6("+ abbrevArrayStr(nodes) + ")";
        }
    }

    @SuppressWarnings("unchecked")
    public static final ImList EMPTY = new Nest0(new Object[0]);

    @SuppressWarnings("unchecked")
    public static <E> ImList<E> empty() {return (ImList<E>) EMPTY; }

    // These three fields (plus the wrapper object) are what's duplicated for each change operation.
    private final int size;
    private final Node<E> tree;
    private final E[] tail;

    private NestingVector(Node<E> es, E[] tl, int sz) { tree = es; tail = tl; size = sz; }

    public static <E> ImList<E> ofArray(E[] es) {
        if ( (es == null) || (es.length < 1) ) {
            return empty();
        }
        if (es.length < MAX_BUCKET_LENGTH) {
            return new Nest0<>(Arrays.copyOf(es, es.length));
        }

        // Make the first bucket
        Node<E> node = Node1.ofLeaf(Arrays.copyOf(es, MAX_BUCKET_LENGTH));
        int tailLen = es.length % MAX_BUCKET_LENGTH;

        // For each subsequent bucket, just push leaves into existing Node
        for (int i = MAX_BUCKET_LENGTH; i < es.length - tailLen; i += MAX_BUCKET_LENGTH) {
//            System.out.println("Copied array: " + Arrays.asList(copy));
            node = node.pushLeafArray(Arrays.copyOfRange(es, i, i + MAX_BUCKET_LENGTH));
        }

        // The remainder goes into the tail.
//        System.out.println("Copied tail: " + Arrays.asList(newTail));

        // Return a new vector of the node we made, plust the new tail.
        return new NestingVector<>(node,
                                   Arrays.copyOfRange(es, es.length - tailLen, es.length),
                                   es.length);
    }

    @SuppressWarnings("unchecked")
    public static <E> ImList<E> of(Iterable<E> es) {
        if (es == null) {
            return empty();
        }
        if (es instanceof List) {
            List<E> ls = (List<E>) es;
            if (ls.size() < 1) {
                return empty();
            }

            // Short vectors have their own lighter-weight class that wraps a simple array.
            if (ls.size() <= MAX_BUCKET_LENGTH) {
                return new Nest0<>((E[]) ls.toArray());
            }

            // Make the first bucket
            E[] tmp = (E[]) ls.subList(0, MAX_BUCKET_LENGTH).toArray();
//            System.out.println("First bucket: " + Arrays.toString(tmp));
            Node<E> node = Node1.ofLeaf(tmp);
            int tailLen = ls.size() % MAX_BUCKET_LENGTH;

            int maxIdx = ls.size() - 1;
            // For each subsequent bucket, just push leaves into existing Node
            int i = MAX_BUCKET_LENGTH;
            for (; i < maxIdx - tailLen; i += MAX_BUCKET_LENGTH) {
                E[] cpy = (E[]) ls.subList(i, i + MAX_BUCKET_LENGTH).toArray();
//                System.out.println("Chunk of node size: " + cpy.length);
//                System.out.println("Chunk of node: " + Arrays.toString(cpy));
                node = node.pushLeafArray(cpy);
            }
            // If we skip the above loop (when the input is too short), then i is correct for
            // what's below.  If we pop out of the loop after going around a few times, then i is
            // one too big.  Instead of doing something over and over inside the loop, just
            // correct i once here.
            if (i > MAX_BUCKET_LENGTH) {
                i = i - 1;
            }

//            System.out.println("final i: " + i);
//            System.out.println("ls.get(i): " + ls.get(i));
//            System.out.println("maxIdx: " + maxIdx);
            // The remainder goes into the tail.
            E[] newTail = (E[]) ls.subList(i, ls.size()).toArray();
//            System.out.println("Copied tail: " + Arrays.toString(newTail));

            // Return a new vector of the node we made, plust the new tail.
            return new NestingVector<>(node, newTail, ls.size());
        } else {
            Iterator<E> iter = es.iterator();
            E[] tempArray = null;
            Node<E> node = null;
            int i = 0;
            for (; iter.hasNext(); i++) {
                if ((i % MAX_BUCKET_LENGTH) == 0) {
                    if (tempArray != null) {
                        if (node == null) {
                            node = Node1.ofLeaf(tempArray);
                        }
                        node = node.pushLeafArray(tempArray);
                    }
                    tempArray = arrayOfLength(MAX_BUCKET_LENGTH);
                }
            }
            if (tempArray == null) {
                return empty();
            }
            if (i < MAX_BUCKET_LENGTH) { // Could be if node == null
                return new Nest0<>(Arrays.copyOfRange(tempArray, 0, i));
            }
            return new NestingVector<>(node, tempArray, i);
        }
    }

    /** {@inheritDoc} */
    @Override public ImList<E> append(E e) {
        // Cases:
        //  - Room left in tail - just add it there!
        //  - Tail is full - check last internal data structure
        //    - Room left: promote tail to new leaf node.
        //    - No room: Grow tree from top, make new node with tail added.

        if (tail.length < (MAX_BUCKET_LENGTH - 1)) {
            // Simple case is to just append to the tail
            E[] newTail = Arrays.copyOf(tail, tail.length + 1);
            newTail[tail.length] = e;
            return new NestingVector<>(tree, newTail, size + 1);
        } else {
            return new NestingVector<>(tree.pushLeafArray(tail), singletonArray(e),
                                       size + 1);
        }
    }

    private int tailStartIdx() { return size - tail.length; }

    /** {@inheritDoc} */
    @Override public NestingVector<E> replace(int idx, E e) {
        if ((idx < 0) || (idx >= size) ) { throw new IndexOutOfBoundsException(); }
        int tailStartIdx = tailStartIdx();
        if (idx >= tailStartIdx) {
            return new NestingVector<>(tree, copyReplace(tail, idx - tailStartIdx, e), size);
        }
        return new NestingVector<>(tree.replace(idx, e), tail, size);
    }

    /** {@inheritDoc} */
    @Override public E get(int idx) {
        if ((idx < 0) || (idx >= size) ) { throw new IndexOutOfBoundsException(); }
        int tailStartIdx = tailStartIdx();
//        System.out.println("tailStartIdx: " + tailStartIdx);
        if (idx >= tailStartIdx) {
//            System.out.println("idx - tailStartIdx: " + (idx - tailStartIdx));
            return tail[idx - tailStartIdx];
        }
//        if ( (size >= 22599) && (idx == 0) ) {
//            System.out.println("Get on: " + this);
//        }
        return tree.get(idx);
    }

    /** {@inheritDoc} */
    @Override public int size() { return size; }

    /** {@inheritDoc} */
    @Override public boolean equals(Object o) {
        if (this == o) { return true; }
        if (!(o instanceof ImList)) { return false; }
        ImList that = (ImList) o;
        return (size == that.size()) &&
               UnmodSortedIterable.equals(this, that);
    }

    /** {@inheritDoc} */
    @Override public int hashCode() { return UnmodIterable.hashCode(this); }

    /** {@inheritDoc} */
    @Override public UnmodListIterator<E> listIterator(final int index) {
        return new UnmodListIterator<E>() {

            final int tailStartIdx = tailStartIdx() - 1;
            final int lastTreeIdx = tailStartIdx - 1;
            int idx = index;
            LeafIterator<E> leafIter = (idx >= tailStartIdx) ? null
                                                             : tree.leafIteratorFor(idx);
            E[] leaf = (idx >= tailStartIdx) ? tail
                                             : leafIter.reqLeaf();

            @Override public boolean hasNext() { return idx < size; }

            @Override public E next() {
                int maskedIdx = idx & LOW_BITS;
                E ret = leaf[maskedIdx];
                idx = idx + 1;
                // We just rolled into the next leaf
                if ((maskedIdx) == 0) {
                    leaf = leafIter.nextLeaf();
                    if (leaf == null) {
                        leaf = tail;
                    }
                }
                return ret;
            }

            @Override public boolean hasPrevious() { return idx > 0; }

            @Override public E previous() {
                int maskedIdx = idx & LOW_BITS;
                idx = idx - 1;
                // Did we just roll down to the previous leaf?
                if (maskedIdx == LOW_BITS) {
                    if (idx == lastTreeIdx) {
                        leafIter = tree.leafIteratorFor(idx);
                    } else {
                        leaf = leafIter.prevLeaf();
                    }
                }
                return leaf[maskedIdx];
            }

            @Override public int nextIndex() { return idx; }
        };
    }

    public String toString() {
        return "NestingVector(" + tree + " tail: " + abbrevArrayStr(tail) + ")";
    }
}
