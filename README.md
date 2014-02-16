fp4java7
========

Typical usage might be (in Java 8):

<pre><code>List<Integer> list = ViewFromArray.of(1,2,3,4,5,6,7,8,9,10,11)
        .filter(i -&gt;  i &gt; 3 )
        .map(i -&gt; i + 1)
        .toJavaUnmodArrayList();
        
FunctionUtils.toString(list); // Returns: "UnmodifiableRandomAccessList(5,6,7,8,9...)"</code></pre>

Or verbosely in Java 7:

<pre><code>List<Integer> list = ViewFromArray.of(1, 2, 3, 4, 5).filter(new Filter&lt;Integer&gt;() {
    @Override
    public boolean apply(Integer i) throws Exception {
        return i &gt; 3;
    }
}).map(new Function1&lt;Integer, Object&gt;() {
    @Override
    public Object apply(Integer i) throws Exception {
        return i + 1;
    }
}).toJavaUnmodArrayList();

FunctionUtils.toString(list); // Returns: "UnmodifiableRandomAccessList(5,6)"</code></pre>


Between auto-completion and code folding, the Java 7 code can be almost as easy to write and read as Java 8.  The classes in the function package allow you to use the Java 8 functional interfaces (more or less) in java7.
When you switch to Java 8, you only need to change the import statement and remove the _ from the apply_() methods.
The apply_() methods are there because that's the simplest way to deal with checked exceptions in lambdas in Java 7.

Java 7 and earlier require that all variables declared outside a lambda be finial in order to use them inside the lambda.
The Mutable.____Ref classes work around this limitation in Java 7, but will not be needed with Java 8.

The most interesting classes are probably (in src/main/java/):
<ul>
<li><code>org/organicdesign/fp/Transformable</code> - allows various functional transformations to be lazily applied: filter, map, forEach, etc.</li>
<li><code>org/organicdesign/fp/Realizable</code> - allows any transformations to be eagerly evaluated into either mutable or unmodifiable collections (Java collections have to fit in memory).</li>
<li><code>org/organicdesign/fp/ephemeral/ViewAbstract</code> - a working implementation of most of these transformations</li>
<li><code>org/organicdesign/fp/FunctionUtils</code> - Smartly combine/compose multiple filters and functions.</li>
</ul>

The View model implemented here is for lightweight, lazy, immutable, type-safe, and thread-safe transformations.
The Sequence model is also memoized/cached, so it is useful for repeated queries.
Sequence is most similar to the Clojure sequence abstraction, but it's pure Java and type-safe.
Both allow processing in the smallest possible (and therefore laziest) increments.
I find myself focusing on View more than Sequence because View seems to be adequite for most things I do.

To use, start with a ViewFromIterable or ViewFromArray class.

The focus of this project is on collection transformation (using higher-order functions), but general purpose functional tools are included to make that possible, such as "second-class" functions.  :-)
To that end, the ephemeral.View classes provide the lowest-common-denominator of transformation.
That's good in the sense, that for a single-threaded application transforming a singly linked list, it could not perform any better.
Having a simple, correct reference implementation provides a baseline for concurrent shortcuts to beat.

Some collections, like Sets, are unordered and naturally partitioned, so that some processes (such as mapping one set to another) could be carried out in a highly concurrent manner.
Other collections (like a linked list to an immutable linked list) and transformations (like reduce) usually have to be processed in order, so a simple View is as good as it gets for them.
Other collections and transformations fall between these two extremes and provide a rich field for further study.

A lot has been said about lightweight copies of immutable collections, but I wonder how far
mutable builders could go toward not having to copy immutable collections?

Also worth checking out is src/main/java/org/organicdesign/fp/function/FunctionUtils.and().
It allows chaining of single argument functions that return a boolean.

This project involves some experimentation.
Simplicity is a goal, but the success of this project will be measured by practical application.
Of course, that's said partly tongue-in-cheek because the practical application of functional programming is somewhat in Java7.
The concepts behind this work are even more useful in Java 8.

A Java 8 version of this project is working, but a few commits behind the Java 7 version.  If
someone wants that, let me know and I'll post it.

To Do:
======

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
