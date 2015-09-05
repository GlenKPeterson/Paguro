// Copyright 2015-08-30 PlanBase Inc. & Glen Peterson
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

package org.organicdesign.fp.xform;

import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.organicdesign.fp.Mutable;
import org.organicdesign.fp.collections.PersistentVector;
import org.organicdesign.fp.collections.UnmodIterator;
import org.organicdesign.fp.function.Function1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.organicdesign.fp.StaticImports.vec;
import static org.organicdesign.fp.function.Function1.accept;
import static org.organicdesign.fp.function.Function1.reject;

@RunWith(JUnit4.class)
public class XformTest extends TestCase {

    public static void basics(Xform<Integer> td) {
        assertEquals(Arrays.asList(1, 2, 3),
                     td.foldLeft(new ArrayList<>(), (List<Integer> accum, Integer i) -> {
                         accum.add(i);
                         return accum;
                     }));
        assertEquals(Arrays.asList(2, 3, 4),
                     td.map(i -> i + 1)
                       .foldLeft(new ArrayList<>(), (List<Integer> accum, Integer i) -> {
                           accum.add(i);
                           return accum;
                       }));
        assertEquals(Arrays.asList(1, 3),
                     td.filter(i -> i != 2)
                       .foldLeft(new ArrayList<>(), (List<Integer> accum, Integer i) -> {
                           accum.add(i);
                           return accum;
                       }));
        assertEquals(Arrays.asList(1, 10, 100, 2, 20, 200, 3, 30, 300),
                     td.flatMap(i -> PersistentVector.of(i, i * 10, i * 100))
                       .foldLeft(new ArrayList<>(), (accum, i) -> {
                           accum.add(i);
                           return accum;
                       }));
        assertEquals(Arrays.asList(2, 3),
                     td.drop(1)
                       .foldLeft(new ArrayList<>(), (List<Integer> accum, Integer i) -> {
                           accum.add(i);
                           return accum;
                       }));
        assertEquals(Arrays.asList(1, 2, 3),
                     td.drop(0)
                       .foldLeft(new ArrayList<>(), (List<Integer> accum, Integer i) -> {
                           accum.add(i);
                           return accum;
                       }));
        assertEquals(Collections.emptyList(),
                     td.drop(99)
                       .foldLeft(new ArrayList<>(), (List<Integer> accum, Integer i) -> {
                           accum.add(i);
                           return accum;
                       }));
        assertEquals(Collections.emptyList(),
                     td.drop(Integer.MAX_VALUE)
                       .foldLeft(new ArrayList<>(), (List<Integer> accum, Integer i) -> {
                           accum.add(i);
                           return accum;
                       }));
        assertEquals(Arrays.asList(1, 2),
                     td.take(2)
                       .foldLeft(new ArrayList<>(), (List<Integer> accum, Integer i) -> {
                           accum.add(i);
                           return accum;
                       }));
        assertEquals(Collections.emptyList(),
                     td.take(0)
                       .foldLeft(new ArrayList<>(), (List<Integer> accum, Integer i) -> {
                           accum.add(i);
                           return accum;
                       }));
        assertEquals(Arrays.asList(1, 2, 3),
                     td.take(3)
                       .foldLeft(new ArrayList<>(), (List<Integer> accum, Integer i) -> {
                           accum.add(i);
                           return accum;
                       }));
        assertEquals(Arrays.asList(1, 2, 3),
                     td.take(99)
                       .foldLeft(new ArrayList<>(), (List<Integer> accum, Integer i) -> {
                           accum.add(i);
                           return accum;
                       }));
        assertEquals(Arrays.asList(1, 2, 3),
                     td.take(Integer.MAX_VALUE)
                       .foldLeft(new ArrayList<>(), (List<Integer> accum, Integer i) -> {
                           accum.add(i);
                           return accum;
                       }));
        assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6),
                     td.concat(Arrays.asList(4, 5, 6))
                       .foldLeft(new ArrayList<>(), (List<Integer> accum, Integer i) -> {
                           accum.add(i);
                           return accum;
                       }));
        assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6),
                     td.concat(Xform.ofArray(4, 5, 6).toImSortedSet((a, b) -> a - b))
                       .foldLeft(new ArrayList<>(), (List<Integer> accum, Integer i) -> {
                           accum.add(i);
            return accum;
        }));
        assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6),
                     td.concatArray(new Integer[]{4, 5, 6})
                       .foldLeft(new ArrayList<>(), (List<Integer> accum, Integer i) -> {
                           accum.add(i);
                           return accum;
                       }));
        assertEquals(Arrays.asList(2, 3, 4, 5, 6, 7),
                     td.concat(Arrays.asList(4, 5, 6))
                       .map(i -> i + 1)
                       .foldLeft(new ArrayList<>(), (List<Integer> accum, Integer i) -> {
                           accum.add(i);
                           return accum;
                       }));
    }

    @Test public void testBasics() {
        Integer[] src = new Integer[] {1, 2, 3};
        basics(Xform.of(Arrays.asList(src)));
        basics(Xform.of(Xform.ofArray(src).toImSortedSet((a, b) -> a - b)));
        basics(Xform.ofArray(src));
    }

    public static void longerCombinations(Xform<Integer> td) {
        assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9),
                     td.foldLeft(new ArrayList<>(), (List<Integer> accum, Integer i) -> {
                         accum.add(i);
                         return accum;
                     }));
        assertEquals(Arrays.asList(2, 4, 6, 8),
                     td.filter(i -> i % 2 == 0)
                       .foldLeft(new ArrayList<>(), (List<Integer> accum, Integer i) -> {
                           accum.add(i);
                           return accum;
                       }));
        assertEquals(Arrays.asList(1, 2, 3, 4, 5),
                     td.take(5)
                       .foldLeft(new ArrayList<>(), (List<Integer> accum, Integer i) -> {
                         accum.add(i);
                         return accum;
                     }));
        assertEquals(Arrays.asList(3, 5, 7, 9),
                     td.filter(i -> i % 2 == 0)
                       .map(i -> i + 1)
                       .foldLeft(new ArrayList<>(), (List<Integer> accum, Integer i) -> {
                           accum.add(i);
                           return accum;
                       }));
        assertEquals(Arrays.asList(3, 5, 7),
                     td.filter(i -> i % 2 == 0)
                       .map(i -> i + 1)
                       .take(3)
                       .foldLeft(new ArrayList<>(), (List<Integer> accum, Integer i) -> {
                           accum.add(i);
                           return accum;
                       }));
        assertEquals(Arrays.asList(3, 30, 300, 5, 50, 500, 7, 70, 700, 9, 90, 900),
                     td.filter(i -> i % 2 == 0)
                       .map(i -> i + 1)
                       .flatMap(i -> PersistentVector.of(i, i * 10, i * 100))
                       .foldLeft(new ArrayList<>(), (List<Integer> accum, Integer i) -> {
                           accum.add(i);
                           return accum;
                       }));
        assertEquals(Arrays.asList(5, 50, 500, 7, 70, 700, 9, 90, 900),
                     td.drop(2)
                       .filter(i -> i % 2 == 0)
                       .map(i -> i + 1)
                       .flatMap(i -> PersistentVector.of(i, i * 10, i * 100))
                       .foldLeft(new ArrayList<>(), (List<Integer> accum, Integer i) -> {
                           accum.add(i);
                           return accum;
                       }));
