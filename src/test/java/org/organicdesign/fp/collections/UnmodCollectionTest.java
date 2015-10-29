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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.Assert.*;

public class UnmodCollectionTest {
    @Test public void contains() {
        Collection<String> c1 = Arrays.asList("Hello", "there", "world", "this", "is", "a", "test");
        assertFalse(UnmodCollection.contains(c1, "poodle"));
        assertFalse(UnmodCollection.contains(c1, null));
        assertTrue(UnmodCollection.contains(c1, "Hello"));
        assertTrue(UnmodCollection.contains(c1, "this"));
        assertTrue(UnmodCollection.contains(c1, "test"));

        assertTrue(UnmodCollection.containsAll(c1, c1));
        assertTrue(UnmodCollection.containsAll(c1, Arrays.asList("there", "this", "a")));
        assertTrue(UnmodCollection.containsAll(c1, Arrays.asList("Hello", "world", "is", "test")));
        assertFalse(UnmodCollection.containsAll(c1, Arrays.asList("Hello", "world", "is", "test", "noodle")));
        assertFalse(UnmodCollection.containsAll(c1, Arrays.asList("billboard", "Hello", "world", "is", "test")));
        assertFalse(UnmodCollection.containsAll(c1, Collections.singletonList("phone")));
        assertFalse(UnmodCollection.containsAll(c1, Collections.singletonList(null)));

        assertFalse(UnmodCollection.contains(UnmodCollection.empty(), "phone"));
        assertFalse(UnmodCollection.contains(UnmodCollection.empty(), null));
        assertFalse(UnmodCollection.containsAll(UnmodCollection.empty(), Collections.singletonList("phone")));
        assertFalse(UnmodCollection.containsAll(UnmodCollection.empty(), Collections.singletonList(null)));
    }

    private static final String[] sticksAndStones = new String[] {
            "Sticks", "and", "stones", "will", "break", "my", "bones", "but", "tests",
            "will", "never", "hurt", "me." };

    // unColl is part of where the UncleJim names comes from.
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private static final UnmodCollection<String> unColl = new UnmodCollection<String>() {
        @Override public UnmodIterator<String> iterator() {
            return new UnmodIterator<String>() {
                int idx = 0;
                @Override public boolean hasNext() { return idx < sticksAndStones.length; }

                @Override public String next() {
                    // I think this temporary variable i gets compiled to a register access
                    // Load memory value from idx to register.  This is the index we will use against
                    // our internal data.
                    int i = idx;
                    // Throw based on value in register
                    if (i >= size()) { throw new NoSuchElementException(); }
                    // Store incremented register value back to memory.  Note that this is the
                    // next index value we will access.
                    idx = i + 1;
                    // call get() using the old value of idx (before our increment).
                    // i should still be in the register, not in memory.
                    return sticksAndStones[i];
                }
            };
        }
        @Override public int size() { return sticksAndStones.length; }
    };

    @Test public void containsTest() {
        for (String s : sticksAndStones) {
            assertTrue(unColl.contains(s));
        }
        assertFalse(unColl.contains("phrog"));
    }

    @Test public void containsAllTest() {
        List<String> ls = Arrays.asList(sticksAndStones);
        assertTrue(ls.containsAll(unColl));
        assertTrue(unColl.containsAll(ls));

        List<String> ls2 = Arrays.asList("Sticks", "and", "stones", "will", "break", "my", "bones",
                                         "but", "tests", "will", "never", "hurt");
        assertFalse(ls2.containsAll(unColl));
        assertTrue(unColl.containsAll(ls2));

        List<String> ls3 = Arrays.asList("Sticks", "and", "stones", "will", "break", "my", "bones",
                                         "but", "tests", "will", "never", "hurt", "me.",
                                         "maybe");
        assertTrue(ls3.containsAll(unColl));
        assertFalse(unColl.containsAll(ls3));
    }

    @Test public void toArrayTest() {
        assertArrayEquals(sticksAndStones, unColl.toArray());
        assertArrayEquals(sticksAndStones, unColl.toArray(new String[3]));
        assertArrayEquals(sticksAndStones, unColl.toArray(new String[sticksAndStones.length]));
        String [] result = unColl.toArray(new String[sticksAndStones.length + 3]);
        assertEquals(sticksAndStones.length + 3, result.length);
        assertEquals(null, result[result.length - 3]);
        assertEquals(null, result[result.length - 2]);
        assertEquals(null, result[result.length - 1]);
    }

    @Test public void toArray() {
        Collection<String> c1 = Arrays.asList("Hello", "there", "world", "this", "is", "a", "test");
        assertArrayEquals(new String[]{"Hello", "there", "world", "this", "is", "a", "test"},
                          UnmodCollection.toArray(c1));
        assertArrayEquals(new String[]{"Hello", "there", "world", "this", "is", "a", "test"},
                          UnmodCollection.toArray(c1, new String[0]));
        assertArrayEquals(new String[]{"Hello", "there", "world", "this", "is", "a", "test"},
                          UnmodCollection.toArray(c1, new String[7]));
        assertArrayEquals(new String[]{"Hello", "there", "world", "this", "is", "a", "test"},
                          Arrays.copyOf(UnmodCollection.toArray(c1, new String[99]), 7));

        assertArrayEquals(new Object[0],
                          UnmodCollection.toArray(UnmodCollection.empty()));
        assertArrayEquals(new Object[0],
                          UnmodCollection.toArray(UnmodCollection.empty(), new Object[0]));
    }
}
