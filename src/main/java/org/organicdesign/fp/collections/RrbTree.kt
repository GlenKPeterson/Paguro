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
package org.organicdesign.fp.collections

import org.organicdesign.fp.collections.Indented.Companion.arrayString
import org.organicdesign.fp.collections.Indented.Companion.indentSpace
import org.organicdesign.fp.collections.RrbTree.ImRrbt
import org.organicdesign.fp.collections.RrbTree.MutRrbt
import java.io.InvalidObjectException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable
import java.util.Arrays

/**
 *
 * An RRB Tree is an immutable List (like Clojure's PersistentVector) that also supports random inserts, deletes,
 * and can be split and joined back together in logarithmic time.
 * This is based on the paper, "RRB-Trees: Efficient Immutable Vectors" by Phil Bagwell and
 * Tiark Rompf, with the following differences:
 *
 *
 *  * The Relaxed nodes can be sized between n/3 and 2n/3 (Bagwell/Rompf specify n and n-1)
 *  * The Join operation sticks the shorter tree unaltered into the larger tree (except for very
 * small trees which just get concatenated).
 *
 *
 *
 * Details were filled in from the Cormen, Leiserson, Rivest & Stein Algorithms book entry
 * on B-Trees.  Also with an awareness of the Clojure PersistentVector by Rich Hickey.  All errors
 * are by Glen Peterson.
 *
 * <h4>History (what little I know):</h4>
 * 1972: B-Tree: Rudolf Bayer and Ed McCreight<br></br>
 * 1998: Purely Functional Data Structures: Chris Okasaki<br></br>
 * 2007: Clojure's Persistent Vector (and HashMap) implementations: Rich Hickey<br></br>
 * 2012: RRB-Tree: Phil Bagwell and Tiark Rompf<br></br>
 *
 *
 * Compared to other collections (timings summary from 2017-06-11):
 *
 *
 *  * append() - [ImRrbt] varies between 90% and 100% of the speed of [PersistentVector] (biggest difference above 100K).
 * [MutRrbt] varies between 45% and 80% of the speed of
 * [PersistentVector.MutVector] (biggest difference from 100 to 1M).
 *  * get() - varies between 50% and 150% of the speed of PersistentVector (PV wins above 1K) if you build RRB using append().
 * If you build rrb using random inserts (worst case), it goes from 90% at 10 items down to 15% of the speed of the PV at 1M items.
 *  * iterate() - is about the same speed as PersistentVector
 *  * insert(0, item) - beats ArrayList above 1K items (worse than ArrayList below 100 items).
 *  * insert(random, item) - beats ArrayList above 10K items (worse than ArrayList until then).
 *  * O(log n) split(), join(), and remove() (not timed yet).
 *
 *
 *
 * Latest detailed timing results are
 * [here](https://docs.google.com/spreadsheets/d/1D0bjfsHpmK7aJzyE2WwArlioI6w69YhZ2f-x0yM6_Z0/edit?usp=sharing).
 */
abstract class RrbTree<E> : BaseList<E>, Indented {

    // Focus is like the tail in Rich Hickey's Persistent Vector, but named after the structure
    // in Scala's implementation.  Tail and focus are both designed to allow repeated appends or
    // inserts to the same area of a vector to be done in constant time.  Tail only handles appends
    // but this can handle repeated inserts to any area of a vector.

    /** Mutable version of an [RrbTree].  Timing information is available there.  */
    class MutRrbt<E> internal constructor(private var focus: Array<E?>,
                                          private var focusStartIndex: Int,
                                          private var focusLength: Int,
                                          private var root: Node<E>,
                                          override var size: Int) : RrbTree<E>(), MutList<E> {

        /** {@inheritDoc}  */
        override fun append(item: E?): MutRrbt<E> {
            // If our focus isn't set up for appends or if it's full, insert it into the data structure
            // where it belongs.  Then make a new focus
            if (focusLength >= STRICT_NODE_LENGTH || focusLength > 0 && focusStartIndex < size - focusLength) {
                root = root.pushFocus(focusStartIndex, arrayCopy(focus, focusLength, null))
                @Suppress("UNCHECKED_CAST")
                focus = arrayOfNulls<Any?>(STRICT_NODE_LENGTH) as Array<E?>
                focus[0] = item
                focusStartIndex = size
                focusLength = 1
                size++
                return this
            }

            // TODO: 3. Make the root the first argument to RrbTree, MutRrbt and ImRrbt.

            if (focus.size <= focusLength) {
                focus = arrayCopy(focus, STRICT_NODE_LENGTH, null)
            }
            focus[focusLength] = item
            focusLength++
            size++
            return this
        }

        /** {@inheritDoc}  */
        override fun concat(iterable: Iterable<E>): MutRrbt<E> {
            return super.concat(iterable) as MutRrbt<E>
        }

        override fun debugValidate() {
            if (focusLength > STRICT_NODE_LENGTH) {
                throw IllegalStateException("focus len:" + focusLength +
                                            " gt STRICT_NODE_LENGTH:" + STRICT_NODE_LENGTH +
                                            "\n" + this.indentedStr(0))
            }
            val sz = root.debugValidate()
            if (sz != size - focusLength) {
                throw IllegalStateException("Size incorrect.  Root size: " + root.size() +
                                            " RrbSize: " + size +
                                            " focusLen: " + focusLength + "\n" +
                                            this.indentedStr(0))
            }
            if (focusStartIndex < 0 || focusStartIndex > size) {
                throw IllegalStateException("focusStartIndex out of bounds!\n" + this.indentedStr(0))
            }
            if (root != eliminateUnnecessaryAncestors(root)) {
                throw IllegalStateException("Unnecessary ancestors!\n" + this.indentedStr(0))
            }
        }

        /** {@inheritDoc}  */
        override fun get(index: Int): E? {
            var i = index
            if (i < 0 || i > size) {
                throw IndexOutOfBoundsException("Index: $i size: $size")
            }

            // This is a debugging assertion - can't be covered by a test.
            //        if ( (focusStartIndex < 0) || (focusStartIndex > size) ) {
            //            throw new IllegalStateException("focusStartIndex: " + focusStartIndex +
            //                                            " size: " + size);
            //        }

            if (i >= focusStartIndex) {
                val focusOffset = i - focusStartIndex
                if (focusOffset < focusLength) {
                    return focus[focusOffset]
                }
                i -= focusLength
            }
            return root[i]
        }

        /** {@inheritDoc}  */
        override fun immutable(): ImRrbt<E> {
            return ImRrbt(arrayCopy(focus, focusLength, null),
                          focusStartIndex,
                          root, size)
        }

        /** {@inheritDoc}  */
        override fun indentedStr(indent: Int): String {
            return "RrbTree(size=" + size +
                   " fsi=" + focusStartIndex +
                   " focus=" + arrayString(focus) + "\n" +
                   indentSpace(indent + 8) + "root=" +
                   root.indentedStr(indent + 13) +
                   ")"
        }

        /** {@inheritDoc}  */
        override fun insert(idx: Int, element: E?): MutRrbt<E> {
            // If the focus is full, push it into the tree and make a new one with the new element.
            if (focusLength >= STRICT_NODE_LENGTH) {
                root = root.pushFocus(focusStartIndex,
                                      arrayCopy(focus, focusLength, null))
                focus = singleElementArray(element)
                focusStartIndex = idx
                focusLength = 1
                size++
                return this
            }

            // If we have no focus, add a new one at the ideal spot.
            // TODO: Make sure Immutable does this too.
            if (focusLength == 0) {
                focus = singleElementArray(element)
                focusStartIndex = idx
                focusLength = 1
                size++
                return this
            }

            // If the index is within the focus, add the item there.
            val diff = idx - focusStartIndex

            if ( (diff >= 0) && (diff <= focusLength) ) {
                // Here focus length cannot be zero!
                // We want to double the length each time up to STRICT_NODE_LENGTH
                // because there is no guarantee that the next insert will be in the same
                // place, so this hedges our bets.
                if (focus.size <= focusLength) {
                    val newLen = if (focusLength >= HALF_STRICT_NODE_LENGTH)
                        STRICT_NODE_LENGTH
                    else
                        focusLength shl 1 // double size.
                    focus = arrayCopy(focus, newLen, null)
                }
                // Shift existing items past insertion index to the right
                val numItemsToShift = focusLength - diff
                //                   src, srcPos, dest, destPos,  length
                if (numItemsToShift > 0) {
                    System.arraycopy(focus, diff, focus, diff + 1, numItemsToShift)
                }
                // Put new item into the focus.
                focus[diff] = element
                focusLength++
                size++
                return this
            }

            // Here we are left with an insert somewhere else than the current focus.
            // Here the mutable version has a focus that's longer than the number of items used,
            // So we need to shorten it before pushing it into the tree.
            if (focusLength > 0) {
                root = root.pushFocus(focusStartIndex, arrayCopy(focus, focusLength, null))
            }
            focus = singleElementArray(element)
            focusStartIndex = idx
            focusLength = 1
            size++
            return this
        }

        /** {@inheritDoc}  */
        override fun iterator(): UnmodSortedIterator<E> {
            return Iter(pushFocus())
        }

        /** {@inheritDoc}  */
        override fun pushFocus(): Node<E> {
            return if (focusLength == 0)
                root
            else
                root.pushFocus(focusStartIndex,
                               arrayCopy(focus, focusLength, null))
        }

        /** {@inheritDoc}  */
        override fun toString(): String {
            return UnmodIterable.toString("MutRrbt", this)
        }

        /**
         * Joins the given tree to the right side of this tree (or this to the left side of that one) in
         * something like O(log n) time.
         */
        override fun join(that: RrbTree<E>): RrbTree<E> {
            @Suppress("NAME_SHADOWING")
            var that = that

            // We don't want to wonder below if we're inserting leaves or branch-nodes.
            // Also, it leaves the tree cleaner to just smash leaves onto the bigger tree.
            // Ultimately, we might want to see if we can grab the tail and stick it where it belongs
            // but for now, this should be alright.
            if (that.size < MAX_NODE_LENGTH) {
                return concat(that)
            }
            if (this.size < MAX_NODE_LENGTH) {
                for (i in this.indices) {
                    that = that.insert(i, this[i])
                }
                return that
            }

            // OK, here we've eliminated the case of merging a leaf into a tree.  We only have to
            // deal with tree-into-tree merges below.
            //
            // Note that if the right-hand tree is bigger, we'll effectively add this tree to the
            // left-hand side of that one.  It's logically the same as adding that tree to the right
            // of this, but the mechanism by which it happens is a little different.
            var leftRoot = pushFocus()
            var rightRoot = that.pushFocus()

            //        if (leftRoot != eliminateUnnecessaryAncestors(leftRoot)) {
            //            throw new IllegalStateException("Left had unnecessary ancestors!");
            //        }

            //        if (rightRoot != eliminateUnnecessaryAncestors(rightRoot)) {
            //            throw new IllegalStateException("Right had unnecessary ancestors!");
            //        }

            // Whether to add the right tree to the left one (true) or vice-versa (false).
            // True also means left is taller, false: right is taller.
            val leftIntoRight = leftRoot.height() < rightRoot.height()
            val taller = if (leftIntoRight) rightRoot else leftRoot
            var shorter = if (leftIntoRight) leftRoot else rightRoot

            // Most compact: Descend the taller tree to shorter.height and find room for all
            //     shorter children as children of that node.
            //
            // Next: add the shorter node, unchanged, as a child to the taller tree at
            //       shorter.height + 1
            //
            // If that level of the taller tree is full, add an ancestor to the shorter node and try to
            // fit at the next level up in the taller tree.
            //
            // If this brings us to the top of the taller tree (both trees are the same height), add a
            // new parent node with leftRoot and rightRoot as children

            // Walk down the taller tree to one below the shorter, remembering ancestors.
            var n = taller

            // This is the maximum we can descend into the taller tree (before running out of tree)
            //        int maxDescent = taller.height() - 1;

            // Actual amount we're going to descend.
            val descentDepth = taller.height() - shorter.height()
            //        if ( (descentDepth < 0) || (descentDepth >= taller.height()) ) {
            //            throw new IllegalStateException("Illegal descent depth: " + descentDepth);
            //        }
            val ancestors = genericNodeArray<E>(descentDepth)
            var i = 0
            while (i < ancestors.size) {
                // Add an ancestor to array
                @Suppress("UNCHECKED_CAST")
                (ancestors as Array<Node<E>>)[i] = n
                //            if (n instanceof Leaf) {
                //                throw new IllegalStateException("Somehow found a leaf node");
                //            }
                n = n.endChild(leftIntoRight)
                i++
            }
            // i is incremented before leaving the loop, so decrement it here to make it point
            // to ancestors.length - 1;
            i--

            //        if (n.height() != shorter.height()) {
            //            throw new IllegalStateException("Didn't get to proper height");
            //        }

            // Most compact: Descend the taller tree to shorter.height and find room for all
            //     shorter children as children of that node.
            if (n.thisNodeHasRelaxedCapacity(shorter.numChildren())) {
                // Adding kids of shorter to proper level of taller...
                val kids: Array<out Node<E>> = when (shorter) {
                    is Strict<E> -> shorter.nodes
                    is Relaxed<E> -> shorter.nodes
                    else -> throw IllegalStateException("Expected a strict or relaxed, but found " + shorter::class)
                }
                n = n.addEndChildren(leftIntoRight, kids)
            }

            if (i >= 0) {
                // Go back up one after lowest check.
                n = ancestors[i]
                i--
                //            if (n.height() != shorter.height() + 1) {
                //                throw new IllegalStateException("Didn't go back up enough");
                //            }
            }

            // TODO: Is this used?
            // While nodes in the taller are full, add a parent to the shorter and try the next level
            // up.
            while (!n.thisNodeHasRelaxedCapacity(1) && i >= 0) {

                // no room for short at this level (n has too many kids)
                n = ancestors[i]
                i--

                shorter = addAncestor(shorter)
                //            shorter.debugValidate();

                // Sometimes we care about which is shorter and sometimes about left and right.
                // Since we fixed the shorter tree, we have to update the left/right
                // pointer to point to the new shorter.
                if (leftIntoRight) {
                    leftRoot = shorter
                } else {
                    rightRoot = shorter
                }
            }

            // Here we either have 2 trees of equal height, or
            // we have room in n for the shorter as a child.

            when {
                shorter.height() == n.height() - 1 -> //            if (!n.thisNodeHasRelaxedCapacity(1)) {
                    //                throw new IllegalStateException("somehow got here without relaxed capacity...");
                    //            }
                    // Shorter one level below n and there's room
                    // Trees are not equal height and there's room somewhere.
                    n = n.addEndChild(leftIntoRight, shorter)
            //            n.debugValidate();
                i < 0 -> {
                    // 2 trees of equal height so we make a new parent
                    //            if (shorter.height() != n.height()) {
                    //                throw new IllegalStateException("Expected trees of equal height");
                    //            }

                    val newRootArray = arrayOf(leftRoot, rightRoot)
                    val leftSize = leftRoot.size()
                    val newRoot = Relaxed(intArrayOf(leftSize, leftSize + rightRoot.size()), newRootArray)
                    //            newRoot.debugValidate();
                    return MutRrbt(emptyArray<E?>(), 0, 0, newRoot, newRoot.size())
                }
                else -> throw IllegalStateException("How did we get here?")
            }

            // We've merged the nodes.  Now see if we need to create new parents
            // to hold the changed sub-nodes...
            while (i >= 0) {
                val anc = ancestors[i]
                // By definition, I think that if we need a new root node, then we aren't dealing with

                // leaf nodes, but I could be wrong.
                // I also think we should get rid of relaxed nodes and everything will be much
                // easier.
                val rel = if (anc is Strict<E>)
                    anc.relax()
                else
                    anc as Relaxed<E>

                val repIdx = if (leftIntoRight) 0 else rel.numChildren() - 1
                n = Relaxed.replaceInRelaxedAt(rel.cumulativeSizes, rel.nodes, n, repIdx,
                                               n.size() - rel.nodes[repIdx].size())
                i--
            }

            //        n.debugValidate();
            return MutRrbt(emptyArray<E?>(), 0, 0, n, n.size())
        }

        /** {@inheritDoc}  */
        override fun replace(index: Int, item: E?): MutRrbt<E> {
            @Suppress("NAME_SHADOWING")
            var index = index
            if (index < 0 || index > size) {
                throw IndexOutOfBoundsException("Index: $index size: $size")
            }
            if (index >= focusStartIndex) {
                val focusOffset = index - focusStartIndex
                if (focusOffset < focusLength) {
                    focus[focusOffset] = item
                    return this
                }
                index -= focusLength
            }
            // About to do replace with maybe-adjusted index
            root = root.replace(index, item)
            return this
        }

        /** {@inheritDoc}  */
        override fun without(index: Int): MutRrbt<E> {
            return super.without(index) as MutRrbt<E>
        }

//        override fun size(): Int {
//            return size
//        }

        /**
         * Divides this RRB-Tree such that every index less-than the given index ends up in the
         * left-hand tree and the indexed item and all subsequent ones end up in the right-hand tree.
         *
         * @param splitIndex the split point (excluded from the left-tree, included in the right one)
         * @return two new sub-trees as determined by the split point.  If the point is 0 or
         * this.size() one tree will be empty (but never null).
         */
        override fun split(splitIndex: Int): Pair<MutRrbt<E>, MutRrbt<E>> {
            if (splitIndex < 1 || splitIndex > size) {
                throw IndexOutOfBoundsException(
                        "Constraint violation failed: 1 <= splitIndex <= size")
            }
            // Push the focus before splitting.
            val newRoot = pushFocus()

            // If a leaf-node is split, the fragments become the new focus for each side of the split.
            // Otherwise, the focus can be left empty, or the last node of each side can be made into
            // the focus.

            val split = newRoot.splitAt(splitIndex)

            //        split.left().debugValidate();
            //        split.right().debugValidate();

            val lFocus = split.leftFocus
            val left = eliminateUnnecessaryAncestors(split.left)

            val rFocus = split.rightFocus
            val right = eliminateUnnecessaryAncestors(split.right)

            // These branches are identical, just different classes.
            return Pair(MutRrbt(lFocus, left.size(), lFocus.size,
                                left, left.size() + lFocus.size),
                        MutRrbt(rFocus, 0, rFocus.size,
                                right, right.size() + rFocus.size))
        }
    }

