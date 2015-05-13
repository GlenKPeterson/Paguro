// Copyright (c) 2014-03-16 PlanBase Inc. & Glen Peterson
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

package org.organicdesign.fp.experiments.collection.mutable;

public class MutableLinkedList<T> {
    private Cell<T> first = null;
    private Cell<T> last = first;
    private long size = 0;
    private int cellSize = 1;

    public MutableLinkedList() { }

    public MutableLinkedList<T> append(T item) {
        if (first == null) {
            Cell<T> c = new Cell<>(item, cellSize);
            first = c;
            last = c;
        } else if (last.numValues >= last.values.length) {
            // 512 is about 12 seconds
            // 256 is about 11.5 seconds
            // 128 10.5 seconds
            // 64  12 seconds
            if (cellSize < 128) {
                cellSize = cellSize << 1;
            }
            Cell<T> c = new Cell<>(item, cellSize);
            last.nextCell = c;
            last = c;
        } else {
            last.values[last.numValues] = item;
            last.numValues++;
        }
        size++;
        return this;
    }

    public MutableLinkedList<T> append(MutableLinkedList<T> ll) {
        if (first == null) {
            first = ll.first;
            last = ll.last;
            size = ll.size;
        } else {
            last.nextCell = ll.first;
            last = ll.last;
            size += ll.size;
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    public T[] toTypedArray() {
        if (size > Integer.MAX_VALUE) {
            throw new IllegalStateException("Too big to fit in an array");
        }
        T[] ret = (T[]) new Object[(int) size];
        Cell<T> c = first;
        int i = 0;
        while (c != null) {
            for (int j = 0; j < c.numValues; j++) {
                ret[i] = c.values[j];
                i++;
            }
            c = c.nextCell;
        }
        return ret;
    }

    private class Cell<T> {
        private final T[] values;
        private int numValues = 1;
        private Cell<T> nextCell = null;
        @SuppressWarnings("unchecked")
        private Cell(T t, int size) {
            values = (T[]) new Object[size];
            values[0] = t;
        }
    }
}
