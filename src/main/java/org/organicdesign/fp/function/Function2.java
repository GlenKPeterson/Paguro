// Copyright 2015-04-13 PlanBase Inc. & Glen Peterson
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
package org.organicdesign.fp.function;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import org.organicdesign.fp.Option;
import org.organicdesign.fp.tuple.Tuple2;

/**
 This is like Java 8's java.util.function.BiFunction, but retrofitted to turn checked exceptions
 into unchecked ones.
 */
@FunctionalInterface
public interface Function2<A,B,R> extends BiFunction<A,B,R> {
    /** Implement this one method and you don't have to worry about checked exceptions. */
    R applyEx(A a, B b) throws Exception;

    /**
     The class that takes a consumer as an argument uses this convenience method so that it
     doesn't have to worry about checked exceptions either.
     */
    @Override default R apply(A a, B b) {
        try {
            return applyEx(a, b);
        } catch (RuntimeException re) {
            throw re;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("ConstantConditions")
    Comparator<Comparable<Object>> DEFAULT_COMPARATOR =
            (o1, o2) -> {
                if (o1 == o2) { return 0; }
                if (o1 == null) {
                    if (o2 == null) {
                        return 0;
                    }
                    return -o2.compareTo(o1);
                }
                return o1.compareTo(o2);
            };

    @SuppressWarnings("unchecked")
    static <T> Comparator<T> defaultComparator() { return (Comparator<T>) DEFAULT_COMPARATOR; }


// Don't think this is necessary.  Is it?
//    default BiFunction<A,B,R> asBiFunction() {
//        return (A a, B b) -> apply(a, b);
//    }

    /**
     Use only on pure functions with no side effects.  Wrap an expensive function with this and for each input
     value, the output will only be computed once.  Subsequent calls with the same input will return identical output
     very quickly.  Please note that the parameters to f need to implement equals() and hashCode() correctly
     for this to work correctly and quickly.
     */
    static <A,B,Z> Function2<A,B,Z> memoize(Function2<A,B,Z> f) {
        return new Function2<A,B,Z>() {
            private final Map<Tuple2<A,B>,Option<Z>> map = new HashMap<>();
            @Override
            public synchronized Z applyEx(A a, B b) throws Exception {
                Tuple2<A,B> t = Tuple2.of(a, b);
                Option<Z> val = map.get(t);
                if (val != null) { return val.get(); }
                Z ret = f.apply(a, b);
                map.put(t, Option.of(ret));
                return ret;
            }
        };
    }
}
