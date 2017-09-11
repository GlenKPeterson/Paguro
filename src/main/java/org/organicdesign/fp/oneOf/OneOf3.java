package org.organicdesign.fp.oneOf;

import org.organicdesign.fp.collections.ImList;
import org.organicdesign.fp.function.Fn1;
import org.organicdesign.fp.type.RuntimeTypes;

import java.util.Objects;

import static org.organicdesign.fp.type.RuntimeTypes.union2Str;

/**
 Holds one of 3 values. See {@link OneOf2} for a full description.  If you're passing the same type, you probably
 want a tuple instead.
 */
public class OneOf3<A,B,C> {
    private final Object item;
    private final int sel;
    private final ImList<Class> types;

    /**
     Protected constructor for subclassing.

     @param o the item (may be null)
     @param aClass class 0 (to have at runtime for descriptive error messages and toString()).
     @param bClass class 1 (to have at runtime for descriptive error messages and toString()).
     @param cClass class 2 (to have at runtime for descriptive error messages and toString()).
     @param index 0 means this represents an A, 1 represents a B, 2 represents a C, 3 means D
     */
    protected OneOf3(Object o, Class<A> aClass, Class<B> bClass, Class<C> cClass, int index) {
        types = RuntimeTypes.registerClasses(aClass, bClass, cClass);
        sel = index;
        item = o;
        if (index < 0) {
            throw new IllegalArgumentException("Selected item index must be 0-2");
        } else if (index > 2) {
            throw new IllegalArgumentException("Selected item index must be 0-2");
        }
        if ( (o != null) && (!types.get(index).isInstance(o)) ) {
            throw new ClassCastException("You specified index " + index + ", indicating a(n) " +
                                         types.get(index).getCanonicalName() + "," +
                                         " but passed a " + o.getClass().getCanonicalName());
        }
    }

    /**
     Languages that have union types built in have a match statement that works like this method.
     Exactly one of these functions will be executed - determined by which type of item this object holds.
     @param fa the function to be executed if this OneOf stores the first type.
     @param fb the function to be executed if this OneOf stores the second type.
     @param fc the function to be executed if this OneOf stores the third type.
     @return the return value of whichever function is executed.
     */
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

    @Override public String toString() { return union2Str(item, types); }
}
