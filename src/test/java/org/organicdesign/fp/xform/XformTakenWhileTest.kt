// Copyright 2015 PlanBase Inc. & Glen Peterson
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

package org.organicdesign.fp.xform

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.organicdesign.fp.function.Fn1

import java.util.Arrays

import org.junit.Assert.assertArrayEquals

@RunWith(JUnit4::class)
class XformTakenWhileTest {

    @Test
    fun takeItemsInOneBatch() {
        val seq = Xform.of(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9))
        assertArrayEquals(arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9),
                          seq.takeWhile(Fn1.accept()).toMutList().toTypedArray())
        assertArrayEquals(arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9),
                          seq.takeWhile { i -> i < 10 }.toMutList().toTypedArray())
        assertArrayEquals(arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9),
                          seq.takeWhile { i -> i <= 9 }.toMutList().toTypedArray())
        assertArrayEquals(arrayOf(1, 2, 3, 4, 5, 6, 7, 8),
                          seq.takeWhile { i -> i <= 8 }.toMutList().toTypedArray())
        assertArrayEquals(arrayOf(1, 2, 3, 4, 5, 6, 7),
                          seq.takeWhile { i -> i <= 7 }.toMutList().toTypedArray())
        assertArrayEquals(arrayOf(1, 2, 3),
                          seq.takeWhile { i -> i <= 3 }.toMutList().toTypedArray())
        assertArrayEquals(arrayOf(1, 2),
                          seq.takeWhile { i -> i <= 2 }.toMutList().toTypedArray())
        assertArrayEquals(arrayOf(1),
                          seq.takeWhile { i -> i <= 1 }.toMutList().toTypedArray())
        assertArrayEquals(arrayOf<Int>(),
                          seq.takeWhile(Fn1.reject()).toMutList().toTypedArray())
        assertArrayEquals(arrayOf<Int>(),
                          seq.takeWhile { i -> i > 10 }.toMutList().toTypedArray())
    }

    @Test(expected = IllegalArgumentException::class)
    fun exception1() {
        Xform.of(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9)).takeWhile(null)
    }

}
