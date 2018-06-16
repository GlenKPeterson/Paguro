// Copyright 2014-08-30 PlanBase Inc. & Glen Peterson
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

package org.organicdesign.fp.xform

import org.organicdesign.fp.collections.UnmodIterable
import org.organicdesign.fp.collections.UnmodIterator
import org.organicdesign.fp.oneOf.Or
import org.organicdesign.fp.xform.Xform.Companion.AppendOp
import org.organicdesign.fp.xform.Xform.Companion.EMPTY_XFORM
import org.organicdesign.fp.xform.Xform.Companion.OpStrategy
import org.organicdesign.fp.xform.Xform.Companion.RunList
import org.organicdesign.fp.xform.Xform.Companion.SourceProviderIterableDesc
import java.io.Serializable

/**
 An immutable description of operations to be performed (a transformation, transform, or x-form).
 When fold() (or another terminating function) is called, the Xform definition is "compiled" into
 a one-time mutable transformation which is then carried out.  This allows certain performance
 shortcuts (such as doing a drop with index addition instead of iteration) and also hides the
 mutability otherwise inherent in a transformation.

 Xform is an abstract class.  Most of the methods on Xform produce immutable descriptions of actions
 to take at a later time.  These are represented by ___Desc classes.  When fold() is called
 (or any of the helper methods that wrap it), that produces a result by first stringing together
 a bunch of Operations (____Op classes) and then "running" them.  This is analogous to compiling
 a program and running it.  The ____Desc classes are like the immutable source, the ____Op classes
 like the op-codes it's compiled into.

 Special thanks to Nathan Williams for pointing me toward separating the mutation from the
 description of a transformation.  Also to Paul Phillips (@extempore2) whose lectures provided
 an outline for what was ideal and also what was important.  All errors are my own.
 -Glen 2015-08-30
 *
 * @param prevOp the previous operation or source
 */
abstract class Xform<A>(internal val prevOp: Xform<Any>?) : UnmodIterable<A> {

