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

public interface Transformable<T> extends Realizable<T> {
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

    // TODO: You can always use foldLeft for this operation.  Does having reduceLeft add more clarity to the underlying code, or does it provide some useful additional functionality?
//    /**
//     Eagerly process entire data source.  This is an extremely powerful method, being the only one
//     that currently can produce more output items than input items (flatMap would do that too
//     if implemented).
//     @return
//     @param fun Starting with the first two elements of the list, combines each value in the list with the result so far.  The initial result is u.
//     */
//    public T reduceLeft(BiFunction<T, T, T> fun);

    /**
     One of the two higher-order functions that can produce more output items than input items
     (when u is a collection). FlatMap is the other, but foldLeft is eager while flatMap is lazy.

     @return an eagerly evaluated result which could be a single value like a sum, or a collection.
     @param u the accumulator and starting value.  This will be passed to the function on the
     first iteration to be combined with the first member of the underlying data source.  For some
     operations you'll need to pass an identity, e.g. for a sum, pass 0, for a product, pass 1 as
     this parameter.
     @param fun combines each value in the list with the result so far.  The initial result is u.
     */
    public <U> U foldLeft(U u, BiFunction<U, T, U> fun);


    // Sub-classes cannot inherit from this because the function that you pass in has to know the actal return type.
    // Have to implement this independently on sub-classes.
//    /**
//     One of the two higher-order functions that can produce more output items than input items.
//     foldLeft is the other, but flatMap is lazy while foldLeft is eager.
//     @return a lazily evaluated collection which is expected to be larger than the input
//     collection.  For a collection that's the same size, map() is more efficient.  If the expected
//     return is smaller, use filter followed by map if possible, or vice versa if not.
//     @param fun yields a Transformable of 0 or more results for each input item.
//     */
//    public <U> Transformable<U> flatMap(Function<T,? extends Transformable<U>> func);

}
