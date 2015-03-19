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

package org.organicdesign.fp.function;

/**
 This is like Java 8's java.util.function.Supplier, but retrofitted to turn checked exceptions
 into unchecked ones.  It's also called a thunk when used to delay evaluation.
 */
public interface Function0<U> {
    /** Implement this one method and you don't have to worry about checked exceptions. */
    U apply() throws Exception;

    /**
     The class that takes a consumer as an argument uses this convenience method so that it
     doesn't have to worry about checked exceptions either.
     */
    default U apply_() {
        try {
            return apply();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static final Function0<Object> NULL = new Function0<Object>() {
        @Override
        public Object apply() throws Exception {
            return null;
        }
    };
// Don't think this is necessary.  Is it?
//    default Supplier<U> asSupplier() {
//        return () -> apply_();
//    }
}
