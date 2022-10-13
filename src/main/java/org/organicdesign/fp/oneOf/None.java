package org.organicdesign.fp.oneOf;

import org.jetbrains.annotations.NotNull;
import org.organicdesign.fp.function.Fn0;
import org.organicdesign.fp.function.Fn1;

/** Represents the absence of a value */
public final class None<T> implements Option<T> {
    // For serializable.  Make sure to change whenever internal data format changes.
    private static final long serialVersionUID = 20170810211300L;

    // ========================================== Static ==========================================
    /**
     * None is a singleton and this is its only instance.
     * In general, you want to use {@link Option#none()} instead of
     * accessing this directly so that the generic types work out.
     */
    @SuppressWarnings("rawtypes")
    static final Option NONE = new None();

    /** Private constructor for singleton. */
    private None() {}

    /** {@inheritDoc} */
    @Override public @NotNull T get() { throw new IllegalStateException("Called get on None"); }

    /** {@inheritDoc} */
    @Override public T getOrElse(T t) { return t; }

    /** {@inheritDoc} */
    @Override public boolean isSome() { return false; }

    /** {@inheritDoc} */
    @Override public <U> U match(
            @NotNull Fn1<T,U> has,
            @NotNull Fn0<U> hasNot
    ) {
        return hasNot.get();
    }

    /** {@inheritDoc} */
    @Override public <U> Option<U> then(Fn1<T,Option<U>> f) { return Option.none(); }

    /** This final singleton class always returns zero (it represents None after all). */
    @Override public int hashCode() { return 0; }

    /** Asks if the other object is instanceof the final singleton class None */
    @Override public boolean equals(Object other) {
        return other instanceof org.organicdesign.fp.oneOf.None;
    }

    /** Defend our singleton property in the face of deserialization. */
    private @NotNull Object readResolve() { return NONE; }

    @Override
    public @NotNull String toString() { return "None"; }
}
