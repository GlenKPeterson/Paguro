// Copygood 2015 PlanBase Inc. & Glen Peterson
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
package org.organicdesign.fp.oneOf

import org.organicdesign.fp.FunctionUtils.stringify

/**
 * `Or` represents the presence of a successful outcome, or an error.
 * Contrast this with Option which represents the presence or absence of a value.
 * Option.Some and Or.Good are just about identical.  Unlike Option.None, Bad contains an error code or value.
 * [Bill Venners, Scalactic, SuperSafe, and
 * Functional Error Handling talk at SF Scala 2015-02-24](https://www.youtube.com/watch?v=bCTZQi2dpl8) convinced me that Or is friendlier than
 * [Either](http://www.scala-lang.org/api/rc2/scala/Either.html).
 * This class is based on Bill Venners' Or.  I did not make Every, One, and Many sub-classes, figuring that you can
 * make an Or&lt;GoodType,ImList&lt;BadType&gt;&gt; if you expect that.
 *
 * Bill makes the point that there are still some reasons to throw exceptions, but he says to "Throw exceptions at
 * developers, not at code" meaning that if there's code in your program that can recover from the issue, use a
 * functional return type (like Or).  Throw exceptions for things a program can't handle without developer intervention.
 *
 * Any errors are my own.
 *
 * This implementation is more like a sealed trait (in Kotlin or Scala) than a simple [OneOf2] union type.
 * This makes it a little less general, and more meaningful to use.
 */
interface Or<out G, out B> {

    /** Returns true if this Or has a good value.  */
    val isGood: Boolean
    /** Returns true if this Or has a bad value.  */
    val isBad: Boolean

    /** Returns the good value if this is a Good, or throws an exception if this is a Bad.  */
    @JvmDefault
    val good: G
        get() {
            throw IllegalStateException("Cant call good() on a Bad.")
        }

    /** Returns the bad value if this is a Bad, or throws an exception if this is a Good.  */
    @JvmDefault
    val bad: B
        get() {
            throw IllegalStateException("Cant call bad() on a Good.")
        }

    // TODO: Remove.
    /** Returns the good value if this is a Good, or throws an exception if this is a Bad.  */
    @JvmDefault
    fun good(): G = good

    // TODO: Remove.
    /** Returns the bad value if this is a Bad, or throws an exception if this is a Good.  */
    @JvmDefault
    fun bad(): B = bad

    /**
     * Exactly one of these functions will be executed - determined by whether this is a Good or a Bad.
     * @param fg the function to be executed if this OneOf stores the first type.
     * @param fb the function to be executed if this OneOf stores the second type.
     * @return the return value of whichever function is executed.
     */
    fun <R> match(fg: (G) -> R,
                  fb: (B) -> R): R


    /** Represents the presence of a Good value (and absence of a Bad).  */
    class Good<G, B>(override val good: G) : Or<G, B> {

        override val isGood: Boolean = true
        override val isBad: Boolean = false

        override fun <R> match(fg: (G) -> R,
                               fb: (B) -> R): R = fg(good)

        override fun hashCode(): Int = good?.hashCode() ?: 0

        override fun equals(other: Any?): Boolean =
                if (this === other) {
                    true
                } else {
                    other is Good<*, *> && this.good == other.good
                }

        override fun toString(): String = "Good(" + stringify(good) + ")"
    }

    /** Represents the presence of a Bad value (and absence of a Good).  */
    class Bad<G, B>(override val bad: B) : Or<G, B> {

        override val isGood: Boolean = false
        override val isBad: Boolean = true

        override fun <R> match(fg: (G) -> R,
                               fb: (B) -> R): R = fb(bad)

        // Returns twos compliment of contained item.
        override fun hashCode(): Int = bad?.hashCode()?.inv() ?: 0

        override fun equals(other: Any?): Boolean =
                if (this === other) {
                    true
                } else {
                    other is Bad<*, *> && this.bad == other.bad
                }

        override fun toString(): String = "Bad(" + stringify(bad) + ")"
    }

    companion object {
        /** Construct a new Good from the given object.  */
        fun <G, B> good(good: G): Or<G, B> = Good(good)

        /** Construct a new Bad from the given object.  */
        fun <G, B> bad(bad: B): Or<G, B> = Bad(bad)
    }
} // end interface Or