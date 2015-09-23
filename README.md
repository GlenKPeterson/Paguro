UncleJim ("**Un**modifiable **Coll**ections for **J**ava™ **Imm**utability") makes standard Java code cleaner and safer by providing:

* Type-safe versions of Clojure's immutable collections
* An immutable Transformable.  This is a simplified alternative to Java 8 Streams, based on the ideas behind Paul Philips' Views.
* Simplified functional interfaces wrap checked exceptions and ignore (auto-box) primitives.
* A tiny, type-safe data definition mini-language of brief helper functions: `vec()`, `set()`, `map()`, and `tup()`, (like Clojure's vector `[]`, set `#{}`, and map `{}`).
* Extend Tuples to make your own immutable Java classes (with `private final` member variables and correct `equals()`, `hashCode()`, and `toString()` implementations) almost as easily as writing case classes in Scala.

Java actually has a powerful type inferencing engine built in, but void return types, and different rules for arrays and primatives make it hard to take advantage of.
UncleJim avoids these pitfalls (and checked exceptions in lambdas) decreasing the amount of code you need to write by a factor of at least 2x over traditional Java.

#Maven Dependency
```xml
<dependency>
        <groupId>org.organicdesign</groupId>
        <artifactId>UncleJim</artifactId>
        <version>0.10.10</version>
</dependency>
```

#Examples
Create a vector (List) of integers and perform operations on it.
The results of each operation show in comments to the right.
```java
vec(4, 5)                        //          4, 5
        .precat(vec(1, 2, 3))    // 1, 2, 3, 4, 5
        .concat(vec(6, 7, 8, 9)) // 1, 2, 3, 4, 5, 6, 7, 8, 9
        .filter(i -> i > 4)      //             5, 6, 7, 8, 9
        .map(i -> i - 2)         //       3, 4, 5, 6, 7
        .take(4)                 //       3, 4, 5, 6
        .drop(2)                 //             5, 6
        .toImList());            // Stores result in an immutable PersistentVector
```

More extensive examples are implemented as unit tests to ensure that they remain correct and current.

* [Usage examples](src/test/java/org/organicdesign/fp/UsageExampleTest.java#L34) - Three different ways of improving your Java code with UncleJim.

* [Comparison with Traditional Java and Java 8 Streams](src/test/java/org/organicdesign/fp/TradJavaStreamComparisonTest.java#L22) - UncleJim generally takes 1/2 to 1/3 as much code to accomplish the same thing as Traditional Java, or Java 8 Streams.

* For complete API documentation, please build the javadoc: `mvn javadoc:javadoc`

#Self-Guided Training
[JimTrainer](https://github.com/GlenKPeterson/JimTrainer) contains a few short problem-sets for learning UncleJim 

#FAQ

##Q: How does this compare to pcollections?

[pcollections](http://pcollections.org/) competes only with UncleJim's first bullet point: immutable collections.
Clojure's vector (list) and hashMap/hashSet have "big O of log base 32 of n" asymptotic performance, which theoretically scales better than the binary tree structures it looks like pcollections uses (those are log base 2 of n).
You can see this for yourself by using the change-base formula to Google: y=(ln x)/(ln 2), y=(ln x)/(ln 32)

#[Graph of Log base 32 (red) vs. Log base 2 (blue)](logBase2VsLogBase32.png)

This graph shows how many operations each lookup requires (vertical axis) when there are a given number of items in the collection (horizontal axis).
Daniel Spiewak explains all the ramifications of this really well: https://www.youtube.com/watch?v=pNhBQJN44YQ

The Clojure collections also walk the sibling nodes in the internal data trees of these structures to provide iterators, which is pretty cool performance-wise.
At least in the list implementation, pcollections starts from the top of the tree doing an index lookup for each item, then increments the index to look up the next.

Clojure's (and Java's) sorted/tree map/set implementations are log base 2, so pcollections could theoretically be as fast or faster for those two collections.
If someone does performance testing to verify these theories, please let me know so I can link to it here.

UncleJim has additional benefits listed in the bullets at the top of this document.

##Q: Do these Transforms create intermediate collections between each operation (like the Scala collections)?

No.
Xform is a lazy, immutable builder for transformations.
It records the operations you specify without carrying any of them out.
When you call foldLeft() or one of the "endpoint" methods like toImList(), it creates the lightest-weight execution path and performs simplified operations in a for loop with only 3 if statements (some have sub-branches).
On my machine, single-threaded, with 30 million items in an ArrayList source, Xform takes an average of 122ms as opposed to 120ms for the native for loop - better than 98% as fast as the fastest iteration available on the JVM, but with much more functionality.
The heart of the implementation is [_foldLeft() in Xform](src/main/java/org/organicdesign/fp/xform/Xform.java), but it's kind of where "useful and easy to code" meets "fast to execute" and is probably some of the hardest code in the project to read and comprehend.
The Transfom implementation is loosely based on [Paul Philips concept of a "View,"](https://www.youtube.com/watch?v=uiJycy6dFSQ&t=26m19s) not on Clojure's Sequence abstraction.

There is only one exception: a second version of foldLeft() that takes an extra terminateWhen parameter to stop processing based on an output condition instead of an input condition.
I think I've used it once, ever, in a real-world situation (normally you use takeWhile to terminate based on an input condition).
It uses a temporary internal ArrayList to accumulate results for the termination test, then later converts them to whatever output format you specify.

##Q: How does this compare to Streams and lambda expressions in JDK8?

* When you process data with a Java8 stream, you end up with a mutable collection.
You can choose to do that with UncleJim, but it's safer to store your result in an immutable collection.
java.util.Collections can wrap mutable collections in an unmodifiable wrapper, but UncleJim's wrappers also deprecate the mutator methods so that your IDE and compiler warn you if you try to call them.

* If you later want a near-copy of a collection, (with a few items added or removed), Unmodifiable collections require an expensive defensive copy of the entire collection.
The Clojure-derived collections in UncleJim only duplicate the tiny area of the collection that was changed
to return a new immutable collection that shares as much data as practical with the old one.
As immutable collections go, they have excellent performance.

* The [java.util.function interfaces do nothing to help you with Exceptions](src/test/java/org/organicdesign/fp/TradJavaStreamComparisonTest.java#L258).
 [UncleJim wraps checked exceptions in unchecked ones](src/main/java/org/organicdesign/fp/function/Function1.java#L29) for you, so that you can write
 anonymous functions more like you would in Scala.

* Complexity: For up to 2-argument functions, java.util.function has 43 different interfaces.
The functional methods on these interfaces have 11 different names, depending on which of the 43 interfaces you use: accept(),
apply(), applyAsDouble(), applyAsInt(), applyAsLong(), test(), get(), getAsBoolean(), getAsDouble(), getAsInt(), or getAsLong().
UncleJim has 3 equivalent interfaces, named by number of arguments: Function0, Function1, and Function2.
All have an applyEx() that you override and an apply() method that callers can use if they want to ignore checked exceptions (they usually do).
If you don't want to return a result, declare the return type as ? and return null.
For example: `Function1<Integer,?>` takes an Integer and the return value is ignored.

* UncleJim also has extensible Tuples and a tiny data-definition language (like a type-safe JSON).

#Change Log
See [changeLog.txt](changeLog.txt)

#Licenses
Java&trade; is a registered trademark of the Oracle Corporation in the US and other countries.
UncleJim is not part of Java.
Oracle is in no way affiliated with the UncleJim project.

UncleJim is not part of Clojure.
Rich Hickey and the Clojure team are in no way affiliated with the UncleJim project, though it borrows heavily from their thoughts and is partly a derivative work of their open-source code.

The Clojure collections are licensed under the Eclipse Public License.
Versions of them have been included in this project and modified to add type safety and implement different interfaces.
These files are still derivative works under the EPL.

Unless otherwise stated, the rest of this work is licensed under the Apache 2.0 license.
New contributions should be made under the Apache 2.0 license whenever practical.
I believe it is more popular, clearer, and has been better tested in courts of law.

#Contributors
You can find build requirements, API highlights, and additional information in: [README2.md](README2.md).
