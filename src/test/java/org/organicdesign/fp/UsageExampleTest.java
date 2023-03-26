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
import org.organicdesign.fp.function.Fn1;
import org.organicdesign.fp.tuple.Tuple2;
import org.organicdesign.fp.tuple.Tuple3;

import static org.junit.Assert.assertEquals;
import static org.organicdesign.fp.StaticImports.tup;
import static org.organicdesign.fp.StaticImports.vec;
import static org.organicdesign.fp.UsageExampleTest.EmailType.HOME;
import static org.organicdesign.fp.UsageExampleTest.EmailType.WORK;

// Usage examples are kept in this unit test to ensure they remain correct and current.
public class UsageExampleTest {

    // Fluent interfaces (using methods that evaluate to something useful) takes maximum advantage
    // of Java's type inferencing.  This is still type-checked by the compiler, but without
    // specifying ANY types explicitly
    // Part 1 of 3
    @Test public void dataDefinitionPart1() {
        // In this example, we're going to define some people with email addresses, then look up
        // Jane by her email address. The first line is a JUnit test to verify that "Jane" is the
        // name of the person we find with the subsequent code.
        assertEquals("Jane",
                     // Define some people with lists of email addresses on the fly.
                     // vec() makes a Vector/List
                     // tup() makes a type-safe, anonymous Tuple (pair, triple, etc.)
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
                             ._1());

        // Conclusion:
        // Traditional Java forces you to define all your data types (as classes) before writing any
        // "logic" (functions).  While this discipline can help huge, well defined projects, a lot
        // of coding involves experimentation.  Code like the above skips the pre-work of defining
        // types so you can focus on building the right functions.
        //
        // No amount of processing power can turn O(n^2) to O(n) or to O(log n), but choosing the
        // right collections can!  This style of coding focuses you on that (and on
        // algorithms/transformations) instead of distracting you with looping details:
        // if/break/continue/return/i++, etc.
    }

    // Define some field name constants with a standard Java Enum for subsequent examples
    enum EmailType { HOME, WORK }

