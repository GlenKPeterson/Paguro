// Copyright 2016-05-28 PlanBase Inc. & Glen Peterson
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
package org.organicdesign.fp.experimental;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

import org.organicdesign.fp.collections.ImList;
import org.organicdesign.fp.collections.UnmodSortedIterable;
import org.organicdesign.fp.tuple.Tuple2;

import static org.organicdesign.fp.StaticImports.tup;

/**
 This is an experiment - DO NOT USE except to test.
 This is based on the paper, "RRB-Trees: Efficient Immutable Vectors" by Phil Bagwell and
 Tiark Rompf.  With some background from the Cormen, Leiserson, Rivest & Stein Algorithms book entry
 on B-Trees.  Also with an awareness of the Clojure PersistentVector by Rich Hickey.  All errors
 are by Glen Peterson.

 Priorities:
 append(item)
 get(index)
 insert(item, index)
 */
public class RrbTree1<E> implements ImList<E> {

    // Definitions:
    // Radix: technically refers to the width of all the nodes in the tree, which presumes
    //        a uniform width (and sort of suggests a power of 2 to take advantage of bit shifting
    //        which is a key performance reason for the uniform width.  However, for brevity,
    //        I'm going to have Radix nodes which have all the same leaf widths and are perfectly
    //        left-filled and packed up to the last full node.  The size of each node is
    //        RADIX_NODE_LENGTH.
    // Relaxed: In this case refers to "Relaxed Radix" which means the nodes are of somewhat varying
    //          sizes.  The sizes range from MINIMUM_DEGREE (as defined by Cormen et al) to
    //          MAX_NODE_LENGTH.

    // There's bit shifting going on here because it's a very fast operation.
    // Shifting right by 5 is eons faster than dividing by 32.
    // TODO: Change to 5.
    private static final int NODE_LENGTH_POW_2 = 2; // 2 for testing now, 5 for real later.
    private static final int RADIX_NODE_LENGTH = 1 << NODE_LENGTH_POW_2;// 0b00000000000000000000000000100000 = 0x20 = 32

    // (MinDegree + MaxDegree) / 2 should equal Radix so that they have the same average node size
    // to make the index guessing easier.
    private static final int MINIMUM_DEGREE = RADIX_NODE_LENGTH * 2 / 3;
    private static final int MAX_NODE_LENGTH = (MINIMUM_DEGREE * 2) - 1;

    // In the PersistentVector, this is called the tail, but here it can be at
    // Other areas of the tree besides the tail.
//    private E[] focus;
    // All the tree nodes from the root to the block in focus.
//    private Node<E>[] display;


    // =================================== Array Helper Functions ==================================
    // We only one empty array and it makes the code simpler than pointing to null all the time.
    // Have to time the difference between using this and null.  The only difference I can imagine
    // is that this has an address in memory and null does not, so it could save a memory lookup
    // in some places.
    private static final Object[] EMPTY_ARRAY = new Object[0];

    // Helper function to avoid type warnings.
    @SuppressWarnings("unchecked")
    private static <T> T[] emptyArray() { return (T[]) EMPTY_ARRAY; }

    // Helper function to avoid type warnings.
    @SuppressWarnings("unchecked")
    private static <T> T[] singleElementArray(T elem) { return (T[]) new Object[] { elem }; }

    @SuppressWarnings("unchecked")
    private static <T> T[] insertIntoArrayAt(T item, T[] items, int idx, Class<T> tClass) {
        // Make an array that's one bigger.  It's too bad that the JVM bothers to
        // initialize this with nulls.

        T[] newItems = (T[]) ( (tClass == null) ? new Object[items.length + 1]
                                                : Array.newInstance(tClass, items.length + 1) );

        // If we aren't inserting at the first item, array-copy the items before the insert
        // point.
        if (idx > 0) {
            System.arraycopy(items, 0, newItems, 0, idx);
        }

        // Insert the new item.
        newItems[idx] = item;

        // If we aren't inserting at the last item, array-copy the items after the insert
        // point.
        if (idx < items.length) {
            System.arraycopy(items, idx, newItems, idx + 1, items.length - idx);
        }

        return newItems;
    }

