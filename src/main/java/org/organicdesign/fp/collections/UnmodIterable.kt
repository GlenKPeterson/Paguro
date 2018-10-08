package org.organicdesign.fp.collections

import org.organicdesign.fp.FunctionUtils.stringify
import org.organicdesign.fp.oneOf.Option
import org.organicdesign.fp.oneOf.Or
import org.organicdesign.fp.xform.Transformable
import org.organicdesign.fp.xform.Xform
import java.util.Arrays

/** An unmodifiable Iterable, without any guarantee about order.  */
@PurelyImplements("java.util.Iterable")
interface UnmodIterable<T> : Iterable<T>, Transformable<T> {
    // ========================================== Static ==========================================

    enum class UnIterable : UnmodIterable<Any> {
        EMPTY {
            override fun iterator(): UnmodIterator<Any> {
                return UnmodIterator.emptyUnmodIterator<Any>()
            }
        }
    }

    // ================================== Inherited from Iterable ==================================
    /**
     * A one-time use, mutable, not-thread-safe way to get each value of the underling collection in
     * turn. I experimented with various thread-safe alternatives, but the JVM is optimized around
     * iterators so this is the lowest common denominator of collection iteration, even though
     * iterators are inherently mutable.
     */
    @JvmDefault
    override fun iterator(): UnmodIterator<T>

    // =============================== Inherited from Transformable ===============================

    @JvmDefault
    override fun concat(iterable: Iterable<T>): UnmodIterable<T> = Xform.of(this).concat(iterable)

    @JvmDefault
    override fun precat(list: Iterable<T>): UnmodIterable<T> = Xform.of(this).precat(list)

    @JvmDefault
    override fun drop(numItems: Long): UnmodIterable<T> = Xform.of(this).drop(numItems)

    @JvmDefault
    override fun dropWhile(predicate: (T) -> Boolean): UnmodIterable<T> = Xform.of(this).dropWhile(predicate)

    @JvmDefault
    override fun <B> fold(accum: B, reducer: (B, T) -> B): B = Xform.of(this).fold(accum, reducer)

    @JvmDefault
    override fun <G, B> foldUntil(accum: G,
                                  terminator: (G, T) -> B?,
                                  reducer: (G, T) -> G): Or<G, B> =
            Xform.of(this).foldUntil(accum, terminator, reducer)

//    @JvmDefault
//    override fun <G, B> foldUntil(accum: G,
//                                  terminator: (G, T) -> B,
//                                  reducer: (G, T) -> G): Or<G, B> {
//        // I think this is the magic right here.
//        // Does anything a loop will do, including terminating early, yet the interface is purely functional.
//        var ret = accum
//        for (item in iterator()) {
//            val termVal = terminator(ret, item)
//            if (termVal != null) {
//                return Or.bad(termVal)
//            }
//            ret = reducer(ret, item)
//        }
//        return Or.good(ret)
//    }

    @JvmDefault
    override fun allowWhere(predicate: (T) -> Boolean): UnmodIterable<T> = Xform.of(this).allowWhere(predicate)

    @JvmDefault
    override fun <B> flatMap(f: (T) -> Iterable<B>): UnmodIterable<B> = Xform.of(this).flatMap(f)

    @JvmDefault
    override fun <B> map(f: (T) -> B): UnmodIterable<B> = Xform.of(this).map(f)

    @JvmDefault
    override fun take(numItems: Long): UnmodIterable<T> = Xform.of(this).take(numItems)

    @JvmDefault
    override fun takeWhile(predicate: (T) -> Boolean): UnmodIterable<T> = Xform.of(this).takeWhile(predicate)

    @JvmDefault
    override fun head(): Option<T> {
        val iter = iterator()
        return if (iter.hasNext())
            Option.some(iter.next())
        else
            Option.none()
    }

