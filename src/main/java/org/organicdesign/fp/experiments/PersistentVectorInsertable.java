// Copyright 2015-05-17 PlanBase Inc. & Glen Peterson
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
package org.organicdesign.fp.experiments;

import org.organicdesign.fp.FunctionUtils;
import org.organicdesign.fp.Option;
import org.organicdesign.fp.collections.ImList;
import org.organicdesign.fp.collections.UnListIterator;
import org.organicdesign.fp.tuple.Tuple2;

// TODO: This isn't anything yet.
// Need to implement a B-tree: Cormen, Leiserson, Rivest, Stein p. 490.  Mistakes are my own.
// B-Tree is also: https://en.wikipedia.org/wiki/B-tree
// Except storing the range on each node requires making all new nodes if you insert into the leftmost node,
// so I'm going to store the size of all sub-nodes.  At each level, you'll have to examine each pointer to lower-levels
// in order, adding them up, to see if the index you want is down that path, or a path to the right.
// If the requested index > (size / 2) then search from right to left instead. bringing the worst-case search time
// down to about n/2 instead of n
public class PersistentVectorInsertable<V> implements ImList<V> {
    // This must be even.
    private static final int MAX_NODE_LENGTH = 4; // TODO: Make this 32, or whatever even number performs the best.

    static abstract class Node<V> {
        final int totalSize; // of this node and all sub-nodes.
        final Object[] values;
        Node(int s, Object[] v) {
            totalSize = s; values = v;
        }
        public abstract boolean isLeaf();
        boolean isFull() { return values.length == MAX_NODE_LENGTH; }
    }

    private static class Leaf<V> extends Node<V> {
        @SuppressWarnings("unchecked")
        private static <V> V[] arrayOfOne(V value) {
            V[] items = (V[]) new Object[1];
            items[0] = value;
            return items;
        }

        static final Leaf<?> EMPTY = new Leaf<>(0, new Object[0]);
        @SuppressWarnings("unchecked")
        static <V> Leaf<V> empty() { return (Leaf<V>) EMPTY; }

        Leaf(int s, V[] v) { super(s, v); }

        Leaf(V v) { super(1, arrayOfOne(v)); }

        @Override public boolean isLeaf() { return true; }

        @SuppressWarnings("unchecked")
        public V valueAt(int idx) { return (V) values[idx]; }

        @SuppressWarnings("unchecked")
        public V[] values() { return (V[]) values; }

        public String toString() { return "Leaf(" + totalSize + "," + FunctionUtils.toString(values) + ")"; }

        @SuppressWarnings("unchecked")
        Leaf<V> insertReplaceSlave(int idx, V v, boolean isInsert) {
            V[] newKids = (V[]) new Object[isInsert ? values.length + 1 : values.length];
            // Copy the old kids up to the split kid
            System.arraycopy(values, 0, newKids, 0, idx);
            newKids[idx] = v;
            if (idx < (values.length - 1)) {
                // Copy the old kids from the split-kid onward
                System.arraycopy(values, (isInsert ? idx : idx + 1), newKids, idx + 1, (values.length - idx));

            }
            return new Leaf<>(totalSize + 1, newKids);
        }

        public Leaf<V> replace(int idx, V v) { return insertReplaceSlave(idx, v, false); }

        public Leaf<V> insertNonFull(int idx, V v) {
            if (isFull()) { throw new IllegalStateException("Can't insert on a full node!"); }
            return insertReplaceSlave(idx, v, true);
        }
    }
    private static class Branch<V> extends Node<V> {
        Branch(int s, Node<V>[] v) { super(s, v); }

        @SuppressWarnings("unchecked")
        Branch(Node<V> n) { super(n.totalSize, new Node[]{n}); }

        @Override public boolean isLeaf() { return false; }

        @SuppressWarnings("unchecked")
        public Node<V> nodeAt(int idx) { return (Node<V>) values[idx]; }

        @SuppressWarnings("unchecked")
        public Node<V>[] values() { return (Node<V>[]) values; }

        public String toString() { return "Branch(" + totalSize + "," + FunctionUtils.toString(values) + ")"; }

