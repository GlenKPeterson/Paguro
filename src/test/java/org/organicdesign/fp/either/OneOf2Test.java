package org.organicdesign.fp.either;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.organicdesign.fp.testUtils.EqualsContract.equalsDistinctHashCode;

public class OneOf2Test {
    @Test public void testBasics() {
        OneOf2<Integer,String> oota = OneOf2._1(57);
        assertEquals(Integer.valueOf(57), oota.typeMatch(x -> x,
                                                         y -> "wrong"));

        OneOf2<Integer,String> ootb = OneOf2._2("right");
        assertEquals("right", ootb.typeMatch(x -> -99,
                                             y -> y));
    }

    @Test(expected = IllegalStateException.class)
    public void testEx1() { OneOf2._1(57).throw2("hi"); }

    @Test(expected = IllegalStateException.class)
    public void testEx2() { OneOf2._2("good").throw1(23); }

    @Test public void testEquality() {
        assertEquals(0, OneOf2._1(null).hashCode());
        assertEquals(~0, OneOf2._2(null).hashCode());

        assertFalse(OneOf2._1(37).equals(OneOf2._2(37)));
        assertFalse(OneOf2._2(37).equals(OneOf2._1(37)));

        equalsDistinctHashCode(OneOf2._1("one"), OneOf2._1("one"), OneOf2._1("one"),
                               OneOf2._1("onf"));

        equalsDistinctHashCode(OneOf2._2(97), OneOf2._2(97), OneOf2._2(97),
                               OneOf2._2(-97));
    }

    class A_or_B extends OneOf2<String,Integer> {
        A_or_B(String s, Integer i, int n) { super(s, i, n); }
    }

    @Test(expected = IllegalArgumentException.class)
    public void subClassEx1() { new A_or_B(null, null, 3); }

    @Test(expected = IllegalArgumentException.class)
    public void subClassEx2() { new A_or_B(null, null, 0); }

    @Test(expected = IllegalArgumentException.class)
    public void subClassEx3() { new A_or_B(null, null, -99); }

    @Test(expected = IllegalArgumentException.class)
    public void subClassEx4() { new A_or_B(null, null, 537); }

    @Test(expected = IllegalArgumentException.class)
    public void subClassEx5() { new A_or_B(null, null, Integer.MAX_VALUE); }

    @Test(expected = IllegalArgumentException.class)
    public void subClassEx6() { new A_or_B(null, null, Integer.MIN_VALUE); }
}