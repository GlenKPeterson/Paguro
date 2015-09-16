UncleJim ("**Un**modifiable **Coll**ections for **J**ava&trade; **Imm**utability") brings the following to Java:

* Type-safe versions of Clojure's immutable collections
* An immutable Transformation Builder, kind of like Clojure's sequence abstraction.
* A tiny, type-safe data definition mini-language of brief helper functions: `vec()`, `set()`, `map()`, and `tup()`, (like Clojure's vector `[]`, set `#{}`, and map `{}`).

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

* [Comparison with Traditional Java and Java 8 Streams](src/test/java/org/organicdesign/fp/TradJavaStreamComparisonTest.java#L22) - UncleJim generally takes 1/2 to 1/3 as much code to accomplish the same thing as Traditional Java, or Java 8 Streams.

* [Pure-Jim usage examples](src/test/java/org/organicdesign/fp/UsageExampleTest.java#L34) - Three different ways of improving your Java code with UncleJim.

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
