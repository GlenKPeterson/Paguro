// Copyright 2014-02-02 PlanBase Inc. and Glen Peterson
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.organicdesign.fp.permanent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.organicdesign.fp.function.Filter;
import org.organicdesign.fp.function.Function1;

public abstract class SequenceAbstract<T> implements Sequence<T> {

    @Override
    public abstract T first();

    @Override
    public abstract Sequence<T> rest();

    @Override
    public <U> Sequence<U> map(Function1<T,U> func) {
        return SequenceMapped.of(this, func);
    }

    @Override
    public Sequence<T> filter(Filter<T> func) {
        return SequenceFiltered.of(this, func);
    }


    protected Set<T> asSet(Set<T> ts) {
        Sequence<T> seq = this;
        while (seq != EMPTY_SEQUENCE) {
            ts.add(seq.first());
            seq = seq.rest();
        }
        return ts;
    }

    @Override
    public ArrayList<T> toJavaArrayList() {
        ArrayList<T> ts = new ArrayList<>();
        Sequence<T> seq = this;
        while (seq != EMPTY_SEQUENCE) {
            ts.add(seq.first());
            seq = seq.rest();
        }
        return ts;
    }
    /**
     @param f1 Maps keys to values
     @return A map with the keys from the given set, mapped to values using the given function.
     */
    @Override
    public <U> HashMap<T,U> toJavaHashMap(Function1<T,U> f1) {
        HashMap<T,U> ts = new HashMap<T, U>() {};
        Sequence<T> seq = this;
        while (seq != EMPTY_SEQUENCE) {
            T first = seq.first();
            ts.put(first, f1.apply_(first));
            seq = seq.rest();
        }
        return ts;
    }
    @Override
    public TreeSet<T> toJavaTreeSet(Comparator<? super T> comparator) {
        TreeSet<T> ts = new TreeSet<>(comparator);
        return (TreeSet<T>) asSet(ts);
    }
    @Override
    public TreeSet<T> toJavaTreeSet() {
        TreeSet<T> ts = new TreeSet<>();
        return (TreeSet<T>) asSet(ts);
    }
    @Override
    public HashSet<T> toJavaHashSet() {
        HashSet<T> ts = new HashSet<>();
        return (HashSet<T>) asSet(ts);
    }
}
