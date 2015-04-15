// Copyright 2015-04-12 PlanBase Inc. & Glen Peterson
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
import org.organicdesign.fp.tuple.Tuple2;

public class SequenceTaken<T> implements Sequence<T> {
    private final Lazy.Ref<Tuple2<T,Sequence<T>>> laz;

    SequenceTaken(Sequence<T> v, long n) {
        laz = Lazy.Ref.of(() -> Tuple2.of(v.first(),
                                          ((n == 1) || (Empty.SEQUENCE == v.rest()))
                                          ? Sequence.emptySequence()
                                          : new SequenceTaken<>(v.rest(), n - 1)));
    }

    public static <T> Sequence<T> of(Sequence<T> v, long numItems) {
        if (numItems < 1) { throw new IllegalArgumentException("Num items must be >= 1"); }
        return new SequenceTaken<>(v, numItems);
    }

    @Override public T first() { return laz.get()._1(); }

    @Override public Sequence<T> rest() { return laz.get()._2(); }
}
