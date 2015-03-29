package org.organicdesign.fp.permanent;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertArrayEquals;
import static org.organicdesign.fp.function.Function1.accept;
import static org.organicdesign.fp.function.Function1.reject;

@RunWith(JUnit4.class)
public class SequenceFilteredTest {

    @Test(expected = IllegalArgumentException.class)
    public void nullException() {
        assertArrayEquals(Sequence.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9).filter(null).toArray(),
                          new Integer[] {1,2,3,4,5,6,7,8,9});
    }

    @Test
    public void singleFilter() {
        assertArrayEquals(Sequence.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9).filter(accept()).toArray(),
                          new Integer[] {1, 2, 3, 4, 5, 6, 7, 8, 9});

        assertArrayEquals(Sequence.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9).filter(reject()).toArray(),
                          new Integer[] {});

        assertArrayEquals(Sequence.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9).filter(i -> i > 4).toArray(),
                          new Integer[] {5,6,7,8,9});

        assertArrayEquals(Sequence.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9).filter(i -> i < 1).toArray(),
                          new Integer[] {});

        assertArrayEquals(Sequence.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9).filter(i -> i == 3).toArray(),
                          new Integer[] {3});

        assertArrayEquals(Sequence.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9).filter(i -> i == 1).toArray(),
                          new Integer[] {1});

        assertArrayEquals(Sequence.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9).filter(i -> i == 9).toArray(),
                          new Integer[] {9});

        assertArrayEquals(Sequence.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9).filter(i -> i < 7).toArray(),
                          new Integer[] {1,2,3,4,5,6});

    }

    @Test
    public void chainedFilters() {
        assertArrayEquals(Sequence.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9)
                                  .filter(accept()).filter(accept()).filter(accept())
                                  .filter(accept()).filter(accept()).filter(accept())
                                  .filter(accept()).filter(accept()).toArray(),
                          new Integer[] {1, 2, 3, 4, 5, 6, 7, 8, 9});

        assertArrayEquals(Sequence.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9)
                                  .filter(reject()).filter(accept()).filter(accept())
                                  .filter(accept()).filter(accept()).filter(accept())
                                  .filter(accept()).filter(accept()).toArray(),
                          new Integer[] {});

        assertArrayEquals(Sequence.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9)
                                  .filter(accept()).filter(accept()).filter(accept())
                                  .filter(accept()).filter(reject()).filter(accept())
                                  .filter(accept()).filter(accept()).toArray(),
                          new Integer[] {});

        assertArrayEquals(Sequence.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9)
                                  .filter(accept()).filter(accept()).filter(accept())
                                  .filter(accept()).filter(accept()).filter(accept())
                                  .filter(accept()).filter(reject()).toArray(),
                          new Integer[] {});

        assertArrayEquals(Sequence.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9)
                                  .filter(i -> i > 2)
                                  .filter(i -> i < 7)
                                  .filter(i -> i != 5).toArray(),
                          new Integer[] {3, 4, 6});

    }
}
