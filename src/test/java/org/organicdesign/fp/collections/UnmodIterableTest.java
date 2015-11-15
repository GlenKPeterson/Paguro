package org.organicdesign.fp.collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.organicdesign.fp.FunctionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class UnmodIterableTest {
    class TestIterable<T> implements UnmodIterable<T> {
        private final Iterable<T> inner;
        TestIterable(Iterable<T> i) { inner = i; }
        @Override public UnmodIterator<T> iterator() {
            Iterator<T> iter = inner.iterator();
            return new UnmodIterator<T>() {
                @Override public boolean hasNext() { return iter.hasNext(); }
                @Override public T next() { return iter.next(); }
            };
        }
    }

    @Test public void transformTests() {
        TestIterable<String> testIterable =
                new TestIterable<>(Arrays.asList("How", "now"));

        assertEquals(Arrays.asList("How", "now", "brown", "cow"),
                     testIterable.concat(Arrays.asList("brown", "cow"))
                                 .toMutableList());

        assertEquals(Arrays.asList("My", "poem:", "How", "now"),
                     testIterable.precat(Arrays.asList("My", "poem:"))
                                 .toMutableList());

        testIterable = new TestIterable<>(Arrays.asList("How", "now", "brown", "cow"));

        assertEquals(Arrays.asList("now", "brown", "cow"),
                     testIterable.drop(1).toMutableList());

        assertEquals(Arrays.asList("Howz", "nowz", "brownz", "cowz"),
                     testIterable.foldLeft(new ArrayList<>(),
                                           (List<String> accum, String str) -> {
                                               accum.add(str + "z");
                                               return accum;
                                           }));
        assertEquals(Arrays.asList("Howz", "nowz"),
                     testIterable.foldLeft(new ArrayList<>(),
                                           (List<String> accum, String str) -> {
                                               accum.add(str + "z");
                                               return accum;
                                           },
                                           (List<String> list) -> list.size() > 1));

        assertEquals(Arrays.asList("How", "now", "cow"),
                     testIterable.filter((String s) -> s.endsWith("w")).toMutableList());

        assertEquals(Arrays.asList("How", "are", "you", "now", "are", "you", "brown", "are", "you",
                                   "cow", "are", "you"),
                     testIterable.flatMap((String s) ->
                                                  Arrays.asList(s, "are", "you")).toMutableList());

        assertEquals(Arrays.asList(3, 3, 5, 3),
                     testIterable.map((String s) -> s.length()).toMutableList());

        assertEquals(Arrays.asList("How", "now", "brown"),
                     testIterable.take(3).toMutableList());

        assertEquals(Arrays.asList("How", "now"),
                     testIterable.takeWhile((String s) -> s.length() < 4).toMutableList());
    }

    @Test public void equalsHashcodeTest() {
        UnmodSortedIterable<Integer> a = () -> FunctionUtils.emptyUnmodSortedIterator();
        UnmodSortedIterable<Integer> b = () -> FunctionUtils.emptyUnmodSortedIterator();
        UnmodSortedIterable<Integer> c = () -> new UnmodSortedIterator<Integer>() {
            private final Iterator<Integer> intern = Arrays.asList(1).iterator();
            @Override public boolean hasNext() { return intern.hasNext(); }
            @Override public Integer next() { return intern.next(); }
        };

        assertEquals(UnmodIterable.hashCode(a), UnmodIterable.hashCode(a));
        assertEquals(UnmodIterable.hashCode(a), UnmodIterable.hashCode(b));
        assertNotEquals(UnmodIterable.hashCode(a), UnmodIterable.hashCode(c));

        assertEquals(0, UnmodIterable.hashCode(Arrays.asList(new String[] { null })));

        assertTrue(UnmodSortedIterable.equals(a, a));
        assertTrue(UnmodSortedIterable.equals(a, b));
        assertTrue(UnmodSortedIterable.equals(b, a));
        assertTrue(UnmodSortedIterable.equals(null, null));
        assertFalse(UnmodSortedIterable.equals(a, null));
        assertFalse(UnmodSortedIterable.equals(null, a));
        assertFalse(UnmodSortedIterable.equals(a, c));
        assertFalse(UnmodSortedIterable.equals(c, a));
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

        assertEquals(UnmodIterable.hashCode(a), UnmodIterable.hashCode(a));
        assertEquals(UnmodIterable.hashCode(a), UnmodIterable.hashCode(b));
        assertNotEquals(UnmodIterable.hashCode(a), UnmodIterable.hashCode(c));
        assertNotEquals(UnmodIterable.hashCode(b), UnmodIterable.hashCode(d));

        assertTrue(UnmodSortedIterable.equals(a, a));
        assertTrue(UnmodSortedIterable.equals(a, b));
        assertTrue(UnmodSortedIterable.equals(b, a));
        assertFalse(UnmodSortedIterable.equals(a, c));
        assertFalse(UnmodSortedIterable.equals(c, a));
        assertFalse(UnmodSortedIterable.equals(b, d));
        assertFalse(UnmodSortedIterable.equals(d, b));
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test (expected = IllegalArgumentException.class)
    public void testEx01() { UnmodIterable.hashCode(null); }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test (expected = IllegalArgumentException.class)
    public void testEx02() { UnmodIterable.toString(null, Arrays.asList(1,2,3)); }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test (expected = IllegalArgumentException.class)
    public void testEx03() { UnmodIterable.toString("Oops", null); }

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
