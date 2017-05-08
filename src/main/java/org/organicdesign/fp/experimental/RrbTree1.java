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

import org.organicdesign.fp.collections.ImList;
import org.organicdesign.fp.collections.MutableList;
import org.organicdesign.fp.collections.UnmodIterable;
import org.organicdesign.fp.collections.UnmodIterator;
import org.organicdesign.fp.collections.UnmodSortedIterable;
import org.organicdesign.fp.collections.UnmodSortedIterator;
import org.organicdesign.fp.tuple.Tuple2;
import org.organicdesign.fp.tuple.Tuple4;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

interface Indented { String indentedStr(int indent); }

/**
 This is based on the paper, "RRB-Trees: Efficient Immutable Vectors" by Phil Bagwell and
 Tiark Rompf, with the following differences:

 - The Relaxed nodes can be sized between n/3 and 2n/3 (Bagwell/Rompf specify n and n-1)
 - The Join operation sticks the shorter tree unaltered into the larger tree (except for very
   small trees which just get concatenated).

 Details were filled in from the Cormen, Leiserson, Rivest & Stein Algorithms book entry
 on B-Trees.  Also with an awareness of the Clojure PersistentVector by Rich Hickey.  All errors
 are by Glen Peterson.

 Still TO-DO:
  - More Testing
  - Change radix from 4 to 32.
  - Consider mutable vector
  - Speed testing and optimization.

 History (what little I know):
 1972: B-Tree: Rudolf Bayer and Ed McCreight
 1998: Purely Functional Data Structures: Chris Okasaki
 2007: Clojure's Persistent Vector (and HashMap) implementations: Rich Hickey
 2012: RRB-Tree: Phil Bagwell and Tiark Rompf

 */
@SuppressWarnings("WeakerAccess")
public class RrbTree1<E> implements ImList<E>, Indented {

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

    // Factory
    /**
     This is the public factory method.
     @return the empty RRB-Tree (there is only one)
     */
    @SuppressWarnings("unchecked")
    public static <T> RrbTree1<T> empty() { return (RrbTree1<T>) EMPTY_RRB_TREE; }

    // ===================================== Instance Methods =====================================

    /**
     Adds an item at the end of this structure.  This is the most efficient way to build an
     RRB Tree as it conforms to the Clojure PersistentVector and all of its optimizations.
     @param t the item to append
     @return a new RRB-Tree with the item appended.
     */
    @Override public RrbTree1<E> append(E t) {
//        System.out.println("=== append(" + t + ") ===");
        // If our focus isn't set up for appends or if it's full, insert it into the data structure
        // where it belongs.  Then make a new focus
        if (((focusStartIndex < root.size()) && (focus.length > 0) ) ||
            (focus.length >= STRICT_NODE_LENGTH) ) {
            Node<E> newRoot = root.pushFocus(focusStartIndex, focus);
            E[] newFocus = singleElementArray(t);
            return new RrbTree1<>(newFocus, size, newRoot, size + 1);
        }

        E[] newFocus = insertIntoArrayAt(t, focus, focus.length, null);
        return new RrbTree1<>(newFocus, focusStartIndex, root, size + 1);
    }

    // TODO: This is inefficient due to no mutable version (was 5x difference for PersistentVector)
    // TODO: Allow this to use default impl in ImList once we have a mutable version.
    @Override public RrbTree1<E> concat(Iterable<? extends E> es) {
        RrbTree1<E> ret = this;
        for (E e : es) {
            ret = ret.append(e);
        }
        return ret;
    }

    @Override public E get(int i) {
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

    @Override public RrbTree1<E> immutable() { return this; }

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
            E[] newFocus = insertIntoArrayAt(element, focus, diff, null);
            return new RrbTree1<>(newFocus, focusStartIndex, root, size + 1);
        }

//        System.out.println("insert somewhere other than the current focus.");
//        System.out.println("focusStartIndex: " + focusStartIndex);
//        System.out.println("focus: " + arrayString(focus));
        // Here we are left with an insert somewhere else than the current focus.
        Node<E> newRoot = focus.length > 0 ? root.pushFocus(focusStartIndex, focus)
                                           : root;
        E[] newFocus = singleElementArray(element);
        return new RrbTree1<>(newFocus, idx, newRoot, size + 1);
    }

    @Override public UnmodSortedIterator<E> iterator() {
        return new Iter();
    }

