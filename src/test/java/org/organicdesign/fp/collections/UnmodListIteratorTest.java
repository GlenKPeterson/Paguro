package org.organicdesign.fp.collections;

import org.junit.Test;

import java.util.NoSuchElementException;

import static org.organicdesign.fp.FunctionUtils.emptyUnmodListIterator;

public class UnmodListIteratorTest {

    @SuppressWarnings("deprecation")
    @Test(expected = UnsupportedOperationException.class)
    public void testEmptyExAdd() { emptyUnmodListIterator().add(null); }

    @SuppressWarnings("deprecation")
    @Test(expected = UnsupportedOperationException.class)
    public void testEmptyExRemove() { emptyUnmodListIterator().remove(); }

    @SuppressWarnings("deprecation")
    @Test(expected = UnsupportedOperationException.class)
    public void testEmptyExSet() { emptyUnmodListIterator().set(null); }

    @Test(expected = NoSuchElementException.class)
    public void testEmptyNext() { emptyUnmodListIterator().next(); }

    @Test(expected = NoSuchElementException.class)
    public void testEmptyPrevious() { emptyUnmodListIterator().previous(); }
}