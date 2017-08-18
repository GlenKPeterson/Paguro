package org.organicdesign.fp.oneOf;

import org.junit.Test;
import org.organicdesign.testUtils.EqualsContract;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class OneOf3Test {
    static class Str_Int_Float extends OneOf3<String,Integer,Float> {

        // Constructor
        private Str_Int_Float(String s, Integer i, Float f, int n) { super(s, i, f, n); }

        // Static factory methods
        static Str_Int_Float ofStr(String s) { return new Str_Int_Float(s, null, null, 1); }
        static Str_Int_Float ofInt(Integer i) { return new Str_Int_Float(null, i, null, 2); }
        static Str_Int_Float ofFloat(Float f) { return new Str_Int_Float(null, null, f, 3); }

        // Ensure we use the one and only instance of this array
        transient static final String[] NAMES = { "Str", "Int", "Float"};
        @Override protected String typeName(int selIdx) { return NAMES[selIdx - 1]; }
    }

    @Test
    public void testBasics() {
        Str_Int_Float oots = Str_Int_Float.ofStr("right");
        assertEquals("right", oots.match(s -> s,
                                         i -> "wrong",
                                         f -> "bad"));
        assertEquals("Str(\"right\")", oots.toString());

        Str_Int_Float ooti = Str_Int_Float.ofInt(57);
        assertEquals(Integer.valueOf(57), ooti.match(s -> -99,
                                                     i -> i,
                                                     f -> 99));
        assertEquals("Int(57)", ooti.toString());

        Str_Int_Float ootf = Str_Int_Float.ofFloat(57.2f);
        assertEquals(Float.valueOf(57.2f), ootf.match(s -> -99f,
                                                     i -> 99f,
                                                     f -> f));
        assertEquals("Float(57.2)", ootf.toString());


    }

    @Test public void testEquality() {
        assertEquals(1, Str_Int_Float.ofStr(null).hashCode());
        assertEquals(2, Str_Int_Float.ofInt(null).hashCode());
        assertEquals(3, Str_Int_Float.ofFloat(null).hashCode());

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
    public void subClassEx1() { new Str_Int_Float(null, null, null, 4); }

    @Test(expected = IllegalArgumentException.class)
    public void subClassEx2() { new Str_Int_Float(null, null, null, 0); }

    @Test(expected = IllegalArgumentException.class)
    public void subClassEx3() { new Str_Int_Float(null, null, null, -99); }

    @Test(expected = IllegalArgumentException.class)
    public void subClassEx4() { new Str_Int_Float(null, null, null, 537); }

    @Test(expected = IllegalArgumentException.class)
    public void subClassEx5() { new Str_Int_Float(null, null, null, Integer.MAX_VALUE); }

    @Test(expected = IllegalArgumentException.class)
    public void subClassEx6() { new Str_Int_Float(null, null, null, Integer.MIN_VALUE); }
}