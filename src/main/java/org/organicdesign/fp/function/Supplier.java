// Copyright 2014-01-16 PlanBase Inc. & Glen Peterson
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

package org.organicdesign.fp.function;

/**
 This is like Java 8's java.util.function.Function, but retrofitted to turn checked exceptions
 into unchecked ones in Java 5, 6, and 7.  I originally called this Function0, meaning a zero-
 argument function and called it's method apply() like all the other functions, but I renamed it
 to match Java8.  Yucky.
 */
public abstract class Supplier<T> {
    /** Implement this one method and you don't have to worry about checked exceptions. */
    public abstract T get() throws Exception;

    /**
     The class that takes a consumer as an argument uses this convenience method so that it
     doesn't have to worry about checked exceptions either.
     */
    public T get_() {
        try {
            return get();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}