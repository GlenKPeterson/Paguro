package org.organicdesign.fp.collections;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.*;
import static org.organicdesign.fp.StaticImports.unMap;

@RunWith(JUnit4.class)
public class PersistentVectorTest {
    @Test
    public void basics() {
        Integer[] threeIntArray = new Integer[]{1, 2, 3};
        ImList<Integer> list = PersistentVector.of(1, 2, 3);
        Integer[] resultArray = list.toArray(new Integer[3]);
        assertArrayEquals(threeIntArray, resultArray);
    }

    public void helpEquality(Object o1, Object o2) {
        assertTrue(o1.equals(o2));
        assertTrue(o2.equals(o1));
        assertEquals(o1.hashCode(), o2.hashCode());
    }

    @Test
    public void empty() {
        ImList<Integer> empty1 = PersistentVector.empty();
        ImList<Integer> empty2 = PersistentVector.of(Collections.emptyList());
        ImList<Integer> empty3 = PersistentVector.of(new ArrayList<>());
        ImList<Integer> empty4 = PersistentVector.of();

        helpEquality(empty1, empty1);
        helpEquality(empty1, empty2);
        helpEquality(empty1, empty3);
        helpEquality(empty1, empty4);
        helpEquality(empty2, empty2);
        helpEquality(empty2, empty3);
        helpEquality(empty2, empty4);
        helpEquality(empty3, empty3);
        helpEquality(empty3, empty4);
        helpEquality(empty4, empty4);
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
    public void emptyEx10() { PersistentVector.of(Collections.emptyList()).get(Integer.MIN_VALUE); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx11() { PersistentVector.of(Collections.emptyList()).get(-1); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx12() { PersistentVector.of(Collections.emptyList()).get(0); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx13() { PersistentVector.of(Collections.emptyList()).get(1); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx14() { PersistentVector.of(Collections.emptyList()).get(Integer.MAX_VALUE); }

    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx20() { PersistentVector.of(new ArrayList<>()).get(Integer.MIN_VALUE); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx21() { PersistentVector.of(new ArrayList<>()).get(-1); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx22() { PersistentVector.of(new ArrayList<>()).get(0); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx23() { PersistentVector.of(new ArrayList<>()).get(1); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx24() { PersistentVector.of(new ArrayList<>()).get(Integer.MAX_VALUE); }

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
        ImList<Integer> one2 = PersistentVector.of(oneList);
        ImList<Integer> one3 = PersistentVector.of(Collections.unmodifiableList(oneList));

        helpEquality(one1, one1);
        helpEquality(one1, one2);
        helpEquality(one1, one3);
        helpEquality(one2, one2);
        helpEquality(one2, one3);
        helpEquality(one3, one3);
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
        PersistentVector.of(oneList).get(Integer.MIN_VALUE); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void oneEx11() {
        List<Integer> oneList = new ArrayList<>();
        oneList.add(1);
        PersistentVector.of(oneList).get(-1); }
    @Test
    public void oneIsOne2() {
        List<Integer> oneList = new ArrayList<>();
        oneList.add(1);
        assertEquals(Integer.valueOf(1), PersistentVector.of(oneList).get(0)); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void oneEx13() {
        List<Integer> oneList = new ArrayList<>();
        oneList.add(1);
        PersistentVector.of(oneList).get(1); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void oneEx14() {
        List<Integer> oneList = new ArrayList<>();
        oneList.add(1);
        PersistentVector.of(oneList).get(Integer.MAX_VALUE); }

    @Test(expected = IndexOutOfBoundsException.class)
    public void oneEx20() {
        List<Integer> oneList = new ArrayList<>();
        oneList.add(1);
        PersistentVector.of(Collections.unmodifiableList(oneList)).get(Integer.MIN_VALUE); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void oneEx21() {
        List<Integer> oneList = new ArrayList<>();
        oneList.add(1);
        PersistentVector.of(Collections.unmodifiableList(oneList)).get(-1); }
    @Test
    public void oneIsOne3() {
        List<Integer> oneList = new ArrayList<>();
        oneList.add(1);
        assertEquals(Integer.valueOf(1), PersistentVector.of(Collections.unmodifiableList(oneList)).get(0)); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void oneEx23() {
        List<Integer> oneList = new ArrayList<>();
        oneList.add(1);
        PersistentVector.of(Collections.unmodifiableList(oneList)).get(1); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void oneEx24() {
        List<Integer> oneList = new ArrayList<>();
        oneList.add(1);
        PersistentVector.of(Collections.unmodifiableList(oneList)).get(Integer.MAX_VALUE); }

    @Test public void addSeveralItems() throws NoSuchAlgorithmException {
        final int SEVERAL = SecureRandom.getInstanceStrong().nextInt(999999) + 33 ;
        PersistentVector<Integer> is = PersistentVector.empty();
        for (int j = 0; j < SEVERAL; j++){
            is = is.append(j);
        }
        assertEquals(SEVERAL, is.size());
        for (int j = 0; j < SEVERAL; j++){
            assertEquals(Integer.valueOf(j), is.get(j));
        }
    }

    // Time ImVectorImplementation vs. java.util.ArrayList to prove that performance does not degrade
    // if changes are made.
    @Test public void speedTest() throws NoSuchAlgorithmException, InterruptedException {
        final int maxItems = 1000000;

        System.out.println("Speed tests take time.  The more accurate, the more time.\n" +
                                   "This may fail occasionally, then work when re-run, which is OK.\n" +
                                   "Better that, than set the limit too high and miss a performance drop.");

        // These are worst-case timings, indexed by number of items inserted in the test.
        Map<Integer,Double> benchmarkRatios = unMap(
                1, 1.3,
                10, 2.7,
                100, 4.9,
                1000, 6.4,
                10000, 10.2,
                100000, 13.9,
                1000000, 7.7);

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
        for (int i = 0; i < ratios.size(); i++) {
            sum += ratios.get(i);
        }
        double meanRatio = sum / ratios.size();
        System.out.println("meanRatio: " + meanRatio);

        // Average-case timing over the range of number of inserts.
        // This is typically 2.5, but max 3.8 for unit tests, max 5.3 for unitTests "with coverage" from IDEA.
        // I think this means that PersistentVector performs worse with all the other work being done in the background
        // than ArrayList does.
        assertTrue(meanRatio < 5.3);
    }

}
