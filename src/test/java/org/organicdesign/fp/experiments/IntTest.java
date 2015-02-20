// Copyright (c) 2014-03-08 PlanBase Inc. & Glen Peterson
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

package org.organicdesign.fp.experiments;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.organicdesign.fp.experiments.math.Int;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class IntTest {

    @Test
    public void basics() {
        assertEquals(Int.of(-1), Int.NEG_ONE);
        assertEquals(Int.of(0), Int.ZERO);
        assertEquals(Int.of(1), Int.ONE);
        assertEquals(Int.of((int) 1), Int.ONE);

        assertEquals(Int.of(1 / 24), Int.ZERO);
        assertEquals(Int.of(1 / (-24)), Int.ZERO);

        assertEquals(Int.of(-1).toPrimitiveInt(), -1);
        assertEquals(Int.of(0).toPrimitiveInt(), 0);
        assertEquals(Int.of(1).toPrimitiveInt(), 1);

        assertTrue(Int.NEG_ONE.eq(Int.NEG_ONE));
        assertTrue(Int.ZERO.eq(Int.ZERO));
        assertTrue(Int.ONE.eq(Int.ONE));
    }
}
