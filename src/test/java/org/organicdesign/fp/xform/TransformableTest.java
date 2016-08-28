package org.organicdesign.fp.xform;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.junit.Test;
import org.organicdesign.fp.collections.ImMap;
import org.organicdesign.fp.collections.ImSortedMap;
import org.organicdesign.fp.collections.KeyVal;
import org.organicdesign.fp.collections.PersistentHashMap;
import org.organicdesign.fp.collections.PersistentTreeMap;
import org.organicdesign.fp.collections.UnmodSortedIterable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.organicdesign.fp.FunctionUtils.ordinal;

public class TransformableTest {

    @Test public void testToMutableList() throws Exception {
        List<Integer> control = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);
        Transformable<Integer> trans = Xform.of(control);
        assertEquals(control, trans.toMutableList());
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
        assertEquals(control, trans.toMutableMap(x -> x));
    }

    @Test public void testToMutableSortedMap() throws Exception {
        List<Integer> items = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);
        Comparator<Integer> comp = (a, b) -> a - b;
        SortedMap<Integer,String> control = new TreeMap<>(comp);
        for (int i : items) {
            control.put(i, ordinal(i));
        }
        Transformable<Integer> trans = Xform.of(items);
        assertEquals(control, trans.toMutableSortedMap(comp, i -> new KeyVal<>(i, ordinal(i))));
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
        assertEquals(control, trans.toImSortedMap(comp, i -> KeyVal.of(i, ordinal(i))));
    }

    @Test public void testToMutableSortedSet() throws Exception {
        SortedSet<Integer> control = new TreeSet<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
        Transformable<Integer> trans = Xform.of(control);
        assertTrue(UnmodSortedIterable.equals(UnmodSortedIterable.castFromSortedSet(control),
                                              UnmodSortedIterable.castFromSortedSet(trans.toMutableSortedSet((a, b) -> a - b))));
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
        assertTrue(UnmodSortedIterable.equals(UnmodSortedIterable.castFromSortedSet(control),
                                              trans.toImSortedSet(comp)));
    }

    @Test public void testToMutableSet() throws Exception {
        Set<Integer> control = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
        Transformable<Integer> trans = Xform.of(control);
        assertEquals(control, trans.toMutableSet());
    }
}