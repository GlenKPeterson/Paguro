// Copyright 2015-08-30 PlanBase Inc. & Glen Peterson
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

import junit.framework.TestCase
import org.junit.Assert.assertArrayEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.organicdesign.fp.StaticImports.vec
import org.organicdesign.fp.StaticImports.xform
import org.organicdesign.fp.collections.UnmodIterable.emptyUnmodIterable
import org.organicdesign.fp.function.Fn1.accept
import org.organicdesign.fp.function.Fn1.reject
import org.organicdesign.fp.oneOf.Option
import org.organicdesign.testUtils.EqualsContract.equalsDistinctHashCode
import java.util.ArrayList
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

@RunWith(JUnit4::class)
class XformTest : TestCase() {

    @Test
    fun testDropWhile() {
        val xf = Xform.of(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9))
        assertEquals(listOf(5, 6, 7, 8, 9), xf.dropWhile { x -> x < 5 }.toImList())
        assertEquals(listOf(5, 6, 7, 8, 9), xf.dropWhile { x -> x < 5 }.toImList())
        assertEquals(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9), xf.dropWhile { x -> x < 1 }.toImList())
        assertEquals(listOf(2, 3, 4, 5, 6, 7, 8, 9), xf.dropWhile { x -> x < 2 }.toImList())
        assertEquals(listOf(3, 4, 5, 6, 7, 8, 9), xf.dropWhile { x -> x < 3 }.toImList())
        assertEquals(listOf(8, 9), xf.dropWhile { x -> x < 8 }.toImList())
        assertEquals(listOf(9), xf.dropWhile { x -> x < 9 }.toImList())
        assertEquals(emptyList<Any>(), xf.dropWhile { x -> x < 10 }.toImList())
    }

    @Test
    fun testEqualsHashCode() {
        equalsDistinctHashCode(Xform.SourceProviderIterableDesc(listOf("Hi", "Pleased", "Bye")),
                               Xform.of(listOf("Hi", "Pleased", "Bye")),
                               Xform.of(listOf("Hi", "Pleased", "Bye")),
                               Xform.of(listOf("Hi", "PleaZed", "Bye")))
    }

    @Test
    fun testBasics() {
        val src:List<Int> = listOf(1, 2, 3)
        println("src: $src")
        basics(Xform.of(src))
        basics(Xform.of(Xform.of(src).toImSortedSet { a, b -> a!! - b!! }))
        basics(Xform.of(src))
    }

    @Test
    fun longerCombinations() {
        val src = arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9)
        longerCombinations(Xform.of(listOf(*src)))
        longerCombinations(Xform.of(Xform.of(listOf(*src)).toImSortedSet { a, b -> a!! - b!! }))
        longerCombinations(Xform.of(listOf(*src)))
    }

    @Test(expected = IllegalArgumentException::class)
    fun concatEx() {
        Xform.of(emptyList<Any>()).concat(null)
    }

    @Test(expected = IllegalArgumentException::class)
    fun precatEx() {
        Xform.of(emptyList<Any>()).precat(null)
    }

    @Test
    fun doubleNull() {
        assertArrayEquals(arrayOfNulls<Int>(0),
                          Xform.of(emptyList<Any>())
                                  .concat(emptyList()).toMutList().toTypedArray())
        assertArrayEquals(arrayOfNulls<Int>(0),
                          Xform.of(emptyList<Any>())
                                  .precat(emptyList()).toMutList().toTypedArray())
    }

    @Test
    fun precat() {
        assertArrayEquals(arrayOf(5, 6, 7, 8, 9),
                          Xform.of(listOf(5, 6, 7, 8, 9))
                                  .precat(emptyList()).toMutList().toTypedArray())
        assertArrayEquals(arrayOf(4, 5, 6, 7, 8, 9),
                          Xform.of(listOf(5, 6, 7, 8, 9))
                                  .precat(Xform.of(listOf(4))).toMutList().toTypedArray())
        assertArrayEquals(arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9),
                          Xform.of(listOf(5, 6, 7, 8, 9))
                                  .precat(Xform.of(listOf(1, 2, 3, 4))).toMutList().toTypedArray())
        assertArrayEquals(arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9),
                          Xform.of(listOf(5, 6, 7, 8, 9))
                                  .precat(listOf(1, 2, 3, 4)).toMutList().toTypedArray())
    }

    @Test
    fun concat() {
        assertArrayEquals(arrayOf(1, 2, 3, 4),
                          Xform.of(listOf(1, 2, 3, 4))
                                  .concat(emptyList()).toMutList().toTypedArray())

        //        assertArrayEquals(new Integer[] { 1, 2, 3, 4 },
        //                          Xform.of(listOf(1, 2, 3, 4))
        //                                  .concat(Xform.emptyXform()).toMutList().toArray());

        assertArrayEquals(arrayOf(1, 2, 3, 4, 5),
                          Xform.of(listOf(1, 2, 3, 4))
                                  .concat(Xform.of(listOf(5))).toMutList().toTypedArray())

        assertArrayEquals(arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9),
                          Xform.of(listOf(1, 2, 3, 4))
                                  .concat(Xform.of(listOf(5, 6, 7, 8, 9))).toMutList().toTypedArray())
    }

    @Test
    fun chainedPrependAppend() {
        assertArrayEquals(arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9),
                          Xform.of(listOf(5))                     //         5
                                  .precat(Xform.of(listOf(4)))   //       4,5
                                  .concat(Xform.of(listOf(6)))    //       4,5,6
                                  .precat(Xform.of(listOf(2, 3))) //   2,3,4,5,6
                                  .concat(Xform.of(listOf(7, 8)))  //   2,3,4,5,6,7,8
                                  .precat(Xform.of(listOf(1)))   // 1,2,3,4,5,6,7,8
                                  .concat(Xform.of(listOf(9)))    // 1,2,3,4,5,6,7,8,9
                                  .toMutList().toTypedArray())

        assertArrayEquals(arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9),
                          Xform.of(listOf(5))
                                  .precat(Xform.of(emptyList())).precat(Xform.of(emptyList()))
                                  .concat(Xform.of(emptyList())).concat(Xform.of(emptyList()))
                                  .precat(Xform.of(listOf(4)))
                                  .precat(Xform.of(emptyList())).precat(Xform.of(emptyList()))
                                  .concat(Xform.of(emptyList())).concat(Xform.of(emptyList()))
                                  .concat(Xform.of(listOf(6)))
                                  .precat(Xform.of(emptyList())).precat(Xform.of(emptyList()))
                                  .concat(Xform.of(emptyList())).concat(Xform.of(emptyList()))
                                  .precat(Xform.of(listOf(2, 3)))
                                  .precat(Xform.of(emptyList())).precat(Xform.of(emptyList()))
                                  .concat(Xform.of(emptyList())).concat(Xform.of(emptyList()))
                                  .concat(Xform.of(listOf(7, 8)))
                                  .precat(Xform.of(emptyList())).precat(Xform.of(emptyList()))
                                  .concat(Xform.of(emptyList())).concat(Xform.of(emptyList()))
                                  .precat(Xform.of(listOf(1)))
                                  .precat(Xform.of(emptyList())).precat(Xform.of(emptyList()))
                                  .concat(Xform.of(emptyList())).concat(Xform.of(emptyList()))
                                  .concat(Xform.of(listOf(9)))
                                  .precat(Xform.of(emptyList())).precat(Xform.of(emptyList()))
                                  .concat(Xform.of(emptyList())).concat(Xform.of(emptyList()))
                                  .toMutList().toTypedArray())
    }

    @Test
    fun emptiness() {
        val seq = Xform.of(emptyList<Int>())
        assertArrayEquals(arrayOfNulls<Int>(0), seq.drop(0).toMutList().toTypedArray())
        assertArrayEquals(arrayOfNulls<Int>(0), seq.drop(1).toMutList().toTypedArray())
        assertArrayEquals(arrayOfNulls<Int>(0), seq.drop(2).toMutList().toTypedArray())
    }

    @Test
    fun singleElement() {
        val seq = Xform.of(listOf(1))
        assertArrayEquals(arrayOf(1), seq.drop(0).toMutList().toTypedArray())
        assertArrayEquals(arrayOfNulls<Int>(0), seq.drop(1).toMutList().toTypedArray())
        assertArrayEquals(arrayOfNulls<Int>(0), seq.drop(2).toMutList().toTypedArray())
    }

    @Test
    fun twoElement() {
        val seq = Xform.of(listOf(1, 2))
        assertArrayEquals(arrayOf(1, 2), seq.drop(0).toMutList().toTypedArray())
        assertArrayEquals(arrayOf(2), seq.drop(1).toMutList().toTypedArray())
        assertArrayEquals(arrayOfNulls<Int>(0), seq.drop(2).toMutList().toTypedArray())
    }

    @Test
    fun singleDrops() {
        val ints = arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9)
        val xform = Xform.of(listOf(*ints))
        assertArrayEquals(ints, xform.drop(0).toMutList().toTypedArray())
        assertArrayEquals(arrayOf(2, 3, 4, 5, 6, 7, 8, 9), xform.drop(1).toMutList().toTypedArray())
        assertArrayEquals(arrayOf(3, 4, 5, 6, 7, 8, 9), xform.drop(2).toMutList().toTypedArray())
        assertArrayEquals(arrayOf(4, 5, 6, 7, 8, 9), xform.drop(3).toMutList().toTypedArray())
        assertArrayEquals(arrayOf(9), xform.drop(8).toMutList().toTypedArray())
        assertArrayEquals(arrayOfNulls<Int>(0), Xform.of(emptyList<Any>()).toMutList().toTypedArray())
        assertArrayEquals(arrayOfNulls<Int>(0), xform.drop(9).toMutList().toTypedArray())
        assertArrayEquals(arrayOfNulls<Int>(0), xform.drop(10).toMutList().toTypedArray())
        assertArrayEquals(arrayOfNulls<Int>(0), xform.drop(10000).toMutList().toTypedArray())
        assertArrayEquals(arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9),
                          Xform.of(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9)).drop(0).toMutList().toTypedArray())
        assertArrayEquals(arrayOf(2, 3, 4, 5, 6, 7, 8, 9),
                          Xform.of(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9)).drop(1).toMutList().toTypedArray())
        assertArrayEquals(arrayOf(3, 4, 5, 6, 7, 8, 9),
                          Xform.of(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9)).drop(2).toMutList().toTypedArray())
        assertArrayEquals(arrayOf(4, 5, 6, 7, 8, 9),
                          Xform.of(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9)).drop(3).toMutList().toTypedArray())
        assertArrayEquals(arrayOf(9),
                          Xform.of(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9)).drop(8).toMutList().toTypedArray())
        assertArrayEquals(arrayOf<Int>(),
                          Xform.of(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9)).drop(9).toMutList().toTypedArray())
        assertArrayEquals(arrayOf<Int>(),
                          Xform.of(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9)).drop(10).toMutList().toTypedArray())
        assertArrayEquals(arrayOf<Int>(),
                          Xform.of(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9)).drop(10000).toMutList().toTypedArray())
    }

    @Test
    fun multiDrops() {
        val ints = arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9)
        val seq = Xform.of(listOf(*ints))
        assertArrayEquals(arrayOf(3, 4, 5, 6, 7, 8, 9),
                          seq.drop(1).drop(1).toMutList().toTypedArray())
        assertArrayEquals(arrayOf(4, 5, 6, 7, 8, 9),
                          seq.drop(1).drop(1).drop(1).toMutList().toTypedArray())
        assertArrayEquals(arrayOf(9),
                          seq
                                  .drop(1).drop(1).drop(1).drop(1).drop(1)
                                  .drop(1).drop(1).drop(1).toMutList().toTypedArray())
        assertArrayEquals(arrayOf<Int>(),
                          seq
                                  .drop(1).drop(1).drop(1).drop(1).drop(1)
                                  .drop(1).drop(1).drop(1).drop(1).toMutList().toTypedArray())
        assertArrayEquals(arrayOf<Int>(),
                          seq
                                  .drop(1).drop(1).drop(1).drop(1).drop(1)
                                  .drop(1).drop(1).drop(1).drop(1).drop(1).toMutList().toTypedArray())
        assertArrayEquals(arrayOf<Int>(),
                          seq
                                  .drop(1).drop(1).drop(1).drop(1).drop(1)
                                  .drop(1).drop(1).drop(1).drop(1).drop(1)
                                  .drop(1).drop(1).drop(1).drop(1).drop(1).toMutList().toTypedArray())

        assertArrayEquals(arrayOf(7, 8, 9),
                          seq
                                  .drop(0).drop(1).drop(2).drop(3).toMutList().toTypedArray())
        assertArrayEquals(arrayOf(7, 8, 9),
                          seq
                                  .drop(3).drop(2).drop(1).drop(0).toMutList().toTypedArray())

        assertArrayEquals(arrayOf(3, 4, 5, 6, 7, 8, 9),
                          Xform.of(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9))
                                  .drop(1)
                                  .drop(1)
                                  .toMutList()
                                  .toTypedArray())
        assertArrayEquals(arrayOf(4, 5, 6, 7, 8, 9),
                          Xform.of(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9))
                                  .drop(1)
                                  .drop(1)
                                  .drop(1)
                                  .toMutList()
                                  .toTypedArray())
        assertArrayEquals(arrayOf(9),
                          Xform.of(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9))
                                  .drop(1).drop(1).drop(1).drop(1).drop(1)
                                  .drop(1).drop(1).drop(1).toMutList().toTypedArray())
        assertArrayEquals(arrayOf<Int>(),
                          Xform.of(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9))
                                  .drop(1).drop(1).drop(1).drop(1).drop(1)
                                  .drop(1).drop(1).drop(1).drop(1).toMutList().toTypedArray())
        assertArrayEquals(arrayOf<Int>(),
                          Xform.of(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9))
                                  .drop(1).drop(1).drop(1).drop(1).drop(1)
                                  .drop(1).drop(1).drop(1).drop(1).drop(1).toMutList().toTypedArray())
        assertArrayEquals(arrayOf<Int>(),
                          Xform.of(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9))
                                  .drop(1).drop(1).drop(1).drop(1).drop(1)
                                  .drop(1).drop(1).drop(1).drop(1).drop(1)
                                  .drop(1).drop(1).drop(1).drop(1).drop(1).toMutList().toTypedArray())

        assertArrayEquals(arrayOf(7, 8, 9),
                          Xform.of(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9))
                                  .drop(0).drop(1).drop(2).drop(3).toMutList().toTypedArray())
        assertArrayEquals(arrayOf(7, 8, 9),
                          Xform.of(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9))
                                  .drop(3).drop(2).drop(1).drop(0).toMutList().toTypedArray())
    }

    @Test(expected = IllegalArgumentException::class)
    fun exception1() {
        Xform.of(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9)).drop(-1)
    }

    @Test(expected = IllegalArgumentException::class)
    fun exception2() {
        Xform.of(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9)).drop(-99)
    }

    @Test(expected = IllegalArgumentException::class)
    fun nullException() {
        assertArrayEquals(arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9),
                          Xform.of(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9)).filter(null).toMutList().toTypedArray())
    }

    @Test
    fun singleFilter() {
        assertArrayEquals(arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9),
                          Xform.of(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9))
                                  .filter(accept())
                                  .toMutList()
                                  .toTypedArray())

        assertArrayEquals(arrayOf<Int>(),
                          Xform.of(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9))
                                  .filter(reject())
                                  .toMutList()
                                  .toTypedArray())

        assertArrayEquals(arrayOf(5, 6, 7, 8, 9),
                          Xform.of(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9))
                                  .filter { i -> i > 4 }
                                  .toMutList()
                                  .toTypedArray())

        assertArrayEquals(arrayOf<Int>(),
                          Xform.of(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9))
                                  .filter { i -> i < 1 }
                                  .toMutList()
                                  .toTypedArray())

        assertArrayEquals(arrayOf(3),
                          Xform.of(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9))
                                  .filter { i -> i == 3 }
                                  .toMutList()
                                  .toTypedArray())

        assertArrayEquals(arrayOf(1),
                          Xform.of(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9))
                                  .filter { i -> i == 1 }
                                  .toMutList()
                                  .toTypedArray())

        assertArrayEquals(arrayOf(9),
                          Xform.of(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9))
                                  .filter { i -> i == 9 }
                                  .toMutList()
                                  .toTypedArray())

        assertArrayEquals(arrayOf(1, 2, 3, 4, 5, 6),
                          Xform.of(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9))
                                  .filter { i -> i < 7 }
                                  .toMutList()
                                  .toTypedArray())

    }

    @Test
    fun chainedFilters() {
        assertArrayEquals(arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9),
                          Xform.of(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9))
                                  .filter(accept()).filter(accept()).filter(accept())
                                  .filter(accept()).filter(accept()).filter(accept())
                                  .filter(accept()).filter(accept()).toMutList().toTypedArray())

        assertArrayEquals(arrayOf<Int>(),
                          Xform.of(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9))
                                  .filter(reject()).filter(accept()).filter(accept())
                                  .filter(accept()).filter(accept()).filter(accept())
                                  .filter(accept()).filter(accept()).toMutList().toTypedArray())

        assertArrayEquals(arrayOf<Int>(),
                          Xform.of(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9))
                                  .filter(accept()).filter(accept()).filter(accept())
                                  .filter(accept()).filter(reject()).filter(accept())
                                  .filter(accept()).filter(accept()).toMutList().toTypedArray())

        assertArrayEquals(arrayOf<Int>(),
                          Xform.of(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9))
                                  .filter(accept()).filter(accept()).filter(accept())
                                  .filter(accept()).filter(accept()).filter(accept())
                                  .filter(accept()).filter(reject()).toMutList().toTypedArray())

        assertArrayEquals(arrayOf(3, 4, 6),
                          Xform.of(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9))
                                  .filter { i -> i > 2 }
                                  .filter { i -> i < 7 }
                                  .filter { i -> i != 5 }.toMutList().toTypedArray())

    }

    @Test
    fun singleFlatMap() {
        assertEquals(0, Xform.of(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9))
                .flatMap{ emptyUnmodIterable<Int>() }
                .toImList()
                .size)

        assertEquals(0, Xform.of(emptyList<Any>())
                .flatMap<Any>{ emptyUnmodIterable() }
                .toImList()
                .size)

        assertArrayEquals(arrayOf(1, 2, 3, 2, 4, 6, 3, 6, 9, 4, 8, 12, 5, 10, 15, 6, 12, 18, 7, 14, 21, 8, 16, 24, 9, 18, 27),
                          Xform.of(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9))
                                  .flatMap { i -> Xform.of(listOf(i, i!! * 2, i * 3)) }.toMutList().toTypedArray())

        assertArrayEquals(arrayOf("1", "2", "2", "3", "3", "4"),
                          Xform.of(listOf(1, 2, 3))
                                  .flatMap { i ->
                                      Xform.of(listOf(i.toString(),
                                                             (i!! + 1).toString()))
                                  }
                                  .toMutList().toTypedArray())
    }

    @Test
    fun flatEmpty() {
        assertEquals(0, Xform.of(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9))
                .flatMap{ emptyUnmodIterable<Any>() }
                .toImList()
                .size)

        assertEquals(0, Xform.of(emptyList<Any>())
                .flatMap { _ -> Xform.of(emptyList<Any>()) }
                .toImList()
                .size)

        // This tests that I didn't just look ahead 2 or 3 times.  That the look-ahead is sufficient.
        val count = AtomicInteger(0)
        assertArrayEquals(arrayOf("a9", "b9"),
                          Xform.of(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9))
                                  .flatMap { a ->
                                      if (count.incrementAndGet() > 8)
                                          Xform.of(listOf("a" + a!!, "b" + a))
                                      else
                                          Xform.of(emptyList())
                                  }
                                  .toMutList().toTypedArray())

        count.set(0)
        assertArrayEquals(arrayOf("c8", "d8", "c9", "d9"),
                          Xform.of(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9))
                                  .flatMap { a ->
                                      if (count.incrementAndGet() > 7)
                                          Xform.of(listOf("c" + a!!, "d" + a))
                                      else
                                          Xform.of(emptyList())
                                  }
                                  //                                  .forEach((item) -> {
                                  //                              System.out.println("Item " + item);
                                  //                              return null;
                                  //                          })
                                  .toMutList().toTypedArray())

        count.set(0)
        assertArrayEquals(arrayOf("e1", "f1", "e2", "f2"),
                          Xform.of(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9))
                                  .flatMap { a ->
                                      if (count.incrementAndGet() < 3)
                                          Xform.of(listOf("e" + a!!, "f" + a))
                                      else
                                          Xform.of(emptyList())
                                  }
                                  //                                  .forEach((item) -> {
                                  //                              System.out.println("count: " + count.value() + " Item " + item);
                                  //                              return null;
                                  //                          })
                                  .toMutList().toTypedArray())

        val shrinkSeq = AtomicReference(Xform.of(listOf(1, 2, 3)))
        assertArrayEquals(arrayOf(2, 3, 3),
                          Xform.of(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9))
                                  .flatMap { _ -> shrinkSeq.updateAndGet { seq -> seq.drop(1) } }
                                  .toMutList().toTypedArray())

        // Now start by returning an ofArray, then a seq of length 1, then length 2, etc.
        // The first ofArray should not end the processing.
        val growSeq = AtomicReference(Xform.of(emptyList<Int>()))
        val incInt = AtomicInteger(0)
        assertArrayEquals(arrayOf(1, 1, 2),
                          Xform.of(listOf(1, 2, 3))
                                  .flatMap { _ ->
                                      if (incInt.get() > 0) {
                                          growSeq.updateAndGet { seq -> seq.concat(vec(incInt.get())) }
                                      }
                                      incInt.getAndIncrement()
                                      growSeq.get()
                                  }
                                  .toMutList().toTypedArray())
    }

    @Test
    fun flatMapChain() {
        assertEquals(0, Xform.of(vec(1, 2, 3, 4, 5, 6, 7, 8, 9))
                .flatMap{ emptyUnmodIterable<Any>() }
                .flatMap{ emptyUnmodIterable<Any>() }
                .flatMap{ emptyUnmodIterable<Any>() }
                .toImList()
                .size)

        assertEquals(0, Xform.of(emptyList<Any>())
                .flatMap { i -> Xform.of(listOf(i)) }
                .flatMap { i -> Xform.of(listOf(i)) }
                .flatMap { i -> Xform.of(listOf(i)) }
                .toImList()
                .size)

        assertArrayEquals(arrayOf<Int>(),
                          Xform.of(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9))
                                  .flatMap{ emptyUnmodIterable<Any>() }
                                  .flatMap{ emptyUnmodIterable<Any>() }
                                  .flatMap{ emptyUnmodIterable<Any>() }.toMutList().toTypedArray())

        assertArrayEquals(arrayOf(1, 2, 2, 3, 3, 4, 10, 11, 20, 21, 30, 31),
                          Xform.of(listOf(1, 10))
                                  .flatMap { i -> Xform.of(listOf(i, i!! * 2, i * 3)) }
                                  .flatMap { i -> Xform.of(listOf(i, i!! + 1)) }
                                  .toMutList().toTypedArray())
    }

    // Below here taken from SequenceTest
    @Test
    fun construction() {
        val ints = arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9)
        assertArrayEquals(ints, Xform.of(listOf(*ints)).toMutList().toTypedArray())
        assertArrayEquals(ints, Xform.of(listOf(*ints)).toMutList().toTypedArray())
    }

    @Test
    fun testNullConstruction() {
        assertEquals(Xform.EMPTY, Xform.of<Any>(null))
        assertEquals(Xform.EMPTY, xform<Any>(null))
    }

    //    @Test public void emptyXform() {
    //        assertEquals(0, Xform.EMPTY.hashCode());
    //        assertEquals(0, Xform.EMPTY.drop(1).hashCode());
    //        assertEquals(0, Xform.EMPTY.drop(1).drop(1).drop(1).hashCode());
    //
    //        assertEquals(Option.none(), Xform.EMPTY.head());
    //
    //        assertEquals(Xform.EMPTY, Xform.EMPTY);
    //        assertEquals(Xform.EMPTY, Xform.EMPTY.drop(1));
    //        assertEquals(Xform.EMPTY, Xform.EMPTY.drop(1).drop(1));
    //        assertTrue(Xform.EMPTY.equals(Xform.EMPTY.drop(1)));
    //        assertTrue(Xform.EMPTY.drop(1).equals(Xform.EMPTY));
    //    }

    @Test
    fun fold() {
        val ints = arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9)

        assertEquals(Integer.valueOf(45),
                     Xform.of(listOf(*ints)).fold(0) { accum, i -> accum!! + i!! })
    }

    @Test(expected = IllegalArgumentException::class)
    fun foldEx() {
        assertEquals(Integer.valueOf(45),
                     Xform.of(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9))
                             .fold(0, null))
    }

    @Test(expected = IllegalArgumentException::class)
    fun foldEx2() {
        assertEquals(45,
                     Xform.of(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9))
                             .foldUntil<Any, Any>(null, { a, _ -> a }, null).good())
    }

    @Test(expected = IllegalArgumentException::class)
    fun flatMapEx() {
        Xform.of(listOf(1, 2, 3)).flatMap<Any>(null)
    }

    @Test(expected = IllegalArgumentException::class)
    fun mapEx() {
        Xform.of(listOf(1, 2, 3)).map<Any>(null)
    }

    @Test
    fun foldTerm() {
        val ints = arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9)

        assertEquals(Integer.valueOf(45),
                     Xform.of(listOf(*ints))
                             .foldUntil<Int, Any>(0, null) { accum, i -> accum!! + i!! }.good())

        assertEquals(Integer.valueOf(45),
                     Xform.of(listOf(*ints))
                             .foldUntil<Int, Any>(0, null) { accum, i -> accum!! + i!! }.good())

        assertArrayEquals(arrayOf(2, 3, 4),
                          Xform.of(listOf(*ints))
                                  .foldUntil(mutableListOf<Int>(),
                                             { accum, _ -> if (accum.size == 3) accum else null }
                                  ) { accum, i ->
                                      accum.add(i!! + 1)
                                      accum
                                  }.match({ g -> g }
                          ) { b -> b }!!.toTypedArray())
        assertArrayEquals(arrayOf(2, 3, 4, 5, 6, 7, 8, 9, 10),
                          Xform.of(listOf(*ints))
                                  .foldUntil(ArrayList<Int>(),
                                             { accum, _ -> if (accum.size == 20) accum else null }
                                  ) { accum, i ->
                                      accum.add(i!! + 1)
                                      accum
                                  }.match({ g -> g }) { b -> b }!!.toTypedArray())

        // This is fun and it should work.  But it really sets up for the early-termination test
        // next.
        assertEquals(listOf(1, 2, 3, 2, 4, 6, 3, 6, 9, 4, 8, 12, 5, 10, 15, 6, 12, 18,
                                   7, 14, 21, 8, 16, 24, 9, 18, 27),
                     Xform.of(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9))
                             .flatMap { i -> Xform.of(listOf(i, i!! * 2, i * 3)) }
                             .fold(ArrayList<Any>()
                             ) { alist, item ->
                                 alist.add(item)
                                 alist
                             })

        // Early termination test
        assertEquals(listOf(1, 2, 3, 2, 4, 6, 3, 6, 9, 4, 8, 12, 5, 10, 15, 6, 12, 18,
                                   7, 14, 21, 8, 16),
                     Xform.of(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9))
                             .flatMap { i -> Xform.of(listOf(i, i!! * 2, i * 3)) }
                             .foldUntil(ArrayList<Int>(),
                                        { items, _ -> if (items.contains(16)) items else null }
                             ) { alist, item ->
                                 alist.add(item)
                                 alist
                             }
                             .match({ g -> g }) { b -> b })
    }

    @Test
    fun testFirst() {
        assertEquals(Option.some(4),
                     Xform.of(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9))
                             .filter { i -> i > 3 }
                             .head())
        assertEquals(Option.some(1),
                     Xform.of(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9))
                             .filter { i -> i > 0 }
                             .head())
        assertEquals(Option.some(9),
                     Xform.of(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9))
                             .filter { i -> i > 8 }
                             .head())
        assertEquals(Option.none<Any>(),
                     Xform.of(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9))
                             .filter { i -> i < 1 }
                             .head())
        assertEquals(Option.none<Any>(),
                     Xform.of(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9))
                             .filter { i -> i > 9 }
                             .head())
    }

    @Test
    fun toIterator() {
        val ints = arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9)
        val seqIter = Xform.of(listOf(*ints)).iterator()
        val listIter = listOf(*ints).iterator()
        while (seqIter.hasNext() && listIter.hasNext()) {
            assertEquals(seqIter.next(), listIter.next())
        }
        TestCase.assertFalse(seqIter.hasNext())
        TestCase.assertFalse(listIter.hasNext())
    }

    //    @Test public void objectMethods() {
    //        Integer[] ints = new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
    //        Xform<Integer> seq1 = Xform.of(listOf(ints)).drop(3).take(4);
    //        Xform<Integer> seq2 = Xform.of(listOf(4, 5, 6, 7));
    //
    //        assertEquals(UnmodIterable.hashCode(seq1), UnmodIterable.hashCode(seq2));
    //
    //        assertTrue(UnmodSortedIterable.equals(seq1, seq1));
    //        assertTrue(UnmodSortedIterable.equals(seq2, seq2));
    //
    //        assertTrue(UnmodSortedIterable.equals(seq1, seq2));
    //        assertTrue(UnmodSortedIterable.equals(seq2, seq1));
    //
    //        assertEquals(UnmodIterable.hashCode(seq1.tail()), UnmodIterable.hashCode(seq2.tail()));
    //        assertTrue(UnmodSortedIterable.equals(seq1.tail(), seq2.tail()));
    //        assertTrue(UnmodSortedIterable.equals(seq2.tail(), seq1.tail()));
    //
    //        assertNotEquals(UnmodIterable.hashCode(seq1.tail()), UnmodIterable.hashCode(seq2));
    //        assertNotEquals(UnmodIterable.hashCode(seq1), UnmodIterable.hashCode(seq2.tail()));
    //        assertFalse(UnmodSortedIterable.equals(seq1.tail(), seq2));
    //        assertFalse(UnmodSortedIterable.equals(seq1, seq2.tail()));
    //
    //        assertFalse(UnmodSortedIterable.equals(seq2.tail(), seq1));
    //        assertFalse(UnmodSortedIterable.equals(seq2, seq1.tail()));
    //    }

    /**
     * Note that this tests the forEach() implementation on java.util.Iterable.  There used to be a
     * forEach() on Transformable too, but when I realized that it overloaded (but did not override)
     * the same method on Iterable and Stream, *and* when I read Josh Bloch Item 41 "Use Overloading
     * Judiciously" and realized what a bad idea it was, I just removed the method.  Maybe this test
     * should be deleted?  I guess it's good to prove that the caching works.
     */
    @Test
    fun forEach() {
        val ints = arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9)
        val seq = Xform.of(listOf(*ints))
        val output = ArrayList<Int>()
        seq.forEach { i -> output.add(i) }
        assertArrayEquals(ints, output.toTypedArray())
    }

    @Test
    fun firstMatching() {
        val ints = arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9)
        val seq = Xform.of(listOf(*ints))

        // TODO: We may want a head() method that returns an option because this gets ugly.
        assertEquals(Integer.valueOf(1), seq.filter { i -> i == 1 }.take(1).toImList().head().get())
        assertEquals(Integer.valueOf(3), seq.filter { i -> i > 2 }.take(1).toImList().head().get())
        TestCase.assertFalse(seq.filter { i -> i > 10 }.take(1).toImList().head().isSome)
    }

    @Test
    fun takeAndDrop() {
        assertArrayEquals(Xform.of(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9))
                                  .drop(0).take(8888).toMutList().toTypedArray(),
                          arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9))
        assertArrayEquals(Xform.of(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9))
                                  .drop(1).take(1).toMutList().toTypedArray(),
                          arrayOf(2))
        assertArrayEquals(Xform.of(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9))
                                  .drop(2).take(2).toMutList().toTypedArray(),
                          arrayOf(3, 4))
        assertArrayEquals(Xform.of(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9))
                                  .drop(3).take(3).toMutList().toTypedArray(),
                          arrayOf(4, 5, 6))
        assertArrayEquals(Xform.of(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9))
                                  .drop(9999).take(3).toMutList().toTypedArray(),
                          arrayOf<Int>())
        assertArrayEquals(Xform.of(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9))
                                  .drop(3).take(0).toMutList().toTypedArray(),
                          arrayOf<Int>())
    }

    @Test
    fun chain1() {
        assertArrayEquals(Xform.of(listOf(5))                      //         5
                                  .precat(Xform.of(listOf(4)))    //       4,5
                                  .concat(Xform.of(listOf(6)))     //       4,5,6
                                  .precat(Xform.of(listOf(2, 3))) //   2,3,4,5,6
                                  .concat(Xform.of(listOf(7, 8)))  //   2,3,4,5,6,7,8
                                  .precat(Xform.of(listOf(1)))    // 1,2,3,4,5,6,7,8
                                  .concat(Xform.of(listOf(9)))     // 1,2,3,4,5,6,7,8,9
                                  .filter { i -> i > 3 }              //       4,5,6,7,8,9
                                  .map { i -> i!! - 2 }                 //   2,3,4,5,6,7
                                  .take(5)                         //   2,3,4,5,6
                                  .drop(2)                         //       4,5,6
                                  .toMutList().toTypedArray(),
                          arrayOf(4, 5, 6))
    }

    companion object {

        fun basics(td: Xform<Int>) {
            assertEquals(listOf(1, 2, 3),
                         td.fold(listOf(), { accum: List<Int>, i: Int -> accum.plus(i) }))
            assertEquals(listOf(2, 3, 4),
                         td.map { i -> i!! + 1 }
                                 .fold(listOf(), { accum: List<Int>, i: Int -> accum.plus(i) }))
            assertEquals(listOf(1, 3),
                         td.filter { i -> i != 2 }
                                 .fold(listOf(), { accum: List<Int>, i: Int -> accum.plus(i) }))
            assertEquals(listOf(1, 10, 100, 2, 20, 200, 3, 30, 300),
                         td.flatMap { i -> vec(i, i!! * 10, i * 100) }
                                 .fold(ArrayList()) { accum:List<Int>, i:Int -> accum.plus(i) })
            assertEquals(listOf(2, 3),
                         td.drop(1)
                                 .fold(listOf(), { accum: List<Int>, i: Int -> accum.plus(i) }))
            assertEquals(listOf(1, 2, 3),
                         td.drop(0)
                                 .fold(listOf(), { accum: List<Int>, i: Int -> accum.plus(i) }))
            assertEquals(emptyList<Any>(),
                         td.drop(99)
                                 .fold(listOf(), { accum: List<Int>, i: Int -> accum.plus(i) }))
            assertEquals(emptyList<Any>(),
                         td.drop(Integer.MAX_VALUE.toLong())
                                 .fold(listOf(), { accum: List<Int>, i: Int -> accum.plus(i) }))
            assertEquals(listOf(1, 2),
                         td.take(2)
                                 .fold(listOf(), { accum: List<Int>, i: Int -> accum.plus(i) }))
            assertEquals(emptyList<Any>(),
                         td.take(0)
                                 .fold(listOf(), { accum: List<Int>, i: Int -> accum.plus(i) }))
            assertEquals(listOf(1, 2, 3),
                         td.take(3)
                                 .fold(listOf(), { accum: List<Int>, i: Int -> accum.plus(i) }))
            assertEquals(listOf(1, 2, 3),
                         td.take(99)
                                 .fold(listOf(), { accum: List<Int>, i: Int -> accum.plus(i) }))
            assertEquals(listOf(1, 2, 3),
                         td.take(Integer.MAX_VALUE.toLong())
                                 .fold(listOf(), { accum: List<Int>, i: Int -> accum.plus(i) }))
            assertEquals(listOf(1, 2, 3, 4, 5, 6),
                         td.concat(listOf(4, 5, 6))
                                 .fold(listOf(), { accum: List<Int>, i: Int -> accum.plus(i) }))
            assertEquals(listOf(1, 2, 3, 4, 5, 6),
                         td.concat(Xform.of(listOf(4, 5, 6)).toImSortedSet { a, b -> a!! - b!! })
                                 .fold(listOf(), { accum: List<Int>, i: Int -> accum.plus(i) }))
            //        assertEquals(listOf(1, 2, 3, 4, 5, 6),
            //                     td.concatArray(new Integer[]{4, 5, 6})
            //                       .fold(new ArrayList<>(), (MutableList<Integer> accum, Integer i) -> {
            //                           accum.plus(i);
            //                           return accum;
            //                       }));
            assertEquals(listOf(2, 3, 4, 5, 6, 7),
                         td.concat(listOf(4, 5, 6))
                                 .map { i -> i!! + 1 }
                                 .fold(listOf(), { accum: List<Int>, i: Int -> accum.plus(i) }))
        }

        fun longerCombinations(td: Xform<Int>) {
            assertEquals(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9),
                         td.fold(listOf(), { accum: List<Int>, i: Int -> accum.plus(i) }))
            assertEquals(listOf(2, 4, 6, 8),
                         td.filter { i -> i!! % 2 == 0 }
                                 .fold(listOf(), { accum: List<Int>, i: Int -> accum.plus(i) }))
            assertEquals(listOf(1, 2, 3, 4, 5),
                         td.take(5)
                                 .fold(listOf(), { accum: List<Int>, i: Int -> accum.plus(i) }))
            assertEquals(listOf(3, 5, 7, 9),
                         td.filter { i -> i!! % 2 == 0 }
                                 .map { i -> i!! + 1 }
                                 .fold(listOf(), { accum: List<Int>, i: Int -> accum.plus(i) }))
            assertEquals(listOf(3, 5, 7),
                         td.filter { i -> i!! % 2 == 0 }
                                 .map { i -> i!! + 1 }
                                 .take(3)
                                 .fold(listOf(), { accum: List<Int>, i: Int -> accum.plus(i) }))
            assertEquals(listOf(3, 30, 300, 5, 50, 500, 7, 70, 700, 9, 90, 900),
                         td.filter { i -> i!! % 2 == 0 }
                                 .map { i -> i!! + 1 }
                                 .flatMap { i -> vec(i, i!! * 10, i * 100) }
                                 .fold(listOf(), { accum: List<Int>, i: Int -> accum.plus(i) }))
            assertEquals(listOf(5, 50, 500, 7, 70, 700, 9, 90, 900),
                         td.drop(2)
                                 .filter { i -> i!! % 2 == 0 }
                                 .map { i -> i!! + 1 }
                                 .flatMap { i -> vec(i, i!! * 10, i * 100) }
                                 .fold(listOf(), { accum: List<Int>, i: Int -> accum.plus(i) }))
            //        System.out.println("Testing separate drop.");
            assertEquals(listOf(7, 9),
                         td.filter { i -> i!! % 2 == 0 }
                                 .map { i -> i!! + 1 }
                                 .drop(2)
                                 .fold(listOf(), { accum: List<Int>, i: Int -> accum.plus(i) }))
            //        System.out.println("Done testing separate drop.");
            assertEquals(listOf(7, 70, 700, 9, 90, 900),
                         td.filter { i -> i!! % 2 == 0 }
                                 .map { i -> i!! + 1 }
                                 .drop(2)
                                 .flatMap { i -> vec(i, i!! * 10, i * 100) }
                                 .fold(listOf(), { accum: List<Int>, i: Int -> accum.plus(i) }))
            assertEquals(listOf(500, 7, 70, 700, 9, 90, 900),
                         td.filter { i -> i!! % 2 == 0 }
                                 .map { i -> i!! + 1 }
                                 .flatMap { i -> vec(i, i!! * 10, i * 100) }
                                 .drop(5)
                                 .fold(listOf(), { accum: List<Int>, i: Int -> accum.plus(i) }))
            assertEquals(listOf(500, 7, 70, 700, 9, 90),
                         td.filter { i -> i!! % 2 == 0 }
                                 .map { i -> i!! + 1 }
                                 .flatMap { i -> vec(i, i!! * 10, i * 100) }
                                 .drop(5)
                                 .take(6)
                                 .fold(listOf(), { accum: List<Int>, i: Int -> accum.plus(i) }))
            assertEquals(listOf(500, 7, 70, 700, 9, 90, 91, 92, 93),
                         td.filter { i -> i!! % 2 == 0 }
                                 .map { i -> i!! + 1 }
                                 .flatMap { i -> vec(i, i!! * 10, i * 100) }
                                 .drop(5)
                                 .take(6)
                                 .concat(listOf(91, 92, 93))
                                 .fold(listOf(), { accum: List<Int>, i: Int -> accum.plus(i) }))
        }
    }
    // Above here taken from SequenceTest.
}