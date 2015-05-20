// Copyright 2015 PlanBase Inc. & Glen Peterson
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
package org.organicdesign.fp;

import java.util.Objects;

import org.organicdesign.fp.function.Function1;

/**
 Where Option repesents the presence or absence of a value, Either represents the presence of one value OR another.
 By convention, Left is often used like Option.None, but stores error codes or objects.
 Right is used like Option.Some.
 Loosely based on and/or inspired by Scala's
 <a href="http://www.scala-lang.org/api/rc2/scala/Either.html" target="_blank">Either</a>.
 */
public interface Either<L,R> {
    /** Returns true if this Either has a left value. */
    boolean isLeft();
    /** Returns true if this Either has a right value. */
    boolean isRight();

    /** Returns the left value if this is a Left, or throws an exception if this is a Right. */
    L left();

    /** Returns the right value if this is a Right, or throws an exception if this is a Left. */
    R right();

    /** Construct a new Left from the given object. */
    static <L,R> Left<L,R> left(L left) { return new Left<>(left); }

    /** Construct a new Right from the given object. */
    static <L,R> Right<L,R> right(R right) { return new Right<>(right); }

    static <L,R,T> T patMatch(Either<L,R> either,
                              Function1<L,T> l,
                              Function1<R,T> r) {
        if (either == null) {
            throw new IllegalArgumentException("Can't handle a null either");
        }
        return either.isLeft() ? l.apply(either.left())
                               : r.apply(either.right());
    }

    /** Represents the presence of a Left value (and absence of a Right). */
    class Left<L,R> implements Either<L,R> {
        private final L left;
        private Left(L l) { left = l; }

        /** Returns the left value. */
        @Override public L left() { return left; }
        /** Throws an UnsupportedOperationException because you can't ask a Left for a right value. */
        @Deprecated
        @Override public R right() {
            throw new UnsupportedOperationException("This Either does not have a right.");
        }
        /** Returns true; */
        @Override public boolean isLeft() { return true; }
        /** Returns false; */
        @Override public boolean isRight() { return false; }

        @Override public int hashCode() {
            // Return the binary compliment of the left hashCode, just so that a left and a right of the same object
            // have opposite hashcodes.
            return ~ left.hashCode();
        }
        @Override public boolean equals(Object other) {
            if (this == other) { return true; }
            if ( !(other instanceof Left) ) { return false; }
            final Left that = (Left) other;
            return Objects.equals(left, that.left());
        }
        @Override public String toString() { return "Left(" + String.valueOf(left) + ")"; }
    }

    /** Represents the presence of a Right value (and absence of a Left). */
    class Right<L,R> implements Either<L,R> {
        private final R right;
        private Right(R r) { right = r; }

        /** Throws an UnsupportedOperationException because you can't ask a Right for a left value. */
        @Deprecated
        @Override public L left() {
            throw new UnsupportedOperationException("This Either does not have a left.");
        }
        /** Returns the right value. */
        @Override public R right() { return right; }
        /** Returns false; */
        @Override public boolean isLeft() { return false; }
        /** Returns true; */
        @Override public boolean isRight() { return true; }

        @Override public int hashCode() { return right.hashCode(); }

        @Override public boolean equals(Object other) {
            if (this == other) { return true; }
            if ( !(other instanceof Right) ) { return false; }
            final Right that = (Right) other;
            return Objects.equals(right, that.right());
        }
        @Override public String toString() { return "Right(" + String.valueOf(right) + ")"; }
    }
} // end interface Either
