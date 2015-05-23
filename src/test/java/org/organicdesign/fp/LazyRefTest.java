// Copyright (c) 2015-03-06 PlanBase Inc. & Glen Peterson
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
package org.organicdesign.fp;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.organicdesign.fp.function.Function0;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class LazyRefTest {

    @Test
    public void testLazyRef() {
        Mutable.IntRef intRef = Mutable.IntRef.of(3);
        Function0<Integer> f = () -> intRef.increment().value();
        assertEquals(f.apply(), new Integer(4));
        assertEquals(f.apply(), new Integer(5));
        assertEquals(f.apply(), new Integer(6));

        LazyRef<Integer> lr = LazyRef.of(f);

        assertEquals("LazyRef(*not-computed-yet*)", lr.toString());

        assertEquals(lr.get(), new Integer(7));

        assertEquals("LazyRef(7)", lr.toString());

        assertEquals(f.apply(), new Integer(8));
        intRef.set(-1);

        assertEquals(lr.get(), new Integer(7));
        assertEquals(lr.get(), new Integer(7));
        assertEquals(lr.get(), new Integer(7));
    }

    @Test (expected = IllegalArgumentException.class)
    public void ofEx() {
        LazyRef.of(null);
    }

//    @Test
//    public void testLazyInt() {
//        Mutable.IntRef intRef = Mutable.IntRef.of(3);
//        Function0<Integer> f = () -> intRef.increment().value();
//        assertEquals(f.apply(), new Integer(4));
//        assertEquals(f.apply(), new Integer(5));
//        assertEquals(f.apply(), new Integer(6));
//
//        Lazy.Int lr = Lazy.Int.of(f);
//        assertEquals(lr.get(), 7);
//
//        assertEquals(f.apply(), new Integer(8));
//        intRef.set(-1);
//
//        assertEquals(lr.get(), 7);
//        assertEquals(lr.get(), 7);
//        assertEquals(lr.get(), 7);
//    }

}
