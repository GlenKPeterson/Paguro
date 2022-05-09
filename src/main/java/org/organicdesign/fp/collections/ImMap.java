package org.organicdesign.fp.collections;

import java.util.Map;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** An immutable map with no guarantees about its ordering. */
public interface ImMap<K,V> extends BaseUnsortedMap<K,V> {

    /** Returns a new map with the given key/value added */
    @NotNull
    @Override ImMap<K,V> assoc(K key, V val);

    /** Returns a new map with an immutable copy of the given entry added */
    @NotNull
    @Override default ImMap<K,V> assoc(@NotNull Map.Entry<K,V> entry) {
        return assoc(entry.getKey(), entry.getValue());
    }

    /** Returns a new map with the given key/value removed */
    @NotNull
    @Override ImMap<K,V> without(K key);

    /**
     Returns a view of the mappings contained in this map.  The set should actually contain
     UnmodMap.Entry items, but that return signature is illegal in Java, so you'll just have to
     remember.
     */
    @NotNull
    @Override default ImSet<Map.Entry<K,V>> entrySet() {
        return map(e -> (Map.Entry<K,V>) e)
                .toImSet();
    }

    /** Returns an immutable view of the keys contained in this map. */
    @Override default @NotNull ImSet<K> keySet() {
        return mutable().keySet().immutable();
    }

    /** Returns a mutable version of this mutable map. */
    @NotNull MutMap<K,V> mutable();

    /**
     * Efficiently adds {@link UnmodMap.UnEntry}&lt;K,V> to this ImMap.  Ordering is ignored.
     * @param items the values to add
     * @return a new ImMap with the additional items.
     */
    @Override
    @Contract(pure = true)
    default @NotNull ImMap<K,V> concat(@Nullable Iterable<? extends UnmodMap.UnEntry<K,V>> items) {
        return (items == null) ? this
                               : mutable().concat(items).immutable();
    }

    /**
     * Efficiently adds {@link UnmodMap.UnEntry}&lt;K,V> to this ImMap.  Ordering is ignored.
     * @param items the values to add
     * @return a new ImMap with the additional items.
     */
    @Override
    @Contract(pure = true)
    default @NotNull ImMap<K,V> precat(@Nullable Iterable<? extends UnmodMap.UnEntry<K,V>> items) {
        return concat(items);
    }
}
