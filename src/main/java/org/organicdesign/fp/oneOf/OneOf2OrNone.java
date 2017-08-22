package org.organicdesign.fp.oneOf;

import org.organicdesign.fp.collections.ImList;
import org.organicdesign.fp.function.Fn0;
import org.organicdesign.fp.function.Fn1;
import org.organicdesign.fp.type.RuntimeTypes;

import java.util.Objects;

import static org.organicdesign.fp.FunctionUtils.stringify;

/**
 Holds one of 3 values or {@link None}. See {@link OneOf2} for a full description.  If you're passing the same type,
 you probably want a tuple instead.
 */
public abstract class OneOf2OrNone<A,B> {

    private final Object item;
    private final int sel;
    private final ImList<Class> types;

    /**
     Protected constructor for subclassing.  Both A and B parameters can be null, but if one is non-null, the index
     must specify the non-null value (to keep you from assigning a bogus index value).

     @param a the first possibility.
     @param aClass the class of item A (to have at runtime for descriptive error messages and toString()).
     @param b the second possibility
     @param bClass the class of item B (to have at runtime for descriptive error messages and toString()).
     @param index 0 means this represents an a, 1 represents a b, 2 represents None.
     */
    protected OneOf2OrNone(A a, Class<A> aClass,
                           B b, Class<B> bClass,
                           int index) {
        types = RuntimeTypes.registerClasses(aClass, bClass, None.class);
        sel = index;
        if (index == 0) {
            item = a;
            if (b != null) {
                throw new IllegalArgumentException("You specified item A (index = 0), but passed a non-null item B");
            }
        } else if (index == 1) {
            item = b;
            if (a != null) {
                throw new IllegalArgumentException("You specified item B (index = 1), but passed a non-null item A");
            }
        } else if (index == 2) {
            item = null;
            if ( (a != null) || (b != null) ) {
                throw new IllegalArgumentException("For None, all items must be null");
            }
        } else {
            throw new IllegalArgumentException("Selected item index must be 0-2 where 2 selects None");
        }
    }

    /**
     Languages that have union types built in have a match statement that works like this method.
     Exactly one of these functions will be executed - determined by which type of item this object holds.
     @param fa the function to be executed if this OneOf stores the first type.
     @param fb the function to be executed if this OneOf stores the second type.
     @param fz the function to be executed if this OneOf stores a None.
     @return the return value of whichever function is executed.
     */
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
