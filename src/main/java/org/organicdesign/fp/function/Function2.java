// Copyright 2013-12-11 PlanBase Inc. & Glen Peterson
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
 This is like Java 8's java.util.function.Consumer, but retrofitted to turn checked exceptions
 into unchecked ones in Java 5, 6, and 7.
 */
public abstract class Function2<T,U,V> {
    /** Implement this one method and you don't have to worry about checked exceptions. */
    public abstract V apply(T t, U u) throws Exception;

    /**
     The class that takes a consumer as an argument uses this convenience method so that it
     doesn't have to worry about checked exceptions either.
     */
    public V apply_(T t, U u) {
        try {
            return apply(t, u);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
