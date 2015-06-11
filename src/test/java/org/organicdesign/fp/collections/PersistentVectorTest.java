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

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import static org.junit.Assert.*;
import static org.organicdesign.fp.StaticImports.un;
import static org.organicdesign.fp.testUtils.EqualsContract.equalsDistinctHashCode;

@RunWith(JUnit4.class)
public class PersistentVectorTest {
    @Test
    public void basics() {
        Integer[] threeIntArray = new Integer[]{1, 2, 3};
        ImList<Integer> list = PersistentVector.of(1, 2, 3);
        Integer[] resultArray = list.toArray(new Integer[3]);
        assertArrayEquals(threeIntArray, resultArray);
    }

    @Test
    public void empty() {
        ImList<Integer> empty1 = PersistentVector.empty();
        ImList<Integer> empty2 = PersistentVector.ofIter(Collections.emptyList());
        ImList<Integer> empty3 = PersistentVector.ofIter(new ArrayList<>());
        ImList<Integer> empty4 = PersistentVector.of();

        equalsDistinctHashCode(empty1, empty2, empty3,
                               PersistentVector.of(1));

        equalsDistinctHashCode(empty2, empty3, empty4,
                               PersistentVector.of((Integer) null));
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
    public void emptyEx30() { PersistentVector.of().get(Integer.MIN_VALUE); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx31() { PersistentVector.of().get(-1); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx32() { PersistentVector.of().get(0); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx33() { PersistentVector.of().get(1); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx34() { PersistentVector.of().get(Integer.MAX_VALUE); }

    @Test
    public void oneInt() {
        ImList<Integer> one1 = PersistentVector.of(1);
        List<Integer> oneList = new ArrayList<>();
        oneList.add(1);
        ImList<Integer> one2 = PersistentVector.ofIter(oneList);
        ImList<Integer> one3 = PersistentVector.ofIter(Collections.unmodifiableList(oneList));

        equalsDistinctHashCode(one1, one2, one3,
                               PersistentVector.of(-1));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void oneEx00() { PersistentVector.of(1).get(Integer.MIN_VALUE); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void oneEx01() { PersistentVector.of(1).get(-1); }
    @Test
    public void oneIsOne() {
        assertEquals(Integer.valueOf(1), PersistentVector.of(1).get(0));
    }
    @Test(expected = IndexOutOfBoundsException.class)
    public void oneEx03() { PersistentVector.of(1).get(1); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void oneEx04() { PersistentVector.of(1).get(Integer.MAX_VALUE); }

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
        final int SEVERAL = SecureRandom.getInstanceStrong().nextInt(999999) + 33 ;
        PersistentVector<Integer> is = PersistentVector.empty();
        for (int j = 0; j < SEVERAL; j++){
            is = is.appendOne(j);
        }
        assertEquals(SEVERAL, is.size());
        for (int j = 0; j < SEVERAL; j++){
            assertEquals(Integer.valueOf(j), is.get(j));
        }
    }

    @Test
    public void transienceTest() {
        ImList<Integer> list = PersistentVector.of(1,2,3,4,5,6,7,8,9,0,11,12,13,14,15,16,17,18,19,20,
                                                   21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40);
        List<Integer> l2 = Arrays.asList(1,2,3,4,5,6,7,8,9,0,11,12,13,14,15,16,17,18,19,20,
                                         21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40);
        List<Integer> different = Arrays.asList(1,2,3,4,5,6,7,8,9,0,11,12,13,14,15,16,17,18,19,20,
                                                 21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,41);

        equalsDistinctHashCode(list, l2, un(l2), different);
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
        final UnMap<Integer,Double> benchmarkRatios;
        {
            Map<Integer,Double> mm = new HashMap<>();
            mm.put(1, 1.4);
            mm.put(10, 2.7);
            mm.put(100, 6.5);
            mm.put(1000, 9.0);
            mm.put(10000, 18.0);
            mm.put(100000, 13.9);
            mm.put(1000000, 7.8);
            benchmarkRatios = un(mm);
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
                    test = test.appendOne(i);
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
        for (int i = 0; i < ratios.size(); i++) {
            sum += ratios.get(i);
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
        pv = pv.appendOne("Hello").appendOne("World");
        assertArrayEquals(new String[] { "Hello", "World" },
                          pv.toArray());

        assertArrayEquals(new String[]{"Goodbye", "World"},
                          pv.replace(0, "Goodbye").toArray());

        PersistentVector<Integer> pv2 = PersistentVector.empty();
        int len = 999;
        Integer[] test = new Integer[len];
        for (int i = 0; i < len; i++) {
            pv2 = pv2.appendOne(i);
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
        PersistentVector<Integer> pv2 = PersistentVector.empty();
        int len = 999;
        Integer[] test = new Integer[len];

        for (int i = 0; i < len; i++) {
            pv2 = pv2.appendOne(len - i);
            test[i] = len - i;
        }
        assertArrayEquals(test, pv2.toArray());

        List<Integer> tList = Arrays.asList(test);
        ListIterator<Integer> benchmark = tList.listIterator(7);
        ListIterator<Integer> subjectIter = pv2.listIterator(7);

        assertEquals(benchmark.hasNext(), subjectIter.hasNext());
        assertEquals(benchmark.hasPrevious(), subjectIter.hasPrevious());
        assertEquals(benchmark.nextIndex(), subjectIter.nextIndex());
        assertEquals(benchmark.previousIndex(), subjectIter.previousIndex());

        while(benchmark.hasNext() && subjectIter.hasNext()) {
            assertEquals(benchmark.next(), subjectIter.next());
            assertEquals(benchmark.hasNext(), subjectIter.hasNext());
            assertEquals(benchmark.hasPrevious(), subjectIter.hasPrevious());
            assertEquals(benchmark.nextIndex(), subjectIter.nextIndex());
            assertEquals(benchmark.previousIndex(), subjectIter.previousIndex());
        }

        assertEquals(benchmark.hasNext(), subjectIter.hasNext());
        assertEquals(benchmark.hasPrevious(), subjectIter.hasPrevious());
        assertEquals(benchmark.nextIndex(), subjectIter.nextIndex());
        assertEquals(benchmark.previousIndex(), subjectIter.previousIndex());

        while(benchmark.hasPrevious() && subjectIter.hasPrevious()) {
            assertEquals(benchmark.previous(), subjectIter.previous());
            assertEquals(benchmark.hasNext(), subjectIter.hasNext());
            assertEquals(benchmark.hasPrevious(), subjectIter.hasPrevious());
            assertEquals(benchmark.nextIndex(), subjectIter.nextIndex());
            assertEquals(benchmark.previousIndex(), subjectIter.previousIndex());
        }

        assertEquals(benchmark.hasNext(), subjectIter.hasNext());
        assertEquals(benchmark.hasPrevious(), subjectIter.hasPrevious());
        assertEquals(benchmark.nextIndex(), subjectIter.nextIndex());
        assertEquals(benchmark.previousIndex(), subjectIter.previousIndex());

    }
}
