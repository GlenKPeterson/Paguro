package org.organicdesign.fp.permanent;

import org.junit.Test;
import org.organicdesign.fp.Option;
import org.organicdesign.fp.ephemeral.View;

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

    @Test
    public void takeAndDrop() {
        assertArrayEquals(Sequence.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9)
                                  .drop(0).take(8888).toArray(),
                          new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 });
        assertArrayEquals(Sequence.ofArray(1,2,3,4,5,6,7,8,9)
                                  .drop(1).take(1).toArray(),
                   new Integer[] { 2 });
        assertArrayEquals(Sequence.ofArray(1,2,3,4,5,6,7,8,9)
                                  .drop(2).take(2).toArray(),
                   new Integer[] { 3,4 });
        assertArrayEquals(Sequence.ofArray(1,2,3,4,5,6,7,8,9)
                                  .drop(3).take(3).toArray(),
                   new Integer[] { 4,5,6 });
        assertArrayEquals(Sequence.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9)
                                  .drop(9999).take(3).toArray(),
                          new Integer[]{});
        assertArrayEquals(Sequence.ofArray(1,2,3,4,5,6,7,8,9)
                                  .drop(3).take(0).toArray(),
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
                                  .toArray(),
                          new Integer[]{4, 5, 6});
    }

}
