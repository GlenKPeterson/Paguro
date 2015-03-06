// Copyright 2015-03-06 PlanBase Inc. & Glen Peterson
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.organicdesign.fp;

import org.organicdesign.fp.function.Function0;

/**
 Take a Function0 and lazily initialize a value (and frees the initialization resources) on the first call to get().
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
