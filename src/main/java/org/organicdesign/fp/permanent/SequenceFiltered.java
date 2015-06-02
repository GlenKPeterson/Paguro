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

import org.organicdesign.fp.LazyRef;
import org.organicdesign.fp.Option;
import org.organicdesign.fp.function.Function1;
import org.organicdesign.fp.tuple.Tuple2;

public class SequenceFiltered<T> implements Sequence<T> {
    private final LazyRef<Tuple2<Option<T>,Sequence<T>>> laz;

    private SequenceFiltered(Sequence<T> s, Function1<? super T,Boolean> predicate) {
        laz = LazyRef.of(() -> {
            Sequence<T> seq = s;
            Option<T> item = seq.head();
            while (item.isSome()) {
                if (predicate.apply(item.get())) {
                    return Tuple2.of(item, new SequenceFiltered<>(seq.tail(), predicate));
                }
                // If we didn't find one, repeat with next element
                seq = seq.tail();
                item = seq.head();
            }
            return Sequence.emptySeqTuple();
        });
    }

    public static <T> Sequence<T> of(Sequence<T> s, Function1<? super T,Boolean> f) {
        if (f == null) { throw new IllegalArgumentException("Must provide a predicate"); }
        if ( (f == Function1.REJECT) || (s == null) || (EMPTY_SEQUENCE == s) ) { return Sequence.emptySequence(); }
        if (f == Function1.ACCEPT) { return s; }
        return new SequenceFiltered<>(s, f);
    }

    @Override
    public Option<T> head() { return laz.get()._1(); }

    @Override
    public Sequence<T> tail() { return laz.get()._2(); }

//    @Override public int hashCode() { return Sequence.hashCode(this); }
//
//    @Override public boolean equals(Object o) {
//        if (this == o) { return true; }
//        if ( (o == null) || !(o instanceof Sequence) ) { return false; }
//        return Sequence.equals(this, (Sequence) o);
//    }
//
//    @Override public String toString() {
//        return "SequenceFiltered(" + (laz.isRealizedYet() ? laz.get()._1() : "*lazy*") + ",...)";
//    }
}