    companion object {

        /** We only ever need one empty iterable in memory.  */
        fun <T> emptyUnmodIterable(): UnmodIterable<T> {
            @Suppress("UNCHECKED_CAST")
            return UnIterable.EMPTY as UnmodIterable<T>
        }

        //    /**
        // Caution: this is a convenient optimization for immutable data structures and a nightmare
        // waiting to happen to mutable ones.  Don't go slapping this on immutable wrappers for mutable
        // data.  If all the underlying data is truly immutable, this allows you to compute the hashCode
        // the first time it is needed, then return that same code without re-computing it again.  It's
        // the internal version of a memoizer.  Also, use this only for decent sized collections.  If you
        // only have 2 or 3 fields, this isn't buying you anything.
        //     */
        //    static Lazy.Int lazyHashCode(UnmodIterable iter) {
        //        if (iter == null) { throw new IllegalArgumentException("Can't have a null iterable."); }
        //        return Lazy.Int.of(() -> UnmodIterable.hashCode(iter));
        //    }
        //
        //    /**
        //     Caution: this is a convenient optimization for immutable data structures and a nightmare
        //     waiting to happen to mutable ones.  Don't go slapping this on immutable wrappers for mutable
        //     data structures.  If all the underlying data is truly immutable, this allows you to compute a
        //     reasonable toString() the first time it is needed, then return that same String without
        //     re-computing it again.  It's the internal version of a memoizer.
        //     */
        //    static LazyRef<String> lazyToString(String name, UnmodIterable iter) {
        //        if (name == null) { throw new IllegalArgumentException("Can't have a null name."); }
        //        if (iter == null) { throw new IllegalArgumentException("Can't have a null iterable."); }
        //        return LazyRef.of(() -> UnmodIterable.toString(name, iter));
        //    }

        //    /** Lets underlying compareTo method handle comparing nulls to non-null values. */
        //    static <E extends Comparable<E>> int compareHelper(E e1, E e2) {
        //        if (e1 == e2) { return 0; }
        //        if (e1 == null) {
        //            //noinspection ConstantConditions
        //            return -e2.compareTo(e1);
        //        }
        //        return e1.compareTo(e2);
        //    }
        //
        //    /** A default comparator for UnIterables comparable */
        //    static <F extends Comparable<F>,E extends UnmodIterable<F>> Comparator<E>
        //    iterableComparator() {
        //        return new Comparator<E>() {
        //            @Override
        //            public int compare(E o1, E o2) {
        //                if (o1 == null) {
        //                    if (o2 == null) {
        //                        return 0;
        //                    } else {
        //                        //noinspection ConstantConditions
        //                        return -compare(o2, o1);
        //                    }
        //                }
        //                UnmodIterator<F> as = o1.iterator();
        //                UnmodIterator<F> bs = o2.iterator();
        //                while (as.hasNext() && bs.hasNext()) {
        //                    int ret = compareHelper(as.next(), bs.next());
        //                    if (ret != 0) {
        //                        return ret;
        //                    }
        //                }
        //                // If we run out of items in one, the longer one is considered greater, just like
        //                // ordering words in a dictionary.
        //                if (as.hasNext()) { return -1; }
        //                if (bs.hasNext()) { return 1; }
        //                // All items compare 0 and same number of items - these are sorted the same (and
        //                // probably equal)
        //                return 0;
        //            }
        //        };
        //    }

        /**
         * This is correct, but O(n).  It also works regardless of the order of the items because
         * a + b = b + a, even when an overflow occurs.
         */
        fun hash(iterable: Iterable<*>): Int {
            //        System.out.println("hashCode for: " + is);
            var ret = 0
            for (t in iterable) {
                if (t != null) {
                    //                System.out.println("\tt: " + t + " hashCode: " + t.hashCode());
                    ret = ret + t.hashCode()
                }
            }
            return ret
        }

        /** Computes a reasonable to-string.  */
        fun toString(name: String, iterable: Iterable<*>): String {
            val sB = StringBuilder()
            sB.append(name).append("(")
            var i = 0
            val iter = iterable.iterator()
            while (iter.hasNext()) {
                if (i > 0) {
                    sB.append(",")
                }
                //            if (i > 4) { break; }
                val item = iter.next()
                sB.append(stringify(item))
                i++
            }
            //        if (iter.hasNext()) {
            //            sB.append("...");
            //        }
            return sB.append(")").toString()
        }

        /**
         * This method goes against Josh Bloch's Item 25: "Prefer Lists to Arrays", but is provided for backwards
         * compatibility.  If you need to create an array then the best way to use this method is:
         *
         * `MyThing[] things = toArray(collection, new MyThing[coll.size()], size);`
         *
         * Calling this method any other way causes unnecessary work to be done - an extra memory allocation and
         * potential garbage collection if the passed array is too small, extra effort to fill the end of the array
         * with nulls if it is too large.
         */
        fun <T> toArray(iterable:Iterable<T>, elements: Array<T>, size:Int): Array<out T> {
            var elems:Array<T> = elements
            if (elems.size < size) {
                // This produced a class cast exception when the return was put into a
                // variable of type non-Object[] (like String[] or Integer[]).  To see the problem
                // you must.
                // 1. pass a smaller array so that an Object[] is created
                // 2. Force the return type to be a String[] (or other not-exactly Object)
                // Merely running Arrays.AsList() on it is not enough to get the exception.

                // Bad: watch for this in the future!
                //            as = (T[]) new Object[size()];
                @Suppress("UNCHECKED_CAST")
                elems = java.lang.reflect.Array.newInstance(elems.javaClass.componentType as Class<T>, size) as Array<T>
//            elems = arrayOfNulls(size)
            }
            val iter = iterable.iterator()
            for (i in 0 until size) {
                elems[i] = iter.next()
            }
            if (size < elems.size) {
                Arrays.fill(elems, size, elems.size, null)
            }
            return elems
        }

    }

    //    /**
    //     The rest of this sequnce (all the items after its head).  This was originally called rest(),
    //     but when I renamed first() to head(), I renamed rest() to tail() so that it wouldn't mix
    //     metaphors.
    //     */
    //    @Deprecated
    //    default Transformable<T> tail() {
    //        return Xform.of(this).drop(1);
    //    }
}
