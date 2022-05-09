package org.organicdesign.fp.collections;


import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.organicdesign.fp.FunctionUtils.ordinal;

/**
 Created by gpeterso on 9/13/16.
 */
public class ImUnsortedSetTest {

    @Test
    public void testUnion() {
        List<String> origItems = Arrays.asList("This", "is", "a", "test");
        Set<String> control = new HashSet<>(origItems);
        ImSet<String> test = new ImSetTest.ImTestSet<>(origItems);

        assertEquals(control, test);

        for (int i = 0; i < 10; i++) {
            String ord = ordinal(i);
            control.add(ord);
            test = test.put(ord);
            assertEquals(control.size(), test.size());
            assertEquals(control, test);
        }

        List<String> addedItems = Arrays.asList("more", "stuff");

        control.addAll(addedItems);
        test = test.union(addedItems);

        assertEquals(control, test);

        assertTrue(test == test.union(null));
    }

}