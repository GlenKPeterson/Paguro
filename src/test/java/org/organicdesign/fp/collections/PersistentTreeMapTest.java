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

package org.organicdesign.fp.collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.organicdesign.fp.StaticImportsTest;
import org.organicdesign.fp.function.Function2;
import org.organicdesign.fp.permanent.Sequence;
import org.organicdesign.fp.tuple.Tuple2;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.SortedMap;
import java.util.TreeMap;

import static org.junit.Assert.*;
import static org.organicdesign.fp.StaticImports.un;
import static org.organicdesign.fp.testUtils.EqualsContract.equalsDistinctHashCode;
import static org.organicdesign.fp.testUtils.EqualsContract.equalsSameHashCode;

@RunWith(JUnit4.class)
public class PersistentTreeMapTest {
    @Test public void assocAndGet() {
        PersistentTreeMap<String,Integer> m1 = PersistentTreeMap.empty();
        PersistentTreeMap<String,Integer> m2 = m1.assoc("one", 1);

        // Prove m1 unchanged
        assertEquals(0, m1.size());
        assertNull(m1.get("one"));

        // Show m2 correct.
        assertEquals(1, m2.size());
        assertEquals(Integer.valueOf(1), m2.get("one"));
        assertNull(m1.get("two"));

        Integer twoInt = Integer.valueOf(2);
        PersistentTreeMap<String,Integer> m3 = m2.assoc("two", twoInt);

        // Prove m1 unchanged
        assertEquals(0, m1.size());
        assertNull(m1.get("one"));

        // Show m2 unchanged
        assertEquals(1, m2.size());
        assertEquals(Integer.valueOf(1), m2.get("one"));

        // Show m3 correct
        assertEquals(2, m3.size());
        assertEquals(Integer.valueOf(1), m3.get("one"));
        assertEquals(Integer.valueOf(2), m3.get("two"));
        assertNull(m3.get("three"));

//        System.out.println("m3: " + m3);
//        PersistentTreeMap<String,Integer> m4 = m3.assoc("two", twoInt);
//        System.out.println("m4: " + m4);

        // Check that inserting the same key/value pair returns the same collection.
        assertTrue(m3 == m3.assoc("two", twoInt));

        // Check that it uses the == test and not the .equals() test.
        assertFalse(m3 == m3.assoc("two", new Integer(2)));
    }

    @Test public void order() {
        PersistentTreeMap<String,Integer> m1 = PersistentTreeMap.of(
                "c", 1,
                "b", 2,
                "a", 3);

        // Prove m1 unchanged
        assertEquals(3, m1.size());
        assertEquals(Integer.valueOf(1), m1.get("c"));
        assertEquals(Integer.valueOf(2), m1.get("b"));
        assertEquals(Integer.valueOf(3), m1.get("a"));
        assertNull(m1.get("d"));

//        System.out.println(m1.keySet().toString());

        assertArrayEquals(new String[]{"a", "b", "c"}, m1.keySet().toArray());

        // Values are a sorted set as well...
        assertArrayEquals(new Integer[]{3, 2, 1},
                          m1.map(e -> e.getValue()).toTypedArray());

        assertArrayEquals(new String[]{"a", "b", "c"},
                          PersistentTreeMap.of("a", 3,
                                               "b", 2,
                                               "c", 1)
                                  .keySet().toArray());


        {
            UnIterator<Integer> iter = PersistentTreeMap.of("b", 2)
                    .assoc("c", 1)
                    .assoc("a", 3)
                    .map(e -> e.getValue()).iterator();

            assertTrue(iter.hasNext());
            assertEquals(Integer.valueOf(3), iter.next());

            assertTrue(iter.hasNext());
            assertEquals(Integer.valueOf(2), iter.next());

            assertTrue(iter.hasNext());
            assertEquals(Integer.valueOf(1), iter.next());

            assertFalse(iter.hasNext());
        }

        PersistentTreeMap<String,Integer> m2 = PersistentTreeMap.of("c", 3)
                        .assoc("b", 2)
                        .assoc("a", 1);
        UnIterator<UnMap.UnEntry<String,Integer>> iter = m2.iterator();
        UnMap.UnEntry<String,Integer> next = iter.next();
        assertEquals("a", next.getKey());
        assertEquals(Integer.valueOf(1), next.getValue());

        next = iter.next();
        assertEquals("b", next.getKey());
        assertEquals(Integer.valueOf(2), next.getValue());

        next = iter.next();
        assertEquals("c", next.getKey());
        assertEquals(Integer.valueOf(3), next.getValue());

        assertNull(m2.comparator());
        assertNotEquals(String.CASE_INSENSITIVE_ORDER.reversed(), m2.comparator());

        PersistentTreeMap<String,Integer> m3 =
                PersistentTreeMap.ofCompSkipNull(String.CASE_INSENSITIVE_ORDER.reversed(),
                                                 Tuple2.of("a", 1),
                                                 Tuple2.of("b", 2),
                                                 Tuple2.of("c", 3));
        UnIterator<UnMap.UnEntry<String,Integer>> iter2 = m3.iterator();

        next = iter2.next();
        assertEquals("c", next.getKey());
        assertEquals(Integer.valueOf(3), next.getValue());

        next = iter2.next();
        assertEquals("b", next.getKey());
        assertEquals(Integer.valueOf(2), next.getValue());

        next = iter2.next();
        assertEquals("a", next.getKey());
        assertEquals(Integer.valueOf(1), next.getValue());

        assertEquals(String.CASE_INSENSITIVE_ORDER.reversed(), m3.comparator());
        assertNotEquals(Function2.defaultComparator(), m3.comparator());
    }

