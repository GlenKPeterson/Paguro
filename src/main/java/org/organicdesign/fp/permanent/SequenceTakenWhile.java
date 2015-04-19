package org.organicdesign.fp.permanent;

import org.organicdesign.fp.Lazy;
import org.organicdesign.fp.Option;
import org.organicdesign.fp.function.Function1;
import org.organicdesign.fp.tuple.Tuple2;

// Without using an option for first, it seems we have to look ahead one.
public class SequenceTakenWhile<T> implements Sequence<T> {
    private final Lazy.Ref<Tuple2<Option<T>,Sequence<T>>> laz;

    SequenceTakenWhile(Sequence<T> seq, Function1<T,Boolean> pred) {
        laz = Lazy.Ref.of(() -> {
            Option<T> first = seq.head();
            return (first.isSome() && pred.apply(first.get()))
                    ? Tuple2.of(first, new SequenceTakenWhile<>(seq.tail(), pred))
                    : Sequence.emptySeqTuple();
        });
    }

    public static <T> Sequence<T> of(Sequence<T> v, Function1<T,Boolean> p) {
        if (p == null) { throw new IllegalArgumentException("Must provide a predicate"); }
        if ( (p == Function1.REJECT) || (v == null) || (EMPTY_SEQUENCE == v) ) {
            return Sequence.emptySequence();
        }
        if (p == Function1.ACCEPT) { return v; }
        return new SequenceTakenWhile<>(v, p);
    }

    @Override public Option<T> head() { return laz.get()._1(); }

    @Override public Sequence<T> tail() { return laz.get()._2(); }
    }
