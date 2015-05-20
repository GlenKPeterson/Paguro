package org.organicdesign.fp.experiments;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PersistentVectorInsertableTest {
    @Test public void basicAppends() {
        PersistentVectorInsertable<Integer> pvi = PersistentVectorInsertable.empty();
        System.out.println(pvi.toString());
        for (int i = 0; i < 20; i++) {
            pvi = pvi.insert(i, i);
            System.out.println("=====================================================\n" +
                               pvi.toString() +
                               "\n=====================================================\n");
            assertEquals(Integer.valueOf(i), pvi.get(i));
            assertEquals(i + 1, pvi.size());
        }
        System.out.println("\n\nFinal Vector:");
        System.out.println(pvi.toString());

        for (int i = 0; i < 20; i++) {
            assertEquals(Integer.valueOf(i), pvi.get(i));
       }
// Hey, it's even balanced - Yay!
// Branch(10, Array<Branch>(Branch(4, Array<Leaf>(Leaf(2,Array<Integer>(0,1)),
//                                                Leaf(2,Array<Integer>(2,3)))),
//                          Branch(6, Array<Leaf>(Leaf(2,Array<Integer>(4,5)),
//                                                Leaf(4,Array<Integer>(6,7,8,9))))))

    }

}
