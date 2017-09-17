package org.organicdesign.fp.collections;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ImSetTest {
    static class TestSet<E> extends AbstractUnmodSet<E> implements ImSet<E> {
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
            return new MutSetTest.TestSet<>(inner);
        }

        @Override public ImSet<E> put(E e) {
            Set<E> next = dup(inner);
            next.add(e);
            return new TestSet<>(next);
        }

        @Override public ImSet<E> without(E key) {
            Set<E> next = dup(inner);
            next.remove(key);
            return new TestSet<>(next);
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
    }

    @Test public void testUnion() {
        ImSet<String> imSet = new TestSet<>(Arrays.asList("This", "is", "a", "test"));
        ImSet<String> unionized = imSet.union(Arrays.asList("more", "stuff"));

        assertEquals(imSet.size() + 2, unionized.size());
        assertTrue(unionized.containsAll(imSet));
        assertFalse(imSet.containsAll(unionized));

        assertTrue(imSet == imSet.union(null));
    }
}