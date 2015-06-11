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

package org.organicdesign.fp;

import org.junit.Test;
import org.organicdesign.fp.collections.UnCollection;
import org.organicdesign.fp.collections.UnListIterator;
import org.organicdesign.fp.collections.UnMap;
import org.organicdesign.fp.collections.UnMapOrdered;
import org.organicdesign.fp.collections.UnSet;
import org.organicdesign.fp.collections.UnSetOrdered;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

import static org.junit.Assert.*;
import static org.organicdesign.fp.StaticImports.un;
import static org.organicdesign.fp.testUtils.EqualsContract.equalsDistinctHashCode;

public class StaticImportsTest {

    public static String[] ss = {"One","Two","Three","Four","Five","Six","Seven","Eight","Nine","Ten","Eleven",
            "Twelve","Thirteen","Fourteen","Fifteen","Sixteen","Seventeen","Eighteen","Nineteen","Twenty"};

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
//        // There is an implementation of equals and hashCode commented out in UnMap.
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
//        Map<Integer,String> a = UnMap.empty();
//        int max = 0;
//        mapHelper(a, max);
//    }
//
//    @Test public void testUnSet3() {
//        UnSet<Integer> a = unSet(1, 2, 3);
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
//        UnSet<Integer> c = unSetSkipNull(null, 1, null, 2, null, 3);
//        assertEquals(c, a);
//        assertEquals(a, c);
//        assertEquals(c, b);
//        assertEquals(b, c);
//        assertEquals(a.hashCode(), c.hashCode());
//
//        assertEquals(UnSet.empty(), unSet());
//
//        assertEquals(UnSet.empty(), unSetSkipNull(null, null));
//        assertEquals(UnSet.empty(), unSetSkipNull());
//    }

    @Test public void unListIterator() {
        UnListIterator<Integer> uli = un(Arrays.asList(5, 4, 3).listIterator());
        assertFalse(uli.hasPrevious());
        assertTrue(uli.hasNext());

        assertEquals(-1, uli.previousIndex());
        assertEquals(0, uli.nextIndex());
        assertEquals(Integer.valueOf(5), uli.next());
        assertTrue(uli.hasPrevious());
        assertTrue(uli.hasNext());

        assertEquals(0, uli.previousIndex());
        assertEquals(1, uli.nextIndex());
        assertEquals(Integer.valueOf(4), uli.next());
        assertTrue(uli.hasPrevious());
        assertTrue(uli.hasNext());

        assertEquals(1, uli.previousIndex());
        assertEquals(2, uli.nextIndex());
        assertEquals(Integer.valueOf(3), uli.next());
        assertTrue(uli.hasPrevious());
        assertFalse(uli.hasNext());

        assertEquals(2, uli.previousIndex());
        assertEquals(3, uli.nextIndex());
        assertEquals(Integer.valueOf(3), uli.previous());
        assertTrue(uli.hasPrevious());
        assertTrue(uli.hasNext());

        assertEquals(1, uli.previousIndex());
        assertEquals(2, uli.nextIndex());
        assertEquals(Integer.valueOf(4), uli.previous());
        assertTrue(uli.hasPrevious());
        assertTrue(uli.hasNext());

        assertEquals(0, uli.previousIndex());
        assertEquals(1, uli.nextIndex());
        assertEquals(Integer.valueOf(5), uli.previous());
        assertFalse(uli.hasPrevious());
        assertTrue(uli.hasNext());

        assertEquals(-1, uli.previousIndex());
        assertEquals(0, uli.nextIndex());
    }

    @Test public void unListTest() {
        equalsDistinctHashCode(un(Arrays.asList(3, 4, 5)),
                               un(new ArrayList<>(Arrays.asList(3, 4, 5))),
                               new LinkedList<>(Arrays.asList(3, 4, 5)),
                               new ArrayList<>(Arrays.asList(4, 5, 3)));
    }

    @Test public void unSetTest() {
        UnSet<Integer> s = un(new HashSet<>(Arrays.asList(5, 4, 3)));

        assertTrue(s.contains(3));
        assertFalse(s.contains(-1));
        assertFalse(s.isEmpty());
        assertTrue(un(Collections.emptySet()).isEmpty());

        equalsDistinctHashCode(s,
                               un(new HashSet<>(Arrays.asList(3, 4, 5))),
                               new HashSet<>(Arrays.asList(4, 3, 5)),
                               un(new HashSet<>(Arrays.asList(4, 5, 6)))
        );
    }

    @Test public void unSetSorted() {
        UnSetOrdered<Integer> ts = un(new TreeSet<>(Arrays.asList(5, 4, 3)));
        assertNull(ts.comparator());
        // headSet is exclusive.
        assertTrue(ts.headSet(4).contains(3));
        assertFalse(ts.headSet(4).contains(4));
        assertFalse(ts.headSet(4).contains(5));

        // tailSet is inclusive.
        assertTrue(ts.tailSet(4).contains(5));
        assertTrue(ts.tailSet(4).contains(4));
        assertFalse(ts.tailSet(4).contains(3));

        assertEquals(Integer.valueOf(3), ts.first());
        assertEquals(Integer.valueOf(5), ts.last());

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

        assertEquals(ts.hashCode(), un(new TreeSet<>(Arrays.asList(5, 4, 3))).hashCode());
        assertEquals(ts, un(new TreeSet<>(Arrays.asList(5, 4, 3))));

        equalsDistinctHashCode(un(new TreeSet<>(Arrays.asList(5, 4, 3))),
                               un(new TreeSet<>(Arrays.asList(3, 4, 5))),
                               new TreeSet<>(Arrays.asList(4, 3, 5)),
                               un(new TreeSet<>(Arrays.asList(4, 5, 6)))
        );
    }

