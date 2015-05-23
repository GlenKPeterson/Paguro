// Copyright 2013 PlanBase Inc. & Glen Peterson
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

import java.util.Map.Entry;
import java.util.Objects;

import org.organicdesign.fp.collections.UnMap;

/**
Holds 2 items of potentially different types, and implements Map.Entry (and UnMap.UnEntry (there is no ImMap.ImEntry)).
 */
public final class Tuple2<T,U> implements Entry<T,U>, UnMap.UnEntry<T,U> {
    private final T _1;
    private final U _2;
    private Tuple2(T t, U u) { _1 = t; _2 = u; }

    /** Public static factory method */
    public static <T,U> Tuple2<T,U> of(T first, U second) {
        return new Tuple2<>(first, second);
    }

    /**
     Returns the first field of the tuple (the Key if this is a Key/Value pair).  This field naming scheme is compatible
     with other (larger) tuples.
     */
    public T _1() { return _1; }

    /**
     Returns the second field of the tuple (the Value if this is a Key/Value pair)  This field naming scheme is
     compatible with other (larger) tuples.
     */
    public U _2() { return _2; }

    @Override
    public String toString() { return "Tuple2(" + _1 + "," + _2 + ")"; }

    @Override
    public boolean equals(Object other) {
        // Cheapest operation first...
        if (this == other) { return true; }
        if (!(other instanceof Entry)) { return false; }
        // Details...
        final Entry that = (Entry) other;
        return Objects.equals(_1, that.getKey()) && Objects.equals(_2, that.getValue());
    }

    @Override
    public int hashCode() {
        // This is specified in java.util.Map as part of the map contract.
        return  (_1 == null ? 0 : _1.hashCode()) ^
                (_2 == null ? 0 : _2.hashCode());
    }

    // Inherited from Map.Entry
    /** Returns the first field of the tuple.  This field naming scheme is to implement Map.Entry. */
    @Override public T getKey() { return _1; }
    /** Returns the second field of the tuple.  This field naming scheme is to implement Map.Entry. */
    @Override public U getValue() { return _2; }
    /** This method is required to implement Map.Entry, but calling it only issues an exception */
    @SuppressWarnings("deprecation")
    @Override @Deprecated public U setValue(U value) { throw new UnsupportedOperationException("Tuple2 is immutable"); }
}