/*
I'm implementing something like the [Bagwell/Rompf RRB-Tree][1] and I'm a little unsatisfied with
the details of the join/merge algorithm.  I wonder if there's a standard way to do this that they
assume that I know (I don't), or if someone has come up with a better way to do this.

I'm thinking the signature is something like:

    public RrbTree<E> join(RrbTree<? extends E> that)

My basic approach was to fit the shorter tree into the left-most or right-most leg of the taller
tree at the correct height.  For `a.join(b)`, if `b` is taller, fit `a` into `b`'s left-most leg at
the right height, otherwise fit `b` into `a`'s right-most leg at the right height

Overview:

1. Push the focus of both trees so we don't have to worry about it.

2. Find the height of each tree.

3. Stick the shorter tree into the proper level of the larger tree (on the left or right as
appropriate).  If the leftmost/rightmost node at the proper level level of the larger tree is full,
add a "skinny leg" (a new root with a single child) to the short tree and stick it on the left or
right one level up in the large one.  If the large tree is packed, several skinny-leg insertion
attempts may be required, or even a new root added to the large tree with 2 children: the old
large tree and the smaller tree on the appropriate side of it.

Optimization: If one tree is really small, we could do an append or prepend.

I don't see the advantage of zipping nodes together at every level the very complicated
way it seems to do in the paper.  Even after all that work, it still has nodes of varying sizes and
involves changing more nodes than maybe necessary.

  [1]: https://infoscience.epfl.ch/record/169879/files/RMTrees.pdf
*/

    private Node<E> pushFocus() {
        if (focus.length == 0) {
            return root;
        }
        return root.pushFocus(focusStartIndex, focus);
    }

    private static <E> Node<E> eliminateUnnecessaryAncestors(Node<E> n) {
        while ( !(n instanceof Leaf) &&
                (n.numChildren() == 1) ) {
            n = n.child(0);
        }
        return n;
    }

    /**
     Joins the given tree to the right side of this tree (or this to the left side of that one) in
     something like O(log n) time.
     */
    public RrbTree1<E> join(RrbTree1<E> that) {

        // We don't want to wonder below if we're inserting leaves or branch-nodes.
        // Also, it leaves the tree cleaner to just smash leaves onto the bigger tree.
        // Ultimately, we might want to see if we can grab the tail and stick it where it belongs
        // but for now, this should be alright.
        if (that.size < MAX_NODE_LENGTH) {
            return concat(that);
        }
        if (this.size < MAX_NODE_LENGTH) {
            for (int i = 0; i < size; i++) {
                that = that.insert(i, this.get(i));
            }
            return that;
        }

        // OK, here we've eliminated the case of merging a leaf into a tree.  We only have to
        // deal with tree-into-tree merges below.
        //
        // Note that if the right-hand tree is bigger, we'll effectively add this tree to the
        // left-hand side of that one.  It's logically the same as adding that tree to the right
        // of this, but the mechanism by which it happens is a little different.
        Node<E> leftRoot = eliminateUnnecessaryAncestors(pushFocus());
        Node<E> rightRoot = eliminateUnnecessaryAncestors(that.pushFocus());

        // Whether to add the right tree to the left one (true) or vice-versa (false).
        // True also means left is taller, false: right is taller.
        boolean leftIntoRight = leftRoot.height() < rightRoot.height();
        Node<E> taller = leftIntoRight ? rightRoot : leftRoot;
        Node<E> shorter = leftIntoRight ? leftRoot : rightRoot;

//        taller.pushTree(shorter);

        // Walk down the taller tree to the height of the shorter, remembering ancestors.
        Node<E> n = taller;
        Node<E>[] ancestors = genericNodeArray(taller.height() - shorter.height());
        int i = 0;
        for (; i < ancestors.length; i++) {
            ancestors[i] = n;
            if (n instanceof Leaf) {
//                System.out.println("this: " + this.indentedStr(6));
                System.out.println("leaf: " + n.indentedStr(6));
                throw new IllegalStateException("Somehow found a leaf node");
            }
            n = n.endChild(leftIntoRight);
        }
//        System.out.println("ancestors.length: " + ancestors.length + " i: " + i);
        i--;
        // While nodes in the taller are full, add a parent to the shorter and try the next level
        // up.
        while (!n.thisNodeHasRelaxedCapacity(shorter.numChildren()) && (i >= 0)) {
            n = ancestors[i];
            i--;

            //noinspection unchecked
            shorter = new Relaxed<>(new int[] { shorter.size() },
                                    (Node<E>[]) new Node[] { shorter });

            // Weird, I know, but sometimes we care about which is shorter and sometimes about
            // left and right.  So now we fixed the shorter tree, we have to update the left/right
            // pointer that was pointing to it.
            if (leftIntoRight) {
                leftRoot = shorter;
            } else {
                rightRoot = shorter;
            }
        }

        // Here we've got 2 trees of equal height so we make a new parent.
        if (i < 0) {
            @SuppressWarnings("unchecked")
            Node<E>[] newRootArray = new Node[] {leftRoot, rightRoot};
            int leftSize = leftRoot.size();
            Node<E> newRoot =
                    new Relaxed<>(new int[] {leftSize, leftSize + rightRoot.size()}, newRootArray);

            return new RrbTree1<>(emptyArray(), 0, newRoot, newRoot.size());
        }

        // Trees are not equal height and there's room somewhere.
        n = n.addEndChild(leftIntoRight, shorter);

        while (i >= 0) {
            Node<E> anc = ancestors[i];
            // By definition, I think that if we need a new root node, then we aren't dealing with
            // leaf nodes, but I could be wrong.
            // I also think we should get rid of relaxed nodes and everything will be much easier.
            Relaxed<E> rel = (anc instanceof Strict) ? ((Strict) anc).relax()
                                                     : (Relaxed<E>) anc;

            int repIdx = leftIntoRight ? 0 : rel.numChildren() - 1;
            n = Relaxed.replaceInRelaxedAt(rel.cumulativeSizes, rel.nodes, n, repIdx,
                                           n.size() - rel.nodes[repIdx].size());
            i--;
        }

        return new RrbTree1<>(emptyArray(), 0, n, n.size());
    }

    @Override public MutableList<E> mutable() {
        // TODO: Implement or change interfaces.
        throw new UnsupportedOperationException("No mutable version (yet)");
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
                return new RrbTree1<>(replaceInArrayAt(item, focus, focusOffset, null),
                                      focusStartIndex, root, size);
            }
//            System.out.println("    Subtracting focus.length");
            index -= focus.length;
        }
