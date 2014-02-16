fp4java7
========

Typical usage in Java 8:

<pre><code>List<Integer> list = ViewFromArray.of(1,2,3,4,5,6,7,8,9,10,11)
        .filter(i -&gt;  i &gt; 3 )
        .map(i -&gt; i + 1)
        .toJavaUnmodArrayList();
        
FunctionUtils.toString(list);
// Returns: "UnmodifiableRandomAccessList(5,6,7,8,9...)"</code></pre>

None of these transformations change the underlying collections.  Ratherly they lazily build a new collection by chaining together all the operations you specify, then applying them in a single pass through the unerlying data.

A good editor like Intellij IDEA has auto-completion and code folding features that make the Java 7 code somewhat easier to write and read, but the above example completely expanded in Java 7 looks like this:

<pre><code>List<Integer> list = ViewFromArray.of(1,2,3,4,5,6,7,8,9,10,11)
        .filter(new Filter&lt;Integer&gt;() {
            @Override
            public boolean apply(Integer i) {
                return i &gt; 3;
            }})
        .map(new Function1&lt;Integer,Integer&gt;() {
            @Override
            public Object apply(Integer i) {
                return i + 1;
            }})
        .toJavaUnmodArrayList();

FunctionUtils.toString(list);
// Returns: "UnmodifiableRandomAccessList(5,6,7,8,9...)"</code></pre>


Functions available in <code>View</code> (as of 2014-02-16):
<pre><code>// Starting Points:
View&lt;T&gt; ViewFromArray.of(T... i)
View&lt;T&gt; ViewFromIterator.of(Iterator&lt;T&gt; i)
View&lt;T&gt; ViewFromIterator.of(Iterable&lt;T&gt; i)

// Transforms:
void forEach(Consumer&lt;T&gt; se)
T firstMatching(Predicate&lt;T&gt; pred)
U foldLeft(U u, BiFunction&lt;U, T, U&gt; fun)
View&lt;T&gt; filter(Predicate&lt;T&gt; pred)
View&lt;T&gt; take(long numItems)
View&lt;T&gt; drop(long numItems)
View&lt;U&gt; map(Function&lt;T,U&gt; func)
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

The View model implemented here is for lightweight, lazy, immutable, type-safe, and thread-safe transformations.
The Sequence model is also memoized/cached, so it is useful for repeated queries.
Sequence is most similar to the Clojure sequence abstraction, but it's pure Java and type-safe.
Both allow processing in the smallest possible (and therefore laziest) increments.
I find myself focusing on View more than Sequence because View seems to be adequite for most things I do.

The classes in the <code>function</code> package allow you to use the Java 8 functional interfaces (more or less) as "second class" functions in Java 7.
When you switch to Java 8, you only need to change the import statement and remove the _ from the apply_() methods.
The apply_() methods are there to deal with checked exceptions in lambdas in Java 7.

Java 7 and earlier require that all variables declared outside a lambda be finial in order to use them inside the lambda.
If you need that functionality in Java 7 or earlier, the Mutable.____Ref classes work around this limitation.

The most interesting classes are probably (in src/main/java/):
<ul>
<li><code><a href="https://github.com/GlenKPeterson/fp4java7/blob/master/src/main/java/org/organicdesign/fp/Transformable.java">org/organicdesign/fp/Transformable</a></code> - allows various functional transformations to be lazily applied: filter, map, forEach, etc., and allows any transformations to be eagerly evaluated into either mutable or unmodifiable collections (Java collections have to fit in memory).</li>
<li><code><a href="https://github.com/GlenKPeterson/fp4java7/blob/master/src/main/java/org/organicdesign/fp/ephemeral/View.java">org/organicdesign/fp/ephemeral/View</a></code> - a working implementation of most of these transformations</li>
<li><code><a href="https://github.com/GlenKPeterson/fp4java7/blob/master/src/main/java/org/organicdesign/fp/FunctionUtils.java">org/organicdesign/fp/FunctionUtils</a></code> - smartly combine/compose multiple predicates, convert collections to Strings, etc.</li>
</ul>

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

Update Sequence to have all the transforms that View does.

Some collections, like Sets, are unordered and naturally partitioned, so that some processes (such as mapping one set to another) could be carried out in a highly concurrent manner.

A Java 8 version of this project is working, but a few commits behind the Java 7 version.  If
someone wants that, let me know and I'll post it.

This would be an even smaller project in Scala than in Java 8 so that may be in the works as well, if people would find it useful.

A lot has been said about lightweight copies of immutable collections, but I wonder how far
mutable builders could go toward not having to copy immutable collections?

Out of Scope
============

reduceLeft() is like foldLeft without the "u" parameter.
I implemented it, but deleted it because it seemed like a very special case of foldLeft that only operated on items of the same type as the original collection.
I didn't think it improved readability or ease of use to have both methods.
How hard is it to pass a 0 or 1 to foldLeft?
It's easy enough to implement if there is a compelling use case where it's significantly better than foldLeft.
Otherwise, fewer methods means a simpler interface to learn.

I also implemented interpose(), but took it out because my only use case was to add commas to a list to display
it in English and for that, you also need a conjunction, and often a continuation symbol:

a, b, c, or d.

a, b, c, and d.

a,b,c...

None of those are simple uses of interpose.
