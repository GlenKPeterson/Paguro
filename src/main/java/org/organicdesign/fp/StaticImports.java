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

public class StaticImports {
    // Prevent instantiation
    private StaticImports() { throw new UnsupportedOperationException("No instantiation"); }

    @SuppressWarnings("UnusedDeclaration")
    public static <K,V> Map<K,V> uMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5,
                                      K k6, V v6, K k7, V v7, K k8, V v8, K k9, V v9, K k10, V v10,
                                      K k11, V v11, K k12, V v12, K k13, V v13, K k14, V v14,
                                      K k15, V v15, K k16, V v16, K k17, V v17, K k18, V v18,
                                      K k19, V v19, K k20, V v20) {
        Map<K,V> m = new HashMap<>(20);
        m.put(k1, v1); m.put(k2, v2); m.put(k3, v3); m.put(k4, v4); m.put(k5, v5); m.put(k6, v6);
        m.put(k7, v7); m.put(k8, v8); m.put(k9, v9); m.put(k10, v10); m.put(k11, v11);
        m.put(k12, v12); m.put(k13, v13); m.put(k14, v14); m.put(k15, v15); m.put(k16, v16);
        m.put(k17, v17); m.put(k18, v18); m.put(k19, v19); m.put(k20, v20);
        return Collections.unmodifiableMap(m);
    }
    @SuppressWarnings("UnusedDeclaration")
    public static <K,V> Map<K,V> uMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5,
                                      K k6, V v6, K k7, V v7, K k8, V v8, K k9, V v9, K k10, V v10,
                                      K k11, V v11, K k12, V v12, K k13, V v13, K k14, V v14,
                                      K k15, V v15, K k16, V v16, K k17, V v17, K k18, V v18,
                                      K k19, V v19) {
        Map<K,V> m = new HashMap<>(20);
        m.put(k1, v1); m.put(k2, v2); m.put(k3, v3); m.put(k4, v4); m.put(k5, v5); m.put(k6, v6);
        m.put(k7, v7); m.put(k8, v8); m.put(k9, v9); m.put(k10, v10); m.put(k11, v11);
        m.put(k12, v12); m.put(k13, v13); m.put(k14, v14); m.put(k15, v15); m.put(k16, v16);
        m.put(k17, v17); m.put(k18, v18); m.put(k19, v19);
        return Collections.unmodifiableMap(m);
    }
    @SuppressWarnings("UnusedDeclaration")
    public static <K,V> Map<K,V> uMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5,
                                      K k6, V v6, K k7, V v7, K k8, V v8, K k9, V v9, K k10, V v10,
                                      K k11, V v11, K k12, V v12, K k13, V v13, K k14, V v14,
                                      K k15, V v15, K k16, V v16, K k17, V v17, K k18, V v18) {
        Map<K,V> m = new HashMap<>(20);
        m.put(k1, v1); m.put(k2, v2); m.put(k3, v3); m.put(k4, v4); m.put(k5, v5); m.put(k6, v6);
        m.put(k7, v7); m.put(k8, v8); m.put(k9, v9); m.put(k10, v10); m.put(k11, v11);
        m.put(k12, v12); m.put(k13, v13); m.put(k14, v14); m.put(k15, v15); m.put(k16, v16);
        m.put(k17, v17); m.put(k18, v18);
        return Collections.unmodifiableMap(m);
    }
    @SuppressWarnings("UnusedDeclaration")
    public static <K,V> Map<K,V> uMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5,
                                      K k6, V v6, K k7, V v7, K k8, V v8, K k9, V v9, K k10, V v10,
                                      K k11, V v11, K k12, V v12, K k13, V v13, K k14, V v14,
                                      K k15, V v15, K k16, V v16, K k17, V v17) {
        Map<K,V> m = new HashMap<>(20);
        m.put(k1, v1); m.put(k2, v2); m.put(k3, v3); m.put(k4, v4); m.put(k5, v5); m.put(k6, v6);
        m.put(k7, v7); m.put(k8, v8); m.put(k9, v9); m.put(k10, v10); m.put(k11, v11);
        m.put(k12, v12); m.put(k13, v13); m.put(k14, v14); m.put(k15, v15); m.put(k16, v16);
        m.put(k17, v17);
        return Collections.unmodifiableMap(m);
    }
    @SuppressWarnings("UnusedDeclaration")
    public static <K,V> Map<K,V> uMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5,
                                      K k6, V v6, K k7, V v7, K k8, V v8, K k9, V v9, K k10, V v10,
                                      K k11, V v11, K k12, V v12, K k13, V v13, K k14, V v14,
                                      K k15, V v15, K k16, V v16) {
        Map<K,V> m = new HashMap<>(20);
        m.put(k1, v1); m.put(k2, v2); m.put(k3, v3); m.put(k4, v4); m.put(k5, v5); m.put(k6, v6);
        m.put(k7, v7); m.put(k8, v8); m.put(k9, v9); m.put(k10, v10); m.put(k11, v11);
        m.put(k12, v12); m.put(k13, v13); m.put(k14, v14); m.put(k15, v15); m.put(k16, v16);
        return Collections.unmodifiableMap(m);
    }
    @SuppressWarnings("UnusedDeclaration")
    public static <K,V> Map<K,V> uMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5,
                                      K k6, V v6, K k7, V v7, K k8, V v8, K k9, V v9, K k10, V v10,
                                      K k11, V v11, K k12, V v12, K k13, V v13, K k14, V v14,
                                      K k15, V v15) {
        Map<K,V> m = new HashMap<>(20);
        m.put(k1, v1); m.put(k2, v2); m.put(k3, v3); m.put(k4, v4); m.put(k5, v5); m.put(k6, v6);
        m.put(k7, v7); m.put(k8, v8); m.put(k9, v9); m.put(k10, v10); m.put(k11, v11);
        m.put(k12, v12); m.put(k13, v13); m.put(k14, v14); m.put(k15, v15);
        return Collections.unmodifiableMap(m);
    }
    @SuppressWarnings("UnusedDeclaration")
    public static <K,V> Map<K,V> uMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5,
                                      K k6, V v6, K k7, V v7, K k8, V v8, K k9, V v9, K k10, V v10,
                                      K k11, V v11, K k12, V v12, K k13, V v13, K k14, V v14) {
        Map<K,V> m = new HashMap<>(20);
        m.put(k1, v1); m.put(k2, v2); m.put(k3, v3); m.put(k4, v4); m.put(k5, v5); m.put(k6, v6);
        m.put(k7, v7); m.put(k8, v8); m.put(k9, v9); m.put(k10, v10); m.put(k11, v11);
        m.put(k12, v12); m.put(k13, v13); m.put(k14, v14);
        return Collections.unmodifiableMap(m);
    }
    @SuppressWarnings("UnusedDeclaration")
    public static <K,V> Map<K,V> uMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5,
                                      K k6, V v6, K k7, V v7, K k8, V v8, K k9, V v9, K k10, V v10,
                                      K k11, V v11, K k12, V v12, K k13, V v13) {
        Map<K,V> m = new HashMap<>(20);
        m.put(k1, v1); m.put(k2, v2); m.put(k3, v3); m.put(k4, v4); m.put(k5, v5); m.put(k6, v6);
        m.put(k7, v7); m.put(k8, v8); m.put(k9, v9); m.put(k10, v10); m.put(k11, v11);
        m.put(k12, v12); m.put(k13, v13);
        return Collections.unmodifiableMap(m);
    }
    @SuppressWarnings("UnusedDeclaration")
    public static <K,V> Map<K,V> uMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5,
                                      K k6, V v6, K k7, V v7, K k8, V v8, K k9, V v9, K k10, V v10,
                                      K k11, V v11, K k12, V v12) {
        Map<K,V> m = new HashMap<>(20);
        m.put(k1, v1); m.put(k2, v2); m.put(k3, v3); m.put(k4, v4); m.put(k5, v5); m.put(k6, v6);
        m.put(k7, v7); m.put(k8, v8); m.put(k9, v9); m.put(k10, v10); m.put(k11, v11);
        m.put(k12, v12);
        return Collections.unmodifiableMap(m);
    }
    @SuppressWarnings("UnusedDeclaration")
    public static <K,V> Map<K,V> uMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5,
                                      K k6, V v6, K k7, V v7, K k8, V v8, K k9, V v9, K k10, V v10,
                                      K k11, V v11) {
        Map<K,V> m = new HashMap<>(20);
        m.put(k1, v1); m.put(k2, v2); m.put(k3, v3); m.put(k4, v4); m.put(k5, v5); m.put(k6, v6);
        m.put(k7, v7); m.put(k8, v8); m.put(k9, v9); m.put(k10, v10); m.put(k11, v11);
        return Collections.unmodifiableMap(m);
    }
    @SuppressWarnings("UnusedDeclaration")
    public static <K,V> Map<K,V> uMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5,
                                      K k6, V v6, K k7, V v7, K k8, V v8, K k9, V v9,
                                      K k10, V v10) {
        Map<K,V> m = new HashMap<>(20);
        m.put(k1, v1); m.put(k2, v2); m.put(k3, v3); m.put(k4, v4); m.put(k5, v5); m.put(k6, v6);
        m.put(k7, v7); m.put(k8, v8); m.put(k9, v9); m.put(k10, v10);
        return Collections.unmodifiableMap(m);
    }
    @SuppressWarnings("UnusedDeclaration")
    public static <K,V> Map<K,V> uMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5,
                                      K k6, V v6, K k7, V v7, K k8, V v8, K k9, V v9) {
        Map<K,V> m = new HashMap<>(9);
        m.put(k1, v1); m.put(k2, v2); m.put(k3, v3); m.put(k4, v4); m.put(k5, v5); m.put(k6, v6);
        m.put(k7, v7); m.put(k8, v8); m.put(k9, v9);
        return Collections.unmodifiableMap(m);
    }
    @SuppressWarnings("UnusedDeclaration")
    public static <K,V> Map<K,V> uMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5,
                                      K k6, V v6, K k7, V v7, K k8, V v8) {
        Map<K,V> m = new HashMap<>(8);
        m.put(k1, v1); m.put(k2, v2); m.put(k3, v3); m.put(k4, v4); m.put(k5, v5); m.put(k6, v6);
        m.put(k7, v7); m.put(k8, v8);
        return Collections.unmodifiableMap(m);
    }
    @SuppressWarnings("UnusedDeclaration")
    public static <K,V> Map<K,V> uMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5,
                                      K k6, V v6, K k7, V v7) {
        Map<K,V> m = new HashMap<>(7);
        m.put(k1, v1); m.put(k2, v2); m.put(k3, v3); m.put(k4, v4); m.put(k5, v5); m.put(k6, v6);
        m.put(k7, v7);
        return Collections.unmodifiableMap(m);
    }
    @SuppressWarnings("UnusedDeclaration")
    public static <K,V> Map<K,V> uMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5,
                                      K k6, V v6) {
        Map<K,V> m = new HashMap<>(6);
        m.put(k1, v1); m.put(k2, v2); m.put(k3, v3); m.put(k4, v4); m.put(k5, v5); m.put(k6, v6);
        return Collections.unmodifiableMap(m);
    }
    @SuppressWarnings("UnusedDeclaration")
    public static <K,V> Map<K,V> uMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5) {
        Map<K,V> m = new HashMap<>(5);
        m.put(k1, v1); m.put(k2, v2); m.put(k3, v3); m.put(k4, v4); m.put(k5, v5);
        return Collections.unmodifiableMap(m);
    }
    @SuppressWarnings("UnusedDeclaration")
    public static <K,V> Map<K,V> uMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
        Map<K,V> m = new HashMap<>(4); m.put(k1, v1); m.put(k2, v2); m.put(k3, v3); m.put(k4, v4);
        return Collections.unmodifiableMap(m);
    }
    @SuppressWarnings("UnusedDeclaration")
    public static <K,V> Map<K,V> uMap(K k1, V v1, K k2, V v2, K k3, V v3) {
        Map<K,V> m = new HashMap<>(3); m.put(k1, v1); m.put(k2, v2); m.put(k3, v3);
        return Collections.unmodifiableMap(m);
    }
    @SuppressWarnings("UnusedDeclaration")
    public static <K,V> Map<K,V> uMap(K k1, V v1, K k2, V v2) {
        Map<K,V> m = new HashMap<>(2); m.put(k1, v1); m.put(k2, v2);
        return Collections.unmodifiableMap(m);
    }
    @SuppressWarnings("UnusedDeclaration")
    public static <K,V> Map<K,V> uMap(K k1, V v1) {
        Map<K,V> m = new HashMap<>(1); m.put(k1, v1); return Collections.unmodifiableMap(m);
    }

    @SuppressWarnings("UnusedDeclaration")
    @SafeVarargs
    public static <T> Set<T> uSet(T... ts) {
        return Collections.unmodifiableSet(
                (ts == null) ? Collections.emptySet()
                             : new HashSet<>(Arrays.asList(ts)));
    }

    @SuppressWarnings("UnusedDeclaration")
    @SafeVarargs
    public static <T> List<T> uList(T... ts) {
        return Collections.unmodifiableList(
                (ts == null) ? Collections.emptyList()
                             : new ArrayList<T>(Arrays.asList(ts)));
    }
}
