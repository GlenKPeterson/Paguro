package org.organicdesign.fp.collections;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import static org.junit.Assert.*;

public class ImSortedSetTest {
    static class TestSortSet<T> implements ImSortedSet<T> {
        static <T> SortedSet<T> dup(SortedSet<T> in) {
            SortedSet<T> out = new TreeSet<>(in.comparator());
            out.addAll(in);
            return out;
        }

        private final SortedSet<T> inner;

        TestSortSet(Collection<T> items, Comparator<? super T> comp) {
            inner = (comp == null)
                    ? new TreeSet<>()
                    : new TreeSet<>(comp);
            inner.addAll(items);
        }
        @NotNull
        @Override public ImSortedSet<T> put(T s) {
            SortedSet<T> next = dup(inner);
            next.add(s);
            return new TestSortSet<>(next, inner.comparator());
        }

        @NotNull
        @Override public ImSortedSet<T> without(T key) {
            SortedSet<T> next = dup(inner);
            next.remove(key);
            return new TestSortSet<>(next, inner.comparator());
        }

        @NotNull
        @Override public UnmodSortedIterator<T> iterator() {
            return new UnmodSortedIterator<T>() {
                Iterator<T> iter = inner.iterator();
                @Override public boolean hasNext() { return iter.hasNext(); }
                @Override public T next() { return iter.next(); }
            };
        }

        @NotNull
        @Override public ImSortedSet<T> subSet(T fromElement, T toElement) {
            SortedSet<T> next = dup(inner);
            return new TestSortSet<>(next.subSet(fromElement, toElement), inner.comparator());
        }

        @NotNull
        @Override public ImSortedSet<T> tailSet(T fromElement) {
            SortedSet<T> next = dup(inner);
            return new TestSortSet<>(next.tailSet(fromElement), inner.comparator());
        }

        @Override public Comparator<? super T> comparator() { return inner.comparator(); }

        @Override public T first() { return inner.first(); }

        @Override public T last() { return inner.last(); }

        @Override public boolean contains(Object o) { return inner.contains(o); }

        @Override public int size() { return inner.size(); }
    };

    static final String[] vals = new String[] { "apple", "cat", "dog", "egg", "frog", "goat" };

    ImSortedSet<String> imSortSet = new TestSortSet<>(Arrays.asList(vals), null);

    @Test public void basics() {
        assertEquals(0, imSortSet.headSet("apple").size());
        assertEquals(1, imSortSet.headSet("cat").size());
        assertTrue(imSortSet.headSet("cat").contains("apple"));
        assertFalse(imSortSet.headSet("cat").contains("cat"));
        assertTrue(imSortSet == imSortSet.headSet("horse"));

        ImSortedSet<String> compSet = new TestSortSet<>(Arrays.asList(vals),
                                                        String.CASE_INSENSITIVE_ORDER);

        assertEquals(0, compSet.headSet("apple").size());
        assertEquals(1, compSet.headSet("cat").size());
        assertTrue(compSet.headSet("cat").contains("apple"));
        assertTrue(compSet == compSet.headSet("horse"));

        // IllegalArgumentException.
//        assertEquals(0, imSortSet.tailSet("horse").size());

        assertEquals(1, imSortSet.tailSet("goat").size());
        assertTrue(imSortSet.tailSet("goat").contains("goat"));

        ImSortedSet<String> unionized = imSortSet.union(Arrays.asList("horse", "iguana"));
        assertEquals(imSortSet.size() + 2, unionized.size());
        assertTrue(unionized.containsAll(imSortSet));
        assertFalse(imSortSet.containsAll(unionized));
    }
}