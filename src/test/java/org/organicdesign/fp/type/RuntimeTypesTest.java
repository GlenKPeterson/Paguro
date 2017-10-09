package org.organicdesign.fp.type;

import org.junit.Ignore;
import org.junit.Test;
import org.organicdesign.fp.collections.ImList;

import static org.junit.Assert.assertEquals;
import static org.organicdesign.fp.StaticImportsKt.vec;
import static org.organicdesign.fp.type.RuntimeTypes.union2Str;

/**
 Created by gpeterso on 4/23/17.
 */
public class RuntimeTypesTest {

    // This can't be run in conjunction with other tests because they register runtime types!
    @Ignore
    @Test public void testBasics() {
        ImList<Class> cs1 = vec(Integer.class, String.class);
        ImList<Class> cs2 = RuntimeTypes.registerClasses(Integer.class, String.class);
        assertEquals(cs1, cs2);
        assertEquals(vec(Integer.class, String.class), cs2);
        assertEquals(1, RuntimeTypes.size());

        RuntimeTypes.registerClasses(Integer.class, String.class);
        RuntimeTypes.registerClasses(Integer.class, String.class);
        RuntimeTypes.registerClasses(Integer.class, String.class);
        assertEquals(1, RuntimeTypes.size());

        ImList<Class> cs3 = vec(String.class, Integer.class);
        ImList<Class> cs4 = RuntimeTypes.registerClasses(String.class, Integer.class);
        assertEquals(cs3, cs4);
        assertEquals(2, RuntimeTypes.size());

        RuntimeTypes.registerClasses(Integer.class, String.class);
        RuntimeTypes.registerClasses(Integer.class, String.class);
        RuntimeTypes.registerClasses(String.class, Integer.class);
        RuntimeTypes.registerClasses(String.class, Integer.class);
        assertEquals(2, RuntimeTypes.size());

        ImList<Class> cs5 = vec(Integer.class, Double.class);
        ImList<Class> cs6 = RuntimeTypes.registerClasses(Integer.class, Double.class);
        assertEquals(cs5, cs6);
        assertEquals(3, RuntimeTypes.size());

        RuntimeTypes.registerClasses(Integer.class, String.class);
        RuntimeTypes.registerClasses(String.class, Integer.class);
        RuntimeTypes.registerClasses(Integer.class, Double.class);
        assertEquals(3, RuntimeTypes.size());

        ImList<Class> cs7 = vec(Double.class, Integer.class);
        ImList<Class> cs8 = RuntimeTypes.registerClasses(Double.class, Integer.class);
        assertEquals(cs7, cs8);
        assertEquals(4, RuntimeTypes.size());

        RuntimeTypes.registerClasses(Integer.class, String.class);
        RuntimeTypes.registerClasses(String.class, Integer.class);
        RuntimeTypes.registerClasses(Integer.class, Double.class);
        RuntimeTypes.registerClasses(Double.class, Integer.class);
        assertEquals(4, RuntimeTypes.size());

//        RuntimeTypes.registerObjects("Hi", 3);
//        assertEquals(4, RuntimeTypes.size());
//
//        RuntimeTypes.registerObjects("Hi", "there");
//        assertEquals(5, RuntimeTypes.size());
//
//        RuntimeTypes.registerObjects(7, 9);
//        assertEquals(6, RuntimeTypes.size());

        assertEquals("String", RuntimeTypes.name(String.class));
        assertEquals("Integer", RuntimeTypes.name(Integer.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullClassArray() { RuntimeTypes.registerClasses((Class[]) null); }

    @Test(expected = IllegalArgumentException.class)
    public void testNullObjectArray() { RuntimeTypes.registerClasses(); }

    @Test(expected = IllegalArgumentException.class)
    public void testClassArrayContainsNull() {
        RuntimeTypes.registerClasses(String.class, null, Integer.class);
    }

    @Test public void testUnion2Str() {
        assertEquals("3:Integer|String", union2Str(3, vec(Integer.class, String.class)));
        assertEquals("\"hi\":Integer|String", union2Str("hi", vec(Integer.class, String.class)));
    }
}