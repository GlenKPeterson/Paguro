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
package org.organicdesign.fp.oneOf;

import org.organicdesign.fp.type.RuntimeTypes;

import static org.organicdesign.fp.FunctionUtils.stringify;

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
public class Or<G,B> extends OneOf2<G,B> {
    @SuppressWarnings("unchecked")
    private Or(G g, B b, int s) { super(g, (Class<G>) Or.Good.class, b, (Class<B>) Or.Bad.class, s); }

    /** Construct a new Good from the given object. */
    public static <G,B> Or<G,B> good(G good) { return new Or<>(good, null, 0); }

    /** Construct a new Bad from the given object. */
    public static <G,B> Or<G,B> bad(B bad) { return new Or<>(null, bad, 1); }

    /** Returns true if this Or has a good value. */
    public boolean isGood() { return match(g -> true, f -> false); }
    /** Returns true if this Or has a bad value. */
    public boolean isBad() { return match(g -> false, f -> true); }

    /** Returns the good value if this is a Good, or throws an exception if this is a Bad. */
    public G good() {
        return match(g -> g,
                     super::throw2);
    }

    /** Returns the bad value if this is a Bad, or throws an exception if this is a Good. */
    public B bad() {
        return match(super::throw1,
                         b -> b);
    }

    /** Represents the presence of a Good value (and absence of a Bad). */
    private static final class Good { }

    /** Represents the presence of a Bad value (and absence of a Good). */
    private static final class Bad {}

    @Override public String toString() {
        return match(g -> "Good(" + stringify(g) + ")",
                     b -> "Bad(" + stringify(b) + ")");
    }
} // end interface Or