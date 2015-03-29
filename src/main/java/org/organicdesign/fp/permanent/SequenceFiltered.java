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

import org.organicdesign.fp.Option;
import org.organicdesign.fp.function.Function1;

public class SequenceFiltered<T> implements Sequence<T> {
    private Sequence<T> seq;
    private final Function1<T,Boolean> predicate;
    private Option<T> first = null;

    private SequenceFiltered(Sequence<T> s, Function1<T,Boolean> f) { seq = s; predicate = f; }

    public static <T> Sequence<T> of(Sequence<T> s, Function1<T,Boolean> f) {
        if (f == null) { throw new IllegalArgumentException("Must provide a predicate"); }
        if (f == Function1.REJECT) { return Sequence.emptySequence(); }
        if (f == Function1.ACCEPT) { return s; }
        if ( (s == null) || (s == EMPTY_SEQUENCE) ) { return Sequence.emptySequence(); }
        return new SequenceFiltered<>(s, f);
    }

    private synchronized void init() {
        if (first == null) {
            while (seq != EMPTY_SEQUENCE) {
                Option<T> item = seq.first();
                if (!item.isSome()) {
                    break;
                }

                if (predicate.apply(item.get())) {
                    first = item;
                    seq = of(seq.rest(), predicate);
                    return;
                }

                // If we didn't find one, repeat with next element
                seq = seq.rest();
            }
            first = Option.none();
            seq = Sequence.emptySequence();
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
        // if initialized, seq has been replaced with the appropriate filtered sequence.
        return seq;
    }
}