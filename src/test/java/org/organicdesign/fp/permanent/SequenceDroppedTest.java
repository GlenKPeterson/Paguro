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

package org.organicdesign.fp.permanent;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertArrayEquals;

@RunWith(JUnit4.class)
public class SequenceDroppedTest {

    @Test public void emptiness() {
        Sequence<Integer> seq = Sequence.ofArray();
        assertArrayEquals(new Integer[0], seq.drop(0).toArray());
        assertArrayEquals(new Integer[0], seq.drop(1).toArray());
        assertArrayEquals(new Integer[0], seq.drop(2).toArray());
    }

    @Test public void singleElement() {
        Sequence<Integer> seq = Sequence.ofArray(1);
        assertArrayEquals(new Integer[] {1}, seq.drop(0).toArray());
        assertArrayEquals(new Integer[0], seq.drop(1).toArray());
        assertArrayEquals(new Integer[0], seq.drop(2).toArray());
    }

    @Test public void twoElement() {
        Sequence<Integer> seq = Sequence.ofArray(1, 2);
        assertArrayEquals(new Integer[] {1,2}, seq.drop(0).toArray());
        assertArrayEquals(new Integer[] {2}, seq.drop(1).toArray());
        assertArrayEquals(new Integer[0], seq.drop(2).toArray());
    }

    @Test
    public void singleDrops() {
        Integer[] ints = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9};
        Sequence<Integer> seq = Sequence.ofArray(ints);
        assertArrayEquals(ints, seq.drop(0).toArray());
        assertArrayEquals(new Integer[] {2,3,4,5,6,7,8,9}, seq.drop(1).toArray());
        assertArrayEquals(new Integer[] {3,4,5,6,7,8,9}, seq.drop(2).toArray());
        assertArrayEquals(new Integer[] {4,5,6,7,8,9}, seq.drop(3).toArray());
        assertArrayEquals(new Integer[]{9}, seq.drop(8).toArray());
        assertArrayEquals(new Integer[0], Sequence.emptySequence().toArray());
        assertArrayEquals(new Integer[0], seq.drop(9).toArray());
        assertArrayEquals(new Integer[0], seq.drop(10).toArray());
        assertArrayEquals(new Integer[0], seq.drop(10000).toArray());
    }

    @Test(expected = IllegalArgumentException.class)
    public void exception1() { Sequence.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9).drop(-1); }
    @Test(expected = IllegalArgumentException.class)
    public void exception2() { Sequence.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9).drop(-99); }

    @Test
    public void multiDrops() {
        Integer[] ints = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9};
        Sequence<Integer> seq = Sequence.ofArray(ints);
        assertArrayEquals(new Integer[] {3,4,5,6,7,8,9},
                seq.drop(1).drop(1).toArray());
        assertArrayEquals(new Integer[] {4,5,6,7,8,9},
                seq.drop(1).drop(1).drop(1).toArray());
        assertArrayEquals(new Integer[] {9},
                seq
                                  .drop(1).drop(1).drop(1).drop(1).drop(1)
                                  .drop(1).drop(1).drop(1).toArray());
        assertArrayEquals(new Integer[] {},
                seq
                                  .drop(1).drop(1).drop(1).drop(1).drop(1)
                                  .drop(1).drop(1).drop(1).drop(1).toArray());
        assertArrayEquals(new Integer[] {},
                seq
                                  .drop(1).drop(1).drop(1).drop(1).drop(1)
                                  .drop(1).drop(1).drop(1).drop(1).drop(1).toArray());
        assertArrayEquals(new Integer[] {},
                seq
                                  .drop(1).drop(1).drop(1).drop(1).drop(1)
                                  .drop(1).drop(1).drop(1).drop(1).drop(1)
                                  .drop(1).drop(1).drop(1).drop(1).drop(1).toArray());

        assertArrayEquals(new Integer[] {7,8,9},
                seq
                                  .drop(0).drop(1).drop(2).drop(3).toArray());
        assertArrayEquals(new Integer[] {7,8,9},
                seq
                                  .drop(3).drop(2).drop(1).drop(0).toArray());
    }
}