//        System.out.println("Testing separate drop.");
        assertEquals(Arrays.asList(7, 9),
                     td.filter(i -> i % 2 == 0)
                       .map(i -> i + 1)
                       .drop(2)
                       .foldLeft(new ArrayList<>(), (List<Integer> accum, Integer i) -> {
                           accum.add(i);
                           return accum;
                       }));
//        System.out.println("Done testing separate drop.");
        assertEquals(Arrays.asList(7, 70, 700, 9, 90, 900),
                     td.filter(i -> i % 2 == 0)
                       .map(i -> i + 1)
                       .drop(2)
                       .flatMap(i -> PersistentVector.of(i, i * 10, i * 100))
                       .foldLeft(new ArrayList<>(), (List<Integer> accum, Integer i) -> {
                           accum.add(i);
                           return accum;
                       }));
        assertEquals(Arrays.asList(500, 7, 70, 700, 9, 90, 900),
                     td.filter(i -> i % 2 == 0)
                       .map(i -> i + 1)
                       .flatMap(i -> vec(i, i * 10, i * 100))
                       .drop(5)
                       .foldLeft(new ArrayList<>(), (List<Integer> accum, Integer i) -> {
                           accum.add(i);
                           return accum;
                       }));
        assertEquals(Arrays.asList(500, 7, 70, 700, 9, 90),
                     td.filter(i -> i % 2 == 0)
                       .map(i -> i + 1)
                       .flatMap(i -> PersistentVector.of(i, i * 10, i * 100))
                       .drop(5)
                       .take(6)
                       .foldLeft(new ArrayList<>(), (List<Integer> accum, Integer i) -> {
                           accum.add(i);
                           return accum;
                       }));
        assertEquals(Arrays.asList(500, 7, 70, 700, 9, 90, 91, 92, 93),
                     td.filter(i -> i % 2 == 0)
                       .map(i -> i + 1)
                       .flatMap(i -> PersistentVector.of(i, i * 10, i * 100))
                       .drop(5)
                       .take(6)
                       .concat(Arrays.asList(91, 92, 93))
                       .foldLeft(new ArrayList<>(), (List<Integer> accum, Integer i) -> {
                           accum.add(i);
                           return accum;
                       }));
    }

    @Test public void longerCombinations() {
        Integer[] src = new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        longerCombinations(Xform.of(Arrays.asList(src)));
        longerCombinations(Xform.of(Xform.ofArray(src).toImSortedSet((a, b) -> a - b)));
        longerCombinations(Xform.ofArray(src));
    }

    @Test (expected = IllegalArgumentException.class)
    public void concatEx() {
        Xform.ofArray().concat(null);
    }
    @Test (expected = IllegalArgumentException.class)
    public void precatEx() {
        Xform.ofArray().precat(null);
    }

    @Test public void doubleNull() {
        assertArrayEquals(new Integer[0],
                          Xform.ofArray().concatArray().toArray());
        assertArrayEquals(new Integer[0],
                          Xform.ofArray().precatArray().toArray());
    }

    @Test public void precat() {
        assertArrayEquals(new Integer[] { 5, 6, 7, 8, 9 },
                          Xform.ofArray(5, 6, 7, 8, 9)
                                  .precatArray().toArray());
        assertArrayEquals(new Integer[]{4, 5, 6, 7, 8, 9},
                          Xform.ofArray(5, 6, 7, 8, 9)
                                  .precat(Xform.ofArray(4)).toArray());
        assertArrayEquals(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9},
                          Xform.ofArray(5, 6, 7, 8, 9)
                                  .precat(Xform.ofArray(1, 2, 3, 4)).toArray());
    }

    @Test
    public void concat() {
        assertArrayEquals(new Integer[] { 1, 2, 3, 4 },
                          Xform.ofArray(1, 2, 3, 4)
                                  .concatArray().toArray());

//        assertArrayEquals(new Integer[] { 1, 2, 3, 4 },
//                          Xform.ofArray(1, 2, 3, 4)
//                                  .concat(Xform.emptyXform()).toArray());

        assertArrayEquals(new Integer[]{1, 2, 3, 4, 5},
                          Xform.ofArray(1, 2, 3, 4)
                               .concat(Xform.ofArray(5)).toArray());

        assertArrayEquals(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9},
                          Xform.ofArray(1, 2, 3, 4)
                               .concat(Xform.ofArray(5, 6, 7, 8, 9)).toArray());
    }

    @Test
    public void chainedPrependAppend() {
        assertArrayEquals(new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 },
                          Xform.ofArray(5)                     //         5
                                  .precat(Xform.ofArray(4))   //       4,5
                                  .concat(Xform.ofArray(6))    //       4,5,6
                                  .precat(Xform.ofArray(2, 3)) //   2,3,4,5,6
                                  .concat(Xform.ofArray(7, 8))  //   2,3,4,5,6,7,8
                                  .precat(Xform.ofArray(1))   // 1,2,3,4,5,6,7,8
                                  .concat(Xform.ofArray(9))    // 1,2,3,4,5,6,7,8,9
                                  .toArray());

        assertArrayEquals(new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 },
                          Xform.ofArray(5)
                                  .precat(Xform.ofArray()).precat(Xform.ofArray())
                                  .concat(Xform.ofArray()).concat(Xform.ofArray())
                                  .precat(Xform.ofArray(4))
                                  .precat(Xform.ofArray()).precat(Xform.ofArray())
                                  .concat(Xform.ofArray()).concat(Xform.ofArray())
                                  .concat(Xform.ofArray(6))
                                  .precat(Xform.ofArray()).precat(Xform.ofArray())
                                  .concat(Xform.ofArray()).concat(Xform.ofArray())
                                  .precat(Xform.ofArray(2, 3))
                                  .precat(Xform.ofArray()).precat(Xform.ofArray())
                                  .concat(Xform.ofArray()).concat(Xform.ofArray())
                                  .concat(Xform.ofArray(7, 8))
                                  .precat(Xform.ofArray()).precat(Xform.ofArray())
                                  .concat(Xform.ofArray()).concat(Xform.ofArray())
                                  .precat(Xform.ofArray(1))
                                  .precat(Xform.ofArray()).precat(Xform.ofArray())
                                  .concat(Xform.ofArray()).concat(Xform.ofArray())
                                  .concat(Xform.ofArray(9))
                                  .precat(Xform.ofArray()).precat(Xform.ofArray())
                                  .concat(Xform.ofArray()).concat(Xform.ofArray())
                                  .toArray());
    }

    @Test public void emptiness() {
        Xform<Integer> seq = Xform.ofArray();
        assertArrayEquals(new Integer[0], seq.drop(0).toArray());
        assertArrayEquals(new Integer[0], seq.drop(1).toArray());
        assertArrayEquals(new Integer[0], seq.drop(2).toArray());
    }

    @Test public void singleElement() {
        Xform<Integer> seq = Xform.ofArray(1);
        assertArrayEquals(new Integer[]{1}, seq.drop(0).toArray());
        assertArrayEquals(new Integer[0], seq.drop(1).toArray());
        assertArrayEquals(new Integer[0], seq.drop(2).toArray());
    }

    @Test public void twoElement() {
        Xform<Integer> seq = Xform.ofArray(1, 2);
        assertArrayEquals(new Integer[]{1, 2}, seq.drop(0).toArray());
        assertArrayEquals(new Integer[]{2}, seq.drop(1).toArray());
        assertArrayEquals(new Integer[0], seq.drop(2).toArray());
    }

    @Test
    public void singleDrops() {
        Integer[] ints = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9};
        Xform<Integer> xform = Xform.ofArray(ints);
        assertArrayEquals(ints, xform.drop(0).toArray());
        assertArrayEquals(new Integer[]{2, 3, 4, 5, 6, 7, 8, 9}, xform.drop(1).toArray());
        assertArrayEquals(new Integer[]{3, 4, 5, 6, 7, 8, 9}, xform.drop(2).toArray());
        assertArrayEquals(new Integer[]{4, 5, 6, 7, 8, 9}, xform.drop(3).toArray());
        assertArrayEquals(new Integer[]{9}, xform.drop(8).toArray());
        assertArrayEquals(new Integer[0], Xform.ofArray().toArray());
        assertArrayEquals(new Integer[0], xform.drop(9).toArray());
        assertArrayEquals(new Integer[0], xform.drop(10).toArray());
        assertArrayEquals(new Integer[0], xform.drop(10000).toArray());
        assertArrayEquals(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9},
                          Xform.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9).drop(0).toArray());
        assertArrayEquals(new Integer[]{2, 3, 4, 5, 6, 7, 8, 9},
                          Xform.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9).drop(1).toArray());
        assertArrayEquals(new Integer[]{3, 4, 5, 6, 7, 8, 9},
                          Xform.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9).drop(2).toArray());
        assertArrayEquals(new Integer[]{4, 5, 6, 7, 8, 9},
                          Xform.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9).drop(3).toArray());
        assertArrayEquals(new Integer[]{9},
                          Xform.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9).drop(8).toArray());
        assertArrayEquals(new Integer[]{},
                          Xform.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9).drop(9).toArray());
        assertArrayEquals(new Integer[]{},
                          Xform.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9).drop(10).toArray());
        assertArrayEquals(new Integer[]{},
                          Xform.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9).drop(10000).toArray());
    }

    @Test
    public void multiDrops() {
        Integer[] ints = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9};
        Xform<Integer> seq = Xform.ofArray(ints);
        assertArrayEquals(new Integer[] {3,4,5,6,7,8,9},
                seq.drop(1).drop(1).toArray());
        assertArrayEquals(new Integer[] {4,5,6,7,8,9},
                seq.drop(1).drop(1).drop(1).toArray());
        assertArrayEquals(new Integer[] {9},
                seq
                                  .drop(1).drop(1).drop(1).drop(1).drop(1)
                                  .drop(1).drop(1).drop(1).toArray());
        assertArrayEquals(new Integer[] {},
                seq
                                  .drop(1).drop(1).drop(1).drop(1).drop(1)
                                  .drop(1).drop(1).drop(1).drop(1).toArray());
        assertArrayEquals(new Integer[] {},
                seq
                                  .drop(1).drop(1).drop(1).drop(1).drop(1)
                                  .drop(1).drop(1).drop(1).drop(1).drop(1).toArray());
        assertArrayEquals(new Integer[] {},
                seq
                                  .drop(1).drop(1).drop(1).drop(1).drop(1)
                                  .drop(1).drop(1).drop(1).drop(1).drop(1)
                                  .drop(1).drop(1).drop(1).drop(1).drop(1).toArray());

        assertArrayEquals(new Integer[] {7,8,9},
                seq
                                  .drop(0).drop(1).drop(2).drop(3).toArray());
        assertArrayEquals(new Integer[] {7,8,9},
                seq
                                  .drop(3).drop(2).drop(1).drop(0).toArray());

        assertArrayEquals(new Integer[] {3,4,5,6,7,8,9},
                          Xform.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9).drop(1).drop(1).toArray());
        assertArrayEquals(new Integer[] {4,5,6,7,8,9},
                          Xform.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9).drop(1).drop(1).drop(1).toArray());
        assertArrayEquals(new Integer[] {9},
                          Xform.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9)
                                  .drop(1).drop(1).drop(1).drop(1).drop(1)
                                  .drop(1).drop(1).drop(1).toArray());
        assertArrayEquals(new Integer[] {},
                          Xform.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9)
                                  .drop(1).drop(1).drop(1).drop(1).drop(1)
                                  .drop(1).drop(1).drop(1).drop(1).toArray());
        assertArrayEquals(new Integer[] {},
                          Xform.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9)
                                  .drop(1).drop(1).drop(1).drop(1).drop(1)
                                  .drop(1).drop(1).drop(1).drop(1).drop(1).toArray());
        assertArrayEquals(new Integer[]{},
                          Xform.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9)
                              .drop(1).drop(1).drop(1).drop(1).drop(1)
                              .drop(1).drop(1).drop(1).drop(1).drop(1)
                              .drop(1).drop(1).drop(1).drop(1).drop(1).toArray());

        assertArrayEquals(new Integer[]{7, 8, 9},
                          Xform.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9)
                              .drop(0).drop(1).drop(2).drop(3).toArray());
        assertArrayEquals(new Integer[]{7, 8, 9},
                          Xform.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9)
                               .drop(3).drop(2).drop(1).drop(0).toArray());
    }

    @Test(expected = IllegalArgumentException.class)
    public void exception1() {
        Xform.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9).drop(-1);
    }
    @Test(expected = IllegalArgumentException.class)
    public void exception2() {
        Xform.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9).drop(-99);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullException() {
        assertArrayEquals(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9},
                          Xform.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9).filter(null).toArray());
    }

    @Test
    public void singleFilter() {
        assertArrayEquals(new Integer[] {1, 2, 3, 4, 5, 6, 7, 8, 9},
                          Xform.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9).filter(accept()).toArray());

        assertArrayEquals(new Integer[] {},
                          Xform.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9).filter(reject()).toArray());

        assertArrayEquals(new Integer[] {5,6,7,8,9},
                          Xform.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9).filter(i -> i > 4).toArray());

        assertArrayEquals(new Integer[] {},
                          Xform.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9).filter(i -> i < 1).toArray());

        assertArrayEquals(new Integer[] {3},
                          Xform.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9).filter(i -> i == 3).toArray());

        assertArrayEquals(new Integer[] {1},
                          Xform.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9).filter(i -> i == 1).toArray());

        assertArrayEquals(new Integer[] {9},
                          Xform.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9).filter(i -> i == 9).toArray());

        assertArrayEquals(new Integer[]{1, 2, 3, 4, 5, 6},
                          Xform.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9).filter(i -> i < 7).toArray());

    }

    @Test
    public void chainedFilters() {
        assertArrayEquals(new Integer[] {1, 2, 3, 4, 5, 6, 7, 8, 9},
                          Xform.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9)
                                  .filter(accept()).filter(accept()).filter(accept())
                                  .filter(accept()).filter(accept()).filter(accept())
                                  .filter(accept()).filter(accept()).toArray());

        assertArrayEquals(new Integer[] {},
                          Xform.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9)
                                  .filter(reject()).filter(accept()).filter(accept())
                                  .filter(accept()).filter(accept()).filter(accept())
                                  .filter(accept()).filter(accept()).toArray());

        assertArrayEquals(new Integer[] {},
                          Xform.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9)
                                  .filter(accept()).filter(accept()).filter(accept())
                                  .filter(accept()).filter(reject()).filter(accept())
                                  .filter(accept()).filter(accept()).toArray());

        assertArrayEquals(new Integer[] {},
                          Xform.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9)
                                  .filter(accept()).filter(accept()).filter(accept())
                                  .filter(accept()).filter(accept()).filter(accept())
                                  .filter(accept()).filter(reject()).toArray());

        assertArrayEquals(new Integer[] {3, 4, 6},
                          Xform.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9)
                                  .filter(i -> i > 2)
                                  .filter(i -> i < 7)
                                  .filter(i -> i != 5).toArray());

    }

    @Test public void singleFlatMap() {
        assertEquals(0, Xform.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9)
                             .flatMap((i) -> () -> UnmodIterator.empty())
                             .toImList()
                             .size());

        assertEquals(0, Xform.ofArray()
                             .flatMap((i) -> () -> UnmodIterator.empty())
                             .toImList()
                             .size());

        assertArrayEquals(new Integer[] { 1,2,3, 2,4,6, 3,6,9, 4,8,12, 5,10,15, 6,12,18,
                                          7,14,21, 8,16,24, 9,18,27},
                          Xform.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9)
                              .flatMap(i -> Xform.ofArray(i, i * 2, i * 3)).toArray());

        assertArrayEquals(new String[] { "1","2", "2","3", "3","4"},
                          Xform.ofArray(1, 2, 3)
                              .flatMap(i -> Xform.ofArray(String.valueOf(i),
                                                          String.valueOf(i + 1))).toArray());
    }

    @Test public void flatEmpty() {
        assertEquals(0, Xform.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9)
                             .flatMap((a) -> () -> UnmodIterator.empty())
                             .toImList()
                             .size());

        assertEquals(0, Xform.ofArray()
                             .flatMap((a) -> Xform.ofArray())
                             .toImList()
                             .size());

        // This tests that I didn't just look ahead 2 or 3 times.  That the look-ahead is sufficient.
        Mutable.Ref<Integer> count = Mutable.Ref.of(0);
        assertArrayEquals(new String[]{"a9", "b9"},
                          Xform.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9)
                                  .flatMap((a) -> {
                                      count.set(count.value() + 1);
                                      return (count.value() > 8)
                                             ? Xform.ofArray("a" + a, "b" + a)
                                             : Xform.ofArray();
                                  }).toArray());

        count.set(0);
        assertArrayEquals(new String[]{"c8", "d8", "c9", "d9"},
                          Xform.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9)
                                  .flatMap((a) -> {
                                      count.set(count.value() + 1);
                                      return (count.value() > 7)
                                             ? Xform.ofArray("c" + a, "d" + a)
                                             : Xform.ofArray();
                                  })
