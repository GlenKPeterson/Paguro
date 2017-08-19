package org.organicdesign.fp.oneOf;

import org.organicdesign.fp.collections.ImList;
import org.organicdesign.fp.function.Fn0;
import org.organicdesign.fp.function.Fn1;
import org.organicdesign.fp.type.RuntimeTypes;

import java.util.Objects;

import static org.organicdesign.fp.FunctionUtils.stringify;

/** Holds one of 2 values or {@link None}. */
public abstract class OneOf2OrNone<A,B> {

    private final Object item;
    private final int sel;
    private final ImList<Class> types;

    protected OneOf2OrNone(A a, Class<A> ca,
                           B b, Class<B> cb,
                           int s) {
        types = RuntimeTypes.registerClasses(ca, cb, None.class);
        sel = s;
        if (s == 0) {
            item = a;
            if (b != null) { throw new IllegalArgumentException("Only one item can be non-null"); }
        } else if (s == 1) {
            item = b;
            if (a != null) { throw new IllegalArgumentException("Only one item can be non-null"); }
        } else if (s == 2) {
            item = null;
            if (a != null) { throw new IllegalArgumentException("For None, all items must be null"); }
            if (b != null) { throw new IllegalArgumentException("For None, all items must be null"); }
        } else {
            throw new IllegalArgumentException("Selected item index must be 0-2 where 2 selects None");
        }
    }

    // We only store one item and it's type is erased, so we have to cast it at runtime.
    // If sel is managed correctly, it ensures that the cast is accurate.
    @SuppressWarnings("unchecked")
    public <R> R match(Fn1<A, R> fa,
                       Fn1<B, R> fb,
                       Fn0<R> fz) {
        if (sel == 0) {
            return fa.apply((A) item);
        } else if (sel == 1) {
            return fb.apply((B) item);
        } else {
            return fz.apply();
        }
    }

    public int hashCode() {
        // Simplest way to make the two items different.
        return Objects.hashCode(item) + sel;
    }

    @SuppressWarnings("unchecked")
    @Override public boolean equals(Object other) {
        if (this == other) { return true; }
        if (!(other instanceof OneOf2OrNone)) { return false; }

        OneOf2OrNone that = (OneOf2OrNone) other;
        return (sel == that.sel) &&
               Objects.equals(item, that.item);
    }

    @Override public String toString() {
        String tn = RuntimeTypes.name(types.get(sel));
        return sel == 2 ? tn
                        : tn + "/2n(" + stringify(item) + ")";
    }
}
