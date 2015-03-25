Formerly called fp4java7, J-cicle (pronounced "Jay-sick-ul" like a frozen/immutable Java-icicle) is about limiting mutation and replacing loops with lazy transformations.

You are on the Java 8 branch of this project.  If you're using Java 7 or earlier, get the Java 7 legacy support branch from here:
https://github.com/GlenKPeterson/fp4java7/tree/java7


#Usage
How hard is it to create an immutable, type safe map in Java?  Are you tired of writing code like this:
http://glenpeterson.blogspot.com/2013/07/immutable-java-with-lists-and-other.html

Like Guava, J-cicle cuts through the boilerplate:
```java
Map<String,Integer> sToI = uMap(
        "One", 1,
        "Two", 2,
        "Three", 3);
```

What if you want to add items conditionally?  Would you create a temporary, mutable map, test each item, adding some to
the mutable map, then call Collections.unmodifiableMap(tempMap) on it?  Ouch!  The following will create an
UnmodifiableMap of 0, 1, 2, or 3 items (no nulls) depending on the values of showFirst, showSecond, and showThird:
```java
Map<String,Integer> sToI = uMapSkipNull(
        showFirst ? Tuple2.of("One", 1) : null,
        showSecond ? Tuple2.of("Two", 2) : null,
        showThird ? Tuple2.of("Three", 3) : null);
```

Similar type-safe methods are available for producing unmodifiable Sets and Lists of any length (uMaps currently go
from 0 to 20 type-safe parameters).

What about transforming your unmodifiable data into other unmodifiable data?  Lazily, without any extra processing?
Typical usage (based on this unit test: <a href="https://github.com/GlenKPeterson/fp4java7/blob/master/src/test/java/org/organicdesign/fp/ephemeral/ViewTest.java">ViewTest.java</a>):

```java
List<Integer> list = View.ofArray(4,5) //       4,5
        .prepend(View.ofArray(1,2,3))  // 1,2,3,4,5
        .append(View.ofArray(6,7,8,9)) // 1,2,3,4,5,6,7,8,9
        .filter(i -> i > 4)            //         5,6,7,8,9
        .map(i -> i - 2)               //     3,4,5,6,7
        .take(5)                       //     3,4,5,6
        .drop(2)                       //         5,6
        .toJavaUnmodList();

FunctionUtils.toString(list);
// Returns: "UnmodifiableRandomAccessList(4,5,6)"
```
These transformations do not change the underlying data.  They build a new collection by chaining together all the
operations you specify, then lazily applying them in a single pass through the unerlying data.  The laziness is
implemented as an incremental pull, so that if your last operation is take(1), then the absolute minimum number of
items will be evaluated through all the functions you specified.  In the example above, items 7, 8, and 9 are never
processed.

#Learn

There is now a problem-set for learning this tool-kit: https://github.com/GlenKPeterson/LearnFpJava

#Motivations

Higher order functions are not just briefer to write and read, they are less to *think* about.  They are useful abstractions that simplify your code and focus your attention on your goals rather than the details of how to accomplish them.  Function chaining: <code>xs.map(x -> x + 1).filter(x -> x > 7).first()</code> defines what you are doing and how you are doing it in the simplest possible way, hiding all details about how to iterate through the underlying collection.

The alternative - loops - are bundles of unnecessary complexity.  Loops generally require setting up accumulators, then running a gamut of <code>if</code>, <code>break</code>, and <code>continue</code> statements, like some kind of mad obstacle race that involves as many state changes as possible.  Different kinds of collections require different looping constructs - more complexity.  Looping code is vulnerable to "off-by-one" boundary overflow/underflow, improper initialization, accidental exit, infinite loops, forgetting to update a counter, updating the wrong counter...  The list goes on!  None of that has anything to do with why the loop was created in the first place which is to transform the underlying data.

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

// Apply the function to each item in the list, accumulating the result in u.
// You could perform many other transformations with just this one function, but
// it is clearer to use the most specific transformations that meets your needs.
// Still, sometimes you need the flexibility foldLeft provides, so here it is:
U foldLeft(U u, BiFunction<U, T, U> fun)

