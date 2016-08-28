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

package org.organicdesign.fp.tuple;

import java.util.Objects;

/**
 Holds 2 items of potentially different types. Designed to let you easily create immutable subclasses (to give your
 data structures meaningful names) with correct equals(), hashCode(), and toString() methods.
 A sub-class, @{link org.organicdesign.fp.collections.KeyValuePair} now implements Map.Entry, UnMap.UnEntry, and Serializable.
 */
public class Tuple2<A,B> {
    // Fields are protected so that sub-classes can make accessor methods with meaningful names.
    protected final A _1;
    protected final B _2;

    /**
     Constructor is protected (not public) for easy inheritance.  Josh Bloch's "Item 1" says public
     static factory methods are better than constructors because they have names, they can return
     an existing object instead of a new one, and they can return a sub-type.  Therefore, you
     have more flexibility with a static factory as part of your public API then with a public
     constructor.
     */
    protected Tuple2(A a, B b) { _1 = a; _2 = b; }

    /** Public static factory method */
    public static <A,B> Tuple2<A,B> of(A a, B b) {
        return new Tuple2<>(a, b);
    }

    /**
     Returns the first field of the tuple (the Key if this is a Key/Value pair).  This field naming
     scheme is compatible with other (larger) tuples.
     */
    public A _1() { return _1; }

    /**
     Returns the second field of the tuple (the Value if this is a Key/Value pair).  This field
     naming scheme is compatible with other (larger) tuples.
     */
    public B _2() { return _2; }

    @Override
    public String toString() { return getClass().getSimpleName() + "(" + _1 + "," + _2 + ")"; }

    @Override
    public boolean equals(Object other) {
        // Cheapest operation first...
        if (this == other) { return true; }
        if (!(other instanceof Tuple2)) { return false; }
        // Details...
        final Tuple2 that = (Tuple2) other;
        return Objects.equals(_1, that._1()) && Objects.equals(_2, that._2());
    }

    @Override
    public int hashCode() {
        // This is specified in java.util.Map as part of the map contract.
        return  (_1 == null ? 0 : _1.hashCode()) +
                (_2 == null ? 0 : _2.hashCode());
    }
}