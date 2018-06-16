// Copyright 2013-12-30 PlanBase Inc. & Glen Peterson
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

import org.organicdesign.fp.oneOf.Option
import java.util.function.Consumer
import java.util.function.Function
import javax.management.Query.or
import org.organicdesign.fp.function.Fn1.Companion.ConstObjBool
import org.organicdesign.fp.xform.Xform
import org.organicdesign.fp.collections.UnmodIterable
import org.organicdesign.fp.oneOf.Or
import org.organicdesign.fp.xform.Transformable
import javax.swing.text.html.HTML.Tag.U



/**
 * This is like Java 8's java.util.function.Function, but retrofitted to turn checked exceptions
 * into unchecked ones.
 */
@FunctionalInterface
interface Fn1<T, U> : Function<T, U>, Consumer<T>, (T) -> U {

    /** Implement this one method and you don't have to worry about checked exceptions.  */
    @Throws(Exception::class)
    fun invokeEx(t: T): U

    /** Call this convenience method so that you don't have to worry about checked exceptions.  */
    @JvmDefault
    override operator fun invoke(t: T): U {
        try {
            return invokeEx(t)
        } catch (re: RuntimeException) {
            throw re
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    @JvmDefault
    override fun apply(t: T): U = invoke(t)

    /** For compatibility with java.util.function.Consumer.  Just a wrapper around invoke().  */
    @JvmDefault
    override fun accept(t: T) {
        invoke(t)
    }

    @JvmDefault
    fun <S> compose(f: Fn1<in S, out T>): Fn1<S, U> {
        if (f === ConstObjObj.IDENTITY) {
            // This violates type safety, but makes sense - composing any function with the
            // identity function should return the original function unchanged.  If you mess up the
            // types, then that's your problem.  With generics and type erasure this may be the
            // best you can do.
            @Suppress("UNCHECKED_CAST")
            return this as Fn1<S, U>
        }
        val parent = this
        return object: Fn1<S,U> {
            override fun invokeEx(t: S): U = parent.invokeEx(f.invokeEx(t))
        }
    }

    companion object {

        // This should be more useful once Kotlin 1.3 gets rid of the .Companion. with the JvmStatic annotation.
        /** For Java users to be able to cast without explicitly writing out the types. */
        fun <A,Z> toFn1(f:Fn1<A,Z>): (A) -> Z = f

        /** Constant functions that take an Object and return an Object  */
        enum class ConstObjObj : Fn1<Any, Any> {
            /** The Identity function  */
            IDENTITY {
                @Throws(Exception::class)
                override fun invokeEx(t: Any): Any {
                    return t
                }

                override fun <S> compose(f: Fn1<in S, out Any>): Fn1<S, Any> {
                    // Composing any function with the identity function has no effect on the original
                    // function (by definition of identity) - just return it.
                    @Suppress("UNCHECKED_CAST")
                    return f as Fn1<S, Any>
                }
            }
        }

        /** Constant functions that take an Object and return a Boolean  */
        enum class ConstObjBool : Fn1<Any, Boolean> {
            /**
             * A predicate that always returns true.  Use [.accept] for a type-safe version of
             * this predicate.
             */
            ACCEPT {
                @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
                override fun invokeEx(ignore: Any): Boolean = java.lang.Boolean.TRUE
            },

            /**
             * A predicate that always returns false. Use [.reject] for a type-safe version of
             * this predicate.
             */
            REJECT {
                @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
                override fun invokeEx(ignored: Any): Boolean = java.lang.Boolean.FALSE
            }
        }

        enum class BooleanCombiner {
            AND {
                override fun <T> combine(predicates: Iterable<Fn1<T, Boolean>>): Fn1<T, Boolean> {
                    return and(predicates)
                }
            },
            OR {
                override fun <T> combine(predicates: Iterable<Fn1<T, Boolean>>): Fn1<T, Boolean> {
                    return or(predicates)
                }
            };

            abstract fun <T> combine(predicates: Iterable<Fn1<T, Boolean>>): Fn1<T, Boolean>
        }

        @Suppress("UNCHECKED_CAST")
        fun <V> identity(): Fn1<V, V> = ConstObjObj.IDENTITY as Fn1<V, V>

        fun <S> or(a: Fn1<S, Boolean>, b: Fn1<S, Boolean>): Fn1<S, Boolean> =
                when { // Composition is not necessary in every case:
                    a === ConstObjBool.ACCEPT -> a // If any are true, all are true.
                    a === ConstObjBool.REJECT -> b // return whatever b is.
                    b === ConstObjBool.ACCEPT -> b // If any are true, all are true.
                    b === ConstObjBool.REJECT -> a
                    else                      -> object: Fn1<S, Boolean> {
                        // Just amounts to if a else false.
                        override fun invokeEx(t: S): Boolean = a.apply(t) || b.apply(t)
                    } // compose
                } // end or(a, b)

        fun <S> and(a: Fn1<S, Boolean>, b: Fn1<S, Boolean>): Fn1<S, Boolean> =
                when { // Composition is not necessary in every case:
                    a === ConstObjBool.ACCEPT -> b // return whatever b is.
                    a === ConstObjBool.REJECT -> a // if any are false, all are false.
                    b === ConstObjBool.ACCEPT -> a // Just amounts to if a else false.
                    b === ConstObjBool.REJECT -> b
                    else                      -> object: Fn1<S, Boolean> {
                        // If any are false, all are false.
                        override fun invokeEx(t: S): Boolean = a.apply(t) && b.apply(t)
                    } // compose
                } // end and(a, b)

        fun <S> negate(a: Fn1<in S, Boolean>): Fn1<S, Boolean> =
                when {
                    a === ConstObjBool.ACCEPT -> reject()
                    a === ConstObjBool.REJECT -> accept()
                    else                      -> object: Fn1<S, Boolean> {
                        override fun invokeEx(t: S): Boolean =
                                if (a.apply(t)) java.lang.Boolean.FALSE else java.lang.Boolean.TRUE
                    }
                }


        /** Returns a type-safe version of the ConstObjBool.ACCEPT predicate.  */
        @Suppress("UNCHECKED_CAST")
        fun <T> accept(): Fn1<T, Boolean> = ConstObjBool.ACCEPT as Fn1<T, Boolean>

        /** Returns a type-safe version of the ConstObjBool.REJECT predicate.  */
        @Suppress("UNCHECKED_CAST")
        fun <T> reject(): Fn1<T, Boolean> = ConstObjBool.REJECT as Fn1<T, Boolean>

        /**
         * Composes multiple functions into a single function to potentially minimize trips through
         * the source data.  The resultant function will loop through the functions for each item in the
         * source.  For a few functions and many source items, that takes less memory.  Considers no
         * function to mean the IDENTITY function.  This decision is based on the way filters work and
         * may or may not prove useful in practice.  Please use the identity()/IDENTITY
         * sentinel value in this abstract class since function comparison is done by reference.
         *
         * LIMITATION: You could have a function that maps from T to U then the next from U to V, the
         * next from V to W and so on.  So long as the output type of one matches up to the input type of
         * the next, you're golden.  But type safety curls up and dies when you try to detect the
         * IDENTITY function at some point in the chain.
         *
         * For arbitrary chaining, it's best to roll your own.  The following example shows how simple it
         * is to chain two functions with an intermediate type into a single composite function:
         *
         * <pre>`
         * public static <A,B,C> Fn1<A,C> chain2(final Fn1<A,B> f1,
         * final Fn1<B,C> f2) {
         * return new Fn1<A,C>() {
         * &#64;Override
         * public C invokeEx(A a) throws Exception {
         * return f2.invokeEx(f1.invokeEx(a));
         * }
         * };
         * }`</pre>
         *
         * Even with 2 arguments, there are several signatures that would work: imagine where A=B, B=C,
         * or A=C.  I just don't see the value to providing a bunch of chain2(), chain3() etc. functions
         * that will ultimately not be type-safe and cannot perform optimizations for you, when you can
         * roll your own type safe versions as you need them.  Only the simplest case seems worth
         * providing, along the lines of the and() helper function in Filter()
         *
         * @param fns the functions to invokeEx in order.  Nulls and IDENTITY functions are ignored.
         * No functions means IDENTITY.
         * @param <V> the type of object to chain functions on
         * @return a function which applies all the given functions in order.
        </V> */
        fun <V> compose(fns: Iterable<Fn1<V, V>?>?): Fn1<V, V> {
            if (fns == null) {
                return identity()
            }
            val resFns = mutableListOf<Fn1<V, V>>()
            for (f in fns) {
                // Skip any null or identity functions
                @Suppress("SuspiciousEqualsCombination")
                if (f == null || f === ConstObjObj.IDENTITY) {
                    continue
                }
                resFns.add(f)
            }
            return when {
                resFns.size < 1  -> identity() // No functions means to return the original item
                resFns.size == 1 -> resFns[0]
                else             -> object: Fn1<V, V> {
                    override fun invokeEx(t: V): V {
                        var ret:V = t
                        for (f in resFns) {
                            ret = f.invokeEx(ret)
                        }
                        return ret
                    }
                }
            }
        }

        /**
         * Composes multiple predicates into a single predicate to potentially minimize trips through
         * the source data.  The resultant predicate will loop through the predicates for each item in
         * the source, but for few predicates and many source items, that takes less memory.  Considers
         * no predicate to mean "accept all."  Use only accept()/ACCEPT and reject()/REJECT since
         * function comparison is done by reference.
         *
         * @param predicates the predicates to test in order.  Nulls and ACCEPT predicates are ignored.  Any
         * REJECT predicate will cause this entire method to return a single REJECT predicate.  No
         * predicates means ACCEPT.
         * @param <T> the type of object to predicate on.
         * @return a predicate which returns true if all input predicates return true, false otherwise.
        </T> */
        fun <T> and(predicates: Iterable<Fn1<T, Boolean>?>?): Fn1<T, Boolean> {
            if (predicates == null) {
                return accept()
            }

            var ret = accept<T>()

            for (p in predicates) {
                if ( (p == null) || (p === ConstObjBool.ACCEPT) ) {
                    continue
                }
                if (p === ConstObjBool.REJECT) {
                    return p
                }
                ret = and(ret, p)
            }
            return ret
        }

        /**
         * Composes multiple predicates into a single predicate to potentially minimize trips through
         * the source data.  The resultant predicate will loop through the predicates for each item in
         * the source, but for few predicates and many source items, that takes less memory.  Considers
         * no predicate to mean "reject all."  Use only accept()/ConstObjBool.ACCEPT and ConstObjBool.REJECT since
         * function comparison is done by reference.
         *
         * @param predicates the predicates to test in order.  Nulls and ConstObjBool.REJECT predicates are ignored.
         * Any ACCEPT predicate will cause this entire method to return the ACCEPT predicate.
         * No predicates means ConstObjBool.REJECT.
         * @param <T> the type of object to predicate on.
         * @return a predicate which returns true if any of the input predicates return true,
         * false otherwise.
        </T> */
        fun <T> or(predicates: Iterable<Fn1<T, Boolean>?>?): Fn1<T, Boolean> {
            if (predicates == null) { return reject() }

//            val v = if (predicates is UnmodIterable<*>)
//                predicates as UnmodIterable<Fn1<T, Boolean>?>
//            else
//                Xform.of(predicates)
//
//            val ret: Or<Fn1<T, Boolean>, Fn1<T, Boolean>> =
//                    v.allowWhere { p -> (p != null) && (p !== ConstObjBool.REJECT) }
//                            .foldUntil(reject(),
//                                       { _, p -> if (p === ConstObjBool.ACCEPT) p else null },
//                                       { accum, p -> or(accum, p!!) })
//            // We don't care whether it returns early or not.  Just return whatever is in the or.
//            return ret.match({ g -> g },
//                             { b -> b })


            var ret = reject<T>()

            for (p in predicates) {
                if ( (p == null) || (p === ConstObjBool.REJECT) ) {
                    continue
                }
                if (p === ConstObjBool.ACCEPT) {
                    return p
                }
                ret = or(ret, p)
            }
            return ret
        }

        /**
         * Use only on pure functions with no side effects.  Wrap an expensive function with this and for
         * each input value, the output will only be computed once.  Subsequent calls with the same input
         * will return identical output very quickly.  Please note that the return values from f need to
         * implement equals() and hashCode() correctly for this to work correctly and quickly.
         */
        fun <A, B> memoize(f: Fn1<A, B>): Fn1<A, B> {
            return object : Fn1<A, B> {
                private val memo = mutableMapOf<A, Option<B>>()
                @Synchronized
                @Throws(Exception::class)
                override fun invokeEx(t: A): B {
                    val `val` = memo[t]
                    if (`val` != null && `val`.isSome) {
                        return `val`.get()
                    }
                    val ret = f.apply(t)
                    memo[t] = Option.some(ret)
                    return ret
                }
            }
        }
    }
}