package org.organicdesign.fp;

import org.junit.Test;
import org.organicdesign.fp.tuple.Tuple2;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.organicdesign.fp.StaticImports.uMap;
import static org.organicdesign.fp.StaticImports.uMapSkipNull;


public class StaticImportsTest {

    private static String[] ss = {"One","Two","Three","Four","Five","Six","Seven","Eight","Nine","Ten","Eleven",
            "Twelve","Thirteen","Fourteen","Fifteen","Sixteen","Seventeen","Eighteen","Nineteen","Twenty"};
    
    @Test public void testPreliminary() {
        assertEquals("One", uMap(1, "One").get(1));
        assertEquals("Two", uMap(1, "One", 2, "Two").get(2));
    }

    public void helper(Map<Integer,String> m, int max) {
        assertEquals("Size check", max, m.size());
        for (int i = 0; i < max; i++) {
            assertEquals(ss[i], m.get(i + 1));
        }
        assertNull(m.get(max + 1));
        assertNull(m.get(max + 999));
    }

    public void helperOdd(Map<Integer,String> m, int max) {
        assertEquals("Size check", (max + 1) / 2, m.size());
        for (int i = 0; i < max; i++) {
            if ( (i % 2) == 0 ) {
                assertEquals(ss[i], m.get(i + 1));
            } else {
                assertNull(m.get(i + 1));
            }
        }
        assertNull(m.get(max + 1));
        assertNull(m.get(max + 999));
    }

    public void helperEven(Map<Integer,String> m, int max) {
        assertEquals("Size check", max / 2, m.size());
        for (int i = 0; i < max; i++) {
            if ( (i % 2) == 0 ) {
                assertNull(m.get(i + 1));
            } else {
                assertEquals(ss[i], m.get(i + 1));
            }
        }
        assertNull(m.get(max + 1));
        assertNull(m.get(max + 999));
    }

