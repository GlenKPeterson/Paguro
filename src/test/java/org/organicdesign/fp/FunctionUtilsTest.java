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

import org.junit.jupiter.api.Test;
import org.organicdesign.fp.tuple.Tuple2;

import java.util.Map;

import static com.planbase.indented.StringUtils.indent;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;


public class FunctionUtilsTest {

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
    private static final String[] ss = {"One","Two","Three","Four","Five","Six","Seven","Eight","Nine",
                                  "Ten","Eleven", "Twelve","Thirteen","Fourteen","Fifteen",
                                  "Sixteen","Seventeen","Eighteen","Nineteen","Twenty"};

    public static void mapHelper(Map<Integer,String> m, int max) {
        assertEquals(max, m.size(), "Size check");
        for (int i = 0; i < max; i++) {
            assertEquals(ss[i], m.get(i + 1));
        }
        assertNull(m.get(max + 1));
        assertNull(m.get(max + 999));
    }

    public static void mapHelperOdd(Map<Integer,String> m, int max) {
        assertEquals((max + 1) / 2, m.size(), "Size check");
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
        assertEquals(max / 2, m.size(), "Size check");
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
}
