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

import org.organicdesign.fp.function.BiFunction;
import org.organicdesign.fp.function.Function;

public abstract class TransformableAbstract<T> implements Transformable<T> {

    @Override
    public abstract <U> U foldLeft(U u, BiFunction<U, T, U> fun);

    @Override
    public ArrayList<T> toJavaArrayList() {
        return foldLeft(new ArrayList<T>(), new BiFunction<ArrayList<T>, T, ArrayList<T>>() {
            @Override
            public ArrayList<T> apply(ArrayList<T> ts, T t) throws Exception {
                ts.add(t);
                return ts;
            }
        });
    }

    @Override
    public List<T> toJavaUnmodList() {
        return Collections.unmodifiableList(toJavaArrayList());
    }

    /**
     @param f1 Maps keys to values
     @return A map with the keys from the given set, mapped to values using the given function.
     */
    @Override
    public <U> HashMap<T,U> toJavaHashMap(final Function<T,U> f1) {
        return foldLeft(new HashMap<T, U>(), new BiFunction<HashMap<T, U>, T, HashMap<T, U>>() {
            @Override
            public HashMap<T, U> apply(HashMap<T, U> ts, T t) throws Exception {
                ts.put(t, f1.apply(t));
                return ts;
            }
        });
    }

    @Override
    public <U> Map<T,U> toJavaUnmodMap(Function<T,U> f1) {
        return Collections.unmodifiableMap(toJavaHashMap(f1));
    }

    /**
     @param f1 Maps values to keys
     @return A map with the values from the given set, mapped by keys supplied by the given function.
     */
    @Override
    public <U> HashMap<U,T> toReverseJavaHashMap(final Function<T, U> f1) {
        return foldLeft(new HashMap<U, T>(), new BiFunction<HashMap<U, T>, T, HashMap<U, T>>() {
            @Override
            public HashMap<U, T> apply(HashMap<U, T> ts, T t) throws Exception {
                ts.put(f1.apply_(t), t);
                return ts;
            }
        });
    }

    @Override
    public <U> Map<U,T> toReverseJavaUnmodMap(Function<T,U> f1) {
        return Collections.unmodifiableMap(toReverseJavaHashMap(f1));
    }

    @Override
    public TreeSet<T> toJavaTreeSet(Comparator<? super T> comparator) {
        return foldLeft(new TreeSet<T>(comparator), new BiFunction<TreeSet<T>, T, TreeSet<T>>() {
            @Override
            public TreeSet<T> apply(TreeSet<T> ts, T t) throws Exception {
                ts.add(t);
                return ts;
            }
        });
    }
    @Override
    public TreeSet<T> toJavaTreeSet() { return toJavaTreeSet(null); }


    @Override
    public SortedSet<T> toJavaUnmodSortedSet(Comparator<? super T> comparator) {
        return Collections.unmodifiableSortedSet(toJavaTreeSet(comparator));
    }
    @Override
    public SortedSet<T> toJavaUnmodSortedSet() {
        return toJavaUnmodSortedSet(null);
    }

    @Override
    public HashSet<T> toJavaHashSet() {
        return foldLeft(new HashSet<T>(), new BiFunction<HashSet<T>, T, HashSet<T>>() {
            @Override
            public HashSet<T> apply(HashSet<T> ts, T t) throws Exception {
                ts.add(t);
                return ts;
            }
        });
    }

    @Override
    public Set<T> toJavaUnmodSet() {
        return Collections.unmodifiableSet(toJavaHashSet());
    }
}
