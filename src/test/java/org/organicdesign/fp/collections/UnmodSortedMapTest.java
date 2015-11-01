package org.organicdesign.fp.collections;

import org.junit.Test;
import org.organicdesign.fp.FunctionUtils;
import org.organicdesign.fp.tuple.Tuple2;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import static org.junit.Assert.*;

public class UnmodSortedMapTest {
    static class TestMap<K, V> implements UnmodSortedMap<K,V> {

        static <K, V> SortedMap<K,V> dup(SortedMap<K,V> in) {
            SortedMap<K,V> out = new TreeMap<>(in.comparator());
            out.putAll(in);
            return out;
        }

        private final SortedMap<K,V> inner;

        TestMap(SortedMap<K,V> items, Comparator<? super K> comp) {
            inner = (comp == null)
                    ? new TreeMap<>()
                    : new TreeMap<>(comp);
            inner.putAll(items);
        }

        @Override
        public int size() { return inner.size(); }

        @Override
        public boolean containsKey(Object key) { return inner.containsKey(key); }

        @Override
        public V get(Object key) { return inner.get(key); }

        @Override
        public Comparator<? super K> comparator() { return inner.comparator(); }

        @Override
        public UnmodSortedMap<K,V> subMap(K fromKey, K toKey) {
            return FunctionUtils.unmodSortedMap(inner.subMap(fromKey, toKey));
        }

        @Override
        public UnmodSortedMap<K,V> tailMap(K fromKey) {
            return FunctionUtils.unmodSortedMap(inner.tailMap(fromKey));
        }

        @Override
        public K firstKey() { return inner.firstKey(); }

        @Override
        public K lastKey() { return inner.lastKey(); }

        @Override public UnmodSortedIterator<UnEntry<K,V>> iterator() {
            return new UnmodSortedIterator<UnEntry<K,V>>() {
                Iterator<Entry<K,V>> iter = inner.entrySet().iterator();
                @Override public boolean hasNext() { return iter.hasNext(); }

                @Override public UnEntry<K,V> next() {
                    Entry<K,V> entry = iter.next();
                    return Tuple2.of(entry.getKey(), entry.getValue());
                }
            };
        }
    }

    static class TestEntry<K,V> implements Map.Entry<K,V> {
        private final K key;
        private final V value;
        TestEntry(K k, V v) { key = k; value = v; }
        @Override public K getKey() { return key; }

        @Override public V getValue() { return value; }

        @Override public V setValue (V v) {
            throw new UnsupportedOperationException("no way");
        }
    }

