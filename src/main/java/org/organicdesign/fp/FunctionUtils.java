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

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.ListIterator;

import org.organicdesign.fp.collections.UnmodIterator;
import org.organicdesign.fp.collections.UnmodListIterator;

/**
 A dumping ground for utility functions that aren't useful enough to belong in StaticImports.

 The unmod___() methods are an alternative to Collections.unmodifiable____().  They provide
 unmodifiable wrappers to protect mutable collections for sharing.  Except for the Iterators,
 the returned classes are Serializable.  These will never return null, the closest they get is to
 return an empty unmodifiable collection.  The unmodifiable interfaces they return have deprecated
 the modification methods so that any attempt to use those methods causes a warning in your IDE and
 compiler.
 */
public class FunctionUtils {

    // I don't want any instances of this class.
    private FunctionUtils() {
        throw new UnsupportedOperationException("No instantiation");
    }

    public static String stringify(Object o) {
        if (o == null) { return "null"; }
        if (o instanceof String) { return "\"" + o + "\""; }
        return o.toString();
    }

    // Not Needed in Java 8.
//    /** Returns a String showing the type and first few elements of a map */
//    public static <A,B> String mapToString(Map<A,B> map) {
//        if (map == null) {
//            return "null";
//        }
//        StringBuilder sB = new StringBuilder();
//
//        sB.append(map.getClass().getSimpleName());
//        sB.append("(");
//
//        int i = 0;
//        for (Map.Entry<A,B> item : map.entrySet()) {
//            if (i > 4) {
//                sB.append(",...");
//                break;
//            } else if (i > 0) {
//                sB.append(",");
//            }
//            sB.append("Entry(").append(String.valueOf(item.getKey())).append(",");
//            sB.append(String.valueOf(item.getValue())).append(")");
//            i++;
//        }
//
//        sB.append(")");
//        return sB.toString();
//    }
//
//    /** Returns a String showing the type and first few elements of an array */
//    public static String arrayToString(Object[] as) {
//        if (as == null) {
//            return "null";
//        }
//        StringBuilder sB = new StringBuilder();
//        sB.append("Array");
//
//        if ( (as.length > 0) && (as[0] != null) ) {
//            sB.append("<");
//            sB.append(as[0].getClass().getSimpleName());
//            sB.append(">");
//        }
//
//        sB.append("(");
//
//        int i = 0;
//        for (Object item : as) {
//            if (i > 4) {
//                sB.append(",...");
//                break;
//            } else if (i > 0) {
//                sB.append(",");
//            }
//        }
//
//        sB.append(")");
//        return sB.toString();
//    }

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

// EqualsWhichDoesntCheckParameterClass Note:
// http://codereview.stackexchange.com/questions/88333/is-one-sided-equality-more-helpful-or-more-confusing-than-quick-failure
// "There is no one-sided equality. If it is one-sided, that is it's asymmetric, then it's just
// wrong."  Which is a little ironic because with inheritance, there are many cases in Java
// where equality is one-sided.

    // ========================================== Classes ==========================================

    // The point of these classes existing at all are to wrap mutable collections for safe
    // sharing.  Use them either to retrofit existing Java code, or to wrap mutable collections
    // you may use for performance reasons.
    //
    // These are true, named classes instead of anonymous implementations so that they can properly
    // implement Serializable.
    //
    // These classes seem to have to be public in order to compile without
    // "remove() in org.organicdesign.fp.collections.UnmodIterator is defined in an inaccessible class or interface"
    //
    // They belong here, instead of being a static class in the Unmod___ interfacies, so that they
    // don't overshadow same-named static classes in sub-interfaces, prevent use in method
    // references (due to overloading), make overloading in subclasses onerous, or generally cause\
    // confusion.


    /**
     Wraps an iterator.  Not Serializable.  You probably want to use this by calling
     {@link #unmodIterator(Iterator)}.
     */
    @SuppressWarnings("WeakerAccess")
    public static class UnmodifiableIterator<E> implements UnmodIterator<E> {
        // Iterators are not serializable (today) because they aren't in Java.
        // I'm assuming Java had a good reason for that, but I really don't know.
//        , Serializable {
//        // For serializable.  Make sure to change whenever internal data format changes.
//        private static final long serialVersionUID = 20160903174100L;

        private final Iterator<E> iter;
        private UnmodifiableIterator(Iterator<E> i) { iter = i; }

        @Override public boolean hasNext() { return iter.hasNext(); }
        @Override public E next() { return iter.next(); }

        // Defining equals and hashcode makes no sense because can't call them without changing
        // the iterator which both makes it useless, and changes the equals and hashcode
        // results.
//            @Override public int hashCode() { return iter.hashCode(); }
//            @SuppressWarnings("EqualsWhichDoesntCheckParameterClass") // See Note above.
//            @Override public boolean equals(Object o) { return iter.equals(o); }
    }

    /** Wraps a list iterator.  The is NOT serializable. */
    @SuppressWarnings("WeakerAccess")
    public static class UnmodifiableListIterator<T> implements UnmodListIterator<T>, Serializable {
        private final ListIterator<T> iter;
        private UnmodifiableListIterator(ListIterator<T> is) { iter = is; }

        // For serializable.  Make sure to change whenever internal data format changes.
        private static final long serialVersionUID = 20160918033000L;

        @Override public boolean hasNext() { return iter.hasNext(); }
        @Override public T next() { return iter.next(); }
        @Override public boolean hasPrevious() { return iter.hasPrevious(); }
        @Override public T previous() { return iter.previous(); }
        @Override public int nextIndex() { return iter.nextIndex(); }
        // Defining equals and hashcode makes no sense because can't call them without changing
        // the iterator which both makes it useless, and changes the equals and hashcode
        // results.
//            @Override public int hashCode() { return iter.hashCode(); }
//            @SuppressWarnings("EqualsWhichDoesntCheckParameterClass") // See Note above.
//            @Override public boolean equals(Object o) { return iter.equals(o); }
    }

    // ========================================== Empties ==========================================

    // I had originally provided special implementations for empty collections and iterators.
    // I started to further enhance these with Serializable singletons with a defensive
    // readResolve method to defend against deserializing non-singleton instances.  But that
    // added a lot of complexity without adding a lot of real value.  It also increased the jar
    // file size and I'm increasingly finding that the JVM optimizes best with minimal code size
    // (and maximum reuse).  So I picked simple.  Some implementations are left commented out.

    /** Returns an empty unmodifiable iterator.  The result is not serializable. */
    public static <T> UnmodifiableIterator<T> emptyUnmodIterator() {
        return new UnmodifiableIterator<>(Collections.emptyIterator());
    }

    /** Returns an empty list iterator.  The result is NOT serializable. */
    public static <T> UnmodListIterator<T> emptyUnmodListIterator() {
        return new UnmodifiableListIterator<>(Collections.emptyListIterator());
    }

    /**
     Returns an unmodifiable version of the given iterator.  The result is NOT serializable.
     You could pass a partially used-up iterator to this method, but that's probably something you
     want to avoid.
     */
    public static <T> UnmodIterator<T> unmodIterator(Iterator<T> iter) {
        return ( (iter == null) || !iter.hasNext() ) ? emptyUnmodIterator() :
               (iter instanceof UnmodIterator)       ? (UnmodIterator<T>) iter :
               new UnmodifiableIterator<>(iter);
    }
}
