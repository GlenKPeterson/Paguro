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

import java.util.HashMap

import org.organicdesign.fp.oneOf.Option
import org.organicdesign.fp.tuple.Tuple3

/** A three-argument, exception-safe functional interface.  */
@FunctionalInterface
interface Fn3<A, B, C, R>: (A, B, C) -> R {
    /** Implement this one method and you don't have to worry about checked exceptions.  */
    @Throws(Exception::class)
    fun invokeEx(a: A, b: B, c: C): R

    /**
     * The class that takes a consumer as an argument uses this convenience method so that it
     * doesn't have to worry about checked exceptions either.
     */
    @JvmDefault
    override operator fun invoke(a: A, b: B, c: C): R {
        try {
            return invokeEx(a, b, c)
        } catch (re: RuntimeException) {
            throw re
        } catch (e: Exception) {
            throw RuntimeException(e)
        }

    }

    companion object {
        // This should be more useful once Kotlin 1.3 gets rid of the .Companion. with the JvmStatic annotation.
        /** For Java users to be able to cast without explicitly writing out the types. */
        fun <A,B,C,Z> toFn3(f:Fn3<A,B,C,Z>): (A,B,C) -> Z = f

        /**
         * Use only on pure functions with no side effects.  Wrap an expensive function with this and for each input
         * value, the output will only be computed once.  Subsequent calls with the same input will return identical output
         * very quickly.  Please note that the parameters to f need to implement equals() and hashCode() correctly
         * for this to work correctly and quickly.  Also, make sure your domain is very small!  This function uses O(n^3)
         * memory.
         */
        fun <A, B, C, Z> memoize(f: Fn3<A, B, C, Z>): Fn3<A, B, C, Z> {
            return object : Fn3<A, B, C, Z> {
                private val map = HashMap<Tuple3<A, B, C>, Option<Z>>()
                @Synchronized
                @Throws(Exception::class)
                override fun invokeEx(a: A, b: B, c: C): Z {
                    val t3 = Tuple3.of(a, b, c)
                    val `val` = map[t3]
                    if (`val` != null) {
                        return `val`.get()
                    }
                    val ret = f.invoke(a, b, c)
                    map[t3] = Option.some(ret)
                    return ret
                }
            }
        }
    }

}
