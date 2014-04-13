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

import java.util.Iterator;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import org.organicdesign.fp.Option;
import org.organicdesign.fp.Transformable;

/**
 A Sequence abstraction that lazy operations can be built from.  The idea is to create a lazy,
 immutable, persistent (memoized/cached), type-safe, thread-safe storage for finite data sources
 that fit in memory (because those that don't cannot be memoized/cached).
 @param <T>
 */
public interface Sequence<T> extends Transformable<T> {
    public static final Sequence<?> EMPTY_SEQUENCE = new Sequence<Object>() {
        /** @return USED_UP */
        @Override public Option<Object> first() { return Option.none(); }

        /** @return EMPTY_SEQUENCE (this) */
        @Override public Sequence<Object> rest() { return this; }
    };
    @SuppressWarnings("unchecked")
    public static <T> Sequence<T> emptySequence() {
        return (Sequence<T>) EMPTY_SEQUENCE;
    }

    public static <T> Sequence<T> of(Iterator<T> i) {
        return SequenceFromIterator.of(i);
    }

    public static <T> Sequence<T> of(Iterable<T> i) {
        return SequenceFromIterator.of(i);
    }

    @SafeVarargs
    public static <T> Sequence<T> ofArray(T... i) { return SequenceFromArray.of(i); }

    // ======================================= Base methods =======================================
    Option<T> first();
    Sequence<T> rest();

//    // ======================================= Other methods ======================================

    @Override
    default <U> Sequence<U> map(Function<T,U> func) {
        return SequenceMapped.of(this, func);
    }

    @Override
    default Sequence<T> filter(Predicate<T> func) {
        return SequenceFiltered.of(this, func);
    }

    @Override
    default void forEach(Consumer<T> se) {
        Sequence<T> seq = this;
        Option<T> item = seq.first();
        while (!item.isSome()) {
            se.accept(item.get());
            // repeat with next element
            seq = seq.rest();
            item = seq.first();
        }
    }

    @Override
    default Option<T> firstMatching(Predicate<T> pred) {
        Sequence<T> seq = this;
        Option<T> item = seq.first();
        while (!item.isSome()) {
            if (pred.test(item.get())) { return item; }
            // repeat with next element
            seq = seq.rest();
            item = seq.first();
        }
        return null;
    }

    @Override
    default <U> U foldLeft(U u, BiFunction<U, T, U> fun) {
        Sequence<T> seq = this;
        // System.out.println("seq: " + seq);
        Option<T> item = seq.first();
        // System.out.println("===>item: " + item);
        while (item.isSome()) {
            u = fun.apply(u, item.get());
            // repeat with next element
            seq = seq.rest();
            item = seq.first();
        }
        return u;
    }

//    @Override
//    T reduceLeft(BiFunction<T, T, T> fun) {
//        Option<T> item =next();
//        T accum = item;
//        while (!item.isSome()) {
//            item = next();
//            accum = fun.apply_(accum, item);
//        }
//        return accum;
//    }

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
//    <U> Sequence<U> flatMap(Function<T,Sequence<U>> func) {
//        return SequenceFlatMapped.of(this, func);
//    }
}