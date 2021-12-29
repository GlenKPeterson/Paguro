package org.organicdesign.fp.function;

// Should this bee an inner class of Fn0?

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 Lazily initialize a value (and free the initialization resources) on the first call to get().
 Subsequent calls to get() cheaply return the previously initialized value.  This class is thread-safe if the producer
 function and the value it produces are free from side effects.
 */
public class LazyRef<T> implements Fn0<T> {
    private @Nullable Fn0<T> producer;
    private T value;

    private LazyRef(Fn0<T> p) { producer = p; }

    /**
     Construct a LazyRef from the given initialization function.

     @param producer a zero-argument function that produces the desired value when called.

     @return a LazyRef with the given producer.
     */
    public static <T> @NotNull LazyRef<T> of(@NotNull Fn0<T> producer) {
        return new LazyRef<>(producer);
    }

    /**
     The first call to this method calls the initialization function, caches the result, and hands
     the initialization function reference to the garbage collector so that initialization resources
     can be freed.  Subsequent calls return the precomputed value.

     @return the same value every time it is called.
     */
    // This whole method is synchronized on the advice of Goetz2006 p. 347
    public synchronized T applyEx() {
        // Have we produced our value yet?
        if (producer != null) {
            // produce our value.
            value = producer.apply();
            // Delete the producer to 1. mark the work done and 2. free resources.
            producer = null;
        }
        // We're clear to return the lazily computed value.
        return value;
    }

    // I don't like this because it's not referentially transparent.
//        public boolean isRealizedYet() { return producer == null; }

    // I don't like this because it's not referentially transparent, but it could be helpful for testing.
    /**
     Useful for debugging, but not referentially transparent (sometimes returns LazyRef(*not-computed-yet*),
     sometimes shows the value that was computed).
     @return a string describing this LazyRef and showing whether or not its value has been computed yet.
     */
    public @NotNull String toString() { return "LazyRef(" + ((producer == null) ? value : "*not-computed-yet*") + ")"; }
}
