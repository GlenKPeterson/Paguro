package org.organicdesign.fp.function;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.organicdesign.fp.ephemeral.View;

import java.io.IOException;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertArrayEquals;

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
        assertTrue(Function1.and(View.ofArray(Function1.accept())) ==
                   Function1.accept());

        assertTrue(Function1.<Object>andArray(Function1.accept(),
                                                  Function1.accept(),
                                                  Function1.accept()) ==
                   Function1.accept());
        assertTrue(Function1.<Object>and(View.ofArray(Function1.accept(),
                                                          Function1.accept(),
                                                          Function1.accept())) ==
                   Function1.accept());

        assertTrue(Function1.andArray(Function1.reject()) == Function1.reject());
        assertTrue(Function1.and(View.ofArray(Function1.reject())) ==
                Function1.reject());
    }

    @Test
    public void composePredicatesWithOr() {
        assertTrue(Function1.orArray() == Function1.reject());
        assertTrue(Function1.or(null) == Function1.reject());

        assertTrue(Function1.orArray(Function1.accept()) == Function1.accept());
        assertTrue(Function1.or(View.ofArray(Function1.accept())) ==
                   Function1.accept());

        assertTrue(Function1.<Object>orArray(Function1.reject(),
                Function1.reject(),
                Function1.reject(),
                Function1.accept()) ==
                   Function1.accept());
        assertTrue(Function1.<Object>or(View.ofArray(Function1.reject(),
                Function1.reject(),
                Function1.reject(),
                Function1.accept())) ==
                   Function1.accept());

        assertTrue(Function1.<Object>orArray(Function1.accept(),
                Function1.reject(),
                Function1.reject(),
                Function1.reject()) ==
                   Function1.accept());
        assertTrue(Function1.<Object>or(View.ofArray(Function1.accept(),
                Function1.reject(),
                Function1.reject(),
                Function1.reject())) ==
                   Function1.accept());

        assertTrue(Function1.orArray(Function1.reject()) == Function1.reject());
        assertTrue(Function1.or(View.ofArray(Function1.reject())) ==
                Function1.reject());
    }

    @Test
    public void filtersOfPredicates() {
        assertArrayEquals(View.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9)
                        .filter(Function1.<Integer>andArray((i) -> i > 2,
                                (i) -> i < 6))
                        .toJavaArrayList()
                        .toArray(),
                new Integer[]{3, 4, 5});

        assertArrayEquals(View.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9)
                        .filter(Function1.orArray(i -> i < 3,
                                i -> i > 5))
                        .toJavaArrayList()
                        .toArray(),
                new Integer[]{1, 2, 6, 7, 8, 9});

        assertArrayEquals(View.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9)
                        .filter(Function1.orArray(i -> i < 3,
                                i -> i == 4,
                                i -> i > 5))
                        .toJavaArrayList()
                        .toArray(),
                new Integer[]{1, 2, 4, 6, 7, 8, 9});
    }
}
