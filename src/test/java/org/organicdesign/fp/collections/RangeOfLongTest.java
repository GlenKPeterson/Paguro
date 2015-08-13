package org.organicdesign.fp.collections;

import org.junit.Test;

import java.util.Arrays;
import java.util.Iterator;

import static org.junit.Assert.*;

public class RangeOfLongTest {
    @Test(expected = IllegalArgumentException.class)
    public void factory1() {
        RangeOfLong.of(null, Long.valueOf(1));
    }
    @Test(expected = IllegalArgumentException.class)
    public void factory2() {
        RangeOfLong.of(Long.valueOf(1), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void factory3() {
        RangeOfLong.of(1, 0);
    }

    @Test public void basics() {
        RangeOfLong ir1 = RangeOfLong.of(0, 0);
        assertEquals(ir1.contains(0), true);
        assertEquals(ir1.contains(1), false);
        assertEquals(ir1.contains(-1), false);
        assertEquals(ir1.size(), 1);
    }

    @Test public void iteratorTest() {
        Iterator<Long> a = Arrays.asList(-2L, -1L, 0L, 1L, 2L, 3L, 4L).iterator();
        Iterator<Long> b = RangeOfLong.of(-2, 5).iterator();

        while (a.hasNext()) {
            assertTrue(b.hasNext());
            assertEquals(a.next(), b.next());
        }
        assertFalse(b.hasNext());

    }

}