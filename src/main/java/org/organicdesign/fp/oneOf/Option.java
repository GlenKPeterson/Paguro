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

package org.organicdesign.fp.oneOf;

import org.organicdesign.fp.function.Fn0;
import org.organicdesign.fp.function.Fn1;

import java.io.Serializable;
import java.util.Objects;

/**
 Indicates presence or absence of a value (null is a valid, present value).  None often means "end of stream" or
 "none found". This is NOT a type-safe null (unless you want that, in which case use the
 {@link #someOrNullNoneOf(Object)} static factory method).  You can think of this class as OneOf1OrNone.
 */
public interface Option<T> extends Serializable { // extends UnmodSortedIterable<T> {

    /** Return the value wrapped in this Option.  Only safe to call this on Some. */
    T get();

    /**
     If this is Some, return the value wrapped in this Option.  Otherwise, return the given value.
     */
    T getOrElse(T t);

    /**
     If this is Some, Apply the given function, else return None.  Use this to chain options
     together, failing fast at the first none() or continuing through as many operations that return
     some as possible.
     */
    <U> Option<U> then(Fn1<T,Option<U>> f);

    /** Is this Some? */
    boolean isSome();

    /** Pass in a function to execute if its Some and another to execute if its None. */
    <U> U match(Fn1<T,U> has, Fn0<U> hasNot);

    // ========================================== Static ==========================================
    /** None is a singleton and this is its only instance. */
    Option NONE = new None();

    /** Calling this instead of referring to NONE directly can make the type infrencer happy. */
    @SuppressWarnings("unchecked")
    static <T> Option<T> none() { return NONE; }

    /**
     Would {@link #some(Object)} be better for your purposes?  For some reason, this returns none
     if you pass it a none.  This is like flatmapping Options.  Is this good for chaining or
     is this just weird?  If the first, I'm going to rename it flatten() or flatmap() or something.
     If the second, it should be deleted.  Remember, you also have {@link Or} for chaining or
     returning an error which is probably a better choice than Option for that kind of thing.
     */
    @Deprecated static <T> Option<T> of(T t) {
        if (NONE.equals(t)) {
            return none();
        }
        return new Some<>(t);
    }

    /** Public static factory method for constructing the Some Option. */
    static <T> Option<T> some(T t) {
        return new Some<>(t);
    }

    /** Construct an option, but if t is null, make it None instead of Some. */
    static <T> Option<T> someOrNullNoneOf(T t) {
        if ( (t == null) || NONE.equals(t) ) {
            return none();
        }
        return new Some<>(t);
    }

    /** Represents the presence of a value, even if that value is null. */
    class Some<T> implements Option<T> {
        // For serializable.  Make sure to change whenever internal data format changes.
        private static final long serialVersionUID = 20160915081300L;

        private final T item;
        private Some(T t) { item = t; }

        //public static Some<T> of(T t) { return new Option(t); }

        /** {@inheritDoc} */
        @Override public T get() { return item; }

        /** {@inheritDoc} */
        @Override public T getOrElse(T t) { return item; }

        /** {@inheritDoc} */
        @Override public boolean isSome() { return true; }
//
//        @Override public UnmodSortedIterator<T> iterator() {
//            return new UnmodSortedIterator<T>() {
//                private boolean hasNext = true;
//                @Override public boolean hasNext() { return hasNext; }
//                @Override public T next() {
//                    if (hasNext) {
//                        hasNext = false;
//                        return item;
//                    }
//                    throw new NoSuchElementException();
//                }
//            };
//        }

        /** {@inheritDoc} */
        @Override public <U> U match(Fn1<T,U> has, Fn0<U> hasNot) {
            return has.apply(item);
        }

        /** {@inheritDoc} */
        @Override public <U> Option<U> then(Fn1<T,Option<U>> f) { return f.apply(item); }

        /** Valid, but deprecated because it's usually an error to call this in client code. */
        @Deprecated // Has no effect.  Darn!
        @Override public int hashCode() {
            // We return Integer.MIN_VALUE for null to make it different from None which always
            // returns zero.
            return item == null ? Integer.MIN_VALUE : item.hashCode();
        }

        /** Valid, but deprecated because it's usually an error to call this in client code. */
        @Deprecated // Has no effect.  Darn!
        @Override public boolean equals(Object other) {
            if (this == other) { return true; }
            if ( !(other instanceof Option) ) { return false; }

            final Option that = (Option) other;
            return that.isSome() && Objects.equals(this.item, that.get());
        }
    }
}
