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
public class ViewPrependedTest {

    @Test
    public void prepend() {
        assertArrayEquals(new Integer[] { 5, 6, 7, 8, 9 },
                          View.of(5, 6, 7, 8, 9)
                              .prepend(null).toTypedArray());

        assertArrayEquals(new Integer[] { 5, 6, 7, 8, 9 },
                          View.of(5, 6, 7, 8, 9)
                              .prepend(View.emptyView()).toTypedArray());

        assertArrayEquals(new Integer[] { 4, 5, 6, 7, 8, 9 },
                          View.of(5, 6, 7, 8, 9)
                              .prepend(View.of(4)).toTypedArray());

        assertArrayEquals(new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 },
                          View.of(5, 6, 7, 8, 9)
                              .prepend(View.of(1, 2, 3, 4)).toTypedArray());
    }

    @Test
    public void append() {
        assertArrayEquals(new Integer[] { 1, 2, 3, 4 },
                          View.of(1, 2, 3, 4)
                              .append(null).toTypedArray());

        assertArrayEquals(new Integer[] { 1, 2, 3, 4 },
                          View.of(1, 2, 3, 4)
                              .append(View.emptyView()).toTypedArray());

        assertArrayEquals(new Integer[] { 1, 2, 3, 4, 5 },
                          View.of(1, 2, 3, 4)
                              .append(View.of(5)).toTypedArray());

        assertArrayEquals(new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 },
                          View.of(1, 2, 3, 4)
                              .append(View.of(5, 6, 7, 8, 9)).toTypedArray());
    }

    @Test
    public void chainedPrependAppend() {
        assertArrayEquals(new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 },
                          View.of(5)                     //         5
                                  .prepend(View.of(4))   //       4,5
                                  .append(View.of(6))    //       4,5,6
                                  .prepend(View.of(2, 3)) //   2,3,4,5,6
                                  .append(View.of(7, 8))  //   2,3,4,5,6,7,8
                                  .prepend(View.of(1))   // 1,2,3,4,5,6,7,8
                                  .append(View.of(9))    // 1,2,3,4,5,6,7,8,9
                                  .toTypedArray());

        assertArrayEquals(new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 },
                          View.of(5)
                              .prepend(null).prepend(null).prepend(null).prepend(null)
                              .prepend(View.emptyView()).prepend(View.emptyView())
                              .prepend(null)
                              .append(View.emptyView()).append(View.emptyView())
                              .append(null).append(null).append(null).append(null).append(null)
                              .prepend(View.of(4))
                              .prepend(null).prepend(null).prepend(null).prepend(null)
                              .prepend(View.emptyView()).prepend(View.emptyView())
                              .prepend(null)
                              .append(View.emptyView()).append(View.emptyView())
                              .append(null).append(null).append(null).append(null).append(null)
                              .append(View.of(6))
                              .prepend(null).prepend(null).prepend(null).prepend(null)
                              .prepend(View.emptyView()).prepend(View.emptyView())
                              .prepend(null)
                              .append(View.emptyView()).append(View.emptyView())
                              .append(null).append(null).append(null).append(null).append(null)
                              .prepend(View.of(2, 3))
                              .prepend(null).prepend(null).prepend(null).prepend(null)
                              .prepend(View.emptyView()).prepend(View.emptyView())
                              .prepend(null)
                              .append(View.emptyView()).append(View.emptyView())
                              .append(null).append(null).append(null).append(null).append(null)
                              .append(View.of(7, 8))
                              .prepend(null).prepend(null).prepend(null).prepend(null)
                              .prepend(View.emptyView()).prepend(View.emptyView())
                              .prepend(null)
                              .append(View.emptyView()).append(View.emptyView())
                              .append(null).append(null).append(null).append(null).append(null)
                              .prepend(View.of(1))
                              .prepend(null).prepend(null).prepend(null).prepend(null)
                              .prepend(View.emptyView()).prepend(View.emptyView())
                              .prepend(null)
                              .append(View.emptyView()).append(View.emptyView())
                              .append(null).append(null).append(null).append(null).append(null)
                              .append(View.of(9))
                              .prepend(null).prepend(null).prepend(null).prepend(null)
                              .prepend(View.emptyView()).prepend(View.emptyView())
                              .prepend(null)
                              .append(View.emptyView()).append(View.emptyView())
                              .append(null).append(null).append(null).append(null).append(null)
                              .toTypedArray());
    }

}
