package org.organicdesign.fp.experimental;

import org.junit.Test;
import org.organicdesign.fp.collections.ImList;
import org.organicdesign.fp.collections.UnmodListTest;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class RrbTree1Test {
    @Test
    public void basics() {
        RrbTree1<Integer> rrb = RrbTree1.empty();
        assertEquals(0, rrb.size());

        RrbTree1<Integer> rrb1 = rrb.append(5);
        assertEquals(0, rrb.size());
        assertEquals(1, rrb1.size());
        assertEquals(Integer.valueOf(5), rrb1.get(0));

        RrbTree1<Integer> rrb2 = rrb1.append(4);
        assertEquals(0, rrb.size());
        assertEquals(1, rrb1.size());
        assertEquals(2, rrb2.size());
        assertEquals(Integer.valueOf(5), rrb1.get(0));
        assertEquals(Integer.valueOf(5), rrb2.get(0));
        assertEquals(Integer.valueOf(4), rrb2.get(1));

        RrbTree1<Integer> rrb3 = rrb2.append(3);
        assertEquals(0, rrb.size());
        assertEquals(1, rrb1.size());
        assertEquals(2, rrb2.size());
        assertEquals(3, rrb3.size());
        assertEquals(Integer.valueOf(5), rrb1.get(0));
        assertEquals(Integer.valueOf(5), rrb2.get(0));
        assertEquals(Integer.valueOf(5), rrb3.get(0));
        assertEquals(Integer.valueOf(4), rrb2.get(1));
        assertEquals(Integer.valueOf(4), rrb3.get(1));
        assertEquals(Integer.valueOf(3), rrb3.get(2));
    }

    @Test public void emptyListIterator() {
        UnmodListTest.listIteratorTest(Collections.emptyList(), RrbTree1.empty());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx00() { RrbTree1.empty().get(Integer.MIN_VALUE); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx01() { RrbTree1.empty().get(-1); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx02() { RrbTree1.empty().get(0); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx03() { RrbTree1.empty().get(1); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx04() { RrbTree1.empty().get(Integer.MAX_VALUE); }

    @Test public void addSeveralItems() throws NoSuchAlgorithmException {
        System.out.println("addSeveral start");
        final int SEVERAL = 100; //0; //0; //SecureRandom.getInstanceStrong().nextInt(999999) + 33 ;
        RrbTree1<Integer> is = RrbTree1.empty();
        for (int j = 0; j < SEVERAL; j++){
            is = is.append(j);
            assertEquals(j + 1, is.size());
            assertEquals(Integer.valueOf(j), is.get(j));
            for (int k = 0; k <= j; k++) {
                assertEquals(Integer.valueOf(k), is.get(k));
            }
        }
        assertEquals(SEVERAL, is.size());
        for (int j = 0; j < SEVERAL; j++){
            assertEquals(Integer.valueOf(j), is.get(j));
        }
    }

    // TODO: Think about what exception to expect.
    @Test(expected = Exception.class)
    public void putEx() { RrbTree1.empty().replace(1, "Hello"); }

    @Test public void replace() {
        RrbTree1<String> pv = RrbTree1.empty();
        pv = pv.append("Hello").append("World");
        assertArrayEquals(new String[] { "Hello", "World" },
                          pv.toArray());

        assertArrayEquals(new String[]{"Goodbye", "World"},
                          pv.replace(0, "Goodbye").toArray());

        ImList<Integer> pv2 = RrbTree1.empty();
        int len = 999;
        Integer[] test = new Integer[len];
        for (int i = 0; i < len; i++) {
            pv2 = pv2.append(i);
            test[i] = i;
        }
        assertArrayEquals(test, pv2.toArray());

        for (int i = 0; i < len; i++) {
            pv2 = pv2.replace(i, len - i);
            test[i] = len - i;
        }
        assertArrayEquals(test, pv2.toArray());
    }

    @Test public void listIterator() {
        RrbTree1<Integer> pv2 = RrbTree1.empty();
        int len = 99;
        Integer[] test = new Integer[len];

        for (int i = 0; i < len; i++) {
            int testVal = len - 1;
            pv2 = pv2.append(testVal);
            assertEquals(Integer.valueOf(testVal), pv2.get(i));
            test[i] = testVal;
        }
        assertArrayEquals(test, pv2.toArray());

        List<Integer> tList = Arrays.asList(test);
        UnmodListTest.listIteratorTest(tList, pv2);
    }
}