//        System.out.println("    About to do replace with maybe-adjusted index=" + index);
//        System.out.println("    this=" + this);
        return new RrbTree1<>(focus, focusStartIndex, root.replace(index, item), size);
    }

    public RrbTree1<E> without(int index) {
        if ( (index > 0) && (index < size - 1) ) {
            Tuple2<RrbTree1<E>,RrbTree1<E>> s1 = split(index);
            Tuple2<RrbTree1<E>,RrbTree1<E>> s2 = s1._2().split(1);
//            System.out.println("this: " + this.indentedStr(6));
//            System.out.println("s1-L: " + s1._1().indentedStr(6));
//            System.out.println("s1-R: " + s1._2().indentedStr(6));
//            System.out.println("s2-L: " + s2._1().indentedStr(6));
//            System.out.println("s2-R: " + s2._2().indentedStr(6));

            return s1._1().join(s2._2());
        } else if (index == 0) {
            return split(1)._2();
        } else if (index == size - 1) {
            return split(size - 1)._1();
        } else {
            throw new IllegalArgumentException("out of bounds");
        }
    }

    @Override public int size() { return size; }

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

        // TODO: Do not abbreviate the returned tree at a lower level, or we can get left with a short-leg
        // TODO: Instead, remove single parent nodes until we are left with a leaf, or a node with multiple children.
        SplitNode<E> split = newRoot.splitAt(splitIndex);

        E[] leftFocus = split.leftFocus();
        Node<E> left = split.left();

        E[] rightFocus = split.rightFocus();
        Node<E> right = split.right();

        return Tuple2.of(new RrbTree1<>(leftFocus, left.size(), left, left.size() + leftFocus.length),
                         new RrbTree1<>(rightFocus, 0, right, right.size() + rightFocus.length));
    }

    // ================================== Standard Object Methods ==================================

    @SuppressWarnings("unchecked")
    @Override public boolean equals(Object other) {
        if (this == other) { return true; }
        if ( !(other instanceof List) ) { return false; }
        List<? extends E> that = (List<? extends E>) other;
        return (this.size() == that.size()) &&
               UnmodSortedIterable.equal(this, UnmodSortedIterable.castFromList(that));
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

    @Override public String toString() {
        return UnmodIterable.toString("RrbTree", this);
    }

    @Override public String indentedStr(int indent) {
        return "RrbTree(size=" + size +
               " fsi=" + focusStartIndex +
               " focus=" + arrayString(focus) + "\n" +
               indentSpace(indent + 8) + "root=" + (root == null ? "null" : root.indentedStr(indent + 13)) + ")";
    }

    // ================================== Implementation Details ==================================

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

    // We only one empty array and it makes the code simpler than pointing to null all the time.
    // Have to time the difference between using this and null.  The only difference I can imagine
    // is that this has an address in memory and null does not, so it could save a memory lookup
    // in some places.
    private static final Object[] EMPTY_ARRAY = new Object[0];

    private static final Leaf EMPTY_LEAF = new Leaf<>(EMPTY_ARRAY);
    @SuppressWarnings("unchecked")
    private static <T> Leaf<T> emptyLeaf() { return (Leaf<T>) EMPTY_LEAF; }

    private static final RrbTree1 EMPTY_RRB_TREE =
            new RrbTree1<>(emptyArray(), 0, emptyLeaf(), 0);

    // ================================ Node private inner classes ================================

    private interface Node<T> extends Indented {
        /** Returns the immediate child node at the given index. */
        Node<T> child(int childIdx);

        /** Returns the leftMost (first) or right-most (last) child */
        Node<T> endChild(boolean leftMost);

        /** Adds a node as the first/leftmost or last/rightmost child */
        Node<T> addEndChild(boolean leftMost, Node<T> shorter);

        /** Return the item at the given index */
        T get(int i);

        /** Returns true if this strict-Radix tree can take another 32 items. */
        boolean hasStrictCapacity();

        /** Returns the maximum depth below this node.  Leaf nodes are height 1. */
        int height();

//        /** Try to add all sub-nodes to this one. */
//        Node<T> join(Node<T> that);

        /** Number of items stored in this node */
        int size();
//        /** Returns true if this node's array is not full */
//        boolean thisNodeHasCapacity();

        /** Can this node take the specified number of children? */
        boolean thisNodeHasRelaxedCapacity(int numItems);

        /**
         Can we put focus at the given index without reshuffling nodes?
         @param index the index we want to insert at
         @param size the number of items to insert.  Must be size < MAX_NODE_LENGTH
         @return true if we can do so without otherwise adjusting the tree.
         */
        boolean hasRelaxedCapacity(int index, int size);

        // Splitting a strict node yields an invalid Relaxed node (too short).
        // We don't yet split Leaf nodes.
        // So this needs to only be implemented on Relaxed for now.
//        Relaxed<T>[] split();

        /** Returns the number of immediate children of this node, not all descendants. */
        int numChildren();

        // Because we want to append/insert into the focus as much as possible, we will treat
        // the insert or append of a single item as a degenerate case.  Instead, the primary way
        // to add to the internal data structure will be to push the entire focus array into it
        Node<T> pushFocus(int index, T[] oldFocus);

        Node<T> replace(int idx, T t);

        SplitNode<T> splitAt(int splitIndex);
    }

//    private interface BranchNode<T> extends Node<T> {
//    }

//    /** For calcCumulativeSizes */
//    private static final class CumulativeSizes {
//        int szSoFar; // Size so far (of all things to left)
//        int srcOffset; // offset in source array
//        int[] destArray; // the destination array
//        int destPos; // offset in destArray to copy to
//        int length; // number of items to copy.
//    }

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
                     .append(nextIndentStr).append("leftFocus=").append(arrayString(leftFocus())).append(",\n")
                     .append(nextIndentStr).append("right=").append(right().indentedStr(nextIndent + 6)).append(",\n")
                     .append(nextIndentStr).append("rightFocus=").append(arrayString(rightFocus())).append(")")
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

        @Override public Node<T> child(int childIdx) {
            throw new UnsupportedOperationException("Don't call this on a leaf");
        }

        /** Returns the leftMost (first) or right-most (last) child */
        @Override public Node<T> endChild(boolean leftMost) {
            throw new UnsupportedOperationException("Don't call this on a leaf");
        }

        /** Adds a node as the first/leftmost or last/rightmost child */
        @Override public Node<T> addEndChild(boolean leftMost, Node<T> shorter) {
            throw new UnsupportedOperationException("Don't call this on a leaf");
        }

        @Override public T get(int i) { return items[i]; }

        @Override public int height() { return 1; }

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
//            System.out.println("   Leaf.items=" + arrayString(items));
//            System.out.println("   MAX_NODE_LENGTH=" + MAX_NODE_LENGTH);
            return (items.length + size) < MAX_NODE_LENGTH;
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
            Tuple2<T[],T[]> split = splitArray(items, splitIndex);
            return new SplitNode<>(emptyLeaf(), split._1(), emptyLeaf(), split._2());
        }

        @SuppressWarnings("unchecked")
        private Leaf<T>[] spliceAndSplit(T[] oldFocus, int splitIndex) {
            // Consider optimizing:
            T[] newItems = spliceIntoArrayAt(oldFocus, items, splitIndex,
                                             (Class<T>) items[0].getClass());

//            System.out.println("    newItems: " + arrayString(newItems));
            // Shift right one is divide-by 2.
            Tuple2<T[],T[]> split = splitArray(newItems, newItems.length >> 1);

            return new Leaf[] {new Leaf<>(split._1()), new Leaf<>(split._2())};
        }

        @Override public int numChildren() { return size(); }

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
            return new Leaf<>(replaceInArrayAt(t, items, idx, null));
        }

        @Override public boolean thisNodeHasRelaxedCapacity(int numItems) {
            return items.length + numItems <= MAX_NODE_LENGTH;
        }

        @Override public String toString() {
//            return "Leaf("+ arrayString(items) + ")";
            return arrayString(items);
        }

        @Override public String indentedStr(int indent) {
            return arrayString(items);
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
//            System.out.println("    new Strict" + shift + arrayString(ns));
        }

        @Override public Node<T> child(int childIdx) { return nodes[childIdx]; }

        /** Returns the leftMost (first) or right-most (last) child */
        @Override public Node<T> endChild(boolean leftMost) {
            return nodes[leftMost ? 0 : nodes.length - 1];
        }

        /** Adds a node as the first/leftmost or last/rightmost child */
        @Override public Node<T> addEndChild(boolean leftMost, Node<T> shorter) {
            if (leftMost || !(shorter instanceof Strict)) {
                return relax().addEndChild(leftMost, shorter);
            }
            //noinspection unchecked
            return new Strict<>(shift, insertIntoArrayAt(shorter, nodes, nodes.length, Node.class));
        }

        @Override public int height() { return nodes[0].height() + 1; }

        /**
         Returns the high bits which we use to index into our array.  This is the simplicity (and
         speed) of Strict indexing.  When everything works, this can be inlined for performance.
         This could maybe yield a good guess for Relaxed nodes?

         Shifting right by a number is equivalent to dividing by: 2 raised to the power of that number.
         i >> n is equivalent to i / (2^n)
         */
        private int highBits(int i) { return i >> shift; }

        /**
         Returns the low bits of the index (the part Strict sub-nodes need to know about).  This only works because
         the leaf nodes are all the same size and that size is a power of 2 (the radix).  All branch must have the same
         radix (branching factor or number of immediate sub-nodes).

         Bit shifting is faster than addition or multiplication, but perhaps more importantly, it means we don't have
         to store the sizes of the nodes which means we don't have to fetch those sizes from memory or use up cache
         space.  All of this helps make this data structure simple and fast.

         When everything works, this function can be inlined for performance (if that even helps).
         Contrast this with how Relaxed nodes work: they use subtraction instead!
         */
        private int lowBits(int i) {
            // Little trick: -1 in binary is all ones: 0b11111111111111111111111111111111
            // We shift it left, filling the right-most bits with zeros and creating a bit-mask with ones on the left
            // and zeros on the right
            int shifter = -1 << shift;

            // Now we take the inverse so our bit-mask has zeros on the left and ones on the right
            int invShifter = ~shifter;

            // Finally, we bitwise-and the mask with the index to leave only the low bits.
            return  i & invShifter;
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
                Node<T>[] leftNodes = genericNodeArray(numLeftItems);
                //                    debug("leftCumSizes=" + arrayString(leftCumSizes));
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

        @Override public int numChildren() { return nodes.length; }

        @SuppressWarnings("unchecked")
        @Override
        public Node<T> pushFocus(int index, T[] oldFocus) {
//            System.out.println("Strict pushFocus(" + arrayString(oldFocus) +
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
                        Node<T>[] newNodes = replaceInArrayAt(newNode, nodes, nodes.length - 1, Node.class);
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
                                insertIntoArrayAt(newNode, nodes, subNodeIndex, Node.class);
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

                    Node<T>[] newNodes =
                            insertIntoArrayAt(newNode, nodes, subNodeIndex, Node.class);
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
//            System.out.println("End indices: " + arrayString(cumulativeSizes));
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

        @Override public boolean thisNodeHasRelaxedCapacity(int numNodes) {
            return nodes.length + numNodes <= MAX_NODE_LENGTH;
        }

//        @Override public Node<T>[] children() { return nodes; }
//
//        @Override public Relaxed<T> precatChildren(BranchNode<T> n) {
//            return relax().precatChildren(n);
//        }
//
//        @Override public Relaxed<T> concatChildren(BranchNode<T> n) {
//            return relax().concatChildren(n);
//        }
//
//        @Override public void calcCumulativeSizes(CumulativeSizes cs) {
//            int szSoFar = cs.szSoFar;
//            for (int i = 0; i < cs.length; i++) {
//                szSoFar += nodes[cs.srcOffset + i].size();
//                cs.destArray[cs.destPos + i] = szSoFar;
//            }
//        }

        @Override public String toString() {
//            return "Strict(nodes.length="+ nodes.length + ", shift=" + shift + ")";
            return "Strict" + shift + arrayString(nodes);
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

        // Holds the size of each sub-node and plus all nodes to its left.  You could think of this
        // as maxIndex + 1. This is a separate array so it can be retrieved in a single memory
        // fetch.  Note that this is a 1-based count, not a zero-based index.
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
                                                       "\n\tcumulativeSizes=" + arrayString(cumulativeSizes) +
                                                       "\n\tnodes=" + arrayString(nodes));
                }
            }
        }

        @Override public Node<T> child(int childIdx) { return nodes[childIdx]; }

        /** Returns the leftMost (first) or right-most (last) child */
        @Override public Node<T> endChild(boolean leftMost) {
            return nodes[leftMost ? 0 : nodes.length - 1];
        }

        /** Adds a node as the first/leftmost or last/rightmost child */
        @Override public Node<T> addEndChild(boolean leftMost, Node<T> shorter) {
            return insertInRelaxedAt(cumulativeSizes, nodes, shorter,
                                     leftMost ? 0 : nodes.length);
        }

        @Override public int height() { return nodes[0].height() + 1; }

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
//            System.out.println(" cumulativeSizes=" + arrayString(cumulativeSizes));
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

        @Override public boolean thisNodeHasRelaxedCapacity(int numNodes) {
//            System.out.println("thisNodeHasCapacity(): nodes.length=" + nodes.length +
//                               " MAX_NODE_LENGTH=" + MAX_NODE_LENGTH +
//                               " MIN_NODE_LENGTH=" + MIN_NODE_LENGTH +
//                               " STRICT_NODE_LENGTH=" + STRICT_NODE_LENGTH);
            return nodes.length + numNodes <= MAX_NODE_LENGTH;
        }

