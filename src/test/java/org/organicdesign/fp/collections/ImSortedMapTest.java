package org.organicdesign.fp.collections;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ImSortedMapTest {
    @Test public void testCov() {
        ImSortedMap<String,Integer> m = PersistentTreeMap.empty();
        m = m.assoc("Hello", 73);
        m = m.assoc("Goodbye", 74);
        m = m.assoc("Maybe", 75);
        assertEquals(Integer.valueOf(73), m.getOrElse("Hello", 37));
        assertEquals(Integer.valueOf(37), m.getOrElse("Helloz", 37));

        assertEquals(2, m.headMap("Maybe").size());
    }
}