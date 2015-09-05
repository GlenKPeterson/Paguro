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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
                     td.concatList(Arrays.asList(4, 5, 6))
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
                     td.concatList(Arrays.asList(4, 5, 6))
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
                       .concatList(Arrays.asList(91, 92, 93))
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
        assertArrayEquals(new Integer[] {1}, seq.drop(0).toArray());
        assertArrayEquals(new Integer[0], seq.drop(1).toArray());
        assertArrayEquals(new Integer[0], seq.drop(2).toArray());
    }

    @Test public void twoElement() {
        Xform<Integer> seq = Xform.ofArray(1, 2);
        assertArrayEquals(new Integer[] {1,2}, seq.drop(0).toArray());
        assertArrayEquals(new Integer[] {2}, seq.drop(1).toArray());
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
     // TODO: Continue using unit tests from Sequence (those from below are from View and are likely not as good).


}