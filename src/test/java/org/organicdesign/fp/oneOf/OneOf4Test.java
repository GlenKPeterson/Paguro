package org.organicdesign.fp.oneOf;

import org.junit.Test;
import org.organicdesign.testUtils.EqualsContract;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

public class OneOf4Test {
    static class Str_Int_Float_Dub extends OneOf4<String,Integer,Float,Double> {

        // Constructor
        private Str_Int_Float_Dub(Object o, int sel) {
            super(o, String.class, Integer.class, Float.class, Double.class, sel);
        }

        // Static factory methods
        static Str_Int_Float_Dub ofStr(String o) { return new Str_Int_Float_Dub(o, 0); }
        static Str_Int_Float_Dub ofInt(Integer o) { return new Str_Int_Float_Dub(o, 1); }
        static Str_Int_Float_Dub ofFloat(Float o) { return new Str_Int_Float_Dub(o, 2); }
        static Str_Int_Float_Dub ofDub(Double o) { return new Str_Int_Float_Dub(o, 3); }
    }

    @Test
    public void testBasics() {
        Str_Int_Float_Dub oots = Str_Int_Float_Dub.ofStr("right");
        assertEquals("right", oots.match(s -> s,
                                         i -> "wrong",
                                         f -> "bad",
                                         d -> "evil"));
        assertEquals("\"right\":String|Integer|Float|Double", oots.toString());

        Str_Int_Float_Dub ooti = Str_Int_Float_Dub.ofInt(57);
        assertEquals(Integer.valueOf(57), ooti.match(s -> -99,
                                                     i -> i,
                                                     f -> 99,
                                                     d -> 2));
        assertEquals("57:String|Integer|Float|Double", ooti.toString());

        Str_Int_Float_Dub ootf = Str_Int_Float_Dub.ofFloat(57.2f);
        assertEquals(Float.valueOf(57.2f), ootf.match(s -> -99f,
                                                      i -> 99f,
                                                      f -> f,
                                                      d -> 2));
        assertEquals("57.2:String|Integer|Float|Double", ootf.toString());

        Str_Int_Float_Dub ootd = Str_Int_Float_Dub.ofDub(17.2);
        assertEquals(Double.valueOf(17.2), ootd.match(s -> -99f,
                                                      i -> 99f,
                                                      f -> 2,
                                                      d -> d));
        assertEquals("17.2:String|Integer|Float|Double", ootd.toString());
    }

    @Test public void testEquality() {
        assertEquals(0, Str_Int_Float_Dub.ofStr(null).hashCode());
        assertEquals(1, Str_Int_Float_Dub.ofInt(null).hashCode());
        assertEquals(2, Str_Int_Float_Dub.ofFloat(null).hashCode());
        assertEquals(3, Str_Int_Float_Dub.ofDub(null).hashCode());
        assertNotEquals(3, Str_Int_Float_Dub.ofDub(-1.3).hashCode());

        assertFalse(Str_Int_Float_Dub.ofFloat(5f).equals(Str_Int_Float_Dub.ofInt(5)));
        assertFalse(Str_Int_Float_Dub.ofInt(41).equals(Str_Int_Float_Dub.ofStr("A")));
        assertFalse(Str_Int_Float_Dub.ofStr("A").equals(Str_Int_Float_Dub.ofInt(41)));
        assertFalse(Str_Int_Float_Dub.ofFloat(-19.3f).equals(Str_Int_Float_Dub.ofDub(-19.3)));

        assertFalse(Str_Int_Float_Dub.ofInt(65).equals(Str_Int_Float_Dub.ofStr("A")));
        assertFalse(Str_Int_Float_Dub.ofStr("A").equals(Str_Int_Float_Dub.ofInt(65)));
        assertFalse(Str_Int_Float_Dub.ofFloat(65.0f).equals(Str_Int_Float_Dub.ofDub(65.0)));

        assertTrue(Str_Int_Float_Dub.ofInt(37).equals(Str_Int_Float_Dub.ofInt(37)));

        EqualsContract.equalsDistinctHashCode(Str_Int_Float_Dub.ofStr("one"), Str_Int_Float_Dub.ofStr("one"),
                                              Str_Int_Float_Dub.ofStr("one"),
                                              Str_Int_Float_Dub.ofStr("onf"));

        EqualsContract.equalsDistinctHashCode(Str_Int_Float_Dub.ofInt(97), Str_Int_Float_Dub.ofInt(97),
                                              Str_Int_Float_Dub.ofInt(97),
                                              Str_Int_Float_Dub.ofInt(-97));

        EqualsContract.equalsDistinctHashCode(Str_Int_Float_Dub.ofFloat(17f), Str_Int_Float_Dub.ofFloat(17f),
                                              Str_Int_Float_Dub.ofFloat(17f),
                                              Str_Int_Float_Dub.ofFloat(-17f));

        EqualsContract.equalsDistinctHashCode(Str_Int_Float_Dub.ofDub(31.7), Str_Int_Float_Dub.ofDub(31.7),
                                              Str_Int_Float_Dub.ofDub(31.7),
                                              Str_Int_Float_Dub.ofDub(-3333.7));
    }

