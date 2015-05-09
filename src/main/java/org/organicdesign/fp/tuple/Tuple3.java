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

import java.util.Objects;

/**
 Use tuples as immutable, type-safe, data structures instead of defining your own classes (when appropriate).
 Defining your own class is better for building models, but tuples can be more convenient, especially for adapter code.
 */
public class Tuple3<T,U,V> {
    private final T _1;
    private final U _2;
    private final V _3;
    private Tuple3(T t, U u, V v) { _1 = t; _2 = u; _3 = v; }

    /** Public static factory method */
    public static <T,U,V> Tuple3<T,U,V> of(T first, U second, V third) { return new Tuple3<>(first, second, third); }

    /** Returns the first field of the tuple */
    public T _1() { return _1; }

    /** Returns the second field of the tuple */
    public U _2() { return _2; }

    /** Returns the third field of the tuple */
    public V _3() { return _3; }

    @Override
    public String toString() { return "(" + _1 + "," + _2 + "," + _3 + ")"; }

    @Override
    public boolean equals(Object other) {
        // Cheapest operation first...
        if (this == other) { return true; }
        if ((other == null) ||
            !(other instanceof Tuple3) ||
            (this.hashCode() != other.hashCode())) {
            return false;
        }
        // Details...
        @SuppressWarnings("rawtypes") final Tuple3 that = (Tuple3) other;

        return Objects.equals(this._1, that._1()) &&
               Objects.equals(this._2, that._2()) &&
               Objects.equals(this._3, that._3());
    }

    @Override
    public int hashCode() {
        int ret = 0;
        if (_1 != null) { ret = _1.hashCode(); }
        if (_2 != null) { ret += _2.hashCode(); }
        if (_3 != null) { ret += _3.hashCode(); }
        // If it's uninitialized, it's equal to every other uninitialized instance.
        return ret;
    }
}