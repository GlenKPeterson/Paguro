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

import org.organicdesign.fp.LazyRef;
import org.organicdesign.fp.Option;
import org.organicdesign.fp.tuple.Tuple2;

public class SequenceTaken<T> implements Sequence<T> {
    private final LazyRef<Tuple2<Option<T>,Sequence<T>>> laz;

    private SequenceTaken(Sequence<T> v, long n) {
        laz = LazyRef.of(() -> {
            Option<T> first = v.head();
            return Tuple2.of(first,
                             (first.isSome()) ? SequenceTaken.of(v.tail(), n - 1)
                                              : Sequence.emptySequence());
        });
    }

    public static <T> Sequence<T> of(Sequence<T> v, long numItems) {
        if (numItems < 0) { throw new IllegalArgumentException("Num items must be >= 0"); }
        if ( (v == null) || (EMPTY_SEQUENCE == v) || (numItems == 0) ) { return Sequence.emptySequence(); }
        return new SequenceTaken<>(v, numItems);
    }

    @Override public Option<T> head() { return laz.get()._1(); }

    @Override public Sequence<T> tail() { return laz.get()._2(); }

//    @Override public int hashCode() { return Sequence.hashCode(this); }
//
//    @Override public boolean equals(Object o) {
//        if (this == o) { return true; }
//        if ( (o == null) || !(o instanceof Sequence) ) { return false; }
//        return Sequence.equals(this, (Sequence) o);
//    }
//
//    @Override public String toString() {
//        return "SequenceTaken(" + (laz.isRealizedYet() ? laz.get()._1() : "*lazy*") + ",...)";
//    }
}
