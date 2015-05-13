package org.organicdesign.fp.function;

import java.io.IOException;

import org.junit.Test;
import org.organicdesign.fp.Mutable;

import static org.junit.Assert.assertEquals;

public class Function3Test {
    @Test(expected = RuntimeException.class)
    public void applyIOException() {
        new Function3<Integer,Integer,Integer,Integer>() {
            @Override public Integer applyEx(Integer a, Integer b, Integer c) throws Exception {
                if (a < b) {
                    throw new IOException("test exception");
                }
                return a;
            }
        }.apply(1, 2, 3);
    }

    @Test(expected = IllegalStateException.class)
    public void applyIllegalStateException() {
        new Function3<Integer,Integer,Integer,Integer>() {
            @Override public Integer applyEx(Integer a, Integer b, Integer c) throws Exception {
                if (a < b) {
                    throw new IllegalStateException("test exception");
                }
                return a;
            }
        }.apply(3, 4, 5);
    }

    @Test public void memoize() {
        Mutable.IntRef counter = Mutable.IntRef.of(0);
        Function3<Boolean,Integer,Double,String> f = (b, l, d) -> {
            counter.increment();
            return (b ? "+" : "-") + String.valueOf(l) + "~" + String.valueOf(d);
        };
        Function3<Boolean,Integer,Double,String> g = Function3.memoize(f);
        assertEquals("+3~2.5", g.apply(true, 3, 2.5));
        assertEquals(1, counter.value());
        assertEquals("+3~2.5", g.apply(true, 3, 2.5));
        assertEquals(1, counter.value());

        assertEquals("+3~2.5", f.apply(true, 3, 2.5));
        assertEquals(2, counter.value());

        assertEquals("+3~2.5", g.apply(true, 3, 2.5));
        assertEquals(2, counter.value());

        assertEquals("-5~4.3", g.apply(false, 5, 4.3));
        assertEquals(3, counter.value());
        assertEquals("+3~2.5", g.apply(true, 3, 2.5));
        assertEquals(3, counter.value());
        assertEquals("-5~4.3", g.apply(false, 5, 4.3));
        assertEquals(3, counter.value());
        assertEquals("+3~2.5", g.apply(true, 3, 2.5));
        assertEquals(3, counter.value());
    }

}
