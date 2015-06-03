// Copyright (c) 2014-03-06 PlanBase Inc. & Glen Peterson
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

import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;

@RunWith(JUnit4.class)
public class ViewTest {
    @Test
    public void construction() {
        Integer[] ints = new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        assertArrayEquals(ints, View.of(ints).toTypedArray());
        assertArrayEquals(ints, View.ofIter(Arrays.asList(ints)).toTypedArray());
    }

    @Test
    public void takeAndDrop() {
        assertArrayEquals(new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 },
                          View.of(1, 2, 3, 4, 5, 6, 7, 8, 9).drop(0).take(8888).toTypedArray());

        assertArrayEquals(new Integer[] { 2 },
                          View.of(1, 2, 3, 4, 5, 6, 7, 8, 9).drop(1).take(1).toTypedArray());

        assertArrayEquals(new Integer[] { 3,4 },
                          View.of(1, 2, 3, 4, 5, 6, 7, 8, 9).drop(2).take(2).toTypedArray());

        assertArrayEquals(new Integer[] { 4,5,6 },
                          View.of(1, 2, 3, 4, 5, 6, 7, 8, 9).drop(3).take(3).toTypedArray());

        assertArrayEquals(new Integer[] { },
                          View.of(1, 2, 3, 4, 5, 6, 7, 8, 9).drop(9999).take(3).toTypedArray());

        assertArrayEquals(new Integer[] { },
                          View.of(1, 2, 3, 4, 5, 6, 7, 8, 9).drop(3).take(0).toTypedArray());
    }

    @Test
    public void chain1() {
        assertArrayEquals(new Integer[] { 4, 5, 6 },
                          View.of(5)                     //         5
                                  .prepend(View.of(4))   //       4,5
                                  .append(View.of(6))    //       4,5,6
                                  .prepend(View.of(2, 3)) //   2,3,4,5,6
                                  .append(View.of(7, 8))  //   2,3,4,5,6,7,8
                                  .prepend(View.of(1))   // 1,2,3,4,5,6,7,8
                                  .append(View.of(9))    // 1,2,3,4,5,6,7,8,9
                                  .filter(i -> i > 3)         //       4,5,6,7,8,9
                                  .map(i -> i - 2)            //   2,3,4,5,6,7
                                  .take(5)                    //   2,3,4,5,6
                                  .drop(2)                    //       4,5,6
                                  .toTypedArray());
    }

}
