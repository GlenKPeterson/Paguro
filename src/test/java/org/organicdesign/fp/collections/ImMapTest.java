package org.organicdesign.fp.collections;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.organicdesign.fp.oneOf.Option;
import org.organicdesign.fp.tuple.Tuple2;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.organicdesign.fp.FunctionUtils.ordinal;

public class ImMapTest {
    private static class TestMap<K,V> implements ImMap<K,V> {
        private final Map<K,V> inner;

        private TestMap(Map<K,V> m) { inner = m; }

        @NotNull
        @Override public Option<UnEntry<K, V>> entry(K key) {
            return inner.containsKey(key) ? Option.some(Tuple2.of(key, inner.get(key)))
                                          : Option.none();
        }

        @Override
        public @NotNull ImMap<K, V> assoc(K key, V val) {
            Map<K,V> m = new HashMap<>(inner);
            m.put(key, val);
            return new TestMap<>(m);
        }

        @Override public Equator<K> equator() {
            throw new UnsupportedOperationException("not implemented");
        }

        @NotNull
        @Override public MutMap<K,V> mutable() {
            throw new UnsupportedOperationException("not implemented");
        }

        @Override
        public @NotNull ImMap<K, V> without(K key) {
            Map<K,V> m = new HashMap<>(inner);
            m.remove(key);
            return new TestMap<>(m);
        }

        @Override public int size() { return inner.size(); }

        @Override
        public @NotNull ImSet<K> keySet() {
            return new ImSetTest.ImTestSet<>(inner.keySet());
        }

        @NotNull
        @Override public UnmodIterator<UnEntry<K, V>> iterator() {
            return UnmodMap.UnEntry.entryIterToUnEntryUnIter(inner.entrySet().iterator());
        }
    }

    @Test
    public void testAssoc() {
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