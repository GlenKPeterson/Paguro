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

package org.organicdesign.fp.collections

/**
 * An Equator represents an equality context in a way that is analagous to the java.util.Comparator
 * interface.
 * [Comparing Objects is Relative](http://glenpeterson.blogspot.com/2013/09/object-equality-is-context-relative.html)
 * This will need to be passed to Hash-based collections the way a Comparator is passed to tree-based
 * ones.
 *
 * The method names hash() and eq() are intentionally elisions of hashCode() and equals() so that your
 * IDE will suggest the shorter name as you start typing which is almost always what you want.
 * You want the hash() and eq() methods because that's how Equators compare things.  You don't want
 * an equator's .hashCode() or .equals() methods because those are for comparing *Equators* and are
 * inherited from java.lang.Object.  I'd deprecate those methods, but you can't do that on an
 * interface.
 *
 * A common mistake is to implement an Equator, ComparisonContext, or Comparator as an anonymous class
 * or lambda, then be surprised when it is can't be serialized, or is deserialized as null.  These
 * one-off classes are often singletons, which are easiest to serialize as enums.  If your
 * implementation requires generic type parameters, observe how [.defaultEquator] tricks
 * the type system into using generic type parameters (correctly) with an enum.
 */
interface Equator<T> {


    // ========================================= Instance =========================================
    /**
     * An integer digest used for very quick "can-equal" testing.
     * This method MUST return equal hash codes for equal objects.
     * It should USUALLY return unequal hash codes for unequal objects.
     * You should not change mutable objects while you rely on their hash codes.
     * That said, if a mutable object's internal state changes, the hash code generally must change to
     * reflect the new state.
     * The name of this method is short so that auto-complete can offer it before hashCode().
     */
    fun hash(t: T?): Int

    /**
     * Determines whether two objects are equal.  The name of this method is short so that
     * auto-complete can offer it before equals().
     *
     * You can do anything you want here, but consider having null == null but null != anything else.
     *
     * @return true if this Equator considers the two objects to be equal.
     */
    fun eq(o1: T, o2: T): Boolean

    /**
     * Returns true if two objects are NOT equal.  By default, just delegates to [.eq] and reverses
     * the result.
     *
     * @return true if this Equator considers the two objects to NOT be equal.
     */
    @JvmDefault
    fun neq(o1: T, o2: T): Boolean = !eq(o1, o2)

    companion object {

        private enum class Comp : Comparator<Comparable<Any>> {
            DEFAULT {
                override fun compare(o1: Comparable<Any>, o2: Comparable<Any>): Int = Equator.doCompare(o1, o2)
            };
        }

        // Enums are serializable and lambdas are not.  Therefore enums make better singletons.
        private enum class Equat : Equator<Any?> {
            /**
             * Assumes the object is not an array.  Default array.equals() is == comparison which is probably not what
             * you want.
             */
            DEFAULT {
                override fun hash(t: Any?): Int = t?.hashCode() ?: 0

                override fun eq(o1: Any?, o2: Any?): Boolean =
                        if (o1 == null) {
                            o2 == null
                        } else {
                            o1 == o2
                        }
                //        },
                //        ARRAY {
                //            @Override public int hash(Object o) {
                //                return Arrays.hashCode( (Object[]) o);
                //            }
                //
                //            @Override public boolean eq(Object o1, Object o2) {
                //                try {
                //                    return Arrays.equals((Object[]) o1, (Object[]) o2);
                //                } catch (Exception e) {
                //                    return false;
                //                }
                //            }
            };
        }

        @JvmStatic
        @Suppress("UNCHECKED_CAST")
        fun <T> defaultEquator(): Equator<T> = Equat.DEFAULT as Equator<T>

        @JvmStatic
        @Suppress("UNCHECKED_CAST")
        fun <T> defaultComparator(): Comparator<T> = Comp.DEFAULT as Comparator<T>

        /** This is the guts of building a comparator from Comparables.  */
        fun doCompare(o1: Comparable<Any>, o2: Comparable<Any>): Int = o1.compareTo(o2)
    }

}
