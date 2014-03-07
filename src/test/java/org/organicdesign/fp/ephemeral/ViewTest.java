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

import static org.junit.Assert.assertArrayEquals;

@RunWith(JUnit4.class)
public class ViewTest {
    @Test
    public void takeAndDrop() {
        assertArrayEquals(ViewFromArray.of(1,2,3,4,5,6,7,8,9)
                                  .drop(0).take(8888).toJavaArrayList().toArray(),
                          new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 });
        assertArrayEquals(ViewFromArray.of(1,2,3,4,5,6,7,8,9)
                                  .drop(1).take(1).toJavaArrayList().toArray(),
                   new Integer[] { 2 });
        assertArrayEquals(ViewFromArray.of(1,2,3,4,5,6,7,8,9)
                                  .drop(2).take(2).toJavaArrayList().toArray(),
                   new Integer[] { 3,4 });
        assertArrayEquals(ViewFromArray.of(1,2,3,4,5,6,7,8,9)
                                  .drop(3).take(3).toJavaArrayList().toArray(),
                   new Integer[] { 4,5,6 });
        assertArrayEquals(ViewFromArray.of(1,2,3,4,5,6,7,8,9)
                                  .drop(9999).take(3).toJavaArrayList().toArray(),
                   new Integer[] { });
        assertArrayEquals(ViewFromArray.of(1,2,3,4,5,6,7,8,9)
                                  .drop(3).take(0).toJavaArrayList().toArray(),
                   new Integer[] { });
    }
}
