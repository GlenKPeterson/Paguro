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
import static org.organicdesign.fp.StaticImportsKt.vec;
import static org.organicdesign.fp.function.Fn1.Companion.ConstObjObj.IDENTITY;
import static org.organicdesign.fp.function.Fn1.Companion.ConstObjBool.ACCEPT;
import static org.organicdesign.fp.function.Fn1.Companion.ConstObjBool.REJECT;

@RunWith(JUnit4.class)
public class Fn1Test {
    @Test(expected = RuntimeException.class)
    public void applyIOException() {
        new Fn1<Integer,Integer>() {
            @Override public Integer invokeEx(Integer o) throws Exception {
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
            @Override public Integer invokeEx(Integer o) throws Exception {
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
        assertEquals(ACCEPT, Fn1.Companion.and(null));
        assertEquals(ACCEPT, Fn1.Companion.and(vec()));
        assertEquals(ACCEPT, Fn1.Companion.and(Collections.emptyList()));

        assertEquals(REJECT,
                     Fn1.Companion.and(vec(Fn1.Companion.reject(),
                                 NOT_PROCESSED)));

        assertEquals(REJECT,
                     Fn1.Companion.and(vec(null, null, null, Fn1.Companion.reject(),
                                 NOT_PROCESSED)));

        assertEquals(REJECT,
                     Fn1.Companion.and(Arrays.asList(null, null, null, Fn1.Companion.reject(),
                                                     NOT_PROCESSED)));

        assertEquals(REJECT,
                     Fn1.Companion.and(vec(Fn1.Companion.accept(),
                                           Fn1.Companion.accept(),
                                           Fn1.Companion.accept(),
                                           Fn1.Companion.reject(),
                                           NOT_PROCESSED)));

        assertEquals(REJECT,
                     Fn1.Companion.and(vec(Fn1.Companion.reject(),
                                 Fn1.Companion.accept(),
                                 Fn1.Companion.accept(),
                                 Fn1.Companion.accept())));

        assertEquals(ACCEPT,
                     Fn1.Companion.and(vec(Fn1.Companion.accept())));
    }

    @Test public void composePredicatesWithOr() {
        assertEquals(REJECT, Fn1.Companion.or(null));

        assertEquals(ACCEPT,
                     Fn1.Companion.or(vec(Fn1.Companion.accept(),
                                NOT_PROCESSED)));

        assertEquals(ACCEPT,
                     Fn1.Companion.or(vec(null, null, null, Fn1.Companion.accept(),
                                NOT_PROCESSED)));

        assertEquals(ACCEPT,
                     Fn1.Companion.or(Arrays.asList(null, null, null, Fn1.Companion.accept(),
                                          NOT_PROCESSED)));

        assertEquals(ACCEPT,
                     Fn1.Companion.or(vec(Fn1.Companion.reject(),
                                Fn1.Companion.reject(),
                                Fn1.Companion.reject(),
                                Fn1.Companion.accept(),
                                NOT_PROCESSED)));

        assertEquals(ACCEPT,
                     Fn1.Companion.or(vec(Fn1.Companion.accept(),
                                Fn1.Companion.reject(),
                                Fn1.Companion.reject(),
                                Fn1.Companion.reject())));

        assertEquals(REJECT,
                     Fn1.Companion.or(vec(Fn1.Companion.reject())));
    }

    @Test public void compose() {
        assertEquals(IDENTITY,
                     Fn1.Companion.compose((Iterable<Fn1<String,String>>) null));

        assertEquals(IDENTITY, Fn1.Companion.compose(vec(null, null, null)));

        assertEquals(IDENTITY, Fn1.Companion.compose(vec(null, Fn1.Companion.identity(), null)));

        assertEquals(ACCEPT, Fn1.Companion.compose(vec(null, Fn1.Companion.identity(), null,
                                             Fn1.Companion.accept())));

        Fn1<Integer,String> intToStr = new Fn1<Integer, String>() {
            @Override
            public String invokeEx(Integer i) throws Exception {
                return (i == 0) ? "zero" :
                       (i == 1) ? "one" :
                       (i == 2) ? "two" : "unknown";
            }
        };
        Fn1<String,String> wordToOrdinal = new Fn1<String, String>() {
            @Override
            public String invokeEx(String s) throws Exception {
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

        Fn1<Integer,String> g = intToStr.compose(Fn1.Companion.identity());
        assertEquals("unknown", g.apply(-1));
        assertEquals("zero", g.apply(0));
        assertEquals("one", g.apply(1));
        assertEquals("two", g.apply(2));
        assertEquals("unknown", g.apply(3));

        Fn1<Integer,String> h = Fn1.Companion.<String>identity().compose(intToStr);
        assertEquals("unknown", h.apply(-1));
        assertEquals("zero", h.apply(0));
        assertEquals("one", h.apply(1));
        assertEquals("two", h.apply(2));
        assertEquals("unknown", h.apply(3));

        Fn1<String,String> i = Fn1.Companion.compose(vec((Fn1<String, String>) s -> s.substring(0, s.indexOf(" hundred")),
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

        assertEquals(ACCEPT, Fn1.Companion.or(Fn1.Companion.accept(), (Integer i) -> i < 6));
        assertEquals(ACCEPT, Fn1.Companion.or((Integer i) -> i < 6, Fn1.Companion.accept()));

        assertArrayEquals(oneToNineArray,
                          oneToNine.allowWhere(Fn1.Companion.or(i -> i < 3,
                                                  Fn1.Companion.accept()))
                                   .toMutList()
                                   .toArray());

        assertArrayEquals(oneToNineArray,
                          oneToNine.allowWhere(Fn1.Companion.or(Fn1.Companion.accept(),
                                                        i -> i > 5))
                                   .toMutList()
                                   .toArray());


        assertArrayEquals(new Integer[]{6, 7, 8, 9},
                          oneToNine.allowWhere(Fn1.Companion.or(Fn1.Companion.reject(),
                                                        i -> i > 5))
                                   .toMutList()
                                   .toArray());

        assertArrayEquals(new Integer[]{1, 2},
                          oneToNine.allowWhere(Fn1.Companion.or(i -> i < 3,
                                                  Fn1.Companion.reject()))
                                   .toMutList()
                                   .toArray());

        assertArrayEquals(new Integer[]{1, 2, 6, 7, 8, 9},
                          oneToNine.allowWhere(Fn1.Companion.or(i -> i < 3,
                                                        i -> i > 5))
                                   .toMutList()
                                   .toArray());

        assertArrayEquals(new Integer[]{1, 2, 4, 6, 7, 8, 9},
                          oneToNine.allowWhere(Fn1.Companion.or(vec((Fn1<Integer,Boolean>) i -> i < 3,
                                                      (Fn1<Integer,Boolean>) i -> i == 4,
                                                      (Fn1<Integer,Boolean>) i -> i > 5)))
                                   .toMutList()
                                   .toArray());

        // and(a, b)
        assertEquals(REJECT, Fn1.Companion.and(Fn1.Companion.reject(), (Integer i) -> i < 6));
        assertEquals(REJECT, Fn1.Companion.and((Integer i) -> i < 6, Fn1.Companion.reject()));

        assertArrayEquals(new Integer[]{},
                          oneToNine.allowWhere(Fn1.Companion.and((i) -> i > 2,
                                                   Fn1.Companion.reject()))
                                   .toMutList()
                                   .toArray());

        assertArrayEquals(new Integer[]{},
                          oneToNine.allowWhere(Fn1.Companion.and(Fn1.Companion.reject(),
                                                   (i) -> i > 2))
                                   .toMutList()
                                   .toArray());

        assertArrayEquals(new Integer[]{3, 4, 5, 6, 7, 8, 9},
                          oneToNine.allowWhere(Fn1.Companion.and((i) -> i > 2,
                                                   Fn1.Companion.accept()))
                                   .toMutList()
                                   .toArray());

        assertArrayEquals(new Integer[]{1, 2, 3, 4, 5},
                          oneToNine.allowWhere(Fn1.Companion.and(Fn1.Companion.accept(),
                                                   (i) -> i < 6))
                                   .toMutList()
                                   .toArray());

        assertArrayEquals(new Integer[]{3, 4, 5},
                          oneToNine.allowWhere(Fn1.Companion.and((i) -> i > 2,
                                                   (i) -> i < 6))
                                   .toMutList()
                                   .toArray());

        assertArrayEquals(new Integer[]{4, 5},
                          oneToNine.allowWhere(Fn1.Companion.and(vec((Fn1<Integer,Boolean>) i -> i > 2,
                                                       (Fn1<Integer,Boolean>) i -> i > 3,
                                                       (Fn1<Integer,Boolean>) i -> i < 6)))
                                   .toMutList()
                                   .toArray());

        assertEquals(REJECT, Fn1.Companion.negate(Fn1.Companion.accept()));
        assertEquals(ACCEPT, Fn1.Companion.negate(Fn1.Companion.reject()));

        assertArrayEquals(new Integer[]{1, 2},
                          oneToNine.allowWhere(Fn1.Companion.negate(i -> i > 2))
                                   .toMutList()
                                   .toArray());
    }

    @Test public void testMemoize() {
        final int MAX_INT = 1000;
        AtomicInteger counter = new AtomicInteger(0);
        Fn1<Integer,String> f = Fn1.Companion.memoize(i -> {
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
