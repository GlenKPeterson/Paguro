package org.organicdesign.fp.function;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.organicdesign.fp.ephemeral.View;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class Function1Test {
    @Test(expected = RuntimeException.class)
    public void applyIOException() {
        new Function1<Integer,Integer>() {
            @Override public Integer applyEx(Integer o) throws Exception {
                if (o < 10) {
                    throw new IOException("test exception");
                }
                return o;
            }
        }.apply(3);
    }

    @Test(expected = IllegalStateException.class)
    public void applyIllegalStateException() {
        new Function1<Integer,Integer>() {
            @Override public Integer applyEx(Integer o) throws Exception {
                if (o < 10) {
                    throw new IllegalStateException("test exception");
                }
                return o;
            }
        }.apply(3);
    }

    @Test
    public void composePredicatesWithAnd() {
        assertTrue(Function1.andArray() == Function1.accept());
        assertTrue(Function1.and(null) == Function1.accept());
        assertTrue(Function1.and(View.emptyView()) == Function1.accept());

        assertTrue(Function1.andArray(Function1.accept()) == Function1.accept());
        assertTrue(Function1.and(View.of(Function1.accept())) ==
                   Function1.accept());

        assertTrue(Function1.<Object>andArray(Function1.accept(),
                                                  Function1.accept(),
                                                  Function1.accept()) ==
                   Function1.accept());
        assertTrue(Function1.<Object>and(View.of(Function1.accept(),
                                                 Function1.accept(),
                                                 Function1.accept())) ==
                   Function1.accept());

        assertTrue(Function1.andArray(Function1.reject()) == Function1.reject());
        assertTrue(Function1.and(View.of(Function1.reject())) ==
                   Function1.reject());
    }

    @Test
    public void composePredicatesWithOr() {
        assertTrue(Function1.orArray() == Function1.reject());
        assertTrue(Function1.or(null) == Function1.reject());

        assertTrue(Function1.orArray(Function1.accept()) == Function1.accept());
        assertTrue(Function1.or(View.of(Function1.accept())) ==
                   Function1.accept());

        assertTrue(Function1.<Object>orArray(Function1.reject(),
                                             Function1.reject(),
                                             Function1.reject(),
                                             Function1.accept()) ==
                   Function1.accept());
        assertTrue(Function1.<Object>or(View.of(Function1.reject(),
                                                Function1.reject(),
                                                Function1.reject(),
                                                Function1.accept())) ==
                   Function1.accept());

        assertTrue(Function1.<Object>orArray(Function1.accept(),
                                             Function1.reject(),
                                             Function1.reject(),
                                             Function1.reject()) ==
                   Function1.accept());
        assertTrue(Function1.<Object>or(View.of(Function1.accept(),
                                                Function1.reject(),
                                                Function1.reject(),
                                                Function1.reject())) ==
                   Function1.accept());

        assertTrue(Function1.orArray(Function1.reject()) == Function1.reject());
        assertTrue(Function1.or(View.of(Function1.reject())) ==
                Function1.reject());
    }

    @Test public void compose() {
        Function1<Integer,String> intToStr = new Function1<Integer, String>() {
            @Override
            public String applyEx(Integer i) throws Exception {
                return (i == 0) ? "zero" :
                       (i == 1) ? "one" :
                       (i == 2) ? "two" : "unknown";
            }
        };
        Function1<String,String> wordToOrdinal = new Function1<String, String>() {
            @Override
            public String applyEx(String s) throws Exception {
                return ("one".equals(s)) ? "first" :
                       ("two".equals(s)) ? "second" : s;
            }
        };
        Function1<Integer,String> f = wordToOrdinal.compose(intToStr);
        assertEquals("unknown", f.apply(-1));
        assertEquals("zero", f.apply(0));
        assertEquals("first", f.apply(1));
        assertEquals("second", f.apply(2));
        assertEquals("unknown", f.apply(3));

        Function1<Integer,String> g = intToStr.compose(Function1.identity());
        assertEquals("unknown", g.apply(-1));
        assertEquals("zero", g.apply(0));
        assertEquals("one", g.apply(1));
        assertEquals("two", g.apply(2));
        assertEquals("unknown", g.apply(3));

        Function1<Integer,String> h = Function1.<String>identity().compose(intToStr);
        assertEquals("unknown", h.apply(-1));
        assertEquals("zero", h.apply(0));
        assertEquals("one", h.apply(1));
        assertEquals("two", h.apply(2));
        assertEquals("unknown", h.apply(3));

        Function1<String,String> i = Function1.compose((s) -> s.substring(0, s.indexOf(" hundred")), wordToOrdinal);
        assertEquals("zillion", i.apply("zillion hundred"));
        assertEquals("zero", i.apply("zero hundred"));
        assertEquals("first", i.apply("one hundred"));
        assertEquals("second", i.apply("two hundred"));
        assertEquals("three", i.apply("three hundred"));
    }

    @Test
    public void filtersOfPredicates() {
        assertArrayEquals(View.of(1, 2, 3, 4, 5, 6, 7, 8, 9)
                        .filter(Function1.<Integer>andArray((i) -> i > 2,
                                (i) -> i < 6))
                        .toJavaList()
                        .toArray(),
                new Integer[]{3, 4, 5});

        assertArrayEquals(View.of(1, 2, 3, 4, 5, 6, 7, 8, 9)
                        .filter(Function1.orArray(i -> i < 3,
                                i -> i > 5))
                        .toJavaList()
                        .toArray(),
                new Integer[]{1, 2, 6, 7, 8, 9});

        assertArrayEquals(View.of(1, 2, 3, 4, 5, 6, 7, 8, 9)
                        .filter(Function1.orArray(i -> i < 3,
                                i -> i == 4,
                                i -> i > 5))
                        .toJavaList()
                        .toArray(),
                new Integer[]{1, 2, 4, 6, 7, 8, 9});
    }
}
