// Copyright (c) 2014-03-06 PlanBase Inc. & Glen Peterson
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless r==uired by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.organicdesign.fp;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.organicdesign.fp.collections.ImList;
import org.organicdesign.fp.collections.ImMap;
import org.organicdesign.fp.collections.ImSet;
import org.organicdesign.fp.collections.ImSortedMap;
import org.organicdesign.fp.collections.ImSortedSet;
import org.organicdesign.fp.collections.UnmodCollection;
import org.organicdesign.fp.collections.UnmodList;
import org.organicdesign.fp.collections.UnmodListTest;
import org.organicdesign.fp.collections.UnmodMap;
import org.organicdesign.fp.collections.UnmodSet;
import org.organicdesign.fp.collections.UnmodSortedMap;
import org.organicdesign.fp.collections.UnmodSortedSet;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.*;
import static org.organicdesign.fp.FunctionUtils.*;
import static org.organicdesign.fp.StaticImports.*;
import static org.organicdesign.fp.testUtils.EqualsContract.equalsDistinctHashCode;

@RunWith(JUnit4.class)
public class FunctionUtilsTest {

    @Test (expected = UnsupportedOperationException.class)
    public void instantiationEx() throws Throwable {
        Class<FunctionUtils> c = FunctionUtils.class;
        Constructor defCons = c.getDeclaredConstructor();
        defCons.setAccessible(true);
        try {
            // This catches the exception and wraps it in an InvocationTargetException
            defCons.newInstance();
        } catch (InvocationTargetException ite) {
            // Here we throw the original exception.
            throw ite.getTargetException();
        }
    }

    @Test
    public void testToString() {
        List<Integer> is = new ArrayList<>();
        is.add(1);
        is.add(2);
        is.add(3);
        is.add(4);
        is.add(5);
        assertEquals("Array<Integer>(1,2,3,4,5)", FunctionUtils.arrayToString(is.toArray()));

        is.add(6);
        assertEquals("Array<Integer>(1,2,3,4,5,...)", FunctionUtils.arrayToString(is.toArray()));

        Map<String,Integer> m = new TreeMap<>();
        m.put("Hello", 99);
        m.put("World", -237);
        assertEquals("TreeMap(Entry(Hello,99),Entry(World,-237))", FunctionUtils.mapToString(m));

        m.put("x", 3);
        m.put("y", 2);
        m.put("z", 1);
        m.put("zz", 0);

        assertEquals("TreeMap(Entry(Hello,99),Entry(World,-237),Entry(x,3),Entry(y,2),Entry(z,1),...)",
                     FunctionUtils.mapToString(m));

        assertEquals("Array()", FunctionUtils.arrayToString(new Integer[0]));
        assertEquals("Array(null)", FunctionUtils.arrayToString(new Integer[] {null}));

    }

    @SuppressWarnings({"ConstantConditions","Unchecked"})
    @Test public void testToStringNull() {
        assertEquals("null", FunctionUtils.mapToString(null));
        Integer[] zs = null;
        assertEquals("null", FunctionUtils.arrayToString(zs));
    }

//    @SuppressWarnings("Convert2Lambda")
//    public static final Predicate<Integer> r = new Predicate<Integer>() {
//        @Override
//        public boolean test(Integer i) {
//            return i < -1;
//        }
//    };
//
//    @Test
//    public void goBoom2() {
//        Predicate<Integer> p = i -> i > 0;
//
//        @SuppressWarnings("Convert2Lambda")
//        Predicate<Integer> q = new Predicate<Integer>() {
//            @Override
//            public boolean test(Integer i) {
//                return i < 0;
//            }
//        };
//
//        boolean q1 = p == q;
//        boolean q2 = Transformable.USED_UP == new Object();
//        boolean q3 = new Object() == Transformable.USED_UP;
//        boolean q4 = p == new Object();
//        boolean q5 = q != Transformable.USED_UP;
//        boolean q6 = p != Transformable.USED_UP;
//        Transformable.isUsedUp(p);
//        Transformable.isUsedUp(q);
//        Transformable.isUsedUp(FunctionUtils.REJECT);
//        boolean q7 = FunctionUtils.REJECT == FunctionUtils.ACCEPT;
//        boolean q8 = q == r;
////        boolean q7 = p != FunctionUtils.ACCEPT;
////        boolean q7 = ((Predicate<Object>) p) != FunctionUtils.accept();
//    }
//
////    @Test
////    public void goBoom1() {
////        Predicate<Integer> p = i -> i > 0;
////        throw new IllegalStateException("Type of p is: " + p.getClass().getCanonicalName());
////    }

