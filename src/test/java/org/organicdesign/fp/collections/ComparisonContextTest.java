// Copyright 2017 PlanBase Inc. & Glen Peterson
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

package org.organicdesign.fp.collections;


import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.organicdesign.fp.collections.ComparisonContextTest.Ctx.CC;

public class ComparisonContextTest {
    enum Ctx implements ComparisonContext<Integer> {
        CC;
        @Override
        public int compare(Integer left, Integer right) {
            if (left == right) { return 0; }
            if ( (left == null) || (right == null) ) {
                throw new IllegalArgumentException("Can't compare nulls");
            }
            return left > right ? 1 :
                   left < right ? -1 :
                   0;
        }

        @Override
        public int hash(Integer integer) {
            return integer.hashCode();
        }
    };


    @Test
    public void testNoEx() {
        assertTrue(CC.compare(1, 2) < 0);
        assertTrue(CC.compare(2, 1) > 0);
        assertTrue(CC.compare(3, 3) == 0);

        assertTrue(CC.gt(2, 1));
        assertFalse(CC.gt(1, 2));

        assertTrue(CC.gte(2, 1));
        assertFalse(CC.gte(1, 2));

        assertTrue(CC.lt(1, 2));
        assertFalse(CC.lt(2, 1));

        assertTrue(CC.lte(1, 2));
        assertFalse(CC.lte(2, 1));

        assertTrue(CC.eq(3, 3));
        assertTrue(CC.gte(3, 3));
        assertTrue(CC.lte(3, 3));

        assertFalse(CC.eq(null, 3));
        assertFalse(CC.eq(3, null));

        assertFalse(CC.gte(null, 3));
        assertFalse(CC.gte(3, null));

        assertFalse(CC.lte(null, 3));
        assertFalse(CC.lte(3, null));

        assertTrue(CC.eq(null, null));
        assertTrue(CC.gte(null, null));
        assertTrue(CC.lte(null, null));
    }

    @Test
    public void testEx() {
        assertThrowsExactly(IllegalArgumentException.class, () ->
                CC.compare(1, null));
        assertThrowsExactly(IllegalArgumentException.class, () ->
                CC.compare(null, 1));
        assertThrowsExactly(IllegalArgumentException.class, () ->
                CC.gt(1, null));
        assertThrowsExactly(IllegalArgumentException.class, () ->
                CC.gt(null, 1));
        assertThrowsExactly(IllegalArgumentException.class, () ->
                CC.lt(1, null));
        assertThrowsExactly(IllegalArgumentException.class, () ->
                CC.lt(null, 1));
    }

    @Test public void testMinMax() {
        assertEquals((Integer) 2, CC.min(Arrays.asList(2, 3, 4, 5, 6, 7, 8, 9)));
        assertEquals((Integer) 2, CC.min(Arrays.asList(3, 4, 5, 6, 7, 8, 9, 2)));
        assertEquals((Integer) 2, CC.min(Arrays.asList(9, 8, 7, 6, 2, 5, 4, 3)));

        assertEquals((Integer) 2, CC.min(Arrays.asList(2, 3, null, 5, 6, 7, 8, 9)));
        assertEquals((Integer) 2, CC.min(Arrays.asList(null, null, null, 3, 4, 5, 6, 7, 8, 9, 2)));
        assertEquals((Integer) 2, CC.min(Arrays.asList(9, 8, 7, 6, 2, 5, 4, 3, null)));

        assertEquals((Integer) 9, CC.max(Arrays.asList(2, 3, 4, 5, 6, 7, 8, 9)));
        assertEquals((Integer) 9, CC.max(Arrays.asList(3, 4, 5, 6, 9, 7, 8, 2)));
        assertEquals((Integer) 9, CC.max(Arrays.asList(9, 8, 7, 6, 2, 5, 4, 3)));

        assertEquals((Integer) 9, CC.max(Arrays.asList(null, null, 2, 3, 4, 5, 6, 7, 8, 9)));
        assertEquals((Integer) 9, CC.max(Arrays.asList(3, 4, 5, 6, 9, null, 7, 8, 2)));
        assertEquals((Integer) 9, CC.max(Arrays.asList(9, 8, 7, 6, 2, 5, 4, 3, null, null)));
    }
}