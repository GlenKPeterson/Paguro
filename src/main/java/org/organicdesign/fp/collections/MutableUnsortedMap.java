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

import java.util.Map;

/**
 Interface for mutable (hash) map builder.
 */
public interface MutableUnsortedMap<K,V> extends ImUnsortedMap<K,V> {
    /** {@inheritDoc} */
    @Override
    MutableUnsortedMap<K,V> assoc(K key, V val);

    /** {@inheritDoc} */
    @Override default MutableUnsortedMap<K,V> assoc(Map.Entry<K,V> entry) {
        return assoc(entry.getKey(), entry.getValue());
    }

    /** Returns a mutable version of this immutable map. */
    @Override default MutableUnsortedMap<K,V> mutable() { return this; }

    /** {@inheritDoc} */
    @Override
    MutableUnsortedMap<K,V> without(K key);
}