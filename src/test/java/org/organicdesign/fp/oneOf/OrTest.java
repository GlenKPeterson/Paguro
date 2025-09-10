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

import org.junit.jupiter.api.Test;

import static com.planbase.testutils.EqualsContract.equalsDistinctHashCode;
import static org.junit.jupiter.api.Assertions.*;

public class OrTest {
    @Test
    public void bad() {
        Or<Integer,String> or = Or.bad("Hello");
        assertTrue(or.isBad());
        assertFalse(or.isGood());
        assertEquals("Hello", or.bad());

        assertTrue((Boolean) or.match((g) -> Boolean.FALSE, (b) -> "Hello".equals(b)));
    }

    @Test public void good() {
        Or<String,Integer> or = Or.good("Hello");
        assertFalse(or.isBad());
        assertTrue(or.isGood());
        assertEquals("Hello", or.good());

        assertTrue((Boolean) or.match((g) -> "Hello".equals(g), (b) -> Boolean.FALSE));
    }

    @Test
    public void exception() {
        assertThrowsExactly(IllegalStateException.class, () ->
        Or.bad("Hello").good());
        assertThrowsExactly(IllegalStateException.class, () ->
        Or.good("Hello").bad());
    }

    @Test public void equalsHashCodeToStr() {
        equalsDistinctHashCode(Or.bad("Hello"),
                               Or.bad("Hello"),
                               Or.bad("Hello"),
                               Or.good("Hello"));

        equalsDistinctHashCode(Or.good("Hello"),
                               Or.good("Hello"),
                               Or.good("Hello"),
                               Or.bad("Hello"));

        Or<Integer,String> b = Or.bad("Hello");
//        System.out.println("b:" + b.toString());
        assertEquals(b.hashCode(), Or.bad("Hello").hashCode());
        assertTrue(b.equals(Or.bad("Hello")));

        Or<String,Integer> g = Or.good("Hello");
//        System.out.println("g:" + g);
        assertEquals(g.hashCode(), Or.good("Hello").hashCode());
        assertTrue(g.equals(Or.good("Hello")));

        assertNotEquals(b.hashCode(), g.hashCode());
        assertNotEquals(b, g);

        assertEquals("Bad(\"Hello\")", b.toString());
        assertEquals("Good(\"Hello\")", g.toString());
    }
}
