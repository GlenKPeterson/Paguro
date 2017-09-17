package org.organicdesign.fp.collections;

import org.junit.Test;
import org.organicdesign.fp.oneOf.Option;
import org.organicdesign.fp.tuple.Tuple2;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.organicdesign.fp.FunctionUtils.ordinal;

/**
 Created by gpeterso on 9/13/16.
 */
public class ImMapTest {
    private static class TestMap<K,V> implements ImMap<K,V> {
        private static <K,V> Map<K,V> copyMap(Map<K,V> m) {
            Map<K,V> ret = new HashMap<>();
            ret.putAll(m);
            return ret;
        }

        private final Map<K,V> inner;

        private TestMap(Map<K,V> m) { inner = m; }

        @Override public Option<UnEntry<K, V>> entry(K key) {
            return inner.containsKey(key) ? Option.some(Tuple2.of(key, inner.get(key)))
                                          : Option.none();
        }

        @Override public ImMap<K, V> assoc(K key, V val) {
            Map<K,V> m = copyMap(inner);
            m.put(key, val);
            return new TestMap<>(m);
        }

        @Override public Equator<K> equator() {
            throw new UnsupportedOperationException("not implemented");
        }

        @Override public MutMap<K,V> mutable() {
            throw new UnsupportedOperationException("not implemented");
        }

        @Override public ImMap<K, V> without(K key) {
            Map<K,V> m = copyMap(inner);
            m.remove(key);
            return new TestMap<>(m);
        }

        @Override public int size() { return inner.size(); }

        @Override public ImSet<K> keySet() {
            return new ImSetTest.TestSet<>(inner.keySet());
        }

        @Override public UnmodIterator<UnEntry<K, V>> iterator() {
            return UnmodMap.UnEntry.entryIterToUnEntryUnIter(inner.entrySet().iterator());
        }
    }

    @Test public void testAssoc() {
        Map<String,Integer> control = new HashMap<>();
        ImMap<String,Integer> test = new TestMap<>(new HashMap<>());
        for (int i = 0; i < 20; i++) {
            Tuple2<String,Integer> entry = Tuple2.of(ordinal(i), i);
            control.put(entry.getKey(), entry.getValue());
            test = test.assoc(entry);
            assertEquals(control.size(), test.size());
            assertEquals(control.get(entry.getKey()), test.get(entry.getKey()));
        }
    }
}