package org.organicdesign.fp.collections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.organicdesign.fp.TestUtilities;

import static org.junit.Assert.assertTrue;
import static org.organicdesign.fp.FunctionUtils.ordinal;

public class UnmodSortedIterableTest {
    @Test public void equalsTest() {
        class Usi<T> implements UnmodSortedIterable<T> {
            private final List<T> ts;
            private Usi(List<T> l) { ts = l; }
            /** Returns items in a guaranteed order. */
            @NotNull
            @Override
            public UnmodSortedIterator<T> iterator() {
                return new UnmodSortedIterator.Wrapper<>(ts.iterator());
            }
        };
        assertTrue(UnmodSortedIterable.equal(new Usi<>(Arrays.asList("1", "2", "3")),
                                             new Usi<>(Arrays.asList("1", "2", "3"))));
    }

    @Test public void castFromSortedSetTest() {
        SortedSet<Integer> control = new TreeSet<>();
        control.addAll(Arrays.asList(9,8,7,6,5,4,3,2,1));
        UnmodSortedIterable<Integer> usii = UnmodSortedIterable.castFromSortedSet(control);
        TestUtilities.compareIterators(control.iterator(), usii.iterator());

        TestUtilities.compareIterators(control.iterator(),
                                       TestUtilities.serializeDeserialize(usii).iterator());
    }

    @Test public void castFromListTest() {
        List<Integer> control = new ArrayList<>();
        control.addAll(Arrays.asList(1,2,3,4,5,6,7,8,9));
        UnmodSortedIterable<Integer> usii = UnmodSortedIterable.castFromList(control);
        TestUtilities.compareIterators(control.iterator(), usii.iterator());

        TestUtilities.compareIterators(control.iterator(),
                                       TestUtilities.serializeDeserialize(usii).iterator());
    }

    @Test public void castFromSortedMapTest() {
        SortedMap<Integer,String> control = new TreeMap<>();
        for (int i = 9; i >= 0; i--) {
            control.put(i, ordinal(i));
        }
        UnmodSortedIterable<? extends Map.Entry<Integer,String>> usii =
                UnmodSortedIterable.castFromSortedMap(control);

        TestUtilities.compareIterators(control.entrySet().iterator(), usii.iterator());

        TestUtilities.compareIterators(control.entrySet().iterator(),
                                       TestUtilities.serializeDeserialize(usii).iterator());

    }
}