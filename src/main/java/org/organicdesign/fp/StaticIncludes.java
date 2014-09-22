// Copyright 2014-09-22 PlanBase Inc. & Glen Peterson
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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class StaticIncludes {
    // Prevent instantiation
    private StaticIncludes() { throw new UnsupportedOperationException("No instantiation"); }

    public static <K,V> Map<K,V> uMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5,
                                      K k6, V v6, K k7, V v7, K k8, V v8, K k9, V v9) {
        Map<K,V> m = new HashMap<>(9);
        m.put(k1, v1); m.put(k2, v2); m.put(k3, v3); m.put(k4, v4); m.put(k5, v5); m.put(k6, v6);
        m.put(k7, v7); m.put(k8, v8); m.put(k9, v9);
        return Collections.unmodifiableMap(m);
    }
    public static <K,V> Map<K,V> uMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5,
                                      K k6, V v6, K k7, V v7, K k8, V v8) {
        Map<K,V> m = new HashMap<>(8);
        m.put(k1, v1); m.put(k2, v2); m.put(k3, v3); m.put(k4, v4); m.put(k5, v5); m.put(k6, v6);
        m.put(k7, v7); m.put(k8, v8);
        return Collections.unmodifiableMap(m);
    }
    public static <K,V> Map<K,V> uMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5,
                                      K k6, V v6, K k7, V v7) {
        Map<K,V> m = new HashMap<>(7);
        m.put(k1, v1); m.put(k2, v2); m.put(k3, v3); m.put(k4, v4); m.put(k5, v5); m.put(k6, v6);
        m.put(k7, v7);
        return Collections.unmodifiableMap(m);
    }
    public static <K,V> Map<K,V> uMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5,
                                      K k6, V v6) {
        Map<K,V> m = new HashMap<>(6);
        m.put(k1, v1); m.put(k2, v2); m.put(k3, v3); m.put(k4, v4); m.put(k5, v5); m.put(k6, v6);
        return Collections.unmodifiableMap(m);
    }
    public static <K,V> Map<K,V> uMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5) {
        Map<K,V> m = new HashMap<>(5);
        m.put(k1, v1); m.put(k2, v2); m.put(k3, v3); m.put(k4, v4); m.put(k5, v5);
        return Collections.unmodifiableMap(m);
    }
    public static <K,V> Map<K,V> uMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
        Map<K,V> m = new HashMap<>(4); m.put(k1, v1); m.put(k2, v2); m.put(k3, v3); m.put(k4, v4);
        return Collections.unmodifiableMap(m);
    }
    public static <K,V> Map<K,V> uMap(K k1, V v1, K k2, V v2, K k3, V v3) {
        Map<K,V> m = new HashMap<>(3); m.put(k1, v1); m.put(k2, v2); m.put(k3, v3);
        return Collections.unmodifiableMap(m);
    }
    public static <K,V> Map<K,V> uMap(K k1, V v1, K k2, V v2) {
        Map<K,V> m = new HashMap<>(2); m.put(k1, v1); m.put(k2, v2);
        return Collections.unmodifiableMap(m);
    }
    public static <K,V> Map<K,V> uMap(K k1, V v1) {
        Map<K,V> m = new HashMap<>(1); m.put(k1, v1); return Collections.unmodifiableMap(m);
    }

    @SafeVarargs
    public static <T> Set<T> uSet(T... ts) {
        return Collections.unmodifiableSet(
                (ts == null) ? Collections.emptySet()
                             : new HashSet<>(Arrays.asList(ts)));
    }

    @SafeVarargs
    public static <T> List<T> uList(T... ts) {
        return Collections.unmodifiableList(
                (ts == null) ? Collections.emptyList()
                             : new ArrayList<T>(Arrays.asList(ts)));
    }
}
