// Copyright 2015 PlanBase Inc. & Glen Peterson
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

package org.organicdesign.fp.permanent;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertArrayEquals;
import static org.organicdesign.fp.function.Function1.accept;
import static org.organicdesign.fp.function.Function1.reject;

@RunWith(JUnit4.class)
public class SequenceFilteredTest {

    @Test(expected = IllegalArgumentException.class)
    public void nullException() {
        assertArrayEquals(new Integer[] {1,2,3,4,5,6,7,8,9},
Sequence.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9).filter(null).toArray());
    }

    @Test
    public void singleFilter() {
        assertArrayEquals(new Integer[] {1, 2, 3, 4, 5, 6, 7, 8, 9},
                          Sequence.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9).filter(accept()).toArray());

        assertArrayEquals(new Integer[] {},
                          Sequence.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9).filter(reject()).toArray());

        assertArrayEquals(new Integer[] {5,6,7,8,9},
                          Sequence.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9).filter(i -> i > 4).toArray());

        assertArrayEquals(new Integer[] {},
                          Sequence.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9).filter(i -> i < 1).toArray());

        assertArrayEquals(new Integer[] {3},
                          Sequence.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9).filter(i -> i == 3).toArray());

        assertArrayEquals(new Integer[] {1},
                          Sequence.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9).filter(i -> i == 1).toArray());

        assertArrayEquals(new Integer[] {9},
                          Sequence.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9).filter(i -> i == 9).toArray());

        assertArrayEquals(new Integer[] {1,2,3,4,5,6},
                          Sequence.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9).filter(i -> i < 7).toArray());

    }

    @Test
    public void chainedFilters() {
        assertArrayEquals(new Integer[] {1, 2, 3, 4, 5, 6, 7, 8, 9},
                          Sequence.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9)
                                  .filter(accept()).filter(accept()).filter(accept())
                                  .filter(accept()).filter(accept()).filter(accept())
                                  .filter(accept()).filter(accept()).toArray());

        assertArrayEquals(new Integer[] {},
                          Sequence.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9)
                                  .filter(reject()).filter(accept()).filter(accept())
                                  .filter(accept()).filter(accept()).filter(accept())
                                  .filter(accept()).filter(accept()).toArray());

        assertArrayEquals(new Integer[] {},
                          Sequence.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9)
                                  .filter(accept()).filter(accept()).filter(accept())
                                  .filter(accept()).filter(reject()).filter(accept())
                                  .filter(accept()).filter(accept()).toArray());

        assertArrayEquals(new Integer[] {},
                          Sequence.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9)
                                  .filter(accept()).filter(accept()).filter(accept())
                                  .filter(accept()).filter(accept()).filter(accept())
                                  .filter(accept()).filter(reject()).toArray());

        assertArrayEquals(new Integer[] {3, 4, 6},
                          Sequence.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9)
                                  .filter(i -> i > 2)
                                  .filter(i -> i < 7)
                                  .filter(i -> i != 5).toArray());

    }
}
