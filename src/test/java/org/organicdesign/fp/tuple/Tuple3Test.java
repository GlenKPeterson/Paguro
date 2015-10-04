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
    @Test public void constructionAndAccess() {
        Tuple3<Integer,String,Boolean> a = Tuple3.of(3, "2nd", true);

        assertEquals(new Integer(3), a._1());
        assertEquals("2nd", a._2());
        assertTrue(a._3());

        Tuple3<String,Integer,Boolean> b = Tuple3.of("1st", 5, false);

        assertEquals("1st", b._1());
        assertEquals(Integer.valueOf(5), b._2());
        assertFalse(b._3());

        Tuple3<Boolean,Integer,String> c = Tuple3.of(true, null, null);

        assertTrue(c._1());
        assertEquals(null, c._2());
        assertEquals(null, c._3());

        Tuple3<Integer,String,Boolean> d = Tuple3.of(null, "2nd", null);

        assertEquals(null, d._1());
        assertEquals("2nd", d._2());
        assertEquals(null, d._3());

        Tuple3<?,?,Integer> e = Tuple3.of(null, null, 9);

        assertEquals(null, e._1());
        assertEquals(null, e._2());
        assertEquals(Integer.valueOf(9), e._3());

        equalsDistinctHashCode(a, Tuple3.of(3, "2nd", true), Tuple3.of(3, "2nd", true),
                               Tuple3.of(3, "2nd", false));

        equalsSameHashCode(a, Tuple3.of(3, "2nd", true), Tuple3.of(3, "2nd", true),
                           Tuple3.of("2nd", 3, true));

        assertEquals("Tuple3(hi,11,true)", Tuple3.of("hi", 11, true).toString());
    }
}
