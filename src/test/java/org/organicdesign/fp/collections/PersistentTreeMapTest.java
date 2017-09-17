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

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.SortedMap;
import java.util.TreeMap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.organicdesign.fp.FunctionUtilsTest;
import org.organicdesign.fp.TestUtilities;
import org.organicdesign.fp.function.Fn1;
import org.organicdesign.fp.tuple.Tuple2;

import static org.junit.Assert.*;
import static org.organicdesign.fp.FunctionUtils.ordinal;
import static org.organicdesign.fp.StaticImports.*;
import static org.organicdesign.fp.TestUtilities.compareIterators;
import static org.organicdesign.fp.TestUtilities.serializeDeserialize;
import static org.organicdesign.fp.collections.PersistentTreeMap.empty;
import static org.organicdesign.testUtils.EqualsContract.equalsDistinctHashCode;
import static org.organicdesign.testUtils.EqualsContract.equalsSameHashCode;

@RunWith(JUnit4.class)
public class PersistentTreeMapTest {
    private static <K,V> void compareEntryIterSer(Iterator<? extends Map.Entry<K,V>> cIter,
                                                  Iterator<? extends Map.Entry<K,V>> tIter) {
        while (cIter.hasNext()) {
            assertTrue(tIter.hasNext());
            Map.Entry<K,V> cNext = cIter.next();
            Map.Entry<K,V> tNext = tIter.next();
            assertEquals(cNext, tNext);
            assertEquals(tNext, cNext);

            Map.Entry<K,V> sNext = serializeDeserialize(tNext);
            assertEquals(cNext, sNext);
            assertEquals(sNext, cNext);
        }
        assertFalse(tIter.hasNext());
    }

    @Test public void assocAndGet() {
        PersistentTreeMap<String,Integer> m1 = empty();
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
        PersistentTreeMap<String,Integer> m1 = PersistentTreeMap.of(vec(
                tup("c", 1),
                tup("b", 2),
                tup("a", 3)));

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
                          m1.map(e -> e.getValue()).toMutList().toArray());

        assertArrayEquals(new String[]{"a", "b", "c"},
                          PersistentTreeMap.of(vec(tup("a", 3),
                                                   tup("b", 2),
                                                   tup("c", 1)))
                                           .keySet().toArray());


        {
            UnmodIterator<Integer> iter = PersistentTreeMap.of(vec(tup("b", 2)))
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

        PersistentTreeMap<String,Integer> m2 = PersistentTreeMap.of(vec(tup("c", 3)))
                                                                .assoc("b", 2)
                                                                .assoc("a", 1);
        UnmodIterator<UnmodMap.UnEntry<String,Integer>> iter = m2.iterator();
        UnmodMap.UnEntry<String,Integer> next = iter.next();
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
                PersistentTreeMap.ofComp(String.CASE_INSENSITIVE_ORDER.reversed(),
                                         vec(tup("a", 1),
                                             tup("b", 2),
                                             tup("c", 3)));
        UnmodIterator<UnmodMap.UnEntry<String,Integer>> iter2 = m3.iterator();

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
        assertNotEquals(Equator.defaultComparator(), m3.comparator());
    }

