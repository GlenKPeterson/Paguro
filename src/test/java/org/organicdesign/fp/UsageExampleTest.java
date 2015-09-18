// Copyright 2015 PlanBase Inc. & Glen Peterson
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.organicdesign.fp;

import org.junit.Test;
import org.organicdesign.fp.collections.ImList;
import org.organicdesign.fp.collections.ImMap;
import org.organicdesign.fp.collections.UnmodIterable;
import org.organicdesign.fp.function.Function1;
import org.organicdesign.fp.tuple.Tuple2;
import org.organicdesign.fp.tuple.Tuple3;

import static org.junit.Assert.assertEquals;
import static org.organicdesign.fp.StaticImports.tup;
import static org.organicdesign.fp.StaticImports.vec;
import static org.organicdesign.fp.UsageExampleTest.EmailType.HOME;
import static org.organicdesign.fp.UsageExampleTest.EmailType.WORK;

// Usage examples are kept in this unit test to ensure they remain correct and current.
public class UsageExampleTest {

    // Transormable/UnmodIterable interfaces.
    @Test public void transformTest() {

        // These transformations do not change the underlying data.  They build a new collection by
        // chaining together the specified operations, then applying them in a single pass.  vec()
        // creates a vector (immutable list) of any length.
        ImList<Integer> v1 = vec(4, 5);

        // Make a new vector with more numbers at the beginning.  "precat" is short for
        // "prepend version of concatenate" or "add-to-beginning".  UnmodIterable is the primary
        // bridge between UncleJim and traditional Java.  It extends Iterable, but adds
        // transformations and deprecates the remove() method of the iterator it provides.
        UnmodIterable<Integer> v2 = v1.precat(vec(1, 2, 3));

        // v2 now represents a bigger list of numbers
        assertEquals(vec(1, 2, 3, 4, 5), v2.toImList());

        // v1 is unchanged
        assertEquals(vec(4, 5), v1);

        // Instead of updating in place, each change returns a new data structure which is an
        // extremely lightweight copy of the old because it shares as much as possible with the
        // previous structure.
        v2 = v2.concat(vec(6, 7, 8, 9));

        assertEquals(vec(1, 2, 3, 4, 5, 6, 7, 8, 9), v2.toImList());

        v2 = v2.filter(i -> i > 4);

        assertEquals(vec(5,6,7,8,9), v2.toImList());

        v2 = v2.map(i -> i - 2);

        assertEquals(vec(3,4,5,6,7), v2.toImList());

        // After a take, the subsequent items are not processed by the transformation.
        // If you had a billion items, this would only allow the first 5 to be processed.
        v2 = v2.take(4);

        assertEquals(vec(3,4,5,6), v2.toImList());

        v2 = v2.drop(2);

        assertEquals(vec(5,6), v2.toImList());

        // Let's see that again with the methods all chained together
        assertEquals(vec(5, 6),
                     vec(4, 5)                        //          4, 5
                             .precat(vec(1, 2, 3))    // 1, 2, 3, 4, 5
                             .concat(vec(6, 7, 8, 9)) // 1, 2, 3, 4, 5, 6, 7, 8, 9
                             .filter(i -> i > 4)      //             5, 6, 7, 8, 9
                             .map(i -> i - 2)         //       3, 4, 5, 6, 7
                             .take(4)                 //       3, 4, 5, 6
                             .drop(2)                 //             5, 6
                             .toImList());

        // Conclusion:
        // Once you get used to them, Transformations are easier to write and read than their
        // traditional Java looping counterparts (and almost as fast).  They are also much
        // easier to understand than Java 8 streams and handle and immutable destination
        // collections well.
    }

    // Define some field name constants with a standard Java Enum for subsequent examples
    enum EmailType { HOME, WORK };

