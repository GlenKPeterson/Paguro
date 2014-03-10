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
        IntRange.of(null, Long.valueOf(1));
    }
    @Test(expected = IllegalArgumentException.class)
    public void factory2() {
        IntRange.of(Long.valueOf(1), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void factory3() {
        IntRange.of(1, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void subRange() {
        IntRange.of(1, 8).getSubRanges(0);
    }

    @Test
    public void basics() {
        IntRange ir1 = IntRange.of(0, 0);
        assertEquals(ir1.contains(0), true);
        assertEquals(ir1.contains(1), false);
        assertEquals(ir1.contains(-1), false);
        assertEquals(ir1.size(), 1);
    }

    @Test
    public void exactSubRanges() {
        IntRange ir2 = IntRange.of(1, 8);
        List<IntRange> l = ir2.getSubRanges(1);
        assertEquals(l.size(), 1);
        assertEquals(l.get(0), ir2);

        l = IntRange.of(1, 1).getSubRanges(1);
        assertEquals(l.size(), 1);
        assertEquals(l.get(0), IntRange.of(1, 1));

        l = IntRange.of(1, 2).getSubRanges(2);
        assertEquals(l.size(), 2);
        assertEquals(l.get(0), IntRange.of(1, 1));
        assertEquals(l.get(1), IntRange.of(2, 2));

        l = IntRange.of(1, 8).getSubRanges(2);
        assertEquals(l.size(), 2);
        assertEquals(l.get(0), IntRange.of(1, 4));
        assertEquals(l.get(1), IntRange.of(5, 8));

        l = IntRange.of(1, 3).getSubRanges(3);
        assertEquals(l.size(), 3);
        assertEquals(l.get(0), IntRange.of(1, 1));
        assertEquals(l.get(1), IntRange.of(2, 2));
        assertEquals(l.get(2), IntRange.of(3, 3));

        l = IntRange.of(1, 9).getSubRanges(3);
        assertEquals(l.size(), 3);
        assertEquals(l.get(0), IntRange.of(1, 3));
        assertEquals(l.get(1), IntRange.of(4, 6));
        assertEquals(l.get(2), IntRange.of(7, 9));

        l = IntRange.of(1, 99).getSubRanges(3);
        assertEquals(l.size(), 3);
        assertEquals(l.get(0), IntRange.of(1, 33));
        assertEquals(l.get(1), IntRange.of(34, 66));
        assertEquals(l.get(2), IntRange.of(67, 99));
    }

    @Test
    public void roundedSubRanges() {
        List<IntRange> l = IntRange.of(1, 100).getSubRanges(3);
        assertEquals(l.size(), 3);
        assertEquals(l.get(0), IntRange.of(1, 34));
        assertEquals(l.get(1), IntRange.of(35, 67));
        assertEquals(l.get(2), IntRange.of(68, 100));
    }
}