    @Test public void hashCodeAndEquals() {
        equalsDistinctHashCode(PersistentTreeMap.of(vec(tup("one", 1)))
                                                .assoc("two", 2).assoc("three", 3),
                               PersistentTreeMap.of(vec(tup("three", 3)))
                                                .assoc("two", 2).assoc("one", 1),
                               PersistentTreeMap.of(vec(tup("two", 2),
                                                        tup("three", 3),
                                                        tup("one", 1))),
                               PersistentTreeMap.of(vec(tup("two", 2),
                                                        tup("three", 3),
                                                        tup("four", 4))));

        SortedMap<String,Integer> control = new TreeMap<>();
        control.put("one", 1);
        control.put("two", 2);
        control.put("three", 3);

        ImSortedMap<String,Integer> test = PersistentTreeMap.of(vec(tup("one", 1),
                                                                tup("two", 2),
                                                                tup("three", 3)));

        assertEquals(control.hashCode(), test.hashCode());
        assertEquals(control.hashCode(), serializeDeserialize(test).hashCode());

        compareIterators(control.entrySet().iterator(), test.iterator());

        assertTrue(test.equals(control));
        assertTrue(control.equals(test));

        assertTrue(serializeDeserialize(test).equals(control));
        assertTrue(control.equals(serializeDeserialize(test)));

        equalsDistinctHashCode(test,
                               control,
                               Collections.unmodifiableSortedMap(control),
                               PersistentTreeMap.of(vec(tup("two", 2),
                                                        tup("three", 3),
                                                        tup("four", 4))));

        equalsDistinctHashCode(PersistentTreeMap.of(vec(tup("one", 1)))
                                                .assoc("two", 2).assoc("three", 3),
                               PersistentTreeMap.of(vec(tup("three", 3)))
                                                .assoc("two", 2).assoc("one", 1),
                               PersistentTreeMap.of(vec(tup("two", 2),
                                                        tup("three", 3),
                                                        tup("one", 1))),
                               PersistentTreeMap.of(vec(tup("zne", 1),
                                                        tup("two", 2),
                                                        tup("three", 3))));

        equalsDistinctHashCode(PersistentTreeMap.of(vec(tup("one", 1)))
                                                .assoc("two", 2).assoc("three", 3),
                               PersistentTreeMap.of(vec(tup("three", 3)))
                                                .assoc("two", 2).assoc("one", 1),
                               PersistentTreeMap.of(vec(tup("two", 2),
                                                        tup("three", 3),
                                                        tup("one", 1))),
                               PersistentTreeMap.of(vec(tup("one", 1),
                                                        tup("two", 2),
                                                        tup("three", 2))));

        equalsSameHashCode(PersistentTreeMap.of(vec(tup("one", 1)))
                                            .assoc("two", 2).assoc("three", 3),
                           PersistentTreeMap.of(vec(tup("three", 3)))
                                            .assoc("two", 2).assoc("one", 1),
                           PersistentTreeMap.of(vec(tup("two", 2), tup("three", 3), tup("one", 1))),
                           PersistentTreeMap.of(vec(tup(1, "one"),
                                                    tup(2, "two"),
                                                    tup(3, "three"))));
    }

    @Test public void compareToHashMapTest() {
        Map<String,Integer> control = new HashMap<>();
        control.put("one", 1);
        control.put("two", 2);
        control.put("three", 3);

        ImSortedMap<String,Integer> test = PersistentTreeMap.of(control.entrySet());

        assertEquals(control.hashCode(), test.hashCode());
        assertTrue(control.equals(test));
        assertTrue(test.equals(control));

        ImSortedMap<String,Integer> ser = serializeDeserialize(test);

        assertEquals(control.hashCode(), ser.hashCode());
        assertTrue(control.equals(ser));
        assertTrue(ser.equals(control));

        equalsDistinctHashCode(control, test, ser, map(tup("one", 1), tup("twp", 2), tup("three", 3)));
    }

    @Test public void buildLeftyTest() {
        SortedMap<String,Integer> control = new TreeMap<>();
        control.put("a", 1);
        control.put("b", 2);
        control.put("c", 3);
        control.put("d", 4);
        control.put("e", 5);
        control.put("f", 6);
        control.put("g", 7);
        control.put("h", 8);
        control.put("i", 9);
        control.put("j", 10);
        control.put("k", 11);
        control.put("l", 12);
        control.put("m", 13);
        control.put("n", 14);
        control.put("o", 15);
        control.put("p", 16);
        control.put("q", 17);
        control.put("r", 18);
        control.put("s", 19);
        control.put("t", 20);
        control.put("u", 21);
        control.put("v", 22);
        control.put("w", 23);
        control.put("x", 24);
        control.put("y", 25);
        control.put("z", 26);

        ImSortedMap<String,Integer> test = PersistentTreeMap.of(control.entrySet());

        control.remove("g");
        test = test.without("g");

        assertEquals(control.hashCode(), test.hashCode());
        assertTrue(control.equals(test));
        assertTrue(test.equals(control));

        ImSortedMap<String,Integer> ser = serializeDeserialize(test);

        assertEquals(control.hashCode(), ser.hashCode());
        assertTrue(control.equals(ser));
        assertTrue(ser.equals(control));

        equalsDistinctHashCode(control, test, ser, test.assoc("v", null));

        compareIterators(control.entrySet().iterator(), test.iterator());
        compareIterators(control.entrySet().iterator(), ser.iterator());

        compareEntryIterSer(control.entrySet().iterator(), test.iterator());

        HashMap<String,Integer> hash = new HashMap<>();
        hash.putAll(control);
        equalsDistinctHashCode(hash, test, ser, test.assoc("v", null));

        hash = new HashMap<>();
        hash.putAll(test.assoc("v", null));
        equalsDistinctHashCode(control, test, ser, hash);

        control.put("zz", null);
        test = test.assoc("zz", null);

        control.remove("a");
        test = test.without("a");
        compareEntryIterSer(control.entrySet().iterator(), test.iterator());

        control.remove("z");
        test = test.without("z");
        compareEntryIterSer(control.entrySet().iterator(), test.iterator());
    }

