package org.organicdesign.fp.collections;

import java.util.Arrays;
import java.util.Iterator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class UnIterableTest {
    @Test public void emptyEqualsHashcode() {
        UnIterable<Integer> a = () -> UnIterator.empty();
        UnIterable<Integer> b = () -> UnIterator.empty();
        UnIterable<Integer> c = () -> new UnIterator<Integer>() {
            private final Iterator<Integer> intern = Arrays.asList(1).iterator();
            @Override public boolean hasNext() { return intern.hasNext(); }
            @Override public Integer next() { return intern.next(); }
        };

        assertEquals(UnIterableOrdered.hashCode(a), UnIterableOrdered.hashCode(a));
        assertEquals(UnIterableOrdered.hashCode(a), UnIterableOrdered.hashCode(b));
        assertNotEquals(UnIterableOrdered.hashCode(a), UnIterableOrdered.hashCode(c));

        assertTrue(UnIterableOrdered.equals(a, a));
        assertTrue(UnIterableOrdered.equals(a, b));
        assertTrue(UnIterableOrdered.equals(b, a));
        assertTrue(UnIterableOrdered.equals(null, null));
        assertFalse(UnIterableOrdered.equals(a, null));
        assertFalse(UnIterableOrdered.equals(null, a));
        assertFalse(UnIterableOrdered.equals(a, c));
        assertFalse(UnIterableOrdered.equals(c, a));
    }

    @Test public void equalsHashcode() {
        UnIterable<Integer> a = () -> new UnIterator<Integer>() {
            private final Iterator<Integer> intern = Arrays.asList(1,2,3).iterator();
            @Override public boolean hasNext() { return intern.hasNext(); }
            @Override public Integer next() { return intern.next(); }
        };
        UnIterable<Integer> b = () -> new UnIterator<Integer>() {
            private final Iterator<Integer> intern = Arrays.asList(1,2,3).iterator();
            @Override public boolean hasNext() { return intern.hasNext(); }
            @Override public Integer next() { return intern.next(); }
        };
        UnIterable<Integer> c = () -> new UnIterator<Integer>() {
            private final Iterator<Integer> intern = Arrays.asList(1,2,3,4).iterator();
            @Override public boolean hasNext() { return intern.hasNext(); }
            @Override public Integer next() { return intern.next(); }
        };
        UnIterable<Integer> d = () -> new UnIterator<Integer>() {
            private final Iterator<Integer> intern = Arrays.asList(1,2,2).iterator();
            @Override public boolean hasNext() { return intern.hasNext(); }
            @Override public Integer next() { return intern.next(); }
        };

        assertEquals(UnIterableOrdered.hashCode(a), UnIterableOrdered.hashCode(a));
        assertEquals(UnIterableOrdered.hashCode(a), UnIterableOrdered.hashCode(b));
        assertNotEquals(UnIterableOrdered.hashCode(a), UnIterableOrdered.hashCode(c));
        assertNotEquals(UnIterableOrdered.hashCode(b), UnIterableOrdered.hashCode(d));

        assertTrue(UnIterableOrdered.equals(a, a));
        assertTrue(UnIterableOrdered.equals(a, b));
        assertTrue(UnIterableOrdered.equals(b, a));
        assertFalse(UnIterableOrdered.equals(a, c));
        assertFalse(UnIterableOrdered.equals(c, a));
        assertFalse(UnIterableOrdered.equals(b, d));
        assertFalse(UnIterableOrdered.equals(d, b));
    }

//    @Test public void compareHelper() {
//        UnIterable<Integer> a = () -> new UnIterator<Integer>() {
//            private final Iterator<Integer> intern = Arrays.asList(1,2,3).iterator();
//            @Override public boolean hasNext() { return intern.hasNext(); }
//            @Override public Integer next() { return intern.next(); }
//        };
//        UnIterable<Integer> b = () -> new UnIterator<Integer>() {
//            private final Iterator<Integer> intern = Arrays.asList(1,2,3).iterator();
//            @Override public boolean hasNext() { return intern.hasNext(); }
//            @Override public Integer next() { return intern.next(); }
//        };
//        assertEquals(Integer.valueOf(0), UnIterable.compareHelper(a, a));
//
//    }
}
