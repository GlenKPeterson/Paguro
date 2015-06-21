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

import org.organicdesign.fp.Option;

public class SequenceFromArray<T> implements Sequence<T> {
    private T[] theArray;
    private int idx;
    private Sequence<T> next;

    private SequenceFromArray(int i, T[] ts) {
        theArray = ts; idx = i;
    }

    @SafeVarargs
    static <T> Sequence<T> of(T... ts) {
        if ((ts == null) || (ts.length < 1)) { return Sequence.emptySequence(); }
        return new SequenceFromArray<>(0, ts);
    }

    @SafeVarargs
    static <T> Sequence<T> from(int startIdx, T... ts) {
        if (startIdx < 0) { throw new IllegalArgumentException("Start index must be >= 0"); }
        if ( (ts == null) || (ts.length < 1) ) {
            return Sequence.emptySequence();
        }
        if (startIdx >= ts.length) {
            throw new IllegalArgumentException("Start index must be < array length");
        }
        return new SequenceFromArray<>(startIdx, ts);
    }

    @Override public Option<T> head() { return Option.of(theArray[idx]); }

    // This whole method is synchronized on the advice of Goetz2006 p. 347
    @Override public synchronized Sequence<T> tail() {
        if (next == null) {
            int nextIdx = idx + 1;
            if (nextIdx < theArray.length) {
                next = new SequenceFromArray<>(nextIdx, theArray);
            } else {
                next = Sequence.emptySequence();
            }
        }
        return next;
    }
}
