package org.organicdesign.fp.collections;

import org.junit.Test;
import org.organicdesign.fp.testUtils.EqualsContract;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import static org.junit.Assert.*;

public class RangeOfIntTest {
    @Test(expected = IllegalArgumentException.class)
    public void factory1() {
        RangeOfInt.of(null, Integer.valueOf(1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void factory2() {
        RangeOfInt.of(Integer.valueOf(1), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void factory3() {
        RangeOfInt.of(1, 0);
    }

    @Test public void basics() {
        RangeOfInt ir1 = RangeOfInt.of(0, 1);
        assertEquals(ir1.contains(0), true);
        assertEquals(ir1.contains(1), false);
        assertEquals(ir1.contains(-1), false);
        assertEquals(ir1.size(), 1);

        List<Integer> a = Collections.singletonList(99);
        List<Integer> b = RangeOfInt.of(99, 100); // Is this correct?  It matches Scala, but...
        assertEquals(a.size(), b.size());
        assertEquals(a.get(0), b.get(0));
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    @Test public void containsTest() {
        RangeOfInt ir1 = RangeOfInt.of(Integer.MIN_VALUE, Integer.MAX_VALUE);

        assertTrue(ir1.contains(33));
        assertTrue(ir1.contains(-99999));
        assertTrue(ir1.contains(0));
        assertTrue(ir1.contains(Integer.MIN_VALUE));
        assertTrue(ir1.contains(Integer.MAX_VALUE - 1));
        assertFalse(ir1.contains(Integer.MAX_VALUE));
        assertFalse(ir1.contains(Integer.MAX_VALUE + 1L));

        assertTrue(ir1.contains(Integer.valueOf(88888888)));

        assertTrue(ir1.contains(Long.valueOf(Integer.MIN_VALUE)));
        assertTrue(ir1.contains(Long.valueOf(Integer.MAX_VALUE - 1)));

        assertFalse(ir1.contains(Long.valueOf(Integer.MIN_VALUE - 1L)));
        assertFalse(ir1.contains(Long.valueOf(Integer.MAX_VALUE)));

        assertTrue(ir1.contains(BigInteger.valueOf(Integer.MIN_VALUE)));
        assertTrue(ir1.contains(BigInteger.valueOf(Integer.MAX_VALUE - 1)));

        assertFalse(ir1.contains(BigInteger.valueOf(Integer.MIN_VALUE - 1L)));
        assertFalse(ir1.contains(BigInteger.valueOf(Integer.MAX_VALUE)));

        assertTrue(ir1.contains("33"));
        assertTrue(ir1.contains("-21"));
        assertTrue(ir1.contains(String.valueOf(Integer.MIN_VALUE)));
        assertTrue(ir1.contains(String.valueOf(Integer.MAX_VALUE - 1L)));
        assertFalse(ir1.contains(String.valueOf(Integer.MAX_VALUE)));
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    @Test(expected = IllegalArgumentException.class)
    public void containsEx01() {
        RangeOfInt.of(Integer.MIN_VALUE, Integer.MAX_VALUE)
                  .contains(new Number() {
                      @Override public int intValue() { return 0; }
                      @Override public long longValue() { return 0L; }
                      @Override public float floatValue() { return 0.0f; }
                      @Override public double doubleValue() { return 0.0; }
                  });
    }
    @SuppressWarnings("SuspiciousMethodCalls")
    @Test(expected = IllegalArgumentException.class)
    public void containsEx02() {
        RangeOfInt.of(Integer.MIN_VALUE, Integer.MAX_VALUE).contains(Float.valueOf(0.0f));
    }
    @SuppressWarnings("SuspiciousMethodCalls")
    @Test(expected = IllegalArgumentException.class)
    public void containsEx03() {
        RangeOfInt.of(Integer.MIN_VALUE, Integer.MAX_VALUE).contains(Double.valueOf(0.0d));
    }
    @SuppressWarnings("SuspiciousMethodCalls")
    @Test(expected = NumberFormatException.class)
    public void containsEx04() {
        RangeOfInt.of(Integer.MIN_VALUE, Integer.MAX_VALUE)
                  .contains("I wish they changed this to T");
    }
    @SuppressWarnings("SuspiciousMethodCalls")
    @Test(expected = NumberFormatException.class)
    public void containsEx05() {
        RangeOfInt.of(Integer.MIN_VALUE, Integer.MAX_VALUE)
                  .contains("12.5"); // float
    }
    @SuppressWarnings("SuspiciousMethodCalls")
    @Test(expected = NumberFormatException.class)
    public void containsEx06() {
        RangeOfInt.of(Integer.MIN_VALUE, Integer.MAX_VALUE)
                  .contains("-0.6"); // float
    }
    @SuppressWarnings("SuspiciousMethodCalls")
    @Test(expected = NumberFormatException.class)
    public void containsEx07() {
        RangeOfInt.of(Integer.MIN_VALUE, Integer.MAX_VALUE)
                  .contains("0x7"); // Hexidecimal
    }
    @SuppressWarnings("SuspiciousMethodCalls")
    @Test(expected = NumberFormatException.class)
    public void containsEx08() {
        RangeOfInt.of(Integer.MIN_VALUE, Integer.MAX_VALUE)
                  .contains(String.valueOf(Integer.MAX_VALUE + 1L));
    }
    @SuppressWarnings("SuspiciousMethodCalls")
    @Test(expected = NumberFormatException.class)
    public void containsEx09() {
        RangeOfInt.of(Integer.MIN_VALUE, Integer.MAX_VALUE)
                  .contains(String.valueOf(Integer.MIN_VALUE - 1L));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testEx01() { RangeOfInt.of(1, 2).get(-1); }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testEx02() { RangeOfInt.of(1, 2).get(2); }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testEx03() { RangeOfInt.of(1, 2).get(Integer.MIN_VALUE); }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testEx04() { RangeOfInt.of(1, 2).get(Integer.MAX_VALUE); }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testEx05() {
        RangeOfInt r = RangeOfInt.of(99);
        r.get(r.size());
    }

    @Test public void equality() {
        EqualsContract.equalsDistinctHashCode(RangeOfInt.of(Integer.valueOf(-1),
                                                            Integer.valueOf(4)),
                                              RangeOfInt.of(-1, 4),
                                              RangeOfInt.of(Integer.valueOf(0 - 1),
                                                            Integer.valueOf(3 + 1)),
                                              RangeOfInt.of(-1, 3));
    }

    @Test public void ListIteratorTest() {
        ListIterator<Integer> a = Arrays.asList(-2, -1, 0, 1, 2, 3, 4).listIterator();
        UnmodListIterator<Integer> b = RangeOfInt.of(-2, 5).listIterator();

        while (a.hasNext()) {
            assertTrue(b.hasNext());

            assertEquals(a.nextIndex(), b.nextIndex());
            assertEquals(a.previousIndex(), b.previousIndex());

            assertEquals(a.next(), b.next());

            assertEquals(a.nextIndex(), b.nextIndex());
            assertEquals(a.previousIndex(), b.previousIndex());
        }
        assertFalse(b.hasNext());

        while (a.hasPrevious()) {
            assertTrue(b.hasPrevious());

            assertEquals(a.nextIndex(), b.nextIndex());
            assertEquals(a.previousIndex(), b.previousIndex());

            assertEquals(a.previous(), b.previous());

            assertEquals(a.nextIndex(), b.nextIndex());
            assertEquals(a.previousIndex(), b.previousIndex());
        }
        assertFalse(b.hasPrevious());
    }

    @Test public void equatorTest() {
        List<Integer> a = Arrays.asList(-2, -1, 0, 1, 2, 3, 4);
        List<Integer> b = RangeOfInt.of(-2, 5);
        assertEquals(RangeOfInt.LIST_EQUATOR.hash(a),
                     RangeOfInt.LIST_EQUATOR.hash(b));
        assertEquals(a.size(), b.size());

        assertTrue(UnmodSortedIterable.equals(UnmodSortedIterable.castFromList(a),
                                              UnmodSortedIterable.castFromList(b)));

        assertTrue("List and range are equal", RangeOfInt.LIST_EQUATOR.eq(a, b));

        assertTrue("Range and range are equal", RangeOfInt.LIST_EQUATOR.eq(RangeOfInt.of(-2, 5),
                                                                           b));

    }

    @Test public void indexOfTest() {
        List<Integer> a = Arrays.asList(-2, -1, 0, 1, 2, 3, 4);
        List<Integer> b = RangeOfInt.of(-2, 5);

        for (int i : a) {
            assertEquals(a.indexOf(i), b.indexOf(i));
            assertEquals(a.lastIndexOf(i), b.lastIndexOf(i));
        }

        assertEquals(a.indexOf(Integer.MAX_VALUE), b.indexOf(Integer.MAX_VALUE));
        assertEquals(a.lastIndexOf(Integer.MAX_VALUE), b.lastIndexOf(Integer.MAX_VALUE));
    }

    @Test public void subListTest() {
        List<Integer> a = Arrays.asList(-2, -1, 0, 1, 2, 3, 4);
        List<Integer> b = RangeOfInt.of(-2, 5);

        assertEquals(a.size(), b.size());
        assertEquals(a.get(0), b.get(0));
        assertEquals(a.get(a.size() - 1), b.get(b.size() - 1));

        List<Integer> sla = a.subList(1, 3);
        List<Integer> slb = b.subList(1, 3);

        assertEquals(sla.size(), slb.size());
        assertEquals(sla.get(0), slb.get(0));
        assertEquals(sla.get(sla.size() - 1), slb.get(slb.size() - 1));

        assertEquals(RangeOfInt.LIST_EQUATOR.hash(sla),
                     RangeOfInt.LIST_EQUATOR.hash(slb));
        assertTrue(RangeOfInt.LIST_EQUATOR.eq(sla, slb));

        EqualsContract.equalsDistinctHashCode(slb,
                                              RangeOfInt.of(-1, 1).subList(0, 2),
                                              RangeOfInt.of(-3, 5).subList(2, 4),
                                              RangeOfInt.of(-2, 5));

        sla = a.subList(0, a.size());
        slb = b.subList(0, b.size());

        assertEquals(sla.size(), slb.size());
        assertEquals(sla.get(0), slb.get(0));
        assertEquals(sla.get(sla.size() - 1), slb.get(slb.size() - 1));

        sla = a.subList(0, a.size() - 1);
        slb = b.subList(0, b.size() - 1);

        assertEquals(sla.size(), slb.size());
        assertEquals(sla.get(0), slb.get(0));
        assertEquals(sla.get(sla.size() - 1), slb.get(slb.size() - 1));

        sla = a.subList(0, 1);
        slb = b.subList(0, 1);

        assertEquals(sla.size(), slb.size());
        assertEquals(sla.get(0), slb.get(0));
        assertEquals(sla.get(sla.size() - 1), slb.get(slb.size() - 1));

        sla = a.subList(0, 0);
        slb = b.subList(0, 0);

        assertEquals(sla.size(), slb.size());

        sla = a.subList(2, 2);
        slb = b.subList(2, 2);

        assertEquals(sla.size(), slb.size());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void subListExArray1() { Arrays.asList(-2, -1, 0, 1, 2, 3, 4).subList(-1, 1); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void subListEx1() { RangeOfInt.of(-2, 5).subList(-1, 1); }

    @Test(expected = IllegalArgumentException.class)
    public void subListExArray2() { Arrays.asList(-2, -1, 0, 1, 2, 3, 4).subList(1, 0); }
    @Test(expected = IllegalArgumentException.class)
    public void subListEx2() { RangeOfInt.of(-2, 5).subList(1, 0); }

    @Test(expected = IndexOutOfBoundsException.class)
    public void subListExArray3() {
        List<Integer> r = Arrays.asList(-2, -1, 0, 1, 2, 3, 4);
        r.subList(0, r.size() + 1);
    }
    @Test(expected = IndexOutOfBoundsException.class)
    public void subListEx3() {
        RangeOfInt r = RangeOfInt.of(-2, 5);
        r.subList(0, r.size() + 1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void subListExArray4() {
        List<Integer> r = Arrays.asList(-2, -1, 0, 1, 2, 3, 4);
        r.subList(0, 0).get(0);
    }
    @Test(expected = IndexOutOfBoundsException.class)
    public void subListEx4() {
        RangeOfInt r = RangeOfInt.of(-2, 5);
        r.subList(0, 0).get(0);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void subListExArray5() {
        List<Integer> r = Arrays.asList(-2, -1, 0, 1, 2, 3, 4);
        r.subList(3,3).get(0);
    }
    @Test(expected = IndexOutOfBoundsException.class)
    public void subListEx5() {
        RangeOfInt r = RangeOfInt.of(-2, 5);
        r.subList(3,3).get(0);
    }

    @Test public void foundBug() {
        int MAX_BUCKET_LENGTH = 32;
//        List<Integer> ls = new ArrayList<>();
//        for (int i = 0; i < 81; i++) {
//            ls.add(i);
//        }
        List<Integer> ls = RangeOfInt.of(81);

        // Make the first bucket
        Object[] tmp = ls.subList(0, MAX_BUCKET_LENGTH).toArray();
//        System.out.println("First bucket: " + Arrays.toString(tmp));
//        Node<E> node = Node1.ofLeaf(tmp);
        int tailLen = ls.size() % MAX_BUCKET_LENGTH;

        int maxIdx = ls.size() - 1;
        // For each subsequent bucket, just push leaves into existing Node
        int i = MAX_BUCKET_LENGTH;
        for (; i < maxIdx - tailLen; i += MAX_BUCKET_LENGTH) {
            Object[] cpy = ls.subList(i, i + MAX_BUCKET_LENGTH).toArray();
//            System.out.println("Chunk of node size: " + cpy.length);
//            System.out.println("Chunk of node: " + Arrays.toString(cpy));
//            node = node.pushLeafArray(cpy);
        }
        // If we skip the above loop (when the input is too short), then i is correct for
        // what's below.  If we pop out of the loop after going around a few times, then i is
        // one too big.  Instead of doing something over and over inside the loop, just
        // correct i once here.
        if (i > MAX_BUCKET_LENGTH) {
            i = i - 1;
        }

//        System.out.println("final i: " + i);
        // Here it go boom!
//        System.out.println("ls.get(i): " + ls.get(i));
//        System.out.println("maxIdx: " + maxIdx);
        // The remainder goes into the tail.
        ls.subList(i, ls.size()).toArray();
//        System.out.println("Copied tail: " + Arrays.toString(newTail));
    }
}