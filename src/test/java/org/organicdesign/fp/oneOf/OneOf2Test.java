package org.organicdesign.fp.oneOf;

import org.junit.Test;
import org.organicdesign.testUtils.EqualsContract;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class OneOf2Test {

    static class Str_Int extends OneOf2<String,Integer> {
        // Constructor
        private Str_Int(Object o, int n) { super(o, String.class, Integer.class, n); }

        // Static factory methods
        static Str_Int ofStr(String o) { return new Str_Int(o, 0); }
        static Str_Int ofInt(Integer o) { return new Str_Int(o, 1); }
    }

    @Test public void testBasics() {
        Str_Int oota = Str_Int.ofInt(57);
        assertEquals(Integer.valueOf(57), oota.match(x -> -99,
                                                     y -> y));
//        assertEquals(Integer.valueOf(57), oota.integer());

        Str_Int ootb = Str_Int.ofStr("right");
        assertEquals("right", ootb.match(x -> x,
                                         y -> "wrong"));
//        assertEquals("right", ootb.str());
    }

//    @Test(expected = ClassCastException.class)
//    public void testEx1() { Str_Int.ofInt(57).throw1("hi"); }
//
//    @Test(expected = ClassCastException.class)
//    public void testEx2() { Str_Int.ofStr("good").throw2(23); }

    @Test public void testEquality() {
        assertEquals(0, Str_Int.ofStr(null).hashCode());
        assertEquals(1, Str_Int.ofInt(null).hashCode());

        assertFalse(Str_Int.ofInt(41).equals(Str_Int.ofStr("A")));
        assertFalse(Str_Int.ofStr("A").equals(Str_Int.ofInt(41)));

        assertFalse(Str_Int.ofInt(65).equals(Str_Int.ofStr("A")));
        assertFalse(Str_Int.ofStr("A").equals(Str_Int.ofInt(65)));

        assertTrue(Str_Int.ofInt(37).equals(Str_Int.ofInt(37)));

        EqualsContract.equalsDistinctHashCode(Str_Int.ofStr("one"), Str_Int.ofStr("one"),
                                              Str_Int.ofStr("one"),
                                              Str_Int.ofStr("onf"));

        EqualsContract.equalsDistinctHashCode(Str_Int.ofInt(97), Str_Int.ofInt(97),
                                              Str_Int.ofInt(97),
                                              Str_Int.ofInt(-97));
    }

    @Test(expected = IllegalArgumentException.class)
    public void subClassEx____n() { new Str_Int(null, -1); }

    @Test(expected = IllegalArgumentException.class)
    public void subClassExa___n() { new Str_Int("hi", -1); }

    @Test(expected = IllegalArgumentException.class)
    public void subClassEx_b__n() { new Str_Int(1, -1); }

    @Test(expected = ClassCastException.class)
    public void subClassExa_c_0() { new Str_Int(2f, 0); }

    @Test(expected = ClassCastException.class)
    public void subClassExa__d0() { new Str_Int(3.0, 0); }

    @Test(expected = ClassCastException.class)
    public void subClassExa___1() { new Str_Int("hi", 1); }

    @Test(expected = ClassCastException.class)
    public void subClassEx_bc_1() { new Str_Int(2f, 1); }

    @Test(expected = ClassCastException.class)
    public void subClassEx_b_c1() { new Str_Int(3.0, 1); }


    @Test(expected = IllegalArgumentException.class)
    public void subClassExa___2() { new Str_Int("hi", 2); }

    @Test(expected = IllegalArgumentException.class)
    public void subClassEx_b__2() { new Str_Int(1, 2); }

    @Test(expected = IllegalArgumentException.class)
    public void subClassEx__cd2() { new Str_Int(3.0, 2); }
}