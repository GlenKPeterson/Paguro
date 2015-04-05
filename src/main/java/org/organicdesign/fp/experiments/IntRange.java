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

package org.organicdesign.fp.experiments;

import org.organicdesign.fp.collections.UnIterable;
import org.organicdesign.fp.collections.UnIterator;
import org.organicdesign.fp.experiments.math.Rational;

import java.util.ArrayList;
import java.util.List;

/**
 This is an idea, which is why I put it in the test folder.  It would be super nice if
 Integer implemented something beyond Comparable that would give a sense of what the *next* integer
 is.  In theory, this class could be used for anything that can provide it's next item.

 Does NOT handle an infinite range (yet).
 */
public class IntRange implements UnIterable<Long> {
    private final long start;
    private final long end;
    private final long size;

    private IntRange(long s, long e) { start = s; end = e; size = (end - start) + 1; }

    public static IntRange of(long s, long e) {
        if (e < s) {
            throw new IllegalArgumentException("end of range must be >= start of range");
        }
        return new IntRange(s, e);
    }

    public static IntRange of(Long s, Long e) {
        if ((s == null) || (e == null)) {
            throw new IllegalArgumentException("Nulls not allowed");
        }
        return new IntRange(s.longValue(), e.longValue());
    }

    public static IntRange of(int s, int e) { return of((long) s, (long) e); }

    public long start() { return start; }
    public long end() { return end; }

    public long size() { return size; }

    public boolean contains(long i) { return (i >= start) && (i <= end); }

    public long get(long idx) {
        if (idx < size) { return start + idx; }
        throw new IllegalArgumentException("Index " + idx + " was outside the size of this range: " + start + " to " + end);
    }

    // You can ask for a given number of views, but what you get could be that number of fewer.
    public List<IntRange> getSubRanges(int n) {
        if (n < 1) {
            throw new IllegalArgumentException("Must specify a positive number of ranges");
        }
        long numParts = (long) n;
        List<IntRange> ranges = new ArrayList<>();
        if (numParts == 1) {
            ranges.add(this);
        } else {
            // TODO: Handle case where range is too small and also handle rounding error

//            System.out.println("sub-ranges for: " + this);
//            System.out.println("\tNum ranges: " + numParts);
//            System.out.println("\tsize: " + size());

            Rational viewSize = Rational.of(size(), numParts);
//            System.out.println("\tviewSize: " + viewSize);

            Rational partitionEnd = viewSize; // exact partition size - no rounding error.
            long startIdx = 0;
            for (long i = 0; partitionEnd.lte(size()); i++) {
//                System.out.println();
                long endIdx = partitionEnd.ceiling() - 1;
//                System.out.println("\t\tstartIdx: " + startIdx);
//                System.out.println("\t\tendIdx: " + endIdx);
                ranges.add(IntRange.of(get(startIdx), get(endIdx)));
                startIdx = endIdx + 1;
//                System.out.println("\t\tnext startIdx: " + startIdx);
                partitionEnd = partitionEnd.plus(viewSize); // no rounding error
//                System.out.println("\t\tpartitionEnd: " + partitionEnd);
            }
        }
        return ranges;
    }

    @Override
    public int hashCode() { return (int) (start + end); }

    @Override
    public boolean equals(Object other) {
        // Cheapest operation first...
        if (this == other) { return true; }

        if ( (other == null) ||
             !(other instanceof IntRange) ||
             (this.hashCode() != other.hashCode()) ) {
            return false;
        }
        // Details...
        final IntRange that = (IntRange) other;
        // If this is not a database object, compare "significant" fields here.
        return (this.start == that.start) &&
                (this.end == that.end);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UnIterator<Long> iterator() {
        return new UnIterator<Long>() {
            long s = start;
            @Override public boolean hasNext() { return s < end; }
            @Override public Long next() { s = s + 1; return s; }
        };
    }
}