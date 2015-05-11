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

import org.organicdesign.fp.Option;
import org.organicdesign.fp.function.Function1;

class ViewMapped<T,U> implements View<U> {
    private final View<T> view;
    private final Function1<? super T,? extends U> func;

    ViewMapped(View<T> v, Function1<? super T,? extends U> f) { view = v; func = f; }

    @SuppressWarnings("unchecked")
    public static <T,U> View<U> of(View<T> v, Function1<? super T,? extends U> f) {
        // You can put nulls in, but you don't get nulls out.
        if (f == null) { return View.emptyView(); }
        if (f == Function1.IDENTITY) { return (View<U>) v; }
        if ( (v == null) || (v == EMPTY_VIEW) ) { return View.emptyView(); }
        return new ViewMapped<>(v, f);
    }

    @Override
    public Option<U> next() {
        Option<T> item = view.next();
        if (!item.isSome()) { return Option.none(); }
        return Option.of(func.apply(item.get()));
    }
}
