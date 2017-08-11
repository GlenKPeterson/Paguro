package org.organicdesign.fp.oneOf;

import org.junit.Test;
import org.organicdesign.testUtils.EqualsContract;

import static org.junit.Assert.assertEquals;

public class OneOf2OrNoneTest {

    static class Str_Int_None extends OneOf2OrNone<String,Integer> {

        // Constructor
        private Str_Int_None(String s, Integer i, int n) { super(s, i, n); }

        private transient static final Class[] CLASS_STRING_INTEGER_NONE =
                { String.class, Integer.class, None.class };
        @Override
        protected Class classFor(int selIdx) {
            return CLASS_STRING_INTEGER_NONE[selIdx - 1];
        }

        // Static factory methods
        static Str_Int_None ofStr(String s) { return new Str_Int_None(s, null, 1); }
        static Str_Int_None ofInt(Integer i) { return new Str_Int_None(null, i, 2); }
        static Str_Int_None ofNone() { return new Str_Int_None(null, null, 3); }
    }

    @Test public void testBasics() {
        Str_Int_None sin = Str_Int_None.ofInt(57);
        assertEquals(Integer.valueOf(57), sin.match(x -> -99,
                                                    y -> y,
                                                    () -> -99));
        assertEquals("Integer(57)", sin.toString());

        sin = Str_Int_None.ofStr("right");
        assertEquals("right", sin.match(x -> x,
                                        y -> "wrong",
                                        () -> "wrong"));
        assertEquals("String(\"right\")", sin.toString());

        sin = Str_Int_None.ofNone();
        assertEquals("right", sin.match(x -> "wrong",
                                        y -> "wrong",
                                        () -> "right"));
        assertEquals("None", sin.toString());
    }

    @Test public void testEquality() {
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
    @SuppressWarnings("unchecked")
    public void testEx03() {
        new OneOf2OrNone(null, null, 4) {
            @Override
            protected Class classFor(int selIdx) {
                return Str_Int_None.CLASS_STRING_INTEGER_NONE[selIdx - 1];
            }
        };
    }
}