    // Part 2 of 3
    @Test public void dataDefinitionPart2() {
        // Let's expand on the previous example by specifying home and work email addresses.  We'll
        // also divide the processing into distinct pieces that can be used separately.  In a
        // subsequent example, we'll show how to simplify the type signatures.
        ImList<Tuple3<String,String,ImList<Tuple2<EmailType,String>>>> people =
                vec(tup("Jane", "Smith", vec(tup(HOME, "a@b.c"),
                                             tup(WORK, "b@c.d"))),
                    tup("Fred", "Tase", vec(tup(HOME, "c@d.e"))));

        // Everything has build-in toString() methods.  Collections show the first 3-5 elements.
        assertEquals("PersistentVector(" +
                     "Tuple3(\"Jane\",\"Smith\",PersistentVector(Tuple2(HOME,\"a@b.c\")," +
                     "Tuple2(WORK,\"b@c.d\")))," +
                     "Tuple3(\"Fred\",\"Tase\",PersistentVector(Tuple2(HOME,\"c@d.e\"))))",
                     people.toString());

        // Inspect Jane's record:
        Tuple3<String,String,ImList<Tuple2<EmailType,String>>> jane = people.get(0);

        assertEquals("Tuple3(\"Jane\",\"Smith\",PersistentVector(Tuple2(HOME,\"a@b.c\")," +
                     "Tuple2(WORK,\"b@c.d\")))",
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
                                               // Note: mail._2() is the address string
                                               .map(mail -> tup(mail._2(), person)))
                        // Convert the result into an immutable map.  The function argument is
                        // normally used to convert data into key/value pairs, but we already have
                        // key/value pairs, so we just pass the identity function (returns its
                        // argument unchanged)
                      .toImMap(x -> x);

        // Look at the map we just created
        assertEquals("PersistentHashMap(" +
                     "Tuple2(\"a@b.c\"," +
                     "Tuple3(\"Jane\",\"Smith\",PersistentVector(Tuple2(HOME,\"a@b.c\")," +
                     "Tuple2(WORK,\"b@c.d\"))))," +
                     "Tuple2(\"b@c.d\"," +
                     "Tuple3(\"Jane\",\"Smith\",PersistentVector(Tuple2(HOME,\"a@b.c\")," +
                     "Tuple2(WORK,\"b@c.d\"))))," +
                     "Tuple2(\"c@d.e\"," +
                     "Tuple3(\"Fred\",\"Tase\",PersistentVector(Tuple2(HOME,\"c@d.e\")))))",
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

    // Part 3 of 3
    // The previous example could have been cleaned up with ML's or Scala's type aliases, or Scala's
    // case classes.  In Java we have to use objects, but doing so lets us name the accessor
    // methods.  Extending Tuples also give us immutable fields, equals(), hashCode(), and
    // toString() implementations for free!
    static class Email extends Tuple2<EmailType,String> {
        Email(EmailType t, String s) { super(t, s); }

        // Give descriptive names to the field getters
        EmailType mailType() { return field1; }
        String address() { return field2; }
    }

    // Notice in this type signature, we have replaced Tuple2<EmailType,String> with Email
    private static class Person extends Tuple3<String,String,ImList<Email>> {
        Person(String f, String l, ImList<Email> es) { super(f, l, es); }

        // Give descriptive names to the field getters
        String first() { return _1; }
        String last() { return _2; }
        ImList<Email> emailAddrs() { return _3; }
    }

    // Part 3 of 3 (continued)
    // Use the classes we made above to simplify the types and improve the toString implementations.
    @Test public void dataDefinitionExample3() {

        // Compare this type signature with the previous example.  Wow!
        ImList<Person> people =
                vec(new Person("Jane", "Smith", vec(new Email(HOME, "a@b.c"),
                                                    new Email(WORK, "b@c.d"))),
                    new Person("Fred", "Tase", vec(new Email(HOME, "c@d.e"),
                                                   new Email(WORK, "d@e.f"))));

        // Notice that the tuples are smart enough to take their new names, Person and Email instead
        // of Tuple3 and Tuple2.  This aids readability when debugging.
        assertEquals("PersistentVector(" +
                     "Person(\"Jane\",\"Smith\"," +
                     "PersistentVector(Email(HOME,\"a@b.c\"),Email(WORK,\"b@c.d\")))," +
                     "Person(\"Fred\",\"Tase\"," +
                     "PersistentVector(Email(HOME,\"c@d.e\"),Email(WORK,\"d@e.f\"))))",
                     people.toString());

        // This type signature couldn't be simpler (or more descriptive):
        Person jane = people.get(0);

        assertEquals("Person(\"Jane\",\"Smith\"," +
                     "PersistentVector(Email(HOME,\"a@b.c\"),Email(WORK,\"b@c.d\")))",
                     jane.toString());

        // Let's use our new, descriptive field getter methods:
        assertEquals("Jane", jane.first());
        assertEquals("Smith", jane.last());

        Email janesAddr = jane.emailAddrs().get(0);
        assertEquals(HOME, janesAddr.mailType());
        assertEquals("a@b.c", janesAddr.address());

        // Another simplified type signature.  Descriptive method names are much easier to read.
        ImMap<String,Person> peopleByEmail =
                people.flatMap(person -> person.emailAddrs()
                                               .map(mail -> tup(mail.address(), person)))
                      .toImMap(Fn1.identity());

        assertEquals("PersistentHashMap(" +
                     "Tuple2(\"d@e.f\",Person(\"Fred\",\"Tase\",PersistentVector(Email(HOME,\"c@d.e\")," +
                     "Email(WORK,\"d@e.f\"))))," +
                     "Tuple2(\"a@b.c\",Person(\"Jane\",\"Smith\",PersistentVector(Email(HOME,\"a@b.c\")," +
                     "Email(WORK,\"b@c.d\"))))," +
                     "Tuple2(\"b@c.d\",Person(\"Jane\",\"Smith\",PersistentVector(Email(HOME,\"a@b.c\")," +
                     "Email(WORK,\"b@c.d\"))))," +
                     "Tuple2(\"c@d.e\",Person(\"Fred\",\"Tase\",PersistentVector(Email(HOME,\"c@d.e\")," +
                     "Email(WORK,\"d@e.f\")))))",
                     peopleByEmail.toString());

        // Now look Jane up by her address.
        assertEquals(jane, peopleByEmail.get("b@c.d"));

        // Conclusion:
        // Extending Tuples lets us write Immutable, Object-Oriented, Functional code with less
        // boilerplate than traditional Java coding.  In addition to brevity, this approach improves
        // legibility, reliability, and consistency.
        //
        // You can begin with a quick and dirty proof of concept, then define types later to make
        // your code easy to maintain.  If you need to write out a complex type like the second
        // example, consider defining some classes with good names.
    }

    // Transormable/UnmodIterable interface example.  These provide a simpler version of something
    // like java.util.Stream, but wrapping checked exceptions and assuming immutability.
    @Test public void transformTest() {

        // These transformations do not change the underlying data.  They build a new collection by
        // chaining together the specified operations, then applying them in a single pass.  vec()
        // creates a vector (immutable list) of any length.
        ImList<Integer> v = vec(4, 5);

        // Add more numbers at the beginning.  "precat" is short for "prepend version of
        // concatenate" or "add-to-beginning".  UnmodIterable is the primary bridge between Paguro
        // and traditional Java.  It extends Iterable, but adds transformations and deprecates the
        // remove() method of the iterator it provides.
        UnmodIterable<Integer> u1 = v.precat(vec(1, 2, 3));

        // u1 now represents a bigger list of numbers
        assertEquals(vec(1, 2, 3, 4, 5), u1.toImList());

        // v is unchanged
        assertEquals(vec(4, 5), v);

        // Many people ask if these transformations are immutable.  Save off this transformation
        // at this point so we can check that it's unchanged later.
        UnmodIterable<Integer> u2 = u1;

        // Instead of updating in place, each change returns a new data structure which is an
        // extremely lightweight copy of the old because it shares as much as possible with the
        // previous structure.
        u2 = u2.concat(vec(6, 7, 8, 9));

        assertEquals(vec(1, 2, 3, 4, 5, 6, 7, 8, 9), u2.toImList());

        u2 = u2.filter(i -> i > 4);

        assertEquals(vec(5,6,7,8,9), u2.toImList());

        u2 = u2.map(i -> i - 2);

        assertEquals(vec(3,4,5,6,7), u2.toImList());

        // After a take, the subsequent items are not processed by the transformation.
        // If you had a billion items, this would only allow the first 5 to be processed.
        u2 = u2.take(4);

        assertEquals(vec(3,4,5,6), u2.toImList());

        u2 = u2.drop(2);

        assertEquals(vec(5,6), u2.toImList());

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

        // u1 is unchanged
        assertEquals(vec(1, 2, 3, 4, 5), u1.toImList());

        // Conclusion:
        // Once you get used to them, Transformations are easier to write and read than their
        // traditional Java looping counterparts (and 98% as fast).  They are also much
        // easier to understand than Java 8 streams and handle and immutable destination
        // collections well.
    }
}