    @Test public void buildRightyTest() {
        SortedMap<Integer,String> control = new TreeMap<>();
        ImSortedMap<Integer,String> test = PersistentTreeMap.empty();
        for (int i = 26; i > 0; i--) {
            control.put(i, ordinal(i));
            test = test.assoc(i, ordinal(i));
        }

        control.put(27, null);
        test = test.assoc(27, null);

        control.remove(13);
        test = test.without(13);

        assertEquals(control.hashCode(), test.hashCode());
        assertTrue(control.equals(test));
        assertTrue(test.equals(control));

        ImSortedMap<Integer,String> ser = serializeDeserialize(test);

        assertEquals(control.hashCode(), ser.hashCode());
        assertTrue(control.equals(ser));
        assertTrue(ser.equals(control));

        equalsDistinctHashCode(control, test, ser, test.assoc(20, null));

        HashMap<Integer,String> hash = new HashMap<>();
        hash.putAll(control);
        equalsDistinctHashCode(hash, test, ser, test.assoc(20, null));

        hash = new HashMap<>();
        hash.putAll(test.assoc(20, null));
        equalsDistinctHashCode(control, test, ser, hash);

        compareIterators(control.entrySet().iterator(), test.iterator());
        compareIterators(control.entrySet().iterator(), ser.iterator());

        compareEntryIterSer(control.entrySet().iterator(), test.iterator());

        control.put(0, null);
        test = test.assoc(0, null);

        control.remove(1);
        test = test.without(1);
        compareEntryIterSer(control.entrySet().iterator(), test.iterator());

        control.remove(26);
        test = test.without(26);
        compareEntryIterSer(control.entrySet().iterator(), test.iterator());
    }

