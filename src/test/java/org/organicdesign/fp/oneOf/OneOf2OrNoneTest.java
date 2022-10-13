package org.organicdesign.fp.oneOf;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.organicdesign.testUtils.EqualsContract;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
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

    @Test
    public void testBasics() {
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

    @SuppressWarnings({"deprecation", "RedundantSuppression"})
    @Test
    public void testEquality() {
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

    @Test
    public void subClassExceptions() {
        Exception e;

        e = assertThrowsExactly(IllegalArgumentException.class,
                                () -> new Str_Int("hi", -1));
        assertEquals("Selected item index must be 0-1", e.getMessage());

        e = assertThrowsExactly(IllegalArgumentException.class,
                                () -> new Str_Int(1, -1));
        assertEquals("Selected item index must be 0-1", e.getMessage());

        e = assertThrowsExactly(ClassCastException.class,
                                () -> new Str_Int(1, 0));
        assertEquals("You specified index 0, indicating a java.lang.String, but passed a java.lang.Integer", e.getMessage());

        e = assertThrowsExactly(ClassCastException.class,
                                () -> new Str_Int("hi", 1));
        assertEquals("You specified index 1, indicating a java.lang.Integer, but passed a java.lang.String", e.getMessage());

        e = assertThrowsExactly(IllegalArgumentException.class,
                                () -> new Str_Int("hi", 2));
        assertEquals("Selected item index must be 0-1", e.getMessage());

        e = assertThrowsExactly(IllegalArgumentException.class,
                                () -> new Str_Int(1, 2));
        assertEquals("Selected item index must be 0-1", e.getMessage());

        e = assertThrowsExactly(IllegalArgumentException.class,
                                () -> new Str_Int(1, 3));
        assertEquals("Selected item index must be 0-1", e.getMessage());
    }
}