// Copyright 2015 PlanBase Inc. & Glen Peterson
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.organicdesign.fp.collections;

import org.junit.Test;
import org.organicdesign.fp.TestUtilities;

import java.util.*;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class ImListTest {
//    @Test public void insert() {
//        // Computer science is no more about computers than astronomy is about telescopes. - Dijkstra
//        ImList<String> p = vec("computers ").insert(0, "is ");
//
//        p = p.insert(1, "more ")
//             .insert(0, "Computer ")
//             .insert(1, "science ");
//        p = p.insert(3, "no ")
//             .insert(5, "about ")
//             .insert(7, "about ")
//             .insert(7, "is ")
//             .insert(7, "astronomy ")
//             .insert(7, "than ");
//        p = p.insert(p.size(), "telescopes.");
//
//        StringBuilder sB = new StringBuilder();
//        for (String s : p) { sB.append(s); }
//        assertEquals("Computer science is no more about computers than astronomy is about telescopes.",
//                     sB.toString());
//
//        assertEquals("PersistentVector(Computer ,science ,is ,no ,more ,...)",
//                     p.toString());
//    }

    private static class TestList<T> implements ImListTrans<T> {
        static <T> List<T> dup(Collection<T> in) {
            List<T> out = new ArrayList<>();
            out.addAll(in);
            return out;
        }

        private final List<T> inner;

        TestList(Collection<T> s) {
            inner = new ArrayList<>();
            inner.addAll(s);
        }

        @Override public ImListTrans<T> append(T t) {
            List<T> next = dup(inner);
            next.add(t);
            return new TestList<>(next);
        }

        @Override public ImListTrans<T> replace(int idx, T t) {
            List<T> next = dup(inner);
            next.set(idx, t);
            return new TestList<>(next);
        }

        @Override public int size() { return inner.size(); }

        @Override public T get(int index) { return inner.get(index); }

        @Override public ImList<T> persistent() { return this; }

        @Override public ImListTrans<T> asTransient() {
            // Not in the spirit of what's expected, but it technically does
            // implement Transient.
            return this;
        }
    }

    @Test public void get() {
        ImList<String> pv =
                new TestList<>(Arrays.asList("Four", "score", "and", "seven",
                                             "years", "ago..."));
        assertEquals("Million", pv.get(Integer.MIN_VALUE, "Million"));
        assertEquals("Million", pv.get(-1, "Million"));

        assertEquals("Four", pv.get(0, "Million"));
        assertEquals("ago...", pv.get(5, "Million"));

        assertEquals("Million", pv.get(6, "Million"));
        assertEquals("Million", pv.get(Integer.MAX_VALUE, "Million"));
    }

    @Test public void concatTest() {
        assertArrayEquals(new String[]{"a", "b", "c", "d", "e", "f"},
                          new TestList<>(Arrays.asList("a", "b", "c"))
                                  .concat(Arrays.asList("d", "e", "f")).toArray());
    }

    @Test public void iteratorTest() {
        String[] fourScore = new String[] {"Four", "score", "and", "seven", "years", "ago..."};
        TestUtilities.listIteratorTest(Arrays.asList(fourScore),
                                       new TestList<>(Arrays.asList(fourScore)));
    }

    @Test public void reverseTest() {
        List<Integer> control = new ArrayList<>();
        ImList<Integer> test = new TestList<>(Collections.emptyList());
        for (int i = 0; i < 100; i++) {
            control.add(i);
            test = test.append(i);
        }
        assertEquals(control.size(), test.size());
        TestUtilities.compareIterators(control.iterator(), test.iterator());

        Collections.reverse(control);
        test = test.reverse();
        assertEquals(control.size(), test.size());
        TestUtilities.compareIterators(control.iterator(), test.iterator());
    }
}
