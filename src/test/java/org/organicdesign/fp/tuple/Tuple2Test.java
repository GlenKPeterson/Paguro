// Copyright 2016 PlanBase Inc. & Glen Peterson
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.organicdesign.fp.tuple;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;
import static org.organicdesign.fp.TestUtilities.serializeDeserialize;
import static org.organicdesign.testUtils.EqualsContract.equalsDistinctHashCode;

public class Tuple2Test {
    @Rule
    public ExpectedException thrown= ExpectedException.none();

    public void constructionAndAccess() {
        Tuple2<Integer,String> a = new Tuple2<>(7, "Hello");

        assertEquals(new Integer(7), a.get_1());
        assertEquals(new Integer(7), a.getKey());

        assertEquals("Hello", a.get_2());
        assertEquals("Hello", a.getValue());

        assertEquals(a, a);
        assertEquals(a.hashCode(), a.hashCode());

        Tuple2<Integer,String> b = new Tuple2<>(5, "hello");

        assertEquals(new Integer(5), b.get_1());
        assertEquals(new Integer(5), b.getKey());

        assertEquals("hello", b.get_2());
        assertEquals("hello", b.getValue());

        assertEquals(b, b);
        assertEquals(b.hashCode(), b.hashCode());

        assertFalse(a.equals(b));
        assertFalse(b.equals(a));
        assertNotEquals(a.hashCode(), b.hashCode());

        Tuple2<Integer,String> c = new Tuple2<>(7, null);

        assertEquals(new Integer(7), c.get_1());
        assertEquals(new Integer(7), c.getKey());

        assertEquals(null, c.get_2());
        assertEquals(null, c.getValue());

        assertEquals(c, c);
        assertEquals(c.hashCode(), c.hashCode());

        assertFalse(c.equals(a));
        assertFalse(a.equals(c));
        assertFalse(c.equals(b));
        assertFalse(b.equals(c));
        assertNotEquals(c.hashCode(), a.hashCode());
        assertNotEquals(c.hashCode(), b.hashCode());

        Tuple2<Integer,String> d = new Tuple2<>(null, "Hello");

        assertEquals(null, d.get_1());
        assertEquals(null, d.getKey());

        assertEquals("Hello", d.get_2());
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
        //noinspection ObjectEqualsNull
        assertFalse(b.equals(null));
        assertFalse(c.equals(7));
        assertNotEquals(d.hashCode(), a.hashCode());
        assertNotEquals(d.hashCode(), b.hashCode());
        assertNotEquals(d.hashCode(), c.hashCode());

        assertEquals("kv(\"hi\",3)", new Tuple2<>("hi", 3).toString());


        Map<Integer,String> realMap = new HashMap<>();
        realMap.put(7, "Hello");
        Map.Entry<Integer,String> realEntry = realMap.entrySet().iterator().next();

        assertEquals(realEntry, a);
        assertEquals(a, realEntry);

        assertEquals(realEntry, serializeDeserialize(a));
        assertEquals(serializeDeserialize(a), realEntry);

        equalsDistinctHashCode(a,
                               Tuple2.Companion.of(realEntry),
                               realEntry,
                               new Tuple2<>(7, "hello"));

        assertEquals(a, serializeDeserialize(a));
        assertEquals(b, serializeDeserialize(b));
        assertEquals(c, serializeDeserialize(c));
        assertEquals(d, serializeDeserialize(d));
        assertEquals(realEntry, serializeDeserialize(Tuple2.Companion.of(realEntry)));
        assertEquals(serializeDeserialize(Tuple2.Companion.of(realEntry)), realEntry);
    }

    public <K,V> void testHashVsEntry(K k, V v) {
        Map<K,V> realMap = new HashMap<>();
        realMap.put(k, v);
        Map.Entry<K,V> realEntry = realMap.entrySet().iterator().next();
        assertEquals(realEntry.hashCode(), new Tuple2<>(k, v).hashCode());

        if (k != null) {
            TreeMap<K, V> treeMap = new TreeMap<>();
            treeMap.put(k, v);
            Map.Entry<K, V> treeEntry = treeMap.entrySet().iterator().next();
            assertEquals(treeEntry.hashCode(), new Tuple2<>(k, v).hashCode());
        }
    }

    @Test public void compareHashCodeWithMapEntries() {
        testHashVsEntry(0, 1);
        testHashVsEntry(1, 0);
        testHashVsEntry(3, 4);
        testHashVsEntry(4, 3);
        testHashVsEntry("Hello", 7.5);
        testHashVsEntry(29.7, "Cuckoo");
        testHashVsEntry(5, null);
        testHashVsEntry(null, 5);
        testHashVsEntry(null, null);
    }

    @Test public void treeMapEntryTest() {
        TreeMap<String,Integer> control = new TreeMap<>();
        control.put("one", 1);
        Map.Entry<String,Integer> realEntry = control.entrySet().iterator().next();
        Tuple2<String,Integer> test = new Tuple2<>("one", 1);

        assertEquals(realEntry.hashCode(), test.hashCode());

        assertEquals(realEntry.hashCode(), serializeDeserialize(test).hashCode());

        assertTrue(test.equals(realEntry));
        assertTrue(realEntry.equals(test));

        assertTrue(serializeDeserialize(test).equals(realEntry));
        assertTrue(realEntry.equals(serializeDeserialize(test)));
    }

    @SuppressWarnings("deprecation")
    @Test
    public void modification() {
        Tuple2<Integer,String> t = new Tuple2<>(19, "World");
        thrown.expect(UnsupportedOperationException.class);
        t.setValue("Boom!");
    }
}
