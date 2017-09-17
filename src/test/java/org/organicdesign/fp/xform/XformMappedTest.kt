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

import org.junit.Assert.assertArrayEquals
import org.junit.Test
import org.organicdesign.fp.function.Fn1
import java.util.Arrays

class XformMappedTest {

    @Test
    fun mapInOneBatch() {
        val ints = arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9)
        val seq = Xform.of(Arrays.asList(*ints))
        assertArrayEquals(ints,
                          seq.map(Fn1.identity()).toMutList().toTypedArray())

        assertArrayEquals(arrayOf(2, 3, 4, 5, 6, 7, 8, 9, 10),
                          seq.map(plusOne).toMutList().toTypedArray())

        assertArrayEquals(arrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8),
                          seq.map(minusOne).toMutList().toTypedArray())

        assertArrayEquals(ints,
                          seq.map(plusOne).map(minusOne).toMutList().toTypedArray())

        assertArrayEquals(ints,
                          seq.map(minusOne).map(plusOne).toMutList().toTypedArray())

        assertArrayEquals(arrayOf(3, 4, 5, 6, 7, 8, 9, 10, 11),
                          seq.map(plusOne).map(plusOne).toMutList().toTypedArray())

    }

    @Test
    fun mapInMultipleBatches() {

        assertArrayEquals(Xform.of(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9))
                                  .map(plusOne).map(plusOne).map(plusOne).map(plusOne).map(plusOne)
                                  .map(plusOne).map(plusOne).map(plusOne).map(plusOne).map(plusOne)
                                  .toMutList().toTypedArray(),
                          arrayOf(11, 12, 13, 14, 15, 16, 17, 18, 19))

        assertArrayEquals(Xform.of(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9))
                                  .map(minusOne).map(minusOne).map(minusOne).map(minusOne)
                                  .map(minusOne).map(minusOne).map(minusOne).map(minusOne)
                                  .map(minusOne).map(minusOne)
                                  .toMutList().toTypedArray(),
                          arrayOf(-9, -8, -7, -6, -5, -4, -3, -2, -1))

    }

    companion object {
        private val plusOne = { x:Int -> x + 1 }
        private val minusOne = { x:Int -> x - 1 }
    }

}
