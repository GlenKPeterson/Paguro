package org.organicdesign.fp.collections;

import org.junit.Ignore;
import org.junit.Test;
import org.organicdesign.fp.FunctionUtils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.organicdesign.fp.StaticImports.vec;
import static org.organicdesign.fp.testUtils.EqualsContract.equalsDistinctHashCode;

public class NestingVectorTest {
    @Test
    public void basics() {
        ImList<Integer> ls = NestingVector.ofArray(new Integer[]{1, 2, 3});
        assertEquals(3, ls.size());
        assertEquals(Integer.valueOf(1), ls.get(0));
        assertEquals(Integer.valueOf(2), ls.get(1));
        assertEquals(Integer.valueOf(3), ls.get(2));

        assertEquals(0, NestingVector.ofArray(null).size());
        assertEquals(0, NestingVector.ofArray(new Object[0]).size());
        assertEquals(NestingVector.ofArray(null), NestingVector.ofArray(new Object[0]));

        int max = 31;
        Integer[] is = new Integer[max];
        for (int i = 0; i < max; i++) {
            is[i] = i;
        }
        ImList<Integer> nv = NestingVector.ofArray(is);
        assertEquals(max, nv.size());
        for (int i = 0; i < max; i++) {
            assertEquals(Integer.valueOf(is[i]), nv.get(i));
        }

        max = 32;
        is = new Integer[max];
        for (int i = 0; i < max; i++) {
            is[i] = i;
        }
        nv = NestingVector.ofArray(is);
        assertEquals(max, nv.size());
        for (int i = 0; i < max; i++) {
            assertEquals(Integer.valueOf(is[i]), nv.get(i));
        }
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx00() { NestingVector.ofArray(null).get(Integer.MIN_VALUE); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx01() { NestingVector.ofArray(null).get(-1); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx02() { NestingVector.ofArray(null).get(0); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx03() { NestingVector.ofArray(null).get(1); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx04() { NestingVector.ofArray(null).get(Integer.MAX_VALUE); }

//    @Test(expected = IndexOutOfBoundsException.class)
//    public void emptyEx10() { PersistentVector.ofIter(Collections.emptyList()).get(Integer.MIN_VALUE); }
//    @Test(expected = IndexOutOfBoundsException.class)
//    public void emptyEx11() { PersistentVector.ofIter(Collections.emptyList()).get(-1); }
//    @Test(expected = IndexOutOfBoundsException.class)
//    public void emptyEx12() { PersistentVector.ofIter(Collections.emptyList()).get(0); }
//    @Test(expected = IndexOutOfBoundsException.class)
//    public void emptyEx13() { PersistentVector.ofIter(Collections.emptyList()).get(1); }
//    @Test(expected = IndexOutOfBoundsException.class)
//    public void emptyEx14() { PersistentVector.ofIter(Collections.emptyList()).get(Integer.MAX_VALUE); }
//
//    @Test(expected = IndexOutOfBoundsException.class)
//    public void emptyEx20() { PersistentVector.ofIter(new ArrayList<>()).get(Integer.MIN_VALUE); }
//    @Test(expected = IndexOutOfBoundsException.class)
//    public void emptyEx21() { PersistentVector.ofIter(new ArrayList<>()).get(-1); }
//    @Test(expected = IndexOutOfBoundsException.class)
//    public void emptyEx22() { PersistentVector.ofIter(new ArrayList<>()).get(0); }
//    @Test(expected = IndexOutOfBoundsException.class)
//    public void emptyEx23() { PersistentVector.ofIter(new ArrayList<>()).get(1); }
//    @Test(expected = IndexOutOfBoundsException.class)
//    public void emptyEx24() { PersistentVector.ofIter(new ArrayList<>()).get(Integer.MAX_VALUE); }

    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx30() { NestingVector.ofArray(null).get(Integer.MIN_VALUE); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx31() { NestingVector.ofArray(null).get(-1); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx32() { NestingVector.ofArray(null).get(0); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx33() { NestingVector.ofArray(null).get(1); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx34() { NestingVector.ofArray(null).get(Integer.MAX_VALUE); }

    @Ignore
    @Test public void oneInt() {
        ImList<Integer> one1 = NestingVector.ofArray(new Integer[] { 1 });
        ImList<Integer> one2 = NestingVector.ofArray(new Integer[] { 1 });
        ImList<Integer> one3 = NestingVector.ofArray(new Integer[]{1});

        equalsDistinctHashCode(one1, one2, one3,
                               vec(-1));
    }

    @Test public void arrayConstruction() {
        // Never finished (at least 10 min), but didn't blow up.
        // int num = 100000000;
        int num = 1;
//        for (long num = 31; num <= (long) Integer.MAX_VALUE; num = num * 1021) {
        for (; num < 1000000; num = num * 7) {
//        for (int num = 31; num < 50000000; num = num * 7) {
//            System.out.println("Size: " + num);
            Integer[] is = new Integer[ (int) num];
            for (int i = 0; i < num; i++) {
                is[i] = i;
            }
//            long startMs = System.currentTimeMillis();
            ImList<Integer> nv = NestingVector.ofArray(is);
//            System.out.println("Created nesting vector from array in " +
//                               (System.currentTimeMillis() - startMs) + "ms");
            assertEquals(num, nv.size());
//            startMs = System.currentTimeMillis();
            for (int i = 0; i < num; i++) {
                assertEquals("Trouble getting " + i + "th element from vector of size " + num,
                             Integer.valueOf(is[i]), nv.get(i));
            }
//            System.out.println("Accessed each item in " +
//                               (System.currentTimeMillis() - startMs) + "ms");
        }
//        System.out.println("Max test was for vector of size " + num);
    }

    /*
With flyweight list:

Using Arrays.copyOf and .copyOfRange
Size: 1
Created nesting vector from Range in 0ms
Accessed each item in 0ms
Size: 7
Created nesting vector from Range in 0ms
Accessed each item in 1ms
Size: 49
Created nesting vector from Range in 1ms
Accessed each item in 0ms
Size: 343
Created nesting vector from Range in 1ms
Accessed each item in 0ms
Size: 2401
Created nesting vector from Range in 1ms
Accessed each item in 2ms
Size: 16807
Created nesting vector from Range in 4ms
Accessed each item in 12ms
Size: 117649
Created nesting vector from Range in 19ms
Accessed each item in 55ms
Size: 823543
Created nesting vector from Range in 74ms
Accessed each item in 212ms
Size: 5764801
Created nesting vector from Range in 289ms
Accessed each item in 1312ms
Size: 40353607
Created nesting vector from Range in 2960ms
Accessed each item in 11252ms
Size: 282475249
Created nesting vector from Range in 24441ms
Accessed each item in 61866ms

NextingVector.ofIter() uses toArray() on the source
Size: 1
Created nesting vector from Range in 0ms
Accessed each item in 0ms
Size: 7
Created nesting vector from Range in 0ms
Accessed each item in 0ms
Size: 49
Created nesting vector from Range in 3ms
Accessed each item in 0ms
Size: 343
Created nesting vector from Range in 2ms
Accessed each item in 0ms
Size: 2401
Created nesting vector from Range in 1ms
Accessed each item in 2ms
Size: 16807
Created nesting vector from Range in 3ms
Accessed each item in 12ms
Size: 117649
Created nesting vector from Range in 15ms
Accessed each item in 63ms
Size: 823543
Created nesting vector from Range in 48ms
Accessed each item in 216ms
Size: 5764801
Created nesting vector from Range in 249ms
Accessed each item in 1248ms
Size: 40353607
Created nesting vector from Range in 3507ms
Accessed each item in 11032ms
Size: 282475249
Created nesting vector from Range in 21407ms
Accessed each item in 64793ms
Size: 1977326743
Stopped before GC Crash.

ArrayList() uses toArray() on the source.
Size: 1
Created nesting vector from Range in 0ms
Accessed each item in 0ms
Size: 7
Created nesting vector from Range in 0ms
Accessed each item in 1ms
Size: 49
Created nesting vector from Range in 0ms
Accessed each item in 0ms
Size: 343
Created nesting vector from Range in 0ms
Accessed each item in 0ms
Size: 2401
Created nesting vector from Range in 0ms
Accessed each item in 2ms
Size: 16807
Created nesting vector from Range in 1ms
Accessed each item in 9ms
Size: 117649
Created nesting vector from Range in 3ms
Accessed each item in 45ms
Size: 823543
Created nesting vector from Range in 17ms
Accessed each item in 239ms
Size: 5764801
Created nesting vector from Range in 55ms
Accessed each item in 1488ms
Size: 40353607
Created nesting vector from Range in 2948ms
Accessed each item in 8743ms
Size: 282475249
java.lang.OutOfMemoryError: Java heap space

PersistentVector.ofIter() which uses listIterator on the source.
Size: 1
Created nesting vector from Range in 0ms
Accessed each item in 1ms
Size: 7
Created nesting vector from Range in 0ms
Accessed each item in 0ms
Size: 49
Created nesting vector from Range in 0ms
Accessed each item in 0ms
Size: 343
Created nesting vector from Range in 2ms
Accessed each item in 2ms
Size: 2401
Created nesting vector from Range in 1ms
Accessed each item in 2ms
Size: 16807
Created nesting vector from Range in 4ms
Accessed each item in 15ms
Size: 117649
Created nesting vector from Range in 18ms
Accessed each item in 74ms
Size: 823543
Created nesting vector from Range in 44ms
Accessed each item in 202ms
Size: 5764801
Created nesting vector from Range in 124ms
Accessed each item in 1527ms
Size: 40353607
Created nesting vector from Range in 876ms
Accessed each item in 10674ms
Size: 282475249
Created nesting vector from Range in 27180ms


     */

    static class Repeater implements UnmodList<String> {
        private final int size;
        Repeater(int sz) { size = sz; }
        String[] testStrs = new String[] { "First", "Second", "Third", "Fourth", "Fifth" };
        final int testLen = testStrs.length;
        @Override public int size() { return size; }
        @Override public String get(int index) { return testStrs[index % testLen]; }
        @Override public String[] toArray() {
            String[] ret = new String[size];
            for (int i = 0; i < size; i++) {
                ret[i] = testStrs[i % testLen];
            }
            return ret;
        }
        @Override public UnmodListIterator<String> listIterator(int index) {
//                    throw new UnsupportedOperationException("Not implemented");
            return new UnmodListIterator<String>() {
                int i = index;
                @Override public boolean hasNext() { return i < size; }

                @Override public String next() {
                    String ret = get(i);
                    i = i + 1;
                    return ret;
                }

                @Override public boolean hasPrevious() { return i > 0; }

                @Override public String previous() {
                    i = i - 1;
                    return get(i);
                }

                @Override public int nextIndex() { return i; }

                @Override public int previousIndex() { return i - 1; }
            };
        }
    };


    @Test public void listConstruction() {
        // For some reason, my JVM won't use more than 2.1GB for these tests.
        // Created 62,748,517 items in 23 seconds.  Took forever to check each index.
        // Runs into a GC loop and crashes before 410,338,673
        long num = 1;
        // To prevent optimization
        String s = null;
        System.out.println("Size\tCreation\tAccess");
        long startNs = 0;
        long endNs = 0;

//        for (; num <= Integer.MAX_VALUE; num = num * 7) {
//        for (; num <= 40353607; num = num * 7) {
        for (; num <= 1000000; num = num * 7) {
            System.out.print(num);
            // This is a very low memory, low GC implementation of list, being a flyweight
            // with only 5 objects.
            @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
            UnmodList<String> testList = new Repeater((int) num);
//            RangeOfInt is = RangeOfInt.of(num);

            startNs = System.nanoTime();
//            List<String> nv = new ArrayList<>(testList);
            List<String> nv = NestingVector.of(testList);
//            List<String> nv = PersistentVector.ofIter(testList);
            endNs = System.nanoTime();
            System.out.print("\t" + ((endNs - startNs) / num));
            assertEquals(num, nv.size());
            startNs = System.nanoTime();
            for (int i = 0; i < num; i++) {
                s = nv.get(i);
//                assertEquals("Trouble getting " + i + "th element from vector of size " + num,
//                             testList.get(i), nv.get(i));
            }
            endNs = System.nanoTime();
            System.out.println("\t" + ((endNs - startNs) / num));
        }
        System.out.println("All times are nanoseconds per item processed.");
        System.out.println("Max test was for vector of size " + num + " with last string: " + s);
    }


    @Test(expected = IndexOutOfBoundsException.class)
    public void oneEx00() { NestingVector.ofArray(new Integer[] { 1 }).get(Integer.MIN_VALUE); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void oneEx01() { NestingVector.ofArray(new Integer[] { 1 }).get(-1); }
    @Test
    public void oneIsOne() {
        assertEquals(Integer.valueOf(1), NestingVector.ofArray(new Integer[] { 1 }).get(0));
    }
    @Test(expected = IndexOutOfBoundsException.class)
    public void oneEx03() { NestingVector.ofArray(new Integer[] { 1 }).get(1); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void oneEx04() { NestingVector.ofArray(new Integer[] { 1 }).get(Integer.MAX_VALUE); }

    @Test(expected = IndexOutOfBoundsException.class)
    public void oneEx10() {
        List<Integer> oneList = new ArrayList<>();
        oneList.add(1);
        NestingVector.ofArray(oneList.toArray()).get(Integer.MIN_VALUE); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void oneEx11() {
        List<Integer> oneList = new ArrayList<>();
        oneList.add(1);
        NestingVector.ofArray(oneList.toArray()).get(-1); }
    @Test
    public void oneIsOne2() {
        List<Integer> oneList = new ArrayList<>();
        oneList.add(1);
        assertEquals(Integer.valueOf(1), NestingVector.ofArray(oneList.toArray()).get(0)); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void oneEx13() {
        List<Integer> oneList = new ArrayList<>();
        oneList.add(1);
        NestingVector.ofArray(oneList.toArray()).get(1); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void oneEx14() {
        List<Integer> oneList = new ArrayList<>();
        oneList.add(1);
        NestingVector.ofArray(oneList.toArray()).get(Integer.MAX_VALUE); }

//    @Test(expected = IndexOutOfBoundsException.class)
//    public void oneEx20() {
//        List<Integer> oneList = new ArrayList<>();
//        oneList.add(1);
//        PersistentVector.ofIter(Collections.unmodifiableList(oneList)).get(Integer.MIN_VALUE); }
//    @Test(expected = IndexOutOfBoundsException.class)
//    public void oneEx21() {
//        List<Integer> oneList = new ArrayList<>();
//        oneList.add(1);
//        PersistentVector.ofIter(Collections.unmodifiableList(oneList)).get(-1); }
//    @Test
//    public void oneIsOne3() {
//        List<Integer> oneList = new ArrayList<>();
//        oneList.add(1);
//        assertEquals(Integer.valueOf(1), PersistentVector.ofIter(Collections.unmodifiableList(oneList)).get(0)); }
//    @Test(expected = IndexOutOfBoundsException.class)
//    public void oneEx23() {
//        List<Integer> oneList = new ArrayList<>();
//        oneList.add(1);
//        PersistentVector.ofIter(Collections.unmodifiableList(oneList)).get(1); }
//    @Test(expected = IndexOutOfBoundsException.class)
//    public void oneEx24() {
//        List<Integer> oneList = new ArrayList<>();
//        oneList.add(1);
//        PersistentVector.ofIter(Collections.unmodifiableList(oneList)).get(Integer.MAX_VALUE); }

    @Ignore
    @Test public void addSeveralItems() throws NoSuchAlgorithmException {
        final int SEVERAL = SecureRandom.getInstanceStrong().nextInt(999999) + 33 ;
        ImList<Integer> is = NestingVector.ofArray(null);
        for (int j = 0; j < SEVERAL; j++){
            is = is.append(j);
        }
        assertEquals(SEVERAL, is.size());
        for (int j = 0; j < SEVERAL; j++){
            assertEquals(Integer.valueOf(j), is.get(j));
        }
    }

    @Ignore
    @Test
    public void transienceTest() {
        ImList<Integer> list = NestingVector.ofArray(new Integer[] {
                1,2,3,4,5,6,7,8,9,0,11,12,13,14,15,16,17,18,19,20,
                21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40});
        List<Integer> l2 = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20,
                                         21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40);
        List<Integer> different = Arrays.asList(1,2,3,4,5,6,7,8,9,0,11,12,13,14,15,16,17,18,19,20,
                                                 21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,41);

        equalsDistinctHashCode(list, l2, FunctionUtils.unmodList(l2), different);
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
        final UnmodMap<Integer,Double> benchmarkRatios;
        {
            Map<Integer,Double> mm = new HashMap<>();
            mm.put(1, 1.4);
            mm.put(10, 2.7);
            mm.put(100, 6.5);
            mm.put(1000, 9.0);
            mm.put(10000, 18.0);
            mm.put(100000, 13.9);
            mm.put(1000000, 7.8);
            benchmarkRatios = FunctionUtils.unmodMap(mm);
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
                ImList<Integer> test = NestingVector.ofArray(null);
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
        assertTrue(meanRatio < 5.6);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void putEx() { NestingVector.ofArray(null).replace(1, "Hello"); }

    @Ignore
    @Test public void replace() {
        ImList<String> pv = NestingVector.ofArray(null);
        pv = pv.append("Hello").append("World");
        assertArrayEquals(new String[] { "Hello", "World" },
                          pv.toArray());

        assertArrayEquals(new String[]{"Goodbye", "World"},
                          pv.replace(0, "Goodbye").toArray());

        ImList<Integer> pv2 = NestingVector.ofArray(null);
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

    /*
    @Test public void listIterator() {
        ImList<Integer> pv2 = NestingVector.ofArray(null);
        int len = 999;
        Integer[] test = new Integer[len];

        for (int i = 0; i < len; i++) {
            pv2 = pv2.append(len - i);
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
*/
}