    @Test(expected = IllegalArgumentException.class)
    public void subClassEx____n() { new Str_Int_Float_Dub(null, -1); }

    @Test(expected = IllegalArgumentException.class)
    public void subClassExa___n() { new Str_Int_Float_Dub("hi", -1); }

    @Test(expected = IllegalArgumentException.class)
    public void subClassEx_b__n() { new Str_Int_Float_Dub(1, -1); }

    @Test(expected = ClassCastException.class)
    public void subClassExa_c_0() { new Str_Int_Float_Dub(2f, 0); }

    @Test(expected = ClassCastException.class)
    public void subClassExa__d0() { new Str_Int_Float_Dub(3.0, 0); }

    @Test(expected = ClassCastException.class)
    public void subClassExa___1() { new Str_Int_Float_Dub("hi", 1); }

    @Test(expected = ClassCastException.class)
    public void subClassEx_bc_1() { new Str_Int_Float_Dub(2f, 1); }

    @Test(expected = ClassCastException.class)
    public void subClassEx_b_c1() { new Str_Int_Float_Dub(3.0, 1); }


    @Test(expected = ClassCastException.class)
    public void subClassExa___2() { new Str_Int_Float_Dub("hi", 2); }

    @Test(expected = ClassCastException.class)
    public void subClassEx_b__2() { new Str_Int_Float_Dub(1, 2); }

    @Test(expected = ClassCastException.class)
    public void subClassEx__cd2() { new Str_Int_Float_Dub(3.0, 2); }


    @Test(expected = ClassCastException.class)
    public void subClassExa___3() { new Str_Int_Float_Dub("hi", 3); }

    @Test(expected = ClassCastException.class)
    public void subClassEx_b__3() { new Str_Int_Float_Dub(1, 3); }

    @Test(expected = ClassCastException.class)
    public void subClassEx__c_3() { new Str_Int_Float_Dub(2f, 3); }


    @Test(expected = IllegalArgumentException.class)
    public void subClassExa___4() { new Str_Int_Float_Dub("hi", 4); }

    @Test(expected = IllegalArgumentException.class)
    public void subClassEx_b__4() { new Str_Int_Float_Dub(1, 4); }

    @Test(expected = IllegalArgumentException.class)
    public void subClassEx__c_4() { new Str_Int_Float_Dub(2f, 4); }

    @Test(expected = IllegalArgumentException.class)
    public void subClassEx___d4() { new Str_Int_Float_Dub(3.0, 4); }

    @Test(expected = IllegalArgumentException.class)
    public void subClassEx____4() { new Str_Int_Float_Dub(null, 4); }
}