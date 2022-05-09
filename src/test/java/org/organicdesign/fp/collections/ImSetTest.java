package org.organicdesign.fp.collections;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ImSetTest {

    /**
     * Immutable HashSet based on deep-copying the internal set,
     * then returning the modified copy.
     */
    static class ImTestSet<E> extends AbstractUnmodSet<E> implements ImSet<E> {

        private final Set<E> inner;

        ImTestSet(Collection<E> s) {
            inner = new HashSet<>(s);
        }

        @Override
        public @NotNull MutSet<E> mutable() {
            return new MutSetTest.MutTestSet<>(new HashSet<>(inner));
        }

        @Override
        public @NotNull ImSet<E> put(E e) {
            Set<E> next = new HashSet<>(inner);
            next.add(e);
            return new ImTestSet<>(next);
        }

        @Override
        public @NotNull ImSet<E> without(E key) {
            Set<E> next = new HashSet<>(inner);
            next.remove(key);
            return new ImTestSet<>(next);
        }

        @Override
        public int size() { return inner.size(); }

        @Override
        public boolean contains(Object o) { return inner.contains(o); }

        @Override
        public @NotNull UnmodIterator<E> iterator() {
            return new UnmodIterator<>() {
                private final Iterator<E> iter = inner.iterator();
                @Override public boolean hasNext() { return iter.hasNext(); }
                @Override public E next() { return iter.next(); }
            };
        }

        /** Note: not reflexive with TreeSet.equals() */
        @Override
        public boolean equals(Object other) {
            if ( !(other instanceof Set) ) { return false; }
            Set<?> that = (Set<?>) other;
            return that.size() == inner.size() &&
                   containsAll(that);
        }

        @Override
        public int hashCode() { return UnmodIterable.hash(this); }

        @Override
        public @NotNull String toString() { return inner.toString(); }
    }

    @Test
    public void testUnion() {
        ImSet<String> imSet = PersistentHashSet.of(Arrays.asList("This", "is", "a", "test"));
        ImSet<String> unionized = imSet.union(Arrays.asList("more", "stuff"));

        assertEquals(imSet.size() + 2, unionized.size());
        assertTrue(unionized.containsAll(imSet));
        assertFalse(imSet.containsAll(unionized));

        assertTrue(imSet == imSet.union(null));

        // Do it again with our test implementation.
        imSet = new ImTestSet<>(Arrays.asList("This", "is", "a", "test"));
        unionized = imSet.union(Arrays.asList("more", "stuff"));

        assertEquals(imSet.size() + 2, unionized.size());
        assertTrue(unionized.containsAll(imSet));
        assertFalse(imSet.containsAll(unionized));

        assertTrue(imSet == imSet.union(null));
    }
}