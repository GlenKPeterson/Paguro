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

import java.util.HashMap;
import java.util.Map;

import org.organicdesign.fp.Option;
import org.organicdesign.fp.tuple.Tuple3;

/** A three-argument, exception-safe functional interface. */
@FunctionalInterface
public interface Function3<A,B,C,R> {
    /** Implement this one method and you don't have to worry about checked exceptions. */
    R applyEx(A a, B b, C c) throws Exception;

    /**
     The class that takes a consumer as an argument uses this convenience method so that it
     doesn't have to worry about checked exceptions either.
     */
    default R apply(A a, B b, C c) {
        try {
            return applyEx(a, b, c);
        } catch (RuntimeException re) {
            throw re;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     Use only on pure functions with no side effects.  Wrap an expensive function with this and for each input
     value, the output will only be computed once.  Subsequent calls with the same input will return identical output
     very quickly.  Please note that the parameters to f need to implement equals() and hashCode() correctly
     for this to work correctly and quickly.  Also, make sure your domain is very small!  This function uses O(n^3)
     memory.
     */
    static <A,B,C,Z> Function3<A,B,C,Z> memoize(Function3<A,B,C,Z> f) {
        return new Function3<A,B,C,Z>() {
            private final Map<Tuple3<A,B,C>,Option<Z>> map = new HashMap<>();
            @Override
            public synchronized Z applyEx(A a, B b, C c) throws Exception {
                Tuple3<A,B,C> t3 = Tuple3.of(a, b, c);
                Option<Z> val = map.get(t3);
                if (val != null) { return val.get(); }
                Z ret = f.apply(a, b, c);
                map.put(t3, Option.of(ret));
                return ret;
            }
        };
    }

}
