package org.organicdesign.fp.type;

import org.junit.Test;
import org.organicdesign.fp.collections.ImList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.organicdesign.fp.StaticImports.vec;
import static org.organicdesign.fp.type.RuntimeTypes.registerClasses;
import static org.organicdesign.fp.type.RuntimeTypes.union2Str;

/**
 Created by gpeterso on 4/23/17.
 */
public class RuntimeTypesTest {

    @Test
    public void testBasics() {
        @SuppressWarnings("rawtypes")
        ImList<Class> cs0 = registerClasses();
        assertEquals(vec(), cs0);

        @SuppressWarnings("rawtypes")
        ImList<Class> cs2 = registerClasses(Integer.class, String.class);
        assertEquals(vec(Integer.class, String.class), cs2);
        assertSame(cs2, registerClasses(Integer.class, String.class));

        registerClasses(Integer.class, String.class);
        registerClasses(Integer.class, String.class);
        registerClasses(Integer.class, String.class);
        assertSame(cs2, registerClasses(Integer.class, String.class));

        @SuppressWarnings("rawtypes")
        ImList<Class> cs4 = registerClasses(String.class, Integer.class);
        assertEquals(vec(String.class, Integer.class), cs4);
        assertSame(cs4, registerClasses(String.class, Integer.class));

        @SuppressWarnings("rawtypes")
        ImList<Class> cs6 = registerClasses(Integer.class, Double.class);
        assertEquals(vec(Integer.class, Double.class), cs6);
        assertSame(cs6, registerClasses(Integer.class, Double.class));

        @SuppressWarnings("rawtypes")
        ImList<Class> cs9 = registerClasses(Integer.class, Double.class, String.class);
        assertEquals(vec(Integer.class, Double.class, String.class), cs9);
        assertSame(cs9, registerClasses(Integer.class, Double.class, String.class));

        assertSame(cs2, registerClasses(Integer.class, String.class));
        assertSame(cs4, registerClasses(String.class, Integer.class));
        assertSame(cs6, registerClasses(Integer.class, Double.class));
        assertSame(cs9, registerClasses(Integer.class, Double.class, String.class));

        assertEquals("String", RuntimeTypes.name(String.class));
        assertEquals("Integer", RuntimeTypes.name(Integer.class));
    }

    // This can't be run in conjunction with other tests because they register runtime types too!
//    @Test
//    public void testRegisterClasses2() {
//        ImList<Class> cs0 = registerClasses();
//        assertEquals(vec(), cs0);
//        assertEquals(0, RuntimeTypes.size);
//
//        ImList<Class> cs1 = vec(Integer.class, String.class);
//        ImList<Class> cs2 = registerClasses(Integer.class, String.class);
//        assertEquals(cs1, cs2);
//        assertEquals(vec(Integer.class, String.class), cs2);
//        assertEquals(2, RuntimeTypes.size);
//
//        registerClasses(Integer.class, String.class);
//        registerClasses(Integer.class, String.class);
//        registerClasses(Integer.class, String.class);
//        assertEquals(2, RuntimeTypes.size);
//
//        ImList<Class> cs3 = vec(String.class, Integer.class);
//        ImList<Class> cs4 = registerClasses(String.class, Integer.class);
//        assertEquals(cs3, cs4);
//        assertEquals(4, RuntimeTypes.size);
//
//        registerClasses(Integer.class, String.class);
//        registerClasses(Integer.class, String.class);
//        registerClasses(String.class, Integer.class);
//        registerClasses(String.class, Integer.class);
//        assertEquals(4, RuntimeTypes.size);
//
//        ImList<Class> cs5 = vec(Integer.class, Double.class);
//        ImList<Class> cs6 = registerClasses(Integer.class, Double.class);
//        assertEquals(cs5, cs6);
//        assertEquals(5, RuntimeTypes.size);
//
//        registerClasses(Integer.class, String.class);
//        registerClasses(String.class, Integer.class);
//        registerClasses(Integer.class, Double.class);
//        assertEquals(5, RuntimeTypes.size);
//
//        ImList<Class> cs7 = vec(Double.class, Integer.class);
//        ImList<Class> cs8 = registerClasses(Double.class, Integer.class);
//        assertEquals(cs7, cs8);
//        assertEquals(7, RuntimeTypes.size);
//
//        registerClasses(Integer.class, String.class);
//        registerClasses(String.class, Integer.class);
//        registerClasses(Integer.class, Double.class);
//        registerClasses(Double.class, Integer.class);
//        assertEquals(7, RuntimeTypes.size);
//    }

    @Test public void testUnion2Str() {
        assertEquals("3:Integer|String", union2Str(3, vec(Integer.class, String.class)));
        assertEquals("\"hi\":Integer|String", union2Str("hi", vec(Integer.class, String.class)));
    }
}