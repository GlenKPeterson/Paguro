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

    // Keep a single copy of combinations of generic parameters at runtime.
    // Thanks to martynas at https://stackoverflow.com/a/27378976/1128668 for the trie implementation!
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
            // Synchronize on the parent node to inspect it or add children.
            // I'd love it if someone can confirm this is safe.
            // I think all the items involved are immutable except the contents of the HashMap,
            // which is why we need to access it in a synchronized block.
            //noinspection SynchronizationOnLocalVariableOrMethodParameter
            synchronized (node) {
                if (node.map.containsKey(currClass)) {
                    // Node becomes the next node down in the trie.  We aren't synchronized on it yet,
                    // But we're neither querying nor modifying it.
                    node = node.map.get(currClass);
                } else {
                    // Still synchronized on the same node to query and modify it.
                    // NOT modifying node.list in place - this creates a lightweight modified copy.
                    ListAndMap newNode = new ListAndMap(node.list.append(currClass));
                    node.map.put(currClass, newNode);
//                    size++;
                    // Node becomes the next node down in the trie.  We aren't synchronized on it yet,
                    // But we're neither querying nor modifying it.
                    node = newNode;
                }
            }
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

    @SuppressWarnings("rawtypes")
    private static class ListAndMap {
        public final @NotNull ImList<Class> list;
        public final @NotNull Map<Class, ListAndMap> map = new HashMap<>();
        public ListAndMap(@NotNull ImList<Class> l) { list = l; }
    }
}
