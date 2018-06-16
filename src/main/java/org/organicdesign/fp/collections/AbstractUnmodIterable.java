package org.organicdesign.fp.collections;

/**
 Implements equals and hashCode() methods compatible with all java.util collections (this
 algorithm is not order-dependent) and toString which takes the name of the sub-class.
 */
public abstract class AbstractUnmodIterable<T> implements UnmodIterable<T> {
    @Override public int hashCode() { return UnmodIterable.Companion.hash(this); }
    @Override public String toString() {
        return UnmodIterable.Companion.toString(getClass().getSimpleName(), this);
    }
}
