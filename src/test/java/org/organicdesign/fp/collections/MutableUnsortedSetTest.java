package org.organicdesign.fp.collections;

import org.junit.Test;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static org.junit.Assert.*;
import static org.organicdesign.fp.FunctionUtils.ordinal;

/**
 Created by gpeterso on 9/13/16.
 */
public class MutableUnsortedSetTest {
    public static class TestSet<E> implements MutableUnsortedSet<E> {
        private final Set<E> inner;
        public TestSet(Set<E> s) { inner = s; }

        @Override public ImUnsortedSet<E> immutable() {
            return new ImUnsortedSetTest.TestSet<>(inner);
        }

        @Override public MutableUnsortedSet<E> put(E val) {
            inner.add(val);
            return this;
        }

        @Override public MutableUnsortedSet<E> without(E key) {
            inner.remove(key);
            return this;
        }

        @Override public int size() { return inner.size(); }

        @Override public boolean contains(Object o) { return inner.contains(o); }

        @Override public UnmodIterator<E> iterator() {
            return new UnmodIterator<E>() {
                private final Iterator<E> iter = inner.iterator();
                @Override public boolean hasNext() { return iter.hasNext(); }
                @Override public E next() { return iter.next(); }
            };
        }
    }

    @Test public void testMutable() {
        Set<String> control = new HashSet<>();
        TestSet<String> test = new TestSet<>(new HashSet<>());
        for (int i = 0; i < 27; i++) {
            String ord = ordinal(i);
            control.add(ord);
            test.put(ord);
            assertEquals(control.size(), test.size());
            assertEquals(control, test);
        }
        PersistentHashSetTest.setIterTest(control, test.iterator());
        assertTrue(test == test.mutable());
    }
}