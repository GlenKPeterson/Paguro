package org.organicdesign.fp.xform;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.organicdesign.fp.function.Function1;

import static org.junit.Assert.assertArrayEquals;

@RunWith(JUnit4.class)
public class XformMappedTest {
    private static final Function1<Integer,Integer> plusOne = x -> x + 1;
    private static final Function1<Integer,Integer> minusOne = x -> x - 1;

    @Test
    public void mapInOneBatch() {
        Integer[] ints = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9};
        Xform<Integer> seq = Xform.ofArray(ints);
        assertArrayEquals(ints,
                          seq.map(Function1.identity()).toArray());

        assertArrayEquals(new Integer[] { 2,3,4,5,6,7,8,9,10 },
                          seq.map(plusOne).toArray());

        assertArrayEquals(new Integer[] { 0,1,2,3,4,5,6,7,8 },
                          seq.map(minusOne).toArray());

        assertArrayEquals(ints,
                          seq.map(plusOne).map(minusOne).toArray());

        assertArrayEquals(ints,
                          seq.map(minusOne).map(plusOne).toArray());

        assertArrayEquals(new Integer[] { 3,4,5,6,7,8,9,10,11 },
                          seq.map(plusOne).map(plusOne).toArray());

    }
    @Test
    public void mapInMultipleBatches() {

        assertArrayEquals(Xform.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9)
                                  .map(plusOne).map(plusOne).map(plusOne).map(plusOne).map(plusOne)
                                  .map(plusOne).map(plusOne).map(plusOne).map(plusOne).map(plusOne)
                                  .toArray(),
                          new Integer[] { 11,12,13,14,15,16,17,18,19 });

        assertArrayEquals(Xform.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9)
                                  .map(minusOne).map(minusOne).map(minusOne).map(minusOne)
                                  .map(minusOne).map(minusOne).map(minusOne).map(minusOne)
                                  .map(minusOne).map(minusOne)
                                  .toArray(),
                          new Integer[] { -9,-8,-7,-6,-5,-4,-3,-2,-1 });

    }

}
