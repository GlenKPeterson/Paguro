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
import org.organicdesign.fp.function.Fn1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertArrayEquals;
import static org.organicdesign.fp.FunctionUtils.emptyUnmodIterator;
import static org.organicdesign.fp.StaticImports.vec;
import static org.organicdesign.fp.StaticImports.xform;
import static org.organicdesign.fp.function.Fn1.accept;
import static org.organicdesign.fp.function.Fn1.reject;
import static org.organicdesign.testUtils.EqualsContract.equalsDistinctHashCode;

@RunWith(JUnit4.class)
public class XformTest extends TestCase {

    public static void basics(Xform<Integer> td) {
        assertEquals(Arrays.asList(1, 2, 3),
                     td.fold(new ArrayList<>(), (List<Integer> accum, Integer i) -> {
                         accum.add(i);
                         return accum;
                     }));
        assertEquals(Arrays.asList(2, 3, 4),
                     td.map(i -> i + 1)
                       .fold(new ArrayList<>(), (List<Integer> accum, Integer i) -> {
                           accum.add(i);
                           return accum;
                       }));
        assertEquals(Arrays.asList(1, 3),
                     td.filter(i -> i != 2)
                       .fold(new ArrayList<>(), (List<Integer> accum, Integer i) -> {
                           accum.add(i);
                           return accum;
                       }));
        assertEquals(Arrays.asList(1, 10, 100, 2, 20, 200, 3, 30, 300),
                     td.flatMap(i -> vec(i, i * 10, i * 100))
                       .fold(new ArrayList<>(), (accum, i) -> {
                           accum.add(i);
                           return accum;
                       }));
        assertEquals(Arrays.asList(2, 3),
                     td.drop(1)
                       .fold(new ArrayList<>(), (List<Integer> accum, Integer i) -> {
                           accum.add(i);
                           return accum;
                       }));
        assertEquals(Arrays.asList(1, 2, 3),
                     td.drop(0)
                       .fold(new ArrayList<>(), (List<Integer> accum, Integer i) -> {
                           accum.add(i);
                           return accum;
                       }));
        assertEquals(Collections.emptyList(),
                     td.drop(99)
                       .fold(new ArrayList<>(), (List<Integer> accum, Integer i) -> {
                           accum.add(i);
                           return accum;
                       }));
        assertEquals(Collections.emptyList(),
                     td.drop(Integer.MAX_VALUE)
                       .fold(new ArrayList<>(), (List<Integer> accum, Integer i) -> {
                           accum.add(i);
                           return accum;
                       }));
        assertEquals(Arrays.asList(1, 2),
                     td.take(2)
                       .fold(new ArrayList<>(), (List<Integer> accum, Integer i) -> {
                           accum.add(i);
                           return accum;
                       }));
        assertEquals(Collections.emptyList(),
                     td.take(0)
                       .fold(new ArrayList<>(), (List<Integer> accum, Integer i) -> {
                           accum.add(i);
                           return accum;
                       }));
        assertEquals(Arrays.asList(1, 2, 3),
                     td.take(3)
                       .fold(new ArrayList<>(), (List<Integer> accum, Integer i) -> {
                           accum.add(i);
                           return accum;
                       }));
        assertEquals(Arrays.asList(1, 2, 3),
                     td.take(99)
                       .fold(new ArrayList<>(), (List<Integer> accum, Integer i) -> {
                           accum.add(i);
                           return accum;
                       }));
        assertEquals(Arrays.asList(1, 2, 3),
                     td.take(Integer.MAX_VALUE)
                       .fold(new ArrayList<>(), (List<Integer> accum, Integer i) -> {
                           accum.add(i);
                           return accum;
                       }));
        assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6),
                     td.concat(Arrays.asList(4, 5, 6))
                       .fold(new ArrayList<>(), (List<Integer> accum, Integer i) -> {
                           accum.add(i);
                           return accum;
                       }));
        assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6),
                     td.concat(Xform.of(Arrays.asList(4, 5, 6)).toImSortedSet((a, b) -> a - b))
                       .fold(new ArrayList<>(), (List<Integer> accum, Integer i) -> {
                           accum.add(i);
            return accum;
        }));