    private static <T> T[] insertIntoArrayAt(T item, T[] items, int idx) {
        return insertIntoArrayAt(item, items, idx, null);
    }

    @SuppressWarnings("unchecked")
    private static <T> T[] spliceIntoArrayAt(T[] insertedItems, T[] origItems, int idx) {
        // Make an array that big enough.  It's too bad that the JVM bothers to
        // initialize this with nulls.
        T[] newItems = (T[]) new Object[insertedItems.length + origItems.length];

        // If we aren't inserting at the first item, array-copy the items before the insert
        // point.
        if (idx > 0) {
            System.arraycopy(origItems, 0, newItems, 0, idx);
        }

        // Insert the new items
        System.arraycopy(insertedItems, 0, newItems, idx, insertedItems.length);

        // If we aren't inserting at the last item, array-copy the items after the insert
        // point.
        if (idx < origItems.length) {
            System.arraycopy(origItems, idx, newItems, idx + insertedItems.length,
                             origItems.length - idx);
        }
        return newItems;
    }

    @SuppressWarnings("unchecked")
    private static <T> T[] replaceInArrayAt(T replacedItem, T[] origItems, int idx, Class<T> tClass) {
        // Make an array that big enough.  It's too bad that the JVM bothers to
        // initialize this with nulls.
        T[] newItems = (T[]) ( (tClass == null) ? new Object[origItems.length]
                                                : Array.newInstance(tClass, origItems.length) );
        System.arraycopy(origItems, 0, newItems, 0, origItems.length);
        newItems[idx] = replacedItem;
        return newItems;
    }

    private static <T> T[] replaceInArrayAt(T replacedItem, T[] origItems, int idx) {
        return replaceInArrayAt(replacedItem, origItems, idx, null);
    }

    private static RrbTree1 EMPTY_RRB_TREE =
            new RrbTree1<>(emptyArray(), 0, NodeLeaf.emptyNodeLeaf(), 0);

    /**
     This is the public factory method.
     @return the empty RRB-Tree (there is only one)
     */
    @SuppressWarnings("unchecked")
    public static <T> RrbTree1<T> empty() { return (RrbTree1<T>) EMPTY_RRB_TREE; }

    // Focus is like the tail in Rich Hickey's Persistent Vector, but named after the structure
    // in Scala's implementation.  Tail and focus are both designed to allow repeated appends or
    // inserts to the same area of a vector to be done in constant time.  Tail only handles appends
    // but this can handle repeated inserts to any area of a vector.
    private final E[] focus;
    private final int focusStartIndex;
    private final Node<E> root;
    private final int size;

    // Constructor
    private RrbTree1(E[] f, int fi, Node<E> r, int s) {
        focus = f; focusStartIndex = fi; root = r; size = s;
    }

    @Override public int size() { return size; }

    @Override public boolean equals(Object other) {
        if (this == other) { return true; }
        if ( !(other instanceof List) ) { return false; }
        List that = (List) other;
        return (this.size() == that.size()) &&
               UnmodSortedIterable.equals(this, UnmodSortedIterable.castFromList(that));
    }

    /** This is correct, but O(n).  This implementation is compatible with java.util.AbstractList. */
    @Override public int hashCode() {
        int ret = 1;
        for (E item : this) {
            ret *= 31;
            if (item != null) {
                ret += item.hashCode();
            }
        }
        return ret;
    }

    @Override  public E get(int i) {
//        System.out.println("get(" + i + ")");
        if ( (i < 0) || (i > size) ) {
            throw new IndexOutOfBoundsException("Index: " + i + " size: " + size);
        }
        if (i >= focusStartIndex) {
            int focusOffset = i - focusStartIndex;
            if (focusOffset < focus.length) {
                return focus[focusOffset];
            }
            i -= focus.length;
        }
//        System.out.println("  focusStartIndex: " + focusStartIndex);
//        System.out.println("  focus.length: " + focus.length);
//        System.out.println("  adjusted index: " + i);
        return root.get(i);
    }

