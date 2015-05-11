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

package org.organicdesign.fp;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import org.organicdesign.fp.collections.ImList;
import org.organicdesign.fp.collections.ImMapSorted;
import org.organicdesign.fp.collections.UnIterable;
import org.organicdesign.fp.collections.UnIterator;
import org.organicdesign.fp.collections.UnSetSorted;
import org.organicdesign.fp.function.Function1;

/**
 Calling any of these methods forces eager evaluation of the entire underlying collection.
 This is incomplete, but enough to run some simple experiments with.
 @param <T>
 */
public interface Realizable<T> extends UnIterable<T> {
    ArrayList<T> toJavaArrayList();
    ImList<T> toImList();

    /**
     @return A map with the keys from the given set, mapped to values using the given function.
      * @param f1 Maps keys to values
     */
    <U,V> HashMap<U,V> toJavaHashMap(final Function1<? super T,Map.Entry<U,V>> f1);

    /**
     @return A map with the keys from the given set, mapped to values using the given function.
      * @param f1 Maps keys to values
     */
    <U,V> TreeMap<U,V> toJavaTreeMap(final Function1<? super T,Map.Entry<U,V>> f1);

//    /**
//     @return An unmodifiable map with the keys from the given set, mapped to values using the given
//     function.
//      * @param f1 Maps keys to values
//     */
//    <U,V> UnMap<U,V> toUnMap(Function1<T,Map.Entry<U,V>> f1);

    /**
     @return An immutable map with the keys from the given set, mapped to values using the given
     function.
      * @param f1 Maps keys to values
     */
    <U,V> ImMapSorted<U,V> toImMapSorted(Comparator<? super U> comp, Function1<? super T,Map.Entry<U,V>> f1);

    TreeSet<T> toJavaTreeSet(Comparator<? super T> comparator);

    UnSetSorted<T> toImSetSorted(Comparator<? super T> comparator);

    HashSet<T> toJavaHashSet();

    T[] toTypedArray();

    @Override UnIterator<T> iterator();
}
