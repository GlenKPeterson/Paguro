// Copyright 2015 PlanBase Inc. & Glen Peterson
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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Map;

import org.junit.Test;
import org.organicdesign.fp.collections.ImList;
import org.organicdesign.fp.collections.ImMap;
import org.organicdesign.fp.collections.ImSet;
import org.organicdesign.fp.collections.ImSortedMap;
import org.organicdesign.fp.collections.ImSortedSet;
import org.organicdesign.fp.collections.KeyVal;
import org.organicdesign.fp.collections.PersistentHashMap;
import org.organicdesign.fp.collections.PersistentHashSet;
import org.organicdesign.fp.collections.PersistentTreeMap;
import org.organicdesign.fp.collections.PersistentTreeSet;
import org.organicdesign.fp.collections.PersistentVector;
import org.organicdesign.fp.tuple.Tuple2;
import org.organicdesign.fp.tuple.Tuple3;
import org.organicdesign.fp.xform.Xform;

import static org.junit.Assert.assertEquals;
import static org.organicdesign.fp.StaticImports.*;

public class StaticImportsTest {

    @Test (expected = UnsupportedOperationException.class)
    public void instantiationEx() throws Throwable {
        Class<StaticImports> c = StaticImports.class;
        Constructor defCons = c.getDeclaredConstructor();
        defCons.setAccessible(true);
        try {
            // This catches the exception and wraps it in an InvocationTargetException
            defCons.newInstance();
        } catch (InvocationTargetException ite) {
            // Here we throw the original exception.
            throw ite.getTargetException();
        }
    }

    @SuppressWarnings({"NullArgumentToVariableArgMethod", "RedundantArrayCreation", "unchecked"})
    @Test public void testMap() throws Exception {
        assertEquals(PersistentHashMap.EMPTY, map());
        assertEquals(PersistentHashMap.EMPTY, map((Map.Entry[]) null));
        assertEquals(PersistentHashMap.EMPTY, map(new Map.Entry[0]));
        ImMap<String,Integer> phm = PersistentHashMap.<String,Integer>empty()
                .assoc("Hi", 43);
        assertEquals(phm, map(kv("Hi", 43)));
    }

    @SuppressWarnings({"NullArgumentToVariableArgMethod", "RedundantArrayCreation"})
    @Test public void testSet() throws Exception {
        assertEquals(PersistentHashSet.EMPTY, set());
        assertEquals(PersistentHashSet.EMPTY, set((Map.Entry[]) null));
        assertEquals(PersistentHashSet.EMPTY, set(new Object[0]));
        ImSet<String> phm = PersistentHashSet.<String>empty()
                .put("Hi");
        assertEquals(phm, set("Hi"));
    }

    @Test public void testSortedMap() throws Exception {
        ImList<Map.Entry<String,Integer>> ls = PersistentVector.empty();
        ls = ls.append(kv("Hi", 1))
               .append(KeyVal.of("Bye", -99))
               .append(new KeyVal<>("hi", 33))
               .append(kv("bye", -9999));
        ImSortedMap<String,Integer> refMap =
                PersistentTreeMap.ofComp(String.CASE_INSENSITIVE_ORDER, ls);
        ImSortedMap<String,Integer> testMap = sortedMap(String.CASE_INSENSITIVE_ORDER, ls);
        assertEquals(refMap, testMap);
        // Because equals is meant to be compatible with an unsorted map, the comparator is not
        // considered.  Check the comparators to see that they are truly identical.
        assertEquals(refMap.comparator(), testMap.comparator());

        // Try using the default comparator.
        refMap = PersistentTreeMap.of(ls);
        testMap = sortedMap(ls);

        assertEquals(refMap, testMap);
        // Because equals is meant to be compatible with an unsorted map, the comparator is not
        // considered.  Check the comparators to see that they are truly identical.
        assertEquals(refMap.comparator(), testMap.comparator());
    }

    @Test public void testSortedSet() throws Exception {
        ImList<String> ls = PersistentVector.empty();
        ls = ls.append("Hi")
               .append("Bye")
               .append("hi")
               .append("bye");
        ImSortedSet<String> refSet =
                PersistentTreeSet.ofComp(String.CASE_INSENSITIVE_ORDER, ls);
        ImSortedSet<String> testSet = sortedSet(String.CASE_INSENSITIVE_ORDER, ls);
        assertEquals(refSet, testSet);
        // Because equals is meant to be compatible with an unsorted set, the comparator is not
        // considered.  Check the comparators to see that they are truly identical.
        assertEquals(refSet.comparator(), testSet.comparator());

        // Try using the default comparator.
        refSet = PersistentTreeSet.of(ls);
        testSet = sortedSet(ls);

        assertEquals(refSet, testSet);
        // Because equals is meant to be compatible with an unsorted set, the comparator is not
        // considered.  Check the comparators to see that they are truly identical.
        assertEquals(refSet.comparator(), testSet.comparator());
    }

    @Test public void testTup() throws Exception {
        assertEquals(Tuple2.of("Hello", -32), tup("Hello", -32));
        assertEquals(Tuple3.of("Hello", -32, 9.5), tup("Hello", -32, 9.5));
    }

    @SuppressWarnings({"NullArgumentToVariableArgMethod", "RedundantArrayCreation"})
    @Test public void testVec() throws Exception {
        assertEquals(PersistentVector.EMPTY, vec());
        assertEquals(PersistentVector.EMPTY, vec((Map.Entry[]) null));
        assertEquals(PersistentVector.EMPTY, vec(new Map.Entry[0]));
        ImList<String> ls = PersistentVector.<String>empty()
                .append("Hi");
        assertEquals(ls, vec("Hi"));
    }

    @Test public void testXform() throws Exception {
        assertEquals(Xform.of(Arrays.asList(1,2,3)), xform(Arrays.asList(1,2,3)));
    }
}