    /** Immutable version of an [RrbTree].  Timing information is available there.  */
    class ImRrbt<E> internal constructor(private val focus: Array<E?>,
                                         private val focusStartIndex: Int,
                                         private val root: Node<E>,
                                         override val size: Int) : RrbTree<E>(), ImList<E>, Serializable {

        // Check out Josh Bloch Item 78, p. 312 for an explanation of what's going on here.
        private class SerializationProxy<E> internal constructor(@field:Transient() private var rrbTree: RrbTree<E>) : Serializable {

            private val size: Int = rrbTree.size

            // Taken from Josh Bloch Item 75, p. 298
            private fun writeObject(s: ObjectOutputStream) {
                s.defaultWriteObject()
                // Write out all elements in the proper order
                for (entry in rrbTree) {
                    s.writeObject(entry)
                }
            }

            private fun readObject(s: ObjectInputStream) {
                s.defaultReadObject()
                val temp = emptyMutable<E>()
                for (i in 0 until size) {
                    @Suppress("UNCHECKED_CAST")
                    temp.append(s.readObject() as E)
                }
                rrbTree = temp.immutable()
            }

            private fun readResolve(): Any? {
                return rrbTree
            }

            companion object {
                // For serializable.  Make sure to change whenever internal data format changes.
                private const val serialVersionUID = 20160904155600L
            }
        }

        private fun writeReplace(): Any {
            return SerializationProxy(this)
        }

        @Suppress("UNUSED_PARAMETER") // This is required for a non-inherited Java internal used for Serialization.
        private fun readObject(obj: java.io.ObjectInputStream) {
            throw InvalidObjectException("Proxy required")
        }

        // =================================== Instance Methods ===================================

        /** {@inheritDoc}  */
        override fun append(item: E?): ImRrbt<E> {
            // If our focus isn't set up for appends or if it's full, insert it into the data
            // structure where it belongs.  Then make a new focus
            if (focus.size >= STRICT_NODE_LENGTH || focus.isNotEmpty() && focusStartIndex < size - focus.size) {
                val newRoot = root.pushFocus(focusStartIndex, focus)
                return ImRrbt(singleElementArray(item), size, newRoot,
                              size + 1)
            }
            return ImRrbt(insertIntoArrayAt(item, focus, focus.size),
                          focusStartIndex, root,
                          size + 1)
        }

        /** {@inheritDoc}  */
        override fun concat(iterable: Iterable<E>): ImRrbt<E> {
            return this.mutable().concat(iterable).immutable()
        }

        override fun debugValidate() {
            if (focus.size > STRICT_NODE_LENGTH) {
                throw IllegalStateException("focus len:" + focus.size +
                                            " gt STRICT_NODE_LENGTH:" + STRICT_NODE_LENGTH +
                                            "\n" + this.indentedStr(0))
            }
            val sz = root.debugValidate()
            if (sz != size - focus.size) {
                throw IllegalStateException("Size incorrect.  Root size: " + root.size() +
                                            " RrbSize: " + size +
                                            " focusLen: " + focus.size + "\n" +
                                            this.indentedStr(0))
            }
            if (focusStartIndex < 0 || focusStartIndex > size) {
                throw IllegalStateException("focusStartIndex out of bounds!\n" + this.indentedStr(0))
            }
            if (root != eliminateUnnecessaryAncestors(root)) {
                throw IllegalStateException("Unnecessary ancestors!\n" + this.indentedStr(0))
            }
        }

        /** {@inheritDoc}  */
        override fun get(index: Int): E? {
            var i = index
            if (i < 0 || i > size) {
                throw IndexOutOfBoundsException("Index: $i size: $size")
            }

            // This is a debugging assertion - can't be covered by a test.
            //        if ( (focusStartIndex < 0) || (focusStartIndex > size) ) {
            //            throw new IllegalStateException("focusStartIndex: " + focusStartIndex +
            //                                            " size: " + size);
            //        }

            if (i >= focusStartIndex) {
                val focusOffset = i - focusStartIndex
                if (focusOffset < focus.size) {
                    return focus[focusOffset]
                }
                i -= focus.size
            }
            return root[i]
        }

        /** {@inheritDoc}  */
        override fun insert(idx: Int, element: E?): ImRrbt<E> {
            // If the focus is full, push it into the tree and make a new one with the new element.
            if (focus.size >= STRICT_NODE_LENGTH) {
                val newRoot = root.pushFocus(focusStartIndex, focus)
                val newFocus = singleElementArray(element)
                return ImRrbt(newFocus, idx, newRoot, size + 1)
            }

            // If the index is within the focus, add the item there.
            val diff = idx - focusStartIndex

            if (diff >= 0 && diff <= focus.size) {
                // new focus
                val newFocus = insertIntoArrayAt(element, focus, diff)
                return ImRrbt(newFocus, focusStartIndex, root, size + 1)
            }

            // Here we are left with an insert somewhere else than the current focus.
            val newRoot = if (focus.isNotEmpty()) {
                root.pushFocus(focusStartIndex, focus)
            } else {
                root
            }
            val newFocus = singleElementArray(element)
            return ImRrbt(newFocus, idx, newRoot, size + 1)
        }

        /** {@inheritDoc}  */
        override fun mutable(): MutRrbt<E> =
                MutRrbt(arrayCopy(focus, focus.size, null),
                        focusStartIndex, focus.size,
                        root, size)

        /** {@inheritDoc}  */
        override fun iterator(): UnmodSortedIterator<E> = Iter(pushFocus())

        /** {@inheritDoc}  */
        override fun pushFocus(): Node<E> =
                if (focus.isEmpty())
                    root
                else
                    root.pushFocus(focusStartIndex, focus)

        /**
         * Joins the given tree to the right side of this tree (or this to the left side of that one)
         * in something like O(log n) time.
         */
        override fun join(that: RrbTree<E>): RrbTree<E> {
            @Suppress("NAME_SHADOWING")
            var that = that

            // We don't want to wonder below if we're inserting leaves or branch-nodes.
            // Also, it leaves the tree cleaner to just smash leaves onto the bigger tree.
            // Ultimately, we might want to see if we can grab the tail and stick it where it
            // belongs but for now, this should be alright.
            if (that.size < MAX_NODE_LENGTH) {
                return concat(that)
            }
            if (this.size < MAX_NODE_LENGTH) {
                for (i in 0 until size) {
                    that = that.insert(i, this[i])
                }
                return that
            }

            // OK, here we've eliminated the case of merging a leaf into a tree.  We only have to
            // deal with tree-into-tree merges below.
            //
            // Note that if the right-hand tree is bigger, we'll effectively add this tree to the
            // left-hand side of that one.  It's logically the same as adding that tree to the right
            // of this, but the mechanism by which it happens is a little different.
            var leftRoot = pushFocus()
            var rightRoot = that.pushFocus()

            //        if (leftRoot != eliminateUnnecessaryAncestors(leftRoot)) {
            //            throw new IllegalStateException("Left had unnecessary ancestors!");
            //        }

            //        if (rightRoot != eliminateUnnecessaryAncestors(rightRoot)) {
            //            throw new IllegalStateException("Right had unnecessary ancestors!");
            //        }

            // Whether to add the right tree to the left one (true) or vice-versa (false).
            // True also means left is taller, false: right is taller.
            val leftIntoRight = leftRoot.height() < rightRoot.height()
            val taller = if (leftIntoRight) rightRoot else leftRoot
            var shorter = if (leftIntoRight) leftRoot else rightRoot

            // Most compact: Descend the taller tree to shorter.height and find room for all
            //     shorter children as children of that node.
            //
            // Next: add the shorter node, unchanged, as a child to the taller tree at
            //       shorter.height + 1
            //
            // If that level of the taller tree is full, add an ancestor to the shorter node and try to
            // fit at the next level up in the taller tree.
            //
            // If this brings us to the top of the taller tree (both trees are the same height), add a
            // new parent node with leftRoot and rightRoot as children

            // Walk down the taller tree to one below the shorter, remembering ancestors.
            var n = taller

            // This is the maximum we can descend into the taller tree (before running out of tree)
            //        int maxDescent = taller.height() - 1;

            // Actual amount we're going to descend.
            val descentDepth = taller.height() - shorter.height()
            //        if ( (descentDepth < 0) || (descentDepth >= taller.height()) ) {
            //            throw new IllegalStateException("Illegal descent depth: " + descentDepth);
            //        }
            val ancestors = genericNodeArray<E>(descentDepth)
            var i = 0
            while (i < ancestors.size) {
                // Add an ancestor to array
                @Suppress("UNCHECKED_CAST")
                (ancestors as Array<Node<E>>)[i] = n
                n = n.endChild(leftIntoRight)
                i++
            }
            // i is incremented before leaving the loop, so decrement it here to make it point
            // to ancestors.length - 1;
            i--

            //        if (n.height() != shorter.height()) {
            //            throw new IllegalStateException("Didn't get to proper height");
            //        }

            // Most compact: Descend the taller tree to shorter.height and find room for all
            //     shorter children as children of that node.
            if (n.thisNodeHasRelaxedCapacity(shorter.numChildren())) {
                // Adding kids of shorter to proper level of taller...
                val kids: Array<out Node<E>> =
                        when (shorter) {
                            is Strict<E> -> shorter.nodes
                            is Relaxed<E> -> shorter.nodes
                            else -> throw IllegalStateException("Expected a strict or relaxed, but found " +
                                                                shorter::class)
                        }
                n = n.addEndChildren(leftIntoRight, kids)
            }

            if (i >= 0) {
                // Go back up one after lowest check.
                n = ancestors[i]
                i--
                //            if (n.height() != shorter.height() + 1) {
                //                throw new IllegalStateException("Didn't go back up enough");
                //            }
            }

            // TODO: Is this used?
            // While nodes in the taller are full, add a parent to the shorter and try the next level
            // up.
            while (!n.thisNodeHasRelaxedCapacity(1) && i >= 0) {

                // no room for short at this level (n has too many kids)
                n = ancestors[i]
                i--

                shorter = addAncestor(shorter)
                //            shorter.debugValidate();

                // Sometimes we care about which is shorter and sometimes about left and right.
                // Since we fixed the shorter tree, we have to update the left/right
                // pointer to point to the new shorter.
                if (leftIntoRight) {
                    leftRoot = shorter
                } else {
                    rightRoot = shorter
                }
            }

            // Here we either have 2 trees of equal height, or
            // we have room in n for the shorter as a child.

            when {
                shorter.height() == n.height() - 1 ->
                    //            if (!n.thisNodeHasRelaxedCapacity(1)) {
                    //                throw new IllegalStateException("somehow got here without relaxed capacity...");
                    //            }
                    // Shorter one level below n and there's room
                    // Trees are not equal height and there's room somewhere.
                    n = n.addEndChild(leftIntoRight, shorter)
                    // n.debugValidate();
                i < 0 -> {
                    // 2 trees of equal height so we make a new parent
                    //            if (shorter.height() != n.height()) {
                    //                throw new IllegalStateException("Expected trees of equal height");
                    //            }

                    val newRootArray = arrayOf(leftRoot, rightRoot)
                    val leftSize = leftRoot.size()
                    val newRoot = Relaxed(intArrayOf(leftSize, leftSize + rightRoot.size()), newRootArray)
                    //            newRoot.debugValidate();
                    return ImRrbt(emptyArray<E?>(), 0, newRoot, newRoot.size())
                }
                else -> throw IllegalStateException("How did we get here?")
            }

            // We've merged the nodes.  Now see if we need to create new parents
            // to hold the changed sub-nodes...
            while (i >= 0) {
                val anc = ancestors[i]
                // By definition, I think that if we need a new root node, then we aren't dealing with

                // leaf nodes, but I could be wrong.
                // I also think we should get rid of relaxed nodes and everything will be much
                // easier.
                val rel = if (anc is Strict<*>)
                    (anc as Strict<E>).relax()
                else
                    anc as Relaxed<E>

                val repIdx = if (leftIntoRight) 0 else rel.numChildren() - 1
                n = Relaxed.replaceInRelaxedAt(rel.cumulativeSizes, rel.nodes, n, repIdx,
                                               n.size() - rel.nodes[repIdx].size())
                i--
            }

            //        n.debugValidate();
            return ImRrbt(emptyArray<E?>(), 0, n, n.size())
        }

        /** {@inheritDoc}  */
        override fun replace(index: Int, item: E?): ImRrbt<E> {
            @Suppress("NAME_SHADOWING")
            var index = index
            if (index < 0 || index > size) {
                throw IndexOutOfBoundsException("Index: $index size: $size")
            }
            if (index >= focusStartIndex) {
                val focusOffset = index - focusStartIndex
                if (focusOffset < focus.size) {
                    return ImRrbt(replaceInArrayAt(item, focus, focusOffset),
                                  focusStartIndex, root, size)
                }
                index -= focus.size
            }
            // About to do replace with maybe-adjusted index
            return ImRrbt(focus, focusStartIndex, root.replace(index, item), size)
        }


        /** {@inheritDoc}  */
        override fun without(index: Int): ImRrbt<E> {
            return super.without(index) as ImRrbt<E>
        }

//        override fun size(): Int {
//            return size
//        }

        /**
         * Divides this RRB-Tree such that every index less-than the given index ends up in the left-hand
         * tree and the indexed item and all subsequent ones end up in the right-hand tree.
         *
         * @param splitIndex the split point (excluded from the left-tree, included in the right one)
         * @return two new sub-trees as determined by the split point.  If the point is 0 or this.size()
         * one tree will be empty (but never null).
         */
        override fun split(splitIndex: Int): Pair<ImRrbt<E>, ImRrbt<E>> {
            if (splitIndex < 1 || splitIndex > size) {
                throw IndexOutOfBoundsException(
                        "Constraint violation failed: 1 <= splitIndex <= size")
            }
            // Push the focus before splitting.
            val newRoot = pushFocus()

            // If a leaf-node is split, the fragments become the new focus for each side of the split.
            // Otherwise, the focus can be left empty, or the last node of each side can be made into
            // the focus.

            val split = newRoot.splitAt(splitIndex)

            //        split.left().debugValidate();
            //        split.right().debugValidate();

            val lFocus = split.leftFocus
            val left = eliminateUnnecessaryAncestors(split.left)

            val rFocus = split.rightFocus
            val right = eliminateUnnecessaryAncestors(split.right)

            // These branches are identical, just different classes.
            return Pair(ImRrbt(lFocus, left.size(),
                               left, left.size() + lFocus.size),
                        ImRrbt(rFocus, 0,
                               right, right.size() + rFocus.size))
        }

        /** {@inheritDoc}  */
        override fun indentedStr(indent: Int): String {
            return "RrbTree(size=" + size +
                   " fsi=" + focusStartIndex +
                   " focus=" + arrayString(focus) + "\n" +
                   indentSpace(indent + 8) + "root=" +
                   root.indentedStr(indent + 13) +
                   ")"
        }


        /** {@inheritDoc}  */
        override fun toString(): String {
            return UnmodIterable.toString("ImRrbt", this)
        }

        companion object {

            // ===================================== Serialization =====================================
            // This class has a custom serialized form designed to be as small as possible.  It does not
            // have the same internal structure as an instance of this class.

            // For serializable.  Make sure to change whenever internal data format changes.
            private const val serialVersionUID = 20170625165600L

            internal val EMPTY_IM_RRBT = ImRrbt(emptyArray<Any?>(), 0, emptyLeaf(), 0)
        }
    }


