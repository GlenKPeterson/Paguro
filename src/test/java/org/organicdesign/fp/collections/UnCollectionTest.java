package org.organicdesign.fp.collections;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.junit.Test;

import static org.junit.Assert.*;

public class UnCollectionTest {
    @Test public void contains() {
        Collection<String> c1 = Arrays.asList("Hello", "there", "world", "this", "is", "a", "test");
        assertFalse(UnCollection.contains(c1, "poodle"));
        assertFalse(UnCollection.contains(c1, null));
        assertTrue(UnCollection.contains(c1, "Hello"));
        assertTrue(UnCollection.contains(c1, "this"));
        assertTrue(UnCollection.contains(c1, "test"));

        assertTrue(UnCollection.containsAll(c1, c1));
        assertTrue(UnCollection.containsAll(c1, Arrays.asList("there", "this", "a")));
        assertTrue(UnCollection.containsAll(c1, Arrays.asList("Hello", "world", "is", "test")));
        assertFalse(UnCollection.containsAll(c1, Arrays.asList("Hello", "world", "is", "test", "noodle")));
        assertFalse(UnCollection.containsAll(c1, Arrays.asList("billboard", "Hello", "world", "is", "test")));
        assertFalse(UnCollection.containsAll(c1, Collections.singletonList("phone")));
        assertFalse(UnCollection.containsAll(c1, Collections.singletonList(null)));

        assertFalse(UnCollection.contains(UnCollection.empty(), "phone"));
        assertFalse(UnCollection.contains(UnCollection.empty(), null));
        assertFalse(UnCollection.containsAll(UnCollection.empty(), Collections.singletonList("phone")));
        assertFalse(UnCollection.containsAll(UnCollection.empty(), Collections.singletonList(null)));
    }

    @Test public void toArray() {
        Collection<String> c1 = Arrays.asList("Hello", "there", "world", "this", "is", "a", "test");
        assertArrayEquals(new String[]{"Hello", "there", "world", "this", "is", "a", "test"},
                          UnCollection.toArray(c1));
        assertArrayEquals(new String[]{"Hello", "there", "world", "this", "is", "a", "test"},
                          UnCollection.toArray(c1, new String[0]));
        assertArrayEquals(new String[]{"Hello", "there", "world", "this", "is", "a", "test"},
                          UnCollection.toArray(c1, new String[7]));
        assertArrayEquals(new String[]{"Hello", "there", "world", "this", "is", "a", "test"},
                          Arrays.copyOf(UnCollection.toArray(c1, new String[99]), 7));

        assertArrayEquals(new Object[0],
                          UnCollection.toArray(UnCollection.empty()));
        assertArrayEquals(new Object[0],
                          UnCollection.toArray(UnCollection.empty(), new Object[0]));
    }
}
