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

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.organicdesign.fp.TestUtilities;
import org.organicdesign.fp.tuple.Tuple2;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class UnmodListTest {
    @Test public void permutationsTest() {
        Set<Tuple2<Integer,Integer>> answerSet = new HashSet<>();
        UnmodList.permutations(Arrays.asList(1,2,3,4),
                               (a, b) -> {
                                   answerSet.add(Tuple2.of(a, b));
                                   return answerSet;
                               });
        assertEquals(6, answerSet.size());
        assertTrue(answerSet.contains(Tuple2.of(1, 2)));
        assertTrue(answerSet.contains(Tuple2.of(1, 3)));
        assertTrue(answerSet.contains(Tuple2.of(1, 4)));
        assertTrue(answerSet.contains(Tuple2.of(2, 3)));
        assertTrue(answerSet.contains(Tuple2.of(2, 4)));
        assertTrue(answerSet.contains(Tuple2.of(3, 4)));
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

    @SuppressWarnings("deprecation")
    @Test
    public void subListEx() {
        @NotNull Exception ex;
        ex = assertThrowsExactly(IllegalArgumentException.class, () ->
            unList.subList(3, 2));
        // Note that this is an IllegalArgumentException, not IndexOutOfBoundsException in order to
        // match ArrayList.
        assertEquals("fromIndex(3) > toIndex(2)", ex.getMessage());

        assertThrowsExactly(IndexOutOfBoundsException.class, () ->
                unList.subList(-1, 2));

        // This text matches ArrayList (eew)
        ex = assertThrowsExactly(IndexOutOfBoundsException.class, () ->
                unList.subList(1, unList.size() + 1));
        assertEquals("toIndex = 14", ex.getMessage());

        ex = assertThrowsExactly(UnsupportedOperationException.class, () ->
                unList.add("hi"));
        assertEquals("Modification attempted", ex.getMessage());

        ex = assertThrowsExactly(UnsupportedOperationException.class, () ->
                unList.add(0, "hi"));
        assertEquals("Modification attempted", ex.getMessage());

        ex = assertThrowsExactly(UnsupportedOperationException.class, () ->
                unList.addAll(Arrays.asList("hi", "there")));
        assertEquals("Modification attempted", ex.getMessage());

        ex = assertThrowsExactly(UnsupportedOperationException.class, () ->
                unList.addAll(0, Arrays.asList("hi", "there")));
        assertEquals("Modification attempted", ex.getMessage());

        ex = assertThrowsExactly(UnsupportedOperationException.class, () ->
                unList.clear());
        assertEquals("Modification attempted", ex.getMessage());

        ex = assertThrowsExactly(UnsupportedOperationException.class, () ->
                unList.remove(0));
        assertEquals("Modification attempted", ex.getMessage());

        ex = assertThrowsExactly(UnsupportedOperationException.class, () ->
                unList.remove("hi"));
        assertEquals("Modification attempted", ex.getMessage());

        ex = assertThrowsExactly(UnsupportedOperationException.class, () ->
                unList.removeAll(Arrays.asList("hi", "there")));
        assertEquals("Modification attempted", ex.getMessage());

        ex = assertThrowsExactly(UnsupportedOperationException.class, () ->
                unList.replaceAll(x -> x));
        assertEquals("Modification attempted", ex.getMessage());

        ex = assertThrowsExactly(UnsupportedOperationException.class, () ->
                unList.removeIf(item -> false));
        assertEquals("Modification attempted", ex.getMessage());

        ex = assertThrowsExactly(UnsupportedOperationException.class, () ->
                unList.retainAll(Arrays.asList("hi", "there")));
        assertEquals("Modification attempted", ex.getMessage());

        ex = assertThrowsExactly(UnsupportedOperationException.class, () ->
                unList.set(0, "hi"));
        assertEquals("Modification attempted", ex.getMessage());

        ex = assertThrowsExactly(UnsupportedOperationException.class, () ->
                unList.sort(String.CASE_INSENSITIVE_ORDER));
        assertEquals("Modification attempted", ex.getMessage());
    }

    @Test public void toArrayTest() {
        assertArrayEquals(sticksAndStones, unList.toArray());
        assertArrayEquals(sticksAndStones, unList.toArray(new String[3]));
        assertArrayEquals(sticksAndStones, unList.toArray(new String[sticksAndStones.length]));
        assertTrue(sticksAndStones.length <
                   unList.toArray(new String[sticksAndStones.length + 1]).length);
    }
}
