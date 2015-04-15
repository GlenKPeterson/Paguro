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

import org.organicdesign.fp.Transformable;
import org.organicdesign.fp.function.Function1;
import org.organicdesign.fp.function.Function2;
import org.organicdesign.fp.tuple.Tuple2;

import java.util.Iterator;
import java.util.Objects;

/**
 A Sequence abstraction that lazy operations can be built from.  The idea is to create a lazy,
 immutable, persistent (memoized/cached), type-safe, thread-safe storage for finite data sources
 that fit in memory (because those that don't cannot be memoized/cached).
 @param <T>
 */
public interface Sequence<T> extends Transformable<T> {
    enum Empty implements Sequence {
        SEQUENCE {
            @Override public Object first() { return null; }
            @Override public Sequence rest() { return this; }
        };
    }

    @SuppressWarnings("unchecked")
    public static <T> Sequence<T> emptySequence() {
        return (Sequence<T>) Empty.SEQUENCE;
    }

    static <U> Tuple2<U,Sequence<U>> emptySeqTuple() {
        return Tuple2.of(null, Sequence.emptySequence());
    }

    public static <T> Sequence<T> of(Iterator<T> i) {
        return SequenceFromIterator.of(i);
    }

    public static <T> Sequence<T> of(Iterable<T> i) {
        return SequenceFromIterator.of(i);
    }

    @SafeVarargs
    public static <T> Sequence<T> ofArray(T... i) { return SequenceFromArray.of(i); }

//    default Option<T> head() { return (Empty.SEQUENCE == rest()) ? Option.none() : Option.of(first()); }

    // ======================================= Base methods =======================================
    T first();
    Sequence<T> rest();

//    // ======================================= Other methods ======================================

    @Override
    default <U> Sequence<U> map(Function1<T,U> func) {
        return SequenceMapped.of(this, func);
    }

    @Override
    default Sequence<T> filter(Function1<T,Boolean> predicate) {
        return SequenceFiltered.of(this, predicate);
    }

    @Override
    default void forEach(Function1<T,?> consumer) {
        Sequence<T> seq = this;
        while (Empty.SEQUENCE != seq) {
            consumer.apply(seq.first());
            // repeat with next element
            seq = seq.rest();
        }
    }


    // Use filter(...).first() instead!
//    @Override
//    default Option<T> firstMatching(Predicate<T> pred) {
//        Sequence<T> seq = this;
//        Option<T> item = seq.head();
//        while (item.isSome()) {
//            if (pred.test(item.get())) { return item; }
//            // repeat with next element
//            seq = seq.rest();
//            item = seq.head();
//        }
//        return null;
//    }

    @Override
    default <U> U foldLeft(U u, Function2<U,T,U> fun) {
        Sequence<T> seq = this;
        // System.out.println("seq: " + seq);
        // System.out.println("===>item: " + item);
        while (Empty.SEQUENCE != seq) {
            u = fun.apply(u, seq.first());
            // repeat with next element
            seq = seq.rest();
        }
        return u;
    }

    @Override
    default <U> U foldLeft(U u, Function2<U,T,U> fun, Function1<U,Boolean> terminateWhen) {
        Sequence<T> seq = this;
        // System.out.println("seq: " + seq);
        // System.out.println("===>item: " + item);
        while (Empty.SEQUENCE != seq) {
            u = fun.apply(u, seq.first());
            if (terminateWhen.apply(u)) {
                return u;
            }
            // repeat with next element
            seq = seq.rest();
        }
        return u;
    }

    /**
     Shorten this Transformable to contain no more than the specified number of items.
     @param numItems the maximum number of items in the returned Sequence.
     @return a lazy Sequence containing no more than the specified number of items.
     */
    @Override
    default Transformable<T> take(long numItems) { return SequenceTaken.of(this, numItems); }

    @Override
    default Sequence<T> takeWhile(Function1<T,Boolean> predicate) { return SequenceTakenWhile.of(this, predicate); }

    /** {@inheritDoc} */
    @Override default Sequence<T> drop(long numItems) { return SequenceDropped.of(this, numItems); }

    /**
     One of the two higher-order functions that can produce more output items than input items.
     foldLeft is the other, but flatMap is lazy while foldLeft is eager.
     @return a lazily evaluated collection which is expected to be larger than the input
     collection.  For a collection that's the same size, map() is more efficient.  If the expected
     return is smaller, use filter followed by map if possible, or vice versa if not.
     @param func yields a Transformable of 0 or more results for each input item.
     */
    default <U> Sequence<U> flatMap(Function1<T,Sequence<U>> func) { return SequenceFlatMapped.of(this, func); }

    /** Add the given Sequence after the end of this one. */
    default Sequence<T> append(Sequence<T> other) { return SequenceConcatenated.of(this, other); }

    /** Add the given Sequence before the beginning of this one. */
    default Sequence<T> prepend(Sequence<T> other) { return SequenceConcatenated.of(other, this); }

//    @Override
//    T reduceLeft(BiFunction<T, T, T> fun) {
//        Option<T> item =next();
//        T accum = item;
//        while (!item.isSome()) {
//            item = next();
//            accum = fun.apply(accum, item);
//        }
//        return accum;
//    }

//    // I don't see how I can legally declare this on Transformable!
      // When implementing, the innerSequence needs to call rest() on the parent sequence instead
      // of returning USED_UP.  Otherwise, it's a pretty clean copy of SequenceFlatMapped.
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

    /** This is correct, but O(n) */
    static int hashCode(Sequence is) {
        int ret = 0;
        while (Empty.SEQUENCE != is) {
            Object i = is.first();
            if (i != null) { ret = ret + i.hashCode(); }
            is = is.rest();
        }
        return ret;
    }

    /** This is correct, but O(n) */
    static boolean equals(Sequence a, Sequence b) {
        // Cheapest operation first...
        if (a == b) { return true; }

        if ( (a == null) ||
                (a.hashCode() != b.hashCode()) ) {
            return false;
        }
        while ((Empty.SEQUENCE != a) && (Empty.SEQUENCE != b)) {
            if (!Objects.equals(a.first(), b.first())) {
                return false;
            }
            a = a.rest(); b = b.rest();
        }
        // Should both be The empty sequence, otherwise, false.
        return a == b;
    }

//    public class LazySequence<T> implements Sequence<T> {
//        private final Lazy.Ref<Tuple2<T,Sequence<T>>> laz;
//
//        LazySequence(T first, Sequence<T> rest) {
//            laz = Lazy.Ref.of(() -> Tuple2.of(first, rest));
//        }
//
//        public static <T> Sequence<T> of(T first, Sequence<T> rest) {
//            if ( (first == null) &&
//                 (Sequence.Empty.SEQUENCE == rest) ) { return Sequence.emptySequence(); }
//            return new LazySequence<>(first, rest);
//        }
//
//        @Override public T first() { return laz.get()._1(); }
//
//        @Override public Sequence<T> rest() { return laz.get()._2(); }
//    }

}