    @Test public void testOrdinal() {
        assertTrue("0th".equals(ordinal(0)));
        assertTrue("1st".equals(ordinal(1))); // st
        assertTrue("2nd".equals(ordinal(2))); // nd
        assertTrue("3rd".equals(ordinal(3))); // rd
        assertTrue("4th".equals(ordinal(4)));
        assertTrue("5th".equals(ordinal(5)));
        assertTrue("6th".equals(ordinal(6)));
        assertTrue("7th".equals(ordinal(7)));
        assertTrue("8th".equals(ordinal(8)));
        assertTrue("9th".equals(ordinal(9)));
        assertTrue("10th".equals(ordinal(10)));
        assertTrue("11th".equals(ordinal(11)));
        assertTrue("12th".equals(ordinal(12)));
        assertTrue("13th".equals(ordinal(13)));
        assertTrue("14th".equals(ordinal(14)));
        assertTrue("15th".equals(ordinal(15)));
        assertTrue("16th".equals(ordinal(16)));
        assertTrue("17th".equals(ordinal(17)));
        assertTrue("18th".equals(ordinal(18)));
        assertTrue("19th".equals(ordinal(19)));
        assertTrue("20th".equals(ordinal(20)));
        assertTrue("21st".equals(ordinal(21))); // st
        assertTrue("22nd".equals(ordinal(22))); // nd
        assertTrue("23rd".equals(ordinal(23))); // rd
        assertTrue("24th".equals(ordinal(24)));
        assertTrue("25th".equals(ordinal(25)));
        assertTrue("26th".equals(ordinal(26)));
        assertTrue("27th".equals(ordinal(27)));
        assertTrue("28th".equals(ordinal(28)));
        assertTrue("29th".equals(ordinal(29)));
        assertTrue("30th".equals(ordinal(30)));

        assertTrue("51st".equals(ordinal(51))); // st

        assertTrue("62nd".equals(ordinal(62))); // nd

        assertTrue("73rd".equals(ordinal(73)));

        assertTrue("84th".equals(ordinal(84)));

        assertTrue("95th".equals(ordinal(95)));

        assertTrue("100th".equals(ordinal(100)));
        assertTrue("101st".equals(ordinal(101))); // st
        assertTrue("102nd".equals(ordinal(102))); // nd
        assertTrue("103rd".equals(ordinal(103))); // rd
        assertTrue("104th".equals(ordinal(104)));
        assertTrue("105th".equals(ordinal(105)));
        assertTrue("106th".equals(ordinal(106)));
        assertTrue("107th".equals(ordinal(107)));
        assertTrue("108th".equals(ordinal(108)));
        assertTrue("109th".equals(ordinal(109)));
        assertTrue("110th".equals(ordinal(110)));
        assertTrue("111th".equals(ordinal(111)));
        assertTrue("112th".equals(ordinal(112)));
        assertTrue("113th".equals(ordinal(113)));
        assertTrue("114th".equals(ordinal(114)));
        assertTrue("115th".equals(ordinal(115)));
        assertTrue("116th".equals(ordinal(116)));
        assertTrue("117th".equals(ordinal(117)));
        assertTrue("118th".equals(ordinal(118)));
        assertTrue("119th".equals(ordinal(119)));
        assertTrue("120th".equals(ordinal(120)));
        assertTrue("121st".equals(ordinal(121))); // st
        assertTrue("122nd".equals(ordinal(122))); // nd
        assertTrue("123rd".equals(ordinal(123))); // rd
        assertTrue("124th".equals(ordinal(124)));
        assertTrue("125th".equals(ordinal(125)));
        assertTrue("126th".equals(ordinal(126)));
        assertTrue("127th".equals(ordinal(127)));
        assertTrue("128th".equals(ordinal(128)));
        assertTrue("129th".equals(ordinal(129)));
        assertTrue("130th".equals(ordinal(130)));

        // Close to maximum int...
        assertTrue("2147483640th".equals(ordinal(2147483640)));
        assertTrue("2147483641st".equals(ordinal(2147483641)));
        assertTrue("2147483642nd".equals(ordinal(2147483642)));
        assertTrue("2147483643rd".equals(ordinal(2147483643)));
        assertTrue("2147483644th".equals(ordinal(2147483644)));

        // Maximum int...
        assertTrue("2147483647th".equals(ordinal(2147483647)));

        // Negative numbers - don't know how practical it is, but might as well be thorough.
        assertTrue("-1st".equals(ordinal(-1))); // st
        assertTrue("-2nd".equals(ordinal(-2))); // nd
        assertTrue("-3rd".equals(ordinal(-3))); // rd
        assertTrue("-4th".equals(ordinal(-4)));
        assertTrue("-5th".equals(ordinal(-5)));
        assertTrue("-6th".equals(ordinal(-6)));
        assertTrue("-7th".equals(ordinal(-7)));
        assertTrue("-8th".equals(ordinal(-8)));
        assertTrue("-9th".equals(ordinal(-9)));
        assertTrue("-10th".equals(ordinal(-10)));
        assertTrue("-11th".equals(ordinal(-11)));
        assertTrue("-12th".equals(ordinal(-12)));
        assertTrue("-13th".equals(ordinal(-13)));
        assertTrue("-14th".equals(ordinal(-14)));
        assertTrue("-15th".equals(ordinal(-15)));
        assertTrue("-16th".equals(ordinal(-16)));
        assertTrue("-17th".equals(ordinal(-17)));
        assertTrue("-18th".equals(ordinal(-18)));
        assertTrue("-19th".equals(ordinal(-19)));
        assertTrue("-20th".equals(ordinal(-20)));
        assertTrue("-21st".equals(ordinal(-21))); // st
        assertTrue("-22nd".equals(ordinal(-22))); // nd
        assertTrue("-23rd".equals(ordinal(-23))); // rd
        assertTrue("-24th".equals(ordinal(-24)));
        assertTrue("-25th".equals(ordinal(-25)));
        assertTrue("-26th".equals(ordinal(-26)));
        assertTrue("-27th".equals(ordinal(-27)));
        assertTrue("-28th".equals(ordinal(-28)));
        assertTrue("-29th".equals(ordinal(-29)));
        assertTrue("-30th".equals(ordinal(-30)));

        assertTrue("-51st".equals(ordinal(-51))); // st

        assertTrue("-62nd".equals(ordinal(-62))); // nd

        assertTrue("-73rd".equals(ordinal(-73)));

        assertTrue("-84th".equals(ordinal(-84)));

        assertTrue("-95th".equals(ordinal(-95)));

        assertTrue("-100th".equals(ordinal(-100)));
        assertTrue("-101st".equals(ordinal(-101))); // st
        assertTrue("-102nd".equals(ordinal(-102))); // nd
        assertTrue("-103rd".equals(ordinal(-103))); // rd
        assertTrue("-104th".equals(ordinal(-104)));
        assertTrue("-105th".equals(ordinal(-105)));
        assertTrue("-106th".equals(ordinal(-106)));
        assertTrue("-107th".equals(ordinal(-107)));
        assertTrue("-108th".equals(ordinal(-108)));
        assertTrue("-109th".equals(ordinal(-109)));
        assertTrue("-110th".equals(ordinal(-110)));
        assertTrue("-111th".equals(ordinal(-111)));
        assertTrue("-112th".equals(ordinal(-112)));
        assertTrue("-113th".equals(ordinal(-113)));
        assertTrue("-114th".equals(ordinal(-114)));
        assertTrue("-115th".equals(ordinal(-115)));
        assertTrue("-116th".equals(ordinal(-116)));
        assertTrue("-117th".equals(ordinal(-117)));
        assertTrue("-118th".equals(ordinal(-118)));
        assertTrue("-119th".equals(ordinal(-119)));
        assertTrue("-120th".equals(ordinal(-120)));
        assertTrue("-121st".equals(ordinal(-121))); // st
        assertTrue("-122nd".equals(ordinal(-122))); // nd
        assertTrue("-123rd".equals(ordinal(-123))); // rd
        assertTrue("-124th".equals(ordinal(-124)));
        assertTrue("-125th".equals(ordinal(-125)));
        assertTrue("-126th".equals(ordinal(-126)));
        assertTrue("-127th".equals(ordinal(-127)));
        assertTrue("-128th".equals(ordinal(-128)));
        assertTrue("-129th".equals(ordinal(-129)));
        assertTrue("-130th".equals(ordinal(-130)));

        // Close to minimum int...
        assertTrue("-2147483640th".equals(ordinal(-2147483640)));
        assertTrue("-2147483641st".equals(ordinal(-2147483641)));
        assertTrue("-2147483642nd".equals(ordinal(-2147483642)));
        assertTrue("-2147483643rd".equals(ordinal(-2147483643)));
        assertTrue("-2147483644th".equals(ordinal(-2147483644)));

        // Minimum int...
        assertTrue("-2147483648th".equals(ordinal(-2147483648)));
    } // end testOrdinal();

