package org.organicdesign.fp.collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.organicdesign.fp.permanent.Sequence;

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
        assertArrayEquals(new Integer[]{1, 2, 3}, m1.values().toArray());

        assertArrayEquals(new String[]{"a", "b", "c"},
                          PersistentTreeMap.of("a", 3)
                                  .assoc("b", 2)
                                  .assoc("c", 1)
                                  .keySet().toArray());


        assertArrayEquals(new Integer[]{1, 2, 3},
                          PersistentTreeMap.of("b", 2)
                                  .assoc("c", 1)
                                  .assoc("a", 3)
                                  .values()
                                  .toArray());

        UnIterator<UnMap.UnEntry<String,Integer>> iter = PersistentTreeMap.of("c", 3)
                .assoc("b", 2)
                .assoc("a", 1).iterator();
        UnMap.UnEntry<String,Integer> next = iter.next();
        assertEquals("a", next.getKey());
        assertEquals(Integer.valueOf(1), next.getValue());

        next = iter.next();
        assertEquals("b", next.getKey());
        assertEquals(Integer.valueOf(2), next.getValue());

        next = iter.next();
        assertEquals("c", next.getKey());
        assertEquals(Integer.valueOf(3), next.getValue());
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


        assertTrue(Sequence.ofArray(UnMap.UnEntry.of(2, "two"), UnMap.UnEntry.of(3, "three"))
                           .equals(PersistentTreeMap.of(1, "one").assoc(2, "two").assoc(3, "three").tail()));

        assertTrue(Sequence.emptySequence().equals(PersistentTreeMap.of(1, "one").tail()));
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
                             .assoc(6, "six").assoc(6, "seven").toString());
    }

}