    @Test public void hashCodeAndEquals() {
        equalsDistinctHashCode(PersistentTreeMap.of("one", 1).assoc("two", 2).assoc("three", 3),
                               PersistentTreeMap.of("three", 3).assoc("two", 2).assoc("one", 1),
                               PersistentTreeMap.of("two", 2, "three", 3, "one", 1),
                               PersistentTreeMap.of("two", 2, "three", 3, "four", 4));

        SortedMap<String,Integer> m = new TreeMap<>();
        m.put("one", 1);
        m.put("two", 2);
        m.put("three", 3);

        equalsDistinctHashCode(PersistentTreeMap.of("one", 1, "two", 2, "three", 3),
                               m,
                               un(m),
                               PersistentTreeMap.of("two", 2, "three", 3, "four", 4));

        equalsDistinctHashCode(PersistentTreeMap.of("one", 1).assoc("two", 2).assoc("three", 3),
                               PersistentTreeMap.of("three", 3).assoc("two", 2).assoc("one", 1),
                               PersistentTreeMap.of("two", 2, "three", 3, "one", 1),
                               PersistentTreeMap.of("zne", 1, "two", 2, "three", 3));

        equalsDistinctHashCode(PersistentTreeMap.of("one", 1).assoc("two", 2).assoc("three", 3),
                               PersistentTreeMap.of("three", 3).assoc("two", 2).assoc("one", 1),
                               PersistentTreeMap.of("two", 2, "three", 3, "one", 1),
                               PersistentTreeMap.of("one", 1, "two", 2, "three", 2));

        equalsSameHashCode(PersistentTreeMap.of("one", 1).assoc("two", 2).assoc("three", 3),
                           PersistentTreeMap.of("three", 3).assoc("two", 2).assoc("one", 1),
                           PersistentTreeMap.of("two", 2, "three", 3, "one", 1),
                           PersistentTreeMap.of(1, "one", 2, "two", 3, "three"));
    }

    @Test public void sequence() {
        assertEquals(PersistentTreeMap.of(1, "one").assoc(2, "two").assoc(3, "three"),
                     PersistentTreeMap.of(1, "one").assoc(2, "two").assoc(3, "three").tailMap(0));

        assertEquals(PersistentTreeMap.of(1, "one").assoc(2, "two").assoc(3, "three"),
                     PersistentTreeMap.of(1, "one").assoc(2, "two").assoc(3, "three").tailMap(1));

        assertEquals(PersistentTreeMap.of(2, "two").assoc(3, "three"),
                     PersistentTreeMap.of(1, "one").assoc(2, "two").assoc(3, "three").tailMap(2));

        assertEquals(PersistentTreeMap.of(3, "three"),
                     PersistentTreeMap.of(1, "one").assoc(2, "two").assoc(3, "three").tailMap(3));

        assertEquals(PersistentTreeMap.empty(),
                     PersistentTreeMap.of(1, "one").assoc(2, "two").assoc(3, "three").tailMap(4));

        assertEquals(PersistentTreeMap.empty(),
                     PersistentTreeMap.of(1, "one").assoc(2, "two").assoc(3, "three").tailMap(999999999));

        assertArrayEquals(new UnMap.UnEntry[]{Tuple2.of(2, "two"), Tuple2.of(3, "three")},
                          PersistentTreeMap.of(1, "one").assoc(2, "two").assoc(3, "three").tail()
                                           .map((u) -> Tuple2.of(u.getKey(), u.getValue())).toTypedArray());

        assertTrue(Sequence.emptySequence().equals(PersistentTreeMap.of(1, "one").tail()));
    }

