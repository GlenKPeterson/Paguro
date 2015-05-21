// Copygood 2015 PlanBase Inc. & Glen Peterson
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
 `Or` represents the presence of a successful outcome, or an error.
 Contrast this with Option which represents the presence or absence of a value.
 Option.Some and Or.Good are just about identical.  Unlike Option.None, Bad contains an error code or value.
 <a href="https://www.youtube.com/watch?v=bCTZQi2dpl8" target="_blank">Bill Venners, Scalactic, SuperSafe, and
 Functional Error Handling talk at SF Scala 2015-02-24</a> convinced me that Or is friendlier than
 <a href="http://www.scala-lang.org/api/rc2/scala/Either.html" target="_blank">Either</a>.
 This class is based on Bill Venners' Or.  I did not make Every, One, and Many sub-classes, figuring that you can
 make an Or&lt;GoodType,ImList&lt;BadType&gt;&gt; if you expect that.

 Bill makes the point that there are still some reasons to throw exceptions, but he says to "Throw exceptions at
 developers, not at code" meaning that if there's code in your program that can recover from the issue, use a
 functional return type (like Or).  Throw exceptions for things a program can't handle without developer intervention.

 Any errors are my own.
 */
public interface Or<G,B> {
    /** Returns true if this Or has a good value. */
    boolean isGood();
    /** Returns true if this Or has a bad value. */
    boolean isBad();

    /** Returns the good value if this is a Good, or throws an exception if this is a Bad. */
    G good();

    /** Returns the bad value if this is a Bad, or throws an exception if this is a Good. */
    B bad();

    /** Construct a new Good from the given object. */
    static <G,B> Good<G,B> good(G good) { return new Good<>(good); }

    /** Construct a new Bad from the given object. */
    static <G,B> Bad<G,B> bad(B bad) { return new Bad<>(bad); }

    /**
     Pattern-match, applying the first function if the given either is a Bad, the second if it's a Good.

     @param either the non-null Or to match against
     @param g the function to apply if the Or is Good
     @param b the function to apply if the Or is Bad
     @return the result of whichever function is applied.
     @throws IllegalArgumentException if either is null.
     */
    static <B,G,R> R patMatch(Or<G,B> either,
                              Function1<? super G,R> g,
                              Function1<? super B,R> b) {
        if (either == null) {
            throw new IllegalArgumentException("Can't handle a null either");
        }
        return either.isGood() ? g.apply(either.good())
                               : b.apply(either.bad());
    }

    /** Represents the presence of a Good value (and absence of a Bad). */
    class Good<G,B> implements Or<G,B> {
        private final G good;
        private Good(G r) { good = r; }

        /** Returns the good value. */
        @Override public G good() { return good; }
        /** Throws an UnsupportedOperationException because you can't ask a Good for a bad value. */
        @Deprecated
        @Override public B bad() {
            throw new UnsupportedOperationException("This Or does not have a bad.");
        }
        /** Returns true; */
        @Override public boolean isGood() { return true; }
        /** Returns false; */
        @Override public boolean isBad() { return false; }

        @Override public int hashCode() { return good.hashCode(); }

        @Override public boolean equals(Object other) {
            if (this == other) { return true; }
            if ( !(other instanceof Good) ) { return false; }
            final Good that = (Good) other;
            return Objects.equals(good, that.good());
        }
        @Override public String toString() { return "Good(" + String.valueOf(good) + ")"; }
    }

    /** Represents the presence of a Bad value (and absence of a Good). */
    class Bad<G,B> implements Or<G,B> {
        private final B bad;
        private Bad(B l) { bad = l; }

        /** Throws an UnsupportedOperationException because you can't ask a Bad for a good value. */
        @Deprecated
        @Override public G good() {
            throw new UnsupportedOperationException("This Or does not have a good.");
        }
        /** Returns the bad value. */
        @Override public B bad() { return bad; }
        /** Returns false; */
        @Override public boolean isGood() { return false; }
        /** Returns true; */
        @Override public boolean isBad() { return true; }

        @Override public int hashCode() {
            // Return the binary compliment of the bad hashCode, just so that a bad and a good of the same object
            // have opposite hashcodes.
            return ~ bad.hashCode();
        }
        @Override public boolean equals(Object other) {
            if (this == other) { return true; }
            if ( !(other instanceof Bad) ) { return false; }
            final Bad that = (Bad) other;
            return Objects.equals(bad, that.bad());
        }
        @Override public String toString() { return "Bad(" + String.valueOf(bad) + ")"; }
    }
} // end interface Or
