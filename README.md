UncleJim ("**Un**modifiable **Coll**ections for **J**ava™ **Imm**utability") is a small library that enables a cleaner, safer style of Java programming.  It approaches a domain specific language (DSL) inside Java.

#Brief Example
```java
// Define some people with lists of email addresses on the
// fly.  vec() makes a List, tup() makes a Tuple
vec(tup("Jane", "Smith", vec("a@b.c", "b@c.d")),
    tup("Fred", "Tase", vec("c@d.e", "d@e.f", "e@f.g")))

        // Turn that into pairs of emails and people:
        // flatMap() the list of people into a list of
        // emails.  map() the emails to email/person pairs
        // while the person object is still in scope.
        .flatMap(person -> person._3()
                                 .map(mail -> tup(mail,
                                                  person)))

        // toImMap() expects a function that maps items to
        // key/value pairs.  We already have pairs, so pass
        // it the identity function.
        .toImMap(x -> x)

        // Look up Jane by her address
        .get("b@c.d")

        // Get her first name (returns "Jane")
        ._1();
```

#Status
[![Build Status](https://travis-ci.org/GlenKPeterson/UncleJim.svg?branch=master)](https://travis-ci.org/GlenKPeterson/UncleJim)
[![Code Coverage](http://codecov.io/github/GlenKPeterson/UncleJim/coverage.svg?branch=master)](http://codecov.io/github/GlenKPeterson/UncleJim?branch=master)

[![Join the chat at https://gitter.im/GlenKPeterson/UncleJim](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/GlenKPeterson/UncleJim?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

#Maven Dependency
Available from the [Maven Repository](http://mvnrepository.com/artifact/org.organicdesign/UncleJim) as:
```xml
<dependency>
        <groupId>org.organicdesign</groupId>
        <artifactId>UncleJim</artifactId>
        <version>0.10.16</version>
</dependency>
```

#Features

* Type-safe versions of Clojure's immutable collections, implementing the generic `java.util` collection interfaces.
* A simplified immutable alternative to Java 8 Streams, wrapping checked exceptions and avoiding primitives.
* A tiny, type-safe data definition language of brief helper functions: `vec()`, `set()`, `map()`, and `tup()`, (like Clojure's vector `[]`, set `#{}`, and map `{}`).
* Extend Tuples to make your own immutable Java classes (with correct `equals()`, `hashCode()`, and `toString()` implementations) almost as easily as writing case classes in Scala.

UncleJim takes advantages of Java's type inferencing by avoiding void return types, arrays, primatives, and checked exceptions in lambdas.  It can decrease the amount of code you need to write by a factor of at 2x-3x while focusing you on using the right collections for the fastest possible code.

#Details

Usage examples are implemented as unit tests to ensure that they remain correct and current.

* [Usage examples](src/test/java/org/organicdesign/fp/UsageExampleTest.java#L34) - different ways of improving your Java code with UncleJim.

* [Comparison with Traditional Java and Java 8 Streams](src/test/java/org/organicdesign/fp/TradJavaStreamComparisonTest.java#L22)

* [Class/Interface Hierarchy](inheritanceHierarchy.pdf) (PDF)

* For complete API documentation, please build the javadoc: `mvn javadoc:javadoc`

* [JimTrainer self-guided training](https://github.com/GlenKPeterson/JimTrainer) consists of a few short problem-sets for learning UncleJim

* A summary of recent updates is in the [Change Log](changeLog.md)

#Manifesto

* Immutability promotes correct code as much as type safety does.
* Better to focus on picking the appropriate collections and transformations than on looping details.
* Write functions before defining classes, yet still take advantage of type safety.
* On-the fly data definition should be simple and easy.  Naming/formalizing those data structures should be too.
* Minimal, easy-to-understand interface covering the most critical building blocks for higher functionality.

#FAQ

##Q: How does this compare to PCollections?

[UncleJim is based on Clojure, theoretically faster and has additional features](wiki/UncleJim%20vs.%20PCollections)

##Q: Do these Transforms create intermediate collections between each operation (like the Scala collections)?

[No](https://github.com/GlenKPeterson/UncleJim/wiki/How-do-%22Xforms%22---Transformations-work%3F)

##Q: How does this compare to Streams and lambda expressions in JDK8?

[Comparison](https://github.com/GlenKPeterson/UncleJim/wiki/Comparison-with-Streams-and-Lambdas-in-JDK8)

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
You can find outdated build requirements, API highlights, and additional information in: [README2.md](README2.md).
