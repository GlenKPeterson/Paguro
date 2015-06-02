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

import java.util.Iterator;

/** A single-pass transformer backed by a Java Iterator. */
public class ViewFromIterable<T> implements View<T> {

    private final Iterator<T> iter;

    private ViewFromIterable(Iterator<T> i) { iter = i; }

    // Just wrong - you can't trust an iterator that you didn't get yourself.
//    public static <T> View<T> of(Iterator<T> i) {
//        if (i == null) { return View.emptyView(); }
//        return new ViewFromIterable<>(i);
//    }

    public static <T> View<T> of(Iterable<T> i) {
        if (i == null) { return View.emptyView(); }
        Iterator<T> iiter = i.iterator();
        if (iiter == null) { return View.emptyView(); }
        return new ViewFromIterable<>(iiter);
    }

    @Override
    public synchronized Option<T> next() {
        return iter.hasNext() ? Option.of(iter.next()) : Option.none();
    }
}
