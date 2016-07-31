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
package org.organicdesign.fp.experimental;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.junit.Test;
import org.organicdesign.fp.collections.ImList;
import org.organicdesign.fp.collections.UnmodListTest;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.organicdesign.fp.StaticImports.xform;
import static org.organicdesign.testUtils.EqualsContract.equalsDistinctHashCode;

public class RrbTree1Test {

    private Random rand = new java.security.SecureRandom();

    @Test
    public void insertAtZero() {
        final int SEVERAL = 100; //0; //0; //SecureRandom.getInstanceStrong().nextInt(999999) + 33 ;
        RrbTree1<Integer> is = RrbTree1.empty();
        ArrayList<Integer> control = new ArrayList<>();
        for (int j = 0; j < SEVERAL; j++){
            is = is.insert(0, j);
            control.add(0, j);
            assertEquals(j + 1, is.size());
            assertEquals(Integer.valueOf(j), is.get(0));
//            System.out.println(" ==" + is);
            for (int k = 0; k <= j; k++) {
                assertEquals("Checking index: " + k + " for size=" + control.size(), control.get(k), is.get(k));
            }
//            System.out.println(is);
        }
        assertEquals(SEVERAL, is.size());
        for (int j = 0; j < SEVERAL; j++){
            assertEquals(Integer.valueOf(SEVERAL - j - 1), is.get(j));
        }
    }

    private RrbTree1<Integer> randomInsertTest(int[] indices) {
        RrbTree1<Integer> is = RrbTree1.empty();
        ArrayList<Integer> control = new ArrayList<>();
        for (int j = 0; j < indices.length; j++){
            int idx = indices[j];
            is = is.insert(idx, j);
            control.add(idx, j);
            assertEquals("size", j + 1, is.size());
            assertEquals("item at " + idx, Integer.valueOf(j), is.get(idx));
//            System.out.println("control:" + control);
//            System.out.println("===test:" + is);
            for (int k = 0; k <= j; k++) {
                assertEquals("item at " + k + " still correct at size " + is.size(),
                             control.get(k), is.get(k));
//                System.out.println("control[" + k + "]:" + control.get(k) + " test[" + k + "]:" + is.get(k));
            }
        }
//        assertEquals(indices.length, is.size());
//        for (int j = 0; j < indices.length; j++){
//            assertEquals(control.get(j), is.get(j));
//        }
        return is;
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
        RrbTree1<Integer> is = RrbTree1.empty();
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

    @Test
    public void insertRandomIntoStrict() {
        final int SEVERAL = 100; //0; //0; //SecureRandom.getInstanceStrong().nextInt(999999) + 33 ;
        RrbTree1<Integer> is = RrbTree1.empty();
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
        } catch (Exception e) {
            System.out.println("rands:" + rands); // print before blowing up...
            // OK, now we can continue throwing exception.
            throw e;
        }
    }

    @Test
    public void basics() {
        RrbTree1<Integer> rrb = RrbTree1.empty();
        assertEquals(0, rrb.size());

        RrbTree1<Integer> rrb1 = rrb.append(5);
        assertEquals(0, rrb.size());
        assertEquals(1, rrb1.size());
        assertEquals(Integer.valueOf(5), rrb1.get(0));

        RrbTree1<Integer> rrb2 = rrb1.append(4);
        assertEquals(0, rrb.size());
        assertEquals(1, rrb1.size());
        assertEquals(2, rrb2.size());
        assertEquals(Integer.valueOf(5), rrb1.get(0));
        assertEquals(Integer.valueOf(5), rrb2.get(0));
        assertEquals(Integer.valueOf(4), rrb2.get(1));

        RrbTree1<Integer> rrb3 = rrb2.append(3);
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
    }

