package org.organicdesign.fp.oneOf;

import com.planbase.testutils.EqualsContract;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OneOf3Test {
    static class Str_Int_Float extends OneOf3<String,Integer,Float> {

        // Constructor
        private Str_Int_Float(@NotNull Object o, int n) {
            super(o, String.class, Integer.class, Float.class, n);
        }

        // Static factory methods
        static Str_Int_Float of(@NotNull String o) { return new Str_Int_Float(o, 0); }
        static Str_Int_Float of(@NotNull Integer o) { return new Str_Int_Float(o, 1); }
        static Str_Int_Float of(@NotNull Float o) { return new Str_Int_Float(o, 2); }
    }

    @Test
    public void testBasics() {
        Str_Int_Float oots = Str_Int_Float.of("right");
        assertEquals("right", oots.match(s -> s,
                                         i -> "wrong",
                                         f -> "bad"));
        assertEquals("\"right\":String|Integer|Float", oots.toString());

        Str_Int_Float ooti = Str_Int_Float.of(57);
        assertEquals(Integer.valueOf(57), ooti.match(s -> -99,
                                                     i -> i,
                                                     f -> 99));
        assertEquals("57:String|Integer|Float", ooti.toString());

        Str_Int_Float ootf = Str_Int_Float.of(57.2f);
        assertEquals(Float.valueOf(57.2f), ootf.match(s -> -99f,
                                                     i -> 99f,
                                                     f -> f));
        assertEquals("57.2f:String|Integer|Float", ootf.toString());
    }

    @Test public void testEquality() {
        assertEquals(0, Str_Int_Float.of("").hashCode());
        assertEquals(1, Str_Int_Float.of(0).hashCode());
        assertEquals(2, Str_Int_Float.of(0f).hashCode());

        assertFalse(Str_Int_Float.of(5f).equals(Str_Int_Float.of(5)));
        assertFalse(Str_Int_Float.of(41).equals(Str_Int_Float.of("A")));
        assertFalse(Str_Int_Float.of("A").equals(Str_Int_Float.of(41)));

        assertFalse(Str_Int_Float.of(65).equals(Str_Int_Float.of("A")));
        assertFalse(Str_Int_Float.of("A").equals(Str_Int_Float.of(65)));

        assertTrue(Str_Int_Float.of(37).equals(Str_Int_Float.of(37)));

        EqualsContract.equalsDistinctHashCode(Str_Int_Float.of("one"), Str_Int_Float.of("one"),
                                              Str_Int_Float.of("one"),
                                              Str_Int_Float.of("onf"));

        EqualsContract.equalsDistinctHashCode(Str_Int_Float.of(97), Str_Int_Float.of(97),
                                              Str_Int_Float.of(97),
                                              Str_Int_Float.of(-97));

        EqualsContract.equalsDistinctHashCode(Str_Int_Float.of(17f), Str_Int_Float.of(17f),
                                              Str_Int_Float.of(17f),
                                              Str_Int_Float.of(-17f));
    }

    // Cut-pasted and edited from OneOf4Test
    @Test
    public void subclassExceptions() {
        @NotNull Exception ex;
        ex = assertThrowsExactly(IllegalArgumentException.class, () ->
                new Str_Int_Float(1, 0));
        assertEquals("Index 0 takes a java.lang.String, but was passed a java.lang.Integer", ex.getMessage());

        ex = assertThrowsExactly(IllegalArgumentException.class, () ->
                new Str_Int_Float(2f, 0));
        assertEquals("Index 0 takes a java.lang.String, but was passed a java.lang.Float", ex.getMessage());

        ex = assertThrowsExactly(IllegalArgumentException.class, () ->
                new Str_Int_Float(3.0, 0));
        assertEquals("Index 0 takes a java.lang.String, but was passed a java.lang.Double", ex.getMessage());

        ex = assertThrowsExactly(IllegalArgumentException.class, () ->
                new Str_Int_Float('x', 0));
        assertEquals("Index 0 takes a java.lang.String, but was passed a java.lang.Character", ex.getMessage());

        ex = assertThrowsExactly(IllegalArgumentException.class, () ->
                new Str_Int_Float("hi", 1));
        assertEquals("Index 1 takes a java.lang.Integer, but was passed a java.lang.String", ex.getMessage());

        ex = assertThrowsExactly(IllegalArgumentException.class, () ->
                new Str_Int_Float(2f, 1));
        assertEquals("Index 1 takes a java.lang.Integer, but was passed a java.lang.Float", ex.getMessage());

        ex = assertThrowsExactly(IllegalArgumentException.class, () ->
                new Str_Int_Float(3.0, 1));
        assertEquals("Index 1 takes a java.lang.Integer, but was passed a java.lang.Double", ex.getMessage());

        ex = assertThrowsExactly(IllegalArgumentException.class, () ->
                new Str_Int_Float('x', 1));
        assertEquals("Index 1 takes a java.lang.Integer, but was passed a java.lang.Character", ex.getMessage());

        ex = assertThrowsExactly(IllegalArgumentException.class, () ->
                new Str_Int_Float("hi", 2));
        assertEquals("Index 2 takes a java.lang.Float, but was passed a java.lang.String", ex.getMessage());

        ex = assertThrowsExactly(IllegalArgumentException.class, () ->
                new Str_Int_Float(1, 2));
        assertEquals("Index 2 takes a java.lang.Float, but was passed a java.lang.Integer", ex.getMessage());

        ex = assertThrowsExactly(IllegalArgumentException.class, () ->
                new Str_Int_Float(3.0, 2));
        assertEquals("Index 2 takes a java.lang.Float, but was passed a java.lang.Double", ex.getMessage());

        ex = assertThrowsExactly(IllegalArgumentException.class, () ->
                new Str_Int_Float('x', 2));
        assertEquals("Index 2 takes a java.lang.Float, but was passed a java.lang.Character", ex.getMessage());

        ex = assertThrowsExactly(IllegalArgumentException.class, () ->
                new Str_Int_Float("hi", -1));
        assertEquals("Selected item index must be 0-2", ex.getMessage());

        ex = assertThrowsExactly(IllegalArgumentException.class, () ->
                new Str_Int_Float(1, 3));
        assertEquals("Selected item index must be 0-2", ex.getMessage());

        ex = assertThrowsExactly(IllegalArgumentException.class, () ->
                new Str_Int_Float(2f, -1));
        assertEquals("Selected item index must be 0-2", ex.getMessage());

        ex = assertThrowsExactly(IllegalArgumentException.class, () ->
                new Str_Int_Float(3.0, 3));
        assertEquals("Selected item index must be 0-2", ex.getMessage());

        ex = assertThrowsExactly(IllegalArgumentException.class, () ->
                new Str_Int_Float('x', -1));
        assertEquals("Selected item index must be 0-2", ex.getMessage());
    }
}