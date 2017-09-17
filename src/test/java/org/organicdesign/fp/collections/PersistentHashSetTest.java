package org.organicdesign.fp.collections;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.organicdesign.fp.oneOf.Option;

import static org.junit.Assert.*;
import static org.organicdesign.fp.StaticImports.vec;
import static org.organicdesign.fp.TestUtilities.serializeDeserialize;
import static org.organicdesign.fp.collections.PersistentHashSetTest.Ctx.mod3Eq;
import static org.organicdesign.testUtils.EqualsContract.equalsDistinctHashCode;

public class PersistentHashSetTest {

    static <K> void setIterTest(Set<K> c, Iterator<? extends K> test) {
        Set<K> control = new HashSet<>();
        control.addAll(c);
        while (test.hasNext()) {
            K testK = test.next();
            assertTrue(control.contains(testK));
            control.remove(testK);
        }
        assertEquals(0, control.size());
    }

    @Test
    public void assocAndGet() throws Exception {
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

        // This makes no sense to me.  Order is not meant to be preserved.
        // Not sure what I was thinking, but until it breaks, I won't worry.
        assertArrayEquals(new Integer[]{1, 2, 3}, s3.toArray());

        assertArrayEquals(new Integer[]{1, 2, 3},
                          PersistentHashSet.<Integer>empty().put(3).put(2).put(1).toArray());

        PersistentHashSet<Integer> ser = serializeDeserialize(s3);
        assertEquals(ser, s3);
        assertArrayEquals(new Integer[]{1, 2, 3}, ser.toArray());

        assertArrayEquals(new Integer[]{1, 2, 3},
                          serializeDeserialize(PersistentHashSet.<Integer>empty().put(3).put(2).put(1))
                                  .toArray());

    }

    @Test public void moreAssoc() throws Exception {
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
        // System.out.println("s1.seq().toMutList()" + s1.seq().toMutList());

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
        String[] vals = new String[] { "one", "two", "three", "four", "five" };
        for (String s : vals) {
            // System.out.println("item.get(): " + s);
            u = u.put(s);
            assertTrue(u.contains(s));
            // System.out.println("u: " + u.toString());
        }
        // System.out.println("Final u: " + u);

        PersistentHashSet<String> ser = serializeDeserialize(u);
        assertEquals(u, ser);
        for (String s : vals) {
            assertTrue(ser.contains(s));
        }
    }

    @Test public void disjoin() {
        PersistentHashSet<String> s1 = PersistentHashSet.empty();
        assertTrue(s1.isEmpty());

        PersistentHashSet<String> s2 = s1.put("one");
        assertFalse(s2.isEmpty());

        PersistentHashSet<String> s3 = s1.without("one");
        assertEquals(0, s3.size());
        assertTrue(s3.isEmpty());
        assertEquals(s1.hashCode(), s3.hashCode());
        assertTrue(s1.equals(s3));
        assertTrue(s3.equals(s1));

        PersistentHashSet<String> s4 = s2.without("two");
        assertEquals(1, s4.size());
        assertEquals(s2.hashCode(), s4.hashCode());
        assertTrue(s2.equals(s4));
        assertTrue(s4.equals(s2));
    }

    @Test public void seq3() {
        PersistentHashSet<String> m1 = PersistentHashSet.of(vec("c"));
        assertEquals(Option.some("c"),
                     m1.head());

        PersistentHashSet<String> m2 = PersistentHashSet.of(vec("c", "b", "a"));

        Set<Option<String>> s = new HashSet<>(Arrays.asList(Option.some("c"),
                                                            Option.some("b"),
                                                            Option.some("a")));

        UnmodIterable<String> seq = m2;
        Option o = seq.head();
        assertTrue(s.contains(o));
        s.remove(o);

        seq = seq.drop(1);
        o = seq.head();
        assertTrue(s.contains(o));
        s.remove(o);

        seq = seq.drop(1);
        o = seq.head();
        assertTrue(s.contains(o));
        s.remove(o);

        seq = seq.drop(1);
        o = seq.head();
        assertEquals(Option.none(), o);
    }

    @Test public void seqMore() {
        PersistentHashSet<String> m1 = PersistentHashSet.of(vec("g", "f", "e", "d", "c", "b", "a"));
        // System.out.println("m1.toString(): " + m1.toString());

        Set<String> s1 = new HashSet<>(Arrays.asList("a", "b", "c", "d", "e", "f", "g"));

        // System.out.println("s1: " + s1);

        UnmodIterable<String> seq1 = m1;
        Option<String> o1 = seq1.head();
        while (o1.isSome()) {
            String entry = o1.get();
            // System.out.println("entry: " + entry);
            assertTrue(s1.contains(entry));
            s1.remove(entry);
            seq1 = seq1.drop(1);
            o1 = seq1.head();
        }
        assertEquals(0, s1.size());
        assertTrue(s1.isEmpty());
    }

