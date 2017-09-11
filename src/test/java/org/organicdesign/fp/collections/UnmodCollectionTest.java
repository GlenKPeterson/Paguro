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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.junit.Test;

import static org.junit.Assert.*;

@SuppressWarnings("WeakerAccess")
public class UnmodCollectionTest {

    static class TestColl<T> implements UnmodCollection<T> {
        HashSet<T> items;
//        TestColl(Collection<T> is) { items = new HashSet<>(is); }
        TestColl(T[] is) { items = new HashSet<>(Arrays.asList(is)); }

        @Override public int size() { return items.size(); }

        @Override public boolean contains(Object o) {
            for (Object item : items) {
                if (Objects.equals(item, o)) { return true; }
            }
            return false;
        }

        @Override public UnmodIterator<T> iterator() {
            Iterator<T> iter = items.iterator();
            return new UnmodIterator<T>() {
                @Override public boolean hasNext() { return iter.hasNext(); }
                @Override public T next() { return iter.next(); }
            };
        }

        @Override public String toString() {
            StringBuilder sB = new StringBuilder("TestColl(");
            boolean isFirst = true;
            for (T item : this) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    sB.append(",");
                }
                sB.append(item);
            }
            return sB.append(")").toString();
        }
    }

    private static final String[] sticksAndStones = new String[] {
            "Sticks", "and", "stones", "will", "break", "my", "bones", "but", "tests",
            "will", "never", "hurt", "me." };

    // Fun Fact: This project used to be called, "UncleJim" because it just started out with
    // UNmodifiable COLLections (UN-COLL sounds like "Uncle").  The Jim part was similarly
    // for Java IMmutability (JIM).  But it sounded vaguely Brogrammerly, so the name was
    // changed to Paguro.
    private static final UnmodCollection<String> unColl = new TestColl<>(sticksAndStones);

    @Test public void containsTest() {
        for (String s : sticksAndStones) {
            assertTrue(unColl.contains(s));
        }
        assertFalse(unColl.contains("phrog"));
    }

    static String[] sticksMay = new String[] {"Sticks", "and", "stones", "MAY",
                                              "break", "my", "bones", "but", "tests",
                                              "will", "never", "hurt", "me."};

    @Test public void containsAllTest() {
        List<String> ls = Arrays.asList(sticksAndStones);
        assertTrue(ls.containsAll(unColl));
        assertTrue(unColl.containsAll(ls));

        List<String> ls2 = Arrays.asList("Sticks", "and", "stones", "will", "break", "my", "bones",
                                         "but", "tests", "will", "never", "hurt");
        assertFalse(ls2.containsAll(unColl));
        assertTrue(unColl.containsAll(ls2));

        List<String> ls3 = Arrays.asList("Sticks", "and", "stones", "will", "break", "my", "bones",
                                         "but", "tests", "will", "never", "hurt", "me.",
                                         "maybe");
        assertTrue(ls3.containsAll(unColl));
        assertFalse(unColl.containsAll(ls3));

        assertFalse(unColl.containsAll(Arrays.asList(sticksMay)));

        // OK, this was eye-opening.  The sticksMay list contains all the elements of the
        // sticksAndStones list, plus the additional element "MAY" - and the lists are the same
        // size.  How? Because "will" occurs twice.  The ultimate issue here is that equality
        // for lists and sets is not the same.  Lists are ordered and have duplicates.
        // sets have no duplicates and may or may not be ordered.
        assertTrue(Arrays.asList(sticksMay).containsAll(unColl));

        assertTrue(unColl.containsAll(null));
        assertTrue(unColl.containsAll(Collections.unmodifiableCollection(Collections.emptySet())));
        assertFalse(Collections.emptySet().containsAll(unColl));
    }

    // I don't think equals() can be implemented on a Collection.  It's the return type for
    // Map.values() which can have duplicates and may be ordered, or unordered.
