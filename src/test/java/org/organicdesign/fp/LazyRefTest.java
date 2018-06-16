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
import org.organicdesign.fp.function.Fn0;
import org.organicdesign.fp.function.LazyRef;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class LazyRefTest {

    @Test
    public void testLazyRef() {
        AtomicInteger intRef = new AtomicInteger(3);
        Fn0<Integer> f = () -> intRef.incrementAndGet();
        assertEquals(new Integer(4), f.invoke());
        assertEquals(new Integer(5), f.invoke());
        assertEquals(new Integer(6), f.invoke());

        LazyRef<Integer> lr = LazyRef.of(f);

        assertEquals("LazyRef(*not-computed-yet*)", lr.toString());

        assertEquals(new Integer(7), lr.get());

        assertEquals("LazyRef(7)", lr.toString());

        assertEquals(new Integer(8), f.invoke());
        intRef.set(-1);

        assertEquals(new Integer(7), lr.get());
        assertEquals(new Integer(7), lr.get());
        assertEquals(new Integer(7), lr.get());
    }

    @Test (expected = IllegalArgumentException.class)
    public void ofEx() {
        LazyRef.of(null);
    }

//    @Test
//    public void testLazyInt() {
//        Mutable.IntRef intRef = Mutable.IntRef.of(3);
//        Fn0<Integer> f = () -> intRef.increment().value();
//        assertEquals(f.invoke(), new Integer(4));
//        assertEquals(f.invoke(), new Integer(5));
//        assertEquals(f.invoke(), new Integer(6));
//
//        Lazy.Int lr = Lazy.Int.of(f);
//        assertEquals(lr.get(), 7);
//
//        assertEquals(f.invoke(), new Integer(8));
//        intRef.set(-1);
//
//        assertEquals(lr.get(), 7);
//        assertEquals(lr.get(), 7);
//        assertEquals(lr.get(), 7);
//    }

}
