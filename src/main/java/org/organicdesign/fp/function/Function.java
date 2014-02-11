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
import java.util.List;

/**
 This is like Java 8's java.util.function.Function, but retrofitted to turn checked exceptions
 into unchecked ones in Java 5, 6, and 7.
 */
public abstract class Function<T,U> {
    /** Implement this one method and you don't have to worry about checked exceptions. */
    public abstract U apply(T t) throws Exception;

    /**
     The class that takes a consumer as an argument uses this convenience method so that it
     doesn't have to worry about checked exceptions either.
     */
    public U apply_(T t) {
        try {
            return apply(t);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public <S> Function<S,U> compose(final Function<S,T> f) {
        if (f == IDENTITY) {
            // This violates type safety, but makes sense - composing any function with the
            // identity function should return the original function unchanged.  If you mess up the
            // types, then that's your problem.  With generics and type erasure this may be the
            // best you can do.
            return (Function<S,U>) this;
        }
        final Function<T,U> parent = this;
        return new Function<S, U>() {
            @Override
            public U apply(S s) throws Exception {
                return parent.apply(f.apply(s));
            }
        };
    }

    public static final Function<Object,Object> IDENTITY = new Function<Object,Object>() {

        @Override
        public Object apply(Object t) throws Exception {
            return t;
        }

        @Override
        public <S> Function<S, Object> compose(Function<S, Object> f) {
            // Composing any function with the identity function has no effect on the original
            // function (by definition of identity) - just return it.
            return f;
        }
    };

    @SuppressWarnings("unchecked")
    public static <V> Function<V,V> identity() { return (Function<V,V>) IDENTITY; }

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
     public static &lt;A,B,C> Function&lt;A,C> chain2(final Function&lt;A,B> f1, final Function&lt;B,C> f2) {
         return new Function&lt;A,C>() {
             @Override
             public C apply(A a) throws Exception {
                 return f2.apply(f1.apply(a));
             }
         };
     }</code></pre>

     Even with 2 arguments, there are several signatures that would work: imagine where A=B, B=C,
     or A=C.  I just don't see the value to providing a bunch of chain2(), chain3() etc. functions
     that will ultimately not be type-safe and cannot perform optimizations for you, when you can
     roll your own type safe versions as you need them.  Only the simplest case seems worth
     providing, along the lines of the and() helper function in Predicate()

     @param in the functions to apply in order.  Nulls and IDENTITY functions are ignored.
     No functions means IDENTITY.

     @param <V> the type of object to chain functions on

     @return a function which applies all the given functions in order.
     */
    @SafeVarargs
    public static <V> Function<V,V> compose(Function<V,V>... in) {
        if ( (in == null) || (in.length < 1) ) {
            return identity();
        }
        final List<Function<V,V>> out = new ArrayList<>();
        for (Function<V,V> f : in) {
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
            return new Function<V,V>() {
                @Override
                public V apply(V v) throws Exception {
                    V ret = v;
                    for (Function<V,V> f : out) {
                        ret = f.apply(ret);
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
//     @param in the functions to apply in order.  Nulls and IDENTITY functions are ignored.
//     No functions means IDENTITY.
//
//     @param <V> the type of object to chain functions on
//
//     @return a function which applies all the given functions in order.
//     */
//    public static <A,B,C> Function<A,C> chain2(final Function<A,B> f1, final Function<B,C> f2) {
//        return new Function<A,C>() {
//            @Override
//            public C apply(A a) throws Exception {
//                return f2.apply(f1.apply(a));
//            }
//        };
//    }

}