// Return only the items for which the given predicate returns true
View<T> filter(Predicate<T> pred)

// Return items from the beginning of the list until the given predicate returns false
View<T> takeWhile(Predicate<T> p)

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
Iterator<T> toIterator()
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

#Change Log
2015-03-14 version 0.8.0: Removed firstMatching - in your code, replace firstMatching(...) with filter(...).first().
Implemented filter() on Sequence.

0.7.4:
Added uMapSkipNull and other skipNull versions of the StaticImports methods.  This allows little one-liner add-if items
to still go efficiently into an immutable map.  Next step is to probably implement an immutable map that you can
"add things to" (returning a new immutable map, leaving the original unchanged).  Made Tuple2 implement Map.Entry.
Added unit tests for the above.

0.7.3:
 - Added back exception-safe Function0 (Producer)
 - Added LazyRef class to take a Function0 and lazily initialize a value (and free the initialization resources) on the
 first call to get().  Subsequent calls to get() cheaply return the previously initialized value.  This class is thread
 safe if the producer and the values it produces are free from outside influences.

#To Do

Use LazyRef to re-implement Sequence classes much more simply.

I think what I'm calling foldLeft actually processes items in order like foldRight.  I should either fix it to process items in reverse order, or rename it.  You can see this if you pass it the cons() function.

Collection Variations:
 - Immutable vs. Mutable (Persistent vs. Ephemeral) and Permitting lightweight copies
 - Finite vs. Infinite (finite sub-categories: fits in memory or not)
 - Heterogenious vs. Homogenious
 - Write-only Builder with read-only collection?
 - Thread-safe vs. unsafe

Transform Variations:
 - Lazy vs. Eager

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

#Licenses
Java&trade; is a registered trademark of the Oracle Corporation in the US and other countries.  J-cicle is not part of Java.  Oracle is in no way affiliated with the J-cicle project.

This work is licensed under both the Apache 2.0 license and the Eclipse Public License.  You must comply with the rules of both licenses (you don't get to choose).  New contributions should be made under the Apache 2.0 license whenever practical.

Most of this work is licensed under the Apache 2.0 license.  However, the persistent collections (Vec-sicle, Mapsicle, Setsicle, etc. in the expiraments/collections folder as of 2015-03-24) are originally copied from, and still based on, the Clojure source code by Rich Hickey which is released under the Eclipse Public License (as of fall 2014).  Those files are derivative works and must remain under the EPL license unless the original authors chooses a new license.

I am not a lawyer and this is not legal advice.  Both the EPL and Apache projects list each other's license as being compatible.  I am not aware of a clear difference between them, or a reason why works written under the two licenses cannot be combined.

As of 2015-03-24, the following statements made me think the Apache and EPL licenses were compatible.

###From Apache
> For the purposes of being a dependency to an Apache product, which licenses
> are considered to be similar in terms to the Apache License 2.0?
>
> Works under the following licenses may be included within Apache products:
>
> ...
>
> Eclipse Distribution License 1.0
>
> ...
>
> Many of these licenses have specific attribution terms that need to be
> adhered to, for example CC-A, often by adding them to the NOTICE file. Ensure
> you are doing this when including these works. Note, this list is
> colloquially known as the Category A list.

Source (as of 2015-03-24): https://www.apache.org/legal/resolved

###From Eclipse
> What licenses are acceptable for third-party code redistributed by Eclipse
> projects?
>
> Eclipse views license compatibility through the lens of enabling successful
> commercial adoption of Eclipse technology in software products and services.
> We wish to create a commercial ecosystem based on the redistribution of
> Eclipse software technologies in commercially licensed software products.
> Determining whether a license for third-party code is acceptable often
> requires the input and advice of Eclipse’s legal advisors. If you have any
> questions, please contact license@eclipse.org.
>
> The current list of licenses approved for use by third-party code
> redistributed by Eclipse projects is:
>
> Apache Software License 1.1
>
> Apache Software License 2.0
>
> ...

Source (as of 2015-03-24): https://eclipse.org/legal/eplfaq.php#3RDPARTY
