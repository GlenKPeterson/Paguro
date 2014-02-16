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

class ViewTaken<T> extends View<T> {
    private final View<T> outerView;

    private long numItems;

    ViewTaken(View<T> v, long l) { outerView = v; numItems = l; }

    public static <T> View<T> of(View<T> v, long l) {
        if (l < 0) { throw new IllegalArgumentException("You can only take a non-negative number of items"); }
        if ( (v == null) || (v == EMPTY_VIEW) ) { return emptyView(); }
        return new ViewTaken<>(v, l);
    }

    @Override
    public synchronized T next() {
        if (numItems < 1) { return usedUp(); }
        numItems--;
        return outerView.next();
    }
}
