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

public class Rational {
    public static final Rational NEG_ONE = new Rational(-1,1);
    public static final Rational ZERO = new Rational(0,1) {
        @Override
        public Rational plus(Rational r) { return r; }
    };
    public static final Rational ONE = new Rational(1,1);

    private final long numerator;
    private final long denominator;

    Rational(long n, long d) { numerator = n; denominator = d; }

    public static Rational of(long n, long d) {
        // Check special cases
        if (d == 0) {
            throw new IllegalArgumentException("A rational number cannot have a denominator of zero.");
        }
        if (n == 0) { return ZERO; }
        // If the bottom number is negative flip the signs.  This makes them both positive
        // if they are both negative (a negative divided by a negative is a positive) and
        // it puts the sign in the numerator if they have opposite signs.
        if (d < 0) {
            n = 0 - n;
            d = 0 - d;
        }
        // Reduce
        long gcd = gcd(abs(n), abs(d));
        if (gcd > 1) {
            n = n / gcd;
            d = d / gcd;
        }
        // Check for cached instances
        if (d == 1) {
            if (n == 1) { return ONE; }
            if (n == -1) { return NEG_ONE; }
        }
        return new Rational(n, d);
    }
    public static Rational of(long i) { return Rational.of(i, 1); }

    public Rational negate(Rational r) { return Rational.of(-r.numerator, r.denominator); }

    public Rational plus(Rational r) {
        if (denominator == r.denominator) {
            return Rational.of(numerator + r.numerator, denominator);
        }
        // Could check for common divisor of the denominators to keep numbers smaller
        // and prevent an overflow, but probably doesn't matter and probably faster this way.
        return Rational.of((r.denominator * numerator) + (denominator * r.numerator),
                           (denominator * r.denominator));
    }
    public Rational minus(Rational r) { return plus(negate(r)); }
    public Rational minus(int i) { return plus(negate(Rational.of(i, 1))); }

    public boolean lt(Rational r) { return this.minus(r).numerator < 0; }
    public boolean lte(Rational r) { return this.minus(r).numerator <= 0; }
    public boolean lte(long i) { return this.minus(Rational.of(i, 1)).numerator <= 0; }
    public boolean eq(Rational r) { return this.equals(r); }

    // Always less than or equal to the quotient, even for negative numbers.
    // NOT closest to zero.
    public long floor() {
        long ret = numerator / denominator;
        if ((numerator < 0) && ((numerator % denominator) != 0)) {
            return ret - 1;
        }
        return ret;
    }

    // Always greater than or equal to the quotient, even for negative numbers.
    // NOT closest to zero.
    public long ceiling() {
        long ret = numerator / denominator;
        if ((numerator > 0) && ((numerator % denominator) != 0)) {
            return ret + 1;
        }
        return ret;
    }

    private static long abs(long i) { return (i < 0) ? 0 - i : i; }

    // Euclid's Algorithm takes advantage of the fact that the gcd of two numbers also divides
    // their difference.
    private static long gcd(long a, long b) {
        if (b == 0) { return a; }
        return gcd(b, a % b);
    }

    @Override
    public String toString() {
        return new StringBuilder().append(numerator).append("/").append(denominator)
                .toString();
    }

    @Override
    public int hashCode() { return (int) (numerator + denominator); }

    @Override
    public boolean equals(Object other) {
        if (this == other) { return true; }
        if ( (other == null) ||
             !(other instanceof Rational) ||
             (this.hashCode() != other.hashCode()) ) {
            return false;
        }
        Rational that = (Rational) other;
        return (denominator == that.denominator) && (numerator == that.numerator);
    }
}
