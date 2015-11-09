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
import org.organicdesign.fp.tuple.Tuple2;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import static org.junit.Assert.*;
import static org.organicdesign.fp.testUtils.EqualsContract.equalsDistinctHashCode;

public class UnmodMapTest {
    static class TestMap<K,V> implements UnmodMap<K,V> {
        private final Map<K,V> inner;

        TestMap(Iterable<Entry<K,V>> items) {
            inner = new HashMap<>();
            for (Entry<K,V> item : items) {
                inner.put(item.getKey(), item.getValue());
            }
        }

        @Override public UnmodIterator<UnEntry<K,V>> iterator() {
            Iterator<Entry<K,V>> iter = inner.entrySet().iterator();
            return new UnmodIterator<UnEntry<K,V>>() {
                @Override public boolean hasNext() { return iter.hasNext(); }

                @Override public UnEntry<K,V> next() {
                    Entry<K,V> next = iter.next();
                    return Tuple2.of(next.getKey(), next.getValue());
                }
            };
        }

        @Override public int size() { return inner.size(); }

        @Override public boolean containsKey(Object key) { return inner.containsKey(key); }

        @Override public V get(Object key) { return inner.get(key); }
    }

    TestMap<String,Integer> unMap = new TestMap<>(Arrays.asList(
            Tuple2.of("a", 1),
            Tuple2.of("b", 2),
            Tuple2.of("c", 3)));

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

    final static UnmodMapTest.TestEntry<String,Integer> avoKey =
            new UnmodMapTest.TestEntry<>("avocado", -9);
    final static UnmodMapTest.TestEntry<String,Integer> banKey =
            new UnmodMapTest.TestEntry<>("banana", 777);
    final static UnmodMapTest.TestEntry<String,Integer> clemKey =
            new UnmodMapTest.TestEntry<>("clementine", 1);
    final static UnmodMapTest.TestEntry<String,Integer> junkKey =
            new UnmodMapTest.TestEntry<>("junk", -2);
    final static UnmodMapTest.TestEntry<String,Integer> pastLast =
            new UnmodMapTest.TestEntry<>("zzzLast", Integer.MIN_VALUE);

    final static Map<String,Integer> refMap = new HashMap<>();
    static {
        refMap.put(avoKey.getKey(), avoKey.getValue());
        refMap.put(banKey.getKey(), banKey.getValue());
        refMap.put(clemKey.getKey(), clemKey.getValue());
    }

    final static TestMap<String,Integer> testMap =
            new TestMap<>(Arrays.asList(avoKey, banKey, clemKey));

    final static Map<String,Integer> uneqMap = new HashMap<>();
    static {
        uneqMap.put(avoKey.getKey(), avoKey.getValue());
        uneqMap.put(banKey.getKey(), banKey.getValue());
        uneqMap.put(junkKey.getKey(), junkKey.getValue());
    }

    @Test public void entrySet() {
        Set<Map.Entry<String,Integer>> refEntSet = refMap.entrySet();
        Set<Map.Entry<String,Integer>> testEntSet = testMap.entrySet();

        assertEquals(refEntSet.size(), testEntSet.size());

        assertFalse(refEntSet.contains(null));
        assertFalse(testEntSet.contains(null));
        assertFalse(refEntSet.contains(junkKey));
        assertFalse(testEntSet.contains(junkKey));
        assertTrue(refEntSet.contains(avoKey));
        assertTrue(testEntSet.contains(avoKey));
        assertTrue(refEntSet.contains(banKey));
        assertTrue(testEntSet.contains(banKey));
        assertTrue(refEntSet.contains(clemKey));
        assertTrue(testEntSet.contains(clemKey));

        UnmodListTest.iteratorTest(refEntSet.iterator(), testEntSet.iterator());

        equalsDistinctHashCode(testEntSet, refEntSet, testMap.entrySet(), uneqMap.entrySet());

        assertTrue(testEntSet.toString().startsWith("UnmodMap.entrySet"));
    }

