package org.organicdesign.fp.collections;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.organicdesign.fp.TestUtilities;
import org.organicdesign.fp.collections.RangeOfInt.Equat;
import org.organicdesign.testUtils.EqualsContract;

import static org.junit.Assert.*;
import static org.organicdesign.fp.TestUtilities.serializeDeserialize;

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
        TestUtilities.listIteratorTest(Arrays.asList(-2, -1, 0, 1, 2, 3, 4),
                                       RangeOfInt.of(-2, 5));
    }

    @Test public void equatorTest() {
        List<Integer> a = Arrays.asList(-2, -1, 0, 1, 2, 3, 4);
        List<Integer> b = RangeOfInt.of(-2, 5);
        assertEquals(Equat.LIST.hash(a),
                     Equat.LIST.hash(b));
        assertEquals(a.size(), b.size());

        assertTrue(UnmodSortedIterable.equals(UnmodSortedIterable.castFromList(a),
                                              UnmodSortedIterable.castFromList(b)));

        assertTrue("List and range are equal", Equat.LIST.eq(a, b));

        assertTrue("List equal to self", Equat.LIST.eq(a, a));

        assertTrue("Range equal to self", Equat.LIST.eq(b, b));

        assertTrue("Range equal to different Range", Equat.LIST.eq(b, RangeOfInt.of(-2, 5)));

        // Is this a good idea?
        assertTrue("Null equal to self", Equat.LIST.eq(null, null));

        assertTrue("Range and range are equal", Equat.LIST.eq(RangeOfInt.of(-2, 5),
                                                                           b));

        assertFalse("Not equal to null", Equat.LIST.eq(a, null));
        assertFalse("Not equal to null", Equat.LIST.eq(null, a));

        assertFalse("Not equal to different Range", Equat.LIST.eq(a, RangeOfInt.of(-3, 4)));
        assertFalse("Not equal to different Range", Equat.LIST.eq(b, RangeOfInt.of(-2, 4)));
        assertFalse("Not equal to different Range", Equat.LIST.eq(RangeOfInt.of(-3, 4), b));
        assertFalse("Not equal to different Range", Equat.LIST.eq(RangeOfInt.of(-3, 5), a));
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    @Test public void indexOfTest() {
        List<Integer> a = Arrays.asList(-2, -1, 0, 1, 2, 3, 4);
        List<Integer> b = RangeOfInt.of(-2, 5);

        assertEquals(a.size(), b.size());

        for (int i : a) {
            assertEquals(a.indexOf(i), b.indexOf(i));
            assertEquals(a.lastIndexOf(i), b.lastIndexOf(i));
        }

        // List can't take a Long as an index for some reason, but we can.
        int i = 0;
        for (long l : a) {
            assertEquals(i, b.indexOf(Long.valueOf(l)));
            assertEquals(i, b.lastIndexOf(Long.valueOf(l)));
            i = i + 1;
        }

        assertEquals(a.indexOf(Integer.MAX_VALUE), b.indexOf(Integer.MAX_VALUE));
        assertEquals(a.indexOf(Integer.MIN_VALUE), b.indexOf(Integer.MIN_VALUE));
        assertEquals(a.indexOf(a.size()), b.indexOf(b.size()));
        assertEquals(a.lastIndexOf(Integer.MAX_VALUE), b.lastIndexOf(Integer.MAX_VALUE));

        assertEquals(a.indexOf("Hullabaloo"), b.indexOf("Hullabaloo"));
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

        assertEquals(Equat.LIST.hash(sla),
                     Equat.LIST.hash(slb));
        assertTrue(Equat.LIST.eq(sla, slb));

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

        sla = a.subList(1, a.size());
        slb = b.subList(1, b.size());

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

    @Test(expected = IllegalArgumentException.class)
    public void factoryEx1() { RangeOfInt.of(null); }

    @Test(expected = IllegalArgumentException.class)
    public void factoryEx2() { RangeOfInt.of(-1); }

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

    @Test public void serializationTest() throws Exception {
        List<Integer> a = Arrays.asList(-2, -1, 0, 1, 2, 3, 4);
        List<Integer> b = RangeOfInt.of(-2, 5);
        Equator<List<Integer>> deserEq = serializeDeserialize(Equat.LIST);
        assertEquals(Equat.LIST.hash(a), deserEq.hash(a));
        assertEquals(Equat.LIST.hash(b), deserEq.hash(b));
        assertEquals(deserEq.hash(a), deserEq.hash(b));
        assertEquals(a, b);
        assertEquals(a.size(), b.size());
        assertEquals(a, serializeDeserialize(b));
        assertEquals(a.size(), serializeDeserialize(b).size());

        assertTrue(UnmodSortedIterable.equals(UnmodSortedIterable.castFromList(a),
                                              UnmodSortedIterable.castFromList(b)));

        assertTrue("List and range are equal", deserEq.eq(a, b));

        assertTrue("List equal to self", deserEq.eq(a, a));

        assertTrue("Range equal to self", deserEq.eq(b, b));

        assertTrue("Range equal to different Range", deserEq.eq(b, RangeOfInt.of(-2, 5)));

        // Is this a good idea?
        assertTrue("Null equal to self", deserEq.eq(null, null));

        assertTrue("Range and range are equal", deserEq.eq(RangeOfInt.of(-2, 5),
                                                                           b));

        assertFalse("Not equal to null", deserEq.eq(a, null));
        assertFalse("Not equal to null", deserEq.eq(null, a));

        assertFalse("Not equal to different Range", deserEq.eq(a, RangeOfInt.of(-3, 4)));
        assertFalse("Not equal to different Range", deserEq.eq(b, RangeOfInt.of(-2, 4)));
        assertFalse("Not equal to different Range", deserEq.eq(RangeOfInt.of(-3, 4), b));
        assertFalse("Not equal to different Range", deserEq.eq(RangeOfInt.of(-3, 5), a));

        RangeOfInt ir1 = serializeDeserialize(RangeOfInt.of(0, 1));
        assertEquals(ir1.contains(0), true);
        assertEquals(ir1.contains(1), false);
        assertEquals(ir1.contains(-1), false);
        assertEquals(ir1.size(), 1);

        List<Integer> a2 = Collections.singletonList(99);
        List<Integer> b2 = serializeDeserialize(RangeOfInt.of(99, 100));
        assertEquals(a2.size(), b2.size());
        assertEquals(a2.get(0), b2.get(0));
    }
}