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

import java.util.Map;

/**
 A couple utilities that aren't supplied in Java.  Most of these have been moved to other classes.  For instance,
 toString(Iterable) has been moved to UnIterable.  All the functions for composing predicates are now on Function1.
 */
public class FunctionUtils {

    // I don't want any instances of this class.
    private FunctionUtils() {}


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
            sB.append("<");
            sB.append(as[0].getClass().getSimpleName());
            sB.append(">");
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
