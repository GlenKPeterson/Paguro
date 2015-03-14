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

public class Tuple2<T,U> implements Entry<T,U> {
    private final T _1;
    private final U _2;
    private Tuple2(T t, U u) { _1 = t; _2 = u; }
    public static <T,U> Tuple2<T,U> of(T first, U second) {
        return new Tuple2<>(first, second);
    }
    public T _1() { return _1; }
    public U _2() { return _2; }

    @Override
    public String toString() { return "(" + _1 + "," + _2 + ")"; }

    @Override
    public boolean equals(Object other) {
        // Cheapest operation first...
        if (this == other) { return true; }
        if ((other == null) ||
            !(other instanceof Tuple2) ||
            (this.hashCode() != other.hashCode())) {
            return false;
        }
        // Details...
        @SuppressWarnings("rawtypes") final Tuple2 that = (Tuple2) other;

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
        return true;
    }

    @Override
    public int hashCode() {
        int ret = 0;
        if (_1 != null) { ret = _1.hashCode(); }
        if (_2 != null) { return ret ^ _2.hashCode(); }
        // If it's uninitialized, it's equal to every other uninitialized instance.
        return ret;
    }

    // Inherited from Map.Entry
    @Override public T getKey() { return _1; }
    @Override public U getValue() { return _2; }
    @Override @Deprecated public U setValue(U value) { throw new UnsupportedOperationException("Tuple2 is immutable"); }
}