    // Part 1 of 3 for defining data briefly and/or in a way that's easy to read.
    @Test public void dataDefinitionExample1() {
        // Create a list of "people."  tup() creates a Tuple (some languages call this a "record").
        // Tuples are type-safe anonymous objects.  Parts 2 and 3 of this example will show how to
        // simplify the type signatures.
        ImList<Tuple3<String,String,ImList<Tuple2<EmailType,String>>>> people =
                vec(tup("Jane", "Smith", vec(tup(HOME, "a@b.c"),
                                             tup(WORK, "b@c.d"))),
                    tup("Fred", "Tase", vec(tup(HOME, "c@d.e"),
                                            tup(WORK, "d@e.f"))));

        // Everything has build-in toString() methods.  Collections show the first 3-5 elements.
        assertEquals("PersistentVector(" +
                     "Tuple3(Jane,Smith,PersistentVector(Tuple2(HOME,a@b.c),Tuple2(WORK,b@c.d)))," +
                     "Tuple3(Fred,Tase,PersistentVector(Tuple2(HOME,c@d.e),Tuple2(WORK,d@e.f))))",
                     people.toString());

        // Inspect Jane's record:
        Tuple3<String,String,ImList<Tuple2<EmailType,String>>> jane = people.get(0);

        assertEquals("Tuple3(Jane,Smith,PersistentVector(Tuple2(HOME,a@b.c),Tuple2(WORK,b@c.d)))",
                     jane.toString());

        // Make a map to look up people by their email address.
        ImMap<String,Tuple3<String,String,ImList<Tuple2<EmailType,String>>>> peopleByEmail =

                // We may need to produce multiple entries in the resulting map/dictionary for each
                // person.  This is a one-to-many relationship and flatMap is the way to produce
                // zero or more output items for each input item.
                // Note: person._3() is the vector of email addresses
                people.flatMap(person -> person._3()
                                               // Map/dictionary entries are key value pairs.
                                               // Tuple2 implements Map.Entry making it easy to
                                               // create the pair right here.
                                               // Note: mail._2() is the address
                                               .map(mail -> tup(mail._2(), person)))
                        // Now convert the result into an immutable map.  The function argument is
                        // normally used to convert data into key/value pairs, but we already have
                        // key/value pairs, so we just pass the identity function (returns its
                        // argument unchanged)
                      .toImMap(Function1.identity());

        // Look at the map we just created
        assertEquals("PersistentHashMap(" +
                     "Tuple2(d@e.f," +
                     "Tuple3(Fred,Tase,PersistentVector(Tuple2(HOME,c@d.e),Tuple2(WORK,d@e.f))))," +
                     "Tuple2(a@b.c," +
                     "Tuple3(Jane,Smith,PersistentVector(Tuple2(HOME,a@b.c),Tuple2(WORK,b@c.d))))," +
                     "Tuple2(b@c.d," +
                     "Tuple3(Jane,Smith,PersistentVector(Tuple2(HOME,a@b.c),Tuple2(WORK,b@c.d))))," +
                     "Tuple2(c@d.e," +
                     "Tuple3(Fred,Tase,PersistentVector(Tuple2(HOME,c@d.e),Tuple2(WORK,d@e.f)))))",
                     peopleByEmail.toString());

        // Prove that we can now look up Jane by her address
        assertEquals(jane, peopleByEmail.get("b@c.d"));

        // Conclusion:
        // For the price of a few long type signatures, we can write very succinct Java code
        // without all the pre-work of defining types.  This is great for experiments, one-off
        // reports, or other low-investment high-value projects.
        //
        // Next we'll look at two different ways to eliminate or simplify those type signatures.
    }

    // Part 2 of 3
    @Test public void dataDefinitionExample2() {
        // Fluent interfaces can take maximum advantage of Java's type inferencing.  Here is
        // more-or-less the same code as the above, still type-checked by the compiler, but without
        // specifying ANY types explicitly:
        assertEquals("Jane",
                     // This is the "people" data structure from above
                     vec(tup("Jane", "Smith", vec(tup(HOME, "a@b.c"),
                                                  tup(WORK, "b@c.d"))),
                         tup("Fred", "Tase", vec(tup(HOME, "c@d.e"),
                                                 tup(WORK, "d@e.f"))))
                             // Create a map to look up people by their address
                             .flatMap(person -> person._3()
                                                      .map(mail -> tup(mail._2(), person)))
                             .toImMap(Function1.identity())
                             // Look up Jane by her address
                             .get("b@c.d")
                             // Get her first name
                             ._1());

        // Conclusion:
        // This style of coding is great for trying out ideas, but it can be difficult for the
        // person after you to read, especially with all the _1(), _2(), and _3() methods on tuples.
        // Naming your data types well can make it more legible as we'll see next.
    }