    @Test public void sequence() {
        assertEquals(PersistentTreeMap.of(vec(tup(1, "one")))
                                      .assoc(2, "two").assoc(3, "three"),
                     PersistentTreeMap.of(vec(tup(1, "one")))
                                      .assoc(2, "two").assoc(3, "three").tailMap(0));

        assertEquals(PersistentTreeMap.of(vec(tup(1, "one"))).assoc(2, "two").assoc(3, "three"),
                     PersistentTreeMap.of(vec(tup(1, "one"))).assoc(2, "two").assoc(3, "three")
                                      .tailMap(1));

        assertEquals(PersistentTreeMap.of(vec(tup(2, "two"))).assoc(3, "three"),
                     PersistentTreeMap.of(vec(tup(1, "one"))).assoc(2, "two").assoc(3, "three")
                                      .tailMap(2));

        assertEquals(PersistentTreeMap.of(vec(tup(3, "three"))),
                     PersistentTreeMap.of(vec(tup(1, "one"))).assoc(2, "two").assoc(3, "three")
                                      .tailMap(3));

        assertEquals(empty(),
                     PersistentTreeMap.of(vec(tup(1, "one"))).assoc(2, "two").assoc(3, "three")
                                      .tailMap(4));

        assertEquals(empty(),
                     PersistentTreeMap.of(vec(tup(1, "one"))).assoc(2, "two").assoc(3, "three")
                                      .tailMap(999999999));

        assertArrayEquals(new UnmodMap.UnEntry[]{tup(2, "two"), tup(3, "three")},
                          PersistentTreeMap.of(vec(tup(1, "one"))).assoc(2, "two").assoc(3, "three")
                                           .drop(1)
                                           .map((u) -> tup(u.getKey(), u.getValue()))
                                           .toMutList().toArray());

        assertFalse(PersistentTreeMap.of(vec(tup(1, "one"))).drop(1).iterator().hasNext());
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
        assertEquals(PersistentTreeMap.of(vec(tup(1, "one"))).assoc(2, "two").assoc(3, "three"),
                     PersistentTreeMap.of(vec(tup(1, "one"))).assoc(2, "two").assoc(3, "three").subMap(1, 4));

        assertEquals(PersistentTreeMap.of(vec(tup(1, "one"))).assoc(2, "two"),
                     PersistentTreeMap.of(vec(tup(1, "one"))).assoc(2, "two").assoc(3, "three").subMap(1, 3));

        assertEquals(PersistentTreeMap.of(vec(tup(1, "one"))),
                     PersistentTreeMap.of(vec(tup(1, "one"))).assoc(2, "two").assoc(3, "three").subMap(1, 2));

        assertEquals(empty(),
                     PersistentTreeMap.of(vec(tup(1, "one"))).assoc(2, "two").assoc(3, "three").subMap(1, 1));

        assertEquals(PersistentTreeMap.of(vec(tup(2, "two"))).assoc(3, "three"),
                     PersistentTreeMap.of(vec(tup(1, "one"))).assoc(2, "two").assoc(3, "three").subMap(2, 4));

        assertEquals(PersistentTreeMap.of(vec(tup(3, "three"))),
                     PersistentTreeMap.of(vec(tup(1, "one"))).assoc(2, "two").assoc(3, "three").subMap(3, 4));

        assertEquals(empty(),
                     PersistentTreeMap.of(vec(tup(1, "one"))).assoc(2, "two").assoc(3, "three").subMap(4, 4));

        assertEquals(PersistentTreeMap.of(vec(tup(2, "two"))),
                     PersistentTreeMap.of(vec(tup(1, "one"))).assoc(2, "two").assoc(3, "three").subMap(2, 3));


        assertEquals(PersistentTreeMap.of(vec(tup(1, "one"))).assoc(2, "two").assoc(3, "three"),
                     PersistentTreeMap.of(vec(tup(1, "one"))).assoc(2, "two").assoc(3, "three").subMap(0, 999999999));
    }

    @Test public void testToString() {
        assertEquals("PersistentTreeMap()",
                     empty().toString());
        assertEquals("PersistentTreeMap(1=\"one\")",
                     PersistentTreeMap.of(vec(tup(1, "one"))).toString());
        assertEquals("PersistentTreeMap(1=\"one\",2=\"two\")",
                     PersistentTreeMap.of(vec(tup(1, "one"))).assoc(2, "two").toString());
        assertEquals("PersistentTreeMap(1=\"one\",2=\"two\",3=\"three\")",
                     PersistentTreeMap.of(vec(tup(1, "one"))).assoc(2, "two").assoc(3, "three")
                                      .toString());
        assertEquals("PersistentTreeMap(1=\"one\",2=\"two\",3=\"three\"," +
                     "4=\"four\")",
                     PersistentTreeMap.of(vec(tup(1, "one"))).assoc(2, "two").assoc(3, "three")
                                      .assoc(4, "four").toString());
        assertEquals("PersistentTreeMap(1=\"one\",2=\"two\",3=\"three\"," +
                     "4=\"four\",5=\"five\")",
                     PersistentTreeMap.of(vec(tup(1, "one"))).assoc(2, "two").assoc(3, "three")
                                      .assoc(4, "four").assoc(5, "five").toString());
        assertEquals("PersistentTreeMap(1=\"one\",2=\"two\",3=\"three\",4=\"four\",5=\"five\"," +
                     "6=\"six\")",
                     PersistentTreeMap.of(vec(tup(1, "one"))).assoc(2, "two").assoc(3, "three")
                                      .assoc(4, "four").assoc(5, "five").assoc(6, "six")
                                      .toString());
        assertEquals("PersistentTreeMap(1=\"one\",2=\"two\",3=\"three\",4=\"four\",5=\"five\"," +
                     "6=\"six\",7=\"seven\")",
                     PersistentTreeMap.of(vec(tup(1, "one"))).assoc(2, "two").assoc(3, "three")
                                      .assoc(4, "four").assoc(5, "five").assoc(6, "six")
                                      .assoc(7, "seven").toString());
    }

