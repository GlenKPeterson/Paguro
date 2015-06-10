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

import org.organicdesign.fp.Option;
import org.organicdesign.fp.Transformable;
import org.organicdesign.fp.collections.UnIterableOrdered;
import org.organicdesign.fp.collections.UnIteratorOrdered;
import org.organicdesign.fp.function.Function1;
import org.organicdesign.fp.function.Function2;
import org.organicdesign.fp.tuple.Tuple2;

/**
 A Sequence abstraction that lazy operations can be built from.  The idea is to create a lazy,
 immutable, persistent (memoized/cached), type-safe, thread-safe storage for finite data sources
 that fit in memory (because those that don't cannot be memoized/cached).
 @param <T>
 */
public interface Sequence<T> extends Transformable<T>, UnIterableOrdered<T> {
    Sequence<?> EMPTY_SEQUENCE = new Sequence<Object>() {
        /** @return USED_UP */
        @Override public Option<Object> head() { return Option.none(); }

        // TODO: Should we throw IllegalStateException("Can't tail emptySequence")?
        /** @return EMPTY_SEQUENCE (this) */
        @Override public Sequence<Object> tail() { return this; }

        @Override public int hashCode() { return 0; }

        @Override public boolean equals(Object other) {
            // Cheapest operation first...
            if (this == other) { return true; }

            return (other != null) &&
                    (other instanceof Sequence) &&
                    !((Sequence) other).head().isSome();
        }

        @Override public String toString() { return "emptySequence()"; }
    };

    @SuppressWarnings("unchecked")
    static <T> Sequence<T> emptySequence() {
        return (Sequence<T>) EMPTY_SEQUENCE;
    }

    static <U> Tuple2<Option<U>,Sequence<U>> emptySeqTuple() {
        return Tuple2.of(Option.none(), Sequence.emptySequence());
    }

    // This is just wrong.  You can't reliably share an iterator with anyone else.
    // Therefore, you can only wrap an Interable so that you can get your own private
    // iterator from it.
//    static <T> Sequence<T> of(Iterator<T> i) {
//        return SequenceFromIterable.of(i);
//    }

    static <T> Sequence<T> ofIter(Iterable<T> i) {
        return SequenceFromIterable.of(i);
    }

    @SafeVarargs
    static <T> Sequence<T> of(T... i) { return SequenceFromArray.of(i); }

    // ======================================= Base methods =======================================
    /**
     The first item in this sequence.  This was originally called first() but that conflicted with SortedSet.first()
     which did not return an Option and threw an exception when the set was empty.
     */
    Option<T> head();

    /**
     The rest of this sequnce (all the items after its head).  This was originally called rest(), but when I renamed
     first() to head(), I renamed rest() to tail() so that it wouldn't mix metaphors.
     */
    Sequence<T> tail();

//    // ======================================= Other methods ======================================

    @Override
    default <U> Sequence<U> map(Function1<? super T,? extends U> func) {
        return SequenceMapped.of(this, func);
    }

    @Override
    default Sequence<T> filter(Function1<? super T,Boolean> predicate) {
        return SequenceFiltered.of(this, predicate);
    }

    @Override
    default Sequence<T> forEach(Function1<? super T,?> consumer) {
        Sequence<T> seq = this;
        Option<T> item = seq.head();
        while (item.isSome()) {
            consumer.apply(item.get());
            // repeat with next element
            seq = seq.tail();
            item = seq.head();
        }
        return this;
    }


    // Use filter(...).head() instead!
//    @Override
//    default Option<T> firstMatching(Predicate<T> pred) {
//        Sequence<T> seq = this;
//        Option<T> item = seq.head();
//        while (item.isSome()) {
//            if (pred.test(item.get())) { return item; }
//            // repeat with next element
//            seq = seq.tail();
//            item = seq.head();
//        }
//        return null;
//    }

    @Override
    default <U> U foldLeft(U u, Function2<U,? super T,U> fun) {
        Sequence<T> seq = this;
        // System.out.println("seq: " + seq);
        // System.out.println("===>item: " + item);
        Option<T> item = seq.head();
        while (item.isSome()) {
            u = fun.apply(u, item.get());
            // repeat with next element
            seq = seq.tail();
            item = seq.head();
        }
        return u;
    }

    @Override
    default <U> U foldLeft(U u, Function2<U,? super T,U> fun, Function1<? super U,Boolean> terminateWhen) {
        Sequence<T> seq = this;
        // System.out.println("seq: " + seq);
        // System.out.println("===>item: " + item);
        Option<T> item = seq.head();
        while (item.isSome()) {
            u = fun.apply(u, item.get());
            if (terminateWhen.apply(u)) {
                return u;
            }
            // repeat with next element
            seq = seq.tail();
            item = seq.head();
        }
        return u;
    }

    /**
     Shorten this Transformable to contain no more than the specified number of items.
     @param numItems the maximum number of items in the returned Sequence.
     @return a lazy Sequence containing no more than the specified number of items.
     */
    @Override
    default Sequence<T> take(long numItems) { return SequenceTaken.of(this, numItems); }

    @Override
    default Sequence<T> takeWhile(Function1<? super T,Boolean> predicate) { return SequenceTakenWhile.of(this, predicate); }

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
    default <U> Sequence<U> flatMap(Function1<? super T,Sequence<U>> func) {
        return SequenceFlatMapped.of(this, func);
    }

    /** Add the given Sequence after the end of this one. */
    default Sequence<T> concat(Sequence<T> other) { return SequenceConcatenated.of(this, other); }

