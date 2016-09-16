package org.organicdesign.fp.collections;

import java.util.HashMap;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;
import static org.organicdesign.fp.TestUtilities.serializeDeserialize;
import static org.organicdesign.testUtils.EqualsContract.equalsDistinctHashCode;

public class KeyValTest {
    @Rule
    public ExpectedException thrown= ExpectedException.none();

    @SuppressWarnings("deprecation")
    @Test
    public void constructionAndAccess() {
        KeyVal<Integer,String> a = new KeyVal<>(7, "Hello");

        assertEquals(new Integer(7), a._1());
        assertEquals(new Integer(7), a.getKey());

        assertEquals("Hello", a._2());
        assertEquals("Hello", a.getValue());

        assertEquals(a, a);
        assertEquals(a.hashCode(), a.hashCode());

        KeyVal<Integer,String> b = KeyVal.of(5, "hello");

        assertEquals(new Integer(5), b._1());
        assertEquals(new Integer(5), b.getKey());

        assertEquals("hello", b._2());
        assertEquals("hello", b.getValue());

        assertEquals(b, b);
        assertEquals(b.hashCode(), b.hashCode());

        assertFalse(a.equals(b));
        assertFalse(b.equals(a));
        assertNotEquals(a.hashCode(), b.hashCode());

        KeyVal<Integer,String> c = new KeyVal<>(7, null);

        assertEquals(new Integer(7), c._1());
        assertEquals(new Integer(7), c.getKey());

        assertEquals(null, c._2());
        assertEquals(null, c.getValue());

        assertEquals(c, c);
        assertEquals(c.hashCode(), c.hashCode());

        assertFalse(c.equals(a));
        assertFalse(a.equals(c));
        assertFalse(c.equals(b));
        assertFalse(b.equals(c));
        assertNotEquals(c.hashCode(), a.hashCode());
        assertNotEquals(c.hashCode(), b.hashCode());

        KeyVal<Integer,String> d = KeyVal.of(null, "Hello");

        assertEquals(null, d._1());
        assertEquals(null, d.getKey());

        assertEquals("Hello", d._2());
        assertEquals("Hello", d.getValue());

        assertEquals(d, d);
        assertEquals(d.hashCode(), d.hashCode());

        assertFalse(d.equals(a));
        assertFalse(d.equals(b));
        assertFalse(d.equals(c));
        assertFalse(a.equals(d));
        assertFalse(b.equals(d));
        assertFalse(c.equals(d));
        assertFalse(a.equals("Hello"));
        assertFalse(b.equals(null));
        assertFalse(c.equals(7));
        assertNotEquals(d.hashCode(), a.hashCode());
        assertNotEquals(d.hashCode(), b.hashCode());
        assertNotEquals(d.hashCode(), c.hashCode());

        assertEquals("kv(\"hi\",3)", new KeyVal<>("hi", 3).toString());


        Map<Integer,String> realMap = new HashMap<>();
        realMap.put(7, "Hello");
        Map.Entry<Integer,String> realEntry = realMap.entrySet().iterator().next();

        equalsDistinctHashCode(a,
                               KeyVal.of(realEntry),
                               realEntry,
                               new KeyVal<>(7, "hello"));

        assertEquals(a, serializeDeserialize(a));
        assertEquals(b, serializeDeserialize(b));
        assertEquals(c, serializeDeserialize(c));
        assertEquals(d, serializeDeserialize(d));
        assertEquals(realEntry, serializeDeserialize(new KeyVal<>(realEntry)));
    }

    @SuppressWarnings("deprecation")
    @Test
    public void modification() {
        KeyVal<Integer,String> t = KeyVal.of(19, "World");
        thrown.expect(UnsupportedOperationException.class);
        t.setValue("Boom!");
    }
}