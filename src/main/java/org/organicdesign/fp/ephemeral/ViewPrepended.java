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

class ViewPrepended<T> implements View<T> {
    private final View<T> originalView;

    private View<T> prependedView;

    private ViewPrepended(View<T> v, View<T> pv) {
        originalView = v; prependedView = pv;
    }

    @SuppressWarnings("unchecked")
    public static <T> View<T> of(View<T> v, View<T> pv) {
        // You can put nulls in, but you don't get nulls out.
        if ( (pv == null) || (pv == EMPTY_VIEW)) {
            if (v == null) { return View.emptyView(); }
            return v;
        } else if ((v == null) || (v == EMPTY_VIEW)) {
            return pv;
        }
        return new ViewPrepended<>(v, pv);
    }

    @Override
    public Option<T> next() {
        if (prependedView == EMPTY_VIEW) {
            return originalView.next();
        }
        Option<T> innerNext = prependedView.next();
        if (!innerNext.isSome()) {
            prependedView = View.emptyView();
            return originalView.next();
        }
        return innerNext;
    }
}
