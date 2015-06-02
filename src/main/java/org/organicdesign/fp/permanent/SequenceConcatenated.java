// Copyright (c) 2015-04-05 PlanBase Inc. & Glen Peterson
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

class SequenceConcatenated<T> implements Sequence<T> {
    private final LazyRef<Sequence<T>> laz;

    SequenceConcatenated(Sequence<T> preSeq, Sequence<T> postSeq) {
        laz = LazyRef.of(() -> {
            final Option<T> preFirst = preSeq.head();
            return preFirst.isSome()
                    ? new Sequence<T>() {
                        @Override public Option<T> head() { return preFirst; }
                        @Override public Sequence<T> tail() { return new SequenceConcatenated<>(preSeq.tail(), postSeq); }
                    }
                    : postSeq;
        });
    }

    public static <T> Sequence<T> of(Sequence<T> pre, Sequence<T> post) {
        // You can put nulls in, but you don't get nulls out.
        if ( (pre == null) || (EMPTY_SEQUENCE == pre)) {
            if (post == null) { return Sequence.emptySequence(); }
            return post;
        } else if ((post == null) || (EMPTY_SEQUENCE == post)) {
            return pre;
        }
        return new SequenceConcatenated<>(pre, post);
    }

    @Override public Option<T> head() { return laz.get().head(); }

    @Override public Sequence<T> tail() { return laz.get().tail(); }

//    @Override public int hashCode() { return Sequence.hashCode(this); }
//
//    @Override public boolean equals(Object o) {
//        if (this == o) { return true; }
//        if ( (o == null) || !(o instanceof Sequence) ) { return false; }
//        return Sequence.equals(this, (Sequence) o);
//    }
//
//    @Override public String toString() {
//        return "SequenceConcatenated(" + (laz.isRealizedYet() ? laz.get().head() : "*lazy*") + ",...)";
//    }
}