//        assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6),
//                     td.concatArray(new Integer[]{4, 5, 6})
//                       .fold(new ArrayList<>(), (List<Integer> accum, Integer i) -> {
//                           accum.add(i);
//                           return accum;
//                       }));
        assertEquals(Arrays.asList(2, 3, 4, 5, 6, 7),
                     td.concat(Arrays.asList(4, 5, 6))
                       .map(i -> i + 1)
                       .fold(new ArrayList<>(), (List<Integer> accum, Integer i) -> {
                           accum.add(i);
                           return accum;
                       }));
    }

    @Test public void testEqualsHashCode() {
        equalsDistinctHashCode(new Xform.SourceProviderIterableDesc<>(Arrays.asList("Hi", "Pleased", "Bye")),
                               Xform.of(Arrays.asList("Hi", "Pleased", "Bye")),
                               Xform.of(Arrays.asList("Hi", "Pleased", "Bye")),
                               Xform.of(Arrays.asList("Hi", "PleaZed", "Bye")));
    }

    @Test public void testBasics() {
        Integer[] src = new Integer[] {1, 2, 3};
        basics(Xform.of(Arrays.asList(src)));
        basics(Xform.of(Xform.of(Arrays.asList(src)).toImSortedSet((a, b) -> a - b)));
        basics(Xform.of(Arrays.asList(src)));
    }

    public static void longerCombinations(Xform<Integer> td) {
        assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9),
                     td.fold(new ArrayList<>(), (List<Integer> accum, Integer i) -> {
                         accum.add(i);
                         return accum;
                     }));
        assertEquals(Arrays.asList(2, 4, 6, 8),
                     td.filter(i -> i % 2 == 0)
                       .fold(new ArrayList<>(), (List<Integer> accum, Integer i) -> {
                           accum.add(i);
                           return accum;
                       }));
        assertEquals(Arrays.asList(1, 2, 3, 4, 5),
                     td.take(5)
                       .fold(new ArrayList<>(), (List<Integer> accum, Integer i) -> {
                         accum.add(i);
                         return accum;
                     }));
        assertEquals(Arrays.asList(3, 5, 7, 9),
                     td.filter(i -> i % 2 == 0)
                       .map(i -> i + 1)
                       .fold(new ArrayList<>(), (List<Integer> accum, Integer i) -> {
                           accum.add(i);
                           return accum;
                       }));
        assertEquals(Arrays.asList(3, 5, 7),
                     td.filter(i -> i % 2 == 0)
                       .map(i -> i + 1)
                       .take(3)
                       .fold(new ArrayList<>(), (List<Integer> accum, Integer i) -> {
                           accum.add(i);
                           return accum;
                       }));
        assertEquals(Arrays.asList(3, 30, 300, 5, 50, 500, 7, 70, 700, 9, 90, 900),
                     td.filter(i -> i % 2 == 0)
                       .map(i -> i + 1)
                       .flatMap(i -> vec(i, i * 10, i * 100))
                       .fold(new ArrayList<>(), (List<Integer> accum, Integer i) -> {
                           accum.add(i);
                           return accum;
                       }));
        assertEquals(Arrays.asList(5, 50, 500, 7, 70, 700, 9, 90, 900),
                     td.drop(2)
                       .filter(i -> i % 2 == 0)
                       .map(i -> i + 1)
                       .flatMap(i -> vec(i, i * 10, i * 100))
                       .fold(new ArrayList<>(), (List<Integer> accum, Integer i) -> {
                           accum.add(i);
                           return accum;
                       }));
//        System.out.println("Testing separate drop.");
        assertEquals(Arrays.asList(7, 9),
                     td.filter(i -> i % 2 == 0)
                       .map(i -> i + 1)
                       .drop(2)
                       .fold(new ArrayList<>(), (List<Integer> accum, Integer i) -> {
                           accum.add(i);
                           return accum;
                       }));
