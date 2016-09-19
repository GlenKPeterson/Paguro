package org.organicdesign.fp.collections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.Test;
import org.organicdesign.fp.Option;

import static org.junit.Assert.assertEquals;
import static org.organicdesign.fp.FunctionUtils.ordinal;
import static org.organicdesign.fp.StaticImports.tup;
import static org.organicdesign.fp.TestUtilities.compareIterators;
import static org.organicdesign.fp.TestUtilities.serializeDeserialize;
import static org.organicdesign.testUtils.EqualsContract.equalsDistinctHashCode;

/**
 Created by gpeterso on 9/16/16.
 */
public class ImInsertOrderMapTest2 {
    @Test public void testBasics() {
        List<Map.Entry<String,Integer>> control1 = new ArrayList<>();
        Map<String,Integer> control2 = new HashMap<>();
        ImMap<String,Integer> test = ImInsertOrderMap2.empty();
        assertEquals(0, test.size());
        assertEquals(Option.none(), test.entry("hello"));

        final int SIZE = 50;

        for (int i = SIZE; i > 0; i--) {
            control1.add(tup(ordinal(i), i));
            control2.put(ordinal(i), i);
            test = test.assoc(ordinal(i), i);
            assertEquals(Integer.valueOf(i), test.get(ordinal(i)));
            assertEquals((SIZE - i) + 1, test.size());
        }

        compareIterators(control1.iterator(), test.iterator());
        PersistentHashMapTest.mapIterTest(control2, test.iterator());
        PersistentHashMapTest.mapIterTest(test, control2.entrySet().iterator());
        compareIterators(control1.iterator(), serializeDeserialize(test).iterator());
        PersistentHashSetTest.setIterTest(control2.keySet(), test.keySet().iterator());

        assertEquals(control2.hashCode(), test.hashCode());

        equalsDistinctHashCode(control2, test, serializeDeserialize(test), test.assoc("hello", -3));

        control1.add(tup(null, -1));
        control2.put(null, -1);
        test = test.assoc(null, -1);

        compareIterators(control1.iterator(), test.iterator());
        PersistentHashMapTest.mapIterTest(control2, test.iterator());
        PersistentHashMapTest.mapIterTest(test, control2.entrySet().iterator());
        compareIterators(control1.iterator(), serializeDeserialize(test).iterator());
        PersistentHashSetTest.setIterTest(control2.keySet(), test.keySet().iterator());

        Random rand = new Random();

        for (int i = SIZE; i > 0; i--) {
            int idx = rand.nextInt(i);
            control1.remove(tup(ordinal(idx), idx));
            control2.remove(ordinal(idx), idx);
            test = test.without(ordinal(idx));
            assertEquals(control1.size(), test.size());
            compareIterators(control1.iterator(), test.iterator());
            PersistentHashMapTest.mapIterTest(control2, test.iterator());
            PersistentHashMapTest.mapIterTest(test, control2.entrySet().iterator());
            compareIterators(control1.iterator(), serializeDeserialize(test).iterator());
            PersistentHashSetTest.setIterTest(control2.keySet(), test.keySet().iterator());
        }

        // This doesn't work.
//        compareIterators(control.iterator(), test.entrySet().iterator());
    }

}