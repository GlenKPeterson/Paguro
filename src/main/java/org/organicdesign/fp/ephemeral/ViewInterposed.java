// Copyright 2014-02-16 PlanBase Inc. & Glen Peterson
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

public class ViewInterposed<T> extends View<T> {
    private final View<T> outerView;

    private final T item;

    private boolean showItem = false;

    ViewInterposed(View<T> v, T i) { outerView = v; item = i; }

    @SuppressWarnings("unchecked")
    public static <T> View<T> of(View<T> v, T i) {
        if ( (v == null) || (v == EMPTY_VIEW) ) { return emptyView(); }
        return new ViewInterposed<>(v, i);
    }

    @Override
    public synchronized T next() {
        if (showItem) {
            showItem = false;
            return item;
        }
        showItem = true;
        return outerView.next();
    }

    @SuppressWarnings("unchecked")
    public static <T,U> View<U> emptyView() {
        return (View<U>) EMPTY_VIEW;
    }
}
