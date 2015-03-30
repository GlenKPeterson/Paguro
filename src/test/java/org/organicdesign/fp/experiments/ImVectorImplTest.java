package org.organicdesign.fp.experiments;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.organicdesign.fp.collections.ImList;
import org.organicdesign.fp.collections.ImVectorImpl;

import static org.junit.Assert.assertArrayEquals;

@RunWith(JUnit4.class)
public class ImVectorImplTest {
    @Test
    public void basics() {
        Integer[] threeIntArray = new Integer[]{1, 2, 3};
        ImList<Integer> list = ImVectorImpl.of(1, 2, 3);
        Integer[] resultArray = list.toArray(new Integer[3]);
        assertArrayEquals(threeIntArray, resultArray);
    }
}
