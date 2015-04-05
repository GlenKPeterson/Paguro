package org.organicdesign.fp.collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class ImVectorImplTest {
    @Test
    public void basics() {
        Integer[] threeIntArray = new Integer[]{1, 2, 3};
        ImList<Integer> list = ImVectorImpl.of(1, 2, 3);
        Integer[] resultArray = list.toArray(new Integer[3]);
        assertArrayEquals(threeIntArray, resultArray);
    }

    public void helpEquality(Object o1, Object o2) {
        assertTrue(o1.equals(o2));
        assertTrue(o2.equals(o1));
        assertEquals(o1.hashCode(), o2.hashCode());
    }

    @Test
    public void empty() {
        ImList<Integer> empty1 = ImVectorImpl.empty();
        ImList<Integer> empty2 = ImVectorImpl.of(Collections.emptyList());
        ImList<Integer> empty3 = ImVectorImpl.of(new ArrayList<>());
        ImList<Integer> empty4 = ImVectorImpl.of();

        helpEquality(empty1, empty1);
        helpEquality(empty1, empty2);
        helpEquality(empty1, empty3);
        helpEquality(empty1, empty4);
        helpEquality(empty2, empty2);
        helpEquality(empty2, empty3);
        helpEquality(empty2, empty4);
        helpEquality(empty3, empty3);
        helpEquality(empty3, empty4);
        helpEquality(empty4, empty4);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx00() { ImVectorImpl.empty().get(Integer.MIN_VALUE); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx01() { ImVectorImpl.empty().get(-1); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx02() { ImVectorImpl.empty().get(0); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx03() { ImVectorImpl.empty().get(1); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx04() { ImVectorImpl.empty().get(Integer.MAX_VALUE); }

    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx10() { ImVectorImpl.of(Collections.emptyList()).get(Integer.MIN_VALUE); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx11() { ImVectorImpl.of(Collections.emptyList()).get(-1); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx12() { ImVectorImpl.of(Collections.emptyList()).get(0); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx13() { ImVectorImpl.of(Collections.emptyList()).get(1); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx14() { ImVectorImpl.of(Collections.emptyList()).get(Integer.MAX_VALUE); }

    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx20() { ImVectorImpl.of(new ArrayList<>()).get(Integer.MIN_VALUE); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx21() { ImVectorImpl.of(new ArrayList<>()).get(-1); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx22() { ImVectorImpl.of(new ArrayList<>()).get(0); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx23() { ImVectorImpl.of(new ArrayList<>()).get(1); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx24() { ImVectorImpl.of(new ArrayList<>()).get(Integer.MAX_VALUE); }

    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx30() { ImVectorImpl.of().get(Integer.MIN_VALUE); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx31() { ImVectorImpl.of().get(-1); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx32() { ImVectorImpl.of().get(0); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx33() { ImVectorImpl.of().get(1); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyEx34() { ImVectorImpl.of().get(Integer.MAX_VALUE); }

    @Test
    public void oneInt() {
        ImList<Integer> one1 = ImVectorImpl.of(1);
        List<Integer> oneList = new ArrayList<>();
        oneList.add(1);
        ImList<Integer> one2 = ImVectorImpl.of(oneList);
        ImList<Integer> one3 = ImVectorImpl.of(Collections.unmodifiableList(oneList));

        helpEquality(one1, one1);
        helpEquality(one1, one2);
        helpEquality(one1, one3);
        helpEquality(one2, one2);
        helpEquality(one2, one3);
        helpEquality(one3, one3);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void oneEx00() { ImVectorImpl.of(1).get(Integer.MIN_VALUE); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void oneEx01() { ImVectorImpl.of(1).get(-1); }
    @Test
    public void oneIsOne() {
        assertEquals(Integer.valueOf(1), ImVectorImpl.of(1).get(0));
    }
    @Test(expected = IndexOutOfBoundsException.class)
    public void oneEx03() { ImVectorImpl.of(1).get(1); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void oneEx04() { ImVectorImpl.of(1).get(Integer.MAX_VALUE); }

    @Test(expected = IndexOutOfBoundsException.class)
    public void oneEx10() {
        List<Integer> oneList = new ArrayList<>();
        oneList.add(1);
        ImVectorImpl.of(oneList).get(Integer.MIN_VALUE); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void oneEx11() {
        List<Integer> oneList = new ArrayList<>();
        oneList.add(1);
        ImVectorImpl.of(oneList).get(-1); }
    @Test
    public void oneIsOne2() {
        List<Integer> oneList = new ArrayList<>();
        oneList.add(1);
        assertEquals(Integer.valueOf(1), ImVectorImpl.of(oneList).get(0)); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void oneEx13() {
        List<Integer> oneList = new ArrayList<>();
        oneList.add(1);
        ImVectorImpl.of(oneList).get(1); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void oneEx14() {
        List<Integer> oneList = new ArrayList<>();
        oneList.add(1);
        ImVectorImpl.of(oneList).get(Integer.MAX_VALUE); }

    @Test(expected = IndexOutOfBoundsException.class)
    public void oneEx20() {
        List<Integer> oneList = new ArrayList<>();
        oneList.add(1);
        ImVectorImpl.of(Collections.unmodifiableList(oneList)).get(Integer.MIN_VALUE); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void oneEx21() {
        List<Integer> oneList = new ArrayList<>();
        oneList.add(1);
        ImVectorImpl.of(Collections.unmodifiableList(oneList)).get(-1); }
    @Test
    public void oneIsOne3() {
        List<Integer> oneList = new ArrayList<>();
        oneList.add(1);
        assertEquals(Integer.valueOf(1), ImVectorImpl.of(Collections.unmodifiableList(oneList)).get(0)); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void oneEx23() {
        List<Integer> oneList = new ArrayList<>();
        oneList.add(1);
        ImVectorImpl.of(Collections.unmodifiableList(oneList)).get(1); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void oneEx24() {
        List<Integer> oneList = new ArrayList<>();
        oneList.add(1);
        ImVectorImpl.of(Collections.unmodifiableList(oneList)).get(Integer.MAX_VALUE); }

}