    public static String[] ss = {"One","Two","Three","Four","Five","Six","Seven","Eight","Nine","Ten","Eleven",
            "Twelve","Thirteen","Fourteen","Fifteen","Sixteen","Seventeen","Eighteen","Nineteen","Twenty"};

    public static void mapHelper(Map<Integer,String> m, int max) {
        Assert.assertEquals("Size check", max, m.size());
        for (int i = 0; i < max; i++) {
            Assert.assertEquals(ss[i], m.get(i + 1));
        }
        assertNull(m.get(max + 1));
        assertNull(m.get(max + 999));
    }

    public static void mapHelperOdd(Map<Integer,String> m, int max) {
        Assert.assertEquals("Size check", (max + 1) / 2, m.size());
        for (int i = 0; i < max; i++) {
            if ( (i % 2) == 0 ) {
                Assert.assertEquals(ss[i], m.get(i + 1));
            } else {
                assertNull(m.get(i + 1));
            }
        }
        assertNull(m.get(max + 1));
        assertNull(m.get(max + 999));
    }

    public static void mapHelperEven(Map<Integer,String> m, int max) {
        Assert.assertEquals("Size check", max / 2, m.size());
        for (int i = 0; i < max; i++) {
            if ( (i % 2) == 0 ) {
                assertNull(m.get(i + 1));
            } else {
                Assert.assertEquals(ss[i], m.get(i + 1));
            }
        }
        assertNull(m.get(max + 1));
        assertNull(m.get(max + 999));
    }

//    @Test public void testPreliminary() {
//        assertEquals("One", unMap(1, "One").get(1));
//        assertEquals("Two", unMap(1, "One", 2, "Two").get(2));
//    }
//
//    @Test public void testUnMap10() {
//        Map<Integer,String> a = unMap(1, "One", 2, "Two", 3, "Three", 4, "Four", 5, "Five", 6, "Six", 7, "Seven",
//                8, "Eight", 9, "Nine", 10, "Ten");
//        int max = 10;
//        mapHelper(a, max);
//        Map<Integer,String> b = unMapSkipNull(Tuple2.of(1, "One"), Tuple2.of(2, "Two"), Tuple2.of(3, "Three"),
//                Tuple2.of(4, "Four"), Tuple2.of(5, "Five"), Tuple2.of(6, "Six"), Tuple2.of(7, "Seven"),
//                Tuple2.of(8, "Eight"), Tuple2.of(9, "Nine"), Tuple2.of(10, "Ten"));
//        mapHelper(b, max);
//        assertEquals(a, b);
//        assertEquals(b, a);
//        assertEquals(a.hashCode(), b.hashCode());
//        mapHelperOdd(unMapSkipNull(Tuple2.of(1, "One"), null, Tuple2.of(3, "Three"), null, Tuple2.of(5, "Five"), null,
//                Tuple2.of(7, "Seven"), null, Tuple2.of(9, "Nine"), null), max);
//        mapHelperEven(unMapSkipNull(null, Tuple2.of(2, "Two"), null, Tuple2.of(4, "Four"), null, Tuple2.of(6, "Six"), null,
//                Tuple2.of(8, "Eight"), null, Tuple2.of(10, "Ten")), max);
//    }
//    @Test public void testUnMap9() {
//        Map<Integer,String> a = unMap(1, "One", 2, "Two", 3, "Three", 4, "Four", 5, "Five", 6, "Six", 7, "Seven",
//                8, "Eight", 9, "Nine");
//        int max = 9;
//        mapHelper(a, max);
//        Map<Integer,String> b = unMapSkipNull(Tuple2.of(1, "One"), Tuple2.of(2, "Two"), Tuple2.of(3, "Three"),
//                Tuple2.of(4, "Four"), Tuple2.of(5, "Five"), Tuple2.of(6, "Six"), Tuple2.of(7, "Seven"),
//                Tuple2.of(8, "Eight"), Tuple2.of(9, "Nine"));
//        mapHelper(b, max);
//        assertEquals(a, b);
//        assertEquals(b, a);
//        assertEquals(a.hashCode(), b.hashCode());
//        mapHelperOdd(unMapSkipNull(Tuple2.of(1, "One"), null, Tuple2.of(3, "Three"), null, Tuple2.of(5, "Five"), null,
//                Tuple2.of(7, "Seven"), null, Tuple2.of(9, "Nine")), max);
//        mapHelperEven(unMapSkipNull(null, Tuple2.of(2, "Two"), null, Tuple2.of(4, "Four"), null, Tuple2.of(6, "Six"), null,
//                Tuple2.of(8, "Eight"), null), max);
//    }
//    @Test public void testUnMap8() {
//        Map<Integer,String> a = unMap(1, "One", 2, "Two", 3, "Three", 4, "Four", 5, "Five", 6, "Six", 7, "Seven",
//                8, "Eight");
//        int max = 8;
//        mapHelper(a, max);
//        Map<Integer,String> b = unMapSkipNull(Tuple2.of(1, "One"), Tuple2.of(2, "Two"), Tuple2.of(3, "Three"),
//                Tuple2.of(4, "Four"), Tuple2.of(5, "Five"), Tuple2.of(6, "Six"), Tuple2.of(7, "Seven"),
//                Tuple2.of(8, "Eight"));
//        mapHelper(b, max);
//        assertEquals(a, b);
//        assertEquals(b, a);
//        assertEquals(a.hashCode(), b.hashCode());
//        mapHelperOdd(unMapSkipNull(Tuple2.of(1, "One"), null, Tuple2.of(3, "Three"), null, Tuple2.of(5, "Five"), null,
//                Tuple2.of(7, "Seven"), null), max);
//        mapHelperEven(unMapSkipNull(null, Tuple2.of(2, "Two"), null, Tuple2.of(4, "Four"), null, Tuple2.of(6, "Six"), null,
//                Tuple2.of(8, "Eight")), max);
//    }
//    @Test public void testUnMap7() {
//        Map<Integer,String> a = unMap(1, "One", 2, "Two", 3, "Three", 4, "Four", 5, "Five", 6, "Six", 7, "Seven");
//        int max = 7;
//        mapHelper(a, max);
//        Map<Integer,String> b = unMapSkipNull(Tuple2.of(1, "One"), Tuple2.of(2, "Two"), Tuple2.of(3, "Three"),
//                Tuple2.of(4, "Four"), Tuple2.of(5, "Five"), Tuple2.of(6, "Six"), Tuple2.of(7, "Seven"));
//        mapHelper(b, max);
//        assertEquals(a, b);
//        assertEquals(b, a);
//        assertEquals(a.hashCode(), b.hashCode());
//        mapHelperOdd(unMapSkipNull(Tuple2.of(1, "One"), null, Tuple2.of(3, "Three"), null, Tuple2.of(5, "Five"), null,
//                Tuple2.of(7, "Seven")), max);
//        mapHelperEven(unMapSkipNull(null, Tuple2.of(2, "Two"), null, Tuple2.of(4, "Four"), null, Tuple2.of(6, "Six"),
//                null), max);
//    }
//    @Test public void testUnMap6() {
//        Map<Integer,String> a = unMap(1, "One", 2, "Two", 3, "Three", 4, "Four", 5, "Five", 6, "Six");
//        int max = 6;
//        mapHelper(a, max);
//        Map<Integer,String> b = unMapSkipNull(Tuple2.of(1, "One"), Tuple2.of(2, "Two"), Tuple2.of(3, "Three"),
//                Tuple2.of(4, "Four"), Tuple2.of(5, "Five"), Tuple2.of(6, "Six"));
//        mapHelper(b, max);
//        assertEquals(a, b);
//        assertEquals(b, a);
//        assertEquals(a.hashCode(), b.hashCode());
//
//        // Little extra here for equals.  I'm really not sure what to do about this, but we should know when it breaks
//        // so we can make a good decision at that time.
//        // There is an implementation of equals and hashCode commented out in UnmodMap.
//        Map<Integer,String> c = new HashMap<>();
//        c.put(1, "One");
//        c.put(2, "Two");
//        c.put(3, "Three");
//        c.put(4, "Four");
//        c.put(5, "Five");
//        c.put(6, "Six");
//        mapHelper(c, max);
//        assertEquals(a, c);
//        assertEquals(c, a);
//        assertEquals(b, c);
//        assertEquals(c, b);
//        assertEquals(a.hashCode(), c.hashCode());
//        assertEquals(b.hashCode(), c.hashCode());
//
//        mapHelperOdd(unMapSkipNull(Tuple2.of(1, "One"), null, Tuple2.of(3, "Three"), null, Tuple2.of(5, "Five"), null),
//                max);
//        mapHelperEven(unMapSkipNull(null, Tuple2.of(2, "Two"), null, Tuple2.of(4, "Four"), null, Tuple2.of(6, "Six")), max);
//    }
//    @Test public void testUnMap5() {
//        Map<Integer,String> a = unMap(1, "One", 2, "Two", 3, "Three", 4, "Four", 5, "Five");
//        int max = 5;
//        mapHelper(a, max);
//        Map<Integer,String> b = unMapSkipNull(Tuple2.of(1, "One"), Tuple2.of(2, "Two"), Tuple2.of(3, "Three"),
//                Tuple2.of(4, "Four"), Tuple2.of(5, "Five"));
//        mapHelper(b, max);
//        assertEquals(a, b);
//        assertEquals(b, a);
//        assertEquals(a.hashCode(), b.hashCode());
//        mapHelperOdd(unMapSkipNull(Tuple2.of(1, "One"), null, Tuple2.of(3, "Three"), null, Tuple2.of(5, "Five")), max);
//        mapHelperEven(unMapSkipNull(null, Tuple2.of(2, "Two"), null, Tuple2.of(4, "Four"), null), max);
//    }
//    @Test public void testUnMap4() {
//        Map<Integer,String> a = unMap(1, "One", 2, "Two", 3, "Three", 4, "Four");
//        int max = 4;
//        mapHelper(a, max);
//        Map<Integer,String> b = unMapSkipNull(Tuple2.of(1, "One"), Tuple2.of(2, "Two"), Tuple2.of(3, "Three"),
//                Tuple2.of(4, "Four"));
//        mapHelper(b, max);
//        assertEquals(a, b);
//        assertEquals(b, a);
//        assertEquals(a.hashCode(), b.hashCode());
//        mapHelperOdd(unMapSkipNull(Tuple2.of(1, "One"), null, Tuple2.of(3, "Three"), null), max);
//        mapHelperEven(unMapSkipNull(null, Tuple2.of(2, "Two"), null, Tuple2.of(4, "Four")), max);
//    }
//    @Test public void testUnMap3() {
//        Map<Integer,String> a = unMap(1, "One", 2, "Two", 3, "Three");
//        int max = 3;
//        mapHelper(a, max);
//        Map<Integer,String> b = unMapSkipNull(Tuple2.of(1, "One"), Tuple2.of(2, "Two"), Tuple2.of(3, "Three"));
//        mapHelper(b, max);
//        assertEquals(a, b);
//        assertEquals(b, a);
//        assertEquals(a.hashCode(), b.hashCode());
//        mapHelperOdd(unMapSkipNull(Tuple2.of(1, "One"), null, Tuple2.of(3, "Three")), max);
//        mapHelperEven(unMapSkipNull(null, Tuple2.of(2, "Two"), null), max);
//    }
//    @Test public void testUnMap2() {
//        Map<Integer,String> a = unMap(1, "One", 2, "Two");
//        int max = 2;
//        mapHelper(a, max);
//        Map<Integer,String> b = unMapSkipNull(Tuple2.of(1, "One"), Tuple2.of(2, "Two"));
//        mapHelper(b, max);
//        assertEquals(a, b);
//        assertEquals(b, a);
//        assertEquals(a.hashCode(), b.hashCode());
//        mapHelperOdd(unMapSkipNull(Tuple2.of(1, "One"), null), max);
//        mapHelperEven(unMapSkipNull(null, Tuple2.of(2, "Two")), max);
//    }
//    @Test public void testUnMap1() {
//        Map<Integer,String> a = unMap(1, "One");
//        int max = 1;
//        mapHelper(a, max);
//        Map<Integer,String> b = unMapSkipNull(Tuple2.of(1, "One"));
//        mapHelper(b, max);
//        assertEquals(a, b);
//        assertEquals(b, a);
//        assertEquals(a.hashCode(), b.hashCode());
//        mapHelperOdd(unMapSkipNull(Tuple2.of(1, "One")), max);
//        mapHelperEven(unMapSkipNull((Map.Entry<Integer,String>) null), max);
//        mapHelperEven(unMapSkipNull(null, null, null, null, null, null, null, null), max);
//    }
//    @Test public void testUnMap0() {
//        Map<Integer,String> a = UnmodMap.empty();
//        int max = 0;
//        mapHelper(a, max);
//    }
//
//    @Test public void testUnSet3() {
//        UnmodSet<Integer> a = unSet(1, 2, 3);
//        assertEquals(3, a.size());
//
//        Set<Integer> b = new HashSet<>();
//        b.add(1);
//        b.add(2);
//        b.add(3);
//
//        assertEquals(a, b);
//        assertEquals(b, a);
//        assertEquals(a.hashCode(), b.hashCode());
//
//        UnmodSet<Integer> c = unSetSkipNull(null, 1, null, 2, null, 3);
//        assertEquals(c, a);
//        assertEquals(a, c);
//        assertEquals(c, b);
//        assertEquals(b, c);
//        assertEquals(a.hashCode(), c.hashCode());
//
//        assertEquals(UnmodSet.empty(), unSet());
//
//        assertEquals(UnmodSet.empty(), unSetSkipNull(null, null));
//        assertEquals(UnmodSet.empty(), unSetSkipNull());
//    }

