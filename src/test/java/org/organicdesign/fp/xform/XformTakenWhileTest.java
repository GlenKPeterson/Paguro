package org.organicdesign.fp.xform;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.organicdesign.fp.function.Function1;

import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;

@RunWith(JUnit4.class)
public class XformTakenWhileTest {

    @Test
    public void takeItemsInOneBatch() {
        Xform<Integer> seq = Xform.of(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
        assertArrayEquals(new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 },
                          seq.takeWhile(Function1.accept()).toMutableList().toArray());
        assertArrayEquals(new Integer[] { 1,2,3,4,5,6,7,8,9 },
                          seq.takeWhile(i -> i < 10).toMutableList().toArray());
        assertArrayEquals(new Integer[] { 1,2,3,4,5,6,7,8,9 },
                          seq.takeWhile(i -> i <= 9).toMutableList().toArray());
        assertArrayEquals(new Integer[] { 1,2,3,4,5,6,7,8 },
                          seq.takeWhile(i -> i <= 8).toMutableList().toArray());
        assertArrayEquals(new Integer[] { 1,2,3,4,5,6,7 },
                          seq.takeWhile(i -> i <= 7).toMutableList().toArray());
        assertArrayEquals(new Integer[] { 1,2,3 },
                          seq.takeWhile(i -> i <= 3).toMutableList().toArray());
        assertArrayEquals(new Integer[] { 1,2 },
                          seq.takeWhile(i -> i <= 2).toMutableList().toArray());
        assertArrayEquals(new Integer[] { 1 },
                          seq.takeWhile(i -> i <= 1).toMutableList().toArray());
        assertArrayEquals(new Integer[] {  },
                          seq.takeWhile(Function1.reject()).toMutableList().toArray());
        assertArrayEquals(new Integer[] {  },
                          seq.takeWhile(i -> i > 10).toMutableList().toArray());
    }

    @Test(expected = IllegalArgumentException.class)
    public void exception1() {
        Xform.of(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9)).takeWhile(null);
    }

}
