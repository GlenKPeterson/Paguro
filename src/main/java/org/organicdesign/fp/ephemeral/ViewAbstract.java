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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.organicdesign.fp.function.Function1;

public abstract class ViewAbstract<T> implements View<T> {

    @Override
    public abstract T next();

    protected Set<T> asSet(Set<T> ts) {
        T item = next();
        while (item != USED_UP) {
            ts.add(item);
            item = next();
        }
        return ts;
    }

    @Override
    public ArrayList<T> toJavaArrayList() {
        ArrayList<T> ts = new ArrayList<>();
        T item = next();
        while (item != USED_UP) {
            ts.add(item);
            item = next();
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
        T item = next();
        while (item != USED_UP) {
            ts.put(item, f1.apply_(item));
            item = next();
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
