package org.organicdesign.fp.collections;

import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.junit.Test;
import org.organicdesign.fp.FunctionUtils;

import static org.junit.Assert.*;
import static org.organicdesign.fp.StaticImports.*;
import static org.organicdesign.fp.TestUtilities.serializeDeserialize;
import static org.organicdesign.fp.collections.Equator.defaultComparator;
import static org.organicdesign.testUtils.EqualsContract.equalsDistinctHashCode;

public class UnmodSortedMapTest {
    static class TestMap<K, V> implements UnmodSortedMap<K,V>, Serializable {

        private static final long serialVersionUID = 20160901201600L;

//        static <K, V> SortedMap<K,V> dup(SortedMap<K,V> in) {
//            SortedMap<K,V> out = new TreeMap<>(in.comparator());
//            out.putAll(in);
//            return out;
//        }

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
            return UnmodSortedIterable.castFromSortedMap(inner).iterator();
        }
    }

//    static class TestEntry<K,V> implements Map.Entry<K,V> {
//        private final K key;
//        private final V value;
//        TestEntry(K k, V v) { key = k; value = v; }
//        @Override public K getKey() { return key; }
//
//        @Override public V getValue() { return value; }
//
//        @Override public V setValue (V v) {
//            throw new UnsupportedOperationException("no way");
//        }
//
//        @Override public String toString() { return "Entry(" + key + "," + value + ")"; }
//    }

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

    final static SortedMap<String,Integer> refMap = new TreeMap<>();
    static {
        refMap.put(avoKey.getKey(), avoKey.getValue());
        refMap.put(banKey.getKey(), banKey.getValue());
        refMap.put(clemKey.getKey(), clemKey.getValue());
    }

    final static TestMap<String,Integer> testMap = new TestMap<>(refMap, null);

    @SuppressWarnings("unchecked")
    @Test public void entrySetTest() {

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

        UnmodListTest.iteratorTest(refEntSet.iterator(), serializeDeserialize(testEntSet).iterator());

        // I can't fix TreeMap, so I guess this is good enough.
        // java.io.NotSerializableException: java.util.TreeMap$EntryIterator
//        UnmodListTest.iteratorTest(refEntSet.iterator(), serializeDeserialize(testEntSet.iterator()));

        Comparator<? super Map.Entry<String,Integer>> testComp0 = testEntSet.comparator();
        assert testComp0 != null;
        assertTrue("D".compareTo("d") < 0);
        assertTrue(testComp0.compare(kv("D", 3), kv("d", -1)) < 0);

        Comparator<? super Map.Entry<String,Integer>> testComp =
                new TestMap<>(refMap, String.CASE_INSENSITIVE_ORDER).entrySet().comparator();

        assertEquals(0, String.CASE_INSENSITIVE_ORDER.compare("D", "d"));
        assert testComp != null;
        assertEquals(0, testComp.compare(kv("D", 3), kv("d", -1)));

//        UnmodMapTest.TestEntry<String,Integer>[] testEntries =
//                (UnmodMapTest.TestEntry<String,Integer>[]) new UnmodMapTest.TestEntry[] {
//                        new UnmodMapTest.TestEntry<>("011first", Integer.MAX_VALUE),
//                        avoKey, banKey, clemKey,
//                        new UnmodMapTest.TestEntry<>("zzzLast", Integer.MIN_VALUE),
//                };

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

        equalsDistinctHashCode(testEntSet,
                               sortedSet(testEntSet.comparator(),
                                         vec(avoKey, banKey, clemKey)),
                               sortedSet(testEntSet.comparator(),
                                         vec(avoKey, banKey, clemKey)),
                               sortedSet(testEntSet.comparator(),
                                         vec(avoKey, banKey, pastLast)));

        // Test size less than
        equalsDistinctHashCode(testEntSet,
                               sortedSet(testEntSet.comparator(),
                                         vec(avoKey, banKey, clemKey)),
                               sortedSet(testEntSet.comparator(),
                                         vec(avoKey, banKey, clemKey)),
                               sortedSet(testEntSet.comparator(),
                                         vec(avoKey, banKey)));

        // Test size greater than
        equalsDistinctHashCode(testEntSet,
                               sortedSet(testEntSet.comparator(),
                                         vec(avoKey, banKey, clemKey)),
                               sortedSet(testEntSet.comparator(),
                                         vec(avoKey, banKey, clemKey)),
                               sortedSet(testEntSet.comparator(),
                                         vec(avoKey, banKey, clemKey, pastLast)));

        // This blows up just creating the sorted set with a null entry.
//        // Test with a null
//        equalsDistinctHashCode(testEntSet,
//                               sortedSet(testEntSet.comparator(),
//                                         vec(avoKey, banKey, clemKey)),
//                               sortedSet(testEntSet.comparator(),
//                                         vec(avoKey, banKey, clemKey)),
//                               sortedSet(testEntSet.comparator(),
//                                         vec(avoKey, null, clemKey)));

        // This blows up just creating the sorted set with a bogus object.
//        // Test with a the wrong type of object.
//        equalsDistinctHashCode(testEntSet,
//                               sortedSet(testEntSet.comparator(),
//                                         vec(avoKey, banKey, clemKey)),
//                               sortedSet(testEntSet.comparator(),
//                                         vec(avoKey, banKey, clemKey)),
//                               sortedSet(testEntSet.comparator(),
//                                         vec(avoKey, (Map.Entry<String,Integer>) new Object(),
//                                             clemKey)));

        // Test with an unsorted set
        equalsDistinctHashCode(testEntSet,
                               sortedSet(testEntSet.comparator(),
                                         vec(avoKey, banKey, clemKey)),
                               sortedSet(testEntSet.comparator(),
                                         vec(avoKey, banKey, clemKey)),
                               set(vec(avoKey, banKey, clemKey)));
    }

    @Test public void headMapTest() {
        UnmodSortedMap<String,Integer> emptyHeadMap = testMap.headMap(avoKey.getKey());
        // The headMap is the empty set.
        assertEquals(0, emptyHeadMap.size());
        assertFalse(emptyHeadMap.containsKey(avoKey.getKey()));
        assertFalse(emptyHeadMap.containsKey(banKey.getKey()));
        assertFalse(emptyHeadMap.containsKey(clemKey.getKey()));

        UnmodSortedMap<String,Integer> clemHeadMap = testMap.headMap(banKey.getKey());
        assertEquals(1, clemHeadMap.size());
        assertTrue(clemHeadMap.containsKey(avoKey.getKey()));
        assertFalse(clemHeadMap.containsKey(banKey.getKey()));
        assertFalse(clemHeadMap.containsKey(clemKey.getKey()));

        UnmodSortedMap<String,Integer> banHeadMap = testMap.headMap(clemKey.getKey());
        assertEquals(2, banHeadMap.size());
        assertTrue(banHeadMap.containsKey(avoKey.getKey()));
        assertTrue(banHeadMap.containsKey(banKey.getKey()));
        assertFalse(banHeadMap.containsKey(clemKey.getKey()));

        UnmodSortedMap<String,Integer> fullHeadMap = testMap.headMap(pastLast.getKey());

        // The subset is the whole set.
        assertEquals(3, fullHeadMap.size());
        assertTrue(fullHeadMap.containsKey(avoKey.getKey()));
        assertTrue(fullHeadMap.containsKey(banKey.getKey()));
        assertTrue(fullHeadMap.containsKey(clemKey.getKey()));
    }

    @Test public void keySetTest() {
        Set<String> refKeySet = refMap.keySet();
        UnmodSortedSet<String> testKeySet = testMap.keySet();

        assertEquals(refKeySet.size(), testKeySet.size());

        // For treeMap, a null key throws an exception.  Dunno if that's worthwhile or not.
//        assertFalse(refKeySet.contains(null));
//        assertFalse(testKeySet.contains(null));
        assertFalse(refKeySet.contains(junkKey.getKey()));
        assertFalse(testKeySet.contains(junkKey.getKey()));
        assertTrue(refKeySet.contains(avoKey.getKey()));
        assertTrue(testKeySet.contains(avoKey.getKey()));
        assertTrue(refKeySet.contains(banKey.getKey()));
        assertTrue(testKeySet.contains(banKey.getKey()));
        assertTrue(refKeySet.contains(clemKey.getKey()));
        assertTrue(testKeySet.contains(clemKey.getKey()));

        UnmodListTest.iteratorTest(refKeySet.iterator(), testKeySet.iterator());
        UnmodListTest.iteratorTest(refKeySet.iterator(),
                                   serializeDeserialize(testKeySet).iterator());

        Comparator<? super String> testComp0 = testKeySet.comparator();
        assert testComp0 != null;
        assertTrue("D".compareTo("d") < 0);
        assertTrue(testComp0.compare("D", "d") < 0);

        Comparator<? super String> testComp =
                new TestMap<>(refMap, String.CASE_INSENSITIVE_ORDER).keySet().comparator();

        assertEquals(0, String.CASE_INSENSITIVE_ORDER.compare("D", "d"));
        assert testComp != null;
        assertEquals(0, testComp.compare("D", "d"));

        UnmodSortedSet<String> empty = testKeySet.subSet(avoKey.getKey(), avoKey.getKey());
        // The subset is the empty set.
        assertEquals(0, empty.size());
        assertFalse(empty.contains(avoKey.getKey()));
        assertFalse(empty.contains(banKey.getKey()));
        assertFalse(empty.contains(clemKey.getKey()));

        UnmodSortedSet<String> avoSet = testKeySet.subSet(avoKey.getKey(), banKey.getKey());
        assertEquals(1, avoSet.size());
        assertTrue(avoSet.contains(avoKey.getKey()));
        assertFalse(avoSet.contains(banKey.getKey()));
        assertFalse(avoSet.contains(clemKey.getKey()));

        UnmodSortedSet<String> banSet = testKeySet.subSet(banKey.getKey(), clemKey.getKey());
        assertEquals(1, banSet.size());
        assertFalse(banSet.contains(avoKey.getKey()));
        assertTrue(banSet.contains(banKey.getKey()));
        assertFalse(banSet.contains(clemKey.getKey()));

        UnmodSortedSet<String> clemSet = testKeySet.subSet(clemKey.getKey(), pastLast.getKey());
        assertEquals(1, clemSet.size());
        assertFalse(clemSet.contains(avoKey.getKey()));
        assertFalse(clemSet.contains(banKey.getKey()));
        assertTrue(clemSet.contains(clemKey.getKey()));

        UnmodSortedSet<String> avoBanSet = testKeySet.subSet(avoKey.getKey(), clemKey.getKey());
        assertEquals(2, avoBanSet.size());
        assertTrue(avoBanSet.contains(avoKey.getKey()));
        assertTrue(avoBanSet.contains(banKey.getKey()));
        assertFalse(avoBanSet.contains(clemKey.getKey()));

        UnmodSortedSet<String> banClemSet = testKeySet.subSet(banKey.getKey(), pastLast.getKey());
        assertEquals(2, banClemSet.size());
        assertFalse(banClemSet.contains(avoKey.getKey()));
        assertTrue(banClemSet.contains(banKey.getKey()));
        assertTrue(banClemSet.contains(clemKey.getKey()));

        UnmodSortedSet<String> fullSet = testKeySet.subSet(avoKey.getKey(), pastLast.getKey());

        // The subset is the whole set.
        assertEquals(3, fullSet.size());
        assertTrue(fullSet.containsAll(testKeySet));
        assertTrue(testKeySet.containsAll(fullSet));

        // tailSet tests

        UnmodSortedSet<String> emptyTail = testKeySet.tailSet(pastLast.getKey());
        // The tailSet is the empty set.
        assertEquals(0, emptyTail.size());
        assertFalse(emptyTail.contains(avoKey.getKey()));
        assertFalse(emptyTail.contains(banKey.getKey()));
        assertFalse(emptyTail.contains(clemKey.getKey()));

        UnmodSortedSet<String> clemTail = testKeySet.tailSet(clemKey.getKey());
        assertEquals(1, clemTail.size());
        assertFalse(clemTail.contains(avoKey.getKey()));
        assertFalse(clemTail.contains(banKey.getKey()));
        assertTrue(clemTail.contains(clemKey.getKey()));

        UnmodSortedSet<String> banTail = testKeySet.tailSet(banKey.getKey());
        assertEquals(2, banTail.size());
        assertFalse(banTail.contains(avoKey.getKey()));
        assertTrue(banTail.contains(banKey.getKey()));
        assertTrue(banTail.contains(clemKey.getKey()));

        UnmodSortedSet<String> fullTail = testKeySet.tailSet(avoKey.getKey());

        // The subset is the whole set.
        assertEquals(3, fullTail.size());
        assertTrue(fullTail.containsAll(testKeySet));
        assertTrue(testKeySet.containsAll(fullTail));

        // headSet tests

        UnmodSortedSet<String> emptyHead = testKeySet.headSet(avoKey.getKey());
        // The headSet is the empty set.
        assertEquals(0, emptyHead.size());
        assertFalse(emptyHead.contains(avoKey.getKey()));
        assertFalse(emptyHead.contains(banKey.getKey()));
        assertFalse(emptyHead.contains(clemKey.getKey()));

        UnmodSortedSet<String> clemHead = testKeySet.headSet(banKey.getKey());
        assertEquals(1, clemHead.size());
        assertTrue(clemHead.contains(avoKey.getKey()));
        assertFalse(clemHead.contains(banKey.getKey()));
        assertFalse(clemHead.contains(clemKey.getKey()));

        UnmodSortedSet<String> banHead = testKeySet.headSet(clemKey.getKey());
        assertEquals(2, banHead.size());
        assertTrue(banHead.contains(avoKey.getKey()));
        assertTrue(banHead.contains(banKey.getKey()));
        assertFalse(banHead.contains(clemKey.getKey()));

        UnmodSortedSet<String> fullHead = testKeySet.headSet(pastLast.getKey());

        // The subset is the whole set.
        assertEquals(3, fullHead.size());
        assertTrue(fullHead.containsAll(testKeySet));
        assertTrue(testKeySet.containsAll(fullHead));

        equalsDistinctHashCode(testKeySet,
                               sortedSet(testKeySet.comparator(),
                                         vec(avoKey.getKey(), banKey.getKey(), clemKey.getKey())),
                               sortedSet(testKeySet.comparator(),
                                         vec(avoKey.getKey(), banKey.getKey(), clemKey.getKey())),
                               sortedSet(testKeySet.comparator(),
                                         vec(avoKey.getKey(), banKey.getKey(), pastLast.getKey())));

        // Test smaller
        equalsDistinctHashCode(testKeySet,
                               sortedSet(testKeySet.comparator(),
                                         vec(avoKey.getKey(), banKey.getKey(), clemKey.getKey())),
                               sortedSet(testKeySet.comparator(),
                                         vec(avoKey.getKey(), banKey.getKey(), clemKey.getKey())),
                               sortedSet(testKeySet.comparator(),
                                         vec(avoKey.getKey(), banKey.getKey())));

        // Test larger
        equalsDistinctHashCode(testKeySet,
                               sortedSet(testKeySet.comparator(),
                                         vec(avoKey.getKey(), banKey.getKey(), clemKey.getKey())),
                               sortedSet(testKeySet.comparator(),
                                         vec(avoKey.getKey(), banKey.getKey(), clemKey.getKey())),
                               sortedSet(testKeySet.comparator(),
                                         vec(avoKey.getKey(), banKey.getKey(), clemKey.getKey(),
                                             pastLast.getKey())));

        equalsDistinctHashCode(testKeySet,
                               sortedSet(testKeySet.comparator(),
                                         vec(avoKey.getKey(), banKey.getKey(), clemKey.getKey())),
                               sortedSet(testKeySet.comparator(),
                                         vec(avoKey.getKey(), banKey.getKey(), clemKey.getKey())),
                               set(vec(avoKey.getKey(), banKey.getKey(), clemKey.getKey())));

    }

    @Test public void valuesTest() {
        Collection<Integer> refValColl = refMap.values();
        UnmodSortedCollection<Integer> testValColl = testMap.values();

        assertEquals(refValColl.size(), testValColl.size());

        // For treeMap, a null key throws an exception.  Dunno if that's worthwhile or not.
//        assertFalse(refValColl.contains(null));
//        assertFalse(testValColl.contains(null));
        assertFalse(refValColl.contains(junkKey.getValue()));
        assertFalse(testValColl.contains(junkKey.getValue()));
        assertTrue(refValColl.contains(avoKey.getValue()));
        assertTrue(testValColl.contains(avoKey.getValue()));
        assertTrue(refValColl.contains(banKey.getValue()));
        assertTrue(testValColl.contains(banKey.getValue()));
        assertTrue(refValColl.contains(clemKey.getValue()));
        assertTrue(testValColl.contains(clemKey.getValue()));

        UnmodListTest.iteratorTest(refValColl.iterator(), testValColl.iterator());

        UnmodListTest.iteratorTest(refValColl.iterator(),
                                   serializeDeserialize(testValColl).iterator());

        equalsDistinctHashCode(testValColl,
                               testMap.values(),
                               testMap.values(),
                               sortedSet(defaultComparator(),
                                         vec(avoKey.getValue(), banKey.getValue(), junkKey.getValue())));

//        equalsSameHashCode(testValColl,
//                               testMap.values(),
//                               testMap.values(),
//                               vec(avoKey.getValue(), clemKey.getValue(), banKey.getValue()));

    }
}