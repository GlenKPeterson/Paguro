fp4java7
========

Typical usage (in Java 8):

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

Between auto-completion and code folding, the Java 7 code can be almost as easy to write and read as Java 8.

Functions available in View (as of 2014-02-16):
<pre><code>// Starting Points:
View&lt;T&gt; ViewFromArray.of(T... i)
View&lt;T&gt; ViewFromIterator.of(Iterator&lt;T&gt; i)
View&lt;T&gt; ViewFromIterator.of(Iterable&lt;T&gt; i)

// Transforms:
View&lt;U&gt; map(Function&lt;T,U&gt; func)
View&lt;T&gt; filter(Predicate&lt;T&gt; pred)
void forEach(Consumer&lt;T&gt; se)
T firstMatching(Predicate&lt;T&gt; pred)
U foldLeft(U u, BiFunction&lt;U, T, U&gt; fun)
View&lt;T&gt; take(long numItems)
View&lt;T&gt; drop(long numItems)
View&lt;U&gt; flatMap(Function&lt;T,View&lt;U&gt;&gt; func)

// Endpoints
ArrayList&lt;T&gt; toJavaArrayList()
List&lt;T&gt; toJavaUnmodList()
HashMap&lt;T,U&gt; toJavaHashMap(Function&lt;T,U&gt; f1)
Map&lt;T,U&gt; toJavaUnmodMap(Function&lt;T,U&gt; f1)
HashMap&lt;U,T&gt; toReverseJavaHashMap(Function&lt;T,U&gt; f1)
Map&lt;U,T&gt; toReverseJavaUnmodMap(Function&lt;T,U&gt; f1)
TreeSet&lt;T&gt; toJavaTreeSet(Comparator&lt;? super T&gt; comparator)
SortedSet&lt;T&gt; toJavaUnmodSortedSet(Comparator&lt;? super T&gt; comparator)
TreeSet&lt;T&gt; toJavaTreeSet()
SortedSet&lt;T&gt; toJavaUnmodSortedSet()
HashSet&lt;T&gt; toJavaHashSet()
Set&lt;T&gt; toJavaUnmodSet()</code></pre>

The classes in the <code>function</code> package allow you to use the Java 8 functional interfaces (more or less) as "second class" functions in Java 7.
When you switch to Java 8, you only need to change the import statement and remove the _ from the apply_() methods.
The apply_() methods are there to deal with checked exceptions in lambdas in Java 7.

Java 7 and earlier require that all variables declared outside a lambda be finial in order to use them inside the lambda.
If you need that functionality (and you usually won't), the Mutable.____Ref classes work around this limitation.

The most interesting classes are probably (in src/main/java/):
<ul>
<li><code><a href="https://github.com/GlenKPeterson/fp4java7/blob/master/src/main/java/org/organicdesign/fp/Transformable.java">org/organicdesign/fp/Transformable</a></code> - allows various functional transformations to be lazily applied: filter, map, forEach, etc., and allows any transformations to be eagerly evaluated into either mutable or unmodifiable collections (Java collections have to fit in memory).</li>
<li><code><a href="https://github.com/GlenKPeterson/fp4java7/blob/master/src/main/java/org/organicdesign/fp/ephemeral/View.java">org/organicdesign/fp/ephemeral/View</a></code> - a working implementation of most of these transformations</li>
<li><code><a href="https://github.com/GlenKPeterson/fp4java7/blob/master/src/main/java/org/organicdesign/fp/FunctionUtils.java">org/organicdesign/fp/FunctionUtils</a></code> - smartly combine/compose multiple filters and functions, convert collections to Strings, etc.</li>
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
