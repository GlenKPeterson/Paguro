// Copyright 2016 PlanBase Inc. & Glen Peterson
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
package org.organicdesign.fp.collections

import org.junit.Assert.*
import org.junit.Test
import org.organicdesign.fp.StaticImports.xform
import org.organicdesign.fp.TestUtilities
import org.organicdesign.fp.TestUtilities.compareIterators
import org.organicdesign.fp.TestUtilities.serializeDeserialize
import org.organicdesign.fp.collections.RrbTree.Companion.STRICT_NODE_LENGTH
import org.organicdesign.testUtils.EqualsContract.equalsDistinctHashCode
import java.security.NoSuchAlgorithmException
import java.util.ArrayList
import java.util.Arrays

class RrbTreeTest {

    internal val SEVERAL = 100 //0; //0; //SecureRandom.getInstanceStrong().nextInt(999999) + 33 ;

    @Test
    fun buildStrict() {
        buildInOrderTest(RrbTree.empty(), 10000)
        buildInOrderTest(RrbTree.emptyMutable(), 100000)
    }

    @Test
    fun insertAtZero() {
        buildReverseOrderTest(RrbTree.empty(), 1000)
        buildReverseOrderTest(RrbTree.emptyMutable(), 1000)
    }

    private fun randomInsertTest(indices: IntArray): RrbTree<Int> {
        randomInsertTest2(RrbTree.empty(), ArrayList(), indices)
        return randomInsertTest2(RrbTree.emptyMutable(), ArrayList(), indices)
    }

    /**
     * Sequences of random inserts which previously failed.  So far, these are
     */
    @Test
    fun insertRandPrevFail() {
        randomInsertTest(intArrayOf(0, 0, 2, 2, 2, 3, 5, 1))
        randomInsertTest(intArrayOf(0, 1, 2, 1, 0, 5, 2))
        randomInsertTest(intArrayOf(0, 0, 1, 2, 3, 0, 1, 5, 8, 2))
        randomInsertTest(intArrayOf(0, 1, 2, 2, 3, 2, 0, 6, 5, 6, 9, 9, 5, 6, 14, 2, 12, 8, 15))
        randomInsertTest(intArrayOf(0, 0, 0, 3, 4, 4, 5, 3, 0, 7, 5, 1, 11, 9, 0, 2, 7, 11, 12, 7, 6, 10, 2, 15, 24, 11, 18, 24, 20, 29, 17, 26, 3, 26, 20, 18, 11, 17, 14, 3, 0, 40, 7, 41, 6, 40, 5))
        randomInsertTest(intArrayOf(0, 0, 1, 2, 0, 1, 3, 4, 3, 1))
        randomInsertTest(intArrayOf(0, 1, 0, 2, 0, 3, 1, 0, 0, 1, 7))
    }

    @Test
    fun insertRandom() {
        val SEVERAL = 1000 //0; //0; //SecureRandom.getInstanceStrong().nextInt(999999) + 33 ;
        var rrb: RrbTree<Int> = RrbTree.empty()
        val control = ArrayList<Int>()
        val rands = ArrayList<Int>()
        try {
            for (j in 0 until SEVERAL) {
                val idx = rand.nextInt(rrb.size + 1)
                rands.add(idx)
                rrb = rrb.insert(idx, j)
                control.add(idx, j)
                assertEquals((j + 1).toLong(), rrb.size.toLong())
                assertEquals(Integer.valueOf(j), rrb[idx])
                //            System.out.println("control:" + control);
                //            System.out.println("===test:" + is);
                //            for (int k = 0; k <= j; k++) {
                ////                System.out.println("control[" + k + "]:" + control.get(k) + " test[" + k + "]:" + is.get(k));
                //                assertEquals("Checking index: " + k + " for size=" + control.size(), control.get(k), is.get(k));
                //            }
                //            System.out.println(is);
            }
            assertEquals(SEVERAL.toLong(), rrb.size.toLong())
            for (j in 0 until SEVERAL) {
                assertEquals(control[j], rrb[j])
            }
        } catch (e: Exception) {
            println("rands:" + rands) // print before blowing up...
            // OK, now we can continue throwing exception.
            throw e
        }

    }

    private fun randomInsertTest2(r: RrbTree<Int>, control: MutableList<Int>, indices: IntArray): RrbTree<Int> {
        var rrb = r
        assertEquals("inputSize (if this blows up, this test is being used incorrectly)", control.size.toLong(), rrb.size.toLong())
        //        System.out.println("Before:" + is.indentedStr());
        for (j in indices.indices) {
            val idx = indices[j]
            //            System.out.println("About to insert at=" + idx + " elem=" + j + " into=" + is.indentedStr());
            rrb = rrb.insert(idx, j)
            control.add(idx, j)
            rrb.debugValidate()
            //            System.out.println("control:" + control);
            //            System.out.println("===test:" + is.indentedStr());
            assertEquals("size", control.size.toLong(), rrb.size.toLong())
            assertEquals("item at " + idx, control[idx], rrb[idx])
            //            System.out.println("control:" + control);
            //            System.out.println("===test:" + is);
            for (k in 0..j) {
                assertEquals("Wrong item at " + k + ", but still correct size (" + rrb.size + ")\n" +
                             "control:\n" + control.toString() + "\n" +
                             "test:\n" + rrb.indentedStr(0),
                             control[k], rrb[k])
                //                System.out.println("control[" + k + "]:" + control.get(k) + " test[" + k + "]:" + is.get(k));
            }
        }
        //        assertEquals(indices.length, is.size());
        //        for (int j = 0; j < indices.length; j++){
        //            assertEquals(control.get(j), is.get(j));
        //        }
        //        System.out.println("After:" + is.indentedStr());
        return rrb
    }

