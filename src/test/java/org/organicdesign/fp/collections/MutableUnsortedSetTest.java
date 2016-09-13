package org.organicdesign.fp.collections;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

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
            return new UnmodIterator.Implementation<>(inner.iterator());
        }
    }

    @Test public void testMutable() {
        TestSet<String> s = new TestSet<>(new HashSet<>());
        assertTrue(s == s.mutable());
    }
}