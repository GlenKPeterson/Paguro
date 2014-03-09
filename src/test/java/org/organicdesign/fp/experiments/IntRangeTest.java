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

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class IntRangeTest {
    @Test(expected = IllegalArgumentException.class)
    public void factory1() {
        IntRange.of(null, Int.ONE);
    }
    @Test(expected = IllegalArgumentException.class)
    public void factory2() {
        IntRange.of(Int.ONE, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void factory3() {
        IntRange.of(Int.ONE, Int.ZERO);
    }

    @Test(expected = IllegalArgumentException.class)
    public void subRange() {
        IntRange.of(Int.ONE, Int.of(8)).getSubRanges(0);
    }

    @Test
    public void basics() {
        IntRange ir1 = IntRange.of(Int.ZERO, Int.ZERO);
        assertEquals(ir1.contains(Int.ZERO), true);
        assertEquals(ir1.contains(Int.ONE), false);
        assertEquals(ir1.contains(Int.NEG_ONE), false);
        assertEquals(ir1.size(), Int.ONE);
    }

    @Test
    public void exactSubRanges() {
        IntRange ir2 = IntRange.of(Int.ONE, Int.of(8));
        List<IntRange> l = ir2.getSubRanges(1);
        assertEquals(l.size(), 1);
        assertEquals(l.get(0), ir2);

        l = IntRange.of(Int.ONE, Int.of(1)).getSubRanges(1);
        assertEquals(l.size(), 1);
        assertEquals(l.get(0), IntRange.of(Int.ONE, Int.of(1)));

        l = IntRange.of(Int.ONE, Int.of(2)).getSubRanges(2);
        assertEquals(l.size(), 2);
        assertEquals(l.get(0), IntRange.of(Int.ONE, Int.of(1)));
        assertEquals(l.get(1), IntRange.of(Int.of(2), Int.of(2)));

        l = IntRange.of(Int.ONE, Int.of(8)).getSubRanges(2);
        assertEquals(l.size(), 2);
        assertEquals(l.get(0), IntRange.of(Int.ONE, Int.of(4)));
        assertEquals(l.get(1), IntRange.of(Int.of(5), Int.of(8)));

        l = IntRange.of(Int.ONE, Int.of(3)).getSubRanges(3);
        assertEquals(l.size(), 3);
        assertEquals(l.get(0), IntRange.of(Int.ONE, Int.ONE));
        assertEquals(l.get(1), IntRange.of(Int.of(2), Int.of(2)));
        assertEquals(l.get(2), IntRange.of(Int.of(3), Int.of(3)));

        l = IntRange.of(Int.ONE, Int.of(9)).getSubRanges(3);
        assertEquals(l.size(), 3);
        assertEquals(l.get(0), IntRange.of(Int.ONE, Int.of(3)));
        assertEquals(l.get(1), IntRange.of(Int.of(4), Int.of(6)));
        assertEquals(l.get(2), IntRange.of(Int.of(7), Int.of(9)));

        l = IntRange.of(Int.ONE, Int.of(99)).getSubRanges(3);
        assertEquals(l.size(), 3);
        assertEquals(l.get(0), IntRange.of(Int.ONE, Int.of(33)));
        assertEquals(l.get(1), IntRange.of(Int.of(34), Int.of(66)));
        assertEquals(l.get(2), IntRange.of(Int.of(67), Int.of(99)));
    }

    @Test
    public void roundedSubRanges() {
        List<IntRange> l = IntRange.of(Int.ONE, Int.of(100)).getSubRanges(3);
        assertEquals(l.size(), 3);
        assertEquals(l.get(0), IntRange.of(Int.ONE, Int.of(34)));
        assertEquals(l.get(1), IntRange.of(Int.of(35), Int.of(67)));
        assertEquals(l.get(2), IntRange.of(Int.of(68), Int.of(100)));
    }
}
