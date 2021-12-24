![Hermit Crab](HermitCrabByRushen.jpg)

[![Maven Central](https://img.shields.io/maven-central/v/org.organicdesign/Paguro.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22org.organicdesign%22%20AND%20a:%22Paguro%22)
[![javadoc](https://javadoc.io/badge2/org.organicdesign/Paguro/javadoc.svg)](https://javadoc.io/doc/org.organicdesign/Paguro)
[![Build Status](https://travis-ci.org/GlenKPeterson/Paguro.svg?branch=master)](https://travis-ci.org/GlenKPeterson/Paguro)
[![codecov](https://codecov.io/gh/GlenKPeterson/Paguro/branch/master/graph/badge.svg)](https://codecov.io/gh/GlenKPeterson/Paguro)
[![Google group : Email List](https://img.shields.io/badge/Google%20Group-Email%20List-blue.svg)](https://groups.google.com/d/forum/paguro)

# Why Use Paguro?

Paguro is designed to:
 1. Make Functional Programming simpler and easier in Java.
 2. Decrease the number of things you need to think about when coding.

Removing distractions leaves you more energy for creativity and problem-solving.
Paguro lets you forget about:

 * Potential modifications to shared collections (immutable collections are safe to share)
 * The cost of adding items to an unmodifiable collection (immutable collections support extremely lightweight modified copies)
 * Which collections are modifiable (your compiler knows)
 * Arrays vs. collections (use `xformArray(myArray)` to encapsulate arrays)
 * Primitives vs. boxed objects (Don't use primitives - generics can't handle them anyway)
 * Checked exceptions in lambdas (Paguro accepts them without complaint)
 * Null pointer exceptions

Kotlin fixes almost all these issues too, but if you're stuck in Java, Paguro is a great solution.

# What's in Paguro?
Type-safe, null-safe versions of Clojure's immutable/persistent collections, an immutable alternative to Java 8 Streams that handles checked exceptions in lambdas, and other tools to make functional programming in Java easier.

# Why is it called Paguro?

Paguro is short for the Latin "Paguroidea" - the name of the Hermit Crab superfamily in Biology.  These collections grow by adding a new shell, leaving the insides the same, much the way [Hermit Crabs trade up to a new shell when they grow](https://www.youtube.com/watch?v=f1dnocPQXDQ).

# Specific Features

* **Immutable collections** [api](https://javadoc.io/doc/org.organicdesign/Paguro/latest/org/organicdesign/fp/collections/package-summary.html) / [src](src/main/java/org/organicdesign/fp/collections) - type-safe generic Java versions of Clojure's immutable (HAMT = 'Hash Array Mapped Trie') collections - arguably the best immutable collections on the JVM.  Plus an RRB Tree!
* **Functional transformations** [api](https://javadoc.io/doc/org.organicdesign/Paguro/latest/org/organicdesign/fp/xform/package-summary.html) / [src](src/main/java/org/organicdesign/fp/xform/Transformable.java) are like a type-safe version of Clojure's Transducers, or a simplified immutable alternative to Java 8 Streams, wrapping checked exceptions and avoiding primitives (you can still use Java 8 streams if you want to).
* **Brief collection constructors** [api](https://javadoc.io/doc/org.organicdesign/Paguro/latest/org/organicdesign/fp/StaticImports.html) / [src](src/main/java/org/organicdesign/fp/StaticImports.java) are like a tiny, type-safe data definition language (a little like JSON for Java):
  * `vec("one", "two", "three")` - an immutable vector/list of three strings
  * `set(3, 5, 7)` - an immutable set of three integers
  * `tup("Alice", 11, 3.14)` - an immutable 3-field tuple or record
  * `map(tup(1, "single"), tup(2, "double"), tup(3, "triple"))` - an immutable map that uses integers to look up appropriate strings.
* **Extensible, immutable tuples** [api](https://javadoc.io/doc/org.organicdesign/Paguro/latest/org/organicdesign/fp/tuple/package-summary.html) / [src](src/main/java/org/organicdesign/fp/tuple) - use them for rapid, yet type-safe prototyping, then later extend them to make your own lightweight, immutable Java classes with correct `equals()`, `hashCode()`, and `toString()` implementations.
* **Lazy initialization** [api](https://javadoc.io/doc/org.organicdesign/Paguro/latest/org/organicdesign/fp/function/LazyRef.html) / [src](src/main/java/org/organicdesign/fp/function/LazyRef.java) - LazyRef thread-safely performs initialization and frees initialization resources on first use.  Subsequent uses get the now-constant initialized value.  Use this instead of static initializers to avoid initialization loops.  Cache results of expensive operations for reuse.
* **Union types** [api](https://javadoc.io/doc/org.organicdesign/Paguro/latest/org/organicdesign/fp/oneOf/package-summary.html) / [src](src/main/java/org/organicdesign/fp/oneOf) - Not as nice as being built into the language, but they extend type safety outside the object hierarchy.
* **Memoization** [api](https://javadoc.io/doc/org.organicdesign/Paguro/latest/org/organicdesign/fp/function/Fn3.html) / [src](src/main/java/org/organicdesign/fp/function/Fn3.java) - Turns function calls into hashtable lookups to speed up slow functions over a limited range of inputs.
* **Tiny** with no dependencies - The entire project fits in a 270K jar file.

# Examples

[Usage examples](src/test/java/org/organicdesign/fp/UsageExampleTest.java) are implemented as unit tests to ensure that they remain correct and current.


# Getting Started

* [Class/Interface Hierarchy](inheritanceHierarchy.pdf) (PDF)
* [API Docs](https://javadoc.io/doc/org.organicdesign/Paguro/latest/index.html)
* Get started now by following the [Usage Tips](https://github.com/GlenKPeterson/Paguro/wiki/Usage-Tips)
* [Comparison with Traditional Java and Java 8 Streams](src/test/java/org/organicdesign/fp/TradJavaStreamComparisonTest.java)

# Classic
You are on the Paguro Classic, or main branch of this project.
If you work with pure Java, or a mix of Java and Kotlin files, this is your branch.
If you want to live dangerously, try the all-Kotlin version in the 4.0 branch when it becomes available.

# News
## RrbTree.join() seems to work now
RrbTree is still a new class, but as of 3.7.2, there are no known bugs.  Fingers crossed!

Check the [Change Log](CHANGE_LOG.md) for details of recent changes.

# FAQ

### Q: Why are you doing this?

It started with a Software Engineering Stack Exchange question: [Why doesn't Java provide immutable collections?](https://softwareengineering.stackexchange.com/questions/221762/why-doesnt-java-8-include-immutable-collections)

### Q: How does this compare to PCollections?

[Paguro is based on Clojure, is faster, and has additional features](https://github.com/GlenKPeterson/Paguro/wiki/UncleJim-vs.-PCollections)

### Q: Do these Transforms create intermediate collections between each operation (like the Scala collections)?

[No](https://github.com/GlenKPeterson/Paguro/wiki/How-do-%22Xforms%22---Transformations-work%3F)

### Q: How does this compare to Streams and lambda expressions in JDK8?

[Comparison](https://github.com/GlenKPeterson/Paguro/wiki/Comparison-with-Streams-and-Lambdas-in-JDK8)

### Q: Why Java instead of another/better JVM language?

[Why Java?](https://github.com/GlenKPeterson/Paguro/wiki/Why-is-Paguro-written-in-Java%3F)
That said, this could become a Kotlin-based project.

# Licenses
Java&trade; is a registered trademark of the Oracle Corporation in the US and other countries.
Paguro is not part of Java.
Oracle is in no way affiliated with the Paguro project.

Paguro is not part of Clojure.
Rich Hickey and the Clojure team are in no way affiliated with the Paguro project, though it borrows heavily from their thoughts and is partly a derivative work of their open-source code.

The Clojure collections are licensed under the Eclipse Public License.
Versions of them have been included in this project and modified to add type safety and implement different interfaces.
These files are still derivative works under the EPL.

Unless otherwise stated, the rest of this work may be licensed under EITHER the Eclipse 1.0 or the Apache 2.0.
You get to choose!
New contributions should be made under both licenses whenever practical.
I believe Apache is more popular, clearer, and has been better tested in courts of law.

Hermit Crab Photo by [Rushen](https://www.flickr.com/photos/rushen/12171498934/in/photostream/)

# Contributing

Questions?  Ideas?  Feedback?  Use the [Google Group Email List](https://groups.google.com/d/forum/paguro).
Clear bugs or simple pull requests can be made on Github without discussing them first on the email list.

If you submit a patch, please:
 - Keep the changes minimal (don't let your IDE reformat whole files).
 - Try to match the code style as best you can.
 - Clearly document your changes.
 - Update the unit tests to clearly and simply prove that your code works.
 - It's a good idea to discuss proposed changes on the email list before you spend time coding.

### Build from Source

The [pre-built jar file](https://search.maven.org/search?q=g:%22org.organicdesign%22%20AND%20a:%22Paguro%22) is the easiest way to use Paguro.
Users typically only build Paguro from source to make a contribution, or to experiment with the source code.

#### Prerequisites
Paguro is usually built on Ubuntu 18.04 and later with `openjdk-11`, `git`, and `maven` installed from the official repositories.
Being Java it should theoretically build with JDK 11+ on any system.

##### Environment Variables
Depending on how you installed Java and Maven, you may need to set some of the following in your `~/.profile` file and reboot (or source that file like `. ~/.profile` from the command line you will use for the build).
Or do whatever Windows does.
If your tools are installed in different directories, you will have to fix the following:
```bash
export JDK_HOME=/usr/lib/jvm/java-11-openjdk-amd64
export JAVA_HOME=$JDK_HOME/jre
export M2_HOME=$TOOLS/apache-maven-3.3.9/
export M2="$M2_HOME"bin
export PATH=$PATH:$M2
```

##### Build
```bash
# Start in an appropriate directory

# You need TestUtils for Paguro's equality testing.
# The first time you build, get a local copy of that and Paguro
git clone https://github.com/GlenKPeterson/Paguro.git

# Build Paguro:
cd Paguro
git pull
mvn clean install
```

# More
Additional information is in: [README2.md](README2.md).
