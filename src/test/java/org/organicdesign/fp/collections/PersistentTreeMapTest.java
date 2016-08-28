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

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.SortedMap;
import java.util.TreeMap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.organicdesign.fp.FunctionUtils;
import org.organicdesign.fp.FunctionUtilsTest;
import org.organicdesign.fp.function.Function1;

import static org.junit.Assert.*;
import static org.organicdesign.fp.FunctionUtils.ordinal;
import static org.organicdesign.fp.StaticImports.kv;
import static org.organicdesign.fp.StaticImports.vec;
import static org.organicdesign.fp.TestUtilities.serializeDeserialize;
import static org.organicdesign.testUtils.EqualsContract.equalsDistinctHashCode;
import static org.organicdesign.testUtils.EqualsContract.equalsSameHashCode;

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
        PersistentTreeMap<String,Integer> m1 = PersistentTreeMap.of(vec(
                kv("c", 1),
                kv("b", 2),
                kv("a", 3)));

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
                          m1.map(e -> e.getValue()).toMutableList().toArray());

        assertArrayEquals(new String[]{"a", "b", "c"},
                          PersistentTreeMap.of(vec(kv("a", 3),
                                                   kv("b", 2),
                                                   kv("c", 1)))
                                           .keySet().toArray());


        {
            UnmodIterator<Integer> iter = PersistentTreeMap.of(vec(kv("b", 2)))
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

        PersistentTreeMap<String,Integer> m2 = PersistentTreeMap.of(vec(kv("c", 3)))
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
                                         vec(kv("a", 1),
                                             kv("b", 2),
                                             kv("c", 3)));
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
        equalsDistinctHashCode(PersistentTreeMap.of(vec(kv("one", 1)))
                                                .assoc("two", 2).assoc("three", 3),
                               PersistentTreeMap.of(vec(kv("three", 3)))
                                                .assoc("two", 2).assoc("one", 1),
                               PersistentTreeMap.of(vec(kv("two", 2),
                                                        kv("three", 3),
                                                        kv("one", 1))),
                               PersistentTreeMap.of(vec(kv("two", 2),
                                                        kv("three", 3),
                                                        kv("four", 4))));

        SortedMap<String,Integer> m = new TreeMap<>();
        m.put("one", 1);
        m.put("two", 2);
        m.put("three", 3);

        equalsDistinctHashCode(PersistentTreeMap.of(vec(kv("one", 1),
                                                        kv("two", 2),
                                                        kv("three", 3))),
                               m,
                               FunctionUtils.unmodSortedMap(m),
                               PersistentTreeMap.of(vec(kv("two", 2),
                                                        kv("three", 3),
                                                        kv("four", 4))));

        equalsDistinctHashCode(PersistentTreeMap.of(vec(kv("one", 1)))
                                                .assoc("two", 2).assoc("three", 3),
                               PersistentTreeMap.of(vec(kv("three", 3)))
                                                .assoc("two", 2).assoc("one", 1),
                               PersistentTreeMap.of(vec(kv("two", 2),
                                                        kv("three", 3),
                                                        kv("one", 1))),
                               PersistentTreeMap.of(vec(kv("zne", 1),
                                                        kv("two", 2),
                                                        kv("three", 3))));

        equalsDistinctHashCode(PersistentTreeMap.of(vec(kv("one", 1)))
                                                .assoc("two", 2).assoc("three", 3),
                               PersistentTreeMap.of(vec(kv("three", 3)))
                                                .assoc("two", 2).assoc("one", 1),
                               PersistentTreeMap.of(vec(kv("two", 2),
                                                        kv("three", 3),
                                                        kv("one", 1))),
                               PersistentTreeMap.of(vec(kv("one", 1),
                                                        kv("two", 2),
                                                        kv("three", 2))));

        equalsSameHashCode(PersistentTreeMap.of(vec(kv("one", 1)))
                                            .assoc("two", 2).assoc("three", 3),
                           PersistentTreeMap.of(vec(kv("three", 3)))
                                            .assoc("two", 2).assoc("one", 1),
                           PersistentTreeMap.of(vec(kv("two", 2), kv("three", 3), kv("one", 1))),
                           PersistentTreeMap.of(vec(kv(1, "one"),
                                                    kv(2, "two"),
                                                    kv(3, "three"))));
    }

    @Test public void sequence() {
        assertEquals(PersistentTreeMap.of(vec(kv(1, "one")))
                                      .assoc(2, "two").assoc(3, "three"),
                     PersistentTreeMap.of(vec(kv(1, "one")))
                                      .assoc(2, "two").assoc(3, "three").tailMap(0));

        assertEquals(PersistentTreeMap.of(vec(kv(1, "one"))).assoc(2, "two").assoc(3, "three"),
                     PersistentTreeMap.of(vec(kv(1, "one"))).assoc(2, "two").assoc(3, "three")
                                      .tailMap(1));

        assertEquals(PersistentTreeMap.of(vec(kv(2, "two"))).assoc(3, "three"),
                     PersistentTreeMap.of(vec(kv(1, "one"))).assoc(2, "two").assoc(3, "three")
                                      .tailMap(2));

        assertEquals(PersistentTreeMap.of(vec(kv(3, "three"))),
                     PersistentTreeMap.of(vec(kv(1, "one"))).assoc(2, "two").assoc(3, "three")
                                      .tailMap(3));

        assertEquals(PersistentTreeMap.empty(),
                     PersistentTreeMap.of(vec(kv(1, "one"))).assoc(2, "two").assoc(3, "three")
                                      .tailMap(4));

        assertEquals(PersistentTreeMap.empty(),
                     PersistentTreeMap.of(vec(kv(1, "one"))).assoc(2, "two").assoc(3, "three")
                                      .tailMap(999999999));

        assertArrayEquals(new UnmodMap.UnEntry[]{kv(2, "two"), kv(3, "three")},
                          PersistentTreeMap.of(vec(kv(1, "one"))).assoc(2, "two").assoc(3, "three")
                                           .drop(1)
                                           .map((u) -> kv(u.getKey(), u.getValue()))
                                           .toMutableList().toArray());

        assertFalse(PersistentTreeMap.of(vec(kv(1, "one"))).drop(1).iterator().hasNext());
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
        assertEquals(PersistentTreeMap.of(vec(kv(1, "one"))).assoc(2, "two").assoc(3, "three"),
                     PersistentTreeMap.of(vec(kv(1, "one"))).assoc(2, "two").assoc(3, "three").subMap(1, 4));

        assertEquals(PersistentTreeMap.of(vec(kv(1, "one"))).assoc(2, "two"),
                     PersistentTreeMap.of(vec(kv(1, "one"))).assoc(2, "two").assoc(3, "three").subMap(1, 3));

        assertEquals(PersistentTreeMap.of(vec(kv(1, "one"))),
                     PersistentTreeMap.of(vec(kv(1, "one"))).assoc(2, "two").assoc(3, "three").subMap(1, 2));

        assertEquals(PersistentTreeMap.empty(),
                     PersistentTreeMap.of(vec(kv(1, "one"))).assoc(2, "two").assoc(3, "three").subMap(1, 1));

        assertEquals(PersistentTreeMap.of(vec(kv(2, "two"))).assoc(3, "three"),
                     PersistentTreeMap.of(vec(kv(1, "one"))).assoc(2, "two").assoc(3, "three").subMap(2, 4));

        assertEquals(PersistentTreeMap.of(vec(kv(3, "three"))),
                     PersistentTreeMap.of(vec(kv(1, "one"))).assoc(2, "two").assoc(3, "three").subMap(3, 4));

        assertEquals(PersistentTreeMap.empty(),
                     PersistentTreeMap.of(vec(kv(1, "one"))).assoc(2, "two").assoc(3, "three").subMap(4, 4));

        assertEquals(PersistentTreeMap.of(vec(kv(2, "two"))),
                     PersistentTreeMap.of(vec(kv(1, "one"))).assoc(2, "two").assoc(3, "three").subMap(2, 3));


        assertEquals(PersistentTreeMap.of(vec(kv(1, "one"))).assoc(2, "two").assoc(3, "three"),
                     PersistentTreeMap.of(vec(kv(1, "one"))).assoc(2, "two").assoc(3, "three").subMap(0, 999999999));
    }

    @Test public void testToString() {
        assertEquals("PersistentTreeMap()",
                     PersistentTreeMap.empty().toString());
        assertEquals("PersistentTreeMap(kv(1,one))",
                     PersistentTreeMap.of(vec(kv(1, "one"))).toString());
        assertEquals("PersistentTreeMap(kv(1,one),kv(2,two))",
                     PersistentTreeMap.of(vec(kv(1, "one"))).assoc(2, "two").toString());
        assertEquals("PersistentTreeMap(kv(1,one),kv(2,two),kv(3,three))",
                     PersistentTreeMap.of(vec(kv(1, "one"))).assoc(2, "two").assoc(3, "three").toString());
        assertEquals("PersistentTreeMap(kv(1,one),kv(2,two),kv(3,three),kv(4,four))",
                     PersistentTreeMap.of(vec(kv(1, "one"))).assoc(2, "two").assoc(3, "three").assoc(4, "four")
                                      .toString());
        assertEquals("PersistentTreeMap(kv(1,one),kv(2,two),kv(3,three),kv(4,four),kv(5,five))",
                     PersistentTreeMap.of(vec(kv(1, "one"))).assoc(2, "two").assoc(3, "three").assoc(4, "four").assoc(5, "five")
                                      .toString());
        assertEquals("PersistentTreeMap(kv(1,one),kv(2,two),kv(3,three),kv(4,four),kv(5,five),...)",
                     PersistentTreeMap.of(vec(kv(1, "one"))).assoc(2, "two").assoc(3, "three").assoc(4, "four").assoc(5, "five")
                                      .assoc(6, "six").toString());
        assertEquals("PersistentTreeMap(kv(1,one),kv(2,two),kv(3,three),kv(4,four),kv(5,five),...)",
                     PersistentTreeMap.of(vec(kv(1, "one"))).assoc(2, "two").assoc(3, "three").assoc(4, "four").assoc(5, "five")
                                      .assoc(6, "six").assoc(7, "seven").toString());
    }

    @Test public void without() {
        PersistentTreeMap<Integer,String> m = PersistentTreeMap.of(vec(kv(1, "one"))).assoc(2, "two").assoc(3, "three");

        assertEquals(m, m.without(0));

        assertEquals(PersistentTreeMap.of(vec(kv(2, "two"))).assoc(3, "three"),
                     m.without(1));

        assertEquals(PersistentTreeMap.of(vec(kv(1, "one"))).assoc(3, "three"),
                     m.without(2));

        assertEquals(PersistentTreeMap.of(vec(kv(1, "one"))).assoc(2, "two"),
                     m.without(3));

        assertEquals(m, m.without(4));

        assertEquals(PersistentTreeMap.of(vec(kv(3, "three"))),
                     m.without(1).without(2));

        assertEquals(PersistentTreeMap.of(vec(kv(1, "one"))).assoc(3, "three"),
                     m.without(2));

        assertEquals(PersistentTreeMap.of(vec(kv(1, "one"))).assoc(2, "two"),
                     m.without(3));

        assertEquals(PersistentTreeMap.EMPTY, PersistentTreeMap.<Integer,String>empty().without(4));
    }

    @Test public void lastKey() {
        PersistentTreeMap<Integer,String> m = PersistentTreeMap.of(vec(kv(1, "one")))
                                                               .assoc(2, "two").assoc(3, "three");
        assertEquals(Integer.valueOf(3), m.lastKey());
        assertEquals(Integer.valueOf(2), m.without(3).lastKey());
        assertEquals(Integer.valueOf(1), m.without(2).without(3).lastKey());
    }

    @Test(expected = NoSuchElementException.class)
    public void lastKeyEx() { PersistentTreeMap.empty().lastKey(); }

    @Test public void largerMap() throws Exception {
        PersistentTreeMap<Integer,String> m =
                PersistentTreeMap.of(vec(kv(1, "one")))
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
        PersistentTreeMap<String,Integer> m = PersistentTreeMap.empty();

        for (int i = 0; i < NUM_ITEMS; i++) {
            m = m.assoc(ordinal(i), i);
            assertEquals(i + 1, m.size());
        }
        assertEquals(NUM_ITEMS, m.size());

        PersistentTreeMap<String,Integer> ser = serializeDeserialize(m);
        assertEquals(m, ser);

        for (int i = 0; i < NUM_ITEMS; i++) {
            assertEquals(Integer.valueOf(i), m.get(ordinal(i)));
            assertEquals(Integer.valueOf(i), ser.get(ordinal(i)));
        }
        assertNull(m.get(ordinal(NUM_ITEMS)));
        assertNull(ser.get(ordinal(NUM_ITEMS)));

        for (int i = 0; i < NUM_ITEMS; i++) {
            assertTrue(m.containsKey(ordinal(i)));
            assertTrue(ser.containsKey(ordinal(i)));
        }

        assertFalse(m.containsKey(ordinal(NUM_ITEMS)));
        assertFalse(ser.containsKey(ordinal(NUM_ITEMS)));

        for (int i = 0; i < NUM_ITEMS; i++) {
            Integer i2 = Integer.valueOf(i);
            assertTrue(m.containsValue(i2));
            assertTrue(ser.containsValue(i2));
        }
        assertFalse(m.containsValue(Integer.valueOf(NUM_ITEMS)));
        assertFalse(ser.containsValue(Integer.valueOf(NUM_ITEMS)));

        // If you remove a key that's not there, you should get back the original map.
        assertTrue(m == m.without(ordinal(NUM_ITEMS)));
        assertTrue(ser == ser.without(ordinal(NUM_ITEMS)));

        for (int i = 0; i < NUM_ITEMS; i++) {
            assertEquals(NUM_ITEMS - i, m.size());
            m = m.without(ordinal(i));
            assertNull(m.get(ordinal(i)));
            assertFalse(m.containsKey(ordinal(i)));
            assertFalse(m.containsValue(Integer.valueOf(i)));
        }

        assertEquals(0, m.size());

        // Because this map is sorted, building it in reverse is also a useful test.
        for (int i = NUM_ITEMS - 1; i >= 0; i--) {
            m = m.assoc(ordinal(i), i);
            assertEquals(NUM_ITEMS - i, m.size());
        }
        assertEquals(NUM_ITEMS, m.size());

        for (int i = 0; i < NUM_ITEMS; i++) {
            assertEquals(Integer.valueOf(i), m.get(ordinal(i)));
        }
        assertNull(m.get(ordinal(NUM_ITEMS)));

        for (int i = 0; i < NUM_ITEMS; i++) {
            assertTrue(m.containsKey(ordinal(i)));
        }

        assertFalse(m.containsKey(ordinal(NUM_ITEMS)));

        for (int i = 0; i < NUM_ITEMS; i++) {
            assertTrue(m.containsValue(Integer.valueOf(i)));
        }
        assertFalse(m.containsValue(Integer.valueOf(NUM_ITEMS)));

        // If you remove a key that's not there, you should get back the original map.
        assertTrue(m == m.without(ordinal(NUM_ITEMS)));

        for (int i = 0; i < NUM_ITEMS; i++) {
            assertEquals(NUM_ITEMS - i, m.size());
            m = m.without(ordinal(i));
            assertNull(m.get(ordinal(i)));
            assertFalse(m.containsKey(ordinal(i)));
            assertFalse(m.containsValue(Integer.valueOf(i)));
        }
    }

    @Test public void entrySet() throws Exception {
        PersistentTreeMap<Integer,String> m =
                PersistentTreeMap.of(vec(kv(1, "one"))).assoc(2, "two").assoc(3, "three").assoc(4, "four").assoc(5, "five");
        ImSet<Map.Entry<Integer,String>> s =
                PersistentTreeSet.ofComp((a, b) -> a.getKey() - b.getKey(),
                                         vec(kv(1, "one"),
                                             kv(2, "two"),
                                             kv(3, "three"),
                                             kv(4, "four"),
                                             kv(5, "five")));
        assertArrayEquals(s.toArray(),
                          m.entrySet().map((u) -> kv(u.getKey(), u.getValue()))
                           .toMutableList().toArray());

        assertArrayEquals(s.toArray(),
                          serializeDeserialize(m).entrySet()
                                  .map((u) -> kv(u.getKey(), u.getValue()))
                                  .toMutableList().toArray());

        assertArrayEquals(s.toArray(),
                          serializeDeserialize(m.entrySet())
                                  .map((u) -> kv(u.getKey(), u.getValue()))
                                  .toMutableList().toArray());
    }

    @Test public void values() throws Exception {
        PersistentTreeMap<Integer,String> m1 =
                PersistentTreeMap.of(vec(kv(4, "four")))
                                 .assoc(5, "five").assoc(2, "two").assoc(3, "three").assoc(1, "one");

        assertArrayEquals(new String[]{"one", "two", "three", "four", "five"},
                          m1.map(e -> e.getValue()).toMutableList().toArray());

        assertArrayEquals(new String[]{"one", "two", "three", "four", "five"},
                          serializeDeserialize(m1).map(e -> e.getValue()).toMutableList().toArray());

//        assertTrue(m.values().equals(Arrays.asList("one", "two", "three", "four", "five")));
        assertNotEquals(0, m1.values().hashCode());
        assertNotEquals(m1.values().hashCode(),
                        PersistentTreeMap.of(vec(kv(4, "four"))).assoc(5, "five").hashCode());

//        System.out.println("m1.values(): " + m1.values());
        PersistentTreeMap<Integer,String> m2 = PersistentTreeMap.of(vec(kv(4, "four")))
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
        Map<Integer,String> a = PersistentTreeMap.of(vec(kv(1, "One"), kv(2, "Two"),
                                                         kv(3, "Three"), kv(4, "Four"),
                                                         kv(5, "Five"), kv(6, "Six"),
                                                         kv(7, "Seven"), kv(8, "Eight"),
                                                         kv(9, "Nine"), kv(10, "Ten")));
        FunctionUtilsTest.mapHelper(a, max);
        Map<Integer,String> b = PersistentTreeMap.of(vec(
                kv(1, "One"), kv(2, "Two"), kv(3, "Three"),
                kv(4, "Four"), kv(5, "Five"), kv(6, "Six"),
                kv(7, "Seven"), kv(8, "Eight"), kv(9, "Nine"),
                kv(10, "Ten")));
        FunctionUtilsTest.mapHelper(b, max);
        Map<Integer,String> c = PersistentTreeMap.ofComp(Equator.defaultComparator(),
                                                         vec(kv(1, "One"), kv(2, "Two"),
                                                             kv(3, "Three"), kv(4, "Four"),
                                                             kv(5, "Five"), kv(6, "Six"),
                                                             kv(7, "Seven"), kv(8, "Eight"),
                                                             kv(9, "Nine"), kv(10, "Ten")));
        assertEquals(a, b);
        assertEquals(b, a);
        assertEquals(a, c);
        assertEquals(c, a);
        assertEquals(b, c);
        assertEquals(c, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertEquals(a.hashCode(), c.hashCode());
        FunctionUtilsTest.mapHelperOdd(
                vec(kv(1, "One"), null, kv(3, "Three"), null, kv(5, "Five"), null,
                    kv(7, "Seven"), null, kv(9, "Nine"), null)
                        .filter(t -> t != null)
                        .toImSortedMap(Equator.defaultComparator(), Function1.identity()),
                max);
        FunctionUtilsTest.mapHelperEven(
                vec(null, kv(2, "Two"), null, kv(4, "Four"), null,
                    kv(6, "Six"), null, kv(8, "Eight"), null, kv(10, "Ten"))
                        .filter(t -> t != null)
                        .toImSortedMap(Equator.defaultComparator(), Function1.identity()),
                max);
    }

    @Test public void testImMap1() {
        int max = 1;
        Map<Integer,String> a = PersistentTreeMap.of(vec(kv(1, "One")));
        FunctionUtilsTest.mapHelper(a, max);
        Map<Integer,String> b = vec(kv(1, "One"))
                .toImSortedMap(Equator.defaultComparator(), Function1.identity());
        FunctionUtilsTest.mapHelper(b, max);
        Map<Integer,String> c = PersistentTreeMap.ofComp(Equator.defaultComparator(),
                                                         vec(kv(1, "One")));
        assertEquals(a, b);
        assertEquals(b, a);
        assertEquals(a, c);
        assertEquals(c, a);
        assertEquals(b, c);
        assertEquals(c, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertEquals(a.hashCode(), c.hashCode());
        FunctionUtilsTest.mapHelperOdd(vec(kv(1, "One"))
                                               .toImSortedMap(Equator.defaultComparator(),
                                                              Function1.identity()),
                                       max);
        FunctionUtilsTest.mapHelperEven(vec((Map.Entry<Integer,String>) null)
                                                .filter(t -> t != null)
                                                .toImSortedMap(Equator.defaultComparator(),
                                                               Function1.identity()),
                                        max);
    }

    @Test public void testImMap0() {
        int max = 0;
        Map<Integer,String> b = vec((KeyValuePair<Integer,String>) null)
                .filter(t -> t != null)
                .toImSortedMap(Equator.defaultComparator(), Function1.identity());
        FunctionUtilsTest.mapHelper(b, max);
        Map<Integer,String> c = PersistentTreeMap.empty(Equator.defaultComparator());
        assertEquals(b, c);
        assertEquals(c, b);
        assertEquals(b.hashCode(), c.hashCode());
        FunctionUtilsTest.mapHelperOdd(vec((KeyValuePair<Integer,String>) null)
                                               .filter(t -> t != null)
                                               .toImSortedMap(Equator.defaultComparator(),
                                                              Function1.identity()),
                                       max);
        FunctionUtilsTest.mapHelperEven(vec((KeyValuePair<Integer,String>) null)
                                                .filter(t -> t != null)
                                                .toImSortedMap(Equator.defaultComparator(),
                                                               Function1.identity()),
                                        max);
    }
}
