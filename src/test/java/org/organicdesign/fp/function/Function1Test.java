package org.organicdesign.fp.function;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.organicdesign.fp.collections.ImList;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.organicdesign.fp.FunctionUtils.ordinal;
import static org.organicdesign.fp.StaticImports.vec;
import static org.organicdesign.fp.function.Function1.Const.IDENTITY;
import static org.organicdesign.fp.function.Function1.ConstBool.ACCEPT;
import static org.organicdesign.fp.function.Function1.ConstBool.REJECT;

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

    static Function1<Object,Boolean> NOT_PROCESSED = i -> {
        throw new IllegalStateException("Didn't short-circuit");
    };

    @Test public void composePredicatesWithAnd() {
        assertEquals(ACCEPT, Function1.and(null));
        assertEquals(ACCEPT, Function1.and(vec()));
        assertEquals(ACCEPT, Function1.and(Collections.emptyList()));

        assertEquals(REJECT,
                     Function1.and(vec(Function1.reject(),
                                       NOT_PROCESSED)));

        assertEquals(REJECT,
                     Function1.and(vec(null, null, null, Function1.reject(),
                                       NOT_PROCESSED)));

        assertEquals(REJECT,
                     Function1.and(Arrays.asList(null, null, null, Function1.reject(),
                                                 NOT_PROCESSED)));

        assertEquals(REJECT,
                     Function1.and(vec(Function1.accept(),
                                       Function1.accept(),
                                       Function1.accept(),
                                       Function1.reject(),
                                       NOT_PROCESSED)));

        assertEquals(REJECT,
                     Function1.and(vec(Function1.reject(),
                                       Function1.accept(),
                                       Function1.accept(),
                                       Function1.accept())));

        assertEquals(ACCEPT,
                     Function1.and(vec(Function1.accept())));
    }

    @Test public void composePredicatesWithOr() {
        assertEquals(REJECT, Function1.or(null));

        assertEquals(ACCEPT,
                     Function1.or(vec(Function1.accept(),
                                      NOT_PROCESSED)));

        assertEquals(ACCEPT,
                     Function1.or(vec(null, null, null, Function1.accept(),
                                      NOT_PROCESSED)));

        assertEquals(ACCEPT,
                     Function1.or(Arrays.asList(null, null, null, Function1.accept(),
                                                NOT_PROCESSED)));

        assertEquals(ACCEPT,
                     Function1.or(vec(Function1.reject(),
                                      Function1.reject(),
                                      Function1.reject(),
                                      Function1.accept(),
                                      NOT_PROCESSED)));

        assertEquals(ACCEPT,
                     Function1.or(vec(Function1.accept(),
                                      Function1.reject(),
                                      Function1.reject(),
                                      Function1.reject())));

        assertEquals(REJECT,
                     Function1.or(vec(Function1.reject())));
    }

    @Test public void compose() {
        assertEquals(IDENTITY,
                     Function1.compose((Iterable<Function1<String,String>>) null));

        assertEquals(IDENTITY, Function1.compose(vec(null, null, null)));

        assertEquals(IDENTITY, Function1.compose(vec(null, Function1.identity(), null)));

        assertEquals(ACCEPT, Function1.compose(vec(null, Function1.identity(), null,
                                                   Function1.accept())));

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

        Function1<String,String> i = Function1.compose(vec(s -> s.substring(0, s.indexOf(" hundred")),
                                                           wordToOrdinal));
        assertEquals("zillion", i.apply("zillion hundred"));
        assertEquals("zero", i.apply("zero hundred"));
        assertEquals("first", i.apply("one hundred"));
        assertEquals("second", i.apply("two hundred"));
        assertEquals("three", i.apply("three hundred"));
    }

    @Test
    public void filtersOfPredicates() {
        Integer[] oneToNineArray = new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        ImList<Integer> oneToNine = vec(oneToNineArray);

        assertEquals(ACCEPT, Function1.or(Function1.accept(), (Integer i) -> i < 6));
        assertEquals(ACCEPT, Function1.or((Integer i) -> i < 6, Function1.accept()));

        assertArrayEquals(oneToNineArray,
                          oneToNine.filter(Function1.or(i -> i < 3,
                                                        Function1.accept()))
                                   .toMutableList()
                                   .toArray());

        assertArrayEquals(oneToNineArray,
                          oneToNine.filter(Function1.or(Function1.accept(),
                                                        i -> i > 5))
                                   .toMutableList()
                                   .toArray());


        assertArrayEquals(new Integer[]{6, 7, 8, 9},
                          oneToNine.filter(Function1.or(Function1.reject(),
                                                        i -> i > 5))
                                   .toMutableList()
                                   .toArray());

        assertArrayEquals(new Integer[]{1, 2},
                          oneToNine.filter(Function1.or(i -> i < 3,
                                                        Function1.reject()))
                                   .toMutableList()
                                   .toArray());

        assertArrayEquals(new Integer[]{1, 2, 6, 7, 8, 9},
                          oneToNine.filter(Function1.or(i -> i < 3,
                                                        i -> i > 5))
                                   .toMutableList()
                                   .toArray());

        assertArrayEquals(new Integer[]{1, 2, 4, 6, 7, 8, 9},
                          oneToNine.filter(Function1.or(vec(i -> i < 3,
                                                            i -> i == 4,
                                                            i -> i > 5)))
                                   .toMutableList()
                                   .toArray());

        // and(a, b)
        assertEquals(REJECT, Function1.and(Function1.reject(), (Integer i) -> i < 6));
        assertEquals(REJECT, Function1.and((Integer i) -> i < 6, Function1.reject()));

        assertArrayEquals(new Integer[]{},
                          oneToNine.filter(Function1.and((i) -> i > 2,
                                                         Function1.reject()))
                                   .toMutableList()
                                   .toArray());

        assertArrayEquals(new Integer[]{},
                          oneToNine.filter(Function1.and(Function1.reject(),
                                                         (i) -> i > 2))
                                   .toMutableList()
                                   .toArray());

        assertArrayEquals(new Integer[]{3, 4, 5, 6, 7, 8, 9},
                          oneToNine.filter(Function1.and((i) -> i > 2,
                                                         Function1.accept()))
                                   .toMutableList()
                                   .toArray());

        assertArrayEquals(new Integer[]{1, 2, 3, 4, 5},
                          oneToNine.filter(Function1.and(Function1.accept(),
                                                         (i) -> i < 6))
                                   .toMutableList()
                                   .toArray());

        assertArrayEquals(new Integer[]{3, 4, 5},
                          oneToNine.filter(Function1.and((i) -> i > 2,
                                                         (i) -> i < 6))
                                   .toMutableList()
                                   .toArray());

        assertArrayEquals(new Integer[]{4, 5},
                          oneToNine.filter(Function1.and(vec(i -> i > 2,
                                                             i -> i > 3,
                                                             i -> i < 6)))
                                   .toMutableList()
                                   .toArray());

        assertEquals(REJECT, Function1.negate(Function1.accept()));
        assertEquals(ACCEPT, Function1.negate(Function1.reject()));

        assertArrayEquals(new Integer[]{1, 2},
                          oneToNine.filter(Function1.negate(i -> i > 2))
                                   .toMutableList()
                                   .toArray());
    }

    @Test public void testMemoize() {
        final int MAX_INT = 1000;
        AtomicInteger counter = new AtomicInteger(0);
        Function1<Integer,String> f = Function1.memoize(i -> {
            counter.getAndIncrement();
            return ordinal(i);
        });

        assertEquals(0, counter.get());

        // Call function a bunch of times, memoizing the results.
        for (int i = 0; i < MAX_INT; i++) {
            assertEquals(ordinal(i), f.apply(i));
        }
        // Assert count of calls equals the actual number.
        assertEquals(MAX_INT, counter.get());

        // Make all those calls again.
        for (int i = 0; i < MAX_INT; i++) {
            assertEquals(ordinal(i), f.apply(i));
            // this is for compatibility with Consumer.
            f.accept(i);
        }

        // Assert that function has not actually been called again.
        assertEquals(MAX_INT, counter.get());
    }
}
