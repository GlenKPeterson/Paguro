package org.organicdesign.fp.collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;
import java.util.Iterator;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class UnmodIterableTest {
    @Test public void emptyEqualsHashcode() {
        UnmodSortedIterable<Integer> a = () -> UnmodSortedIterator.empty();
        UnmodSortedIterable<Integer> b = () -> UnmodSortedIterator.empty();
        UnmodSortedIterable<Integer> c = () -> new UnmodSortedIterator<Integer>() {
            private final Iterator<Integer> intern = Arrays.asList(1).iterator();
            @Override public boolean hasNext() { return intern.hasNext(); }
            @Override public Integer next() { return intern.next(); }
        };

        assertEquals(UnmodIterable.hashCode(a), UnmodIterable.hashCode(a));
        assertEquals(UnmodIterable.hashCode(a), UnmodIterable.hashCode(b));
        assertNotEquals(UnmodIterable.hashCode(a), UnmodIterable.hashCode(c));

        assertTrue(UnmodSortedIterable.equals(a, a));
        assertTrue(UnmodSortedIterable.equals(a, b));
        assertTrue(UnmodSortedIterable.equals(b, a));
        assertTrue(UnmodSortedIterable.equals(null, null));
        assertFalse(UnmodSortedIterable.equals(a, null));
        assertFalse(UnmodSortedIterable.equals(null, a));
        assertFalse(UnmodSortedIterable.equals(a, c));
        assertFalse(UnmodSortedIterable.equals(c, a));
    }

    @Test public void equalsHashcode() {
        UnmodSortedIterable<Integer> a = () -> new UnmodSortedIterator<Integer>() {
            private final Iterator<Integer> intern = Arrays.asList(1,2,3).iterator();
            @Override public boolean hasNext() { return intern.hasNext(); }
            @Override public Integer next() { return intern.next(); }
        };
        UnmodSortedIterable<Integer> b = () -> new UnmodSortedIterator<Integer>() {
            private final Iterator<Integer> intern = Arrays.asList(1,2,3).iterator();
            @Override public boolean hasNext() { return intern.hasNext(); }
            @Override public Integer next() { return intern.next(); }
        };
        UnmodSortedIterable<Integer> c = () -> new UnmodSortedIterator<Integer>() {
            private final Iterator<Integer> intern = Arrays.asList(1,2,3,4).iterator();
            @Override public boolean hasNext() { return intern.hasNext(); }
            @Override public Integer next() { return intern.next(); }
        };
        UnmodSortedIterable<Integer> d = () -> new UnmodSortedIterator<Integer>() {
            private final Iterator<Integer> intern = Arrays.asList(1,2,2).iterator();
            @Override public boolean hasNext() { return intern.hasNext(); }
            @Override public Integer next() { return intern.next(); }
        };

        assertEquals(UnmodIterable.hashCode(a), UnmodIterable.hashCode(a));
        assertEquals(UnmodIterable.hashCode(a), UnmodIterable.hashCode(b));
        assertNotEquals(UnmodIterable.hashCode(a), UnmodIterable.hashCode(c));
        assertNotEquals(UnmodIterable.hashCode(b), UnmodIterable.hashCode(d));

        assertTrue(UnmodSortedIterable.equals(a, a));
        assertTrue(UnmodSortedIterable.equals(a, b));
        assertTrue(UnmodSortedIterable.equals(b, a));
        assertFalse(UnmodSortedIterable.equals(a, c));
        assertFalse(UnmodSortedIterable.equals(c, a));
        assertFalse(UnmodSortedIterable.equals(b, d));
        assertFalse(UnmodSortedIterable.equals(d, b));
    }

//    @Test public void compareHelper() {
//        UnmodIterable<Integer> a = () -> new UnmodIterator<Integer>() {
//            private final Iterator<Integer> intern = Arrays.asList(1,2,3).iterator();
//            @Override public boolean hasNext() { return intern.hasNext(); }
//            @Override public Integer next() { return intern.next(); }
//        };
//        UnmodIterable<Integer> b = () -> new UnmodIterator<Integer>() {
//            private final Iterator<Integer> intern = Arrays.asList(1,2,3).iterator();
//            @Override public boolean hasNext() { return intern.hasNext(); }
//            @Override public Integer next() { return intern.next(); }
//        };
//        assertEquals(Integer.valueOf(0), UnmodIterable.compareHelper(a, a));
//
//    }
}
