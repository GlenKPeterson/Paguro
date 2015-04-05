package org.organicdesign.fp.permanent;

import org.organicdesign.fp.Lazy;
import org.organicdesign.fp.Option;
import org.organicdesign.fp.tuple.Tuple2;

public class SequenceTaken<T> implements Sequence<T> {
    private final Lazy.Ref<Tuple2<Option<T>,Sequence<T>>> laz;

    SequenceTaken(Sequence<T> v, long n) {
        laz = Lazy.Ref.of(() -> {
            Option<T> first = v.first();
            return Tuple2.of(
                    first,
                    (first.isSome())
                            ? SequenceTaken.of(v.rest(), n - 1)
                            : Sequence.emptySequence());
        });
    }

    public static <T> Sequence<T> of(Sequence<T> v, long numItems) {
        if (numItems == 0) { return Sequence.emptySequence(); }
        if (numItems < 0) { throw new IllegalArgumentException("Num items must be >= 0"); }
        return new SequenceTaken<>(v, numItems);
    }

    @Override public Option<T> first() { return laz.get()._1(); }

    @Override public Sequence<T> rest() { return laz.get()._2(); }
}
