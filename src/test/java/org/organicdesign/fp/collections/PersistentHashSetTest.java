package org.organicdesign.fp.collections;

import org.junit.Test;

import static org.junit.Assert.*;

public class PersistentHashSetTest {
    @Test
    public void assocAndGet() {
        PersistentHashSet<String> s1 = PersistentHashSet.empty();
        assertTrue(s1.isEmpty());

        PersistentHashSet<String> s2 = s1.put("one");
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

        PersistentHashSet<Integer> s3 = PersistentHashSet.<Integer>empty().put(1).put(2).put(3);

        assertEquals(3, s3.size());
        assertTrue(s3.contains(1));
        assertTrue(s3.contains(2));
        assertTrue(s3.contains(3));
        assertFalse(s3.contains(4));

        assertArrayEquals(new Integer[]{1, 2, 3}, s3.toArray());

        assertArrayEquals(new Integer[]{1, 2, 3},
                          PersistentHashSet.<Integer>empty().put(3).put(2).put(1).toArray());
    }

    @Test public void disjoin() {
        PersistentHashSet<String> s1 = PersistentHashSet.empty();
        assertTrue(s1.isEmpty());

        PersistentHashSet<String> s2 = s1.put("one");
        assertFalse(s2.isEmpty());

        PersistentHashSet<String> s3 = s1.disjoin("one");
        assertEquals(0, s3.size());
        assertTrue(s3.isEmpty());
        assertEquals(s1.hashCode(), s3.hashCode());
        assertTrue(s1.equals(s3));
        assertTrue(s3.equals(s1));

        PersistentHashSet<String> s4 = s2.disjoin("two");
        assertEquals(1, s4.size());
        assertEquals(s2.hashCode(), s4.hashCode());
        assertTrue(s2.equals(s4));
        assertTrue(s4.equals(s2));
    }

//    @Test public void ordering() {
//        PersistentHashSet<Integer> s1 = PersistentHashSet.of(5, 2, 4, 1, 3);
//        assertEquals(5, s1.size());
//        assertEquals(Integer.valueOf(1), s1.first());
//        assertEquals(Integer.valueOf(5), s1.last());
//        assertArrayEquals(new Integer[]{1, 2, 3, 4, 5},
//                          s1.toTypedArray());
//
//        assertArrayEquals(new Integer[] {1,2,3,4,5},
//                          s1.subSet(Integer.MIN_VALUE, Integer.MAX_VALUE).toTypedArray());
//
//        assertArrayEquals(new Integer[] {1,2,3,4,5},
//                          s1.subSet(-99, 99).toTypedArray());
//
//        assertArrayEquals(new Integer[]{1, 2, 3, 4, 5},
//                          s1.subSet(1, 6).toTypedArray());
//
//        assertArrayEquals(new Integer[]{1, 2, 3, 4},
//                          s1.subSet(1, 5).toTypedArray());
//
//        assertArrayEquals(new Integer[]{2, 3, 4, 5},
//                          s1.subSet(2, 6).toTypedArray());
//
//        assertArrayEquals(new Integer[]{3},
//                          s1.subSet(3, 4).toTypedArray());
//
//        assertArrayEquals(new Integer[0],
//                          s1.subSet(3, 3).toTypedArray());
//
//        assertNull(s1.comparator());
//
//
//        PersistentHashSet<String> s2 = PersistentHashSet.ofComp(STR_LEN_COMP,
//                                                                "hello", "an", "work", "b", "the");
//        assertEquals(STR_LEN_COMP, s2.comparator());
//        assertEquals(5, s2.size());
//        assertEquals("b", s2.first());
//        assertEquals("hello", s2.last());
//
//        assertEquals(STR_LEN_COMP, s2.subSet("", "                 ").comparator());
//
//        assertArrayEquals(new String[]{"b", "an", "the", "work", "hello"},
//                          s2.toTypedArray());
//
//        assertArrayEquals(new String[]{"b", "an", "the", "work", "hello"},
//                          s2.subSet("", "                 ").toTypedArray());
//
//        assertArrayEquals(new String[] {"b", "an", "the", "work", "hello"},
//                          s2.subSet("", "._._._").toTypedArray());
//
//        assertArrayEquals(new String[]{"b", "an", "the", "work", "hello"},
//                          s2.subSet("z", "aaaaaa").toTypedArray());
//
//        assertEquals(STR_LEN_COMP, s2.subSet("a", "four").comparator());
//
//        assertArrayEquals(new String[]{"b", "an", "the"},
//                          s2.subSet("a", "four").toTypedArray());
//
//        assertArrayEquals(new String[] {"an", "the", "work", "hello"},
//                          s2.subSet("UH", "SLDFKJS").toTypedArray());
//
//        assertArrayEquals(new String[] {"the"},
//                          s2.subSet("THE", "JUNK").toTypedArray());
//
//        assertArrayEquals(new String[0],
//                          s2.subSet("the", "the").toTypedArray());
//
//        assertEquals(STR_LEN_COMP, s2.comparator());
//    }

//    @Test public void equality() {
//        PersistentHashSet<String> s1 = PersistentHashSet.of("hello", "an", "work", "b", "the");
//
//        Set<String> ss1 = new HashSet<>();
//        ss1.add("the");
//        ss1.add("b");
//        ss1.add("work");
//        ss1.add("an");
//        ss1.add("hello");
//        equalsDistinctHashCode(s1, ss1, un(ss1),
//                               PersistentHashSet.of("hello", "an", "work", "the"));
//
////        // Really, you need to read the JavaDoc for PersistentHashSet.equals() to understand the bizarre notion of
////        // equality being checked here.
////        equalsDistinctHashCode(s1, ss1, un(ss1),
////                               PersistentHashSet.ofComp(STR_LEN_COMP,
////                                                        "helloz", "an", "work", "b", "the"));
////
////        PersistentHashSet<String> s2 = PersistentHashSet.ofComp(STR_LEN_COMP,
////                                                                "hello", "an", "work", "b", "the");
////
////        // This illustrates the bug in java
////        Set<String> ss = new HashSet<>();
////        ss.add("the");
////        ss.add("b");
////        ss.add("work");
////        ss.add("an");
////        ss.add("hello");
////
////        Set<String> ss2 = new HashSet<>();
////        ss2.add("the");
////        ss2.add("b");
////        ss2.add("work");
////        ss2.add("an");
////        ss2.add("Hello");
////
////        // Yeah, totally bizarre.  Doesn't care that the order is different.
////        assertEquals(ss, ss2);
////        assertEquals(ss2, ss);
////
////
////        equalsDistinctHashCode(s2, ss, un(ss),
////                               PersistentHashSet.ofComp(STR_LEN_COMP, "helloz", "an", "work", "the"));
////
//////        assertEquals(s2, yadda);
//////        assertEquals(ss, yadda);
//////        assertEquals(un(ss), yadda);
//////
//////        assertNotEquals(yadda, s2);
//////        assertNotEquals(yadda, ss);
//////        assertNotEquals(yadda, un(ss));
////
////        equalsDistinctHashCode(s2, ss, un(ss),
////                               PersistentHashSet.of("an", "helloz", "work", "b", "the"));
////
////        equalsSameHashCode(s2,
////                           PersistentHashSet.ofComp(STR_LEN_COMP, "an", "hello", "work", "the").put("b"),
////                           PersistentHashSet.ofComp(STR_LEN_COMP, "an", "b", "work").put("hello").put("the"),
////                           PersistentHashSet.of("an", "hello", "work", "b", "the"));
//
//    }

//    // TODO: Finish this!
//    @Test public void testToString() {
//        PersistentHashSet<String> s2 = PersistentHashSet.ofComp(STR_LEN_COMP);
//
//        assertEquals("PersistentHashSet(b,an,the,work,hello)",
//                     s2.put("hello").put("an").put("work").put("b").put("the").toString());
//    }

}
