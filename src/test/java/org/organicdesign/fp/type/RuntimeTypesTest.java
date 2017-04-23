package org.organicdesign.fp.type;

import org.junit.Ignore;
import org.junit.Test;
import org.organicdesign.fp.collections.ImList;

import static org.junit.Assert.assertEquals;
import static org.organicdesign.fp.StaticImports.vec;

/**
 Created by gpeterso on 4/23/17.
 */
public class RuntimeTypesTest {

    // This can't be run in conjunction with other tests because they register runtime types!
    @Ignore
    @Test public void testBasics() {
        ImList<Class> cs1 = vec(Integer.class, String.class);
        ImList<Class> cs2 = RuntimeTypes.registerClasses(cs1);
        assertEquals(cs1, cs2);
        assertEquals(vec(Integer.class, String.class), cs2);
        assertEquals(1, RuntimeTypes.size());

        RuntimeTypes.registerClasses(cs1);
        RuntimeTypes.registerClasses(cs2);
        RuntimeTypes.registerClasses(vec(Integer.class, String.class));
        assertEquals(1, RuntimeTypes.size());

        ImList<Class> cs3 = vec(String.class, Integer.class);
        ImList<Class> cs4 = RuntimeTypes.registerClasses(cs3);
        assertEquals(cs3, cs4);
        assertEquals(2, RuntimeTypes.size());

        RuntimeTypes.registerClasses(cs1);
        RuntimeTypes.registerClasses(cs2);
        RuntimeTypes.registerClasses(vec(Integer.class, String.class));
        RuntimeTypes.registerClasses(cs3);
        RuntimeTypes.registerClasses(cs4);
        RuntimeTypes.registerClasses(vec(String.class, Integer.class));
        assertEquals(2, RuntimeTypes.size());

        ImList<Class> cs5 = vec(Integer.class, null);
        ImList<Class> cs6 = RuntimeTypes.registerClasses(cs5);
        assertEquals(cs5, cs6);
        assertEquals(3, RuntimeTypes.size());

        RuntimeTypes.registerClasses(cs1);
        RuntimeTypes.registerClasses(cs2);
        RuntimeTypes.registerClasses(vec(Integer.class, String.class));
        RuntimeTypes.registerClasses(cs3);
        RuntimeTypes.registerClasses(cs4);
        RuntimeTypes.registerClasses(vec(String.class, Integer.class));
        RuntimeTypes.registerClasses(cs5);
        RuntimeTypes.registerClasses(cs6);
        RuntimeTypes.registerClasses(vec(Integer.class, null));
        assertEquals(3, RuntimeTypes.size());

        ImList<Class> cs7 = vec(null, Integer.class);
        ImList<Class> cs8 = RuntimeTypes.registerClasses(cs7);
        assertEquals(cs7, cs8);
        assertEquals(4, RuntimeTypes.size());

        RuntimeTypes.registerClasses(cs1);
        RuntimeTypes.registerClasses(cs2);
        RuntimeTypes.registerClasses(vec(Integer.class, String.class));
        RuntimeTypes.registerClasses(cs3);
        RuntimeTypes.registerClasses(cs4);
        RuntimeTypes.registerClasses(vec(String.class, Integer.class));
        RuntimeTypes.registerClasses(cs5);
        RuntimeTypes.registerClasses(cs6);
        RuntimeTypes.registerClasses(vec(Integer.class, null));
        RuntimeTypes.registerClasses(cs7);
        RuntimeTypes.registerClasses(cs8);
        RuntimeTypes.registerClasses(vec(null, Integer.class));
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
    public void testNullClassArray() { RuntimeTypes.registerClasses(null); }

//    @Test(expected = IllegalArgumentException.class)
//    public void testNullObjectArray() { RuntimeTypes.registerObjects((Object[]) null); }
}