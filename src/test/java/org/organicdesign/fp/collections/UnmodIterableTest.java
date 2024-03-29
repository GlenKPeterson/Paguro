package org.organicdesign.fp.collections;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.organicdesign.fp.collections.UnmodIterable.emptyUnmodIterable;

public class UnmodIterableTest {
    static class TestIterable<T> implements UnmodIterable<T> {
        private final Iterable<T> inner;
        TestIterable(Iterable<T> i) { inner = i; }

        @Override
        public @NotNull UnmodIterator<T> iterator() {
            final Iterator<T> iter = inner.iterator();
            return new UnmodIterator<>() {
                @Override public boolean hasNext() { return iter.hasNext(); }
                @Override public T next() { return iter.next(); }
            };
        }
    }

    @Test
    public void transformTests() {
        TestIterable<String> testIterable =
                new TestIterable<>(Arrays.asList("How", "now"));

        assertEquals(Arrays.asList("How", "now", "brown", "cow"),
                     testIterable.concat(Arrays.asList("brown", "cow"))
                                 .toMutList());

        assertEquals(Arrays.asList("My", "poem:", "How", "now"),
                     testIterable.precat(Arrays.asList("My", "poem:"))
                                 .toMutList());

        testIterable = new TestIterable<>(Arrays.asList("How", "now", "brown", "cow"));

        assertEquals(Arrays.asList("now", "brown", "cow"),
                     testIterable.drop(1).toMutList());

        assertEquals(Arrays.asList("Howz", "nowz", "brownz", "cowz"),
                     testIterable.fold(new ArrayList<>(),
                                       (List<String> accum, String str) -> {
                                               accum.add(str + "z");
                                               return accum;
                                           }));
        assertEquals(Arrays.asList("Howz", "nowz"),
                     testIterable.foldUntil(new ArrayList<>(),
                                            (List<String> list, String str) -> (list.size() > 1) ? list : null,
                                            (List<String> accum, String str) -> {
                                               accum.add(str + "z");
                                               return accum;
                                           }).bad());

        assertEquals(Arrays.asList("How", "now", "cow"),
                     testIterable.filter((String s) -> s.endsWith("w")).toMutList());

        assertEquals(Arrays.asList("Holy", "cow!"),
                     new TestIterable<>(Arrays.asList("Holy", null, "cow!"))
                             .whereNonNull()
                             .toMutList());

        assertEquals(Arrays.asList("Holy", "cow!"),
                     new TestIterable<>(Arrays.asList(null, null, null,
                                                      "Holy", null, null, null, null, "cow!",
                                                      null, null))
                             .whereNonNull()
                             .toMutList());

        assertEquals(Arrays.asList("How", "are", "you", "now", "are", "you", "brown", "are", "you",
                                   "cow", "are", "you"),
                     testIterable.flatMap((String s) ->
                                                  Arrays.asList(s, "are", "you")).toMutList());

        assertEquals(Collections.emptyList(),
                     testIterable.flatMap((String s) -> emptyUnmodIterable()).toMutList());

        assertEquals(Arrays.asList(3, 3, 5, 3),
                     testIterable.map((String s) -> s.length()).toMutList());

        assertEquals(Arrays.asList("How", "now", "brown"),
                     testIterable.take(3).toMutList());

        assertEquals(Arrays.asList("How", "now"),
                     testIterable.takeWhile((String s) -> s.length() < 4).toMutList());

        assertEquals(Arrays.asList("brown", "cow"),
                     testIterable.dropWhile((s) -> !"brown".equals(s)).toMutList());

    }

    @Test public void equalsHashcode() {
        UnmodSortedIterable<Integer> a = () -> new UnmodSortedIterator<Integer>() {
            private final Iterator<Integer> intern = Arrays.asList(1,2,3).iterator();
            @Override public boolean hasNext() { return intern.hasNext(); }
            @Override public Integer next() { return intern.next(); }
        };
        UnmodSortedIterable<Integer> b = () -> new UnmodSortedIterator<Integer>() {
            private final Iterator<Integer> intern = Arrays.asList(1,2,3).iterator();
            @Override public boolean hasNext() { return intern.hasNext(); }
            @Override public Integer next() { return intern.next(); }
        };
        UnmodSortedIterable<Integer> c = () -> new UnmodSortedIterator<Integer>() {
            private final Iterator<Integer> intern = Arrays.asList(1,2,3,4).iterator();
            @Override public boolean hasNext() { return intern.hasNext(); }
            @Override public Integer next() { return intern.next(); }
        };
        UnmodSortedIterable<Integer> d = () -> new UnmodSortedIterator<Integer>() {
            private final Iterator<Integer> intern = Arrays.asList(1,2,2).iterator();
            @Override public boolean hasNext() { return intern.hasNext(); }
            @Override public Integer next() { return intern.next(); }
        };

        assertEquals(UnmodIterable.hash(a), UnmodIterable.hash(a));
        assertEquals(UnmodIterable.hash(a), UnmodIterable.hash(b));
        assertNotEquals(UnmodIterable.hash(a), UnmodIterable.hash(c));
        assertNotEquals(UnmodIterable.hash(b), UnmodIterable.hash(d));

        assertTrue(UnmodSortedIterable.equal(a, a));
        assertTrue(UnmodSortedIterable.equal(a, b));
        assertTrue(UnmodSortedIterable.equal(b, a));
        assertFalse(UnmodSortedIterable.equal(a, c));
        assertFalse(UnmodSortedIterable.equal(c, a));
        assertFalse(UnmodSortedIterable.equal(b, d));
        assertFalse(UnmodSortedIterable.equal(d, b));
    }

//    @SuppressWarnings("ResultOfMethodCallIgnored")
//    @Test (expected = IllegalArgumentException.class)
//    public void testEx01() { UnmodIterable.hash(null); }
//
//    @SuppressWarnings("ResultOfMethodCallIgnored")
//    @Test (expected = IllegalArgumentException.class)
//    public void testEx02() { UnmodIterable.toString(null, Arrays.asList(1,2,3)); }
//
//    @SuppressWarnings("ResultOfMethodCallIgnored")
//    @Test (expected = IllegalArgumentException.class)
//    public void testEx03() { UnmodIterable.toString("Oops", null); }

//    @Test public void compareHelper() {
//        UnmodIterable<Integer> a = () -> new UnmodIterator<Integer>() {
//            private final Iterator<Integer> intern = Arrays.asList(1,2,3).iterator();
//            @Override public boolean hasNext() { return intern.hasNext(); }
//            @Override public Integer next() { return intern.next(); }
//        };
//        UnmodIterable<Integer> b = () -> new UnmodIterator<Integer>() {
//            private final Iterator<Integer> intern = Arrays.asList(1,2,3).iterator();
//            @Override public boolean hasNext() { return intern.hasNext(); }
//            @Override public Integer next() { return intern.next(); }
//        };
//        assertEquals(Integer.valueOf(0), UnmodIterable.compareHelper(a, a));
//
//    }
}
