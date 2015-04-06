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

import org.organicdesign.fp.Lazy;
import org.organicdesign.fp.Option;
import org.organicdesign.fp.function.Function1;

class SequenceFlatMapped<T,U> implements Sequence<U> {
    private final Lazy.Ref<Sequence<U>> laz;

    @SuppressWarnings("unchecked")
    SequenceFlatMapped(Sequence<T> seq, Function1<T,Sequence<U>> f) {
        laz = Lazy.Ref.of(() -> {
            final Option<T> first = seq.first();
            return first.isSome()
                    ? new SequenceConcatenated<>(f.apply(first.get()), new SequenceFlatMapped(seq.rest(), f))
                    : Sequence.emptySequence();
        });
    }

    @SuppressWarnings("unchecked")
    public static <T,U> Sequence<U> of(Sequence<T> seq, Function1<T,Sequence<U>> f) {
        // You can put nulls in, but you don't get nulls out.
        if (f == null) { return Sequence.emptySequence(); }
        // Is this comparison possible?
        if (Function1.IDENTITY.equals(f)) { return (Sequence<U>) seq; }
        if ( (seq == null) || (seq == EMPTY_SEQUENCE) ) { return Sequence.emptySequence(); }
        return new SequenceFlatMapped<>(seq, f);
    }

    @Override public Option<U> first() { return laz.get().first(); }

    @Override public Sequence<U> rest() { return laz.get().rest(); }
}
