// Copyright 2014-02-15 PlanBase Inc. & Glen Peterson
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

package org.organicdesign.fp;

import org.organicdesign.fp.function.BiFunction;
import org.organicdesign.fp.function.Consumer;
import org.organicdesign.fp.function.Function;
import org.organicdesign.fp.function.Predicate;

public interface Transformable<T> {
    /**
     Lazily applies the given function to each item in the underlying data source, and returns
     a View with one item for each result.
     @param func a function that returns a new value for any value in the input
     @return a lazy view of the same size as the input (may contain duplicates) containing the
     return values of the given function in the same order as the input values.
     */
    public <U> Transformable<U> map(Function<T,U> func);

    /**
     Lazily applies the filter function to the underlying data source and returns a new view
     containing only the items for which the filter returned true
     @param func a function that returns true for items to keep, false for items to drop
     @return a lazy view of only the filtered items.
     */
    public Transformable<T> filter(Predicate<T> func);

    /**
     Eagerly processes the entire data source for side effects.
     @param se the function to do the processing
     */
    public void forEach(Consumer<T> se);

    /**
     Eagerly returns the first item matching the given predicate.
     @param pred the test that the item needs to pass
     @return the first item that passes the test, or null if no such item is found
     */
    public T firstMatching(Predicate<T> pred);

    /**
     Eagerly process entire data source.  This is an extremely powerful method, being the only one
     that currently can produce more output items than input items (flatMap would do that too
     if implemented).
     @param fun combines each value in the list with the result so far.  The initial result is u.
     @param u the starting value to be combined with the first member of the underlying data source
     @return
     */
    public <U> U reduce(BiFunction<T,U,U> fun, U u);
}
