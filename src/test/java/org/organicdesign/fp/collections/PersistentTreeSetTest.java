package org.organicdesign.fp.collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class PersistentTreeSetTest {
    @Test
    public void assocAndGet() {
        PersistentTreeSet<String> s1 = PersistentTreeSet.empty();
        PersistentTreeSet<String> s2 = s1.put("one");

        // Prove m1 unchanged
        assertEquals(0, s1.size());
        assertFalse(s1.contains("one"));

        // Show m2 correct.
        assertEquals(1, s2.size());

        assertTrue(s2.contains("one"));
        assertFalse(s2.contains("two"));

        PersistentTreeSet<Integer> s3 = PersistentTreeSet.<Integer>empty().put(1).put(2).put(3);

        assertEquals(3, s3.size());
        assertTrue(s3.contains(1));
        assertTrue(s3.contains(2));
        assertTrue(s3.contains(3));
        assertFalse(s3.contains(4));

        assertArrayEquals(new Integer[]{1, 2, 3}, s3.toArray());

        assertArrayEquals(new Integer[]{1, 2, 3},
                          PersistentTreeSet.<Integer>empty().put(3).put(2).put(1).toArray());

    }

}
