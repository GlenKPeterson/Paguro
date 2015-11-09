#Notice
Read [README.md](README.md) before this file as that is the official introduction to UncleJim.
This file contains additional information for contributors, or maybe people who are considering opening an issue.

#Additional experimental features:
* An [Equator](src/main/java/org/organicdesign/fp/collections/Equator.java) and [ComparisonContext](src/main/java/org/organicdesign/fp/collections/Equator.java#L45) which work like `java.util.Comparator`, but for hash-based collections.
* [Memoization](src/main/java/org/organicdesign/fp/function/Function2.java#L59) for functions
* Unmodifiable interfaces which deprecate mutator methods and throw exceptions to retrofit legacy code and catch errors in your IDE instead of at runtime.
These were useful before the Clojure collections and Transformable were fully integrated, but may still provide a useful extension point for integrating your own immutable collections into the traditional Java ecosystem. 

#API Highlights

* Type-safe versions of Clojure's [immutable collections](src/main/java/org/organicdesign/fp/collections) (classes start with the word "Persistent")

* [Immutable Transformation Builder](src/main/java/org/organicdesign/fp/xform/Transformable.java)
(implementation is in [Xform](src/main/java/org/organicdesign/fp/xform/Xform.java))

* [Data Description Mini-Language](src/main/java/org/organicdesign/fp/StaticImports.java)

* Simplified Java 8 [functional interfaces](src/main/java/org/organicdesign/fp/function) that wrap checked exceptions

For complete API documentation, please build the javadoc:
`mvn javadoc:javadoc`

#Thank You
Some of the people I'm listing as contributors may not actually be aware of this project, but I found them inspiring in various ways.

The bulk of this project started as a simple question on StackExchange: [Why doesn't Java 8 include immutable collections?](http://programmers.stackexchange.com/questions/221762/why-doesnt-java-8-include-immutable-collections)  People's answers were a big help in figuring out what this project should and shouldn't do.

Paul Philips [Scala Collections talk](https://www.youtube.com/watch?v=uiJycy6dFSQ&t=26m19s) was a huge inspiration to me.  I watched it over and over and tried to learn from his experience as much as possible.

John Tollison: For a brief review and suggestions.

Nathan Williams: for many lengthy email conversations about this project, encouragement to separate state from the transformation, and occasional light code review.

GreenJUG: for bearing with talks on early versions of this code two years in a row.

Greenville Clojure (and Jeff Dik before that): for bearing with my newbie Clojure questions.

Mike Greata: for providing encouragement, advice, and patiently listening to me drone on about this as we carpooled to user group meetings.

Context-sensitive equality: prefer Equator and Comparator to <code>equals()</code>, <code>hashcode()</code> and <code>compareTo()</code> ([Daniel Spiewak, Viktor Klang, Rúnar Óli Bjarnason, Hughes Chabot](http://glenpeterson.blogspot.com/2013/09/object-equality-is-context-relative.html), java.util.TreeSet, java.util.TreeMap)

Everyone whose ideas are collected in this project: I tried to put names in as close as possible to the contributions.

Bodil Stokke for pointing out the EPL/GPL compatibility issue and work-around.

Joshua Bloch for his book, Effective Java.

Rich Hickey for Clojure

#Build from Source

- Java 8 (tested with 64-bit Linux build 1.8.0_51).
- Maven (tested version: 3.19.0-26 64-bit Linux build)
- Maven will download jUnit for you
- First `mvn clean install` on: https://github.com/GlenKPeterson/TestUtils
- Then `mvn clean test` on UncleJim

#To Do
 - Rename foldLeft() to just fold().  It's too confusing for people used to linked list implementations
 to think about what foldLeft() means in terms of ordering.
 - Add insert(int i, E element) to ImList, implemented by wrapping the existing list in an ImSortedMap and time it.
 - Cover a few more lines with tests in UnmodCollection and merge changes into NestingVector branch.
 - Can I make UnmodMap extend UnmodCollection instead of UnmodIterable?  Or do the contains() and contains() all methods conflict?
   Hmm... Maybe have a SizedIterable that both maps and collections can extend?  Ditto UnmodSortedMap extend UnmodSortedCollection instead of UnmodSortedIterable.
 - Have an Ordered version of Transform as well as the (default) unreliable order.  Only the ordered version can be used for implementing things like equals() and hashCode()
 - Implement drop() for list with listIterator(dropAmount)
 - Consider `max(Comparator<T> c, Iterable<? extends T> is)` and min()...  Actually, these probably belong on Transformable.
 - Study monadic thinking and ensure that Or is "monad-friendly".
 Ensure you can chain together functions in a short-circuiting way, without exceptions or other side-effects.
 - Add a [Persistent RRB Tree](http://infoscience.epfl.ch/record/169879/files/RMTrees.pdf) and compare its performance to the PersistentVector.
 - Re-implement Persistent collections from Algorithms and Purely Functional Data Structures without relying on a wrapped transient collection and without locking checks, then compare efficiency.
 - This project needs a better name.  It's really not about Unmodifiable collections any more.  Consider just Jimm (used), Cake Pan, Baking Mold...
 Something else that conveys Immutability.  I hate that name because these collections are absolutely changeable.  They just aren't changeable in-place.
 Really, moulting (or shedding an exoskeleton) is a better analogy because these collections grow by changing a lightweight wrapper while what's inside stays basically the same.
 Of the various animals that moult or shed their skins or exoskeletons, most are pretty darn disgusting.
 Hermit crabs changing shells, on the other hand, is very cute.  So maybe HermitCrab would be a better name?
 Or at least a better mascot.
 Paguroidea is not taken as a name yet, and it's the name for "Hermit Crab" in all romance languages, plus Sweedish.
 Pong-pongan is a pretty-cute name too, and it's Javanese, so that's pretty appropriate.
 I think Italian is just Paguro.

#Out of Scope

###Option<T> firstMatching(Predicate<T> pred);
Use with filter(...).head() instead

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

###Transformable<T> interpose(T item)
I also implemented interpose(), but took it out because my only use case was to add commas to a list to display
it in English and for that, you also need a conjunction, and often a continuation symbol:

a, b, c, or d.

a, b, c, and d.

a,b,c...

None of those are simple uses of interpose.

#Motivation

##Executive summary
To be able to write Java at work more like the way I write Clojure without taking any significant performance penalty for doing so.  Also, to be able to use the Clojure collections in a type-safe language.  I was thinking "Java" but really Scala can take advantage of the type safety improvements as well.

##Details
The goals of this project are to make it easy to use Java:

 - Immutably (Josh Bloch Item 15 and Clojure)
 - Type safely (Josh Bloch Item 23)
 - Functionally (using first-class functions more easily: Clojure and Scala)
 - Expressiveness/Brevity (Expressions over statements: all API calls evaluate to something useful for subsequent calls: Clojure and Scala).
 - Minimizing the use of primitives and arrays (except for varargs in 3 places, Suggested by Josh Bloch Items 23, 25, 26, 27, 28, 29, also Clojure and Scala)
 - Returning empty collections instead of <code>null</code> (Josh Bloch Item 43, also Clojure and Scala)
 - "Throw exceptions at people, not at code" (says Bill Venners, but also Josh Bloch Item 59)
 - Concurrency friendly (Josh Bloch Item 66, 67)
 - Context-sensitive equality: prefer Equator and Comparator to <code>equals()</code>, <code>hashcode()</code> and <code>compareTo()</code> ([Daniel Spiewak, Viktor Klang, Rúnar Óli Bjarnason, Hughes Chabot](http://glenpeterson.blogspot.com/2013/09/object-equality-is-context-relative.html), java.util.TreeSet, java.util.TreeMap)
 - Sensible toString() implementations (like Scala)
 - Compatibly with existing/legacy Java code

Higher order functions are not just briefer to write and read, they are less to *think* about.
They are useful abstractions that simplify code and focus your attention on your goals rather than the details of how to accomplish them.
Function chaining: <code>xs.map(x -> x + 1).filter(x -> x > 7).head()</code> defines what you are doing in the simplest possible way while hiding all details about how to iterate through the underlying collection.

The alternative - loops - are bundles of unnecessary complexity.
Loops generally require setting up accumulators, then running a gamut of <code>if</code>, <code>break</code>, and <code>continue</code> statements, like some kind of mad obstacle race that involves as many state changes as possible.
Different kinds of collections require different looping constructs - more complexity.
Looping code is vulnerable to "off-by-one" boundary overflow/underflow, improper initialization, accidental exit, infinite loops, forgetting to update a counter, updating the wrong counter...  The list goes on!
None of that has anything to do with why the loop was created in the first place which is to transform the underlying data.

You don't have to write that kind of code any more.
If you want to map one set of values according to a given function, say so with xs.map().
Filter?  xs.filter().
It's clearer, simpler, and like type safety, it eliminates whole classes of errors.

Clojure works like this, only the syntax makes the evaluation go inside out from the order you read the statements in (hence Clojure's two arrow operators).
With method chaining, the evaluation happens in the same order as the methods are written on the page, much like piping commands to one another in shell scripts.

The Xform class is the third one of its kind that I have written.  For a single thread, my timings show that its speed is comparable to a for loop.  In general, the overhead for using these transformations is minimal or non-existant.  In the cases where imutability does cause overhead (and there definitely are) it is generally well worth the clarity, safety, and productivity benefits it provides.
If you find a better/faster implementation, please submit your improvements!

Within your own FP-centric world, you will use the Im interfaces and implementations and transform them with the Transformation abstraction.  Methods that interact with imperative Java code will take the java.util interfaces and return either the Im- interfaces (or Un- interfaces) as necessary.

In Java, variables declared outside a lambda and used within one must be effectively finial.  The Mutable.Ref class works around this limitation.

#Licenses (continued)
The [EPL is not compatable with the GPL version 2 or 3](https://eclipse.org/legal/eplfaq.php#GPLCOMPATIBLE).
You can [add an exception to the GPL to allow you to release EPL code under this modified GPL](http://www.gnu.org/licenses/gpl-faq.html#GPLIncompatibleLibs), but not the other way around.

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
