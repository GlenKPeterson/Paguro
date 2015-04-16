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

import org.organicdesign.fp.function.Function1;

import static org.organicdesign.fp.permanent.Sequence.Empty;

class SequenceFlatMapped {
//    private final Lazy.Ref<Sequence<U>> laz;

    // TODO: Totally eager and I don't know what to do about it without using Option.
    private static <T,U> Sequence<U> next(Sequence<T> seq, Function1<T,Sequence<U>> f) {
        if (Empty.SEQUENCE == seq) { return Sequence.emptySequence(); }
        Sequence<U> first = f.apply(seq.first());
        while (Empty.SEQUENCE == first) {
            seq = seq.rest();
            if (Empty.SEQUENCE == seq) { return Sequence.emptySequence(); }
            first = f.apply(seq.first());
        }
        Sequence<T> rest = seq.rest();
        if (Empty.SEQUENCE == rest) { return first; }

        Sequence<U> second = f.apply(rest.first());
        while (Empty.SEQUENCE == second) {
            rest = rest.rest();
            if (Empty.SEQUENCE == rest) { return first; }
            second = f.apply(rest.first());
        }

        if (Empty.SEQUENCE == rest.rest()) {
            return SequenceConcatenated.of(first, second);
        }

        // Does this recurse eagerly?
        return SequenceConcatenated.of(first, next(rest, f));
    }


//    @SuppressWarnings("unchecked")
//    SequenceFlatMapped(Sequence<T> seq, Function1<T,Sequence<U>> f) {
//        laz = Lazy.Ref.of(() -> next(seq, f));
//    }

    @SuppressWarnings("unchecked")
    public static <T,U> Sequence<U> of(Sequence<T> seq, Function1<T,Sequence<U>> f) {
        // You can put nulls in, but you don't get nulls out.
        if (f == null) { return Sequence.emptySequence(); }
        // Is this comparison possible?
        if (Function1.IDENTITY.equals(f)) { return (Sequence<U>) seq; }
        if ( (seq == null) || (Empty.SEQUENCE == seq) ) { return Sequence.emptySequence(); }

        return next(seq, f);
    }

//    @Override public U first() { return laz.get().first(); }
//
//    @Override public Sequence<U> rest() { return laz.get().rest(); }
}
