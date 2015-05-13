// Copyright 2015 PlanBase Inc. & Glen Peterson
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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

public class Tuple2Test {
    @Rule
    public ExpectedException thrown= ExpectedException.none();

    @Test
    public void constructionAndAccess() {
        Tuple2<Integer,String> a = Tuple2.of(7, "Hello");

        assertEquals(new Integer(7), a._1());
        assertEquals(new Integer(7), a.getKey());

        assertEquals("Hello", a._2());
        assertEquals("Hello", a.getValue());

        assertEquals(a, a);
        assertEquals(a.hashCode(), a.hashCode());

        Tuple2<Integer,String> b = Tuple2.of(5, "hello");

        assertEquals(new Integer(5), b._1());
        assertEquals(new Integer(5), b.getKey());

        assertEquals("hello", b._2());
        assertEquals("hello", b.getValue());

        assertEquals(b, b);
        assertEquals(b.hashCode(), b.hashCode());

        assertFalse(a.equals(b));
        assertFalse(b.equals(a));
        assertNotEquals(a.hashCode(), b.hashCode());

        Tuple2<Integer,String> c = Tuple2.of(7, null);

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
        assertNotEquals(d.hashCode(), a.hashCode());
        assertNotEquals(d.hashCode(), b.hashCode());
        assertNotEquals(d.hashCode(), c.hashCode());

        assertEquals("Tuple2(hi,3)", Tuple2.of("hi", 3).toString());
    }

    @SuppressWarnings("deprecation")
    @Test
    public void modification() {
        Tuple2<Integer,String> t = Tuple2.of(19, "World");
        thrown.expect(UnsupportedOperationException.class);
        t.setValue("Boom!");
    }
}
