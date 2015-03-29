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

import org.organicdesign.fp.Option;
import org.organicdesign.fp.Transformable;
import org.organicdesign.fp.function.Function1;
import org.organicdesign.fp.function.Function2;

import java.util.Iterator;

/**
 A lightweight, one-time view that lazy, thread-safe operations can be built from.  Because there
 is no caching/memoization, this may be useful for data sources that do not fit in memory.
 */

public interface View<T> extends Transformable<T> {
    static final View<?> EMPTY_VIEW = Option::none;

    @SuppressWarnings("unchecked")
    static <U> View<U> emptyView() { return (View<U>) EMPTY_VIEW; }

    static <T> View<T> of(Iterator<T> i) { return ViewFromIterator.of(i); }

    static <T> View<T> of(Iterable<T> i) { return ViewFromIterator.of(i); }

    @SafeVarargs
    static <T> View<T> ofArray(T... i) { return ViewFromArray.of(i); }

    /**
      This is the distinguishing method of the view interface.
     @return the next item in the view, or None()
     */
    Option<T> next();

    @Override
    default <U> View<U> map(Function1<T,U> func) { return ViewMapped.of(this, func); }

    @Override
    default View<T> filter(Function1<T,Boolean> pred) { return ViewFiltered.of(this, pred); }

    @Override
    default void forEach(Function1<T,?> consumer) {
        Option<T> item = next();
        while (item.isSome()) {
            consumer.apply_(item.get());
            item = next();
        }
    }

//    /**
//     Deprecated: use filter(...).first() instead.
//     */
//    @Override
//    @Deprecated
//    default Option<T> firstMatching(Predicate<T> pred) {
//        Option<T> item = next();
//        while (item.isSome()) {
//            if (pred.test(item.get())) { return item; }
//            item = next();
//        }
//        return null;
//    }

    @Override
    default <U> U foldLeft(U u, Function2<U,T,U> fun) {
        Option<T> item = next();
        while (item.isSome()) {
            u = fun.apply_(u, item.get());
            item = next();
        }
        return u;
    }

    @Override
    default <U> U foldLeft(U u, Function2<U,T,U> fun, Function1<U,Boolean> terminateWith) {
        Option<T> item = next();
        while (item.isSome()) {
            u = fun.apply_(u, item.get());
            if (terminateWith.apply_(u)) {
                return u;
            }
            item = next();
        }
        return u;
    }

//    default <U> U foldLeft(Tuple2<U,Boolean> u,
//                           BiFunction<U, T, Tuple2<U,Boolean>> fun) {
//        Option<T> item = next();
//        while (item.isSome()) {
//            u = fun.apply(u._1(), item.get());
//            if (u._2()) {
//                return u._1();
//            }
//            item = next();
//        }
//        return u._1();
//    }

//    @Override
//    public T reduceLeft(BiFunction<T, T, T> fun) {
//        T item = next();
//        T accum = item;
//        while (item != None()) {
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
    @Override default View<T> take(long numItems) { return ViewTaken.of(this, numItems); }

    @Override default View<T> takeWhile(Function1<T,Boolean> predicate) { return ViewTakenWhile.of(this, predicate); }

    // default View<T> takeUntilInclusive(Predicate<T> p) { return ViewTakenUntilIncl.of(this, p); }

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
    default <U> View<U> flatMap(Function1<T,View<U>> func) { return ViewFlatMapped.of(this, func); }

    default View<T> append(View<T> pv) { return ViewPrepended.of(pv, this); }

    default View<T> prepend(View<T> pv) { return ViewPrepended.of(this, pv); }
}