        @SuppressWarnings("unchecked")
        Branch<V> insertReplaceSlave(int idx, Node<V> v, boolean isInsert) {
//            System.out.println("insertReplaceSlave(" + idx + ", " + v + ", " + isInsert + ")");
//            System.out.println("\tvalues: " + FunctionUtils.toString(values));
            Node<V>[] newKids = (Node<V>[]) new Node[isInsert ? values.length + 1 : values.length];
            // Copy the old kids up to the split kid
            System.arraycopy(values, 0, newKids, 0, Math.min(idx, values.length));
//            System.out.println("\tcopied old kids up to insert point: " + FunctionUtils.toString(newKids));
            newKids[idx] = v;
//            System.out.println("\tInserted new kid at insertion point: " + FunctionUtils.toString(newKids));
            if (idx < (values.length - 1)) {
                // Copy the old kids from the split-kid onward
                System.arraycopy(values, (isInsert ? idx : idx + 1), newKids, idx + 1, (values.length - idx));
//                System.out.println("\tCopied remaining old kids after insertion point: " + FunctionUtils.toString(newKids));

            }
            Branch<V> b = new Branch<>(totalSize + 1, newKids);
//            System.out.println("\tinsertReplaceSlave returns: " + b);
            return b;
        }

        public Branch<V> replace(int idx, Node<V> v) { return insertReplaceSlave(idx, v, false); }

        public Branch<V> insertNonFull(int idx, Node<V> v) {
//            System.out.println("insertNonFull(" + idx + ", " + v + ")");
            if (isFull()) { throw new IllegalStateException("Can't insert on a full node!"); }
            return insertReplaceSlave(idx, v, true);
        }
    }

    /**
     Returns the node for the given index in the tree, or else null.
     Based on Cormen, Leiserson, Rivest, Stein pp. 494-495.  Mistakes are my own.
     x becomes node
     x.n is node.totalSize
     key becomes idx
     @param node a non-full branch-node.
     @param idx the index of the full child node to split.
     @return a new branch node containing all the old sub-nodes up to the split child,
     then 2 new nodes (the results of splitting the child), then the old sub-nodes after the split child.
     */
    @SuppressWarnings("unchecked")
    static <V> Branch<V> splitChild(Branch<V> node, int idx) {
//        System.out.println("splitChild(" + node + ", " + idx + ")");
        if (node.isFull()) {
            throw new IllegalArgumentException("Can't call splitChild on a full parent node.");
        }
        if (node.isLeaf()) {
            throw new IllegalArgumentException("Can't call splitChild on a leaf node.");
        }
        Node<V>[] newKids = new Node[node.values.length + 1];

        // Copy the old kids up to the split kid
        System.arraycopy(node.values(), 0, newKids, 0, idx);

//        System.out.println("node.values(): " + FunctionUtils.toString(node.values()) +
//                           " node.values.length: " + node.values.length +
//                           " newKids.length: " + newKids.length +
//                           " idx: " + idx);

        // Copy the old kids from the split-kid onward, leaving 2 spaces in the new array for the split kids.
        System.arraycopy(node.values(), idx + 1, newKids, idx + 2, (node.values.length - idx) - 1);

        Node<V> splitKid = node.nodeAt(idx);
        if (!splitKid.isFull()) {
            throw new IllegalStateException("We only split full nodes");
        }
        int halfLen = splitKid.values.length / 2;

        if (splitKid.isLeaf()) {
            Object[] leftGrandkids = new Object[halfLen];
            Object[] rightGrandkids = new Object[halfLen];

            System.arraycopy(splitKid.values, 0, leftGrandkids, 0, halfLen);
            System.arraycopy(splitKid.values, halfLen, rightGrandkids, 0, halfLen);

            newKids[idx] = new Leaf<>(halfLen, (V[]) leftGrandkids);
            newKids[idx + 1] = new Leaf<>(halfLen, (V[]) rightGrandkids);
        } else {
            Node<V>[] leftGrandkids = (Node<V>[]) new Node[halfLen];
            Node<V>[] rightGrandkids = (Node<V>[]) new Node[halfLen];

            System.arraycopy(((Branch<V>) splitKid).values(), 0, leftGrandkids, 0, halfLen);
            System.arraycopy(((Branch<V>) splitKid).values(), halfLen, rightGrandkids, 0, halfLen);

            int leftKidSize = 0;
            int rightKidSize = 0;
            for (int i = 0; i < halfLen; i++) {
                leftKidSize += leftGrandkids[i].totalSize;
                rightKidSize += rightGrandkids[i].totalSize;
            }

            newKids[idx] = new Branch<>(leftKidSize, leftGrandkids);
            newKids[idx + 1] = new Branch<>(rightKidSize, rightGrandkids);
        }

        return new Branch<>(node.totalSize, newKids);
    }

