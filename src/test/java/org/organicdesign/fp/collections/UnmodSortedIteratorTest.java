package org.organicdesign.fp.collections;

import org.junit.Test;

import java.util.NoSuchElementException;

import static org.junit.Assert.assertFalse;

public class UnmodSortedIteratorTest {
    @Test public void testEmpty() {
        assertFalse(UnmodSortedIterator.empty().hasNext());
    }

    @Test(expected = NoSuchElementException.class)
    public void testEmptyNext() { UnmodSortedIterator.empty().next(); }
}