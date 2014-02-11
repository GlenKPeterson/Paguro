// Copyright 2013-12-31 PlanBase Inc. & Glen Peterson
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
 This is a bit like Java 8's java.util.function.Predicate, but retrofitted to turn checked
 exceptions into unchecked ones in Java 5, 6, and 7.  I originally called this Filter and used
 apply() as the main method name to work more like the other functions, but Java 8 uses Predicate
 and test().  Predicate is a fine name, but using a different method name from other
 functions is just ugly.
 */
public abstract class Predicate<T> {
    /** Implement this one method and you don't have to worry about checked exceptions. */
    public abstract boolean test(T t) throws Exception;

    /**
     The class that takes a consumer as an argument uses this convenience method so that it
     doesn't have to worry about checked exceptions either.
     */
    public boolean test_(T t) {
        try {
            return test(t);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public Predicate<T> and(final Predicate<? super T> other) {
        final Predicate<T> parent = this;
        Objects.requireNonNull(other);
        return new Predicate<T>() {
            @Override
            public boolean test(T t) throws Exception {
                return parent.test(t) && other.test(t);
            }
        };
    }

    public Predicate<T> negate() {
        final Predicate<T> parent = this;
        return new Predicate<T>() {
            @Override
            public boolean test(T t) throws Exception {
                return !parent.test(t);
            }
        };
    }

    public Predicate<T> or(final Predicate<? super T> other) {
        Objects.requireNonNull(other);
        final Predicate<T> parent = this;
        return new Predicate<T>() {
            @Override
            public boolean test(T t) throws Exception {
                return parent.test(t) || other.test(t);
            }
        };
    }


    /** Returns a filter that returns the boolean opposite of the given filter. */
    public static <T> Predicate<T> not(final Predicate<T> f) {
        if (FunctionUtils.ACCEPT == f) { return FunctionUtils.reject(); }
        if (FunctionUtils.REJECT == f) { return FunctionUtils.accept(); }
        return new Predicate<T>() {
            @Override
            public boolean test(T t) throws Exception {
                return !f.test(t);
            }
        };
    }

}
