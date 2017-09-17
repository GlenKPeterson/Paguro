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
package org.organicdesign.fp.collections;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.junit.Test;
import org.organicdesign.fp.TestUtilities;
import org.organicdesign.fp.collections.RrbTree.ImRrbt;
import org.organicdesign.fp.collections.RrbTree.MutRrbt;
import org.organicdesign.fp.tuple.Tuple2;

import static org.junit.Assert.*;
import static org.organicdesign.fp.StaticImports.xform;
import static org.organicdesign.fp.TestUtilities.compareIterators;
import static org.organicdesign.fp.TestUtilities.serializeDeserialize;
import static org.organicdesign.fp.collections.RrbTree.STRICT_NODE_LENGTH;
import static org.organicdesign.testUtils.EqualsContract.equalsDistinctHashCode;

public class RrbTreeTest {

    // Ensures that we've got at least height=1 + a full focus.
    private static final int ONE_LEVEL_SZ = STRICT_NODE_LENGTH * (STRICT_NODE_LENGTH + 2);
    // Ensures that we've got at least height=2 + a full focus.
    private static final int TWO_LEVEL_SZ =
            STRICT_NODE_LENGTH * STRICT_NODE_LENGTH * (STRICT_NODE_LENGTH + 2);

    private static Random rand = new java.security.SecureRandom();

    @SuppressWarnings("unchecked")
    private static <T extends RrbTree<Integer>> T generateRelaxed(int size, T rs) {
        Random rand = new java.security.SecureRandom();
        for (int j = 0; j < size; j++) {
            int idx = rand.nextInt(rs.size() + 1);
            rs = (T) rs.insert(idx, j);
        }
        return rs;
    }

    private static RrbTree<Integer> buildInOrderTest(RrbTree<Integer> is, int iterations) {
        ArrayList<Integer> control = new ArrayList<>();
        for (int j = 0; j < iterations; j++) {
            is = is.append(j);
            control.add(j);
            assertEquals(j + 1, is.size());
            assertEquals(Integer.valueOf(j), is.get(j));
        }
        //noinspection AssertEqualsBetweenInconvertibleTypes
        assertEquals(control, is);
        is.debugValidate();
        assertEquals(iterations, is.size());
        for (int j = 0; j < iterations; j++){
            assertEquals(Integer.valueOf(j), is.get(j));
        }
        return is;
    }

