package org.organicdesign.fp.experiments;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PersistentVectorInsertableTest {
    @Test public void basicAppends() {
        PersistentVectorInsertable<Integer> pvi = PersistentVectorInsertable.empty();
        System.out.println(pvi.toString());
        for (int i = 0; i < 10; i++) {
            pvi = pvi.insert(i, i);
            System.out.println("=====================================================\n" +
                               pvi.toString() +
                               "\n=====================================================\n");
            assertEquals(Integer.valueOf(i), pvi.get(i));
            assertEquals(i + 1, pvi.size());
        }
        System.out.println("\n\nFinal Vector:");
        System.out.println(pvi.toString());

        for (int i = 0; i < 10; i++) {
            assertEquals(Integer.valueOf(i), pvi.get(i));
       }
    }

}
