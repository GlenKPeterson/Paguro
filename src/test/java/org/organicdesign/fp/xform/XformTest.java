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
import org.organicdesign.fp.collections.PersistentVector;

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

        assertArrayEquals(new Integer[] { 1, 2, 3, 4, 5 },
                          Xform.ofArray(1, 2, 3, 4)
                                  .concat(Xform.ofArray(5)).toArray());

        assertArrayEquals(new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 },
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

    // TODO: Continue using unit tests from Sequence (those from below are from View and are likely not as good).


    @Test
    public void singleDrops() {
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

    @Test(expected = IllegalArgumentException.class)
    public void exception1() {
        Xform.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9).drop(-1);
    }
    @Test(expected = IllegalArgumentException.class)
    public void exception2() {
        Xform.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9).drop(-99);
    }

    @Test
    public void multiDrops() {
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

    @Test
    public void singleFlatMap() {
//        assertEquals(Xform.EMPTY_VIEW,
//                     Xform.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9).flatMap(null));
//
//        assertEquals(Xform.EMPTY_VIEW,
//                     Xform.EMPTY_VIEW.flatMap(null));

//        assertArrayEquals(new Integer[] {},
//                          Xform.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9)
//                              .flatMap(null).toArray());

        assertArrayEquals(new Integer[] { 1,2,3, 2,4,6, 3,6,9, 4,8,12, 5,10,15, 6,12,18,
                                          7,14,21, 8,16,24, 9,18,27},
                          Xform.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9)
                              .flatMap(i -> Xform.ofArray(i, i * 2, i * 3)).toArray());

        assertArrayEquals(new String[] { "1","2", "2","3", "3","4"},
                          Xform.ofArray(1, 2, 3)
                              .flatMap(i -> Xform.ofArray(String.valueOf(i),
                                                         String.valueOf(i + 1))).toArray());

    }

    @Test
    public void flatMapChain() {
//        assertEquals(Xform.EMPTY_VIEW,
//                     Xform.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9)
//                         .flatMap(null).flatMap(null).flatMap(null));
//
//        assertEquals(Xform.EMPTY_VIEW,
//                     Xform.EMPTY_VIEW.flatMap(null).flatMap(null).flatMap(null));

//        assertArrayEquals(new Integer[] {},
//                          Xform.ofArray(1, 2, 3, 4, 5, 6, 7, 8, 9)
//                                  .flatMap(null).flatMap(null).flatMap(null).toArray());

        assertArrayEquals(new Integer[] { 1,2, 2,3, 3,4, 10,11, 20,21, 30,31},
                          Xform.ofArray(1, 10)
                                  .flatMap(i -> Xform.ofArray(i, i * 2, i * 3))
                                  .flatMap(i -> Xform.ofArray(i, i + 1))
                                  .toArray());
    }

}