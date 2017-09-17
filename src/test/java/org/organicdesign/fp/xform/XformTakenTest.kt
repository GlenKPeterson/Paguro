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

import java.util.Arrays

import org.junit.Assert.assertArrayEquals

@RunWith(JUnit4::class)
class XformTakenTest {
    @Test
    fun takeItemsInOneBatch() {
        val ints = arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9)
        val seq = Xform.of(Arrays.asList(*ints))
        assertArrayEquals(ints, seq.take(9999).toMutList().toTypedArray())
        assertArrayEquals(ints, seq.take(10).toMutList().toTypedArray())
        assertArrayEquals(ints, seq.take(9).toMutList().toTypedArray())
        assertArrayEquals(arrayOf(1, 2, 3, 4, 5, 6, 7, 8), seq.take(8).toMutList().toTypedArray())
        assertArrayEquals(arrayOf(1, 2, 3, 4, 5, 6, 7), seq.take(7).toMutList().toTypedArray())
        assertArrayEquals(arrayOf(1, 2, 3), seq.take(3).toMutList().toTypedArray())
        assertArrayEquals(arrayOf(1, 2), seq.take(2).toMutList().toTypedArray())
        assertArrayEquals(arrayOf(1), seq.take(1).toMutList().toTypedArray())
        assertArrayEquals(arrayOfNulls<Int>(0), seq.take(0).toMutList().toTypedArray())
    }

    @Test(expected = IllegalArgumentException::class)
    fun exception1() {
        Xform.of(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9)).take(-1)
    }

    @Test(expected = IllegalArgumentException::class)
    fun exception2() {
        Xform.of(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9)).take(-99)
    }

    @Test
    fun takeItemsInMultiBatches() {
        val ints = arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9)
        val seq = Xform.of(Arrays.asList(*ints))
        assertArrayEquals(ints, seq.take(10).take(9999).take(10).toMutList().toTypedArray())
        assertArrayEquals(ints, seq.take(9).take(9).take(9).toMutList().toTypedArray())
        assertArrayEquals(arrayOf(1, 2, 3, 4, 5, 6),
                          seq.take(8).take(7).take(6).toMutList().toTypedArray())
        assertArrayEquals(arrayOf(1, 2, 3, 4, 5, 6),
                          seq.take(6).take(7).take(8).toMutList().toTypedArray())
        assertArrayEquals(arrayOf(1),
                          seq.take(999).take(1).take(9999999).toMutList().toTypedArray())
        assertArrayEquals(arrayOf(1),
                          seq.take(9999).take(1).take(3).toMutList().toTypedArray())
        assertArrayEquals(arrayOfNulls<Int>(0), seq.take(0).take(0).toMutList().toTypedArray())
        assertArrayEquals(arrayOfNulls<Int>(0), seq.take(0).take(1).toMutList().toTypedArray())
        assertArrayEquals(arrayOfNulls<Int>(0),
                          seq.take(0).take(99999999).take(9999999).toMutList().toTypedArray())
        assertArrayEquals(arrayOfNulls<Int>(0),
                          seq.take(99).take(9999).take(0).toMutList().toTypedArray())
    }
}