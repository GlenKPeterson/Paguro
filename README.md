UncleJim ("**Un**modifiable **Coll**ections for **J**ava&trade; **Imm**utability") brings the following to Java:

* Type-safe versions of Clojure's immutable collections
* An immutable Transformable.  This is a simplified alternative to Java 8 Streams, based on the ideas behind Paul Philips' Collections (Scala) Views (or Clojure Transducers).
* Simplified functional interfaces wrap checked exceptions
* A tiny, type-safe data definition mini-language of brief helper functions: `vec()`, `set()`, `map()`, and `tup()`, (like Clojure's vector `[]`, set `#{}`, and map `{}`).
* Tuples can be extended to make your own immutable Java classes (with `private final` member variables and correct `equals()`, `hashCode()`, and `toString()` methods) about as easily as using case classes in Scala.

Java actually has a powerful type inferencing engine built in. Void return types, and different rules for arrays and primatives make it hard to take advantage of.  UncleJim avoids these pitfalls (and checked exceptions as well), decreasing the amount of code you need to write by a factor of 2x or 3x.

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
```

More extensive examples are implemented as unit tests to ensure that they remain correct and current.

* [Usage examples](src/test/java/org/organicdesign/fp/UsageExampleTest.java#L34) - Three different ways of improving your Java code with UncleJim.

* [Comparison with Traditional Java and Java 8 Streams](src/test/java/org/organicdesign/fp/TradJavaStreamComparisonTest.java#L22) - UncleJim generally takes 1/2 to 1/3 as much code to accomplish the same thing as Traditional Java, or Java 8 Streams.

* For complete API documentation, please build the javadoc: `mvn javadoc:javadoc`

#Self-Guided Training
[JimTrainer](https://github.com/GlenKPeterson/JimTrainer) contains a few short problem-sets for learning UncleJim 

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
