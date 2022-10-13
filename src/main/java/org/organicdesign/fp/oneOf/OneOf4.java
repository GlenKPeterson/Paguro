package org.organicdesign.fp.oneOf;

import org.jetbrains.annotations.NotNull;
import org.organicdesign.fp.collections.ImList;
import org.organicdesign.fp.function.Fn1;
import org.organicdesign.fp.type.RuntimeTypes;

import java.util.Objects;

import static org.organicdesign.fp.type.RuntimeTypes.union2Str;

/** Holds one of 4 types of value. See {@link OneOf2} for a full description. */
public abstract class OneOf4<A,B,C,D> {
    protected final @NotNull Object item;
    private final int sel;
    @SuppressWarnings("rawtypes")
    private final @NotNull ImList<Class> types;

    /**
     * Protected constructor for subclassing.
     *
     * @param o the item
     * @param aClass class 0
     * @param bClass class 1
     * @param cClass class 2
     * @param dClass class 3
     * @param index 0 means this represents an A, 1 a B, 2 a C, and 3 a D.
     */
    protected OneOf4(
            @NotNull Object o,
            @NotNull Class<A> aClass,
            @NotNull Class<B> bClass,
            @NotNull Class<C> cClass,
            @NotNull Class<D> dClass,
            int index
    ) {
        types = RuntimeTypes.registerClasses(aClass, bClass, cClass, dClass);
        sel = index;
        item = o;
        if (index < 0) {
            throw new IllegalArgumentException("Selected item index must be 0-3");
        } else if (index > 3) {
            throw new IllegalArgumentException("Selected item index must be 0-3");
        }
        if (!types.get(index).isInstance(o)) {
            throw new ClassCastException("You specified index " + index + ", indicating a " +
                                         types.get(index).getCanonicalName() + "," +
                                         " but passed a " + o.getClass().getCanonicalName());
        }
    }

    /**
     * Languages that have union types built in have a match statement that works like this method.
     * Exactly one of these functions will be executed - determined by which type of item this object holds.
     * @param fa applied iff this stores the first type.
     * @param fb applied iff this stores the second type.
     * @param fc applied iff this stores the third type.
     * @param fd applied iff this stores the fourth type.
     * @return the return value of whichever function is executed.
     */
    // We only store one item and its type is erased, so we have to cast it at runtime.
    // If sel is managed correctly, this ensures that cast is accurate.
    @SuppressWarnings("unchecked")
    public <R> R match(
            @NotNull Fn1<A, R> fa,
            @NotNull Fn1<B, R> fb,
            @NotNull Fn1<C, R> fc,
            @NotNull Fn1<D, R> fd
    ) {
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

    @Override public boolean equals(Object other) {
        if (this == other) { return true; }
        if (!(other instanceof OneOf4)) { return false; }

        @SuppressWarnings("rawtypes")
        OneOf4 that = (OneOf4) other;
        return (sel == that.sel) &&
               Objects.equals(item, that.item);
    }

    @Override
    public @NotNull String toString() { return union2Str(item, types); }
}
