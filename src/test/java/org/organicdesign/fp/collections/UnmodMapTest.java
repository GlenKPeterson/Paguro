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
import org.organicdesign.fp.FunctionUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import static org.junit.Assert.*;

public class UnmodMapTest {
    static UnmodMap<String,Integer> unMap = new UnmodMap<String,Integer>() {
        String[] keys = new String[] { "a", "b", "c" };
        int[] vals = new int[] { 1, 2, 3 };

        @Override public UnmodSet<Entry<String,Integer>> entrySet() { return UnmodSet.empty(); }

        @Override public UnmodSet<String> keySet() { return UnmodSet.empty(); }

        @Override public UnmodCollection<Integer> values() {
            return new UnmodCollection<Integer>() {
                @Override public UnmodIterator<Integer> iterator() {
                    return new UnmodIterator<Integer>() {
                        int i = 0;
                        @Override public boolean hasNext() { return i < vals.length; }

                        @Override public Integer next() {
                            int idx = i;
                            i = i + 1;
                            return vals[idx];
                        }
                    };
                }
                @Override public int size() { return vals.length; }
            };
        }

        @Override public int size() { return keys.length; }

        @Override public boolean containsKey(Object key) {
            for (String s : keys) {
                if (Objects.equals(key, s)) { return true; }
            }
            return false;
        }

        @Override public Integer get(Object key) {
            for (int i = 0; i < keys.length; i++) {
                if (Objects.equals(key, keys[i])) {
                    return vals[i];
                }
            }
            return null;
        }

        @Override public UnmodIterator<UnEntry<String,Integer>> iterator() {
            return null;
        }
    };

    @Test public void containsValue() {
        Map<String,Integer> mm = new HashMap<>();
        mm.put("Hello", 3);
        mm.put("World", 2);
        mm.put("This", 1);
        mm.put("Is", 0);
        mm.put("A", -1);
        mm.put("test", -2);

        UnmodMap<String,Integer> m = FunctionUtils.unmodMap(mm);
        assertFalse(m.containsValue(Integer.MAX_VALUE));
        assertFalse(m.containsValue(4));
        assertTrue(m.containsValue(3));
        assertTrue(m.containsValue(0));
        assertTrue(m.containsValue(-2));
        assertFalse(m.containsValue(-3));
        assertFalse(m.containsValue(Integer.MIN_VALUE));

        assertFalse(unMap.containsValue(null));
        assertFalse(unMap.containsValue(Integer.MIN_VALUE));
        assertFalse(unMap.containsValue(Integer.MAX_VALUE));
        assertFalse(unMap.containsValue(-99));
        assertTrue(unMap.containsValue(1));
        assertTrue(unMap.containsValue(2));
        assertTrue(unMap.containsValue(3));
        assertFalse(unMap.isEmpty());
    }

    @SuppressWarnings("deprecation")
    @Test (expected = UnsupportedOperationException.class)
    public void unsupportedOpUnEntrySetValue() {
        new UnmodMap.UnEntry<String,Integer>() {
            @Override public String getKey() { return null; }
            @Override public Integer getValue() { return null; }
        }.setValue(null);
    }

//    @Test public void unEntryTest() {
//        UnmodMap.UnEntry<String,Integer> unEntry = new UnmodMap.UnEntry<String,Integer>() {
//            @Override public String getKey() { return "Hello"; }
//            @Override public Integer getValue() { return 1; }
//        };
//    }


    @Test public void emptyTest() {
        assertEquals(0, UnmodMap.empty().entrySet().size());
        assertTrue(UnmodSet.EMPTY == UnmodMap.empty().keySet());
        assertTrue(UnmodCollection.EMPTY == UnmodMap.empty().values());
        assertEquals(0, UnmodMap.empty().size());
        assertTrue(UnmodMap.empty().isEmpty());
        assertTrue(UnmodIterator.<UnmodMap.UnEntry<Object,Object>>empty() ==
                   UnmodMap.empty().iterator());
        assertFalse(UnmodMap.empty().containsKey(null));
        assertFalse(UnmodMap.empty().containsValue(null));
        assertNull(UnmodMap.empty().get(null));
    }

