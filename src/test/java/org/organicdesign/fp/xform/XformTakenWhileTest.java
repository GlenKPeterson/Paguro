package org.organicdesign.fp.xform;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.organicdesign.fp.function.Fn1;

import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;

@RunWith(JUnit4.class)
public class XformTakenWhileTest {

    @Test
    public void takeItemsInOneBatch() {
        Xform<Integer> seq = Xform.of(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
        assertArrayEquals(new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 },
                          seq.takeWhile(Fn1.accept()).toMutList().toArray());
        assertArrayEquals(new Integer[] { 1,2,3,4,5,6,7,8,9 },
                          seq.takeWhile(i -> i < 10).toMutList().toArray());
        assertArrayEquals(new Integer[] { 1,2,3,4,5,6,7,8,9 },
                          seq.takeWhile(i -> i <= 9).toMutList().toArray());
        assertArrayEquals(new Integer[] { 1,2,3,4,5,6,7,8 },
                          seq.takeWhile(i -> i <= 8).toMutList().toArray());
        assertArrayEquals(new Integer[] { 1,2,3,4,5,6,7 },
                          seq.takeWhile(i -> i <= 7).toMutList().toArray());
        assertArrayEquals(new Integer[] { 1,2,3 },
                          seq.takeWhile(i -> i <= 3).toMutList().toArray());
        assertArrayEquals(new Integer[] { 1,2 },
                          seq.takeWhile(i -> i <= 2).toMutList().toArray());
        assertArrayEquals(new Integer[] { 1 },
                          seq.takeWhile(i -> i <= 1).toMutList().toArray());
        assertArrayEquals(new Integer[] {  },
                          seq.takeWhile(Fn1.reject()).toMutList().toArray());
        assertArrayEquals(new Integer[] {  },
                          seq.takeWhile(i -> i > 10).toMutList().toArray());
    }

//    @Test(expected = IllegalArgumentException.class)
//    public void exception1() {
//        Xform.of(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9)).takeWhile(null);
//    }

}
