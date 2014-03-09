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

import org.organicdesign.fp.Mutable;

public class ConcurrentXform {
    private final int maxThreads;
    private final IntRange range;

    private ConcurrentXform(int t, IntRange r) { maxThreads = t; range = r; }

    public static ConcurrentXform of(int t, IntRange r) { return new ConcurrentXform(t, r); }

    public Int[] toArray() {
        Int[] ret = new Int[range.size().toPrimitiveInt()];
        List<IntRange> ranges = range.getSubRanges(maxThreads);

        List<IntRange> idxRanges = IntRange.of(Int.ZERO, range.size().minus(Int.ONE)).getSubRanges(maxThreads);

        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < ranges.size(); i++) {
            System.out.println("Starting thread: " + i);
            IntRange r = ranges.get(i);
            IntRange rIdx = idxRanges.get(i);
            Thread t = new Thread() {
                @Override
                public void run() {
                    final Mutable.IntRef idx = Mutable.IntRef.of(rIdx.start().toPrimitiveInt());
                    ViewFromIntRange.of(r).forEach(i -> {
//                        System.out.println("\tidx: " + idx.value() + " value: " + (Int) i);
                        ret[idx.value()] = (Int) i;
                        idx.increment();
                    });
                }
            };
            threads.add(t);
            t.start();
        }
        // Wait for 'em all to finish
        for (Thread t : threads) {
            System.out.println("Joining thread...");
            try {
                t.join();
            } catch (InterruptedException tie) {
                ; // ignore.
            }
        }
        return ret;
    }


    // Keep this method private because it provides access to private fields!
    private Object[] fields() {
        // So long as your class does not have any internal arrays,
        // simply list your fields here.
        return new Object[] { range, maxThreads };
    }

    @Override
    public int hashCode() { return Arrays.hashCode(fields()); }

    @Override
    public boolean equals(Object other) {
        // Cheapest operation first...
        if (this == other) { return true; }

        if ( (other == null) ||
             !(other instanceof ConcurrentXform) ||
             (this.hashCode() != other.hashCode()) ) {
            return false;
        }
        // Details...
        final ConcurrentXform that = (ConcurrentXform) other;
        // If this is not a database object, compare "significant" fields here.
        return Arrays.equals(fields(), that.fields());
    }
}
