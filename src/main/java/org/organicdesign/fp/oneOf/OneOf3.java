package org.organicdesign.fp.oneOf;

import org.organicdesign.fp.function.Fn1;

import java.util.Objects;

import static org.organicdesign.fp.FunctionUtils.stringify;

/** Holds one of 2 values. */
abstract public class OneOf3<A,B,C> {
    protected final Object item;
    protected final int sel;

    protected OneOf3(A a, B b, C c, int s) {
        sel = s;
        if (sel == 1) {
            item = a;
        } else if (sel == 2) {
            item = b;
        } else if (sel == 3) {
            item = c;
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
     { "String", "Integer", "Float" };

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
                       Fn1<C, R> fc) {
        if (sel == 1) {
            return fa.apply((A) item);
        } else if (sel == 2) {
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
        return typeName(sel) + "(" + stringify(item) + ")";
    }
}
