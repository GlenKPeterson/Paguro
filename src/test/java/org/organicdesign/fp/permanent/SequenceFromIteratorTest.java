// Copyright 2015-04-14 PlanBase Inc. & Glen Peterson
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

package org.organicdesign.fp.permanent;

import org.junit.Test;
import org.organicdesign.fp.Mutable;
import org.organicdesign.fp.function.Function0;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class SequenceFromIteratorTest {

    @Test public void basic() {
        List<Integer> ints = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);
        assertEquals(ints,
                     Sequence.of(ints.iterator()).toJavaArrayList());
        assertArrayEquals(ints.toArray(),
                          Sequence.of(ints.iterator()).toTypedArray());
    }


    @Test
    public void construction() {
        List<Integer> ints = Arrays.asList(5, 4, 3, 2, 1);
        Sequence<Integer> five = Sequence.of(ints.iterator());
        Sequence<Integer> four = five.rest();
        Sequence<Integer> three = four.rest();
        Sequence<Integer> two = three.rest();
        Sequence<Integer> one = two.rest();
        Sequence<Integer> zero = one.rest();
        Sequence<Integer> nada = zero.rest();
        assertEquals(Integer.valueOf(5), five.first());
        assertEquals(Integer.valueOf(4), four.first());
        assertEquals(Integer.valueOf(3), three.first());
        assertEquals(Integer.valueOf(2), two.first());
        assertEquals(Integer.valueOf(1), one.first());
        assertEquals(Sequence.emptySequence(), one.rest());
        assertEquals(Sequence.emptySequence(), zero);
        assertEquals(Sequence.emptySequence(), zero.rest());

        assertEquals(Sequence.emptySequence(), nada);
        assertEquals(Sequence.emptySequence(), nada.rest());

        three.rest().rest().rest().rest().rest().rest().rest().rest().rest().rest().rest().rest().rest().rest().rest();
        assertEquals(Integer.valueOf(3), three.first());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void exception1() {
        Sequence<Integer> zero = Sequence.of(Collections.<Integer>emptyList().iterator());
        assertEquals(Sequence.emptySequence(), zero);
        zero.first();
    }


    @Test public void singleInit() {
        final Mutable.ObjectRef<Integer> i = Mutable.ObjectRef.of(0);
        @SuppressWarnings("unchecked") List<Function0<Integer>> ints =
                Arrays.asList(() -> {
                    i.set(i.value() + 1);
                    return 3;
                },
                              () -> {
                                  i.set(i.value() + 1);
                                  return 2;
                              },
                              () -> {
                                  i.set(i.value() + 1);
                                  return 1;
                              });
        Sequence<Function0<Integer>> three = Sequence.of(ints.iterator());
        Sequence<Function0<Integer>> two = three.rest();
        Sequence<Function0<Integer>> one = two.rest();
        Sequence<Function0<Integer>> zero = one.rest();
        assertEquals(Integer.valueOf(3), three.first().apply());
        assertEquals(Integer.valueOf(1), i.value());
        assertEquals(Integer.valueOf(2), two.first().apply());
        assertEquals(Integer.valueOf(2), i.value());
        assertEquals(Integer.valueOf(1), one.first().apply());
        assertEquals(Integer.valueOf(3), i.value());

        assertEquals(Sequence.emptySequence(), one.rest());
        assertEquals(Sequence.emptySequence(), zero);
        assertEquals(Sequence.emptySequence(), zero.rest());

        assertEquals(Integer.valueOf(3), i.value());
        assertTrue(Sequence.Empty.SEQUENCE == zero.rest());
        assertEquals(Integer.valueOf(3), i.value());

        assertTrue(Sequence.Empty.SEQUENCE == zero.rest());
        i.set(999);

        three.rest().rest().rest().rest().rest().rest().rest().rest().rest().rest().rest().rest().rest().rest().rest();
        assertEquals(Integer.valueOf(3), three.first().apply());
        assertEquals(Integer.valueOf(1000), i.value());

        three.first(); three.first(); three.first();
        assertEquals(Integer.valueOf(3), three.first().apply());
        assertEquals(Integer.valueOf(1001), i.value());
    }

}
