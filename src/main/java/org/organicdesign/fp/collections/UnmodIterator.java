package org.organicdesign.fp.collections;

import java.util.Iterator;

import org.organicdesign.fp.FunctionUtils;

/**
 A one-time use, mutable, not-thread-safe way to get each value of the underling collection in
 turn. I experimented with various thread-safe alternatives, but the JVM is optimized around
 iterators so this is the lowest common denominator of collection iteration, even though
 iterators are inherently mutable.

 This is called "Unmod" in the sense that it doesn't modify the underlying collection.  Iterators
 are inherently mutable.  The only safe way to handle them is to pass around IteraBLEs so that
 the ultimate client gets its own, unshared iteraTOR.  Order is not guaranteed.
 */
public interface UnmodIterator<E> extends Iterator<E> {
    /** Use {@link FunctionUtils#unmodIterator(Iterator iter)} instead. */
    // TODO: This code was duplicated in FunctionUtils.  Does it belong there, or here?
    // This should not be public because we can't control instance creation.
    // Also it can shadow same-named static classes in sub-interfaces,
    // causing method overloading, preventing use in method references, and generally causing
    // confusion.
    @Deprecated
    class Wrapper<E> implements UnmodIterator<E> {
        // Iterators are not serializable (today) because they aren't in Java.
        // I'm assuming Java had a good reason for that, but I really don't know.
//        , Serializable {
//        // For serializable.  Make sure to change whenever internal data format changes.
//        private static final long serialVersionUID = 20160903174100L;

        private final Iterator<E> iter;
        Wrapper(Iterator<E> i) { iter = i; }

        @Override public boolean hasNext() { return iter.hasNext(); }
        @Override public E next() { return iter.next(); }

        // Defining equals and hashcode makes no sense because can't call them without changing
        // the iterator which both makes it useless, and changes the equals and hashcode
        // results.
//            @Override public int hashCode() { return iter.hashCode(); }
//            @SuppressWarnings("EqualsWhichDoesntCheckParameterClass") // See Note above.
//            @Override public boolean equals(Object o) { return iter.equals(o); }
    }

    // ========================================= Instance =========================================
//default void forEachRemaining(Consumer<? super E> action)
//boolean hasNext()
//E next()

    /** Not allowed - this is supposed to be unmodifiable */
    @Override @Deprecated default void remove() {
        throw new UnsupportedOperationException("Modification attempted");
    }
}
