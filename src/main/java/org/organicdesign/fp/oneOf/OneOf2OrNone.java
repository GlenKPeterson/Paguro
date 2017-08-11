package org.organicdesign.fp.oneOf;

import org.organicdesign.fp.collections.ImList;
import org.organicdesign.fp.function.Fn0;
import org.organicdesign.fp.function.Fn1;
import org.organicdesign.fp.type.RuntimeTypes;

import java.util.Objects;

import static org.organicdesign.fp.FunctionUtils.stringify;

public class OneOf2OrNone<A,B> {

    protected final Object item;
    protected final int sel;
    private final ImList<Class> types;

    protected OneOf2OrNone(ImList<Class> runtimeTypes, A a, B b, int s) {
        if (runtimeTypes.size() != 3) {
            throw new IllegalArgumentException("OneOf2OrNone requires exactly 3 types, the third being Option.None.class");
        }
        if (!Option.None.class.equals(runtimeTypes.get(2))) {
            throw new IllegalArgumentException("The third type for OneOf2OrNone must be Option.None.class");
        }
        types = RuntimeTypes.registerClasses(runtimeTypes);

        sel = s;
        if (sel == 1) {
            item = a;
        } else if (sel == 2) {
            item = b;
        } else if (sel == 3) {
            item = null;
        } else {
            throw new IllegalArgumentException("You must specify whether this holds a(n) " +
                                               RuntimeTypes.name(types.get(0)) + ", a(n) " +
                                               RuntimeTypes.name(types.get(1)) + ", or nothing");
        }
    }

    // We only store one item and it's type is erased, so we have to cast it at runtime.
    // If sel is managed correctly, it ensures that the cast is accurate.
    @SuppressWarnings("unchecked")
    public <R> R match(Fn1<A, R> fa,
                       Fn1<B, R> fb,
                       Fn0<R> fz) {
        if (sel == 1) {
            return fa.apply((A) item);
        } else if (sel == 2) {
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
        return RuntimeTypes.name(types.get(sel - 1)) + "(" + stringify(item) + ")";
    }
}