//        @Override public Node<T>[] children() { return nodes; }
//
//        /*
//                private static final class CumulativeSizes {
//                    int szSoFar;
//                    int srcOffset;
//                    int[] destArray;
//                    int destPos;
//                    int length;
//                }
//        */
//        @Override public void calcCumulativeSizes(CumulativeSizes cs) {
//            if ( (cs.szSoFar == 0) && (cs.srcOffset == 0) ) {
//                //                      src, srcPos,    dest,destPos, length
//                System.arraycopy(cumulativeSizes, cs.srcOffset, cs.destArray, cs.destPos,
//                                 cs.length);
//            } else {
//                for (int i = 0; i < cs.length; i++) {
//                    cs.destArray[cs.destPos + i] = cumulativeSizes[cs.srcOffset + i] + cs.szSoFar;
//                }
//            }
//        }

//        private Relaxed<T> catKids(BranchNode<T> left, BranchNode<T> right) {
//            Node<T>[] leftKids = left.children();
//            Node<T>[] rightKids = right.children();
//            int numNewNodes = leftKids.length + rightKids.length;
//            int[] sizes = new int[numNewNodes];
//            Node<T>[] kids = arrayGenericConcat(leftKids, rightKids);
//
//            { // "Let" block for for variable scope.
//                CumulativeSizes cs = new CumulativeSizes();
////                cs.szSoFar = 0;
////                cs.srcOffset = 0;
//                cs.destArray = sizes;
////                cs.destPos = 0;
//                cs.length = leftKids.length;
//                calcCumulativeSizes(cs);
//            }
//
//            { // "Let" block for for variable scope.
//                CumulativeSizes cs = new CumulativeSizes();
//                cs.szSoFar = sizes[leftKids.length - 1];
//                cs.srcOffset = leftKids.length;
//                cs.destArray = sizes;
//                cs.destPos = leftKids.length;
//                cs.length = rightKids.length;
//                calcCumulativeSizes(cs);
//            }
//
//            //                      src, srcPos,    dest,destPos, length
//            return new Relaxed<>(sizes, kids);
//        }

