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
import java.util.TreeSet;

import org.organicdesign.fp.function.Function1;

/**
 Calling any of these methods forces eager evaluation of the entire underlying collection.
 This is incomplete, but enough to run some simple experiments with.
 @param <T>
 */
public interface Realizable<T> {
    public ArrayList<T> toJavaArrayList();
    /**
     @param f1 Maps keys to values
     @return A map with the keys from the given set, mapped to values using the given function.
     */
    public <U> HashMap<T,U> toJavaHashMap(Function1<T,U> f1);
    public TreeSet<T> toJavaTreeSet(Comparator<? super T> comparator);
    public TreeSet<T> toJavaTreeSet();
    public HashSet<T> toJavaHashSet();

}