//                                  .forEach((item) -> {
//                              System.out.println("Item " + item);
//                              return null;
//                          })
                                  .toArray());

        count.set(0);
        assertArrayEquals(new String[]{"e1", "f1", "e2", "f2"},
                          Xform.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9)
                                  .flatMap((a) -> {
                                      count.set(count.value() + 1);
                                      return (count.value() < 3)
                                             ? Xform.ofArray("e" + a, "f" + a)
                                             : Xform.ofArray();
                                  })
//                                  .forEach((item) -> {
//                              System.out.println("count: " + count.value() + " Item " + item);
//                              return null;
//                          })
                                  .toArray());

        Mutable.Ref<Xform<Integer>> shrinkSeq = Mutable.Ref.of(Xform.ofArray(1, 2, 3));
        assertArrayEquals(new Integer[]{2, 3, 3},
                          Xform.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9)
                                  .flatMap((a) -> {
                                      shrinkSeq.set(shrinkSeq.value().drop(1));
//                                      System.out.print("seq val: " + shrinkSeq.value());
                                      return shrinkSeq.value();
                                  })
                                  .toArray());

        // Now start by returning an ofArray, then a seq of length 1, then length 2, etc.
        // The first ofArray should not end the processing.
        Mutable.Ref<Xform<Integer>> growSeq = Mutable.Ref.of(Xform.ofArray());
        Mutable.Ref<Integer> incInt = Mutable.Ref.of(0);
        assertArrayEquals(new Integer[]{1, 1,2},
                          Xform.ofArray(1, 2, 3)
                                  .flatMap((a) -> {
                                      if (incInt.value() > 0) {
                                          growSeq.set(growSeq.value().concat(Xform.ofArray(incInt.value())));
                                      }
                                      incInt.set(incInt.value() + 1);
                                      return growSeq.value();
                                  })
                                  .toArray());

    }

    @Test public void flatMapChain() {
        assertEquals(0, Xform.of(vec(1, 2, 3, 4, 5, 6, 7, 8, 9))
                             .flatMap((i) -> () -> UnmodIterator.empty())
                             .flatMap((i) -> () -> UnmodIterator.empty())
                             .flatMap((i) -> () -> UnmodIterator.empty())
                             .toImList()
                             .size());

        assertEquals(0, Xform.ofArray()
                             .flatMap((i) -> Xform.ofArray(i))
                             .flatMap((i) -> Xform.ofArray(i))
                             .flatMap((i) -> Xform.ofArray(i))
                             .toImList()
                             .size());

        assertArrayEquals(new Integer[] {},
                          Xform.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9)
                               .flatMap((i) -> () -> UnmodIterator.empty())
                               .flatMap((i) -> () -> UnmodIterator.empty())
                               .flatMap((i) -> () -> UnmodIterator.empty()).toArray());

        assertArrayEquals(new Integer[] { 1,2, 2,3, 3,4, 10,11, 20,21, 30,31},
                          Xform.ofArray(1, 10)
                                  .flatMap(i -> Xform.ofArray(i, i * 2, i * 3))
                                  .flatMap(i -> Xform.ofArray(i, i + 1))
                                  .toArray());
    }

    // Below here taken from SequenceTest
    @Test public void construction() {
        Integer[] ints = new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        assertArrayEquals(ints, Xform.ofArray(ints).toArray());
        assertArrayEquals(ints, Xform.of(Arrays.asList(ints)).toArray());
    }

