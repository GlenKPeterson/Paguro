package org.organicdesign.fp.collections;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * For testing internal Cowry methods.
 */
public class CowryTest {
    @Test
    public void testEmptyArray() {
        assertArrayEquals(new Object[0], Cowry.emptyArray());
        assertArrayEquals(new String[0], Cowry.emptyArray());
        assertArrayEquals(new Integer[0], Cowry.emptyArray());
    }

    @Test
    public void testSingleElementArray() {
        assertArrayEquals(new Object[]{1}, Cowry.singleElementArray(1));
        assertArrayEquals(new Object[]{1}, Cowry.singleElementArray(1, null));
        assertArrayEquals(new Integer[]{1}, Cowry.singleElementArray(1, Integer.class));
    }

    @Test
    public void testArrayCopy() {
        assertArrayEquals(new String[] { "A", "B", "C", "D" },
                          Cowry.arrayCopy(new String[] { "A", "B", "C", "D" }, 4, String.class));
        assertArrayEquals(new Object[] { "A", "B", "C" },
                          Cowry.arrayCopy(new String[] { "A", "B", "C", "D" }, 3, null));
        assertArrayEquals(new String[] { "A", "B" },
                          Cowry.arrayCopy(new String[] { "A", "B", "C", "D" }, 2, String.class));
        assertArrayEquals(new CharSequence[] { "A" },
                          Cowry.arrayCopy(new String[] { "A", "B", "C", "D" }, 1, CharSequence.class));
        assertArrayEquals(new String[0],
                          Cowry.arrayCopy(new String[] { "A", "B", "C", "D" }, 0, String.class));
        assertArrayEquals(new Object[] { "A", "B", "C", "D", null },
                          Cowry.arrayCopy(new String[] { "A", "B", "C", "D" }, 5, null));
        assertArrayEquals(new String[] { "A", "B", "C", "D", null, null },
                          Cowry.arrayCopy(new String[] { "A", "B", "C", "D" }, 6, String.class));
    }

    @Test
    public void testInsertIntoArrayAt() {
        assertArrayEquals(new String[] {"Hooligan", "A", "B", "C"},
                          Cowry.insertIntoArrayAt("Hooligan", new String[] {"A", "B", "C"}, 0, String.class));
        assertArrayEquals(new String[] {"A", "Hooligan", "B", "C"},
                          Cowry.insertIntoArrayAt("Hooligan", new String[] {"A", "B", "C"}, 1, String.class));
        assertArrayEquals(new String[] {"A", "B", "Hooligan", "C"},
                          Cowry.insertIntoArrayAt("Hooligan", new String[] {"A", "B", "C"}, 2, String.class));
        assertArrayEquals(new String[] {"A", "B", "C", "Hooligan"},
                          Cowry.insertIntoArrayAt("Hooligan", new String[] {"A", "B", "C"}, 3, String.class));
    }

    @Test public void testSplitIntArray() {
        assertArrayEquals(new int[][] {
                                  new int[] {},
                                  new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 }
                          },
                          Cowry.splitArray(new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 }, 0));

        assertArrayEquals(new int[][] {
                                  new int[] { 1 },
                                  new int[] { 2, 3, 4, 5, 6, 7, 8, 9 }
                          },
                          Cowry.splitArray(new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 }, 1));

        assertArrayEquals(new int[][] {
                                  new int[] { 1, 2 },
                                  new int[] { 3, 4, 5, 6, 7, 8, 9 }
                          },
                          Cowry.splitArray(new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 }, 2));

        assertArrayEquals(new int[][] {
                                  new int[] { 1, 2, 3 },
                                  new int[] { 4, 5, 6, 7, 8, 9 }
                          },
                          Cowry.splitArray(new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 }, 3));

        assertArrayEquals(new int[][] {
                                  new int[] { 1, 2, 3, 4 },
                                  new int[] { 5, 6, 7, 8, 9 }
                          },
                          Cowry.splitArray(new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 }, 4));

        assertArrayEquals(new int[][] {
                                  new int[] { 1, 2, 3, 4, 5 },
                                  new int[] { 6, 7, 8, 9 }
                          },
                          Cowry.splitArray(new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 }, 5));

        assertArrayEquals(new int[][] {
                                  new int[] { 1, 2, 3, 4, 5, 6 },
                                  new int[] { 7, 8, 9 }
                          },
                          Cowry.splitArray(new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 }, 6));

        assertArrayEquals(new int[][] {
                                  new int[] { 1, 2, 3, 4, 5, 6, 7 },
                                  new int[] { 8, 9 }
                          },
                          Cowry.splitArray(new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 }, 7));

        assertArrayEquals(new int[][] {
                                  new int[] { 1, 2, 3, 4, 5, 6, 7, 8 },
                                  new int[] { 9 }
                          },
                          Cowry.splitArray(new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 }, 8));

        assertArrayEquals(new int[][] {
                                  new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 },
                                  new int[] { }
                          },
                          Cowry.splitArray(new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 }, 9));
    }

    @Test
    public void testSplitArray() {
        @NotNull Exception ex;
        ex = assertThrowsExactly(NegativeArraySizeException.class, () ->
                Cowry.splitArray(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9}, 10));
        // This is a lame exception, but this function is the innermost-piece powering these collections
        // and needs to be fast, and this is what the underlying Array returns,
        // so we'll just live with that.
        assertEquals("-1", ex.getMessage());

        ex = assertThrowsExactly(NegativeArraySizeException.class, () ->
                Cowry.splitArray(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9}, -1));
        assertEquals("-1", ex.getMessage());
    }
}