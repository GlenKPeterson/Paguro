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
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.organicdesign.fp.TestUtilities;

import static org.junit.Assert.*;
import static org.organicdesign.fp.StaticImportsKt.vec;
import static org.organicdesign.fp.TestUtilities.compareIterators;
import static org.organicdesign.fp.TestUtilities.serializeDeserialize;
import static org.organicdesign.testUtils.EqualsContract.equalsDistinctHashCode;
import static org.organicdesign.testUtils.EqualsContract.equalsSameHashCode;

@RunWith(JUnit4.class)
public class PersistentTreeSetTest {

    public static final Comparator<String> STR_LEN_COMP =
            (a, b) -> Integer.compare(a.length(), b.length());

    @Test public void javaBug() {
        // This illustrates the bug in java
        SortedSet<String> ss = new TreeSet<>(STR_LEN_COMP);
        ss.add("bye");
        ss.add("hello");

        SortedSet<String> ss2 = new TreeSet<>();
        ss2.add("bye");
        ss2.add("12345");

        // Uses comparator.compare(a, b) == 0 to define equality.  This is good.
        assertNotEquals(ss2, ss);

        // Doesn't care that the order is different.  This is the bug!
        assertEquals(ss, ss2);

        // Because ordering is a critical component of SortedSets, they should not be compared to Sets.
        // Yet TreeSet TreeSet inherits .equals() from AbstractSet, which doesn't care about comparators
        // or ordering.  It relies on size() and containsAll(). containsAll() calls TreeSet.contains()
        // which effectively uses its comparator.compare(a,b) == 0 instead of equals.  TreeSet.contains()
        // is correct, because the comparator rules all the other operations on this TreeSet.
        // Mixing equals() with it would muddy the waters.
        //
        // The best fix would be for TreeSet to implement equals() to take into account ordering.
        // So the subtle issue here is that ordering (whether the comparator returns positive or negative)
        // doesn't matter.  But equality is still determined by whether or not the comparator returns 0.  Not by a
        // call to equals().  That is incredibly weird.  I don't even know what to do about that.
    }

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

        PersistentTreeSet<String> s3 = s1.without("one");
        assertEquals(0, s3.size());
        assertTrue(s3.isEmpty());
        assertEquals(s1.hashCode(), s3.hashCode());
        assertTrue(s1.equals(s3));
        assertTrue(s3.equals(s1));

