// Copyright 2013-12-31 PlanBase Inc. & Glen Peterson
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

import java.util.ArrayList;
import java.util.List;

/**
 This is a bit like Java 8's java.util.function.Predicate, but retrofitted to turn checked
 exceptions into unchecked ones in Java 5, 6, and 7.  I originally called this Filter and used
 apply() as the main method name to work more like the other functions, but Java 8 uses Predicate
 and test().  Predicate is a fine name, but using a different method name from other
 functions is just ugly.
 */
public abstract class Predicate<T> {
    /** Implement this one method and you don't have to worry about checked exceptions. */
    public abstract boolean test(T t) throws Exception;

    /**
     The class that takes a consumer as an argument uses this convenience method so that it
     doesn't have to worry about checked exceptions either.
     */
    public boolean test_(T t) {
        try {
            return test(t);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /** A filter that always returns true.  Use accept() for a type-safe version of this filter. */
    public static final Predicate<Object> ACCEPT = new Predicate<Object>() {
        @Override
        public boolean test(Object t) throws Exception {
            return true;
        }
    };

    /** A filter that always returns false. Use reject() for a type-safe version of this filter. */
    public static final Predicate<Object> REJECT = new Predicate<Object>() {
        @Override
        public boolean test(Object t) throws Exception {
            return false;
        }
    };

    /** Returns a type-safe version of the ACCEPT filter. */
    @SuppressWarnings("unchecked")
    public static <T> Predicate<T> accept() { return (Predicate<T>) ACCEPT; }

    /** Returns a type-safe version of the REJECT filter. */
    @SuppressWarnings("unchecked")
    public static <T> Predicate<T> reject() { return (Predicate<T>) REJECT; }

    /** Returns a filter that returns the boolean opposite of the given filter. */
    public static <T> Predicate<T> not(final Predicate<T> f) {
        if (ACCEPT == f) { return reject(); }
        if (REJECT == f) { return accept(); }
        return new Predicate<T>() {
            @Override
            public boolean test(T t) throws Exception {
                return !f.test(t);
            }
        };
    }

    /**
     Composes multiple filters into a single filter to potentially minimize trips through
     the source data.  The resultant filter will loop through the filters for each item in the
     source, but for few filters and many source items, that takes less memory.  Considers no
     filter to mean "accept all."  This decision is based on the meaning of the word "filter" and
     may or may not prove useful in practice.  Please use the accept()/ACCEPT and reject()/REJECT
     sentinel values in this abstract class since function comparison is done by reference.

     @param in the filters to test in order.  Nulls and ACCEPT filters are ignored.  Any REJECT
     filter will cause this entire method to return a single REJECT filter.  No filters means
     ACCEPT.

     @param <T> the type of object to filter on.

     @return a filter which returns true if all the input filters return true, false otherwise.
     */
    @SafeVarargs
    public static <T> Predicate<T> and(Predicate<T>... in) {
        if ( (in == null) || (in.length < 1) ) {
            return accept();
        }
        final List<Predicate<T>> out = new ArrayList<>();
        for (Predicate<T> f : in) {
            if ((f == null) || (f == ACCEPT)) {
                continue;
            }
            if (f == REJECT) {
                // One reject in an and-list means to always reject.
                return reject();
            }
            out.add(f);
        }
        if (out.size() < 1) {
            return accept(); // No filters means to accept all.
        } else if (out.size() == 1) {
            return out.get(0);
        } else {
            return new Predicate<T>() {
                @Override
                public boolean test(T t) throws Exception {
                    for (Predicate<T> f : out) {
                        if (!f.test(t)) {
                            return false;
                        }
                    }
                    return true;
                }
            };
        }
    }
}