    companion object {

        enum class OpStrategy { HANDLE_INTERNALLY, ASK_SUPPLIER, CANNOT_HANDLE }

        private val TERMINATE = Any()

        @Suppress("UNCHECKED_CAST")
        private fun <A> terminate(): A = TERMINATE as A

        /**
         These are mutable operations that the transform carries out when it is run.  This is like the
         compiled "op codes" in contrast to the Xform is like the immutable "source code" of the
         transformation description.
         */
        // Time using a linked list of ops instead of array, so that we can easily remove ops from
        // the list when they are used up.
        internal abstract class Operation(open var filter: ((Any?) -> Boolean)? = null,
                                          open var map: ((Any) -> Any)? = null,
                                          open var flatMap: ((Any) -> Iterable<*>)? = null) {

            /**
             Drops as many items as the source can handle.
             @param num the number of items to drop
             @return  whether the source can handle the take, or pass-through (ask-supplier), or can't
             do either.
             */
            open fun drop(num: Long): Or<Long, OpStrategy> =
                    if (num < 1) {
                        Or.good(0L)
                    } else {
                        Or.bad(OpStrategy.CANNOT_HANDLE)
                    }

            /**
             Takes as many items as the source can handle.
             @param num the number of items to take.
             @return whether the source can handle the take, or pass-through (ask-supplier), or can't
             do either.
             */
            open fun take(num: Long): OpStrategy = OpStrategy.CANNOT_HANDLE

            /**
             * We need to model this as a separate op for when the previous op is CANNOT_HANDLE.  It is
             * coded as a filter, but still needs to be modeled separately so that subsequent drops can be
             * combined into the earliest single explicit drop op.  Such combinations are additive,
             * meaning that drop(3).drop(5) is equivalent to drop(8).
             */
            internal class DropOp(private var leftToDrop: Long = 0) : Operation() {

                override var filter: ((Any?) -> Boolean)? =
                        { _ ->
                            if (leftToDrop > 0) {
                                leftToDrop -= 1
                                java.lang.Boolean.FALSE
                            } else {
                                java.lang.Boolean.TRUE
                            }
                        }

                override fun drop(num: Long): Or<Long, OpStrategy> {
                    leftToDrop += num
                    return Or.good(num)
                }
            }

            internal class FilterOp(filter: (Any?) -> Boolean) : Operation(filter = filter)

            internal class MapOp(map: (Any) -> Any) : Operation(map = map) {
                override fun drop(num: Long): Or<Long, OpStrategy> = Or.bad(OpStrategy.ASK_SUPPLIER)
                override fun take(num: Long): OpStrategy = OpStrategy.ASK_SUPPLIER
            }

            // TODO: FlatMap should drop and take internally using addition/subtraction on each output
            // TODO: list instead of testing each list item individually.
            internal class FlatMapOp(flatMap: ((Any) -> Iterable<Any>)?) : Operation(flatMap = flatMap)
            //            ListSourceDesc<U> cache = null;
            //            int numToDrop = 0;

            /**
             * We need to model this as a separate op for when the previous op is CANNOT_HANDLE.  It is
             * coded as a map, but still needs to be modeled separately so that subsequent takes can be
             * combined into the earliest single explicit take op.  Such combination is a pick-least of
             * all the takes, meaning that take(5).take(3) is equivalent to take(3).
             */
            internal class TakeOp(private var numToTake: Long = 0) : Operation() {


                    override var map: ((Any) -> Any)? =
                            { a ->
                                if (numToTake > 0) {
                                    numToTake -= 1
                                    a
                                } else {
                                    TERMINATE
                                }
                            }

                override fun take(num: Long): OpStrategy {
                    // This data condition is prevented in Xform.take()
                    //                if (num < 0) {
                    //                    throw new IllegalArgumentException("Can't take less than 0 items.");
                    //                }
                    if (num < numToTake) {
                        numToTake = num
                    }
                    return OpStrategy.HANDLE_INTERNALLY
                }
            }
        } // end class Operation

        /**
         * A RunList is a list of Operations "compiled" from an Xform.  It contains an Iterable data
         * source (or some day and array source or List source) and a List of Operation op-codes.
         *
         * A RunList is also a SourceProvider, since the output of one transform can be the input to
         * another.  FlatMap is implemented that way.  Notice that there are almost no generic types used
         * here: Since the input could be one type, and each map or flatmap operation could change that to
         * another type.
         *
         * For speed, we ignore all that in the "compiled" version and just use Objects and avoid any
         * wrapping or casting.
         */
        open class RunList(internal var prev: RunList? = null,
                           internal var source: Iterable<Any>) : Iterable<Any> {
            internal open var list: MutableList<Operation> = mutableListOf()
            //        RunList next = null;
            //            if (prv != null) { prv.next = ret; }
            //            return ret;

            internal open fun opArray(): Array<Operation> = list.toTypedArray()

            override fun iterator(): Iterator<Any> = source.iterator()
        }

        /**
         * When iterator() is called, the AppendOp processes the previous source and operation into
         * an MutableList.  Then yields an iterator that yield the result of that operation until it runs
         * out.  Then continues to yield the appended items until they run out, at which point hasNext()
         * returns false;
         */
        private class AppendOp(prev: RunList, src: Iterable<Any>) : RunList(prev, src) {

            override fun iterator(): Iterator<Any> {
                val prevSrc = _fold(prev, prev!!.opArray(), 0, mutableListOf(),
                                    { res: MutableList<Any>, item: Any? ->
                                        res.add(item!!)
                                        res
                                    })

                return object : Iterator<Any> {
                    internal var innerIter: Iterator<Any> = prevSrc.iterator()
                    internal var usingPrevSrc = true
                    /** {@inheritDoc}  */
                    override operator fun hasNext(): Boolean {
                        if (innerIter.hasNext()) {
                            return true
                        } else if (usingPrevSrc) {
                            usingPrevSrc = false
                            innerIter = source.iterator()
                        }
                        return innerIter.hasNext()
                    }

                    override operator fun next(): Any {
                        return innerIter.next()
                    }
                }
            } // end iterator()
        }

        /** Describes an concat() operation, but does not perform it.  */
        @Suppress("UNCHECKED_CAST")
        internal class AppendIterDesc<T>(prev: Xform<T>, internal val src: Xform<T>) : Xform<T>(prev as Xform<Any>) {

            override fun toRunList(): RunList = AppendOp(prevOp!!.toRunList(), src as Iterable<Any>)
        }

        /**
         * Describes a "drop" operation.  Drops will be pushed as early in the operation-list as possible,
         * ideally being done using one-time pointer addition on the source.
         *
         * I have working source-pointer-addition code, but it added a fair amount of complexity to
         * implement it for Lists and arrays, but not for Iterables in general, so it is not currently
         * (2015-08-21) part of this implementation.
         *
         * When source-pointer-addition is not possible, a Drop op-code is created (implemented as a
         * filter function).  Subsequent drop ops will be combined into the earliest drop (for speed).
         * @param <T> the expected input type to drop.
        </T> */
        @Suppress("UNCHECKED_CAST")
        internal class DropDesc<T>(prev: Xform<T>,
                                   private val dropAmt: Long) : Xform<T>(prev as Xform<Any>) {

            override fun toRunList(): RunList {
                //                System.out.println("in toRunList() for drop");
                val ret = prevOp!!.toRunList()
                var i = ret.list.size - 1
                //              System.out.println("\tchecking previous items to see if they can handle a drop...");
                var earlierDs: Or<Long, OpStrategy>
                while (i >= 0) {
                    val op = ret.list[i]
                    earlierDs = op.drop(dropAmt)
                    if (earlierDs.isBad && earlierDs.bad() == OpStrategy.CANNOT_HANDLE) {
                        //                        System.out.println("\tNone can handle a drop...");
                        break
                    } else if (earlierDs.isGood) {
                        //                        System.out.println("\tHandled internally by " + opRun);
                        return ret
                    }
                    i--
                }
                //            if ( !Or.bad(OpStrategy.CANNOT_HANDLE).equals(earlierDs) && (i <= 0) ) {
                //                Or<Long,OpStrategy> srcDs = ret.source.drop(dropAmt);
                //                if (srcDs.isGood()) {
                //                    if (srcDs.good() == dropAmt) {
                ////                        System.out.println("\tHandled internally by source: " + ret.source);
                //                        return ret;
                //                    } else {
                //                        // TODO: Think about this and implement!
                //                        throw new UnsupportedOperationException("Not implemented yet!");
                //                    }
                //                }
                //            }
                //                System.out.println("\tSource could not handle drop.");
                //                System.out.println("\tMake a drop for " + dropAmt + " items.");
                ret.list.add(Operation.DropOp(dropAmt))
                return ret
            }
        }

        /** Describes a dropWhile() operation (implemented as a filter), but does not perform it.  */
        @Suppress("UNCHECKED_CAST")
        internal class DropWhileDesc<T>(prev: Xform<T>,
                                        internal val f: (Any) -> Boolean) : Xform<T>(prev as Xform<Any>) {

            override fun toRunList(): RunList {
                val ret = prevOp!!.toRunList()
                ret.list.add(Operation.FilterOp(object : (Any?) -> Boolean {
                    // Starts out active (meaning dropping items until the inner function returns true).
                    // Once inner function returns true, switches into passive mode in which this (outer)
                    // function always returns true.
                    // There are probably more efficient ways to do this, but I'm going for correct first.
                    private var active = true

                    override fun invoke(o: Any?): Boolean =
                            if (!active) {
                                true
                            } else {
                                val r = !f(o!!)
                                if (r) {
                                    active = false
                                }
                                r
                            }
                }))
                return ret
            }
        }

        /** Describes a allowWhere() operation, but does not perform it.  */
        @Suppress("UNCHECKED_CAST")
        internal class FilterDesc<T>(prev: Xform<T>,
                                     internal val f: (T) -> Boolean) : Xform<T>(prev as Xform<Any>) {

            override fun toRunList(): RunList {
                val ret = prevOp!!.toRunList()
                @Suppress("UNCHECKED_CAST")
                ret.list.add(Operation.FilterOp(f as (Any?) -> Boolean))
                return ret
            }
        }

        /** Describes a map() operation, but does not perform it.  */
        @Suppress("UNCHECKED_CAST")
        internal class MapDesc<T, U>(prev: Xform<T>,
                                     internal val f: (T) -> U) : Xform<U>(prev as Xform<Any>) {

            override fun toRunList(): RunList {
                val ret = prevOp!!.toRunList()
                @Suppress("UNCHECKED_CAST")
                ret.list.add(Operation.MapOp(f as (Any) -> Any))
                return ret
            }
        }

        /** Describes a flatMap() operation, but does not perform it.  */
        @Suppress("UNCHECKED_CAST")
        internal class FlatMapDesc<T, U>(prev: Xform<T>,
                                         internal val f: (T) -> Iterable<U>) : Xform<U>(prev as Xform<Any>) {

            override fun toRunList(): RunList {
                val ret = prevOp!!.toRunList()
                ret.list.add(Operation.FlatMapOp(f as (Any) -> Iterable<Any>))
                return ret
            }
        }

        /**
         * Describes a "take" operation, but does not perform it.  Takes will be pushed as early in the
         * operation-list as possible, ideally being done using one-time pointer addition on the source.
         * When source pointer addition is not possible, a Take op-code is created (implemented as a
         * filter function).  Subsequent take ops will be combined into the earliest take (for speed).
         * @param <T> the expected input type to take.
        </T> */
        @Suppress("UNCHECKED_CAST")
        internal class TakeDesc<T>(prev: Xform<T>,
                                   private val take: Long) : Xform<T>(prev as Xform<Any>) {

            override fun toRunList(): RunList {
                //                System.out.println("in toRunList() for take");
                val ret = prevOp!!.toRunList()
                var i = ret.list.size - 1
                //              System.out.println("\tchecking previous items to see if they can handle a take...");
                var earlierTs: OpStrategy
                while (i >= 0) {
                    val op = ret.list[i]
                    earlierTs = op.take(take)
                    if (earlierTs == OpStrategy.CANNOT_HANDLE) {
                        //                        System.out.println("\tNone can handle a take...");
                        break
                    } else if (earlierTs == OpStrategy.HANDLE_INTERNALLY) {
                        //                        System.out.println("\tHandled internally by " + opRun);
                        return ret
                    }
                    i--
                }
                //            if ( (earlierTs != OpStrategy.CANNOT_HANDLE) && (i <= 0) ) {
                //                OpStrategy srcDs = ret.source.take(take);
                //                if (srcDs == OpStrategy.HANDLE_INTERNALLY) {
                ////                        System.out.println("\tHandled internally by source: " + ret.source);
                //                    return ret;
                //                }
                //            }
                //                System.out.println("\tSource could not handle take.");
                //                System.out.println("\tMake a take for " + take + " items.");
                ret.list.add(Operation.TakeOp(take))
                return ret
            }
        }

        // TODO: Circular definition with emptyXform()!
        internal open class SourceProviderIterableDesc<T>(private val list: Iterable<T>) : Xform<T>(emptyXform()) {
            @Suppress("UNCHECKED_CAST")
            override fun toRunList(): RunList = RunList(null, list as Iterable<Any>)

            override fun hashCode(): Int = UnmodIterable.hash(this)

            override fun equals(other: Any?): Boolean {
                if (this === other) {
                    return true
                }

                return other is SourceProviderIterableDesc<*> &&
                       this.list == other.list
            }
        }

        //    /** Static factory methods */
        //    @SafeVarargs
        //    public static <T> Xform<T> ofArray(T... list) {
        //        return new SourceProviderIterableDesc<>(Arrays.asList(list));
        //    }

//        private val EMPTY_LEAF = RrbTree.Leaf(EMPTY_ARRAY)
//        @Suppress("UNCHECKED_CAST")
//        private fun <T> emptyLeaf() = EMPTY_LEAF as RrbTree.Leaf<T>

    // internal object EmptyList : List<Nothing>, Serializable, RandomAccess {
    //     private const val serialVersionUID: Long = -7390468764508069838L
    //
    //     override fun equals(other: Any?): Boolean = other is List<*> && other.isEmpty()
    //     override fun hashCode(): Int = 1
    //     override fun toString(): String = "[]"
    //     ...
    // }
    // public fun <T> emptyList(): List<T> = EmptyList
        // SourceProviderIterableDesc(emptyList())
//    private object EMPTY_XFORM: SourceProviderIterableDesc<Nothing>(emptyList()), Serializable {
//        private const val serialVersionUID: Long = 20180610174300L
//        override fun hashCode(): Int = 1
//        private fun readResolve(): Any = EMPTY_XFORM
//    }

        private object EMPTY_XFORM: Xform<Nothing>(null), Serializable {
            @Suppress("unused")
            private const val serialVersionUID: Long = 20180610174300L

            override fun toRunList(): Companion.RunList = RunList(null, emptyList())

            override fun hashCode(): Int = 0

            override fun equals(other: Any?): Boolean =
                    other is Xform<*> &&
                    other.prevOp == null

            //        override fun hashCode(): Int = 1
            private fun readResolve(): Any = EMPTY_XFORM
        }

    @Suppress("UNCHECKED_CAST")
    internal fun <T> emptyXform(): Xform<T> = EMPTY_XFORM as Xform<T>

        fun <T> of(list: Iterable<T>?): Xform<T> =
                if (list == null) {
                    emptyXform()
                } else {
                    SourceProviderIterableDesc(list)
                }

        // TODO: Everything should be implemented in terms of foldUntil now that we have that.
        /**
         @param reducer combines each value in the list with the result so far.  The result so far is the first argument.
         the current value to combine with it is the second argument.  The return type is the same as the result so far.
         Fn2&lt;? super U,? super T,U&gt;
         */
        // This is the main method of this whole file.  Everything else lives to serve this.
        // We used a linked-list to build the type-safe operations so if that code compiles, the types
        // should work out here too.  However, for performance, we don't want to be stuck creating and
        // passing Options around, nor do we want a telescoping stack of hasNext() and next() calls.
        // So abandon type safety, store all the intermediate results as Objects, and use loops and
        // sentinel values to break out or skip processing as appropriate.  Initial tests indicate this
        // is 2.6 times faster than wrapping items type-safely in Options and 10 to 100 times faster
        // than lazily evaluated and cached linked-list, Sequence model.
        private fun <H> _fold(
                source: Iterable<*>?,
                ops: Array<Operation>,
                opIdx: Int,
                ident: H,
                reducer: (H, Any?) -> H): H {

            var ret: Any = ident as Any

            // This is a label - the first one I have used in Java in years, or maybe ever.
            // I'm assuming this is fast, but will have to test to confirm it.
            sourceLoop@ for (item in source!!) {
                var o:Any? = item
                for (j in opIdx until ops.size) {
                    val op = ops[j]
                    if (op.filter != null && !op.filter!!(o)) {
                        // stop processing this source item and go to the next one.
                        continue@sourceLoop
                    }
                    if (op.map != null) {
                        o = op.map!!(o!!)
                        // This is how map can handle takeWhile, take, and other termination marker
                        // roles.  Remember, the fewer functions we have to check for, the faster this
                        // will execute.
                        if (o === TERMINATE) {
                            @Suppress("UNCHECKED_CAST")
                            return ret as H
                        }
                    } else if (op.flatMap != null) {
                        @Suppress("UNCHECKED_CAST")
                        ret = _fold(op.flatMap?.invoke(o!!), ops, j + 1, ret as H, reducer) as Any
                        // stop processing this source item and go to the next one.
                        continue@sourceLoop
                    }
                    //                    if ( (op.terminate != null) && op.terminate.invoke(o) ) {
                    //                        return (G) ret;
                    //                    }
                }
                // Here, the item made it through all the operations.  Combine it with the result.
                @Suppress("UNCHECKED_CAST")
                ret = reducer(ret as H, o) as Any
            }
            @Suppress("UNCHECKED_CAST")
            return ret as H
        } // end _fold();
    }