    // ===================================== Instance Methods =====================================

    /** {@inheritDoc}  */
    abstract override fun append(item: E?): RrbTree<E>

    /** Internal validation method for testing.  */
    internal abstract fun debugValidate()

    /** {@inheritDoc}  */
    abstract override fun get(index: Int): E?

    /**
     * Inserts an item in the RRB tree pushing the current element at that index and all subsequent
     * elements to the right.
     * @param idx the insertion point
     * @param element the item to insert
     * @return a new RRB-Tree with the item inserted.
     */
    //    @SuppressWarnings("WeakerAccess")
    abstract fun insert(idx: Int, element: E?): RrbTree<E>

    /** {@inheritDoc}  */
    abstract override fun iterator(): UnmodSortedIterator<E>

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
    /**
     * Joins the given tree to the right side of this tree (or this to the left side of that one) in
     * something like O(log n) time.
     */
    abstract fun join(that: RrbTree<E>): RrbTree<E>

    /** Internal method - do not use.  */
    internal abstract fun pushFocus(): Node<E>

    /** {@inheritDoc}  */
    abstract override fun replace(index: Int, item: E?): RrbTree<E>

    // TODO: HERE!!!
//    /** {@inheritDoc}  */
//    abstract override fun size(): Int

    /**
     * Divides this RRB-Tree such that every index less-than the given index ends up in the left-hand
     * tree and the indexed item and all subsequent ones end up in the right-hand tree.
     *
     * @param splitIndex the split point (excluded from the left-tree, included in the right one)
     * @return two new sub-trees as determined by the split point.  If the point is 0 or this.size()
     * one tree will be empty (but never null).
     */
    abstract fun split(splitIndex: Int): Pair<RrbTree<E>,RrbTree<E>>

