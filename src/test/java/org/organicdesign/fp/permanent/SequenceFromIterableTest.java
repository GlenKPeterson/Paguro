package org.organicdesign.fp.permanent;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SequenceFromIterableTest {

    @Test public void basics() {
        assertEquals(Sequence.EMPTY_SEQUENCE,
                     SequenceFromIterable.of(null));
        assertEquals(Sequence.EMPTY_SEQUENCE,
                     SequenceFromIterable.of(Collections.emptyList()));

        List<String> sList = Arrays.asList("one", "two", "three");
        Sequence<String> seq = SequenceFromIterable.of(sList);

        assertTrue(seq.head().isSome());
        assertEquals("one", seq.head().get());

        Sequence<String> seq2 = seq.tail();
        assertTrue(seq2.head().isSome());
        assertEquals("two", seq2.head().get());

        Sequence<String> seq3 = seq2.tail();
        assertTrue(seq3.head().isSome());
        assertEquals("three", seq3.head().get());

        assertEquals(Sequence.EMPTY_SEQUENCE,
                     seq3.tail());

        // Verify that they haven't changed.
        assertEquals("one", seq.head().get());
        assertEquals("two", seq2.head().get());
        assertEquals("three", seq3.head().get());
    }

//    @Test
//    public void speedTest() {
//        int MAX = 10000000;
//        Long[] ls = new Long[MAX];
//        for (int i = 0; i < MAX; i++) {
//            ls[i] = Long.valueOf(i);
//        }
//
//        long startTime;
//        List<Long> longList = Arrays.asList(ls);
//
//        startTime = System.nanoTime();
//        {
//            Sequence<Long> s1 = SequenceFromIterable2.of(longList);
//            while (s1.head().isSome()) {
//                if (s1.head().get() < -1) { break; }
//                s1 = s1.tail();
//            }
//        }
//        System.out.println("SequenceFromIterable2: " + (System.nanoTime() - startTime));
//
//        startTime = System.nanoTime();
//        {
//            Sequence<Long> s1 = SequenceFromIterable3.of(longList);
//            while (s1.head().isSome()) {
//                if (s1.head().get() < -1) { break; }
//                s1 = s1.tail();
//            }
//        }
//        System.out.println("SequenceFromIterable3: " + (System.nanoTime() - startTime));
//
//        startTime = System.nanoTime();
//        {
//            Sequence<Long> s1 = SequenceFromIterable.of(longList);
//            while (s1.head().isSome()) {
//                if (s1.head().get() < -1) { break; }
//                s1 = s1.tail();
//            }
//        }
//        System.out.println("SequenceFromIterable1: " + (System.nanoTime() - startTime));
//
//        startTime = System.nanoTime();
//        {
//            Sequence<Long> s1 = SequenceFromIterable2.of(longList);
//            while (s1.head().isSome()) {
//                if (s1.head().get() < -1) { break; }
//                s1 = s1.tail();
//            }
//        }
//        System.out.println("SequenceFromIterable2: " + (System.nanoTime() - startTime));
//
//        startTime = System.nanoTime();
//        {
//            Sequence<Long> s1 = SequenceFromIterable.of(longList);
//            while (s1.head().isSome()) {
//                if (s1.head().get() < -1) { break; }
//                s1 = s1.tail();
//            }
//        }
//        System.out.println("SequenceFromIterable1: " + (System.nanoTime() - startTime));
//
//        startTime = System.nanoTime();
//        {
//            Sequence<Long> s1 = SequenceFromIterable3.of(longList);
//            while (s1.head().isSome()) {
//                if (s1.head().get() < -1) { break; }
//                s1 = s1.tail();
//            }
//        }
//        System.out.println("SequenceFromIterable3: " + (System.nanoTime() - startTime));
//
//        startTime = System.nanoTime();
//        {
//            Iterator<Long> iter = longList.iterator();
//            Long l = null;
//            while (iter.hasNext()) {
//                l = iter.next();
//                if (l < -1) { break; }
//            }
//        }
//        System.out.println("Iterator:              " + (System.nanoTime() - startTime));
//
//    }

}