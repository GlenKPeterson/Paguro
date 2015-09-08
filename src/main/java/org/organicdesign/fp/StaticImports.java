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
import org.organicdesign.fp.xform.Transformable;
import org.organicdesign.fp.xform.Xform;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;

/**
 The four methods map(), set(), tup(), and vec() comprise a mini data-definition language.  It's
 wordier than JSON, but still brief for Java and fairly brief over-all.  Of those four methods, only
 tup() uses overloading to take heterogeneous arguments. The other three are the only places in this
 project that use varargs.

 Statically importing the functions in this file is like building a mini language in Java.  As with
 any usage of import *, there will probably be issues if you import 2 different versions of this
 file in your classpath.  Let me know if you find that the convenience is not worth the danger.

 The unmod___() methods are an alternative to Collections.unmodifiable____() for building immutable
 collections.  These will never return null, the closest they get is to return an empty immutable
 collection (the same one is reused).  Also, the unmodifiable interfaces they return have deprecated
 the modification methods so that any attempt to use those methods causes a warning in your IDE and
 compiler.
 */
@SuppressWarnings("UnusedDeclaration")
public class StaticImports {
    // Prevent instantiation
    private StaticImports() { throw new UnsupportedOperationException("No instantiation"); }

    /** Returns a new Tuple2 of the given items. */
    public static <T,U> Tuple2<T,U> tup(T t, U u) { return Tuple2.of(t, u); }

    /** Returns a new Tuple3 of the given items. */
    public static <T,U,V> Tuple3<T,U,V> tup(T t, U u, V v) { return Tuple3.of(t, u, v); }

    /**
     Returns a new PersistentVector of the given items.  This data definition method is one of the
     three methods in this project that support varargs.
     */
    @SafeVarargs
    static public <T> ImList<T> vec(T... items) {
        if ( (items == null) || (items.length < 1) ) { return PersistentVector.empty(); }
        return PersistentVector.ofIter(Arrays.asList(items));
    }

    // I don't know whether this is a worthwhile convenience, or a crutch.
    // See the last two examples in PersistentHashMapTest.testSkipNull() to see how this saves
    // a cast due to the (otherwise slightly evil) varargs.
//    /** Returns a new PersistentVector of the given items, omitting any nulls. */
//    @SafeVarargs
//    public static <T> ImList<T> vecSkipNull(T... items) {
//        return PersistentVector.ofSkipNull(items);
//    }

    /**
     Returns a new PersistentHashMap of the given keys and their paired values.  This data
     definition method is one of the three methods in this project that support varargs.
     */
    @SafeVarargs
    public static <K,V> ImMap<K,V> map(Map.Entry<K,V>... es) {
        if ( (es == null) || (es.length < 1) ) { return PersistentHashMap.empty(); }
        return PersistentHashMap.of(Arrays.asList(es));
    }

    /**
     Returns a new PersistentHashSet of the values.  This data definition method is one of the three
     methods in this project that support varargs.
     */
    @SafeVarargs
    public static <T> ImSet<T> set(T... items) {
        if ( (items == null) || (items.length < 1) ) { return PersistentHashSet.empty(); }
        return PersistentHashSet.of(Arrays.asList(items));
    }

//    /** Returns a new PersistentTreeMap of the given comparable keys and their paired values. */
//    public static <K extends Comparable<K>,V> ImSortedMap<K,V> sortedMap() {
//        return PersistentTreeMap.empty();
//    }

    /**
     Returns a new PersistentTreeMap of the given comparable keys and their paired values, skipping
     any null Entries.
     */
    public static <K extends Comparable<K>,V> ImSortedMap<K,V>
    sortedMap(Iterable<Map.Entry<K,V>> es) {
        return PersistentTreeMap.of(es);
    }

    /**
     Returns a new PersistentTreeMap of the specified comparator and the given key/value pairs.
     */
    public static <K,V> ImSortedMap<K,V>
    sortedMap(Comparator<? super K> c, Iterable<Map.Entry<K,V>> es) {
        return PersistentTreeMap.ofComp(c, es);
    }

    /** Returns a new PersistentTreeSet of the given comparable items. */
    public static <T extends Comparable<T>> ImSortedSet<T> sortedSet(Iterable<T> items) {
        return PersistentTreeSet.of(items);
    }

    /** Returns a new PersistentTreeSet of the given comparator and items. */
    public static <T> ImSortedSet<T> sortedSet(Comparator<? super T> comp, Iterable<T> items) {
        return Xform.of(items).toImSortedSet(comp);
    }

    /**
     If you need to wrap a regular Java collection or other iterable outside this project to perform
     a transformation on it, this method is the most convenient, efficient way to do so - more
     efficient than using the unmod____ methods (if your only purpose is to start a transformation).
     */
    public static <T> Transformable<T> xform(Iterable<T> iterable) {
        return Xform.of(iterable);
    }
}
