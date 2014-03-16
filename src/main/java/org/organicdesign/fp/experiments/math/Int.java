// Copyright (c) 2014-03-08 PlanBase Inc. & Glen Peterson
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

package org.organicdesign.fp.experiments.math;

public class Int {
    public static final Int NEG_ONE = new Int(-1);
    public static final Int ZERO = new Int(0);
    public static final Int ONE = new Int(1);
    private static final Int TWO = new Int(2);
    private static final Int THREE = new Int(3);

    private final long value;

    private Int(long v) { value = v; }

    public static Int of(long v) {
        if ((v > 3) || (v < -1)) {
            return new Int(v);
        }
        return (v == 0) ? ZERO : (v == 1) ? ONE : (v == 2) ? TWO : (v == 3) ? THREE : NEG_ONE;
    }

    public Int negate() { return Int.of(-value); }

    public Int plus(Int i) { return Int.of(value + i.value); }
    public Int minus(Int i) { return Int.of(value - i.value); }
    public Int times(Int i) { return Int.of(value * i.value); }
    public Rational div(Int i) { return Rational.of(this.value, i.value); }

    public boolean eq(Int i) { return value == i.value; }
    public boolean gt(Int i) { return value > i.value; }
    public boolean gte(Int i) { return value >= i.value; }
    public boolean lt(Int i) { return value < i.value; }
    public boolean lte(Int i) { return value <= i.value; }

    public int toPrimitiveInt() { return (int) value; }
    public long toPrimitiveLong() { return value; }
    public Long toLongObj() { return Long.valueOf(value); }

    public Int next() { return Int.of(value + 1); }
    public Int previous() { return Int.of(value - 1); }

    @Override
    public String toString() { return String.valueOf(value); }

    @Override
    public int hashCode() { return toPrimitiveInt(); }

    @Override
    public boolean equals(Object other) {
        if (this == other) { return true; }
        if ( (other == null) ||
             !(other instanceof Int) ||
             (this.hashCode() != other.hashCode()) ) {
            return false;
        }
        Int that = (Int) other;
        return (value == that.value);
    }
}
