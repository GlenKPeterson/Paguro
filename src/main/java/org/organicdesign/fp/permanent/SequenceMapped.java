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

public class SequenceMapped<T,U>  implements Sequence<U> {
    private final Lazy.Ref<Tuple2<U,Sequence<U>>> laz;

    SequenceMapped(Sequence<T> seq, Function1<T,U> func) {
        laz = Lazy.Ref.of(() -> (Sequence.Empty.SEQUENCE == seq)
                                ? Sequence.emptySeqTuple()
                                : Tuple2.of(func.apply(seq.first()), new SequenceMapped<>(seq.rest(), func)));
    }

    @SuppressWarnings("unchecked")
    public static <T,U> Sequence<U> of(Sequence<T> s, Function1<T,U> f) {
        // You can put nulls in, but you don't get nulls out.
        if (f == null) { return Sequence.emptySequence(); }
        if (f == Function1.IDENTITY) { return (Sequence<U>) s; }
        if ( (s == null) || (Sequence.Empty.SEQUENCE == s) ) { return Sequence.emptySequence(); }
        return new SequenceMapped<>(s, f);
    }

    @Override public U first() { return laz.get()._1(); }

    @Override public Sequence<U> rest() { return laz.get()._2(); }
}