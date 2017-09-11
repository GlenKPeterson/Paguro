package org.organicdesign.fp.collections;

import java.util.AbstractMap;
import java.util.Map;

/**
 Implements equals() and hashCode() methods compatible with java.util.Map (which ignores order)
 to make defining unmod Maps easier.  Inherits hashCode() and toString() from
 AbstractUnmodIterable.
 */
public abstract class AbstractUnmodMap<K,V>
        extends AbstractUnmodIterable<UnmodMap.UnEntry<K,V>>
        implements UnmodMap<K,V> {

    /** Compatible with {@link AbstractMap#equals(Object)} */
    @Override public boolean equals(Object other) {
        if (this == other) { return true; }
        if (!(other instanceof Map)) { return false; }

        Map<?, ?> that = (Map<?, ?>) other;
        if (that.size() != size()) { return false; }

        try {
            for (Entry<K, V> e : this) {
                K key = e.getKey();
                V value = e.getValue();
                if (value == null) {
                    if (!(that.get(key) == null && that.containsKey(key))) {
                        return false;
                    }
                } else {
                    if (!value.equals(that.get(key))) {
                        return false;
                    }
                }
            }
        } catch (ClassCastException | NullPointerException unused) {
            return false;
        }
        return true;
    }
}
