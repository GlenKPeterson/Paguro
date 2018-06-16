// Copyright 2015-04-13 PlanBase Inc. & Glen Peterson
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
package org.organicdesign.fp.function

import org.organicdesign.fp.function.Fn2.Companion.Singletons.FIRST
import org.organicdesign.fp.function.Fn2.Companion.Singletons.SECOND
import java.util.HashMap
import java.util.function.BiFunction

import org.organicdesign.fp.oneOf.Option
import org.organicdesign.fp.tuple.Tuple2

/**
 * This is like Java 8's java.util.function.BiFunction, but retrofitted to turn checked exceptions
 * into unchecked ones.
 */
@FunctionalInterface
interface Fn2<A, B, R> : BiFunction<A, B, R>, (A, B) -> R {
    /** Implement this one method and you don't have to worry about checked exceptions.  */
    @Throws(Exception::class)
    fun invokeEx(a: A, b: B): R

    /**
     * The class that takes a consumer as an argument uses this convenience method so that it
     * doesn't have to worry about checked exceptions either.
     */
    @JvmDefault
    override operator fun invoke(a: A, b: B): R {
        try {
            return invokeEx(a, b)
        } catch (re: RuntimeException) {
            throw re
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    /**
     * The class that takes a consumer as an argument uses this convenience method so that it
     * doesn't have to worry about checked exceptions either.
     */
    @JvmDefault
    override fun apply(a: A, b: B): R = invoke(a, b)

    companion object {
        // This should be more useful once Kotlin 1.3 gets rid of the .Companion. with the JvmStatic annotation.
        /** For Java users to be able to cast without explicitly writing out the types. */
        fun <A,B,Z> toFn2(f:Fn2<A,B,Z>): (A,B) -> Z = f

        // Don't think this is necessary.  Is it?
        //    default BiFunction<A,B,R> asBiFunction() {
        //        return (A a, B b) -> invoke(a, b);
        //    }

        /**
         * Use only on pure functions with no side effects.  Wrap an expensive function with this and for each input
         * value, the output will only be computed once.  Subsequent calls with the same input will return identical output
         * very quickly.  Please note that the parameters to f need to implement equals() and hashCode() correctly
         * for this to work correctly and quickly.
         */
        fun <A, B, Z> memoize(f: Fn2<A, B, Z>): Fn2<A, B, Z> {
            return object : Fn2<A, B, Z> {
                private val map = HashMap<Tuple2<A, B>, Option<Z>>()
                @Synchronized
                @Throws(Exception::class)
                override fun invokeEx(a: A, b: B): Z {
                    val t = Tuple2(a, b)
                    val `val` = map[t]
                    if (`val` != null) {
                        return `val`.get()
                    }
                    val ret = f.apply(a, b)
                    map[t] = Option.some(ret)
                    return ret
                }
            }
        }

        enum class Singletons : Fn2<Any, Any, Any> {
            /**
             * A static function that always returns the first argument it is given.
             * For type safety, please use [Fn2.first] instead of accessing this directly.
             */
            FIRST {
                override fun invokeEx(a: Any, b: Any): Any = a
            },
            /**
             * A static function that always returns the second argument it is given.
             * For type safety, please use [Fn2.second] instead of accessing this directly.
             */
            SECOND {
                override fun invokeEx(a: Any, b: Any): Any = b
            }
        }

        /**
         * Returns a static function that always returns the first argument it is given.
         * @return the first argument, unmodified.
         */
        @Suppress("UNCHECKED_CAST")
        fun <A1, B1> first(): Fn2<A1, in B1, A1> = FIRST as Fn2<A1, in B1, A1>

        /**
         * Returns a static function that always returns the second argument it is given.
         * @return the second argument, unmodified.
         */
        @Suppress("UNCHECKED_CAST")
        fun <A1, B1> second(): Fn2<in A1, B1, B1> = SECOND as Fn2<in A1, B1, B1>
    }
}
