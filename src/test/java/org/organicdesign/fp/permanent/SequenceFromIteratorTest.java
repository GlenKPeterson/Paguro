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
                     Sequence.ofIter(ints).toJavaList());
        assertArrayEquals(ints.toArray(),
                          Sequence.ofIter(ints).toTypedArray());
    }


    @Test
    public void construction() {
        List<Integer> ints = Arrays.asList(5, 4, 3, 2, 1);
        Sequence<Integer> five = Sequence.ofIter(ints);
        Sequence<Integer> four = five.tail();
        Sequence<Integer> three = four.tail();
        Sequence<Integer> two = three.tail();
        Sequence<Integer> one = two.tail();
        Sequence<Integer> zero = one.tail();
        Sequence<Integer> nada = zero.tail();
        assertEquals(Integer.valueOf(5), five.head().get());
        assertEquals(Integer.valueOf(4), four.head().get());
        assertEquals(Integer.valueOf(3), three.head().get());
        assertEquals(Integer.valueOf(2), two.head().get());
        assertEquals(Integer.valueOf(1), one.head().get());
        assertEquals(Sequence.EMPTY_SEQUENCE, one.tail());
        assertEquals(Sequence.EMPTY_SEQUENCE, zero);
        assertEquals(Sequence.EMPTY_SEQUENCE, zero.tail());

        assertEquals(Sequence.emptySequence(), nada);
        assertEquals(Sequence.emptySequence(), nada.tail());

        three.tail().tail().tail().tail().tail().tail().tail().tail().tail().tail().tail().tail().tail().tail().tail();
        assertEquals(Integer.valueOf(3), three.head().get());
    }

    @Test
    public void blankIterator() {
        Sequence<Integer> zero = Sequence.ofIter(Collections.<Integer>emptyList());
        assertEquals(Sequence.emptySequence(), zero);
    }


    @Test public void singleInit() {
        final Mutable.Ref<Integer> i = Mutable.Ref.of(0);
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
        Sequence<Function0<Integer>> three = Sequence.ofIter(ints);
        Sequence<Function0<Integer>> two = three.tail();
        Sequence<Function0<Integer>> one = two.tail();
        Sequence<Function0<Integer>> zero = one.tail();
        assertEquals(Integer.valueOf(3), three.head().get().apply());
        assertEquals(Integer.valueOf(1), i.value());
        assertEquals(Integer.valueOf(2), two.head().get().apply());
        assertEquals(Integer.valueOf(2), i.value());
        assertEquals(Integer.valueOf(1), one.head().get().apply());
        assertEquals(Integer.valueOf(3), i.value());

        assertEquals(Sequence.<Function0<Integer>>emptySequence(), one.tail());
        assertFalse(zero.head().isSome());
        assertEquals(Sequence.<Function0<Integer>>emptySequence(), zero.tail());

        assertEquals(Integer.valueOf(3), i.value());
        assertEquals(Sequence.<Function0<Integer>>emptySequence(), one.tail());
        assertEquals(Integer.valueOf(3), i.value());

        assertEquals(Sequence.<Function0<Integer>>emptySequence(), one.tail());
        i.set(999);

        three.tail().tail().tail().tail().tail().tail().tail().tail().tail().tail().tail().tail().tail().tail().tail();
        assertEquals(Integer.valueOf(3), three.head().get().apply());
        assertEquals(Integer.valueOf(1000), i.value());

        three.head().get(); three.head().get(); three.head().get();
        assertEquals(Integer.valueOf(3), three.head().get().apply());
        assertEquals(Integer.valueOf(1001), i.value());
    }

}
