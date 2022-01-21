package org.organicdesign.fp.oneOf;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.organicdesign.testUtils.EqualsContract;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

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
        assertTrue(sOrBs.match(s -> true,
                               sB -> false));

        Str_Bldr sOrBb = Str_Bldr.of(new StringBuilder("goodbye"));
        assertEquals(7, sOrBb.length());
        assertEquals('y', sOrBb.charAt(5));
        assertEquals("db", sOrBb.subSequence(3, 5));
        assertTrue(sOrBb.match(s -> false,
                               sB -> true));
    }

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