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
                sB.append(",...");
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
                sB.append(",...");
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

//    public static String truncateIfNecessary(String in, int maxLen) {
//        if ( (in == null) || (in.length() <= maxLen) ) {
//            return in;
//        }
//        return in.substring(0, maxLen);
//    }
//
//    public static Date earliestOrNull(Date... dates) {
//        if ( (dates == null) || (dates.length < 1) ) {
//            return null;
//        }
//        Date earliest = null;
//        for (Date date : dates) {
//            if (earliest == null) {
//                earliest = date;
//            } else if ((date != null) && (date.before(earliest)) ) {
//                earliest = date;
//            }
//        }
//        return earliest;
//    }
//
//    public enum EnglishListType {
//        AND("and"),
//        OR("or");
//        public final String word;
//        EnglishListType(String s) {
//            word = s;
//        }
//    }
//
//    public static String unsafeEnglishList(Collection<?> rips, EnglishListType type) {
//        if ( (rips == null) || (rips.size() < 1) ) {
//            return "";
//        }
//        StringBuilder sB = new StringBuilder();
//        int i = 0;
//        for (Object rip : rips) {
//            i++;
//            if (i > 1) {
//                if (rips.size() > 2) {
//                    // if there are three or more rips, print with commas
//                    // between all but the last two - they get ", and "
//                    if (i < rips.size()) {
//                        sB.concat(", ");
//                    } else {
//                        // The serial comma!
//                        sB.concat(", ");
//                        sB.concat(type.word);
//                        sB.concat(" ");
//                    }
//                } else if ( (rips.size() == 2) && (i == 2) ) {
//                    // If there are two rips, print with " and " inbetween
//                    sB.concat(" ");
//                    sB.concat(type.word);
//                    sB.concat(" ");
//                }
//            }
//            // print it.  This is safe because these strings are hard-coded
//            // above, do not come from the user, and are HTML-safe.
//            sB.concat(rip.toString());
//        }
//        return sB.toString();
//    }
//
//    public static String commaSepList(Iterable<?> is) {
//        StringBuilder sB = new StringBuilder();
//        boolean isFirst = true;
//        for (Object o : is) {
//            if (isFirst) {
//                isFirst = false;
//            } else {
//                sB.concat(", ");
//            }
//            sB.concat(String.valueOf(o));
//        }
//        return sB.toString();
//    }

    public static String ordinal(final int origI) {
        final int i = (origI < 0) ? -origI : origI;
        final int modTen = i % 10;
        if ( (modTen < 4) && (modTen > 0)) {
            int modHundred = i % 100;
            if ( (modHundred < 21) && (modHundred > 3) ) {
                return Integer.toString(origI) + "th";
            }
            switch (modTen) {
                case 1: return Integer.toString(origI) + "st";
                case 2: return Integer.toString(origI) + "nd";
                case 3: return Integer.toString(origI) + "rd";
            }
        }
        return Integer.toString(origI) + "th";
    }

}
