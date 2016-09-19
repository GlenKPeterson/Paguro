package org.organicdesign.fp.collections;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.organicdesign.fp.Option;
import org.organicdesign.fp.tuple.Tuple2;

public class ImInsertOrderMap2<K,V> implements ImMap<K,V>, Serializable {

    // For serializable.  Make sure to change whenever internal data format changes.
    private static final long serialVersionUID = 20160914085100L;

    private static final ImInsertOrderMap2 EMPTY =
            new ImInsertOrderMap2<>(PersistentHashMap.empty(), 0);

    @SuppressWarnings("unchecked")
    public static <K,V> ImInsertOrderMap2<K,V> empty() { return EMPTY; }

    private static final class Pair<V> implements Serializable {
        // For serializable.  Make sure to change whenever internal data format changes.
        private static final long serialVersionUID = 20160914085100L;

        int idx;
        V val;
        Pair(V v, int i) { val = v; idx = i;}
    }

    private final ImMap<K,Pair<V>> inner;
    private final int index;
    private ImInsertOrderMap2(ImMap<K,Pair<V>> kvs, int idx) {
        inner = kvs; index = idx;
    }

    @Override public ImInsertOrderMap2<K, V> assoc(K key, V val) {
        int nextIdx = index + 1;
        return new ImInsertOrderMap2<>(inner.assoc(key, new Pair<>(val, nextIdx)),
                                       nextIdx);
    }

    @Override public Option<UnEntry<K, V>> entry(K key) {
        Option<UnEntry<K,Pair<V>>> innerEntry = inner.entry(key);
        return innerEntry.then(entry -> Option.of(Tuple2.of(key, entry.getValue().val)));
    }

    @Override public ImInsertOrderSet2<Entry<K,V>> entrySet() {
        return inner.foldLeft(ImInsertOrderSet2.empty(),
                              (set, entry) -> set.put(Tuple2.of(entry.getKey(),
                                                                entry.getValue().val)));
    }

    @Override public UnmodSortedIterator<UnEntry<K,V>> iterator() {
        return new Iter(inner.iterator());
    }

    @Override public int size() { return inner.size(); }

    @Override public ImInsertOrderSet2<K> keySet() {
        return ImInsertOrderSet2.ofMap(this);
    }

    @Override public ImInsertOrderMap2<K, V> without(K key) {
        return new ImInsertOrderMap2<>(inner.without(key), index);
    }

    @Override public boolean equals(Object o) {
        if (this == o) { return true; }
        if ( !(o instanceof Map) ) return false;
        Map that = (Map) o;
        if (this.size() != that.size()) {
            return false;
        }
        for (UnEntry<K,Pair<V>> entry : inner) {
            K key = entry.getKey();
            if (!that.containsKey(key)) { return false; }
            if (!that.get(key).equals(entry.getValue().val)) { return false; }
        }
        return true;
    }

    @Override public int hashCode() {
        return inner.foldLeft(0,
                              (count, e) -> count + Tuple2.of(e.getKey(), e.getValue().val)
                                                          .hashCode());

    }

    private final class Iter implements UnmodSortedIterator<UnEntry<K,V>> {
        private final TreeMap<Integer,UnEntry<K,Pair<V>>> sortedItems = new TreeMap<>();
        private final UnmodIterator<UnEntry<K,Pair<V>>> inner;
        private int idx = 1;
        Iter(UnmodIterator<UnEntry<K,Pair<V>>> is) { inner = is; }

        @Override public boolean hasNext() {
            return inner.hasNext() || sortedItems.size() > 0;
        }

        @Override public UnEntry<K, V> next() {
            UnEntry<K,Pair<V>> entry = sortedItems.get(idx);
            if (entry == null) {
                if (!inner.hasNext()) {
                    idx++;
                    return next();
                }
                entry = inner.next();
                while (idx != entry.getValue().idx) {
                    sortedItems.put(entry.getValue().idx, entry);
                    if (!inner.hasNext()) {
                        idx++;
                        return next();
                    }
                    entry = inner.next();
                }
            } else {
                sortedItems.remove(idx);
            }
            idx++;
            return Tuple2.of(entry.getKey(), entry.getValue().val);
        }
    }
}