    /**
     * Returns a new RrbTree minus the given item (all items to the right are shifted left one)
     * This is O(log n).
     */
    open fun without(index: Int): RrbTree<E> =
            if (index > 0 && index < size - 1) {
                val s1 = split(index)
                val s2 = s1.second.split(1)
                s1.first.join(s2.second)
            } else if (index == 0) {
                split(1).second
            } else if (index == size - 1) {
                split(size - 1).first
            } else {
                throw IndexOutOfBoundsException("Failed test: 0 <= index < size")
            }

    // ================================== Standard Object Methods ==================================

    /** {@inheritDoc}  */
    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other !is List<*>) {
            return false
        }
        return this.size == other.size && UnmodSortedIterable.equal(this, UnmodSortedIterable.castFromList(other))
    }

    /** This implementation is correct and compatible with java.util.AbstractList, but O(n).  */
    override fun hashCode(): Int {
        var ret = 1
        for (item in this) {
            ret *= 31
            if (item != null) {
                ret += item.hashCode()
            }
        }
        return ret
    }

    /** {@inheritDoc}  */
    abstract override fun indentedStr(indent: Int): String

    // ================================ Node private inner classes ================================

    internal interface Node<T> : Indented {
        /** Returns the immediate child node at the given index.  */
        fun child(childIdx: Int): Node<T>

        fun debugValidate(): Int

        /** Returns the leftMost (first) or right-most (last) child  */
        fun endChild(leftMost: Boolean): Node<T>

        /** Adds a node as the first/leftmost or last/rightmost child  */
        fun addEndChild(leftMost: Boolean, shorter: Node<T>): Node<T>

        /** Adds kids as leftmost or rightmost of current children  */
        fun addEndChildren(leftMost: Boolean, newKids: Array<out Node<T>>): Node<T>

        /** Return the item at the given index  */
        operator fun get(i: Int): T?

        /** Returns true if this strict-Radix tree can take another 32 items.  */
        fun hasStrictCapacity(): Boolean

        /** Returns the maximum depth below this node.  Leaf nodes are height 1.  */
        fun height(): Int

        //        /** Try to add all sub-nodes to this one. */
        //        Node<T> join(Node<T> that);

        /** Number of items stored in this node  */
        fun size(): Int
        //        /** Returns true if this node's array is not full */
        //        boolean thisNodeHasCapacity();

        /** Can this node take the specified number of children?  */
        fun thisNodeHasRelaxedCapacity(numItems: Int): Boolean

        /**
         * Can we put focus at the given index without reshuffling nodes?
         * @param index the index we want to insert at
         * @param size the number of items to insert.  Must be size < MAX_NODE_LENGTH
         * @return true if we can do so without otherwise adjusting the tree.
         */
        fun hasRelaxedCapacity(index: Int, size: Int): Boolean

        // Splitting a strict node yields an invalid Relaxed node (too short).
        // We don't yet split Leaf nodes.
        // So this needs to only be implemented on Relaxed for now.
        //        Relaxed<T>[] split();

        /** Returns the number of immediate children of this node, not all descendants.  */
        fun numChildren(): Int

        // Because we want to append/insert into the focus as much as possible, we will treat
        // the insert or append of a single item as a degenerate case.  Instead, the primary way
        // to add to the internal data structure will be to push the entire focus array into it
        fun pushFocus(index: Int, oldFocus: Array<T?>): Node<T>

        fun replace(idx: Int, t: T?): Node<T>

        fun splitAt(splitIndex: Int): SplitNode<T>
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

    /** This class is the return type when splitting a node.
     * @param left Left-hand whole-node
     * @param leftFocus Left-focus (leftover items from left node)
     * @param right Right-hand whole-node
     * @param rightFocus Right-focus (leftover items from right node)
     */
    internal class SplitNode<T>(val left: Node<T>,
                                val leftFocus: Array<T?>,
                                val right: Node<T>,
                                val rightFocus: Array<T?>) : Indented {
    //            if (lf.length > STRICT_NODE_LENGTH) {
    //                throw new IllegalStateException("Left focus too long: " + arrayString(lf));
    //            }
    //            if (rf.length > STRICT_NODE_LENGTH) {
    //                throw new IllegalStateException("Right focus too long: " + arrayString(rf));
    //            }

        fun size(): Int {
            return left.size() + leftFocus.size + right.size() + rightFocus.size
        }

        override fun indentedStr(indent: Int): String {
            val sB = StringBuilder() // indentSpace(indent)
                    .append("SplitNode(")
            val nextIndent = indent + sB.length
            val nextIndentStr = indentSpace(nextIndent).toString()
            return sB.append("left=").append(left.indentedStr(nextIndent + 5)).append(",\n")
                    .append(nextIndentStr).append("leftFocus=").append(arrayString(leftFocus))
                    .append(",\n")
                    .append(nextIndentStr).append("right=")
                    .append(right.indentedStr(nextIndent + 6)).append(",\n")
                    .append(nextIndentStr).append("rightFocus=")
                    .append(arrayString(rightFocus)).append(")")
                    .toString()
        }

        override fun toString(): String {
            return indentedStr(0)
        }
    }

    private class Leaf<T>(internal val items: Array<T?>) : Node<T> {
        // It can only be Strict if items.length == STRICT_NODE_LENGTH and if its parents are strict.
        //        boolean isStrict;

        override fun child(childIdx: Int): Node<T> {
            throw UnsupportedOperationException("Don't call this on a leaf")
        }

        override fun debugValidate(): Int {
            if (items.isEmpty()) {
                return 0
            }
            if (items.size < MIN_NODE_LENGTH) {
                throw IllegalStateException("Leaf too short!\n" + this.indentedStr(0))
            } else if (items.size >= MAX_NODE_LENGTH) {
                throw IllegalStateException("Leaf too long!\n" + this.indentedStr(0))
            }
            return items.size
        }

        /** Returns the leftMost (first) or right-most (last) child  */
        override fun endChild(leftMost: Boolean): Node<T> {
            throw UnsupportedOperationException("Don't call this on a leaf")
        }

        /** Adds a node as the first/leftmost or last/rightmost child  */
        override fun addEndChild(leftMost: Boolean, shorter: Node<T>): Node<T> {
            throw UnsupportedOperationException("Don't call this on a leaf")
        }

        /** Adds kids as leftmost or rightmost of current children  */
        override fun addEndChildren(leftMost: Boolean, newKids: Array<out Node<T>>): Node<T> {
            throw UnsupportedOperationException("Don't call this on a leaf")
        }

        override fun get(i: Int): T? {
            return items[i]
        }

        override fun height(): Int {
            return 1
        }

        override fun size(): Int {
            return items.size
        }
        // If we want to add one more to an existing leaf node, it must already be part of a
        // relaxed tree.
        //        public boolean thisNodeHasCapacity() { return items.length < MAX_NODE_LENGTH; }

        override fun hasStrictCapacity(): Boolean {
            return false
        }

        override fun hasRelaxedCapacity(index: Int, size: Int): Boolean {
            // Appends and prepends need to be a good size, but random inserts do not.
            //            if ( (size < 1) || (size >= MAX_NODE_LENGTH) ) {
            //                throw new IllegalArgumentException("Bad size: " + size);
            //              // + " MIN_NODE_LENGTH=" + MIN_NODE_LENGTH + " MAX_NODE_LENGTH=" + MAX_NODE_LENGTH);
            //            }
            return items.size + size < MAX_NODE_LENGTH
        }

        override fun splitAt(splitIndex: Int): SplitNode<T> {
            //            if (splitIndex < 0) {
            //                throw new IllegalArgumentException("Called splitAt when splitIndex < 0");
            //            }
            //            if (splitIndex > items.length - 1) {
            //                throw new IllegalArgumentException("Called splitAt when splitIndex > orig.length - 1");
            //            }

            // Should we just ensure that the split is between 1 and items.length (exclusive)?
            if (splitIndex == 0) {
                return SplitNode<T>(emptyLeaf(), emptyArray(), emptyLeaf(), items)
            }
            if (splitIndex == items.size) {
                return SplitNode<T>(emptyLeaf(), items, emptyLeaf(), emptyArray())
            }
            val split = splitArray(items, splitIndex)
            var splitL = split.first
            var splitR = split.second
            var leafL = emptyLeaf<T>()
            var leafR = emptyLeaf<T>()
            if (splitL.size > STRICT_NODE_LENGTH) {
                leafL = Leaf(splitL)
                splitL = emptyArray()
            }
            if (splitR.size > STRICT_NODE_LENGTH) {
                leafR = Leaf(splitR)
                splitR = emptyArray()
            }
            return SplitNode(leafL, splitL, leafR, splitR)
        }

        internal fun spliceAndSplit(oldFocus: Array<T?>, splitIndex: Int): Array<Leaf<T>> {
            // Consider optimizing:
            val newItems = spliceIntoArrayAt(oldFocus, items, splitIndex, null)

            // Shift right one is divide-by 2.
            val split = splitArray(newItems, newItems.size shr 1)

            return arrayOf(Leaf(split.first), Leaf(split.second))
        }

        override fun numChildren(): Int {
            return size()
        }

        // I think this can only be called when the root node is a leaf.
        override fun pushFocus(index: Int, oldFocus: Array<T?>): Node<T> {
            //            if (oldFocus.length == 0) {
            //                throw new IllegalStateException("Never call this with an empty focus!");
            //            }
            // We put the empty Leaf as the root of the empty vector and it stays there
            // until the first call to this method, at which point, the oldFocus becomes the
            // new root.
            if (items.isEmpty()) {
                return Leaf(oldFocus)
            }

            // Try first to yield a Strict node.  For a leaf like this, that means both this node
            // and the pushed focus are STRICT_NODE_LENGTH.  It also means the old focus is being
            // pushed at either the beginning or the end of this node (not anywhere in-between).
            if (items.size == STRICT_NODE_LENGTH &&
                oldFocus.size == STRICT_NODE_LENGTH &&
                (index == STRICT_NODE_LENGTH || index == 0)) {

                val newNodes = if (index == STRICT_NODE_LENGTH)
                    arrayOf(this, Leaf(oldFocus))
                else
                    arrayOf(Leaf(oldFocus), this)
                // Size is twice STRICT_NODE_LENGTH, so shift left 1 to double.
                return Strict(NODE_LENGTH_POW_2, STRICT_NODE_LENGTH shl 1, newNodes)
            }

            if (items.size + oldFocus.size < MAX_NODE_LENGTH) {
                return Leaf(spliceIntoArrayAt(oldFocus, items, index, null))
            }

            // We should only get here when the root node is a leaf.
            // Maybe we should be more circumspect with our array creation, but for now, just jam
            // jam it into one big array, then split it up for simplicity
            val res = spliceAndSplit(oldFocus, index)
            val leftLeaf = res[0]
            val rightLeaf = res[1]
            val leftSize = leftLeaf.size()
            return Relaxed(intArrayOf(leftSize, leftSize + rightLeaf.size()),
                           res)
        }

        override fun replace(idx: Int, t: T?): Node<T> {
            //            if (idx >= size()) {
            //                throw new IllegalArgumentException("Invalid index " + idx + " >= " + size());
            //            }
            return Leaf(replaceInArrayAt(t, items, idx))
        }

        override fun thisNodeHasRelaxedCapacity(numItems: Int): Boolean {
            //            if ( (numItems < 1) || (numItems >= MAX_NODE_LENGTH) ) {
            //                throw new IllegalArgumentException("Bad size: " + numItems);
            //            }
            return items.size + numItems < MAX_NODE_LENGTH
        }

        override fun toString(): String {
            //            return "Leaf("+ arrayString(items) + ")";
            return arrayString(items)
        }

        override fun indentedStr(indent: Int): String {
            return arrayString(items)
        }
    } // end class Leaf

    // Contains a left-packed tree of exactly 32-item nodes.
    private class Strict<T>(// This is the number of levels below this node (height) times NODE_LENGTH
            // For speed, we calculate it as height << NODE_LENGTH_POW_2
            // TODO: Can we store shift at the top-level Strict only?
            internal val shift: Int,
            internal val size: Int, // These are the child nodes
            internal val nodes: Array<out Node<T>>) : Node<T> {

        override fun child(childIdx: Int): Node<T> {
            return nodes[childIdx]
        }

        override fun debugValidate(): Int {
            if (nodes.size > STRICT_NODE_LENGTH) {
                throw IllegalStateException("Too many child nodes!\n" + this.indentedStr(0))
            }
            var sz = 0
            val height = height() - 1
            val sh = shift - NODE_LENGTH_POW_2
            for (i in nodes.indices) {
                val n = nodes[i]
                if (n !is Strict<*> && n !is Leaf<*>) {
                    throw IllegalStateException(
                            "Strict nodes can only have strict or leaf children!\n" + this.indentedStr(0))
                }
                if (n.height() != height) {
                    throw IllegalStateException("Unequal height!  My height = " + height() + "\n" + this.indentedStr(0))
                }
                if (n is Strict<*> && (n as Strict<*>).shift != sh) {
                    throw IllegalStateException(
                            "Unexpected shift difference between levels!\n" + this.indentedStr(0))
                }
                if (i < nodes.size - 1) {
                    if (n.hasStrictCapacity()) {
                        throw IllegalStateException("Non-last strict node is not full!\n" + this.indentedStr(0))
                    }
                    if (n.size() % STRICT_NODE_LENGTH != 0) {
                        throw IllegalStateException("Non-last strict node has a weird size!\n" + this.indentedStr(0))
                    }
                }
                if (n is Strict<*>) {
                    n.debugValidate()
                }
                sz += n.size()
            }
            return sz
        }

        /** Returns the leftMost (first) or right-most (last) child  */
        override fun endChild(leftMost: Boolean): Node<T> {
            return nodes[if (leftMost) 0 else nodes.size - 1]
        }

        /** Adds a node as the first/leftmost or last/rightmost child  */
        override fun addEndChild(leftMost: Boolean, shorter: Node<T>): Node<T> {
            return if (leftMost || shorter !is Strict<*>) {
                relax().addEndChild(leftMost, shorter)
            } else Strict(shift, size + shorter.size(),
                          insertIntoArrayAt(shorter, nodes, nodes.size, nodeClass()))
        }

        /** Adds kids as leftmost or rightmost of current children  */
        override fun addEndChildren(leftMost: Boolean, newKids: Array<out Node<T>>): Node<T> {
            //            if (!thisNodeHasRelaxedCapacity(newKids.length)) {
            //                throw new IllegalStateException("Can't add enough kids");
            //            }
            return relax().addEndChildren(leftMost, newKids)
        }

        override fun height(): Int {
            return shift / NODE_LENGTH_POW_2 + 1
        }

        /**
         * Returns the highest bits which we use to index into our array - the index of the immediate
         * child of this node.  This is the simplicity (and
         * speed) of Strict indexing.  When everything works, this can be inlined for performance.
         * This could maybe yield a good guess for Relaxed nodes?
         *
         * Shifting right by a number is equivalent to dividing by: 2 raised to the power of that
         * number.
         * i >> n is equivalent to i / (2^n)
         */
        private fun highBits(i: Int): Int {
            return i shr shift
        }

        /**
         * Returns the low bits of the index (the part Strict sub-nodes need to know about).  This
         * only works because the leaf nodes are all the same size and that size is a power of 2
         * (the radix).  All branch must have the same radix (branching factor or number of immediate
         * sub-nodes).
         *
         * Bit shifting is faster than addition or multiplication, but perhaps more importantly, it
         * means we don't have to store the sizes of the nodes which means we don't have to fetch
         * those sizes from memory or use up cache space.  All of this helps make this data structure
         * simple and fast.
         *
         * When everything works, this function can be inlined for performance (if that even helps).
         * Contrast this with how Relaxed nodes work: they use subtraction instead!
         */
        private fun lowBits(i: Int): Int {
            // Little trick: -1 in binary is all ones: 0b11111111111111111111111111111111
            // We shift it left, filling the right-most bits with zeros and creating a bit-mask
            // with ones on the left and zeros on the right
            val shifter = -1 shl shift

            // Now we take the inverse so our bit-mask has zeros on the left and ones on the right
            val invShifter = shifter.inv()

            // Finally, we bitwise-and the mask with the index to leave only the low bits.
            return i and invShifter
        }

        override fun get(i: Int): T? {
            // Find the node indexed by the high bits (for this height).
            // Send the low bits on to our sub-nodes.
            return nodes[highBits(i)][lowBits(i)]
        }

        override fun size(): Int {
            return size
        }

        //        private boolean thisNodeHasCapacity() { return nodes.length < STRICT_NODE_LENGTH; }

        override fun hasStrictCapacity(): Boolean {
            //            boolean ret = thisNodeHasCapacity() || nodes[nodes.length - 1].hasStrictCapacity();
            //            boolean ret2 = highBits(size) != STRICT_NODE_LENGTH;
            //            if (ret != ret2) {
            //                System.out.println("size: " + size);
            //                System.out.println("hasStrict: " + ret);
            //                System.out.println("highBits(size)" + highBits(size));
            //                System.out.println("lowBits(size)" + lowBits(size));
            //                throw new IllegalStateException("Won't work!");
            //            }

            // This works because when a strict node is not full, it's highest child index is
            // STRICT_NODE_LENGTH - 1 (which is what highBits(size) will return.
            // This used to then walk down the right hand side of the tree checking for room.
            // But it turns out that the highest index would have lowBits() equals all ones.
            // size is the maxIndex + 1, so that flips the lowest bit of highBits() and makes
            // all lowBits() zeros so that the following line works:
            return highBits(size) != STRICT_NODE_LENGTH
        }

        override fun hasRelaxedCapacity(index: Int, size: Int): Boolean {
            //            if ( (size < 1) || (size >= MAX_NODE_LENGTH) ) {
            //                throw new IllegalArgumentException("Bad size: " + size);
            //            }
            // It has relaxed capacity because a Relaxed node could have up to MAX_NODE_LENGTH nodes
            // and by definition this Strict node has exactly STRICT_NODE_LENGTH items.
            return size < MAX_NODE_LENGTH - STRICT_NODE_LENGTH
        }

        override fun splitAt(splitIndex: Int): SplitNode<T> {
            //            int size = size();
            //            if ( (splitIndex < 0) || (splitIndex > size) ) {
            //                throw new IllegalArgumentException("Bad splitIndex: " + splitIndex);
            //            }
            if (splitIndex == 0) {
                return SplitNode<T>(emptyLeaf(), emptyArray(), this, emptyArray())
            }
            if (splitIndex == size) {
                return SplitNode<T>(this, emptyArray(), emptyLeaf(), emptyArray())
            }

            // Not split on a child boundary, so find which child to split and pass it the
            // appropriate index.
            val subNodeIndex = highBits(splitIndex)
            val subNode = nodes[subNodeIndex]
            val subNodeAdjustedIndex = lowBits(splitIndex)

            val split:SplitNode<T> = subNode.splitAt(subNodeAdjustedIndex)

            val left: Node<T>
            val splitLeft:Node<T> = split.left
            if (subNodeIndex == 0) {
                left = Strict(shift, splitLeft.size(), arrayOf(splitLeft))
            } else {
                val haveLeft = splitLeft.size() > 0
                val numLeftItems = subNodeIndex + if (haveLeft) 1 else 0
                val leftNodes:Array<out Node<T>> = genericNodeArray(numLeftItems)
                // Copy one less item if we are going to add the split one in a moment.
                // I could have written:
                //     haveLeft ? numLeftItems - 1
                //              : numLeftItems
                // but that's always equal to subNodeIndex.
                System.arraycopy(nodes, 0, leftNodes, 0, subNodeIndex)
                if (haveLeft) {
                    @Suppress("UNCHECKED_CAST")
                    (leftNodes as Array<Node<T>>)[numLeftItems - 1] = splitLeft
                }
                left = Strict(shift, leftNodes.sumBy { it.size() }, leftNodes)
            }

            //            left.debugValidate();
            //            split.right().debugValidate();

            //            if ( (split.right().size() > 0) &&
            //                 (nodes[0].height() != split.right().height()) ) {
            //                throw new IllegalStateException(
            //                        "Have a right node of a different height!" +
            //                        "nodes:" + showSubNodes(new StringBuilder(), nodes, 6) +
            //                        "\nright:" + split.right().indentedStr(6));
            //            }
            val right = Relaxed.fixRight(nodes, split.right, subNodeIndex)

            //            right.debugValidate();

            //            if ( (left.size() > 0) &&
            //                 (right.size() > 0) &&
            //                 (left.height() != right.height()) ) {
            //                throw new IllegalStateException("Unequal heights of split!\n" +
            //                "left: " + left.indentedStr(6) +
            //                "\nright:" + right.indentedStr(6));
            //            }

            //            if (this.size() != ret.size()) {
            //                throw new IllegalStateException(
            //                        "Split on " + this.size() + " items returned " + ret.size() + " items");
            //            }

            return SplitNode(left, split.leftFocus,
                             right, split.rightFocus)
        }

        internal fun relax(): Relaxed<T> {
            val newCumSizes = IntArray(nodes.size)
            var cumulativeSize = 0
            // We know all sub-nodes (except the last) have the same size because they are
            // packed-left.
            val subNodeSize = nodes[0].size()
            for (i in 0 until nodes.size - 1) {
                cumulativeSize += subNodeSize
                newCumSizes[i] = cumulativeSize
            }

            // Final node may not be packed, so it could have a different size
            cumulativeSize += nodes[nodes.size - 1].size()
            newCumSizes[newCumSizes.size - 1] = cumulativeSize

            return Relaxed(newCumSizes, nodes)
        }

        override fun numChildren(): Int {
            return nodes.size
        }

        override fun pushFocus(index: Int, oldFocus: Array<T?>): Node<T> {
            // If the proper sub-node can take the additional array, let it!
            val subNodeIndex = highBits(index)

            // It's a strict-compatible addition if the focus being pushed is of
            // STRICT_NODE_LENGTH and the index it's pushed to falls on the final leaf-node boundary
            // and the children of this node are leaves and this node is not full.
            if (oldFocus.size == STRICT_NODE_LENGTH) {

                if (index == size()) {
                    val lastNode = nodes[nodes.size - 1]
                    if (lastNode.hasStrictCapacity()) {
                        // Pushing focus down to lower-level node with capacity.
                        // TODO: This line appears to be the slowest part.

                        // This variable is my attempt to prevent dynamic dispatch on the call
                        // to pushFocus()
                        val strict = lastNode as Strict<T>
                        val newNode = strict.pushFocus(lowBits(index), oldFocus)
                        val newNodes = replaceInArrayAt(newNode, nodes, nodes.size - 1, nodeClass())
                        return Strict(shift, size + oldFocus.size, newNodes)
                    }
                    // Regardless of what else happens, we're going to add a new node.
                    var newNode: Node<T> = Leaf(oldFocus)

                    // Make a skinny branch of a tree by walking up from the leaf node until our
                    // new branch is at the same level as the old one.  We have to build evenly
                    // (like hotels in Monopoly) in order to keep the tree balanced.  Even height,
                    // but left-packed (the lower indices must all be filled before adding new
                    // nodes to the right).
                    var newShift = NODE_LENGTH_POW_2

                    // If we've got space in our array, we just have to add skinny-branch nodes up
                    // to the level below ours.  But if we don't have space, we have to add a
                    // single-element strict node at the same level as ours here too.
                    val maxShift = if (nodes.size < STRICT_NODE_LENGTH) shift else shift + 1

                    // Make the skinny-branch of single-element strict nodes:
                    while (newShift < maxShift) {
                        // Add a skinny branch node
                        newNode = Strict(newShift, oldFocus.size, arrayOf(newNode))
                        newShift += NODE_LENGTH_POW_2
                    }

                    return if (nodes.size < STRICT_NODE_LENGTH) {
                        // Add a node to the existing array
                        val newNodes = insertIntoArrayAt(newNode, nodes, subNodeIndex, nodeClass())
                        // This could allow cheap strict inserts on any leaf-node boundary...
                        Strict(shift, size + oldFocus.size, newNodes)
                    } else {
                        // Add a level to the Strict tree
                        Strict(shift + NODE_LENGTH_POW_2,
                               size + oldFocus.size,
                               arrayOf(this, newNode))
                    }
                } else if (shift == NODE_LENGTH_POW_2 &&
                           lowBits(index) == 0 &&
                           nodes.size < STRICT_NODE_LENGTH) {
                    // Here we are:
                    //    Pushing a STRICT_NODE_LENGTH focus
                    //    At the level above the leaf nodes
                    //    Inserting *between* existing leaf nodes (or before or after)
                    //    Have room for at least one more leaf child
                    // That makes it free and legal to insert a new STRICT_NODE_LENGTH leaf node and
                    // still yield a Strict (as opposed to Relaxed).

                    // Regardless of what else happens, we're going to add a new node.
                    val newNode = Leaf(oldFocus)

                    val newNodes = insertIntoArrayAt(newNode, nodes, subNodeIndex, nodeClass())
                    // This allows cheap strict inserts on any leaf-node boundary...
                    return Strict(shift, size + oldFocus.size, newNodes)
                }
            } // end if oldFocus.length == STRICT_NODE_LENGTH

            // Here we're going to yield a Relaxed Radix node, so punt to that (slower) logic.
            return relax().pushFocus(index, oldFocus)
        }

        override fun replace(idx: Int, t: T?): Node<T> {
            // Find the node indexed by the high bits (for this height).
            // Send the low bits on to our sub-nodes.
            val thisNodeIdx = highBits(idx)
            val newNode = nodes[thisNodeIdx].replace(lowBits(idx), t)
            return Strict(shift, size,
                          replaceInArrayAt(newNode, nodes, thisNodeIdx, nodeClass()))
        }

        override fun thisNodeHasRelaxedCapacity(numItems: Int): Boolean {
            return nodes.size + numItems < MAX_NODE_LENGTH
        }

        override fun toString(): String {
            //            return "Strict(nodes.length="+ nodes.length + ", shift=" + shift + ")";
            return "Strict" + shift + arrayString(nodes)
        }

        override fun indentedStr(indent: Int): String {
            val sB = StringBuilder() // indentSpace(indent)
                    .append("Strict").append(shift).append("(")
            val len = sB.length
            sB.append("size=").append(size).append("\n")
            sB.append(indentSpace(len + indent))
            return showSubNodes(sB, nodes, indent + len)
                    .append(")")
                    .toString()
        }
    }

    // Contains a relaxed tree of nodes that average around 32 items each.
    // Holds the size of each sub-node and plus all nodes to its left.  You could think of this
    // as maxIndex + 1. This is a separate array so it can be retrieved in a single memory
    // fetch.  Note that this is a 1-based count, not a zero-based index.
    internal class Relaxed<T>(
            internal val cumulativeSizes: IntArray, // The sub nodes
            internal val nodes: Array<out Node<T>>) : Node<T> {
    // Consider removing constraint validations before shipping for performance
    //            if (cumulativeSizes.length < 1) {
    //                throw new IllegalArgumentException("cumulativeSizes.length < 1");
    //            }
    //            if (nodes.length < 1) {
    //                throw new IllegalArgumentException("nodes.length < 1");
    //            }
    //            if (cumulativeSizes.length != nodes.length) {
    //                throw new IllegalArgumentException(
    //                        "cumulativeSizes.length:" + cumulativeSizes.length +
    //                        " != nodes.length:" + nodes.length);
    //            }
    //            int cumulativeSize = 0;
    //            for (int i = 0; i < nodes.length; i++) {
    //                cumulativeSize += nodes[i].size();
    //                if (cumulativeSize != cumulativeSizes[i]) {
    //                    throw new IllegalArgumentException(
    //                            "nodes[" + i + "].size() was " +
    //                            nodes[i].size() +
    //                            " which is not compatable with cumulativeSizes[" +
    //                            i + "] which was " + cumulativeSizes[i] +
    //                            "\n\tcumulativeSizes=" + arrayString(cumulativeSizes) +
    //                            "\n\tnodes=" + arrayString(nodes));
    //                }
    //            }

        override fun child(childIdx: Int): Node<T> {
            return nodes[childIdx]
        }

        override fun debugValidate(): Int {
            var sz = 0
            val height = height() - 1
            if (nodes.size != cumulativeSizes.size) {
                throw IllegalStateException("Unequal size of nodes and sizes!\n" + this.indentedStr(0))
            }
            for (i in nodes.indices) {
                val n = nodes[i]
                if (n.height() != height) {
                    throw IllegalStateException("Unequal height!\n" + this.indentedStr(0))
                }
                sz += n.size()
                if (sz != cumulativeSizes[i]) {
                    throw IllegalStateException("Cumulative Sizes are wrong!\n" + this.indentedStr(0))
                }
            }
            return sz
        }

        /** Returns the leftMost (first) or right-most (last) child  */
        override fun endChild(leftMost: Boolean): Node<T> {
            return nodes[if (leftMost) 0 else nodes.size - 1]
        }

        /** Adds a node as the first/leftmost or last/rightmost child  */
        override fun addEndChild(leftMost: Boolean, shorter: Node<T>): Node<T> {
            return insertInRelaxedAt(cumulativeSizes, nodes, shorter,
                                     if (leftMost) 0 else nodes.size)
        }

        /** Adds kids as leftmost or rightmost of current children  */
        override fun addEndChildren(leftMost: Boolean, newKids: Array<out Node<T>>): Node<T> {
            //            if (!thisNodeHasRelaxedCapacity(newKids.length)) {
            //                throw new IllegalStateException("Can't add enough kids");
            //            }
            //            if (nodes[0].height() != newKids[0].height()) {
            //                throw new IllegalStateException("Kids not same height");
            //            }
            val res = spliceIntoArrayAt(newKids, nodes,
                                        if (leftMost) 0 else nodes.size,
                                        nodeClass())
            // TODO: Figure out which side we inserted on and do the math to adjust counts instead
            // of looking them up.
            return Relaxed(makeSizeArray(res), res)
        }

        override fun height(): Int {
            return nodes[0].height() + 1
        }

        override fun size(): Int {
            return cumulativeSizes[cumulativeSizes.size - 1]
        }

        /**
         * Converts the index of an item into the index of the sub-node containing that item.
         * @param treeIndex The index of the item in the tree
         * @return The index of the immediate child of this node that the desired node resides in.
         */
        private fun subNodeIndex(treeIndex: Int): Int {
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

            var guess = cumulativeSizes.size * treeIndex / size()
            if (guess >= cumulativeSizes.size) {
                // Guessed beyond end length - returning last item.
                return cumulativeSizes.size - 1
            }
            var guessedCumSize = cumulativeSizes[guess]

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
            when {
                guessedCumSize < treeIndex -> {
                    while (guess < cumulativeSizes.size - 1) {
                        //                    System.out.println("    Too low.  Check higher...");
                        guessedCumSize = cumulativeSizes[++guess]
                        if (guessedCumSize >= treeIndex) {
                            //                        System.out.println("    RIGHT!");
                            // See note in equal case below...
                            return if (guessedCumSize == treeIndex)
                                guess + 1
                            else
                                guess
                        }
                        //                    System.out.println("    ==== Guess higher...");
                    }
                    throw IllegalStateException("Can we get here?  If so, how?")
                }
                guessedCumSize > treeIndex + MIN_NODE_LENGTH -> {

                    // guessedCumSize greater than (treeIndex + MIN_NODE_LENGTH)
                    //         Decrement guess and check result again until less, then return PREVIOUS guess
                    while (guess > 0) {
                        //                    System.out.println("    Maybe too high.  Check lower...");
                        val nextGuess = guess - 1
                        guessedCumSize = cumulativeSizes[nextGuess]

                        if (guessedCumSize <= treeIndex) {
                            //                        System.out.println("    RIGHT!");
                            return guess
                        }
                        //                    System.out.println("    ======= Guess lower...");
                        guess = nextGuess
                    }
                    //                System.out.println("    Returning lower: " + guess);
                    return guess
                }
                else -> return if (guessedCumSize == treeIndex) {
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
                    if (treeIndex == size()) guess else guess + 1
                } else {
                    //                System.out.println("    First guess: " + guess);
                    guess
                }
            }
        }

        /**
         * Converts the index of an item into the index to pass to the sub-node containing that item.
         * @param index The index of the item in the entire tree
         * @param subNodeIndex the index into this node's array of sub-nodes.
         * @return The index to pass to the sub-branch the item resides in
         */
        // Better name might be: nextLevelIndex?  subNodeSubIndex?
        private fun subNodeAdjustedIndex(index: Int, subNodeIndex: Int): Int {
            return if (subNodeIndex == 0)
                index
            else
                index - cumulativeSizes[subNodeIndex - 1]
        }

        override fun get(i: Int): T? {
            val subNodeIndex = subNodeIndex(i)
            return nodes[subNodeIndex][subNodeAdjustedIndex(i, subNodeIndex)]
        }

        override fun thisNodeHasRelaxedCapacity(numItems: Int): Boolean {
            return nodes.size + numItems < MAX_NODE_LENGTH
        }

        // I don't think this should ever be called.  Should this throw an exception instead?
        override fun hasStrictCapacity(): Boolean {
            throw UnsupportedOperationException("I don't think this should ever be called.")
            //            return false;
        }

        override fun hasRelaxedCapacity(index: Int, size: Int): Boolean {
            // I think we can add any number of items (less than MAX_NODE_LENGTH)
            //            if ( (size < MIN_NODE_LENGTH) || (size > MAX_NODE_LENGTH) ) {
            //            if ( (size < 1) || (size > MAX_NODE_LENGTH) ) {
            //                throw new IllegalArgumentException("Bad size: " + size);
            //            }
            if (thisNodeHasRelaxedCapacity(1)) {
                return true
            }
            val subNodeIndex = subNodeIndex(index)
            return nodes[subNodeIndex].hasRelaxedCapacity(subNodeAdjustedIndex(index, subNodeIndex),
                                                          size)
        }

        private fun split(): Array<Relaxed<T>> {
            val midpoint = nodes.size shr 1 // Shift-right one is the same as dividing by 2.
            val left = Relaxed(Arrays.copyOf(cumulativeSizes, midpoint),
                               Arrays.copyOf(nodes, midpoint))
            val rightCumSizes = IntArray(nodes.size - midpoint)
            val leftCumSizes = cumulativeSizes[midpoint - 1]
            for (j in rightCumSizes.indices) {
                rightCumSizes[j] = cumulativeSizes[midpoint + j] - leftCumSizes
            }
            // I checked this at javaRepl and indeed this starts from the correct item.
            val right = Relaxed(rightCumSizes,
                                Arrays.copyOfRange(nodes, midpoint, nodes.size))
            return arrayOf(left, right)
        }

        override fun splitAt(splitIndex: Int): SplitNode<T> {
            val size = size()
            //            if ( (splitIndex < 0) || (splitIndex > size) ) {
            //                throw new IllegalArgumentException("Bad splitIndex: " + splitIndex);
            //            }
            if (splitIndex == 0) {
                return SplitNode(emptyLeaf(), emptyArray<T?>(), emptyLeaf(), emptyArray<T?>())
            }
            if (splitIndex == size) {
                return SplitNode<T>(this, emptyArray(), emptyLeaf(), emptyArray())
            }

            val subNodeIndex = subNodeIndex(splitIndex)
            val subNode = nodes[subNodeIndex]

            if (subNodeIndex > 0 && splitIndex == cumulativeSizes[subNodeIndex - 1]) {
                // Falls on an existing node boundary
                val splitNodes = splitArray(nodes, subNodeIndex)

                val splitCumSizes = splitArray(cumulativeSizes, subNodeIndex)
                val leftCumSizes = splitCumSizes[0]
                val rightCumSizes = splitCumSizes[1]
                val bias = leftCumSizes[leftCumSizes.size - 1]
                for (i in rightCumSizes.indices) {
                    rightCumSizes[i] = rightCumSizes[i] - bias
                }
                val left = Relaxed(leftCumSizes, splitNodes.first)
                //                left.debugValidate();
                val right = Relaxed(rightCumSizes, splitNodes.second)
                //                right.debugValidate();

                return SplitNode<T>(left, emptyArray(), right, emptyArray())
            }

            val subNodeAdjustedIndex = subNodeAdjustedIndex(splitIndex, subNodeIndex)
            val split = subNode.splitAt(subNodeAdjustedIndex)

            val left: Node<T>
            var splitLeft = split.left
            //            splitLeft.debugValidate();

            if (subNodeIndex == 0) {
                //                debug("If we have a single left node, it doesn't need a parent.");
                left = splitLeft
            } else {
                val haveLeft = splitLeft.size() > 0
                val numLeftItems = subNodeIndex + if (haveLeft) 1 else 0
                val leftCumSizes = IntArray(numLeftItems)
                val leftNodes = genericNodeArray<T>(numLeftItems)
                //                      src, srcPos,    dest,destPos, length
                System.arraycopy(cumulativeSizes, 0, leftCumSizes, 0, numLeftItems)
                if (haveLeft) {
                    val cumulativeSize = if (numLeftItems > 1) leftCumSizes[numLeftItems - 2] else 0
                    leftCumSizes[numLeftItems - 1] = cumulativeSize + splitLeft.size()
                }
                //                    debug("leftCumSizes=" + arrayString(leftCumSizes));
                // Copy one less item if we are going to add the split one in a moment.
                // I could have written:
                //     haveLeft ? numLeftItems - 1
                //              : numLeftItems
                // but that's always equal to subNodeIndex.
                System.arraycopy(nodes, 0, leftNodes, 0, subNodeIndex)
                if (haveLeft) {
                    // I don't know why I have to fix this here.
                    while (splitLeft.height() < this.height() - 1) {
                        splitLeft = addAncestor(splitLeft)
                    }
                    //                    if ( (leftNodes.length > 0) &&
                    //                         (leftNodes[0].height() != splitLeft.height()) ) {
                    //                        throw new IllegalStateException("nodesHeight: " + leftNodes[0].height() +
                    //                                                        " splitLeftHeight: " + splitLeft.height());
                    //                    }
                    @Suppress("UNCHECKED_CAST")
                    (leftNodes as Array<Node<T>>)[numLeftItems - 1] = splitLeft
                }
                left = Relaxed(leftCumSizes, leftNodes)
                //                left.debugValidate();
            }

            val right = fixRight(nodes, split.right, subNodeIndex)
            //            right.debugValidate();

            //            debug("RETURNING=", ret);
            //            if (this.size() != ret.size()) {
            //                throw new IllegalStateException(
            //                        "Split on " + this.size() + " items returned " +
            //                        ret.size() + " items\n" +
            //                        "original=" + this.indentedStr(9) + "\n" +
            //                        "splitIndex=" + splitIndex + "\n" +
            //                        "leftFocus=" + arrayString(split.leftFocus()) + "\n" +
            //                        "left=" + left.indentedStr(5) + "\n" +
            //                        "rightFocus=" + arrayString(split.rightFocus()) + "\n" +
            //                        "right=" + right.indentedStr(6));
            //            }

            return SplitNode(left, split.leftFocus,
                             right, split.rightFocus)
        }

        override fun numChildren(): Int {
            return nodes.size
        }

        override fun pushFocus(index: Int, oldFocus: Array<T?>): Node<T> {
            // TODO: Review this entire method.
            var subNodeIndex = subNodeIndex(index)
            val subNode = nodes[subNodeIndex]
            val subNodeAdjustedIndex = subNodeAdjustedIndex(index, subNodeIndex)

            // 1st choice: insert into the subNode if it has enough space enough to handle it
            if (subNode.hasRelaxedCapacity(subNodeAdjustedIndex, oldFocus.size)) {
                // Push the focus down to a lower-level node w. capacity.
                val newNode = subNode.pushFocus(subNodeAdjustedIndex, oldFocus)
                // Make a copy of our nodesArray, replacing the old node at subNodeIndex with the
                // new node
                return replaceInRelaxedAt(cumulativeSizes, nodes, newNode, subNodeIndex,
                                          oldFocus.size)
            }

            // I think this is a root node thing.
            if (!thisNodeHasRelaxedCapacity(1)) {
                // For now, split at half of size.
                val split = split()
                val max1 = split[0].size()
                val newRelaxed = Relaxed(intArrayOf(max1, max1 + split[1].size()), split)
                return newRelaxed.pushFocus(index, oldFocus)
            }

            if (subNode is Leaf<*>) {
                // Here we already know:
                //  - the leaf doesn't have capacity
                //  - We don't need to split ourselves
                // Therefore:
                //  - If the focus is big enough to be its own leaf and the index is on a leaf
                //    boundary and , make it one.
                //  - Else, insert into the array and replace one leaf with two.

                val newNodes: Array<out Node<T>>
                val newCumSizes: IntArray
                val numToSkip: Int

                //  If the focus is big enough to be its own leaf and the index is on a leaf
                // boundary, make it one.
                if (oldFocus.size >= MIN_NODE_LENGTH && (subNodeAdjustedIndex == 0 || subNodeAdjustedIndex == subNode.size())) {

                    // Insert-between
                    // Just add a new leaf
                    val newNode = Leaf(oldFocus)

                    // If we aren't inserting before the existing leaf node, we're inserting after.
                    if (subNodeAdjustedIndex != 0) {
                        subNodeIndex++
                    }

                    newNodes = insertIntoArrayAt(newNode, nodes, subNodeIndex, nodeClass())
                    // Increment newCumSizes for the changed item and all items to the right.
                    newCumSizes = IntArray(cumulativeSizes.size + 1)
                    var cumulativeSize = 0
                    if (subNodeIndex > 0) {
                        System.arraycopy(cumulativeSizes, 0, newCumSizes, 0, subNodeIndex)
                        cumulativeSize = newCumSizes[subNodeIndex - 1]
                    }
                    newCumSizes[subNodeIndex] = cumulativeSize + oldFocus.size
                    numToSkip = 1
                    //                    for (int i = subNodeIndex + 1; i < newCumSizes.length; i++) {
                    //                        newCumSizes[i] = cumulativeSizes[i - 1] + oldFocus.length;
                    //                    }
                } else {
                    // Grab the array from the existing leaf node, make the insert, and yield two
                    // new leaf nodes.
                    // Split-to-insert
                    val res:Array<Leaf<T>> = (subNode as Leaf<T>).spliceAndSplit(oldFocus, subNodeAdjustedIndex)
                    val leftLeaf:Leaf<T> = res[0]
                    val rightLeaf:Leaf<T> = res[1]

                    @Suppress("UNCHECKED_CAST")
                    newNodes = arrayOfNulls<Leaf<T>>(nodes.size + 1) as Array<Leaf<T>>

                    // Increment newCumSizes for the changed item and all items to the right.
                    newCumSizes = IntArray(cumulativeSizes.size + 1)
                    var leftSize = 0

                    // Copy nodes and cumulativeSizes before split
                    if (subNodeIndex > 0) {
                        //               src,srcPos,dest,destPos,length
                        System.arraycopy(nodes, 0, newNodes, 0, subNodeIndex)
                        //               src,   srcPos, dest,    destPos, length
                        System.arraycopy(cumulativeSizes, 0, newCumSizes, 0, subNodeIndex)

                        leftSize = cumulativeSizes[subNodeIndex - 1]
                    }

                    // Copy split nodes and cumulativeSizes
                    newNodes[subNodeIndex] = leftLeaf
                    newNodes[subNodeIndex + 1] = rightLeaf
                    leftSize += leftLeaf.size()
                    newCumSizes[subNodeIndex] = leftSize
                    newCumSizes[subNodeIndex + 1] = leftSize + rightLeaf.size()

                    if (subNodeIndex < nodes.size - 1) {
                        //               src,srcPos,dest,destPos,length
                        System.arraycopy(nodes, subNodeIndex + 1, newNodes, subNodeIndex + 2,
                                         nodes.size - subNodeIndex - 1)
                    }
                    numToSkip = 2
                }
                for (i in (subNodeIndex + numToSkip) until newCumSizes.size) {
                    newCumSizes[i] = cumulativeSizes[i - 1] + oldFocus.size
                }

                return Relaxed(newCumSizes, newNodes)
                // end if subNode instanceof Leaf
            } else if (subNode is Strict<*>) {
                // Convert Strict to Relaxed
                val relaxed = (subNode as Strict<T>).relax()
                val newNode = relaxed.pushFocus(subNodeAdjustedIndex, oldFocus)
                return replaceInRelaxedAt(cumulativeSizes, nodes, newNode, subNodeIndex,
                                          oldFocus.size)
            }

            // Here we have capacity and the full sub-node is not a leaf or strict, so we have to
            // split the appropriate sub-node.

            // For now, split at half of size.
            val newSubNode = (subNode as Relaxed<T>).split()

            val node1:Relaxed<T> = newSubNode[0]
            val node2:Relaxed<T> = newSubNode[1]
            val newNodes = genericNodeArray<Relaxed<T>>(nodes.size + 1)

            // If we aren't inserting at the first item, array-copy the nodes before the insert
            // point.
            if (subNodeIndex > 0) {
                System.arraycopy(nodes, 0, newNodes, 0, subNodeIndex)
            }

            // Insert the new item.
            @Suppress("UNCHECKED_CAST")
            (newNodes as Array<Node<T>>)[subNodeIndex] = node1
            newNodes[subNodeIndex + 1] = node2

            // If we aren't inserting at the last item, array-copy the nodes after the insert
            // point.
            if (subNodeIndex < nodes.size) {
                System.arraycopy(nodes, subNodeIndex + 1, newNodes, subNodeIndex + 2,
                                 nodes.size - subNodeIndex - 1)
            }

            val newCumSizes = IntArray(cumulativeSizes.size + 1)
            var cumulativeSize = 0
            if (subNodeIndex > 0) {
                System.arraycopy(cumulativeSizes, 0, newCumSizes, 0, subNodeIndex)
                cumulativeSize = cumulativeSizes[subNodeIndex - 1]
            }

            for (i in subNodeIndex until newCumSizes.size) {
                // TODO: Calculate instead of loading into memory.  See splitAt calculation above.
                cumulativeSize += newNodes[i].size()
                newCumSizes[i] = cumulativeSize
            }

            val newRelaxed = Relaxed(newCumSizes, newNodes)
            //            debug("newRelaxed2:\n" + newRelaxed.indentedStr(0));

            return newRelaxed.pushFocus(index, oldFocus)
            //            debug("Parent after:" + after.indentedStr(0));
        }

        override fun replace(idx: Int, t: T?): Node<T> {
            val subNodeIndex = subNodeIndex(idx)
            val alteredNode = nodes[subNodeIndex].replace(subNodeAdjustedIndex(idx, subNodeIndex), t)
            val newNodes = replaceInArrayAt(alteredNode, nodes, subNodeIndex, nodeClass())
            return Relaxed(cumulativeSizes, newNodes)
        }

        override fun indentedStr(indent: Int): String {
            val sB = StringBuilder() // indentSpace(indent)
                    .append("Relaxed(")
            val nextIndent = indent + sB.length
            sB.append("cumulativeSizes=").append(arrayString(cumulativeSizes)).append("\n")
                    .append(indentSpace(nextIndent)).append("nodes=[")
            // + 6 for "nodes="
            return showSubNodes(sB, nodes, nextIndent + 7)
                    .append("])")
                    .toString()
        }

        override fun toString(): String {
            return indentedStr(0)
        }

        companion object {

            /**
             * This asks each node what size it is, then puts the cumulative sizes into a new array.
             * In theory, it might be faster to figure out what side we added/removed nodes and do
             * some addition/subtraction (in the amount of the added/removed nodes).  But I want to
             * optimize other things first and be sure everything is correct before experimenting with
             * that.  After all, it might not even be faster!
             * @param newNodes the nodes to take sizes from.
             * @return An array of cumulative sizes of each node in the passed array.
             */
            private fun makeSizeArray(newNodes: Array<out Node<*>>): IntArray {
                val newCumSizes = IntArray(newNodes.size)
                var cumulativeSize = 0
                for (i in newCumSizes.indices) {
                    cumulativeSize += newNodes[i].size()
                    newCumSizes[i] = cumulativeSize
                }
                return newCumSizes
            }

            // TODO: Search for more opportunities to use this
            /**
             * Replace a node in a relaxed node by recalculating the cumulative sizes and copying
             * all sub nodes.
             * @param ints original cumulative sizes
             * @param ns original nodes
             * @param newNode replacement node
             * @param subNodeIndex index to replace in this node's immediate children
             * @param insertSize the difference in size between the original node and the new node.
             * @return a new immutable Relaxed node with the immediate child node replaced.
             */
            internal fun <T> replaceInRelaxedAt(ints: IntArray, ns: Array<out Node<T>>, newNode: Node<T>,
                                                subNodeIndex: Int, insertSize: Int): Relaxed<T> {
                val newNodes = replaceInArrayAt(newNode, ns, subNodeIndex, nodeClass())
                // Increment newCumSizes for the changed item and all items to the right.
                val newCumSizes = IntArray(ints.size)
                if (subNodeIndex > 0) {
                    System.arraycopy(ints, 0, newCumSizes, 0, subNodeIndex)
                }
                for (i in subNodeIndex until ints.size) {
                    newCumSizes[i] = ints[i] + insertSize
                }
                return Relaxed(newCumSizes, newNodes)
            }

            /**
             * Insert a node in a relaxed node by recalculating the cumulative sizes and copying
             * all sub nodes.
             * @param oldCumSizes original cumulative sizes
             * @param ns original nodes
             * @param newNode replacement node
             * @param subNodeIndex index to insert in this node's immediate children
             * @return a new immutable Relaxed node with the immediate child node inserted.
             */
            internal fun <T> insertInRelaxedAt(oldCumSizes: IntArray, ns: Array<out Node<T>>, newNode: Node<T>,
                                               subNodeIndex: Int): Relaxed<T> {
                val newNodes = insertIntoArrayAt(newNode, ns, subNodeIndex, nodeClass())

                val oldLen = oldCumSizes.size
                //            if (subNodeIndex > oldLen) {
                //                throw new IllegalStateException("subNodeIndex > oldCumSizes.length");
                //            }

                val newCumSizes = IntArray(oldLen + 1)
                // Copy unchanged cumulative sizes
                if (subNodeIndex > 0) {
                    System.arraycopy(oldCumSizes, 0, newCumSizes, 0, subNodeIndex)
                }
                // insert the cumulative size of the new node
                val newNodeSize = newNode.size()
                // Find cumulative size of previous node
                val prevNodeTotal = if (subNodeIndex == 0)
                    0
                else
                    oldCumSizes[subNodeIndex - 1]

                newCumSizes[subNodeIndex] = newNodeSize + prevNodeTotal

                for (i in subNodeIndex until oldCumSizes.size) {
                    newCumSizes[i + 1] = oldCumSizes[i] + newNodeSize
                }
                return Relaxed(newCumSizes, newNodes)
            }

            /**
             * Fixes up nodes on the right-hand side of the split.  Might want to explain this better...
             *
             * @param origNodes the immediate children of the node we're splitting
             * @param splitRight the pre-split right node
             * @param subNodeIndex the index to split children at?
             * @return a copy of this node with only the right-hand side of the split.
             */
            fun <T> fixRight(origNodes: Array<out Node<T>>, splitRight: Node<T>,
                             subNodeIndex: Int): Node<T> {
                //            if ( (splitRight.size() > 0) &&
                //                 (origNodes[0].height() != splitRight.height()) ) {
                //                throw new IllegalStateException("Passed a splitRight node of a different height" +
                //                                                " than the origNodes!");
                //            }
                val right: Node<T>
                if (subNodeIndex == origNodes.size - 1) {
                    //                right = splitRight;
                    right = Relaxed(intArrayOf(splitRight.size()), arrayOf(splitRight))
                } else {
                    val haveRightSubNode = splitRight.size() > 0
                    // If we have a rightSubNode, it's going to need a space in our new node array.
                    val numRightNodes = origNodes.size - subNodeIndex - if (haveRightSubNode) 0 else 1
                    // Here the first (leftmost) node of the right-hand side was turned into the focus
                    // and we have additional right-hand origNodes to adjust the parent for.
                    val rightCumSizes = IntArray(numRightNodes)
                    val rightNodes = genericNodeArray<T>(numRightNodes)

                    var cumulativeSize = 0
                    var destCopyStartIdx = 0

                    if (haveRightSubNode) {
                        //                 src,       srcPos,          dest, destPos, length
                        System.arraycopy(origNodes, subNodeIndex + 1, rightNodes, 1, numRightNodes - 1)

                        @Suppress("UNCHECKED_CAST")
                        (rightNodes as Array<Node<T>>)[0] = splitRight
                        cumulativeSize = splitRight.size()
                        rightCumSizes[0] = cumulativeSize
                        destCopyStartIdx = 1
                    } else {
                        //                 src,       srcPos,          dest, destPos, length
                        System.arraycopy(origNodes, subNodeIndex + 1, rightNodes, 0, numRightNodes)
                    }

                    // For relaxed nodes, we could calculate from previous cumulativeSizes instead of
                    // calling .size() on each one.  For strict, we could just add a strict amount.
                    // For now, this works.
                    for (i in destCopyStartIdx until numRightNodes) {
                        cumulativeSize += rightNodes[i].size()
                        rightCumSizes[i] = cumulativeSize
                    }

                    right = Relaxed(rightCumSizes, rightNodes)
                    //                right.debugValidate();
                }
                return right
            } // end fixRight()
        }
    } // end class Relaxed

    // =================================== Tree-walking Iterator ==================================

    /** Holds a node and the index of the child node we are currently iterating in.  */
    private class IdxNode<E>(internal val node: Node<E>) {
        internal var idx = 0
        operator fun hasNext(): Boolean {
            return idx < node.numChildren()
        }

        operator fun next(): Node<E> {
            return node.child(idx++)
        }
//        override fun toString() = "IdxNode($idx $node)"
    }

    // Focus must be pre-pushed so we don't have to ever check the index.
    internal inner class Iter(root: Node<E>) : UnmodSortedIterator<E> {

        // We want this iterator to walk the node tree.
        @Suppress("UNCHECKED_CAST")
        private val stack: Array<IdxNode<E>> = arrayOfNulls<IdxNode<E>>(root.height()) as Array<IdxNode<E>>
        private var stackMaxIdx = -1

        private var leafArray = findLeaf(root)
        private var leafArrayIdx: Int = 0

        // Descent to the leftmost unused leaf node.
        private fun findLeaf(node: Node<E>): Array<E?> {
            var n = node
            // Descent to left-most bottom node.
            while (n !is Leaf<*>) {
                val idxNode = IdxNode(n)
                // Add indexNode to ancestor stack
                stack[++stackMaxIdx] = idxNode
//                println("Stack: " + Arrays.toString(stack))
                n = idxNode.next()
            }
            return (n as Leaf<E>).items
        }

        private fun nextLeafArray(): Array<E?> {
            // While nodes are used up, get next node from node one level up.
            while (stackMaxIdx > -1 && !stack[stackMaxIdx].hasNext()) {
                stackMaxIdx--
            }

            return if (stackMaxIdx < 0) {
                emptyArray()
            } else findLeaf(stack[stackMaxIdx].next())
            // If node one level up is used up, find a node that isn't used up and descend to its
            // leftmost leaf.
        }

        override fun hasNext(): Boolean {
            if (leafArrayIdx < leafArray.size) {
                return true
            }
            //            if (leafArray.length == 0) { return false; }
            leafArray = nextLeafArray()
            leafArrayIdx = 0
            return leafArray.isNotEmpty()
        }

        override fun next(): E? {
            // If there's no more in this leaf array, get the next one
            if (leafArrayIdx >= leafArray.size) {
                leafArray = nextLeafArray()
                leafArrayIdx = 0
            }
            // Return the next item in the leaf array and increment index
            return leafArray[leafArrayIdx++]
        }
    }

    companion object {

        @Suppress("UNCHECKED_CAST")
        private val nodeClassVal = Node::class.java

        @Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
        inline internal fun <T> nodeClass() = nodeClassVal as java.lang.Class<Node<T>>

        /** Returns the empty, immutable RRB-Tree (there is only one)  */
        @Suppress("UNCHECKED_CAST")
        fun <T> empty(): ImRrbt<T> = ImRrbt.EMPTY_IM_RRBT as ImRrbt<T>

        /** Returns the empty, mutable RRB-Tree (there is only one)  */
        @Suppress("UNCHECKED_CAST")
        fun <T> emptyMutable() = empty<Any>().mutable() as MutRrbt<T>

        private fun <E> eliminateUnnecessaryAncestors(n: Node<E>): Node<E> {
            var node = n
            while (node !is Leaf<*> && node.numChildren() == 1) {
                node = node.child(0)
            }
            return node
        }

        private fun <E> addAncestor(n: Node<E>): Node<E> {
            return if (n is Leaf<E> && n.size() == STRICT_NODE_LENGTH)
                Strict(NODE_LENGTH_POW_2, n.size(), arrayOf(n))
            else if (n is Strict<*>)
                Strict((n as Strict<E>).shift + NODE_LENGTH_POW_2, n.size(), arrayOf(n))
            else
                Relaxed(intArrayOf(n.size()), arrayOf(n))
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
        private val NODE_LENGTH_POW_2 = 5 // 2 for testing, 5 for real

        // 0b00000000000000000000000000100000 = 0x20 = 32
        internal val STRICT_NODE_LENGTH = 1 shl NODE_LENGTH_POW_2

        private val HALF_STRICT_NODE_LENGTH = STRICT_NODE_LENGTH shr 1

        // (MIN_NODE_LENGTH + MAX_NODE_LENGTH) / 2 should equal STRICT_NODE_LENGTH so that they have the
        // same average node size to make the index interpolation easier.
        private val MIN_NODE_LENGTH = (STRICT_NODE_LENGTH + 1) * 2 / 3
        // Always check if less-than this.  Never less-than-or-equal.  Cormen adds a -1 here and tests
        // for <= (I think!).
        private val MAX_NODE_LENGTH = (STRICT_NODE_LENGTH + 1) * 4 / 3

        private val EMPTY_LEAF = Leaf(EMPTY_ARRAY)
        @Suppress("UNCHECKED_CAST")
        private fun <T> emptyLeaf() = EMPTY_LEAF as Leaf<T>

        // =================================== Array Helper Functions ==================================
        // Helper function to avoid type warnings.

        @Suppress("UNCHECKED_CAST")
        private fun <T> genericNodeArray(size: Int) =
                arrayOfNulls<Node<T>>(size) as Array<out Node<T>>

        // =============================== Debugging and pretty-printing ===============================

        private fun showSubNodes(sB: StringBuilder, items: Array<out Any>, nextIndent: Int): StringBuilder {
            var isFirst = true
            for (n in items) {
                if (isFirst) {
                    isFirst = false
                } else {
                    //                sB.append(" ");
                    if (items[0] is Leaf<*>) {
                        sB.append(" ")
                    } else {
                        sB.append("\n").append(indentSpace(nextIndent))
                    }
                }
                sB.append((n as Node<*>).indentedStr(nextIndent))
            }
            return sB
        }
    }

} // end class RrbTree
