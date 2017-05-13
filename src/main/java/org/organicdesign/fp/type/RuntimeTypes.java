package org.organicdesign.fp.type;

import org.organicdesign.fp.collections.ImList;
import org.organicdesign.fp.oneOf.OneOf2;

import java.util.HashMap;

/**
 Stores the classes from the compile-time generic type parameters in a vector in the *same order* as the
 generics in the type signature of that class.
 Store them here using {@link #registerClasses(ImList)} to avoid duplication.  For example:

 <pre><code>private static final ImList<Class> CLASS_STRING_INTEGER =
    RuntimeTypes.registerClasses(vec(String.class, Integer.class));</code></pre>

 Now you if you use CLASS_STRING_INTEGER, you are never creating a new vector.
 For a full example of how to use these RuntimeTypes, see {@link OneOf2}.

 This is an experiment in runtime types for Java.  Constructive criticism is appreciated!
 If you write a programming language, your compiler can manage these vectors so that humans don't have to
 ever think about them, except to query them when they want to.  I wanted to do this with arrays, but I didn't trust
 that they wouldn't be mutated, so I used ImList's.
 */
public final class RuntimeTypes {

    // This is a static (mutable) class.  Don't instantiate.
    @Deprecated
    private RuntimeTypes() { throw new UnsupportedOperationException("No instantiation"); }

    private enum Lock { INSTANCE }

    // Keep a single copy of combinations of generic parameters at runtime. These are all arrays for size,
    // cache-closeness, and speed.  But arrays can be modified, so this must be kept private to guard against
    // modification.
    private static final HashMap<ImList<Class>,ImList<Class>> typeMap = new HashMap<>();

    /**
     Use this to prevent duplicate runtime types.
     @param cs an immutable vector of classes to register
     */
    public static ImList<Class> registerClasses(ImList<Class> cs) {
        if (cs == null) {
            throw new IllegalArgumentException("Can't register a null type array");
        }
        ImList<Class> registeredTypes;
        synchronized (Lock.INSTANCE) {
            registeredTypes = typeMap.get(cs);
            if (registeredTypes == null) {
                typeMap.put(cs, cs);
                registeredTypes = cs;
            }
        }
        // We are returning the original array.  If we returned our safe copy, it could be modified!
        return registeredTypes;
    }

    // The one time I thought I needed this I was confused.  I had a class Or which has types Good and Bad.
    // I got confused and passed whatever the good and bad values were when I really wanted just the types Good and Bad.
//    /**
//     Use this to prevent duplicate runtime types.
//     @param os an array of objects whose classes to register
//     */
//    public static ImList<Class> registerObjects(Object... os) {
//        if (os == null) {
//            throw new IllegalArgumentException("Can't register a null object array");
//        }
//        // We aren't doing extra work here because we have to return a different array from what we store
//        // in the hash map.
//        return registerClasses( vec(os).map(o -> o == null ? (Class) null
//                                                           : o.getClass())
//                                       .toImList() );
//    }

    public static String name(Class c) { return (c == null) ? "null" : c.getSimpleName(); }

    static int size() { return typeMap.size(); }
}
