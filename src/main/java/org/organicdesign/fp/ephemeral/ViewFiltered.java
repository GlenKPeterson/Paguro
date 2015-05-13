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

class ViewFiltered<T> implements View<T> {

    private final View<T> view;

    private final Function1<? super T,Boolean> predicate;

    ViewFiltered(View<T> v, Function1<? super T,Boolean> f) { view = v; predicate = f; }

    public static <T> View<T> of(View<T> v, Function1<? super T,Boolean> f) {
        if (f == null) { throw new IllegalArgumentException("Must provide a predicate"); }
        if (f == Function1.REJECT) { return View.emptyView(); }
        if (f == Function1.ACCEPT) { return v; }
        if ( (v == null) || (v == EMPTY_VIEW) ) { return View.emptyView(); }
        return new ViewFiltered<>(v, f);
    }

    @Override
    public Option<T> next() {
        Option<T> item = view.next();
        while (item.isSome()) {
            if (predicate.apply(item.get())) { return item; }
            item = view.next();
        }
        return Option.none();
    }
}