    @Test public void keySetTest() {
        Set<String> refKeySet = refMap.keySet();
        Set<String> testKeySet = testMap.keySet();

        assertEquals(refKeySet.size(), testKeySet.size());

        assertFalse(refKeySet.contains(null));
        assertFalse(testKeySet.contains(null));
        assertFalse(refKeySet.contains(junkKey.getKey()));
        assertFalse(testKeySet.contains(junkKey.getKey()));
        assertTrue(refKeySet.contains(avoKey.getKey()));
        assertTrue(testKeySet.contains(avoKey.getKey()));
        assertTrue(refKeySet.contains(banKey.getKey()));
        assertTrue(testKeySet.contains(banKey.getKey()));
        assertTrue(refKeySet.contains(clemKey.getKey()));
        assertTrue(testKeySet.contains(clemKey.getKey()));

        UnmodListTest.iteratorTest(refKeySet.iterator(), testKeySet.iterator());

        equalsDistinctHashCode(testKeySet, refKeySet, testMap.keySet(), uneqMap.keySet());

        assertTrue(testKeySet.toString().startsWith("UnmodMap.keySet"));
    }

    @Test public void valuesTest() {
        Collection<Integer> refValues = refMap.values();
        Collection<Integer> testValues = testMap.values();

        assertEquals(refValues.size(), testValues.size());

        assertFalse(refValues.contains(null));
        assertFalse(testValues.contains(null));
        assertFalse(refValues.contains(junkKey.getValue()));
        assertFalse(testValues.contains(junkKey.getValue()));
        assertTrue(refValues.contains(avoKey.getValue()));
        assertTrue(testValues.contains(avoKey.getValue()));
        assertTrue(refValues.contains(banKey.getValue()));
        assertTrue(testValues.contains(banKey.getValue()));
        assertTrue(refValues.contains(clemKey.getValue()));
        assertTrue(testValues.contains(clemKey.getValue()));

        UnmodListTest.iteratorTest(refValues.iterator(), testValues.iterator());

//        System.out.println("uneqMap.values(): " + uneqMap.values());
//        System.out.println("uneqMap.values() class: " + uneqMap.values().getClass().getCanonicalName());

        // java.util.HashMap.Values does not implement equals() or hashCode() and therefore
        // inherits them from java.lang.Object, which only does referential equality.
        // As a result, there is no way to be equal to the resulting collection.
        // Hmm...  I don't think equals() can be implemented the return type for Map.values() which
        // can have duplicates and may be ordered, or unordered.
//        equalsDistinctHashCode(testValues, testMap.values(), testMap.values(), uneqMap.values());

        assertTrue(testValues.toString().startsWith("UnmodMap.values"));
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

        @Override public String toString() { return "Entry(" + key + "," + value + ")"; }

        @Override public int hashCode() {
            int ret = (key == null) ? 0 : key.hashCode();
            return ret ^ ((key == null) ? 0 : value.hashCode());
        }

        @Override public boolean equals(Object other) {
            if (this == other) { return true; }
            if ( !(other instanceof Map.Entry)) { return false; }
            Map.Entry that = (Map.Entry) other;
            return Objects.equals(key,  that.getKey()) &&
                   Objects.equals(value, that.getValue());
        }
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


        Map<String,Integer> mm = new HashMap<>();
        mm.put("Hello", 3);
//        Map.Entry<String,Integer> entry = mm.entrySet().iterator().next();
//        System.out.println("helloHash: " + "Hello".hashCode());
//        System.out.println("entryHash: " + entry.hashCode());
//        System.out.println("tupHash: " + Tuple2.of("Hello", 3).hashCode());
//        System.out.println("entryClass: " + entry.getClass().getCanonicalName());
//        System.out.println("mm.entrySet() Class: " + mm.entrySet().getClass().getCanonicalName());

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