    /**
     Adds an item at the end of this structure.  This is the most efficient way to build an
     RRB Tree as it conforms to the Clojure PersistentVector and all of its optimizations.
     @param t the item to append
     @return a new RRB-Tree with the item appended.
     */
    @Override  public RrbTree1<E> append(E t) {
//        System.out.println("=== append(" + t + ") ===");
        // If our focus isn't set up for appends or if it's full, insert it into the data structure
        // where it belongs.  Then make a new focus
        if ( ( (focusStartIndex < root.maxIndex()) && (focus.length > 0) ) ||
             (focus.length >= RADIX_NODE_LENGTH) ) {
            // TODO: Does focusStartIndex only work for the root node, or is it translated as it goes down?
            Node<E> newRoot = root.pushFocus(focusStartIndex, focus);
            E[] newFocus = singleElementArray(t);
            return new RrbTree1<>(newFocus, size, newRoot, size + 1);
        }

        E[] newFocus = insertIntoArrayAt(t, focus, focus.length);
        return new RrbTree1<>(newFocus, focusStartIndex, root, size + 1);
    }

    /**
     I would have called this insert and reversed the order or parameters.
     @param idx the insertion point
     @param element the item to insert
     @return a new RRB-Tree with the item inserted.
     */
    public RrbTree1<E> insert(int idx, E element) {
        System.out.println("insert(int " + idx + ", E " + element + ")");

        // If the focus is full, push it into the tree and make a new one with the new element.
        if (focus.length >= RADIX_NODE_LENGTH) {
            Node<E> newRoot = root.pushFocus(focusStartIndex, focus);
            E[] newFocus = singleElementArray(element);
            return new RrbTree1<>(newFocus, idx, newRoot, size + 1);
        }

        // If the index is within the focus, add the item there.
        int diff = idx - focusStartIndex;
        System.out.println("diff: " + diff);

        if ( (diff >= 0) && (diff < focus.length) ) {
            System.out.println("new focus...");
            E[] newFocus = insertIntoArrayAt(element, focus, diff);
            return new RrbTree1<>(newFocus, focusStartIndex, root, size + 1);
        }

        // Here we are left with an insert somewhere else than the current focus.
        Node<E> newRoot = focus.length > 0 ? root.pushFocus(focusStartIndex, focus)
                                           : root;
        E[] newFocus = singleElementArray(element);
        return new RrbTree1<>(newFocus, idx, newRoot, size + 1);
    }

    /**
     Replace the item at the given index.  Note: i.replace(i.size(), o) used to be equivalent to
     i.concat(o), but it probably won't be for the RRB tree implementation, so this will change too.

     @param i the index where the value should be stored.
     @param t   the value to store
     @return a new RrbTree1 with the replaced item
     */
    @Override
    public RrbTree1<E> replace(int i, E t) {
        if ( (i < 0) || (i > size) ) {
            throw new IndexOutOfBoundsException("Index: " + i + " size: " + size);
        }
        if (i >= focusStartIndex) {
            int focusOffset = i - focusStartIndex;
            if (focusOffset < focus.length) {
                return new RrbTree1<>(replaceInArrayAt(t, focus, focusOffset),
                                      focusStartIndex, root, size);
            }
            i -= focus.length;
        }
        return new RrbTree1<>(focus, focusStartIndex, root.replace(i, t), size);
    }

    private interface Node<T> {
        /** Return the item at the given index */
        T get(int i);
        /** Highest index returnable by this node */
        int maxIndex();
        /** Inserts an item at the given index */
//        @Override public Node<T> insert(int i, T item);
        Node<T> append(T item);
        /** Returns true if this node's array is not full */
        boolean thisNodeHasCapacity();
        /** Returns true if this strict-Radix tree can take another 32 items. */
        boolean hasRadixCapacity();
        Tuple2<? extends Node<T>,? extends Node<T>> splitAt(int i);

        // Because we want to append/insert into the focus as much as possible, we will treat
        // the insert or append of a single item as a degenerate case.  Instead, the primary way
        // to add to the internal data structure will be to push the entire focus array into it
        Node<T> pushFocus(int index, T[] oldFocus);

        Node<T> replace(int idx, T t);
    }

    private static class NodeLeaf<T> implements Node<T> {
        private static final NodeLeaf EMPTY_NODE_LEAF = new NodeLeaf<>(EMPTY_ARRAY);
        @SuppressWarnings("unchecked")
        private static final <T> NodeLeaf<T> emptyNodeLeaf() {
            return (NodeLeaf<T>) EMPTY_NODE_LEAF;
        }

