// Copyright (c) 2014-03-08 PlanBase Inc. & Glen Peterson
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

import java.io.ObjectInputStream
import java.io.Serializable
import java.util.NoSuchElementException

/**
 An efficient (in both time and memory) implementation of List.  If you want to compare a RangeOfInt
 to generic List&lt;Integer&gt;, use RangeOfInt.LIST_EQUATOR so that the hashCodes will be
 compatible.

 A RangeOfInt is an indexed sequence of integers.  It currently assumes a step of 1.
 Like everything in Java and similar classes in Clojure, Python, and Scala, it is inclusive of the
 start value but exclusive of the end.

 In theory, a class like this could be made for anything that can provide it's next() and previous()
 item and a size.  To do that, Integer would need to implement something that defined what the
 next() and previous() values.  Currently limited to Integer.MIN_VALUE to Integer.MAX_VALUE.
 */
// Note: In theory, this could implement both List<Integer> and SortedSet<Integer>
class RangeOfInt(s:Number, e:Number): UnmodList<Int>, Serializable {
    constructor(e:Number) : this(0, e)

    val start: Int
    val end: Int
    init {
        if (e.toLong() < s.toLong()) {
            throw IllegalArgumentException("end of range must be >= start of range")
        }
        start = s.toInt()
        end = e.toInt()
    }

    @Transient
    override var size: Int = end - start
        private set



    // Enums are serializable and anonymous classes are not.  Therefore enums make better singletons.
    enum class Equat: Equator<List<Int>> {
        LIST {
            override fun hash(t: List<Int>?): Int = if (t == null) 0 else UnmodIterable.hash(t)

            override fun eq(o1: List<Int>, o2: List<Int>): Boolean {
                if (o1 === o2) { return true; }
//                if ( (o1 == null) || (o2 == null) ) { return false; }
                if ((o1 is RangeOfInt) && (o2 is RangeOfInt)) {
                    return o1 == o2
                }
                return o1.size == o2.size &&
                       UnmodSortedIterable.equal(o1 as UnmodSortedIterable<*>,
                                                 o2 as UnmodSortedIterable<*>)
            }
        }
    }

    private fun readObject(s: ObjectInputStream) {
        s.defaultReadObject()
        size = end - start
    }

    /**
     Returns true if the number is within the bounds of this range (low end incluive, high end
     exclusive).  In math terms, returns true if the argument is [low, high).  False
     otherwise.  This is an efficient method when the compiler can see that it's being passed a
     primitive int, but contains(Object o) delegates to this method when it's passed a reasonable
     and unambiguous argument.
     */
    @Suppress("OverridingDeprecatedMember")
    override fun contains(element: Int):Boolean = (element >= start) && (element < end)

//    /**
//     Though this overrides List.contains(Object o), it is effectively a convenience method for
//     calling contains(int i).  Therefore, it only accepts Integers, Longs, BigIntegers, and
//     Strings that parse as signed decimal Integers.  It does not accept Numbers since they can't
//     easily be checked for truncation and floating-point might not round properly with respect to
//     bounds, or might not make sense if you are using your range to define a set of integers.
//     Handles truncation (returns false) for the types it accepts.  Throws exceptions for types it
//     does not accept.
//
//     Thanks for all the help from codereview.stackexchange:
//     http://codereview.stackexchange.com/questions/100846/rounding-and-truncation-in-intrange-containsobject-o
//
//     @param o an Integer, Long, BigInteger, or String that parses as a signed decimal Integer.
//     @return true if the number is within the bounds of this range (high end is exclusive).  False
//     otherwise.
//     @throws IllegalArgumentException if the argument is not an Integer, Long, BigInteger, or
//     String.
//     @throws NumberFormatException if a String argument cannot be parsed using Integer.valueOf().
//     */
//    @SuppressWarnings("deprecation")
//    override fun contains(element: Any): Boolean {
//        // Only accept classes where we can convert to an integer while preventing
//        // overflow/underflow.  If there were a way to reliably test for overflow/underflow in
//        // Numbers, we could accept them too, but with the rounding errors of floats and doubles
//        // that's impractical.
//        if (o instanceof Integer) {
//            return contains(((Integer) o).intValue());
//        } else if (o instanceof Long) {
//            long l = (Long) o;
//            return (l <= ((long) Integer.MAX_VALUE)) &&
//                   (l >= ((long) Integer.MIN_VALUE)) &&
//                   contains((int) l);
//        } else if (o instanceof BigInteger) {
//            try {
//                // Throws an exception if it's more than 32 bits.
//                return contains(((BigInteger) o).intValueExact());
//            } catch (ArithmeticException ignore) {
//                return false;
//            }
//        } else if (o instanceof String) {
//            return contains(Integer.valueOf((String) o));
//        } else {
//            throw new IllegalArgumentException("Don't know how to convert to a primitive int" +
//                                               " without risking accidental truncation.  Pass an" +
//                                               " Integer, Long, BigInteger, or String that parses" +
//                                               " within Integer bounds instead.");
//        }
//    }

