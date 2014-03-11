You are on the Java 8 branch of this project.  If you're using Java 7 or earlier, get the Java 7 legacy support branch from here:
https://github.com/GlenKPeterson/fp4java7/tree/java7

#Usage

Typical usage (based on this unit test: <a href="https://github.com/GlenKPeterson/fp4java7/blob/master/src/test/java/org/organicdesign/fp/ephemeral/ViewTest.java">ViewTest.java</a>):

```java
List<Integer> list = View.ofArray(5)    //         5
        .prepend(View.ofArray(1,2,3,4)) // 1,2,3,4,5
        .append(View.ofArray(6,7,8,9))  // 1,2,3,4,5,6,7,8,9
        .filter(i -> i > 3)             //       4,5,6,7,8,9
        .map(i -> i - 2)                //   2,3,4,5,6,7
        .take(5)                        //   2,3,4,5,6
        .drop(2)                        //       4,5,6
        .toJavaArrayList();

FunctionUtils.toString(list);
// Returns: "UnmodifiableRandomAccessList(4,5,6)"
```

Unlike Java 8 streams, these transformations do not change the underlying data.  They build a new collection by chaining together all the operations you specify, then lazily applying them in a single pass through the unerlying data.  The laziness is implemented as an incremental pull, so that if your last operation is take(1), then the absolute minimum number of items will be evaluated through all the functions you specified.

#Motivations

Higher order functions are not just briefer to write and read, they are less to *think* about.  They are useful abstractions that simplify your code and focus your attention on your goals rather than the details of how to accomplish them.  Function chaining: <code>xs.map(x -> x + 1).filter(x -> x > 7).take(1)</code> defines what you are doing and how you are doing it in the simplest possible way, hiding all details about how to iterate through the underlying collection.

The alternative - loops - are bundles of unnecessary complexity.  Loops generally require setting up accumulators, then running a gamut of <code>if</code>, <code>break</code>, and <code>continue</code> statements, like some kind of mad obstacle race that involves as many state changes as possible.  Different kinds of collections require different looping constructs which adds unnecessary variety to looping boilerplate.  Looping code is vulnerable "off-by-one" boundary overflow/underflow, improper initialization, accidental exit, infinite loops, forgetting to update a counter, updating the wrong counter...  The list goes on!  None of that has anything to do with why the loop was created in the first place which is to transform the underlying data.

You don't have to write that kind of code any more.  If you want to map one set of values according to a given function, say so with xs.map().  Filter?  xs.filter().  It's clearer, simpler, and like type safety, it eliminates whole classes of errors.

No data is changed when using the transformers in this project.  They allow you to write nearly stateless programs whose statements chain together and evaluate into a useful result.  Lisp works like this, only the syntax makes the evaluation go inside out from the order you read the statements in (hence Clojure's two arrow operators).  With method chaining, the evaluation happens in the same order as the methods are written on the page, much like piping commands to one another in shell scripts.

Incremental evaluation prevents some items from being evaluated to produce the results you need which is sometimes more efficient than traditional whole-collection transforms.  There may be cases where a well hand-written loop will be faster, but in general, the overhead for using these transformations is minimal and, I believe, well worth the clarity, safety, and productivity benefits they provide.  If you find a better/faster implementation, please submit your improvements!

#API

Functions available in <code>View</code> (as of 2014-03-07):
###Starting Points:
```java
View<T> View.ofArray(T... i)
View<T> View.of(Iterator<T> i)
View<T> View.of(Iterable<T> i)
```
###Transformations:
```java
// Run a function against each item for side effects (e.g. writing output)
void forEach(Consumer<T> se)

// Return the first item for which the given predicate returns true.
// You can call filter(...).take(1) to achieve the same result
// (because Views are incrementally evaluated) so this may not be strictly
// necessary (I might remove it in a future release).
T firstMatching(Predicate<T> pred)

// Apply the function to each item in the list, accumulating the result in u
// You could perform many of the other functions with just this one, but
// it is clearer to use the most specific function that meets your needs.
// Still, sometimes you need the flexibility foldLeft provides.
U foldLeft(U u, BiFunction<U, T, U> fun)

// Return only the items for which the given predicate returns true
View<T> filter(Predicate<T> pred)

// Return only the first n items
View<T> take(long numItems)

// Ignore the first n items and return only those that come after
View<T> drop(long numItems)

// Transform each item into exactly one new item using the given function
View<U> map(Function<T,U> func)

// Add items to the end of this view
View<T> append(View<T> pv)

// Add items to the beginning of this view
View<T> prepend(View<T> pv)

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
T[] toArray()
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

Some variables declared outside a lambda and used within one must be finial.
The Mutable.____Ref classes work around this limitation.

The most interesting classes are probably (in src/main/java/):
<ul>
<li><code><a href="https://github.com/GlenKPeterson/fp4java7/blob/master/src/main/java/org/organicdesign/fp/Transformable.java">org/organicdesign/fp/Transformable</a></code> - allows various functional transformations to be lazily applied: filter, map, forEach, etc., and allows any transformations to be eagerly evaluated into either mutable or unmodifiable collections (Java collections have to fit in memory).</li>
<li><code><a href="https://github.com/GlenKPeterson/fp4java7/blob/master/src/main/java/org/organicdesign/fp/ephemeral/View.java">org/organicdesign/fp/ephemeral/View</a></code> - a working implementation of most of these transformations</li>
<li><code><a href="https://github.com/GlenKPeterson/fp4java7/blob/master/src/main/java/org/organicdesign/fp/FunctionUtils.java">org/organicdesign/fp/FunctionUtils</a></code> - smartly combine/compose multiple predicates, convert collections to Strings, etc.</li>
</ul>

#Dependencies
- Java 8 (tested with 64-bit Linux build 1.8.0-b129)
 
#Build Dependencies
- Maven (tested version: 3.11.0-18-generic 64-bit Linux build)

#Test Dependencies
- Maven will download jUnit for you
- As of 2014-03-08, all major areas of functionality are covered by unit tests.

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
