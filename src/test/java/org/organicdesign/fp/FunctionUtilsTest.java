// Copyright (c) 2014-03-06 PlanBase Inc. & Glen Peterson
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless r==uired by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.organicdesign.fp;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.organicdesign.fp.ephemeral.ViewFromArray;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class FunctionUtilsTest {

    @Test
    public void composePredicatesWithAndAndOr() {
        assertTrue(FunctionUtils.and() == FunctionUtils.accept());
        assertTrue(FunctionUtils.and(FunctionUtils.accept()) == FunctionUtils.accept());
        assertTrue(FunctionUtils.<Object>and(FunctionUtils.accept(),
                                             FunctionUtils.accept(),
                                             FunctionUtils.accept()) == FunctionUtils.accept());
        assertTrue(FunctionUtils.and(FunctionUtils.reject()) == FunctionUtils.reject());


        assertTrue(FunctionUtils.or() == FunctionUtils.reject());
        assertTrue(FunctionUtils.or(FunctionUtils.accept()) == FunctionUtils.accept());
        assertTrue(FunctionUtils.<Object>or(FunctionUtils.reject(),
                                            FunctionUtils.reject(),
                                            FunctionUtils.reject(),
                                            FunctionUtils.accept()) == FunctionUtils.accept());
        assertTrue(FunctionUtils.<Object>or(FunctionUtils.accept(),
                                            FunctionUtils.reject(),
                                            FunctionUtils.reject(),
                                            FunctionUtils.reject()) == FunctionUtils.accept());

        assertTrue(FunctionUtils.or(FunctionUtils.reject()) == FunctionUtils.reject());


        assertArrayEquals(ViewFromArray.of(1,2,3,4,5,6,7,8,9)
                                  .filter(FunctionUtils.and(i -> i > 2,
                                                            i -> i < 6))
                                  .toJavaArrayList()
                                  .toArray(),
                new Integer[] { 3,4,5 });

        assertArrayEquals(ViewFromArray.of(1,2,3,4,5,6,7,8,9)
                                  .filter(FunctionUtils.or(i -> i < 3,
                                                           i -> i > 5))
                                  .toJavaArrayList()
                                  .toArray(),
                new Integer[] { 1,2, 6,7,8,9 });

        assertArrayEquals(ViewFromArray.of(1,2,3,4,5,6,7,8,9)
                                  .filter(FunctionUtils.or(i -> i < 3,
                                                           i -> i == 4,
                                                           i -> i > 5))
                                  .toJavaArrayList()
                                  .toArray(),
                new Integer[] { 1,2, 4, 6,7,8,9 });
    }
}
