package org.organicdesign.fp.collections;

import java.util.Map;
import java.util.Objects;

import org.organicdesign.fp.tuple.Tuple2;

import static org.organicdesign.fp.FunctionUtils.stringify;

/**
 Replaced with {@link org.organicdesign.fp.tuple.Tuple2} and the shortcut
 {@link org.organicdesign.fp.StaticImports#tup(Object, Object)}.
 */
@Deprecated
public class KeyVal<K,V> extends Tuple2<K,V> {

    /** Public static Key/Value factory method */
    public static <K,V> KeyVal<K,V> of(K a, V b) { return new KeyVal<>(a, b); }

    /** Public static Map.Entry factory method */
    public static <K,V> KeyVal<K,V> of(Map.Entry<K,V> entry) { return new KeyVal<>(entry); }

    /** Key/Value Constructor */
    public KeyVal(K k, V v) { super(k, v); }

    /** Map.Entry Constructor */
    public KeyVal(Map.Entry<K,V> entry) { super(entry.getKey(), entry.getValue()); }

    @Override public String toString() {
        return "kv(" + stringify(_1) + "," + stringify(_2) + ")";
    }
}