//        System.out.println("Done testing separate drop.");
        assertEquals(Arrays.asList(7, 70, 700, 9, 90, 900),
                     td.filter(i -> i % 2 == 0)
                       .map(i -> i + 1)
                       .drop(2)
                       .flatMap(i -> vec(i, i * 10, i * 100))
                       .fold(new ArrayList<>(), (List<Integer> accum, Integer i) -> {
                           accum.add(i);
                           return accum;
                       }));
        assertEquals(Arrays.asList(500, 7, 70, 700, 9, 90, 900),
                     td.filter(i -> i % 2 == 0)
                       .map(i -> i + 1)
                       .flatMap(i -> vec(i, i * 10, i * 100))
                       .drop(5)
                       .fold(new ArrayList<>(), (List<Integer> accum, Integer i) -> {
                           accum.add(i);
                           return accum;
                       }));
        assertEquals(Arrays.asList(500, 7, 70, 700, 9, 90),
                     td.filter(i -> i % 2 == 0)
                       .map(i -> i + 1)
                       .flatMap(i -> vec(i, i * 10, i * 100))
                       .drop(5)
                       .take(6)
                       .fold(new ArrayList<>(), (List<Integer> accum, Integer i) -> {
                           accum.add(i);
                           return accum;
                       }));
        assertEquals(Arrays.asList(500, 7, 70, 700, 9, 90, 91, 92, 93),
                     td.filter(i -> i % 2 == 0)
                       .map(i -> i + 1)
                       .flatMap(i -> vec(i, i * 10, i * 100))
                       .drop(5)
                       .take(6)
                       .concat(Arrays.asList(91, 92, 93))
                       .fold(new ArrayList<>(), (List<Integer> accum, Integer i) -> {
                           accum.add(i);
                           return accum;
                       }));
    }

    @Test public void longerCombinations() {
        Integer[] src = new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        longerCombinations(Xform.of(Arrays.asList(src)));
        longerCombinations(Xform.of(Xform.of(Arrays.asList(src)).toImSortedSet((a, b) -> a - b)));
        longerCombinations(Xform.of(Arrays.asList(src)));
    }

    @Test (expected = IllegalArgumentException.class)
    public void concatEx() {
        Xform.of(Collections.emptyList()).concat(null);
    }
    @Test (expected = IllegalArgumentException.class)
    public void precatEx() {
        Xform.of(Collections.emptyList()).precat(null);
    }

    @Test public void doubleNull() {
        assertArrayEquals(new Integer[0],
                          Xform.of(Collections.emptyList())
                               .concat(Collections.emptyList()).toMutableList().toArray());
        assertArrayEquals(new Integer[0],
                          Xform.of(Collections.emptyList())
                               .precat(Collections.emptyList()).toMutableList().toArray());
    }

    @Test public void precat() {
        assertArrayEquals(new Integer[] { 5, 6, 7, 8, 9 },
                          Xform.of(Arrays.asList(5, 6, 7, 8, 9))
                                  .precat(Collections.emptyList()).toMutableList().toArray());
        assertArrayEquals(new Integer[]{4, 5, 6, 7, 8, 9},
                          Xform.of(Arrays.asList(5, 6, 7, 8, 9))
                                  .precat(Xform.of(Collections.singletonList(4))).toMutableList().toArray());
        assertArrayEquals(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9},
                          Xform.of(Arrays.asList(5, 6, 7, 8, 9))
                                  .precat(Xform.of(Arrays.asList(1, 2, 3, 4))).toMutableList().toArray());
        assertArrayEquals(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9},
                          Xform.of(Arrays.asList(5, 6, 7, 8, 9))
                               .precat(Arrays.asList(1, 2, 3, 4)).toMutableList().toArray());
    }

    @Test
    public void concat() {
        assertArrayEquals(new Integer[] { 1, 2, 3, 4 },
                          Xform.of(Arrays.asList(1, 2, 3, 4))
                                  .concat(Collections.emptyList()).toMutableList().toArray());

//        assertArrayEquals(new Integer[] { 1, 2, 3, 4 },
//                          Xform.of(Arrays.asList(1, 2, 3, 4))
//                                  .concat(Xform.emptyXform()).toMutableList().toArray());

        assertArrayEquals(new Integer[]{1, 2, 3, 4, 5},
                          Xform.of(Arrays.asList(1, 2, 3, 4))
                               .concat(Xform.of(Collections.singletonList(5))).toMutableList().toArray());

        assertArrayEquals(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9},
                          Xform.of(Arrays.asList(1, 2, 3, 4))
                               .concat(Xform.of(Arrays.asList(5, 6, 7, 8, 9))).toMutableList().toArray());
    }

    @Test
    public void chainedPrependAppend() {
        assertArrayEquals(new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 },
                          Xform.of(Collections.singletonList(5))                     //         5
                                  .precat(Xform.of(Collections.singletonList(4)))   //       4,5
                                  .concat(Xform.of(Collections.singletonList(6)))    //       4,5,6
                                  .precat(Xform.of(Arrays.asList(2, 3))) //   2,3,4,5,6
                                  .concat(Xform.of(Arrays.asList(7, 8)))  //   2,3,4,5,6,7,8
                                  .precat(Xform.of(Collections.singletonList(1)))   // 1,2,3,4,5,6,7,8
                                  .concat(Xform.of(Collections.singletonList(9)))    // 1,2,3,4,5,6,7,8,9
                                  .toMutableList().toArray());

        assertArrayEquals(new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 },
                          Xform.of(Collections.singletonList(5))
                                  .precat(Xform.of(Collections.emptyList())).precat(Xform.of(Collections.emptyList()))
                                  .concat(Xform.of(Collections.emptyList())).concat(Xform.of(Collections.emptyList()))
                                  .precat(Xform.of(Collections.singletonList(4)))
                                  .precat(Xform.of(Collections.emptyList())).precat(Xform.of(Collections.emptyList()))
                                  .concat(Xform.of(Collections.emptyList())).concat(Xform.of(Collections.emptyList()))
                                  .concat(Xform.of(Collections.singletonList(6)))
                                  .precat(Xform.of(Collections.emptyList())).precat(Xform.of(Collections.emptyList()))
                                  .concat(Xform.of(Collections.emptyList())).concat(Xform.of(Collections.emptyList()))
                                  .precat(Xform.of(Arrays.asList(2, 3)))
                                  .precat(Xform.of(Collections.emptyList())).precat(Xform.of(Collections.emptyList()))
                                  .concat(Xform.of(Collections.emptyList())).concat(Xform.of(Collections.emptyList()))
                                  .concat(Xform.of(Arrays.asList(7, 8)))
                                  .precat(Xform.of(Collections.emptyList())).precat(Xform.of(Collections.emptyList()))
                                  .concat(Xform.of(Collections.emptyList())).concat(Xform.of(Collections.emptyList()))
                                  .precat(Xform.of(Collections.singletonList(1)))
                                  .precat(Xform.of(Collections.emptyList())).precat(Xform.of(Collections.emptyList()))
                                  .concat(Xform.of(Collections.emptyList())).concat(Xform.of(Collections.emptyList()))
                                  .concat(Xform.of(Collections.singletonList(9)))
                                  .precat(Xform.of(Collections.emptyList())).precat(Xform.of(Collections.emptyList()))
                                  .concat(Xform.of(Collections.emptyList())).concat(Xform.of(Collections.emptyList()))
                                  .toMutableList().toArray());
    }

    @Test public void emptiness() {
        Xform<Integer> seq = Xform.of(Collections.emptyList());
        assertArrayEquals(new Integer[0], seq.drop(0).toMutableList().toArray());
        assertArrayEquals(new Integer[0], seq.drop(1).toMutableList().toArray());
        assertArrayEquals(new Integer[0], seq.drop(2).toMutableList().toArray());
    }

    @Test public void singleElement() {
        Xform<Integer> seq = Xform.of(Collections.singletonList(1));
        assertArrayEquals(new Integer[]{1}, seq.drop(0).toMutableList().toArray());
        assertArrayEquals(new Integer[0], seq.drop(1).toMutableList().toArray());
        assertArrayEquals(new Integer[0], seq.drop(2).toMutableList().toArray());
    }

    @Test public void twoElement() {
        Xform<Integer> seq = Xform.of(Arrays.asList(1, 2));
        assertArrayEquals(new Integer[]{1, 2}, seq.drop(0).toMutableList().toArray());
        assertArrayEquals(new Integer[]{2}, seq.drop(1).toMutableList().toArray());
        assertArrayEquals(new Integer[0], seq.drop(2).toMutableList().toArray());
    }

    @Test
    public void singleDrops() {
        Integer[] ints = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9};
        Xform<Integer> xform = Xform.of(Arrays.asList(ints));
        assertArrayEquals(ints, xform.drop(0).toMutableList().toArray());
        assertArrayEquals(new Integer[]{2, 3, 4, 5, 6, 7, 8, 9}, xform.drop(1).toMutableList().toArray());
        assertArrayEquals(new Integer[]{3, 4, 5, 6, 7, 8, 9}, xform.drop(2).toMutableList().toArray());
        assertArrayEquals(new Integer[]{4, 5, 6, 7, 8, 9}, xform.drop(3).toMutableList().toArray());
        assertArrayEquals(new Integer[]{9}, xform.drop(8).toMutableList().toArray());
        assertArrayEquals(new Integer[0], Xform.of(Collections.emptyList()).toMutableList().toArray());
        assertArrayEquals(new Integer[0], xform.drop(9).toMutableList().toArray());
        assertArrayEquals(new Integer[0], xform.drop(10).toMutableList().toArray());
        assertArrayEquals(new Integer[0], xform.drop(10000).toMutableList().toArray());
        assertArrayEquals(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9},
                          Xform.of(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9)).drop(0).toMutableList().toArray());
        assertArrayEquals(new Integer[]{2, 3, 4, 5, 6, 7, 8, 9},
                          Xform.of(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9)).drop(1).toMutableList().toArray());
        assertArrayEquals(new Integer[]{3, 4, 5, 6, 7, 8, 9},
                          Xform.of(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9)).drop(2).toMutableList().toArray());
        assertArrayEquals(new Integer[]{4, 5, 6, 7, 8, 9},
                          Xform.of(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9)).drop(3).toMutableList().toArray());
        assertArrayEquals(new Integer[]{9},
                          Xform.of(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9)).drop(8).toMutableList().toArray());
        assertArrayEquals(new Integer[]{},
                          Xform.of(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9)).drop(9).toMutableList().toArray());
        assertArrayEquals(new Integer[]{},
                          Xform.of(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9)).drop(10).toMutableList().toArray());
        assertArrayEquals(new Integer[]{},
                          Xform.of(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9)).drop(10000).toMutableList().toArray());
    }

    @Test
    public void multiDrops() {
        Integer[] ints = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9};
        Xform<Integer> seq = Xform.of(Arrays.asList(ints));
        assertArrayEquals(new Integer[] {3,4,5,6,7,8,9},
                seq.drop(1).drop(1).toMutableList().toArray());
        assertArrayEquals(new Integer[] {4,5,6,7,8,9},
                seq.drop(1).drop(1).drop(1).toMutableList().toArray());
        assertArrayEquals(new Integer[] {9},
                seq
                                  .drop(1).drop(1).drop(1).drop(1).drop(1)
                                  .drop(1).drop(1).drop(1).toMutableList().toArray());
        assertArrayEquals(new Integer[] {},
                seq
                                  .drop(1).drop(1).drop(1).drop(1).drop(1)
                                  .drop(1).drop(1).drop(1).drop(1).toMutableList().toArray());
        assertArrayEquals(new Integer[] {},
                seq
                                  .drop(1).drop(1).drop(1).drop(1).drop(1)
                                  .drop(1).drop(1).drop(1).drop(1).drop(1).toMutableList().toArray());
        assertArrayEquals(new Integer[] {},
                seq
                                  .drop(1).drop(1).drop(1).drop(1).drop(1)
                                  .drop(1).drop(1).drop(1).drop(1).drop(1)
                                  .drop(1).drop(1).drop(1).drop(1).drop(1).toMutableList().toArray());

        assertArrayEquals(new Integer[] {7,8,9},
                seq
                                  .drop(0).drop(1).drop(2).drop(3).toMutableList().toArray());
        assertArrayEquals(new Integer[] {7,8,9},
                seq
                                  .drop(3).drop(2).drop(1).drop(0).toMutableList().toArray());

        assertArrayEquals(new Integer[] {3,4,5,6,7,8,9},
                          Xform.of(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9)).drop(1).drop(1).toMutableList().toArray());
        assertArrayEquals(new Integer[] {4,5,6,7,8,9},
                          Xform.of(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9)).drop(1).drop(1).drop(1).toMutableList().toArray());
        assertArrayEquals(new Integer[] {9},
                          Xform.of(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9))
                                  .drop(1).drop(1).drop(1).drop(1).drop(1)
                                  .drop(1).drop(1).drop(1).toMutableList().toArray());
        assertArrayEquals(new Integer[] {},
                          Xform.of(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9))
                                  .drop(1).drop(1).drop(1).drop(1).drop(1)
                                  .drop(1).drop(1).drop(1).drop(1).toMutableList().toArray());
        assertArrayEquals(new Integer[] {},
                          Xform.of(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9))
                                  .drop(1).drop(1).drop(1).drop(1).drop(1)
                                  .drop(1).drop(1).drop(1).drop(1).drop(1).toMutableList().toArray());
        assertArrayEquals(new Integer[]{},
                          Xform.of(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9))
                              .drop(1).drop(1).drop(1).drop(1).drop(1)
                              .drop(1).drop(1).drop(1).drop(1).drop(1)
                              .drop(1).drop(1).drop(1).drop(1).drop(1).toMutableList().toArray());

        assertArrayEquals(new Integer[]{7, 8, 9},
                          Xform.of(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9))
                              .drop(0).drop(1).drop(2).drop(3).toMutableList().toArray());
        assertArrayEquals(new Integer[]{7, 8, 9},
                          Xform.of(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9))
                               .drop(3).drop(2).drop(1).drop(0).toMutableList().toArray());
    }

    @Test(expected = IllegalArgumentException.class)
    public void exception1() {
        Xform.of(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9)).drop(-1);
    }
    @Test(expected = IllegalArgumentException.class)
    public void exception2() {
        Xform.of(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9)).drop(-99);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullException() {
        assertArrayEquals(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9},
                          Xform.of(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9)).filter(null).toMutableList().toArray());
    }

    @Test
    public void singleFilter() {
        assertArrayEquals(new Integer[] {1, 2, 3, 4, 5, 6, 7, 8, 9},
                          Xform.of(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9)).filter(accept()).toMutableList().toArray());

        assertArrayEquals(new Integer[] {},
                          Xform.of(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9)).filter(reject()).toMutableList().toArray());

        assertArrayEquals(new Integer[] {5,6,7,8,9},
                          Xform.of(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9)).filter(i -> i > 4).toMutableList().toArray());

        assertArrayEquals(new Integer[] {},
                          Xform.of(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9)).filter(i -> i < 1).toMutableList().toArray());

        assertArrayEquals(new Integer[] {3},
                          Xform.of(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9)).filter(i -> i == 3).toMutableList().toArray());

        assertArrayEquals(new Integer[] {1},
                          Xform.of(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9)).filter(i -> i == 1).toMutableList().toArray());

        assertArrayEquals(new Integer[] {9},
                          Xform.of(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9)).filter(i -> i == 9).toMutableList().toArray());

        assertArrayEquals(new Integer[]{1, 2, 3, 4, 5, 6},
                          Xform.of(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9)).filter(i -> i < 7).toMutableList().toArray());

    }

    @Test
    public void chainedFilters() {
        assertArrayEquals(new Integer[] {1, 2, 3, 4, 5, 6, 7, 8, 9},
                          Xform.of(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9))
                                  .filter(accept()).filter(accept()).filter(accept())
                                  .filter(accept()).filter(accept()).filter(accept())
                                  .filter(accept()).filter(accept()).toMutableList().toArray());

        assertArrayEquals(new Integer[] {},
                          Xform.of(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9))
                                  .filter(reject()).filter(accept()).filter(accept())
                                  .filter(accept()).filter(accept()).filter(accept())
                                  .filter(accept()).filter(accept()).toMutableList().toArray());

        assertArrayEquals(new Integer[] {},
                          Xform.of(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9))
                                  .filter(accept()).filter(accept()).filter(accept())
                                  .filter(accept()).filter(reject()).filter(accept())
                                  .filter(accept()).filter(accept()).toMutableList().toArray());

        assertArrayEquals(new Integer[] {},
                          Xform.of(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9))
                                  .filter(accept()).filter(accept()).filter(accept())
                                  .filter(accept()).filter(accept()).filter(accept())
                                  .filter(accept()).filter(reject()).toMutableList().toArray());

        assertArrayEquals(new Integer[] {3, 4, 6},
                          Xform.of(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9))
                                  .filter(i -> i > 2)
                                  .filter(i -> i < 7)
                                  .filter(i -> i != 5).toMutableList().toArray());

    }

    @Test public void singleFlatMap() {
        assertEquals(0, Xform.of(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9))
                             .flatMap((i) -> () -> emptyUnmodIterator())
                             .toImList()
                             .size());

        assertEquals(0, Xform.of(Collections.emptyList())
                             .flatMap((i) -> () -> emptyUnmodIterator())
                             .toImList()
                             .size());

        assertArrayEquals(new Integer[] { 1,2,3, 2,4,6, 3,6,9, 4,8,12, 5,10,15, 6,12,18,
                                          7,14,21, 8,16,24, 9,18,27},
                          Xform.of(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9))
                              .flatMap(i -> Xform.of(Arrays.asList(i, i * 2, i * 3))).toMutableList().toArray());

        assertArrayEquals(new String[] { "1","2", "2","3", "3","4"},
                          Xform.of(Arrays.asList(1, 2, 3))
                               .flatMap(i -> Xform.of(Arrays.asList(String.valueOf(i),
                                                                    String.valueOf(i + 1))))
                               .toMutableList().toArray());
    }

    @Test public void flatEmpty() {
        assertEquals(0, Xform.of(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9))
                             .flatMap((a) -> () -> emptyUnmodIterator())
                             .toImList()
                             .size());

        assertEquals(0, Xform.of(Collections.emptyList())
                             .flatMap((a) -> Xform.of(Collections.emptyList()))
                             .toImList()
                             .size());

        // This tests that I didn't just look ahead 2 or 3 times.  That the look-ahead is sufficient.
        AtomicInteger count = new AtomicInteger(0);
        assertArrayEquals(new String[]{"a9", "b9"},
                          Xform.of(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9))
                               .flatMap((a) ->  (count.incrementAndGet() > 8)
                                                ? Xform.of(Arrays.asList("a" + a, "b" + a))
                                                : Xform.of(Collections.emptyList()))
                               .toMutableList().toArray());

        count.set(0);
        assertArrayEquals(new String[]{"c8", "d8", "c9", "d9"},
                          Xform.of(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9))
                               .flatMap((a) -> (count.incrementAndGet() > 7)
                                               ? Xform.of(Arrays.asList("c" + a, "d" + a))
                                               : Xform.of(Collections.emptyList()))
