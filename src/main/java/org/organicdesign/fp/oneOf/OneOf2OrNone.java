package org.organicdesign.fp.oneOf;

import org.organicdesign.fp.function.Fn0;
import org.organicdesign.fp.function.Fn1;

import java.util.Objects;

import static org.organicdesign.fp.FunctionUtils.stringify;

/** Holds one of 2 values or {@link None}. */
public abstract class OneOf2OrNone<A,B> {

    protected final Object item;
    protected final int sel;

    protected OneOf2OrNone(A a, B b, int s) {
        sel = s;
        if (sel == 1) {
            item = a;
        } else if (sel == 2) {
            item = b;
        } else if (sel == 3) {
            item = null;
        } else {
            throw new IllegalArgumentException("You must specify whether this holds 1. a(n) " +
                                               typeName(1) + ", 2. a(n) " +
                                               typeName(2) + ", or 3. " +
                                               typeName(3));
        }
    }

    /**
     This should be implemented as
     <pre><code>
private transient static final String[] NAMES =
        { "String", "Integer", "None" };

&#64;Override protected String typeName(int selIdx) {
    return NAMES[selIdx - 1];
}</code></pre>

     Be sure to use the right number of names and make "None" the final one.
     */
    protected abstract String typeName(int selIdx);

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
        String tn = typeName(sel);
        return sel == 3 ? tn
                        : tn + "(" + stringify(item) + ")";
    }
}
