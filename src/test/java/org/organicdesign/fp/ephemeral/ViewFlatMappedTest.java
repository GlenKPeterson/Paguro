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

package org.organicdesign.fp.ephemeral;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;

@RunWith(JUnit4.class)
public class ViewFlatMappedTest {
    @Test
    public void singleFlatMap() {
        assertEquals(View.EMPTY_VIEW,
                     View.of(1, 2, 3, 4, 5, 6, 7, 8, 9).flatMap(null));

        assertEquals(View.EMPTY_VIEW,
                     View.EMPTY_VIEW.flatMap(null));

        assertArrayEquals(new Integer[] {},
                          View.of(1, 2, 3, 4, 5, 6, 7, 8, 9)
                              .flatMap(null).toTypedArray());

        assertArrayEquals(new Integer[] { 1,2,3, 2,4,6, 3,6,9, 4,8,12, 5,10,15, 6,12,18,
                                          7,14,21, 8,16,24, 9,18,27},
                          View.of(1, 2, 3, 4, 5, 6, 7, 8, 9)
                              .flatMap(i -> View.of(i, i * 2, i * 3)).toTypedArray());

        assertArrayEquals(new String[] { "1","2", "2","3", "3","4"},
                          View.of(1, 2, 3)
                              .flatMap(i -> View.of(String.valueOf(i),
                                                    String.valueOf(i + 1))).toTypedArray());

    }

    @Test
    public void flatMapChain() {
        assertEquals(View.EMPTY_VIEW,
                     View.of(1, 2, 3, 4, 5, 6, 7, 8, 9)
                             .flatMap(null).flatMap(null).flatMap(null));

        assertEquals(View.EMPTY_VIEW,
                     View.EMPTY_VIEW.flatMap(null).flatMap(null).flatMap(null));

        assertArrayEquals(new Integer[] {},
                          View.of(1, 2, 3, 4, 5, 6, 7, 8, 9)
                                  .flatMap(null).flatMap(null).flatMap(null).toTypedArray());

        assertArrayEquals(new Integer[] { 1,2, 2,3, 3,4, 10,11, 20,21, 30,31},
                          View.of(1, 10)
                                  .flatMap(i -> View.of(i, i * 2, i * 3))
                                  .flatMap(i -> View.of(i, i + 1))
                                  .toTypedArray());
    }
}
