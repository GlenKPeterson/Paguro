package org.organicdesign.fp.collections;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UnmodSortedSetTest {
    static class TestSortSet<E> implements UnmodSortedSet<E> {
        static <T> SortedSet<T> dup(SortedSet<T> in) {
            SortedSet<T> out = new TreeSet<>(in.comparator());
            out.addAll(in);
            return out;
        }

        private final SortedSet<E> inner;

        TestSortSet(Collection<E> items, Comparator<? super E> comp) {
            inner = (comp == null)
                    ? new TreeSet<>()
                    : new TreeSet<>(comp);
            inner.addAll(items);
        }

        @Override public int size() { return inner.size(); }

        @Override public boolean contains(Object o) { return inner.contains(o); }

        @NotNull
        @Override public UnmodSortedIterator<E> iterator() {
            return new UnmodSortedIterator<E>() {
                Iterator<E> iter = inner.iterator();
                @Override public boolean hasNext() { return iter.hasNext(); }
                @Override public E next() { return iter.next(); }
            };
        }

        @Override public Comparator<? super E> comparator() { return inner.comparator(); }

        @NotNull
        @Override public UnmodSortedSet<E> subSet(E fromElement, E toElement) {
            SortedSet<E> next = dup(inner);
            return new TestSortSet<>(next.subSet(fromElement, toElement), inner.comparator());
        }

        @NotNull
        @Override public UnmodSortedSet<E> tailSet(E fromElement) {
            SortedSet<E> next = dup(inner);
            return new TestSortSet<>(next.tailSet(fromElement), inner.comparator());
        }

        @Override public E first() { return inner.first(); }

        @Override public E last() { return inner.last(); }
    }

    static final String[] vals = new String[] { "apple", "cat", "dog", "egg", "frog", "goat" };

    UnmodSortedSet<String> unSortSet = new TestSortSet<>(Arrays.asList(vals), null);

    @Test public void basics() {
        assertEquals(0, unSortSet.headSet("apple").size());
        assertEquals(1, unSortSet.headSet("cat").size());
        assertTrue(unSortSet.headSet("cat").contains("apple"));
        assertTrue(unSortSet == unSortSet.headSet("horse"));

        UnmodSortedSet<String> compSet = new TestSortSet<>(Arrays.asList(vals), String.CASE_INSENSITIVE_ORDER);

        assertEquals(0, compSet.headSet("apple").size());
        assertEquals(1, compSet.headSet("cat").size());
        assertTrue(compSet.headSet("cat").contains("apple"));
        assertTrue(compSet == compSet.headSet("horse"));


        // IllegalArgumentException.
        assertEquals(0, unSortSet.tailSet("horse").size());

        assertEquals(1, unSortSet.tailSet("goat").size());
        assertTrue(unSortSet.tailSet("goat").contains("goat"));

//        ImSortedSet<String> unionized = unSortSet.oneOf(Arrays.asList("horse", "iguana"));
//        assertEquals(unSortSet.size() + 2, unionized.size());
//        assertTrue(unionized.containsAll(unSortSet));
//        assertFalse(unSortSet.containsAll(unionized));
    }
}