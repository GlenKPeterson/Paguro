// Copyright 2016 PlanBase Inc. & Glen Peterson
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

/**
 *
 * Represents a context for comparison because sometimes you order the same things differently.
 * For instance, a class of students might be sorted by height for the yearbook picture (shortest in
 * front), alphabetically for role call, and by GPA at honors ceremonies.  Sometimes you need
 * to sort non-compatible classes together for some reason.  If you didn't define those classes,
 * this provides an external means of ordering them.
 *
 *
 * A Comparison Context represents both ordering and equality, since the two often need to be
 * defined compatibly.  Implement compare() and hash() and you get a compatible eq() for free!
 * If you don't want ordering, use [Equator] instead.
 *
 *
 * Typical implementations of [.compare] throw an
 * IllegalArgumentExceptions if one argument is null because most objects cannot be meaningfully
 * be orderd with respect to null.  It's also OK if you want to return 0 when both arguments are null
 * because null == null.  Default implementations of eq(), gte(), and lte() check for nulls
 * first, before calling compare() so they will work either way you choose to implement
 * compare().
 *
 *
 * A common mistake is to implement a ComparisonContext, Equator, or Comparator as an anonymous
 * class or lambda, then be surprised when it can't be serialized, or is deserialized as null.
 * These one-off classes are often singletons, which are easiest to serialize as enums.  If your
 * implementation requires generic type parameters, look at how [.defCompCtx] tricks the type
 * system into using generic type parameters (correctly) with an enum.
 *
 * Nulls: http://glenpeterson.blogspot.com/search/label/comparing%20objects
 * For a while I thought, "What harm could come from just sorting nulls last?" What harm indeed!
 * Really, there is no meaningful way to compare a value to null because null represents "undefined."
 * Well, isGreaterThan(other) can meaningfully return false if other is null,
 * but the general-purpose compare(left, right) method cannot.
 * It must return less-than (any negative int), greater-than (any positive int), or equal (0).
 * The only rational thing to do if either parameter to the compare(a, b) method is null is to throw an exception.
 */
interface ComparisonContext<T> : Equator<T>, Comparator<T> {
    /** Returns true if the first object is less than the second.  */
    @JvmDefault
    fun lt(o1: T, o2: T?): Boolean = compare(o1, o2) < 0

    /** Returns true if the first object is less than or equal to the second.  */
    @JvmDefault
    fun lte(o1: T?, o2: T?): Boolean =
            if (o1 == null || o2 == null) {
                o1 === o2
            } else {
                compare(o1, o2) <= 0
            }

    /** Returns true if the first object is greater than the second.  */
    @JvmDefault
    fun gt(o1: T, o2: T?): Boolean = compare(o1, o2) > 0

    /** Returns true if the first object is greater than or equal to the second.  */
    @JvmDefault
    fun gte(o1: T?, o2: T?): Boolean = 
            if (o1 == null || o2 == null) {
                o1 === o2
            } else {
                compare(o1, o2) >= 0
            }

    /**
     * The default implementation of this method returns false if only one parameter is null then
     * checks if compare() returns zero.
     */
    @JvmDefault
    override fun eq(o1: T, o2: T): Boolean =
            if (o1 == null || o2 == null) {
                o1 === o2
            } else {
                // Now they are equal if compare returns zero.
                compare(o1, o2) == 0
            }

    /**
     * Returns the minimum (as defined by this Comparison Context).  Nulls are skipped.  If there are
     * duplicate minimum values, the first one is returned.
     */
    @JvmDefault
    fun min(items: Iterable<T>): T? {
        // Note: following code is identical to max() except for lt() vs. gt()
        val iter = items.iterator()
        var ret: T? = null
        while (ret == null && iter.hasNext()) {
            ret = iter.next()
        }
        while (iter.hasNext()) {
            val next = iter.next()
            if (next != null && lt(next, ret)) {
                ret = next
            }
        }
        return ret // could be null if all items are null.
    }

    /**
     * Returns the maximum (as defined by this Comparison Context).  Nulls are skipped.  If there are
     * duplicate maximum values, the first one is returned.
     */
    @JvmDefault
    fun max(items: Iterable<T>): T? {
        // Note: following code is identical to min() except for lt() vs. gt()
        val iter = items.iterator()
        var ret: T? = null
        while (ret == null && iter.hasNext()) {
            ret = iter.next()
        }
        while (iter.hasNext()) {
            val next = iter.next()
            if (next != null && gt(next, ret)) {
                ret = next
            }
        }
        return ret // could be null if all items are null.
    }


    companion object {

        /**
         * Please access this type-safely through [.defCompCtx] instead of calling directly.
         * This exists because Enums are serializable and lambdas are not.  Enums also make ideal
         * singletons.
         */
        private enum class CompCtx : ComparisonContext<Comparable<Any>> {
            DEFAULT {
                override fun hash(t: Comparable<Any>?): Int = t?.hashCode() ?: 0

                override fun compare(o1: Comparable<Any>, o2: Comparable<Any>): Int = o1.compareTo(o2)
            }
        }
        /**
         * Returns a typed, serializable ComparisonContext that works on any class that implements
         * [Comparable].
         */
        @JvmStatic
        @Suppress("UNCHECKED_CAST")
        fun <T> defCompCtx(): ComparisonContext<T> = CompCtx.DEFAULT as ComparisonContext<T>
    }
}
