// Copyright 2014-02-09 PlanBase Inc. & Glen Peterson
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
import java.util.List;
import java.util.Map;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 Utilities that functional programmers would want, but aren't supplied in Java 7 (or 8)
 */
public class FunctionUtils {

    // I don't want any instances of this class.
    private FunctionUtils() {}

    /** A predicate that always returns true.  Use accept() for a type-safe version of this predicate. */
    public static final Predicate<Object> ACCEPT = new Predicate<Object>() {
        @Override
        public boolean test(Object t) {
            return true;
        }

        @Override
        @SuppressWarnings("unchecked")
        public Predicate<Object> and(Predicate<? super Object> other) {
            return (Predicate<Object>) other;
        }

        @Override
        public Predicate<Object> negate() {
            return REJECT;
        }

        @Override
        public Predicate<Object> or(Predicate<? super Object> other) {
            return this;
        }
    };

    /** A predicate that always returns false. Use reject() for a type-safe version of this predicate. */
    public static final Predicate<Object> REJECT = new Predicate<Object>() {
        @Override
        public boolean test(Object t) {
            return false;
        }

        @Override
        public Predicate<Object> and(Predicate<? super Object> other) {
            return this;
        }

        @Override
        public Predicate<Object> negate() {
            return ACCEPT;
        }

        @Override
        @SuppressWarnings("unchecked")
        public Predicate<Object> or(Predicate<? super Object> other) {
            return (Predicate<Object>) other;
        }
    };

    /** Returns a type-safe version of the ACCEPT predicate. */
    @SuppressWarnings("unchecked")
    public static <T> Predicate<T> accept() { return (Predicate<T>) ACCEPT; }

    /** Returns a type-safe version of the REJECT predicate. */
    @SuppressWarnings("unchecked")
    public static <T> Predicate<T> reject() { return (Predicate<T>) REJECT; }

    /**
     Composes multiple predicates into a single predicate to potentially minimize trips through
     the source data.  The resultant predicate will loop through the predicates for each item in
     the source, but for few predicates and many source items, that takes less memory.  Considers
     no predicate to mean "accept all."  Use only accept()/ACCEPT and reject()/REJECT since
     function comparison is done by reference.

     @param in the predicates to test in order.  Nulls and ACCEPT predicates are ignored.  Any
     REJECT predicate will cause this entire method to return a single REJECT predicate.  No
     predicates means ACCEPT.

     @param <T> the type of object to predicate on.

     @return a predicate which returns true if all the input predicates return true, false otherwise.
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
            return accept(); // No predicates means to accept all.
        } else if (out.size() == 1) {
            return out.get(0);
        } else {
            return t -> {
                for (Predicate<T> f : out) {
                    if (!f.test(t)) {
                        return false;
                    }
                }
                return true;
            };
        }
    }

    /**
     Composes multiple predicates into a single predicate to potentially minimize trips through
     the source data.  The resultant predicate will loop through the predicates for each item in
     the source, but for few predicates and many source items, that takes less memory.  Considers
     no predicate to mean "reject all."  Use only accept()/ACCEPT and reject()/REJECT since
     function comparison is done by reference.

     @param in the predicates to test in order.  Nulls and REJECT predicates are ignored.  Any
     ACCEPT predicate will cause this entire method to return the ACCEPT predicate.
     No predicates means REJECT.

     @param <T> the type of object to predicate on.

     @return a predicate which returns true if any of the input predicates return true,
     false otherwise.
     */
    @SafeVarargs
    public static <T> Predicate<T> or(Predicate<T>... in) {
        if ( (in == null) || (in.length < 1) ) {
            return reject();
        }
        final List<Predicate<T>> out = new ArrayList<>();
        for (Predicate<T> f : in) {
            if ( (f == null) || (f == REJECT)) {
                continue;
            }
            if (f == ACCEPT) {
                // One reject in an or-list means to always accept.
                return accept();
            }
            out.add(f);
        }
        if (out.size() < 1) {
            return reject(); // No predicates means to reject all.
        } else if (out.size() == 1) {
            return out.get(0);
        } else {
            return t -> {
                for (Predicate<T> f : out) {
                    if (f.test(t)) {
                        return true;
                    }
                }
                return false;
            };
        }
    }

    /**
     A function that returns its input unchanged.
     Use identity() for a type-safe version of this function.
     */
    public static final Function<?,?> IDENTITY = new Function<Object,Object>() {

        @Override
        public Object apply(Object t) {
            return t;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <V> Function<V, Object> compose(Function<? super V, ?> before) {
            // Composing any function with the identity function has no effect on the original
            // function (by definition of identity) - just return it.
            return (Function<V, Object>) before;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <V> Function<Object, V> andThen(Function<? super Object, ? extends V> after) {
            return (Function<Object, V>) after;
        }
    };

    /** Returns a type-safe version of the IDENTITY function. */
    @SuppressWarnings("unchecked")
    public static <T> Function<T,T> identity() { return (Function<T,T>) IDENTITY; }

    /** Returns a String showing the type and first few elements of a Collection */
    public static <T> String toString(Iterable<T> iterable) {
        if (iterable == null) {
            return "null";
        }
        StringBuilder sB = new StringBuilder();

        sB.append(iterable.getClass().getSimpleName());
        sB.append("(");

        int i = 0;
        for (T item : iterable) {
            if (i > 4) {
                sB.append("...");
                break;
            } else if (i > 0) {
                sB.append(",");
            }
            sB.append(String.valueOf(item));
            i++;
        }

        sB.append(")");
        return sB.toString();
    }

    /** Returns a String showing the type and first few elements of a map */
    public static <A,B> String toString(Map<A,B> map) {
        if (map == null) {
            return "null";
        }
        StringBuilder sB = new StringBuilder();

        sB.append(map.getClass().getSimpleName());
        sB.append("(");

        int i = 0;
        for (Map.Entry<A,B> item : map.entrySet()) {
            if (i > 4) {
                sB.append("...");
                break;
            } else if (i > 0) {
                sB.append(",");
            }
            sB.append("Entry(").append(String.valueOf(item.getKey())).append(",");
            sB.append(String.valueOf(item.getValue())).append(")");
            i++;
        }

        sB.append(")");
        return sB.toString();
    }

    /** Returns a String showing the type and first few elements of an array */
    public static String toString(Object[] as) {
        if (as == null) {
            return "null";
        }
        StringBuilder sB = new StringBuilder();
        sB.append("Array");

        if ( (as.length > 0) && (as[0] != null) ) {
            sB.append(" of ");
            sB.append(as[0].getClass().getSimpleName());
        }

        sB.append("(");

        int i = 0;
        for (Object item : as) {
            if (i > 4) {
                sB.append("...");
                break;
            } else if (i > 0) {
                sB.append(",");
            }
            sB.append(String.valueOf(item));
            i++;
        }

        sB.append(")");
        return sB.toString();
    }
}
