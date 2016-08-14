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
import org.organicdesign.fp.collections.UnmodIterable;
import org.organicdesign.fp.collections.UnmodSortedIterable;
import org.organicdesign.fp.tuple.Tuple2;
import org.organicdesign.fp.tuple.Tuple4;

interface Indented { String indentedStr(int indent); }

/**
 This is based on the paper, "RRB-Trees: Efficient Immutable Vectors" by Phil Bagwell and
 Tiark Rompf.  With some background from the Cormen, Leiserson, Rivest & Stein Algorithms book entry
 on B-Trees.  Also with an awareness of the Clojure PersistentVector by Rich Hickey.  All errors
 are by Glen Peterson.

 Still TO-DO:
  - More Testing
  - Tuple2&lt;RrbTree1,RrbTree1&gt; split(int index)
  - insert(int index, RrbTree1 other)
  - remove(int index)
  - Change radix from 4 to 32.
  - Speed testing and optimization.
 */
@SuppressWarnings("WeakerAccess")
public class RrbTree1<E> implements ImList<E>, Indented {

    // Definitions:
    // Strict: Short for "Strict Radix."  Strict nodes have leaf widths of exactly
    //         STRICT_NODE_LENGTH and are left-filled and packed up to the last full node.  This
    //         lets us use a power of 2 to take advantage of bit shifting to exactly index which
    //         sub-node an item is found in.  Does not support inserts (only appends).
    // Relaxed: short for "Relaxed Radix."   Relaxed nodes are of somewhat varying sizes, ranging
    //          from MIN_NODE_LENGTH (Cormen et al calls this "Minimum Degree") to MAX_NODE_LENGTH.
    //          This requires linear interpolation, a bit of searching, and subtraction to find an
    //          index into a sub-node, but supports inserts, split, and combine (with another
    //          RrbTree)

    // There's bit shifting going on here because it's a very fast operation.
    // Shifting right by 5 is eons faster than dividing by 32.
    // TODO: Change to 5.
    private static final int NODE_LENGTH_POW_2 = 2; // 2 for testing now, 5 for real later.

    // 0b00000000000000000000000000100000 = 0x20 = 32
    private static final int STRICT_NODE_LENGTH = 1 << NODE_LENGTH_POW_2;

