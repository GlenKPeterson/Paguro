package org.organicdesign.fp.permanent;

import org.organicdesign.fp.Lazy;
import org.organicdesign.fp.Option;

public class SequenceDropped<T> implements Sequence<T> {
    private final Lazy.Ref<Sequence<T>> laz;

    SequenceDropped(Sequence<T> v, long n) {
        laz = Lazy.Ref.of(() -> {
            Sequence<T> seq = v;
            for (long i = n; i > 0; i--) {
                Option<T> first = seq.first();
                if (!first.isSome()) {
                    return Sequence.emptySequence();
                }
                seq = seq.rest();
            }
            return seq;
        });
    }

    public static <T> Sequence<T> of(Sequence<T> v, long numItems) {
        if (numItems < 0) { throw new IllegalArgumentException("You can only drop a non-negative number of items"); }
        if ( (v == null) || (v == EMPTY_SEQUENCE) ) { return Sequence.emptySequence(); }
        return new SequenceDropped<>(v, numItems);
    }

    @Override public Option<T> first() { return laz.get().first(); }

    @Override public Sequence<T> rest() { return laz.get().rest(); }
}