    @Test public void without() {
        PersistentTreeMap<Integer,String> m =
                PersistentTreeMap.of(vec(tup(1, "one"))).assoc(2, "two").assoc(3, "three");

        assertEquals(m, m.without(0));

        assertEquals(PersistentTreeMap.of(vec(tup(2, "two"))).assoc(3, "three"),
                     m.without(1));

        assertEquals(PersistentTreeMap.of(vec(tup(1, "one"))).assoc(3, "three"),
                     m.without(2));

        assertEquals(PersistentTreeMap.of(vec(tup(1, "one"))).assoc(2, "two"),
                     m.without(3));

        assertEquals(m, m.without(4));

        assertEquals(PersistentTreeMap.of(vec(tup(3, "three"))),
                     m.without(1).without(2));

        assertEquals(PersistentTreeMap.of(vec(tup(1, "one"))).assoc(3, "three"),
                     m.without(2));

        assertEquals(PersistentTreeMap.of(vec(tup(1, "one"))).assoc(2, "two"),
                     m.without(3));

        assertEquals(PersistentTreeMap.EMPTY, PersistentTreeMap.<Integer,String>empty().without(4));
    }

    @Test public void lastKey() {
        PersistentTreeMap<Integer,String> m = PersistentTreeMap.of(vec(tup(1, "one")))
                                                               .assoc(2, "two").assoc(3, "three");
        assertEquals(Integer.valueOf(3), m.lastKey());
        assertEquals(Integer.valueOf(2), m.without(3).lastKey());
        assertEquals(Integer.valueOf(1), m.without(2).without(3).lastKey());
    }

    @Test(expected = NoSuchElementException.class)
    public void lastKeyEx() { empty().lastKey(); }

    @Test public void serializeEmptyTest() {
        PersistentTreeMap<String,Integer> e = empty();
        assertEquals(1, e.assoc("hello", 99).size());
        assertEquals(1, serializeDeserialize(e).assoc("hello", 99).size());
    }

    @Test public void largerMap() throws Exception {
        PersistentTreeMap<Integer,String> m =
                PersistentTreeMap.of(vec(tup(1, "one")))
                                 .assoc(2, "two").assoc(3, "three").assoc(4, "four").assoc(5, "five")
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

        assertArrayEquals(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21},
                          serializeDeserialize(m).keySet().toArray());

        assertArrayEquals(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21},
                          serializeDeserialize(m.keySet()).toArray());

        ImList v = vec("one again", "two", "three", "four", "five", "six", "seven", "eight",
                       "nine again", "ten again", "eleven again", "twelve", "thirteen",
                       "fourteen",
                       "fifteen", "sixteen", "seventeen", "eighteen", "nineteen", "twenty again",
                       "twenty one");

        assertEquals(v, m.map(e -> e.getValue()).toImList());

        assertEquals(v, serializeDeserialize(m).map(e -> e.getValue()).toImList());

        // TODO: 2016-08-28 xform is not currently serializable.  Not sure if it should be!
