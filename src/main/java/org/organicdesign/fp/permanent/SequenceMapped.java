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

public class SequenceMapped<T,U>  implements Sequence<U> {
    private final LazyRef<Tuple2<Option<U>,Sequence<U>>> laz;

    private SequenceMapped(Sequence<T> seq, Function1<? super T,? extends U> func) {
        laz = LazyRef.of(() -> {
            Option<T> first = seq.head();
            return first.isSome()
                   ? Tuple2.of(Option.of(func.apply(first.get())), new SequenceMapped<>(seq.tail(), func))
                   : Sequence.emptySeqTuple();
        });
    }

    @SuppressWarnings("unchecked")
    public static <T,U> Sequence<U> of(Sequence<T> s, Function1<? super T,? extends U> f) {
        if (f == null) { throw new IllegalArgumentException("Can't map with a null function."); }
        // You can put nulls in, but you don't get nulls out.
        if ( (s == null) || (EMPTY_SEQUENCE == s) ) { return Sequence.emptySequence(); }
        if (f == Function1.IDENTITY) { return (Sequence<U>) s; }
        return new SequenceMapped<>(s, f);
    }

    @Override public Option<U> head() { return laz.get()._1(); }

    @Override public Sequence<U> tail() { return laz.get()._2(); }

//    @Override public int hashCode() { return Sequence.hashCode(this); }
//
//    @Override public boolean equals(Object o) {
//        if (this == o) { return true; }
//        if ( (o == null) || !(o instanceof Sequence) ) { return false; }
//        return Sequence.equals(this, (Sequence) o);
//    }
//
//    @Override public String toString() {
//        return "SequenceMapped(" + (laz.isRealizedYet() ? laz.get()._1() : "*lazy*") + ",...)";
//    }
}