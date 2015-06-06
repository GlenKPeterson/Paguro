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

package org.organicdesign.fp.permanent;

import org.junit.Test;
import org.organicdesign.fp.Option;
import org.organicdesign.fp.collections.UnIterable;
import org.organicdesign.fp.collections.UnIterableOrdered;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;

public class SequenceTest {

    @Test public void construction() {
        Integer[] ints = new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        assertArrayEquals(ints, Sequence.of(ints).toTypedArray());
        assertArrayEquals(ints, Sequence.ofIter(Arrays.asList(ints)).toTypedArray());
    }

    @Test public void emptySequence() {
        assertEquals(0, Sequence.EMPTY_SEQUENCE.hashCode());
        assertEquals(0, Sequence.EMPTY_SEQUENCE.tail().hashCode());
        assertEquals(0, Sequence.EMPTY_SEQUENCE.tail().tail().tail().hashCode());

        assertEquals(Option.none(), Sequence.EMPTY_SEQUENCE.head());

        assertEquals(Sequence.EMPTY_SEQUENCE, Sequence.EMPTY_SEQUENCE);
        assertEquals(Sequence.EMPTY_SEQUENCE, Sequence.EMPTY_SEQUENCE.tail());
        assertEquals(Sequence.EMPTY_SEQUENCE, Sequence.EMPTY_SEQUENCE.tail().tail());
        assertTrue(Sequence.EMPTY_SEQUENCE.equals(Sequence.EMPTY_SEQUENCE.tail()));
        assertTrue(Sequence.EMPTY_SEQUENCE.tail().equals(Sequence.EMPTY_SEQUENCE));
    }

    @Test public void foldLeftTerm() {
        Integer[] ints = new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        assertArrayEquals(new Integer[]{2, 3, 4},
                          Sequence.of(ints)
                                  .foldLeft(new ArrayList<>(),
                                            (accum, i) -> {
                                                accum.add(i + 1);
                                                return accum;
                                            },
                                            (accum) -> accum.size() == 3).toArray());
        assertArrayEquals(new Integer[]{2, 3, 4, 5, 6, 7, 8, 9, 10},
                          Sequence.of(ints)
                                  .foldLeft(new ArrayList<>(),
                                            (accum, i) -> {
                                                accum.add(i + 1);
                                                return accum;
                                            },
                                            (accum) -> accum.size() == 20).toArray());
    }

    @Test public void toIterator() {
        Integer[] ints = new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        Iterator<Integer> seqIter = Sequence.of(ints).iterator();
        Iterator<Integer> listIter = Arrays.asList(ints).iterator();
        while (seqIter.hasNext() && listIter.hasNext()) {
            assertEquals(seqIter.next(), listIter.next());
        }
        assertFalse(seqIter.hasNext());
        assertFalse(listIter.hasNext());
    }

    @Test public void objectMethods() {
        Integer[] ints = new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        Sequence<Integer> seq1 = Sequence.of(ints).drop(3).take(4);
        Sequence<Integer> seq2 = Sequence.of(4, 5, 6, 7);

        assertEquals(UnIterable.hashCode(seq1), UnIterable.hashCode(seq2));

        assertTrue(UnIterableOrdered.equals(seq1, seq1));
        assertTrue(UnIterableOrdered.equals(seq2, seq2));

        assertTrue(UnIterableOrdered.equals(seq1, seq2));
        assertTrue(UnIterableOrdered.equals(seq2, seq1));

        assertEquals(UnIterable.hashCode(seq1.tail()), UnIterable.hashCode(seq2.tail()));
        assertTrue(UnIterableOrdered.equals(seq1.tail(), seq2.tail()));
        assertTrue(UnIterableOrdered.equals(seq2.tail(), seq1.tail()));

        assertNotEquals(UnIterable.hashCode(seq1.tail()), UnIterable.hashCode(seq2));
        assertNotEquals(UnIterable.hashCode(seq1), UnIterable.hashCode(seq2.tail()));
        assertFalse(UnIterableOrdered.equals(seq1.tail(), seq2));
        assertFalse(UnIterableOrdered.equals(seq1, seq2.tail()));

        assertFalse(UnIterableOrdered.equals(seq2.tail(), seq1));
        assertFalse(UnIterableOrdered.equals(seq2, seq1.tail()));
    }

    @Test public void forEach() {
        Integer[] ints = new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        Sequence<Integer> seq = Sequence.of(ints);
        final List<Integer> output = new ArrayList<>();
        seq.forEach(i -> output.add(i));
        assertArrayEquals(ints, output.toArray());
    }

    @Test
    public void firstMatching() {
        Integer[] ints = new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        Sequence<Integer> seq = Sequence.of(ints);

        assertEquals(Integer.valueOf(1), seq.filter(i -> i == 1).head().get());
        assertEquals(Integer.valueOf(3), seq.filter(i -> i > 2).head().get());
        assertFalse(seq.filter(i -> i > 10).head().isSome());
    }

    @Test
    public void takeAndDrop() {
        assertArrayEquals(Sequence.of(1, 2, 3, 4, 5, 6, 7, 8, 9)
                                  .drop(0).take(8888).toTypedArray(),
                          new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 });
        assertArrayEquals(Sequence.of(1, 2, 3, 4, 5, 6, 7, 8, 9)
                                  .drop(1).take(1).toTypedArray(),
                   new Integer[] { 2 });
        assertArrayEquals(Sequence.of(1, 2, 3, 4, 5, 6, 7, 8, 9)
                                  .drop(2).take(2).toTypedArray(),
                   new Integer[] { 3,4 });
        assertArrayEquals(Sequence.of(1, 2, 3, 4, 5, 6, 7, 8, 9)
                                  .drop(3).take(3).toTypedArray(),
                   new Integer[] { 4,5,6 });
        assertArrayEquals(Sequence.of(1, 2, 3, 4, 5, 6, 7, 8, 9)
                                  .drop(9999).take(3).toTypedArray(),
                          new Integer[]{});
        assertArrayEquals(Sequence.of(1, 2, 3, 4, 5, 6, 7, 8, 9)
                                  .drop(3).take(0).toTypedArray(),
                   new Integer[] { });
    }

    @Test public void chain1() {
        assertArrayEquals(Sequence.of(5)                      //         5
                                  .precat(Sequence.of(4))    //       4,5
                                  .concat(Sequence.of(6))     //       4,5,6
                                  .precat(Sequence.of(2, 3)) //   2,3,4,5,6
                                  .concat(Sequence.of(7, 8))  //   2,3,4,5,6,7,8
                                  .precat(Sequence.of(1))    // 1,2,3,4,5,6,7,8
                                  .concat(Sequence.of(9))     // 1,2,3,4,5,6,7,8,9
                                  .filter(i -> i > 3)              //       4,5,6,7,8,9
                                  .map(i -> i - 2)                 //   2,3,4,5,6,7
                                  .take(5)                         //   2,3,4,5,6
                                  .drop(2)                         //       4,5,6
                                  .toTypedArray(),
                          new Integer[]{4, 5, 6});
    }
}
