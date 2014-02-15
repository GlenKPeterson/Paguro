// Copyright 2014-01-08 PlanBase Inc. & Glen Peterson
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

package org.organicdesign.fp.ephemeral;

import org.organicdesign.fp.Sentinal;
import org.organicdesign.fp.Transformable;
import org.organicdesign.fp.function.Function;

/**
 A lightweight, one-time view that lazy, thread-safe operations can be built from.  Because there
 is no caching/memoization, this may be useful for data sources that do not fit in memory.
 @param <T>
 */

public interface View<T> extends Transformable<T> {
    public static final View<?> EMPTY_VIEW = new ViewAbstract<Object>() {
        @Override
        public Object next() {
            return Sentinal.USED_UP;
        }
    };

    public T next();

    // I don't see how I can legally declare this on Transformable!
    /**
     One of the two higher-order functions that can produce more output items than input items.
     foldLeft is the other, but flatMap is lazy while foldLeft is eager.
     @return a lazily evaluated collection which is expected to be larger than the input
     collection.  For a collection that's the same size, map() is more efficient.  If the expected
     return is smaller, use filter followed by map if possible, or vice versa if not.
     @param fun yields a Transformable of 0 or more results for each input item.
     */
    public <U> View<U> flatMap(Function<T,View<U>> func);
}