    override fun get(index:Int ):Int =
            if ( (index >= 0) && (index < size) ) {
                start + index
            } else {
                throw IndexOutOfBoundsException("Index $index was outside the size of the range $start to $end")
            }

    override fun indexOf(element: Int): Int =
            if ( (element >= start) && (element < end) ) {
                element - start
            } else {
                -1
            }

    /**
    Unlike most implementations of List, this method has excellent O(1) performance!
    {@inheritDoc}
     */
    override fun lastIndexOf(element: Int): Int = indexOf(element)

//    @Override
//    public int hashCode() {
//        // My theory is that we can compute a hashCode compatible with ArrayList using
//        // sum of a series.  Maybe the summing hashCode truncates differently on overflow, but I
//        // thought this was worth a try.
//
//        // size * (start + end) / 2
//        // But convert to long to avoid premature overflow
//        long tmp = ((long) start) + ((long) end);
//        tmp = tmp * (long) size;
//        return (int) (tmp / 2L);
//    }
//    @Override
//    public boolean equals(Object other) {
//        if (this == other) { return true; }
//        if ( !(other instanceof List) ) { return false; }
//
//        // Fast compare for other int ranges:
//        if (other instanceof RangeOfInt) {
//            final RangeOfInt that = (RangeOfInt) other;
//            return (this.start == that.start) &&
//                   (this.end == that.end);
//        }
//
//        // Slower compare for other lists:
//        final List that = (List) other;
//        // This is not a database object; compare "significant" fields here.
//        return UnmodSortedIterable.equals(this, UnmodSortedIterable.castFromList(that));
//    }

    override fun hashCode(): Int = start + end

    override fun equals(other: Any?): Boolean =
            (other is RangeOfInt) &&
            (this.start == other.start) &&
            (this.end == other.end)

    /** index is startIndex */
    override fun listIterator(index: Int): UnmodListIterator<Int> =
        if( (index < 0) || (index > size) ) {
            // To match ArrayList and other java.util.List expectations
            throw IndexOutOfBoundsException("Index: $index")
        } else {
            object : UnmodListIterator<Int> {
                private var v: Int = start + index
                override fun hasNext(): Boolean = v < end
                override fun next(): Int =
                        if (v >= end) {
                            // To match ArrayList and other java.util.List expectations
                            throw NoSuchElementException()
                        } else {
                            val t:Int  = v
                            v += 1
                            t
                        }


                override fun hasPrevious(): Boolean = v > start
                override fun previous(): Int =
                        if (v <= start) {
                            // To match ArrayList and other java.util.List expectations
                            throw NoSuchElementException()
                        } else {
                            v -= 1
                            v
                        }
                override fun nextIndex(): Int = v - start
            }
        }

    override fun subList(fromIndex: Int, toIndex: Int): UnmodList<Int> {
        if ( (fromIndex == 0) && (toIndex == size) ) {
            return this
        }
        // Note that this is an IllegalArgumentException, not IndexOutOfBoundsException in order to
        // match ArrayList.
        if (fromIndex > toIndex) {
            throw IllegalArgumentException("fromIndex($fromIndex) > toIndex($toIndex)")
        }
        // The text of this matches ArrayList
        if (fromIndex < 0) {
            throw IndexOutOfBoundsException("fromIndex = $fromIndex")
        }
        if (toIndex > size) {
            throw IndexOutOfBoundsException("toIndex = $toIndex")
        }

        // Look very closely at the second parameter because the bounds checking is *different*
        // from the get() method.  get(toIndex) can throw an exception if toIndex >= start+size.
        // But since a range is exclusive of it's right-bound, we can create a new sub-range
        // with a right bound index of size, as opposed to size minus 1.  I spent hours
        // understanding this before fixing a bug with it.  In the end, subList should do the same
        // thing on a Range that it does on the equivalent ArrayList.  I made tests for the same.
        return RangeOfInt(start + fromIndex, start + toIndex)
    }

    companion object {
        // ======================================= Serialization =======================================

        // For serializable.  Make sure to change whenever internal data format changes.
        const val serialVersionUID: Long = 20180629061300L

    }
}