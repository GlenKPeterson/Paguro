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
import org.organicdesign.fp.FunctionUtils;

import static org.junit.Assert.assertArrayEquals;

@RunWith(JUnit4.class)
public class SequenceTakenWhileTest {

    @Test
    public void takeItemsInOneBatch() {
        assertArrayEquals(Sequence.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9)
                                  .takeWhile(FunctionUtils.accept()).toArray(),
                          new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 });
        assertArrayEquals(Sequence.ofArray(1,2,3,4,5,6,7,8,9).takeWhile(i -> i < 10).toArray(),
                          new Integer[] { 1,2,3,4,5,6,7,8,9 });
        assertArrayEquals(Sequence.ofArray(1,2,3,4,5,6,7,8,9).takeWhile(i -> i <= 9).toArray(),
                          new Integer[] { 1,2,3,4,5,6,7,8,9 });
        assertArrayEquals(Sequence.ofArray(1,2,3,4,5,6,7,8,9).takeWhile(i -> i <= 8).toArray(),
                          new Integer[] { 1,2,3,4,5,6,7,8 });
        assertArrayEquals(Sequence.ofArray(1,2,3,4,5,6,7,8,9).takeWhile(i -> i <= 7).toArray(),
                          new Integer[] { 1,2,3,4,5,6,7 });
        assertArrayEquals(Sequence.ofArray(1,2,3,4,5,6,7,8,9).takeWhile(i -> i <= 3).toArray(),
                          new Integer[] { 1,2,3 });
        assertArrayEquals(Sequence.ofArray(1,2,3,4,5,6,7,8,9).takeWhile(i -> i <= 2).toArray(),
                          new Integer[] { 1,2 });
        assertArrayEquals(Sequence.ofArray(1,2,3,4,5,6,7,8,9).takeWhile(i -> i <= 1).toArray(),
                          new Integer[] { 1 });
        assertArrayEquals(Sequence.ofArray(1,2,3,4,5,6,7,8,9).takeWhile(FunctionUtils.reject())
                                  .toArray(),
                          new Integer[] {  });
        assertArrayEquals(Sequence.ofArray(1,2,3,4,5,6,7,8,9).takeWhile(i -> i > 10).toArray(),
                          new Integer[] {  });
    }

    @Test(expected = IllegalArgumentException.class)
    public void exception1() {
        Sequence.ofArray(1,2,3,4,5,6,7,8,9).takeWhile(null);
    }
}
