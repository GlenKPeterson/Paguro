// Copyright (c) 2014-03-07 PlanBase Inc. & Glen Peterson
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

package org.organicdesign.fp.ephemeral;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.organicdesign.fp.function.Function1;

import static org.junit.Assert.assertArrayEquals;

@RunWith(JUnit4.class)
public class ViewMappedTest {

    private static final Function1<Integer,Integer> plusOne = x -> x + 1;
    private static final Function1<Integer,Integer> minusOne = x -> x - 1;

    @Test
    public void mapInOneBatch() {
        assertArrayEquals(new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 },
                          View.of(1, 2, 3, 4, 5, 6, 7, 8, 9)
                              .map(Function1.identity()).toTypedArray());

        assertArrayEquals(new Integer[] { 2,3,4,5,6,7,8,9,10 },
                          View.of(1, 2, 3, 4, 5, 6, 7, 8, 9)
                              .map(plusOne).toTypedArray());

        assertArrayEquals(new Integer[] { 0,1,2,3,4,5,6,7,8 },
                          View.of(1, 2, 3, 4, 5, 6, 7, 8, 9)
                              .map(minusOne).toTypedArray());

        assertArrayEquals(new Integer[] { 1,2,3,4,5,6,7,8,9 },
                          View.of(1, 2, 3, 4, 5, 6, 7, 8, 9)
                              .map(plusOne).map(minusOne).toTypedArray());

        assertArrayEquals(new Integer[] { 1,2,3,4,5,6,7,8,9 },
                          View.of(1, 2, 3, 4, 5, 6, 7, 8, 9)
                              .map(minusOne).map(plusOne).toTypedArray());

        assertArrayEquals(new Integer[] { 3,4,5,6,7,8,9,10,11 },
                          View.of(1, 2, 3, 4, 5, 6, 7, 8, 9)
                              .map(plusOne).map(plusOne).toTypedArray());

    }
    @Test
    public void mapInMultipleBatches() {

        assertArrayEquals(new Integer[] { 11,12,13,14,15,16,17,18,19 },
                          View.of(1, 2, 3, 4, 5, 6, 7, 8, 9)
                              .map(plusOne).map(plusOne).map(plusOne).map(plusOne).map(plusOne)
                              .map(plusOne).map(plusOne).map(plusOne).map(plusOne).map(plusOne)
                              .toTypedArray());

        assertArrayEquals(new Integer[] { -9,-8,-7,-6,-5,-4,-3,-2,-1 },
                          View.of(1, 2, 3, 4, 5, 6, 7, 8, 9)
                              .map(minusOne).map(minusOne).map(minusOne).map(minusOne)
                              .map(minusOne).map(minusOne).map(minusOne).map(minusOne)
                              .map(minusOne).map(minusOne)
                              .toTypedArray());
    }
}
