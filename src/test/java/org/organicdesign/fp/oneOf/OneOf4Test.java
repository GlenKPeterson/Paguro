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
        private Str_Int_Float_Dub(String s, Integer i, Float f, Double d, int sel) {
            super(s, String.class, i, Integer.class, f, Float.class, d, Double.class, sel);
        }

        // Static factory methods
        static Str_Int_Float_Dub ofStr(String s) { return new Str_Int_Float_Dub(s, null, null, null, 0); }
        static Str_Int_Float_Dub ofInt(Integer i) { return new Str_Int_Float_Dub(null, i, null, null, 1); }
        static Str_Int_Float_Dub ofFloat(Float f) { return new Str_Int_Float_Dub(null, null, f, null, 2); }
        static Str_Int_Float_Dub ofDub(Double d) { return new Str_Int_Float_Dub(null, null, null, d, 3); }
    }

    @Test
    public void testBasics() {
        Str_Int_Float_Dub oots = Str_Int_Float_Dub.ofStr("right");
        assertEquals("right", oots.match(s -> s,
                                         i -> "wrong",
                                         f -> "bad",
                                         d -> "evil"));
        assertEquals("String/4(\"right\")", oots.toString());

        Str_Int_Float_Dub ooti = Str_Int_Float_Dub.ofInt(57);
        assertEquals(Integer.valueOf(57), ooti.match(s -> -99,
                                                     i -> i,
                                                     f -> 99,
                                                     d -> 2));
        assertEquals("Integer/4(57)", ooti.toString());

        Str_Int_Float_Dub ootf = Str_Int_Float_Dub.ofFloat(57.2f);
        assertEquals(Float.valueOf(57.2f), ootf.match(s -> -99f,
                                                      i -> 99f,
                                                      f -> f,
                                                      d -> 2));
        assertEquals("Float/4(57.2)", ootf.toString());

        Str_Int_Float_Dub ootd = Str_Int_Float_Dub.ofDub(17.2);
        assertEquals(Double.valueOf(17.2), ootd.match(s -> -99f,
                                                      i -> 99f,
                                                      f -> 2,
                                                      d -> d));
        assertEquals("Double/4(17.2)", ootd.toString());
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
    public void subClassEx1000n() { new Str_Int_Float_Dub("hi", null, null, null, -1); }

    @Test(expected = IllegalArgumentException.class)
    public void subClassEx10001() { new Str_Int_Float_Dub("hi", null, null, null, 1); }

    @Test(expected = IllegalArgumentException.class)
    public void subClassEx10002() { new Str_Int_Float_Dub("hi", null, null, null, 2); }

    @Test(expected = IllegalArgumentException.class)
    public void subClassEx10003() { new Str_Int_Float_Dub("hi", null, null, null, 3); }

    @Test(expected = IllegalArgumentException.class)
    public void subClassEx10004() { new Str_Int_Float_Dub("hi", null, null, null, 4); }

    @Test(expected = IllegalArgumentException.class)
    public void subClassEx11000() { new Str_Int_Float_Dub("hi", 1, null, null, 0); }

    @Test(expected = IllegalArgumentException.class)
    public void subClassEx11001() { new Str_Int_Float_Dub("hi", 1, null, null, 1); }

    @Test(expected = IllegalArgumentException.class)
    public void subClassEx10100() { new Str_Int_Float_Dub("hi", null, 2f, null, 0); }

    @Test(expected = IllegalArgumentException.class)
    public void subClassEx10010() { new Str_Int_Float_Dub("hi", null, null, 3.0, 0); }

    @Test(expected = IllegalArgumentException.class)
    public void subClassEx0100n() { new Str_Int_Float_Dub(null, 1, null, null, -1); }

    @Test(expected = IllegalArgumentException.class)
    public void subClassEx01000() { new Str_Int_Float_Dub(null, 1, null, null, 0); }

    @Test(expected = IllegalArgumentException.class)
    public void subClassEx01002() { new Str_Int_Float_Dub(null, 1, null, null, 2); }

    @Test(expected = IllegalArgumentException.class)
    public void subClassEx01003() { new Str_Int_Float_Dub(null, 1, null, null, 3); }

    @Test(expected = IllegalArgumentException.class)
    public void subClassEx01004() { new Str_Int_Float_Dub(null, 1, null, null, 4); }

    @Test(expected = IllegalArgumentException.class)
    public void subClassEx01101() { new Str_Int_Float_Dub(null, 1, 2f, null, 1); }

    @Test(expected = IllegalArgumentException.class)
    public void subClassEx5() { new Str_Int_Float_Dub(null, 1, null, 3.0, 1); }

    @Test(expected = IllegalArgumentException.class)
    public void subClassEx6() { new Str_Int_Float_Dub(null, null, 2f, 3.0, 2); }

    @Test(expected = IllegalArgumentException.class)
    public void subClassEx0000n() { new Str_Int_Float_Dub(null, null, null, null, -1); }

    @Test(expected = IllegalArgumentException.class)
    public void subClassEx00004() { new Str_Int_Float_Dub(null, null, null, null, 4); }
}