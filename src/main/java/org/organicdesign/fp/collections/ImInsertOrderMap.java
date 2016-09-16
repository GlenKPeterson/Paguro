package org.organicdesign.fp.collections;

import org.organicdesign.fp.Option;
import org.organicdesign.fp.tuple.Tuple2;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Map;

public class ImInsertOrderMap<K,V> implements ImMap<K,V>, Serializable {

    // For serializable.  Make sure to change whenever internal data format changes.
    private static final long serialVersionUID = 20160914085100L;

    private static final class Pair<V> implements Serializable {
        // For serializable.  Make sure to change whenever internal data format changes.
        private static final long serialVersionUID = 20160914085100L;

        int idx;
        V val;
        Pair(V v, int i) { val = v; idx = i;}
    }

    private final ImMap<K,Pair<V>> inner;
    private final int index;
    private ImInsertOrderMap(ImMap<K,Pair<V>> kvs, int idx) {
        inner = kvs; index = idx;
    }

    private static final ImInsertOrderMap EMPTY =
            new ImInsertOrderMap<>(PersistentHashMap.empty(), 0);

    @SuppressWarnings("unchecked")
    public static <K,V> ImInsertOrderMap<K,V> empty() { return EMPTY; }

    @Override public Option<UnEntry<K, V>> entry(K key) {
        Option<UnEntry<K,Pair<V>>> innerEntry = inner.entry(key);
        return innerEntry.then(entry -> Option.of(Tuple2.of(key, entry.getValue().val)));
    }

    @Override public ImInsertOrderSet<Map.Entry<K,V>> entrySet() {
        return inner.foldLeft(ImInsertOrderSet.empty(),
                              (set, entry) -> set.put(Tuple2.of(entry.getKey(),
                                                                entry.getValue().val)));
    }

    @Override public ImInsertOrderMap<K, V> assoc(K key, V val) {
        int nextIdx = index + 1;
        return new ImInsertOrderMap<>(inner.assoc(key, new Pair<>(val, nextIdx)),
                                      nextIdx);
    }

    @Override public ImInsertOrderMap<K, V> without(K key) {
        return new ImInsertOrderMap<>(inner.without(key), index);
    }

    @Override public int size() { return inner.size(); }

    @Override public ImInsertOrderSet<K> keySet() {
        return ImInsertOrderSet.ofMap(this);
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

    private static final class InsOrdComp<K,V> implements Comparator<UnEntry<K,Pair<V>>>,
                                                          Serializable {
        // For serializable.  Make sure to change whenever internal data format changes.
        private static final long serialVersionUID = 20160914085100L;

        private InsOrdComp() {}

        @Override
        public int compare(UnEntry<K,Pair<V>> a,
                           UnEntry<K,Pair<V>> b) {
            int aInt = a.getValue().idx;
            int bInt = b.getValue().idx;
            return aInt > bInt ? 1 :
                   aInt < bInt ? -1 : 0;
        }

        private static final InsOrdComp INSTANCE = new InsOrdComp();
    }

    @SuppressWarnings("unchecked")
    private static <K,V> Comparator<UnEntry<K,Pair<V>>> defaultComparator() {
        return InsOrdComp.INSTANCE;
    }

    @Override public UnmodSortedIterator<UnEntry<K,V>> iterator() {
        return inner.toImSortedSet(defaultComparator())
                    .map(e -> (UnEntry<K,V>) Tuple2.of(e.getKey(), e.getValue().val))
                    .toImList().iterator();
    }
}
