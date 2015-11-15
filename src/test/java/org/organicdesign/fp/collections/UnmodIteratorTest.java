package org.organicdesign.fp.collections;

import org.junit.Test;

import static org.organicdesign.fp.FunctionUtils.emptyUnmodIterator;

public class UnmodIteratorTest {

    @SuppressWarnings("deprecation")
    @Test(expected = UnsupportedOperationException.class)
    public void testEmptyExRemove() { emptyUnmodIterator().remove(); }
}