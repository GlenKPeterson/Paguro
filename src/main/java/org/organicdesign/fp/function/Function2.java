package org.organicdesign.fp.function;

/**
 This is like Java 8's java.util.function.BiFunction, but retrofitted to turn checked exceptions
 into unchecked ones.
 */
public interface Function2<A,B,R> {
    /** Implement this one method and you don't have to worry about checked exceptions. */
    R apply(A a, B b) throws Exception;

    /**
     The class that takes a consumer as an argument uses this convenience method so that it
     doesn't have to worry about checked exceptions either.
     */
    default R apply_(A a, B b) {
        try {
            return apply(a, b);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

// Don't think this is necessary.  Is it?
//    default BiFunction<A,B,R> asBiFunction() {
//        return (A a, B b) -> apply_(a, b);
//    }
}