        PersistentTreeSet<String> s4 = s2.without("two");
        assertEquals(1, s4.size());
        assertEquals(s2.hashCode(), s4.hashCode());
        assertTrue(s2.equals(s4));
        assertTrue(s4.equals(s2));
    }

    @Test public void ordering() {
        PersistentTreeSet<Integer> s1 = PersistentTreeSet.of(Arrays.asList(5, 2, 4, 1, 3));
        assertEquals(5, s1.size());
        assertEquals(Integer.valueOf(1), s1.first());
        assertTrue(s1.head().isSome());
        assertEquals(Integer.valueOf(1), s1.head().get());

        compareIterators(Arrays.asList(2,3,4,5).iterator(), s1.tailSet(2).iterator());

        assertEquals(Integer.valueOf(5), s1.last());
        assertArrayEquals(new Integer[]{1, 2, 3, 4, 5},
                          s1.toArray());

        assertArrayEquals(new Integer[] {1,2,3,4,5},
                          s1.subSet(Integer.MIN_VALUE, Integer.MAX_VALUE).toArray());

        assertArrayEquals(new Integer[] {1,2,3,4,5},
                          s1.subSet(-99, 99).toArray());

        assertArrayEquals(new Integer[]{1, 2, 3, 4, 5},
                          s1.subSet(1, 6).toArray());

        assertArrayEquals(new Integer[]{1, 2, 3, 4},
                          s1.subSet(1, 5).toArray());

        assertArrayEquals(new Integer[]{2, 3, 4, 5},
                          s1.subSet(2, 6).toArray());

        assertArrayEquals(new Integer[]{3},
                          s1.subSet(3, 4).toArray());

        assertArrayEquals(new Integer[0],
                          s1.subSet(3, 3).toArray());

        assertNull(s1.comparator());


        PersistentTreeSet<String> s2 = PersistentTreeSet.ofComp(STR_LEN_COMP,
                                                                Arrays.asList("hello", "an", "work", "b", "the"));
        assertEquals(STR_LEN_COMP, s2.comparator());
        assertEquals(5, s2.size());
        assertEquals("b", s2.first());
        assertEquals("hello", s2.last());

        assertEquals(STR_LEN_COMP, s2.subSet("", "                 ").comparator());

        assertArrayEquals(new String[]{"b", "an", "the", "work", "hello"},
                          s2.toArray());

        assertArrayEquals(new String[]{"b", "an", "the", "work", "hello"},
                          s2.subSet("", "                 ").toArray());

        assertArrayEquals(new String[] {"b", "an", "the", "work", "hello"},
                          s2.subSet("", "._._._").toArray());

        assertArrayEquals(new String[]{"b", "an", "the", "work", "hello"},
                          s2.subSet("z", "aaaaaa").toArray());

        assertEquals(STR_LEN_COMP, s2.subSet("a", "four").comparator());

        assertArrayEquals(new String[]{"b", "an", "the"},
                          s2.subSet("a", "four").toArray());

        assertArrayEquals(new String[] {"an", "the", "work", "hello"},
                          s2.subSet("UH", "SLDFKJS").toArray());

        assertArrayEquals(new String[] {"the"},
                          s2.subSet("THE", "JUNK").toArray());

        assertArrayEquals(new String[0],
                          s2.subSet("the", "the").toArray());

        assertEquals(STR_LEN_COMP, s2.comparator());
    }

    @Test public void serializeEmptyTest() {
        PersistentTreeSet<String> e = PersistentTreeSet.empty();
        assertEquals(1, e.put("hello").size());
        assertEquals(1, serializeDeserialize(e).put("hello").size());
    }

    @Test public void serializationTest() throws Exception {
        PersistentTreeSet<String> s1 = serializeDeserialize(PersistentTreeSet.empty());
        assertTrue(s1.isEmpty());

        PersistentTreeSet<String> s2 = serializeDeserialize(s1.put("one"));
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

        PersistentTreeSet<Integer> s3 = serializeDeserialize(PersistentTreeSet.<Integer>empty().put(1)).put(2).put(3);

        assertEquals(3, s3.size());
        assertTrue(s3.contains(1));
        assertTrue(s3.contains(2));
        assertTrue(s3.contains(3));
        assertFalse(s3.contains(4));

        assertArrayEquals(new Integer[]{1, 2, 3}, s3.toArray());

        PersistentTreeSet<String> pts1 = PersistentTreeSet.of(Arrays.asList("hello", "an", "work", "b", "the"));
        PersistentTreeSet<String> pts2 = serializeDeserialize(pts1);
        assertEquals(pts1, pts2);

        PersistentTreeSet<Integer> pts3 = PersistentTreeSet.empty();
        pts3 = pts3.put(7).put(3).put(1).put(6).put(5).put(4).put(2);
        PersistentTreeSet<Integer> pts4 = serializeDeserialize(pts3);
        assertEquals(pts3, pts4);

        UnmodSortedIterator<Integer> i3 = pts3.iterator();
        UnmodSortedIterator<Integer> i4 = pts4.iterator();
        while (i3.hasNext()) {
            assertTrue(i4.hasNext());
            Integer item3 = i3.next();
            Integer item4 = i4.next();
            assertEquals(item3, item4);
        }
        assertFalse(i4.hasNext());

        TestUtilities.assertEx(() -> serializeDeserialize(PersistentTreeSet
                                                                  .ofComp(Integer::compare,
                                                                          Arrays.asList(1, 2, 3))),
                               "PersistentTreeSet", IllegalStateException.class);
    }

    @Test public void equality() {
        PersistentTreeSet<String> s1 = PersistentTreeSet.of(Arrays.asList("hello", "an", "work", "b", "the"));

        SortedSet<String> ss1 = new TreeSet<>();
        ss1.add("the");
        ss1.add("b");
        ss1.add("work");
        ss1.add("an");
        ss1.add("hello");
        equalsDistinctHashCode(s1, ss1, Collections.unmodifiableSortedSet(ss1),
                               PersistentTreeSet.of(Arrays.asList("hello", "an", "work", "the")));

        // Really, you need to read the JavaDoc for PersistentTreeSet.equals() to understand the bizarre notion of
        // equality being checked here.
        equalsDistinctHashCode(s1, ss1, Collections.unmodifiableSortedSet(ss1),
                               PersistentTreeSet.ofComp(STR_LEN_COMP,
                                                        Arrays.asList("helloz", "an", "work", "b", "the")));

        PersistentTreeSet<String> s2 = PersistentTreeSet.ofComp(STR_LEN_COMP,
                                                                Arrays.asList("hello", "an", "work", "b", "the"));

        // This illustrates the bug in java
        SortedSet<String> ss = new TreeSet<>(STR_LEN_COMP);
        ss.add("the");
        ss.add("b");
        ss.add("work");
        ss.add("an");
        ss.add("hello");
//
//        SortedSet<String> ss2 = new TreeSet<>();
//        ss2.add("the");
//        ss2.add("b");
//        ss2.add("work");
//        ss2.add("an");
//        ss2.add("Hello");
//
//        // Yeah, totally bizarre.  Doesn't care that the order is different.
//        assertEquals(ss, ss2);
//        assertEquals(ss2, ss);
//
//
        equalsDistinctHashCode(s2, ss, Collections.unmodifiableSortedSet(ss),
                               PersistentTreeSet.ofComp(STR_LEN_COMP,
                                                        Arrays.asList("helloz", "an", "work", "the")));

//        assertEquals(s2, yadda);
//        assertEquals(ss, yadda);
//        assertEquals(unmodSortedSet(ss), yadda);
//
//        assertNotEquals(yadda, s2);
//        assertNotEquals(yadda, ss);
//        assertNotEquals(yadda, unmodSortedSet(ss));

        equalsDistinctHashCode(s2, ss, Collections.unmodifiableSortedSet(ss),
                               PersistentTreeSet.of(Arrays.asList("an", "helloz", "work", "b", "the")));

        equalsSameHashCode(s2,
                           PersistentTreeSet.ofComp(STR_LEN_COMP,
                                                    vec("an", "hello", "work", "the")).put("b"),
                           PersistentTreeSet.ofComp(STR_LEN_COMP,
                                                    vec("an", "b", "work")).put("hello").put("the"),
                           PersistentTreeSet.of(vec("an", "hello", "work", "b", "the")));

    }

    // TODO: Finish this!
    @Test public void testToString() {
        PersistentTreeSet<String> s2 = PersistentTreeSet.ofComp(STR_LEN_COMP);

        assertEquals("PersistentTreeSet(\"b\",\"an\",\"the\",\"work\",\"hello\")",
                     s2.put("hello").put("an").put("work").put("b").put("the").toString());
    }

}
