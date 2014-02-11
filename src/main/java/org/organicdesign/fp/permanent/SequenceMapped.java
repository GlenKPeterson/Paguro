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

import org.organicdesign.fp.function.Function;

public class SequenceMapped<T,U>  extends SequenceAbstract<U> {
    private final Sequence<T> seq;
    private final Function<T,U> func;

    private SequenceMapped(Sequence<T> s, Function<T,U> f) { seq = s; func = f; }

    @SuppressWarnings("unchecked")
    public static <T,U> Sequence<U> of(Sequence<T> s, Function<T,U> f) {
        // You can put nulls in, but you don't get nulls out.
        if (f == null) { return emptySequence(); }
        if (f == Function.IDENTITY) { return (Sequence<U>) s; }
        if ( (s == null) || (s == EMPTY_SEQUENCE) ) { return emptySequence(); }
        return new SequenceMapped<>(s, f);
    }

    @Override
    public U first() {
        return func.apply_(seq.first());
    }

    @Override
    public Sequence<U> rest() {
        return of(seq.rest(), func);
    }

    @SuppressWarnings("unchecked")
    public static <T,U> SequenceMapped<T,U> emptySequence() {
        return (SequenceMapped<T,U>) EMPTY_SEQUENCE;
    }
}