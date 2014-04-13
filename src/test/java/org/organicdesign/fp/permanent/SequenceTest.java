package org.organicdesign.fp.permanent;

import java.util.Arrays;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class SequenceTest {

    @Test
    public void construction() {
        Integer[] ints = new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        assertArrayEquals(Sequence.ofArray(ints).toArray(), ints);
        assertArrayEquals(Sequence.of(Arrays.asList(ints)).toArray(), ints);
        assertArrayEquals(Sequence.of(Arrays.asList(ints).iterator()).toArray(),
                          ints);
    }
}
