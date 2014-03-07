// Copyright (c) 2014-03-06 PlanBase Inc. & Glen Peterson
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless r==uired by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.organicdesign.fp;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.organicdesign.fp.ephemeral.View;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertArrayEquals;

@RunWith(JUnit4.class)
public class FunctionUtilsTest {
//    @SuppressWarnings("Convert2Lambda")
//    public static final Predicate<Integer> r = new Predicate<Integer>() {
//        @Override
//        public boolean test(Integer i) {
//            return i < -1;
//        }
//    };
//
//    @Test
//    public void goBoom2() {
//        Predicate<Integer> p = i -> i > 0;
//
//        @SuppressWarnings("Convert2Lambda")
//        Predicate<Integer> q = new Predicate<Integer>() {
//            @Override
//            public boolean test(Integer i) {
//                return i < 0;
//            }
//        };
//
//        boolean q1 = p == q;
//        boolean q2 = Transformable.USED_UP == new Object();
//        boolean q3 = new Object() == Transformable.USED_UP;
//        boolean q4 = p == new Object();
//        boolean q5 = q != Transformable.USED_UP;
//        boolean q6 = p != Transformable.USED_UP;
//        Transformable.isUsedUp(p);
//        Transformable.isUsedUp(q);
//        Transformable.isUsedUp(FunctionUtils.REJECT);
//        boolean q7 = FunctionUtils.REJECT == FunctionUtils.ACCEPT;
//        boolean q8 = q == r;
////        boolean q7 = p != FunctionUtils.ACCEPT;
////        boolean q7 = ((Predicate<Object>) p) != FunctionUtils.accept();
//    }
//
////    @Test
////    public void goBoom1() {
////        Predicate<Integer> p = i -> i > 0;
////        throw new IllegalStateException("Type of p is: " + p.getClass().getCanonicalName());
////    }

    @Test
    public void composePredicatesWithAnd() {
        assertTrue(FunctionUtils.andArray() == FunctionUtils.accept());
        assertTrue(FunctionUtils.and(null) == FunctionUtils.accept());

        assertTrue(FunctionUtils.andArray(FunctionUtils.accept()) == FunctionUtils.accept());
        assertTrue(FunctionUtils.and(View.ofArray(FunctionUtils.accept())) ==
                   FunctionUtils.accept());

        assertTrue(FunctionUtils.<Object>andArray(FunctionUtils.accept(),
                                                  FunctionUtils.accept(),
                                                  FunctionUtils.accept()) ==
                   FunctionUtils.accept());
        assertTrue(FunctionUtils.<Object>and(View.ofArray(FunctionUtils.accept(),
                                                          FunctionUtils.accept(),
                                                          FunctionUtils.accept())) ==
                   FunctionUtils.accept());

        assertTrue(FunctionUtils.andArray(FunctionUtils.reject()) == FunctionUtils.reject());
        assertTrue(FunctionUtils.and(View.ofArray(FunctionUtils.reject())) ==
                   FunctionUtils.reject());
    }

    @Test
    public void composePredicatesWithOr() {
        assertTrue(FunctionUtils.orArray() == FunctionUtils.reject());
        assertTrue(FunctionUtils.or(null) == FunctionUtils.reject());

        assertTrue(FunctionUtils.orArray(FunctionUtils.accept()) == FunctionUtils.accept());
        assertTrue(FunctionUtils.or(View.ofArray(FunctionUtils.accept())) ==
                   FunctionUtils.accept());

        assertTrue(FunctionUtils.<Object>orArray(FunctionUtils.reject(),
                                                 FunctionUtils.reject(),
                                                 FunctionUtils.reject(),
                                                 FunctionUtils.accept()) ==
                   FunctionUtils.accept());
        assertTrue(FunctionUtils.<Object>or(View.ofArray(FunctionUtils.reject(),
                                                         FunctionUtils.reject(),
                                                         FunctionUtils.reject(),
                                                         FunctionUtils.accept())) ==
                   FunctionUtils.accept());

        assertTrue(FunctionUtils.<Object>orArray(FunctionUtils.accept(),
                                                 FunctionUtils.reject(),
                                                 FunctionUtils.reject(),
                                                 FunctionUtils.reject()) ==
                   FunctionUtils.accept());
        assertTrue(FunctionUtils.<Object>or(View.ofArray(FunctionUtils.accept(),
                                                         FunctionUtils.reject(),
                                                         FunctionUtils.reject(),
                                                         FunctionUtils.reject())) ==
                   FunctionUtils.accept());

        assertTrue(FunctionUtils.orArray(FunctionUtils.reject()) == FunctionUtils.reject());
        assertTrue(FunctionUtils.or(View.ofArray(FunctionUtils.reject())) ==
                   FunctionUtils.reject());
    }

    @Test
    public void filtersOfPredicates() {
        assertArrayEquals(View.ofArray(1,2,3,4,5,6,7,8,9)
                                  .filter(FunctionUtils.andArray(i -> i > 2,
                                                                 i -> i < 6))
                                  .toJavaArrayList()
                                  .toArray(),
                          new Integer[] { 3,4,5 });

        assertArrayEquals(View.ofArray(1,2,3,4,5,6,7,8,9)
                                  .filter(FunctionUtils.orArray(i -> i < 3,
                                                                i -> i > 5))
                                  .toJavaArrayList()
                                  .toArray(),
                          new Integer[] { 1,2, 6,7,8,9 });

        assertArrayEquals(View.ofArray(1,2,3,4,5,6,7,8,9)
                                  .filter(FunctionUtils.orArray(i -> i < 3,
                                                                i -> i == 4,
                                                                i -> i > 5))
                                  .toJavaArrayList()
                                  .toArray(),
                          new Integer[] { 1,2, 4, 6,7,8,9 });
    }
}