    @Test public void buildStrict() {
        buildInOrderTest(RrbTree.empty(), 10000);
        buildInOrderTest(RrbTree.emptyMutable(), 100000);
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

    private static RrbTree<Integer> buildReverseOrderTest(RrbTree<Integer> is, int iterations) {
        ArrayList<Integer> control = new ArrayList<>();
        for (int j = 0; j < iterations; j++){
            is = is.insert(0, j);
            control.add(0, j);
            assertEquals(j + 1, is.size());
            assertEquals(Integer.valueOf(j), is.get(0));
//            System.out.println(" ==" + is);
            for (int k = 0; k <= j; k++) {
                assertEquals("Checking index: " + k + " for size=" + control.size(), control.get(k), is.get(k));
            }
            is.debugValidate();
//            System.out.println(is);
        }
        assertEquals(iterations, is.size());
        for (int j = 0; j < iterations; j++){
            assertEquals(Integer.valueOf(iterations - j - 1), is.get(j));
        }
//        System.out.println(is.indentedStr(0));
        return is;
    }

    @Test public void insertAtZero() {
        buildReverseOrderTest(RrbTree.empty(),        1000);
        buildReverseOrderTest(RrbTree.emptyMutable(), 1000);
    }

    private RrbTree<Integer> randomInsertTest(int[] indices) {
        randomInsertTest2(RrbTree.empty(), new ArrayList<>(), indices);
        return randomInsertTest2(RrbTree.emptyMutable(), new ArrayList<>(), indices);
    }

    /**
     Sequences of random inserts which previously failed.  So far, these are
     */
    @Test public void insertRandPrevFail() {
        randomInsertTest(new int[] {0, 0, 2, 2, 2, 3, 5, 1});
        randomInsertTest(new int[] {0, 1, 2, 1, 0, 5, 2});
        randomInsertTest(new int[] {0, 0, 1, 2, 3, 0, 1, 5, 8, 2});
        randomInsertTest(new int[] {0, 1, 2, 2, 3, 2, 0, 6, 5, 6, 9, 9, 5, 6, 14, 2, 12, 8, 15});
        randomInsertTest(new int[] {0, 0, 0, 3, 4, 4, 5, 3, 0, 7, 5, 1, 11, 9, 0, 2, 7, 11, 12, 7,
                                    6, 10, 2, 15, 24, 11, 18, 24, 20, 29, 17, 26, 3, 26, 20, 18, 11,
                                    17, 14, 3, 0, 40, 7, 41, 6, 40, 5});
        randomInsertTest(new int[] {0, 0, 1, 2, 0, 1, 3, 4, 3, 1});
        randomInsertTest(new int[] {0, 1, 0, 2, 0, 3, 1, 0, 0, 1, 7});
    }

    @Test
    public void insertRandom() {
        final int SEVERAL = 1000; //0; //0; //SecureRandom.getInstanceStrong().nextInt(999999) + 33 ;
        RrbTree<Integer> is = RrbTree.empty();
        ArrayList<Integer> control = new ArrayList<>();
        ArrayList<Integer> rands = new ArrayList<>();
        try {
            for (int j = 0; j < SEVERAL; j++) {
                int idx = rand.nextInt(is.size() + 1);
                rands.add(idx);
                is = is.insert(idx, j);
                control.add(idx, j);
                assertEquals(j + 1, is.size());
                assertEquals(Integer.valueOf(j), is.get(idx));
//            System.out.println("control:" + control);
//            System.out.println("===test:" + is);
//            for (int k = 0; k <= j; k++) {
////                System.out.println("control[" + k + "]:" + control.get(k) + " test[" + k + "]:" + is.get(k));
//                assertEquals("Checking index: " + k + " for size=" + control.size(), control.get(k), is.get(k));
//            }
//            System.out.println(is);
            }
            assertEquals(SEVERAL, is.size());
            for (int j = 0; j < SEVERAL; j++) {
                assertEquals(control.get(j), is.get(j));
            }
        } catch (Exception e) {
            System.out.println("rands:" + rands); // print before blowing up...
            // OK, now we can continue throwing exception.
            throw e;
        }
    }

    private RrbTree<Integer> randomInsertTest2(RrbTree<Integer> is, List<Integer> control, int[] indices) {
        assertEquals("inputSize (if this blows up, this test is being used incorrectly)", control.size(), is.size());
//        System.out.println("Before:" + is.indentedStr());
        for (int j = 0; j < indices.length; j++){
            int idx = indices[j];
//            System.out.println("About to insert at=" + idx + " elem=" + j + " into=" + is.indentedStr());
            is = is.insert(idx, j);
            control.add(idx, j);
            is.debugValidate();
//            System.out.println("control:" + control);
//            System.out.println("===test:" + is.indentedStr());
            assertEquals("size", control.size(), is.size());
            assertEquals("item at " + idx, control.get(idx), is.get(idx));
//            System.out.println("control:" + control);
//            System.out.println("===test:" + is);
            for (int k = 0; k <= j; k++) {
                assertEquals("Wrong item at " + k + ", but still correct size (" + is.size() + ")\n" +
                        "control:\n" + control.toString() + "\n" +
                        "test:\n" + is.indentedStr(0),
                             control.get(k), is.get(k));
//                System.out.println("control[" + k + "]:" + control.get(k) + " test[" + k + "]:" + is.get(k));
            }
        }
//        assertEquals(indices.length, is.size());
//        for (int j = 0; j < indices.length; j++){
//            assertEquals(control.get(j), is.get(j));
//        }
//        System.out.println("After:" + is.indentedStr());
        return is;
    }

    final int SEVERAL = 100; //0; //0; //SecureRandom.getInstanceStrong().nextInt(999999) + 33 ;

    private static <E> ArrayList<E> deepCopy(ArrayList<E> in) {
        ArrayList<E> out = new ArrayList<E>();
        out.addAll(in);
        return out;
    }

    /**
     Sequences of random inserts which previously failed.  So far, these are
     */
    @Test public void randIntoStrictPrevFail() {
        RrbTree<Integer> is = RrbTree.empty();
        ArrayList<Integer> control = new ArrayList<>();
        for (int i = 0; i < SEVERAL; i++) {
            is = is.append(i);
            control.add(i);
        }
        randomInsertTest2(is, deepCopy(control), new int[] {74, 45, 46, 50});
//        System.out.println("================= HERE ================");
        randomInsertTest2(is, deepCopy(control), new int[] {25, 47, 19, 101, 21, 37, 7, 25, 23, 79, 21, 103, 44, 31, 32, 110,
                                                            58, 55, 7, 72, 73, 115});
    }

    @Test
    public void insertRandomIntoStrict() {
        RrbTree<Integer> is = RrbTree.empty();
        ArrayList<Integer> control = new ArrayList<>();
        ArrayList<Integer> rands = new ArrayList<>();
        for (int i = 0; i < SEVERAL; i++) {
            is = is.append(i);
            control.add(i);
        }
        try {
            for (int j = 0; j < SEVERAL; j++) {
                int idx = rand.nextInt(is.size() + 1);
                rands.add(idx);
                is = is.insert(idx, j);
                control.add(idx, j);
                assertEquals(control.size(), is.size());
                assertEquals(Integer.valueOf(j), is.get(idx));
            }
            assertEquals(control.size(), is.size());
            for (int j = 0; j < is.size(); j++) {
                assertEquals(control.get(j), is.get(j));
            }
            is.debugValidate();
        } catch (Exception e) {
            System.out.println("rands:" + rands); // print before blowing up...
            // OK, now we can continue throwing exception.
            throw e;
        }
    }

    @Test
    public void basics() {
        RrbTree<Integer> rrb = RrbTree.empty();
        assertEquals(0, rrb.size());

        RrbTree<Integer> rrb1 = rrb.append(5);
        assertEquals(0, rrb.size());
        assertEquals(1, rrb1.size());
        assertEquals(Integer.valueOf(5), rrb1.get(0));

        RrbTree<Integer> rrb2 = rrb1.append(4);
        assertEquals(0, rrb.size());
        assertEquals(1, rrb1.size());
        assertEquals(2, rrb2.size());
        assertEquals(Integer.valueOf(5), rrb1.get(0));
        assertEquals(Integer.valueOf(5), rrb2.get(0));
        assertEquals(Integer.valueOf(4), rrb2.get(1));

        RrbTree<Integer> rrb3 = rrb2.append(3);
        assertEquals(0, rrb.size());
        assertEquals(1, rrb1.size());
        assertEquals(2, rrb2.size());
        assertEquals(3, rrb3.size());
        assertEquals(Integer.valueOf(5), rrb1.get(0));
        assertEquals(Integer.valueOf(5), rrb2.get(0));
        assertEquals(Integer.valueOf(5), rrb3.get(0));
        assertEquals(Integer.valueOf(4), rrb2.get(1));
        assertEquals(Integer.valueOf(4), rrb3.get(1));
        assertEquals(Integer.valueOf(3), rrb3.get(2));
        rrb.debugValidate();
        rrb1.debugValidate();
        rrb2.debugValidate();
        rrb3.debugValidate();
    }

    @Test public void testEmptyIterator() {
        assertFalse(RrbTree.empty().iterator().hasNext());
    }

    @Test public void testIterator1() {
        UnmodIterator<Integer> it = RrbTree.<Integer>empty().append(1).iterator();
        assertTrue(it.hasNext());
        assertEquals((Integer) 1, it.next());
        assertFalse(it.hasNext());
    }

    @Test public void testIterator2() {
        UnmodIterator<Integer> it = RrbTree.<Integer>empty().append(1).append(2).iterator();
        assertTrue(it.hasNext());
        assertEquals((Integer) 1, it.next());
        assertTrue(it.hasNext());
        assertEquals((Integer) 2, it.next());
        assertFalse(it.hasNext());
    }

    @Test public void testIterator3() {
        UnmodIterator<Integer> it = RrbTree.<Integer>empty().append(1).append(2).append(3).iterator();
        assertTrue(it.hasNext());
        assertEquals((Integer) 1, it.next());
        assertTrue(it.hasNext());
        assertEquals((Integer) 2, it.next());
        assertTrue(it.hasNext());
        assertEquals((Integer) 3, it.next());
        assertFalse(it.hasNext());
    }

    @Test public void testIterator4() {
        UnmodIterator<Integer> it = RrbTree.<Integer>empty().append(1).append(2).append(3).append(4)
                                                            .iterator();
        assertTrue(it.hasNext());
        assertEquals((Integer) 1, it.next());
        assertTrue(it.hasNext());
        assertEquals((Integer) 2, it.next());
        assertTrue(it.hasNext());
        assertEquals((Integer) 3, it.next());
        assertTrue(it.hasNext());
        assertEquals((Integer) 4, it.next());
        assertFalse(it.hasNext());
    }

    @Test public void testIterator5() {
        UnmodIterator<Integer> it = RrbTree.<Integer>empty()
                .append(1).append(2).append(3).append(4).append(5).iterator();
        assertTrue(it.hasNext());
        assertEquals((Integer) 1, it.next());
        assertTrue(it.hasNext());
        assertEquals((Integer) 2, it.next());
        assertTrue(it.hasNext());
        assertEquals((Integer) 3, it.next());
        assertTrue(it.hasNext());
        assertEquals((Integer) 4, it.next());
        assertTrue(it.hasNext());
        assertEquals((Integer) 5, it.next());
        assertFalse(it.hasNext());
    }

    @Test public void testIterator() {
        List<Integer> control = new ArrayList<>();
        RrbTree<Integer> test = RrbTree.empty();

        int SOME = 2000;
        for (int i = 0; i < SOME; i++) {
            control.add(i);
            test = test.append(i);
        }
//        System.out.println("control:" + control);
//        System.out.println("test:" + test.indentedStr(0));
        compareIterators(control.iterator(), test.iterator());
        test.debugValidate();
    }

    @Test public void emptyListIterator() {
        TestUtilities.listIteratorTest(Collections.emptyList(), RrbTree.empty());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx00() { RrbTree.empty().get(Integer.MIN_VALUE); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx01() { RrbTree.emptyMutable().get(-1); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx02() { RrbTree.empty().get(0); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx03() { RrbTree.emptyMutable().get(1); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx04() { RrbTree.empty().get(Integer.MAX_VALUE); }

    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx10() { RrbTree.emptyMutable().replace(Integer.MIN_VALUE, null); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx11() { RrbTree.empty().replace(-1, null); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx12() { RrbTree.emptyMutable().replace(0, null); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx13() { RrbTree.empty().replace(1, null); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx14() { RrbTree.emptyMutable().replace(Integer.MAX_VALUE, null); }

    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx20() { RrbTree.empty().split(Integer.MIN_VALUE); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx21() { RrbTree.emptyMutable().split(-1); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx22() { RrbTree.empty().split(0); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx23() { RrbTree.emptyMutable().split(1); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx24() { RrbTree.empty().split(Integer.MAX_VALUE); }

    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx30() { RrbTree.emptyMutable().without(Integer.MIN_VALUE); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx31() { RrbTree.empty().without(-1); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx32() { RrbTree.emptyMutable().without(0); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx33() { RrbTree.empty().without(1); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx34() { RrbTree.emptyMutable().without(Integer.MAX_VALUE); }

    @Test public void addSeveralItems() throws NoSuchAlgorithmException {
//        System.out.println("addSeveral start");
        final int SEVERAL = 100; //0; //0; //SecureRandom.getInstanceStrong().nextInt(999999) + 33 ;
        RrbTree<Integer> is = RrbTree.empty();
        for (int j = 0; j < SEVERAL; j++){
            is = is.append(j);
            assertEquals(j + 1, is.size());
            assertEquals(Integer.valueOf(j), is.get(j));
            for (int k = 0; k <= j; k++) {
                assertEquals(Integer.valueOf(k), is.get(k));
            }
            is.debugValidate();
        }
        assertEquals(SEVERAL, is.size());
        for (int j = 0; j < SEVERAL; j++){
            assertEquals(Integer.valueOf(j), is.get(j));
        }
    }

    // TODO: Think about what exception to expect.
    @Test(expected = Exception.class)
    public void putEx() { RrbTree.empty().replace(1, "Hello"); }

    private static boolean isPrime(int num) {
        return false;
//        if (num < 2) return false;
//        if (num == 2) return true;
//        if (num % 2 == 0) return false;
//        for (int i = 3; i * i <= num; i += 2)
//            if (num % i == 0) return false;
//        return true;
    }

    private static <T> void testSplit(ArrayList<T> control, RrbTree<T> test, int splitIndex) {
        if ( (splitIndex < 1) && (splitIndex > control.size()) ) {
            throw new IllegalArgumentException("Constraint violation failed: 1 <= splitIndex <= size");
        }
//        System.out.println("test=" + test.indentedStr(5));
        Tuple2<? extends RrbTree<T>,? extends RrbTree<T>> split = test.split(splitIndex);
//        System.out.println("leftSplit=" + split._1().indentedStr(10));
//        System.out.println("rightSplit=" + split._2().indentedStr(11));
        List<T> leftControl = control.subList(0, splitIndex);
        List<T> rightControl = control.subList(splitIndex, control.size());
        RrbTree<T> leftSplit = split._1();
        RrbTree<T> rightSplit = split._2();
        if (isPrime(splitIndex)) {
            System.out.println("original=\n" + test.indentedStr(0));
            System.out.println("splitIndex=" + splitIndex);
            System.out.println("left=\n" + leftSplit.indentedStr(0));
            System.out.println("right=\n" + rightSplit.indentedStr(0));
        }
        assertEquals("leftControl:" + leftControl + "\n doesn't equal leftSplit:" + leftSplit,
                     leftControl, leftSplit);
        assertEquals("rightControl:" + rightControl + "\n doesn't equal rightSplit:" + rightSplit,
                     rightControl, rightSplit);
        leftSplit.debugValidate();
        rightSplit.debugValidate();
//        System.out.println("==================================");
    }

    @Test public void splitTestPrevFail() {
        RrbTree<Integer> is = RrbTree.empty();
        ArrayList<Integer> control = new ArrayList<>();
        for (int i = 0; i < SEVERAL; i++) {
            is = is.append(i);
            control.add(i);
        }
        testSplit(control, is, 29);
    }

    @Test public void strictSplitTest() {
        ImRrbt<Integer> is = RrbTree.empty();
        MutRrbt<Integer> ms = RrbTree.emptyMutable();
        ArrayList<Integer> control = new ArrayList<>();
//        int splitIndex = rand.nextInt(is.size() + 1);
        for (int i = 0; i < TWO_LEVEL_SZ; i++) {
            is = is.append(i);
            ms.append(i);
            control.add(i);
        }
        for (int splitIndex = 1; splitIndex <= TWO_LEVEL_SZ;
             splitIndex += (STRICT_NODE_LENGTH * STRICT_NODE_LENGTH * 0.333)) {
//            int splitIndex = i; //rand.nextInt(is.size() + 1);
//            System.out.println("splitIndex=" + splitIndex);
//        System.out.println("empty=" + RrbTree.empty().indentedStr(6));
            try {
                testSplit(control, is, splitIndex);
            } catch (Exception e) {
                System.out.println("Bad splitIndex (im): " + splitIndex); // print before blowing up...
                System.out.println("before split (im): " + is.indentedStr(13)); // print before blowing up...
                // OK, now we can continue throwing exception.
                throw e;
            }
            try {
                testSplit(control, ms, splitIndex);
            } catch (Exception e) {
                System.out.println("Bad splitIndex (mu): " + splitIndex); // print before blowing up...
                System.out.println("before split (mu): " + ms.indentedStr(13)); // print before blowing up...
                // OK, now we can continue throwing exception.
                throw e;
            }
        }
    }

    @Test public void relaxedSplitTest() {
        ImRrbt<Integer> is = RrbTree.empty();
        MutRrbt<Integer> ms = RrbTree.emptyMutable();
        ArrayList<Integer> control = new ArrayList<>();
        ArrayList<Integer> rands = new ArrayList<>();
        int splitIndex = 0;
        try {
            for (int j = 0; j < TWO_LEVEL_SZ; j++) {
                int idx = rand.nextInt(is.size() + 1);
                rands.add(idx);
                is = is.insert(idx, j);
                ms.insert(idx, j);
                control.add(idx, j);
            }
            assertEquals(TWO_LEVEL_SZ, is.size());
            assertEquals(TWO_LEVEL_SZ, ms.size());
//            System.out.println("is:" + is.indentedStr(3));
            for (int j = 1; j <= ONE_LEVEL_SZ; j+= ONE_LEVEL_SZ / 10) {
                splitIndex = j; // So we have it when exception is thrown.
                testSplit(control, is, splitIndex);
                testSplit(control, ms, splitIndex);
            }
        } catch (Exception e) {
            System.out.println("splitIndex:" + splitIndex + " rands:" + rands); // print before blowing up...
            // OK, now we can continue throwing exception.
            throw e;
        }
    }

    @Test public void replaceTest() {
        ImRrbt<String> im = RrbTree.empty();
        MutRrbt<String> mu = RrbTree.emptyMutable();
        im = im.append("Hello").append("World");
        mu.append("Hello").append("World");
        assertArrayEquals(new String[] { "Hello", "World" },
                          im.toArray());
        assertArrayEquals(new String[] { "Hello", "World" },
                          mu.toArray());

        assertArrayEquals(new String[]{"Goodbye", "World"},
                          im.replace(0, "Goodbye").toArray());
        im.debugValidate();
        assertArrayEquals(new String[]{"Goodbye", "World"},
                          mu.replace(0, "Goodbye").toArray());
        mu.debugValidate();

        ImRrbt<Integer> im2 = RrbTree.empty();
        MutRrbt<Integer> mu2 = RrbTree.emptyMutable();
        int len = 999;
        Integer[] control = new Integer[len];
        // Build test vector
        for (int i = 0; i < len; i++) {
            im2 = im2.append(i);
            mu2.append(i);
            control[i] = i;
            im2.debugValidate();
            mu2.debugValidate();
        }
        assertArrayEquals(control, im2.toArray());
        assertArrayEquals(control, mu2.toArray());

        ImRrbt<Integer> im3 = RrbTree.empty();
        MutRrbt<Integer> mu3 = RrbTree.emptyMutable();
        for (int i = 0; i < len; i++) {
            im3 = im3.insert(0, len - 1 - i);
            mu3.insert(0, len - 1 - i);
        }
        assertArrayEquals(control, im3.toArray());
        assertArrayEquals(control, mu3.toArray());

        // Replace from end to start
        for (int i = len - 1; i >= 0; i--) {
            int replacement = len - i;
            im2 = im2.replace(i, replacement);
            mu2.replace(i, replacement);
            im3 = im3.replace(i, replacement);
            mu3.replace(i, replacement);
            im2.debugValidate();
            mu2.debugValidate();
            im3.debugValidate();
            mu3.debugValidate();
            control[i] = replacement;
        }
        assertArrayEquals(control, im2.toArray());
        assertArrayEquals(control, mu2.toArray());
        assertArrayEquals(control, im3.toArray());
        assertArrayEquals(control, mu3.toArray());

        // Replace in random order
        for (int j = 0; j < len; j++) {
            int idx = rand.nextInt(len);
            int replacement = len - idx;
            im2 = im2.replace(idx, replacement);
            mu2.replace(idx, replacement);
            im3 = im3.replace(idx, replacement);
            mu3 = mu3.replace(idx, replacement);
            im2.debugValidate();
            mu2.debugValidate();
            im3.debugValidate();
            mu3.debugValidate();
            control[idx] = replacement;
        }
        assertArrayEquals(control, im2.toArray());
        assertArrayEquals(control, mu2.toArray());
        assertArrayEquals(control, im3.toArray());
        assertArrayEquals(control, mu3.toArray());
    }

    private void testReplaceGuts(RrbTree<Integer> im) {
        for (int i = 0; i < im.size(); i++) {
            im = im.replace(i, i);
        }
        im.debugValidate();
        for (int i = 0; i < im.size(); i++) {
            assertEquals(Integer.valueOf(i), im.get(i));
        }
    }

    @Test public void testReplace2() {
        testReplaceGuts(generateRelaxed(TWO_LEVEL_SZ, RrbTree.empty()));
        testReplaceGuts(generateRelaxed(TWO_LEVEL_SZ, RrbTree.emptyMutable()));
    }

    @Test public void listIterator() {
        ImRrbt<Integer> im = RrbTree.empty();
        MutRrbt<Integer> mu = RrbTree.emptyMutable();
        int len = 99;
        Integer[] test = new Integer[len];

        for (int i = 0; i < len; i++) {
            int testVal = len - 1;
            im = im.append(testVal);
            mu.append(testVal);
            assertEquals(Integer.valueOf(testVal), im.get(i));
            assertEquals(Integer.valueOf(testVal), mu.get(i));
            test[i] = testVal;
        }
        assertArrayEquals(test, im.toArray());
        assertArrayEquals(test, mu.toArray());
        assertArrayEquals(test, serializeDeserialize(im.toArray()));

        List<Integer> tList = Arrays.asList(test);
        TestUtilities.listIteratorTest(tList, im);
        TestUtilities.listIteratorTest(tList, mu);
    }

    @Test public void equalsAndHashCode() {
        List<Integer> control = Arrays.asList(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20);
        ImRrbt<Integer> rrb1 =
                xform(control).fold(RrbTree.<Integer>empty(),
                                    (accum, item) -> accum.append(item));
        MutRrbt<Integer> rrb2 =
                xform(control).fold(RrbTree.<Integer>emptyMutable(),
                                    (accum, item) -> accum.append(item));

        List<Integer> other = Arrays.asList(1,3,2,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20);

        equalsDistinctHashCode(control, rrb1, rrb2, other);

        List<Integer> shorter = Arrays.asList(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19);
        equalsDistinctHashCode(control, rrb1, rrb2, shorter);

        List<Integer> hasNull = Arrays.asList(1,2,3,4,5,6,null,8,9,10,11,12,13,14,15,16,17,18,19,20);

        ImRrbt<Integer> rrb3 =
                xform(hasNull).fold(RrbTree.<Integer>empty(),
                                    (accum, item) -> accum.append(item));
        MutRrbt<Integer> rrb4 =
                xform(hasNull).fold(RrbTree.<Integer>emptyMutable(),
                                    (accum, item) -> accum.append(item));

        equalsDistinctHashCode(rrb3, rrb4, hasNull, other);
    }

    @Test public void coverageJunky() {
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

        RrbTree<Integer> rrb1 = randomInsertTest(new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8 });
        String s1 = rrb1.indentedStr(0);
        assertTrue(s1.contains("RrbTree(size=9 "));
        assertTrue(s1.contains("        root="));

        assertEquals("MutRrbt(0,1,2,3,4,5,6,7,8)", rrb1.toString());

        RrbTree<Integer> rrb2 = randomInsertTest(new int[] {0, 1, 2, 1, 3, 2, 6, 1, 7});
        s1 = rrb2.indentedStr(0);
        assertTrue(s1.contains("RrbTree(size=9 "));
        assertTrue(s1.contains("        root="));

        assertEquals("MutRrbt(0,7,3,5,1,4,2,8,6)", rrb2.toString());

        ImRrbt<Integer> im = RrbTree.empty();
        MutRrbt<Integer> mu = RrbTree.emptyMutable();
        for (int j = 1; j < SEVERAL; j++) {
            im = im.append(j);
            mu.append(j);
        }
        assertTrue(im.indentedStr(7).startsWith("RrbTree(size=99 fsi=96 focus="));
        assertTrue(im.indentedStr(7).contains("               root=Strict"));

        assertTrue(mu.indentedStr(7).startsWith("RrbTree(size=99 fsi=96 focus="));
        assertTrue(mu.indentedStr(7).contains("               root=Strict"));
    }

    @SafeVarargs
    private static <T> RrbTree<T> rrb(T... ts) {
        RrbTree<T> ret = RrbTree.empty();
        for (T t : ts) {
            ret = ret.append(t);
        }
        return ret;
    }

    @Test public void joinImTest() {
        assertEquals(rrb(1,2,3,4,5,6), rrb(1,2,3).join(rrb(4,5,6)));
        RrbTree<Integer> r1 = rrb(1, 2, 3, 4, 5, 6, 7, 8, 9,
                                  10, 11, 12, 13, 14, 15, 16, 17, 18, 19,
                                  20, 21, 22, 23, 24, 25, 26, 27, 28, 29,
                                  30, 31, 32, 33, 34, 35, 36, 37, 38, 39,
                                  40, 41, 42, 43, 44, 45, 46, 47, 48, 49);
        RrbTree<Integer> r2 = rrb(50, 51, 52, 53, 54, 55, 56, 57, 58, 59,
                                  60, 61, 62, 63, 64, 65, 66, 67, 68, 69,
                                  70, 71, 72, 73, 74, 75, 76, 77, 78, 79,
                                  80, 81, 82, 83, 84, 85, 86, 87, 88, 89,
                                  90, 91, 92, 93, 94, 95, 96, 97, 98, 99);

        RrbTree<Integer> r3 = rrb(1, 2, 3, 4, 5, 6, 7, 8, 9,
                                  10, 11, 12, 13, 14, 15, 16, 17, 18, 19,
                                  20, 21, 22, 23, 24, 25, 26, 27, 28, 29,
                                  30, 31, 32, 33, 34, 35, 36, 37, 38, 39,
                                  40, 41, 42, 43, 44, 45, 46, 47, 48, 49,
                                  50, 51, 52, 53, 54, 55, 56, 57, 58, 59,
                                  60, 61, 62, 63, 64, 65, 66, 67, 68, 69,
                                  70, 71, 72, 73, 74, 75, 76, 77, 78, 79,
                                  80, 81, 82, 83, 84, 85, 86, 87, 88, 89,
                                  90, 91, 92, 93, 94, 95, 96, 97, 98, 99);

        assertEquals(r3, r1.join(r2));

        int MAX_ITEMS = 2000;
        List<Integer> control = new ArrayList<>();
        for (int j = 1; j < MAX_ITEMS; j++) {
            control.add(j);
        }
        for (int i = 1; i < MAX_ITEMS; i++) {
            r1 = RrbTree.empty();
            r2 = RrbTree.empty();
            for (int j = 1; j < i; j++) {
                r1 = r1.append(j);
            }
            for (int j = i; j < MAX_ITEMS; j++) {
                r2 = r2.append(j);
            }


//            System.out.println("\n==============================================");
//            System.out.println("join index: " + i);
//            System.out.println("r1: " + r1.indentedStr(4));
//            System.out.println("r2: " + r2.indentedStr(4));
            r3 = r1.join(r2);
//            System.out.println("r3: " + r3.indentedStr(13));
            assertEquals(control, r3);
            r3.debugValidate();
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

    @SafeVarargs
    private static <T> RrbTree<T> mut(T... ts) {
        RrbTree<T> ret = RrbTree.emptyMutable();
        for (T t : ts) {
            ret = ret.append(t);
        }
        return ret;
    }

    @Test public void joinMutableTest() {
        assertEquals(mut(1,2,3,4,5,6), mut(1,2,3).join(mut(4,5,6)));
        RrbTree<Integer> r1 = mut(1, 2, 3, 4, 5, 6, 7, 8, 9,
                                  10, 11, 12, 13, 14, 15, 16, 17, 18, 19,
                                  20, 21, 22, 23, 24, 25, 26, 27, 28, 29,
                                  30, 31, 32, 33, 34, 35, 36, 37, 38, 39,
                                  40, 41, 42, 43, 44, 45, 46, 47, 48, 49);
        RrbTree<Integer> r2 = mut(50, 51, 52, 53, 54, 55, 56, 57, 58, 59,
                                  60, 61, 62, 63, 64, 65, 66, 67, 68, 69,
                                  70, 71, 72, 73, 74, 75, 76, 77, 78, 79,
                                  80, 81, 82, 83, 84, 85, 86, 87, 88, 89,
                                  90, 91, 92, 93, 94, 95, 96, 97, 98, 99);

        RrbTree<Integer> r3 = mut(1, 2, 3, 4, 5, 6, 7, 8, 9,
                                  10, 11, 12, 13, 14, 15, 16, 17, 18, 19,
                                  20, 21, 22, 23, 24, 25, 26, 27, 28, 29,
                                  30, 31, 32, 33, 34, 35, 36, 37, 38, 39,
                                  40, 41, 42, 43, 44, 45, 46, 47, 48, 49,
                                  50, 51, 52, 53, 54, 55, 56, 57, 58, 59,
                                  60, 61, 62, 63, 64, 65, 66, 67, 68, 69,
                                  70, 71, 72, 73, 74, 75, 76, 77, 78, 79,
                                  80, 81, 82, 83, 84, 85, 86, 87, 88, 89,
                                  90, 91, 92, 93, 94, 95, 96, 97, 98, 99);

        assertEquals(r3, r1.join(r2));

        int MAX_ITEMS = 2000;
        List<Integer> control = new ArrayList<>();
        for (int j = 1; j < MAX_ITEMS; j++) {
            control.add(j);
        }
        for (int i = 1; i < MAX_ITEMS; i++) {
            r1 = RrbTree.emptyMutable();
            r2 = RrbTree.emptyMutable();
            for (int j = 1; j < i; j++) {
                r1 = r1.append(j);
            }
            for (int j = i; j < MAX_ITEMS; j++) {
                r2 = r2.append(j);
            }
            r3 = r1.join(r2);
            assertEquals(control, r3);
            r3.debugValidate();
        }
    }

    @Test public void testBiggerJoin() {
        ImRrbt<Integer> is = RrbTree.empty();
        MutRrbt<Integer> ms = RrbTree.emptyMutable();
        for (int i = 0; i < TWO_LEVEL_SZ; i++) {
            is = is.append(i);
            ms.append(i);
        }
        assertEquals(is, serializeDeserialize(is));
        for (int splitIndex = 1; splitIndex <= TWO_LEVEL_SZ;
             splitIndex += STRICT_NODE_LENGTH * STRICT_NODE_LENGTH * 0.333) {
            Tuple2<ImRrbt<Integer>,ImRrbt<Integer>> isSplit = is.split(splitIndex);
            assertEquals(is, isSplit._1().join(isSplit._2()));

            Tuple2<RrbTree.MutRrbt<Integer>,MutRrbt<Integer>> msSplit = ms.split(splitIndex);
            assertEquals(ms, msSplit._1().join(msSplit._2()));
        }
    }


    @Test public void testWithout() {
        assertEquals(rrb(1,2,3,5,6), rrb(1,2,3,4,5,6).without(3));
        assertEquals(mut(1,2,3,5,6), mut(1,2,3,4,5,6).without(3));

//        for (int m = 1; m < 1000; m++) {
//            System.out.println("m: " + m);

        int MAX_ITEMS = 76; //m; //100; // TODO: Make this 76 to see issue
        ImRrbt<Integer> im = RrbTree.empty();
        MutRrbt<Integer> mu = RrbTree.emptyMutable();
        for (int j = 1; j < MAX_ITEMS; j++) {
            im = im.append(j);
            mu.append(j);
        }
        for (int i = 68; i < MAX_ITEMS - 1; i++) { // TODO: Start i = 68 to see issue.
            List<Integer> control = new ArrayList<>();
            for (int j = 1; j < MAX_ITEMS; j++) {
                control.add(j);
            }

//            System.out.println("i: " + i);
            control.remove(i);
            RrbTree im2 = im.without(i);
            RrbTree mu2 = mu.without(i);
            assertEquals(control, im2);
            assertEquals(control, mu2);
            im2.debugValidate();
            mu2.debugValidate();
            assertEquals(im2, mu2);
        }
//        }
    }
}