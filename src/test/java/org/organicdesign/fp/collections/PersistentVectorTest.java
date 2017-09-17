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

package org.organicdesign.fp.collections;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.organicdesign.fp.TestUtilities;

import static org.junit.Assert.*;
import static org.organicdesign.fp.StaticImports.vec;
import static org.organicdesign.fp.TestUtilities.compareIterators;
import static org.organicdesign.fp.TestUtilities.serializeDeserialize;
import static org.organicdesign.fp.collections.PersistentVector.emptyMutable;
import static org.organicdesign.testUtils.EqualsContract.equalsDistinctHashCode;

@RunWith(JUnit4.class)
public class PersistentVectorTest {
    @Test
    public void basics() {
        Integer[] threeIntArray = new Integer[]{1, 2, 3};
        ImList<Integer> list = vec(1, 2, 3);
        Integer[] resultArray = list.toArray(new Integer[3]);
        assertArrayEquals(threeIntArray, resultArray);

        // Calling this with a too-small array used to result in a class-cast exception as [LObject was cast to
        // [LInteger.  So calling with a smaller input array is an important test.
        resultArray = list.toArray(new Integer[2]);
        assertArrayEquals(threeIntArray, resultArray);

        String[] resultArray2 = vec("hi", "bye", "ok").toArray(new String[3]);
        assertArrayEquals(new String[] {"hi", "bye", "ok"}, resultArray2);
    }

    @Test
    public void empty() {
        ImList<Integer> empty1 = PersistentVector.empty();
        ImList<Integer> empty2 = PersistentVector.ofIter(Collections.emptyList());
        ImList<Integer> empty3 = PersistentVector.ofIter(new ArrayList<>());
        ImList<Integer> empty4 = vec();

        equalsDistinctHashCode(empty1, empty2, empty3,
                               vec(1));

        equalsDistinctHashCode(empty2, empty3, empty4,
                               vec((Integer) null));
    }

