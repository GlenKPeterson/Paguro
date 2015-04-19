package org.organicdesign.fp.permanent;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class SequenceTest {

    @Test
    public void construction() {
        Integer[] ints = new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        assertArrayEquals(ints, Sequence.ofArray(ints).toTypedArray());
        assertArrayEquals(ints, Sequence.of(Arrays.asList(ints)).toTypedArray());
        assertArrayEquals(ints, Sequence.of(Arrays.asList(ints).iterator()).toTypedArray());
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

        assertEquals(Integer.valueOf(1), seq.filter(i -> i == 1).head().get());
        assertEquals(Integer.valueOf(3), seq.filter(i -> i > 2).head().get());
        assertFalse(seq.filter(i -> i > 10).head().isSome());
    }

    @Test
    public void takeAndDrop() {
        assertArrayEquals(Sequence.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9)
                                  .drop(0).take(8888).toTypedArray(),
                          new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 });
        assertArrayEquals(Sequence.ofArray(1,2,3,4,5,6,7,8,9)
                                  .drop(1).take(1).toTypedArray(),
                   new Integer[] { 2 });
        assertArrayEquals(Sequence.ofArray(1,2,3,4,5,6,7,8,9)
                                  .drop(2).take(2).toTypedArray(),
                   new Integer[] { 3,4 });
        assertArrayEquals(Sequence.ofArray(1,2,3,4,5,6,7,8,9)
                                  .drop(3).take(3).toTypedArray(),
                   new Integer[] { 4,5,6 });
        assertArrayEquals(Sequence.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9)
                                  .drop(9999).take(3).toTypedArray(),
                          new Integer[]{});
        assertArrayEquals(Sequence.ofArray(1,2,3,4,5,6,7,8,9)
                                  .drop(3).take(0).toTypedArray(),
                   new Integer[] { });
    }

    @Test
    public void chain1() {
        assertArrayEquals(Sequence.ofArray(5)                      //         5
                                  .prepend(Sequence.ofArray(4))    //       4,5
                                  .append(Sequence.ofArray(6))     //       4,5,6
                                  .prepend(Sequence.ofArray(2, 3)) //   2,3,4,5,6
                                  .append(Sequence.ofArray(7, 8))  //   2,3,4,5,6,7,8
                                  .prepend(Sequence.ofArray(1))    // 1,2,3,4,5,6,7,8
                                  .append(Sequence.ofArray(9))     // 1,2,3,4,5,6,7,8,9
                                  .filter(i -> i > 3)              //       4,5,6,7,8,9
                                  .map(i -> i - 2)                 //   2,3,4,5,6,7
                                  .take(5)                         //   2,3,4,5,6
                                  .drop(2)                         //       4,5,6
                                  .toTypedArray(),
                          new Integer[]{4, 5, 6});
    }

}
