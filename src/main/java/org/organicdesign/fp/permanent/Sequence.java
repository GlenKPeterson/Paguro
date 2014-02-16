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

import org.organicdesign.fp.Sentinel;
import org.organicdesign.fp.Transformable;
import org.organicdesign.fp.function.BiFunction;
import org.organicdesign.fp.function.Consumer;
import org.organicdesign.fp.function.Function;
import org.organicdesign.fp.function.Predicate;

/**
 A Sequence abstraction that lazy operations can be built from.  The idea is to create a lazy,
 immutable, persistent (memoized/cached), type-safe, thread-safe storage for finite data sources
 that fit in memory (because those that don't cannot be memoized/cached).
 @param <T>
 */
public abstract class Sequence<T> extends Transformable<T> {
    public static final Sequence<?> EMPTY_SEQUENCE = new Sequence<Object>() {
        /** @return USED_UP */
        @Override public Object first() { return Sentinel.USED_UP; }

        /** @return EMPTY_SEQUENCE (this) */
        @Override public Sequence<Object> rest() { return this; }
    };

    // ======================================= Base methods =======================================
    public abstract T first();
    public abstract Sequence<T> rest();

//    // ======================================= Other methods ======================================

    @Override
    public <U> Sequence<U> map(Function<T,U> func) {
        return SequenceMapped.of(this, func);
    }

    @Override
    public Sequence<T> filter(Predicate<T> func) {
        return SequenceFiltered.of(this, func);
    }

    @Override
    public void forEach(Consumer<T> se) {
        Sequence<T> seq = this;
        T item = seq.first();
        while (item != Sentinel.USED_UP) {
            se.accept_(item);
            // repeat with next element
            seq = seq.rest();
            item = seq.first();
        }
    }

    @Override
    public T firstMatching(Predicate<T> pred) {
        Sequence<T> seq = this;
        T item = seq.first();
        while (item != Sentinel.USED_UP) {
            if (pred.test_(item)) { return item; }
            // repeat with next element
            seq = seq.rest();
            item = seq.first();
        }
        return null;
    }

    @Override
    public <U> U foldLeft(U u, BiFunction<U, T, U> fun) {
        Sequence<T> seq = this;
        T item = seq.first();
        while (item != Sentinel.USED_UP) {
            u = fun.apply_(u, item);
            // repeat with next element
            seq = seq.rest();
            item = seq.first();
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
//    public <U> Sequence<U> flatMap(Function<T,Sequence<U>> func) {
//        return SequenceFlatMapped.of(this, func);
//    }




}