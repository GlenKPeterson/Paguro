package org.organicdesign.fp.function;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class Function0Test {
    @Test(expected = RuntimeException.class)
    public void applyIOException() {
        new Function0<Integer>() {
            @Override public Integer applyEx() throws Exception {
                throw new IOException("test exception");
            }
        }.apply();
    }

    @Test(expected = IllegalStateException.class)
    public void applyIllegalStateException() {
        new Function0<Integer>() {
            @Override public Integer applyEx() throws Exception {
                throw new IllegalStateException("test exception");
            }
        }.apply();
    }

    @Test public void constantFunction() {
        Function0<Integer> f = Function0.constantFunction(7);
        assertEquals(Integer.valueOf(7), f.apply());
        assertEquals(Integer.valueOf(7), f.get());
        assertEquals(f.hashCode(), Function0.constantFunction(Integer.valueOf(7)).hashCode());
        assertTrue(f.equals(Function0.constantFunction(Integer.valueOf(7))));
    }
}
