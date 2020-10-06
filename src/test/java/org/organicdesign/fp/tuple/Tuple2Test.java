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

import org.junit.Test;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.Assert.*;
import static org.organicdesign.fp.TestUtilities.serializeDeserialize;
import static org.organicdesign.testUtils.EqualsContract.equalsDistinctHashCode;

public class Tuple2Test {

    public void constructionAndAccess() {
        Tuple2<Integer,String> a = new Tuple2<>(7, "Hello");

        assertEquals(Integer.valueOf(7), a._1());
        assertEquals(Integer.valueOf(7), a.getKey());

        assertEquals("Hello", a._2());
        assertEquals("Hello", a.getValue());

        assertEquals(a, a);
        assertEquals(a.hashCode(), a.hashCode());

        Tuple2<Integer,String> b = Tuple2.of(5, "hello");

        assertEquals(Integer.valueOf(5), b._1());
        assertEquals(Integer.valueOf(5), b.getKey());

        assertEquals("hello", b._2());
        assertEquals("hello", b.getValue());

        assertEquals(b, b);
        assertEquals(b.hashCode(), b.hashCode());

        assertFalse(a.equals(b));
        assertFalse(b.equals(a));
        assertNotEquals(a.hashCode(), b.hashCode());

        Tuple2<Integer,String> c = new Tuple2<>(7, null);

        assertEquals(Integer.valueOf(7), c._1());
        assertEquals(Integer.valueOf(7), c.getKey());

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

        Tuple2<Integer,String> d = Tuple2.of(null, "Hello");

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
                               Tuple2.of(realEntry),
                               realEntry,
                               new Tuple2<>(7, "hello"));

        assertEquals(a, serializeDeserialize(a));
        assertEquals(b, serializeDeserialize(b));
        assertEquals(c, serializeDeserialize(c));
        assertEquals(d, serializeDeserialize(d));
        assertEquals(realEntry, serializeDeserialize(Tuple2.of(realEntry)));
        assertEquals(serializeDeserialize(Tuple2.of(realEntry)), realEntry);
    }

    @Test public void treeMapEntryTest() {
        TreeMap<String,Integer> control = new TreeMap<>();
        control.put("one", 1);
        Map.Entry<String,Integer> realEntry = control.entrySet().iterator().next();
        Tuple2<String,Integer> test = Tuple2.of("one", 1);

        assertEquals(realEntry.hashCode(), test.hashCode());

        assertEquals(realEntry.hashCode(), serializeDeserialize(test).hashCode());

        assertTrue(test.equals(realEntry));
        assertTrue(realEntry.equals(test));

        assertTrue(serializeDeserialize(test).equals(realEntry));
        assertTrue(realEntry.equals(serializeDeserialize(test)));
    }

    @SuppressWarnings("deprecation")
    @Test(expected = UnsupportedOperationException.class)
    public void modification() {
        Tuple2<Integer,String> t = Tuple2.of(19, "World");
        t.setValue("Boom!");
    }
}
