package org.organicdesign.fp.oneOf;

import org.junit.Test;
import org.organicdesign.testUtils.EqualsContract;

import static org.junit.Assert.assertEquals;
import static org.organicdesign.fp.oneOf.Option.none;
import static org.organicdesign.testUtils.Serialization.serializeDeserialize;

public class OneOf2OrNoneTest {

    static class Str_Int_None extends OneOf2OrNone<String,Integer> {

        // Constructor
        private Str_Int_None(Object o, int n) { super(o, String.class, Integer.class, n); }

        // Static factory methods
        static Str_Int_None ofStr(String o) { return new Str_Int_None(o, 0); }
        static Str_Int_None ofInt(Integer o) { return new Str_Int_None(o, 1); }
        static Str_Int_None ofNone() { return new Str_Int_None(null, 2); }
    }

    @Test public void testBasics() {
        Str_Int_None sin = Str_Int_None.ofInt(57);
        assertEquals(Integer.valueOf(57), sin.match(x -> -99,
                                                    y -> y,
                                                    () -> -99));
        assertEquals("57:String|Integer|None", sin.toString());

        sin = Str_Int_None.ofStr("right");
        assertEquals("right", sin.match(x -> x,
                                        y -> "wrong",
                                        () -> "wrong"));
        assertEquals("\"right\":String|Integer|None", sin.toString());

        sin = Str_Int_None.ofNone();
        assertEquals("right", sin.match(x -> "wrong",
                                        y -> "wrong",
                                        () -> "right"));
        assertEquals("None:String|Integer|None", sin.toString());

        assertEquals(None.NONE, serializeDeserialize(none()));
    }

    @Test public void testEquality() {
        assertEquals(0, Str_Int_None.ofStr(null).hashCode());
        assertEquals(1, Str_Int_None.ofInt(null).hashCode());
        assertEquals(2, Str_Int_None.ofNone().hashCode());

        EqualsContract.equalsDistinctHashCode(Str_Int_None.ofStr("one"), Str_Int_None.ofStr("one"),
                                              Str_Int_None.ofStr("one"),
                                              Str_Int_None.ofStr("onf"));

        EqualsContract.equalsDistinctHashCode(Str_Int_None.ofInt(97), Str_Int_None.ofInt(97),
                                              Str_Int_None.ofInt(97),
                                              Str_Int_None.ofInt(-97));

        EqualsContract.equalsDistinctHashCode(Str_Int_None.ofNone(), Str_Int_None.ofNone(),
                                              Str_Int_None.ofNone(),
                                              Str_Int_None.ofInt(-97));
    }

    @Test(expected = IllegalArgumentException.class)
    public void subClassEx__n() { new Str_Int_None(null, -1); }

    @Test(expected = IllegalArgumentException.class)
    public void subClassExa_n() { new Str_Int_None("hi", -1); }

    @Test(expected = IllegalArgumentException.class)
    public void subClassEx_bn() { new Str_Int_None(1, -1); }

    @Test(expected = ClassCastException.class)
    public void subClassExab_0() { new Str_Int_None(1, 0); }

    @Test(expected = ClassCastException.class)
    public void subClassExa_1() { new Str_Int_None("hi", 1); }

    @Test(expected = IllegalArgumentException.class)
    public void subClassExa_2() { new Str_Int_None("hi", 2); }

    @Test(expected = IllegalArgumentException.class)
    public void subClassEx_b2() { new Str_Int_None(1, 2); }

    @Test(expected = IllegalArgumentException.class)
    public void subClassEx_n3() { new Str_Int_None(null, 3); }

    @Test(expected = IllegalArgumentException.class)
    public void subClassEx_b3() { new Str_Int_None(1, 3); }
}