//        assertEquals(v, serializeDeserialize(m.map(e -> e.getValue())).toImList());
    }

    @Test public void biggerTreeMaps() throws Exception {
        int NUM_ITEMS = 300;
        SortedMap<String,Integer> control = new TreeMap<>();
        PersistentTreeMap<String,Integer> test = empty();

        for (int i = 0; i < NUM_ITEMS; i++) {
            String ord = ordinal(i);
            control.put(ord, i);
            test = test.assoc(ord, i);
            assertEquals(control.size(), test.size());
        }

        assertTrue(UnmodSortedIterable.equal(UnmodSortedIterable.castFromSortedMap(control),
                                             test));

        assertTrue(UnmodSortedIterable.equal(UnmodSortedIterable.castFromSortedMap(control),
                                             serializeDeserialize(test)));

        assertEquals(NUM_ITEMS, test.size());

        PersistentTreeMap<String,Integer> ser = serializeDeserialize(test);
        assertEquals(test, ser);

        for (int i = 0; i < NUM_ITEMS; i++) {
            assertEquals(Integer.valueOf(i), test.get(ordinal(i)));
            assertEquals(Integer.valueOf(i), ser.get(ordinal(i)));
        }
        assertNull(test.get(ordinal(NUM_ITEMS)));
        assertNull(ser.get(ordinal(NUM_ITEMS)));

        for (int i = 0; i < NUM_ITEMS; i++) {
            assertTrue(test.containsKey(ordinal(i)));
            assertTrue(ser.containsKey(ordinal(i)));
        }

        assertFalse(test.containsKey(ordinal(NUM_ITEMS)));
        assertFalse(ser.containsKey(ordinal(NUM_ITEMS)));

        for (int i = 0; i < NUM_ITEMS; i++) {
            Integer i2 = Integer.valueOf(i);
            assertTrue(test.containsValue(i2));
            assertTrue(ser.containsValue(i2));
        }
        assertFalse(test.containsValue(Integer.valueOf(NUM_ITEMS)));
        assertFalse(ser.containsValue(Integer.valueOf(NUM_ITEMS)));

        // If you remove a key that's not there, you should get back the original map.
        assertTrue(test == test.without(ordinal(NUM_ITEMS)));
        assertTrue(ser == ser.without(ordinal(NUM_ITEMS)));

        for (int i = 0; i < NUM_ITEMS; i++) {
            assertEquals(NUM_ITEMS - i, test.size());
            test = test.without(ordinal(i));
            assertNull(test.get(ordinal(i)));
            assertFalse(test.containsKey(ordinal(i)));
            assertFalse(test.containsValue(Integer.valueOf(i)));
        }

        assertEquals(0, test.size());

        // Because this map is sorted, building it in reverse is also a useful test.
        for (int i = NUM_ITEMS - 1; i >= 0; i--) {
            test = test.assoc(ordinal(i), i);
            assertEquals(NUM_ITEMS - i, test.size());
        }
        assertEquals(NUM_ITEMS, test.size());

        for (int i = 0; i < NUM_ITEMS; i++) {
            assertEquals(Integer.valueOf(i), test.get(ordinal(i)));
        }
        assertNull(test.get(ordinal(NUM_ITEMS)));

        for (int i = 0; i < NUM_ITEMS; i++) {
            assertTrue(test.containsKey(ordinal(i)));
        }

        assertFalse(test.containsKey(ordinal(NUM_ITEMS)));

        for (int i = 0; i < NUM_ITEMS; i++) {
            assertTrue(test.containsValue(Integer.valueOf(i)));
        }
        assertFalse(test.containsValue(Integer.valueOf(NUM_ITEMS)));

        // If you remove a key that's not there, you should get back the original map.
        assertTrue(test == test.without(ordinal(NUM_ITEMS)));

        for (int i = 0; i < NUM_ITEMS; i++) {
            assertEquals(NUM_ITEMS - i, test.size());
            test = test.without(ordinal(i));
            assertNull(test.get(ordinal(i)));
            assertFalse(test.containsKey(ordinal(i)));
            assertFalse(test.containsValue(Integer.valueOf(i)));
        }
    }

    @Test public void entrySet() throws Exception {

//        PersistentTreeMap<Integer,String> test = empty();
//        System.out.println("test=" + test.debugStr());
//        test = test.assoc(1, "one");
//        System.out.println("test=" + test.debugStr());
//        test = test.assoc(2, "two");
//        System.out.println("test=" + test.debugStr());
//        test = test.assoc(3, "three");
//        System.out.println("test=" + test.debugStr());
//        test = test.assoc(4, "four");
//        System.out.println("test=" + test.debugStr());
//        test = test.assoc(5, "five");
//        System.out.println("test=" + test.debugStr());
//
//        for (Map.Entry<Integer,String> entry : test) {
//            System.out.println("entry:" + entry);
//        }
//
//        System.out.println("Now the entry SET...");
//        for (Map.Entry<Integer,String> entry : test.entrySet()) {
//            System.out.println("entry:" + entry);
//        }
//
//        System.out.println("Now the SERIALIZED map then entrySet...");
//        for (Map.Entry<Integer,String> entry : serializeDeserialize(test).entrySet()) {
//            System.out.println("entry:" + entry);
//        }
//
//        System.out.println("Now the SERIALIZED entry SET...");
//        for (Map.Entry<Integer,String> entry : serializeDeserialize(test.entrySet())) {
//            System.out.println("entry:" + entry);
//        }

        PersistentTreeMap<Integer,String> m =
                PersistentTreeMap.of(vec(tup(1, "one"))).assoc(2, "two").assoc(3, "three").assoc(4, "four").assoc(5, "five");
        ImSortedSet<Map.Entry<Integer,String>> s =
                PersistentTreeSet.ofComp((a, b) -> a.getKey() - b.getKey(),
                                         vec(tup(1, "one"),
                                             tup(2, "two"),
                                             tup(3, "three"),
                                             tup(4, "four"),
                                             tup(5, "five")));
        SortedMap<Integer,String> control = new TreeMap<>();
        control.put(1, "one");
        control.put(2, "two");
        control.put(3, "three");
        control.put(4, "four");
        control.put(5, "five");

        TestUtilities.compareIterators(control.entrySet().iterator(), m.iterator());

        TestUtilities.compareIterators(control.entrySet().iterator(),
                                       serializeDeserialize(m).iterator());

        TestUtilities.compareIterators(control.entrySet().iterator(),
                                       m.entrySet().iterator());

        TestUtilities.compareIterators(control.entrySet().iterator(),
                                       serializeDeserialize(m.entrySet()).iterator());

        assertArrayEquals(s.toArray(),
                          m.entrySet()
                           .map(Fn1.identity())
                           .toMutList()
                           .toArray());

        assertArrayEquals(s.toArray(),
                          serializeDeserialize(m).entrySet()
                                                 .map((u) -> tup(u.getKey(), u.getValue()))
                                                 .toMutList().toArray());

        assertArrayEquals(s.toArray(),
                          serializeDeserialize(m.entrySet())
                                  .map((u) -> tup(u.getKey(), u.getValue()))
                                  .toMutList().toArray());
    }

    @Test public void values() throws Exception {
        PersistentTreeMap<Integer,String> m1 =
                PersistentTreeMap.of(vec(tup(4, "four")))
                                 .assoc(5, "five").assoc(2, "two").assoc(3, "three").assoc(1, "one");

        assertArrayEquals(new String[]{"one", "two", "three", "four", "five"},
                          m1.map(e -> e.getValue()).toMutList().toArray());

        assertArrayEquals(new String[]{"one", "two", "three", "four", "five"},
                          serializeDeserialize(m1).map(e -> e.getValue()).toMutList().toArray());

//        assertTrue(m.values().equals(Arrays.asList("one", "two", "three", "four", "five")));
        assertNotEquals(0, m1.values().hashCode());
        assertNotEquals(m1.values().hashCode(),
                        PersistentTreeMap.of(vec(tup(4, "four"))).assoc(5, "five").hashCode());

//        System.out.println("m1.values(): " + m1.values());
        PersistentTreeMap<Integer,String> m2 = PersistentTreeMap.of(vec(tup(4, "four")))
                                                                .assoc(2, "two").assoc(5, "five")
                                                                .assoc(1, "one").assoc(3, "three");
//        System.out.println("m2.values(): " + m2.values());

        assertEquals(m1.values().hashCode(),
                     m2.values().hashCode());

        assertEquals(m1.values().hashCode(), serializeDeserialize(m1).values().hashCode());
        assertEquals(m1.values().hashCode(), serializeDeserialize(m1.values()).hashCode());
    }

    @Test public void testImMap10() {
        int max = 10;
        Map<Integer,String> a = PersistentTreeMap.of(vec(tup(1, "One"), tup(2, "Two"),
                                                         tup(3, "Three"), tup(4, "Four"),
                                                         tup(5, "Five"), tup(6, "Six"),
                                                         tup(7, "Seven"), tup(8, "Eight"),
                                                         tup(9, "Nine"), tup(10, "Ten")));
        FunctionUtilsTest.mapHelper(a, max);
        Map<Integer,String> b = PersistentTreeMap.of(vec(
                tup(1, "One"), tup(2, "Two"), tup(3, "Three"),
                tup(4, "Four"), tup(5, "Five"), tup(6, "Six"),
                tup(7, "Seven"), tup(8, "Eight"), tup(9, "Nine"),
                tup(10, "Ten")));
        FunctionUtilsTest.mapHelper(b, max);
        Map<Integer,String> c = PersistentTreeMap.ofComp(Equator.defaultComparator(),
                                                         vec(tup(1, "One"), tup(2, "Two"),
                                                             tup(3, "Three"), tup(4, "Four"),
                                                             tup(5, "Five"), tup(6, "Six"),
                                                             tup(7, "Seven"), tup(8, "Eight"),
                                                             tup(9, "Nine"), tup(10, "Ten")));
        assertEquals(a, b);
        assertEquals(b, a);
        assertEquals(a, c);
        assertEquals(c, a);
        assertEquals(b, c);
        assertEquals(c, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertEquals(a.hashCode(), c.hashCode());
        FunctionUtilsTest.mapHelperOdd(
                vec(tup(1, "One"), null, tup(3, "Three"), null, tup(5, "Five"), null,
                    tup(7, "Seven"), null, tup(9, "Nine"), null)
                        .filter(t -> t != null)
                        .toImSortedMap(Equator.defaultComparator(), Fn1.identity()),
                max);
        FunctionUtilsTest.mapHelperEven(
                vec(null, tup(2, "Two"), null, tup(4, "Four"), null,
                    tup(6, "Six"), null, tup(8, "Eight"), null, tup(10, "Ten"))
                        .filter(t -> t != null)
                        .toImSortedMap(Equator.defaultComparator(), Fn1.identity()),
                max);
    }

    @Test public void testImMap1() {
        int max = 1;
        Map<Integer,String> a = PersistentTreeMap.of(vec(tup(1, "One")));
        FunctionUtilsTest.mapHelper(a, max);
        Map<Integer,String> b = vec(tup(1, "One"))
                .toImSortedMap(Equator.defaultComparator(), Fn1.identity());
        FunctionUtilsTest.mapHelper(b, max);
        Map<Integer,String> c = PersistentTreeMap.ofComp(Equator.defaultComparator(),
                                                         vec(tup(1, "One")));
        assertEquals(a, b);
        assertEquals(b, a);
        assertEquals(a, c);
        assertEquals(c, a);
        assertEquals(b, c);
        assertEquals(c, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertEquals(a.hashCode(), c.hashCode());
        FunctionUtilsTest.mapHelperOdd(vec(tup(1, "One"))
                                               .toImSortedMap(Equator.defaultComparator(),
                                                              Fn1.identity()),
                                       max);
        FunctionUtilsTest.mapHelperEven(vec((Map.Entry<Integer,String>) null)
                                                .filter(t -> t != null)
                                                .toImSortedMap(Equator.defaultComparator(),
                                                               Fn1.identity()),
                                        max);
    }

    @Test public void testImMap0() {
        int max = 0;
        Map<Integer,String> b = vec((Tuple2<Integer,String>) null)
                .filter(t -> t != null)
                .toImSortedMap(Equator.defaultComparator(), Fn1.identity());
        FunctionUtilsTest.mapHelper(b, max);
        Map<Integer,String> c = empty(Equator.defaultComparator());
        assertEquals(b, c);
        assertEquals(c, b);
        assertEquals(b.hashCode(), c.hashCode());
        FunctionUtilsTest.mapHelperOdd(vec((Tuple2<Integer,String>) null)
                                               .filter(t -> t != null)
                                               .toImSortedMap(Equator.defaultComparator(),
                                                              Fn1.identity()),
                                       max);
        FunctionUtilsTest.mapHelperEven(vec((Tuple2<Integer,String>) null)
                                                .filter(t -> t != null)
                                                .toImSortedMap(Equator.defaultComparator(),
                                                               Fn1.identity()),
                                        max);
    }
}
