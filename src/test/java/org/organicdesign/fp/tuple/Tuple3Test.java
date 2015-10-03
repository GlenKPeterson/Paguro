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
import static org.organicdesign.fp.testUtils.EqualsContract.equalsDistinctHashCode;
import static org.organicdesign.fp.testUtils.EqualsContract.equalsSameHashCode;

public class Tuple3Test {
    @Test
    public void constructionAndAccess() {
        Tuple3<Integer,String,Boolean> a = Tuple3.of(3, "Hello", true);

        assertEquals(new Integer(3), a._1());
        assertEquals("Hello", a._2());
        assertTrue(a._3());

        Tuple3<Integer,String,Boolean> b = Tuple3.of(5, "hello", false);

        assertEquals(new Integer(5), b._1());
        assertEquals("hello", b._2());
        assertFalse(b._3());

        Tuple3<Integer,String,Boolean> c = Tuple3.of(7, null, null);

        assertEquals(new Integer(7), c._1());
        assertEquals(null, c._2());
        assertEquals(null, c._3());

        Tuple3<Integer,String,Boolean> d = Tuple3.of(null, "Hello", null);

        assertEquals(null, d._1());
        assertEquals("Hello", d._2());
        assertEquals(null, d._3());

        equalsDistinctHashCode(a, Tuple3.of(3, "Hello", true), Tuple3.of(3, "Hello", true),
                               Tuple3.of(3, "Hello", false));

        equalsSameHashCode(a, Tuple3.of(3, "Hello", true), Tuple3.of(3, "Hello", true),
                           Tuple3.of("Hello", 3, true));

        assertEquals("Tuple3(hi,11,true)", Tuple3.of("hi", 11, true).toString());
    }

}
