// Copyright 2014-04-15 PlanBase Inc. & Glen Peterson
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

import static org.junit.Assert.assertArrayEquals;

public class SequenceTakenTest {

    @Test
    public void takeItemsInOneBatch() {
        Integer[] ints = new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        Sequence<Integer> seq = Sequence.of(ints);
        assertArrayEquals(ints, seq.take(9999).toTypedArray());
        assertArrayEquals(ints, seq.take(10).toTypedArray());
        assertArrayEquals(ints, seq.take(9).toTypedArray());
        assertArrayEquals(new Integer[] { 1,2,3,4,5,6,7,8 }, seq.take(8).toTypedArray());
        assertArrayEquals(new Integer[] { 1,2,3,4,5,6,7 }, seq.take(7).toTypedArray());
        assertArrayEquals(new Integer[] { 1,2,3 }, seq.take(3).toTypedArray());
        assertArrayEquals(new Integer[] { 1,2 }, seq.take(2).toTypedArray());
        assertArrayEquals(new Integer[] { 1 }, seq.take(1).toTypedArray());
        assertArrayEquals(new Integer[0], seq.take(0).toTypedArray());
    }

    @Test(expected = IllegalArgumentException.class)
    public void exception1() { Sequence.of(1, 2, 3, 4, 5, 6, 7, 8, 9).take(-1); }
    @Test(expected = IllegalArgumentException.class)
    public void exception2() { Sequence.of(1, 2, 3, 4, 5, 6, 7, 8, 9).take(-99); }

    @Test
    public void takeItemsInMultiBatches() {
        Integer[] ints = new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        Sequence<Integer> seq = Sequence.of(ints);
        assertArrayEquals(ints, seq.take(10).take(9999).take(10).toTypedArray());
        assertArrayEquals(ints, seq.take(9).take(9).take(9).toTypedArray());
        assertArrayEquals(new Integer[] { 1,2,3,4,5,6 }, seq.take(8).take(7).take(6).toTypedArray());
        assertArrayEquals(new Integer[] { 1,2,3,4,5,6 }, seq.take(6).take(7).take(8).toTypedArray());
        assertArrayEquals(new Integer[] { 1 }, seq.take(999).take(1).take(9999999).toTypedArray());
        assertArrayEquals(new Integer[] { 1 }, seq.take(9999).take(1).take(3).toTypedArray());
        assertArrayEquals(new Integer[0], seq.take(0).take(0).toTypedArray());
        assertArrayEquals(new Integer[0], seq.take(0).take(1).toTypedArray());
        assertArrayEquals(new Integer[0], seq.take(0).take(99999999).take(9999999).toTypedArray());
        assertArrayEquals(new Integer[0], seq.take(99).take(9999).take(0).toTypedArray());
    }
}
