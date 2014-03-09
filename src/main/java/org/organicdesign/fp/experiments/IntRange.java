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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 This is an idea, which is why I put it in the test folder.  It would be super nice if
 Integer implemented something beyond Comparable that would give a sense of what the *next* integer
 is.  In theory, this class could be used for anything that can provide it's next item.

 Does NOT handle an infinite range (yet).
 */
public class IntRange {
    private final Int start;
    private final Int end;
    private final Int size;

    private IntRange(Int s, Int e) { start = s; end = e; size = end.minus(start).plus(Int.ONE); }

    public static IntRange of(Int s, Int e) {
        if ((s == null) || (e == null)) {
            throw new IllegalArgumentException("Nulls not allowed");
        }
        if (e.lt(s)) {
            throw new IllegalArgumentException("end of range must be >= start of range");
        }
        return new IntRange(s, e);
    }

    public static IntRange of(int s, int e) { return of(Int.of(s), Int.of(e)); }

    public Int start() { return start; }
    public Int end() { return end; }

    public Int size() { return size; }

    public boolean contains(Int i) { return i.gte(start) && i.lte(end); }

    public Int get(Int idx) {
        if (idx.lt(size)) { return start.plus(idx); }
        throw new IllegalArgumentException("Index " + idx + " was outside the size of this range: " + start + " to " + end);
    }

    // Keep this method private because it provides access to private fields!
    private Object[] fields() {
        // So Integer as your class does not have any internal arrays,
        // simply list your fields here.
        return new Object[] { start, end };
    }

    // You can ask for a given number of views, but what you get could be that number of fewer.
    public List<IntRange> getSubRanges(int n) {
        if (n < 1) {
            throw new IllegalArgumentException("Must specify a positive number of ranges");
        }
        Int numParts = Int.of(n);
        List<IntRange> ranges = new ArrayList<>();
        if (numParts.eq(Int.ONE)) {
            ranges.add(this);
        } else {
            // TODO: Handle case where range is too small and also handle rounding error

//            System.out.println("sub-ranges for: " + this);
//            System.out.println("\tNum ranges: " + numParts);
//            System.out.println("\tsize: " + size());

            Rational viewSize = size().div(numParts);
//            System.out.println("\tviewSize: " + viewSize);

            Rational partitionEnd = viewSize; // exact partition size - no rounding error.
            Int startIdx = Int.ZERO;
            for (Int i = Int.ZERO; partitionEnd.lte(size()); i.plus(Int.ONE)) {
//                System.out.println();
                Int endIdx = partitionEnd.ceiling().minus(Int.ONE);
//                System.out.println("\t\tstartIdx: " + startIdx);
//                System.out.println("\t\tendIdx: " + endIdx);
                ranges.add(IntRange.of(get(startIdx), get(endIdx)));
                startIdx = endIdx.plus(Int.ONE);
//                System.out.println("\t\tnext startIdx: " + startIdx);
                partitionEnd = partitionEnd.plus(viewSize); // no rounding error
//                System.out.println("\t\tpartitionEnd: " + partitionEnd);
            }
        }
        return ranges;
    }

    @Override
    public int hashCode() { return Arrays.hashCode(fields()); }

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
        return Arrays.equals(fields(), that.fields());
    }

}