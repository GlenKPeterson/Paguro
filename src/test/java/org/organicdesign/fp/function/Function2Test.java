package org.organicdesign.fp.function;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.organicdesign.fp.Mutable;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class Function2Test {
    @Test(expected = RuntimeException.class)
    public void applyIOException() {
        new Function2<Integer,Integer,Integer>() {
            @Override public Integer applyEx(Integer a, Integer b) throws Exception {
                if (a < b) {
                    throw new IOException("test exception");
                }
                return a;
            }
        }.apply(1, 2);
    }

    @Test(expected = IllegalStateException.class)
    public void applyIllegalStateException() {
        new Function2<Integer,Integer,Integer>() {
            @Override public Integer applyEx(Integer a, Integer b) throws Exception {
                if (a < b) {
                    throw new IllegalStateException("test exception");
                }
                return a;
            }
        }.apply(3, 4);
    }

    @Test public void memoize() {
        Mutable.IntRef counter = Mutable.IntRef.of(0);
        Function2<Integer,Double,String> f = (l, d) -> {
            counter.increment();
            return String.valueOf(l) + "~" + String.valueOf(d);
        };
        Function2<Integer,Double,String> g = Function2.memoize(f);
        assertEquals("3~2.5", g.apply(3, 2.5));
        assertEquals(1, counter.value());
        assertEquals("3~2.5", g.apply(3, 2.5));
        assertEquals(1, counter.value());

        assertEquals("3~2.5", f.apply(3, 2.5));
        assertEquals(2, counter.value());

        assertEquals("3~2.5", g.apply(3, 2.5));
        assertEquals(2, counter.value());

        assertEquals("5~4.3", g.apply(5, 4.3));
        assertEquals(3, counter.value());
        assertEquals("3~2.5", g.apply(3, 2.5));
        assertEquals(3, counter.value());
        assertEquals("5~4.3", g.apply(5, 4.3));
        assertEquals(3, counter.value());
        assertEquals("3~2.5", g.apply(3, 2.5));
        assertEquals(3, counter.value());
    }
}
