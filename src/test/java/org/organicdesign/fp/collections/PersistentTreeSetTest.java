package org.organicdesign.fp.collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class PersistentTreeSetTest {
    @Test
    public void assocAndGet() {
        PersistentTreeSet<String> m1 = PersistentTreeSet.empty();
        PersistentTreeSet<String> m2 = m1.put("one");

        // Prove m1 unchanged
        assertEquals(0, m1.size());
        assertFalse(m1.contains("one"));

        // Show m2 correct.
        assertEquals(1, m2.size());

// TODO: Enable and fix!
//        assertTrue(m2.contains("one"));
//        assertFalse(m2.contains("two"));
//
//        PersistentTreeSet<Integer> m3 = PersistentTreeSet.<Integer>empty().put(1).put(2).put(3);
//
//        assertEquals(3, m3.size());
//        assertTrue(m3.contains(1));
//        assertTrue(m2.contains(2));
//        assertTrue(m2.contains(3));
//        assertFalse(m2.contains(4));
    }

}
