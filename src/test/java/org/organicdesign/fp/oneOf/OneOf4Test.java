package org.organicdesign.fp.oneOf;

import com.planbase.testutils.EqualsContract;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OneOf4Test {
    static class Str_Int_Float_Dub extends OneOf4<String,Integer,Float,Double> {

        // Constructor
        private Str_Int_Float_Dub(Object o, int sel) {
            super(o, String.class, Integer.class, Float.class, Double.class, sel);
        }

        // Static factory methods
        static Str_Int_Float_Dub of(String o) { return new Str_Int_Float_Dub(o, 0); }
        static Str_Int_Float_Dub of(Integer o) { return new Str_Int_Float_Dub(o, 1); }
        static Str_Int_Float_Dub of(Float o) { return new Str_Int_Float_Dub(o, 2); }
        static Str_Int_Float_Dub of(Double o) { return new Str_Int_Float_Dub(o, 3); }
    }

    @Test
    public void testBasics() {
        Str_Int_Float_Dub oots = Str_Int_Float_Dub.of("right");
        assertEquals("right", oots.match(s -> s,
                                         i -> "wrong",
                                         f -> "bad",
                                         d -> "evil"));
        assertEquals("\"right\":String|Integer|Float|Double", oots.toString());

        Str_Int_Float_Dub ooti = Str_Int_Float_Dub.of(57);
        assertEquals(Integer.valueOf(57), ooti.match(s -> -99,
                                                     i -> i,
                                                     f -> 99,
                                                     d -> 2));
        assertEquals("57:String|Integer|Float|Double", ooti.toString());

        Str_Int_Float_Dub ootf = Str_Int_Float_Dub.of(57.2f);
        assertEquals(Float.valueOf(57.2f), (Float) ootf.match(s -> -99f,
                                                      i -> 99f,
                                                      f -> f,
                                                      d -> 2));
        assertEquals("57.2f:String|Integer|Float|Double", ootf.toString());

        Str_Int_Float_Dub ootd = Str_Int_Float_Dub.of(17.2);
        assertEquals(Double.valueOf(17.2), (Double) ootd.match(s -> -99f,
                                                      i -> 99f,
                                                      f -> 2,
                                                      d -> d));
        assertEquals("17.2:String|Integer|Float|Double", ootd.toString());
    }

    @Test public void testEquality() {
        assertEquals(0, Str_Int_Float_Dub.of("").hashCode());
        assertEquals(1, Str_Int_Float_Dub.of(0).hashCode());
        assertEquals(2, Str_Int_Float_Dub.of(0f).hashCode());
        assertEquals(3, Str_Int_Float_Dub.of(0.0).hashCode());
        assertNotEquals(3, Str_Int_Float_Dub.of(-1.3).hashCode());

        assertFalse(Str_Int_Float_Dub.of(5f).equals(Str_Int_Float_Dub.of(5)));
        assertFalse(Str_Int_Float_Dub.of(41).equals(Str_Int_Float_Dub.of("A")));
        assertFalse(Str_Int_Float_Dub.of("A").equals(Str_Int_Float_Dub.of(41)));
        assertFalse(Str_Int_Float_Dub.of(-19.3f).equals(Str_Int_Float_Dub.of(-19.3)));

        assertFalse(Str_Int_Float_Dub.of(65).equals(Str_Int_Float_Dub.of("A")));
        assertFalse(Str_Int_Float_Dub.of("A").equals(Str_Int_Float_Dub.of(65)));
        assertFalse(Str_Int_Float_Dub.of(65.0f).equals(Str_Int_Float_Dub.of(65.0)));

        assertTrue(Str_Int_Float_Dub.of(37).equals(Str_Int_Float_Dub.of(37)));

        EqualsContract.equalsDistinctHashCode(Str_Int_Float_Dub.of("one"), Str_Int_Float_Dub.of("one"),
                                              Str_Int_Float_Dub.of("one"),
                                              Str_Int_Float_Dub.of("onf"));

        EqualsContract.equalsDistinctHashCode(Str_Int_Float_Dub.of(97), Str_Int_Float_Dub.of(97),
                                              Str_Int_Float_Dub.of(97),
                                              Str_Int_Float_Dub.of(-97));

        EqualsContract.equalsDistinctHashCode(Str_Int_Float_Dub.of(17f), Str_Int_Float_Dub.of(17f),
                                              Str_Int_Float_Dub.of(17f),
                                              Str_Int_Float_Dub.of(-17f));

        EqualsContract.equalsDistinctHashCode(Str_Int_Float_Dub.of(31.7), Str_Int_Float_Dub.of(31.7),
                                              Str_Int_Float_Dub.of(31.7),
                                              Str_Int_Float_Dub.of(-3333.7));
    }

    // Cut-pasted and edited from OneOf5Test
    @Test
    public void subclassExceptions() {
        @NotNull Exception ex;
        ex = assertThrowsExactly(IllegalArgumentException.class, () ->
                new Str_Int_Float_Dub(1, 0));
        assertEquals("Index 0 takes a java.lang.String, but was passed a java.lang.Integer", ex.getMessage());

        ex = assertThrowsExactly(IllegalArgumentException.class, () ->
                new Str_Int_Float_Dub(2f, 0));
        assertEquals("Index 0 takes a java.lang.String, but was passed a java.lang.Float", ex.getMessage());

        ex = assertThrowsExactly(IllegalArgumentException.class, () ->
                new Str_Int_Float_Dub(3.0, 0));
        assertEquals("Index 0 takes a java.lang.String, but was passed a java.lang.Double", ex.getMessage());

        ex = assertThrowsExactly(IllegalArgumentException.class, () ->
                new Str_Int_Float_Dub('x', 0));
        assertEquals("Index 0 takes a java.lang.String, but was passed a java.lang.Character", ex.getMessage());

        ex = assertThrowsExactly(IllegalArgumentException.class, () ->
                new Str_Int_Float_Dub("hi", 1));
        assertEquals("Index 1 takes a java.lang.Integer, but was passed a java.lang.String", ex.getMessage());

        ex = assertThrowsExactly(IllegalArgumentException.class, () ->
                new Str_Int_Float_Dub(2f, 1));
        assertEquals("Index 1 takes a java.lang.Integer, but was passed a java.lang.Float", ex.getMessage());

        ex = assertThrowsExactly(IllegalArgumentException.class, () ->
                new Str_Int_Float_Dub(3.0, 1));
        assertEquals("Index 1 takes a java.lang.Integer, but was passed a java.lang.Double", ex.getMessage());

        ex = assertThrowsExactly(IllegalArgumentException.class, () ->
                new Str_Int_Float_Dub('x', 1));
        assertEquals("Index 1 takes a java.lang.Integer, but was passed a java.lang.Character", ex.getMessage());

        ex = assertThrowsExactly(IllegalArgumentException.class, () ->
                new Str_Int_Float_Dub("hi", 2));
        assertEquals("Index 2 takes a java.lang.Float, but was passed a java.lang.String", ex.getMessage());

        ex = assertThrowsExactly(IllegalArgumentException.class, () ->
                new Str_Int_Float_Dub(1, 2));
        assertEquals("Index 2 takes a java.lang.Float, but was passed a java.lang.Integer", ex.getMessage());

        ex = assertThrowsExactly(IllegalArgumentException.class, () ->
                new Str_Int_Float_Dub(3.0, 2));
        assertEquals("Index 2 takes a java.lang.Float, but was passed a java.lang.Double", ex.getMessage());

        ex = assertThrowsExactly(IllegalArgumentException.class, () ->
                new Str_Int_Float_Dub('x', 2));
        assertEquals("Index 2 takes a java.lang.Float, but was passed a java.lang.Character", ex.getMessage());

        ex = assertThrowsExactly(IllegalArgumentException.class, () ->
                new Str_Int_Float_Dub("hi", 3));
        assertEquals("Index 3 takes a java.lang.Double, but was passed a java.lang.String", ex.getMessage());

        ex = assertThrowsExactly(IllegalArgumentException.class, () ->
                new Str_Int_Float_Dub(1, 3));
        assertEquals("Index 3 takes a java.lang.Double, but was passed a java.lang.Integer", ex.getMessage());

        ex = assertThrowsExactly(IllegalArgumentException.class, () ->
                new Str_Int_Float_Dub(2f, 3));
        assertEquals("Index 3 takes a java.lang.Double, but was passed a java.lang.Float", ex.getMessage());

        ex = assertThrowsExactly(IllegalArgumentException.class, () ->
                new Str_Int_Float_Dub('x', 3));
        assertEquals("Index 3 takes a java.lang.Double, but was passed a java.lang.Character", ex.getMessage());

        ex = assertThrowsExactly(IllegalArgumentException.class, () ->
                new Str_Int_Float_Dub("hi", -1));
        assertEquals("Selected item index must be 0-3", ex.getMessage());

        ex = assertThrowsExactly(IllegalArgumentException.class, () ->
                new Str_Int_Float_Dub(1, 4));
        assertEquals("Selected item index must be 0-3", ex.getMessage());

        ex = assertThrowsExactly(IllegalArgumentException.class, () ->
                new Str_Int_Float_Dub(2f, -1));
        assertEquals("Selected item index must be 0-3", ex.getMessage());

        ex = assertThrowsExactly(IllegalArgumentException.class, () ->
                new Str_Int_Float_Dub(3.0, 4));
        assertEquals("Selected item index must be 0-3", ex.getMessage());

        ex = assertThrowsExactly(IllegalArgumentException.class, () ->
                new Str_Int_Float_Dub('x', -1));
        assertEquals("Selected item index must be 0-3", ex.getMessage());
    }
}