//        @Override public Relaxed<T> precatChildren(BranchNode<T> n) {
//            if ( (height() - n.height()) < 1 ) {
//                int subNodeIndex = subNodeIndex(index);
//                Relaxed<T> alteredNode =
//                        nodes[subNodeIndex].precatChildren(n);
//                Node<T>[] newNodes = replaceInArrayAt(alteredNode, nodes, subNodeIndex, Node.class);
//                return new Relaxed<>(cumulativeSizes, newNodes);
//
//            }
//            if (!thisNodeHasRelaxedCapacity(n.numChildren())) {
//                throw new IllegalArgumentException("Called precatChildren without checking thisNodeHasRelaxedCapacity");
//            }
//
//            return catKids(n, this);
//        }
//
//        @Override public Relaxed<T> concatChildren(BranchNode<T> n) {
//            if (!thisNodeHasRelaxedCapacity(n.numChildren())) {
//                throw new IllegalArgumentException("Called concatChildren without checking thisNodeHasRelaxedCapacity");
//            }
//            return catKids(this, n);
//        }

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
            if (thisNodeHasRelaxedCapacity(1)) { return true; }
            int subNodeIndex = subNodeIndex(index);
            return nodes[subNodeIndex].hasRelaxedCapacity(subNodeAdjustedIndex(index, subNodeIndex),
                                                          size);
        }

//        @Override Relaxed<T> join(Node<T> that) {
//            if (that.height() > this.height()) {
//                return nodes[nodes.length - 1].join(that);
//            }
//        }

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

//            System.out.println("subNodeIndex=" + subNodeIndex);
            if ( (subNodeIndex > 0) && (splitIndex == cumulativeSizes[subNodeIndex - 1]) ) {
//                System.out.println("FALLS ON AN EXISTING NODE BOUNDARY");
                Tuple2<Node<T>[],Node<T>[]> splitNodes = splitArray(nodes, subNodeIndex);

                int[][] splitCumSizes = splitArray(cumulativeSizes, subNodeIndex);
                int[] leftCumSizes = splitCumSizes[0];
                int[] rightCumSizes = splitCumSizes[1];
                int bias = leftCumSizes[leftCumSizes.length - 1];
                for (int i = 0; i < rightCumSizes.length; i++) {
                    rightCumSizes[i] = rightCumSizes[i] - bias;
                }
                return new SplitNode<>(new Relaxed<>(leftCumSizes, splitNodes._1()), emptyArray(),
                                       new Relaxed<>(rightCumSizes, splitNodes._2()), emptyArray());
            }

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
                int[] leftCumSizes = new int[numLeftItems];
                Node<T>[] leftNodes = genericNodeArray(numLeftItems);
                //                      src, srcPos,    dest,destPos, length
                System.arraycopy(cumulativeSizes, 0, leftCumSizes, 0, numLeftItems);
                if (haveLeft) {
                    int cumulativeSize = (numLeftItems > 1) ? leftCumSizes[numLeftItems - 2] : 0;
                    leftCumSizes[numLeftItems - 1] = cumulativeSize + splitLeft.size();
                }
//                    debug("leftCumSizes=" + arrayString(leftCumSizes));
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

            final Node<T> right = fixRight(nodes, split.right(), subNodeIndex);

            SplitNode<T> ret = new SplitNode<>(left, split.leftFocus(),
                                               right, split.rightFocus());