        T[] items;
        // It can only be Radix if items.length == RADIX_NODE_LENGTH and if its parents
        // are radix.
//        boolean isRadix;
        NodeLeaf(T[] ts) { items = ts; }
        @Override public T get(int i) { return items[i]; }
        @Override public int maxIndex() { return items.length; }
        @Override public Node<T> append(T item) {
            T[] newItems = Arrays.copyOf(items, items.length + 1);
            newItems[items.length] = item;
            return new NodeLeaf<>(newItems);
        }
        // If we want to add one more to an existing leaf node, it must already be part of a
        // relaxed tree.
        @Override public boolean thisNodeHasCapacity() {
            return items.length < MAX_NODE_LENGTH;
        }

        @Override public boolean hasRadixCapacity() { return false; }

        /**
         This is a Relaxed operation.  Performing it on an Exact Radix node causes it and all
         ancestors to become Relaxed Radix.  The parent should only split when
         size < MINIMUM_DEGREE during a slice operation.

         @param i the index to split before.
         @return Two new nodes.
         */
        @Override  public Tuple2<NodeLeaf<T>,NodeLeaf<T>> splitAt(int i) {
            // TODO: if we split for an insert-when-full, one side of the split should be bigger in preparation for the insert.
            if (i == 0) {
                return tup(emptyNodeLeaf(), this);
            }
            if (i == items.length) {
                // Not sure this can possibly be called, but just in case...
                return tup(this, emptyNodeLeaf());
            }

            return tup(new NodeLeaf<>(Arrays.copyOf(items, i)),
                       new NodeLeaf<>(Arrays.copyOfRange(items, i, items.length - i)));
        }


        // I think this can only be called when the root node is a leaf.
        @SuppressWarnings("unchecked")
        @Override public Node<T> pushFocus(int index, T[] oldFocus) {
            if (oldFocus.length == 0) {
                throw new IllegalStateException("Never call this with an empty focus!");
            }
            // We put the empty LeafNode as the root of the empty vector and it stays there
            // until the first call to this method, at which point, the oldFocus becomes the
            // new root.
            if (items.length == 0) {
                return new NodeLeaf<>(oldFocus);
            }
            // If we there is room for the entire focus to fit into this node, just stick it in
            // there!
            if ( (items.length + oldFocus.length) < MAX_NODE_LENGTH ) {
                return new NodeLeaf<>(spliceIntoArrayAt(oldFocus, items, index));
            }
            if ( (items.length == RADIX_NODE_LENGTH) &&
                 (oldFocus.length == RADIX_NODE_LENGTH) &&
                 (index == RADIX_NODE_LENGTH) ) {

                return new NodeRadix<>(NODE_LENGTH_POW_2,
                                       (NodeLeaf<T>[]) new NodeLeaf[] { this,
                                                                        new NodeLeaf<>(oldFocus)});
            }

            System.out.println("pushFocus(" + Arrays.toString(oldFocus) + ", " + index + ")");
            System.out.println("  items.length: " + items.length);
            System.out.println("  oldFocus.length: " + oldFocus.length);

            // Ugh, we have to chop it across 2 arrays.
            // TODO: Gets complicated!
            throw new UnsupportedOperationException("Not implemented yet!");
        }

        @Override
        public Node<T> replace(int idx, T t) {
            return new NodeLeaf<>(replaceInArrayAt(t, items, idx));
        }

        //        @Override
        public NodeLeaf<T> insert(int i, T item) {
            if (!thisNodeHasCapacity()) {
                throw new IllegalStateException("Called insert, but can't add one more!" +
                                                "  Parent should have called split first.");
            }

            // Return our new node.
            return new NodeLeaf<>(insertIntoArrayAt(item, items, i));
        }

        @Override public String toString() {
//            return "NodeLeaf("+ Arrays.toString(items) + ")";
            return Arrays.toString(items);
        }
    }

