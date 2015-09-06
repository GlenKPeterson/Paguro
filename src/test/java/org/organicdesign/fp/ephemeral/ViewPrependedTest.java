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
                          View.ofArray(5, 6, 7, 8, 9)
                              .precat(null).toArray());

        assertArrayEquals(new Integer[] { 5, 6, 7, 8, 9 },
                          View.ofArray(5, 6, 7, 8, 9)
                              .precat(View.emptyView()).toArray());

        assertArrayEquals(new Integer[] { 4, 5, 6, 7, 8, 9 },
                          View.ofArray(5, 6, 7, 8, 9)
                              .precat(View.ofArray(4)).toArray());

        assertArrayEquals(new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 },
                          View.ofArray(5, 6, 7, 8, 9)
                              .precat(View.ofArray(1, 2, 3, 4)).toArray());
    }

    @Test
    public void append() {
        assertArrayEquals(new Integer[] { 1, 2, 3, 4 },
                          View.ofArray(1, 2, 3, 4)
                              .concat(null).toArray());

        assertArrayEquals(new Integer[] { 1, 2, 3, 4 },
                          View.ofArray(1, 2, 3, 4)
                              .concat(View.emptyView()).toArray());

        assertArrayEquals(new Integer[] { 1, 2, 3, 4, 5 },
                          View.ofArray(1, 2, 3, 4)
                              .concat(View.ofArray(5)).toArray());

        assertArrayEquals(new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 },
                          View.ofArray(1, 2, 3, 4)
                              .concat(View.ofArray(5, 6, 7, 8, 9)).toArray());
    }

    @Test
    public void chainedPrependAppend() {
        assertArrayEquals(new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 },
                          View.ofArray(5)                     //         5
                                  .precat(View.ofArray(4))   //       4,5
                                  .concat(View.ofArray(6))    //       4,5,6
                                  .precat(View.ofArray(2, 3)) //   2,3,4,5,6
                                  .concat(View.ofArray(7, 8))  //   2,3,4,5,6,7,8
                                  .precat(View.ofArray(1))   // 1,2,3,4,5,6,7,8
                                  .concat(View.ofArray(9))    // 1,2,3,4,5,6,7,8,9
                                  .toArray());

        assertArrayEquals(new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 },
                          View.ofArray(5)
                              .precat(null).precat(null).precat(null).precat(null)
                              .precat(View.emptyView()).precat(View.emptyView())
                              .precat(null)
                              .concat(View.emptyView()).concat(View.emptyView())
                              .concat(null).concat(null).concat(null).concat(null).concat(null)
                              .precat(View.ofArray(4))
                              .precat(null).precat(null).precat(null).precat(null)
                              .precat(View.emptyView()).precat(View.emptyView())
                              .precat(null)
                              .concat(View.emptyView()).concat(View.emptyView())
                              .concat(null).concat(null).concat(null).concat(null).concat(null)
                              .concat(View.ofArray(6))
                              .precat(null).precat(null).precat(null).precat(null)
                              .precat(View.emptyView()).precat(View.emptyView())
                              .precat(null)
                              .concat(View.emptyView()).concat(View.emptyView())
                              .concat(null).concat(null).concat(null).concat(null).concat(null)
                              .precat(View.ofArray(2, 3))
                              .precat(null).precat(null).precat(null).precat(null)
                              .precat(View.emptyView()).precat(View.emptyView())
                              .precat(null)
                              .concat(View.emptyView()).concat(View.emptyView())
                              .concat(null).concat(null).concat(null).concat(null).concat(null)
                              .concat(View.ofArray(7, 8))
                              .precat(null).precat(null).precat(null).precat(null)
                              .precat(View.emptyView()).precat(View.emptyView())
                              .precat(null)
                              .concat(View.emptyView()).concat(View.emptyView())
                              .concat(null).concat(null).concat(null).concat(null).concat(null)
                              .precat(View.ofArray(1))
                              .precat(null).precat(null).precat(null).precat(null)
                              .precat(View.emptyView()).precat(View.emptyView())
                              .precat(null)
                              .concat(View.emptyView()).concat(View.emptyView())
                              .concat(null).concat(null).concat(null).concat(null).concat(null)
                              .concat(View.ofArray(9))
                              .precat(null).precat(null).precat(null).precat(null)
                              .precat(View.emptyView()).precat(View.emptyView())
                              .precat(null)
                              .concat(View.emptyView()).concat(View.emptyView())
                              .concat(null).concat(null).concat(null).concat(null).concat(null)
                              .toArray());
    }

}
