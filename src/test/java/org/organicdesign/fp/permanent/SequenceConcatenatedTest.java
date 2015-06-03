// Copyright (c) 2015-04-05 PlanBase Inc. & Glen Peterson
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

@RunWith(JUnit4.class)
public class SequenceConcatenatedTest {

    @Test public void doubleNull() {
        assertArrayEquals(new Integer[0],
                          Sequence.of().concat(null).toTypedArray());

        assertArrayEquals(new Integer[0],
                          Sequence.of().concat(Sequence.emptySequence()).toTypedArray());

        assertArrayEquals(new Integer[0],
                          Sequence.of().precat(null).toTypedArray());

        assertArrayEquals(new Integer[0],
                          Sequence.of().precat(Sequence.emptySequence()).toTypedArray());
    }

    @Test public void prepend() {
        assertArrayEquals(new Integer[] { 5, 6, 7, 8, 9 },
                          Sequence.of(5, 6, 7, 8, 9)
                                  .precat(null).toTypedArray());

        assertArrayEquals(new Integer[] { 5, 6, 7, 8, 9 },
                          Sequence.of(5, 6, 7, 8, 9)
                                  .precat(Sequence.emptySequence()).toTypedArray());

        assertArrayEquals(new Integer[] { 4, 5, 6, 7, 8, 9 },
                          Sequence.of(5, 6, 7, 8, 9)
                                  .precat(Sequence.of(4)).toTypedArray());

        assertArrayEquals(new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 },
                          Sequence.of(5, 6, 7, 8, 9)
                                  .precat(Sequence.of(1, 2, 3, 4)).toTypedArray());
    }

    @Test
    public void append() {
        assertArrayEquals(new Integer[] { 1, 2, 3, 4 },
                          Sequence.of(1, 2, 3, 4)
                                  .concat(null).toTypedArray());

        assertArrayEquals(new Integer[] { 1, 2, 3, 4 },
                          Sequence.of(1, 2, 3, 4)
                                  .concat(Sequence.emptySequence()).toTypedArray());

        assertArrayEquals(new Integer[] { 1, 2, 3, 4, 5 },
                          Sequence.of(1, 2, 3, 4)
                                  .concat(Sequence.of(5)).toTypedArray());

        assertArrayEquals(new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 },
                          Sequence.of(1, 2, 3, 4)
                                  .concat(Sequence.of(5, 6, 7, 8, 9)).toTypedArray());
    }

    @Test
    public void chainedPrependAppend() {
        assertArrayEquals(new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 },
                          Sequence.of(5)                     //         5
                                  .precat(Sequence.of(4))   //       4,5
                                  .concat(Sequence.of(6))    //       4,5,6
                                  .precat(Sequence.of(2, 3)) //   2,3,4,5,6
                                  .concat(Sequence.of(7, 8))  //   2,3,4,5,6,7,8
                                  .precat(Sequence.of(1))   // 1,2,3,4,5,6,7,8
                                  .concat(Sequence.of(9))    // 1,2,3,4,5,6,7,8,9
                                  .toTypedArray());

        assertArrayEquals(new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 },
                          Sequence.of(5)
                                  .precat(null).precat(null).precat(null).precat(null)
                                  .precat(Sequence.emptySequence()).precat(Sequence.emptySequence())
                                  .precat(null)
                                  .concat(Sequence.emptySequence()).concat(Sequence.emptySequence())
                                  .concat(null).concat(null).concat(null).concat(null).concat(null)
                                  .precat(Sequence.of(4))
                                  .precat(null).precat(null).precat(null).precat(null)
                                  .precat(Sequence.emptySequence()).precat(Sequence.emptySequence())
                                  .precat(null)
                                  .concat(Sequence.emptySequence()).concat(Sequence.emptySequence())
                                  .concat(null).concat(null).concat(null).concat(null).concat(null)
                                  .concat(Sequence.of(6))
                                  .precat(null).precat(null).precat(null).precat(null)
                                  .precat(Sequence.emptySequence()).precat(Sequence.emptySequence())
                                  .precat(null)
                                  .concat(Sequence.emptySequence()).concat(Sequence.emptySequence())
                                  .concat(null).concat(null).concat(null).concat(null).concat(null)
                                  .precat(Sequence.of(2, 3))
                                  .precat(null).precat(null).precat(null).precat(null)
                                  .precat(Sequence.emptySequence()).precat(Sequence.emptySequence())
                                  .precat(null)
                                  .concat(Sequence.emptySequence()).concat(Sequence.emptySequence())
                                  .concat(null).concat(null).concat(null).concat(null).concat(null)
                                  .concat(Sequence.of(7, 8))
                                  .precat(null).precat(null).precat(null).precat(null)
                                  .precat(Sequence.emptySequence()).precat(Sequence.emptySequence())
                                  .precat(null)
                                  .concat(Sequence.emptySequence()).concat(Sequence.emptySequence())
                                  .concat(null).concat(null).concat(null).concat(null).concat(null)
                                  .precat(Sequence.of(1))
                                  .precat(null).precat(null).precat(null).precat(null)
                                  .precat(Sequence.emptySequence()).precat(Sequence.emptySequence())
                                  .precat(null)
                                  .concat(Sequence.emptySequence()).concat(Sequence.emptySequence())
                                  .concat(null).concat(null).concat(null).concat(null).concat(null)
                                  .concat(Sequence.of(9))
                                  .precat(null).precat(null).precat(null).precat(null)
                                  .precat(Sequence.emptySequence()).precat(Sequence.emptySequence())
                                  .precat(null)
                                  .concat(Sequence.emptySequence()).concat(Sequence.emptySequence())
                                  .concat(null).concat(null).concat(null).concat(null).concat(null)
                                  .toTypedArray());
    }
}
