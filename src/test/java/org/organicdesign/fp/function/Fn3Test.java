package org.organicdesign.fp.function;

import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;

public class Fn3Test {
    @Test(expected = RuntimeException.class)
    public void applyIOException() {
        new Fn3<Integer,Integer,Integer,Integer>() {
            @Override public Integer invokeEx(Integer a, Integer b, Integer c) throws Exception {
                if (a < b) {
                    throw new IOException("test exception");
                }
                return a;
            }
        }.invoke(1, 2, 3);
    }

    @Test(expected = IllegalStateException.class)
    public void applyIllegalStateException() {
        new Fn3<Integer,Integer,Integer,Integer>() {
            @Override public Integer invokeEx(Integer a, Integer b, Integer c) throws Exception {
                if (a < b) {
                    throw new IllegalStateException("test exception");
                }
                return a;
            }
        }.invoke(3, 4, 5);
    }

    @Test public void memoize() {
        AtomicInteger counter = new AtomicInteger(0);
        Fn3<Boolean,Integer,Double,String> f = (b, l, d) -> {
            counter.getAndIncrement();
            return (b ? "+" : "-") + String.valueOf(l) + "~" + String.valueOf(d);
        };
        Fn3<Boolean,Integer,Double,String> g = Fn3.Companion.memoize(f);
        assertEquals("+3~2.5", g.invoke(true, 3, 2.5));
        assertEquals(1, counter.get());
        assertEquals("+3~2.5", g.invoke(true, 3, 2.5));
        assertEquals(1, counter.get());

        assertEquals("+3~2.5", f.invoke(true, 3, 2.5));
        assertEquals(2, counter.get());

        assertEquals("+3~2.5", g.invoke(true, 3, 2.5));
        assertEquals(2, counter.get());

        assertEquals("-5~4.3", g.invoke(false, 5, 4.3));
        assertEquals(3, counter.get());
        assertEquals("+3~2.5", g.invoke(true, 3, 2.5));
        assertEquals(3, counter.get());
        assertEquals("-5~4.3", g.invoke(false, 5, 4.3));
        assertEquals(3, counter.get());
        assertEquals("+3~2.5", g.invoke(true, 3, 2.5));
        assertEquals(3, counter.get());
    }

}