    // TODO: Rename every idx, index, origIdx to sizeIdx, or valIdx.
    /**
     Based on Cormen, Leiserson, Rivest, Stein pp. 496.  Mistakes are my own.
     */
    private static <V> Node<V> insertNonFull(Node<V> node, final int origIdx, V v) {
//        System.out.println("insertNonFull(" + node + ", " + v + ", " + origIdx + ")");

        // TODO: I prevent these situations elsewhere, so can skip this check.
        if ( (origIdx < 0) || (origIdx > (node.totalSize)) ) {
            throw new IllegalArgumentException("Can't have an index < 0 or > totalSize");
        }

        if (node.isLeaf()) {
            return ((Leaf<V>) node).insertNonFull(origIdx, v);
        }
        Branch<V> branch = (Branch<V>) node;
        int sizeIdx = origIdx;
        for (int valIdx = 0; valIdx < branch.values.length; valIdx++) {
            Node<V> nextLevNode = branch.nodeAt(valIdx);
//            System.out.println("nextLevNode: " + nextLevNode);
            // We've found the right node if this nextLevNode contains our index,
            // or if the index is one greater than the index of the last nextLevNode (append to that node).
            if ( (sizeIdx < nextLevNode.totalSize) ||
                 (valIdx == (branch.values.length - 1)) ) {
//                System.out.println("sizeIdx:" + sizeIdx + " < nextLevNode.totalSize:" + nextLevNode.totalSize);
                if (nextLevNode.isFull()) {
//                    System.out.println("\nnextLevNode.isFull, so call splitChild...");
                    // here we have a new branch, otherwise, it's an old one.  Do we care?
                    branch = splitChild(branch, valIdx); // need the index relative to branch's nodes.
//                    System.out.println("\nHere is our new branch with the splitChild: " + branch);

                    nextLevNode = branch.nodeAt(valIdx);

                    // Unfortunately, now what we've split the child into 2, there is only a 50% chance that we still
                    // have the sizeIdx of the correct child because the spot we want could have ended up in either
                    // side of the split.
                    if (nextLevNode.totalSize < sizeIdx) {
                        // Eew, this is so imperative.
                        sizeIdx -= nextLevNode.totalSize;
                        valIdx++;
                        nextLevNode = branch.nodeAt(valIdx);
                    }
                }
                Node<V> tempNode = insertNonFull(nextLevNode, sizeIdx, v);
//                System.out.println("Here is the temporary node returned by insetNonFull: " + tempNode);
                Node<V> newBranch = branch.replace(valIdx, tempNode);
//                System.out.println("about to return newBranch: " + newBranch);
                return newBranch;

//                    // Recursive call
//                    Node<V> subNode = insertNonFull(nextLevNode, v, idx);
//
//                    // Need an array with room for one more.
//                    Node<V>[] newKids = arrayOfSize(branch.values.length + 1);
//                    // Copy the old kids up to the split kid
//                    System.arraycopy(branch.values(), 0, newKids, 0, idx);
//                    newKids[idx] = subNode;
//                    // Copy the old kids from the split-kid onward, leaving 2 spaces in the new array for the split kids.
//                    System.arraycopy(branch.values(), idx + 1, newKids, idx + 1, (node.values.length - idx));
//                    return new Branch<>(node.totalSize + 1, newKids);

            }
            sizeIdx -= nextLevNode.totalSize;
        }
        throw new IllegalStateException("Couldn't find a place for the insert.");
//        // Append at end.
//        System.out.println("\tBefore append at end: " + branch);
//        Branch<V> newBranch = branch.insertNonFull(branch.values.length, new Leaf<>(v));
//        System.out.println("\tReturning: " + newBranch);
//        return newBranch;
    }

//    @SuppressWarnings("unchecked")
//    private static <V> V[] arrayOfSize(int sz) {
//        return (V[]) new Object[sz];
//    }

