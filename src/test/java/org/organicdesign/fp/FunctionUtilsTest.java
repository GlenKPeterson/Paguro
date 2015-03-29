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

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

@RunWith(JUnit4.class)
public class FunctionUtilsTest {

    @Test
    public void iterableToString() {
        List<Integer> is = new ArrayList<>();
        is.add(1);
        is.add(2);
        is.add(3);
        is.add(4);
        is.add(5);
        assertEquals("ArrayList(1,2,3,4,5)", FunctionUtils.toString(is));
    }
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


}
