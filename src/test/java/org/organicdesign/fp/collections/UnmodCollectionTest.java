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
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.Assert.*;

public class UnmodCollectionTest {
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

    @Test public void testDidley() {
        // for those of you who want 100% test coverage just on principle, this one's for you.
        assertFalse(UnmodCollection.empty().contains(null));
        assertEquals(0, UnmodCollection.empty().size());
        assertTrue(UnmodCollection.empty().isEmpty());
        assertTrue(UnmodIterator.EMPTY == UnmodCollection.empty().iterator());
        assertFalse(unColl.isEmpty());
    }

    @SuppressWarnings("deprecation")
    @Test (expected = UnsupportedOperationException.class)
    public void unsupportedOp01() { unColl.add("hi"); }

    @SuppressWarnings("deprecation")
    @Test (expected = UnsupportedOperationException.class)
    public void unsupportedOp02() { unColl.addAll(Arrays.asList("hi", "there")); }

    @SuppressWarnings("deprecation")
    @Test (expected = UnsupportedOperationException.class)
    public void unsupportedOp03() { unColl.clear(); }

    @SuppressWarnings("deprecation")
    @Test (expected = UnsupportedOperationException.class)
    public void unsupportedOp04() { unColl.remove("hi"); }

    @SuppressWarnings("deprecation")
    @Test (expected = UnsupportedOperationException.class)
    public void unsupportedOp05() { unColl.removeAll(Arrays.asList("hi", "there")); }

    @SuppressWarnings("deprecation")
    @Test (expected = UnsupportedOperationException.class)
    public void unsupportedOp06() { unColl.removeIf(item -> false); }

    @SuppressWarnings("deprecation")
    @Test (expected = UnsupportedOperationException.class)
    public void unsupportedOp07() { unColl.retainAll(Arrays.asList("hi", "there")); }
}
