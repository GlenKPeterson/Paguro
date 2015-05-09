package org.organicdesign.fp.collections;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ImListTest {
    @Test public void insert() {
        // Computer science is no more about computers than astronomy is about telescopes. - Dijkstra
        ImList<String> p = PersistentVector.of("computers ")
                                           .insert(0, "is ");

        p = p.insert(1, "more ")
             .insert(0, "Computer ")
             .insert(1, "science ");
        p = p.insert(3, "no ")
             .insert(5, "about ")
             .insert(7, "about ")
             .insert(7, "is ")
             .insert(7, "astronomy ")
             .insert(7, "than ");
        p = p.insert(p.size(), "telescopes.");

        StringBuilder sB = new StringBuilder();
        for (String s : p) { sB.append(s); }
        assertEquals("Computer science is no more about computers than astronomy is about telescopes.",
                     sB.toString());

        assertEquals("PersistentVector(Computer ,science ,is ,no ,more ,...)",
                     p.toString());
    }

    @Test public void get() {
        PersistentVector<String> pv = PersistentVector.of("Four", "score", "and", "seven", "years", "ago...");
        assertEquals("Million", pv.get(Integer.MIN_VALUE, "Million"));
        assertEquals("Million", pv.get(-1, "Million"));

        assertEquals("Four", pv.get(0, "Million"));
        assertEquals("ago...", pv.get(5, "Million"));

        assertEquals("Million", pv.get(6, "Million"));
        assertEquals("Million", pv.get(Integer.MAX_VALUE, "Million"));
    }
}
