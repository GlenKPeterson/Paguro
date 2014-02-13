// Copyright 2014-02-09 PlanBase Inc. & Glen Peterson
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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.organicdesign.fp.function.Function;

public abstract class RealizableAbstract<T> implements Realizable<T> {
    @Override
    public abstract ArrayList<T> toJavaArrayList();

    @Override
    public List<T> toJavaUnmodList() {
        return Collections.unmodifiableList(toJavaArrayList());
    }

    /**
     @param f1 Maps keys to values
     @return A map with the keys from the given set, mapped to values using the given function.
     */
    @Override
    public abstract <U> HashMap<T,U> toJavaHashMap(Function<T,U> f1);

    @Override
    public <U> Map<T,U> toJavaUnmodMap(Function<T,U> f1) {
        return Collections.unmodifiableMap(toJavaHashMap(f1));
    }

    /**
     @param f1 Maps values to keys
     @return A map with the values from the given set, mapped by keys supplied by the given function.
     */
    @Override
    public abstract <U> HashMap<U,T> toReverseJavaHashMap(Function<T,U> f1);

    @Override
    public <U> Map<U,T> toReverseJavaUnmodMap(Function<T,U> f1) {
        return Collections.unmodifiableMap(toReverseJavaHashMap(f1));
    }

    @Override
    public abstract TreeSet<T> toJavaTreeSet(Comparator<? super T> comparator);

    @Override
    public SortedSet<T> toJavaUnmodSortedSet(Comparator<? super T> comparator) {
        return Collections.unmodifiableSortedSet(toJavaTreeSet(comparator));
    }

    @Override
    public abstract TreeSet<T> toJavaTreeSet();

    @Override
    public SortedSet<T> toJavaUnmodSortedSet() {
        return Collections.unmodifiableSortedSet(toJavaTreeSet());
    }

    @Override
    public abstract HashSet<T> toJavaHashSet();

    @Override
    public Set<T> toJavaUnmodSet() {
        return Collections.unmodifiableSet(toJavaHashSet());
    }
}
