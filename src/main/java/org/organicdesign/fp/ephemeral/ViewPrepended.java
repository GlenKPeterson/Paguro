// Copyright (c) 2014-03-07 PlanBase Inc. & Glen Peterson
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

// TODO: Rename to ViewConcatenated
class ViewPrepended<T> implements View<T> {
    private View<T> firstView;
    private final View<T> secondView;

    private ViewPrepended(View<T> pre, View<T> post) {
        firstView = pre; secondView = post;
    }

    @SuppressWarnings("unchecked")
    public static <T> View<T> of(Iterable<? extends T> pre, Iterable<? extends T> post) {
        // You can put nulls in, but you don't get nulls out.
        if ( (pre == null) || (pre == EMPTY_VIEW)) {
            if (post == null) { return View.emptyView(); }

            return (post instanceof View) ? (View<T>) post
                                          : View.ofIter(post);
        } else if ((post == null) || (post == EMPTY_VIEW)) {

            return (pre instanceof View) ? (View<T>) pre
                                         : View.ofIter(pre);
        }
        return new ViewPrepended<>((pre instanceof View) ? (View<T>) pre
                                                         : View.ofIter(pre),
                                   (post instanceof View) ? (View<T>) post
                                                          : View.ofIter(post));
    }

    @Override
    public Option<T> next() {
        if (firstView == EMPTY_VIEW) {
            return secondView.next();
        }
        Option<T> innerNext = firstView.next();
        if (!innerNext.isSome()) {
            firstView = View.emptyView();
            return secondView.next();
        }
        return innerNext;
    }
}