    // Contains a left-packed tree of exactly 32-item nodes.
    private static class NodeRadix<T> implements Node<T> {
        // This is the number of levels below this node (height) times NODE_LENGTH
        // For speed, we calculate it as height << NODE_LENGTH_POW_2
        // TODO: Can we store shift at the top-level NodeRadix only?
        int shift;
        // These are the child nodes
        Node<T>[] nodes;
        // Constructor
        NodeRadix(int s, Node<T>[] ns) {
            shift = s; nodes = ns;
//            System.out.println("    new Radix" + shift + Arrays.toString(ns));
        }

        /**
         Returns the high bits which we use to index into our array.  This is the simplicity (and
         speed) of Radix indexing.  When everything works, this can be inlined for performance.
         This could maybe yield a good guess for Relaxed nodes?
         */
        private int highBits(int i) { return i >> shift; }

        /**
         Returns the low bits of the index (the part Radix sub-nodes need to know about).
         This helps make this data structure simple and fast.  When everything works, this can
         be inlined for performance.
         DO NOT use this for Relaxed nodes - they use subtraction instead!
         */
        private int lowBits(int i) {
            int shifter = -1 << shift;

//            System.out.println("    shifter (binary): " + Integer.toBinaryString(shift));

            int invShifter = ~shifter;
//            System.out.println("    invShifter (binary): " + Integer.toBinaryString(invShifter));

//            System.out.println("             i (binary): " + Integer.toBinaryString(invShifter));
            return  i & invShifter;
//            System.out.println("    subNodeIdx (binary): " + Integer.toBinaryString(subNodeIdx));
//            System.out.println("    subNodeIdx: " + subNodeIdx);
        }

        @Override public T get(int i) {
//            System.out.println("  NodeRadix.get(" + i + ")");
            // Find the node indexed by the high bits (for this height).
            // Send the low bits on to our sub-nodes.
            return nodes[highBits(i)].get(lowBits(i));
        }
        @Override public int maxIndex() {
            int lastNodeIdx = nodes.length - 1;
//            System.out.println("    NodeRadix.maxIndex()");
//            System.out.println("      nodes.length:" + nodes.length);
//            System.out.println("      shift:" + shift);
//            System.out.println("      RADIX_NODE_LENGTH:" + RADIX_NODE_LENGTH);

            // Add up all the full nodes (only the last can be partial)
            int shiftedLength = lastNodeIdx << shift;
//            System.out.println("      shifed length:" + shiftedLength);
            int partialNodeSize = nodes[lastNodeIdx].maxIndex();
//            System.out.println("      Remainder:" + partialNodeSize);
            return shiftedLength + partialNodeSize;
        }
        @Override public boolean thisNodeHasCapacity() {
            return nodes.length < RADIX_NODE_LENGTH;
        }

        @Override public boolean hasRadixCapacity() {
            return thisNodeHasCapacity() || nodes[nodes.length - 1].hasRadixCapacity();
        }

        @Override
        public Tuple2<Node<T>,Node<T>> splitAt(int i) {
            // TODO: Implement
            throw new UnsupportedOperationException("Not implemented yet");
        }

