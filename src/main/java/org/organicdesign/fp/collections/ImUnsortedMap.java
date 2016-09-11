// Copyright 2016 PlanBase Inc. & Glen Peterson
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

package org.organicdesign.fp.collections;

/**
 Interface for unsorted (today that probably means "hash") maps.
 */
public interface ImUnsortedMap<K,V> extends ImMap<K,V> {
    /** {@inheritDoc} */
    @Override ImUnsortedMap<K,V> assoc(K key, V val);

    /** Returns the Equator used by this map for equals comparisons and hashCodes */
    Equator<K> equator();

    /**
     Returns a immutable version of this (maybe) mutable map.  Maybe this should be
     on ImUnsortedMapTrans only, but that prevents reusing a variable to hold both a mutable
     and immutable map.  I really wasn't sure of the right thing to do here.  Sometimes you want
     a guaranteed mutable.  Sometimes a guaranteed immutable.  I suppose you can use the
     specific implementation class in that case.  Inheritance forces you to pick one.
     */
    ImUnsortedMap<K,V> immutable();

    /** {@inheritDoc} */
    @Override default ImSet<K> keySet() { return PersistentHashSet.ofMap(this); }

    /** Returns a mutable version of this immutable map. */
    MutableUnsortedMap<K,V> mutable();

    /** {@inheritDoc} */
    @Override
    ImUnsortedMap<K,V> without(K key);
}