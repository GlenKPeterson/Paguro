package org.organicdesign.fp.collections;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class CowryTest {
    @Test public void testSingleElementArray() {
        assertArrayEquals(new Object[] { 1 }, CowryKt.singleElementArray(1));
//        assertArrayEquals(new Object[] { 1 }, CowryKt.singleElementArray(1, null));
        assertArrayEquals(new Integer[] { 1 }, CowryKt.singleElementArray(1, Integer.class));
    }

    @Test public void testSplitIntArray() {
        assertArrayEquals(new int[][] {
                                  new int[] {},
                                  new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 }
                          },
                          CowryKt.splitArray(new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 }, 0));

        assertArrayEquals(new int[][] {
                                  new int[] { 1 },
                                  new int[] { 2, 3, 4, 5, 6, 7, 8, 9 }
                          },
                          CowryKt.splitArray(new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 }, 1));

        assertArrayEquals(new int[][] {
                                  new int[] { 1, 2 },
                                  new int[] { 3, 4, 5, 6, 7, 8, 9 }
                          },
                          CowryKt.splitArray(new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 }, 2));

        assertArrayEquals(new int[][] {
                                  new int[] { 1, 2, 3 },
                                  new int[] { 4, 5, 6, 7, 8, 9 }
                          },
                          CowryKt.splitArray(new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 }, 3));

        assertArrayEquals(new int[][] {
                                  new int[] { 1, 2, 3, 4 },
                                  new int[] { 5, 6, 7, 8, 9 }
                          },
                          CowryKt.splitArray(new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 }, 4));

        assertArrayEquals(new int[][] {
                                  new int[] { 1, 2, 3, 4, 5 },
                                  new int[] { 6, 7, 8, 9 }
                          },
                          CowryKt.splitArray(new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 }, 5));

        assertArrayEquals(new int[][] {
                                  new int[] { 1, 2, 3, 4, 5, 6 },
                                  new int[] { 7, 8, 9 }
                          },
                          CowryKt.splitArray(new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 }, 6));

        assertArrayEquals(new int[][] {
                                  new int[] { 1, 2, 3, 4, 5, 6, 7 },
                                  new int[] { 8, 9 }
                          },
                          CowryKt.splitArray(new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 }, 7));

        assertArrayEquals(new int[][] {
                                  new int[] { 1, 2, 3, 4, 5, 6, 7, 8 },
                                  new int[] { 9 }
                          },
                          CowryKt.splitArray(new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 }, 8));

        assertArrayEquals(new int[][] {
                                  new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 },
                                  new int[] { }
                          },
                          CowryKt.splitArray(new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 }, 9));
    }

    @Test(expected = Exception.class)
    public void testSplitArrayEx1() throws Exception {
        CowryKt.splitArray(new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 }, 10);
    }

    @Test(expected = Exception.class)
    public void testSplitArrayEx2() throws Exception {
        CowryKt.splitArray(new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 }, -1);
    }
}