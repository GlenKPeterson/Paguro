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

public class SequenceFromArray<T> implements Sequence<T> {
    private final LazyRef<Tuple2<Option<T>,Sequence<T>>> laz;

    // TODO: Develop tests for this and test for what happens when idx > ts.length or idx < 0;
    private SequenceFromArray(int idx, T[] ts) {
        laz = LazyRef.of(() -> Tuple2.of(Option.of(ts[idx]), (idx == (ts.length - 1))
                                                              ? Sequence.emptySequence()
                                                              : new SequenceFromArray<>(idx + 1, ts)));
    }

    @SafeVarargs
    static <T> Sequence<T> of(T... i) {
        if ((i == null) || (i.length < 1)) { return Sequence.emptySequence(); }
        return new SequenceFromArray<>(0, i);
    }

    @SafeVarargs
    static <T> Sequence<T> from(int startIdx, T... i) {
        if (startIdx < 0) { throw new IllegalArgumentException("Start index must be >= 0"); }
        if ( (i == null) || (i.length < 1) || (startIdx >= i.length) ) {
            return Sequence.emptySequence();
        }
        return new SequenceFromArray<>(startIdx, i);
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
//        return "SequenceFromArray(" + (laz.isRealizedYet() ? laz.get()._1() : "*lazy*") + ",...)";
//    }
}