    @Test public void longerSeq() throws Exception {
        // This is an assumed to work mutable set - the "control" for this test.
        Set<Integer> set = new HashSet<>();
        // This is the map being tested.
        ImSet<Integer> accum = PersistentHashSet.empty();

        int MAX = 1000;

        for (int i = 0; i < MAX; i++) {
            set.add(i);
            accum = accum.put(i);

            Option<Integer> o = accum.head();
            assertTrue(o.isSome());
            //noinspection ConstantConditions
            assertTrue(o.get() instanceof Integer);
        }

        ImSet<Integer> ser = serializeDeserialize(accum);
        assertEquals(accum, ser);

        for (int i = 0; i < MAX; i++) {
            assertTrue(accum.contains(i));
            assertTrue(ser.contains(i));
        }

        UnmodIterable<Integer> seq = accum;
        for (int i = 0; i < MAX; i++) {
            Option<Integer> o = seq.head();

            assertTrue(set.contains(o.get()));
            set.remove(o.get());

            seq = seq.drop(1);
        }
//        System.out.println("seq: " + seq);
        assertFalse(seq.head().isSome());
        assertTrue(set.isEmpty());

//        println("accum: " + accum);
    }

    @Test public void mutableTest() {
        Set<Integer> control = new HashSet<>();
        MutSet<Integer> test = PersistentHashSet.emptyMutable();
        assertEquals(control.size(), test.size());
        assertEquals(control.contains(-1), test.contains(-1));

        final int SOME = 2000;
        for (int i = 0; i < SOME; i++) {
            assertEquals(control.isEmpty(), test.isEmpty());
            control.add(i);
            test.put(i);
            assertEquals(control.size(), test.size());
        }

        assertEquals(control.contains(-1), test.contains(-1));
        assertEquals(control.contains(999), test.contains(999));

        setIterTest(control, test.iterator());
        // Reversed test.
        setIterTest(test, control.iterator());
        ImSet<Integer> immutable = test.immutable();

        setIterTest(control, immutable.iterator());
        setIterTest(control, serializeDeserialize(immutable).iterator());

        test = immutable.mutable();

        for (int i = 0; i < SOME; i++) {
            control.remove(i);
            test.without(i);
            assertEquals(control.size(), test.size());
        }

        List<Integer> others = Arrays.asList(SOME + 2, SOME + 3, SOME + 4);
        control.addAll(others);
        test = test.union(others);
        assertEquals(control.size(), test.size());
        setIterTest(control, test.iterator());
        assertEquals(control, test);
    }

    enum Ctx implements ComparisonContext<Integer> {
        mod3Eq {
            @Override public int compare(Integer o1, Integer o2) { return (o1 % 3) - (o2 % 3); }
            @Override public int hash(Integer integer) { return integer % 3; }
        }
    }

    @Test public void equator() throws Exception {
        PersistentHashSet<Integer> s1 = PersistentHashSet.ofEq(mod3Eq, vec(5, 2, 4, 1, 3));
//        System.out.println("s1: " + s1);
        assertEquals(3, s1.size());
        Set<Integer> hs = new HashSet<>();
        hs.add(3);
        hs.add(4);
        hs.add(5);
        assertEquals(hs.size(), s1.size());

        UnmodIterable<Integer> seq = s1;
        Integer item = seq.head().get();
        assertTrue(hs.contains(item));

        hs.remove(item);
        s1 = s1.without(item);
        seq = seq.drop(1);
        item = seq.head().get();
        assertEquals(hs.size(), s1.size());
        assertTrue(hs.contains(item));

        hs.remove(item);
        s1 = s1.without(item);
        seq = seq.drop(1);
        item = seq.head().get();
        assertEquals(hs.size(), s1.size());
        assertTrue(hs.contains(item));

        hs.remove(item);
        s1 = s1.without(item);
        seq = seq.drop(1);
        assertFalse(seq.iterator().hasNext());
        assertEquals(0, hs.size());
        assertEquals(0, s1.size());

        assertEquals(mod3Eq, s1.equator());

        assertEquals(s1, serializeDeserialize(s1));
    }

    @Test public void equality() {
        PersistentHashSet<String> s1 = PersistentHashSet.of(vec("hello", "an", "work", "b", "the"));

        Set<String> ss1 = new HashSet<>();
        ss1.add("the");
        ss1.add("b");
        ss1.add("work");
        ss1.add("an");
        ss1.add("hello");
        equalsDistinctHashCode(s1, ss1, Collections.unmodifiableSet(ss1),
                               PersistentHashSet.of(vec("hello", "an", "work", "the")));
    }

//    // TODO: Finish this!
//    @Test public void testToString() {
//        PersistentHashSet<String> s2 = PersistentHashSet.ofComp(STR_LEN_COMP);
//
//        assertEquals("PersistentHashSet(b,an,the,work,hello)",
//                     s2.put("hello").put("an").put("work").put("b").put("the").toString());
//    }

}
