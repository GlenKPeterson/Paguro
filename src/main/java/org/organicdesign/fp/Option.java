// Copyright (c) 2014-03-09 PlanBase Inc. & Glen Peterson
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

package org.organicdesign.fp;

import java.lang.Deprecated;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 This is NOT a type-safe null.  Null is a valid value for a Some.  It's more to indicate
 the presence or absence of a value, or indicate end-of-stream.
 @param <T>
 */
public interface Option<T> {

    public static final Option NONE = new None();

    @SuppressWarnings("unchecked")
    public static <T> Option<T> none() { return NONE; }

    public static <T> Option<T> of(T t) {
        if (NONE.equals(t)) {
            return none();
        }
        return new Some<>(t);
    }

    public static <T> Option<T> someOrNullNoneOf(T t) {
        if ( (t == null) || NONE.equals(t) ) {
            return none();
        }
        return new Some<>(t);
    }

    T get();
    T getOrElse(T t);
    boolean isSome();
    <U> U patMat(Function<T,U> has, Supplier<U> hasNot);

    static class None<T> implements Option<T> {
        //private None();

        @Override
        public T get() { throw new IllegalStateException("Called get on None"); }

        @Override
        public T getOrElse(T t) { return t; }

        @Override
        public boolean isSome() { return false; }

        @Override
        public <U> U patMat(Function<T,U> has, Supplier<U> hasNot) {
            return hasNot.get();
        }

        /** Valid, but deprecated because it's usually an error to call this in client code. */
        @Deprecated // Has no effect.  Darn!
        @Override
        public int hashCode() { return 0; }

        /** Valid, but deprecated because it's usually an error to call this in client code. */
        @Deprecated // Has no effect.  Darn!
        @Override
        public boolean equals(Object other) {
            if (this == other) { return true; }
            return (other != null) && (other instanceof None);
        }
    }

    public static class Some<T> implements Option<T> {
        private final T item;
        private Some(T t) { item = t; }

        //public static Some<T> of(T t) { return new Option(t); }

        @Override
        public T get() { return item; }

        @Override
        public T getOrElse(T t) { return item; }

        @Override
        public boolean isSome() { return true; }

        @Override
        public <U> U patMat(Function<T,U> has, Supplier<U> hasNot) {
            return has.apply(item);
        }

        /** Valid, but deprecated because it's usually an error to call this in client code. */
        @Deprecated // Has no effect.  Darn!
        @Override
        public int hashCode() {
            return item.hashCode();
        }

        /** Valid, but deprecated because it's usually an error to call this in client code. */
        @Deprecated // Has no effect.  Darn!
        @Override
        public boolean equals(Object other) {
            // Cheapest operation first...
            if (this == other) { return true; }

            if ((other == null) ||
                !(other instanceof Option) ||
                (this.hashCode() != other.hashCode())) {
                return false;
            }
            // Details...
            final Some that = (Some) other;

            // If this is not a database object, compare "significant" fields here.
            return this.item.equals(that.item);
        }
    }
}
