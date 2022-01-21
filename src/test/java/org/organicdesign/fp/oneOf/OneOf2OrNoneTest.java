package org.organicdesign.fp.oneOf;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.organicdesign.testUtils.EqualsContract;

import static org.junit.Assert.assertEquals;
import static org.organicdesign.fp.oneOf.Option.none;
import static org.organicdesign.testUtils.Serialization.serializeDeserialize;

public class OneOf2OrNoneTest {

    static class Str_Int extends OneOf2<String,Integer> {
        // Constructor
        private Str_Int(@NotNull Object o, int n) { super(o, String.class, Integer.class, n); }

        // Static factory methods
        static Option<Str_Int> of(@NotNull String o) { return Option.some(new Str_Int(o, 0)); }
        static Option<Str_Int> of(@NotNull Integer o) { return Option.some(new Str_Int(o, 1)); }
        static Option<Str_Int> ofNone() { return Option.none(); }
    }

    @Test public void testBasics() {
        Option<Str_Int> si = Str_Int.of(57);
        assertEquals(Integer.valueOf(57), si.match(some -> some.match(x -> -99,
                                                                      y -> y),
                                                   () -> -99));
        assertEquals("Some(57:String|Integer)", si.toString());

        si = Str_Int.of("right");
        assertEquals("right", si.match(some -> some.match(x -> x,
                                                          y -> "wrong"),
                                       () -> "wrong"));
        assertEquals("Some(\"right\":String|Integer)", si.toString());

        si = Str_Int.ofNone();
        assertEquals("right", si.match(some -> some.match(x -> "wrong",
                                                          y -> "wrong"),
                                       () -> "right"));
        assertEquals("None", si.toString());

        assertEquals(None.NONE, serializeDeserialize(none()));
    }

    @Test public void testEquality() {
        assertEquals(0, Str_Int.of("").hashCode());
        assertEquals(1, Str_Int.of(0).hashCode());
        assertEquals(none().hashCode(), Str_Int.ofNone().hashCode());

        EqualsContract.equalsDistinctHashCode(Str_Int.of("one"), Str_Int.of("one"),
                                              Str_Int.of("one"),
                                              Str_Int.of("onf"));

        EqualsContract.equalsDistinctHashCode(Str_Int.of(97), Str_Int.of(97),
                                              Str_Int.of(97),
                                              Str_Int.of(-97));
    }

    @Test(expected = IllegalArgumentException.class)
    public void subClassExa_n() { new Str_Int("hi", -1); }

    @Test(expected = IllegalArgumentException.class)
    public void subClassEx_bn() { new Str_Int(1, -1); }

    @Test(expected = ClassCastException.class)
    public void subClassExab_0() { new Str_Int(1, 0); }

    @Test(expected = ClassCastException.class)
    public void subClassExa_1() { new Str_Int("hi", 1); }

    @Test(expected = IllegalArgumentException.class)
    public void subClassExa_2() { new Str_Int("hi", 2); }

    @Test(expected = IllegalArgumentException.class)
    public void subClassEx_b2() { new Str_Int(1, 2); }

    @Test(expected = IllegalArgumentException.class)
    public void subClassEx_b3() { new Str_Int(1, 3); }
}