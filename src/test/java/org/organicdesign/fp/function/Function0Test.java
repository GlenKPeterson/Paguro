package org.organicdesign.fp.function;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.util.concurrent.Callable;

import static org.junit.Assert.*;
import static org.organicdesign.testUtils.EqualsContract.equalsDistinctHashCode;

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

    @Test public void constantFunction() throws Exception {
        Function0<Integer> f = Function0.constantFunction(7);
        assertEquals(Integer.valueOf(7), f.apply());
        assertEquals(Integer.valueOf(7), f.applyEx());
        assertEquals(Integer.valueOf(7), f.get());
        assertEquals(Integer.valueOf(7), f.call());
        assertEquals(f.hashCode(), Function0.constantFunction(Integer.valueOf(7)).hashCode());
        assertTrue(f.equals(Function0.constantFunction(Integer.valueOf(7))));

        assertEquals("() -> 7", f.toString());

        equalsDistinctHashCode(Function0.constantFunction(7),
                               Function0.constantFunction(7),
                               Function0.constantFunction(7),
                               Function0.constantFunction(8));

        assertEquals(0, Function0.constantFunction(null).hashCode());

        assertNotEquals(Function0.constantFunction(null), null);

        assertFalse(Function0.constantFunction(35).equals((Callable<Integer>) () -> 35));
    }

    @Test(expected = IllegalStateException.class)
    public void testCall() throws Exception {
        new Function0<Integer>() {
            @Override public Integer applyEx() throws Exception {
                throw new IllegalStateException("test exception");
            }
        }.call();

    }

    @Test public void testNull() {
        assertNull(Function0.NULL.apply());
    }
}