    @Test public void emptyListIterator() {
        TestUtilities.listIteratorTest(Collections.emptyList(), PersistentVector.empty());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx00() { PersistentVector.empty().get(Integer.MIN_VALUE); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx01() { PersistentVector.empty().get(-1); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx02() { PersistentVector.empty().get(0); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx03() { PersistentVector.empty().get(1); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx04() { PersistentVector.empty().get(Integer.MAX_VALUE); }

    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx10() { PersistentVector.ofIter(Collections.emptyList()).get(Integer.MIN_VALUE); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx11() { PersistentVector.ofIter(Collections.emptyList()).get(-1); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx12() { PersistentVector.ofIter(Collections.emptyList()).get(0); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx13() { PersistentVector.ofIter(Collections.emptyList()).get(1); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx14() { PersistentVector.ofIter(Collections.emptyList()).get(Integer.MAX_VALUE); }

    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx20() { PersistentVector.ofIter(new ArrayList<>()).get(Integer.MIN_VALUE); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx21() { PersistentVector.ofIter(new ArrayList<>()).get(-1); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx22() { PersistentVector.ofIter(new ArrayList<>()).get(0); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx23() { PersistentVector.ofIter(new ArrayList<>()).get(1); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx24() { PersistentVector.ofIter(new ArrayList<>()).get(Integer.MAX_VALUE); }

    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx30() { vec().get(Integer.MIN_VALUE); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx31() { vec().get(-1); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx32() { vec().get(0); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx33() { vec().get(1); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx34() { vec().get(Integer.MAX_VALUE); }

    @Test
    public void oneInt() {
        ImList<Integer> one1 = vec(1);
        List<Integer> oneList = new ArrayList<>();
        oneList.add(1);
        ImList<Integer> one2 = PersistentVector.ofIter(oneList);
        ImList<Integer> one3 = PersistentVector.ofIter(Collections.unmodifiableList(oneList));

        equalsDistinctHashCode(one1, one2, one3,
                               vec(-1));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void oneEx00() { vec(1).get(Integer.MIN_VALUE); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void oneEx01() { vec(1).get(-1); }
    @Test
    public void oneIsOne() {
        assertEquals(Integer.valueOf(1), vec(1).get(0));
    }
    @Test(expected = IndexOutOfBoundsException.class)
    public void oneEx03() { vec(1).get(1); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void oneEx04() { vec(1).get(Integer.MAX_VALUE); }

    @Test(expected = IndexOutOfBoundsException.class)
    public void oneEx10() {
        List<Integer> oneList = new ArrayList<>();
        oneList.add(1);
        PersistentVector.ofIter(oneList).get(Integer.MIN_VALUE); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void oneEx11() {
        List<Integer> oneList = new ArrayList<>();
        oneList.add(1);
        PersistentVector.ofIter(oneList).get(-1); }
    @Test
    public void oneIsOne2() {
        List<Integer> oneList = new ArrayList<>();
        oneList.add(1);
        assertEquals(Integer.valueOf(1), PersistentVector.ofIter(oneList).get(0)); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void oneEx13() {
        List<Integer> oneList = new ArrayList<>();
        oneList.add(1);
        PersistentVector.ofIter(oneList).get(1); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void oneEx14() {
        List<Integer> oneList = new ArrayList<>();
        oneList.add(1);
        PersistentVector.ofIter(oneList).get(Integer.MAX_VALUE); }

    @Test(expected = IndexOutOfBoundsException.class)
    public void oneEx20() {
        List<Integer> oneList = new ArrayList<>();
        oneList.add(1);
        PersistentVector.ofIter(Collections.unmodifiableList(oneList)).get(Integer.MIN_VALUE); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void oneEx21() {
        List<Integer> oneList = new ArrayList<>();
        oneList.add(1);
        PersistentVector.ofIter(Collections.unmodifiableList(oneList)).get(-1); }
    @Test
    public void oneIsOne3() {
        List<Integer> oneList = new ArrayList<>();
        oneList.add(1);
        assertEquals(Integer.valueOf(1), PersistentVector.ofIter(Collections.unmodifiableList(oneList)).get(0)); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void oneEx23() {
        List<Integer> oneList = new ArrayList<>();
        oneList.add(1);
        PersistentVector.ofIter(Collections.unmodifiableList(oneList)).get(1); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void oneEx24() {
        List<Integer> oneList = new ArrayList<>();
        oneList.add(1);
        PersistentVector.ofIter(Collections.unmodifiableList(oneList)).get(Integer.MAX_VALUE); }

    @Test public void addSeveralItems() throws NoSuchAlgorithmException {
//        System.out.println("addSeveral start");
        final int SEVERAL = 100; //SecureRandom.getInstanceStrong().nextInt(999999) + 33 ;
        PersistentVector<Integer> is = PersistentVector.empty();
        for (int j = 0; j < SEVERAL; j++){
            is = is.append(j);
        }
        assertEquals(SEVERAL, is.size());
        for (int j = 0; j < SEVERAL; j++){
            assertEquals(Integer.valueOf(j), is.get(j));
        }
    }

    @Test public void serializationTest() throws Exception {
        ImList<Integer> empty1 = serializeDeserialize(PersistentVector.empty());
        ImList<Integer> empty2 = PersistentVector.ofIter(Collections.emptyList());
        ImList<Integer> empty3 = PersistentVector.ofIter(new ArrayList<>());
        ImList<Integer> empty4 = vec();

        equalsDistinctHashCode(empty1, empty2, empty3,
                               vec(1));

        equalsDistinctHashCode(empty2, empty3, empty4,
                               vec((Integer) null));

//        System.out.println("addSeveral start");
        final int SEVERAL = 100; //SecureRandom.getInstanceStrong().nextInt(999999) + 33 ;
        PersistentVector<Integer> is = PersistentVector.empty();
        for (int j = 0; j < SEVERAL; j++){
            is = is.append(j);
        }
        PersistentVector<Integer> pv = serializeDeserialize(is);
        assertEquals(SEVERAL, pv.size());
        for (int j = 0; j < SEVERAL; j++){
            assertEquals(Integer.valueOf(j), pv.get(j));
        }

    }

    @Test
    public void transienceTest() {
        ImList<Integer> list = vec(1,2,3,4,5,6,7,8,9,0,11,12,13,14,15,16,17,18,19,20,
                                   21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40);
        List<Integer> l2 = Arrays.asList(1,2,3,4,5,6,7,8,9,0,11,12,13,14,15,16,17,18,19,20,
                                         21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40);
        List<Integer> different = Arrays.asList(1,2,3,4,5,6,7,8,9,0,11,12,13,14,15,16,17,18,19,20,
                                                 21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,41);

        equalsDistinctHashCode(list, l2, Collections.unmodifiableList(l2), different);
    }

    // Time ImVectorImplementation vs. java.util.ArrayList to prove that performance does not degrade
    // if changes are made.
    @Ignore
    @Test public void speedTest() throws NoSuchAlgorithmException, InterruptedException {
        final int maxItems = 1000000;

        System.out.println("Speed tests take time.  The more accurate, the more time.\n" +
                                   "This may fail occasionally, then work when re-run, which is OK.\n" +
                                   "Better that, than set the limit too high and miss a performance drop.");

        // These are worst-case timings, indexed by number of items inserted in the test.
        final Map<Integer,Double> benchmarkRatios;
        {
            Map<Integer,Double> mm = new HashMap<>();
            mm.put(1, 1.4);
            mm.put(10, 2.7);
            mm.put(100, 6.5);
            mm.put(1000, 9.0);
            mm.put(10000, 18.0);
            mm.put(100000, 13.9);
            mm.put(1000000, 7.8);
            benchmarkRatios = Collections.unmodifiableMap(mm);
        }

        // Remember the results of each insertion test to average them later.
        List<Double> ratios = new ArrayList<>();

        // Run tests for increasingly more inserts each time (powers of 10 should be fair since underlying
        // implementations use powers of 2).
        for (int numItems = 1; numItems <= maxItems; numItems *= 10) {

            // Run the speed tests this many times (for better accuracy) testing ArrayList and ImVectorImpl alternately.
            int testRepetitions = (numItems < 1000) ? 10000 :
                                  (numItems < 10000) ? 1000 :
                                  (numItems < 100000) ? 100 : 10;

            long[] testTimes = new long[testRepetitions];
            long[] benchTimes = new long[testRepetitions];

            Thread.sleep(0); // GC and other processes, this is your chance.

            for (int z = 0; z < testRepetitions; z++) {
                Thread.sleep(0); // GC and other processes, this is your chance.
                long startTime = System.nanoTime();
                List<Integer> benchmark = new ArrayList<>();
                for (int i = 0; i < numItems; i++) {
                    benchmark.add(i);
                }
                assertEquals(numItems, benchmark.size());
                assertEquals(Integer.valueOf(numItems / 2), benchmark.get(numItems / 2));
                assertEquals(Integer.valueOf(numItems - 1), benchmark.get(numItems - 1));
                benchTimes[z] = System.nanoTime() - startTime;

                Thread.sleep(0); // GC and other processes, this is your chance.
                startTime = System.nanoTime();
                PersistentVector<Integer> test = PersistentVector.empty();
                for (int i = 0; i < numItems; i++) {
                    test = test.append(i);
                }
                assertEquals(numItems, test.size());
                assertEquals(Integer.valueOf(numItems / 2), test.get(numItems / 2));
                assertEquals(Integer.valueOf(numItems - 1), test.get(numItems - 1));
                testTimes[z] = System.nanoTime() - startTime;
            }

            // We want the median time.  That discards all the unlucky (worst) and lucky (best) times.
            // That makes it a fairer measurement for this than the mean time.
            Arrays.sort(testTimes);
            Arrays.sort(benchTimes);
            long testTime = testTimes[testTimes.length / 2];
            long benchTime = benchTimes[benchTimes.length / 2];

            // Ratio of mean times of the tested collection vs. the benchmark.
            double ratio = ((double) testTime) / ((double) benchTime);
            System.out.println("Iterations: " + numItems + " test: " + testTime + " benchmark: " + benchTime +
                                       " test/benchmark: " + ratio);

            // Verify that the median time is within established bounds for this test
            assertTrue(ratio <= benchmarkRatios.get(numItems));

            // Record these ratios to take an over-all mean later.
            ratios.add(ratio);
        }

        // Compute mean ratio.
        double sum = 0;
        for (Double ratio : ratios) {
            sum += ratio;
        }
        double meanRatio = sum / ratios.size();
        System.out.println("meanRatio: " + meanRatio);

        // Average-case timing over the range of number of inserts.
        // This is typically 2.5, but max 3.8 for unit tests, max 5.3 for unitTests "with coverage" from IDEA.
        // I think this means that PersistentVector performs worse with all the other work being done in the background
        // than ArrayList does.
        assertTrue(meanRatio < 5.6);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void putEx() { PersistentVector.empty().replace(1, "Hello"); }

    @Test public void replace() {
        PersistentVector<String> pv = PersistentVector.empty();
        pv = pv.append("Hello").append("World");
        assertArrayEquals(new String[] { "Hello", "World" },
                          pv.toArray());

        assertArrayEquals(new String[]{"Goodbye", "World"},
                          pv.replace(0, "Goodbye").toArray());

        PersistentVector<Integer> pv2 = PersistentVector.empty();
        int len = 999;
        Integer[] test = new Integer[len];
        for (int i = 0; i < len; i++) {
            pv2 = pv2.append(i);
            test[i] = i;
        }
        assertArrayEquals(test, pv2.toArray());

        for (int i = 0; i < len; i++) {
            pv2 = pv2.replace(i, len - i);
            test[i] = len - i;
        }
        assertArrayEquals(test, pv2.toArray());
    }

    @Test public void listIterator() {
        List<Integer> control = new ArrayList<>();
        PersistentVector<Integer> test = PersistentVector.empty();
        final int SOME = 200;

        for (int i = 0; i < SOME; i++) {
            control.add(i);
            test = test.append(i);
            assertEquals(control.size(), test.size());
        }

        PersistentVector<Integer> serTest = serializeDeserialize(test);

        for (int i = 0; i < SOME; i++) {
            assertEquals(control.get(i), test.get(i));
            assertEquals(control.get(i), serTest.get(i));
        }

        compareIterators(control.iterator(), test.iterator());
        compareIterators(control.iterator(), serTest.iterator());
        assertArrayEquals(control.toArray(), test.toArray());

        TestUtilities.listIteratorTest(control, test);
        TestUtilities.listIteratorTest(control, serTest);
    }

    @Test public void testConcat() throws Exception {
        PersistentVector<String> pv = PersistentVector.ofIter(Arrays.asList("1st", "2nd", "3rd"));
        pv = pv.concat(Arrays.asList("4th", "5th", "6th"));
        assertEquals(6, pv.size());
        assertEquals(PersistentVector.ofIter(Arrays.asList("1st", "2nd", "3rd", "4th", "5th",
                                                           "6th")),
                     pv);
    }

    @Test public void testMutable() {
        List<Integer> control = new ArrayList<>();
        MutList<Integer> test = PersistentVector.emptyMutable();
        final int SEVERAL = 2000; // more than 1024 so 3 levels deep.
        for (int i = 0; i < SEVERAL; i++) {
            control.add(i);
            test.append(i);
            assertEquals(control.size(), test.size());
        }

        for (int i = 0; i < SEVERAL; i++) {
            assertEquals(control.get(i), test.get(i));
        }

        for (int i = 0; i < SEVERAL; i++) {
            control.set(i, i + 10);
            test.replace(i, i + 10);
            assertEquals(control.size(), test.size());
        }

        for (int i = 0; i < SEVERAL; i++) {
            assertEquals(control.get(i), test.get(i));
        }

        List<Integer> additional = Arrays.asList(SEVERAL + 3, SEVERAL + 4, SEVERAL + 5);
        control.addAll(additional);
        test.concat(additional);

        assertEquals(control.size(), test.size());
        compareIterators(control.iterator(), test.iterator());
        assertEquals(control, test);

        ImList<Integer> imTest = test.immutable();
        assertEquals(control, imTest);
        assertEquals(control.size(), imTest.size());
        compareIterators(control.iterator(), imTest.iterator());

        assertEquals(emptyMutable(), emptyMutable());

        equalsDistinctHashCode(emptyMutable(), emptyMutable(), emptyMutable(),
                               emptyMutable().append(new Object()));

        MutList<Integer> m = emptyMutable();
        assertEquals(Arrays.asList(3, 5, 7), m.append(3).append(5).append(7));
        assertEquals(Arrays.asList(3, 5, 7), m.immutable());
        assertEquals(Arrays.asList(3, 5, 7), emptyMutable().append(3).append(5).append(7));

        m = PersistentVector.<Integer>empty().mutable();
        assertEquals(Arrays.asList(3, 5, 7), m.append(3).append(5).append(7));
        assertEquals(Arrays.asList(3, 5, 7), m.immutable());
        assertEquals(Arrays.asList(3, 5, 7), PersistentVector.emptyMutable().append(3).append(5).append(7));

    }

    @Test public void testAddReplace() {
        List<Integer> control = new ArrayList<>();
        ImList<Integer> test = PersistentVector.empty();
        final int SEVERAL = 2000; // more than 1024 so 3 levels deep.
        for (int i = 0; i < SEVERAL; i++) {
            control.add(i);
            test = test.append(i);
            assertEquals(control.size(), test.size());
        }

        for (int i = 0; i < SEVERAL; i++) {
            assertEquals(control.get(i), test.get(i));
        }

        for (int i = 0; i < SEVERAL; i++) {
            control.set(i, i + 10);
            test = test.replace(i, i + 10);
            assertEquals(control.size(), test.size());
        }

        for (int i = 0; i < SEVERAL; i++) {
            assertEquals(control.get(i), test.get(i));
        }

        // Replacing one beyond the index is an add (from Clojure).  Not so for java.util.List.
        control.add(SEVERAL, 9);
        test = test.replace(SEVERAL, 9);
        assertEquals(control.size(), test.size());

        assertEquals(test, test.mutable());
    }

    @Test public void reverseTest() {
        List<Integer> control = new ArrayList<>();
        ImList<Integer> test = PersistentVector.empty();
        for (int i = 0; i < 2000; i++) {
            control.add(i);
            test = test.append(i);
        }
        assertEquals(control.size(), test.size());
        TestUtilities.compareIterators(control.iterator(), test.iterator());

        Collections.reverse(control);
        test = test.reverse();
        assertEquals(control.size(), test.size());
        TestUtilities.compareIterators(control.iterator(), test.iterator());
    }
}
