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

import org.junit.Test;

import static org.junit.Assert.*;

public class Tuple3Test {
    @Test
    public void constructionAndAccess() {
        Tuple3<Integer,String,Boolean> a = Tuple3.of(7, "Hello", true);

        assertEquals(new Integer(7), a._1());
        assertEquals("Hello", a._2());
        assertTrue(a._3());

        assertEquals(a, a);
        assertEquals(a.hashCode(), a.hashCode());

        Tuple3<Integer,String,Boolean> b = Tuple3.of(5, "hello", false);

        assertEquals(new Integer(5), b._1());
        assertEquals("hello", b._2());
        assertFalse(b._3());

        assertEquals(b, b);
        assertEquals(b.hashCode(), b.hashCode());

        assertFalse(a.equals(b));
        assertFalse(b.equals(a));
        assertNotEquals(a.hashCode(), b.hashCode());

        Tuple3<Integer,String,Boolean> c = Tuple3.of(7, null, null);

        assertEquals(new Integer(7), c._1());
        assertEquals(null, c._2());
        assertEquals(null, c._3());

        assertEquals(c, c);
        assertEquals(c.hashCode(), c.hashCode());

        assertFalse(c.equals(a));
        assertFalse(a.equals(c));
        assertFalse(c.equals(b));
        assertFalse(b.equals(c));
        assertNotEquals(c.hashCode(), a.hashCode());
        assertNotEquals(c.hashCode(), b.hashCode());

        Tuple3<Integer,String,Boolean> d = Tuple3.of(null, "Hello", null);

        assertEquals(null, d._1());
        assertEquals("Hello", d._2());
        assertEquals(null, d._3());

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

        assertEquals("Tuple3(hi,3,true)", Tuple3.of("hi", 3, true).toString());
    }

}