//                                  .forEach((item) -> {
//                              System.out.println("Item " + item);
//                              return null;
//                          })
                               .toMutableList().toArray());

        count.set(0);
        assertArrayEquals(new String[]{"e1", "f1", "e2", "f2"},
                          Xform.of(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9))
                               .flatMap((a) -> (count.incrementAndGet() < 3)
                                               ? Xform.of(Arrays.asList("e" + a, "f" + a))
                                               : Xform.of(Collections.emptyList()))
//                                  .forEach((item) -> {
//                              System.out.println("count: " + count.value() + " Item " + item);
//                              return null;
//                          })
                               .toMutableList().toArray());

        AtomicReference<Xform<Integer>> shrinkSeq =
                new AtomicReference<>(Xform.of(Arrays.asList(1, 2, 3)));
        assertArrayEquals(new Integer[]{2, 3, 3},
                          Xform.of(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9))
                                  .flatMap((a) -> shrinkSeq.updateAndGet(seq -> seq.drop(1)))
                                  .toMutableList().toArray());

        // Now start by returning an ofArray, then a seq of length 1, then length 2, etc.
        // The first ofArray should not end the processing.
        AtomicReference<Xform<Integer>> growSeq =
                new AtomicReference<>(Xform.of(Collections.emptyList()));
        AtomicInteger incInt = new AtomicInteger(0);
        assertArrayEquals(new Integer[]{1, 1,2},
                          Xform.of(Arrays.asList(1, 2, 3))
                               .flatMap((a) -> {
                                   if (incInt.get() > 0) {
                                       growSeq.updateAndGet(seq -> seq.concat(vec(incInt.get())));
                                   }
                                   incInt.getAndIncrement();
                                   return growSeq.get();
                               })
                               .toMutableList().toArray());
    }

    @Test public void flatMapChain() {
        assertEquals(0, Xform.of(vec(1, 2, 3, 4, 5, 6, 7, 8, 9))
                             .flatMap((i) -> () -> emptyUnmodIterator())
                             .flatMap((i) -> () -> emptyUnmodIterator())
                             .flatMap((i) -> () -> emptyUnmodIterator())
                             .toImList()
                             .size());

        assertEquals(0, Xform.of(Collections.emptyList())
                             .flatMap((i) -> Xform.of(Collections.singletonList(i)))
                             .flatMap((i) -> Xform.of(Collections.singletonList(i)))
                             .flatMap((i) -> Xform.of(Collections.singletonList(i)))
                             .toImList()
                             .size());

        assertArrayEquals(new Integer[] {},
                          Xform.of(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9))
                               .flatMap((i) -> () -> emptyUnmodIterator())
                               .flatMap((i) -> () -> emptyUnmodIterator())
                               .flatMap((i) -> () -> emptyUnmodIterator()).toMutableList().toArray());

        assertArrayEquals(new Integer[] { 1,2, 2,3, 3,4, 10,11, 20,21, 30,31},
                          Xform.of(Arrays.asList(1, 10))
                                  .flatMap(i -> Xform.of(Arrays.asList(i, i * 2, i * 3)))
                                  .flatMap(i -> Xform.of(Arrays.asList(i, i + 1)))
                                  .toMutableList().toArray());
    }

    // Below here taken from SequenceTest
    @Test public void construction() {
        Integer[] ints = new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        assertArrayEquals(ints, Xform.of(Arrays.asList(ints)).toMutableList().toArray());
        assertArrayEquals(ints, Xform.of(Arrays.asList(ints)).toMutableList().toArray());
    }

    @Test public void testNullConstruction() {
        assertEquals(Xform.EMPTY, Xform.of(null));
        assertEquals(Xform.EMPTY, xform(null));
    }

