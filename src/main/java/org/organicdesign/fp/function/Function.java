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

import java.util.Objects;

import org.organicdesign.fp.FunctionUtils;

/**
 This is like Java 8's java.util.function.Function, but retrofitted to turn checked exceptions
 into unchecked ones in Java 5, 6, and 7.
 */
public abstract class Function<T,R> {
    /** Implement this one method and you don't have to worry about checked exceptions. */
    public abstract R apply(T t) throws Exception;

    /** The caller should use this convenience method to avoid checked exceptions. */
    public R apply_(T t) {
        try {
            return apply(t);
        } catch (Exception e) {
            throw (RuntimeException) e;
        }
    }

    @SuppressWarnings("unchecked")
    public <V> Function<V,R> compose(final Function<? super V,T> before) {
        Objects.requireNonNull(before);
        if (before == FunctionUtils.IDENTITY) {
            // This violates type safety, but makes sense - composing any function with the
            // identity function should return the original function unchanged.  If you mess up the
            // types, then that's your problem.  With generics and type erasure this may be the
            // best you can do.
            return (Function<V,R>) this;
        }
        final Function<T,R> parent = this;
        return new Function<V, R>() {
            @Override
            public R apply(V v) throws Exception {
                return parent.apply(before.apply(v));
            }
        };
    }

    // From Java 8.  I think compose() would be sufficient.
    @SuppressWarnings("unchecked")
    public <V> Function<T, V> andThen(final Function<? super R,V> after) {
        Objects.requireNonNull(after);
        if (after == FunctionUtils.IDENTITY) {
            return (Function<T,V>) after;
        }
        final Function<T,R> parent = this;
        return new Function<T, V>() {
            @Override
            public V apply(T t) throws Exception {
                return after.apply(parent.apply(t));
            }
        };
    }
}
