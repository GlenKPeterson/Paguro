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

import org.organicdesign.fp.function.Filter;

public class SequenceFiltered<T> extends SequenceAbstract<T> {
    private static final Object UNINITIALIZED = new Object();

    private Sequence<T> seq;

    private final Filter<T> filter;

    @SuppressWarnings("unchecked")
    private T first = (T) UNINITIALIZED;

//    @SuppressWarnings("unchecked")
//    private Sequence<T> rest = (Sequence<T>) UNINITIALIZED;

    private SequenceFiltered(Sequence<T> s, Filter<T> f) { seq = s; filter = f; }

    public static <T> Sequence<T> of(Sequence<T> s, Filter<T> f) {
        // You can put nulls in, but you don't get nulls out.
        if ( (f == null) || (f == Filter.REJECT) ) { return emptySequence(); }
        if (f == Filter.ACCEPT) { return s; }
        if ( (s == null) || (s == EMPTY_SEQUENCE) ) { return emptySequence(); }
        return new SequenceFiltered<>(s, f);
    }

    private synchronized void init() {
        if (first == UNINITIALIZED) {
            while (seq != EMPTY_SEQUENCE) {
                T result = seq.first();
                if (result == USED_UP) {
                    first = usedUp();
                    seq = emptySequence();
                    return;
                }

                if (filter.apply_(result)) {
                    first = result;
                    seq = of(seq.rest(), filter);
                    return;
                }

                // If we didn't find one, repeat with next element
                seq = seq.rest();
            }
        }
    }


    @Override
    public T first() {
        init();
        return first;
    }

    @Override
    public Sequence<T> rest() {
        init();
        // if initialized, seq has been replaced with the appropriate filtered sequence.
        return seq;
    }


    @SuppressWarnings("unchecked")
    public static <T> SequenceFiltered<T> emptySequence() {
        return (SequenceFiltered<T>) EMPTY_SEQUENCE;
    }

    @SuppressWarnings("unchecked")
    public static <T> T usedUp() { return (T) USED_UP; }
}