//            debug("RETURNING=", ret);
            if (this.size() != ret.size()) {
                throw new IllegalStateException("Split on " + this.size() + " items returned " +
                                                ret.size() + " items\n" +
                                                "original=" + this.indentedStr(9) + "\n" +
                                                "splitIndex=" + splitIndex + "\n" +
                                                "leftFocus=" + arrayString(split.leftFocus()) + "\n" +
                                                "left=" + left.indentedStr(5) + "\n" +
                                                "rightFocus=" + arrayString(split.rightFocus()) + "\n" +
                                                "right=" + right.indentedStr(6));
            }

            return ret;
        }

        @Override public int numChildren() { return nodes.length; }

        @SuppressWarnings("unchecked")
        @Override public Node<T> pushFocus(int index, T[] oldFocus) {
            // TODO: Review this entire method.
//            System.out.println("===========\n" +
//                               "Relaxed pushFocus(index=" + index + ", oldFocus=" +
//                               arrayString(oldFocus) + ")");
//            System.out.println("  this: " + this);

            int subNodeIndex = subNodeIndex(index);
            Node<T> subNode = nodes[subNodeIndex];
            int subNodeAdjustedIndex = subNodeAdjustedIndex(index, subNodeIndex);

            // 1st choice: insert into the subNode if it has enough space enough to handle it
            if (subNode.hasRelaxedCapacity(subNodeAdjustedIndex, oldFocus.length)) {
//                System.out.println("  Pushing the focus down to a lower-level node with capacity.");
                Node<T> newNode = subNode.pushFocus(subNodeAdjustedIndex, oldFocus);
                // Make a copy of our nodesArray, replacing the old node at subNodeIndex with the
                // new node
                return replaceInRelaxedAt(cumulativeSizes, nodes, newNode, subNodeIndex, oldFocus.length);
            }

            // I think this is a root node thing.
            if (!thisNodeHasRelaxedCapacity(1)) {
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

//                    System.out.println("old cumulativeSizes=" + arrayString(cumulativeSizes));

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
//                        System.out.println("start of newCumSizes=" + arrayString(newCumSizes));

                        leftSize = cumulativeSizes[subNodeIndex - 1];
//                        System.out.println("cumulativeSize=" + cumulativeSize);
                    }

                    // Copy split nodes and cumulativeSizes
                    newNodes[subNodeIndex] = leftLeaf;
                    newNodes[subNodeIndex + 1] = rightLeaf;
                    leftSize += leftLeaf.size();
                    newCumSizes[subNodeIndex] = leftSize;
                    newCumSizes[subNodeIndex + 1] = leftSize + rightLeaf.size();

//                    System.out.println("continued newNodes=" + arrayString(newNodes));
//                    System.out.println("continued cumulativeSizes=" + arrayString(newCumSizes));


                    if (subNodeIndex < (nodes.length - 1)) {
                        //               src,srcPos,dest,destPos,length
                        System.arraycopy(nodes, subNodeIndex + 1, newNodes, subNodeIndex + 2,
                                         nodes.length - subNodeIndex - 1);
//                        System.out.println("completed newNodes=" + arrayString(newNodes));
                    }
                    numToSkip = 2;
                }
                for (int i = subNodeIndex + numToSkip; i < newCumSizes.length; i++) {
//                    System.out.println("i=" + i);
//                    System.out.println("numToSkip=" + numToSkip);
//                    System.out.println("oldFocus.length=" + oldFocus.length);
//                    System.out.println("cumulativeSizes[i - 1]=" + cumulativeSizes[i - 1]);
                    newCumSizes[i] = cumulativeSizes[i - 1] + oldFocus.length;
//                    System.out.println("newCumSizes so far=" + arrayString(newCumSizes));
                }

//                System.out.println("newNodes=" + arrayString(newNodes));
//                System.out.println("newCumSizes=" + arrayString(newCumSizes));
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
//            System.out.println("To insert: " + arrayString(oldFocus));

            Relaxed<T>[] newSubNode = ((Relaxed<T>) subNode).split();

            Relaxed<T> node1 = newSubNode[0];
            Relaxed<T> node2 = newSubNode[1];

