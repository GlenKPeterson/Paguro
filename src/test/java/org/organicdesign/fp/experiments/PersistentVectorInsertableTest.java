package org.organicdesign.fp.experiments;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PersistentVectorInsertableTest {
    @Test public void basicAppends() {
        PersistentVectorInsertable<Integer> pvi = PersistentVectorInsertable.empty();
//        System.out.println(pvi.toString());
        for (int i = 0; i < 200; i++) {
            pvi = pvi.insert(i, i);
//            System.out.println("=====================================================\n" +
//                               pvi.toString() +
//                               "\n=====================================================\n");
            assertEquals(Integer.valueOf(i), pvi.get(i));
            assertEquals(i + 1, pvi.size());
        }
//        System.out.println("\n\nFinal Vector:");
//        System.out.println(pvi.toString());

        for (int i = 0; i < 200; i++) {
            assertEquals(Integer.valueOf(i), pvi.get(i));
        }
    }
// Hey, it's even balanced - Yay!
// Branch(10, Array<Branch>(Branch(4, Array<Leaf>(Leaf(2,Array<Integer>(0,1)),
//                                                Leaf(2,Array<Integer>(2,3)))),
//                          Branch(6, Array<Leaf>(Leaf(2,Array<Integer>(4,5)),
//                                                Leaf(4,Array<Integer>(6,7,8,9))))))

// Nice, it's still balanced with 20.  I guess inserting at one end makes most nodes half-sized.
// I may end up using a max-size closer to 64, but I guess any multiple of 2 could do.
// One other thing that could work nicely is splitting 2/3 away from the direction I'm inserting, and 1/3 with.
// So if I was prepending, I'd split a node of length 9 into 3 and 6, but if I were appending, I'd split it into
// 6 and 3.
// Branch(20,Array<Branch>(Branch(8, Array<Branch>(Branch(4,Array<Leaf>(Leaf(2,Array<Integer>(0,1)),
//                                                                      Leaf(2,Array<Integer>(2,3)))),
//                                                 Branch(4,Array<Leaf>(Leaf(2,Array<Integer>(4,5)),
//                                                                      Leaf(2,Array<Integer>(6,7)))))),
//                         Branch(12,Array<Branch>(Branch(4,Array<Leaf>(Leaf(2,Array<Integer>(8,9)),
//                                                                      Leaf(2,Array<Integer>(10,11)))),
//                                                 Branch(8,Array<Leaf>(Leaf(2,Array<Integer>(12,13)),
//                                                                      Leaf(2,Array<Integer>(14,15)),
//                                                                      Leaf(4,Array<Integer>(16,17,18,19))))))))

    // TODO: Enable this and fix!
//    @Test public void basicPrepends() {
//        PersistentVectorInsertable<Integer> pvi = PersistentVectorInsertable.empty();
//        for (int i = 0; i < 200; i++) {
//            pvi = pvi.insert(0, i);
////            System.out.println("=====================================================\n" +
////                               pvi.toString() +
////                               "\n=====================================================\n");
//            assertEquals(Integer.valueOf(i), pvi.get(0));
//            assertEquals(i + 1, pvi.size());
//        }
//        System.out.println("\n\nFinal Vector:");
//        System.out.println(pvi.toString());
//
//        for (int i = 0; i < 200; i++) {
//            assertEquals(Integer.valueOf(199 - i), pvi.get(i));
//        }
//    }

}
