// Copyright 2014-01-21 PlanBase Inc. & Glen Peterson
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.organicdesign.fp.permanent;

import org.organicdesign.fp.Lazy;
import org.organicdesign.fp.function.Function1;
import org.organicdesign.fp.tuple.Tuple2;

public class SequenceFiltered<T> implements Sequence<T> {
    private final Lazy.Ref<Tuple2<T,Sequence<T>>> laz;

    SequenceFiltered(Sequence<T> s, Function1<T,Boolean> predicate) {
        laz = Lazy.Ref.of(() -> {
            Sequence<T> seq = s;
            while (Empty.SEQUENCE != seq) {
                T item = seq.first();
                if (predicate.apply(item)) {
                    Sequence<T> rest = seq.rest();
                    while ( (Empty.SEQUENCE != rest) && !predicate.apply(rest.first())) {
                        rest = rest.rest();
                    }
                    return Tuple2.of(item, (Empty.SEQUENCE == rest)
                                           ? rest
                                           : new SequenceFiltered<>(rest, predicate));
                }
                // If we didn't find one, repeat with next element
                seq = seq.rest();
            }
            return Sequence.emptySeqTuple();
        });
    }

    public static <T> Sequence<T> of(Sequence<T> s, Function1<T,Boolean> f) {
        if (f == null) { throw new IllegalArgumentException("Must provide a predicate"); }
        if (f == Function1.REJECT) { return Sequence.emptySequence(); }
        if ( (s == null) || (Empty.SEQUENCE == s) ) { return Sequence.emptySequence(); }
        if (f == Function1.ACCEPT) { return s; }

        Sequence<T> seq = s;
        while (!f.apply(seq.first())) {
            seq = seq.rest();
            if (Empty.SEQUENCE == seq) { return seq; }
        }
        return new SequenceFiltered<>(seq, f);
    }

    @Override
    public T first() { return laz.get()._1(); }

    @Override
    public Sequence<T> rest() { return laz.get()._2(); }
}