//    @Test public void equalityTest() {
//        assertTrue(unColl.contains("will"));
//        assertFalse(unColl.contains("MAY"));
//
//        // Unequal due to distinct elements.
//        equalsDistinctHashCode(unColl,
//                               new TestColl<>(sticksAndStones),
//                               new TestColl<>(sticksAndStones),
//                               new TestColl<>(new String[] {"Sticks", "and", "stones", "MAY",
//                                                            "break", "my", "bones", "but", "tests",
//                                                            "will", "never", "hurt", "me."}));
//
//        // Unequal due to size (missing last element)
//        equalsDistinctHashCode(unColl,
//                               new TestColl<>(sticksAndStones),
//                               new TestColl<>(sticksAndStones),
//                               new TestColl<>(new String[] {"Sticks", "and", "stones", "will",
//                                                            "break", "my", "bones", "but", "tests",
//                                                            "will", "never", "hurt"}));
//    }

    static <T> void assertSetEqualsOnArrays(T[] as, T[] bs) {
        Set<T> aSet = new HashSet<>(Arrays.asList(as));
        Set<T> bSet = new HashSet<>(Arrays.asList(bs));
        assertTrue("set a.size() == b.size()", aSet.size() == bSet.size());
        assertTrue("set a.containsAll(b)", aSet.containsAll(bSet));
        assertTrue("set b.containsAll(a)", bSet.containsAll(aSet));
        assertTrue("set a.equals(b)", aSet.equals(bSet));
        assertTrue("set b.equals(a)", bSet.equals(aSet));
    }

    @Test public void toArrayTest() {
        assertSetEqualsOnArrays(sticksAndStones, unColl.toArray());
        assertSetEqualsOnArrays(sticksAndStones, unColl.toArray(new String[3]));
        assertSetEqualsOnArrays(sticksAndStones,
                                unColl.toArray(new String[unColl.size()]));

        // In order to show the class cast exception, we need to
        // 1. pass a smaller array so that an Object[] is created
        // 2. Force the return type to be a String[]
        // Merely running Arrays.AsList() on it is not enough to get the exception.
        String[] items = unColl.toArray(new String[1]);
        assertSetEqualsOnArrays(sticksAndStones, items);

        String[] result = unColl.toArray(new String[sticksAndStones.length + 3]);
        List<String> temp = new ArrayList<>(Arrays.asList(sticksAndStones));
        temp.add(null);

        String[] stonesAndNull = temp.toArray(new String[temp.size()]);
        assertSetEqualsOnArrays(stonesAndNull, result);
        assertEquals(sticksAndStones.length + 3, result.length);
        assertEquals(null, result[result.length - 3]);
        assertEquals(null, result[result.length - 2]);
        assertEquals(null, result[result.length - 1]);
    }

    @Test public void testDidley() {
        // for those of you who want 100% test coverage just on principle, this one's for you.
        assertFalse(unColl.isEmpty());
        assertTrue(new TestColl<>(new Object[0]).isEmpty());
    }

    @SuppressWarnings("deprecation")
    @Test (expected = UnsupportedOperationException.class)
    public void unsupportedOp01() { unColl.add("hi"); }

    @SuppressWarnings("deprecation")
    @Test (expected = UnsupportedOperationException.class)
    public void unsupportedOp02() { unColl.addAll(Arrays.asList("hi", "there")); }

    @SuppressWarnings("deprecation")
    @Test (expected = UnsupportedOperationException.class)
    public void unsupportedOp03() { unColl.clear(); }

    @SuppressWarnings("deprecation")
    @Test (expected = UnsupportedOperationException.class)
    public void unsupportedOp04() { unColl.remove("hi"); }

    @SuppressWarnings("deprecation")
    @Test (expected = UnsupportedOperationException.class)
    public void unsupportedOp05() { unColl.removeAll(Arrays.asList("hi", "there")); }

    @SuppressWarnings("deprecation")
    @Test (expected = UnsupportedOperationException.class)
    public void unsupportedOp06() { unColl.removeIf(item -> false); }

    @SuppressWarnings("deprecation")
    @Test (expected = UnsupportedOperationException.class)
    public void unsupportedOp07() { unColl.retainAll(Arrays.asList("hi", "there")); }
}