//    @Test public void emptyXform() {
//        assertEquals(0, Xform.EMPTY_SEQUENCE.hashCode());
//        assertEquals(0, Xform.EMPTY_SEQUENCE.tail().hashCode());
//        assertEquals(0, Xform.EMPTY_SEQUENCE.tail().tail().tail().hashCode());
//
//        assertEquals(Option.none(), Xform.EMPTY_SEQUENCE.head());
//
//        assertEquals(Xform.EMPTY_SEQUENCE, Xform.EMPTY_SEQUENCE);
//        assertEquals(Xform.EMPTY_SEQUENCE, Xform.EMPTY_SEQUENCE.tail());
//        assertEquals(Xform.EMPTY_SEQUENCE, Xform.EMPTY_SEQUENCE.tail().tail());
//        assertTrue(Xform.EMPTY_SEQUENCE.equals(Xform.EMPTY_SEQUENCE.tail()));
//        assertTrue(Xform.EMPTY_SEQUENCE.tail().equals(Xform.EMPTY_SEQUENCE));
//    }

    @Test public void foldLeft() {
        Integer[] ints = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9};

        assertEquals(Integer.valueOf(45),
                     Xform.ofArray(ints).foldLeft(0, (accum, i) -> accum + i));
    }

    @Test(expected = IllegalArgumentException.class)
    public void foldLeftEx() {
        assertEquals(Integer.valueOf(45),
                     Xform.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9)
                          .foldLeft(0, null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void foldLeftEx2() {
        assertEquals(Integer.valueOf(45),
                     Xform.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9)
                          .foldLeft(0, null, Function1.reject()));
    }

    @Test public void foldLeftTerm() {
        Integer[] ints = new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 };

        assertEquals(Integer.valueOf(45),
                     Xform.ofArray(ints)
                          .foldLeft(0, (accum, i) -> accum + i, Function1.reject()));

        assertArrayEquals(new Integer[]{2, 3, 4},
                          Xform.ofArray(ints)
                               .foldLeft(new ArrayList<>(),
                                         (accum, i) -> {
                                             accum.add(i + 1);
                                             return accum;
                                         },
                                         (accum) -> accum.size() == 3).toArray());
        assertArrayEquals(new Integer[]{2, 3, 4, 5, 6, 7, 8, 9, 10},
                          Xform.ofArray(ints)
                                  .foldLeft(new ArrayList<>(),
                                            (accum, i) -> {
                                                accum.add(i + 1);
                                                return accum;
                                            },
                                            (accum) -> accum.size() == 20).toArray());

        // This is fun and it should work.  But it really sets up for the early-termination test
        // next.
        assertEquals(Arrays.asList(1, 2, 3, 2, 4, 6, 3, 6, 9, 4, 8, 12, 5, 10, 15, 6, 12, 18,
                                   7, 14, 21, 8, 16, 24, 9, 18, 27),
                     Xform.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9)
                          .flatMap(i -> Xform.ofArray(i, i * 2, i * 3))
                          .foldLeft(new ArrayList<>(),
                                    (alist, item) -> {
                                        alist.add(item);
                                        return alist;
                                    }));

        // Early termination test
        assertEquals(Arrays.asList(1,2,3, 2,4,6, 3,6,9, 4,8,12, 5,10,15, 6,12,18,
                                   7,14,21, 8,16),
                     Xform.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9)
                          .flatMap(i -> Xform.ofArray(i, i * 2, i * 3))
                          .foldLeft(new ArrayList<>(),
                                    (alist, item) -> { alist.add(item); return alist; },
                                    (items) -> items.contains(16)));
    }

    @Test public void toIterator() {
        Integer[] ints = new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        Iterator<Integer> seqIter = Xform.ofArray(ints).iterator();
        Iterator<Integer> listIter = Arrays.asList(ints).iterator();
        while (seqIter.hasNext() && listIter.hasNext()) {
            assertEquals(seqIter.next(), listIter.next());
        }
        assertFalse(seqIter.hasNext());
        assertFalse(listIter.hasNext());
    }

