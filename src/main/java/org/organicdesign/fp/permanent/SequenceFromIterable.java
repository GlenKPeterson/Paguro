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

import org.organicdesign.fp.Option;

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
class SequenceFromIterable<T> implements Sequence<T> {
    private Iterator<? extends T> iter;
    private Option<T> head;
    private Sequence<T> tail;

    // This must always be private because it wraps an iterator
    // And we cannot accept an iterator from anyone but ourselves.
    private SequenceFromIterable(Iterator<? extends T> i) { iter = i; }

    // Just wrong.  You can't share an iterator with anyone else,
    // So only accept an Iterable, to guarantee that we can grab our own,
    // private iterator.
//    public static <T> Sequence<T> of(Iterator<T> i) {
//        if (i == null) { return Sequence.emptySequence(); }
//        return new SequenceFromIterable<>(i);
//    }

    public static <T> Sequence<T> of(Iterable<? extends T> i) {
        if (i == null) { return Sequence.emptySequence(); }
        Iterator<? extends T> iter = i.iterator();
        if ( (iter == null) || !iter.hasNext() ) { return Sequence.emptySequence(); }
        return new SequenceFromIterable<>(iter);
    }

    // This is faster than wrapping a LazyRef (almost 2x), or a separate private synchrnoized
    // init() method (about 10%).
    @Override public synchronized Option<T> head() {
        if (tail == null) {
            head = Option.of(iter.next());
            if (iter.hasNext()) {
                tail = new SequenceFromIterable<>(iter);
            } else {
                tail = Sequence.emptySequence();
            }
            // Release resources.
            iter = null;
        }
        return head;
    }

    @Override public Sequence<T> tail() {
        // This does all the initialization.
        head();
        return tail;
    }

    // You can't get a hashcode or equals for something backed by an iterator because doing so is
    // eager (not lazy) and also changes the state of the iterator
}
