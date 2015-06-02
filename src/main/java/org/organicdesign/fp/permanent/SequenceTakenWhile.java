package org.organicdesign.fp.permanent;

import org.organicdesign.fp.LazyRef;
import org.organicdesign.fp.Option;
import org.organicdesign.fp.function.Function1;
import org.organicdesign.fp.tuple.Tuple2;

// Without using an option for first, it seems we have to look ahead one.
public class SequenceTakenWhile<T> implements Sequence<T> {
    private final LazyRef<Tuple2<Option<T>,Sequence<T>>> laz;

    private SequenceTakenWhile(Sequence<T> seq, Function1<? super T,Boolean> pred) {
        laz = LazyRef.of(() -> {
            Option<T> first = seq.head();
            return (first.isSome() && pred.apply(first.get()))
                    ? Tuple2.of(first, new SequenceTakenWhile<>(seq.tail(), pred))
                    : Sequence.emptySeqTuple();
        });
    }

    public static <T> Sequence<T> of(Sequence<T> v, Function1<? super T,Boolean> p) {
        if (p == null) { throw new IllegalArgumentException("Must provide a predicate"); }
        if ( (p == Function1.REJECT) || (v == null) || (EMPTY_SEQUENCE == v) ) {
            return Sequence.emptySequence();
        }
        if (p == Function1.ACCEPT) { return v; }
        return new SequenceTakenWhile<>(v, p);
    }

    @Override public Option<T> head() { return laz.get()._1(); }

    @Override public Sequence<T> tail() { return laz.get()._2(); }

//    @Override public int hashCode() { return Sequence.hashCode(this); }
//
//    @Override public boolean equals(Object o) {
//        if (this == o) { return true; }
//        if ( (o == null) || !(o instanceof Sequence) ) { return false; }
//        return Sequence.equals(this, (Sequence) o);
//    }
//
//    @Override public String toString() {
//        return "SequenceTakenWhile(" + (laz.isRealizedYet() ? laz.get()._1() : "*lazy*") + ",...)";
//    }
}