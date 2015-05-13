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

import java.util.Comparator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.organicdesign.fp.function.Function2;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class PersistentTreeSetTest {

    public static final Comparator<String> STR_LEN_COMP = (a, b) -> a.length() - b.length();


    @Test public void assocAndGet() {
        PersistentTreeSet<String> s1 = PersistentTreeSet.empty();
        assertTrue(s1.isEmpty());

        PersistentTreeSet<String> s2 = s1.put("one");
        assertFalse(s2.isEmpty());

        // Prove m1 unchanged
        assertEquals(0, s1.size());
        assertFalse(s1.contains("one"));

        // Show m2 correct.
        assertEquals(1, s2.size());

        assertTrue(s2.contains("one"));
        assertFalse(s2.contains("two"));

        //noinspection EqualsWithItself
        assertTrue(s1.equals(s1));

        //noinspection EqualsWithItself
        assertTrue(s2.equals(s2));
        assertNotEquals(s1.hashCode(), s2.hashCode());
        assertFalse(s1.equals(s2));
        assertFalse(s2.equals(s1));

        PersistentTreeSet<Integer> s3 = PersistentTreeSet.<Integer>empty().put(1).put(2).put(3);

        assertEquals(3, s3.size());
        assertTrue(s3.contains(1));
        assertTrue(s3.contains(2));
        assertTrue(s3.contains(3));
        assertFalse(s3.contains(4));

        assertArrayEquals(new Integer[]{1, 2, 3}, s3.toArray());

        assertArrayEquals(new Integer[]{1, 2, 3},
                          PersistentTreeSet.<Integer>empty().put(3).put(2).put(1).toArray());
    }

    @Test public void disjoin() {
        PersistentTreeSet<String> s1 = PersistentTreeSet.empty();
        assertTrue(s1.isEmpty());

        PersistentTreeSet<String> s2 = s1.put("one");
        assertFalse(s2.isEmpty());

        PersistentTreeSet<String> s3 = s1.disjoin("one");
        assertEquals(0, s3.size());
        assertTrue(s3.isEmpty());
        assertEquals(s1.hashCode(), s3.hashCode());
        assertTrue(s1.equals(s3));
        assertTrue(s3.equals(s1));

        PersistentTreeSet<String> s4 = s2.disjoin("two");
        assertEquals(1, s4.size());
        assertEquals(s2.hashCode(), s4.hashCode());
        assertTrue(s2.equals(s4));
        assertTrue(s4.equals(s2));
    }

    @Test public void ordering() {
        PersistentTreeSet<Integer> s1 = PersistentTreeSet.of(5, 2, 4, 1, 3);
        assertEquals(5, s1.size());
        assertEquals(Integer.valueOf(1), s1.first());
        assertEquals(Integer.valueOf(5), s1.last());
        assertArrayEquals(new Integer[]{1, 2, 3, 4, 5},
                          s1.toTypedArray());

        assertArrayEquals(new Integer[] {1,2,3,4,5},
                          s1.subSet(Integer.MIN_VALUE, Integer.MAX_VALUE).toTypedArray());

        assertArrayEquals(new Integer[] {1,2,3,4,5},
                          s1.subSet(-99, 99).toTypedArray());

        assertArrayEquals(new Integer[]{1, 2, 3, 4, 5},
                          s1.subSet(1, 6).toTypedArray());

        assertArrayEquals(new Integer[]{1, 2, 3, 4},
                          s1.subSet(1, 5).toTypedArray());

        assertArrayEquals(new Integer[]{2, 3, 4, 5},
                          s1.subSet(2, 6).toTypedArray());

        assertArrayEquals(new Integer[]{3},
                          s1.subSet(3, 4).toTypedArray());

        assertArrayEquals(new Integer[0],
                          s1.subSet(3, 3).toTypedArray());

        assertEquals(Function2.defaultComparator(), s1.comparator());


        PersistentTreeSet<String> s2 = PersistentTreeSet.ofComp(STR_LEN_COMP,
                                                                "hello", "an", "work", "b", "the");
        assertEquals(STR_LEN_COMP, s2.comparator());
        assertEquals(5, s2.size());
        assertEquals("b", s2.first());
        assertEquals("hello", s2.last());

        assertEquals(STR_LEN_COMP, s2.subSet("", "                 ").comparator());

        assertArrayEquals(new String[]{"b", "an", "the", "work", "hello"},
                          s2.toTypedArray());

        assertArrayEquals(new String[]{"b", "an", "the", "work", "hello"},
                          s2.subSet("", "                 ").toTypedArray());

        assertArrayEquals(new String[] {"b", "an", "the", "work", "hello"},
                          s2.subSet("", "._._._").toTypedArray());

        assertArrayEquals(new String[]{"b", "an", "the", "work", "hello"},
                          s2.subSet("z", "aaaaaa").toTypedArray());

        assertEquals(STR_LEN_COMP, s2.subSet("a", "four").comparator());

        assertArrayEquals(new String[]{"b", "an", "the"},
                          s2.subSet("a", "four").toTypedArray());

        assertArrayEquals(new String[] {"an", "the", "work", "hello"},
                          s2.subSet("UH", "SLDFKJS").toTypedArray());

        assertArrayEquals(new String[] {"the"},
                          s2.subSet("THE", "JUNK").toTypedArray());

        assertArrayEquals(new String[0],
                          s2.subSet("the", "the").toTypedArray());

        assertEquals(STR_LEN_COMP, s2.comparator());
    }

    // TODO: Finish this!
    @Test public void testToString() {
        PersistentTreeSet<String> s2 = PersistentTreeSet.ofComp(STR_LEN_COMP);

        assertEquals("PersistentTreeSet(b,an,the,work,hello)",
                     s2.put("hello").put("an").put("work").put("b").put("the").toString());
    }

}