        @SuppressWarnings("unchecked")
        @Override
        public Node<T> pushFocus(int index, T[] oldFocus) {
//            System.out.println("Radix pushFocus(" + Arrays.toString(oldFocus) + ", " + index + ")");
//            System.out.println("  this: " + this);

            // It's a radix-compatible addition if the focus being pushed is of
            // RADIX_NODE_LENGTH and the index it's pushed to falls on the final leaf-node boundary.
            //
            // TODO: I think we could support this on ANY leaf-node boundary if the children of this
            // node are leaves and this node is not full, but for now we'll just punt to a
            // RelaxedNode when that happens, which can only be within the last 32 leaf nodes
            // so it's a small corner-case optimization.
            if ( (oldFocus.length == RADIX_NODE_LENGTH) &&
                 (index == maxIndex()) ) {

                // If the proper sub-node can take the additional array, let it!
                int subNodeIndex = highBits(index);
//                System.out.println("  subNodeIndex: " + subNodeIndex);

                Node<T> lastNode = nodes[nodes.length - 1];
                if (lastNode.hasRadixCapacity()) {
//                    System.out.println("  Pushing the focus down to a lower-level node with capacity.");
                    Node<T> newNode = lastNode.pushFocus(lowBits(index), oldFocus);
                    Node<T>[] newArray = replaceInArrayAt(newNode, nodes, nodes.length - 1, Node.class);
                    return new NodeRadix<>(shift, newArray);
                }
                // Regardless of what else happens, we're going to add a new node.
                Node<T> newNode = new NodeLeaf<>(oldFocus);

                // Make a skinny branch of a tree by walking up from the leaf node until our
                // new branch is at the same level as the old one.  We have to build evenly
                // (like hotels in Monopoly) in order to keep the tree balanced.  Even height,
                // but left-packed (the lower indices must all be filled before adding new
                // nodes to the right).
                int newShift = NODE_LENGTH_POW_2;

                // If we've got space in our array, we just have to add skinny-branch nodes up to
                // the level below ours.  But if we don't have space, we have to add a
                // single-element radix node at the same level as ours here too.
                int maxShift = (nodes.length < RADIX_NODE_LENGTH) ? shift : shift + 1;

                // Make the skinny-branch of single-element radix nodes:
                while (newShift < maxShift) {
//                    System.out.println("  Adding a skinny branch node...");
                    Node<T>[] newArray = (Node<T>[]) Array.newInstance(newNode.getClass(), 1);
                    newArray[0] = newNode;
                    newNode = new NodeRadix<>(newShift, newArray);
                    newShift += NODE_LENGTH_POW_2;
                }

                if ( (nodes.length < RADIX_NODE_LENGTH) ) {
//                    System.out.println("  Adding a node to the existing array");
                    Node<T>[] newNodes = (Node<T>[]) insertIntoArrayAt(newNode, nodes, subNodeIndex, Node.class);
                    // This could allow cheap radix inserts on any leaf-node boundary...
                    return new NodeRadix<>(shift, newNodes);
                } else {
//                    System.out.println("  Adding a level to the Radix tree");
                    return new NodeRadix(shift + NODE_LENGTH_POW_2,
                                         new Node[] { this, newNode });
                }

//                System.out.println("  nodes.length: " + nodes.length);
//
//                throw new UnsupportedOperationException("Not implemented yet");

//                // TODO: Implement
//                Node<T> subNode = nodes[subNodeIndex];
//                if (subNode.hasRadixCapacity()) {
//                    Node<T>[] newNodes = replaceInArrayAt(subNode.pushFocus(oldFocus, (index & (-1 << shift))), nodes, subNodeIndex);
//                    return new NodeRadix<>(shift, newNodes);
//                }
//
//                throw new UnsupportedOperationException("Not implemented yet");

//                // If we have room for one more, pop it in there!
//                if (nodes.length < RADIX_NODE_LENGTH) {
//                    insertIntoArrayAt(t, focus, focus.length);
//                }
            }

            System.out.println("  oldFocus.length: " + oldFocus.length);
            System.out.println("  index: " + index);
            System.out.println("  maxIndex(): " + maxIndex());
            System.out.println("  nodes.length: " + nodes.length);
            System.out.println("  this: " + this);

            // TODO: Implement
            throw new UnsupportedOperationException("Relaxed (non-append) node insertion not implemented yet");
        }

        @SuppressWarnings("unchecked")
        @Override
        public Node<T> replace(int idx, T t) {
//            System.out.println("  NodeRadix.get(" + i + ")");
            // Find the node indexed by the high bits (for this height).
            // Send the low bits on to our sub-nodes.
            int thisNodeIdx = highBits(idx);
            Node<T> newNode = nodes[thisNodeIdx].replace(lowBits(idx), t);
            return new NodeRadix<>(shift, replaceInArrayAt(newNode, nodes, thisNodeIdx, Node.class));
        }

//        @Override public Tuple2<NodeRadix<T>,NodeRadix<T>> split() {
//            NodeRadix<T> right = new NodeRadix<T>(shift, new NodeRadix[0]);
//            return tup(this, right);
//        }

