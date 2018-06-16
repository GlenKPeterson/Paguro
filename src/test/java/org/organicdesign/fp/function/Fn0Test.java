package org.organicdesign.fp.function;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.organicdesign.fp.StaticImportsTest.Companion.A;
import org.organicdesign.fp.StaticImportsTest.Companion.B;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.organicdesign.fp.StaticImportsTest.getAFromF;

@RunWith(JUnit4.class)
public class Fn0Test {

//    class A {
//        public String foo() { return "A"; }
//    }
//    class B extends A {
//        public String foo() { return "B"; }
//    }
//
//    private A getAFromF(Fn0<? extends A> f) { return f.invoke(); }

    @Test public void testBasics() {
        AtomicInteger i = new AtomicInteger(0);
        assertEquals("A", getAFromF(A::new).foo());
        assertEquals("B", getAFromF(B::new).foo());

        assertEquals("B", getAFromF(new Fn0<B>() {
            @Override
            public B invokeEx() throws Exception {
                if (i.getAndIncrement() > 33) {
                    throw new Exception("Hiya!");
                }
                return new B();
            }
        }).foo());

        // Fn0.Companion.toFn0() will be a handy way to cast in Java once Kotlin 1.3 comes out and lets you static import
        // Fn0.toFn0.
        assertEquals("B", getAFromF(Fn0.Companion.toFn0(() -> {
            if (i.getAndIncrement() > 33) {
                throw new Exception("Hiya!");
            }
            return new B();
        })).foo());

        assertEquals("B", getAFromF(new Fn0<A>() {
            @Override
            public B invokeEx() throws Exception {
                if (i.getAndIncrement() > 33) {
                    throw new Exception("Hiya!");
                }
                return new B();
            }
        }).foo());
        assertEquals("B", getAFromF(new Fn0<A>() {
            @Override
            public A invokeEx() throws Exception {
                if (i.getAndIncrement() > 33) {
                    throw new Exception("Hiya!");
                }
                return new B();
            }
        }).foo());
    }

    @Test(expected = RuntimeException.class)
    public void applyIOException() {
        new Fn0<Integer>() {
            @Override public Integer invokeEx() throws Exception {
                throw new IOException("test exception");
            }
        }.invoke();
    }

    @Test(expected = IllegalStateException.class)
    public void applyIllegalStateException() {
        new Fn0<Integer>() {
            @Override public Integer invokeEx() throws Exception {
                throw new IllegalStateException("test exception");
            }
        }.invoke();
    }

//    @Test public void constantFunction() throws Exception {
//        Fn0<Integer> f = Fn0.constantFunction(7);
//        assertEquals(Integer.valueOf(7), f.invoke());
//        assertEquals(Integer.valueOf(7), f.invokeEx());
//        assertEquals(Integer.valueOf(7), f.get());
//        assertEquals(Integer.valueOf(7), f.call());
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
//    }

    @Test(expected = IllegalStateException.class)
    public void testCall() throws Exception {
        new Fn0<Integer>() {
            @Override public Integer invokeEx() throws Exception {
                throw new IllegalStateException("test exception");
            }
        }.call();
    }

//    @Test public void testNull() {
//        assertNull(Fn0.ConstObjObj.NULL.invoke());
//    }
}
