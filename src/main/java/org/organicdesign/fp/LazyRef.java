package org.organicdesign.fp;

import org.organicdesign.fp.function.Function0;

/**
 Lazily initialize a value (and free the initialization resources) on the first call to get().
 Subsequent calls to get() cheaply return the previously initialized value.  This class is thread-safe if the producer
 function and the value it produces are pure and free from side effects.
 */
public class LazyRef<T> {
    private Function0<T> producer;
    private T value;

    private LazyRef(Function0<T> p) { producer = p; }

    /**
     * Use this function to produce a value on the first call to get().  Delete the pointer to this function when that
     * first call completes, but remember the value to return with all subsequent calls to get().
     * @param producer will produce the desired value when called.
     * @param <T>
     * @return
     */
    public static <T> LazyRef<T> of(Function0<T> producer) {
        if (producer == null) {
            throw new IllegalArgumentException("The producer function cannot be null (the value it returns can)");
        }
        return new LazyRef<>(producer);
    }

    /**
     The first call to this method initializes the value this class wraps and releases the initialization resources.
     Subsequent calls return the precomputed value.
     @return the same value every time it is called.
     */
    public T get() {
        // Have we produced our value yet (cheap, but not thread-safe check)?
        if (producer != null) {
            // One thread comes in here at a time, but this can be expensive.
            synchronized (this) {
                // Checking again inside the sync block ensures only one thread can produce the value.
                if (producer != null) {
                    // Here, a single thread has earned the right to produce our value.
                    value = producer.apply();
                    // Delete the producer to 1. mark the work done and 2. free resources.
                    producer = null;
                }
            }
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
    public String toString() { return "LazyRef(" + ((producer == null) ? value : "*not-computed-yet*") + ")"; }
}
