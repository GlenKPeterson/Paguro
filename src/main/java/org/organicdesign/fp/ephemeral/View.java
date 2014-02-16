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
import org.organicdesign.fp.function.BiFunction;
import org.organicdesign.fp.function.Consumer;
import org.organicdesign.fp.function.Function;
import org.organicdesign.fp.function.Predicate;

/**
 A lightweight, one-time view that lazy, thread-safe operations can be built from.  Because there
 is no caching/memoization, this may be useful for data sources that do not fit in memory.
 @param <T>
 */

public abstract class View<T> extends Transformable<T> {
    public static final View<?> EMPTY_VIEW = new View<Object>() {
        @Override
        public Object next() {
            return usedUp();
        }
    };
    @SuppressWarnings("unchecked")
    public T usedUp() { return (T) Sentinal.USED_UP; }

    @SuppressWarnings("unchecked")
    public static <U> View<U> emptyView() {
        return (View<U>) EMPTY_VIEW;
    }


    public abstract T next();

    @Override
    public <U> View<U> map(Function<T,U> func) {
        return ViewMapped.of(this, func);
    }

    @Override
    public View<T> filter(Predicate<T> pred) {
        return ViewFiltered.of(this, pred);
    }

    @Override
    public void forEach(Consumer<T> se) {
        T item = next();
        while (item != Sentinal.USED_UP) {
            se.accept_(item);
            item = next();
        }
    }

    @Override
    public T firstMatching(Predicate<T> pred) {
        T item = next();
        while (item != Sentinal.USED_UP) {
            if (pred.test_(item)) { return item; }
            item = next();
        }
        return null;
    }

    @Override
    public <U> U foldLeft(U u, BiFunction<U, T, U> fun) {
        T item = next();
        while (item != Sentinal.USED_UP) {
            u = fun.apply_(u, item);
            item = next();
        }
        return u;
    }

//    @Override
//    public T reduceLeft(BiFunction<T, T, T> fun) {
//        T item = next();
//        T accum = item;
//        while (item != Sentinal.USED_UP) {
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
    public View<T> take(long numItems) {
        return ViewTaken.of(this, numItems);
    }

    /**
     Note that all dropped items will be evaluated as they are dropped.  Any side effects
     (including delays) caused by evaluating these items will be incurred.  For this reason,
     you should always drop as early in your chain of functions as practical.
     @param numItems the number of items at the beginning of this view to ignore
     @return a lazy view with the specified number of items ignored.
     */
    public View<T> drop(long numItems) {
        return ViewDropped.of(this, numItems);
    }

    // I don't see how I can legally declare this on Transformable!
    /**
     One of the two higher-order functions that can produce more output items than input items.
     foldLeft is the other, but flatMap is lazy while foldLeft is eager.
     @return a lazily evaluated collection which is expected to be larger than the input
     collection.  For a collection that's the same size, map() is more efficient.  If the expected
     return is smaller, use filter followed by map if possible, or vice versa if not.
     @param fun yields a Transformable of 0 or more results for each input item.
     */
    public <U> View<U> flatMap(Function<T,View<U>> func) {
        return ViewFlatMapped.of(this, func);
    }
}