    // Part 3 of 3
    // The previous examples could have been cleaned up with ML's or Scala's type aliases, or
    // Scala's case classes.  In Java we have to use objects, but doing so has the useful side
    // effect of letting us name the accessor methods.  Extending Tuples also give us immutable
    // fields, equals(), hashCode(), and toString() implementations for free, which we would
    // otherwise have to write and debug by hand!
    static class Email extends Tuple2<EmailType,String> {
        Email(EmailType t, String s) { super(t, s); }

        // As long as we are making an object, we might as well give descriptive names to the
        // field getters
        public EmailType type() { return _1; }
        public String address() { return _2; }
    }

    // Notice in this type signature, we have replaced Tuple2<EmailType,String> with Email
    static class Person extends Tuple3<String,String,ImList<Email>> {
        Person(String f, String l, ImList<Email> es) { super(f, l, es); }

        // Give more descriptive names to the field getters
        public String first() { return _1; }
        public String last() { return _2; }
        public ImList<Email> emailAddrs() { return _3; }
    }

    // Part 3 of 3 (continued)
    // Use the classes we made above to simplify the types and improve the toString implementations.
    @Test public void dataDefinitionExample3() {

        // Compare this type signature with the first example.  Wow!
        ImList<Person> people =
                vec(new Person("Jane", "Smith", vec(new Email(HOME, "a@b.c"),
                                                    new Email(WORK, "b@c.d"))),
                    new Person("Fred", "Tase", vec(new Email(HOME, "c@d.e"),
                                                   new Email(WORK, "d@e.f"))));

        // Notice that the tuples are smart enough to take their new names, Person and Email instead
        // of Tuple3 and Tuple2.  This aids readability when debugging.
        assertEquals("PersistentVector(" +
                     "Person(Jane,Smith," +
                     "PersistentVector(Email(HOME,a@b.c),Email(WORK,b@c.d)))," +
                     "Person(Fred,Tase," +
                     "PersistentVector(Email(HOME,c@d.e),Email(WORK,d@e.f))))",
                     people.toString());

        // This type signature couldn't be simpler:
        Person jane = people.get(0);

        assertEquals("Person(Jane,Smith," +
                     "PersistentVector(Email(HOME,a@b.c),Email(WORK,b@c.d)))",
                     jane.toString());

        // Let's use our new, descriptive field getter methods:
        assertEquals("Jane", jane.first());
        assertEquals("Smith", jane.last());

        Email janesAddr = jane.emailAddrs().get(0);
        assertEquals(HOME, janesAddr.type());
        assertEquals("a@b.c", janesAddr.address());

        // Another simplified type signature.  Descriptive method names are much easier to read.
        ImMap<String,Person> peopleByEmail =
                people.flatMap(person -> person.emailAddrs()
                                               .map(mail -> tup(mail.address(), person)))
                      .toImMap(Function1.identity());

        assertEquals("PersistentHashMap(" +
                     "Tuple2(d@e.f," +
                     "Person(Fred,Tase,PersistentVector(Email(HOME,c@d.e),Email(WORK,d@e.f))))," +
                     "Tuple2(a@b.c," +
                     "Person(Jane,Smith,PersistentVector(Email(HOME,a@b.c),Email(WORK,b@c.d))))," +
                     "Tuple2(b@c.d," +
                     "Person(Jane,Smith,PersistentVector(Email(HOME,a@b.c),Email(WORK,b@c.d))))," +
                     "Tuple2(c@d.e," +
                     "Person(Fred,Tase,PersistentVector(Email(HOME,c@d.e),Email(WORK,d@e.f)))))",
                     peopleByEmail.toString());

        // Now look jane up by address.
        assertEquals(jane, peopleByEmail.get("b@c.d"));

        // Conclusion:
        // Extending Tuples lets us write Immutable, Object-Oriented, Functional code with less
        // boilerplate than traditional Java coding.  In addition to brevity, this approach improves
        // legibility, reliability, and consistency.
        //
        // You can begin with a quick and dirty proof of concept, then define types later to make
        // your code easy to maintain.  If you need to write out a complex type like the first
        // example, that's a sign that it's time to define some classes with good names.
    }
}
