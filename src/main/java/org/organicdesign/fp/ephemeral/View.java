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
import org.organicdesign.fp.xform.Transformable;
import org.organicdesign.fp.collections.UnmodIterator;
import org.organicdesign.fp.function.Function1;
import org.organicdesign.fp.function.Function2;

/**
 A lightweight, one-time view that lazy, thread-safe operations can be built from.  Because there
 is no caching/memoization, this may be useful for data sources that do not fit in memory.
 */
@FunctionalInterface
public interface View<T> extends Transformable<T>, Iterable<T> {
    static final View<?> EMPTY_VIEW = Option::none;

    @SuppressWarnings("unchecked")
    static <U> View<U> emptyView() { return (View<U>) EMPTY_VIEW; }

    // Just wrong.  You can't trust an iterator that you didn't get yourself.
//    static <T> View<T> of(Iterator<T> i) { return ViewFromIterable.of(i); }

    static <T> View<T> ofIter(Iterable<? extends T> i) { return ViewFromIterable.of(i); }

    @SafeVarargs
    static <T> View<T> ofArray(T... i) { return ViewFromArray.of(i); }

    /**
      This is the distinguishing method of the view interface.
     @return the next item in the view, or None()
     */
    Option<T> next();

    @Override
    default <U> View<U> map(Function1<? super T,? extends U> func) { return ViewMapped.of(this, func); }

    @Override
    default View<T> filter(Function1<? super T,Boolean> pred) { return ViewFiltered.of(this, pred); }

//    Deprecated: Instead use filter(x -> { doStuff(x); return Boolean.TRUE; })
//    /** Returning an unmodified view is impossible here - just returns null. */
//    @Override
//    default View<T> forEach(Function1<? super T,?> consumer) {
//        Option<T> item = next();
//        while (item.isSome()) {
//            consumer.apply(item.get());
//            item = next();
//        }
//        return null;
//    }

//    /**
//     Deprecated: use filter(...).head() instead.
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
    default <U> U foldLeft(U u, Function2<U,? super T,U> fun) {
        Option<T> item = next();
        while (item.isSome()) {
            u = fun.apply(u, item.get());
            item = next();
        }
        return u;
    }

    @Override
    default <U> U foldLeft(U u, Function2<U,? super T,U> fun, Function1<? super U,Boolean> terminateWhen) {
        Option<T> item = next();
        while (item.isSome()) {
            u = fun.apply(u, item.get());
            if (terminateWhen.apply(u)) {
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
//            u = fun.applyEx(u._1(), item.get());
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
//            accum = fun.apply(accum, item);
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

    @Override default View<T> takeWhile(Function1<? super T,Boolean> predicate) {
        return ViewTakenWhile.of(this, predicate);
    }

    // default View<T> takeUntilInclusive(Predicate<T> p) { return ViewTakenUntilIncl.of(this, p); }

    /** {@inheritDoc} */
    @Override default View<T> drop(long numItems) { return ViewDropped.of(this, numItems); }

    /** {@inheritDoc} */
    @Override default <U> View<U> flatMap(Function1<? super T,Iterable<U>> func) { return ViewFlatMapped.of(this, func); }

    /** {@InheritDoc} */
    @Override default View<T> concat(Iterable<? extends T> list) {
        return ViewPrepended.of(this, list);
    }
    /** {@InheritDoc} */
    @Override default View<T> precat(Iterable<? extends T> list) {
        return ViewPrepended.of(list, this);
    }

    @Override default UnmodIterator<T> iterator() {
        final View<T> v = this;
        // Maybe not so performant, but gives a chance to see if this is even a useful method.
        return new UnmodIterator<T>() {
            private View<T> inner = v;
            private Option<T> next = v.next();

            @Override public boolean hasNext() { return next.isSome(); }

            @Override public T next() {
                Option<T> old = next;
                next = inner.next();
                return old.getOrElse(null);
            }
        };
    }
}
