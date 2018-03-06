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
    static final List<Integer> controlList = Collections.unmodifiableList(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));

    @Test public void testHead() {
        // TODO: I'm not getting test coverage from this.  It seems to be using the UnmodIterable implementation instead (which is probably more efficient)
        Transformable<Integer> tint1 = Xform.of(controlList);
        assertEquals(Option.some(1), ((Transformable<Integer>) tint1).head());

        Transformable<Integer> tint2 = Xform.of(Collections.emptyList());
        assertEquals(Option.none(), ((Transformable<Integer>) tint2).head());
    }

    @Test public void testToMutableList() {
        Transformable<Integer> trans = Xform.of(controlList);
        assertEquals(controlList, trans.toMutableList());
    }

    @Test public void testToImList() {
        Transformable<Integer> trans = Xform.of(controlList);
        assertEquals(controlList, trans.toImList());
    }

    @Test public void testToMutableRrbt() {
        Transformable<Integer> trans = Xform.of(controlList);
        assertEquals(controlList, trans.toMutableRrbt());
    }

    @Test public void testToImRrbt() {
        Transformable<Integer> trans = Xform.of(controlList);
        assertEquals(controlList, trans.toImRrbt());
    }

    @Test public void testToMutableMap() {
        Map<Integer,String> control = new HashMap<>();
        for (int i = 1; i < 12; i++) {
            control.put(i, ordinal(i));
        }
        Transformable<Map.Entry<Integer,String>> trans = Xform.of(control.entrySet());
        assertEquals(control, trans.toMutableMap(x -> x));
    }

    @Test public void testToMutableSortedMap() {
        Comparator<Integer> comp = (a, b) -> a - b;
        SortedMap<Integer,String> control = new TreeMap<>(comp);
        for (int i : controlList) {
            control.put(i, ordinal(i));
        }
        Transformable<Integer> trans = Xform.of(controlList);
        assertEquals(control, trans.toMutableSortedMap(comp, i -> Tuple2.of(i, ordinal(i))));
    }

    @Test public void testToImMap() {
        ImMap<Integer,String> control = PersistentHashMap.empty();
        for (int i = 1; i < 12; i++) {
            control = control.assoc(i, ordinal(i));
        }
        Transformable<Map.Entry<Integer,String>> trans = Xform.of(control);
        assertEquals(control, trans.toImMap(x -> x));

    }

    @Test public void testToImSortedMap() {
        Comparator<Integer> comp = (a, b) -> a - b;
        ImSortedMap<Integer,String> control = PersistentTreeMap.empty(comp);
        for (int i : controlList) {
            control = control.assoc(i, ordinal(i));
        }
        Transformable<Integer> trans = Xform.of(controlList);
        assertEquals(control, trans.toImSortedMap(comp, i -> Tuple2.of(i, ordinal(i))));
    }

    @Test public void testToMutableSortedSet() {
        SortedSet<Integer> control = new TreeSet<>(controlList);
        Transformable<Integer> trans = Xform.of(control);
        assertTrue(UnmodSortedIterable.equal(UnmodSortedIterable.castFromSortedSet(control),
                                             UnmodSortedIterable.castFromSortedSet(trans.toMutableSortedSet((a, b) -> a - b))));
    }

    @Test public void testToImSet() {
        Set<Integer> control = new HashSet<>(controlList);
        Transformable<Integer> trans = Xform.of(controlList);
        assertEquals(control, trans.toImSet());
    }

    @Test public void testToImSortedSet() {
        Comparator<Integer> comp = (a, b) -> a - b;
        SortedSet<Integer> control = new TreeSet<>(comp);
        control.addAll(controlList);
        Transformable<Integer> trans = Xform.of(controlList);
        assertEquals(control, trans.toImSortedSet(comp));
        assertTrue(UnmodSortedIterable.equal(UnmodSortedIterable.castFromSortedSet(control),
                                             trans.toImSortedSet(comp)));
    }

    @Test public void testToMutableSet() {
        Set<Integer> control = new HashSet<>(controlList);
        Transformable<Integer> trans = Xform.of(control);
        assertEquals(control, trans.toMutableSet());
    }
}