    @Test public void emptyIteratorTest() {
        assertTrue(emptyUnmodIterable() == unmodIterable(null));
        assertFalse(emptyUnmodIterable().iterator().hasNext());
    }

    @Test(expected = NoSuchElementException.class)
    public void emptyIteratorTestEx() { emptyUnmodIterable().iterator().next(); }


    @Test public void unmodIterableTest() {
        ImList<Integer> oneTwoThree = vec(1,2,3);
        assertTrue("An unmod iterable comes through unmodified",
                   oneTwoThree == unmodIterable(oneTwoThree));

        UnmodListTest.iteratorTest(Arrays.asList(1,2,3).iterator(),
                                   unmodIterable(Arrays.asList(1,2,3)).iterator());
    }

    @Test public void testEmptyUnmodListIterator() {
        assertFalse(emptyUnmodListIterator().hasNext());
        assertFalse(emptyUnmodListIterator().hasPrevious());
        assertEquals(0, emptyUnmodListIterator().nextIndex());
        assertEquals(-1, emptyUnmodListIterator().previousIndex());
    }

    @Test (expected = NoSuchElementException.class)
    public void testUnmodListIteratorNext() { EMPTY_UNMOD_LIST_ITERATOR.next(); }

    @Test (expected = NoSuchElementException.class)
    public void testUnmodListIteratorPrev() { EMPTY_UNMOD_LIST_ITERATOR.previous(); }

