package org.organicdesign.fp.permanent;

import org.organicdesign.fp.Lazy;
import org.organicdesign.fp.Option;
import org.organicdesign.fp.tuple.Tuple2;

public class SequenceFromArray<T> implements Sequence<T> {
    private final Lazy.Ref<Tuple2<Option<T>,Sequence<T>>> laz;

    SequenceFromArray(int startIdx, T[] ts) {
        laz = Lazy.Ref.of(() -> Tuple2.of(Option.of(ts[startIdx]), from(startIdx + 1, ts)));
    }

    @SafeVarargs
    static <T> Sequence<T> of(T... i) {
        if ((i == null) || (i.length < 1)) { return Sequence.emptySequence(); }
        return new SequenceFromArray<>(0, i);
    }

    @SafeVarargs
    static <T> Sequence<T> from(int startIdx, T... i) {
        if ((i == null) || (i.length < 1) || (startIdx >= i.length) ) {
            return Sequence.emptySequence();
        }
        return new SequenceFromArray<>(startIdx, i);
    }

    @Override public Option<T> first() { return laz.get()._1(); }

    @Override public Sequence<T> rest() { return laz.get()._2(); }
}
