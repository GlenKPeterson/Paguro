UncleJim ("**Un**modifiable **Coll**ections for **J**ava&trade; **Imm**utability") brings the following to Java:

#Usage Examples
Create a vector (list) and perform an immutable transformation on it:
```java
vec(4, 5)                        //          4, 5
        .precat(vec(1, 2, 3))    // 1, 2, 3, 4, 5
        .concat(vec(6, 7, 8, 9)) // 1, 2, 3, 4, 5, 6, 7, 8, 9
        .filter(i -> i > 4)      //             5, 6, 7, 8, 9
        .map(i -> i - 2)         //       3, 4, 5, 6, 7
        .take(4)                 //       3, 4, 5, 6
        .drop(2)                 //             5, 6
```
The rest of the [usage examples are implemented as unit tests](src/test/java/org/organicdesign/fp/UsageExampleTest.java#L34)
to ensure they remain correct and current.

#UncleJim Provides
* Clojure's [immutable collections](src/main/java/org/organicdesign/fp/collections) (classes start with the word "Persistent")
* An immutable [Transformation Builder](src/main/java/org/organicdesign/fp/xform/Transformable.java) which is baked into every collection and collection wrapper.
* Unmodifiable interfaces which deprecate mutator methods and throw exceptions to help you retrofit legacy code and catch errors in your IDE instead of at runtime.  They also implement Transformable.
* Better [unmodifiable wrappers](src/main/java/org/organicdesign/fp/StaticImports.java#L327) for existing Java collections that deprecate the methods.
* A tiny domain-specific language of brief helper functions: vec(), tup(), map(), set(), etc. in the [StaticImports file](src/main/java/org/organicdesign/fp/StaticImports.java).
* An [Equator](src/main/java/org/organicdesign/fp/collections/Equator.java) and [ComparisonContext](src/main/java/org/organicdesign/fp/collections/Equator.java#L45) which work like `java.util.Comparator`, but for hash-based collections.
* Simplified [functional interfaces](src/main/java/org/organicdesign/fp/function) that wrap checked exceptions
* [Memoization](src/main/java/org/organicdesign/fp/function/Function2.java#L59) for functions

Fluent interfaces encourage you to write expressions (that evaluate) instead of statements (that produce void).
Immutable collections are fast enough to make it unnecessary to modify data in place.
UncleJim pushes Java toward Clojure, but keeps the type saftey, objects, classes, and some of the C-like syntax that Java programmers are accustomed to.

Migrating large code bases to another language is not always practical.
This project lets you think about your code the way that Clojure programmers do, but still write Java.

This is somewhere between an Alpha and Beta release candidate.
The code quality is high, but there is still a chance of API changes before the final release. 
Test coverage at last check: 73%

![Test Coverage](testCoverage.png)

For complete API documentation, please build the javadoc:
`mvn javadoc:javadoc`

#Motivations

The goals of this project are to make it easy to use Java:

 - Immutably (Josh Bloch Item 15)
 - Type safely (Josh Bloch Item 23)
 - Functionally (using first-class functions more easily)
 - Expressiveness/Brevity (Expressions over statements: all API calls evaluate to something useful for subsequent calls).
 - Minimizing the use of primitives and arrays (except for varargs, Suggested by Josh Bloch Items 23, 25, 26, 27, 28, 29)
 - Returning empty collections instead of <code>null</code> (Josh Bloch Item 43)
 - "Throw exceptions at people, not at code" (says Bill Venners, but also Josh Bloch Item 59)
 - Concurrency friendly (Josh Bloch Item 66, 67)
 - Context-sensitive equality: prefer Equator and Comparator to <code>equals()</code>, <code>hashcode()</code> and <code>compareTo()</code> ([Daniel Spiewak, Viktor Klang, Rúnar Óli Bjarnason, Hughes Chabot](http://glenpeterson.blogspot.com/2013/09/object-equality-is-context-relative.html), java.util.TreeSet, java.util.TreeMap)
 - Sensible toString() implementations (like Scala)
 - Compatibly with existing/legacy Java code

Higher order functions are not just briefer to write and read, they are less to *think* about.
They are useful abstractions that simplify your code and focus your attention on your goals rather than the details of how to accomplish them.
Function chaining: <code>xs.map(x -> x + 1).filter(x -> x > 7).head()</code> defines what you are doing and how you are doing it in the simplest possible way, hiding all details about how to iterate through the underlying collection.

The alternative - loops - are bundles of unnecessary complexity.
Loops generally require setting up accumulators, then running a gamut of <code>if</code>, <code>break</code>, and <code>continue</code> statements, like some kind of mad obstacle race that involves as many state changes as possible.
Different kinds of collections require different looping constructs - more complexity.
Looping code is vulnerable to "off-by-one" boundary overflow/underflow, improper initialization, accidental exit, infinite loops, forgetting to update a counter, updating the wrong counter...  The list goes on!
None of that has anything to do with why the loop was created in the first place which is to transform the underlying data.

You don't have to write that kind of code any more.
If you want to map one set of values according to a given function, say so with xs.map().
Filter?  xs.filter().
It's clearer, simpler, and like type safety, it eliminates whole classes of errors.

No data is changed when using the permanent transformers in this project.
They allow you to write nearly elegant programs whose function calls chain together and evaluate into a useful result.
Clojure works like this, only the syntax makes the evaluation go inside out from the order you read the statements in (hence Clojure's two arrow operators).
With method chaining, the evaluation happens in the same order as the methods are written on the page, much like piping commands to one another in shell scripts.

Incremental evaluation prevents some items from being evaluated to produce the results you need which is sometimes more efficient than traditional whole-collection transforms.
There may be cases where a well hand-written loop will be faster, but in general, the overhead for using these transformations is minimal and, I believe, well worth the clarity, safety, and productivity benefits they provide.
If you find a better/faster implementation, please submit your improvements!

#API

###Data Description Mini-Language
```java
import org.organicdesign.fp.StaticImports.*

// Create a new vector of integers
vec(1, 2, 3, 4);

// Create a new set of Strings
set("a", "b", "c");

// Create a tuple of an int and a string (a type-safe heterogeneous container)
tup("a", 1);

// Create a map with a few key value pairs
map(tup("a", 1), tup("b", 2), tup("c", 3);
```
###Transformations:
```java
// Apply the function to each item, accumulating the result in u.  Other
// transformations could be implemented with just this one function, but
// it is clearer to use the most specific transformations that meets your needs.
// Still, sometimes you need the flexibility foldLeft provides.
// This implementation follows the convention that foldLeft processes items
// *in order* unless those items are a linked list, and in this case,
// they are not.
U foldLeft(U u, Function2<U,? super T,U> fun);

// Normally you want to terminate by doing a take(), drop(), or takeWhile() before you get
// to the fold, but if you need to terminate based on the complete result so far, you can
// provide your own termination condition.
U foldLeft(U u, Function2<U,? super T,U> fun, Function1<? super U,Boolean> terminateWhen);

// Return only the items for which the given predicate returns true
Transformable<T> filter(Function1<? super T,Boolean> predicate);

// Return items from the beginning until the given predicate returns false
Transformable<T> takeWhile(Function1<? super T,Boolean> predicate);

// Return only the first n items
Transformable<T> take(long numItems);

// Ignore the first n items and return only those that come after
Transformable<T> drop(long numItems);

// Transform each item into exactly one new item using the given function
Transformable<U> map(Function1<? super T,? extends U> func);

// Add items to the end of this Transformable
Transformable<T> concat(Iterable<? extends T> list);

// Add items to the beginning of this Transformable
Transformable<T> precat(Iterable<? extends T> list);

// Transform each item into zero or more new items using the given function
Transformable<U> flatMap(Function1<? super T,Iterable<U>> f);
```

###Endpoints
```java
// A one-time use, not-thread-safe way to get each value of this Realizable in turn.
UnmodIterator<T> iterator();

// The contents of this Realizable as a thread-safe immutable list.
// Use this when you want to access items quickly O(log32 n) by index.
ImList<T> toImList();

// The contents of this Realizable as an thread-safe, immutable, sorted (tree) map.
// Use this when you want to quickly O(log n) look up values by key, but still be
// able to retrieve Entries in key order.
ImSortedMap<U,V> toImSortedMap(Comparator<? super U> comp,
                               Function1<? super T,Map.Entry<U,V>> f1);

// The contents of this Realizable presented as an immutable, sorted (tree) set.
// Use this when you want to quickly O(log n) tell whether the set contains various items.
ImSortedSet<T> toImSortedSet(Comparator<? super T> comp);

// The contents copied to a mutable list.  Use toImList unless you need to modify the list in-place.
List<T> toMutableList();

// Returns the contents of this Realizable copied to a mutable hash map.
// Use toImMap() unless you need to modify the map in-place.
Map<U,V> toMutableMap(Function1<? super T,Map.Entry<U,V>> f1);

// Returns the contents of this Realizable copied to a mutable tree map.
// Use toImSortedMap() unless you need to modify the map in-place.
SortedMap<U,V> toMutableSortedMap(Function1<? super T,Map.Entry<U,V>> f1);

// Returns the contents of this Realizable copied to a mutable hash set.
// Use toImSet() unless you need to modify the set in-place.
Set<T> toMutableSet();

// Returns the contents of this Realizable copied to a mutable tree set.
// Use toImSortedSet unless you need to modify the set in-place.
SortedSet<T> toMutableSortedSet(Comparator<? super T> comp);

// Returns an Object[] for backward compatibility
Object[] toArray();

// The contents of this Realizable as an unmodifiable hash map.  Use this when
// you want to very quickly O(1) look up values by key, and don't care about ordering.
ImMap<U,V> toImMap(Function1<? super T,Map.Entry<U,V>> f1);

// The contents of this Realizable presented as an unmodifiable hash set.
// Use this when you want to very quickly O(1) tell whether the set contains
// various items, but don't care about ordering.
ImSet<T> toImSet();
```

#Learn

There is a (possibly outdated) problem-set for learning this tool-kit: https://github.com/GlenKPeterson/LearnFpJava

#Details (this section may be obsolete)
 - Like Guava, we want to be as compatible with the java.util... collections as possible, while preventing mutation-in-place.
 - org.organicdesign.fp.collection.**Un**... interfaces extend the java.util collection interfaces of the same name (minus the "Un" prefix) deprecate all the mutate-in-place methods to make your IDE show them in red, and implement them to throw UnsupportedOperationExceptions to fail fast if you try to use them anyway.  These interfaces are useful in its own right as a way to declare that a function does not modify what is passed, or that what it returns cannot be modified.  Modification errors are caught as early as possible due to deprecation warnings.
 - org.organicdesign.fp.collection.**Im**... interfaces are the immutable, lightweight-copy collection interfaces.  Only the "get" methods from the java.util... collection interfaces remain.  Additional "set" methods that return a new collectoin are added at this level.
 - org.organicdesign.fp.collection.**Persistent**... implementations have been taken directly from Clojure (hence the Eclipse licence for those components).  For starters, we will include the celebrated Vector and the sorted (tree) Set and Map implementations.  We will add the hash-based Set and Map later, but they will take a separate Equator to handle equals() and hashCode() much the way the tree-based collections take a Comparator.

Within your own FP-centric world, you will use the Im interfaces and implementations and transform them with the Transformation abstraction.  Methods that interact with imperative Java code will take the java.util interfaces and return either the Im- interfaces, or Un- interfaces as necessary.  Where practical, try to use the Im-interfaces instead of their implementations, as new, better immutable collection designs surface every few years.

The classes in the <code>function</code> package allow you to use the Java 8 functional interfaces smoothly warpping things that throw checked exceptions in Java 8, or as "second class" functions in Java 7.  They are all named Function*N*  where *N* is the number of arguments they take.  They all automatically wrap and re-throw checked exceptions.  There are no versions for primitives, or that return **void**.

In Java, variables declared outside a lambda and used within one must be effectively finial.  The Mutable.Ref class works around this limitation.

In short, Clojure doesn't have static types.  Scala has an TMTOWTDI attitude that reminds me of how C++ and Perl ended up producing write-only code. 
Unwilling to move a million lines of code to either language, I tried to bring the best of both to Java.

#Dependencies
- Java 8 (tested with 64-bit Linux build 1.8.0_51).
Probably can be meaningfully adapted to work well at least as far back as Java 5 with some work.
I plan to keep new development work on the main branch, but am very willing to help maintain branches back-ported to Java 7, 6, 5,.... if other people can share the load.
 
#Build Dependencies
- Maven (tested version: 3.19.0-26 64-bit Linux build)

#Test Dependencies
- Maven will download jUnit for you
- As of 2015-09-06, all major areas of functionality were covered by unit tests.

#Change Log
2015-09-08 version 0.10.6 Fixed bug: Xform would blow up later if you passed a null to its
constructor.

2015-09-08 version 0.10.5 moved all unmod____ method wrappers from StaticImports to FunctionUtils
because they just aren't nearly as useful as they used to be.  xform() has taken over nearly 100%
of the use cases for the unmod____ methods.  Removed Varargs methods from everything except the
three data definition functions in StaticImports.

2015-09-08 version 0.10.3 added xform() method to StaticImports as a convenient and more efficient
way to start a transformation than by using the unmod____ wrappers.

2015-08-30 version 0.10.2 Xform tests at 100%.  Applied Xform to UnmodIterable which makes it
apply to the entire project.  Completely removed all traces of View and Sequence.

2015-08-30 version 0.10.1 Added Xform and moved Transformable and Realizable into the new xform
package.  Xform should replace Sequence and View altogether.

2015-08-25 version 0.10.0 Renamed most methods in StaticImports.  This is why I've been calling this
*alpha*-quality code.  The four methods map(), set(), tup(), and vec() comprise a mini
data-definition language.  It's wordier than JSON, but still brief for Java and fairly brief
over-all.  Of those four methods, only tup() uses overloading to take heterogeneous arguments.
The other three are the only places in this project that use varargs.  The unmod() methods
have all been renamed to unmodSortedSet() or whatever to avoid overloading with the same number
of arguments and bring it in line with Josh Bloch's Item 41.  This project will be *alpha* still,
at least until the new TransDesc code from the One-Off-Examples project is merged into here.
That code should replace Sequence and View.

2015-08-24 version 0.9.14 Made Tuple2 and Tuple3 non-final and made constructors public for extra
and easy inheritance.

2015-08-23 version 0.9.12 Removed `Transformable<T> forEach(Function1<? super T,?> consumer)`.
See reasons in the "Out of Scope" section below.

2015-08-13 version 0.9.11: Added `RangeOfInt` class as an efficient (in both time and memory)
implementation of `List<Integer>`.  If you want to compare a `RangeOfInt` to generic `List<Integer>`, use
`RangeOfInt.LIST_EQUATOR` so that the hashCodes will be compatible.

2015-07-28 version 0.9.10: Changed toTypedArray() to toArray() because the former was not type safe in a way that would blow up only at runtime.  The latter is still provided for backwards compatibility (particularly useful in jUnit tests).

2015-07-25 version 0.9.9: Renamed methods in staticImports imList() to vec(), imSet() to hSet() (think: "hashSet()"),
imSortedSet to tSet() (think: "treeSet()"), imSortedMap to tMap(), etc.  Also removed the telescoping
methods in favor of just passing vec(tup(1, "one"), tup(2, "two"), tup(3, "three"));  It's maybe a little more work,
but a little less cognitive load and a lot less testing!  Also added Mutable.intRef.decrement().
Renamed Sequence.of() to .ofArray() and similarly with View.  I may rename Sequence/View.ofIter() to
just .of() in a future version, but then, I'm probably going to remove Sequence and replace View
with Transform too.  The interface to Transform is not like View without head() and tail() and it's immutable and faster.

2015-06-23 version 0.9.8: Added union(Iterable i) method to ImSet.

2015-06-12 version 0.9.7: Renamed classes and methods so that the unmodifiable prefix is now "unmod" instead of "un".
Changed XxxxOrdered to SortedXxxx to be more compatible with Java naming conventions.
Added Equator to HashMap and HashSet so you can define your own ComparisonContext now.

2015-06-07 version 0.9.6: Added PersistentHashMap and PersistentHashSet from Clojure with some tests for the same.

2015-06-04 version 0.9.5: Renamed everything from Sorted to Ordered.
Added an UnIteratorOrdered that extends UnIterator.  Same methods, just with an ordering guarantee.
Made UnMap and UnSet extend UnIterator, UnMapOrdered and UnSetOrdered extend UnIteratorOrdered.
Deleted some unnecessary wrapping methods in StaticImports.

2015-06-02 version 0.9.4: Renamed methods so that append/prepend means to add one item, while concat/precat means to add many items.
Changed ImList.put() to ImList.replace() to clarify how it's different from inserting (it doesn't push subsequent items to the right).
Made ImList and PersistentVector implement Sequence.
Changed everything that wrapped an Iterator to take an Iterable instead - can't trust an iterator that's been exposed to other code.
Test coverage was above 85% by line at one point.

2015-05-24 version 0.9.3: Made TreeSet and TreeMap.comparator() return null when the default comparator is used (to 
match the contract in SortedMap and SortedSet).

2015-05-24 version 0.9.2: Moved experiments to my One-off_Examples project.

2015-05-24 version 0.9.1: Renamed project from J-cicle to UncleJim.

2015-05-13 Release 0.9 alpha which packages type-safe versions of the Clojure collections and sequence abstraction for Java.
- 3 Immutable collections: [PersistentVector](src/main/java/org/organicdesign/fp/collections/PersistentVector.java), [PersistentTreeMap](src/main/java/org/organicdesign/fp/collections/PersistentTreeMap.java), and [PersistentTreeSet](src/main/java/org/organicdesign/fp/collections/PersistentTreeSet.java).  None of these use equals() or hashcode().
Vector doesn't need to and Map and Set take a Comparator.
- Un-collections which are the Java collection interfaces, but unmodifiable.  These interfaces deprecate the mutator methods and implement them to throw exceptions.  Plus, UnMap implements UnIterable<UnMap.UnEntry<K,V>>.
- Im-collections which add functional "mutator" methods that return a new collection reflecting the change, leaving the old collection unchanged.
- Basic sequence abstraction on the Im- versions of the above.
- Function interfaces that manage exceptions and play nicely with java.util.function.* when practical.
- Memoization methods on functional interfaces.

2015-04-05 version 0.8.2:
- Renamed Sequence.first() and .rest() to .head() and .tail() so that they wouldn't conflict with TreeSet.first()
which returns a T instead of an Option<T>.  This was a difficult decision and I actually implemented all of Sequence
except for flatMap with first() returning a T.  All the functions that could return fewer items that were previously
lazy became eager.  Flatmap became eager, but also became very difficult to implement correctly.  View is already eager,
so I renamed the methods to use the more traditional FP names and restored the Option<T>.  If you don't like the names,
just be glad I didn't use car and cdr.

2015-04-05 version 0.8.1:
- Renamed FunctionX.apply_() to just apply() to match java.util.function interfaces.
 Renamed FunctionX.apply() to applyEx() but this is still what you implement and it can throw an exception.
 Made FunctionX.apply() methods rethrow RuntimeExceptions unchanged, but (still) wrap checked Exceptions in RuntimeExceptions.
 They were previously wrapped in IllegalStateExceptions, except for SideEffect which tried to cast the exception which never worked.
- Added all the functions to Sequence that were previously only in View, plus tests for same.
- Re-implemented Sequence abstraction using LazyRef.
- SideEffect has been deprecated because it may not have been used anywhere.
- Added some tests, improved some documentation, and made a bunch of things private or deleted them in experiments.collections.ImVectorImpl.

2015-03-14 version 0.8.0: Removed firstMatching - in your code, replace firstMatching(...) with filter(...).head().
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
 - Have an Ordered version of Transform as well as the (default) unreliable order.  Only the ordered version can be used for implementing things like equals() and hashCode()
 - Bring unit test coverage back above 80%, or 85% if sensible.  This basically means to add any and all practical tests for PersistentHashMap, then remove unused code.
 - Update JavaDoc, esp. Im vs. Unmod
 - Add `Either` (I have a working implementation) - it's like `Or` without the attitude.
 - Make visio drawig of interface diagram.
 - Clarify/Simplify/Improve Readme.md
 - Update learnFPJava project
 - Add a [Persistent RRB Tree](http://infoscience.epfl.ch/record/169879/files/RMTrees.pdf) and compare its performance to the PersistentVector.

#Out of Scope

###T reduceLeft(BiFunction<T, T, T> fun)
reduceLeft() is like foldLeft without the "u" parameter.
I implemented it, but deleted it because it seemed like a very special case of foldLeft that only operated on items of the same type as the original collection.
I didn't think it improved readability or ease of use to have both methods.
How hard is it to pass a 0 or 1 to foldLeft?
It's easy enough to implement if there is a compelling use case where it's significantly better than foldLeft.
Otherwise, fewer methods means a simpler interface to learn.

###Transformable<T> forEach(Function1<? super T,?> consumer)
Java 8 has `void forEach(Consumer<? super T> action)` on both Iterable and Stream that does what
Transformable.forEach() used to do.  The old Transformable method overloaded (but did not override)
this method which is problematic for the reasons Josh Bloch gives in his Item 41.  Either make
use of the Java 8 `void forEach(i -> log(i))` or pass a constant function like
`i -> { print(i); return Boolean.TRUE; }` to
`Transformable<T> filter(Function1<? super T,Boolean> predicate)` instead. 

###View<T> interpose(T item)
I also implemented interpose(), but took it out because my only use case was to add commas to a list to display
it in English and for that, you also need a conjunction, and often a continuation symbol:

a, b, c, or d.

a, b, c, and d.

a,b,c...

None of those are simple uses of interpose.

#Thank You
Nathan Williams: for many lengthy email conversations about this project, encouragement to separate state from the transformation, and occasional light code review.

GreenJUG: for bearing with talks on early versions of this code two years in a row.

Greenville Clojure (and Jeff Dik before that): for bearing with my newbie Clojure questions.

Everyone whose ideas are collected in this project.
I tried to put names in as close as possible to the contributions.

#Licenses
Java&trade; is a registered trademark of the Oracle Corporation in the US and other countries.
UncleJim is not part of Java.
Oracle is in no way affiliated with the UncleJim project.

UncleJim is not part of Clojure.
Rich Hickey and the Clojure team are in no way affiliated with the UncleJim project, though it borrows heavily from their thoughts and is partly a derivative work of their open-source code.

The Clojure collections are licensed under the Eclipse Public License.
Versions of them have been included in this project and modified to add type safety and implement different interfaces.
These files are still derivative works under the EPL.
The [EPL is not compatable with the GPL version 2 or 3](https://eclipse.org/legal/eplfaq.php#GPLCOMPATIBLE).
You can [add an exception to the GPL to allow you to release EPL code under this modified GPL](http://www.gnu.org/licenses/gpl-faq.html#GPLIncompatibleLibs), but not the other way around.

Unless otherwise stated, the rest of this work is licensed under the Apache 2.0 license.
New contributions should be made under the Apache 2.0 license whenever practical.
I believe it is more popular, clearer, and has been better tested in courts of law.
[The Apache 2.0 license is also one-way compatible with the GPL version 3](http://www.apache.org/licenses/GPL-compatibility.html), so that everything *except* the Clojure collections can be combined and re-distributed with GPLv3 code.
Apache is not compatible with GPLv2, though you might try the GPL modification mentioned in the previous paragraph.

As of 2015-03-24, the following statements made me think the Apache and EPL licenses were compatible enough for my purposes and for general enterprise adoption:

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

Source (as of 2015-05-13): https://www.apache.org/legal/resolved#category-a

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

Source (as of 2015-05-13): https://eclipse.org/legal/eplfaq.php#3RDPARTY
