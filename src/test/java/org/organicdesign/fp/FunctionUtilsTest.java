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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.organicdesign.fp.FunctionUtils.ordinal;

@RunWith(JUnit4.class)
public class FunctionUtilsTest {

    @Test
    public void testToString() {
        List<Integer> is = new ArrayList<>();
        is.add(1);
        is.add(2);
        is.add(3);
        is.add(4);
        is.add(5);
        assertEquals("Array<Integer>(1,2,3,4,5)", FunctionUtils.toString(is.toArray()));

        is.add(6);
        assertEquals("Array<Integer>(1,2,3,4,5,...)", FunctionUtils.toString(is.toArray()));

        Map<String,Integer> m = new TreeMap<>();
        m.put("Hello", 99);
        m.put("World", -237);
        assertEquals("TreeMap(Entry(Hello,99),Entry(World,-237))", FunctionUtils.toString(m));

        m.put("x", 3);
        m.put("y", 2);
        m.put("z", 1);
        m.put("zz", 0);

        assertEquals("TreeMap(Entry(Hello,99),Entry(World,-237),Entry(x,3),Entry(y,2),Entry(z,1),...)", FunctionUtils.toString(m));

    }

    @SuppressWarnings({"ConstantConditions","Unchecked"})
    @Test public void testToStringNull() {
        assertEquals("null", FunctionUtils.toString((Map<String,Integer>) null));
        Integer[] zs = null;
        assertEquals("null", FunctionUtils.toString(zs));
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

    @Test public void testOrdinal() {
        assertTrue("0th".equals(ordinal(0)));
        assertTrue("1st".equals(ordinal(1))); // st
        assertTrue("2nd".equals(ordinal(2))); // nd
        assertTrue("3rd".equals(ordinal(3))); // rd
        assertTrue("4th".equals(ordinal(4)));
        assertTrue("5th".equals(ordinal(5)));
        assertTrue("6th".equals(ordinal(6)));
        assertTrue("7th".equals(ordinal(7)));
        assertTrue("8th".equals(ordinal(8)));
        assertTrue("9th".equals(ordinal(9)));
        assertTrue("10th".equals(ordinal(10)));
        assertTrue("11th".equals(ordinal(11)));
        assertTrue("12th".equals(ordinal(12)));
        assertTrue("13th".equals(ordinal(13)));
        assertTrue("14th".equals(ordinal(14)));
        assertTrue("15th".equals(ordinal(15)));
        assertTrue("16th".equals(ordinal(16)));
        assertTrue("17th".equals(ordinal(17)));
        assertTrue("18th".equals(ordinal(18)));
        assertTrue("19th".equals(ordinal(19)));
        assertTrue("20th".equals(ordinal(20)));
        assertTrue("21st".equals(ordinal(21))); // st
        assertTrue("22nd".equals(ordinal(22))); // nd
        assertTrue("23rd".equals(ordinal(23))); // rd
        assertTrue("24th".equals(ordinal(24)));
        assertTrue("25th".equals(ordinal(25)));
        assertTrue("26th".equals(ordinal(26)));
        assertTrue("27th".equals(ordinal(27)));
        assertTrue("28th".equals(ordinal(28)));
        assertTrue("29th".equals(ordinal(29)));
        assertTrue("30th".equals(ordinal(30)));

        assertTrue("51st".equals(ordinal(51))); // st

        assertTrue("62nd".equals(ordinal(62))); // nd

        assertTrue("73rd".equals(ordinal(73)));

        assertTrue("84th".equals(ordinal(84)));

        assertTrue("95th".equals(ordinal(95)));

        assertTrue("100th".equals(ordinal(100)));
        assertTrue("101st".equals(ordinal(101))); // st
        assertTrue("102nd".equals(ordinal(102))); // nd
        assertTrue("103rd".equals(ordinal(103))); // rd
        assertTrue("104th".equals(ordinal(104)));
        assertTrue("105th".equals(ordinal(105)));
        assertTrue("106th".equals(ordinal(106)));
        assertTrue("107th".equals(ordinal(107)));
        assertTrue("108th".equals(ordinal(108)));
        assertTrue("109th".equals(ordinal(109)));
        assertTrue("110th".equals(ordinal(110)));
        assertTrue("111th".equals(ordinal(111)));
        assertTrue("112th".equals(ordinal(112)));
        assertTrue("113th".equals(ordinal(113)));
        assertTrue("114th".equals(ordinal(114)));
        assertTrue("115th".equals(ordinal(115)));
        assertTrue("116th".equals(ordinal(116)));
        assertTrue("117th".equals(ordinal(117)));
        assertTrue("118th".equals(ordinal(118)));
        assertTrue("119th".equals(ordinal(119)));
        assertTrue("120th".equals(ordinal(120)));
        assertTrue("121st".equals(ordinal(121))); // st
        assertTrue("122nd".equals(ordinal(122))); // nd
        assertTrue("123rd".equals(ordinal(123))); // rd
        assertTrue("124th".equals(ordinal(124)));
        assertTrue("125th".equals(ordinal(125)));
        assertTrue("126th".equals(ordinal(126)));
        assertTrue("127th".equals(ordinal(127)));
        assertTrue("128th".equals(ordinal(128)));
        assertTrue("129th".equals(ordinal(129)));
        assertTrue("130th".equals(ordinal(130)));

        // Close to maximum int...
        assertTrue("2147483640th".equals(ordinal(2147483640)));
        assertTrue("2147483641st".equals(ordinal(2147483641)));
        assertTrue("2147483642nd".equals(ordinal(2147483642)));
        assertTrue("2147483643rd".equals(ordinal(2147483643)));
        assertTrue("2147483644th".equals(ordinal(2147483644)));

        // Maximum int...
        assertTrue("2147483647th".equals(ordinal(2147483647)));

        // Negative numbers - don't know how practical it is, but might as well be thorough.
        assertTrue("-1st".equals(ordinal(-1))); // st
        assertTrue("-2nd".equals(ordinal(-2))); // nd
        assertTrue("-3rd".equals(ordinal(-3))); // rd
        assertTrue("-4th".equals(ordinal(-4)));
        assertTrue("-5th".equals(ordinal(-5)));
        assertTrue("-6th".equals(ordinal(-6)));
        assertTrue("-7th".equals(ordinal(-7)));
        assertTrue("-8th".equals(ordinal(-8)));
        assertTrue("-9th".equals(ordinal(-9)));
        assertTrue("-10th".equals(ordinal(-10)));
        assertTrue("-11th".equals(ordinal(-11)));
        assertTrue("-12th".equals(ordinal(-12)));
        assertTrue("-13th".equals(ordinal(-13)));
        assertTrue("-14th".equals(ordinal(-14)));
        assertTrue("-15th".equals(ordinal(-15)));
        assertTrue("-16th".equals(ordinal(-16)));
        assertTrue("-17th".equals(ordinal(-17)));
        assertTrue("-18th".equals(ordinal(-18)));
        assertTrue("-19th".equals(ordinal(-19)));
        assertTrue("-20th".equals(ordinal(-20)));
        assertTrue("-21st".equals(ordinal(-21))); // st
        assertTrue("-22nd".equals(ordinal(-22))); // nd
        assertTrue("-23rd".equals(ordinal(-23))); // rd
        assertTrue("-24th".equals(ordinal(-24)));
        assertTrue("-25th".equals(ordinal(-25)));
        assertTrue("-26th".equals(ordinal(-26)));
        assertTrue("-27th".equals(ordinal(-27)));
        assertTrue("-28th".equals(ordinal(-28)));
        assertTrue("-29th".equals(ordinal(-29)));
        assertTrue("-30th".equals(ordinal(-30)));

        assertTrue("-51st".equals(ordinal(-51))); // st

        assertTrue("-62nd".equals(ordinal(-62))); // nd

        assertTrue("-73rd".equals(ordinal(-73)));

        assertTrue("-84th".equals(ordinal(-84)));

        assertTrue("-95th".equals(ordinal(-95)));

        assertTrue("-100th".equals(ordinal(-100)));
        assertTrue("-101st".equals(ordinal(-101))); // st
        assertTrue("-102nd".equals(ordinal(-102))); // nd
        assertTrue("-103rd".equals(ordinal(-103))); // rd
        assertTrue("-104th".equals(ordinal(-104)));
        assertTrue("-105th".equals(ordinal(-105)));
        assertTrue("-106th".equals(ordinal(-106)));
        assertTrue("-107th".equals(ordinal(-107)));
        assertTrue("-108th".equals(ordinal(-108)));
        assertTrue("-109th".equals(ordinal(-109)));
        assertTrue("-110th".equals(ordinal(-110)));
        assertTrue("-111th".equals(ordinal(-111)));
        assertTrue("-112th".equals(ordinal(-112)));
        assertTrue("-113th".equals(ordinal(-113)));
        assertTrue("-114th".equals(ordinal(-114)));
        assertTrue("-115th".equals(ordinal(-115)));
        assertTrue("-116th".equals(ordinal(-116)));
        assertTrue("-117th".equals(ordinal(-117)));
        assertTrue("-118th".equals(ordinal(-118)));
        assertTrue("-119th".equals(ordinal(-119)));
        assertTrue("-120th".equals(ordinal(-120)));
        assertTrue("-121st".equals(ordinal(-121))); // st
        assertTrue("-122nd".equals(ordinal(-122))); // nd
        assertTrue("-123rd".equals(ordinal(-123))); // rd
        assertTrue("-124th".equals(ordinal(-124)));
        assertTrue("-125th".equals(ordinal(-125)));
        assertTrue("-126th".equals(ordinal(-126)));
        assertTrue("-127th".equals(ordinal(-127)));
        assertTrue("-128th".equals(ordinal(-128)));
        assertTrue("-129th".equals(ordinal(-129)));
        assertTrue("-130th".equals(ordinal(-130)));

        // Close to minimum int...
        assertTrue("-2147483640th".equals(ordinal(-2147483640)));
        assertTrue("-2147483641st".equals(ordinal(-2147483641)));
        assertTrue("-2147483642nd".equals(ordinal(-2147483642)));
        assertTrue("-2147483643rd".equals(ordinal(-2147483643)));
        assertTrue("-2147483644th".equals(ordinal(-2147483644)));

        // Minimum int...
        assertTrue("-2147483648th".equals(ordinal(-2147483648)));
    } // end testOrdinal();
}
