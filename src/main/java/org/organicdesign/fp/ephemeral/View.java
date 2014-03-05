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

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import org.organicdesign.fp.Sentinel;
import org.organicdesign.fp.Transformable;

/**
 A lightweight, one-time view that lazy, thread-safe operations can be built from.  Because there
 is no caching/memoization, this may be useful for data sources that do not fit in memory.
 @param <T>
 */

public interface View<T> extends Transformable<T> {
    public static final View<?> EMPTY_VIEW = new View<Object>() {
        @Override
        public Object next() {
            return Transformable.usedUp();
        }
    };
    @SuppressWarnings("unchecked")
    public static <U> View<U> emptyView() {
        return (View<U>) EMPTY_VIEW;
    }

    /**
      This is the distinguishing method of the view interface.
     @return the next item in the view, or Sentinel.USED_UP
     */
    T next();

    @Override
    default <U> View<U> map(Function<T,U> func) {
        return ViewMapped.of(this, func);
    }

    @Override
    default View<T> filter(Predicate<T> pred) {
        return ViewFiltered.of(this, pred);
    }

    @Override
    default void forEach(Consumer<T> se) {
        T item = next();
        while (item != Sentinel.USED_UP) {
            se.accept(item);
            item = next();
        }
    }

    @Override
    default T firstMatching(Predicate<T> pred) {
        T item = next();
        while (item != Sentinel.USED_UP) {
            if (pred.test(item)) { return item; }
            item = next();
        }
        return null;
    }

    @Override
    default <U> U foldLeft(U u, BiFunction<U, T, U> fun) {
        T item = next();
        while (item != Sentinel.USED_UP) {
            u = fun.apply(u, item);
            item = next();
        }
        return u;
    }

//    @Override
//    public T reduceLeft(BiFunction<T, T, T> fun) {
//        T item = next();
//        T accum = item;
//        while (item != Sentinel.USED_UP) {
//            item = next();
//            accum = fun.apply_(accum, item);
//        }
//        return accum;
//    }

    // TODO: Add these to Transformable and implement them in permanent.Sequence.

    /**
     Shorten this view to contain no more than the specified number of items.
     @param numItems the maximum number of items in the returned view.
     @return a lazy view containing no more than the specified number of items.
     */
    default View<T> take(long numItems) {
        return ViewTaken.of(this, numItems);
    }

    /**
     Note that all dropped items will be evaluated as they are dropped.  Any side effects
     (including delays) caused by evaluating these items will be incurred.  For this reason,
     you should always drop as early in your chain of functions as practical.
     @param numItems the number of items at the beginning of this view to ignore
     @return a lazy view with the specified number of items ignored.
     */
    default View<T> drop(long numItems) {
        return ViewDropped.of(this, numItems);
    }

    // I don't see how I can legally declare this on Transformable!
    /**
     One of the two higher-order functions that can produce more output items than input items.
     foldLeft is the other, but flatMap is lazy while foldLeft is eager.
     @return a lazily evaluated collection which is expected to be larger than the input
     collection.  For a collection that's the same size, map() is more efficient.  If the expected
     return is smaller, use filter followed by map if possible, or vice versa if not.
     @param func yields a Transformable of 0 or more results for each input item.
     */
    default <U> View<U> flatMap(Function<T,View<U>> func) {
        return ViewFlatMapped.of(this, func);
    }
}
