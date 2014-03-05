// Copyright 2014-02-09 PlanBase Inc. & Glen Peterson
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

package org.organicdesign.fp.ephemeral;

import org.organicdesign.fp.Transformable;

/** A single-pass incremental transformer backed by a Java array. */
public class ViewFromArray<T> implements View<T> {

    private final T[] items;
    private int idx = 0;

    ViewFromArray(T[] i) { items = i; }

    @SafeVarargs
    public static <T> View<T> of(T... i) {
        if (i == null) { return View.emptyView(); }
        return new ViewFromArray<>(i);
    }

    @Override
    public synchronized T next() {
        if (idx == items.length) {
            return Transformable.usedUp();
        }
//        T ret = items[idx];
//        idx = idx + 1;
//        return ret;
        return items[idx++]; // return current idx then increment idx
    }
}
