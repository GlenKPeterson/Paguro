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

import org.junit.Test;
import org.organicdesign.fp.oneOf.Or.*;

import static org.junit.Assert.*;
import static org.organicdesign.testUtils.EqualsContract.equalsDistinctHashCode;

public class OrTest {
    @Test public void testBad() {
        Or<Integer,String> or = Or.Companion.bad("Hello");
        assertTrue(or.isBad());
        assertFalse(or.isGood());
        assertEquals("Hello", or.bad());

        assertTrue(or.match((g) -> Boolean.FALSE,
                            (b) -> "Hello".equals(b)));
    }

    @Test public void testGood() {
        Or<String,Integer> or = Or.Companion.good("Hello");
        assertFalse(or.isBad());
        assertTrue(or.isGood());
        assertEquals("Hello", or.good());

        assertTrue(or.match((g) -> "Hello".equals(g),
                            (b) -> Boolean.FALSE));
    }

    @SuppressWarnings("deprecation")
    @Test (expected = IllegalStateException.class)
    public void badEx() {
        Or.Companion.bad("Hello").good();
    }

    @SuppressWarnings("deprecation")
    @Test (expected = IllegalStateException.class)
    public void goodEx() {
        Or.Companion.good("Hello").bad();
    }

    @Test public void equalsHashCodeToStr() {
        equalsDistinctHashCode(Or.Companion.bad("Hello"),
                               Or.Companion.bad("Hello"),
                               Or.Companion.bad("Hello"),
                               Or.Companion.good("Hello"));

        equalsDistinctHashCode(Or.Companion.good("Hello"),
                               Or.Companion.good("Hello"),
                               Or.Companion.good("Hello"),
                               Or.Companion.bad("Hello"));

        Or<Integer,String> b = Or.Companion.bad("Hello");
//        System.out.println("b:" + b.toString());
        assertEquals(b.hashCode(), Or.Companion.bad("Hello").hashCode());
        assertTrue(b.equals(Or.Companion.bad("Hello")));

        Or<String,Integer> g = Or.Companion.good("Hello");
//        System.out.println("g:" + g);
        assertEquals(g.hashCode(), Or.Companion.good("Hello").hashCode());
        assertTrue(g.equals(Or.Companion.good("Hello")));

        assertNotEquals(b.hashCode(), g.hashCode());
        assertNotEquals(b, g);

        assertEquals("Bad(\"Hello\")", b.toString());
        assertEquals("Good(\"Hello\")", g.toString());
    }
}
