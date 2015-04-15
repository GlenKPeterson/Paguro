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

import org.organicdesign.fp.Lazy;

class SequenceConcatenated<T> implements Sequence<T> {
    private final Lazy.Ref<Sequence<T>> laz;

    SequenceConcatenated(Sequence<T> preSeq, Sequence<T> postSeq) {
        laz = Lazy.Ref.of(() -> (Empty.SEQUENCE == preSeq) ? postSeq : new Sequence<T>() {
                @Override public T first() { return preSeq.first(); }
                @Override public Sequence<T> rest() { return new SequenceConcatenated<>(preSeq.rest(), postSeq); }
        });
    }

    public static <T> Sequence<T> of(Sequence<T> pre, Sequence<T> post) {
        // You can put nulls in, but you don't get nulls out.
        if ( (pre == null) || (Empty.SEQUENCE == pre)) {
            if (post == null) { return Sequence.emptySequence(); }
            return post;
        } else if ((post == null) || (Empty.SEQUENCE == post)) {
            return pre;
        }
        return new SequenceConcatenated<>(pre, post);
    }

    @Override public T first() { return laz.get().first(); }

    @Override public Sequence<T> rest() { return laz.get().rest(); }
}
