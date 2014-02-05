// Copyright 2014-01-08 PlanBase Inc. & Glen Peterson
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

import org.organicdesign.fp.function.Filter;

public class ViewFiltered<T> extends ViewAbstract<T> {

    private final View<T> view;

    private final Filter<T> filter;

    ViewFiltered(View<T> v, Filter<T> f) { view = v; filter = f; }

    public static <T> View<T> of(View<T> v, Filter<T> f) {
        if ( (f == null) || (f == Filter.REJECT) ) { return emptyView(); }
        if (f == Filter.ACCEPT) { return v; }
        if ( (v == null) || (v == EMPTY_VIEW) ) { return emptyView(); }
        return new ViewFiltered<>(v, f);
    }

    @Override
    public T next() {
        T item = view.next();
        while (item != USED_UP) {
            if (filter.apply_(item)) { return item; }
            item = view.next();
        }
        return usedUp();
    }

    @SuppressWarnings("unchecked")
    public static <T> ViewFiltered<T> emptyView() {
        return (ViewFiltered<T>) EMPTY_VIEW;
    }

    @SuppressWarnings("unchecked")
    public T usedUp() { return (T) USED_UP; }
}