    @Test public void emptyListIterator() {
        UnmodListTest.listIteratorTest(Collections.emptyList(), RrbTree1.empty());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx00() { RrbTree1.empty().get(Integer.MIN_VALUE); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx01() { RrbTree1.empty().get(-1); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx02() { RrbTree1.empty().get(0); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx03() { RrbTree1.empty().get(1); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx04() { RrbTree1.empty().get(Integer.MAX_VALUE); }

    @Test public void addSeveralItems() throws NoSuchAlgorithmException {
//        System.out.println("addSeveral start");
        final int SEVERAL = 100; //0; //0; //SecureRandom.getInstanceStrong().nextInt(999999) + 33 ;
        RrbTree1<Integer> is = RrbTree1.empty();
        for (int j = 0; j < SEVERAL; j++){
            is = is.append(j);
            assertEquals(j + 1, is.size());
            assertEquals(Integer.valueOf(j), is.get(j));
            for (int k = 0; k <= j; k++) {
                assertEquals(Integer.valueOf(k), is.get(k));
            }
        }
        assertEquals(SEVERAL, is.size());
        for (int j = 0; j < SEVERAL; j++){
            assertEquals(Integer.valueOf(j), is.get(j));
        }
    }

    // TODO: Think about what exception to expect.
    @Test(expected = Exception.class)
    public void putEx() { RrbTree1.empty().replace(1, "Hello"); }

    @Test public void replace() {
        RrbTree1<String> pv = RrbTree1.empty();
        pv = pv.append("Hello").append("World");
        assertArrayEquals(new String[] { "Hello", "World" },
                          pv.toArray());

        assertArrayEquals(new String[]{"Goodbye", "World"},
                          pv.replace(0, "Goodbye").toArray());

        ImList<Integer> pv2 = RrbTree1.empty();
        int len = 999;
        Integer[] control = new Integer[len];
        // Build test vector
        for (int i = 0; i < len; i++) {
            pv2 = pv2.append(i);
            control[i] = i;
        }
        assertArrayEquals(control, pv2.toArray());

        RrbTree1<Integer> rrb3 = RrbTree1.empty();
        for (int i = 0; i < len; i++) {
            rrb3 = rrb3.insert(0, len - 1 - i);
        }
        assertArrayEquals(control, rrb3.toArray());

        // Replace from end to start
        for (int i = len - 1; i >= 0; i--) {
            int replacement = len - i;
            pv2 = pv2.replace(i, replacement);
            rrb3 = rrb3.replace(i, replacement);
            control[i] = replacement;
        }
        assertArrayEquals(control, pv2.toArray());
        assertArrayEquals(control, rrb3.toArray());

        // Replace in random order
        for (int j = 0; j < len; j++) {
            int idx = rand.nextInt(len);
            int replacement = len - idx;
            pv2 = pv2.replace(idx, replacement);
            rrb3 = rrb3.replace(idx, replacement);
            control[idx] = replacement;
        }
        assertArrayEquals(control, pv2.toArray());
        assertArrayEquals(control, rrb3.toArray());
    }

    @Test public void listIterator() {
        RrbTree1<Integer> pv2 = RrbTree1.empty();
        int len = 99;
        Integer[] test = new Integer[len];

        for (int i = 0; i < len; i++) {
            int testVal = len - 1;
            pv2 = pv2.append(testVal);
            assertEquals(Integer.valueOf(testVal), pv2.get(i));
            test[i] = testVal;
        }
        assertArrayEquals(test, pv2.toArray());

        List<Integer> tList = Arrays.asList(test);
        UnmodListTest.listIteratorTest(tList, pv2);
    }

    @Test public void equalsAndHashCode() {
        List<Integer> control = Arrays.asList(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20);
        RrbTree1<Integer> rrb1 =
                xform(control).foldLeft(RrbTree1.<Integer>empty(),
                                        (accum, item) -> accum.append(item));
        RrbTree1<Integer> rrb2 =
                xform(control).foldLeft(RrbTree1.<Integer>empty(),
                                        (accum, item) -> accum.append(item));

        List<Integer> other = Arrays.asList(1,3,2,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20);

        equalsDistinctHashCode(control, rrb1, rrb2, other);

        List<Integer> shorter = Arrays.asList(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19);
        equalsDistinctHashCode(control, rrb1, rrb2, shorter);

        List<Integer> hasNull = Arrays.asList(1,2,3,4,5,6,null,8,9,10,11,12,13,14,15,16,17,18,19,20);
        equalsDistinctHashCode(control, rrb1, rrb2, hasNull);
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

        RrbTree1<Integer> rrb1 = randomInsertTest(new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8 });
        assertEquals("RrbTree(fsi=8 focus=[8]\n" +
                     "        root=Strict2[[0, 1, 2, 3], [4, 5, 6, 7]])", rrb1.debugString());

        assertEquals("RrbTree(0,1,2,3,4,...)", rrb1.toString());

        RrbTree1<Integer> rrb2 = randomInsertTest(new int[] { 0, 1, 2, 1, 3, 2, 6, 1, 7});
        assertEquals("RrbTree(fsi=7 focus=[8]\n" +
                     "        root=Relaxed(endIndicies=[4, 8] nodes=[[0, 7, 3, 5], [1, 4, 2, 6]]))",
                     rrb2.debugString());

        assertEquals("RrbTree(0,7,3,5,1,...)", rrb2.toString());
    }
}