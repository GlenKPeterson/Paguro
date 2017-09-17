package org.organicdesign.fp.collections;

import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;
import static org.organicdesign.fp.FunctionUtils.ordinal;

/**
 Created by gpeterso on 9/13/16.
 */
public class ImUnsortedSetTest {
    static class TestSet<E> implements ImSet<E> {
        static <T> Set<T> dup(Collection<T> in) {
            Set<T> out = new HashSet<>();
            out.addAll(in);
            return out;
        }

        private final Set<E> inner;
        TestSet(Collection<E> s) {
            inner = new HashSet<>();
            inner.addAll(s);
        }

        @Override public MutSet<E> mutable() {
            return new MutSetTest.TestSet<>(dup(inner));
        }

        @Override public ImSet<E> put(E val){
            Set<E> next = dup(inner);
            next.add(val);
            return new TestSet<>(next);
        }

        @Override public ImSet<E> without(E key) {
            Set<E> next = dup(inner);
            next.remove(key);
            return new ImUnsortedSetTest.TestSet<>(next);
        }

        @Override public int size() { return inner.size(); }

        @Override public boolean contains(Object o) { return inner.contains(o); }

        @Override public UnmodIterator<E> iterator() {
            return new UnmodIterator<E>() {
                Iterator<E> iter = inner.iterator();
                @Override public boolean hasNext() { return iter.hasNext(); }
                @Override public E next() { return iter.next(); }
            };
        }

        /** Note: not reflexive with TreeSet.equals() */
        @Override public boolean equals(Object o) {
            if (this == o) { return true; }
            if ( !(o instanceof Set) ) { return false; }
            Set that = (Set) o;
            return that.size() == inner.size() &&
                   containsAll(that);
        }

        @Override public int hashCode() { return UnmodIterable.hash(this); }

        @Override public String toString() { return inner.toString(); }
    }

    @Test
    public void testUnion() {
        List<String> origItems = Arrays.asList("This", "is", "a", "test");
        Set<String> control = new HashSet<>();
        control.addAll(origItems);
        ImSet<String> test = new TestSet<>(origItems);

        assertEquals(control, test);

        for (int i = 0; i < 10; i++) {
            String ord = ordinal(i);
            control.add(ord);
            test = test.put(ord);
            assertEquals(control.size(), test.size());
            assertEquals(control, test);
        }

        List<String> addedItems = Arrays.asList("more", "stuff");

        control.addAll(addedItems);
        test = test.union(addedItems);

        assertEquals(control, test);

        assertTrue(test == test.union(null));
    }

}