package org.organicdesign.fp.collections;

import org.junit.Test;
import org.organicdesign.fp.Option;
import org.organicdesign.fp.permanent.Sequence;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;
import static org.organicdesign.fp.StaticImports.un;
import static org.organicdesign.fp.testUtils.EqualsContract.equalsDistinctHashCode;

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

    @Test public void moreAssoc() {
        PersistentHashSet<String> s1 = PersistentHashSet.empty();
        s1 = s1.put("one");
        assertEquals(1, s1.size());
        assertTrue(s1.contains("one"));
        assertFalse(s1.contains("two"));

        s1 = s1.put("two");
        assertEquals(2, s1.size());
        assertTrue(s1.contains("one"));
        assertTrue(s1.contains("two"));
        assertFalse(s1.contains("three"));

        // System.out.println("Two: " + s1);
        // System.out.println("Two map:" + s1.impl);

        s1 = s1.put("three");
        assertEquals(3, s1.size());
        assertTrue(s1.contains("one"));
        assertTrue(s1.contains("two"));
        assertTrue(s1.contains("three"));
        assertFalse(s1.contains("four"));

        // System.out.println("Three: " + s1);
        // System.out.println("Three map:" + s1.impl);

        s1 = s1.put("four");
        assertEquals(4, s1.size());
        assertTrue(s1.contains("one"));
        assertTrue(s1.contains("two"));
        assertTrue(s1.contains("three"));
        assertTrue(s1.contains("four"));
        assertFalse(s1.contains("five"));

        // System.out.println("Four: " + s1);

        // System.out.println("Four map:" + s1.impl);
        // System.out.println("s1.seq().toJavaList()" + s1.seq().toJavaList());

        s1 = s1.put("five");
        assertEquals(5, s1.size());
        assertTrue(s1.contains("one"));
        assertTrue(s1.contains("two"));
        assertTrue(s1.contains("three"));
        assertTrue(s1.contains("four"));
        assertTrue(s1.contains("five"));
        assertFalse(s1.contains("six"));

        s1 = s1.put("six");
        assertEquals(6, s1.size());
        assertTrue(s1.contains("one"));
        assertTrue(s1.contains("two"));
        assertTrue(s1.contains("three"));
        assertTrue(s1.contains("four"));
        assertTrue(s1.contains("five"));
        assertTrue(s1.contains("six"));
        assertFalse(s1.contains("seven"));

        PersistentHashSet<String> u = PersistentHashSet.empty();
        // System.out.println("Initial u: " + u);
        for (String s : new String[] { "one", "two", "three", "four", "five" }) {
            // System.out.println("item.get(): " + s);
            u = u.put(s);
            assertTrue(u.contains(s));
            // System.out.println("u: " + u.toString());
        }
        // System.out.println("Final u: " + u);
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

    @Test public void seq3() {
        PersistentHashSet<String> m1 = PersistentHashSet.of("c");
        assertEquals(Option.of("c"),
                     m1.seq().head());

        PersistentHashSet<String> m2 = PersistentHashSet.of("c", "b", "a");

        Set<Option<String>> s = new HashSet<>(Arrays.asList(Option.of("c"),
                                                            Option.of("b"),
                                                            Option.of("a")));

        Sequence<String> seq = m2.seq();
        Option o = seq.head();
        assertTrue(s.contains(o));
        s.remove(o);

        seq = seq.tail();
        o = seq.head();
        assertTrue(s.contains(o));
        s.remove(o);

        seq = seq.tail();
        o = seq.head();
        assertTrue(s.contains(o));
        s.remove(o);

        seq = seq.tail();
        o = seq.head();
        assertEquals(Option.none(), o);
    }

    @Test public void seqMore() {
        PersistentHashSet<String> m1 = PersistentHashSet.of("g", "f", "e", "d", "c", "b", "a");
        // System.out.println("m1.toString(): " + m1.toString());

        Set<String> s1 = new HashSet<>(Arrays.asList("a", "b", "c", "d", "e", "f", "g"));

        // System.out.println("s1: " + s1);

        Sequence<String> seq1 = m1.seq();
        Option<String> o1 = seq1.head();
        while (o1.isSome()) {
            String entry = o1.get();
            // System.out.println("entry: " + entry);
            assertTrue(s1.contains(entry));
            s1.remove(entry);
            seq1 = seq1.tail();
            o1 = seq1.head();
        }
        assertEquals(0, s1.size());
        assertTrue(s1.isEmpty());
    }

    @Test public void longerSeq() {
        // This is an assumed to work mutable set - the "control" for this test.
        Set<Integer> set = new HashSet<>();
        // This is the map being tested.
        ImSet<Integer> accum = PersistentHashSet.empty();

        int MAX = 1000;

        for (int i = 0; i < MAX; i++) {
            set.add(i);
            accum = accum.put(i);

            Option<Integer> o = accum.seq().head();
            assertTrue(o.isSome());
            //noinspection ConstantConditions
            assertTrue(o.get() instanceof Integer);
        }
        for (int i = 0; i < MAX; i++) {
            assertTrue(accum.contains(i));
        }

        Sequence<Integer> seq = accum.seq();
        for (int i = 0; i < MAX; i++) {
            Option<Integer> o = seq.head();

            assertTrue(set.contains(o.get()));
            set.remove(o.get());

            seq = seq.tail();
        }
//        System.out.println("seq: " + seq);
        assertFalse(seq.head().isSome());
        assertTrue(set.isEmpty());

//        println("accum: " + accum);
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

    @Test public void equality() {
        PersistentHashSet<String> s1 = PersistentHashSet.of("hello", "an", "work", "b", "the");

        Set<String> ss1 = new HashSet<>();
        ss1.add("the");
        ss1.add("b");
        ss1.add("work");
        ss1.add("an");
        ss1.add("hello");
        equalsDistinctHashCode(s1, ss1, un(ss1),
                               PersistentHashSet.of("hello", "an", "work", "the"));
    }

//    // TODO: Finish this!
//    @Test public void testToString() {
//        PersistentHashSet<String> s2 = PersistentHashSet.ofComp(STR_LEN_COMP);
//
//        assertEquals("PersistentHashSet(b,an,the,work,hello)",
//                     s2.put("hello").put("an").put("work").put("b").put("the").toString());
//    }

}