        @Override public NodeRadix<T> append(T item) {
            Node<T> last = nodes[nodes.length - 1];
            if (last.thisNodeHasCapacity()) {
                // Make a copy of our node array
                Node<T>[] newNodes = Arrays.copyOf(nodes, nodes.length);
                // Replace the last node with the updated one.
                newNodes[nodes.length - 1] = last.append(item);
                // Return new, updated node.
                return new NodeRadix<>(shift, newNodes);
            }
            if (nodes.length >= RADIX_NODE_LENGTH) {
                throw new UnsupportedOperationException("This I think can only happen to the root node.");
            } else {
                // Make a larger copy of our node array
                Node<T>[] newNodes = Arrays.copyOf(nodes, nodes.length + 1);
                // Add a new node at the end of it.
                newNodes[nodes.length] = new NodeLeaf<>(singleElementArray(item));
                // Return new, updated node.
                return new NodeRadix<>(shift, newNodes);
            }
        }
        @Override public String toString() {
//            return "NodeRadix(nodes.length="+ nodes.length + ", shift=" + shift + ")";
            return "Radix" + shift + Arrays.toString(nodes);
        }
    }

    // Contains a relaxed tree of nodes that average around 32 items each.
    private class NodeRelaxed<T> implements Node<T> {
        // TODO: Long-term, assume the first index is zero and only store subsequent ones.
        // For now, we store them all for safety.
        int[] startIndices;
        Node<T>[] nodes;

        // Constructor
        NodeRelaxed(int[] is, Node<T>[] ns) { startIndices = is; nodes = ns; }


        @Override public T get(int i) {
            // TODO: This is really slow and therefore wrong:
            int idx = 0;
            for (int test : startIndices) {
                if (test < i) {
                    idx = test;
                } else {
                    break;
                }
            }
            return nodes[idx].get(i - idx);
        }

        @Override public int maxIndex() {
            return startIndices[startIndices.length - 1];
        }

        @Override public Tuple2<NodeRelaxed<T>,NodeRelaxed<T>> splitAt(int i) {
//            int midpoint = nodes.length >> 1; // Shift-right one is the same as dividing by 2.
            NodeRelaxed<T> left = new NodeRelaxed<>(Arrays.copyOf(startIndices, i),
                                                    Arrays.copyOf(nodes, i));
            // I checked this at javaRepl and indeed this starts from the correct item.
            NodeRelaxed<T> right = new NodeRelaxed<>(Arrays.copyOfRange(startIndices, i, nodes.length),
                                                     Arrays.copyOfRange(nodes, i, nodes.length));
            return tup(left, right);
        }

        @Override public Node<T> append(T item) {
            Node<T> last = nodes[nodes.length - 1];
            if (last.thisNodeHasCapacity()) {
                // Make a copy of our node array
                Node<T>[] newNodes = Arrays.copyOf(nodes, nodes.length);
                // Replace the last node with the updated one.
                newNodes[nodes.length - 1] = last.append(item);
                // Return new, updated node.
                return new NodeRelaxed<>(startIndices, newNodes);
            }
            if (nodes.length >= MAX_NODE_LENGTH) {
                throw new UnsupportedOperationException("This I think can only happen to the root node.");
            } else {
                // Make a larger copy of our node array
                Node<T>[] newNodes = Arrays.copyOf(nodes, nodes.length + 1);
                // Split the last node into two. (Shift-right one is the same as dividing by 2.)
                Tuple2<? extends Node<T>,? extends Node<T>> splitNodes = last.splitAt(last.maxIndex() >> 1);
                // Put the left split node where the old node was
                newNodes[nodes.length - 1] = splitNodes._1();
                // Append the item to the right node and add that at the new end position.
                newNodes[nodes.length] = splitNodes._2().append(item);
                // Return new, updated node.
                return new NodeRelaxed<>(startIndices, newNodes);
            }
        }


        @Override public boolean thisNodeHasCapacity() {
            return nodes.length < MAX_NODE_LENGTH;
        }

        @Override public boolean hasRadixCapacity() { return false; }

        @Override
        public RrbTree1.Node<T> pushFocus(int index, T[] oldFocus) {
            // TODO: Implement
            throw new UnsupportedOperationException("Not Implemented Yet");
        }

        @Override
        public Node<T> replace(int idx, T t) {
            // TODO: Implement
            throw new UnsupportedOperationException("Not Implemented Yet");
        }

        @Override public String toString() { return "NodeRelaxed(nodes.length="+ nodes.length + ")"; }
    }
}
