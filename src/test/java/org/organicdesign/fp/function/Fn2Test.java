package org.organicdesign.fp.function;


import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

public class Fn2Test {
    @Test
    public void applyIOException() {
        @NotNull Exception ex;
        ex = assertThrowsExactly(RuntimeException.class, () ->
                new Fn2<Integer,Integer,Integer>() {
                    @Override public Integer applyEx(Integer a, Integer b) throws Exception {
                        if (a < b) {
                            throw new IOException("test exception");
                        }
                        return a;
                    }
                }.apply(1, 2));
        assertEquals("java.io.IOException: test exception", ex.getMessage());

        assertThrowsExactly(IllegalStateException.class, () ->
                new Fn2<Integer,Integer,Integer>() {
                    @Override public Integer applyEx(Integer a, Integer b) throws Exception {
                        if (a < b) {
                            throw new IllegalStateException("test exception");
                        }
                        return a;
                    }
                }.apply(3, 4));
    }

    @Test public void memoize() {
        AtomicInteger counter = new AtomicInteger(0);
        Fn2<Integer,Double,String> f = (l, d) -> {
            counter.getAndIncrement();
            return String.valueOf(l) + "~" + String.valueOf(d);
        };
        Fn2<Integer,Double,String> g = Fn2.memoize(f);
        assertEquals("3~2.5", g.apply(3, 2.5));
        assertEquals(1, counter.get());
        assertEquals("3~2.5", g.apply(3, 2.5));
        assertEquals(1, counter.get());

        assertEquals("3~2.5", f.apply(3, 2.5));
        assertEquals(2, counter.get());

        assertEquals("3~2.5", g.apply(3, 2.5));
        assertEquals(2, counter.get());

        assertEquals("5~4.3", g.apply(5, 4.3));
        assertEquals(3, counter.get());
        assertEquals("3~2.5", g.apply(3, 2.5));
        assertEquals(3, counter.get());
        assertEquals("5~4.3", g.apply(5, 4.3));
        assertEquals(3, counter.get());
        assertEquals("3~2.5", g.apply(3, 2.5));
        assertEquals(3, counter.get());

        assertEquals(3.5, Fn2.first().apply(3.5, 4.0));
        assertEquals(4.0, Fn2.second().apply(3.5, 4.0));
    }
}
