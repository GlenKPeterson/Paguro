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

package org.organicdesign.fp.collections;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.organicdesign.fp.TestUtilities;
import org.organicdesign.fp.tuple.Tuple2;

import static org.junit.Assert.*;

public class UnmodListTest {
    @Test public void permutationsTest() {
        Set<Tuple2<Integer,Integer>> answerSet = new HashSet<>();
        UnmodList.permutations(Arrays.asList(1,2,3,4),
                               (a, b) -> {
                                   answerSet.add(new Tuple2<>(a, b));
                                   return answerSet;
                               });
        assertEquals(6, answerSet.size());
        assertTrue(answerSet.contains(new Tuple2<>(1, 2)));
        assertTrue(answerSet.contains(new Tuple2<>(1, 3)));
        assertTrue(answerSet.contains(new Tuple2<>(1, 4)));
        assertTrue(answerSet.contains(new Tuple2<>(2, 3)));
        assertTrue(answerSet.contains(new Tuple2<>(2, 4)));
        assertTrue(answerSet.contains(new Tuple2<>(3, 4)));
    }

    private static final String[] sticksAndStones = new String[] {
            "Sticks", "and", "stones", "will", "break", "my", "bones", "but", "tests",
            "will", "never", "hurt", "me." };

    // Test implementation of UnmodList.
    private static final UnmodList<String> unList = new UnmodList<String>() {
        @Override public int size() { return sticksAndStones.length; }

        @Override public String get(int index) { return sticksAndStones[index]; }
    };

    @SuppressWarnings("deprecation")
    @Test public void containsTest() {
        for (String s : sticksAndStones) {
            assertTrue(unList.contains(s));
        }
        assertFalse(unList.contains("phrog"));
    }

    @Test public void containsAllTest() {
        List<String> ls = Arrays.asList(sticksAndStones);
        assertTrue(ls.containsAll(unList));
        assertTrue(unList.containsAll(ls));

        List<String> ls2 = Arrays.asList("Sticks", "and", "stones", "will", "break", "my", "bones",
                                         "but", "tests", "will", "never", "hurt");
        assertFalse(ls2.containsAll(unList));
        assertTrue(unList.containsAll(ls2));

        List<String> ls3 = Arrays.asList("Sticks", "and", "stones", "will", "break", "my", "bones",
                                         "but", "tests", "will", "never", "hurt", "me.",
                                         "maybe");
        assertTrue(ls3.containsAll(unList));
        assertFalse(unList.containsAll(ls3));
    }

    @Test public void listIteratorTest() {
        TestUtilities.listIteratorTest(Arrays.asList(sticksAndStones), unList);
    }

    @Test public void subList() {
        List<String> a = Arrays.asList(sticksAndStones);
        assertEquals(Arrays.asList("stones", "will", "break"),
                     unList.subList(2, 5));

        // Full subList returns the original unchanged.
        assertTrue(unList == unList.subList(0, unList.size()));

        assertEquals(Collections.emptyList(),
                     unList.subList(2, 2));

        List<String> b = unList;

        assertEquals(a.size(), b.size());
        assertEquals(a.get(0), b.get(0));
        assertEquals(a.get(a.size() - 1), b.get(b.size() - 1));

        List<String> sla = a.subList(1, 3);
        List<String> slb = b.subList(1, 3);

        assertEquals(sla.size(), slb.size());
        assertEquals(sla.get(0), slb.get(0));
        assertEquals(sla.get(sla.size() - 1), slb.get(slb.size() - 1));

//        assertEquals(sla.hashCode(),
//                     slb.hashCode());
//        assertEquals(sla, slb);

//        EqualsContract.equalsDistinctHashCode(slb,
//                                              RangeOfInt.of(-1, 1).subList(0, 2),
//                                              RangeOfInt.of(-3, 5).subList(2, 4),
//                                              RangeOfInt.of(-2, 5));

        sla = a.subList(0, a.size());
        slb = b.subList(0, b.size());

        assertEquals(sla.size(), slb.size());
        assertEquals(sla.get(0), slb.get(0));
        assertEquals(sla.get(sla.size() - 1), slb.get(slb.size() - 1));

        sla = a.subList(0, a.size() - 1);
        slb = b.subList(0, b.size() - 1);

        assertEquals(sla.size(), slb.size());
        assertEquals(sla.get(0), slb.get(0));
        assertEquals(sla.get(sla.size() - 1), slb.get(slb.size() - 1));

        sla = a.subList(1, a.size());
        slb = b.subList(1, b.size());

        assertEquals(sla.size(), slb.size());
        assertEquals(sla.get(0), slb.get(0));
        assertEquals(sla.get(sla.size() - 1), slb.get(slb.size() - 1));

        sla = a.subList(0, 1);
        slb = b.subList(0, 1);

        assertEquals(sla.size(), slb.size());
        assertEquals(sla.get(0), slb.get(0));
        assertEquals(sla.get(sla.size() - 1), slb.get(slb.size() - 1));

        sla = a.subList(0, 0);
        slb = b.subList(0, 0);

        assertEquals(sla.size(), slb.size());

        sla = a.subList(2, 2);
        slb = b.subList(2, 2);

        assertEquals(sla.size(), slb.size());
    }

