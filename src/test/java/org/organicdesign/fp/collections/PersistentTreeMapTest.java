package org.organicdesign.fp.collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(JUnit4.class)
public class PersistentTreeMapTest {
    @Test
    public void assocAndGet() {
        PersistentTreeMap<String,Integer> m1 = PersistentTreeMap.empty();
        PersistentTreeMap<String,Integer> m2 = m1.assoc("one", 1);

        // Prove m1 unchanged
        assertEquals(0, m1.size());
        assertNull(m1.get("one"));

        // Show m2 correct.
        assertEquals(1, m2.size());
        assertEquals(Integer.valueOf(1), m2.get("one"));
        assertNull(m1.get("two"));

        PersistentTreeMap<String,Integer> m3 = m2.assoc("two", 2);

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
    }

    @Test
    public void order() {
        PersistentTreeMap<String,Integer> m1 = PersistentTreeMap.<String,Integer>empty()
                .assoc("c", 1)
                .assoc("b", 2)
                .assoc("a", 3);

        // Prove m1 unchanged
        assertEquals(3, m1.size());
        assertEquals(Integer.valueOf(1), m1.get("c"));
        assertEquals(Integer.valueOf(2), m1.get("b"));
        assertEquals(Integer.valueOf(3), m1.get("a"));
        assertNull(m1.get("d"));

        System.out.println(m1.keySet().toString());

        assertArrayEquals(new String[]{"a", "b", "c"}, m1.keySet().toArray());

        // Values are a sorted set as well...
        assertArrayEquals(new Integer[]{1, 2, 3}, m1.values().toArray());

        assertArrayEquals(new String[]{"a", "b", "c"},
                          PersistentTreeMap.<String,Integer>empty()
                                  .assoc("a", 3)
                                  .assoc("b", 2)
                                  .assoc("c", 1)
                                  .keySet().toArray());


        assertArrayEquals(new Integer[]{1, 2, 3},
                          PersistentTreeMap.<String,Integer>empty()
                                  .assoc("b", 2)
                                  .assoc("c", 1)
                                  .assoc("a", 3)
                                  .values()
                                  .toArray());

    }

}
