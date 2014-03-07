You are on the Java 8 branch of this project.  If you're using Java 7 or earlier, get the Java 7 legacy support branch from here:
https://github.com/GlenKPeterson/fp4java7/tree/java7

#Usage

Typical usage:

```java
List<Integer> list = ViewFromArray.of(1,2,3,4,5,6,7,8,9,10,11)
        .filter(i ->  i > 3)       // 4,5,6,7,8,9,10,11
        .map(i -> i + 1)           // 5,6,7,8,9,10,11,12
        .toJavaUnmodArrayList();
        
FunctionUtils.toString(list);
// Returns: "UnmodifiableRandomAccessList(5,6,7,8,9...)"
```

None of these transformations change the underlying collections.  Ratherly they lazily build a new collection by chaining together all the operations you specify, then applying them in a single pass through the unerlying data.

#Motivations

Using a loop says nothing about what you are trying to accomplish.  Is a given loop supposed to map something, filter it, accumulate a result, or all three?  Different kinds of collections require different looping constructs which can be error prone for the coder and confusing for the reader.  Loops generally require setting up accumulators, then looping through all kinds of <code>if</code>, <code>break</code>, and <code>continue</code> statements, like some kind of mad obstacle race that involves as many state changes as possible.

Higher order functions are not just briefer to write and read, they are less to *think* about.  They are useful abstractions that simplify your code and focus your attention on your goals rather than the details of how to accomplish them.  Function chaining: <code>xs.take(...).foldLeft(...).firstMatching(...)</code> defines what you are doing and how you are doing it in the simplest possible way.

No data is changed when using these transformations.  They allow you to write nearly stateless programs whose statements chain together and evaluate into a useful result.  Lisp works like this, only the syntax makes the evaluation go inside out from the order you read the statements in (hence Clojure's two arrow operators).  With method chaining, the evaluation happens in the same order as the methods are written on the page, much like piping commands to one another in shell scripts.

In some cases, a well hand-written loop may be faster, but in general, the overhead for using these transformations is very low, and well worth the clarity and safety they provide.

#API

Functions available in <code>View</code> (as of 2014-03-07):
###Starting Points:
```java
View<T> ViewFromArray.of(T... i)
View<T> ViewFromIterator.of(Iterator<T> i)
View<T> ViewFromIterator.of(Iterable<T> i)
```
###Transformations:
```java
// Run a function against each item for side effects (e.g. writing output)
void forEach(Consumer<T> se)
// Return the first item for which the given test function returns true
T firstMatching(Predicate<T> pred)
// Apply the function to each item in the list, accumulating the result in u
U foldLeft(U u, BiFunction<U, T, U> fun)
// Return only the items for which the given function returns true
View<T> filter(Predicate<T> pred)
// Return only the first n items
View<T> take(long numItems)
// Ignore the first n items and return only those that come after
View<T> drop(long numItems)
// Transform each item into exactly one new item using the given function
View<U> map(Function<T,U> func)
// Transform each item into zero or more new items using the given function
View<U> flatMap(Function<T,View<U>> func)
```
###Endpoints
```java
ArrayList<T> toJavaArrayList()
List<T> toJavaUnmodList()
HashMap<T,U> toJavaHashMap(Function<T,U> f1)
Map<T,U> toJavaUnmodMap(Function<T,U> f1)
HashMap<U,T> toReverseJavaHashMap(Function<T,U> f1)
Map<U,T> toReverseJavaUnmodMap(Function<T,U> f1)
TreeSet<T> toJavaTreeSet(Comparator<? super T> comparator)
SortedSet<T> toJavaUnmodSortedSet(Comparator<? super T> comparator)
TreeSet<T> toJavaTreeSet()
SortedSet<T> toJavaUnmodSortedSet()
HashSet<T> toJavaHashSet()
Set<T> toJavaUnmodSet()
```

#Details
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

#To Do

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

#Out of Scope

###T reduceLeft(BiFunction<T, T, T> fun)
reduceLeft() is like foldLeft without the "u" parameter.
I implemented it, but deleted it because it seemed like a very special case of foldLeft that only operated on items of the same type as the original collection.
I didn't think it improved readability or ease of use to have both methods.
How hard is it to pass a 0 or 1 to foldLeft?
It's easy enough to implement if there is a compelling use case where it's significantly better than foldLeft.
Otherwise, fewer methods means a simpler interface to learn.

###View<T> interpose(T item)
I also implemented interpose(), but took it out because my only use case was to add commas to a list to display
it in English and for that, you also need a conjunction, and often a continuation symbol:

a, b, c, or d.

a, b, c, and d.

a,b,c...

None of those are simple uses of interpose.
