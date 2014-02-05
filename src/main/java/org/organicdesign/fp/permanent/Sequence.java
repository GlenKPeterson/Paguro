// Copyright 2014-01-20 PlanBase Inc. & Glen Peterson
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

package org.organicdesign.fp.permanent;

import org.organicdesign.fp.Realizable;

/**
 A Sequence abstraction that lazy operations can be built from.  The idea is to create a lazy,
 immutable, persistent (memoized/cached), type-safe, thread-safe storage for finite data sources
 that fit in memory (because those that don't cannot be memoized/cached).
 @param <T>
 */
public interface Sequence<T> extends Realizable<T> {
    public static final Object USED_UP = new Object();
    public static final Sequence<Object> EMPTY_SEQUENCE = new SequenceAbstract<Object>() {
        /**
         @return the first item in the sequence or USED_UP
         */
        @Override
        public Object first() {
            return USED_UP;
        }

        /**
         @return a sequence or EMPTY_SEQUENCE
         */
        @Override
        public Sequence<Object> rest() {
            return this;
        }
    };

    // ======================================= Base methods =======================================
    public T first();
    public Sequence<T> rest();
}