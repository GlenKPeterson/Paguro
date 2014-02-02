fp4java7
========

Functional Programming tools for Java 7.  Specifically the focus will be on collections, but a lot of general purpose functional tools are necessary, such as "second-class" functions.  :-)

The most interesting classes are in: src/main/java/org/organicdesign/fp/sequence.  The Sequence model implemented here is a lazy, immutable, persistent (memoized/cached), type-safe, thread-safe storage for finite data sources that fit in memory (because those that don't cannot be memoized/cached).  It's most similar to the Clojure sequence abstraction, but it's pure Java and type-safe.

One usage starting point begins with a Java Iterator wrapped in a SequenceFromIterator.  The other is to start from SequenceImpl (Implementation).  Various operations return new objects that lazily carry those operations out.  map() produces a SequenceMapped.  filter() produces a SequenceFiltered().  In this way, multiple operations can be chained before actually evaluating anything.  Then evaluation can be done in the smallest possible increments.

The end-points are many and currently unimplimented (2014-02-02) because this will involve a lot of boiler-plate code (though nothing terribly difficult).  The idea is to add a bunch of methods to Sequence that "realize" it into something non-lazy, like toJavaHashSet().  The other reason these aren't implemented yet is that this project should really include immutable collections that allow lightweight copies so that these libraries can compete with java.util.Collections even as the allow interop.  Thus toVector() might go to organicdesign.fp.immutable.Vector - an immutable shallow tree that supports lightweight copies, similar to the Clojre or Scala vector.  toJavaArrayList() would give full Java interop.

Also worth checking out is src/main/java/org/organicdesign/fp/function/Filter.and().  It allows chaining of single argument functions that return a boolean.  Some attempt has been made to do something similar with Function1.compose(), but the greater variation of possible functions makes this more difficult.  Additional considerations are listed in the JavaDocs.

This project involves some experimentation.  Theoretical purity (in the sense of atomic simplicity) is a goal, but the success of this project will be measured by practical application.  Of course, that's said partly tongue-in-cheek because the practical application of functional programming is severely limited by Java7.  But I expect the concepts behind this work to translate to something very useful in Java8 and/or Scala.

An additional element of this, perhaps the most critical element is to look at a design for collections that handle many things the original Java collections did not (and most of the things that they did).

Collection Variations:
 - Mutable vs. Immutable
 - Lazy vs. Eager
 - Persistent vs. Ephemeral
 - Finite vs. Infinite (finite sub-categories: fits in memory or not)
 - Permitting lightweight copies (goes well with Immutable)
 - Type-safe (with proper covariance and contravariance to the degree possible in Java)
 - Thread-safe

Interface Desires:
Implement all expected operations: filter, map, reduce, forall, etc.

 