    // TODO: I had a really fast array-list implementation that I could probably hack into this for performance (assuming it actually works).
    override fun iterator(): UnmodIterator<A> = toMutList().iterator()

    // =============================================================================================
    // These will come from Transformable, but (will be) overridden to have a different return type.

    //    public Xform<A> concatList(List<? extends A> list) {
    //        if ( (list == null) || (list.size() < 1) ) { return this; }
    //        return concat(list);
    //    }

    @Suppress("UNCHECKED_CAST")
    override fun concat(iterable: Iterable<A>): Xform<A> =
            AppendIterDesc(this as Xform<Any>, SourceProviderIterableDesc(iterable as Iterable<Any>)) as Xform<A>

    //    @SafeVarargs
    //    public final Xform<A> concatArray(A... list) {
    //        if ( (list == null) || (list.length < 1) ) { return this; }
    //        return concat(Arrays.asList(list));
    //    }

    //    public Xform<A> precatList(List<? extends A> list) {
    //        if ( (list == null) || (list.size() < 1) ) { return this; }
    //        return precat(list);
    //    }

    @Suppress("UNCHECKED_CAST")
    override fun precat(list: Iterable<A>): Xform<A> =
            AppendIterDesc(of(list) as Xform<Any>, this as Xform<Any>) as Xform<A>

