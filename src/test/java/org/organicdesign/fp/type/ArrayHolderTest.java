package org.organicdesign.fp.type;

import org.junit.Test;
import org.organicdesign.testUtils.EqualsContract;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class ArrayHolderTest {
    @Test public void testBasics() {
        ArrayHolder<Class> ah1 = new ArrayHolder<>(String.class, Integer.class);
        ArrayHolder<Class> ah2 = new ArrayHolder<>(String.class, Integer.class);
        ArrayHolder<Class> ah3 = new ArrayHolder<>(String.class, Float.class);
        assertEquals(ah1.hashCode(), ah2.hashCode());
        assertNotEquals(ah1.hashCode(), ah3.hashCode());
        assertEquals(ah1, ah1);
        EqualsContract.equalsDistinctHashCode(ah1,
                                              ah2,
                                              new ArrayHolder<>(String.class, Integer.class),
                                              ah3);

        EqualsContract.equalsDistinctHashCode(new ArrayHolder<>(true, false),
                                              new ArrayHolder<>(true, false),
                                              new ArrayHolder<>(true, false),
                                              new ArrayHolder<>(false, false));

        // https://stackoverflow.com/questions/15576009/how-to-make-hashmap-work-with-arrays-as-key

    // boolean[] a = {false, false};
    ArrayHolder<Boolean> a = new ArrayHolder<>(false, false);

    // h = new HashMap<boolean[], Integer>();
    Map<ArrayHolder<Boolean>, Integer> h = new HashMap<>();

    h.put(a, 1);

    // if(h.containsKey(a)) System.out.println("Found a");
    assertTrue(h.containsKey(a));

    // boolean[] t = {false, false};
    ArrayHolder<Boolean> t = new ArrayHolder<>(false, false);

    // if(h.containsKey(t)) System.out.println("Found t");
    assertTrue(h.containsKey(t));

    assertFalse(h.containsKey(new ArrayHolder<>(true, false)));
    }

}