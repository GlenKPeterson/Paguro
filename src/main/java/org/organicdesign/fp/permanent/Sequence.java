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

import org.organicdesign.fp.Sentinal;
import org.organicdesign.fp.Transformable;

/**
 A Sequence abstraction that lazy operations can be built from.  The idea is to create a lazy,
 immutable, persistent (memoized/cached), type-safe, thread-safe storage for finite data sources
 that fit in memory (because those that don't cannot be memoized/cached).
 @param <T>
 */
public interface Sequence<T> extends Transformable<T> {
    public static final Sequence<?> EMPTY_SEQUENCE = new SequenceAbstract<Object>() {
        /**
         @return the first item in the sequence or USED_UP
         */
        @Override
        public Object first() {
            return Sentinal.USED_UP;
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

//    // ======================================= Other methods ======================================
//    // I don't see how I can legally declare this on Transformable!
      // When implementing, the innerSequence needs to call rest() on the parent sequence instead
      // of returning USED_UP.  Otherwise, it's a pretty clean copy of ViewFlatMapped.
//    /**
//     One of the two higher-order functions that can produce more output items than input items.
//     foldLeft is the other, but flatMap is lazy while foldLeft is eager.
//     @return a lazily evaluated collection which is expected to be larger than the input
//     collection.  For a collection that's the same size, map() is more efficient.  If the expected
//     return is smaller, use filter followed by map if possible, or vice versa if not.
//     @param fun yields a Transformable of 0 or more results for each input item.
//     */
//    public <U> Sequence<U> flatMap(Function<T,Sequence<U>> func);
}