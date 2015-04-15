// Copyright (c) 2015-04-06 PlanBase Inc. & Glen Peterson
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;

@RunWith(JUnit4.class)
public class SequenceFlatMappedTest {
    @Test
    public void singleFlatMap() {
        assertEquals(Sequence.Empty.SEQUENCE,
                     Sequence.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9).flatMap(null));

        assertEquals(Sequence.Empty.SEQUENCE,
                     Sequence.Empty.SEQUENCE.flatMap(null));

        assertArrayEquals(new Integer[] {},
                          Sequence.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9)
                                  .flatMap(null).toArray());

        assertArrayEquals(new Integer[] { 1,2,3, 2,4,6, 3,6,9, 4,8,12, 5,10,15, 6,12,18,
                                  7,14,21, 8,16,24, 9,18,27},
                          Sequence.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9)
                                  .flatMap(i -> Sequence.ofArray(i, i * 2, i * 3)).toArray());

        assertArrayEquals(new String[]{"1", "2", "2", "3", "3", "4"},
                          Sequence.ofArray(1, 2, 3)
                                  .flatMap(i -> Sequence.ofArray(String.valueOf(i),
                                                                 String.valueOf(i + 1))).toArray());

    }

    @Test
    public void flatMapChain() {
        assertEquals(Sequence.Empty.SEQUENCE,
                     Sequence.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9)
                             .flatMap(null).flatMap(null).flatMap(null));

        assertEquals(Sequence.Empty.SEQUENCE,
                     Sequence.Empty.SEQUENCE.flatMap(null).flatMap(null).flatMap(null));

        assertArrayEquals(new Integer[] {},
                          Sequence.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9)
                                  .flatMap(null).flatMap(null).flatMap(null).toArray());

        assertArrayEquals(new Integer[] { 1,2, 2,3, 3,4, 10,11, 20,21, 30,31},
                          Sequence.ofArray(1, 10)
                                  .flatMap(i -> Sequence.ofArray(i, i * 2, i * 3))
                                  .flatMap(i -> Sequence.ofArray(i, i + 1))
                                  .toArray());
    }
}
