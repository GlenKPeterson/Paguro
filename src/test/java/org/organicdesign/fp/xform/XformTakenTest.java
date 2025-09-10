package org.organicdesign.fp.xform;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

public class XformTakenTest {
    @Test
    public void takeItemsInOneBatch() {
        Integer[] ints = new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        Xform<Integer> seq = Xform.of(Arrays.asList(ints));
        assertArrayEquals(ints, seq.take(9999).toMutList().toArray());
        assertArrayEquals(ints, seq.take(10).toMutList().toArray());
        assertArrayEquals(ints, seq.take(9).toMutList().toArray());
        assertArrayEquals(new Integer[] { 1,2,3,4,5,6,7,8 }, seq.take(8).toMutList().toArray());
        assertArrayEquals(new Integer[] { 1,2,3,4,5,6,7 }, seq.take(7).toMutList().toArray());
        assertArrayEquals(new Integer[] { 1,2,3 }, seq.take(3).toMutList().toArray());
        assertArrayEquals(new Integer[] { 1,2 }, seq.take(2).toMutList().toArray());
        assertArrayEquals(new Integer[] { 1 }, seq.take(1).toMutList().toArray());
        assertArrayEquals(new Integer[0], seq.take(0).toMutList().toArray());
    }

    @Test
    public void exception() {
        assertThrowsExactly(IllegalArgumentException.class, () ->
                Xform.of(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9)).take(-1));
        assertThrowsExactly(IllegalArgumentException.class, () ->
                Xform.of(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9)).take(-99));
    }

    @Test
    public void takeItemsInMultiBatches() {
        Integer[] ints = new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        Xform<Integer> seq = Xform.of(Arrays.asList(ints));
        assertArrayEquals(ints, seq.take(10).take(9999).take(10).toMutList().toArray());
        assertArrayEquals(ints, seq.take(9).take(9).take(9).toMutList().toArray());
        assertArrayEquals(new Integer[] { 1,2,3,4,5,6 },
                          seq.take(8).take(7).take(6).toMutList().toArray());
        assertArrayEquals(new Integer[] { 1,2,3,4,5,6 },
                          seq.take(6).take(7).take(8).toMutList().toArray());
        assertArrayEquals(new Integer[] { 1 },
                          seq.take(999).take(1).take(9999999).toMutList().toArray());
        assertArrayEquals(new Integer[] { 1 },
                          seq.take(9999).take(1).take(3).toMutList().toArray());
        assertArrayEquals(new Integer[0], seq.take(0).take(0).toMutList().toArray());
        assertArrayEquals(new Integer[0], seq.take(0).take(1).toMutList().toArray());
        assertArrayEquals(new Integer[0],
                          seq.take(0).take(99999999).take(9999999).toMutList().toArray());
        assertArrayEquals(new Integer[0],
                          seq.take(99).take(9999).take(0).toMutList().toArray());
    }
}