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

import org.organicdesign.fp.Option;

/** A single-pass incremental transformer backed by a Java array. */
public class ViewFromArray<T> implements View<T> {

    private final T[] items;
    private int idx = 0;

    private ViewFromArray(T[] i) { items = i; }

    @SafeVarargs
    static <T> View<T> of(T... i) {
        if ((i == null) || (i.length < 1)) { return View.emptyView(); }
        return new ViewFromArray<>(i);
    }

    @Override
    public synchronized Option<T> next() {
        if (idx == items.length) {
            return Option.none();
        }
        // TODO: This is a clear 8% speed-up over the ++ operator.
        Option<T> ret = Option.of(items[idx]);
        idx = idx + 1;
        return ret;
//        return Option.of(items[idx++]); // return current idx then increment idx
    }
}
