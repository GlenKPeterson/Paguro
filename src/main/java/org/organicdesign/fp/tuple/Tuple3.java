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

public class Tuple3<T,U,V> {
    private final T _1;
    private final U _2;
    private final V _3;
    private Tuple3(T t, U u, V v) { _1 = t; _2 = u; _3 = v; }
    public static <T,U,V> Tuple3<T,U,V> of(T first, U second, V third) {
        return new Tuple3<>(first, second, third);
    }
    public T _1() { return _1; }
    public U _2() { return _2; }
    public V _3() { return _3; }

    @Override
    public String toString() {
        return new StringBuilder("(")
                .append(_1).append(",")
                .append(_2).append(",")
                .append(_3).append(")").toString();
    }

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

        if (this._1 == null) {
            if (that._1 != null) { return false; }
        } else if ( !this._1.equals(that._1) ) {
            return false;
        }

        if (this._2 == null) {
            if (that._2 != null) { return false; }
        } else if ( !this._2.equals(that._2) ) {
            return false;
        }

        if (this._3 == null) {
            if (that._3 != null) { return false; }
        } else if ( !this._3.equals(that._3) ) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int ret = 0;
        if (_1 != null) { ret = _1.hashCode(); }
        if (_2 != null) { ret ^= _2.hashCode(); }
        if (_3 != null) { ret ^= _3.hashCode(); }
        // If it's uninitialized, it's equal to every other uninitialized instance.
        return ret;
    }
}