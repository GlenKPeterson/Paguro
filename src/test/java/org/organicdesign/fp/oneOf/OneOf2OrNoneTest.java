package org.organicdesign.fp.oneOf;

import org.junit.Test;
import org.organicdesign.fp.collections.ImList;
import org.organicdesign.fp.type.RuntimeTypes;
import org.organicdesign.testUtils.EqualsContract;

import static org.junit.Assert.assertEquals;
import static org.organicdesign.fp.StaticImports.vec;

public class OneOf2OrNoneTest {

    static class Str_Int_None extends OneOf2OrNone<String,Integer> {
        // Ensure we use the one and only instance of this runtime types array to prevent duplicate array creation.
        transient static final ImList<Class> CLASS_STRING_INTEGER_NONE =
                RuntimeTypes.registerClasses(vec(String.class, Integer.class, Option.None.class));

        // Constructor
        private Str_Int_None(String s, Integer i, int n) { super(CLASS_STRING_INTEGER_NONE, s, i, n); }

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

        sin = Str_Int_None.ofStr("right");
        assertEquals("right", sin.match(x -> x,
                                        y -> "wrong",
                                        () -> "wrong"));

        sin = Str_Int_None.ofNone();
        assertEquals("right", sin.match(x -> "wrong",
                                        y -> "wrong",
                                        () -> "right"));
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
    public void testEx01() {
        new OneOf2OrNone(OneOf2Test.String_Integer.CLASS_STRING_INTEGER, null, null, 3) {  };
    }

    @Test(expected = IllegalArgumentException.class)
    @SuppressWarnings("unchecked")
    public void testEx02() {
        new OneOf2OrNone(OneOf2Test.String_Integer.CLASS_STRING_INTEGER.append(String.class), null, null, 3) {  };
    }

    @Test(expected = IllegalArgumentException.class)
    @SuppressWarnings("unchecked")
    public void testEx03() {
        new OneOf2OrNone(Str_Int_None.CLASS_STRING_INTEGER_NONE, null, null, 4) {  };
    }

}