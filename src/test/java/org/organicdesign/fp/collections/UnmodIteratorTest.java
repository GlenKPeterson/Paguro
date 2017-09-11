package org.organicdesign.fp.collections;

import java.util.NoSuchElementException;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.organicdesign.fp.collections.UnmodIterator.emptyUnmodIterator;

public class UnmodIteratorTest {

    @SuppressWarnings("deprecation")
    @Test(expected = UnsupportedOperationException.class)
    public void testEmptyExRemove() { emptyUnmodIterator().remove(); }

    @Test public void testEmptyUnmodIterator() {
        assertFalse(UnmodIterator.emptyUnmodIterator().hasNext());
    }

    @Test(expected = NoSuchElementException.class)
    public void testEmptyNext() { UnmodIterator.emptyUnmodIterator().next(); }


}