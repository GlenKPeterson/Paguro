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

static class Str_Int extends OneOf2<String,Integer> {
    // Constructor
    private Str_Int(String s, Integer i, int n) { super(s, String.class, i, Integer.class, n); }

    // Static factory methods
    public static Str_Int ofStr(String s) { return new Str_Int(s, null, 0); }
    public static Str_Int ofInt(Integer i) { return new Str_Int(null, i, 1); }

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
        Str_Int oota = Str_Int.ofInt(57);
        assertEquals(Integer.valueOf(57), oota.match(x -> -99,
                                                     y -> y));
        assertEquals(Integer.valueOf(57), oota.integer());

        Str_Int ootb = Str_Int.ofStr("right");
        assertEquals("right", ootb.match(x -> x,
                                         y -> "wrong"));
        assertEquals("right", ootb.str());
    }

    @Test(expected = IllegalStateException.class)
    public void testEx1() { Str_Int.ofInt(57).throw1("hi"); }

    @Test(expected = IllegalStateException.class)
    public void testEx2() { Str_Int.ofStr("good").throw2(23); }

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
    public void subClassEx1() { new Str_Int(null, null, 2); }

    @Test(expected = IllegalArgumentException.class)
    public void subClassEx2() { new Str_Int(null, null, -1); }

    @Test(expected = IllegalArgumentException.class)
    public void subClassEx3() { new Str_Int(null, null, -99); }

    @Test(expected = IllegalArgumentException.class)
    public void subClassEx4() { new Str_Int(null, null, 537); }

    @Test(expected = IllegalArgumentException.class)
    public void subClassEx5() { new Str_Int(null, null, Integer.MAX_VALUE); }

    @Test(expected = IllegalArgumentException.class)
    public void subClassEx6() { new Str_Int(null, null, Integer.MIN_VALUE); }


    @Test(expected = IllegalArgumentException.class)
    public void subClassEx__n() { new Str_Int(null, null, -1); }

    @Test(expected = IllegalArgumentException.class)
    public void subClassExa_n() { new Str_Int("hi", null, -1); }

    @Test(expected = IllegalArgumentException.class)
    public void subClassEx_bn() { new Str_Int(null, 1, -1); }


    @Test(expected = IllegalArgumentException.class)
    public void subClassExab_0() { new Str_Int("hi", 1, 0); }

    @Test(expected = IllegalArgumentException.class)
    public void subClassEx_b0() { new Str_Int(null, 1, 0); }


    @Test(expected = IllegalArgumentException.class)
    public void subClassExa_1() { new Str_Int("hi", null, 1); }

    @Test(expected = IllegalArgumentException.class)
    public void subClassExab1() { new Str_Int("hi", 1, 1); }


    @Test(expected = IllegalArgumentException.class)
    public void subClassExa_2() { new Str_Int("hi", null,2); }

    @Test(expected = IllegalArgumentException.class)
    public void subClassEx_b2() { new Str_Int(null, 1, 2); }

}