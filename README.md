fp4java7
========

These are functional programming tools for Java 7.
The focus is on collection transformation, but general purpose functional tools are included to make that possible, such as "second-class" functions.  :-)

Typical usage might be (in Java 8):

<pre><code>ViewFromArray.of(1, 2, 3, 4, 5).filter((i) -> { return i > 3; })
                               .map(i) -> { return i + 1; })
                               .toJavaUnmodArrayList();

// Returns: List(5, 6)</code></pre>

Or verbosely in Java 7:

<pre><code>ViewFromArray.of(1, 2, 3, 4, 5).filter(new Filter<Integer>() {
    @Override
    public boolean apply(Integer i) throws Exception {
        return i > 3;
    }
}).map(new Function1<Integer, Object>() {
    @Override
    public Object apply(Integer i) throws Exception {
        return i + 1;
    }
}).toJavaUnmodArrayList();</code></pre>

The most interesting classes are probably (in src/main/java/):
<ul>
<li><code>org/organicdesign/fp/ephemeral/ViewAbstract</code> - allows various functional transformations to be lazily applied: filter, map, forEach, etc.</li>
<li><code>org/organicdesign/fp/Realizable</code> - allows any transformations to be eagerly evaluated into either mutable or unmodifiable collections (Java collections have to fit in memory).</li>
<li><code>org/organicdesign/fp/function/Filter.and()</code> - Smartly combines multiple filters.</li>
</ul>

The View model implemented here is for lightweight, lazy, immutable, type-safe, and thread-safe transformations.
The Sequence model is also memoized/cached, so it is useful for repeated queries.
Sequence is most similar to the Clojure sequence abstraction, but it's pure Java and type-safe.
Both allow processing in the smallest possible (and therefore laziest) increments.

To use, start with a Java Iterable or Array wrapped in a ViewFrom___ or SequenceFrom___ class.

A lot has been said about lightweight copies of immutable collections, but I wonder how far
mutable builders could go toward not having to copy immutable collections.

Also worth checking out is src/main/java/org/organicdesign/fp/function/Filter.and().
It allows chaining of single argument functions that return a boolean.
Some attempt has been made to do something similar with Function1.compose(), but the greater variation of possible functions makes this more difficult.

This project involves some experimentation.
Simplicity is a goal, but the success of this project will be measured by practical application.
Of course, that's said partly tongue-in-cheek because the practical application of functional programming is somewhat limited by Java7.
But I expect the concepts behind this work to translate to something very useful in Java8 and/or Scala.

Collection Variations:
 - Mutable vs. Immutable
 - Lazy vs. Eager
 - Persistent vs. Ephemeral
 - Finite vs. Infinite (finite sub-categories: fits in memory or not)
 - Write-only Builder with read-only collection?
 - Permitting lightweight copies (goes well with Immutable)
 - Type-safe
 - Thread-safe

Interface Desires:
Implement all expected operations: filter, map, reduce, forall, etc.

 
