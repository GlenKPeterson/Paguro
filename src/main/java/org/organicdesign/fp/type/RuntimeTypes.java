package org.organicdesign.fp.type;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.organicdesign.fp.collections.ImList;

import java.util.HashMap;
import java.util.Map;

import static org.organicdesign.fp.FunctionUtils.stringify;
import static org.organicdesign.fp.StaticImports.vec;

/**
 * Stores the classes from the compile-time generic type parameters in a vector in the *same order* as the
 * generics in the type signature of that class.
 * Store them here using {@link #registerClasses(Class[])} to avoid duplication.  For example:
 *
 * <pre><code>private static final ImList<Class> CLASS_STRING_INTEGER =
 *    RuntimeTypes.registerClasses(vec(String.class, Integer.class));</code></pre>
 *
 * Now you if you use CLASS_STRING_INTEGER, you are never creating a new vector.
 * For a full example of how to use these RuntimeTypes, see {@link org.organicdesign.fp.oneOf.OneOf2}.
 *
 * This is an experiment in runtime types for Java.  Constructive criticism is appreciated!
 * If you write a programming language, your compiler can manage these vectors so that humans don't have to
 * ever think about them, except to query them when they want to.
 *
 * I believe this class is thread-safe.
 */
public final class RuntimeTypes {

    // This is a static (mutable) class.  Don't instantiate.
    @Deprecated
    private RuntimeTypes() { throw new UnsupportedOperationException("No instantiation"); }

    // Keep a single copy of combinations of generic parameters at runtime in a trie.
    private static final @NotNull ListAndMap root = new ListAndMap(vec());

    // This is NOT thread-safe - for testing only!
//    static int size = 0;

    /**
     * Use this to register runtime type signatures
     * @param cs an array of types
     * @return An immutable vector of those types.  Given the same types, always returns the same vector.
     */
    @SuppressWarnings("rawtypes")
    public static @NotNull ImList<Class> registerClasses(@NotNull Class @NotNull ... cs) {
        // Walk the trie to find the ImList corresponding to this array.
        ListAndMap node = root;
        for (Class currClass : cs) {
            node = node.next(currClass);
        }
        return node.list;
    }

    @SuppressWarnings("rawtypes")
    public static @NotNull String name(@Nullable Class c) { return (c == null) ? "null" : c.getSimpleName(); }

    @SuppressWarnings("rawtypes")
    public static @NotNull String union2Str(
            Object item,
            @NotNull ImList<Class> types
    ) {
            StringBuilder sB = new StringBuilder();
            sB.append(stringify(item)).append(":");
            boolean isFirst = true;
            for (Class c : types) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    sB.append("|");
                }
                sB.append(RuntimeTypes.name(c));
            }
            return sB.toString();
    }

    // Thanks to martynas on StackOverflow for the HashMap-based trie implementation!
    // https://stackoverflow.com/a/27378976/1128668
    @SuppressWarnings("rawtypes")
    private static class ListAndMap {
        public final @NotNull ImList<Class> list;
        // Mutable field is private.
        private final @NotNull Map<Class, ListAndMap> map = new HashMap<>();
        public ListAndMap(@NotNull ImList<Class> l) { list = l; }

        // Synchronize on this node to inspect it or add children.
        // This avoids contention with other threads accessing any other nodes in this trie.
        // The list is immutable and threadsafe, the HashMap is neither.
        // That's why we access the HashMap in a tiny, synchronized method.
        // Everything from the start of the read to the end of the modification is therefore atomic.
        public synchronized ListAndMap next(@NotNull Class currClass) {
            ListAndMap next = map.get(currClass);
            if (next == null) {
                // next is null when there is a new class in an existing sequence, or an entirely new sequence.
                // Make the next node in this sequence and return it.
                //
                // Thread-safety:
                // list.append() creates a threadsafe, lightweight, modified copy of the list.
                // Still need to be synchronized between reading and writing the hashmap,
                // but the old and new immutable lists can be shared freely across threads throughout.
                next = new ListAndMap(list.append(currClass));
                map.put(currClass, next);
//                    size++;
            }
            return next;
        }
    }
}
