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

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertArrayEquals;

@RunWith(JUnit4.class)
public class ViewTest {
    @Test
    public void construction() {
        Integer[] ints = new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        assertArrayEquals(View.ofArray(ints).toJavaArrayList().toArray(), ints);
        assertArrayEquals(View.of(Arrays.asList(ints)).toJavaArrayList().toArray(), ints);
        assertArrayEquals(View.of(Arrays.asList(ints).iterator()).toJavaArrayList().toArray(),
                          ints);
    }

    @Test
    public void takeAndDrop() {
        assertArrayEquals(View.ofArray(1,2,3,4,5,6,7,8,9)
                                  .drop(0).take(8888).toJavaArrayList().toArray(),
                          new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 });
        assertArrayEquals(View.ofArray(1,2,3,4,5,6,7,8,9)
                                  .drop(1).take(1).toJavaArrayList().toArray(),
                   new Integer[] { 2 });
        assertArrayEquals(View.ofArray(1,2,3,4,5,6,7,8,9)
                                  .drop(2).take(2).toJavaArrayList().toArray(),
                   new Integer[] { 3,4 });
        assertArrayEquals(View.ofArray(1,2,3,4,5,6,7,8,9)
                                  .drop(3).take(3).toJavaArrayList().toArray(),
                   new Integer[] { 4,5,6 });
        assertArrayEquals(View.ofArray(1,2,3,4,5,6,7,8,9)
                                  .drop(9999).take(3).toJavaArrayList().toArray(),
                   new Integer[] { });
        assertArrayEquals(View.ofArray(1,2,3,4,5,6,7,8,9)
                                  .drop(3).take(0).toJavaArrayList().toArray(),
                   new Integer[] { });
    }

    @Test
    public void chain1() {
        assertArrayEquals(View.ofArray(5)                     //         5
                                  .prepend(View.ofArray(4))   //       4,5
                                  .append(View.ofArray(6))    //       4,5,6
                                  .prepend(View.ofArray(2,3)) //   2,3,4,5,6
                                  .append(View.ofArray(7,8))  //   2,3,4,5,6,7,8
                                  .prepend(View.ofArray(1))   // 1,2,3,4,5,6,7,8
                                  .append(View.ofArray(9))    // 1,2,3,4,5,6,7,8,9
                                  .filter(i -> i > 3)         //       4,5,6,7,8,9
                                  .map(i -> i - 2)            //   2,3,4,5,6,7
                                  .take(5)                    //   2,3,4,5,6
                                  .drop(2)                    //       4,5,6
                                  .toJavaArrayList().toArray(),
                          new Integer[] { 4, 5, 6 });
    }

}
