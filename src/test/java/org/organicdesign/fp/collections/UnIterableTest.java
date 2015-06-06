package org.organicdesign.fp.collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;
import java.util.Iterator;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class UnIterableTest {
    @Test public void emptyEqualsHashcode() {
        UnIterableOrdered<Integer> a = () -> UnIteratorOrdered.empty();
        UnIterableOrdered<Integer> b = () -> UnIteratorOrdered.empty();
        UnIterableOrdered<Integer> c = () -> new UnIteratorOrdered<Integer>() {
            private final Iterator<Integer> intern = Arrays.asList(1).iterator();
            @Override public boolean hasNext() { return intern.hasNext(); }
            @Override public Integer next() { return intern.next(); }
        };

        assertEquals(UnIterable.hashCode(a), UnIterable.hashCode(a));
        assertEquals(UnIterable.hashCode(a), UnIterable.hashCode(b));
        assertNotEquals(UnIterable.hashCode(a), UnIterable.hashCode(c));

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
        UnIterableOrdered<Integer> a = () -> new UnIteratorOrdered<Integer>() {
            private final Iterator<Integer> intern = Arrays.asList(1,2,3).iterator();
            @Override public boolean hasNext() { return intern.hasNext(); }
            @Override public Integer next() { return intern.next(); }
        };
        UnIterableOrdered<Integer> b = () -> new UnIteratorOrdered<Integer>() {
            private final Iterator<Integer> intern = Arrays.asList(1,2,3).iterator();
            @Override public boolean hasNext() { return intern.hasNext(); }
            @Override public Integer next() { return intern.next(); }
        };
        UnIterableOrdered<Integer> c = () -> new UnIteratorOrdered<Integer>() {
            private final Iterator<Integer> intern = Arrays.asList(1,2,3,4).iterator();
            @Override public boolean hasNext() { return intern.hasNext(); }
            @Override public Integer next() { return intern.next(); }
        };
        UnIterableOrdered<Integer> d = () -> new UnIteratorOrdered<Integer>() {
            private final Iterator<Integer> intern = Arrays.asList(1,2,2).iterator();
            @Override public boolean hasNext() { return intern.hasNext(); }
            @Override public Integer next() { return intern.next(); }
        };

        assertEquals(UnIterable.hashCode(a), UnIterable.hashCode(a));
        assertEquals(UnIterable.hashCode(a), UnIterable.hashCode(b));
        assertNotEquals(UnIterable.hashCode(a), UnIterable.hashCode(c));
        assertNotEquals(UnIterable.hashCode(b), UnIterable.hashCode(d));

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