    public void friendlierArrayEq(Object[] a1, Object[] a2) {
        if (a1 == null) {
            assertNull(a2);
            return;
        } else if (a2 == null) {
            assertNull(a1);
            return;
        }
        assertEquals(a1.length, a2.length);
        for (int i = 0; i < a1.length; i++) {
            assertTrue(a1[i].equals(a2[i]));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void subMapEx() {
        PersistentTreeMap.<Integer,Integer>empty().subMap(3, 2);
    }

    @Test public void subMap() {
        assertEquals(PersistentTreeMap.of(1, "one").assoc(2, "two").assoc(3, "three"),
                     PersistentTreeMap.of(1, "one").assoc(2, "two").assoc(3, "three").subMap(1, 4));

        assertEquals(PersistentTreeMap.of(1, "one").assoc(2, "two"),
                     PersistentTreeMap.of(1, "one").assoc(2, "two").assoc(3, "three").subMap(1, 3));

        assertEquals(PersistentTreeMap.of(1, "one"),
                     PersistentTreeMap.of(1, "one").assoc(2, "two").assoc(3, "three").subMap(1, 2));

        assertEquals(PersistentTreeMap.empty(),
                     PersistentTreeMap.of(1, "one").assoc(2, "two").assoc(3, "three").subMap(1, 1));

        assertEquals(PersistentTreeMap.of(2, "two").assoc(3, "three"),
                     PersistentTreeMap.of(1, "one").assoc(2, "two").assoc(3, "three").subMap(2, 4));

        assertEquals(PersistentTreeMap.of(3, "three"),
                     PersistentTreeMap.of(1, "one").assoc(2, "two").assoc(3, "three").subMap(3, 4));

        assertEquals(PersistentTreeMap.empty(),
                     PersistentTreeMap.of(1, "one").assoc(2, "two").assoc(3, "three").subMap(4, 4));

        assertEquals(PersistentTreeMap.of(2, "two"),
                     PersistentTreeMap.of(1, "one").assoc(2, "two").assoc(3, "three").subMap(2, 3));


        assertEquals(PersistentTreeMap.of(1, "one").assoc(2, "two").assoc(3, "three"),
                     PersistentTreeMap.of(1, "one").assoc(2, "two").assoc(3, "three").subMap(0, 999999999));
    }

    @Test public void testToString() {
        assertEquals("PersistentTreeMap()",
                     PersistentTreeMap.empty().toString());
        assertEquals("PersistentTreeMap(UnEntry(1,one))",
                     PersistentTreeMap.of(1, "one").toString());
        assertEquals("PersistentTreeMap(UnEntry(1,one),UnEntry(2,two))",
                     PersistentTreeMap.of(1, "one").assoc(2, "two").toString());
        assertEquals("PersistentTreeMap(UnEntry(1,one),UnEntry(2,two),UnEntry(3,three))",
                     PersistentTreeMap.of(1, "one").assoc(2, "two").assoc(3, "three").toString());
        assertEquals("PersistentTreeMap(UnEntry(1,one),UnEntry(2,two),UnEntry(3,three),UnEntry(4,four))",
                     PersistentTreeMap.of(1, "one").assoc(2, "two").assoc(3, "three").assoc(4, "four")
                             .toString());
        assertEquals("PersistentTreeMap(UnEntry(1,one),UnEntry(2,two),UnEntry(3,three),UnEntry(4,four),UnEntry(5,five))",
                     PersistentTreeMap.of(1, "one").assoc(2, "two").assoc(3, "three").assoc(4, "four").assoc(5, "five")
                             .toString());
        assertEquals("PersistentTreeMap(UnEntry(1,one),UnEntry(2,two),UnEntry(3,three),UnEntry(4,four),UnEntry(5,five),...)",
                     PersistentTreeMap.of(1, "one").assoc(2, "two").assoc(3, "three").assoc(4, "four").assoc(5, "five")
                             .assoc(6, "six").toString());
        assertEquals("PersistentTreeMap(UnEntry(1,one),UnEntry(2,two),UnEntry(3,three),UnEntry(4,four),UnEntry(5,five),...)",
                     PersistentTreeMap.of(1, "one").assoc(2, "two").assoc(3, "three").assoc(4, "four").assoc(5, "five")
                             .assoc(6, "six").assoc(7, "seven").toString());
    }

    @Test public void without() {
        PersistentTreeMap<Integer,String> m = PersistentTreeMap.of(1, "one").assoc(2, "two").assoc(3, "three");

        assertEquals(m, m.without(0));

        assertEquals(PersistentTreeMap.of(2, "two").assoc(3, "three"),
                     m.without(1));

        assertEquals(PersistentTreeMap.of(1, "one").assoc(3, "three"),
                     m.without(2));

        assertEquals(PersistentTreeMap.of(1, "one").assoc(2, "two"),
                     m.without(3));

        assertEquals(m, m.without(4));

        assertEquals(PersistentTreeMap.of(3, "three"),
                     m.without(1).without(2));

        assertEquals(PersistentTreeMap.of(1, "one").assoc(3, "three"),
                     m.without(2));

        assertEquals(PersistentTreeMap.of(1, "one").assoc(2, "two"),
                     m.without(3));

        assertEquals(PersistentTreeMap.EMPTY, PersistentTreeMap.<Integer,String>empty().without(4));
    }

    @Test public void lastKey() {
        PersistentTreeMap<Integer,String> m = PersistentTreeMap.of(1, "one").assoc(2, "two").assoc(3, "three");
        assertEquals(Integer.valueOf(3), m.lastKey());
        assertEquals(Integer.valueOf(2), m.without(3).lastKey());
        assertEquals(Integer.valueOf(1), m.without(2).without(3).lastKey());
    }

    @Test(expected = NoSuchElementException.class)
    public void lastKeyEx() { PersistentTreeMap.empty().lastKey(); }

    @Test public void largerMap() {
        PersistentTreeMap<Integer,String> m =
                PersistentTreeMap.of(1, "one").assoc(2, "two").assoc(3, "three").assoc(4, "four").assoc(5, "five")
                        .assoc(6, "six").assoc(7, "seven").assoc(8, "eight").assoc(9, "nine").assoc(10, "ten")
                        .assoc(11, "eleven").assoc(12, "twelve").assoc(13, "thirteen").assoc(14, "fourteen")
                        .assoc(15, "fifteen").assoc(16, "sixteen").assoc(17, "seventeen").assoc(18, "eighteen")
                        .assoc(19, "nineteen").assoc(20, "twenty");
        m = m.without(10);
        m = m.without(9);
        m = m.without(11);
        m = m.assoc(11, "eleven again");
        m = m.assoc(10, "ten again");
        m = m.assoc(9, "nine again");
        m = m.without(1);
        m = m.assoc(1, "one again");
        m = m.assoc(21, "twenty one");
        m = m.without(20);
        m = m.assoc(20, "twenty again");
        assertArrayEquals(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21},
                          m.keySet().toArray());
        assertTrue(UnIterableOrdered.equals(
                Sequence.of("one again", "two", "three", "four", "five", "six", "seven", "eight",
                            "nine again", "ten again", "eleven again", "twelve", "thirteen",
                            "fourteen",
                            "fifteen", "sixteen", "seventeen", "eighteen", "nineteen", "twenty again",
                            "twenty one"),
                m.map(e -> e.getValue())));
    }

    @Test public void entrySet() {
        PersistentTreeMap<Integer,String> m =
                PersistentTreeMap.of(1, "one").assoc(2, "two").assoc(3, "three").assoc(4, "four").assoc(5, "five");
        ImSet<Map.Entry<Integer,String>> s =
                PersistentTreeSet.ofComp((a, b) -> a.getKey() - b.getKey(),
                                         UnMap.UnEntry.of(1, "one"),
                                         UnMap.UnEntry.of(2, "two"),
                                         UnMap.UnEntry.of(3, "three"),
                                         UnMap.UnEntry.of(4, "four"),
                                         UnMap.UnEntry.of(5, "five"));
        assertArrayEquals(s.toArray(),
                          m.entrySet().map((u) -> Tuple2.of(u.getKey(), u.getValue())).toTypedArray());
    }

    @Test public void values() {
        PersistentTreeMap<Integer,String> m1 =
                PersistentTreeMap.of(4, "four").assoc(5, "five").assoc(2, "two").assoc(3, "three").assoc(1, "one");

        assertArrayEquals(new String[]{"one", "two", "three", "four", "five"},
                          m1.map(e -> e.getValue()).toTypedArray());

//        assertTrue(m.values().equals(Arrays.asList("one", "two", "three", "four", "five")));
        assertNotEquals(0, m1.values().hashCode());
        assertNotEquals(m1.values().hashCode(), PersistentTreeMap.of(4, "four").assoc(5, "five").hashCode());

        System.out.println("m1.values(): " + m1.values());
        PersistentTreeMap<Integer,String> m2 = PersistentTreeMap.of(4, "four").assoc(2, "two").assoc(5, "five").assoc(1, "one").assoc(3, "three");
        System.out.println("m2.values(): " + m2.values());

        assertEquals(m1.values().hashCode(),
                     m2.values().hashCode());

    }

    @Test public void testImMap10() {
        int max = 10;
        Map<Integer,String> a = PersistentTreeMap.of(1, "One", 2, "Two", 3, "Three", 4, "Four", 5, "Five", 6, "Six",
                                                     7, "Seven", 8, "Eight", 9, "Nine", 10, "Ten");
        StaticImportsTest.mapHelper(a, max);
        Map<Integer,String> b = PersistentTreeMap.ofSkipNull(
                Tuple2.of(1, "One"), Tuple2.of(2, "Two"), Tuple2.of(3, "Three"),
                Tuple2.of(4, "Four"), Tuple2.of(5, "Five"), Tuple2.of(6, "Six"),
                Tuple2.of(7, "Seven"), Tuple2.of(8, "Eight"), Tuple2.of(9, "Nine"),
                Tuple2.of(10, "Ten"));
        StaticImportsTest.mapHelper(b, max);
        Map<Integer,String> c = PersistentTreeMap.ofComp(Function2.defaultComparator(),
                                                         1, "One", 2, "Two", 3, "Three", 4, "Four", 5, "Five", 6, "Six",
                                                         7, "Seven", 8, "Eight", 9, "Nine", 10, "Ten");
        assertEquals(a, b);
        assertEquals(b, a);
        assertEquals(a, c);
        assertEquals(c, a);
        assertEquals(b, c);
        assertEquals(c, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertEquals(a.hashCode(), c.hashCode());
        StaticImportsTest.mapHelperOdd(PersistentTreeMap.ofSkipNull(
                Tuple2.of(1, "One"), null, Tuple2.of(3, "Three"), null, Tuple2.of(5, "Five"), null,
                Tuple2.of(7, "Seven"), null, Tuple2.of(9, "Nine"), null), max);
        StaticImportsTest.mapHelperEven(PersistentTreeMap.ofSkipNull(
                null, Tuple2.of(2, "Two"), null, Tuple2.of(4, "Four"), null,
                Tuple2.of(6, "Six"), null, Tuple2.of(8, "Eight"), null, Tuple2.of(10, "Ten")), max);
    }

    @Test public void testImMap9() {
        int max = 9;
        Map<Integer,String> a = PersistentTreeMap.of(1, "One", 2, "Two", 3, "Three", 4, "Four", 5, "Five", 6, "Six",
                                                     7, "Seven", 8, "Eight", 9, "Nine");
        StaticImportsTest.mapHelper(a, max);
        Map<Integer,String> b = PersistentTreeMap.ofSkipNull(
                Tuple2.of(1, "One"), Tuple2.of(2, "Two"), Tuple2.of(3, "Three"),
                Tuple2.of(4, "Four"), Tuple2.of(5, "Five"), Tuple2.of(6, "Six"),
                Tuple2.of(7, "Seven"), Tuple2.of(8, "Eight"), Tuple2.of(9, "Nine"));
        StaticImportsTest.mapHelper(b, max);
        Map<Integer,String> c = PersistentTreeMap.ofComp(Function2.defaultComparator(),
                                                         1, "One", 2, "Two", 3, "Three", 4, "Four", 5, "Five", 6, "Six",
                                                         7, "Seven", 8, "Eight", 9, "Nine");
        assertEquals(a, b);
        assertEquals(b, a);
        assertEquals(a, c);
        assertEquals(c, a);
        assertEquals(b, c);
        assertEquals(c, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertEquals(a.hashCode(), c.hashCode());
        StaticImportsTest.mapHelperOdd(PersistentTreeMap.ofSkipNull(
                Tuple2.of(1, "One"), null, Tuple2.of(3, "Three"), null, Tuple2.of(5, "Five"), null,
                Tuple2.of(7, "Seven"), null, Tuple2.of(9, "Nine")), max);
        StaticImportsTest.mapHelperEven(PersistentTreeMap.ofSkipNull(
                null, Tuple2.of(2, "Two"), null, Tuple2.of(4, "Four"), null,
                Tuple2.of(6, "Six"), null, Tuple2.of(8, "Eight"), null), max);
    }

    @Test public void testImMap8() {
        int max = 8;
        Map<Integer,String> a = PersistentTreeMap.of(1, "One", 2, "Two", 3, "Three", 4, "Four", 5, "Five", 6, "Six",
                                                     7, "Seven", 8, "Eight");
        StaticImportsTest.mapHelper(a, max);
        Map<Integer,String> b = PersistentTreeMap.ofSkipNull(
                Tuple2.of(1, "One"), Tuple2.of(2, "Two"), Tuple2.of(3, "Three"),
                Tuple2.of(4, "Four"), Tuple2.of(5, "Five"), Tuple2.of(6, "Six"),
                Tuple2.of(7, "Seven"), Tuple2.of(8, "Eight"));
        StaticImportsTest.mapHelper(b, max);
        Map<Integer,String> c = PersistentTreeMap.ofComp(Function2.defaultComparator(),
                                                         1, "One", 2, "Two", 3, "Three", 4, "Four", 5, "Five", 6, "Six",
                                                         7, "Seven", 8, "Eight");
        assertEquals(a, b);
        assertEquals(b, a);
        assertEquals(a, c);
        assertEquals(c, a);
        assertEquals(b, c);
        assertEquals(c, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertEquals(a.hashCode(), c.hashCode());
        StaticImportsTest.mapHelperOdd(PersistentTreeMap.ofSkipNull(
                Tuple2.of(1, "One"), null, Tuple2.of(3, "Three"), null, Tuple2.of(5, "Five"), null,
                Tuple2.of(7, "Seven"), null), max);
        StaticImportsTest.mapHelperEven(PersistentTreeMap.ofSkipNull(
                null, Tuple2.of(2, "Two"), null, Tuple2.of(4, "Four"), null,
                Tuple2.of(6, "Six"), null, Tuple2.of(8, "Eight")), max);
    }

    @Test public void testImMap7() {
        int max = 7;
        Map<Integer,String> a = PersistentTreeMap.of(1, "One", 2, "Two", 3, "Three", 4, "Four", 5, "Five", 6, "Six",
                                                     7, "Seven");
        StaticImportsTest.mapHelper(a, max);
        Map<Integer,String> b = PersistentTreeMap.ofSkipNull(
                Tuple2.of(1, "One"), Tuple2.of(2, "Two"), Tuple2.of(3, "Three"),
                Tuple2.of(4, "Four"), Tuple2.of(5, "Five"), Tuple2.of(6, "Six"),
                Tuple2.of(7, "Seven"));
        StaticImportsTest.mapHelper(b, max);
        Map<Integer,String> c = PersistentTreeMap.ofComp(Function2.defaultComparator(),
                                                         1, "One", 2, "Two", 3, "Three", 4, "Four", 5, "Five", 6, "Six",
                                                         7, "Seven");
        assertEquals(a, b);
        assertEquals(b, a);
        assertEquals(a, c);
        assertEquals(c, a);
        assertEquals(b, c);
        assertEquals(c, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertEquals(a.hashCode(), c.hashCode());
        StaticImportsTest.mapHelperOdd(PersistentTreeMap.ofSkipNull(
                Tuple2.of(1, "One"), null, Tuple2.of(3, "Three"), null, Tuple2.of(5, "Five"), null,
                Tuple2.of(7, "Seven")), max);
        StaticImportsTest.mapHelperEven(PersistentTreeMap.ofSkipNull(
                null, Tuple2.of(2, "Two"), null, Tuple2.of(4, "Four"), null,
                Tuple2.of(6, "Six"), null), max);
    }

    @Test public void testImMap6() {
        int max = 6;
        Map<Integer,String> a = PersistentTreeMap.of(1, "One", 2, "Two", 3, "Three", 4, "Four", 5, "Five", 6, "Six");
        StaticImportsTest.mapHelper(a, max);
        Map<Integer,String> b = PersistentTreeMap.ofSkipNull(
                Tuple2.of(1, "One"), Tuple2.of(2, "Two"), Tuple2.of(3, "Three"),
                Tuple2.of(4, "Four"), Tuple2.of(5, "Five"), Tuple2.of(6, "Six"));
        StaticImportsTest.mapHelper(b, max);
        Map<Integer,String> c = PersistentTreeMap.ofComp(Function2.defaultComparator(),
                                                         1, "One", 2, "Two", 3, "Three", 4, "Four", 5, "Five",
                                                         6, "Six");
        assertEquals(a, b);
        assertEquals(b, a);
        assertEquals(a, c);
        assertEquals(c, a);
        assertEquals(b, c);
        assertEquals(c, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertEquals(a.hashCode(), c.hashCode());
        StaticImportsTest.mapHelperOdd(PersistentTreeMap.ofSkipNull(
                Tuple2.of(1, "One"), null, Tuple2.of(3, "Three"), null, Tuple2.of(5, "Five"), null), max);
        StaticImportsTest.mapHelperEven(PersistentTreeMap.ofSkipNull(
                null, Tuple2.of(2, "Two"), null, Tuple2.of(4, "Four"), null,
                Tuple2.of(6, "Six")), max);
    }

    @Test public void testImMap5() {
        int max = 5;
        Map<Integer,String> a = PersistentTreeMap.of(1, "One", 2, "Two", 3, "Three", 4, "Four", 5, "Five");
        StaticImportsTest.mapHelper(a, max);
        Map<Integer,String> b = PersistentTreeMap.ofSkipNull(
                Tuple2.of(1, "One"), Tuple2.of(2, "Two"), Tuple2.of(3, "Three"),
                Tuple2.of(4, "Four"), Tuple2.of(5, "Five"));
        StaticImportsTest.mapHelper(b, max);
        Map<Integer,String> c = PersistentTreeMap.ofComp(Function2.defaultComparator(),
                                                         1, "One", 2, "Two", 3, "Three", 4, "Four", 5, "Five");
        assertEquals(a, b);
        assertEquals(b, a);
        assertEquals(a, c);
        assertEquals(c, a);
        assertEquals(b, c);
        assertEquals(c, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertEquals(a.hashCode(), c.hashCode());
        StaticImportsTest.mapHelperOdd(PersistentTreeMap.ofSkipNull(
                Tuple2.of(1, "One"), null, Tuple2.of(3, "Three"), null, Tuple2.of(5, "Five")), max);
        StaticImportsTest.mapHelperEven(PersistentTreeMap.ofSkipNull(
                null, Tuple2.of(2, "Two"), null, Tuple2.of(4, "Four"), null), max);
    }

    @Test public void testImMap4() {
        int max = 4;
        Map<Integer,String> a = PersistentTreeMap.of(1, "One", 2, "Two", 3, "Three", 4, "Four");
        StaticImportsTest.mapHelper(a, max);
        Map<Integer,String> b = PersistentTreeMap.ofSkipNull(
                Tuple2.of(1, "One"), Tuple2.of(2, "Two"), Tuple2.of(3, "Three"),
                Tuple2.of(4, "Four"));
        StaticImportsTest.mapHelper(b, max);
        Map<Integer,String> c = PersistentTreeMap.ofComp(Function2.defaultComparator(),
                                                         1, "One", 2, "Two", 3, "Three", 4, "Four");
        assertEquals(a, b);
        assertEquals(b, a);
        assertEquals(a, c);
        assertEquals(c, a);
        assertEquals(b, c);
        assertEquals(c, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertEquals(a.hashCode(), c.hashCode());
        StaticImportsTest.mapHelperOdd(PersistentTreeMap.ofSkipNull(
                Tuple2.of(1, "One"), null, Tuple2.of(3, "Three"), null), max);
        StaticImportsTest.mapHelperEven(PersistentTreeMap.ofSkipNull(
                null, Tuple2.of(2, "Two"), null, Tuple2.of(4, "Four")), max);
    }

    @Test public void testImMap3() {
        int max = 3;
        Map<Integer,String> a = PersistentTreeMap.of(1, "One", 2, "Two", 3, "Three");
        StaticImportsTest.mapHelper(a, max);
        Map<Integer,String> b = PersistentTreeMap.ofSkipNull(
                Tuple2.of(1, "One"), Tuple2.of(2, "Two"), Tuple2.of(3, "Three"));
        StaticImportsTest.mapHelper(b, max);
        Map<Integer,String> c = PersistentTreeMap.ofComp(Function2.defaultComparator(),
                                                         1, "One", 2, "Two", 3, "Three");
        assertEquals(a, b);
        assertEquals(b, a);
        assertEquals(a, c);
        assertEquals(c, a);
        assertEquals(b, c);
        assertEquals(c, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertEquals(a.hashCode(), c.hashCode());
        StaticImportsTest.mapHelperOdd(PersistentTreeMap.ofSkipNull(
                Tuple2.of(1, "One"), null, Tuple2.of(3, "Three")), max);
        StaticImportsTest.mapHelperEven(PersistentTreeMap.ofSkipNull(
                null, Tuple2.of(2, "Two"), null), max);
    }

    @Test public void testImMap2() {
        int max = 2;
        Map<Integer,String> a = PersistentTreeMap.of(1, "One", 2, "Two");
        StaticImportsTest.mapHelper(a, max);
        Map<Integer,String> b = PersistentTreeMap.ofSkipNull(
                Tuple2.of(1, "One"), Tuple2.of(2, "Two"));
        StaticImportsTest.mapHelper(b, max);
        Map<Integer,String> c = PersistentTreeMap.ofComp(Function2.defaultComparator(),
                                                         1, "One", 2, "Two");
        assertEquals(a, b);
        assertEquals(b, a);
        assertEquals(a, c);
        assertEquals(c, a);
        assertEquals(b, c);
        assertEquals(c, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertEquals(a.hashCode(), c.hashCode());
        StaticImportsTest.mapHelperOdd(PersistentTreeMap.ofSkipNull(
                Tuple2.of(1, "One"), null), max);
        StaticImportsTest.mapHelperEven(PersistentTreeMap.ofSkipNull(
                null, Tuple2.of(2, "Two")), max);
    }

    @Test public void testImMap1() {
        int max = 1;
        Map<Integer,String> a = PersistentTreeMap.of(1, "One");
        StaticImportsTest.mapHelper(a, max);
        Map<Integer,String> b = PersistentTreeMap.ofSkipNull(
                Tuple2.of(1, "One"));
        StaticImportsTest.mapHelper(b, max);
        Map<Integer,String> c = PersistentTreeMap.ofComp(Function2.defaultComparator(),
                                                         1, "One");
        assertEquals(a, b);
        assertEquals(b, a);
        assertEquals(a, c);
        assertEquals(c, a);
        assertEquals(b, c);
        assertEquals(c, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertEquals(a.hashCode(), c.hashCode());
        StaticImportsTest.mapHelperOdd(PersistentTreeMap.ofSkipNull(
                Tuple2.of(1, "One")), max);
        StaticImportsTest.mapHelperEven(PersistentTreeMap.ofSkipNull((Map.Entry<Integer,String>) null), max);
    }

    @Test public void testImMap0() {
        int max = 0;
        Map<Integer,String> b = PersistentTreeMap.ofSkipNull();
        StaticImportsTest.mapHelper(b, max);
        Map<Integer,String> c = PersistentTreeMap.empty(Function2.defaultComparator());
        assertEquals(b, c);
        assertEquals(c, b);
        assertEquals(b.hashCode(), c.hashCode());
        StaticImportsTest.mapHelperOdd(PersistentTreeMap.ofSkipNull(), max);
        StaticImportsTest.mapHelperEven(PersistentTreeMap.ofSkipNull(), max);
    }
}
