package org.organicdesign.fp.collections;

import java.util.Arrays;
import java.util.Map;
import java.util.NoSuchElementException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.organicdesign.fp.function.Function2;
import org.organicdesign.fp.permanent.Sequence;
import org.organicdesign.fp.tuple.Tuple2;

import static org.junit.Assert.*;

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
        PersistentTreeMap<String,Integer> m1 = PersistentTreeMap.of("c", 1)
                .assoc("b", 2)
                .assoc("a", 3);

        // Prove m1 unchanged
        assertEquals(3, m1.size());
        assertEquals(Integer.valueOf(1), m1.get("c"));
        assertEquals(Integer.valueOf(2), m1.get("b"));
        assertEquals(Integer.valueOf(3), m1.get("a"));
        assertNull(m1.get("d"));

//        System.out.println(m1.keySet().toString());

        assertArrayEquals(new String[]{"a", "b", "c"}, m1.keySet().toArray());

        // Values are a sorted set as well...
        assertArrayEquals(new Integer[]{3, 2, 1}, m1.values().toArray());

        assertArrayEquals(new String[]{"a", "b", "c"},
                          PersistentTreeMap.of("a", 3)
                                  .assoc("b", 2)
                                  .assoc("c", 1)
                                  .keySet().toArray());


        {
            UnIterator<Integer> iter = PersistentTreeMap.of("b", 2)
                    .assoc("c", 1)
                    .assoc("a", 3)
                    .values().iterator();

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

        assertEquals(Function2.defaultComparator(), m2.comparator());
        assertNotEquals(String.CASE_INSENSITIVE_ORDER.reversed(), m2.comparator());

        PersistentTreeMap<String,Integer> m3 = PersistentTreeMap.of("a", 1, String.CASE_INSENSITIVE_ORDER.reversed())
                .assoc("b", 2)
                .assoc("c", 3);
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
        assertEquals(PersistentTreeMap.empty().hashCode(),
                     PersistentTreeMap.empty().hashCode());

        assertEquals(PersistentTreeMap.of("one", 1).hashCode(),
                     PersistentTreeMap.of("one", 1).hashCode());

        assertEquals(PersistentTreeMap.of("one", 1).hashCode(),
                     PersistentTreeMap.of("one", 99999999).hashCode());

        assertNotEquals(PersistentTreeMap.of("onf", 1).hashCode(),
                        PersistentTreeMap.of("one", 1).hashCode());

        assertEquals(PersistentTreeMap.of("one", 1).assoc("two", 2).assoc("three", 3).hashCode(),
                     PersistentTreeMap.of("one", 1).assoc("two", 2).assoc("three", 3).hashCode());

        assertNotEquals(PersistentTreeMap.of("one", 1).assoc("two", 2).assoc("three", 3).hashCode(),
                        PersistentTreeMap.of(1, "one").assoc(2, "two").assoc(3, "three").hashCode());

        assertNotEquals(PersistentTreeMap.of("Zne", 1).assoc("two", 2).assoc("three", 3).hashCode(),
                        PersistentTreeMap.of("one", 1).assoc("two", 2).assoc("three", 3).hashCode());

        assertEquals(PersistentTreeMap.of("one", 1).assoc("two", 2).assoc("three", 3),
                     PersistentTreeMap.of("one", 1).assoc("two", 2).assoc("three", 3));

        assertNotEquals(PersistentTreeMap.of("one", 1).assoc("two", 2).assoc("three", 3),
                        PersistentTreeMap.of(1, "one").assoc(2, "two").assoc(3, "three"));

        assertNotEquals(PersistentTreeMap.of("Zne", 1).assoc("two", 2).assoc("three", 3),
                        PersistentTreeMap.of("one", 1).assoc("two", 2).assoc("three", 3));

        //noinspection ObjectEqualsNull
        assertFalse(PersistentTreeMap.of("one", 1).equals(null));
        //noinspection ObjectEqualsNull
        assertFalse(PersistentTreeMap.empty().equals(null));

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
        PersistentTreeMap.empty().subMap(3, 2);
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

        assertEquals(PersistentTreeMap.EMPTY, PersistentTreeMap.empty().without(4));
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
        assertEquals(Sequence.ofArray(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21),
                     m.keySet());
        assertTrue(UnIterable.equals(
                Sequence.ofArray("one again", "two", "three", "four", "five", "six", "seven", "eight",
                                 "nine again", "ten again", "eleven again", "twelve", "thirteen",
                                 "fourteen",
                                 "fifteen", "sixteen", "seventeen", "eighteen", "nineteen", "twenty again",
                                 "twenty one"),
                m.values()));
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
        PersistentTreeMap<Integer,String> m =
                PersistentTreeMap.of(4, "four").assoc(5, "five").assoc(2, "two").assoc(3, "three").assoc(1, "one");

        assertArrayEquals(new String[] {"one", "two", "three", "four", "five"},
                          m.values().toArray(new String[5]));

        assertTrue(m.values().equals(Arrays.asList("one", "two", "three", "four", "five")));
        assertNotEquals(0, m.values().hashCode());
        assertNotEquals(m.values().hashCode(), PersistentTreeMap.of(4, "four").assoc(5, "five").hashCode());
        assertEquals(m.values().hashCode(),
                     PersistentTreeMap.of(4, "four").assoc(2, "two").assoc(5, "five").assoc(1, "one").assoc(3, "three")
                             .values()
                             .hashCode());

    }
}
