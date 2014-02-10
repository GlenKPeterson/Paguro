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

import org.organicdesign.fp.Realizable;
import org.organicdesign.fp.function.Consumer;
import org.organicdesign.fp.function.Filter;
import org.organicdesign.fp.function.Function1;
import org.organicdesign.fp.function.Function2;

/**
 A lightweight, one-time view that lazy, thread-safe operations can be built from.  Because there
 is no caching/memoization, this may be useful for data sources that do not fit in memory.
 @param <T>
 */

public interface View<T> extends Realizable<T> {
    public static final Object USED_UP = new Object();
    public static final Object EMPTY_VIEW = new ViewAbstract<Object>() {
        @Override
        public Object next() {
            return USED_UP;
        }
    };

    public T next();

    /**
     Lazily applies the given function to each item in the underlying data source, and returns
     a View with one item for each result.
     @param func a function that returns a new value for any value in the input
     @return a lazy view of the same size as the input (may contain duplicates) containing the
     return values of the given function in the same order as the input values.
     */
    public <U> View<U> map(Function1<T,U> func);

    /**
     Lazily applies the filter function to the underlying data source and returns a new view
     containing only the items for which the filter returned true
     @param func a function that returns true for items to keep, false for items to drop
     @return a lazy view of only the filtered items.
     */
    public View<T> filter(Filter<T> func);

    /**
     Eagerly processes the entire data source for side effects.
     @param se the function to do the processing
     */
    public void forEach(Consumer<T> se);

    /**
     Eagerly process entire data source using fold left.  I'm not sure how useful this is really.
     Without first class functions it's slower than forEach, and most reductions are probably
     already handled by toJavaList() and similar functions on ViewAbstract.
     @param fun combines each value in the list with the result so far.  The initial result is u.
     @param u the starting value to be combined with the first member of the underlying data source
     @return
     */
    public <U> U reduce(Function2<T,U,U> fun, U u);
}
