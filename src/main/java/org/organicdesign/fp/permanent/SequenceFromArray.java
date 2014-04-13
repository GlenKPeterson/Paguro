package org.organicdesign.fp.permanent;

import org.organicdesign.fp.Option;

public class SequenceFromArray<T> implements Sequence<T> {

    private final int idx;
    private final T[] items;
    private Option<T> first = null;
    private Sequence<T> rest;

    private SequenceFromArray(int startIdx, T[] ts) { idx = startIdx; items = ts; }

    @SafeVarargs
    static <T> Sequence<T> of(T... i) {
        if ((i == null) || (i.length < 1)) { return Sequence.emptySequence(); }
        return new SequenceFromArray<>(0, i);
    }

    @SafeVarargs
    static <T> Sequence<T> of(int startIdx, T... i) {
        if ((i == null) || (i.length < 1) || (startIdx >= i.length) ) {
            return Sequence.emptySequence();
        }
        return new SequenceFromArray<>(startIdx, i);
    }

    private synchronized void init() {
        if (first == null) {
            first = Option.of(items[idx]);
            rest = of(idx + 1, items);
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
        return rest;
    }
}