//    @Test public void objectMethods() {
//        Integer[] ints = new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
//        Xform<Integer> seq1 = Xform.ofArray(ints).drop(3).take(4);
//        Xform<Integer> seq2 = Xform.ofArray(4, 5, 6, 7);
//
//        assertEquals(UnmodIterable.hashCode(seq1), UnmodIterable.hashCode(seq2));
//
//        assertTrue(UnmodSortedIterable.equals(seq1, seq1));
//        assertTrue(UnmodSortedIterable.equals(seq2, seq2));
//
//        assertTrue(UnmodSortedIterable.equals(seq1, seq2));
//        assertTrue(UnmodSortedIterable.equals(seq2, seq1));
//
//        assertEquals(UnmodIterable.hashCode(seq1.tail()), UnmodIterable.hashCode(seq2.tail()));
//        assertTrue(UnmodSortedIterable.equals(seq1.tail(), seq2.tail()));
//        assertTrue(UnmodSortedIterable.equals(seq2.tail(), seq1.tail()));
//
//        assertNotEquals(UnmodIterable.hashCode(seq1.tail()), UnmodIterable.hashCode(seq2));
//        assertNotEquals(UnmodIterable.hashCode(seq1), UnmodIterable.hashCode(seq2.tail()));
//        assertFalse(UnmodSortedIterable.equals(seq1.tail(), seq2));
//        assertFalse(UnmodSortedIterable.equals(seq1, seq2.tail()));
//
//        assertFalse(UnmodSortedIterable.equals(seq2.tail(), seq1));
//        assertFalse(UnmodSortedIterable.equals(seq2, seq1.tail()));
//    }

    /**
     Note that this tests the forEach() implementation on java.util.Iterable.  There used to be a
     forEach() on Transformable too, but when I realized that it overloaded (but did not override)
     the same method on Iterable and Stream, *and* when I read Josh Bloch Item 41 "Use Overloading
     Judiciously" and realized what a bad idea it was, I just removed the method.  Maybe this test
     should be deleted?  I guess it's good to prove that the caching works.
     */
    @Test public void forEach() {
        Integer[] ints = new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        Xform<Integer> seq = Xform.ofArray(ints);
        final List<Integer> output = new ArrayList<>();
        seq.forEach(i -> output.add(i));
        assertArrayEquals(ints, output.toArray());
    }

    @Test
    public void firstMatching() {
        Integer[] ints = new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        Xform<Integer> seq = Xform.ofArray(ints);

        // TODO: We may want a head() method that returns an option because this gets ugly.
        assertEquals(Integer.valueOf(1), seq.filter(i -> i == 1).take(1).toImList().head().get());
        assertEquals(Integer.valueOf(3), seq.filter(i -> i > 2).take(1).toImList().head().get());
        assertFalse(seq.filter(i -> i > 10).take(1).toImList().head().isSome());
    }

    @Test
    public void takeAndDrop() {
        assertArrayEquals(Xform.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9)
                                  .drop(0).take(8888).toArray(),
                          new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9});
        assertArrayEquals(Xform.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9)
                                  .drop(1).take(1).toArray(),
                          new Integer[]{2});
        assertArrayEquals(Xform.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9)
                                  .drop(2).take(2).toArray(),
                   new Integer[] { 3,4 });
        assertArrayEquals(Xform.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9)
                                  .drop(3).take(3).toArray(),
                   new Integer[] { 4,5,6 });
        assertArrayEquals(Xform.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9)
                                  .drop(9999).take(3).toArray(),
                          new Integer[]{});
        assertArrayEquals(Xform.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9)
                                  .drop(3).take(0).toArray(),
                   new Integer[] { });
    }

    @Test public void chain1() {
        assertArrayEquals(Xform.ofArray(5)                      //         5
                                  .precat(Xform.ofArray(4))    //       4,5
                                  .concat(Xform.ofArray(6))     //       4,5,6
                                  .precat(Xform.ofArray(2, 3)) //   2,3,4,5,6
                                  .concat(Xform.ofArray(7, 8))  //   2,3,4,5,6,7,8
                                  .precat(Xform.ofArray(1))    // 1,2,3,4,5,6,7,8
                                  .concat(Xform.ofArray(9))     // 1,2,3,4,5,6,7,8,9
                                  .filter(i -> i > 3)              //       4,5,6,7,8,9
                                  .map(i -> i - 2)                 //   2,3,4,5,6,7
                                  .take(5)                         //   2,3,4,5,6
                                  .drop(2)                         //       4,5,6
                                  .toArray(),
                          new Integer[]{4, 5, 6});
    }
    // Above here taken from SequenceTest.
}