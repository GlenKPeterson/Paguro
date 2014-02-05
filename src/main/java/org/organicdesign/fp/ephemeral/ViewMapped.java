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

import org.organicdesign.fp.function.Function1;

public class ViewMapped<T,U> extends ViewAbstract<U> {
    private final View<T> view;
    private final Function1<T,U> func;

    ViewMapped(View<T> v, Function1<T,U> f) { view = v; func = f; }

    @SuppressWarnings("unchecked")
    public static <T,U> View<U> of(View<T> v, Function1<T,U> f) {
        // You can put nulls in, but you don't get nulls out.
        if (f == null) { return emptyView(); }
        if (f == Function1.IDENTITY) { return (View<U>) v; }
        if ( (v == null) || (v == EMPTY_VIEW) ) { return emptyView(); }
        return new ViewMapped<>(v, f);
    }

    @Override
    public U next() {
        T item = view.next();
        if (item == USED_UP) { return usedUp(); }
        return func.apply_(item);
    }

    @SuppressWarnings("unchecked")
    public static <T,U> ViewMapped<T,U> emptyView() {
        return (ViewMapped<T,U>) EMPTY_VIEW;
    }

    @SuppressWarnings("unchecked")
    public U usedUp() { return (U) USED_UP; }
}