    @SuppressWarnings("deprecation")
    @Test (expected = UnsupportedOperationException.class)
    public void unsupportedOpClear() { unMap.clear(); }

    @SuppressWarnings("deprecation")
    @Test (expected = UnsupportedOperationException.class)
    public void unsupportedOpCompute() { unMap.compute(null, null); }

    @SuppressWarnings("deprecation")
    @Test (expected = UnsupportedOperationException.class)
    public void unsupportedOpComputeIfAbsent() { unMap.computeIfAbsent(null, null); }

    @SuppressWarnings("deprecation")
    @Test (expected = UnsupportedOperationException.class)
    public void unsupportedOpComputeIfPresent() { unMap.computeIfPresent(null, null); }

    @SuppressWarnings("deprecation")
    @Test (expected = UnsupportedOperationException.class)
    public void unsupportedOpMerge() { unMap.merge(null, null, null); }

    @SuppressWarnings("deprecation")
    @Test (expected = UnsupportedOperationException.class)
    public void unsupportedOpPut() { unMap.put(null, null); }

    @SuppressWarnings("deprecation")
    @Test (expected = UnsupportedOperationException.class)
    public void unsupportedOpPutAll() { unMap.putAll(null); }

    @SuppressWarnings("deprecation")
    @Test (expected = UnsupportedOperationException.class)
    public void unsupportedOpPutIfAbsent() { unMap.putIfAbsent(null, null); }

    @SuppressWarnings("deprecation")
    @Test (expected = UnsupportedOperationException.class)
    public void unsupportedOpRemove() { unMap.remove(null); }

    @SuppressWarnings("deprecation")
    @Test (expected = UnsupportedOperationException.class)
    public void unsupportedOpRemoveKv() { unMap.remove(null, null); }

    @SuppressWarnings("deprecation")
    @Test (expected = UnsupportedOperationException.class)
    public void unsupportedOpReplace() { unMap.replace(null, null); }

    @SuppressWarnings("deprecation")
    @Test (expected = UnsupportedOperationException.class)
    public void unsupportedOpReplace3() { unMap.replace(null, null, null); }

    @SuppressWarnings("deprecation")
    @Test (expected = UnsupportedOperationException.class)
    public void unsupportedOpReplaceAll() { unMap.replaceAll(null); }

    static class TestEntry<K,V> implements Map.Entry<K,V> {
        private K key;
        private V value;

        TestEntry(K k, V v) { key = k; value = v; }

        @Override public K getKey() { return key; }

        @Override public V getValue() { return value; }

        @Override public V setValue(V val) { value = val; return val; }
    }

    static class TestUnEntry<K,V> implements UnmodMap.UnEntry<K,V> {
        private final K key;
        private final V value;

        TestUnEntry(K k, V v) { key = k; value = v; }

        @Override public K getKey() { return key; }

        @Override public V getValue() { return value; }
    }

    static Map.Entry<String,Integer> me = new TestEntry<>("Hello", 37);
    static UnmodMap.UnEntry<String,Integer> ue = UnmodMap.UnEntry.entryToUnEntry(me);

    @Test public void unEntryTest() {
        assertEquals(me.getKey(), ue.getKey());
        assertEquals(me.getValue(), ue.getValue());

        Map<String,Integer> map = new TreeMap<>();
        map.put("ant", 99);
        map.put("bee", 88);
        map.put("caterpillar", 77);

        UnmodListTest.iteratorTest(map.entrySet().iterator(),
                                   UnmodMap.UnEntry.entryIterToUnEntryUnIter(map.entrySet()
                                                                                .iterator()));
    }

    @SuppressWarnings("deprecation")
    @Test (expected = UnsupportedOperationException.class)
    public void testUnEntryEx() { ue.setValue(null); }
}
