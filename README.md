Type-safe versions of Clojure's immutable collections, an immutable alternative to Java 8 Streams, and other tools to make functional programming in Java easier.

![Hermit Crab](https://c7.staticflickr.com/8/7413/12171498934_2934c7ef28_n.jpg)
Photo by [Rushen](https://www.flickr.com/photos/rushen/12171498934/in/photostream/)

Paguro is short for the Latin "Paguroidea" - the name of the Hermit Crab superfamily in Biology.  These collections grow by adding a new shell, leaving the insides the same, much the way [Hermit Crabs trade up to a new shell when they grow](https://www.youtube.com/watch?v=f1dnocPQXDQ).  This project used to be called UncleJim. 

# News
A summary of recent updates is in the [Change Log](CHANGE_LOG.md)

Future development priorities are further down this page.

# Features

* [Immutable collections](src/main/java/org/organicdesign/fp/collections) - type-safe generic Java versions of Clojure's immutable (HAMT = 'Hash Array Mapped Trie') collections - arguably the best immutable collections on the JVM.  Plus an RRB Tree!
* [Functional transformations](src/main/java/org/organicdesign/fp/xform/Transformable.java#L42) are like a type-safe version of Clojure's Transducers, or a simplified immutable alternative to Java 8 Streams, wrapping checked exceptions and avoiding primitives (you can still use Java 8 streams if you want to).
* [Brief collection constructors](src/main/java/org/organicdesign/fp/StaticImports.java#L36) are like a tiny, type-safe data definition language:
  * `vec("one", "two", "three")` - an immutable vector/list of three strings
  * `set(3, 5, 7)` - an immutable set of three integers
  * `tup("Alice", 11, 3.14)` - an immutable 3-field tuple or record
  * `map(tup(1, "single"), tup(2, "double"), tup(3, "triple"))` - an immutable map that uses integers to look up appropriate strings.
* [Extensible, immutable tuples](src/main/java/org/organicdesign/fp/tuple) - use them for rapid prototyping, then later extend them to make your own lightweight, immutable Java classes with correct `equals()`, `hashCode()`, and `toString()` implementations.
* [Lazy initialization](src/main/java/org/organicdesign/fp/LazyRef.java#L5) - LazyRef thread-safely performs initialization and frees initialization resources on first use.  Subsequent uses get the now-constant initialized value.  Use this instead of static initializers to avoid initialization loops.  Cache results of expensive operations for reuse.
* [Memoization](src/main/java/org/organicdesign/fp/function/Fn3.java#L42) - Turns function calls into hashtable lookups to speed up slow functions over a limited range of inputs.
* Tiny with no dependencies - The entire project fits in a 230K jar file that is compiled in the compact1 profile.

For complete API documentation, please build the javadoc:
`mvn javadoc:javadoc`

Paguro takes advantage of Java's type inferencing.  It eschews void return types, arrays, primatives, and checked exceptions in lambdas.  It can decrease the amount of code you need to write by a factor of at 2x-3x.  Using functional transfomrations instead of loops focuses you on choosing the right collections which leads to more readable code AND better Big O complexity/scalability.

[![Build Status](https://travis-ci.org/GlenKPeterson/Paguro.svg?branch=master)](https://travis-ci.org/GlenKPeterson/Paguro)
[![Code Coverage](http://codecov.io/github/GlenKPeterson/Paguro/coverage.svg?branch=master)](http://codecov.io/github/GlenKPeterson/Paguro?branch=master)

[![Join the chat at https://gitter.im/GlenKPeterson/Paguro](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/GlenKPeterson/Paguro?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

Available from the [Maven Repository](http://mvnrepository.com/artifact/org.organicdesign/Paguro) as:
```xml
<dependency>
        <groupId>org.organicdesign</groupId>
        <artifactId>Paguro</artifactId>
        <version>3.0.4-ALPHA</version>
</dependency>
```

# Details

```java
// Define some people with lists of email addresses on the fly.
// vec() makes a Vector/List, tup() makes a Tuple
vec(tup("Jane", "Smith", vec("a@b.c", "b@c.d")),
    tup("Fred", "Tase", vec("c@d.e", "d@e.f", "e@f.g")))

        // We want to look up people by their address.
        // There are multiple addresses per person.
        // For each person, find their email addresses.
        .flatMap(person -> person._3()

                                 // For each address, produce a key/value pair
                                 // of email and person (Tuple2 implements Map.Entry)
                                 .map(email -> tup(email, person)))

        // toImMap() collects the results to key/value pairs and puts
        // them in an immutable map.  We already have pairs, so pass
        // them through unchanged.
        .toImMap(x -> x)

        // Look up Jane by her address
        .get("b@c.d")

        // Get her first name (returns "Jane")
        ._1();
```

* Other [usage examples](src/test/java/org/organicdesign/fp/UsageExampleTest.java#L34) are implemented as unit tests to ensure that they remain correct and current.

* [Class/Interface Hierarchy](inheritanceHierarchy.pdf) (PDF)

* Get started now by following the [Usage Tips](https://github.com/GlenKPeterson/Paguro/wiki/Usage-Tips)

* For complete API documentation, please build the javadoc: `mvn javadoc:javadoc`

* [JimTrainer self-guided training](https://github.com/GlenKPeterson/JimTrainer) consists of a few short problem-sets for learning Paguro

* [Comparison with Traditional Java and Java 8 Streams](src/test/java/org/organicdesign/fp/TradJavaStreamComparisonTest.java#L22)

# Manifesto

* Immutability promotes correct code as much as type safety does.
* Better to focus on picking the appropriate collections and transformations than on looping details.
* Write functions before defining classes, yet still take advantage of type safety.
* On-the fly data definition should be simple and easy.  Naming/formalizing those data structures should be too.
* Minimal, easy-to-understand interface covering the most critical building blocks for higher functionality.

# FAQ

### Q: Why are you doing this?

It started with a Software Engineering Stack Exchange question: [Why doesn't Java provide immutable collections?](https://softwareengineering.stackexchange.com/questions/221762/why-doesnt-java-8-include-immutable-collections)

### Q: How does this compare to PCollections?

[Paguro is based on Clojure, faster and has additional features](https://github.com/GlenKPeterson/Paguro/wiki/UncleJim-vs.-PCollections)

### Q: Do these Transforms create intermediate collections between each operation (like the Scala collections)?

[No](https://github.com/GlenKPeterson/Paguro/wiki/How-do-%22Xforms%22---Transformations-work%3F)

### Q: How does this compare to Streams and lambda expressions in JDK8?

[Comparison](https://github.com/GlenKPeterson/Paguro/wiki/Comparison-with-Streams-and-Lambdas-in-JDK8)

### Q: Why Java instead of another/better JVM language?

[Why Java?](https://github.com/GlenKPeterson/Paguro/wiki/Why-is-UncleJim-written-in-Java%3F)

# Future Development Ideas (as of 2017-05-28)
1. Add reverseIterator() or similar to SortedUnmodIterable
2. Transformable needs `first()` and `last()`, but maybe only on a SortedIterable.  Otherwise, `any(Fn1<Boolean>)`
3. Ensure everything is as friendly as possible to Monadic and Reactive thinking.
4. Provide a Fn1v subclass of Fn1 (and similar for Fn0, Fn2, etc.) that returns void because sometimes you need one of those for backward compatibility and you don't want it to choke on checked exceptions.
5. Make a Kotlin branch and release (Paguro-K).
6. Consider insertion-order maps and sets
7. Make a Java 7 branch and release (Paguro-J7).

### RRB-Tree
Read the [current development status](https://github.com/GlenKPeterson/Paguro/issues/4#issuecomment-239825939) or check out the [latest version of the code](https://github.com/GlenKPeterson/Paguro/blob/2016-05-22_RRB-Tree/src/main/java/org/organicdesign/fp/experimental/RrbTree1.java).

As Norm Zeck pointed out by sending me [Ropes: an Alternative to Strings](http://citeseer.ist.psu.edu/viewdoc/download?doi=10.1.1.14.9450&rep=rep1&type=pdf), an RRB-Tree might make a great implementation of *both* String and StringBuilder.  We might want to add a Char8 (UTF-8 Character class pronounced "crate") and make Str8 (UTF-8 String pronounced "straight") a sub-class of RRB-Tree.  Just a thought.

# Licenses
Java&trade; is a registered trademark of the Oracle Corporation in the US and other countries.
Paguro is not part of Java.
Oracle is in no way affiliated with the Paguro project.

Paguro is not part of Clojure.
Rich Hickey and the Clojure team are in no way affiliated with the Paguro project, though it borrows heavily from their thoughts and is partly a derivative work of their open-source code.

The Clojure collections are licensed under the Eclipse Public License.
Versions of them have been included in this project and modified to add type safety and implement different interfaces.
These files are still derivative works under the EPL.

Unless otherwise stated, the rest of this work is licensed under the Apache 2.0 license.
New contributions should be made under the Apache 2.0 license whenever practical.
I believe it is more popular, clearer, and has been better tested in courts of law.

# More
Additional information is in: [README2.md](README2.md).
