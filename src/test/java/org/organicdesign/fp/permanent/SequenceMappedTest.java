package org.organicdesign.fp.permanent;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.organicdesign.fp.FunctionUtils;
import org.organicdesign.fp.function.Function1;

import static org.junit.Assert.assertArrayEquals;

@RunWith(JUnit4.class)
public class SequenceMappedTest {

    private static final Function1<Integer,Integer> plusOne = x -> x + 1;
    private static final Function1<Integer,Integer> minusOne = x -> x - 1;

    @Test
    public void mapInOneBatch() {
        assertArrayEquals(Sequence.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9)
                                  .map(Function1.identity()).toArray(),
                          new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 });

        assertArrayEquals(Sequence.ofArray(1,2,3,4,5,6,7,8,9)
                                  .map(plusOne).toArray(),
                          new Integer[] { 2,3,4,5,6,7,8,9,10 });
        assertArrayEquals(Sequence.ofArray(1,2,3,4,5,6,7,8,9)
                                  .map(minusOne).toArray(),
                          new Integer[] { 0,1,2,3,4,5,6,7,8 });

        assertArrayEquals(Sequence.ofArray(1,2,3,4,5,6,7,8,9)
                                  .map(plusOne).map(minusOne).toArray(),
                          new Integer[] { 1,2,3,4,5,6,7,8,9 });

        assertArrayEquals(Sequence.ofArray(1,2,3,4,5,6,7,8,9)
                                  .map(minusOne).map(plusOne).toArray(),
                          new Integer[] { 1,2,3,4,5,6,7,8,9 });

        assertArrayEquals(Sequence.ofArray(1,2,3,4,5,6,7,8,9)
                                  .map(plusOne).map(plusOne).toArray(),
                          new Integer[] { 3,4,5,6,7,8,9,10,11 });

    }
    @Test
    public void mapInMultipleBatches() {

        assertArrayEquals(Sequence.ofArray(1,2,3,4,5,6,7,8,9)
                                  .map(plusOne).map(plusOne).map(plusOne).map(plusOne).map(plusOne)
                                  .map(plusOne).map(plusOne).map(plusOne).map(plusOne).map(plusOne)
                                  .toArray(),
                          new Integer[] { 11,12,13,14,15,16,17,18,19 });

        assertArrayEquals(Sequence.ofArray(1,2,3,4,5,6,7,8,9)
                                  .map(minusOne).map(minusOne).map(minusOne).map(minusOne)
                                  .map(minusOne).map(minusOne).map(minusOne).map(minusOne)
                                  .map(minusOne).map(minusOne)
                                  .toArray(),
                          new Integer[] { -9,-8,-7,-6,-5,-4,-3,-2,-1 });

    }
}
