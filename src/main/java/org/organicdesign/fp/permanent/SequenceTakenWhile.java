package org.organicdesign.fp.permanent;

import java.util.function.Predicate;

import org.organicdesign.fp.FunctionUtils;
import org.organicdesign.fp.Option;

public class SequenceTakenWhile<T> implements Sequence<T> {
    private Sequence<T> innerSequence;
    private final Predicate<T> pred;
    private Option<T> first = null;

    SequenceTakenWhile(Sequence<T> v, Predicate<T> p) { innerSequence = v; pred = p; }

    public static <T> Sequence<T> of(Sequence<T> v, Predicate<T> p) {
        if (p == null) { throw new IllegalArgumentException("Must provide a predicate"); }
        if ( (p == FunctionUtils.REJECT) ||
             (v == null) ||
             (v == EMPTY_SEQUENCE) ) { return Sequence.emptySequence(); }
        if (p == FunctionUtils.ACCEPT) { return v; }
        return new SequenceTakenWhile<>(v, p);
    }

    private synchronized void init() {
        if (first == null) {
            first = innerSequence.first();
            if ( first.isSome() &&
                 pred.test(first.get()) ) {
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
