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

package org.organicdesign.fp.function;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import org.organicdesign.fp.Option;
import org.organicdesign.fp.ephemeral.View;

/**
 This is like Java 8's java.util.function.Function, but retrofitted to turn checked exceptions
 into unchecked ones.
 */
@FunctionalInterface
public interface Function1<T,U> extends Function<T,U>, Consumer<T> {
    /** Implement this one method and you don't have to worry about checked exceptions. */
    U applyEx(T t) throws Exception;

    /** Call this convenience method so that you don't have to worry about checked exceptions. */
    @Override default U apply(T t) {
        try {
            return applyEx(t);
        } catch (RuntimeException re) {
            throw re;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override default void accept(T t) { apply(t); }

    @SuppressWarnings("unchecked")
    default <S> Function1<S,U> compose(final Function1<? super S, ? extends T> f) {
        if (f == IDENTITY) {
            // This violates type safety, but makes sense - composing any function with the
            // identity function should return the original function unchanged.  If you mess up the
            // types, then that's your problem.  With generics and type erasure this may be the
            // best you can do.
            return (Function1<S,U>) this;
        }
        final Function1<T,U> parent = this;
        return new Function1<S, U>() {
            @Override
            public U applyEx(S s) throws Exception {
                return parent.applyEx(f.applyEx(s));
            }
        };
    }

    public static final Function1<Object,Object> IDENTITY = new Function1<Object,Object>() {

        @Override
        public Object applyEx(Object t) throws Exception {
            return t;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <S> Function1<S,Object> compose(Function1<? super S, ? extends Object> f) {
            // Composing any function with the identity function has no effect on the original
            // function (by definition of identity) - just return it.
            return (Function1<S,Object>) f;
        }
    };

    @SuppressWarnings("unchecked")
    public static <V> Function1<V,V> identity() { return (Function1<V,V>) IDENTITY; }

    /**
     Composes multiple functions into a single function to potentially minimize trips through
     the source data.  The resultant function will loop through the functions for each item in the
     source.  For a few functions and many source items, that takes less memory.  Considers no
     function to mean the IDENTITY function.  This decision is based on the way filters work and
     may or may not prove useful in practice.  Please use the identity()/IDENTITY
     sentinel value in this abstract class since function comparison is done by reference.

     LIMITATION: You could have a function that maps from T to U then the next from U to V, the
     next from V to W and so on.  So long as the output type of one matches up to the input type of
     the next, you're golden.  But type safety curls up and dies when you try to detect the
     IDENTITY function at some point in the chain.

     For arbitrary chaining, it's best to roll your own.  The following example shows how simple it
     is to chain two functions with an intermediate type into a single composite function:

     <pre><code>
     public static &lt;A,B,C&gt; Function1&lt;A,C&gt; chain2(final Function1&lt;A,B&gt; f1, final Function1&lt;B,C&gt; f2) {
         return new Function1&lt;A,C&gt;() {
             &#64;Override
             public C applyEx(A a) throws Exception {
                 return f2.applyEx(f1.applyEx(a));
             }
         };
     }</code></pre>

     Even with 2 arguments, there are several signatures that would work: imagine where A=B, B=C,
     or A=C.  I just don't see the value to providing a bunch of chain2(), chain3() etc. functions
     that will ultimately not be type-safe and cannot perform optimizations for you, when you can
     roll your own type safe versions as you need them.  Only the simplest case seems worth
     providing, along the lines of the and() helper function in Filter()

     @param in the functions to applyEx in order.  Nulls and IDENTITY functions are ignored.
     No functions means IDENTITY.

     @param <V> the type of object to chain functions on

     @return a function which applies all the given functions in order.
     */
    @SafeVarargs
    public static <V> Function1<V,V> compose(Function1<V,V>... in) {
        if ( (in == null) || (in.length < 1) ) {
            return identity();
        }
        final List<Function1<V,V>> out = new ArrayList<>();
        for (Function1<V,V> f : in) {
            if ((f == null) || (f == IDENTITY)) {
                continue;
            }
            out.add(f);
        }
        if (out.size() < 1) {
            return identity(); // No functions means to return the original item
        } else if (out.size() == 1) {
            return out.get(0);
        } else {
            return new Function1<V,V>() {
                @Override
                public V applyEx(V v) throws Exception {
                    V ret = v;
                    for (Function1<V,V> f : out) {
                        ret = f.applyEx(ret);
                    }
                    return ret;
                }
            };
        }
    }

//    /**
//     Composes multiple functions into a single function to potentially minimize trips through
//     the source data.  The resultant function will loop through the functions for each item in the
//     source, but for few filters and many source items, that takes less memory.  Considers no
//     function to mean the identity function.  This decision is based on the way filters work and
//     may or may not prove useful in practice.  Please use the identity()/IDENTITY
//     sentinel value in this abstract class since function comparison is done by reference.
//
//     @param in the functions to applyEx in order.  Nulls and IDENTITY functions are ignored.
//     No functions means IDENTITY.
//
//     @param <V> the type of object to chain functions on
//
//     @return a function which applies all the given functions in order.
//     */
//    public static <A,B,C> Function1<A,C> chain2(final Function1<A,B> f1, final Function1<B,C> f2) {
//        return new Function1<A,C>() {
//            @Override
//            public C applyEx(A a) throws Exception {
//                return f2.applyEx(f1.applyEx(a));
//            }
//        };
//    }

// Don't think this is necessary.  Is it?
//    default Function<T,U> asFunction() {
//        return (T t) -> apply(t);
//    }

    static <S> Function1<S,Boolean> or(Function1<S,Boolean> a, Function1<S,Boolean> b) {
        return  a == ACCEPT ? a : // If any are true, all are true.  Accept is always true, so no composition necessary.
                a == REJECT ? b : // return whatever b is.
                b == ACCEPT ? b : // If any are true, all are true.
                b == REJECT ? a : // Just amounts to if a else false, no composition necessary.
                (S s) -> (a.apply(s) == Boolean.TRUE) || (b.apply(s) == Boolean.TRUE); // compose new function.
    }

    static <S> Function1<S,Boolean> and(Function1<S,Boolean> a, Function1<S,Boolean> b) {
        return  a == ACCEPT ? b : // return whatever b is.
                a == REJECT ? a : // if any are false, all are false.  No composition necessary.
                b == ACCEPT ? a : // Just amounts to if a else false, no composition necessary.
                b == REJECT ? b : // If any are false, all are false.
                (S s) -> (a.apply(s) == Boolean.TRUE) && (b.apply(s) == Boolean.TRUE); // compose new fn
    }

    static <S> Function1<S,Boolean> negate(Function1<? super S,Boolean> a) {
        return  a == ACCEPT ? reject() :
                a == REJECT ? accept() :
                        (S s) -> (a.apply(s) == Boolean.TRUE) ? Boolean.FALSE : Boolean.TRUE;
    }

    /** A predicate that always returns true.  Use accept() for a type-safe version of this predicate. */
    public static final Function1<Object,Boolean> ACCEPT = new Function1<Object,Boolean>() {
        @Override public Boolean applyEx(Object t) { return Boolean.TRUE; }
    };

    /** A predicate that always returns false. Use reject() for a type-safe version of this predicate. */
    public static final Function1<Object,Boolean> REJECT = new Function1<Object,Boolean>() {
        @Override public Boolean applyEx(Object t) { return Boolean.FALSE; }
    };

    /** Returns a type-safe version of the ACCEPT predicate. */
    @SuppressWarnings("unchecked")
    public static <T> Function1<T,Boolean> accept() { return (Function1<T,Boolean>) ACCEPT; }

    /** Returns a type-safe version of the REJECT predicate. */
    @SuppressWarnings("unchecked")
    public static <T> Function1<T,Boolean> reject() { return (Function1<T,Boolean>) REJECT; }

    /**
     Composes multiple predicates into a single predicate to potentially minimize trips through
     the source data.  The resultant predicate will loop through the predicates for each item in
     the source, but for few predicates and many source items, that takes less memory.  Considers
     no predicate to mean "accept all."  Use only accept()/ACCEPT and reject()/REJECT since
     function comparison is done by reference.

     @param in the predicates to test in order.  Nulls and ACCEPT predicates are ignored.  Any
     REJECT predicate will cause this entire method to return a single REJECT predicate.  No
     predicates means ACCEPT.

     @param <T> the type of object to predicate on.

     @return a predicate which returns true if all the input predicates return true, false otherwise.
     */
    public static <T> Function1<T,Boolean> and(View<Function1<T,Boolean>> in) {
        if (in == null) { return accept(); }

        return in
                .filter(p -> (p != null) && (p != ACCEPT))
                .foldLeft(accept(),
                          (accum, p) -> (p == REJECT)
                                        ? p
                                        : and(accum, p),
                          accum -> accum == REJECT);

    }

    /** A convenience wrapper for and().  This may be a bad idea.  Not sure yet. */
    @SafeVarargs // Not really sure how safe these varargs are...
    public static <T> Function1<T,Boolean> andArray(Function1<T,Boolean>... in) {
        return and(View.of(in));
    }

    /**
     Composes multiple predicates into a single predicate to potentially minimize trips through
     the source data.  The resultant predicate will loop through the predicates for each item in
     the source, but for few predicates and many source items, that takes less memory.  Considers
     no predicate to mean "reject all."  Use only accept()/ACCEPT and reject()/REJECT since
     function comparison is done by reference.

     @param in the predicates to test in order.  Nulls and REJECT predicates are ignored.  Any
     ACCEPT predicate will cause this entire method to return the ACCEPT predicate.
     No predicates means REJECT.

     @param <T> the type of object to predicate on.

     @return a predicate which returns true if any of the input predicates return true,
     false otherwise.
     */
    public static <T> Function1<T,Boolean> or(View<Function1<T,Boolean>> in) {
        if (in == null) { return reject(); }

        return in
                .filter(p -> (p != null) && (p != REJECT))
                .foldLeft(reject(),
                          (accum, p) -> (p == ACCEPT)
                                        ? p
                                        : or(accum, p),
                          accum -> accum == ACCEPT);
    }

    /** A convenience wrapper for of().  This may be a bad idea.  Not sure yet. */
    @SafeVarargs
    public static <T> Function1<T,Boolean> orArray(Function1<T,Boolean>... in) {
        return or(View.of(in));
    }

    enum BooleanCombiner {
        AND {
            @Override
            public <T> Function1<T,Boolean> combine(View<Function1<T,Boolean>> in) {
                return and(in);
            }
            @Override
            @SafeVarargs
            public final <T> Function1<T,Boolean> combineArray(Function1<T,Boolean>... in) {
                return andArray(in);
            }
        },
        OR {
            @Override
            public <T> Function1<T,Boolean> combine(View<Function1<T,Boolean>> in) {
                return or(in);
            }
            @Override
            @SafeVarargs
            public final <T> Function1<T,Boolean> combineArray(Function1<T,Boolean>... in) {
                return orArray(in);
            }
        };
        public abstract <T> Function1<T,Boolean> combine(View<Function1<T,Boolean>> in);

        @SuppressWarnings("unchecked")
        public abstract <T> Function1<T,Boolean> combineArray(Function1<T,Boolean>... in);
    }

    /**
     Use only on pure functions with no side effects.  Wrap an expensive function with this and for each input
     value, the output will only be computed once.  Subsequent calls with the same input will return identical output
     very quickly.  Please note that the return values from f need to implement equals() and hashCode() correctly
     for this to work correctly and quickly.
     */
    static <A,B> Function1<A,B> memoize(Function1<A,B> f) {
        return new Function1<A, B>() {
            private final Map<A,Option<B>> memo = new HashMap<>();
            @Override
            public synchronized B applyEx(A a) throws Exception {
                Option<B> val = memo.get(a);
                if ( (val != null) && val.isSome() ) { return val.get(); }
                B ret = f.apply(a);
                memo.put(a, Option.of(ret));
                return ret;
            }
        };
    }
}