    @Test public void unMapTest() {
        final UnMap<Integer,String> ts;
        Map<Integer,String> sm = new HashMap<>();
        sm.put(5, "five");
        sm.put(4, "four");
        sm.put(3, "three");
        ts = un(sm);

        assertEquals(3, ts.size());
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

        final UnMap<Integer,String> m2;
        {
            Map<Integer,String> sm2 = new HashMap<>();
            sm2.put(3, "three");
            sm2.put(4, "four");
            sm2.put(5, "five");
            m2 = un(sm2);
        }

        final UnMap<Integer,String> m3;
        {
            Map<Integer,String> sm3 = new HashMap<>();
            sm3.put(4, "four");
            sm3.put(5, "five");
            sm3.put(6, "six");
            m3 = un(sm3);
        }

        equalsDistinctHashCode(ts, m2, sm, m3);

        assertEquals(3, ts.entrySet().size());
        assertFalse(ts.entrySet().isEmpty());

        assertEquals(3, ts.keySet().size());
        assertFalse(ts.keySet().isEmpty());

        assertEquals(3, ts.values().size());
        assertFalse(ts.values().isEmpty());

        equalsDistinctHashCode(ts.entrySet(), m2.entrySet(), sm.entrySet(), m3.entrySet());
        equalsDistinctHashCode(ts.keySet(), m2.keySet(), sm.keySet(), m3.keySet());

        assertEquals(m3, m3);

        // Wow.  HashMap.values() returns something that doesn't implement equals.
//        assertEquals(m3.values(), m3.values());
        assertEquals(new ArrayList<>(m3.values()), new ArrayList<>(m3.values()));

        equalsDistinctHashCode(new ArrayList<>(ts.values()), new ArrayList<>(m2.values()),
                               new ArrayList<>(sm.values()), new ArrayList<>(m3.values()));
    }

    @Test public void unMapSorted() {
        final UnMapOrdered<Integer,String> ts;
        SortedMap<Integer,String> sm = new TreeMap<>();
        sm.put(5, "five");
        sm.put(4, "four");
        sm.put(3, "three");
        ts = un(sm);

        assertEquals(3, ts.size());
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

        assertEquals(Integer.valueOf(3), ts.firstKey());
        assertEquals(Integer.valueOf(5), ts.lastKey());

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

        final UnMapOrdered<Integer,String> m2;
        {
            SortedMap<Integer,String> sm2 = new TreeMap<>();
            sm2.put(3, "three");
            sm2.put(4, "four");
            sm2.put(5, "five");
            m2 = un(sm2);
        }

        final UnMapOrdered<Integer,String> m3;
        {
            SortedMap<Integer,String> sm3 = new TreeMap<>();
            sm3.put(4, "four");
            sm3.put(5, "five");
            sm3.put(6, "six");
            m3 = un(sm3);
        }

        equalsDistinctHashCode(ts, m2, sm, m3);

        assertEquals(3, ts.entrySet().size());
        assertFalse(ts.entrySet().isEmpty());

        assertEquals(3, ts.keySet().size());
        assertFalse(ts.keySet().isEmpty());

        assertEquals(3, ts.values().size());
        assertFalse(ts.values().isEmpty());

        equalsDistinctHashCode(ts.entrySet(), m2.entrySet(), sm.entrySet(), m3.entrySet());
        equalsDistinctHashCode(ts.keySet(), m2.keySet(), sm.keySet(), m3.keySet());

        assertEquals(m3, m3);

        // Wow.  TreeMap.values() returns something that doesn't implement equals.
//        assertEquals(m3.values(), m3.values());
        assertEquals(new ArrayList<>(m3.values()), new ArrayList<>(m3.values()));

        equalsDistinctHashCode(new ArrayList<>(ts.values()), new ArrayList<>(m2.values()),
                               new ArrayList<>(sm.values()), new ArrayList<>(m3.values()));

        // Looks like this has the same issue as TreeSet.
//        final UnMap<Integer,String> m4;
//        {
//            // This is a reverse integer comparator.
//            SortedMap<Integer,String> sm2 = new TreeMap<>((a, b) -> b - a);
//            sm2.put(3, "three");
//            sm2.put(4, "four");
//            sm2.put(5, "five");
//            m4 = un(sm2);
//        }
//
//        System.out.println(UnIterable.toString("ts", ts));
//        System.out.println(UnIterable.toString("m2", m2));
//        System.out.println(UnIterable.toString("sm", sm.entrySet()));
//        System.out.println(UnIterable.toString("m4", m4));
//
//        // These will have the same hashcodes, but different comparators.
//        equalsHashCode(ts, m2, sm, m4);
    }

    @Test public void unCollection() {
        ArrayDeque<Integer> ad = new ArrayDeque<>(Arrays.asList(1, 2, 3));
        UnCollection<Integer> a = un(new ArrayDeque<>(Arrays.asList(1, 2, 3)));
        assertEquals(3, a.size());
        assertTrue(a.contains(2));
        assertFalse(a.isEmpty());

        assertEquals(3, a.size());
        assertTrue(a.contains(2));
        assertFalse(a.isEmpty());

//        UnCollection<Integer> b = un(new ArrayDeque<>(Arrays.asList(1, 2, 3)));
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
}
