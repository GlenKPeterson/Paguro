package org.organicdesign.fp.collections;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.organicdesign.fp.StaticImports.unMap;

public class UnMapTest {
    @Test public void containsValue() {
        UnMap<String,Integer> m = unMap("Hello", 3,
                                        "World", 2,
                                        "This", 1,
                                        "Is", 0,
                                        "A", -1,
                                        "test", -2);
        assertFalse(m.containsValue(Integer.MAX_VALUE));
        assertFalse(m.containsValue(4));
        assertTrue(m.containsValue(3));
        assertTrue(m.containsValue(0));
        assertTrue(m.containsValue(-2));
        assertFalse(m.containsValue(-3));
        assertFalse(m.containsValue(Integer.MIN_VALUE));
    }
}
