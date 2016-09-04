package org.organicdesign.fp.collections;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

import org.organicdesign.fp.tuple.Tuple2;

import static org.organicdesign.fp.FunctionUtils.stringify;

// TODO: Rename to KeyVal
/**
 Represents an immutable and serializable Key and Value pair.  This uses the Serialization Proxy
 pattern (Josh Bloch) so if you want to subclass, serialization may be difficult.
 */
public class KeyVal<K,V> extends Tuple2<K,V> implements Map.Entry<K,V>,
        UnmodMap.UnEntry<K,V>, Serializable {
    // For serializable.  Make sure to change whenever internal data format changes.
    private static final long serialVersionUID = 20160827174100L;

    /** Key/Value Constructor */
    public KeyVal(K k, V v) { super(k, v); }

    /** Map.Entry Constructor */
    public KeyVal(Map.Entry<K,V> entry) { super(entry.getKey(), entry.getValue()); }

    /** Public static Key/Value factory method */
    public static <K,V> KeyVal<K,V> of(K a, V b) { return new KeyVal<>(a, b); }

    /** Public static Map.Entry factory method */
    public static <K,V> KeyVal<K,V> of(Map.Entry<K,V> entry) { return new KeyVal<>(entry); }

    @Override public String toString() {
        return "kv(" + stringify(_1) + "," + stringify(_2) + ")";
    }

    @Override
    public boolean equals(Object other) {
        // Cheapest operation first...
        if (this == other) { return true; }
        if (!(other instanceof Map.Entry)) { return false; }
        // Details...
        final Map.Entry that = (Map.Entry) other;
        return Objects.equals(_1, that.getKey()) && Objects.equals(_2, that.getValue());
    }

    @Override
    public int hashCode() {
        // This is specified in java.util.Map as part of the map contract.
        return  (_1 == null ? 0 : _1.hashCode()) ^
                (_2 == null ? 0 : _2.hashCode());
    }

    // Inherited from Map.Entry
    /** Returns the first field of the tuple.  To implement Map.Entry. */
    @Override public K getKey() { return _1; }
    /** Returns the second field of the tuple.  To implement Map.Entry. */
    @Override public V getValue() { return _2; }

    /** This method is required to implement Map.Entry, but calling it only issues an exception */
    @SuppressWarnings("deprecation")
    @Override @Deprecated public V setValue(V value) {
        throw new UnsupportedOperationException("KeyVal is immutable");
    }

    // ======================================= Serialization =======================================
    // Check out Josh Bloch Item 78, p. 312 for an explanation of what's going on here.
    private static class SerializationProxy<K,V> implements Serializable {
        private final K k;
        private final V v;
        SerializationProxy(KeyVal<K,V> kv) { k = kv._1;v = kv._2; }

        private static final long serialVersionUID = 20160827174100L;

        private Object readResolve() { return new KeyVal<>(k, v); }
    }

    private Object writeReplace() { return new SerializationProxy<>(this); }

    private void readObject(java.io.ObjectInputStream in) throws IOException,
            ClassNotFoundException {
        throw new InvalidObjectException("Proxy required");
    }
}
