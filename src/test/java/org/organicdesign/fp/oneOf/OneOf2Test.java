package org.organicdesign.fp.oneOf;

import com.planbase.testutils.EqualsContract;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 2nd-class union types for Java.  There are 2 examples here.  One of a union of unrelated types (String and Integer)
 * and the other of two types (String and StringBuilder) that share a common interface (CharSequence).
 * You may want to add an "OrNull" or "OrMissing" case to any of the OneOf_ types, in which case
 * look at {@link OneOf2OrNoneTest} to see how this is done.
 */
public class OneOf2Test {

    /** No common ancestor */
    static class Str_Int extends OneOf2<String,Integer> {
        // Constructor
        private Str_Int(@NotNull Object o, int n) { super(o, String.class, Integer.class, n); }

        // Static factory methods
        static Str_Int of(@NotNull String o) { return new Str_Int(o, 0); }
        static Str_Int of(@NotNull Integer o) { return new Str_Int(o, 1); }
    }

    @Test public void testBasics() {
        Str_Int oota = Str_Int.of(57);
        assertEquals(Integer.valueOf(57), oota.match(x -> -99,
                                                     y -> y));

        Str_Int ootb = Str_Int.of("right");
        assertEquals("right", ootb.match(x -> x,
                                         y -> "wrong"));
    }

    @Test public void testEquality() {
        assertEquals(0, Str_Int.of("").hashCode());
        assertEquals(1, Str_Int.of(0).hashCode());

        assertFalse(Str_Int.of(41).equals(Str_Int.of("A")));
        assertFalse(Str_Int.of("A").equals(Str_Int.of(41)));

        assertFalse(Str_Int.of(65).equals(Str_Int.of("A")));
        assertFalse(Str_Int.of("A").equals(Str_Int.of(65)));

        assertTrue(Str_Int.of(37).equals(Str_Int.of(37)));

        EqualsContract.equalsDistinctHashCode(Str_Int.of("one"), Str_Int.of("one"),
                                              Str_Int.of("one"),
                                              Str_Int.of("onf"));

        EqualsContract.equalsDistinctHashCode(Str_Int.of(97), Str_Int.of(97),
                                              Str_Int.of(97),
                                              Str_Int.of(-97));
    }

    /**
     * String and StringBuilder both implement CharSequence, so our OneOf2 subtype can too.
     * Polymorphism when you want it, union types when you don't.
     */
    static class Str_Bldr extends OneOf2<String,StringBuilder> implements CharSequence {
        // Constructor
        private Str_Bldr(@NotNull Object o, int n) { super(o, String.class, StringBuilder.class, n); }

        // Static factory methods
        static Str_Bldr of(@NotNull String o) { return new Str_Bldr(o, 0); }
        static Str_Bldr of(@NotNull StringBuilder o) { return new Str_Bldr(o, 1); }

        /** Access `super.item` and cast it to the common interface to implement these methods */
        @Override
        public int length() { return ((CharSequence) item).length(); }

        @Override
        public char charAt(int index) { return ((CharSequence) item).charAt(index); }

        @NotNull
        @Override
        public CharSequence subSequence(int start, int end) { return ((CharSequence) item).subSequence(start, end); }
    }

    @Test
    public void testImplementsCommonSuperclass() {
        Str_Bldr sOrBs = Str_Bldr.of("hello");
        assertEquals(5, sOrBs.length());
        assertEquals('e', sOrBs.charAt(1));
        assertEquals("el", sOrBs.subSequence(1, 3));
        assertTrue((Boolean) sOrBs.match(s -> true,
                sB -> false));

        Str_Bldr sOrBb = Str_Bldr.of(new StringBuilder("goodbye"));
        assertEquals(7, sOrBb.length());
        assertEquals('y', sOrBb.charAt(5));
        assertEquals("db", sOrBb.subSequence(3, 5));
        assertTrue((Boolean) sOrBb.match(s -> false,
                sB -> true));
    }

    // Cut-pasted and edited from OneOf3Test
    @Test
    public void subclassExceptions() {
        @NotNull Exception ex;
        ex = assertThrowsExactly(IllegalArgumentException.class, () ->
                new Str_Int(1, 0));
        assertEquals("Index 0 takes a java.lang.String, but was passed a java.lang.Integer", ex.getMessage());

        ex = assertThrowsExactly(IllegalArgumentException.class, () ->
                new Str_Int(2f, 0));
        assertEquals("Index 0 takes a java.lang.String, but was passed a java.lang.Float", ex.getMessage());

        ex = assertThrowsExactly(IllegalArgumentException.class, () ->
                new Str_Int(3.0, 0));
        assertEquals("Index 0 takes a java.lang.String, but was passed a java.lang.Double", ex.getMessage());

        ex = assertThrowsExactly(IllegalArgumentException.class, () ->
                new Str_Int('x', 0));
        assertEquals("Index 0 takes a java.lang.String, but was passed a java.lang.Character", ex.getMessage());

        ex = assertThrowsExactly(IllegalArgumentException.class, () ->
                new Str_Int("hi", 1));
        assertEquals("Index 1 takes a java.lang.Integer, but was passed a java.lang.String", ex.getMessage());

        ex = assertThrowsExactly(IllegalArgumentException.class, () ->
                new Str_Int(2f, 1));
        assertEquals("Index 1 takes a java.lang.Integer, but was passed a java.lang.Float", ex.getMessage());

        ex = assertThrowsExactly(IllegalArgumentException.class, () ->
                new Str_Int(3.0, 1));
        assertEquals("Index 1 takes a java.lang.Integer, but was passed a java.lang.Double", ex.getMessage());

        ex = assertThrowsExactly(IllegalArgumentException.class, () ->
                new Str_Int('x', 1));
        assertEquals("Index 1 takes a java.lang.Integer, but was passed a java.lang.Character", ex.getMessage());

        ex = assertThrowsExactly(IllegalArgumentException.class, () ->
                new Str_Int("hi", -1));
        assertEquals("Selected item index must be 0-1", ex.getMessage());

        ex = assertThrowsExactly(IllegalArgumentException.class, () ->
                new Str_Int(1, 2));
        assertEquals("Selected item index must be 0-1", ex.getMessage());

        ex = assertThrowsExactly(IllegalArgumentException.class, () ->
                new Str_Int(2f, -1));
        assertEquals("Selected item index must be 0-1", ex.getMessage());

        ex = assertThrowsExactly(IllegalArgumentException.class, () ->
                new Str_Int(3.0, 2));
        assertEquals("Selected item index must be 0-1", ex.getMessage());

        ex = assertThrowsExactly(IllegalArgumentException.class, () ->
                new Str_Int('x', -1));
        assertEquals("Selected item index must be 0-1", ex.getMessage());
    }
}