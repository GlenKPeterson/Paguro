package org.organicdesign.fp.permanent;

import org.junit.Test;
import org.organicdesign.fp.Option;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class SequenceTest {

    @Test
    public void construction() {
        Integer[] ints = new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        assertArrayEquals(ints, Sequence.ofArray(ints).toArray());
        assertArrayEquals(ints, Sequence.of(Arrays.asList(ints)).toArray());
        assertArrayEquals(ints, Sequence.of(Arrays.asList(ints).iterator()).toArray());
    }

    @Test
    public void forEach() {
        Integer[] ints = new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        Sequence<Integer> seq = Sequence.ofArray(ints);
        final List<Integer> output = new ArrayList<>();
        seq.forEach(i -> output.add(i));
        assertArrayEquals(ints, output.toArray());
    }

    @Test
    public void firstMatching() {
        Integer[] ints = new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        Sequence<Integer> seq = Sequence.ofArray(ints);

        assertEquals(Option.of(1), seq.filter(i -> i == 1).first());
        assertEquals(Option.of(3), seq.filter(i -> i > 2).first());
        assertEquals(Option.none(), seq.filter(i -> i > 10).first());
    }
}
