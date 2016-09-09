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
 Interface for transient/mutable (hash) map builder.
 */
public interface ImUnsortMapTrans<K,V> extends ImUnsortMap<K,V> {
    /** Returns a persistent/immutable version of this transient map. */
    ImUnsortMap<K,V> persistent();

    /** {@inheritDoc} */
    @Override ImUnsortMapTrans<K,V> assoc(K key, V val);

    /** {@inheritDoc} */
    @Override ImUnsortMapTrans<K,V> without(K key);
}