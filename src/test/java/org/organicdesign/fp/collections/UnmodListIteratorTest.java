package org.organicdesign.fp.collections;

import org.junit.Test;

import java.util.NoSuchElementException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class UnmodListIteratorTest {

    @Test public void testEmpty() {
        assertFalse(UnmodListIterator.empty().hasNext());
        assertFalse(UnmodListIterator.empty().hasPrevious());
        assertEquals(0, UnmodListIterator.empty().nextIndex());
        assertEquals(-1, UnmodListIterator.empty().previousIndex());
    }

    @SuppressWarnings("deprecation")
    @Test(expected = UnsupportedOperationException.class)
    public void testEmptyExAdd() { UnmodListIterator.empty().add(null); }

    @SuppressWarnings("deprecation")
    @Test(expected = UnsupportedOperationException.class)
    public void testEmptyExRemove() { UnmodListIterator.empty().remove(); }

    @SuppressWarnings("deprecation")
    @Test(expected = UnsupportedOperationException.class)
    public void testEmptyExSet() { UnmodListIterator.empty().set(null); }

    @Test(expected = NoSuchElementException.class)
    public void testEmptyNext() { UnmodListIterator.empty().next(); }

    @Test(expected = NoSuchElementException.class)
    public void testEmptyPrevious() { UnmodListIterator.empty().previous(); }
}