//            System.out.println("Split node1: " + node1);
//            System.out.println("Split node2: " + node2);

            Node<T>[] newNodes = genericNodeArray(nodes.length + 1);

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

        @Override public String indentedStr(int indent) {
            StringBuilder sB = new StringBuilder() // indentSpace(indent)
                    .append("Relaxed(");
            int nextIndent = indent + sB.length();
            sB.append("cumulativeSizes=").append(arrayString(cumulativeSizes)).append("\n")
              .append(indentSpace(nextIndent)).append("nodes=[");
            // + 6 for "nodes="
            return showSubNodes(sB, nodes, nextIndent + 7)
                    .append("])")
                    .toString();
        }

        @Override public String toString() { return indentedStr(0); }

        // TODO: Search for more opportunities to use this
        /**
         Replace a node in a relaxed node by recalculating the cumulative sizes and copying
         all sub nodes.
         @param is original cumulative sizes
         @param ns original nodes
         @param newNode replacement node
         @param subNodeIndex index to replace in this node's immediate children
         @param insertSize the difference in size between the original node and the new node.
         @return a new immutable Relaxed node with the immediate child node replaced.
         */
        static <T> Relaxed<T> replaceInRelaxedAt(int[] is, Node<T>[] ns, Node<T> newNode,
                                                 int subNodeIndex, int insertSize) {
            @SuppressWarnings("unchecked")
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

        /**
         Insert a node in a relaxed node by recalculating the cumulative sizes and copying
         all sub nodes.
         @param oldCumSizes original cumulative sizes
         @param ns original nodes
         @param newNode replacement node
         @param subNodeIndex index to insert in this node's immediate children
         @return a new immutable Relaxed node with the immediate child node inserted.
         */
        static <T> Relaxed<T> insertInRelaxedAt(int[] oldCumSizes, Node<T>[] ns, Node<T> newNode,
                                                int subNodeIndex) {
            @SuppressWarnings("unchecked")
            Node<T>[] newNodes = insertIntoArrayAt(newNode, ns, subNodeIndex, Node.class);

            int oldLen = oldCumSizes.length;
//            if (subNodeIndex > oldLen) {
//                throw new IllegalStateException("subNodeIndex > oldCumSizes.length");
//            }

            int[] newCumSizes = new int[oldLen + 1];
            // Copy unchanged cumulative sizes
            if (subNodeIndex > 0) {
                System.arraycopy(oldCumSizes, 0, newCumSizes, 0, subNodeIndex);
            }
            // insert the cumulative size of the new node
            int newNodeSize = newNode.size();
            // Find cumulative size of previous node
            int prevNodeTotal =
                    (subNodeIndex == 0) ? 0
                                        : oldCumSizes[subNodeIndex - 1];

            newCumSizes[subNodeIndex] = newNodeSize + prevNodeTotal;

            for (int i = subNodeIndex; i < oldCumSizes.length; i++) {
                newCumSizes[i + 1] = oldCumSizes[i] + newNodeSize;
            }
            return new Relaxed<>(newCumSizes, newNodes);
        }

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
                // Here the first (leftmost) node of the right-hand side was turned into the focus
                // and we have additional right-hand origNodes to adjust the parent for.
                int[] rightCumSizes = new int[numRightNodes];
                Node<T>[] rightNodes = genericNodeArray(numRightNodes);

//                    System.out.println("origNodes=" + arrayString(origNodes));
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

//                    System.out.println("rightNodes=" + arrayString(rightNodes));

                // For relaxed nodes, we could calculate from previous cumulativeSizes instead of calling .size()
                // on each one.  For strict, we could just add a strict amount.  For now, this works.
                for (int i = destCopyStartIdx; i < numRightNodes; i++) {
                    cumulativeSize += rightNodes[i].size();
                    rightCumSizes[i] = cumulativeSize;
                }

                right = new Relaxed<>(rightCumSizes, rightNodes);
            }
            return right;
        } // end fixRight()
    } // end class Relaxed

    // =================================== Tree-walking Iterator ==================================

    /** Holds a node and the index of the child node we are currently iterating in. */
    private static final class IdxNode<E> implements UnmodIterator<Node<E>> {
        int idx = 0;
        final Node<E> node;
        IdxNode(Node<E> n) { node = n; }
        @Override public boolean hasNext() { return idx < node.numChildren(); }
        @Override public Node<E> next() {
            Node<E> n = node.child(idx);
            idx++;
            return n;
        }
        @Override public String toString() { return "IdxNode(" + idx + " " + node + ")"; }
    }

    /** Holds a Leaf node and the index of the child we are currently returning. */
    private static final class IdxLeaf<E> implements UnmodIterator<E> {
        int idx = 0;
        final Leaf<E> leaf;
        IdxLeaf(Leaf<E> n) { leaf = n; }
        @Override public boolean hasNext() { return idx < leaf.numChildren(); }
        @Override public E next() {
            E n = leaf.get(idx);
            idx++;
            return n;
        }
        @Override public String toString() { return "IdxLeaf(" + idx + " " + leaf + ")"; }
    }

    private final class Iter implements UnmodSortedIterator<E> {

        @SuppressWarnings("unchecked")
        private IdxNode<E>[] genericArrayCreate(int depth) {
            return (IdxNode<E>[]) new IdxNode<?>[depth];
        }

        // We want this iterator to walk the node tree.
//        private int childIndex = 0;
        private final IdxNode<E>[] stack;
        private int stackMaxIdx = -1;

        private void stackAdd(IdxNode<E> i) {
            stackMaxIdx++;
            stack[stackMaxIdx] = i;
        }


        //        private int leafIdx = 0;
//        private Leaf<E> leaf;
        private IdxLeaf<E> idxLeaf;
        private Iter() {

            // Push the focus so we don't have to ever check the index.
            Node<E> newRoot = ((focus != null) && focus.length > 0)
                              ? root.pushFocus(focusStartIndex, focus)
                              : root;

            stack = genericArrayCreate(newRoot.height());

//            System.out.println("newRoot:" + newRoot.indentedStr("newRoot:".length()));
            idxLeaf = nextLeaf(newRoot);
        }

        // Descent to the leftmost unused leaf node.
        private IdxLeaf<E> nextLeaf(Node<E> node) {
            // Descent to left-most bottom node.
            while (!(node instanceof Leaf)) {
                IdxNode<E> in = new IdxNode<>(node);
                stackAdd(in);
                node = in.next();
            }
            return new IdxLeaf<>((Leaf<E>) node);
        }

        private IdxLeaf<E> ensureLeaf() {
            // While nodes are used up, get next node from node one level up.
            while ( (stackMaxIdx > -1) && !stack[stackMaxIdx].hasNext() ) {
                stackMaxIdx--;
            }

            if (stackMaxIdx < 0) {
                return null;
            }
            // If node one level up is used up, find a node that isn't used up and descend to its
            // leftmost leaf.
            return nextLeaf(stack[stackMaxIdx].next());
        }

        @Override public boolean hasNext() {
            if (idxLeaf == null) { return false; }
            if (idxLeaf.hasNext()) { return true; }
            idxLeaf = ensureLeaf();
            return (idxLeaf != null) && idxLeaf.hasNext();
        }

        @Override public E next() {
            // If there's more in this leaf node, return it.
            if (!idxLeaf.hasNext()) {
                idxLeaf = ensureLeaf();
            }
            return idxLeaf.next();
        }
    }

    // =================================== Array Helper Functions ==================================
    // Helper function to avoid type warnings.
    @SuppressWarnings("unchecked")
    private static <T> T[] emptyArray() { return (T[]) EMPTY_ARRAY; }

    @SuppressWarnings("unchecked")
    private static <T> Node<T>[] genericNodeArray(int size) {
        return (Node<T>[]) new Node<?>[size];
    }

