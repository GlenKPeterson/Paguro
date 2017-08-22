package org.organicdesign.fp.oneOf;

import org.organicdesign.fp.collections.ImList;
import org.organicdesign.fp.function.Fn1;
import org.organicdesign.fp.type.RuntimeTypes;

import java.util.Objects;

import static org.organicdesign.fp.FunctionUtils.stringify;

/**
 Holds one of 4 values. See {@link OneOf2} for a full description.  If you're passing the same type, you probably
 want a tuple instead.
 */
public abstract class OneOf4<A,B,C,D> {
    private final Object item;
    private final int sel;
    private final ImList<Class> types;

    /**
     Protected constructor for subclassing.  A, B, and C parameters can be null, but if one is non-null, the index
     must specify the non-null value (to keep you from assigning a bogus index value).

     @param a the first possibility.
     @param aClass the class of item A (to have at runtime for descriptive error messages and toString()).
     @param b the second possibility
     @param bClass the class of item B (to have at runtime for descriptive error messages and toString()).
     @param c the third possibility
     @param cClass the class of item C (to have at runtime for descriptive error messages and toString()).
     @param index 0 means this represents an A, 1 represents a B, 2 represents a C.
     */
    protected OneOf4(A a, Class<A> aClass,
                     B b, Class<B> bClass,
                     C c, Class<C> cClass,
                     D d, Class<D> dClass,
                     int index) {
        types = RuntimeTypes.registerClasses(aClass, bClass, cClass, dClass);
        sel = index;
        if (index < 2) {
            if (index == 0) {
                item = a;
                if (b != null) {
                    throw new IllegalArgumentException("You specified item A (index = 0)," +
                                                       " but passed a non-null item B");
                }
                if (c != null) {
                    throw new IllegalArgumentException("You specified item A (index = 0)," +
                                                       " but passed a non-null item C");
                }
                if (d != null) {
                    throw new IllegalArgumentException("You specified item A (index = 0)," +
                                                       " but passed a non-null item D");
                }
            } else if (index == 1) {
                item = b;
                if (a != null) {
                    throw new IllegalArgumentException("You specified item B (index = 1)," +
                                                       " but passed a non-null item A");
                }
                if (c != null) {
                    throw new IllegalArgumentException("You specified item B (index = 1)," +
                                                       " but passed a non-null item C");
                }
                if (d != null) {
                    throw new IllegalArgumentException("You specified item B (index = 1)," +
                                                       " but passed a non-null item D");
                }
            } else {
                throw new IllegalArgumentException("Selected item index must be 0-3");
            }
        } else {
            if (index == 2) {
                item = c;
                if (a != null) {
                    throw new IllegalArgumentException("You specified item C (index = 2)," +
                                                       " but passed a non-null item A");
                }
                if (b != null) {
                    throw new IllegalArgumentException("You specified item C (index = 2)," +
                                                       " but passed a non-null item B");
                }
                if (d != null) {
                    throw new IllegalArgumentException("You specified item C (index = 2)," +
                                                       " but passed a non-null item D");
                }
            } else if (index == 3) {
                item = d;
                if (a != null) {
                    throw new IllegalArgumentException("You specified item D (index = 3)," +
                                                       " but passed a non-null item A");
                }
                if (b != null) {
                    throw new IllegalArgumentException("You specified item D (index = 3)," +
                                                       " but passed a non-null item B");
                }
                if (c != null) {
                    throw new IllegalArgumentException("You specified item D (index = 3)," +
                                                       " but passed a non-null item C");
                }
            } else {
                throw new IllegalArgumentException("Selected item index must be 0-3");
            }
        }
    }

    /**
     Languages that have union types built in have a match statement that works like this method.
     Exactly one of these functions will be executed - determined by which type of item this object holds.
     @param fa the function to be executed if this OneOf stores the first type.
     @param fb the function to be executed if this OneOf stores the second type.
     @param fc the function to be executed if this OneOf stores the third type.
     @param fd the function to be executed if this OneOf stores the fourth type.
     @return the return value of whichever function is executed.
     */
    // We only store one item and it's type is erased, so we have to cast it at runtime.
    // If sel is managed correctly, it ensures that the cast is accurate.
    @SuppressWarnings("unchecked")
    public <R> R match(Fn1<A, R> fa,
                       Fn1<B, R> fb,
                       Fn1<C, R> fc,
                       Fn1<D, R> fd) {
        if (sel == 0) {
            return fa.apply((A) item);
        } else if (sel == 1) {
            return fb.apply((B) item);
        } else if (sel == 2) {
            return fc.apply((C) item);
        } else {
            return fd.apply((D) item);
        }
    }

    public int hashCode() {
        // Simplest way to make the two items different.
        return Objects.hashCode(item) + sel;
    }

    @SuppressWarnings("unchecked")
    @Override public boolean equals(Object other) {
        if (this == other) { return true; }
        if (!(other instanceof OneOf4)) { return false; }

        OneOf4 that = (OneOf4) other;
        return (sel == that.sel) &&
               Objects.equals(item, that.item);
    }

    @Override public String toString() {
        return RuntimeTypes.name(types.get(sel)) + "/4(" + stringify(item) + ")";
    }
}
