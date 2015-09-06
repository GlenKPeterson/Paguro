package org.organicdesign.fp.xform;

import org.junit.Test;
import org.organicdesign.fp.collections.UnmodSortedIterable;
import org.organicdesign.fp.permanent.Sequence;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TransformableTest {

    @Test public void testToMutableList() throws Exception {
        List<Integer> control = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);
        Transformable<Integer> trans = Sequence.ofIter(control);
        assertEquals(control, trans.toMutableList());
    }

    @Test public void testToImList() throws Exception {
        List<Integer> control = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);
        Transformable<Integer> trans = Sequence.ofIter(control);
        assertEquals(control, trans.toImList());
    }

    // TODO: Add these.
//    @Test public void testToMutableMap() throws Exception {
//
//    }
//
//    @Test public void testToMutableSortedMap() throws Exception {
//
//    }
//
//    @Test public void testToImMap() throws Exception {
//
//    }
//
//    @Test public void testToImSortedMap() throws Exception {
//
//    }

    @Test public void testToMutableSortedSet() throws Exception {
        SortedSet<Integer> control = new TreeSet<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
        Transformable<Integer> trans = Sequence.ofIter(control);
        assertTrue(UnmodSortedIterable.equals(UnmodSortedIterable.castFromSortedSet(control),
                                              UnmodSortedIterable.castFromSortedSet(trans.toMutableSortedSet((a, b) -> a - b))));
    }

    // TODO: Major Failure, probably due to PersistentHashMap.iterator() implementation.
    @Test public void testToImSet() throws Exception {
        Set<Integer> control = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
        Transformable<Integer> trans = Sequence.ofIter(control);
        assertEquals(control, trans.toImSet());
    }

    @Test public void testToImSortedSet() throws Exception {
        SortedSet<Integer> control = new TreeSet<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
        Transformable<Integer> trans = Sequence.ofIter(control);
        assertTrue(UnmodSortedIterable.equals(UnmodSortedIterable.castFromSortedSet(control),
                                              trans.toImSortedSet((a, b) -> a - b)));
    }

    @Test public void testToMutableSet() throws Exception {
        Set<Integer> control = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
        Transformable<Integer> trans = Sequence.ofIter(control);
        assertEquals(control, trans.toMutableSet());
    }
}