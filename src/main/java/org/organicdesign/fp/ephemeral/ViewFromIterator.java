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

import java.util.Iterator;

import org.organicdesign.fp.function.Filter;
import org.organicdesign.fp.function.Function1;

public class ViewFromIterator<T> extends ViewAbstract<T> {

    private final Iterator<T> iter;

    ViewFromIterator(Iterator<T> i) { iter = i; }

    public static <T> View<T> of(Iterator<T> i) {
        if (i == null) { return emptyView(); }
        return new ViewFromIterator<>(i);
    }

    public static <T> View<T> of(Iterable<T> i) {
        if (i == null) { return emptyView(); }
        Iterator<T> iiter = i.iterator();
        if (iiter == null) { return emptyView(); }
        return new ViewFromIterator<>(iiter);
    }

    @Override
    public synchronized T next() {
        return iter.hasNext() ? iter.next() : usedUp();
    }

    public <U> View<U> map(Function1<T,U> func) {
        return ViewMapped.of(this, func);
    }

    @SuppressWarnings("unchecked")
    public <T> View<T> filter(Filter<T> func) {
        // TODO: Why do I have to cast this and suppress warnings on it when this is already a Sequence<T>
        return ViewFiltered.of((View<T>) this, func);
    }

    @SuppressWarnings("unchecked")
    public static <T> ViewFromIterator<T> emptyView() {
        return (ViewFromIterator<T>) EMPTY_VIEW;
    }

    @SuppressWarnings("unchecked")
    public T usedUp() { return (T) USED_UP; }
}