//    // Thank you jeannicolas
//    // http://stackoverflow.com/questions/80476/how-can-i-concatenate-two-arrays-in-java
//    private static <T> T[] arrayGenericConcat(T[] a, T[] b) {
//        int aLen = a.length;
//        int bLen = b.length;
//
//        @SuppressWarnings("unchecked")
//        T[] c = (T[]) Array.newInstance(a.getClass().getComponentType(), aLen + bLen);
//        System.arraycopy(a, 0, c, 0, aLen);
//        System.arraycopy(b, 0, c, aLen, bLen);
//
//        return c;
//    }

    // Helper function to avoid type warnings.
    @SuppressWarnings("unchecked")
    private static <T> T[] singleElementArray(T elem) {
        return (T[]) new Object[] { elem };
    }

    private static <T> T[] insertIntoArrayAt(T item, T[] items, int idx, Class<T> tClass) {
        // Make an array that's one bigger.  It's too bad that the JVM bothers to
        // initialize this with nulls.

//        System.out.println("items.getClass(): " + items.getClass());
//        System.out.println("items.getClass().getComponentType(): " + items.getClass().getComponentType());
//        System.out.println("item.getClass(): " + item.getClass());

        @SuppressWarnings("unchecked")
        // Make an array that big enough.  It's too bad that the JVM bothers to
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

//    private static <T> T[] insertIntoArrayAt(T item, T[] items, int idx) {
//        return insertIntoArrayAt(item, items, idx, null);
//    }

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

//    private static int[] replaceInIntArrayAt(int replacedItem, int[] origItems, int idx) {
//        // Make an array that big enough.  It's too bad that the JVM bothers to
//        // initialize this with nulls.
//        int[] newItems = new int[origItems.length];
//        System.arraycopy(origItems, 0, newItems, 0, origItems.length);
//        newItems[idx] = replacedItem;
//        return newItems;
//    }

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

    /**
     Only call this if the array actually needs to be split (0 &lt; splitPoint &lt; orig.length).
     @param orig array to split
     @param splitIndex items less than this index go in the left, equal or greater in the right.
     @return a 2D array of leftItems then rightItems
     */
    private static <T> Tuple2<T[],T[]> splitArray(T[] orig, int splitIndex) { //, Class<T> tClass) {
        if (splitIndex < 1) {
            throw new IllegalArgumentException("Called split when splitIndex < 1");
        }
        if (splitIndex > orig.length - 1) {
            throw new IllegalArgumentException("Called split when splitIndex > orig.length - 1");
        }

        // NOTE:
        // I sort of suspect that generic 2D array creation where the two arrays are of a different
        // length is not possible in Java, or if it is, it's not likely to be much faster than
        // what we have here.  I'd just copy the Arrays.copyOf code everywhere this function is used
        // if you want more speed.
//        int rightLength = orig.length - splitIndex;
//        Class<T> tClass = (Class<T>) orig.getClass().getComponentType();
//        Tuple2<T[],T[]> split = Tuple2.of((T[]) Array.newInstance(tClass, splitIndex),
//                                          (T[]) Array.newInstance(tClass, rightLength));
//
        // Tuple2<T[],T[]> split =
        return Tuple2.of(Arrays.copyOf(orig, splitIndex),
                         Arrays.copyOfRange(orig, splitIndex, orig.length));

//        // original array, offset, newArray, offset, length
//        System.arraycopy(orig, 0, split._1(), 0, splitIndex);
////            System.out.println("    left: " + arrayString(left));
//
//        System.arraycopy(orig, splitIndex, split._2(), 0, rightLength);
////            System.out.println("    right: " + arrayString(right));
//        return split;
    }

    /**
     Only call this if the array actually needs to be split (0 &lt; splitPoint &lt; orig.length).
     @param orig array to split
     @param splitIndex items less than this index go in the left, equal or greater in the right.
     @return a 2D array of leftItems then rightItems
     */
    private static int[][] splitArray(int[] orig, int splitIndex) {
        // This function started an exact duplicate of the one above, but for ints.
        if (splitIndex < 1) {
            throw new IllegalArgumentException("Called split when splitIndex < 1");
        }
        if (splitIndex > orig.length - 1) {
            throw new IllegalArgumentException("Called split when splitIndex > orig.length - 1");
        }
        int rightLength = orig.length - splitIndex;
        int[][] split = new int[][] {new int[splitIndex],
                                     new int[rightLength]};
        // original array, offset, newArray, offset, length
        System.arraycopy(orig, 0, split[0], 0, splitIndex);
//            System.out.println("    left: " + arrayString(left));

        System.arraycopy(orig, splitIndex, split[1], 0, rightLength);
//            System.out.println("    right: " + arrayString(right));
        return split;
    }

    // =============================== Debugging and pretty-printing ===============================

    // Note, this is part of something completely different, but was especially useful for
    // debugging the above.  So much so, that I want to keep it when I'm done, but it needs
    // to move somewhere else before releasing.
    private static final String[] SPACES = {
            "",
            " ",
            "  ",
            "   ",
            "    ",
            "     ",
            "      ",
            "       ",
            "        ",
            "         ",
            "          ",
            "           ",
            "            ",
            "             ",
            "              ",
            "               ",
            "                ",
            "                 ",
            "                  ",
            "                   ",
            "                    ",
            "                     ",
            "                      ",
            "                       ",
            "                        ",
            "                         ",
            "                          ",
            "                           ",
            "                            ",
            "                             ",
            "                              ",
            "                               ",
            "                                ",
            "                                 ",
            "                                  ",
            "                                   ",
            "                                    ",
            "                                     ",
            "                                      ",
            "                                       ",
            "                                        ",
            "                                         ",
            "                                          ",
            "                                           ",
            "                                            ",
            "                                             ",
            "                                              ",
            "                                               ",
            "                                                "};

    private static final int SPACES_LENGTH_MINUS_ONE = SPACES.length - 1;

    static StringBuilder indentSpace(int len) {
        StringBuilder sB = new StringBuilder();
        if (len < 1) { return sB; }
        while (len > SPACES_LENGTH_MINUS_ONE) {
            sB.append(SPACES[SPACES_LENGTH_MINUS_ONE]);
            len = len - SPACES_LENGTH_MINUS_ONE;
        }
        return sB.append(SPACES[len]);
    }

    private static <T> String arrayString(T[] items) {
        StringBuilder sB = new StringBuilder("[");
        boolean isFirst = true;
        for (T item : items) {
            if (isFirst) {
                isFirst = false;
            } else {
                sB.append(" ");
            }
            if (item instanceof String) {
                sB.append("\"").append(item).append("\"");
            } else {
                sB.append(item);
            }
        }
        return sB.append("]").toString();
    }

    // TODO: We need one of these for each type of primitive for pretty-printing without commas.
    private static String arrayString(int[] items) {
        StringBuilder sB = new StringBuilder("[");
        boolean isFirst = true;
        for (int item : items) {
            if (isFirst) {
                isFirst = false;
            } else {
                sB.append(" ");
            }
            sB.append(item);
        }
        return sB.append("]").toString();
    }

    private static StringBuilder showSubNodes(StringBuilder sB, Node[] nodes, int nextIndent) {
        boolean isFirst = true;
        for (Node n : nodes) {
            if (isFirst) {
                isFirst = false;
            } else {
//                sB.append(" ");
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

//    private static void debug(String txt, Indented obj) {
//        System.out.println(txt + obj.indentedStr(txt.length()));
//    }
//    private static void debug(String txt) { System.out.println(txt); }

} // end class RrbTree
