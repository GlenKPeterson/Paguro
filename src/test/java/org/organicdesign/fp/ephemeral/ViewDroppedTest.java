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

package org.organicdesign.fp.ephemeral;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertArrayEquals;

@RunWith(JUnit4.class)
public class ViewDroppedTest {

    @Test
    public void singleDrops() {
        assertArrayEquals(ViewFromArray.of(1,2,3,4,5,6,7,8,9).drop(0).toJavaArrayList().toArray(),
                          new Integer[] {1,2,3,4,5,6,7,8,9});
        assertArrayEquals(ViewFromArray.of(1,2,3,4,5,6,7,8,9).drop(1).toJavaArrayList().toArray(),
                          new Integer[] {2,3,4,5,6,7,8,9});
        assertArrayEquals(ViewFromArray.of(1,2,3,4,5,6,7,8,9).drop(2).toJavaArrayList().toArray(),
                          new Integer[] {3,4,5,6,7,8,9});
        assertArrayEquals(ViewFromArray.of(1,2,3,4,5,6,7,8,9).drop(3).toJavaArrayList().toArray(),
                          new Integer[] {4, 5, 6, 7, 8, 9});
        assertArrayEquals(ViewFromArray.of(1,2,3,4,5,6,7,8,9).drop(8).toJavaArrayList().toArray(),
                          new Integer[] {9});
        assertArrayEquals(ViewFromArray.of(1,2,3,4,5,6,7,8,9).drop(9).toJavaArrayList().toArray(),
                          new Integer[] {});
        assertArrayEquals(ViewFromArray.of(1,2,3,4,5,6,7,8,9).drop(10).toJavaArrayList().toArray(),
                          new Integer[] {});
        assertArrayEquals(ViewFromArray.of(1,2,3,4,5,6,7,8,9).drop(10000).toJavaArrayList().toArray(),
                          new Integer[] {});
    }

    @Test(expected = IllegalArgumentException.class)
    public void exception1() {
        ViewFromArray.of(1,2,3,4,5,6,7,8,9).drop(-1);
    }
    @Test(expected = IllegalArgumentException.class)
    public void exception2() {
        ViewFromArray.of(1,2,3,4,5,6,7,8,9).drop(-99);
    }

    @Test
    public void multiDrops() {
        assertArrayEquals(ViewFromArray.of(1,2,3,4,5,6,7,8,9).drop(1).drop(1).toJavaArrayList().toArray(),
                          new Integer[] {3,4,5,6,7,8,9});
        assertArrayEquals(ViewFromArray.of(1,2,3,4,5,6,7,8,9).drop(1).drop(1).drop(1).toJavaArrayList().toArray(),
                          new Integer[] {4,5,6,7,8,9});
        assertArrayEquals(ViewFromArray.of(1,2,3,4,5,6,7,8,9)
                                  .drop(1).drop(1).drop(1).drop(1).drop(1)
                                  .drop(1).drop(1).drop(1).toJavaArrayList().toArray(),
                          new Integer[] {9});
        assertArrayEquals(ViewFromArray.of(1,2,3,4,5,6,7,8,9)
                                  .drop(1).drop(1).drop(1).drop(1).drop(1)
                                  .drop(1).drop(1).drop(1).drop(1).toJavaArrayList().toArray(),
                          new Integer[] {});
        assertArrayEquals(ViewFromArray.of(1,2,3,4,5,6,7,8,9)
                                  .drop(1).drop(1).drop(1).drop(1).drop(1)
                                  .drop(1).drop(1).drop(1).drop(1).drop(1).toJavaArrayList().toArray(),
                          new Integer[] {});
        assertArrayEquals(ViewFromArray.of(1,2,3,4,5,6,7,8,9)
                                  .drop(1).drop(1).drop(1).drop(1).drop(1)
                                  .drop(1).drop(1).drop(1).drop(1).drop(1)
                                  .drop(1).drop(1).drop(1).drop(1).drop(1).toJavaArrayList().toArray(),
                          new Integer[] {});

        assertArrayEquals(ViewFromArray.of(1,2,3,4,5,6,7,8,9)
                                  .drop(0).drop(1).drop(2).drop(3).toJavaArrayList().toArray(),
                          new Integer[] {7,8,9});
        assertArrayEquals(ViewFromArray.of(1,2,3,4,5,6,7,8,9)
                                  .drop(3).drop(2).drop(1).drop(0).toJavaArrayList().toArray(),
                          new Integer[] {7,8,9});
    }
}
