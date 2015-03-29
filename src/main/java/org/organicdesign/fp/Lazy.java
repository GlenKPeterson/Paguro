package org.organicdesign.fp;

import org.organicdesign.fp.function.Function0;

public class Lazy {

    /**
     Take a Function0 and lazily initialize a value (and frees the initialization resources) on the first call to get().
     Subsequent calls to get() cheaply return the previously initialized value.  This class is thread-safe if the producer
     function and the value it produces are pure and free from side effects.
     */
    public static class Ref<T> {
        private Function0<T> producer;
        private T value;

        private Ref(Function0<T> p) { producer = p; }

        /**
         * Use this function to produce a value on the first call to get().  Delete the pointer to this function when that
         * first call completes, but remember the value to return with all subsequent calls to get().
         * @param producer will produce the desired value when called.
         * @param <T>
         * @return
         */
        public static <T> Ref<T> of(Function0<T> producer) {
            if (producer == null) {
                throw new IllegalArgumentException("The producer function cannot be null (the value it returns can)");
            }
            return new Ref<>(producer);
        }

        /**
         * The first call to this method initializes the value this class wraps and releases the initialization resources.
         * Subsequent calls return the precomputed value.
         * @return the same value every time it is called.
         */
        public T get() {
            // Have we produced our value yet (cheap, but not thread-safe check)?
            if (producer != null) {
                // One thread comes in here at a time, but this can be expensive.
                synchronized (this) {
                    // Checking again inside the sync block ensures only one thread can produce the value.
                    if (producer != null) {
                        // Here, a single thread has earned the right to produce our value.
                        value = producer.apply_();
                        // Delete the producer to 1. mark the work done and 2. free resources.
                        producer = null;
                    }
                }
            }
            // We're clear to return the lazily computed value.
            return value;
        }
    }


    /**
     Take a Function0&lt;Integer&gt; and lazily initialize a primitive int (and frees the initialization resources) on
     the first call to get().  Subsequent calls to get() cheaply return the previously initialized int.  This class is
     thread-safe if the producer function is pure and free from side effects.  Since this is a performance booster,
     it makes sense to use primitives sometimes (especially for a hashcode).
     */
    public static class Int {
        public static final Int ZERO = new Int(() -> 0) {
            @Override public int get() { return 0; }
        };

        private Function0<Integer> producer;
        private int value;

        private Int(Function0<Integer> p) { producer = p; }

        /**
         * Use this function to produce a value on the first call to get().  Delete the pointer to this function when that
         * first call completes, but remember the value to return with all subsequent calls to get().
         * @param producer will produce the desired value when called.
         */
        public static Int of(Function0<Integer> producer) {
            if (producer == null) {
                throw new IllegalArgumentException("The producer function cannot be null (the value it returns can)");
            }
            return new Int(producer);
        }

        /**
         * The first call to this method initializes the value this class wraps and releases the initialization resources.
         * Subsequent calls return the precomputed value.
         * @return the same value every time it is called.
         */
        public int get() {
            // Have we produced our value yet (cheap, but not thread-safe check)?
            if (producer != null) {
                // One thread comes in here at a time, but this can be expensive.
                synchronized (this) {
                    // Checking again inside the sync block ensures only one thread can produce the value.
                    if (producer != null) {
                        // Here, a single thread has earned the right to produce our value.
                        value = producer.apply_();
                        // Delete the producer to 1. mark the work done and 2. free resources.
                        producer = null;
                    }
                }
            }
            // We're clear to return the lazily computed value.
            return value;
        }
    }


}