    /**
     * Sequences of random inserts which previously failed.  So far, these are
     */
    @Test
    fun randIntoStrictPrevFail() {
        var rrb: RrbTree<Int> = RrbTree.empty()
        val control = ArrayList<Int>()
        for (i in 0 until SEVERAL) {
            rrb = rrb.append(i)
            control.add(i)
        }
        randomInsertTest2(rrb, deepCopy(control), intArrayOf(74, 45, 46, 50))
        //        System.out.println("================= HERE ================");
        randomInsertTest2(rrb, deepCopy(control), intArrayOf(25, 47, 19, 101, 21, 37, 7, 25, 23, 79, 21, 103, 44, 31, 32, 110, 58, 55, 7, 72, 73, 115))
    }

    @Test
    fun insertRandomIntoStrict() {
        var rrb: RrbTree<Int> = RrbTree.empty()
        val control = ArrayList<Int>()
        val rands = ArrayList<Int>()
        for (i in 0 until SEVERAL) {
            rrb = rrb.append(i)
            control.add(i)
        }
        try {
            for (j in 0 until SEVERAL) {
                val idx = rand.nextInt(rrb.size + 1)
                rands.add(idx)
                rrb = rrb.insert(idx, j)
                control.add(idx, j)
                assertEquals(control.size.toLong(), rrb.size.toLong())
                assertEquals(Integer.valueOf(j), rrb[idx])
            }
            assertEquals(control.size.toLong(), rrb.size.toLong())
            for (j in rrb.indices) {
                assertEquals(control[j], rrb[j])
            }
            rrb.debugValidate()
        } catch (e: Exception) {
            println("rands:" + rands) // print before blowing up...
            // OK, now we can continue throwing exception.
            throw e
        }

    }

    @Test
    fun basics() {
        val rrb = RrbTree.empty<Int>()
        assertEquals(0, rrb.size.toLong())

        val rrb1 = rrb.append(5)
        assertEquals(0, rrb.size.toLong())
        assertEquals(1, rrb1.size.toLong())
        assertEquals(Integer.valueOf(5), rrb1[0])

        val rrb2 = rrb1.append(4)
        assertEquals(0, rrb.size.toLong())
        assertEquals(1, rrb1.size.toLong())
        assertEquals(2, rrb2.size.toLong())
        assertEquals(Integer.valueOf(5), rrb1[0])
        assertEquals(Integer.valueOf(5), rrb2[0])
        assertEquals(Integer.valueOf(4), rrb2[1])

        val rrb3 = rrb2.append(3)
        assertEquals(0, rrb.size.toLong())
        assertEquals(1, rrb1.size.toLong())
        assertEquals(2, rrb2.size.toLong())
        assertEquals(3, rrb3.size.toLong())
        assertEquals(Integer.valueOf(5), rrb1[0])
        assertEquals(Integer.valueOf(5), rrb2[0])
        assertEquals(Integer.valueOf(5), rrb3[0])
        assertEquals(Integer.valueOf(4), rrb2[1])
        assertEquals(Integer.valueOf(4), rrb3[1])
        assertEquals(Integer.valueOf(3), rrb3[2])
        rrb.debugValidate()
        rrb1.debugValidate()
        rrb2.debugValidate()
        rrb3.debugValidate()
    }

    @Test
    fun testEmptyIterator() {
        assertFalse(RrbTree.empty<Any>().iterator().hasNext())
    }

    @Test
    fun testIterator1() {
        val it = RrbTree.empty<Int>().append(1).iterator()
        assertTrue(it.hasNext())
        assertEquals(1, it.next())
        assertFalse(it.hasNext())
    }

    @Test
    fun testIterator2() {
        val it = RrbTree.empty<Int>().append(1).append(2).iterator()
        assertTrue(it.hasNext())
        assertEquals(1, it.next())
        assertTrue(it.hasNext())
        assertEquals(2, it.next())
        assertFalse(it.hasNext())
    }

    @Test
    fun testIterator3() {
        val it = RrbTree.empty<Int>().append(1).append(2).append(3).iterator()
        assertTrue(it.hasNext())
        assertEquals(1, it.next())
        assertTrue(it.hasNext())
        assertEquals(2, it.next())
        assertTrue(it.hasNext())
        assertEquals(3, it.next())
        assertFalse(it.hasNext())
    }

    @Test
    fun testIterator4() {
        val it = RrbTree.empty<Int>().append(1).append(2).append(3).append(4)
                .iterator()
        assertTrue(it.hasNext())
        assertEquals(1, it.next())
        assertTrue(it.hasNext())
        assertEquals(2, it.next())
        assertTrue(it.hasNext())
        assertEquals(3, it.next())
        assertTrue(it.hasNext())
        assertEquals(4, it.next())
        assertFalse(it.hasNext())
    }

