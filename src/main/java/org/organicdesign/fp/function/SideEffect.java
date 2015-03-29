// Copyright 2014-02-02 PlanBase Inc. & Glen Peterson
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

import java.util.function.Supplier;

/**
 Takes no arguments, has no return.  You could use a Supplier and ignore the return, but this
 makes it clearer that you are doing something purely for side-effects.  It would be nice if
 Supplier could implement SideEffect so that they could be used more interchangeably, but
 Java inheritence prohibits overriding methods with the same arguments and different return types.
 So we have an asSupplier() convenience method built in.
 This is deprecated because it just doesn't seem like a good idea.  You can return null from a Function0.
 */
@Deprecated
@FunctionalInterface
public interface SideEffect {

    /** Implement this one method and you don't have to worry about checked exceptions. */
    void applyEx() throws Exception;

    /** The caller should use this convenience method to avoid checked exceptions. */
    default void apply() {
        try {
            applyEx();
        } catch (RuntimeException re) {
            throw re;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings({"unchecked", "deprecation"})
    default <T> Supplier<T> asSupplier() {
        final SideEffect parent = this;
        return () -> {
            parent.apply();
            return (T) null;
        };
    }
}
