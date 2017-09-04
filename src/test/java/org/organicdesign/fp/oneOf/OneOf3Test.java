package org.organicdesign.fp.oneOf;

import org.junit.Test;
import org.organicdesign.testUtils.EqualsContract;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class OneOf3Test {
    static class Str_Int_Float extends OneOf3<String,Integer,Float> {

        // Constructor
        private Str_Int_Float(Object o, int n) {
            super(o, String.class, Integer.class, Float.class, n);
        }

        // Static factory methods
        static Str_Int_Float ofStr(String o) { return new Str_Int_Float(o, 0); }
        static Str_Int_Float ofInt(Integer o) { return new Str_Int_Float(o, 1); }
        static Str_Int_Float ofFloat(Float o) { return new Str_Int_Float(o, 2); }
    }

    @Test
    public void testBasics() {
        Str_Int_Float oots = Str_Int_Float.ofStr("right");
        assertEquals("right", oots.match(s -> s,
                                         i -> "wrong",
                                         f -> "bad"));
        assertEquals("\"right\":String|Integer|Float", oots.toString());

        Str_Int_Float ooti = Str_Int_Float.ofInt(57);
        assertEquals(Integer.valueOf(57), ooti.match(s -> -99,
                                                     i -> i,
                                                     f -> 99));
        assertEquals("57:String|Integer|Float", ooti.toString());

        Str_Int_Float ootf = Str_Int_Float.ofFloat(57.2f);
        assertEquals(Float.valueOf(57.2f), ootf.match(s -> -99f,
                                                     i -> 99f,
                                                     f -> f));
        assertEquals("57.2:String|Integer|Float", ootf.toString());
    }

    @Test public void testEquality() {
        assertEquals(0, Str_Int_Float.ofStr(null).hashCode());
        assertEquals(1, Str_Int_Float.ofInt(null).hashCode());
        assertEquals(2, Str_Int_Float.ofFloat(null).hashCode());

        assertFalse(Str_Int_Float.ofFloat(5f).equals(Str_Int_Float.ofInt(5)));
        assertFalse(Str_Int_Float.ofInt(41).equals(Str_Int_Float.ofStr("A")));
        assertFalse(Str_Int_Float.ofStr("A").equals(Str_Int_Float.ofInt(41)));

        assertFalse(Str_Int_Float.ofInt(65).equals(Str_Int_Float.ofStr("A")));
        assertFalse(Str_Int_Float.ofStr("A").equals(Str_Int_Float.ofInt(65)));

        assertTrue(Str_Int_Float.ofInt(37).equals(Str_Int_Float.ofInt(37)));

        EqualsContract.equalsDistinctHashCode(Str_Int_Float.ofStr("one"), Str_Int_Float.ofStr("one"),
                                              Str_Int_Float.ofStr("one"),
                                              Str_Int_Float.ofStr("onf"));

        EqualsContract.equalsDistinctHashCode(Str_Int_Float.ofInt(97), Str_Int_Float.ofInt(97),
                                              Str_Int_Float.ofInt(97),
                                              Str_Int_Float.ofInt(-97));

        EqualsContract.equalsDistinctHashCode(Str_Int_Float.ofFloat(17f), Str_Int_Float.ofFloat(17f),
                                              Str_Int_Float.ofFloat(17f),
                                              Str_Int_Float.ofFloat(-17f));
    }

    @Test(expected = IllegalArgumentException.class)
    public void subClassEx____n() { new Str_Int_Float(null, -1); }

    @Test(expected = IllegalArgumentException.class)
    public void subClassExa___n() { new Str_Int_Float("hi", -1); }

    @Test(expected = IllegalArgumentException.class)
    public void subClassEx_b__n() { new Str_Int_Float(1, -1); }

    @Test(expected = ClassCastException.class)
    public void subClassExa_c_0() { new Str_Int_Float(2f, 0); }

    @Test(expected = ClassCastException.class)
    public void subClassExa__d0() { new Str_Int_Float(3.0, 0); }

    @Test(expected = ClassCastException.class)
    public void subClassExa___1() { new Str_Int_Float("hi", 1); }

    @Test(expected = ClassCastException.class)
    public void subClassEx_bc_1() { new Str_Int_Float(2f, 1); }

    @Test(expected = ClassCastException.class)
    public void subClassEx_b_c1() { new Str_Int_Float(3.0, 1); }


    @Test(expected = ClassCastException.class)
    public void subClassExa___2() { new Str_Int_Float("hi", 2); }

    @Test(expected = ClassCastException.class)
    public void subClassEx_b__2() { new Str_Int_Float(1, 2); }

    @Test(expected = ClassCastException.class)
    public void subClassEx__cd2() { new Str_Int_Float(3.0, 2); }


    @Test(expected = IllegalArgumentException.class)
    public void subClassExa___3() { new Str_Int_Float("hi", 3); }

    @Test(expected = IllegalArgumentException.class)
    public void subClassEx_b__3() { new Str_Int_Float(1, 3); }

    @Test(expected = IllegalArgumentException.class)
    public void subClassEx__c_3() { new Str_Int_Float(2f, 3); }

    @Test(expected = IllegalArgumentException.class)
    public void subClassEx____4() { new Str_Int_Float(null, 3); }
}