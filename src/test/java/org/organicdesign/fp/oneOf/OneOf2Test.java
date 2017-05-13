package org.organicdesign.fp.oneOf;

import org.junit.Test;
import org.organicdesign.fp.collections.ImList;
import org.organicdesign.fp.type.RuntimeTypes;
import org.organicdesign.testUtils.EqualsContract;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.organicdesign.fp.StaticImports.vec;

public class OneOf2Test {

    static class String_Integer extends OneOf2<String,Integer> {
        // Ensure we use the one and only instance of this runtime types array to prevent duplicate array creation.
        transient private static final ImList<Class> CLASS_STRING_INTEGER =
                RuntimeTypes.registerClasses(vec(String.class, Integer.class));

        // Constructor
        private String_Integer(String s, Integer i, int n) { super(CLASS_STRING_INTEGER, s, i, n); }

        // Static factory methods
        public static String_Integer ofStr(String s) { return new String_Integer(s, null, 1); }
        public static String_Integer ofInt(Integer i) { return new String_Integer(null, i, 2); }

        // Object methods
        public String str() {
            return super.match(s -> s,
                               super::throw2);
        }
        public Integer integer() {
            return super.match(super::throw1,
                               i -> i);
        }
    }

    @Test public void testBasics() {
        String_Integer oota = String_Integer.ofInt(57);
        assertEquals(Integer.valueOf(57), oota.match(x -> -99,
                                                     y -> y));
        assertEquals(Integer.valueOf(57), oota.integer());

        String_Integer ootb = String_Integer.ofStr("right");
        assertEquals("right", ootb.match(x -> x,
                                         y -> "wrong"));
        assertEquals("right", ootb.str());
    }

    @Test(expected = IllegalStateException.class)
    public void testEx1() { String_Integer.ofInt(57).throw1("hi"); }

    @Test(expected = IllegalStateException.class)
    public void testEx2() { String_Integer.ofStr("good").throw2(23); }

    @Test public void testEquality() {
        assertEquals(1, String_Integer.ofStr(null).hashCode());
        assertEquals(2, String_Integer.ofInt(null).hashCode());

        assertFalse(String_Integer.ofInt(41).equals(String_Integer.ofStr("A")));
        assertFalse(String_Integer.ofStr("A").equals(String_Integer.ofInt(41)));

        assertFalse(String_Integer.ofInt(65).equals(String_Integer.ofStr("A")));
        assertFalse(String_Integer.ofStr("A").equals(String_Integer.ofInt(65)));

        assertTrue(String_Integer.ofInt(37).equals(String_Integer.ofInt(37)));

        EqualsContract.equalsDistinctHashCode(String_Integer.ofStr("one"), String_Integer.ofStr("one"),
                                              String_Integer.ofStr("one"),
                                              String_Integer.ofStr("onf"));

        EqualsContract.equalsDistinctHashCode(String_Integer.ofInt(97), String_Integer.ofInt(97),
                                              String_Integer.ofInt(97),
                                              String_Integer.ofInt(-97));
    }

    @Test(expected = IllegalArgumentException.class)
    public void subClassEx1() { new String_Integer(null, null, 3); }

    @Test(expected = IllegalArgumentException.class)
    public void subClassEx2() { new String_Integer(null, null, 0); }

    @Test(expected = IllegalArgumentException.class)
    public void subClassEx3() { new String_Integer(null, null, -99); }

    @Test(expected = IllegalArgumentException.class)
    public void subClassEx4() { new String_Integer(null, null, 537); }

    @Test(expected = IllegalArgumentException.class)
    public void subClassEx5() { new String_Integer(null, null, Integer.MAX_VALUE); }

    @Test(expected = IllegalArgumentException.class)
    public void subClassEx6() { new String_Integer(null, null, Integer.MIN_VALUE); }

    @Test(expected = IllegalArgumentException.class)
    public void subEx7() {

        class BooBoo extends OneOf2<String, Integer> {
            private BooBoo(String s, Integer i, int n) {
                super(vec(String.class), s, i, n);
            }
        }
        // Blows up because of wrong number of types in the type array.
        new BooBoo("hi", 2, 1);
    }

}