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

import java.util.Comparator;

/**
 Implement compare() and hash() and you get a 100% compatible eq() for free.  If you don't need
 Comparator, just use {@link Equator}.  Typical implementations compare() throw an
 IllegalArgumentException if one parameter is null (if both are null, it's probably OK to return 0).
 More at {@link #eq(Object, Object)}.

 A common mistake is to implement a ComparisonContext, Equator, or Comparator as an anonymous class
 or lambda, then be surprised when it is can't be serialized, or is deserialized as null.  These
 one-off classes are often singletons, which are easiest to serialize as enums.  If your
 implementation requires generic type parameters, look at how {@link #defCompCtx()} tricks the type
 system into using generic type parameters (correctly) with an enum.
*/
public interface ComparisonContext<T> extends Equator<T>, Comparator<T> {
    /** Returns true if the first object is less than the second. */
    default boolean lt(T o1, T o2) { return compare(o1, o2) < 0; }

    /** Returns true if the first object is less than or equal to the second. */
    default boolean lte(T o1, T o2) { return compare(o1, o2) <= 0; }

    /** Returns true if the first object is greater than the second. */
    default boolean gt(T o1, T o2) { return compare(o1, o2) > 0; }

    /** Returns true if the first object is greater than or equal to the second. */
    default boolean gte(T o1, T o2) { return compare(o1, o2) >= 0; }

    /**
     The default implementation of this method returns false if only one parameter is null then
     checks if compare() returns zero.

     Only null is equal to null.  If we are passed only one null value, we return false (two
     nulls are always equal).  Many correct implementations of compare(null, nonNull) throw
     IllegalArgumentExceptions if one argument is null because most objects cannot be meaningfully
     be orderd with respect to null, but that's OK because we check for nulls first, then check
     compare().
     */
    @Override default boolean eq(T o1, T o2) {
        if (o1 == null) { return (o2 == null); }

        // Now they are equal if compare returns zero.
        return compare(o1, o2) == 0;
    }

    // Enums are serializable and lambdas are not.  Therefore enums make better singletons.
    enum CompCtx implements ComparisonContext<Comparable<Object>> {
        DEFAULT {
            @Override
            public int hash(Comparable<Object> o) { return (o == null) ? 0 : o.hashCode(); }

            @SuppressWarnings("ConstantConditions")
            @Override public int compare(Comparable<Object> o1, Comparable<Object> o2) {
                if (o1 == o2) { return 0; }
                if (o1 == null) {
                    return - (o2.compareTo(o1));
                }
                return o1.compareTo(o2);
            }
        }
    }

    @SuppressWarnings("unchecked")
    static <T> ComparisonContext<T> defCompCtx() { return (ComparisonContext<T>) CompCtx.DEFAULT; }
}
