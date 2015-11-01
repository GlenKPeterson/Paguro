package org.organicdesign.fp.collections;

import org.junit.Test;

import java.util.NoSuchElementException;

import static org.junit.Assert.assertFalse;

public class UnmodIteratorTest {
    @Test
    public void testEmpty() {
        assertFalse(UnmodIterator.empty().hasNext());
    }

    @Test(expected = NoSuchElementException.class)
    public void testEmptyNext() { UnmodIterator.empty().next(); }

    @SuppressWarnings("deprecation")
    @Test(expected = UnsupportedOperationException.class)
    public void testEmptyExRemove() { UnmodIterator.empty().remove(); }
}