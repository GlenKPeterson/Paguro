// Copyright (c) 2014-04-13 PlanBase Inc. & Glen Peterson
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
import org.organicdesign.fp.function.Function1;

import static org.junit.Assert.assertArrayEquals;

@RunWith(JUnit4.class)
public class SequenceTakenWhileTest {

    @Test
    public void takeItemsInOneBatch() {
        Sequence<Integer> seq = Sequence.of(1, 2, 3, 4, 5, 6, 7, 8, 9);
        assertArrayEquals(new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 },
                          seq.takeWhile(Function1.accept()).toTypedArray());
        assertArrayEquals(new Integer[] { 1,2,3,4,5,6,7,8,9 },
                          seq.takeWhile(i -> i < 10).toTypedArray());
        assertArrayEquals(new Integer[] { 1,2,3,4,5,6,7,8,9 },
                          seq.takeWhile(i -> i <= 9).toTypedArray());
        assertArrayEquals(new Integer[] { 1,2,3,4,5,6,7,8 },
                          seq.takeWhile(i -> i <= 8).toTypedArray());
        assertArrayEquals(new Integer[] { 1,2,3,4,5,6,7 },
                          seq.takeWhile(i -> i <= 7).toTypedArray());
        assertArrayEquals(new Integer[] { 1,2,3 },
                          seq.takeWhile(i -> i <= 3).toTypedArray());
        assertArrayEquals(new Integer[] { 1,2 },
                          seq.takeWhile(i -> i <= 2).toTypedArray());
        assertArrayEquals(new Integer[] { 1 },
                          seq.takeWhile(i -> i <= 1).toTypedArray());
        assertArrayEquals(new Integer[] {  },
                          seq.takeWhile(Function1.reject()).toTypedArray());
        assertArrayEquals(new Integer[] {  },
                          seq.takeWhile(i -> i > 10).toTypedArray());
    }

    @Test(expected = IllegalArgumentException.class)
    public void exception1() {
        Sequence.of(1, 2, 3, 4, 5, 6, 7, 8, 9).takeWhile(null);
    }
}
