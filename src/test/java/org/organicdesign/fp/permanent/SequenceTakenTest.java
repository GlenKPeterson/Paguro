package org.organicdesign.fp.permanent;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class SequenceTakenTest {

    @Test
    public void takeItemsInOneBatch() {
        assertArrayEquals(Sequence.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9)
                        .take(9999).toTypedArray(),
                new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 });
        assertArrayEquals(Sequence.ofArray(1,2,3,4,5,6,7,8,9).take(10).toTypedArray(),
                new Integer[] { 1,2,3,4,5,6,7,8,9 });
        assertArrayEquals(Sequence.ofArray(1,2,3,4,5,6,7,8,9).take(9).toTypedArray(),
                new Integer[] { 1,2,3,4,5,6,7,8,9 });
        assertArrayEquals(Sequence.ofArray(1,2,3,4,5,6,7,8,9).take(8).toTypedArray(),
                new Integer[] { 1,2,3,4,5,6,7,8 });
        assertArrayEquals(Sequence.ofArray(1,2,3,4,5,6,7,8,9).take(7).toTypedArray(),
                new Integer[] { 1,2,3,4,5,6,7 });
        assertArrayEquals(Sequence.ofArray(1,2,3,4,5,6,7,8,9).take(3).toTypedArray(),
                new Integer[] { 1,2,3 });
        assertArrayEquals(Sequence.ofArray(1,2,3,4,5,6,7,8,9).take(2).toTypedArray(),
                new Integer[] { 1,2 });
        assertArrayEquals(Sequence.ofArray(1,2,3,4,5,6,7,8,9).take(1).toTypedArray(),
                new Integer[] { 1 });
    }

    @Test(expected = IllegalArgumentException.class)
    public void exception0() { Sequence.ofArray(1,2,3,4,5,6,7,8,9).take(0); }
    @Test(expected = IllegalArgumentException.class)
    public void exception1() { Sequence.ofArray(1,2,3,4,5,6,7,8,9).take(-1); }
    @Test(expected = IllegalArgumentException.class)
    public void exception2() { Sequence.ofArray(1,2,3,4,5,6,7,8,9).take(-99); }

    @Test
    public void takeItemsInMultiBatches() {
        assertArrayEquals(Sequence.ofArray(1,2,3,4,5,6,7,8,9)
                        .take(10).take(9999).take(10).toTypedArray(),
                new Integer[] { 1,2,3,4,5,6,7,8,9 });
        assertArrayEquals(Sequence.ofArray(1,2,3,4,5,6,7,8,9)
                        .take(9).take(9).take(9).toTypedArray(),
                new Integer[] { 1,2,3,4,5,6,7,8,9 });
        assertArrayEquals(Sequence.ofArray(1,2,3,4,5,6,7,8,9)
                        .take(8).take(7).take(6).toTypedArray(),
                new Integer[] { 1,2,3,4,5,6 });
        assertArrayEquals(Sequence.ofArray(1,2,3,4,5,6,7,8,9)
                        .take(6).take(7).take(8).toTypedArray(),
                new Integer[] { 1,2,3,4,5,6 });
        assertArrayEquals(Sequence.ofArray(1,2,3,4,5,6,7,8,9)
                        .take(999).take(1).take(9999999).toTypedArray(),
                new Integer[] { 1 });
        assertArrayEquals(Sequence.ofArray(1,2,3,4,5,6,7,8,9)
                        .take(9999).take(1).take(3).toTypedArray(),
                new Integer[] { 1 });
        assertArrayEquals(Sequence.ofArray(1,2,3,4,5,6,7,8,9)
                        .take(1).take(99999999).take(9999999)
                        .toTypedArray(),
                new Integer[] { 1 });
        assertArrayEquals(Sequence.ofArray(1,2,3,4,5,6,7,8,9)
                        .take(99).take(9999).take(1).toTypedArray(),
                new Integer[] { 1 });
    }
}