    /** Add the given Sequence before the beginning of this one. */
    default Sequence<T> precat(Sequence<T> other) { return SequenceConcatenated.of(other, this); }

    /** Add the given items at the end of this sequence. */
    @SuppressWarnings("unchecked")
    default Sequence<T> append(T... ts) {
        return SequenceConcatenated.of(this, SequenceFromArray.of(ts));
    }

    /** Add the given items at the beginning of this sequence. */
    @SuppressWarnings("unchecked")
    default Sequence<T> prepend(T... ts) { return SequenceConcatenated.of(SequenceFromArray.of(ts), this); }

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
      // When implementing, the innerSequence needs to call tail() on the parent sequence instead
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

    /**
     This implementation is unsynchronized.
     @return an unsynchronized iterator
     */
    @Override
    default UnIteratorOrdered<T> iterator() { return unIteratorOrdered(this); }

    /**
     This implementation is unsynchronized.
     @return an unsynchronized iterator
     */
    static <T> UnIteratorOrdered<T> unIteratorOrdered(Sequence<T> seq) {
        return new UnIteratorOrdered<T>() {
            private Sequence<T> inner = seq;

            @Override public boolean hasNext() { return inner.head().isSome(); }

            @Override public T next() {
                Option<T> next = inner.head();
                inner = inner.tail();
                return next.getOrElse(null);
            }
        };
    }

    // Use these methods on UnIterable instead.
//    // ==================================================== Static ====================================================
//
//    /** This is correct, but realizes the entire sequence (which may not terminate) and it's O(n) */
//    static int hashCode(Sequence seq) {
//        int ret = 0;
//        Option item = seq.head();
//        while (item.isSome()) {
//            Object i = item.get();
//            if (i != null) { ret = ret + i.hashCode(); }
//            seq = seq.tail();
//            item = seq.head();
//        }
//        return ret;
//    }
//
//    /** This is correct, but realizes the entire sequence (which may not terminate) and it's O(n) */
//    static boolean equals(Sequence a, Sequence b) {
//        // Cheapest operation first...
//        if (a == b) { return true; }
//
//        if ( (a == null) || (b == null) ) { return false; }
//
//        Option oa = a.head(); Option ob = b.head();
//        while (oa.isSome() && ob.isSome()) {
//            if (!Objects.equals(oa.get(), ob.get())) {
//                return false;
//            }
//            a = a.tail(); b = b.tail();
//            oa = a.head(); ob = b.head();
//        }
//        // Should both be used up (equal length).
//        return !oa.isSome() && !ob.isSome();
//    }

// Don't want this - call UnIterable.toString instead.
//    /** This is correct, but realizes some of the sequence, and it's O(n) */
//    static String toString(Sequence seq) {
//        return UnIterable.toString("Sequence", seq);
//    }

//    public class LazySequence<T> implements Sequence<T> {
//        private final LazyRef<Tuple2<T,Sequence<T>>> laz;
//
//        LazySequence(T first, Sequence<T> rest) {
//            laz = LazyRef.of(() -> Tuple2.of(first, rest));
//        }
//
//        public static <T> Sequence<T> of(T first, Sequence<T> rest) {
//            if ( (first == null) &&
//                 (Sequence.Empty.SEQUENCE == rest) ) { return Sequence.emptySequence(); }
//            return new LazySequence<>(first, rest);
//        }
//
//        @Override public Option<T> head() { return laz.get()._1(); }
//
//        @Override public Sequence<T> tail() { return laz.get()._2(); }
//    }

//    /**
//     Take a Function0 and lazily initialize a value (and frees the initialization resources) on the first call to get().
//     Subsequent calls to get() cheaply return the previously initialized value.  This class is thread-safe if the producer
//     function and the value it produces are pure and free from side effects.
//     */
//    class LazySeq<T> implements Sequence<T> {
//        private Function0<Tuple2<Option<T>,Sequence<T>>> producer;
//        private Option<T> head;
//        private Sequence<T> tail;
//
//        private LazySeq(Function0<Tuple2<Option<T>,Sequence<T>>> p) { producer = p; }
//
//        /**
//         * Use this function to produce a value on the first call to get().  Delete the pointer to this function when that
//         * first call completes, but remember the value to return with all subsequent calls to get().
//         * @param producer will produce the desired value when called.
//         * @param <T>
//         * @return
//         */
//        public static <T> LazySeq<T> of(Function0<Tuple2<Option<T>,Sequence<T>>> producer) {
//            if (producer == null) {
//                throw new IllegalArgumentException("The producer function cannot be null (the value it returns can)");
//            }
//            return new LazySeq<>(producer);
//        }
//
//        /**
//         * The first call to this method initializes the value this class wraps and releases the initialization resources.
//         * Subsequent calls return the precomputed value.
//         * @return the same value every time it is called.
//         */
//        private void get() {
//            // Have we produced our value yet (cheap, but not thread-safe check)?
//            if (producer != null) {
//                // One thread comes in here at a time, but this can be expensive.
//                synchronized (this) {
//                    // Checking again inside the sync block ensures only one thread can produce the value.
//                    if (producer != null) {
//                        // Here, a single thread has earned the right to produce our value.
//                        Tuple2<Option<T>,Sequence<T>> value = producer.apply();
//                        head = value._1();
//                        tail = value._2();
//                        // Delete the producer to 1. mark the work done and 2. free resources.
//                        producer = null;
//                    }
//                }
//            }
//            // We're clear to return the lazily computed value.
//        }
//        @Override public Option<T> head() { return head; }
//        @Override public Sequence<T> tail() { return tail; }
//
//    }
}