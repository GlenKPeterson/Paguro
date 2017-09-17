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
import static org.organicdesign.fp.function.Fn1.ConstObjObj.IDENTITY;
import static org.organicdesign.fp.function.Fn1.ConstObjBool.ACCEPT;
import static org.organicdesign.fp.function.Fn1.ConstObjBool.REJECT;

@RunWith(JUnit4.class)
public class Fn1Test {
    @Test(expected = RuntimeException.class)
    public void applyIOException() {
        new Fn1<Integer,Integer>() {
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
        new Fn1<Integer,Integer>() {
            @Override public Integer applyEx(Integer o) throws Exception {
                if (o < 10) {
                    throw new IllegalStateException("test exception");
                }
                return o;
            }
        }.apply(3);
    }

    static Fn1<Object,Boolean> NOT_PROCESSED = i -> {
        throw new IllegalStateException("Didn't short-circuit");
    };

    @Test public void composePredicatesWithAnd() {
        assertEquals(ACCEPT, Fn1.and(null));
        assertEquals(ACCEPT, Fn1.and(vec()));
        assertEquals(ACCEPT, Fn1.and(Collections.emptyList()));

        assertEquals(REJECT,
                     Fn1.and(vec(Fn1.reject(),
                                 NOT_PROCESSED)));

        assertEquals(REJECT,
                     Fn1.and(vec(null, null, null, Fn1.reject(),
                                 NOT_PROCESSED)));

        assertEquals(REJECT,
                     Fn1.and(Arrays.asList(null, null, null, Fn1.reject(),
                                           NOT_PROCESSED)));

        assertEquals(REJECT,
                     Fn1.and(vec(Fn1.accept(),
                                 Fn1.accept(),
                                 Fn1.accept(),
                                 Fn1.reject(),
                                 NOT_PROCESSED)));

        assertEquals(REJECT,
                     Fn1.and(vec(Fn1.reject(),
                                 Fn1.accept(),
                                 Fn1.accept(),
                                 Fn1.accept())));

        assertEquals(ACCEPT,
                     Fn1.and(vec(Fn1.accept())));
    }

    @Test public void composePredicatesWithOr() {
        assertEquals(REJECT, Fn1.or(null));

        assertEquals(ACCEPT,
                     Fn1.or(vec(Fn1.accept(),
                                NOT_PROCESSED)));

        assertEquals(ACCEPT,
                     Fn1.or(vec(null, null, null, Fn1.accept(),
                                NOT_PROCESSED)));

        assertEquals(ACCEPT,
                     Fn1.or(Arrays.asList(null, null, null, Fn1.accept(),
                                          NOT_PROCESSED)));

        assertEquals(ACCEPT,
                     Fn1.or(vec(Fn1.reject(),
                                Fn1.reject(),
                                Fn1.reject(),
                                Fn1.accept(),
                                NOT_PROCESSED)));

        assertEquals(ACCEPT,
                     Fn1.or(vec(Fn1.accept(),
                                Fn1.reject(),
                                Fn1.reject(),
                                Fn1.reject())));

        assertEquals(REJECT,
                     Fn1.or(vec(Fn1.reject())));
    }

    @Test public void compose() {
        assertEquals(IDENTITY,
                     Fn1.compose((Iterable<Fn1<String,String>>) null));

        assertEquals(IDENTITY, Fn1.compose(vec(null, null, null)));

        assertEquals(IDENTITY, Fn1.compose(vec(null, Fn1.identity(), null)));

        assertEquals(ACCEPT, Fn1.compose(vec(null, Fn1.identity(), null,
                                             Fn1.accept())));

        Fn1<Integer,String> intToStr = new Fn1<Integer, String>() {
            @Override
            public String applyEx(Integer i) throws Exception {
                return (i == 0) ? "zero" :
                       (i == 1) ? "one" :
                       (i == 2) ? "two" : "unknown";
            }
        };
        Fn1<String,String> wordToOrdinal = new Fn1<String, String>() {
            @Override
            public String applyEx(String s) throws Exception {
                return ("one".equals(s)) ? "first" :
                       ("two".equals(s)) ? "second" : s;
            }
        };
        Fn1<Integer,String> f = wordToOrdinal.compose(intToStr);
        assertEquals("unknown", f.apply(-1));
        assertEquals("zero", f.apply(0));
        assertEquals("first", f.apply(1));
        assertEquals("second", f.apply(2));
        assertEquals("unknown", f.apply(3));

        Fn1<Integer,String> g = intToStr.compose(Fn1.identity());
        assertEquals("unknown", g.apply(-1));
        assertEquals("zero", g.apply(0));
        assertEquals("one", g.apply(1));
        assertEquals("two", g.apply(2));
        assertEquals("unknown", g.apply(3));

        Fn1<Integer,String> h = Fn1.<String>identity().compose(intToStr);
        assertEquals("unknown", h.apply(-1));
        assertEquals("zero", h.apply(0));
        assertEquals("one", h.apply(1));
        assertEquals("two", h.apply(2));
        assertEquals("unknown", h.apply(3));

        Fn1<String,String> i = Fn1.compose(vec(s -> s.substring(0, s.indexOf(" hundred")),
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

        assertEquals(ACCEPT, Fn1.or(Fn1.accept(), (Integer i) -> i < 6));
        assertEquals(ACCEPT, Fn1.or((Integer i) -> i < 6, Fn1.accept()));

        assertArrayEquals(oneToNineArray,
                          oneToNine.filter(Fn1.or(i -> i < 3,
                                                  Fn1.accept()))
                                   .toMutList()
                                   .toArray());

        assertArrayEquals(oneToNineArray,
                          oneToNine.filter(Fn1.or(Fn1.accept(),
                                                        i -> i > 5))
                                   .toMutList()
                                   .toArray());


        assertArrayEquals(new Integer[]{6, 7, 8, 9},
                          oneToNine.filter(Fn1.or(Fn1.reject(),
                                                        i -> i > 5))
                                   .toMutList()
                                   .toArray());

        assertArrayEquals(new Integer[]{1, 2},
                          oneToNine.filter(Fn1.or(i -> i < 3,
                                                  Fn1.reject()))
                                   .toMutList()
                                   .toArray());

        assertArrayEquals(new Integer[]{1, 2, 6, 7, 8, 9},
                          oneToNine.filter(Fn1.or(i -> i < 3,
                                                        i -> i > 5))
                                   .toMutList()
                                   .toArray());

        assertArrayEquals(new Integer[]{1, 2, 4, 6, 7, 8, 9},
                          oneToNine.filter(Fn1.or(vec(i -> i < 3,
                                                            i -> i == 4,
                                                            i -> i > 5)))
                                   .toMutList()
                                   .toArray());

        // and(a, b)
        assertEquals(REJECT, Fn1.and(Fn1.reject(), (Integer i) -> i < 6));
        assertEquals(REJECT, Fn1.and((Integer i) -> i < 6, Fn1.reject()));

        assertArrayEquals(new Integer[]{},
                          oneToNine.filter(Fn1.and((i) -> i > 2,
                                                   Fn1.reject()))
                                   .toMutList()
                                   .toArray());

        assertArrayEquals(new Integer[]{},
                          oneToNine.filter(Fn1.and(Fn1.reject(),
                                                   (i) -> i > 2))
                                   .toMutList()
                                   .toArray());

        assertArrayEquals(new Integer[]{3, 4, 5, 6, 7, 8, 9},
                          oneToNine.filter(Fn1.and((i) -> i > 2,
                                                   Fn1.accept()))
                                   .toMutList()
                                   .toArray());

        assertArrayEquals(new Integer[]{1, 2, 3, 4, 5},
                          oneToNine.filter(Fn1.and(Fn1.accept(),
                                                   (i) -> i < 6))
                                   .toMutList()
                                   .toArray());

        assertArrayEquals(new Integer[]{3, 4, 5},
                          oneToNine.filter(Fn1.and((i) -> i > 2,
                                                   (i) -> i < 6))
                                   .toMutList()
                                   .toArray());

        assertArrayEquals(new Integer[]{4, 5},
                          oneToNine.filter(Fn1.and(vec(i -> i > 2,
                                                             i -> i > 3,
                                                             i -> i < 6)))
                                   .toMutList()
                                   .toArray());

        assertEquals(REJECT, Fn1.negate(Fn1.accept()));
        assertEquals(ACCEPT, Fn1.negate(Fn1.reject()));

        assertArrayEquals(new Integer[]{1, 2},
                          oneToNine.filter(Fn1.negate(i -> i > 2))
                                   .toMutList()
                                   .toArray());
    }

    @Test public void testMemoize() {
        final int MAX_INT = 1000;
        AtomicInteger counter = new AtomicInteger(0);
        Fn1<Integer,String> f = Fn1.memoize(i -> {
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
