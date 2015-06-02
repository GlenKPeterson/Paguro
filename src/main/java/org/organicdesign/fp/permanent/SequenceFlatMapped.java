// Copyright 2015-04-06 PlanBase Inc. & Glen Peterson
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
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

class SequenceFlatMapped<T,U> implements Sequence<U> {
    private final LazyRef<Sequence<U>> laz;

    @SuppressWarnings("unchecked")
    private SequenceFlatMapped(Sequence<T> seq, Function1<? super T,Sequence<U>> f) {
        laz = LazyRef.of(() -> {
            final Option<T> first = seq.head();
            return first.isSome()
                    ? new SequenceConcatenated<>(f.apply(first.get()), new SequenceFlatMapped(seq.tail(), f))
                    : Sequence.emptySequence();
        });
    }

    @SuppressWarnings("unchecked")
    public static <T,U> Sequence<U> of(Sequence<T> seq, Function1<? super T,Sequence<U>> f) {
        if (f == null) { throw new IllegalArgumentException("Can't flatmap with a null function."); }
        // You can put nulls in, but you don't get nulls out.
        if ( (seq == null) || (EMPTY_SEQUENCE == seq) ) { return Sequence.emptySequence(); }
        // Is this comparison possible?
//        if (Function1.IDENTITY.equals(f)) { return (Sequence<U>) seq; }
        return new SequenceFlatMapped<>(seq, f);
    }

    @Override public Option<U> head() { return laz.get().head(); }

    @Override public Sequence<U> tail() { return laz.get().tail(); }

//    @Override public int hashCode() { return Sequence.hashCode(this); }
//
//    @Override public boolean equals(Object o) {
//        if (this == o) { return true; }
//        if ( (o == null) || !(o instanceof Sequence) ) { return false; }
//        return Sequence.equals(this, (Sequence) o);
//    }
//
//    @Override public String toString() {
//        return "SequenceFlatMapped(" + (laz.isRealizedYet() ? laz.get().head() : "*lazy*") + ",...)";
//    }
}
