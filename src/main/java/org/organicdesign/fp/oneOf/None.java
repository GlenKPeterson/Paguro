package org.organicdesign.fp.oneOf;

import org.organicdesign.fp.function.Fn0;
import org.organicdesign.fp.function.Fn1;

/** Represents the absence of a value */
public final class None<T> implements Option<T> {
    // For serializable.  Make sure to change whenever internal data format changes.
    private static final long serialVersionUID = 20160915081300L;

    /** Private constructor for singleton. */
    None() {}

    /** {@inheritDoc} */
    @Override public T get() { throw new IllegalStateException("Called get on None"); }

    /** {@inheritDoc} */
    @Override public T getOrElse(T t) { return t; }

    /** {@inheritDoc} */
    @Override public boolean isSome() { return false; }
//
//        @Override public UnmodSortedIterator<T> iterator() {
//            return UnmodSortedIterator.empty();
//        }

    /** {@inheritDoc} */
    @Override public <U> U match(Fn1<T,U> has, Fn0<U> hasNot) {
        return hasNot.get();
    }

    /** {@inheritDoc} */
    @Override public <U> Option<U> then(Fn1<T,Option<U>> f) { return Option.none(); }

    /** Valid, but deprecated because it's usually an error to call this in client code. */
    @Deprecated // Has no effect.  Darn!
    @Override public int hashCode() { return 0; }

    /** Valid, but deprecated because it's usually an error to call this in client code. */
    @Deprecated // Has no effect.  Darn!
    @Override public boolean equals(Object other) {
        return (this == other) || (other instanceof org.organicdesign.fp.oneOf.None);
    }

    /** Defend our singleton property in the face of deserialization. */
    private Object readResolve() { return NONE; }
}
