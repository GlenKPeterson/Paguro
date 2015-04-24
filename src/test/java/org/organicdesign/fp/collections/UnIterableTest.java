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

        assertEquals(UnIterable.hashCode(a), UnIterable.hashCode(a));
        assertEquals(UnIterable.hashCode(a), UnIterable.hashCode(b));
        assertNotEquals(UnIterable.hashCode(a), UnIterable.hashCode(c));

        assertTrue(UnIterable.equals(a, a));
        assertTrue(UnIterable.equals(a, b));
        assertTrue(UnIterable.equals(b, a));
        assertTrue(UnIterable.equals(null, null));
        assertFalse(UnIterable.equals(a, null));
        assertFalse(UnIterable.equals(null, a));
        assertFalse(UnIterable.equals(a, c));
        assertFalse(UnIterable.equals(c, a));
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

        assertEquals(UnIterable.hashCode(a), UnIterable.hashCode(a));
        assertEquals(UnIterable.hashCode(a), UnIterable.hashCode(b));
        assertNotEquals(UnIterable.hashCode(a), UnIterable.hashCode(c));
        assertNotEquals(UnIterable.hashCode(b), UnIterable.hashCode(d));

        assertTrue(UnIterable.equals(a, a));
        assertTrue(UnIterable.equals(a, b));
        assertTrue(UnIterable.equals(b, a));
        assertFalse(UnIterable.equals(a, c));
        assertFalse(UnIterable.equals(c, a));
        assertFalse(UnIterable.equals(b, d));
        assertFalse(UnIterable.equals(d, b));
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
