package org.organicdesign.fp.permanent;

import org.organicdesign.fp.Lazy;
import org.organicdesign.fp.function.Function1;
import org.organicdesign.fp.tuple.Tuple2;

// Without using an option for first, it seems we have to look ahead one.
public class SequenceTakenWhile<T> implements Sequence<T> {
    private final Lazy.Ref<Tuple2<T,Sequence<T>>> laz;

    SequenceTakenWhile(Sequence<T> seq, Function1<T,Boolean> pred) {
        T first = seq.first();
        laz = Lazy.Ref.of(() -> {
            if (pred.apply(first)) {
                Sequence<T> rest = seq.rest();
                return Tuple2.of(first, ((Sequence.Empty.SEQUENCE == rest) || !pred.apply(rest.first()))
                                        ? Sequence.emptySequence()
                                        : new SequenceTakenWhile<>(rest, pred));
            }
            return Sequence.emptySeqTuple();
        });
    }

    public static <T> Sequence<T> of(Sequence<T> v, Function1<T,Boolean> p) {
        if (p == null) { throw new IllegalArgumentException("Must provide a predicate"); }
        if ( (p == Function1.REJECT) ||
             (v == null) ||
             (Sequence.Empty.SEQUENCE == v) ||
             !p.apply(v.first()) ) {
            return Sequence.emptySequence();
        }
        if (p == Function1.ACCEPT) { return v; }
        return new SequenceTakenWhile<>(v, p);
    }

    @Override public T first() { return laz.get()._1(); }

    @Override public Sequence<T> rest() { return laz.get()._2(); }
    }