    @Test (expected = IllegalArgumentException.class)
    public void subListEx01() { unList.subList(3, 2); }

    @Test (expected = IndexOutOfBoundsException.class)
    public void subListEx02() { unList.subList(-1, 2); }

    @Test (expected = IndexOutOfBoundsException.class)
    public void subListEx03() { unList.subList(1, unList.size() + 1); }

    @Test public void toArrayTest() {
        assertArrayEquals(sticksAndStones, unList.toArray());
        assertArrayEquals(sticksAndStones, unList.toArray(new String[3]));
        assertArrayEquals(sticksAndStones, unList.toArray(new String[sticksAndStones.length]));
        assertTrue(sticksAndStones.length <
                   unList.toArray(new String[sticksAndStones.length + 1]).length);
    }

    @SuppressWarnings("deprecation")
    @Test (expected = UnsupportedOperationException.class)
    public void unsupportedOpAdd() { unList.add("hi"); }

    @SuppressWarnings("deprecation")
    @Test (expected = UnsupportedOperationException.class)
    public void unsupportedOpAddIdx() { unList.add(0, "hi"); }

    @SuppressWarnings("deprecation")
    @Test (expected = UnsupportedOperationException.class)
    public void unsupportedOpAddAll() { unList.addAll(Arrays.asList("hi", "there")); }

    @SuppressWarnings("deprecation")
    @Test (expected = UnsupportedOperationException.class)
    public void unsupportedOpAddAllIdx() { unList.addAll(0, Arrays.asList("hi", "there")); }

    @SuppressWarnings("deprecation")
    @Test (expected = UnsupportedOperationException.class)
    public void unsupportedOpClear() { unList.clear(); }

    @SuppressWarnings("deprecation")
    @Test (expected = UnsupportedOperationException.class)
    public void unsupportedOpRemoveIdx() { unList.remove(0); }

    @SuppressWarnings("deprecation")
    @Test (expected = UnsupportedOperationException.class)
    public void unsupportedOpRemove() { unList.remove("hi"); }

    @SuppressWarnings("deprecation")
    @Test (expected = UnsupportedOperationException.class)
    public void unsupportedOpRemoveAll() { unList.removeAll(Arrays.asList("hi", "there")); }

    @SuppressWarnings("deprecation")
    @Test (expected = UnsupportedOperationException.class)
    public void unsupportedOpReplaceAll() { unList.replaceAll(x -> x); }

    @SuppressWarnings("deprecation")
    @Test (expected = UnsupportedOperationException.class)
    public void unsupportedOpRemoveIf() { unList.removeIf(item -> false); }

    @SuppressWarnings("deprecation")
    @Test (expected = UnsupportedOperationException.class)
    public void unsupportedOpRetainAll() { unList.retainAll(Arrays.asList("hi", "there")); }

    @SuppressWarnings("deprecation")
    @Test (expected = UnsupportedOperationException.class)
    public void unsupportedOpSet() { unList.set(0, "hi"); }

    @SuppressWarnings("deprecation")
    @Test (expected = UnsupportedOperationException.class)
    public void unsupportedOpSort() { unList.sort(String.CASE_INSENSITIVE_ORDER); }
}
