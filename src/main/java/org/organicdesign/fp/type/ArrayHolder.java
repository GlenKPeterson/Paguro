package org.organicdesign.fp.type;

import java.util.Arrays;

class ArrayHolder<T> {
    private final T[] array;
    @SafeVarargs
    ArrayHolder(T... ts) { array = ts; }
    @Override public int hashCode() { return Arrays.hashCode(array); }
    @Override public boolean equals(Object other) {
        if (array == other) { return true; }
        if (! (other instanceof ArrayHolder) ) {
            return false;
        }
        //noinspection unchecked
        return Arrays.equals(array, ((ArrayHolder) other).array);
    }
}
