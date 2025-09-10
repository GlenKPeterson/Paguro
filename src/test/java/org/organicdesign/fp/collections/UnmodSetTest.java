package org.organicdesign.fp.collections;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class UnmodSetTest {
    static final String[] vals = new String[] { "this", "is", "a", "test" };
    static UnmodSet<String> unSet = new UnmodSet<String>() {
        @Override public boolean contains(Object o) {
            for (String val : vals) {
                if (Objects.equals(o, val)) { return true; }
            }
            return false;
        }

        @NotNull
        @Override public UnmodIterator<String> iterator() {
            return new UnmodIterator<String>() {
                int i = 0;
                @Override public boolean hasNext() { return i < vals.length; }
                @Override public String next() {
                    int idx = i;
                    i = i + 1;
                    return vals[idx];
                }
            };
        }

        @Override public int size() { return vals.length; }
    };

    @Test
    public void containsAllTest() {
        assertTrue(unSet.containsAll(Arrays.asList(vals)));
        assertFalse(unSet.containsAll(Arrays.asList("junk", "no")));
        assertFalse(unSet.containsAll(Arrays.asList("this", "is", "a", "test", "junk", "no")));
        assertFalse(unSet.isEmpty());
        assertArrayEquals(vals, unSet.toArray());
        assertArrayEquals(vals, unSet.toArray(new String[unSet.size()]));

        String[] result = unSet.toArray(new String[unSet.size() + 3]);
        assertEquals(result.length, unSet.size() + 3);
        assertEquals(vals[0], result[0]);
        assertEquals(vals[1], result[1]);
        assertEquals(vals[2], result[2]);
        assertEquals(null, result[unSet.size()]);
        assertEquals(null, result[unSet.size() + 1]);
        assertEquals(null, result[unSet.size() + 2]);
    }

    @SuppressWarnings("deprecation")
    @Test
    public void unsupportedOp() {
        assertThrowsExactly(UnsupportedOperationException.class, () ->
                unSet.add("hi"));
        assertThrowsExactly(UnsupportedOperationException.class, () ->
                unSet.addAll(Arrays.asList("hi", "there")));
        assertThrowsExactly(UnsupportedOperationException.class, () ->
                unSet.clear());
        assertThrowsExactly(UnsupportedOperationException.class, () ->
                unSet.remove(0));
        assertThrowsExactly(UnsupportedOperationException.class, () ->
                unSet.remove("hi"));
        assertThrowsExactly(UnsupportedOperationException.class, () ->
                unSet.removeAll(Arrays.asList("hi", "there")));
        assertThrowsExactly(UnsupportedOperationException.class, () ->
                unSet.removeIf(item -> false));
        assertThrowsExactly(UnsupportedOperationException.class, () ->
                unSet.retainAll(Arrays.asList("hi", "there")));
    }
}