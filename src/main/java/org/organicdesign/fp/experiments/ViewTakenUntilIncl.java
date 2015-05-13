// Copyright 2014-04-13 PlanBase Inc. & Glen Peterson
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

package org.organicdesign.fp.experiments;

import org.organicdesign.fp.Option;
import org.organicdesign.fp.ephemeral.View;
import org.organicdesign.fp.function.Function1;

public class ViewTakenUntilIncl<T> implements View<T> {
    private final View<T> innerView;
    private final Function1<T,Boolean> pred;
    private boolean done = false;

    ViewTakenUntilIncl(View<T> v, Function1<T,Boolean> p) { innerView = v; pred = p; }

    public static <T> View<T> of(View<T> v, Function1<T,Boolean> p) {
        if (p == null) { throw new IllegalArgumentException("Must provide a predicate"); }
        if ( (p == Function1.REJECT) ||
             (v == null) ||
             (v == EMPTY_VIEW) ) { return View.emptyView(); }
        if (p == Function1.ACCEPT) { return v; }
        return new ViewTakenUntilIncl<>(v, p);
    }

    @Override
    public synchronized Option<T> next() {
        if (done) { return Option.none(); }
        Option<T> item = innerView.next();

        if ( !item.isSome() ||
             pred.apply(item.get()) ) {
            done = true;
        }
        return item;
    }

}
