// Copyright 2015-03-06 PlanBase Inc. & Glen Peterson
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

import java.util.concurrent.Callable;
import java.util.function.Supplier;

/**
 This is like Java 8's java.util.function.Supplier, but retrofitted to turn checked exceptions
 into unchecked ones.  It's also called a thunk when used to delay evaluation.
 */
@FunctionalInterface
public interface Function0<U> extends Supplier<U>, Callable<U> {
    /** Implement this one method and you don't have to worry about checked exceptions. */
    U applyEx() throws Exception;

    /**
     The class that takes a consumer as an argument uses this convenience method so that it
     doesn't have to worry about checked exceptions either.
     */
    default U apply() {
        try {
            return applyEx();
        } catch (RuntimeException re) {
            throw re;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /** {@inheritDoc} */
    @Override default U get() { return apply(); }

    /** {@inheritDoc} */
    @Override default U call() throws Exception { return applyEx(); }

    // ==================================================== Static ====================================================
    public static final Function0<Object> NULL = new Function0<Object>() {
        @Override
        public Object applyEx() throws Exception {
            return null;
        }
    };

    /**
     Wraps a value in a constant function.  If you need to "memoize" some really expensive operation, use it to wrap
     a LazyRef.
     */
    static <K> Function0<K> constantFunction(final K k) {
        return new Function0<K>() {
            @Override public K applyEx() {
                return k;
            }
            @Override public int hashCode() { return (k == null) ? 0 : k.hashCode(); }
            @Override public boolean equals(Object o) {
                if (this == o) { return true; }
                if ( (o == null) || !(o instanceof Supplier) ) { return false; }
                return k.equals(((Supplier) o).get());
            }
            @Override public String toString() { return "() -> " + k; };
        };
    }

//    /**
//     Use only on pure functions with no side effects.
//     In this case, that means a constant function (always returns the same value).
//     */
//    static <T> Function0<T> memoize(Function0<T> f) {
//        return new Function0<T>() {
//            LazyRef<T> ref = LazyRef.of(() -> f.apply());
//            @Override public T applyEx() throws Exception {
//                return ref.get();
//            }
//        };
//    }
// Don't think this is necessary.  Is it?
//    default Supplier<U> asSupplier() {
//        return () -> apply();
//    }
}
