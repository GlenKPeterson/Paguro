// Copyright 2016 PlanBase Inc. & Glen Peterson
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

import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.organicdesign.fp.collections.UnmodMap;

import static org.organicdesign.fp.FunctionUtils.stringify;

/**
 Holds 2 items of potentially different types, and implements Map.Entry (and UnmodMap.UnEntry
 (there is no ImMap.ImEntry)).  Designed to let you easily create immutable subclasses (to give your
 data structures meaningful names) with correct equals(), hashCode(), and toString() methods.
 */
public class Tuple2<A,B> implements Entry<A,B>, UnmodMap.UnEntry<A,B>, Serializable {

    // For serializable.  Make sure to change whenever internal data format changes.
    private static final long serialVersionUID = 20160906065000L;

    // Fields are protected so that sub-classes can make accessor methods with meaningful names.
    protected final A field1;
    protected final B field2;

    /**
     Constructor is protected (not public) for easy inheritance.  Josh Bloch's "Item 1" says public
     static factory methods are better than constructors because they have names, they can return
     an existing object instead of a new one, and they can return a sub-type.  Therefore, you
     have more flexibility with a static factory as part of your public API then with a public
     constructor.
     */
    protected Tuple2(A a, B b) {
        field1 = a; field2 = b;
    }

    /** Public static factory method */
    public static <A,B> @NotNull Tuple2<A,B> of(A a, B b) {
        return new Tuple2<>(a, b);
    }

    /** Map.Entry factory method */
    public static <K,V> @NotNull Tuple2<K,V> of(Map.Entry<K,V> entry) {
        // Protect against multiple-instantiation
        if (entry instanceof Tuple2) {
            return (Tuple2<K,V>) entry;
        }
        return new Tuple2<>(entry.getKey(), entry.getValue());
    }

    /** Returns the 1st field */
    public A _1() { return field1; }
    /** Returns the 2nd field */
    public B _2() { return field2; }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
               stringify(field1) + "," +
               stringify(field2) + ")";
    }

    @Override
    public boolean equals(Object other) {
        // Cheapest operation first...
        if (this == other) { return true; }
        if (!(other instanceof Entry)) { return false; }
        // Details...
        @SuppressWarnings("rawtypes") final Entry that = (Entry) other;

        return Objects.equals(field1, that.getKey()) &&
               Objects.equals(field2, that.getValue());
    }

    @Override
    public int hashCode() {
        // This is specified in java.util.Map as part of the map contract.
        return  (field1 == null ? 0 : field1.hashCode()) ^
                (field2 == null ? 0 : field2.hashCode());
    }

    // Inherited from Map.Entry
    /** Returns the first field of the tuple.  To implement Map.Entry. */
    @Contract(pure = true)
    @Override public A getKey() { return field1; }

    /** Returns the second field of the tuple.  To implement Map.Entry. */
    @Override
    @Contract(pure = true)
    public B getValue() { return field2; }

    /** This method is required to implement Map.Entry, but calling it only issues an exception */
    @SuppressWarnings("deprecation")
    @Override
    @Deprecated
    public B setValue(B value) {
        throw new UnsupportedOperationException("Tuple2 is immutable");
    }
}