//    @Test public void emptyXform() {
//        assertEquals(0, Xform.EMPTY.hashCode());
//        assertEquals(0, Xform.EMPTY.drop(1).hashCode());
//        assertEquals(0, Xform.EMPTY.drop(1).drop(1).drop(1).hashCode());
//
//        assertEquals(Option.none(), Xform.EMPTY.head());
//
//        assertEquals(Xform.EMPTY, Xform.EMPTY);
//        assertEquals(Xform.EMPTY, Xform.EMPTY.drop(1));
//        assertEquals(Xform.EMPTY, Xform.EMPTY.drop(1).drop(1));
//        assertTrue(Xform.EMPTY.equals(Xform.EMPTY.drop(1)));
//        assertTrue(Xform.EMPTY.drop(1).equals(Xform.EMPTY));
//    }

    @Test public void fold() {
        Integer[] ints = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9};

        assertEquals(Integer.valueOf(45),
                     Xform.of(Arrays.asList(ints)).fold(0, (accum, i) -> accum + i));
    }

    @Test(expected = IllegalArgumentException.class)
    public void foldEx() {
        assertEquals(Integer.valueOf(45),
                     Xform.of(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9))
                          .fold(0, null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void foldEx2() {
        assertEquals(Integer.valueOf(45),
                     Xform.of(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9))
                          .fold(0, null, Fn1.reject()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void flatMapEx() { Xform.of(Arrays.asList(1, 2, 3)).flatMap(null); }

    @Test(expected = IllegalArgumentException.class)
    public void mapEx() { Xform.of(Arrays.asList(1, 2, 3)).map(null); }

    @Test public void foldTerm() {
        Integer[] ints = new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 };

        assertEquals(Integer.valueOf(45),
                     Xform.of(Arrays.asList(ints))
                          .fold(0, (accum, i) -> accum + i, Fn1.reject()));

        assertEquals(Integer.valueOf(45),
                     Xform.of(Arrays.asList(ints))
                          .fold(0, (accum, i) -> accum + i, null));

        assertArrayEquals(new Integer[]{2, 3, 4},
                          Xform.of(Arrays.asList(ints))
                               .fold(new ArrayList<>(),
                                     (accum, i) -> {
                                             accum.add(i + 1);
                                             return accum;
                                         },
                                     (accum) -> accum.size() == 3).toArray());
        assertArrayEquals(new Integer[]{2, 3, 4, 5, 6, 7, 8, 9, 10},
                          Xform.of(Arrays.asList(ints))
                                  .fold(new ArrayList<>(),
                                        (accum, i) -> {
                                                accum.add(i + 1);
                                                return accum;
                                            },
                                        (accum) -> accum.size() == 20).toArray());

        // This is fun and it should work.  But it really sets up for the early-termination test
        // next.
        assertEquals(Arrays.asList(1, 2, 3, 2, 4, 6, 3, 6, 9, 4, 8, 12, 5, 10, 15, 6, 12, 18,
                                   7, 14, 21, 8, 16, 24, 9, 18, 27),
                     Xform.of(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9))
                          .flatMap(i -> Xform.of(Arrays.asList(i, i * 2, i * 3)))
                          .fold(new ArrayList<>(),
                                (alist, item) -> {
                                        alist.add(item);
                                        return alist;
                                    }));

        // Early termination test
        assertEquals(Arrays.asList(1,2,3, 2,4,6, 3,6,9, 4,8,12, 5,10,15, 6,12,18,
                                   7,14,21, 8,16),
                     Xform.of(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9))
                          .flatMap(i -> Xform.of(Arrays.asList(i, i * 2, i * 3)))
                          .fold(new ArrayList<>(),
                                (alist, item) -> { alist.add(item); return alist; },
                                (items) -> items.contains(16)));
    }

    @Test public void toIterator() {
        Integer[] ints = new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        Iterator<Integer> seqIter = Xform.of(Arrays.asList(ints)).iterator();
        Iterator<Integer> listIter = Arrays.asList(ints).iterator();
        while (seqIter.hasNext() && listIter.hasNext()) {
            assertEquals(seqIter.next(), listIter.next());
        }
        assertFalse(seqIter.hasNext());
        assertFalse(listIter.hasNext());
    }

//    @Test public void objectMethods() {
//        Integer[] ints = new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
//        Xform<Integer> seq1 = Xform.of(Arrays.asList(ints)).drop(3).take(4);
//        Xform<Integer> seq2 = Xform.of(Arrays.asList(4, 5, 6, 7));
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
        Xform<Integer> seq = Xform.of(Arrays.asList(ints));
        final List<Integer> output = new ArrayList<>();
        seq.forEach(i -> output.add(i));
        assertArrayEquals(ints, output.toArray());
    }

    @Test
    public void firstMatching() {
        Integer[] ints = new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        Xform<Integer> seq = Xform.of(Arrays.asList(ints));

        // TODO: We may want a head() method that returns an option because this gets ugly.
        assertEquals(Integer.valueOf(1), seq.filter(i -> i == 1).take(1).toImList().head().get());
        assertEquals(Integer.valueOf(3), seq.filter(i -> i > 2).take(1).toImList().head().get());
        assertFalse(seq.filter(i -> i > 10).take(1).toImList().head().isSome());
    }

    @Test
    public void takeAndDrop() {
        assertArrayEquals(Xform.of(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9))
                                  .drop(0).take(8888).toMutableList().toArray(),
                          new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9});
        assertArrayEquals(Xform.of(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9))
                                  .drop(1).take(1).toMutableList().toArray(),
                          new Integer[]{2});
        assertArrayEquals(Xform.of(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9))
                                  .drop(2).take(2).toMutableList().toArray(),
                   new Integer[] { 3,4 });
        assertArrayEquals(Xform.of(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9))
                                  .drop(3).take(3).toMutableList().toArray(),
                   new Integer[] { 4,5,6 });
        assertArrayEquals(Xform.of(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9))
                                  .drop(9999).take(3).toMutableList().toArray(),
                          new Integer[]{});
        assertArrayEquals(Xform.of(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9))
                                  .drop(3).take(0).toMutableList().toArray(),
                   new Integer[] { });
    }

    @Test public void chain1() {
        assertArrayEquals(Xform.of(Collections.singletonList(5))                      //         5
                                  .precat(Xform.of(Collections.singletonList(4)))    //       4,5
                                  .concat(Xform.of(Collections.singletonList(6)))     //       4,5,6
                                  .precat(Xform.of(Arrays.asList(2, 3))) //   2,3,4,5,6
                                  .concat(Xform.of(Arrays.asList(7, 8)))  //   2,3,4,5,6,7,8
                                  .precat(Xform.of(Collections.singletonList(1)))    // 1,2,3,4,5,6,7,8
                                  .concat(Xform.of(Collections.singletonList(9)))     // 1,2,3,4,5,6,7,8,9
                                  .filter(i -> i > 3)              //       4,5,6,7,8,9
                                  .map(i -> i - 2)                 //   2,3,4,5,6,7
                                  .take(5)                         //   2,3,4,5,6
                                  .drop(2)                         //       4,5,6
                                  .toMutableList().toArray(),
                          new Integer[]{4, 5, 6});
    }
    // Above here taken from SequenceTest.
}