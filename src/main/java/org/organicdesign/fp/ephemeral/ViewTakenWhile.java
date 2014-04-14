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

package org.organicdesign.fp.ephemeral;

import java.util.function.Predicate;

import org.organicdesign.fp.FunctionUtils;
import org.organicdesign.fp.Option;

public class ViewTakenWhile<T> implements View<T> {
    private final View<T> innerView;
    private final Predicate<T> pred;
    private boolean done = false;

    ViewTakenWhile(View<T> v, Predicate<T> p) { innerView = v; pred = p; }

    public static <T> View<T> of(View<T> v, Predicate<T> p) {
        if (p == null) { throw new IllegalArgumentException("Must provide a predicate"); }
        if ( (p == FunctionUtils.REJECT) ||
             (v == null) ||
             (v == EMPTY_VIEW) ) { return View.emptyView(); }
        if (p == FunctionUtils.ACCEPT) { return v; }
        return new ViewTakenWhile<>(v, p);
    }

    @Override
    public synchronized Option<T> next() {
        if (done) { return Option.none(); }
        Option<T> item = innerView.next();

        if ( !item.isSome() ||
             !pred.test(item.get()) ) {

            done = true;
            return Option.none();
        }
        return item;
    }

}
