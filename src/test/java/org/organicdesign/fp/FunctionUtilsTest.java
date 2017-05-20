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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.NoSuchElementException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.organicdesign.fp.tuple.Tuple2;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.*;
import static org.organicdesign.fp.FunctionUtils.*;

@RunWith(JUnit4.class)
public class FunctionUtilsTest {

    @Test public void stringifyTest() {
        assertEquals("null", stringify(null));
        assertEquals("\"Hello\"", stringify("Hello"));
        assertEquals("Tuple2(\"a\",3)", stringify(Tuple2.of("a", 3)));
    }

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

//    @Test
//    public void testToString() {
//        List<Integer> is = new ArrayList<>();
//        is.add(1);
//        is.add(2);
//        is.add(3);
//        is.add(4);
//        is.add(5);
//        assertEquals("Array<Integer>(1,2,3,4,5)", FunctionUtils.arrayToString(is.toArray()));
//
//        is.add(6);
//        assertEquals("Array<Integer>(1,2,3,4,5,...)", FunctionUtils.arrayToString(is.toArray()));
//
//        Map<String,Integer> m = new TreeMap<>();
//        m.put("Hello", 99);
//        m.put("World", -237);
//        assertEquals("TreeMap(Entry(Hello,99),Entry(World,-237))", FunctionUtils.mapToString(m));
//
//        m.put("x", 3);
//        m.put("y", 2);
//        m.put("z", 1);
//        m.put("zz", 0);
//
//        assertEquals("TreeMap(Entry(Hello,99),Entry(World,-237),Entry(x,3),Entry(y,2),Entry(z,1),...)",
//                     FunctionUtils.mapToString(m));
//
//        assertEquals("Array()", FunctionUtils.arrayToString(new Integer[0]));
//        assertEquals("Array(null)", FunctionUtils.arrayToString(new Integer[] {null}));
//
//    }

//    @SuppressWarnings({"ConstantConditions","Unchecked"})
//    @Test public void testToStringNull() {
//        assertEquals("null", FunctionUtils.mapToString(null));
//        Integer[] zs = null;
//        assertEquals("null", FunctionUtils.arrayToString(zs));
//    }

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

    private static String[] ss = {"One","Two","Three","Four","Five","Six","Seven","Eight","Nine",
                                  "Ten","Eleven", "Twelve","Thirteen","Fourteen","Fifteen",
                                  "Sixteen","Seventeen","Eighteen","Nineteen","Twenty"};

    public static void mapHelper(Map<Integer,String> m, int max) {
        assertEquals("Size check", max, m.size());
        for (int i = 0; i < max; i++) {
            assertEquals(ss[i], m.get(i + 1));
        }
        assertNull(m.get(max + 1));
        assertNull(m.get(max + 999));
    }

    public static void mapHelperOdd(Map<Integer,String> m, int max) {
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

    public static void mapHelperEven(Map<Integer,String> m, int max) {
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

//    @Test public void emptyIteratorTest() {
//        assertFalse(unmodIterable(null).iterator().hasNext());
//        assertFalse(emptyUnmodIterable().iterator().hasNext());
//        assertFalse(serializeDeserialize(emptyUnmodIterable()).iterator().hasNext());
//    }
//
//    @Test(expected = NoSuchElementException.class)
//    public void emptyIteratorTestEx() { emptyUnmodIterable().iterator().next(); }
//
//
//    @Test public void unmodIterableTest() {
//        ImList<Integer> oneTwoThree = vec(1,2,3);
//        assertTrue("An unmod iterable comes through unmodified",
//                   oneTwoThree == unmodIterable(oneTwoThree));
//
//        TestUtilities.iteratorTest(Arrays.asList(1, 2, 3).iterator(),
//                                   unmodIterable(Arrays.asList(1,2,3)).iterator());
//    }

    @Test public void testEmptyUnmodIterator() {
        assertFalse(emptyUnmodIterator().hasNext());
    }

    @Test(expected = NoSuchElementException.class)
    public void testEmptyNext() { emptyUnmodIterator().next(); }

}
