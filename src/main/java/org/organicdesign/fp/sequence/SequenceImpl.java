// Copyright 2014-01-20 PlanBase Inc. & Glen Peterson
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

package org.organicdesign.fp.sequence;

import org.organicdesign.fp.function.Filter;
import org.organicdesign.fp.function.Function0;
import org.organicdesign.fp.function.Function1;

public class SequenceImpl<T> implements Sequence<T> {

    private static final Object UNINITIALIZED = new Object();

    private Function0<T> func;
    private Sequence<T> rest;

    @SuppressWarnings("unchecked")
    private T first = (T) UNINITIALIZED;

    private SequenceImpl(Function0<T> f, Sequence<T> s) { func = f; rest = s; }

    public static <T> Sequence<T> of(Function0<T> f, Sequence<T> s) {
        // You can put nulls in, but you don't get nulls out.
        if (f == null) { return emptySequence(); }
        if (s == null) { s = emptySequence(); }
        return new SequenceImpl<>(f, s);
    }

    @Override
    public synchronized T first() {
        if (first == UNINITIALIZED) {
            first = func.apply_();
        }
        return first;
    }
    @Override
    public Sequence<T> rest() {
        return rest;
    }

    public <U> Sequence<U> map(final Function1<T,U> func) {
        return SequenceMapped.of(this, func);
    }

    public Sequence<T> filter(final Filter<T> filter) {
        return SequenceFiltered.of(this, filter);
    }

    @SuppressWarnings("unchecked")
    public static <T> SequenceImpl<T> emptySequence() { return (SequenceImpl<T>) Sequence.EMPTY_SEQUENCE; }

    @SuppressWarnings("unchecked")
    public static <T> T usedUp() { return (T) Sequence.USED_UP; }
}