    //    @SafeVarargs
    //    public final Xform<A> precatArray(A... list) {
    //        if ( (list == null) || (list.length < 1) ) { return this; }
    //        return precat(Arrays.asList(list));
    //    }

    /** The number of items to drop from the beginning of the output.  */
    override fun drop(numItems: Long): Xform<A> {
        if (numItems < 0) {
            throw IllegalArgumentException("Can't drop less than zero items.")
        }
        @Suppress("UNCHECKED_CAST")
        return DropDesc(this as Xform<Any>, numItems) as Xform<A>
    }

    /** The number of items to drop from the beginning of the output.  */
    override fun dropWhile(predicate: ((A) -> Boolean)): Xform<A> =
            @Suppress("UNCHECKED_CAST")
            DropWhileDesc(this, predicate as (Any) -> Boolean)

    /** Provides a way to collect the results of the transformation.  */
    override fun <B> fold(accum: B, reducer: (B, A) -> B): B {
        // Construct an optimized array of OpRuns (mutable operations for this run)
        val runList = toRunList()
        @Suppress("UNCHECKED_CAST")
        return _fold(runList, runList.opArray(), 0, accum, reducer as (B, Any?) -> B)
    }

    /**
     * Thit implementation should be correct, but could be slow in the case where previous operations
     * are slow and the terminateWhen operation is fast and terminates early.  It actually renders
     * items to a mutable List, then runs through the list performing the requested reduction,
     * checking for early termination on the result.  If you can to a takeWhile() or take() earlier
     * in the transform chain instead of doing it here, always do that.  If you really need early
     * termination based on the *result* of a fold, and the operations are expensive or the input
     * is huge, try using a View instead.  If you don't care about those things, then this method is
     * perfect for you.
     *
     * If you are tempted to call this function with a null terminator, please call [fold] instead.
     *
     * {@inheritDoc}
     */
    override fun <G, B> foldUntil(accum: G,
                                  terminator: (G, A) -> B?,
                                  reducer: (G, A) -> G): Or<G, B> {
        var g:G = accum

        // Yes, this is a cheap plastic imitation of what you'd hope for if you really need this
        // method.  The trouble is that when I implemented it correctly in _fold, I found
        // it was going to be incredibly difficult, or more likely impossible to implement
        // when the previous operation was flatMap, since you don't have the right result type to
        // check against when you recurse in to the flat mapping function, and if you check the
        // return from the recursion, it may have too many elements already.
        // In XformTest.java, there's something marked "Early termination test" that illustrates
        // this exact problem.
        val items:MutableList<A> = this.toMutList()
        for (item:A in items) {
            val term: B? = terminator(g, item)
            if (term != null) {
                return Or.bad(term)
            }
            g = reducer(g, item)
        }
        return Or.good(g)
    }

    override fun allowWhere(predicate: (A) -> Boolean): Xform<A> =
            FilterDesc(this, predicate)

    override fun <B> flatMap(f: (A) -> Iterable<B>): Xform<B> =
            FlatMapDesc(this, f)

    override fun <B> map(f: (A) -> B): Xform<B> =
            MapDesc(this, f)

    protected abstract fun toRunList(): RunList

    override fun take(numItems: Long): Xform<A> {
        if (numItems < 0) {
            throw IllegalArgumentException("Num items must be >= 0")
        }
        return TakeDesc(this, numItems)
    }

    // I'm coding this as a map operation that either returns the source, or a TERMINATE
    // sentinel value.
    override fun takeWhile(predicate: (A) -> Boolean): Xform<A> =
            MapDesc(this, { a -> if (predicate(a)) a else terminate() })
}
