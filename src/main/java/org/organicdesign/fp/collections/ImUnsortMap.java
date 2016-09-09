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
public interface ImUnsortMap<K,V> extends ImMap<K,V> {
    /** Returns a transient/mutable version of this (persistent/immutable) map. */
    ImUnsortMapTrans<K,V> asTransient();

    /** Returns the Equator used by this map for equals comparisons and hashCodes */
    Equator<K> equator();

    /** {@inheritDoc} */
    @Override default ImSet<K> keySet() { return PersistentHashSet.ofMap(this); }

    /** {@inheritDoc} */
    @Override ImUnsortMap<K,V> assoc(K key, V val);

    /** {@inheritDoc} */
    @Override ImUnsortMap<K,V> without(K key);
}