    @Test
    fun testIterator5() {
        val it = RrbTree.empty<Int>()
                .append(1).append(2).append(3).append(4).append(5).iterator()
        assertTrue(it.hasNext())
        assertEquals(1, it.next())
        assertTrue(it.hasNext())
        assertEquals(2, it.next())
        assertTrue(it.hasNext())
        assertEquals(3, it.next())
        assertTrue(it.hasNext())
        assertEquals(4, it.next())
        assertTrue(it.hasNext())
        assertEquals(5, it.next())
        assertFalse(it.hasNext())
    }

    @Test
    fun testIterator() {
        val control = ArrayList<Int>()
        var test: RrbTree<Int> = RrbTree.empty()

        val SOME = 2000
        for (i in 0 until SOME) {
            control.add(i)
            test = test.append(i)
        }
//        println("control:" + control);
//        println("test:" + test.indentedStr(0));
        compareIterators(control.iterator(), test.iterator())
        test.debugValidate()
    }

    @Test
    fun emptyListIterator() {
        TestUtilities.listIteratorTest(emptyList<Any>(), RrbTree.empty<Any>())
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun emptyEx00() {
        RrbTree.empty<Any>()[Integer.MIN_VALUE]
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun emptyEx01() {
        RrbTree.emptyMutable<Any>()[-1]
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun emptyEx02() {
        RrbTree.empty<Any>()[0]
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun emptyEx03() {
        RrbTree.emptyMutable<Any>()[1]
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun emptyEx04() {
        RrbTree.empty<Any>()[Integer.MAX_VALUE]
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun emptyEx10() {
        RrbTree.emptyMutable<Any>().replace(Integer.MIN_VALUE, null)
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun emptyEx11() {
        RrbTree.empty<Any>().replace(-1, null)
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun emptyEx12() {
        RrbTree.emptyMutable<Any>().replace(0, null)
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun emptyEx13() {
        RrbTree.empty<Any>().replace(1, null)
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun emptyEx14() {
        RrbTree.emptyMutable<Any>().replace(Integer.MAX_VALUE, null)
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun emptyEx20() {
        RrbTree.empty<Any>().split(Integer.MIN_VALUE)
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun emptyEx21() {
        RrbTree.emptyMutable<Any>().split(-1)
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun emptyEx22() {
        RrbTree.empty<Any>().split(0)
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun emptyEx23() {
        RrbTree.emptyMutable<Any>().split(1)
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun emptyEx24() {
        RrbTree.empty<Any>().split(Integer.MAX_VALUE)
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun emptyEx30() {
        RrbTree.emptyMutable<Any>().without(Integer.MIN_VALUE)
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun emptyEx31() {
        RrbTree.empty<Any>().without(-1)
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun emptyEx32() {
        RrbTree.emptyMutable<Any>().without(0)
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun emptyEx33() {
        RrbTree.empty<Any>().without(1)
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun emptyEx34() {
        RrbTree.emptyMutable<Any>().without(Integer.MAX_VALUE)
    }

    @Test
    @Throws(NoSuchAlgorithmException::class)
    fun addSeveralItems() {
        //        System.out.println("addSeveral start");
        val SEVERAL = 100 //0; //0; //SecureRandom.getInstanceStrong().nextInt(999999) + 33 ;
        var rrb: RrbTree<Int> = RrbTree.empty()
        for (j in 0 until SEVERAL) {
            rrb = rrb.append(j)
            assertEquals((j + 1).toLong(), rrb.size.toLong())
            assertEquals(Integer.valueOf(j), rrb[j])
            for (k in 0..j) {
                assertEquals(Integer.valueOf(k), rrb[k])
            }
            rrb.debugValidate()
        }
        assertEquals(SEVERAL.toLong(), rrb.size.toLong())
        for (j in 0 until SEVERAL) {
            assertEquals(Integer.valueOf(j), rrb[j])
        }
    }

    // TODO: Think about what exception to expect.
    @Test(expected = Exception::class)
    fun putEx() {
        RrbTree.empty<Any>().replace(1, "Hello")
    }

    @Test
    fun splitTestPrevFail() {
        var rrb: RrbTree<Int> = RrbTree.empty()
        val control = ArrayList<Int>()
        for (i in 0 until SEVERAL) {
            rrb = rrb.append(i)
            control.add(i)
        }
        testSplit(control, rrb, 29)
    }

    @Test
    fun strictSplitTest() {
        var rrb = RrbTree.empty<Int>()
        val ms = RrbTree.emptyMutable<Int>()
        val control = ArrayList<Int>()
        //        int splitIndex = rand.nextInt(is.size() + 1);
        for (i in 0 until TWO_LEVEL_SZ) {
            rrb = rrb.append(i)
            ms.append(i)
            control.add(i)
        }
        var splitIndex = 1
        while (splitIndex <= TWO_LEVEL_SZ) {
            //            int splitIndex = i; //rand.nextInt(is.size() + 1);
            //            System.out.println("splitIndex=" + splitIndex);
            //        System.out.println("empty=" + RrbTree.empty().indentedStr(6));
            try {
                testSplit(control, rrb, splitIndex)
            } catch (e: Exception) {
                println("Bad splitIndex (im): " + splitIndex) // print before blowing up...
                println("before split (im): " + rrb.indentedStr(13)) // print before blowing up...
                // OK, now we can continue throwing exception.
                throw e
            }

            try {
                testSplit(control, ms, splitIndex)
            } catch (e: Exception) {
                println("Bad splitIndex (mu): " + splitIndex) // print before blowing up...
                println("before split (mu): " + ms.indentedStr(13)) // print before blowing up...
                // OK, now we can continue throwing exception.
                throw e
            }

            splitIndex += (STRICT_NODE_LENGTH.toDouble() * STRICT_NODE_LENGTH.toDouble() * 0.333).toInt()
        }
    }

    @Test
    fun relaxedSplitTest() {
        var rrb = RrbTree.empty<Int>()
        val ms = RrbTree.emptyMutable<Int>()
        val control = ArrayList<Int>()
        val rands = ArrayList<Int>()
        var splitIndex = 0
        try {
            for (j in 0 until TWO_LEVEL_SZ) {
                val idx = rand.nextInt(rrb.size + 1)
                rands.add(idx)
                rrb = rrb.insert(idx, j)
                ms.insert(idx, j)
                control.add(idx, j)
            }
            assertEquals(TWO_LEVEL_SZ.toLong(), rrb.size.toLong())
            assertEquals(TWO_LEVEL_SZ.toLong(), ms.size.toLong())
            //            System.out.println("is:" + is.indentedStr(3));
            var j = 1
            while (j <= ONE_LEVEL_SZ) {
                splitIndex = j // So we have it when exception is thrown.
                testSplit(control, rrb, splitIndex)
                testSplit(control, ms, splitIndex)
                j += ONE_LEVEL_SZ / 10
            }
        } catch (e: Exception) {
            println("splitIndex:$splitIndex rands:$rands") // print before blowing up...
            // OK, now we can continue throwing exception.
            throw e
        }

    }

    @Test
    fun replaceTest() {
        var im = RrbTree.empty<String>()
        val mu = RrbTree.emptyMutable<String>()
        im = im.append("Hello").append("World")
        mu.append("Hello").append("World")
        assertArrayEquals(arrayOf("Hello", "World"),
                          im.toTypedArray())
        assertArrayEquals(arrayOf("Hello", "World"),
                          mu.toTypedArray())

        assertArrayEquals(arrayOf("Goodbye", "World"),
                          im.replace(0, "Goodbye").toTypedArray())
        im.debugValidate()
        assertArrayEquals(arrayOf("Goodbye", "World"),
                          mu.replace(0, "Goodbye").toTypedArray())
        mu.debugValidate()

        var im2 = RrbTree.empty<Int>()
        val mu2 = RrbTree.emptyMutable<Int>()
        val len = 999
        val control = arrayOfNulls<Int>(len)
        // Build test vector
        for (i in 0 until len) {
            im2 = im2.append(i)
            mu2.append(i)
            control[i] = i
            im2.debugValidate()
            mu2.debugValidate()
        }
        assertArrayEquals(control, im2.toTypedArray())
        assertArrayEquals(control, mu2.toTypedArray())

        var im3 = RrbTree.empty<Int>()
        var mu3 = RrbTree.emptyMutable<Int>()
        for (i in 0 until len) {
            im3 = im3.insert(0, len - 1 - i)
            mu3.insert(0, len - 1 - i)
        }
        assertArrayEquals(control, im3.toTypedArray())
        assertArrayEquals(control, mu3.toTypedArray())

        // Replace from end to start
        for (i in len - 1 downTo 0) {
            val replacement = len - i
            im2 = im2.replace(i, replacement)
            mu2.replace(i, replacement)
            im3 = im3.replace(i, replacement)
            mu3.replace(i, replacement)
            im2.debugValidate()
            mu2.debugValidate()
            im3.debugValidate()
            mu3.debugValidate()
            control[i] = replacement
        }
        assertArrayEquals(control, im2.toTypedArray())
        assertArrayEquals(control, mu2.toTypedArray())
        assertArrayEquals(control, im3.toTypedArray())
        assertArrayEquals(control, mu3.toTypedArray())

        // Replace in random order
        for (j in 0 until len) {
            val idx = rand.nextInt(len)
            val replacement = len - idx
            im2 = im2.replace(idx, replacement)
            mu2.replace(idx, replacement)
            im3 = im3.replace(idx, replacement)
            mu3 = mu3.replace(idx, replacement)
            im2.debugValidate()
            mu2.debugValidate()
            im3.debugValidate()
            mu3.debugValidate()
            control[idx] = replacement
        }
        assertArrayEquals(control, im2.toTypedArray())
        assertArrayEquals(control, mu2.toTypedArray())
        assertArrayEquals(control, im3.toTypedArray())
        assertArrayEquals(control, mu3.toTypedArray())
    }

    private fun testReplaceGuts(rrb: RrbTree<Int>) {
        var im = rrb
        for (i in im.indices) {
            im = im.replace(i, i)
        }
        im.debugValidate()
        for (i in im.indices) {
            assertEquals(Integer.valueOf(i), im[i])
        }
    }

    @Test
    fun testReplace2() {
        testReplaceGuts(generateRelaxed(TWO_LEVEL_SZ, RrbTree.empty()))
        testReplaceGuts(generateRelaxed(TWO_LEVEL_SZ, RrbTree.emptyMutable()))
    }

    @Test
    fun listIterator() {
        var im = RrbTree.empty<Int>()
        val mu = RrbTree.emptyMutable<Int>()
        val len = 99
        val test = arrayOfNulls<Int>(len)

        for (i in 0 until len) {
            val testVal = len - 1
            im = im.append(testVal)
            mu.append(testVal)
            assertEquals(Integer.valueOf(testVal), im[i])
            assertEquals(Integer.valueOf(testVal), mu[i])
            test[i] = testVal
        }
        assertArrayEquals(test, im.toTypedArray())
        assertArrayEquals(test, mu.toTypedArray())
        assertArrayEquals(test, serializeDeserialize<Array<Any>>(im.toTypedArray()))

        val tList = Arrays.asList<Int>(*test)
        TestUtilities.listIteratorTest(tList, im)
        TestUtilities.listIteratorTest(tList, mu)
    }

    @Test
    fun equalsAndHashCode() {
        val control = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20)
        val rrb1 = xform(control).fold(RrbTree.empty<Int>()
        ) { accum, item -> accum.append(item) }
        val rrb2 = xform(control).fold(RrbTree.emptyMutable<Int>()
        ) { accum, item -> accum.append(item) }

        val other = Arrays.asList(1, 3, 2, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20)

        equalsDistinctHashCode(control, rrb1, rrb2, other)

        val shorter = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19)
        equalsDistinctHashCode(control, rrb1, rrb2, shorter)

        val hasNull = Arrays.asList(1, 2, 3, 4, 5, 6, null, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20)

        val rrb3 = xform(hasNull).fold(RrbTree.empty<Int?>()) { accum, item -> accum.append(item) }
        val rrb4 = xform(hasNull).fold(RrbTree.emptyMutable<Int?>()) { accum, item -> accum.append(item) }

        equalsDistinctHashCode(rrb3, rrb4, hasNull, other)
    }

    @Test
    fun coverageJunky() {
        // I don't actually want toString() to return what it does now, but
        // Check any way:
        //        int[] someRands =
        //                { 0, 1, 2, 1, 3, 2, 6, 1, 7, 4, 2, 7, 1, 1, 5, 5, 15, 9, 8, 10, 16, 11, 1, 7, 20,
        //                  14, 11, 18, 23, 9, 29, 2, 3, 3, 19, 31, 15, 32, 28, 3, 38, 35, 10, 37, 43, 5, 3,
        //                  12, 34, 8, 5, 47, 18, 3, 9, 36, 48, 14, 26, 52, 21, 58, 12, 11, 39, 39, 43, 52,
        //                  7, 7, 42, 69, 9, 29, 53, 30, 60, 0, 41, 59, 4, 55, 25, 67, 9, 27, 51, 32, 48,
        //                  46, 65, 19, 7, 73, 6, 11, 84, 29, 51, 40, 94, 83, 102, 90, 23, 58, 66, 83, 64,
        //                  40, 46, 100, 34, 82, 43, 52, 82, 93, 101, 18, 13, 23, 100, 118, 105, 92, 14, 64,
        //                  41, 41, 60, 65, 125, 126, 10, 120, 23, 98, 132, 91, 73, 48, 114, 57, 77, 145,
        //                  96, 30, 5, 22, 41, 79, 59, 56, 41, 70, 58, 147, 136, 93, 83, 52, 93, 99, 61, 34,
        //                  21, 34, 70, 1, 24, 78, 59, 167, 5, 19, 22, 42, 111, 111, 27, 4, 45, 12, 169, 40,
        //                  39, 90, 20, 6, 176, 160, 25, 2, 48, 18, 143, 56, 127, 188, 51, 85, 53, 17, 19,
        //                  17, 86, 122, 143, 51, 88, 166, 48, 92, 168, 114, 211, 189, 210, 9, 146, 185,
        //                  105, 72, 197, 100, 139, 161, 145, 63, 24, 227, 17, 82, 103, 141, 87, 225, 81,
        //                  63, 209, 220, 59, 121, 55, 49, 60, 153, 236, 123, 38, 48, 187, 196, 198, 240,
        //                  42, 146, 178, 14, 216, 62, 243, 164, 123, 61, 176, 257, 126, 173, 198, 73, 3,
        //                  75, 142, 73, 52, 175, 258, 67, 111, 75, 72, 58, 166, 190, 270, 117, 34, 137,
        //                  93, 73, 145, 102, 229, 156, 154, 119, 134, 114, 151, 207, 208, 124, 29, 130,
        //                  297, 45, 66, 50, 62, 118, 261, 217, 282, 162, 185, 225, 78, 101, 111, 313, 203,
        //                  243, 227, 225, 250, 5, 231, 218, 248, 280, 114, 3, 253, 177, 294, 240, 181, 0,
        //                  227, 325, 33, 124, 129, 276, 27, 93, 197, 71, 276, 93, 291, 59, 55, 344, 339, 4,
        //                  322, 229, 350, 105, 101, 119, 342, 134, 218, 55, 258, 205, 327, 298, 309, 27,
        //                  345, 41, 268, 33, 305, 270, 327, 191, 69, 289, 45, 284, 240, 317, 123, 171};

        val rrb1 = randomInsertTest(intArrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8))
        var s1 = rrb1.indentedStr(0)
        assertTrue(s1.contains("RrbTree(size=9 "))
        assertTrue(s1.contains("        root="))

        assertEquals("MutRrbt(0,1,2,3,4,5,6,7,8)", rrb1.toString())

        val rrb2 = randomInsertTest(intArrayOf(0, 1, 2, 1, 3, 2, 6, 1, 7))
        s1 = rrb2.indentedStr(0)
        assertTrue(s1.contains("RrbTree(size=9 "))
        assertTrue(s1.contains("        root="))

        assertEquals("MutRrbt(0,7,3,5,1,4,2,8,6)", rrb2.toString())

        var im = RrbTree.empty<Int>()
        val mu = RrbTree.emptyMutable<Int>()
        for (j in 1 until SEVERAL) {
            im = im.append(j)
            mu.append(j)
        }
        assertTrue(im.indentedStr(7).startsWith("RrbTree(size=99 fsi=96 focus="))
        assertTrue(im.indentedStr(7).contains("               root=Strict"))

        assertTrue(mu.indentedStr(7).startsWith("RrbTree(size=99 fsi=96 focus="))
        assertTrue(mu.indentedStr(7).contains("               root=Strict"))
    }

    @Test
    fun joinImTest() {
        assertEquals(rrb(1, 2, 3, 4, 5, 6), rrb(1, 2, 3).join(rrb(4, 5, 6)))
        var r1 = rrb(1, 2, 3, 4, 5, 6, 7, 8, 9,
                     10, 11, 12, 13, 14, 15, 16, 17, 18, 19,
                     20, 21, 22, 23, 24, 25, 26, 27, 28, 29,
                     30, 31, 32, 33, 34, 35, 36, 37, 38, 39,
                     40, 41, 42, 43, 44, 45, 46, 47, 48, 49)
        var r2 = rrb(50, 51, 52, 53, 54, 55, 56, 57, 58, 59,
                     60, 61, 62, 63, 64, 65, 66, 67, 68, 69,
                     70, 71, 72, 73, 74, 75, 76, 77, 78, 79,
                     80, 81, 82, 83, 84, 85, 86, 87, 88, 89,
                     90, 91, 92, 93, 94, 95, 96, 97, 98, 99)

        var r3 = rrb(1, 2, 3, 4, 5, 6, 7, 8, 9,
                     10, 11, 12, 13, 14, 15, 16, 17, 18, 19,
                     20, 21, 22, 23, 24, 25, 26, 27, 28, 29,
                     30, 31, 32, 33, 34, 35, 36, 37, 38, 39,
                     40, 41, 42, 43, 44, 45, 46, 47, 48, 49,
                     50, 51, 52, 53, 54, 55, 56, 57, 58, 59,
                     60, 61, 62, 63, 64, 65, 66, 67, 68, 69,
                     70, 71, 72, 73, 74, 75, 76, 77, 78, 79,
                     80, 81, 82, 83, 84, 85, 86, 87, 88, 89,
                     90, 91, 92, 93, 94, 95, 96, 97, 98, 99)

        assertEquals(r3, r1.join(r2))

        val MAX_ITEMS = 2000
        val control = ArrayList<Int>()
        for (j in 1 until MAX_ITEMS) {
            control.add(j)
        }
        for (i in 1 until MAX_ITEMS) {
            r1 = RrbTree.empty()
            r2 = RrbTree.empty()
            for (j in 1 until i) {
                r1 = r1.append(j)
            }
            for (j in i until MAX_ITEMS) {
                r2 = r2.append(j)
            }


            //            System.out.println("\n==============================================");
            //            System.out.println("join index: " + i);
            //            System.out.println("r1: " + r1.indentedStr(4));
            //            System.out.println("r2: " + r2.indentedStr(4));
            r3 = r1.join(r2)
            //            System.out.println("r3: " + r3.indentedStr(13));
            assertEquals(control, r3)
            r3.debugValidate()
        }

        //        r1 = rrb(1, 2, 3, 4, 5, 6);
        //        r2 = rrb(7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26);
        //
        //        System.out.println("r1: " + r1.indentedStr(4));
        //        System.out.println("r2: " + r2.indentedStr(4));
        //        System.out.println("r1.join(r2): " + r1.join(r2).indentedStr(13));
        //
        //        assertEquals(rrb(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26),
        //                     r1.join(r2));

    }

    @Test
    fun joinMutableTest() {
        assertEquals(mut(1, 2, 3, 4, 5, 6), mut(1, 2, 3).join(mut(4, 5, 6)))
        var r1 = mut(1, 2, 3, 4, 5, 6, 7, 8, 9,
                     10, 11, 12, 13, 14, 15, 16, 17, 18, 19,
                     20, 21, 22, 23, 24, 25, 26, 27, 28, 29,
                     30, 31, 32, 33, 34, 35, 36, 37, 38, 39,
                     40, 41, 42, 43, 44, 45, 46, 47, 48, 49)
        var r2 = mut(50, 51, 52, 53, 54, 55, 56, 57, 58, 59,
                     60, 61, 62, 63, 64, 65, 66, 67, 68, 69,
                     70, 71, 72, 73, 74, 75, 76, 77, 78, 79,
                     80, 81, 82, 83, 84, 85, 86, 87, 88, 89,
                     90, 91, 92, 93, 94, 95, 96, 97, 98, 99)

        var r3 = mut(1, 2, 3, 4, 5, 6, 7, 8, 9,
                     10, 11, 12, 13, 14, 15, 16, 17, 18, 19,
                     20, 21, 22, 23, 24, 25, 26, 27, 28, 29,
                     30, 31, 32, 33, 34, 35, 36, 37, 38, 39,
                     40, 41, 42, 43, 44, 45, 46, 47, 48, 49,
                     50, 51, 52, 53, 54, 55, 56, 57, 58, 59,
                     60, 61, 62, 63, 64, 65, 66, 67, 68, 69,
                     70, 71, 72, 73, 74, 75, 76, 77, 78, 79,
                     80, 81, 82, 83, 84, 85, 86, 87, 88, 89,
                     90, 91, 92, 93, 94, 95, 96, 97, 98, 99)

        assertEquals(r3, r1.join(r2))

        val MAX_ITEMS = 2000
        val control = ArrayList<Int>()
        for (j in 1 until MAX_ITEMS) {
            control.add(j)
        }
        for (i in 1 until MAX_ITEMS) {
            r1 = RrbTree.emptyMutable()
            r2 = RrbTree.emptyMutable()
            for (j in 1 until i) {
                r1 = r1.append(j)
            }
            for (j in i until MAX_ITEMS) {
                r2 = r2.append(j)
            }
            r3 = r1.join(r2)
            assertEquals(control, r3)
            r3.debugValidate()
        }
    }

    @Test
    fun testBiggerJoin() {
        var rrb = RrbTree.empty<Int>()
        val ms = RrbTree.emptyMutable<Int>()
        for (i in 0 until TWO_LEVEL_SZ) {
            rrb = rrb.append(i)
            ms.append(i)
        }
        assertEquals(rrb, serializeDeserialize(rrb))
        var splitIndex = 1
        while (splitIndex <= TWO_LEVEL_SZ) {
            val isSplit = rrb.split(splitIndex)
            assertEquals(rrb, isSplit.first.join(isSplit.second))

            val msSplit = ms.split(splitIndex)
            assertEquals(ms, msSplit.first.join(msSplit.second))
            splitIndex += (STRICT_NODE_LENGTH.toDouble() * STRICT_NODE_LENGTH.toDouble() * 0.333).toInt()
        }
    }


    @Test
    fun testWithout() {
        assertEquals(rrb(1, 2, 3, 5, 6), rrb(1, 2, 3, 4, 5, 6).without(3))
        assertEquals(mut(1, 2, 3, 5, 6), mut(1, 2, 3, 4, 5, 6).without(3))

        //        for (int m = 1; m < 1000; m++) {
        //            System.out.println("m: " + m);

        val MAX_ITEMS = 76 //m; //100; // TODO: Make this 76 to see issue
        var im = RrbTree.empty<Int>()
        val mu = RrbTree.emptyMutable<Int>()
        for (j in 1 until MAX_ITEMS) {
            im = im.append(j)
            mu.append(j)
        }
        for (i in 68 until MAX_ITEMS - 1) { // TODO: Start i = 68 to see issue.
            val control = ArrayList<Int>()
            for (j in 1 until MAX_ITEMS) {
                control.add(j)
            }

            //            System.out.println("i: " + i);
            control.removeAt(i)
            val im2 = im.without(i)
            val mu2 = mu.without(i)
            assertEquals(control, im2)
            assertEquals(control, mu2)
            im2.debugValidate()
            mu2.debugValidate()
            assertEquals(im2, mu2)
        }
        //        }
    }

    companion object {

        // Ensures that we've got at least height=1 + a full focus.
        private val ONE_LEVEL_SZ = STRICT_NODE_LENGTH * (STRICT_NODE_LENGTH + 2)
        // Ensures that we've got at least height=2 + a full focus.
        private val TWO_LEVEL_SZ =
                STRICT_NODE_LENGTH * STRICT_NODE_LENGTH * (STRICT_NODE_LENGTH + 2)

        private val rand = java.security.SecureRandom()

        private fun <T : RrbTree<Int>> generateRelaxed(size: Int, r: T): T {
            var rrb:T = r
            val rand = java.security.SecureRandom()
            for (j in 0 until size) {
                val idx = rand.nextInt(rrb.size + 1)
                @Suppress("UNCHECKED_CAST")
                rrb = rrb.insert(idx, j) as T
            }
            return rrb
        }

        private fun buildInOrderTest(r: RrbTree<Int>, iterations: Int): RrbTree<Int> {
            var rrb = r
            val control = ArrayList<Int>()
            for (j in 0 until iterations) {
                rrb = rrb.append(j)
                control.add(j)
                assertEquals((j + 1).toLong(), rrb.size.toLong())
                assertEquals(Integer.valueOf(j), rrb[j])
            }

            assertEquals(control, rrb)
            rrb.debugValidate()
            assertEquals(iterations.toLong(), rrb.size.toLong())
            for (j in 0 until iterations) {
                assertEquals(Integer.valueOf(j), rrb[j])
            }
            return rrb
        }

        //    private static final Integer[] INTS =
        //            new Integer[] { Integer.valueOf(0), Integer.valueOf(1), Integer.valueOf(2),
        //                            Integer.valueOf(3), Integer.valueOf(4), Integer.valueOf(5),
        //                            Integer.valueOf(6), Integer.valueOf(7), Integer.valueOf(8),
        //                            Integer.valueOf(9), Integer.valueOf(10), Integer.valueOf(11),
        //                            Integer.valueOf(12), Integer.valueOf(13), Integer.valueOf(14),
        //                            Integer.valueOf(15) };

        //    @Test public void testAppendSpeed() {
        //        MutRrbt<Integer> is = RrbTree.emptyMutable();
        //        for (int j = 0; j < 1000000000; j++) {
        //            is.append(INTS[j & 0xf]);
        //        }
        //    }
        ////        System.out.println("timer1: " + MutRrbt.timer1);
        ////        System.out.println("timer2: " + MutRrbt.timer2);
        ////        System.out.println("timer3: " + MutRrbt.timer3);
        ////        System.out.println("timer4: " + MutRrbt.timer4);
        ////        System.out.println("timer5: " + MutRrbt.timer5);

        private fun buildReverseOrderTest(r: RrbTree<Int>, iterations: Int): RrbTree<Int> {
            var rrb = r
            val control = ArrayList<Int>()
            for (j in 0 until iterations) {
                rrb = rrb.insert(0, j)
                control.add(0, j)
                assertEquals((j + 1).toLong(), rrb.size.toLong())
                assertEquals(Integer.valueOf(j), rrb[0])
                //            System.out.println(" ==" + is);
                for (k in 0..j) {
                    assertEquals("Checking index: " + k + " for size=" + control.size, control[k], rrb[k])
                }
                rrb.debugValidate()
                //            System.out.println(is);
            }
            assertEquals(iterations.toLong(), rrb.size.toLong())
            for (j in 0 until iterations) {
                assertEquals(Integer.valueOf(iterations - j - 1), rrb[j])
            }
            //        System.out.println(is.indentedStr(0));
            return rrb
        }

        private fun <E> deepCopy(`in`: ArrayList<E>): ArrayList<E> {
            val out = ArrayList<E>()
            out.addAll(`in`)
            return out
        }

        private fun isPrime(@Suppress("UNUSED_PARAMETER") num: Int): Boolean {
            return false
            //        if (num < 2) return false;
            //        if (num == 2) return true;
            //        if (num % 2 == 0) return false;
            //        for (int i = 3; i * i <= num; i += 2)
            //            if (num % i == 0) return false;
            //        return true;
        }

        private fun <T> testSplit(control: ArrayList<T>, test: RrbTree<T>, splitIndex: Int) {
            if (splitIndex < 1 && splitIndex > control.size) {
                throw IllegalArgumentException("Constraint violation failed: 1 <= splitIndex <= size")
            }
            //        System.out.println("test=" + test.indentedStr(5));
            val split = test.split(splitIndex)
            //        System.out.println("leftSplit=" + split.first.indentedStr(10));
            //        System.out.println("rightSplit=" + split.second.indentedStr(11));
            val leftControl = control.subList(0, splitIndex)
            val rightControl = control.subList(splitIndex, control.size)
            val leftSplit = split.first
            val rightSplit = split.second
            if (isPrime(splitIndex)) {
                println("original=\n" + test.indentedStr(0))
                println("splitIndex=" + splitIndex)
                println("left=\n" + leftSplit.indentedStr(0))
                println("right=\n" + rightSplit.indentedStr(0))
            }
            assertEquals("leftControl:$leftControl\n doesn't equal leftSplit:$leftSplit",
                         leftControl, leftSplit)
            assertEquals("rightControl:$rightControl\n doesn't equal rightSplit:$rightSplit",
                         rightControl, rightSplit)
            leftSplit.debugValidate()
            rightSplit.debugValidate()
            //        System.out.println("==================================");
        }

        @SafeVarargs
        private fun <T> rrb(vararg ts: T): RrbTree<T> {
            var ret: RrbTree<T> = RrbTree.empty()
            for (t in ts) {
                ret = ret.append(t)
            }
            return ret
        }

        @SafeVarargs
        private fun <T> mut(vararg ts: T): RrbTree<T> {
            var ret: RrbTree<T> = RrbTree.emptyMutable()
            for (t in ts) {
                ret = ret.append(t)
            }
            return ret
        }
    }
}