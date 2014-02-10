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

import java.util.Iterator;

/**
 If you use the source iterator after passing it to this class then the behavior of this class
 will be undefined.  This class is immutable and memoized so that calling it repeatedly returns
 the same results.  Using an iterator changes it and it cannot be reset - calling it repeatedly
 uses it up.   Because of the hasNext() and next() functions, iterators are not thread-safe since
 one thread can get true from hasNext() while another uses the last item by calling next().

 As long as you do not touch the iterator after passing it to the constructor of this object, this
 object will present and immutable, lazy, memoized, thread-safe view of the underlying iterator.
 */
class SequenceFromIterator<T> extends SequenceAbstract<T> {

    private static final Object UNINITIALIZED = new Object();

    private final Iterator<T> iter;

    @SuppressWarnings("unchecked")
    private T first = (T) UNINITIALIZED;

    private Sequence<T> rest;

    SequenceFromIterator(Iterator<T> i) { iter = i; }

    public static <T> Sequence<T> of(Iterator<T> i) {
        if (i == null) { return emptySequence(); }
        return new SequenceFromIterator<>(i);
    }

    public static <T> Sequence<T> of(Iterable<T> i) {
        if (i == null) { return emptySequence(); }
        Iterator<T> iiter = i.iterator();
        if (iiter == null) { return emptySequence(); }
        return new SequenceFromIterator<>(iiter);
    }

    private synchronized void init() {
        if (first == UNINITIALIZED) {
            if (iter.hasNext()) {
                first = iter.next();
                rest = of(iter);
            } else {
                first = usedUp();
                rest = emptySequence();
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
        return rest;
    }

    @SuppressWarnings("unchecked")
    public static <T> SequenceFromIterator<T> emptySequence() {
        return (SequenceFromIterator<T>) EMPTY_SEQUENCE;
    }

    @SuppressWarnings("unchecked")
    public T usedUp() { return (T) USED_UP; }
}
