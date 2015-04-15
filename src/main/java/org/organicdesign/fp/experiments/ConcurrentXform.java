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

import org.organicdesign.fp.Mutable;
import org.organicdesign.fp.experiments.collection.mutable.MutableLinkedList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConcurrentXform {
    private final int maxThreads;
    private final IntRange range;

    private ConcurrentXform(int t, IntRange r) { maxThreads = t; range = r; }

    public static ConcurrentXform of(int t, IntRange r) { return new ConcurrentXform(t, r); }

    public Long[] toTypedArray() {
        if (range.size() > (long) Integer.MAX_VALUE) {
            throw new IllegalStateException("size of range is too big for a Java array.");
        }
        Long[] ret = new Long[(int) range.size()];
        List<IntRange> ranges = range.getSubRanges(maxThreads);

        List<IntRange> idxRanges = IntRange.of(0, range.size() - 1).getSubRanges(maxThreads);

        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < ranges.size(); i++) {
            IntRange r = ranges.get(i);
            IntRange rIdx = idxRanges.get(i);
            if (i == (ranges.size() - 1)) {
                System.out.println("Running in current thread...");
                final Mutable.IntRef idx = Mutable.IntRef.of((int) rIdx.start());
                ViewFromIntRange.of(r).forEach(item -> {
                    // System.out.println("\tidx: " + idx.value() + " value: " + (Int) i);
                    ret[idx.value()] = item;
                    idx.increment();
                    return null;
                });
            } else {
                System.out.println("Starting thread: " + i);
                Thread t = new Thread() {
                    @Override
                    public void run() {
                        final Mutable.IntRef idx = Mutable.IntRef.of((int) rIdx.start());
                        ViewFromIntRange.of(r).forEach(item -> {
    //                        System.out.println("\tidx: " + idx.value() + " value: " + (Int) i);
                            ret[idx.value()] = item;
                            idx.increment();
                            return null;
                        });
                    }
                };
                threads.add(t);
                t.start();
            }
        }
        // Wait for 'em all to finish
        for (Thread t : threads) {
            // System.out.println("Joining thread...");
            try {
                t.join();
            } catch (InterruptedException tie) {
                ; // ignore.
            }
        }
        return ret;
    }

    public MutableLinkedList<Long> toLinkedList() {
        if (range.size() > (long) Integer.MAX_VALUE) {
            throw new IllegalStateException("size of range is too big for a Java array.");
        }
        List<MutableLinkedList<Long>> results = new ArrayList<>();

        List<IntRange> ranges = range.getSubRanges(maxThreads);

        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < ranges.size(); i++) {
            IntRange r = ranges.get(i);
            MutableLinkedList<Long> ll = new MutableLinkedList<>();
            results.add(ll);
            if (i == (ranges.size() - 1)) {
                System.out.println("Running in current thread...");
                ViewFromIntRange.of(r).forEach(ll::append);
            } else {
                System.out.println("Starting thread: " + i);
                Thread t = new Thread() {
                    @Override
                    public void run() {
                        ViewFromIntRange.of(r).forEach(ll::append);
                    }
                };
                threads.add(t);
                t.start();
            }
        }
        // Wait for 'em all to finish
        for (Thread t : threads) {
            // System.out.println("Joining thread...");
            try {
                t.join();
            } catch (InterruptedException tie) {
                ; // ignore.
            }
        }
        // Java LinkedList sucks!  Concatenate should return instantly, not take as long as
        // rebuilding it from scratch.
        System.out.println("Concatenating results...");
        MutableLinkedList<Long> ret = new MutableLinkedList<>();
        for (int i = 0; i < results.size(); i++) {
            ret.append(results.get(i));
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