    @Test public void test20() {
        Map<Integer,String> a = uMap(1, "One", 2, "Two", 3, "Three", 4, "Four", 5, "Five", 6, "Six", 7, "Seven",
                8, "Eight", 9, "Nine", 10, "Ten", 11, "Eleven", 12, "Twelve", 13, "Thirteen", 14, "Fourteen",
                15, "Fifteen", 16, "Sixteen", 17, "Seventeen", 18, "Eighteen", 19, "Nineteen", 20, "Twenty");
        int max = 20;
        helper(a, max);
        Map<Integer,String> b = uMapSkipNull(Tuple2.of(1, "One"), Tuple2.of(2, "Two"), Tuple2.of(3, "Three"),
                Tuple2.of(4, "Four"), Tuple2.of(5, "Five"), Tuple2.of(6, "Six"), Tuple2.of(7, "Seven"),
                Tuple2.of(8, "Eight"), Tuple2.of(9, "Nine"), Tuple2.of(10, "Ten"), Tuple2.of(11, "Eleven"),
                Tuple2.of(12, "Twelve"), Tuple2.of(13, "Thirteen"), Tuple2.of(14, "Fourteen"),
                Tuple2.of(15, "Fifteen"), Tuple2.of(16, "Sixteen"), Tuple2.of(17, "Seventeen"),
                Tuple2.of(18, "Eighteen"), Tuple2.of(19, "Nineteen"), Tuple2.of(20, "Twenty"));
        helper(b, max);
        assertEquals(a, b);
        assertEquals(b, a);
        assertEquals(a.hashCode(), b.hashCode());
        helperOdd(uMapSkipNull(Tuple2.of(1, "One"), null, Tuple2.of(3, "Three"), null, Tuple2.of(5, "Five"), null,
                Tuple2.of(7, "Seven"), null, Tuple2.of(9, "Nine"), null, Tuple2.of(11, "Eleven"), null,
                Tuple2.of(13, "Thirteen"), null, Tuple2.of(15, "Fifteen"), null, Tuple2.of(17, "Seventeen"), null,
                Tuple2.of(19, "Nineteen"), null), max);
        helperEven(uMapSkipNull(null, Tuple2.of(2, "Two"), null, Tuple2.of(4, "Four"), null, Tuple2.of(6, "Six"), null,
                Tuple2.of(8, "Eight"), null, Tuple2.of(10, "Ten"), null, Tuple2.of(12, "Twelve"), null,
                Tuple2.of(14, "Fourteen"), null, Tuple2.of(16, "Sixteen"), null, Tuple2.of(18, "Eighteen"), null,
                Tuple2.of(20, "Twenty")), max);
    }
    @Test public void test19() {
        Map<Integer,String> a = uMap(1, "One", 2, "Two", 3, "Three", 4, "Four", 5, "Five", 6, "Six", 7, "Seven",
                8, "Eight", 9, "Nine", 10, "Ten", 11, "Eleven", 12, "Twelve", 13, "Thirteen", 14, "Fourteen",
                15, "Fifteen", 16, "Sixteen", 17, "Seventeen", 18, "Eighteen", 19, "Nineteen");
        int max = 19;
        helper(a, max);
        Map<Integer,String> b = uMapSkipNull(Tuple2.of(1, "One"), Tuple2.of(2, "Two"), Tuple2.of(3, "Three"),
                Tuple2.of(4, "Four"), Tuple2.of(5, "Five"), Tuple2.of(6, "Six"), Tuple2.of(7, "Seven"),
                Tuple2.of(8, "Eight"), Tuple2.of(9, "Nine"), Tuple2.of(10, "Ten"), Tuple2.of(11, "Eleven"),
                Tuple2.of(12, "Twelve"), Tuple2.of(13, "Thirteen"), Tuple2.of(14, "Fourteen"),
                Tuple2.of(15, "Fifteen"), Tuple2.of(16, "Sixteen"), Tuple2.of(17, "Seventeen"),
                Tuple2.of(18, "Eighteen"), Tuple2.of(19, "Nineteen"));
        helper(b, max);
        assertEquals(a, b);
        assertEquals(b, a);
        assertEquals(a.hashCode(), b.hashCode());
        helperOdd(uMapSkipNull(Tuple2.of(1, "One"), null, Tuple2.of(3, "Three"), null, Tuple2.of(5, "Five"), null,
                Tuple2.of(7, "Seven"), null, Tuple2.of(9, "Nine"), null, Tuple2.of(11, "Eleven"), null,
                Tuple2.of(13, "Thirteen"), null, Tuple2.of(15, "Fifteen"), null, Tuple2.of(17, "Seventeen"), null,
                Tuple2.of(19, "Nineteen")), max);
        helperEven(uMapSkipNull(null, Tuple2.of(2, "Two"), null, Tuple2.of(4, "Four"), null, Tuple2.of(6, "Six"), null,
                Tuple2.of(8, "Eight"), null, Tuple2.of(10, "Ten"), null, Tuple2.of(12, "Twelve"), null,
                Tuple2.of(14, "Fourteen"), null, Tuple2.of(16, "Sixteen"), null, Tuple2.of(18, "Eighteen"), null), max);
    }
    @Test public void test18() {
        Map<Integer,String> a = uMap(1, "One", 2, "Two", 3, "Three", 4, "Four", 5, "Five", 6, "Six", 7, "Seven",
                8, "Eight", 9, "Nine", 10, "Ten", 11, "Eleven", 12, "Twelve", 13, "Thirteen", 14, "Fourteen",
                15, "Fifteen", 16, "Sixteen", 17, "Seventeen", 18, "Eighteen");
        int max = 18;
        helper(a, max);
        Map<Integer,String> b = uMapSkipNull(Tuple2.of(1, "One"), Tuple2.of(2, "Two"), Tuple2.of(3, "Three"),
                Tuple2.of(4, "Four"), Tuple2.of(5, "Five"), Tuple2.of(6, "Six"), Tuple2.of(7, "Seven"),
                Tuple2.of(8, "Eight"), Tuple2.of(9, "Nine"), Tuple2.of(10, "Ten"), Tuple2.of(11, "Eleven"),
                Tuple2.of(12, "Twelve"), Tuple2.of(13, "Thirteen"), Tuple2.of(14, "Fourteen"),
                Tuple2.of(15, "Fifteen"), Tuple2.of(16, "Sixteen"), Tuple2.of(17, "Seventeen"),
                Tuple2.of(18, "Eighteen"));
        helper(b, max);
        assertEquals(a, b);
        assertEquals(b, a);
        assertEquals(a.hashCode(), b.hashCode());
        helperOdd(uMapSkipNull(Tuple2.of(1, "One"), null, Tuple2.of(3, "Three"), null, Tuple2.of(5, "Five"), null,
                Tuple2.of(7, "Seven"), null, Tuple2.of(9, "Nine"), null, Tuple2.of(11, "Eleven"), null,
                Tuple2.of(13, "Thirteen"), null, Tuple2.of(15, "Fifteen"), null, Tuple2.of(17, "Seventeen"), null),
                max);
        helperEven(uMapSkipNull(null, Tuple2.of(2, "Two"), null, Tuple2.of(4, "Four"), null, Tuple2.of(6, "Six"), null,
                Tuple2.of(8, "Eight"), null, Tuple2.of(10, "Ten"), null, Tuple2.of(12, "Twelve"), null,
                Tuple2.of(14, "Fourteen"), null, Tuple2.of(16, "Sixteen"), null, Tuple2.of(18, "Eighteen")), max);
    }
    @Test public void test17() {
        Map<Integer,String> a = uMap(1, "One", 2, "Two", 3, "Three", 4, "Four", 5, "Five", 6, "Six", 7, "Seven",
                8, "Eight", 9, "Nine", 10, "Ten", 11, "Eleven", 12, "Twelve", 13, "Thirteen", 14, "Fourteen",
                15, "Fifteen", 16, "Sixteen", 17, "Seventeen");
        int max = 17;
        helper(a, max);
        Map<Integer,String> b = uMapSkipNull(Tuple2.of(1, "One"), Tuple2.of(2, "Two"), Tuple2.of(3, "Three"),
                Tuple2.of(4, "Four"), Tuple2.of(5, "Five"), Tuple2.of(6, "Six"), Tuple2.of(7, "Seven"),
                Tuple2.of(8, "Eight"), Tuple2.of(9, "Nine"), Tuple2.of(10, "Ten"), Tuple2.of(11, "Eleven"),
                Tuple2.of(12, "Twelve"), Tuple2.of(13, "Thirteen"), Tuple2.of(14, "Fourteen"),
                Tuple2.of(15, "Fifteen"), Tuple2.of(16, "Sixteen"), Tuple2.of(17, "Seventeen"));
        helper(b, max);
        assertEquals(a, b);
        assertEquals(b, a);
        assertEquals(a.hashCode(), b.hashCode());
        helperOdd(uMapSkipNull(Tuple2.of(1, "One"), null, Tuple2.of(3, "Three"), null, Tuple2.of(5, "Five"), null,
                Tuple2.of(7, "Seven"), null, Tuple2.of(9, "Nine"), null, Tuple2.of(11, "Eleven"), null,
                Tuple2.of(13, "Thirteen"), null, Tuple2.of(15, "Fifteen"), null, Tuple2.of(17, "Seventeen")), max);
        helperEven(uMapSkipNull(null, Tuple2.of(2, "Two"), null, Tuple2.of(4, "Four"), null, Tuple2.of(6, "Six"), null,
                Tuple2.of(8, "Eight"), null, Tuple2.of(10, "Ten"), null, Tuple2.of(12, "Twelve"), null,
                Tuple2.of(14, "Fourteen"), null, Tuple2.of(16, "Sixteen"), null), max);
    }
    @Test public void test16() {
        Map<Integer,String> a = uMap(1, "One", 2, "Two", 3, "Three", 4, "Four", 5, "Five", 6, "Six", 7, "Seven",
                8, "Eight", 9, "Nine", 10, "Ten", 11, "Eleven", 12, "Twelve", 13, "Thirteen", 14, "Fourteen",
                15, "Fifteen", 16, "Sixteen");
        int max = 16;
        helper(a, max);
        Map<Integer,String> b = uMapSkipNull(Tuple2.of(1, "One"), Tuple2.of(2, "Two"), Tuple2.of(3, "Three"),
                Tuple2.of(4, "Four"), Tuple2.of(5, "Five"), Tuple2.of(6, "Six"), Tuple2.of(7, "Seven"),
                Tuple2.of(8, "Eight"), Tuple2.of(9, "Nine"), Tuple2.of(10, "Ten"), Tuple2.of(11, "Eleven"),
                Tuple2.of(12, "Twelve"), Tuple2.of(13, "Thirteen"), Tuple2.of(14, "Fourteen"),
                Tuple2.of(15, "Fifteen"), Tuple2.of(16, "Sixteen"));
        helper(b, max);
        assertEquals(a, b);
        assertEquals(b, a);
        assertEquals(a.hashCode(), b.hashCode());
        helperOdd(uMapSkipNull(Tuple2.of(1, "One"), null, Tuple2.of(3, "Three"), null, Tuple2.of(5, "Five"), null,
                Tuple2.of(7, "Seven"), null, Tuple2.of(9, "Nine"), null, Tuple2.of(11, "Eleven"), null,
                Tuple2.of(13, "Thirteen"), null, Tuple2.of(15, "Fifteen"), null), max);
        helperEven(uMapSkipNull(null, Tuple2.of(2, "Two"), null, Tuple2.of(4, "Four"), null, Tuple2.of(6, "Six"), null,
                Tuple2.of(8, "Eight"), null, Tuple2.of(10, "Ten"), null, Tuple2.of(12, "Twelve"), null,
                Tuple2.of(14, "Fourteen"), null, Tuple2.of(16, "Sixteen")), max);
    }
    @Test public void test15() {
        Map<Integer,String> a = uMap(1, "One", 2, "Two", 3, "Three", 4, "Four", 5, "Five", 6, "Six", 7, "Seven",
                8, "Eight", 9, "Nine", 10, "Ten", 11, "Eleven", 12, "Twelve", 13, "Thirteen", 14, "Fourteen",
                15, "Fifteen");
        int max = 15;
        helper(a, max);
        Map<Integer,String> b = uMapSkipNull(Tuple2.of(1, "One"), Tuple2.of(2, "Two"), Tuple2.of(3, "Three"),
                Tuple2.of(4, "Four"), Tuple2.of(5, "Five"), Tuple2.of(6, "Six"), Tuple2.of(7, "Seven"),
                Tuple2.of(8, "Eight"), Tuple2.of(9, "Nine"), Tuple2.of(10, "Ten"), Tuple2.of(11, "Eleven"),
                Tuple2.of(12, "Twelve"), Tuple2.of(13, "Thirteen"), Tuple2.of(14, "Fourteen"),
                Tuple2.of(15, "Fifteen"));
        helper(b, max);
        assertEquals(a, b);
        assertEquals(b, a);
        assertEquals(a.hashCode(), b.hashCode());
        helperOdd(uMapSkipNull(Tuple2.of(1, "One"), null, Tuple2.of(3, "Three"), null, Tuple2.of(5, "Five"), null,
                Tuple2.of(7, "Seven"), null, Tuple2.of(9, "Nine"), null, Tuple2.of(11, "Eleven"), null,
                Tuple2.of(13, "Thirteen"), null, Tuple2.of(15, "Fifteen")), max);
        helperEven(uMapSkipNull(null, Tuple2.of(2, "Two"), null, Tuple2.of(4, "Four"), null, Tuple2.of(6, "Six"), null,
                Tuple2.of(8, "Eight"), null, Tuple2.of(10, "Ten"), null, Tuple2.of(12, "Twelve"), null,
                Tuple2.of(14, "Fourteen"), null), max);
    }
    @Test public void test14() {
        Map<Integer,String> a = uMap(1, "One", 2, "Two", 3, "Three", 4, "Four", 5, "Five", 6, "Six", 7, "Seven",
                8, "Eight", 9, "Nine", 10, "Ten", 11, "Eleven", 12, "Twelve", 13, "Thirteen", 14, "Fourteen");
        int max = 14;
        helper(a, max);
        Map<Integer,String> b = uMapSkipNull(Tuple2.of(1, "One"), Tuple2.of(2, "Two"), Tuple2.of(3, "Three"),
                Tuple2.of(4, "Four"), Tuple2.of(5, "Five"), Tuple2.of(6, "Six"), Tuple2.of(7, "Seven"),
                Tuple2.of(8, "Eight"), Tuple2.of(9, "Nine"), Tuple2.of(10, "Ten"), Tuple2.of(11, "Eleven"),
                Tuple2.of(12, "Twelve"), Tuple2.of(13, "Thirteen"), Tuple2.of(14, "Fourteen"));
        helper(b, max);
        assertEquals(a, b);
        assertEquals(b, a);
        assertEquals(a.hashCode(), b.hashCode());
        helperOdd(uMapSkipNull(Tuple2.of(1, "One"), null, Tuple2.of(3, "Three"), null, Tuple2.of(5, "Five"), null,
                Tuple2.of(7, "Seven"), null, Tuple2.of(9, "Nine"), null, Tuple2.of(11, "Eleven"), null,
                Tuple2.of(13, "Thirteen"), null), max);
        helperEven(uMapSkipNull(null, Tuple2.of(2, "Two"), null, Tuple2.of(4, "Four"), null, Tuple2.of(6, "Six"), null,
                Tuple2.of(8, "Eight"), null, Tuple2.of(10, "Ten"), null, Tuple2.of(12, "Twelve"), null,
                Tuple2.of(14, "Fourteen")), max);
    }
    @Test public void test13() {
        Map<Integer,String> a = uMap(1, "One", 2, "Two", 3, "Three", 4, "Four", 5, "Five", 6, "Six", 7, "Seven",
                8, "Eight", 9, "Nine", 10, "Ten", 11, "Eleven", 12, "Twelve", 13, "Thirteen");
        int max = 13;
        helper(a, max);
        Map<Integer,String> b = uMapSkipNull(Tuple2.of(1, "One"), Tuple2.of(2, "Two"), Tuple2.of(3, "Three"),
                Tuple2.of(4, "Four"), Tuple2.of(5, "Five"), Tuple2.of(6, "Six"), Tuple2.of(7, "Seven"),
                Tuple2.of(8, "Eight"), Tuple2.of(9, "Nine"), Tuple2.of(10, "Ten"), Tuple2.of(11, "Eleven"),
                Tuple2.of(12, "Twelve"), Tuple2.of(13, "Thirteen"));
        helper(b, max);
        assertEquals(a, b);
        assertEquals(b, a);
        assertEquals(a.hashCode(), b.hashCode());
        helperOdd(uMapSkipNull(Tuple2.of(1, "One"), null, Tuple2.of(3, "Three"), null, Tuple2.of(5, "Five"), null,
                Tuple2.of(7, "Seven"), null, Tuple2.of(9, "Nine"), null, Tuple2.of(11, "Eleven"), null,
                Tuple2.of(13, "Thirteen")), max);
        helperEven(uMapSkipNull(null, Tuple2.of(2, "Two"), null, Tuple2.of(4, "Four"), null, Tuple2.of(6, "Six"), null,
                Tuple2.of(8, "Eight"), null, Tuple2.of(10, "Ten"), null, Tuple2.of(12, "Twelve"), null), max);
    }
    @Test public void test12() {
        Map<Integer,String> a = uMap(1, "One", 2, "Two", 3, "Three", 4, "Four", 5, "Five", 6, "Six", 7, "Seven",
                8, "Eight", 9, "Nine", 10, "Ten", 11, "Eleven", 12, "Twelve");
        int max = 12;
        helper(a, max);
        Map<Integer,String> b = uMapSkipNull(Tuple2.of(1, "One"), Tuple2.of(2, "Two"), Tuple2.of(3, "Three"),
                Tuple2.of(4, "Four"), Tuple2.of(5, "Five"), Tuple2.of(6, "Six"), Tuple2.of(7, "Seven"),
                Tuple2.of(8, "Eight"), Tuple2.of(9, "Nine"), Tuple2.of(10, "Ten"), Tuple2.of(11, "Eleven"),
                Tuple2.of(12, "Twelve"));
        helper(b, max);
        assertEquals(a, b);
        assertEquals(b, a);
        assertEquals(a.hashCode(), b.hashCode());
        helperOdd(uMapSkipNull(Tuple2.of(1, "One"), null, Tuple2.of(3, "Three"), null, Tuple2.of(5, "Five"), null,
                Tuple2.of(7, "Seven"), null, Tuple2.of(9, "Nine"), null, Tuple2.of(11, "Eleven"), null), max);
        helperEven(uMapSkipNull(null, Tuple2.of(2, "Two"), null, Tuple2.of(4, "Four"), null, Tuple2.of(6, "Six"), null,
                Tuple2.of(8, "Eight"), null, Tuple2.of(10, "Ten"), null, Tuple2.of(12, "Twelve")), max);
    }
    @Test public void test11() {
        Map<Integer,String> a = uMap(1, "One", 2, "Two", 3, "Three", 4, "Four", 5, "Five", 6, "Six", 7, "Seven",
                8, "Eight", 9, "Nine", 10, "Ten", 11, "Eleven");
        int max = 11;
        helper(a, max);
        Map<Integer,String> b = uMapSkipNull(Tuple2.of(1, "One"), Tuple2.of(2, "Two"), Tuple2.of(3, "Three"),
                Tuple2.of(4, "Four"), Tuple2.of(5, "Five"), Tuple2.of(6, "Six"), Tuple2.of(7, "Seven"),
                Tuple2.of(8, "Eight"), Tuple2.of(9, "Nine"), Tuple2.of(10, "Ten"), Tuple2.of(11, "Eleven"));
        helper(b, max);
        assertEquals(a, b);
        assertEquals(b, a);
        assertEquals(a.hashCode(), b.hashCode());
        helperOdd(uMapSkipNull(Tuple2.of(1, "One"), null, Tuple2.of(3, "Three"), null, Tuple2.of(5, "Five"), null,
                Tuple2.of(7, "Seven"), null, Tuple2.of(9, "Nine"), null, Tuple2.of(11, "Eleven")), max);
        helperEven(uMapSkipNull(null, Tuple2.of(2, "Two"), null, Tuple2.of(4, "Four"), null, Tuple2.of(6, "Six"), null,
                Tuple2.of(8, "Eight"), null, Tuple2.of(10, "Ten"), null), max);
    }
    @Test public void test10() {
        Map<Integer,String> a = uMap(1, "One", 2, "Two", 3, "Three", 4, "Four", 5, "Five", 6, "Six", 7, "Seven",
                8, "Eight", 9, "Nine", 10, "Ten");
        int max = 10;
        helper(a, max);
        Map<Integer,String> b = uMapSkipNull( Tuple2.of(1, "One"), Tuple2.of(2, "Two"), Tuple2.of(3, "Three"),
                Tuple2.of(4, "Four"), Tuple2.of(5, "Five"), Tuple2.of(6, "Six"), Tuple2.of(7, "Seven"),
                Tuple2.of(8, "Eight"), Tuple2.of(9, "Nine"), Tuple2.of(10, "Ten"));
        helper(b, max);
        assertEquals(a, b);
        assertEquals(b, a);
        assertEquals(a.hashCode(), b.hashCode());
        helperOdd(uMapSkipNull(Tuple2.of(1, "One"), null, Tuple2.of(3, "Three"), null, Tuple2.of(5, "Five"), null,
                Tuple2.of(7, "Seven"), null, Tuple2.of(9, "Nine"), null), max);
        helperEven(uMapSkipNull(null, Tuple2.of(2, "Two"), null, Tuple2.of(4, "Four"), null, Tuple2.of(6, "Six"), null,
                Tuple2.of(8, "Eight"), null, Tuple2.of(10, "Ten")), max);
    }
    @Test public void test9() {
        Map<Integer,String> a = uMap( 1, "One", 2, "Two", 3, "Three", 4, "Four", 5, "Five", 6, "Six", 7, "Seven",
                8, "Eight", 9, "Nine");
        int max = 9;
        helper(a, max);
        Map<Integer,String> b = uMapSkipNull( Tuple2.of(1, "One"), Tuple2.of(2, "Two"), Tuple2.of(3, "Three"),
                Tuple2.of(4, "Four"), Tuple2.of(5, "Five"), Tuple2.of(6, "Six"), Tuple2.of(7, "Seven"),
                Tuple2.of(8, "Eight"), Tuple2.of(9, "Nine"));
        helper(b, max);
        assertEquals(a, b);
        assertEquals(b, a);
        assertEquals(a.hashCode(), b.hashCode());
        helperOdd(uMapSkipNull(Tuple2.of(1, "One"), null, Tuple2.of(3, "Three"), null, Tuple2.of(5, "Five"), null,
                Tuple2.of(7, "Seven"), null, Tuple2.of(9, "Nine")), max);
        helperEven(uMapSkipNull(null, Tuple2.of(2, "Two"), null, Tuple2.of(4, "Four"), null, Tuple2.of(6, "Six"), null,
                Tuple2.of(8, "Eight"), null), max);
    }
    @Test public void test8() {
        Map<Integer,String> a = uMap( 1, "One", 2, "Two", 3, "Three", 4, "Four", 5, "Five", 6, "Six", 7, "Seven",
                8, "Eight");
        int max = 8;
        helper(a, max);
        Map<Integer,String> b = uMapSkipNull( Tuple2.of(1, "One"), Tuple2.of(2, "Two"), Tuple2.of(3, "Three"),
                Tuple2.of(4, "Four"), Tuple2.of(5, "Five"), Tuple2.of(6, "Six"), Tuple2.of(7, "Seven"),
                Tuple2.of(8, "Eight"));
        helper(b, max);
        assertEquals(a, b);
        assertEquals(b, a);
        assertEquals(a.hashCode(), b.hashCode());
        helperOdd(uMapSkipNull(Tuple2.of(1, "One"), null, Tuple2.of(3, "Three"), null, Tuple2.of(5, "Five"), null,
                Tuple2.of(7, "Seven"), null), max);
        helperEven(uMapSkipNull(null, Tuple2.of(2, "Two"), null, Tuple2.of(4, "Four"), null, Tuple2.of(6, "Six"), null,
                Tuple2.of(8, "Eight")), max);
    }
    @Test public void test7() {
        Map<Integer,String> a = uMap( 1, "One", 2, "Two", 3, "Three", 4, "Four", 5, "Five", 6, "Six", 7, "Seven");
        int max = 7;
        helper(a, max);
        Map<Integer,String> b = uMapSkipNull( Tuple2.of(1, "One"), Tuple2.of(2, "Two"), Tuple2.of(3, "Three"),
                Tuple2.of(4, "Four"), Tuple2.of(5, "Five"), Tuple2.of(6, "Six"), Tuple2.of(7, "Seven"));
        helper(b, max);
        assertEquals(a, b);
        assertEquals(b, a);
        assertEquals(a.hashCode(), b.hashCode());
        helperOdd(uMapSkipNull(Tuple2.of(1, "One"), null, Tuple2.of(3, "Three"), null, Tuple2.of(5, "Five"), null,
                Tuple2.of(7, "Seven")), max);
        helperEven(uMapSkipNull(null, Tuple2.of(2, "Two"), null, Tuple2.of(4, "Four"), null, Tuple2.of(6, "Six"),
                null), max);
    }
    @Test public void test6() {
        Map<Integer,String> a = uMap( 1, "One", 2, "Two", 3, "Three", 4, "Four", 5, "Five", 6, "Six");
        int max = 6;
        helper(a, max);
        Map<Integer,String> b = uMapSkipNull( Tuple2.of(1, "One"), Tuple2.of(2, "Two"), Tuple2.of(3, "Three"),
                Tuple2.of(4, "Four"), Tuple2.of(5, "Five"), Tuple2.of(6, "Six"));
        helper(b, max);
        assertEquals(a, b);
        assertEquals(b, a);
        assertEquals(a.hashCode(), b.hashCode());
        helperOdd(uMapSkipNull(Tuple2.of(1, "One"), null, Tuple2.of(3, "Three"), null, Tuple2.of(5, "Five"), null),
                max);
        helperEven(uMapSkipNull(null, Tuple2.of(2, "Two"), null, Tuple2.of(4, "Four"), null, Tuple2.of(6, "Six")), max);
    }
    @Test public void test5() {
        Map<Integer,String> a = uMap( 1, "One", 2, "Two", 3, "Three", 4, "Four", 5, "Five");
        int max = 5;
        helper(a, max);
        Map<Integer,String> b = uMapSkipNull( Tuple2.of(1, "One"), Tuple2.of(2, "Two"), Tuple2.of(3, "Three"),
                Tuple2.of(4, "Four"), Tuple2.of(5, "Five"));
        helper(b, max);
        assertEquals(a, b);
        assertEquals(b, a);
        assertEquals(a.hashCode(), b.hashCode());
        helperOdd(uMapSkipNull(Tuple2.of(1, "One"), null, Tuple2.of(3, "Three"), null, Tuple2.of(5, "Five")), max);
        helperEven(uMapSkipNull(null, Tuple2.of(2, "Two"), null, Tuple2.of(4, "Four"), null), max);
    }
    @Test public void test4() {
        Map<Integer,String> a = uMap( 1, "One", 2, "Two", 3, "Three", 4, "Four");
        int max = 4;
        helper(a, max);
        Map<Integer,String> b = uMapSkipNull( Tuple2.of(1, "One"), Tuple2.of(2, "Two"), Tuple2.of(3, "Three"),
                Tuple2.of(4, "Four"));
        helper(b, max);
        assertEquals(a, b);
        assertEquals(b, a);
        assertEquals(a.hashCode(), b.hashCode());
        helperOdd(uMapSkipNull(Tuple2.of(1, "One"), null, Tuple2.of(3, "Three"), null), max);
        helperEven(uMapSkipNull(null, Tuple2.of(2, "Two"), null, Tuple2.of(4, "Four")), max);
    }
    @Test public void test3() {
        Map<Integer,String> a = uMap( 1, "One", 2, "Two", 3, "Three");
        int max = 3;
        helper(a, max);
        Map<Integer,String> b = uMapSkipNull( Tuple2.of(1, "One"), Tuple2.of(2, "Two"), Tuple2.of(3, "Three"));
        helper(b, max);
        assertEquals(a, b);
        assertEquals(b, a);
        assertEquals(a.hashCode(), b.hashCode());
        helperOdd(uMapSkipNull(Tuple2.of(1, "One"), null, Tuple2.of(3, "Three")), max);
        helperEven(uMapSkipNull(null, Tuple2.of(2, "Two"), null), max);
    }
    @Test public void test2() {
        Map<Integer,String> a = uMap( 1, "One", 2, "Two");
        int max = 2;
        helper(a, max);
        Map<Integer,String> b = uMapSkipNull( Tuple2.of(1, "One"), Tuple2.of(2, "Two"));
        helper(b, max);
        assertEquals(a, b);
        assertEquals(b, a);
        assertEquals(a.hashCode(), b.hashCode());
        helperOdd(uMapSkipNull(Tuple2.of(1, "One"), null), max);
        helperEven(uMapSkipNull(null, Tuple2.of(2, "Two")), max);
    }
    @Test public void test1() {
        Map<Integer,String> a = uMap(1, "One");
        int max = 1;
        helper(a, max);
        Map<Integer,String> b = uMapSkipNull(Tuple2.of(1, "One"));
        helper(b, max);
        assertEquals(a, b);
        assertEquals(b, a);
        assertEquals(a.hashCode(), b.hashCode());
        helperOdd(uMapSkipNull(Tuple2.of(1, "One")), max);
        helperEven(uMapSkipNull(null), max);
        helperEven(uMapSkipNull(null,null,null,null,null,null,null,null), max);
    }
    @Test public void test0() {
        Map<Integer,String> a = uMap();
        int max = 0;
        helper(a, max);
    }

//    @Test public void testLazyHashcoder() {
//        int a = 0b1;
//        int b = 0b10;
//        int c = 0b100;
//        int d = 0b1000;
//        int e = 0b10000;
//        Lazy.Int hc = lazyHashCoder(a, b, c, d, e);
//        assertEquals(0b11111, hc.get());
//        a = 999;
//        assertEquals(0b11111, hc.get());
//        a = 0b1;
//        assertEquals(0b10001, lazyHashCoder(a, null, null, null, e).get());
//        assertEquals(0b01110, lazyHashCoder(null, b, c, d, null).get());
//    }
}
