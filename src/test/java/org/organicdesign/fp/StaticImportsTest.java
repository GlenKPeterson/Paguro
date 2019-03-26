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

import org.junit.Test;
import org.organicdesign.fp.collections.ImList;
import org.organicdesign.fp.collections.ImMap;
import org.organicdesign.fp.collections.ImSet;
import org.organicdesign.fp.collections.ImSortedMap;
import org.organicdesign.fp.collections.ImSortedSet;
import org.organicdesign.fp.collections.PersistentHashMap;
import org.organicdesign.fp.collections.PersistentHashSet;
import org.organicdesign.fp.collections.PersistentTreeMap;
import org.organicdesign.fp.collections.PersistentTreeSet;
import org.organicdesign.fp.collections.PersistentVector;
import org.organicdesign.fp.tuple.Tuple2;
import org.organicdesign.fp.tuple.Tuple3;
import org.organicdesign.fp.xform.Xform;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.organicdesign.fp.StaticImports.*;

public class StaticImportsTest {

    @Test public void mutableMapTest() {
        assertEquals(Collections.emptyMap(), mutableMap());
        assertEquals(Collections.singletonMap(35, "Thirty Five"),
                     mutableMap(tup(35, "Thirty Five")));

        Map<String,Integer> control = new HashMap<>();
        control.put("one", 1);
        control.put("two", 2);
        control.put("three", 3);
        assertEquals(control,
                     mutableMap(tup("one", 1),
                                tup("two", 2),
                                tup("three", 3)));
    }

    @Test public void mutableSetTest() {
        assertEquals(Collections.emptySet(), mutableSet());
        assertEquals(Collections.singleton("Thirty Five"),
                     mutableSet("Thirty Five"));

        Set<Integer> control = new HashSet<>();
        control.add(1);
        control.add(2);
        control.add(3);
        assertEquals(control,
                     mutableSet(1, 2, 3));
    }

    @Test public void mutableVecTest() {
        assertEquals(Collections.emptyList(), mutableVec());
        assertEquals(Collections.singletonList("Thirty Five"),
                     mutableVec("Thirty Five"));

        List<Integer> control = new ArrayList<>();
        control.add(1);
        control.add(2);
        control.add(3);
        assertEquals(control,
                     mutableVec(1, 2, 3));
    }

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
    @Test public void testMap() {
        assertEquals(PersistentHashMap.EMPTY, map());
        assertEquals(PersistentHashMap.EMPTY, map((Map.Entry[]) null));
        assertEquals(PersistentHashMap.EMPTY, map(new Map.Entry[0]));
        ImMap<String,Integer> phm = PersistentHashMap.<String,Integer>empty()
                .assoc("Hi", 43);
        assertEquals(phm, map(tup("Hi", 43)));
    }

    @SuppressWarnings({"NullArgumentToVariableArgMethod", "RedundantArrayCreation"})
    @Test public void testSet() {
        assertEquals(PersistentHashSet.EMPTY, set());
        assertEquals(PersistentHashSet.EMPTY, set((Map.Entry[]) null));
        assertEquals(PersistentHashSet.EMPTY, set(new Object[0]));
        ImSet<String> phm = PersistentHashSet.<String>empty()
                .put("Hi");
        assertEquals(phm, set("Hi"));
    }

    @Test public void testSortedMap() {
        ImList<Map.Entry<String,Integer>> ls = PersistentVector.empty();
        ls = ls.append(tup("Hi", 1))
               .append(Tuple2.of("Bye", -99))
               .append(Tuple2.of("hi", 33))
               .append(tup("bye", -9999));
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

    @Test public void testSortedSet() {
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

    @Test public void testTup() {
        assertEquals(Tuple2.of("Hello", -32), tup("Hello", -32));
        assertEquals(Tuple3.of("Hello", -32, 9.5), tup("Hello", -32, 9.5));
    }

    @SuppressWarnings({"NullArgumentToVariableArgMethod", "RedundantArrayCreation"})
    @Test public void testVec() {
        assertEquals(PersistentVector.EMPTY, vec());
        assertEquals(PersistentVector.EMPTY, vec((Map.Entry[]) null));
        assertEquals(PersistentVector.EMPTY, vec(new Map.Entry[0]));
        ImList<String> ls = PersistentVector.<String>empty()
                .append("Hi");
        assertEquals(ls, vec("Hi"));

        Random r = new Random();
        boolean showEditable = r.nextBoolean();
        boolean showDeleted = r.nextBoolean();

        // This just shows whether Objects::nonNull generates a warning in your editor.
        vec(showEditable ? tup("showEditable", "1") : null,
            showDeleted ? tup("showDeleted", "1") : null)
                .filter(Objects::nonNull)
                .toImList();
    }

    @Test public void testXform() {
        assertEquals(Xform.of(Arrays.asList(1,2,3)), xform(Arrays.asList(1,2,3)));
    }

    @Test public void xformArrayTest() {
        assertEquals(Collections.emptyList(), xformArray().toImList());
        assertEquals(Collections.singletonList("Thirty Five"),
                     xformArray("Thirty Five").toImList());

        List<Integer> control = new ArrayList<>();
        control.add(1);
        control.add(2);
        control.add(3);
        assertEquals(control,
                     xformArray(1, 2, 3).toImList());
    }

    @Test public void testXformChars() {
        assertEquals(vec('H', 'e', 'l', 'l', 'o'), xformChars("Hello").toImList());
    }
}
