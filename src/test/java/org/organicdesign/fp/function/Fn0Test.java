package org.organicdesign.fp.function;


import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;


public class Fn0Test {
    @Test
    public void testExceptions() {
        assertThrowsExactly(RuntimeException.class, () ->
                new Fn0<Integer>() {
                    @Override public Integer applyEx() throws Exception {
                        throw new IOException("test exception");
                    }
                }.apply());

        assertThrowsExactly(IllegalStateException.class, () ->
                ((Fn0<Integer>) () -> {
                    throw new IllegalStateException("test exception");
                }).apply());

        assertThrowsExactly(IllegalStateException.class, () ->
                ((Fn0<Integer>) () -> {
                    throw new IllegalStateException("test exception");
                }).call());
    }

    @Test public void constantFunction() throws Exception {
        Fn0<Integer> f = () -> 7;
        assertEquals(Integer.valueOf(7), f.apply());
        assertEquals(Integer.valueOf(7), f.applyEx());
        assertEquals(Integer.valueOf(7), f.get());
        assertEquals(Integer.valueOf(7), f.call());
//        assertEquals(f.hashCode(), Fn0.constantFunction(Integer.valueOf(7)).hashCode());
//        assertTrue(f.equals(Fn0.constantFunction(Integer.valueOf(7))));
//
//        assertEquals("() -> 7", f.toString());
//
//        equalsDistinctHashCode(Fn0.constantFunction(7),
//                               Fn0.constantFunction(7),
//                               Fn0.constantFunction(7),
//                               Fn0.constantFunction(8));
//
//        assertEquals(0, Fn0.constantFunction(null).hashCode());
//
//        assertNotEquals(Fn0.constantFunction(null), null);
//
//        assertFalse(Fn0.constantFunction(35).equals((Callable<Integer>) () -> 35));
    }
}
