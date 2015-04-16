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

package org.organicdesign.fp.permanent;

import org.organicdesign.fp.Lazy;
import org.organicdesign.fp.tuple.Tuple2;

import java.util.Iterator;

// TODO: Generate tests!
/**
 If you use the source iterator after passing it to this class then the behavior of this class
 will be undefined.  This class is immutable and memoized so that calling it repeatedly returns
 the same results.  Using an iterator changes it and it cannot be reset - calling it repeatedly
 uses it up.   Because of the hasNext() and next() functions, iterators are not thread-safe since
 one thread can get true from hasNext() while another uses the last item by calling next().

 As long as you do not touch the iterator after passing it to the constructor of this object, this
 object will present and immutable, lazy, memoized, thread-safe view of the underlying iterator.
 */
class SequenceFromIterator<T> implements Sequence<T> {
    private final Lazy.Ref<Tuple2<T,Sequence<T>>> laz;

    SequenceFromIterator(Iterator<T> iter) {
        laz = Lazy.Ref.of(() -> iter.hasNext()
                ? Tuple2.of(iter.next(), iter.hasNext()
                                         ? new SequenceFromIterator<>(iter)
                                         : Sequence.emptySequence())
                : Sequence.emptySeqTuple());
    }

    public static <T> Sequence<T> of(Iterator<T> i) {
        if ( (i == null) || !i.hasNext() ) { return Sequence.emptySequence(); }
        return new SequenceFromIterator<>(i);
    }

    public static <T> Sequence<T> of(Iterable<T> i) {
        if (i == null) { return Sequence.emptySequence(); }
        Iterator<T> iiter = i.iterator();
        if (iiter == null) { return Sequence.emptySequence(); }
        return new SequenceFromIterator<>(iiter);
    }

    @Override public T first() { return laz.get()._1(); }

    @Override public Sequence<T> rest() { return laz.get()._2(); }
}
