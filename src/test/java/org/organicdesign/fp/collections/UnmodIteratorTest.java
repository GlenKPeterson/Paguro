package org.organicdesign.fp.collections;

import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.organicdesign.fp.collections.UnmodIterator.emptyUnmodIterator;

public class UnmodIteratorTest {

    @SuppressWarnings("deprecation")
    @Test
    public void testEx() {
        assertThrowsExactly(UnsupportedOperationException.class, () ->
                emptyUnmodIterator().remove());
        assertThrowsExactly(NoSuchElementException.class, () ->
                UnmodIterator.emptyUnmodIterator().next());
    }

    @Test public void testEmptyUnmodIterator() {
        assertFalse(UnmodIterator.emptyUnmodIterator().hasNext());
    }
}