package org.organicdesign.fp.xform;

import java.util.*;

import org.junit.Test;
import org.organicdesign.fp.collections.ImMap;
import org.organicdesign.fp.collections.ImSortedMap;
import org.organicdesign.fp.collections.PersistentHashMap;
import org.organicdesign.fp.collections.PersistentTreeMap;
import org.organicdesign.fp.collections.UnmodSortedIterable;
import org.organicdesign.fp.oneOf.Option;
import org.organicdesign.fp.tuple.Tuple2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.organicdesign.fp.FunctionUtils.ordinal;

public class TransformableTest {

    @Test public void testToMutableList() throws Exception {
        List<Integer> control = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);
        Transformable<Integer> trans = Xform.of(control);
        assertEquals(control, trans.toMutList());
    }

    @Test public void testToImList() throws Exception {
        List<Integer> control = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);
        Transformable<Integer> trans = Xform.of(control);
        assertEquals(control, trans.toImList());
    }

    @Test public void testToMutableMap() throws Exception {
        Map<Integer,String> control = new HashMap<>();
        for (int i = 1; i < 12; i++) {
            control.put(i, ordinal(i));
        }
        Transformable<Map.Entry<Integer,String>> trans = Xform.of(control.entrySet());
        assertEquals(control, trans.toMutMap(x -> x));
    }

    @Test public void testToMutableSortedMap() throws Exception {
        List<Integer> items = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);
        Comparator<Integer> comp = (a, b) -> a - b;
        SortedMap<Integer,String> control = new TreeMap<>(comp);
        for (int i : items) {
            control.put(i, ordinal(i));
        }
        Transformable<Integer> trans = Xform.of(items);
        assertEquals(control, trans.toMutSortedMap(comp, i -> Tuple2.of(i, ordinal(i))));
    }

    @Test public void testToImMap() throws Exception {
        ImMap<Integer,String> control = PersistentHashMap.empty();
        for (int i = 1; i < 12; i++) {
            control = control.assoc(i, ordinal(i));
        }
        Transformable<Map.Entry<Integer,String>> trans = Xform.of(control);
        assertEquals(control, trans.toImMap(x -> x));

    }

    @Test public void testToImSortedMap() throws Exception {
        List<Integer> items = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);
        Comparator<Integer> comp = (a, b) -> a - b;
        ImSortedMap<Integer,String> control = PersistentTreeMap.empty(comp);
        for (int i : items) {
            control = control.assoc(i, ordinal(i));
        }
        Transformable<Integer> trans = Xform.of(items);
        assertEquals(control, trans.toImSortedMap(comp, i -> Tuple2.of(i, ordinal(i))));
    }

    @Test public void testToMutableSortedSet() throws Exception {
        SortedSet<Integer> control = new TreeSet<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
        Transformable<Integer> trans = Xform.of(control);
        assertTrue(UnmodSortedIterable.equal(UnmodSortedIterable.castFromSortedSet(control),
                                             UnmodSortedIterable.castFromSortedSet(trans.toMutSortedSet((a, b) -> a - b))));
    }

    @Test public void testToImSet() throws Exception {
        List<Integer> items = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);
        Set<Integer> control = new HashSet<>(items);
        Transformable<Integer> trans = Xform.of(items);
        assertEquals(control, trans.toImSet());
    }

    @Test public void testToImSortedSet() throws Exception {
        List<Integer> items = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);
        Comparator<Integer> comp = (a, b) -> a - b;
        SortedSet<Integer> control = new TreeSet<>(comp);
        control.addAll(items);
        Transformable<Integer> trans = Xform.of(items);
        assertEquals(control, trans.toImSortedSet(comp));
        assertTrue(UnmodSortedIterable.equal(UnmodSortedIterable.castFromSortedSet(control),
                                             trans.toImSortedSet(comp)));
    }

    @Test public void testToMutableSet() throws Exception {
        Set<Integer> control = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
        Transformable<Integer> trans = Xform.of(control);
        assertEquals(control, trans.toMutSet());
    }

    @Test public void testHead() {
        // TODO: I'm not getting test coverage from this.  It seems to be using the UnmodIterable implementation instead (which is probably more efficient)
        Transformable<Integer> tint1 = Xform.of(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
        assertEquals(Option.some(1), ((Transformable<Integer>) tint1).head());

        Transformable<Integer> tint2 = Xform.of(Collections.emptyList());
        assertEquals(Option.none(), ((Transformable<Integer>) tint2).head());
    }
}