package org.organicdesign.fp.collections;

import org.jetbrains.annotations.NotNull;

/**
 Implements equals and hashCode() methods compatible with all java.util collections (this
 algorithm is not order-dependent) and toString which takes the name of the sub-class.
 */
public abstract class AbstractUnmodIterable<T> implements UnmodIterable<T> {
    @Override public int hashCode() { return UnmodIterable.hash(this); }
    @Override public @NotNull String toString() {
        return UnmodIterable.toString(getClass().getSimpleName(), this);
    }
}
