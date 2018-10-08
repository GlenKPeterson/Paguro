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

package org.organicdesign.fp.function

/**
 * Lazily initialize a value (and free the initialization resources) on the first call to get().
 * Subsequent calls to get() cheaply return the previously initialized value.  This class is thread-safe if the producer
 * function and the value it produces are free from side effects.
 */
// TODO: Must override invoke method (not invokeEx)!
class LazyRef<T>(producer: Fn0<T>) : Fn0<T> {
    private var prod: Fn0<T>? = producer
    private var value: T? = null

    /**
     * The first call to this method calls the initialization function, caches the result, and hands
     * the initialization function reference to the garbage collector so that initialization resources
     * can be freed.  Subsequent calls return the precomputed value.
     *
     * @return the same value every time it is called.
     */
    // This whole method is synchronized on the advice of Goetz2006 p. 347
    @Synchronized
    override fun invokeEx(): T {
        // Have we produced our value yet?
        if (prod != null) {
            // produce our value.
            value = prod!!.invoke()
            // Delete the producer to 1. mark the work done and 2. free resources.
            prod = null
        }
        // We're clear to return the lazily computed value.
        return value!!
    }

    // I don't like this because it's not referentially transparent.
    //        public boolean isRealizedYet() { return producer == null; }

    // I don't like this because it's not referentially transparent, but it could be helpful for testing.
    /**
     * Useful for debugging, but not referentially transparent (sometimes returns LazyRef(*not-computed-yet*),
     * sometimes shows the value that was computed).
     * @return a string describing this LazyRef and showing whether or not its value has been computed yet.
     */
    override fun toString(): String {
        return "LazyRef(" + (if (prod == null) value else "*not-computed-yet*") + ")"
    }
}
