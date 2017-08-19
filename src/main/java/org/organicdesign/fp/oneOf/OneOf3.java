package org.organicdesign.fp.oneOf;

import org.organicdesign.fp.collections.ImList;
import org.organicdesign.fp.function.Fn1;
import org.organicdesign.fp.type.RuntimeTypes;

import java.util.Objects;

import static org.organicdesign.fp.FunctionUtils.stringify;

/** Holds one of 3 values.  If you're passing the same type, you probably want a tuple instead. */
public class OneOf3<A,B,C> {
    private final Object item;
    private final int sel;
    private final ImList<Class> types;

    protected OneOf3(A a, Class<A> ca,
                     B b, Class<B> cb,
                     C c, Class<C> cc,
                     int s) {
        types = RuntimeTypes.registerClasses(ca, cb, cc);
        sel = s;
        if (s == 0) {
            item = a;
            if (b != null) { throw new IllegalArgumentException("Only one item can be non-null"); }
            if (c != null) { throw new IllegalArgumentException("Only one item can be non-null"); }
        } else if (s == 1) {
            item = b;
            if (a != null) { throw new IllegalArgumentException("Only one item can be non-null"); }
            if (c != null) { throw new IllegalArgumentException("Only one item can be non-null"); }
        } else if (s == 2) {
            item = c;
            if (a != null) { throw new IllegalArgumentException("Only one item can be non-null"); }
            if (b != null) { throw new IllegalArgumentException("Only one item can be non-null"); }
        } else {
            throw new IllegalArgumentException("Selected item index must be 0-2");
        }
    }

    // We only store one item and it's type is erased, so we have to cast it at runtime.
    // If sel is managed correctly, it ensures that the cast is accurate.
    @SuppressWarnings("unchecked")
    public <R> R match(Fn1<A, R> fa,
                       Fn1<B, R> fb,
                       Fn1<C, R> fc) {
        if (sel == 0) {
            return fa.apply((A) item);
        } else if (sel == 1) {
            return fb.apply((B) item);
        } else {
            return fc.apply((C) item);
        }
    }

    public int hashCode() {
        // Simplest way to make the two items different.
        return Objects.hashCode(item) + sel;
    }

    @SuppressWarnings("unchecked")
    @Override public boolean equals(Object other) {
        if (this == other) { return true; }
        if (!(other instanceof OneOf3)) { return false; }

        OneOf3 that = (OneOf3) other;
        return (sel == that.sel) &&
               Objects.equals(item, that.item);
    }

    @Override public String toString() {
        return RuntimeTypes.name(types.get(sel)) + "/3(" + stringify(item) + ")";
    }
}
