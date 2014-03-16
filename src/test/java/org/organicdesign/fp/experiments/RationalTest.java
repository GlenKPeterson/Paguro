// Copyright (c) 2014-03-09 PlanBase Inc. & Glen Peterson
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

package org.organicdesign.fp.experiments;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.organicdesign.fp.experiments.math.Rational;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class RationalTest {
    @Test(expected = IllegalArgumentException.class)
    public void factory1() {
        Rational.of(1, 0);
    }

    @Test
    public void factory2() {
        assertEquals(Rational.of(0, 1), Rational.ZERO);
        assertEquals(Rational.of(0, -999), Rational.ZERO);
        assertEquals(Rational.of(0, 17), Rational.ZERO);

        assertEquals(Rational.of(-999, -999), Rational.ONE);
        assertEquals(Rational.of(17, 17), Rational.ONE);

        assertEquals(Rational.of(4, 2), Rational.of(2, 1));
        assertEquals(Rational.of(6, 3), Rational.of(2, 1));
        assertEquals(Rational.of(8, 4), Rational.of(2, 1));
        assertEquals(Rational.of(10, 5), Rational.of(2, 1));

        assertEquals(Rational.of(-4, 2), Rational.of(-2, 1));
        assertEquals(Rational.of(6, -3), Rational.of(-2, 1));
        assertEquals(Rational.of(-8, -4), Rational.of(2, 1));
        assertEquals(Rational.of(10, -5), Rational.of(-2, 1));

        assertEquals(Rational.of(2, 4), Rational.of(1, 2));
        assertEquals(Rational.of(3, 6), Rational.of(1, 2));
        assertEquals(Rational.of(4, 8), Rational.of(1, 2));
        assertEquals(Rational.of(5, 10), Rational.of(1, 2));

        assertEquals(Rational.of(-2, -4), Rational.of(1, 2));
        assertEquals(Rational.of(3, -6), Rational.of(-1, 2));
        assertEquals(Rational.of(4, -8), Rational.of(-1, 2));
        assertEquals(Rational.of(-5, 10), Rational.of(-1, 2));

    }

    @Test
    public void plus() {
        assertEquals(Rational.ONE.plus(Rational.ONE), Rational.of(2, 1));
        assertEquals(Rational.ONE.plus(Rational.ONE).plus(Rational.ONE), Rational.of(3, 1));
        // 1/3 + 7/8 = 8/24 + 21/24 = 29/24
        assertEquals(Rational.of(2, 6).plus(Rational.of(7, 8)), Rational.of(29, 24));
    }

    @Test
    public void floor() {
        assertEquals(Rational.of(24, 24).floor(), 1);
        assertEquals(Rational.of(25, 24).floor(), 1);
        assertEquals(Rational.of(29, 24).floor(), 1);
        assertEquals(Rational.of(47, 24).floor(), 1);

        assertEquals(Rational.of(1, -24).floor(), -1);
        assertEquals(Rational.of(-17, 24).floor(), -1);
        assertEquals(Rational.of(23, -24).floor(), -1);
        assertEquals(Rational.of(-24, 24).floor(), -1);
    }

    @Test
    public void ceiling() {
        assertEquals(Rational.of(1, 24).ceiling(), 1);
        assertEquals(Rational.of(17, 24).ceiling(), 1);
        assertEquals(Rational.of(23, 24).ceiling(), 1);
        assertEquals(Rational.of(24, 24).ceiling(), 1);

        assertEquals(Rational.of(-24, 24).ceiling(), -1);
        assertEquals(Rational.of(25, -24).ceiling(), -1);
        assertEquals(Rational.of(-29, 24).ceiling(), -1);
        assertEquals(Rational.of(47, -24).ceiling(), -1);
    }
}
