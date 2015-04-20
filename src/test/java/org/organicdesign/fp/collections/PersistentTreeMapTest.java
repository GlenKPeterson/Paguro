package org.organicdesign.fp.collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

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
                .assoc("one", 1)
                .assoc("two", 2)
                .assoc("three", 3);

        // Prove m1 unchanged
        assertEquals(3, m1.size());
        assertEquals(Integer.valueOf(1), m1.get("one"));
        assertEquals(Integer.valueOf(2), m1.get("two"));
        assertEquals(Integer.valueOf(3), m1.get("three"));
        assertNull(m1.get("four"));

        // TODO: fix, then reenable all of this.
//        assertArrayEquals(new Integer[]{1, 2, 3}, m1.keySet().toArray());
//        assertArrayEquals(new String[]{"one", "two", "three"}, m1.values().toArray());

//        assertArrayEquals(new Integer[]{1, 2, 3},
//                          PersistentTreeMap.<String,Integer>empty()
//                                  .assoc("three", 3)
//                                  .assoc("two", 2)
//                                  .assoc("one", 1)
//                                  .keySet().toArray());


//        assertArrayEquals(new String[]{"one", "two", "three"},
//                          PersistentTreeMap.<String,Integer>empty()
//                                  .assoc("two", 2)
//                                  .assoc("one", 1)
//                                  .assoc("three", 3)
//                                  .values()
//                                  .toArray());

    }

}
