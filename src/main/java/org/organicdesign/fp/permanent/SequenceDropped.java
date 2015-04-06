// Copyright 2015-04-05 PlanBase Inc. & Glen Peterson
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

public class SequenceDropped<T> implements Sequence<T> {
    private final Lazy.Ref<Sequence<T>> laz;

    SequenceDropped(Sequence<T> v, long n) {
        laz = Lazy.Ref.of(() -> {
            Sequence<T> seq = v;
            for (long i = n; i > 0; i--) {
                Option<T> first = seq.first();
                if (!first.isSome()) {
                    return Sequence.emptySequence();
                }
                seq = seq.rest();
            }
            return seq;
        });
    }

    public static <T> Sequence<T> of(Sequence<T> v, long numItems) {
        if (numItems < 0) { throw new IllegalArgumentException("You can only drop a non-negative number of items"); }
        if ( (v == null) || (v == EMPTY_SEQUENCE) ) { return Sequence.emptySequence(); }
        return new SequenceDropped<>(v, numItems);
    }

    @Override public Option<T> first() { return laz.get().first(); }

    @Override public Sequence<T> rest() { return laz.get().rest(); }
}
