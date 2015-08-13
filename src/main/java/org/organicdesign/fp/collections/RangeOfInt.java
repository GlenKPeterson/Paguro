// Copyright (c) 2014-03-08 PlanBase Inc. & Glen Peterson
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

package org.organicdesign.fp.collections;

import java.math.BigInteger;
import java.util.List;

/**
 An efficient (in both time and memory) implementation of List.  If you want to compare a RangeOfInt
 to generic List&lt;Integer&gt;, use RangeOfInt.LIST_EQUATOR so that the hashCodes will be
 compatible.

 A RangeOfInt is an indexed sequence of integers.  It currently assumes a step of 1.
 Like everything in Java and similar classes in Clojure, Python, and Scala, the iterator it produces
 is inclusive of the the start endpoint but exclusive of the end.

 In theory, a class like this could be made for anything that can provide it's next() and previous()
 item and a size.  To do that, Integer would need to implement something that defined what the
 next() and previous() values.  Currently limited to Integer.MIN_VALUE to Integer.MAX_VALUE.
 */
public class RangeOfInt implements UnmodList<Integer> {

    public static final Equator<List<Integer>> LIST_EQUATOR = new Equator<List<Integer>>() {
        /**
         This is an *inefficient* hash code for RangeOfInt, but it is compatible with List.
         @param integers the list
         @return a HashCode which is compatible with java.util.List of the same integers as
         a range.
         */
        @Override public int hash(List<Integer> integers) {
            return UnmodIterable.hashCode(integers);
        }

        @Override
        public boolean eq(List<Integer> o1, List<Integer> o2) {
            if (o1 == o2) { return true; }
            if ( (o1 == null) || (o2 == null) ) { return false; }
            if ((o1 instanceof RangeOfInt) && (o2 instanceof RangeOfInt)) {
                return o1.equals(o2);
            }
            return o1.size() == o2.size() &&
                   UnmodSortedIterable.equals(UnmodSortedIterable.castFromList(o1),
                                              UnmodSortedIterable.castFromList(o2));
        }
    };

    private final int start;
    private final int end;
    private final int size;

    private RangeOfInt(int s, int e) { start = s; end = e; size = (end - start); }

    public static RangeOfInt of(int s, int e) {
        if (e < s) {
            throw new IllegalArgumentException("end of range must be >= start of range");
        }
        return new RangeOfInt(s, e);
    }

    public static RangeOfInt of(Number s, Number e) {
        if ((s == null) || (e == null)) {
            throw new IllegalArgumentException("Nulls not allowed");
        }
        return new RangeOfInt(s.intValue(), e.intValue());
    }

//    public static RangeOfInt of(int s, int e) { return of((int) s, (int) e); }

//    public int start() { return start; }
//    public int end() { return end; }

    public boolean contains(int i) {
        return (i >= start) && (i < end);
    }

    @Override public boolean contains(Object o) {
        // Yuck.  Why couldn't we just have to deal with an Integer here?
        // As it stands, we attempt to prevent overflow/underflow.
        if (o instanceof Integer) {
            return contains(((Integer) o).intValue());
        } else if (o instanceof Long) {
            long l = (Long) o;
            return (l <= ((long) Integer.MAX_VALUE)) &&
                   (l >= ((long) Integer.MIN_VALUE)) &&
                   contains((int) l);
        } else if (o instanceof BigInteger) {
            try {
                // Throws an exception if it's more than 32 bits.
                return contains(((BigInteger) o).intValueExact());
            } catch (ArithmeticException ignore) {
                return false;
            }
        } else if (o instanceof Number) {
            // No way to check this one.  Does that mean the above are misleading?
            return contains(((Number) o).intValue());
        }
        return false;
    }

    @Override public Integer get(int idx) {
        if ( (idx >= 0) && (idx < size) ) { return start + idx; }
        throw new IndexOutOfBoundsException("Index " + idx +
                                            " was outside the size of this range: " + start +
                                            " to " + end);
    }

    /**
     Unlike most implementations of List, this method has excellent O(1) performance!
     {@inheritDoc}
     */
    @Override public int indexOf(Object o) {
        if (o instanceof Number) {
            int i = ((Number) o).intValue();
            if ( (i >= start) && (i < end) ) {
                return i - start;
            }
        }
        return -1;
    }

    /**
     Unlike most implementations of List, this method has excellent O(1) performance!
     {@inheritDoc}
     */
    @Override public int lastIndexOf(Object o) { return indexOf(o); }

    @Override public int size() { return size; }

    @Override
    public int hashCode() { return start + end; }

    @Override
    public boolean equals(Object other) {
        if (this == other) { return true; }
        if ( !(other instanceof RangeOfInt) ) { return false; }

        // Details...
        final RangeOfInt that = (RangeOfInt) other;
        // This is not a database object; compare "significant" fields here.
        return (this.start == that.start) &&
               (this.end == that.end);
    }

    /**
     {@inheritDoc}
     Iterates from start of range (inclusive) up-to, but excluding, the end of the range.
     I'm not sure this is a good idea, but Python, Clojure, Scala, and just about everything in
     Java expects similar behavior.
     */
    @Override public UnmodListIterator<Integer> listIterator(final int startIdx) {
        return new UnmodListIterator<Integer>() {
            int val = start + startIdx;
            int idx = startIdx;
            @Override public boolean hasNext() { return val < end; }
            @Override public Integer next() {
                idx = idx + 1;
                Integer t = val;
                val = val + 1;
                return t;
            }
            @Override public boolean hasPrevious() { return val > start; }
            @Override public Integer previous() {
                idx = idx - 1;
                val = val - 1;
                return val;
            }
            @Override public int nextIndex() { return idx; }
            @Override public int previousIndex() { return idx - 1; }
        };
    }

    /** {@inheritDoc} */
    @Override public RangeOfInt subList(int fromIndex, int toIndex) {
        if ( (fromIndex == 0) && (toIndex == size()) ) {
            return this;
        }
        return RangeOfInt.of(get(fromIndex), get(toIndex));
    }
}