    @Test public void entrySet() {
        SortedMap<String,Integer> refMap = new TreeMap<>();
        UnmodMapTest.TestEntry<String,Integer> avoKey = new UnmodMapTest.TestEntry<>("avocado", -9);
        refMap.put(avoKey.getKey(), avoKey.getValue());
        UnmodMapTest.TestEntry<String,Integer> banKey = new UnmodMapTest.TestEntry<>("banana", 777);
        refMap.put(banKey.getKey(), banKey.getValue());
        UnmodMapTest.TestEntry<String,Integer> clemKey = new UnmodMapTest.TestEntry<>("clementine",
                                                                                      1);
        refMap.put(clemKey.getKey(), clemKey.getValue());
        UnmodMapTest.TestEntry<String,Integer> junkKey = new UnmodMapTest.TestEntry<>("junk",
                                                                                      -2);

        TestMap<String,Integer> testMap = new TestMap<>(refMap, null);

        Set<Map.Entry<String,Integer>> refEntSet = refMap.entrySet();
        UnmodSortedSet<Map.Entry<String,Integer>> testEntSet = testMap.entrySet();

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

        Comparator<? super Map.Entry<String,Integer>> testComp0 = testEntSet.comparator();
        assert testComp0 != null;
        assertTrue("D".compareTo("d") < 0);
        assertTrue(testComp0.compare(Tuple2.of("D", 3), Tuple2.of("d", -1)) < 0);

        Comparator<? super Map.Entry<String,Integer>> testComp =
                new TestMap<>(refMap, String.CASE_INSENSITIVE_ORDER).entrySet().comparator();

        assertEquals(0, String.CASE_INSENSITIVE_ORDER.compare("D", "d"));
        assert testComp != null;
        assertEquals(0, testComp.compare(Tuple2.of("D", 3), Tuple2.of("d", -1)));

//        UnmodMapTest.TestEntry<String,Integer>[] testEntries =
//                (UnmodMapTest.TestEntry<String,Integer>[]) new UnmodMapTest.TestEntry[] {
//                        new UnmodMapTest.TestEntry<>("011first", Integer.MAX_VALUE),
//                        avoKey, banKey, clemKey,
//                        new UnmodMapTest.TestEntry<>("zzzLast", Integer.MIN_VALUE),
//                };

        UnmodMapTest.TestEntry<String,Integer> pastLast =
                new UnmodMapTest.TestEntry<>("zzzLast", Integer.MIN_VALUE);

        UnmodSortedSet<Map.Entry<String,Integer>> empty = testEntSet.subSet(avoKey, avoKey);
        // The subset is the empty set.
        assertEquals(0, empty.size());
        assertFalse(empty.contains(avoKey));
        assertFalse(empty.contains(banKey));
        assertFalse(empty.contains(clemKey));

        UnmodSortedSet<Map.Entry<String,Integer>> avoSet = testEntSet.subSet(avoKey, banKey);
        assertEquals(1, avoSet.size());
        assertTrue(avoSet.contains(avoKey));
        assertFalse(avoSet.contains(banKey));
        assertFalse(avoSet.contains(clemKey));

        UnmodSortedSet<Map.Entry<String,Integer>> banSet = testEntSet.subSet(banKey, clemKey);
        assertEquals(1, banSet.size());
        assertFalse(banSet.contains(avoKey));
        assertTrue(banSet.contains(banKey));
        assertFalse(banSet.contains(clemKey));

        UnmodSortedSet<Map.Entry<String,Integer>> clemSet = testEntSet.subSet(clemKey, pastLast);
        assertEquals(1, clemSet.size());
        assertFalse(clemSet.contains(avoKey));
        assertFalse(clemSet.contains(banKey));
        assertTrue(clemSet.contains(clemKey));

        UnmodSortedSet<Map.Entry<String,Integer>> avoBanSet = testEntSet.subSet(avoKey, clemKey);
        assertEquals(2, avoBanSet.size());
        assertTrue(avoBanSet.contains(avoKey));
        assertTrue(avoBanSet.contains(banKey));
        assertFalse(avoBanSet.contains(clemKey));

        UnmodSortedSet<Map.Entry<String,Integer>> banClemSet = testEntSet.subSet(banKey, pastLast);
        assertEquals(2, banClemSet.size());
        assertFalse(banClemSet.contains(avoKey));
        assertTrue(banClemSet.contains(banKey));
        assertTrue(banClemSet.contains(clemKey));

        UnmodSortedSet<Map.Entry<String,Integer>> fullSet = testEntSet.subSet(avoKey, pastLast);

        // The subset is the whole set.
        assertEquals(3, fullSet.size());
        assertTrue(fullSet.containsAll(testEntSet));
        assertTrue(testEntSet.containsAll(fullSet));

        // tailSet tests

        UnmodSortedSet<Map.Entry<String,Integer>> emptyTail = testEntSet.tailSet(pastLast);
        // The tailSet is the empty set.
        assertEquals(0, emptyTail.size());
        assertFalse(emptyTail.contains(avoKey));
        assertFalse(emptyTail.contains(banKey));
        assertFalse(emptyTail.contains(clemKey));

        UnmodSortedSet<Map.Entry<String,Integer>> clemTail = testEntSet.tailSet(clemKey);
        assertEquals(1, clemTail.size());
        assertFalse(clemTail.contains(avoKey));
        assertFalse(clemTail.contains(banKey));
        assertTrue(clemTail.contains(clemKey));

        UnmodSortedSet<Map.Entry<String,Integer>> banTail = testEntSet.tailSet(banKey);
        assertEquals(2, banTail.size());
        assertFalse(banTail.contains(avoKey));
        assertTrue(banTail.contains(banKey));
        assertTrue(banTail.contains(clemKey));

        UnmodSortedSet<Map.Entry<String,Integer>> fullTail = testEntSet.tailSet(avoKey);

        // The subset is the whole set.
        assertEquals(3, fullTail.size());
        assertTrue(fullTail.containsAll(testEntSet));
        assertTrue(testEntSet.containsAll(fullTail));

        // headSet tests

        UnmodSortedSet<Map.Entry<String,Integer>> emptyHead = testEntSet.headSet(avoKey);
        // The headSet is the empty set.
        assertEquals(0, emptyHead.size());
        assertFalse(emptyHead.contains(avoKey));
        assertFalse(emptyHead.contains(banKey));
        assertFalse(emptyHead.contains(clemKey));

        UnmodSortedSet<Map.Entry<String,Integer>> clemHead = testEntSet.headSet(banKey);
        assertEquals(1, clemHead.size());
        assertTrue(clemHead.contains(avoKey));
        assertFalse(clemHead.contains(banKey));
        assertFalse(clemHead.contains(clemKey));

        UnmodSortedSet<Map.Entry<String,Integer>> banHead = testEntSet.headSet(clemKey);
        assertEquals(2, banHead.size());
        assertTrue(banHead.contains(avoKey));
        assertTrue(banHead.contains(banKey));
        assertFalse(banHead.contains(clemKey));

        UnmodSortedSet<Map.Entry<String,Integer>> fullHead = testEntSet.headSet(pastLast);

        // The subset is the whole set.
        assertEquals(3, fullHead.size());
        assertTrue(fullHead.containsAll(testEntSet));
        assertTrue(testEntSet.containsAll(fullHead));

    }
}