    // (MIN_NODE_LENGTH + MAX_NODE_LENGTH) / 2 should equal STRICT_NODE_LENGTH so that they have the
    // same average node size to make the index interpolation easier.
    private static final int MIN_NODE_LENGTH = (STRICT_NODE_LENGTH+1) * 2 / 3;
    // Always check if less-than this.  Never less-than-or-equal.  Cormen adds a -1 here and tests
    // for <= (I think!).
    private static final int MAX_NODE_LENGTH = ( (STRICT_NODE_LENGTH+1) * 4 / 3);

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
    private static <T> T[] spliceIntoArrayAt(T[] insertedItems, T[] origItems, int idx,
                                             Class<T> tClass) {
        // Make an array that big enough.  It's too bad that the JVM bothers to
        // initialize this with nulls.
        T[] newItems = (T[]) Array.newInstance(tClass, insertedItems.length + origItems.length);

        // If we aren't inserting at the first item, array-copy the items before the insert
        // point.
        if (idx > 0) {
            //               src,  srcPos, dest,destPos,length
            System.arraycopy(origItems, 0, newItems, 0, idx);
        }

        // Insert the new items
        //               src,      srcPos,     dest, destPos, length
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
    private static <T> T[] replaceInArrayAt(T replacedItem, T[] origItems, int idx,
                                            Class<T> tClass) {
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

    private static StringBuilder indentSpace(int len) {
        StringBuilder sB = new StringBuilder();
        while (len >= 32) {
            sB.append("                                ");
            len -= 32;
        }
        while (len >= 16) {
            sB.append("                ");
            len -= 16;
        }
        while (len >= 8) {
            sB.append("        ");
            len -= 8;
        }
        while (len >= 4) {
            sB.append("    ");
            len -= 4;
        }
        while (len >= 2) {
            sB.append("  ");
            len -= 2;
        }
        while (len >= 1) {
            sB.append(" ");
            len -= 1;
        }
        return sB;
    }

    private static StringBuilder showSubNodes(StringBuilder sB, Node[] nodes, int nextIndent) {
        boolean isFirst = true;
        for (Node n : nodes) {
            if (isFirst) {
                isFirst = false;
            } else {
                sB.append(",");
                if (nodes[0] instanceof Leaf) {
                    sB.append(" ");
                } else {
                    sB.append("\n").append(indentSpace(nextIndent));
                }
            }
            sB.append(n.indentedStr(nextIndent));
        }
        return sB;
    }

    private static void debug(String txt, Indented obj) { System.out.println(txt + obj.indentedStr(txt.length())); }
    private static void debug(String txt) { System.out.println(txt); }

    private static final Leaf EMPTY_LEAF = new Leaf<>(EMPTY_ARRAY);
    @SuppressWarnings("unchecked")
    private static final <T> Leaf<T> emptyLeaf() { return (Leaf<T>) EMPTY_LEAF; }

    private static final RrbTree1 EMPTY_RRB_TREE =
            new RrbTree1<>(emptyArray(), 0, emptyLeaf(), 0);

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

    /** This implementation is correct and compatible with java.util.AbstractList, but O(n). */
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
//        System.out.println("  get(" + i + ")");
        if ( (i < 0) || (i > size) ) {
            throw new IndexOutOfBoundsException("Index: " + i + " size: " + size);
        }

        // This is a debugging assertion - can't be covered by a test.
        if ( (focusStartIndex < 0) || (focusStartIndex > size) ) {
            throw new IllegalStateException("focusStartIndex: " + focusStartIndex +
                                            " size: " + size);
        }

        if (i >= focusStartIndex) {
//            System.out.println("    i>=focusStartIndex: " + focusStartIndex);
            int focusOffset = i - focusStartIndex;
            if (focusOffset < focus.length) {
                return focus[focusOffset];
            }
            i -= focus.length;
        }
//        System.out.println("    focusStartIndex: " + focusStartIndex);
//        System.out.println("    focus.length: " + focus.length);
//        System.out.println("    adjusted index: " + i);
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
        if (((focusStartIndex < root.size()) && (focus.length > 0) ) ||
            (focus.length >= STRICT_NODE_LENGTH) ) {
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
    @SuppressWarnings("WeakerAccess")
    public RrbTree1<E> insert(int idx, E element) {
//        System.out.println("insert(int " + idx + ", E " + element + ")");

        // If the focus is full, push it into the tree and make a new one with the new element.
        if (focus.length >= STRICT_NODE_LENGTH) {
            Node<E> newRoot = root.pushFocus(focusStartIndex, focus);
            E[] newFocus = singleElementArray(element);
            return new RrbTree1<>(newFocus, idx, newRoot, size + 1);
        }

        // If the index is within the focus, add the item there.
        int diff = idx - focusStartIndex;
//        System.out.println("diff: " + diff);

        if ( (diff >= 0) && (diff <= focus.length) ) {
//            System.out.println("new focus...");
            E[] newFocus = insertIntoArrayAt(element, focus, diff);
            return new RrbTree1<>(newFocus, focusStartIndex, root, size + 1);
        }

//        System.out.println("insert somewhere other than the current focus.");
//        System.out.println("focusStartIndex: " + focusStartIndex);
//        System.out.println("focus: " + Arrays.toString(focus));
        // Here we are left with an insert somewhere else than the current focus.
        Node<E> newRoot = focus.length > 0 ? root.pushFocus(focusStartIndex, focus)
                                           : root;
        E[] newFocus = singleElementArray(element);
        return new RrbTree1<>(newFocus, idx, newRoot, size + 1);
    }

    /**
     Replace the item at the given index.  Note: i.replace(i.size(), o) used to be equivalent to
     i.concat(o), but it probably won't be for the RRB tree implementation, so this will change too.

     @param index the index where the value should be stored.
     @param item   the value to store
     @return a new RrbTree1 with the replaced item
     */
    @Override
    public RrbTree1<E> replace(int index, E item) {
//        System.out.println("replace(index=" + index + ", item=" + item + ")");
        if ( (index < 0) || (index > size) ) {
            throw new IndexOutOfBoundsException("Index: " + index + " size: " + size);
        }
        if (index >= focusStartIndex) {
            int focusOffset = index - focusStartIndex;
            if (focusOffset < focus.length) {
                return new RrbTree1<>(replaceInArrayAt(item, focus, focusOffset),
                                      focusStartIndex, root, size);
            }
//            System.out.println("    Subtracting focus.length");
            index -= focus.length;
        }
//        System.out.println("    About to do replace with maybe-adjusted index=" + index);
//        System.out.println("    this=" + this);
        return new RrbTree1<>(focus, focusStartIndex, root.replace(index, item), size);
    }

    /**
     Divides this RRB-Tree such that every index less-than the given index ends up in the left-hand tree
     and the indexed item and all subsequent ones end up in the right-hand tree.

     @param splitIndex the split point (excluded from the left-tree, included in the right one)
     @return two new sub-trees as determined by the split point.  If the point is 0 or this.size() one tree will be
     empty (but never null).
     */
    public Tuple2<RrbTree1<E>,RrbTree1<E>> split(int splitIndex) {
        if ( (splitIndex < 1) && (splitIndex > size) ) {
            throw new IllegalArgumentException("Constraint violation failed: 1 <= splitIndex <= size");
        }
        // Push the focus before splitting.
        Node<E> newRoot = (focus.length > 0) ? root.pushFocus(focusStartIndex, focus)
                                             : root;

        // If a leaf-node is split, the fragments become the new focus for each side of the split.
        // Otherwise, the focus can be left empty, or the last node of each side can be made into the focus.
        SplitNode<E> split = newRoot.splitAt(splitIndex);
        
        E[] leftFocus = split.leftFocus();
        Node<E> left = split.left();

        E[] rightFocus = split.rightFocus();
        Node<E> right = split.right();

        return Tuple2.of(new RrbTree1<>(leftFocus, left.size(), left, left.size() + leftFocus.length),
                         new RrbTree1<>(rightFocus, 0, right, right.size() + rightFocus.length));
    }

    @Override public String toString() {
        return UnmodIterable.toString("RrbTree", this);
    }

    @Override public String indentedStr(int indent) {
        return "RrbTree(size=" + size +
               " fsi=" + focusStartIndex +
               " focus=" + Arrays.toString(focus) + "\n" +
               indentSpace(indent + 8) + "root=" + (root == null ? "null" : root.indentedStr(indent + 13)) + ")";
    }

    private interface Node<T> extends Indented {
        /** Return the item at the given index */
        T get(int i);
        /** Number of items stored in this node */
        int size();
//        /** Returns true if this node's array is not full */
//        boolean thisNodeHasCapacity();
        /** Returns true if this strict-Radix tree can take another 32 items. */
        boolean hasStrictCapacity();

        /**
         Can we put focus at the given index without reshuffling nodes?
         @param index the index we want to insert at
         @param size the number of items to insert.  Must be size < MAX_NODE_LENGTH
         @return true if we can do so without otherwise adjusting the tree.
         */
        boolean hasRelaxedCapacity(int index, int size);

        SplitNode<T> splitAt(int splitIndex);

        // Splitting a strict node yields an invalid Relaxed node (too short).
        // We don't yet split Leaf nodes.
        // So this needs to only be implemented on Relaxed for now.
//        Relaxed<T>[] split();

        // Because we want to append/insert into the focus as much as possible, we will treat
        // the insert or append of a single item as a degenerate case.  Instead, the primary way
        // to add to the internal data structure will be to push the entire focus array into it
        Node<T> pushFocus(int index, T[] oldFocus);

        Node<T> replace(int idx, T t);
    }

    private static class SplitNode<T> extends Tuple4<Node<T>,T[],Node<T>,T[]> implements Indented {
        SplitNode(Node<T> ln, T[] lf, Node<T> rn, T[] rf) { super(ln, lf, rn, rf); }
        public Node<T> left() { return _1; }
        public T[] leftFocus() { return _2; }
        public Node<T> right() { return _3; }
        public T[] rightFocus() { return _4; }
        public int size() { return _1.size() + _2.length + _3.size() + _4.length; }

        @Override public String indentedStr(int indent) {
            StringBuilder sB = new StringBuilder() // indentSpace(indent)
                    .append("SplitNode(");
            int nextIndent = indent + sB.length();
            String nextIndentStr = indentSpace(nextIndent).toString();
            return sB.append("left=").append(left().indentedStr(nextIndent + 5)).append(",\n")
                     .append(nextIndentStr).append("leftFocus=").append(Arrays.toString(leftFocus())).append(",\n")
                     .append(nextIndentStr).append("right=").append(right().indentedStr(nextIndent + 6)).append(",\n")
                     .append(nextIndentStr).append("rightFocus=").append(Arrays.toString(rightFocus())).append(")")
                     .toString();
        }

        @Override public String toString() { return indentedStr(0); }
    }

    private static class Leaf<T> implements Node<T> {
        final T[] items;
        // It can only be Strict if items.length == STRICT_NODE_LENGTH and if its parents
        // are strict.
//        boolean isStrict;
        Leaf(T[] ts) { items = ts; }
        @Override public T get(int i) { return items[i]; }
        @Override public int size() { return items.length; }
        // If we want to add one more to an existing leaf node, it must already be part of a
        // relaxed tree.
//        public boolean thisNodeHasCapacity() { return items.length < MAX_NODE_LENGTH; }

        @Override public boolean hasStrictCapacity() { return false; }

        @Override public boolean hasRelaxedCapacity(int index, int size) {
            // Appends and prepends need to be a good size, but random inserts do not.
            if ( (size < 1) || (size >= MAX_NODE_LENGTH) ) {
                throw new IllegalArgumentException("Bad size: " + size);
              // + " MIN_NODE_LENGTH=" + MIN_NODE_LENGTH + " MAX_NODE_LENGTH=" + MAX_NODE_LENGTH);
            }
//            System.out.println("Leaf.hasRelaxedCapacity(index=" + index + ", size=" + size + ")");
//            System.out.println("   Leaf.items=" + Arrays.toString(items));
//            System.out.println("   MAX_NODE_LENGTH=" + MAX_NODE_LENGTH);
            return (items.length + size) < MAX_NODE_LENGTH;
        }

        /**
         Only call this if the array actually needs to be split (0 &lt; splitPoint &lt; orig.length).
         @param orig array to split
         @param splitIndex items less than this index go in the left, equal or greater in the right.
         @return a 2D array of leftItems then rightItems
         */
        @SuppressWarnings("unchecked")
        private T[][] splitArray(T[] orig, int splitIndex) {
            if (splitIndex < 1) {
                throw new IllegalArgumentException("Called split when splitIndex < 1");
            }
            if (splitIndex > orig.length - 1) {
                throw new IllegalArgumentException("Called split when splitIndex > orig.length - 1");
            }
            int rightLength = orig.length - splitIndex;
            T[][] split = (T[][]) new Object[][] {new Object[splitIndex],
                                                  new Object[rightLength]};
            // original array, offset, newArray, offset, length
            System.arraycopy(orig, 0, split[0], 0, splitIndex);
//            System.out.println("    left: " + Arrays.toString(left));

            System.arraycopy(orig, splitIndex, split[1], 0, rightLength);
//            System.out.println("    right: " + Arrays.toString(right));
            return split;
        }

        @Override
        public SplitNode<T> splitAt(int splitIndex) {
//            if (splitIndex < 1) {
//                throw new IllegalArgumentException("Called splitAt when splitIndex < 1");
//            }
//            if (splitIndex > items.length - 1) {
//                throw new IllegalArgumentException("Called splitAt when splitIndex > orig.length - 1");
//            }
            // Should we just ensure that the split is between 1 and items.length (exclusive)?
            if (splitIndex == 0) {
                return new SplitNode<>(emptyLeaf(), emptyArray(), emptyLeaf(), items);
            }
            if (splitIndex == items.length) {
                return new SplitNode<>(emptyLeaf(), items, emptyLeaf(), emptyArray());
            }
            T[][] split = splitArray(items, splitIndex);
            return new SplitNode<>(emptyLeaf(), split[0], emptyLeaf(), split[1]);
        }

        @SuppressWarnings("unchecked")
        private Leaf<T>[] spliceAndSplit(T[] oldFocus, int splitIndex) {
            // Consider optimizing:
            T[] newItems = spliceIntoArrayAt(oldFocus, items, splitIndex,
                                             (Class<T>) items[0].getClass());

//            System.out.println("    newItems: " + Arrays.toString(newItems));
            // Shift right one is divide-by 2.
            T[][] split = splitArray(newItems, newItems.length >> 1);

            return new Leaf[] {new Leaf<>(split[0]), new Leaf<>(split[1])};
        }

        // I think this can only be called when the root node is a leaf.
        @SuppressWarnings("unchecked")
        @Override public Node<T> pushFocus(int index, T[] oldFocus) {
            if (oldFocus.length == 0) {
                throw new IllegalStateException("Never call this with an empty focus!");
            }
            // We put the empty Leaf as the root of the empty vector and it stays there
            // until the first call to this method, at which point, the oldFocus becomes the
            // new root.
            if (items.length == 0) {
                return new Leaf<>(oldFocus);
            }

            // Try first to yield a Strict node.  For a leaf like this, that means both this node
            // and the pushed focus are STRICT_NODE_LENGTH.  It also means the old focus is being
            // pushed at either the beginning or the end of this node (not anywhere in-between).
            if ( (items.length == STRICT_NODE_LENGTH) &&
                 (oldFocus.length == STRICT_NODE_LENGTH) &&
                 ((index == STRICT_NODE_LENGTH) || (index == 0)) ) {

                Leaf<T>[] newNodes = (index == STRICT_NODE_LENGTH)
                                     ? new Leaf[] { this,
                                                    new Leaf<>(oldFocus)}
                                     : new Leaf[] { new Leaf<>(oldFocus),
                                                    this };
                return new Strict<>(NODE_LENGTH_POW_2, newNodes);
            }

            if ((items.length + oldFocus.length) < MAX_NODE_LENGTH) {
                return new Leaf<>(spliceIntoArrayAt(oldFocus, items, index,
                                                    (Class<T>) items[0].getClass()));
            }

            // We should only get here when the root node is a leaf.
            // Maybe we should be more circumspect with our array creation, but for now, just jam
            // jam it into one big array, then split it up for simplicity
            Leaf<T>[] res = spliceAndSplit(oldFocus, index);
            Leaf<T> leftLeaf = res[0];
            Leaf<T> rightLeaf = res[1];
            int leftSize = leftLeaf.size();
            return new Relaxed<>(new int[] { leftSize,
                                             leftSize + rightLeaf.size() },
                                 res);
        }

        @Override
        public Node<T> replace(int idx, T t) {
            if (idx >= size()) {
                throw new IllegalArgumentException("Invalid index " + idx + " >= " + size());
            }
            return new Leaf<>(replaceInArrayAt(t, items, idx));
        }

        @Override public String toString() {
//            return "Leaf("+ Arrays.toString(items) + ")";
            return Arrays.toString(items);
        }

        @Override public String indentedStr(int indent) {
            return Arrays.toString(items);
        }
    } // end class Leaf

    // Contains a left-packed tree of exactly 32-item nodes.
    private static class Strict<T> implements Node<T> {
        // This is the number of levels below this node (height) times NODE_LENGTH
        // For speed, we calculate it as height << NODE_LENGTH_POW_2
        // TODO: Can we store shift at the top-level Strict only?
        final int shift;
        // These are the child nodes
        final Node<T>[] nodes;
        // Constructor
        Strict(int s, Node<T>[] ns) {
            shift = s; nodes = ns;
//            System.out.println("    new Strict" + shift + Arrays.toString(ns));
        }

        /**
         Returns the high bits which we use to index into our array.  This is the simplicity (and
         speed) of Strict indexing.  When everything works, this can be inlined for performance.
         This could maybe yield a good guess for Relaxed nodes?
         */
        private int highBits(int i) { return i >> shift; }

        /**
         Returns the low bits of the index (the part Strict sub-nodes need to know about).
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
//            System.out.println("  Strict.get(" + i + ")");
            // Find the node indexed by the high bits (for this height).
            // Send the low bits on to our sub-nodes.
            return nodes[highBits(i)].get(lowBits(i));
        }
        @Override public int size() {
            int lastNodeIdx = nodes.length - 1;
//            System.out.println("    Strict.size()");
//            System.out.println("      nodes.length:" + nodes.length);
//            System.out.println("      shift:" + shift);
//            System.out.println("      STRICT_NODE_LENGTH:" + STRICT_NODE_LENGTH);

            // Add up all the full nodes (only the last can be partial)
            int shiftedLength = lastNodeIdx << shift;
//            System.out.println("      shifed length:" + shiftedLength);
            int partialNodeSize = nodes[lastNodeIdx].size();
//            System.out.println("      Remainder:" + partialNodeSize);
            return shiftedLength + partialNodeSize;
        }
        private boolean thisNodeHasCapacity() { return nodes.length < STRICT_NODE_LENGTH; }

        @Override public boolean hasStrictCapacity() {
            return thisNodeHasCapacity() || nodes[nodes.length - 1].hasStrictCapacity();
        }

        @Override public boolean hasRelaxedCapacity(int index, int size) {
            if ( (size < 1) || (size >= MAX_NODE_LENGTH) ) {
                throw new IllegalArgumentException("Bad size: " + size);
            }
            // It has relaxed capacity because a Relaxed node could have up to MAX_NODE_LENGTH nodes
            // and by definition this Strict node has exactly STRICT_NODE_LENGTH items.
            return size < MAX_NODE_LENGTH - STRICT_NODE_LENGTH;
        }

//        @SuppressWarnings("unchecked")
//        @Override public Relaxed<T>[] split() {
////            System.out.println("Strict.split(" + i + ")");
//            int midpoint = nodes.length >> 1; // Shift-right one is the same as dividing by 2.
//            int[] leftCumSizes = new int[midpoint];
//            int cumulativeSize = 0;
//            // We know all sub-nodes (except the last) have the same size because they are packed-left.
//            int subNodeSize = nodes[0].size();
//            for (int i = 0; i < midpoint; i++) {
//                cumulativeSize += subNodeSize;
//                leftCumSizes[i] = cumulativeSize;
//            }
//
//            Relaxed<T> left = new Relaxed<>(Arrays.copyOf(leftCumSizes, midpoint),
//                                            Arrays.copyOf(nodes, midpoint));
//            int[] rightCumSizes = new int[nodes.length - midpoint];
//            cumulativeSize = 0;
//            for (int i = 0; i < rightCumSizes.length - 1; i++) {
//                // I don't see any way around asking each node it's length here.
//                // The last one may not be full.
//                cumulativeSize += subNodeSize;
//                rightCumSizes[i] = cumulativeSize;
//            }
//
//            // Fix final size (may not be packed)
//            cumulativeSize += nodes[nodes.length - 1].size();
//            rightCumSizes[rightCumSizes.length - 1] = cumulativeSize;
//
//            // I checked this at javaRepl and indeed this starts from the correct item.
//            Relaxed<T> right = new Relaxed<>(rightCumSizes,
//                                             Arrays.copyOfRange(nodes, midpoint, nodes.length));
//            return new Relaxed[] {left, right};
//        }

        //        @Override public Tuple2<Strict<T>,Strict<T>> split() {
        //            Strict<T> right = new Strict<T>(shift, new Strict[0]);
        //            return tup(this, right);
        //        }

        @SuppressWarnings("unchecked")
        @Override
        public SplitNode<T> splitAt(int splitIndex) {
            int size = size();
            if ( (splitIndex < 0) || (splitIndex > size) ) {
                throw new IllegalArgumentException("Bad splitIndex: " + splitIndex);
            }
            if (splitIndex == 0) {
                return new SplitNode<>(emptyLeaf(), emptyArray(), this, emptyArray());
            }
            if (splitIndex == size) {
                return new SplitNode<>(this, emptyArray(), emptyLeaf(), emptyArray());
            }

            //            System.out.println("==========================");
            //            System.out.println("before=" + this.indentedStr(7));

            int subNodeIndex = highBits(splitIndex);
            Node<T> subNode = nodes[subNodeIndex];
            int subNodeAdjustedIndex = lowBits(splitIndex);

            SplitNode<T> split = subNode.splitAt(subNodeAdjustedIndex);

            //            debug("--------------------------");
            //            debug("before=", this);
            //            debug("splitIndex=" + splitIndex);
            //            debug("nodes.length=" + nodes.length);
            //            debug("subNodeIndex=" + subNodeIndex);
            ////            debug("subNode=", subNode);
            //            debug("split=", split);

            final Node<T> left;
            final Node<T> splitLeft = split.left();
            if (subNodeIndex == 0) {
                //                debug("If we have a single left node, it doesn't need a parent.");
                left = splitLeft;
            } else {
                boolean haveLeft = (splitLeft.size() > 0);
                int numLeftItems = subNodeIndex + (haveLeft ? 1 : 0);
                if ( !haveLeft && (numLeftItems == 1) ) {
                    //                    debug("If the left node became a focus and there are no other lefts, no parent needed.");
                    left = nodes[0];
                } else {
                    Node<T>[] leftNodes = (Node<T>[]) new Node[numLeftItems];
                    //                    debug("leftCumSizes=" + Arrays.toString(leftCumSizes));
                    // Copy one less item if we are going to add the split one in a moment.
                    // I could have written:
                    //     haveLeft ? numLeftItems - 1
                    //              : numLeftItems
                    // but that's always equal to subNodeIndex.
                    System.arraycopy(nodes, 0, leftNodes, 0, subNodeIndex);
                    if (haveLeft) {
                        leftNodes[numLeftItems - 1] = splitLeft;
                    }
                    left = new Strict<>(shift, leftNodes);
                }
            }

            final Node<T> right = Relaxed.fixRight(nodes, split.right(), subNodeIndex);

            SplitNode<T> ret = new SplitNode<>(left, split.leftFocus(),
                                               right, split.rightFocus());
            //            debug("RETURNING=", ret);
            if (this.size() != ret.size()) {
                throw new IllegalStateException("Split on " + this.size() + " items returned " + ret.size() + " items");
            }

            return ret;
        }

        Relaxed<T> relax() {
            int[] newCumSizes = new int[nodes.length];
            int cumulativeSize = 0;
            // We know all sub-nodes (except the last) have the same size because they are packed-left.
            int subNodeSize = nodes[0].size();
            for (int i = 0; i < nodes.length - 1; i++) {
                cumulativeSize += subNodeSize;
                newCumSizes[i] = cumulativeSize;
            }

            // Final node may not be packed, so it could have a different size
            cumulativeSize += nodes[nodes.length - 1].size();
            newCumSizes[newCumSizes.length - 1] = cumulativeSize;

            return new Relaxed<>(newCumSizes, nodes);
        }

        @SuppressWarnings("unchecked")
        @Override
        public Node<T> pushFocus(int index, T[] oldFocus) {
//            System.out.println("Strict pushFocus(" + Arrays.toString(oldFocus) +
//                               ", " + index + ")");
//            System.out.println("  this: " + this);

            // If the proper sub-node can take the additional array, let it!
            int subNodeIndex = highBits(index);
//                System.out.println("  subNodeIndex: " + subNodeIndex);

            // It's a strict-compatible addition if the focus being pushed is of
            // STRICT_NODE_LENGTH and the index it's pushed to falls on the final leaf-node boundary
            // and the children of this node are leaves and this node is not full.
            if (oldFocus.length == STRICT_NODE_LENGTH) {

                if (index == size()) {
                    Node<T> lastNode = nodes[nodes.length - 1];
                    if (lastNode.hasStrictCapacity()) {
//                    System.out.println("  Pushing focus down to lower-level node with capacity.");
                        Node<T> newNode = lastNode.pushFocus(lowBits(index), oldFocus);
                        Node<T>[] newNodes = replaceInArrayAt(newNode, nodes, nodes.length - 1,
                                                              Node.class);
                        return new Strict<>(shift, newNodes);
                    }
                    // Regardless of what else happens, we're going to add a new node.
                    Node<T> newNode = new Leaf<>(oldFocus);

                    // Make a skinny branch of a tree by walking up from the leaf node until our
                    // new branch is at the same level as the old one.  We have to build evenly
                    // (like hotels in Monopoly) in order to keep the tree balanced.  Even height,
                    // but left-packed (the lower indices must all be filled before adding new
                    // nodes to the right).
                    int newShift = NODE_LENGTH_POW_2;

                    // If we've got space in our array, we just have to add skinny-branch nodes up
                    // to the level below ours.  But if we don't have space, we have to add a
                    // single-element strict node at the same level as ours here too.
                    int maxShift = (nodes.length < STRICT_NODE_LENGTH) ? shift : shift + 1;

                    // Make the skinny-branch of single-element strict nodes:
                    while (newShift < maxShift) {
//                    System.out.println("  Adding a skinny branch node...");
                        Node<T>[] newNodes = (Node<T>[]) Array.newInstance(newNode.getClass(), 1);
                        newNodes[0] = newNode;
                        newNode = new Strict<>(newShift, newNodes);
                        newShift += NODE_LENGTH_POW_2;
                    }

                    if ((nodes.length < STRICT_NODE_LENGTH)) {
//                    System.out.println("  Adding a node to the existing array");
                        Node<T>[] newNodes =
                                (Node<T>[]) insertIntoArrayAt(newNode, nodes, subNodeIndex,
                                                              Node.class);
                        // This could allow cheap strict inserts on any leaf-node boundary...
                        return new Strict<>(shift, newNodes);
                    } else {
//                    System.out.println("  Adding a level to the Strict tree");
                        return new Strict(shift + NODE_LENGTH_POW_2,
                                             new Node[]{this, newNode});
                    }
                } else if ( (shift == NODE_LENGTH_POW_2) &&
                            (lowBits(index) == 0) &&
                            (nodes.length < STRICT_NODE_LENGTH) ) {
                    // Here we are:
                    //    Pushing a STRICT_NODE_LENGTH focus
                    //    At the level above the leaf nodes
                    //    Inserting *between* existing leaf nodes (or before or after)
                    //    Have room for at least one more leaf child
                    // That makes it free and legal to insert a new STRICT_NODE_LENGTH leaf node and
                    // still yield a Strict (as opposed to Relaxed).

                    // Regardless of what else happens, we're going to add a new node.
                    Node<T> newNode = new Leaf<>(oldFocus);

                    Node<T>[] newNodes = (Node<T>[]) insertIntoArrayAt(newNode, nodes, subNodeIndex,
                                                                       Node.class);
                    // This allows cheap strict inserts on any leaf-node boundary...
                    return new Strict<>(shift, newNodes);
                }
            } // end if oldFocus.length == STRICT_NODE_LENGTH

            // Here we're going to yield a Relaxed Radix node, so punt to that (slower) logic.
//            System.out.println("Yield a Relaxed node.");
            int[] cumulativeSizes = new int[nodes.length];
            int cumulativeSize = 0;
            for (int i = 0; i < cumulativeSizes.length; i++) {
                cumulativeSize = cumulativeSize + nodes[i].size();
                cumulativeSizes[i] = cumulativeSize;
            }
//            System.out.println("End indices: " + Arrays.toString(cumulativeSizes));
            return new Relaxed<>(cumulativeSizes, nodes).pushFocus(index, oldFocus);
        }

        @SuppressWarnings("unchecked")
        @Override
        public Node<T> replace(int idx, T t) {
//            System.out.println("  Strict.get(" + i + ")");
            // Find the node indexed by the high bits (for this height).
            // Send the low bits on to our sub-nodes.
            int thisNodeIdx = highBits(idx);
            Node<T> newNode = nodes[thisNodeIdx].replace(lowBits(idx), t);
            return new Strict<>(shift, replaceInArrayAt(newNode, nodes, thisNodeIdx, Node.class));
        }

        @Override public String toString() {
//            return "Strict(nodes.length="+ nodes.length + ", shift=" + shift + ")";
            return "Strict" + shift + Arrays.toString(nodes);
        }

        @Override public String indentedStr(int indent) {
            StringBuilder sB = new StringBuilder() // indentSpace(indent)
                    .append("Strict").append(shift).append("(");
            return showSubNodes(sB, nodes, indent + sB.length())
                    .append(")")
                    .toString();
        }
    }

    // Contains a relaxed tree of nodes that average around 32 items each.
    private static class Relaxed<T> implements Node<T> {
        @SuppressWarnings("unchecked")
        static <T> Relaxed<T> replaceInRelaxedAt(int[] is, Node<T>[] ns, Node<T> newNode, int subNodeIndex,
                                                 int insertSize) {
            Node<T>[] newNodes = replaceInArrayAt(newNode, ns, subNodeIndex, Node.class);
            // Increment newCumSizes for the changed item and all items to the right.
            int[] newCumSizes = new int[is.length];
            if (subNodeIndex > 0) {
                System.arraycopy(is, 0, newCumSizes, 0, subNodeIndex);
            }
            for (int i = subNodeIndex; i < is.length; i++) {
                newCumSizes[i] = is[i] + insertSize;
            }
            return new Relaxed<>(newCumSizes, newNodes);
        }

        @SuppressWarnings("unchecked")
        public static <T> Node<T> fixRight(Node<T>[] origNodes, Node<T> splitRight, int subNodeIndex) {
            Node<T> right;
            if (subNodeIndex == (origNodes.length - 1)) {
//                debug("If we have a single right node, it doesn't need a parent.");
                right = splitRight;
            } else {
//                debug("splitRight.size()=" + splitRight.size());
                boolean haveRightSubNode = splitRight.size() > 0;
//                debug("haveRightSubNode=" + haveRightSubNode);
                // If we have a rightSubNode, it's going to need a space in our new node array.
                int numRightNodes = (origNodes.length - subNodeIndex) - (haveRightSubNode ? 0 : 1); //(splitRight.size() > 0 ? 2 : 1); // -2 when splitRight.size() > 0
//                debug("numRightNodes=" + numRightNodes);
                if ( !haveRightSubNode && (numRightNodes == 1)) {
//                    debug("If the right node became a focus and there are no other lefts, no parent needed.");
                    right = origNodes[origNodes.length - 1];
                } else {
                    // Here the first (leftmost) node of the right-hand side was turned into the focus
                    // and we have additional right-hand origNodes to adjust the parent for.
                    int[] rightCumSizes = new int[numRightNodes];
                    Node<T>[] rightNodes = (Node<T>[]) new Node[numRightNodes];

//                    System.out.println("origNodes=" + Arrays.toString(origNodes));
//                    System.out.println("subNodeIndex=" + subNodeIndex);

                    int cumulativeSize = 0;
                    int destCopyStartIdx = 0;

                    if (haveRightSubNode) {
                        //                 src,       srcPos,          dest, destPos, length
                        System.arraycopy(origNodes, subNodeIndex + 1, rightNodes, 1, numRightNodes - 1);

                        rightNodes[0] = splitRight;
                        cumulativeSize = splitRight.size();
                        rightCumSizes[0] = cumulativeSize;
                        destCopyStartIdx = 1;
                    } else {
                        //                 src,       srcPos,          dest, destPos, length
                        System.arraycopy(origNodes, subNodeIndex + 1, rightNodes, 0, numRightNodes);
                    }

//                    System.out.println("rightNodes=" + Arrays.toString(rightNodes));

                    // For relaxed nodes, we could calculate from previous cumulativeSizes instead of calling .size()
                    // on each one.  For strict, we could just add a strict amount.  For now, this works.
                    for (int i = destCopyStartIdx; i < numRightNodes; i++) {
                        cumulativeSize += rightNodes[i].size();
                        rightCumSizes[i] = cumulativeSize;
                    }

                    right = new Relaxed<>(rightCumSizes, rightNodes);
                }
            }
            return right;
        }

        // Holds the size of each sub-node and plus all nodes to its left.  You could think of this as maxIndex + 1.
        // This is a separate array so it can be retrieved in a single memory fetch.  Note that this is a 1-based count,
        // not a zero-based index.
        final int[] cumulativeSizes;
        // The sub nodes
        final Node<T>[] nodes;

        // Constructor
        Relaxed(int[] szs, Node<T>[] ns) {
            cumulativeSizes = szs;
            nodes = ns;

            // Consider removing constraint validations before shipping for performance
            if (cumulativeSizes.length < 1) {
                throw new IllegalArgumentException("cumulativeSizes.length < 1");
            }
            if (nodes.length < 1) {
                throw new IllegalArgumentException("nodes.length < 1");
            }
            if (cumulativeSizes.length != nodes.length) {
                throw new IllegalArgumentException("cumulativeSizes.length:" + cumulativeSizes.length +
                                                   " != nodes.length:" + nodes.length);
            }

            int cumulativeSize = 0;
            for (int i = 0; i < nodes.length; i++) {
                cumulativeSize += nodes[i].size();
                if (cumulativeSize != cumulativeSizes[i]) {
                    throw new IllegalArgumentException("nodes[" + i + "].size() was " +
                                                       nodes[i].size() +
                                                       " which is not compatable with cumulativeSizes[" +
                                                       i + "] which was " + cumulativeSizes[i] +
                                                       "\n\tcumulativeSizes=" + Arrays.toString(cumulativeSizes) +
                                                       "\n\tnodes=" + Arrays.toString(nodes));
                }
            }
        }

        @Override public int size() {
            return cumulativeSizes[cumulativeSizes.length - 1];
        }

        /**
         Converts the index of an item into the index of the sub-node containing that item.
         @param treeIndex The index of the item in the tree
         @return The index of the immediate child of this node that the desired node resides in.
         */
        private int subNodeIndex(int treeIndex) {
            // For radix=4 this is actually faster, or at least as fast...
//            for (int i = 0; i < cumulativeSizes.length; i++) {
//                if (treeIndex < cumulativeSizes[i]) {
//                    return i;
//                }
//            }
//            if (treeIndex == size()) {
//                return cumulativeSizes.length - 1;
//            }

            // treeSize = cumulativeSizes[cumulativeSizes.length - 1]
            // range of sub-node indices: 0 to cumulativeSizes.length - 1
            // range of tree indices: 0 to treeSize
            // liner interpolation:
            //     treeIndex / treeSize ~= subNodeIndex / cumulativeSizes.length
            // solve for endIdxSlot
            //     cumulativeSizes.length * (treeIndex / treeSize) =  subNodeIndex
            // Put division last
            //     subNodeIndex = cumulativeSizes.length * treeIndex / treeSize
            //
            // Now guess the sub-node index (quickly).


//            System.out.println("treeIndex=" + treeIndex);
//            System.out.println(" cumulativeSizes=" + Arrays.toString(cumulativeSizes));
            int guess = (cumulativeSizes.length * treeIndex) / size();
//            System.out.println(" guess=" + guess);
            if (guess >= cumulativeSizes.length) {
//                System.out.println("Guessed beyond end length - returning last item.");
                return cumulativeSizes.length - 1;
            }
            int guessedCumSize = cumulativeSizes[guess];
//            System.out.println(" guessedCumSize=" + guessedCumSize);

            // Now we must check the guess.  The cumulativeSizes we store are slightly misnamed because
            // the max valid treeIndex for a node is its size - 1.  If our guessedCumSize is
            //  - less than the treeIndex
            //         Increment guess and check result again until greater, then return
            //         that guess
            //  - greater than (treeIndex + MIN_NODE_SIZE)
            //         Decrement guess and check result again until less, then return PREVIOUS guess
            //  - equal to the treeIndex (see note about size)
            //         If treeIndex == size Return guess
            //         Else return guess + 1

            // guessedCumSize less than the treeIndex
            //         Increment guess and check result again until greater, then return
            //         that guess
            if (guessedCumSize < treeIndex) {
                while (guess < (cumulativeSizes.length - 1)) {
//                    System.out.println("    Too low.  Check higher...");
                    guessedCumSize = cumulativeSizes[++guess];
                    if (guessedCumSize >= treeIndex) {
//                        System.out.println("    RIGHT!");
                        // See note in equal case below...
                        return (guessedCumSize == treeIndex) ? guess + 1
                                                             : guess;
                    }
//                    System.out.println("    ==== Guess higher...");
                }
                throw new IllegalStateException("Can we get here?  If so, how?");
            } else if (guessedCumSize > (treeIndex + MIN_NODE_LENGTH)) {

                // guessedCumSize greater than (treeIndex + MIN_NODE_LENGTH)
                //         Decrement guess and check result again until less, then return PREVIOUS guess
                while (guess > 0) {
//                    System.out.println("    Maybe too high.  Check lower...");
                    int nextGuess = guess - 1;
                    guessedCumSize = cumulativeSizes[nextGuess];

                    if (guessedCumSize <= treeIndex) {
//                        System.out.println("    RIGHT!");
                        return guess;
                    }
//                    System.out.println("    ======= Guess lower...");
                    guess = nextGuess;
                }
//                System.out.println("    Returning lower: " + guess);
                return guess;
            } else if (guessedCumSize == treeIndex) {
                // guessedCumSize equal to the treeIndex (see note about size)
                //         If treeIndex == size Return guess
                //         Else return guess + 1
//                System.out.println("    Equal, so should be simple...");
                // For an append just one element beyond the end of the existing data structure,
                // just try to add it to the last node.  This might seem overly permissive to accept
                // these as inserts or appends without differentiating between the two, but it flows
                // naturally with this data structure and I think makes it easier to use without
                // encouraging user programming errors.
                // Hopefully this still leads to a relatively balanced tree...
                return (treeIndex == size()) ? guess : guess + 1;
            } else {
//                System.out.println("    First guess: " + guess);
                return guess;
            }
        }

        /**
         Converts the index of an item into the index to pass to the sub-node containing that item.
         @param index The index of the item in the entire tree
         @param subNodeIndex the index into this node's array of sub-nodes.
         @return The index to pass to the sub-branch the item resides in
         */
        // TODO: Better name: nextLevelIndex?  subNodeSubIndex?
        private int subNodeAdjustedIndex(int index, int subNodeIndex) {
            return (subNodeIndex == 0) ? index
                                       : index - cumulativeSizes[subNodeIndex - 1];
        }

        @Override public T get(int index) {
//            System.out.println("        Relaxed.get(" + index + ")");
            int subNodeIndex = subNodeIndex(index);
//            System.out.println("        subNodeIndex: " + subNodeIndex);
//            System.out.println("        subNodeAdjustedIndex(index, subNodeIndex): " +
//                               subNodeAdjustedIndex(index, subNodeIndex));

            return nodes[subNodeIndex].get(subNodeAdjustedIndex(index, subNodeIndex));
        }

        private boolean thisNodeHasCapacity() {
//            System.out.println("thisNodeHasCapacity(): nodes.length=" + nodes.length +
//                               " MAX_NODE_LENGTH=" + MAX_NODE_LENGTH +
//                               " MIN_NODE_LENGTH=" + MIN_NODE_LENGTH +
//                               " STRICT_NODE_LENGTH=" + STRICT_NODE_LENGTH);
            return nodes.length < MAX_NODE_LENGTH;
        }

        // I don't think this should ever be called.  Should this throw an exception instead?
        @Override public boolean hasStrictCapacity() {
            throw new UnsupportedOperationException("I don't think this should ever be called.");
//            return false;
        }

        @Override public boolean hasRelaxedCapacity(int index, int size) {
// I think we can add any number of items (less than MAX_NODE_LENGTH)
//            if ( (size < MIN_NODE_LENGTH) || (size > MAX_NODE_LENGTH) ) {
            if ( (size < 1) || (size > MAX_NODE_LENGTH) ) {
                throw new IllegalArgumentException("Bad size: " + size);
            }
            if (thisNodeHasCapacity()) { return true; }
            int subNodeIndex = subNodeIndex(index);
            return nodes[subNodeIndex].hasRelaxedCapacity(subNodeAdjustedIndex(index, subNodeIndex),
                                                          size);
        }

        @SuppressWarnings("unchecked")
        Relaxed<T>[] split() {
//            System.out.println("Relaxed.split(" + i + ")");
            int midpoint = nodes.length >> 1; // Shift-right one is the same as dividing by 2.
            Relaxed<T> left = new Relaxed<>(Arrays.copyOf(cumulativeSizes, midpoint),
                                                    Arrays.copyOf(nodes, midpoint));
            int[] rightCumSizes = new int[nodes.length - midpoint];
            int leftCumSizes = cumulativeSizes[midpoint - 1];
            for (int j = 0; j < rightCumSizes.length; j++) {
                rightCumSizes[j] = cumulativeSizes[midpoint + j] - leftCumSizes;
            }
            // I checked this at javaRepl and indeed this starts from the correct item.
            Relaxed<T> right = new Relaxed<>(rightCumSizes,
                                             Arrays.copyOfRange(nodes, midpoint, nodes.length));
            return new Relaxed[] {left, right};
        }

        @SuppressWarnings("unchecked")
        @Override
        public SplitNode<T> splitAt(int splitIndex) {
            int size = size();
            if ( (splitIndex < 0) || (splitIndex > size) ) {
                throw new IllegalArgumentException("Bad splitIndex: " + splitIndex);
            }
            if (splitIndex == 0) {
                return new SplitNode<>(emptyLeaf(), emptyArray(), emptyLeaf(), emptyArray());
            }
            if (splitIndex == size) {
                return new SplitNode<>(this, emptyArray(), emptyLeaf(), emptyArray());
            }

//            System.out.println("==========================");
//            System.out.println("before=" + this.indentedStr(7));

            int subNodeIndex = subNodeIndex(splitIndex);
            Node<T> subNode = nodes[subNodeIndex];
            int subNodeAdjustedIndex = subNodeAdjustedIndex(splitIndex, subNodeIndex);

            SplitNode<T> split = subNode.splitAt(subNodeAdjustedIndex);

//            debug("--------------------------");
//            debug("before=", this);
//            debug("splitIndex=" + splitIndex);
//            debug("nodes.length=" + nodes.length);
//            debug("subNodeIndex=" + subNodeIndex);
////            debug("subNode=", subNode);
//            debug("split=", split);

            final Node<T> left;
            final Node<T> splitLeft = split.left();
            if (subNodeIndex == 0) {
//                debug("If we have a single left node, it doesn't need a parent.");
                left = splitLeft;
            } else {
                boolean haveLeft = (splitLeft.size() > 0);
                int numLeftItems = subNodeIndex + (haveLeft ? 1 : 0);
                if ( !haveLeft && (numLeftItems == 1) ) {
//                    debug("If the left node became a focus and there are no other lefts, no parent needed.");
                    left = nodes[0];
                } else {
                    int[] leftCumSizes = new int[numLeftItems];
                    Node<T>[] leftNodes = (Node<T>[]) new Node[numLeftItems];
                    //                      src, srcPos,    dest,destPos, length
                    System.arraycopy(cumulativeSizes, 0, leftCumSizes, 0, numLeftItems);
                    if (haveLeft) {
                        int cumulativeSize = (numLeftItems > 1) ? leftCumSizes[numLeftItems - 2] : 0;
                        leftCumSizes[numLeftItems - 1] = cumulativeSize + splitLeft.size();
                    }
//                    debug("leftCumSizes=" + Arrays.toString(leftCumSizes));
                    // Copy one less item if we are going to add the split one in a moment.
                    // I could have written:
                    //     haveLeft ? numLeftItems - 1
                    //              : numLeftItems
                    // but that's always equal to subNodeIndex.
                    System.arraycopy(nodes, 0, leftNodes, 0, subNodeIndex);
                    if (haveLeft) {
                        leftNodes[numLeftItems - 1] = splitLeft;
                    }
                    left = new Relaxed<>(leftCumSizes, leftNodes);
                }
            }

            final Node<T> right = fixRight(nodes, split.right(), subNodeIndex);

            SplitNode<T> ret = new SplitNode<>(left, split.leftFocus(),
                                               right, split.rightFocus());
//            debug("RETURNING=", ret);
            if (this.size() != ret.size()) {
                throw new IllegalStateException("Split on " + this.size() + " items returned " + ret.size() + " items");
            }

            return ret;
        }

        @SuppressWarnings("unchecked")
        @Override public Node<T> pushFocus(int index, T[] oldFocus) {
            // TODO: Review this entire method.
//            System.out.println("===========\n" +
//                               "Relaxed pushFocus(index=" + index + ", oldFocus=" +
//                               Arrays.toString(oldFocus) + ")");
//            System.out.println("  this: " + this);

            int subNodeIndex = subNodeIndex(index);
            Node<T> subNode = nodes[subNodeIndex];
            int subNodeAdjustedIndex = subNodeAdjustedIndex(index, subNodeIndex);

            // 1st choice: insert into the subNode if it has enought space enough to handle it
            if (subNode.hasRelaxedCapacity(subNodeAdjustedIndex, oldFocus.length)) {
//                System.out.println("  Pushing the focus down to a lower-level node with capacity.");
                Node<T> newNode = subNode.pushFocus(subNodeAdjustedIndex, oldFocus);
                // Make a copy of our nodesArray, replacing the old node at subNodeIndex with the
                // new node
                return replaceInRelaxedAt(cumulativeSizes, nodes, newNode, subNodeIndex, oldFocus.length);
            }

            // I think this is a root node thing.
            if (!thisNodeHasCapacity()) {
                // For now, split at half of size.
                Relaxed<T>[] split = split();

//                Relaxed<T> node1 = split[0];
//                Relaxed<T> node2 = split[1];

//                System.out.println("Split node1: " + node1);
//                System.out.println("Split node2: " + node2);
                int max1 = split[0].size();
                Relaxed<T> newRelaxed =
                        new Relaxed<>(new int[] {max1,
                                                 max1 + split[1].size()},
                                      split);
//                System.out.println("newRelaxed3: " + newRelaxed);
                return newRelaxed.pushFocus(index, oldFocus);
            }

            if (subNode instanceof Leaf) {
                // Here we already know:
                //  - the leaf doesn't have capacity
                //  - We don't need to split ourselves
                // Therefore:
                //  - If the focus is big enough to be its own leaf and the index is on a leaf
                //    boundary and , make it one.
                //  - Else, insert into the array and replace one leaf with two.

//                System.out.println("Leaf!");

                final Node<T>[] newNodes;
                final int[] newCumSizes;
                final int numToSkip;

                //  If the focus is big enough to be its own leaf and the index is on a leaf
                // boundary, make it one.
                if ( (oldFocus.length >= MIN_NODE_LENGTH) &&
                     (subNodeAdjustedIndex == 0 || subNodeAdjustedIndex == subNode.size()) ) {

//                    System.out.println("Insert-between");
                    // Just add a new leaf
                    Leaf<T> newNode = new Leaf<>(oldFocus);

                    // If we aren't inserting before the existing leaf node, we're inserting after.
                    if (subNodeAdjustedIndex != 0) {
                        subNodeIndex++;
                    }

                    newNodes = insertIntoArrayAt(newNode, nodes, subNodeIndex, Node.class);
                    // Increment newCumSizes for the changed item and all items to the right.
                    newCumSizes = new int[cumulativeSizes.length + 1];
                    int cumulativeSize = 0;
                    if (subNodeIndex > 0) {
                        System.arraycopy(cumulativeSizes, 0, newCumSizes, 0, subNodeIndex);
                        cumulativeSize = newCumSizes[subNodeIndex - 1];
                    }
                    newCumSizes[subNodeIndex] = cumulativeSize + oldFocus.length;
                    numToSkip = 1;
//                    for (int i = subNodeIndex + 1; i < newCumSizes.length; i++) {
//                        newCumSizes[i] = cumulativeSizes[i - 1] + oldFocus.length;
//                    }
                } else {
                    // Grab the array from the existing leaf node, make the insert, and yield two
                    // new leaf nodes.
//                    System.out.println("Split-to-insert");
                    Leaf<T>[] res =
                            ((Leaf<T>) subNode).spliceAndSplit(oldFocus, subNodeAdjustedIndex);
                    Leaf<T> leftLeaf = res[0];
                    Leaf<T> rightLeaf = res[1];

                    newNodes = new Node[nodes.length + 1];

//                    System.out.println("old cumulativeSizes=" + Arrays.toString(cumulativeSizes));

                    // Increment newCumSizes for the changed item and all items to the right.
                    newCumSizes = new int[cumulativeSizes.length + 1];
                    int leftSize = 0;
//                    System.out.println("subNodeIndex=" + subNodeIndex);

                    // Copy nodes and cumulativeSizes before split
                    if (subNodeIndex > 0) {
                        //               src,srcPos,dest,destPos,length
                        System.arraycopy(nodes, 0, newNodes, 0, subNodeIndex);
                        //               src,   srcPos, dest,    destPos, length
                        System.arraycopy(cumulativeSizes, 0, newCumSizes, 0, subNodeIndex);
//                        System.out.println("start of newCumSizes=" + Arrays.toString(newCumSizes));

                        leftSize = cumulativeSizes[subNodeIndex - 1];
//                        System.out.println("cumulativeSize=" + cumulativeSize);
                    }

                    // Copy split nodes and cumulativeSizes
                    newNodes[subNodeIndex] = leftLeaf;
                    newNodes[subNodeIndex + 1] = rightLeaf;
                    leftSize += leftLeaf.size();
                    newCumSizes[subNodeIndex] = leftSize;
                    newCumSizes[subNodeIndex + 1] = leftSize + rightLeaf.size();

//                    System.out.println("continued newNodes=" + Arrays.toString(newNodes));
//                    System.out.println("continued cumulativeSizes=" + Arrays.toString(newCumSizes));


                    if (subNodeIndex < (nodes.length - 1)) {
                        //               src,srcPos,dest,destPos,length
                        System.arraycopy(nodes, subNodeIndex + 1, newNodes, subNodeIndex + 2,
                                         nodes.length - subNodeIndex - 1);
//                        System.out.println("completed newNodes=" + Arrays.toString(newNodes));
                    }
                    numToSkip = 2;
                }
                for (int i = subNodeIndex + numToSkip; i < newCumSizes.length; i++) {
//                    System.out.println("i=" + i);
//                    System.out.println("numToSkip=" + numToSkip);
//                    System.out.println("oldFocus.length=" + oldFocus.length);
//                    System.out.println("cumulativeSizes[i - 1]=" + cumulativeSizes[i - 1]);
                    newCumSizes[i] = cumulativeSizes[i - 1] + oldFocus.length;
//                    System.out.println("newCumSizes so far=" + Arrays.toString(newCumSizes));
                }

//                System.out.println("newNodes=" + Arrays.toString(newNodes));
//                System.out.println("newCumSizes=" + Arrays.toString(newCumSizes));
                return new Relaxed<>(newCumSizes, newNodes);
                // end if subNode instanceof Leaf
            } else if (subNode instanceof Strict) {
//                System.out.println("Converting Strict to Relaxed...");
//                System.out.println("Before: " + subNode.indentedStr(8));
                Relaxed<T> relaxed = ((Strict) subNode).relax();
//                System.out.println("After: " + relaxed.indentedStr(7));
//                System.out.println();
                Node<T> newNode = relaxed.pushFocus(subNodeAdjustedIndex, oldFocus);
                return replaceInRelaxedAt(cumulativeSizes, nodes, newNode, subNodeIndex, oldFocus.length);
            }

            // Here we have capacity and the full sub-node is not a leaf or strict, so we have to split the appropriate
            // sub-node.

            // For now, split at half of size.
//            System.out.println("Splitting from:\n" + this.indentedStr(0));
//            System.out.println("About to split:\n" + subNode.indentedStr(0));
//            System.out.println("Split at: " + (subNode.size() >> 1));
//            System.out.println("To insert: " + Arrays.toString(oldFocus));

            Relaxed<T>[] newSubNode = ((Relaxed<T>) subNode).split();

            Relaxed<T> node1 = newSubNode[0];
            Relaxed<T> node2 = newSubNode[1];

//            System.out.println("Split node1: " + node1);
//            System.out.println("Split node2: " + node2);

            Node<T>[] newNodes = (Node<T>[]) new Node[nodes.length + 1];

            // If we aren't inserting at the first item, array-copy the nodes before the insert
            // point.
            if (subNodeIndex > 0) {
                System.arraycopy(nodes, 0, newNodes, 0, subNodeIndex);
            }

            // Insert the new item.
            newNodes[subNodeIndex] = node1;
            newNodes[subNodeIndex + 1] = node2;

            // If we aren't inserting at the last item, array-copy the nodes after the insert
            // point.
            if (subNodeIndex < nodes.length) {
                System.arraycopy(nodes, subNodeIndex + 1, newNodes, subNodeIndex + 2,
                                 nodes.length - subNodeIndex - 1);
            }

            int[] newCumSizes = new int[cumulativeSizes.length + 1];
            int cumulativeSize = 0;
            if (subNodeIndex > 0) {
                System.arraycopy(cumulativeSizes, 0, newCumSizes, 0, subNodeIndex);
                cumulativeSize = cumulativeSizes[subNodeIndex - 1];
            }

            for (int i = subNodeIndex; i < newCumSizes.length; i++) {
                // TODO: Calculate instead of loading into memory.  See splitAt calculation above.
                cumulativeSize += newNodes[i].size();
                newCumSizes[i] = cumulativeSize;
            }

            Relaxed<T> newRelaxed = new Relaxed<>(newCumSizes, newNodes);
//            debug("newRelaxed2:\n" + newRelaxed.indentedStr(0));

            return newRelaxed.pushFocus(index, oldFocus);
//            debug("Parent after:" + after.indentedStr(0));
        }

        @SuppressWarnings("unchecked")
        @Override public Node<T> replace(int index, T t) {
            int subNodeIndex = subNodeIndex(index);
            Node<T> alteredNode =
                    nodes[subNodeIndex].replace(subNodeAdjustedIndex(index, subNodeIndex), t);
            Node<T>[] newNodes = replaceInArrayAt(alteredNode, nodes, subNodeIndex, Node.class);
            return new Relaxed<>(cumulativeSizes, newNodes);
        }

        @Override public String toString() {
            return "Relaxed(cumulativeSizes=" + Arrays.toString(cumulativeSizes) +
                   " nodes=" + Arrays.toString(nodes)
                                     .replaceAll(", Relaxed\\(", ",\n           Relaxed(") + ")";
        }

        @Override public String indentedStr(int indent) {
            StringBuilder sB = new StringBuilder() // indentSpace(indent)
                    .append("Relaxed(");
            int nextIndent = indent + sB.length();
            sB.append("cumulativeSizes=").append(Arrays.toString(cumulativeSizes)).append("\n")
              .append(indentSpace(nextIndent)).append("nodes=[");
            // + 6 for "nodes="
            return showSubNodes(sB, nodes, nextIndent + 7)
                    .append("])")
                    .toString();
        }
    } // end class Relaxed
} // end class RrbTree
