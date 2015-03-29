package org.organicdesign.fp.permanent;

import org.organicdesign.fp.Option;
import org.organicdesign.fp.function.Function1;

public class SequenceTakenWhile<T> implements Sequence<T> {
    private Sequence<T> innerSequence;
    private final Function1<T,Boolean> pred;
    private Option<T> first = null;

    SequenceTakenWhile(Sequence<T> v, Function1<T,Boolean> p) { innerSequence = v; pred = p; }

    public static <T> Sequence<T> of(Sequence<T> v, Function1<T,Boolean> p) {
        if (p == null) { throw new IllegalArgumentException("Must provide a predicate"); }
        if ( (p == Function1.REJECT) ||
             (v == null) ||
             (v == EMPTY_SEQUENCE) ) { return Sequence.emptySequence(); }
        if (p == Function1.ACCEPT) { return v; }
        return new SequenceTakenWhile<>(v, p);
    }

    private synchronized void init() {
        if (first == null) {
            first = innerSequence.first();
            if ( first.isSome() &&
                 pred.apply(first.get()) ) {
                innerSequence = SequenceTakenWhile.of(innerSequence.rest(), pred);
            } else {
                first = Option.none();
                innerSequence = Sequence.emptySequence();
            }
        }
    }

    @Override
    public Option<T> first() {
        init();
        return first;
    }

    @Override
    public Sequence<T> rest() {
        init();
        return innerSequence;
    }
}
