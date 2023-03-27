package org.organicdesign.fp.collections;

import java.io.Serializable;
import java.util.NoSuchElementException;

public class ListOperator implements Serializable {

    private int start;
    private int end;
    private int size;

    ListOperator(int start, int end, int size){
        this.start = start;
        this.end = end;
        this.size = size;
    }


    public UnmodListIterator<Integer> Iterator(int startIdx) {
        if ((startIdx < 0) || (startIdx > size)){
            // To match ArrayList and other java.util.List expectations
            throw new IndexOutOfBoundsException("Index: " + startIdx);
        }
        return new UnmodListIterator<Integer>() {
            int val = start + startIdx;

            public boolean hasNext() {
                return val < end;
            }

            public Integer next() {
                if (val >= end) {
                    // To match ArrayList and other java.util.List expectations
                    throw new NoSuchElementException();
                }
                Integer t = val;
                val = val + 1;
                return t;
            }

            public boolean hasPrevious() {
                return val > start;
            }

            public Integer previous() {
                if (val <= start) {
                    // To match ArrayList and other java.util.List expectations
                    throw new NoSuchElementException();
                }
                val = val - 1;
                return val;
            }

            public int nextIndex() {
                return val - start;
            }
        };
    }


    public RangeOfInt subList(int fromIndex, int toIndex, RangeOfInt ROF) {
        if ((fromIndex == 0) && (toIndex == size)) {
            return ROF;
        }
        // Note that this is an IllegalArgumentException, not IndexOutOfBoundsException in order to
        // match ArrayList.
        if (fromIndex > toIndex) {
            throw new IllegalArgumentException("fromIndex(" + fromIndex + ") > toIndex(" + toIndex +
                    ")");
        }
        // The text of this matches ArrayList
        if (fromIndex < 0) {
            throw new IndexOutOfBoundsException("fromIndex = " + fromIndex);
        }
        if (toIndex > size) {
            throw new IndexOutOfBoundsException("toIndex = " + toIndex);
        }

        // Look very closely at the second parameter because the bounds checking is *different*
        // from the get() method.  get(toIndex) can throw an exception if toIndex >= start+size.
        // But since a range is exclusive of it's right-bound, we can create a new sub-range
        // with a right bound index of size, as opposed to size minus 1.  I spent hours
        // understanding this before fixing a bug with it.  In the end, subList should do the same
        // thing on a Range that it does on the equivalent ArrayList.  I made tests for the same.
        return RangeOfInt.of(start + fromIndex, start + toIndex);
    }
}
