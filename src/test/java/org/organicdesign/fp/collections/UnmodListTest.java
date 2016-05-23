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

import org.junit.Test;
import org.organicdesign.fp.FunctionUtils;
import org.organicdesign.fp.function.Function0;
import org.organicdesign.fp.tuple.Tuple2;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Set;

import static org.junit.Assert.*;

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

    @Test public void indexOf() {
        assertEquals(-1, FunctionUtils.unmodList(Arrays.asList("Along", "came", "a", "spider"))
                                      .indexOf("hamster"));
        assertEquals(0, FunctionUtils.unmodList(Arrays.asList("Along", "came", "a", "spider"))
                                     .indexOf("Along"));
        assertEquals(2, FunctionUtils.unmodList(Arrays.asList("Along", "came", "a", "spider"))
                                     .indexOf("a"));
        assertEquals(3, FunctionUtils.unmodList(Arrays.asList("Along", "came", "a", "spider"))
                                     .indexOf("spider"));

        assertEquals(-1, FunctionUtils.unmodList(Arrays.asList("Along", "came", "a", "spider"))
                                      .lastIndexOf("hamster"));
        assertEquals(0, FunctionUtils.unmodList(Arrays.asList("Along", "came", "a", "spider"))
                                     .lastIndexOf("Along"));
        assertEquals(2, FunctionUtils.unmodList(Arrays.asList("Along", "came", "a", "spider"))
                                     .lastIndexOf("a"));
        assertEquals(3, FunctionUtils.unmodList(Arrays.asList("Along", "came", "a", "spider"))
                                     .lastIndexOf("spider"));

        assertEquals(5, FunctionUtils.unmodList(Arrays.asList("Along", "came", "a", "spider", "and",
                                                              "a", "poodle")).lastIndexOf("a"));
        assertEquals(5, FunctionUtils.unmodList(Arrays.asList("Along", "came", "a", "spider", "and",
                                                              "a")).lastIndexOf("a"));
        assertEquals(6, FunctionUtils.unmodList(Arrays.asList("Along", "came", "a", "spider", "and",
                                                              "a", "Along")).lastIndexOf("Along"));
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

    // TODO: This belongs in testUtils
    /**
     Call with two Iterators to test that they are equal
     @param a the reference iterator
     @param b the iterator under test.
     */
    public static <A,B> void iteratorTest(Iterator<A> a, Iterator<B> b) {
        while (a.hasNext()) {
            assertTrue("When a has a next, b should too", b.hasNext());
            assertEquals("a.next should equal b.next", a.next(), b.next());
        }
        assertFalse("When a has no next, b shouldn't either", b.hasNext());
    }

    // TODO: This belongs in testUtils
    public static <T extends Throwable> void assertEx(Function0<?> f, String beforeText,
                                                      Class<T> exType) {
        try {
            f.apply();
        } catch (Throwable t) {
            if (!exType.isInstance(t)) {
                fail("Expected " + beforeText + " to throw " + exType.getSimpleName() +
                     " but threw " + t);
            }
            return;
        }
        fail("Expected " + beforeText + " to throw " + exType.getSimpleName());
    }

    private static <A,B> void assertLiEq(ListIterator<A> a, ListIterator<B> b, String afterText) {
        assertEquals("a.hasNext should equal b.hasNext " + afterText,
                     a.hasNext(), b.hasNext());
        assertEquals("a.hasPrevious should equal b.hasPrevious " + afterText,
                     a.hasPrevious(), b.hasPrevious());
        assertEquals("a.nextIndex should equal b.nextIndex " + afterText,
                     a.nextIndex(), b.nextIndex());
        assertEquals("a.previousIndex should equal b.previousIndex " + afterText,
                     a.previousIndex(), b.previousIndex());
    }

    // TODO: This belongs in testUtils
    /**
     Call with two ListIterators to test that they are equal
     @param aList the reference iterator
     @param bList the iterator under test.
     */
    public static <A,B> void listIteratorTest(List<A> aList, List<B> bList) {

        assertEx(() -> aList.listIterator(-1), "aList.listIterator(-1)",
                 IndexOutOfBoundsException.class);
        assertEx(() -> bList.listIterator(-1), "bList.listIterator(-1)",
                 IndexOutOfBoundsException.class);

        assertEx(() -> aList.listIterator(aList.size() + 1), "aList.listIterator(aList.size() + 1)",
                 IndexOutOfBoundsException.class);
        assertEx(() -> bList.listIterator(aList.size() + 1), "bList.listIterator(aList.size() + 1)",
                 IndexOutOfBoundsException.class);

        for (int i = 0; i <= aList.size(); i++) {
            ListIterator<A> a = aList.listIterator(i);
            ListIterator<B> b = bList.listIterator(i);

            assertLiEq(a, b, "at start (i = " + i + ")");

            while (a.hasNext()) {
                assertTrue("When a has a next, b should too (started at " + i + ")", b.hasNext());

                assertEquals("a.next should equal b.next (started at " + i + ")",
                             a.next(), b.next());

                assertLiEq(a, b, "after calling next()");
            }
            assertFalse("When a has no next, b shouldn't either (started at " + i + ")",
                        b.hasNext());

            assertLiEq(a, b, "after the last item");

            assertEx(a::next, "a.next() after the last item", NoSuchElementException.class);
            assertEx(b::next, "b.next() after the last item", NoSuchElementException.class);

            while (a.hasPrevious()) {
                assertTrue("When a hasPrevious, b should too. (started at " + i + ")",
                           b.hasPrevious());

                assertEquals("a.previous should equal b.previous (started at " + i + ")",
                             a.previous(), b.previous());

                assertLiEq(a, b, "after calling previous()");
            }
            assertFalse("When a has no previous, b shouldn't either (started at " + i + ")",
                        b.hasPrevious());

            assertLiEq(a, b, "before first item");

            assertEx(a::previous, "a.previous() before first item", NoSuchElementException.class);
            assertEx(b::previous, "b.previous() before first item", NoSuchElementException.class);
        }

        // Check that indexing works when we start with the previous, then switch to the next()
        for (int i = 0; i <= aList.size(); i++) {
            ListIterator<A> a = aList.listIterator(i);
            ListIterator<B> b = bList.listIterator(i);

            assertLiEq(a, b, "at start (i = " + i + ")");

            while (a.hasPrevious()) {
                assertTrue("When a hasPrevious, b should too. (started at " + i + ")",
                           b.hasPrevious());

                assertEquals("a.previous should equal b.previous (started at " + i + ")",
                             a.previous(), b.previous());

                assertLiEq(a, b, "after calling previous()");
            }
            assertFalse("When a has no previous, b shouldn't either (started at " + i + ")",
                        b.hasPrevious());

            assertLiEq(a, b, "before first item");

            assertEx(a::previous, "a.previous()", NoSuchElementException.class);
            assertEx(b::previous, "b.previous()", NoSuchElementException.class);

            while (a.hasNext()) {
                assertTrue("When a has a next, b should too (started at " + i + ")", b.hasNext());

                assertEquals("a.next should equal b.next (started at " + i + ")",
                             a.next(), b.next());

                assertLiEq(a, b, "after calling next()");
            }
            assertFalse("When a has no next, b shouldn't either (started at " + i + ")",
                        b.hasNext());

            assertLiEq(a, b, "after the last item");

            assertEx(a::next, "a.next()", NoSuchElementException.class);
            assertEx(b::next, "b.next()", NoSuchElementException.class);
        }
    }

    @Test public void listIteratorTest() {
        listIteratorTest(Arrays.asList(sticksAndStones), unList);
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