    @Test public void unmodListIteratorTest() {
        UnmodList emptyUnList = FunctionUtils.unmodList(null);
        assertTrue(EMPTY_UNMOD_LIST == emptyUnList);
        assertTrue(EMPTY_UNMOD_LIST_ITERATOR == emptyUnList.listIterator());
        assertTrue(EMPTY_UNMOD_LIST == FunctionUtils.unmodList(Collections.emptyList()));

        ImList<Integer> oneTwoThree = vec(1,2,3);
        assertTrue("An unmod List comes through unmodified",
                   oneTwoThree == unmodList(oneTwoThree));

        UnmodListTest.listIteratorTest(Arrays.asList(1,2,3), unmodList(Arrays.asList(1,2,3)));
    }

    @Test public void unListTest() {
        equalsDistinctHashCode(FunctionUtils.unmodList(Arrays.asList(3, 4, 5)),
                               FunctionUtils.unmodList(new ArrayList<>(Arrays.asList(3, 4, 5))),
                               new LinkedList<>(Arrays.asList(3, 4, 5)),
                               new ArrayList<>(Arrays.asList(4, 5, 3)));

        assertEquals(-1, emptyUnmodList().indexOf("hamster"));
        assertEquals(-1, emptyUnmodList().indexOf(39));
        assertEquals(-1, emptyUnmodList().lastIndexOf("hamster"));
        assertEquals(-1, emptyUnmodList().lastIndexOf(39));
        assertTrue(EMPTY_UNMOD_LIST_ITERATOR == emptyUnmodList().listIterator(0));
        assertEquals(0, emptyUnmodList().size());

        List<Integer> refList = Arrays.asList(1,2,3,4);
        UnmodList<Integer> testList = unmodList(Arrays.asList(1,2,3,4));

        assertEquals(refList.size(), testList.size());

        for (int i = 0; i < refList.size(); i++) {
            assertEquals(refList.get(i), testList.get(i));
        }

        UnmodListTest.iteratorTest(refList.iterator(), testList.iterator());
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void outOfBounds01() { emptyUnmodList().get(0); }


    @Test public void testEmptyUnmodIterator() {
        assertFalse(emptyUnmodIterator().hasNext());
    }

    @Test(expected = NoSuchElementException.class)
    public void testEmptyNext() { emptyUnmodIterator().next(); }

    @Test public void emptyUnmodSetTest() {
        assertFalse(emptyUnmodSet().contains(null));
        assertEquals(0, emptyUnmodSet().size());
        assertTrue(emptyUnmodSet().isEmpty());
        assertTrue(EMPTY_UNMOD_ITERATOR == emptyUnmodSet().iterator());
    }

    @Test public void unSetTest() {
        UnmodSet<Integer> s = FunctionUtils.unmodSet(new HashSet<>(Arrays.asList(5, 4, 3)));

        assertEquals(FunctionUtils.EMPTY_UNMOD_SET, FunctionUtils.unmodSet(null));
        assertEquals(FunctionUtils.EMPTY_UNMOD_SET, FunctionUtils.unmodSet(Collections.emptySet()));

        ImSet<Integer> imSet = set(1,2,3);
        assertTrue(imSet == FunctionUtils.unmodSet(imSet));

        assertTrue(s.contains(3));
        assertFalse(s.contains(-1));
        assertFalse(s.isEmpty());
        assertTrue(FunctionUtils.unmodSet(Collections.emptySet()).isEmpty());

        equalsDistinctHashCode(s,
                               FunctionUtils.unmodSet(new HashSet<>(Arrays.asList(3, 4, 5))),
                               new HashSet<>(Arrays.asList(4, 3, 5)),
                               FunctionUtils.unmodSet(new HashSet<>(Arrays.asList(4, 5, 6)))
        );
    }

    @Test public void emptyUnmodSortedSetTest() {
        assertFalse(emptyUnmodSortedSet().contains(null));
        Assert.assertEquals(0, emptyUnmodSortedSet().size());
        assertTrue(emptyUnmodSortedSet().isEmpty());
        assertTrue(EMPTY_UNMOD_SORTED_ITERATOR == emptyUnmodSortedSet().iterator());
        assertNull(emptyUnmodSortedSet().comparator());
        assertTrue(EMPTY_UNMOD_SORTED_SET == emptyUnmodSortedSet().subSet(null, null));
        assertTrue(EMPTY_UNMOD_SORTED_SET == emptyUnmodSortedSet().tailSet(null));
    }

    @Test (expected = NoSuchElementException.class)
    public void testEmptyUnmodSortedSetExFirst() { emptyUnmodSortedSet().first(); }

    @Test (expected = NoSuchElementException.class)
    public void testEmptyUnmodSortedSetExLast() { emptyUnmodSortedSet().last(); }


    @Test public void unSetSorted() {
        assertEquals(FunctionUtils.EMPTY_UNMOD_SORTED_SET, FunctionUtils.unmodSortedSet(null));
        assertEquals(FunctionUtils.EMPTY_UNMOD_SORTED_SET,
                     FunctionUtils.unmodSortedSet(Collections.emptySortedSet()));

        ImSortedSet<Integer> imSet = sortedSet((a, b) -> a - b, vec(1, 2, 3));
        assertTrue(imSet == FunctionUtils.unmodSortedSet(imSet));

        UnmodSortedSet<Integer> ts = FunctionUtils.unmodSortedSet(new TreeSet<>(Arrays.asList(5, 4, 3)));
        assertNull(ts.comparator());
        // headSet is exclusive.
        assertTrue(ts.headSet(4).contains(3));
        assertFalse(ts.headSet(4).contains(4));
        assertFalse(ts.headSet(4).contains(5));

        // tailSet is inclusive.
        assertTrue(ts.tailSet(4).contains(5));
        assertTrue(ts.tailSet(4).contains(4));
        assertFalse(ts.tailSet(4).contains(3));

        Assert.assertEquals(Integer.valueOf(3), ts.first());
        Assert.assertEquals(Integer.valueOf(5), ts.last());

        assertFalse(ts.contains(2));
        assertTrue(ts.contains(3));
        assertTrue(ts.contains(4));
        assertTrue(ts.contains(5));
        assertFalse(ts.contains(6));

        // low endpoint (inclusive) to high endpoint (exclusive)
        assertFalse(ts.subSet(4, 5).contains(5));
        assertTrue(ts.subSet(4, 5).contains(4));
        assertFalse(ts.subSet(4, 5).contains(3));

        assertFalse(ts.isEmpty());

        Assert.assertEquals(ts.hashCode(), FunctionUtils.unmodSortedSet(new TreeSet<>(Arrays.asList(5, 4, 3))).hashCode());
        Assert.assertEquals(ts, FunctionUtils.unmodSortedSet(new TreeSet<>(Arrays.asList(5, 4, 3))));

        equalsDistinctHashCode(FunctionUtils.unmodSortedSet(new TreeSet<>(Arrays.asList(5, 4, 3))),
                               FunctionUtils.unmodSortedSet(new TreeSet<>(Arrays.asList(3, 4, 5))),
                               new TreeSet<>(Arrays.asList(4, 3, 5)),
                               FunctionUtils.unmodSortedSet(new TreeSet<>(Arrays.asList(4, 5, 6)))
        );
    }

    @SuppressWarnings("deprecation")
    @Test public void emptyUnmodMapTest() {
        assertEquals(0, emptyUnmodMap().entrySet().size());
        assertTrue(EMPTY_UNMOD_SET == emptyUnmodMap().keySet());
        assertTrue(EMPTY_UNMOD_COLLECTION == emptyUnmodMap().values());
        assertEquals(0, emptyUnmodMap().size());
        assertTrue(emptyUnmodMap().isEmpty());
        assertTrue(FunctionUtils.<UnmodMap.UnEntry<Object,Object>>emptyUnmodIterator() ==
                   emptyUnmodMap().iterator());
        assertFalse(emptyUnmodMap().containsKey(null));
        assertFalse(emptyUnmodMap().containsValue(null));
        assertNull(emptyUnmodMap().get(null));
    }

    @SuppressWarnings("deprecation")
    @Test public void unMapTest() {
        assertEquals(FunctionUtils.EMPTY_UNMOD_MAP, FunctionUtils.unmodMap(null));
        assertEquals(FunctionUtils.EMPTY_UNMOD_MAP,
                     FunctionUtils.unmodMap(Collections.emptyMap()));

        ImMap<Integer,String> imMap = map(tup(1, ordinal(1)),
                                          tup(2, ordinal(2)),
                                          tup(3, ordinal(3)));

        assertTrue(imMap == FunctionUtils.unmodMap(imMap));

        final UnmodMap<Integer,String> ts;
        Map<Integer,String> sm = new HashMap<>();
        sm.put(5, "five");
        sm.put(4, "four");
        sm.put(3, "three");
        ts = FunctionUtils.unmodMap(sm);

        Assert.assertEquals(3, ts.size());
        assertFalse(ts.isEmpty());

        assertFalse(ts.containsKey(2));
        assertTrue(ts.containsKey(3));
        assertTrue(ts.containsKey(4));
        assertTrue(ts.containsKey(5));
        assertFalse(ts.containsKey(6));

        assertFalse(ts.containsValue("two"));
        assertTrue(ts.containsValue("three"));
        assertTrue(ts.containsValue("four"));
        assertTrue(ts.containsValue("five"));
        assertFalse(ts.containsValue("six"));

        assertFalse(ts.isEmpty());

        UnmodListTest.iteratorTest(ts.iterator(), sm.entrySet().iterator());

        assertEquals(ts.values().hashCode(), sm.values().hashCode());
        assertTrue(ts.values().equals(sm.values()));
//        assertTrue(sm.values().equals(ts.values()));

        final UnmodMap<Integer,String> m2;
        {
            Map<Integer,String> sm2 = new HashMap<>();
            sm2.put(3, "three");
            sm2.put(4, "four");
            sm2.put(5, "five");
            m2 = FunctionUtils.unmodMap(sm2);
        }

        final UnmodMap<Integer,String> m3;
        {
            Map<Integer,String> sm3 = new HashMap<>();
            sm3.put(4, "four");
            sm3.put(5, "five");
            sm3.put(6, "six");
            m3 = FunctionUtils.unmodMap(sm3);
        }

        equalsDistinctHashCode(ts, m2, sm, m3);

        Assert.assertEquals(3, ts.entrySet().size());
        assertFalse(ts.entrySet().isEmpty());

        Assert.assertEquals(3, ts.keySet().size());
        assertFalse(ts.keySet().isEmpty());

        Assert.assertEquals(3, ts.values().size());
        assertFalse(ts.values().isEmpty());

        equalsDistinctHashCode(ts.entrySet(), m2.entrySet(), sm.entrySet(), m3.entrySet());
        equalsDistinctHashCode(ts.keySet(), m2.keySet(), sm.keySet(), m3.keySet());

        Assert.assertEquals(m3, m3);

        // Wow.  HashMap.values() returns something that doesn't implement equals.
//        assertEquals(m3.values(), m3.values());
        Assert.assertEquals(new ArrayList<>(m3.values()), new ArrayList<>(m3.values()));

        equalsDistinctHashCode(new ArrayList<>(ts.values()), new ArrayList<>(m2.values()),
                               new ArrayList<>(sm.values()), new ArrayList<>(m3.values()));
    }

    @Test public void unMapSorted() {
        assertEquals(FunctionUtils.EMPTY_UNMOD_SORTED_MAP, FunctionUtils.unmodSortedMap(null));
        assertEquals(FunctionUtils.EMPTY_UNMOD_SORTED_MAP,
                     FunctionUtils.unmodSortedMap(Collections.emptySortedMap()));

        ImSortedMap<Integer,String> imMap = sortedMap((a, b) -> a - b, vec(tup(1, ordinal(1)),
                                                                           tup(2, ordinal(2)),
                                                                           tup(3, ordinal(3))));
        assertTrue(imMap == FunctionUtils.unmodSortedMap(imMap));

        final UnmodSortedMap<Integer,String> ts;
        SortedMap<Integer,String> sm = new TreeMap<>();
        sm.put(5, "five");
        sm.put(4, "four");
        sm.put(3, "three");
        ts = FunctionUtils.unmodSortedMap(sm);

        Assert.assertEquals(3, ts.size());
        assertFalse(ts.isEmpty());

        assertNull(ts.comparator());
        // headMap is exclusive.
        assertTrue(ts.headMap(4).containsKey(3));
        assertFalse(ts.headMap(4).containsKey(4));
        assertFalse(ts.headMap(4).containsKey(5));

        assertTrue(ts.headMap(4).containsValue("three"));
        assertFalse(ts.headMap(4).containsValue("four"));
        assertFalse(ts.headMap(4).containsValue("five"));

        // tailMap is inclusive.
        assertTrue(ts.tailMap(4).containsKey(5));
        assertTrue(ts.tailMap(4).containsKey(4));
        assertFalse(ts.tailMap(4).containsKey(3));

        assertTrue(ts.tailMap(4).containsValue("five"));
        assertTrue(ts.tailMap(4).containsValue("four"));
        assertFalse(ts.tailMap(4).containsValue("three"));

        Assert.assertEquals(Integer.valueOf(3), ts.firstKey());
        Assert.assertEquals(Integer.valueOf(5), ts.lastKey());

        assertFalse(ts.containsKey(2));
        assertTrue(ts.containsKey(3));
        assertTrue(ts.containsKey(4));
        assertTrue(ts.containsKey(5));
        assertFalse(ts.containsKey(6));

        assertFalse(ts.containsValue("two"));
        assertTrue(ts.containsValue("three"));
        assertTrue(ts.containsValue("four"));
        assertTrue(ts.containsValue("five"));
        assertFalse(ts.containsValue("six"));

        // low endpoint (inclusive) to high endpoint (exclusive)
        assertFalse(ts.subMap(4, 5).containsKey(5));
        assertTrue(ts.subMap(4, 5).containsKey(4));
        assertFalse(ts.subMap(4, 5).containsKey(3));

        assertFalse(ts.isEmpty());

        final UnmodSortedMap<Integer,String> m2;
        {
            SortedMap<Integer,String> sm2 = new TreeMap<>();
            sm2.put(3, "three");
            sm2.put(4, "four");
            sm2.put(5, "five");
            m2 = FunctionUtils.unmodSortedMap(sm2);
        }

        final UnmodSortedMap<Integer,String> m3;
        {
            SortedMap<Integer,String> sm3 = new TreeMap<>();
            sm3.put(4, "four");
            sm3.put(5, "five");
            sm3.put(6, "six");
            m3 = FunctionUtils.unmodSortedMap(sm3);
        }

        equalsDistinctHashCode(ts, m2, sm, m3);

        Assert.assertEquals(3, ts.entrySet().size());
        assertFalse(ts.entrySet().isEmpty());

        Assert.assertEquals(3, ts.keySet().size());
        assertFalse(ts.keySet().isEmpty());

        Assert.assertEquals(3, ts.values().size());
        assertFalse(ts.values().isEmpty());

        equalsDistinctHashCode(ts.entrySet(), m2.entrySet(), sm.entrySet(), m3.entrySet());
        equalsDistinctHashCode(ts.keySet(), m2.keySet(), sm.keySet(), m3.keySet());

        Assert.assertEquals(m3, m3);

        // Wow.  TreeMap.values() returns something that doesn't implement equals.
//        assertEquals(m3.values(), m3.values());
        Assert.assertEquals(new ArrayList<>(m3.values()), new ArrayList<>(m3.values()));

        equalsDistinctHashCode(new ArrayList<>(ts.values()), new ArrayList<>(m2.values()),
                               new ArrayList<>(sm.values()), new ArrayList<>(m3.values()));

        // Looks like this has the same issue as TreeSet.
//        final UnmodMap<Integer,String> m4;
//        {
//            // This is a reverse integer comparator.
//            SortedMap<Integer,String> sm2 = new TreeMap<>((a, b) -> b - a);
//            sm2.put(3, "three");
//            sm2.put(4, "four");
//            sm2.put(5, "five");
//            m4 = unmodSortedMap(sm2);
//        }
//
//        System.out.println(UnmodIterable.toString("ts", ts));
//        System.out.println(UnmodIterable.toString("m2", m2));
//        System.out.println(UnmodIterable.toString("sm", sm.entrySet()));
//        System.out.println(UnmodIterable.toString("m4", m4));
//
//        // These will have the same hashcodes, but different comparators.
//        equalsHashCode(ts, m2, sm, m4);
    }


    @Test public void testEmptyUnmodSortedIterator() {
        assertFalse(emptyUnmodSortedIterator().hasNext());
    }

    @Test(expected = NoSuchElementException.class)
    public void testEmptyUnmodSortedIteratorNext() { emptyUnmodSortedIterator().next(); }

    @Test public void testEmptyUnmodSortedMap() {
        Assert.assertEquals(emptyUnmodSortedSet(), emptyUnmodSortedMap().entrySet());
        Assert.assertEquals(emptyUnmodSortedSet(), emptyUnmodSortedMap().keySet());
        assertNull(emptyUnmodSortedMap().comparator());
        assertEquals(emptyUnmodSortedMap(), emptyUnmodSortedMap().subMap(null, null));
        assertEquals(emptyUnmodSortedMap(), emptyUnmodSortedMap().tailMap(null));
        Assert.assertEquals(emptyUnmodList(), emptyUnmodSortedMap().values());
        Assert.assertEquals(0, emptyUnmodSortedMap().size());
        assertTrue(emptyUnmodSortedMap().isEmpty());
        Assert.assertEquals(emptyUnmodSortedIterator(), emptyUnmodSortedMap().iterator());
        assertFalse(emptyUnmodSortedMap().containsKey(null));
        assertFalse(emptyUnmodSortedMap().containsValue(null));
        assertNull(emptyUnmodSortedMap().get(null));
    }

    @Test (expected = NoSuchElementException.class)
    public void testEmptyEx01() { emptyUnmodSortedMap().firstKey(); }
    @Test (expected = NoSuchElementException.class)
    public void testEmptyEx02() { emptyUnmodSortedMap().lastKey(); }


    @SuppressWarnings("deprecation")
    @Test public void unCollection() {
        assertEquals(FunctionUtils.EMPTY_UNMOD_COLLECTION, FunctionUtils.unmodCollection(null));
        assertEquals(FunctionUtils.EMPTY_UNMOD_COLLECTION,
                     FunctionUtils.unmodCollection(Collections.emptySet()));

        ImSet<Integer> imSet = set(1, 2, 3);
        assertTrue(imSet == FunctionUtils.unmodCollection(imSet));

        ArrayDeque<Integer> ad = new ArrayDeque<>(Arrays.asList(1, 2, 3));
        UnmodCollection<Integer> a = unmodCollection(new ArrayDeque<>(Arrays.asList(1, 2, 3)));
        Assert.assertEquals(3, a.size());
        assertTrue(a.contains(2));
        assertFalse(a.isEmpty());

        Assert.assertEquals(3, a.size());
        assertTrue(a.contains(2));
        assertFalse(a.isEmpty());

//        UnmodCollection<Integer> b = unmod(new ArrayDeque<>(Arrays.asList(1, 2, 3)));
//        assertEquals(a.hashCode(), b.hashCode());
//        assertEquals(a, b);
//        assertTrue(a.equals(b));
//        assertTrue(b.equals(a));

        equalsDistinctHashCode(new ArrayList<>(a), new ArrayList<>(ad), Arrays.asList(1, 2, 3),
                               Arrays.asList(3, 2, 1));
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

    @Test public void testEmptyCollection() {
        assertFalse(emptyUnmodCollection().contains(null));
        assertEquals(0, emptyUnmodCollection().size());
        assertTrue(emptyUnmodCollection().isEmpty());
        assertTrue(EMPTY_UNMOD_ITERATOR == emptyUnmodCollection().iterator());

    }

}
