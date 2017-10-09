package org.organicdesign.fp.collections

import org.junit.Test

import org.junit.Assert.assertArrayEquals

class CowryTest {
    @Test
    fun testSingleElementArray() {
        assertArrayEquals(arrayOf<Any>(1), singleElementArray(1))
    }

    @Test
    fun testSplitIntArray() {
        assertArrayEquals(arrayOf(intArrayOf(), intArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9)),
                          splitArray(intArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9), 0))

        assertArrayEquals(arrayOf(intArrayOf(1), intArrayOf(2, 3, 4, 5, 6, 7, 8, 9)),
                          splitArray(intArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9), 1))

        assertArrayEquals(arrayOf(intArrayOf(1, 2), intArrayOf(3, 4, 5, 6, 7, 8, 9)),
                          splitArray(intArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9), 2))

        assertArrayEquals(arrayOf(intArrayOf(1, 2, 3), intArrayOf(4, 5, 6, 7, 8, 9)),
                          splitArray(intArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9), 3))

        assertArrayEquals(arrayOf(intArrayOf(1, 2, 3, 4), intArrayOf(5, 6, 7, 8, 9)),
                          splitArray(intArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9), 4))

        assertArrayEquals(arrayOf(intArrayOf(1, 2, 3, 4, 5), intArrayOf(6, 7, 8, 9)),
                          splitArray(intArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9), 5))

        assertArrayEquals(arrayOf(intArrayOf(1, 2, 3, 4, 5, 6), intArrayOf(7, 8, 9)),
                          splitArray(intArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9), 6))

        assertArrayEquals(arrayOf(intArrayOf(1, 2, 3, 4, 5, 6, 7), intArrayOf(8, 9)),
                          splitArray(intArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9), 7))

        assertArrayEquals(arrayOf(intArrayOf(1, 2, 3, 4, 5, 6, 7, 8), intArrayOf(9)),
                          splitArray(intArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9), 8))

        assertArrayEquals(arrayOf(intArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9), intArrayOf()),
                          splitArray(intArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9), 9))
    }

    @Test(expected = Exception::class)
    @Throws(Exception::class)
    fun testSplitArrayEx1() {
        splitArray(intArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9), 10)
    }

    @Test(expected = Exception::class)
    @Throws(Exception::class)
    fun testSplitArrayEx2() {
        splitArray(intArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9), -1)
    }
}