    @SuppressWarnings("unchecked") public static final PersistentVectorInsertable EMPTY =
            new PersistentVectorInsertable(Leaf.empty());

    @SuppressWarnings("unchecked")
    public static <V> PersistentVectorInsertable<V> empty() { return EMPTY; }


    // Fields
    private final Node<V> tree;

    // Constructor
    private PersistentVectorInsertable(Node<V> t) {
        tree = t;
    }

    /** {@inheritDoc} */
    @Override public ImList<V> append(V v) { return insert(tree.totalSize, v); }

    @Override public PersistentVectorInsertable<V> insert(int idx, V v) {
        if (tree == null) {
            return new PersistentVectorInsertable<>(new Leaf<>(v));
        }
        if (tree.isFull()) {
            // We can't split a full root node.
            // Make a new branch node with only the root of this tree in it.
            // Then split that new node to make a new vector.
            // Then call insert on that and return the result.
//            System.out.println("\tTree is full: " + tree);
            Branch<V> nextTree = new Branch<>(tree);
//            System.out.println("\tPushed tree into new Node: " + nextTree);
            nextTree = splitChild(nextTree, 0);
//            System.out.println("\tSplit the original node: " + nextTree);
//            System.out.println("\tAbout to insert new item at: " + idx);
            Node<V> lastTree = insertNonFull(nextTree, idx, v);
//            System.out.println("\tInserted new item: " + lastTree);

            return new PersistentVectorInsertable<>(lastTree);
        }
        return new PersistentVectorInsertable<>(insertNonFull(tree, idx, v));
    }

    /** {@inheritDoc} */
    @Override public ImList<V> put(int idx, V v) {
        // TODO: Implement (after renaming to replace to differentiate from insert)
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /** {@inheritDoc} */
    @Override public int size() { return tree.totalSize; }

    /** {@inheritDoc} */
    @Override public V get(int index) {
        Option<Tuple2<Leaf<V>,Integer>> o = findNodeForIdx(tree, index);
        if (o.isSome()) {
            Tuple2<Leaf<V>,Integer> t = o.get();
            return t._1().valueAt(t._2());
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override public UnListIterator<V> listIterator(int index) {
        // TODO: Implement
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /** {@inheritDoc} */
    @Override public String toString() {
        return tree.toString();
    }


    /**
     Returns the node for the given index in the tree, or else null.
     Based on Cormen, Leiserson, Rivest, Stein pp. 491-492.  Mistakes are my own.
     x becomes node
     x.n is node.totalSize
     key becomes idx
     @param node the node to start searching in (starts at top node of tree)
     @param idx the index within this part of the tree to find.
     @return a tuple containing the matching node and the index of the key within that node.  Or null if not found.
     */
    Option<Tuple2<Leaf<V>,Integer>> findNodeForIdx(Node<V> node, int idx) {
        // TODO: This check is only necessary at the top level, but it's safer to check every time while debugging.
//        System.out.println("node.totalSize: " + node.totalSize + " idx: " + idx);
        if ( (idx < 0) || (idx >= node.totalSize) ) {
            throw new IndexOutOfBoundsException();
        }
        if (node.isLeaf()) {
            return Option.of(Tuple2.of((Leaf<V>) node, idx));
        }
        // TODO: search left-right or right-left by comparing idx to node.totalSize / 2
        // Search from left to right for now.
        for (int i = 0; i < node.values.length; i++) {
            Node<V> nextLevNode = ((Branch<V>) node).nodeAt(i);
            if (idx < nextLevNode.totalSize) {
                // Recursive call
                return findNodeForIdx(nextLevNode, idx);
            }
            idx -= nextLevNode.totalSize;
        }
        return Option.none(); // not found.
    }
}
