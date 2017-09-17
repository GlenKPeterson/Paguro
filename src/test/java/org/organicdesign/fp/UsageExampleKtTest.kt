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

package org.organicdesign.fp

import org.junit.Assert.assertEquals
import org.junit.Test
import org.organicdesign.fp.StaticImports.tup
import org.organicdesign.fp.StaticImports.vec
import org.organicdesign.fp.UsageExampleKtTest.EmailType.HOME
import org.organicdesign.fp.UsageExampleKtTest.EmailType.WORK
import org.organicdesign.fp.collections.ImList
import org.organicdesign.fp.function.Fn1

// Usage examples are kept in this unit test to ensure they remain correct and current.
class UsageExampleKtTest {

    // Fluent interfaces (using methods that evaluate to something useful) take maximum advantage
    // of Kotlin's type inferencing.  This is still type-checked by the compiler, but without
    // specifying ANY types explicitly
    // Part 1 of 2
    @Test
    fun dataDefinitionPart1() {
        // In this example, we're going to define some people with email addresses, then look up
        // Jane by her email address. The first line is a JUnit test to verify that "Jane" is the
        // name of the person we find with the subsequent code.
        assertEquals("Jane",
                // Define some people with lists of email addresses on the fly.
                // vec() makes a Vector/List, tup() makes a Tuple
                     vec(tup("Jane", "Smith", vec("a@b.c", "b@c.d")),
                         tup("Fred", "Tase", vec("c@d.e", "d@e.f", "e@f.g")))

                             // We want to look up people by their address.
                             // There are multiple addresses per person.
                             // For each person, find their email addresses.
                             .flatMap { person ->
                                 person._3()

                                         // For each address, produce a key/value pair
                                         // of email and person (Tuple2 implements Map.Entry)
                                         .map { email -> tup(email, person) }
                             }

                             // toImMap() collects the results to key/value pairs and puts
                             // them in an immutable map.  We already have pairs, so pass
                             // them through unchanged.
                             .toImMap { x -> x }["b@c.d"]!!

                             // Get her first name (returns "Jane")
                             ._1())

        // Conclusion:
        // Traditional Kotlin forces you to define all your data types (as classes) before writing any
        // "logic" (functions).  While this discipline can help huge, well defined projects, a lot
        // of coding involves experimentation.  Code like the above skips the pre-work of defining
        // types so you can focus on building the right functions.
        //
        // No amount of processing power can turn O(n^2) to O(n) or to O(log n), but choosing the
        // right collections can!  Using functional transformations focuses you on that (and on
        // algorithms) instead of distracting you with looping details:
        // if/break/continue/return/i++, etc.
    }

    // Part 2 of 2
    // Define some field name constants with a standard Kotlin Enum for subsequent examples
    internal enum class EmailType {
        HOME, WORK
    }

    // Tuples are intended to give you a quick start.  For maximum readability/maintainability,
    // convert most of your tuples to data classes.  We could have done the previous examples with
    // kotlin's Pair and Triple and these would have extended those nicely, but this works too.
    internal data class Email(val mailType: EmailType,
                              val address: String)

    // Notice in this type signature, we have replaced Tuple2<EmailType,String> with Email
    internal data class Person(val first: String,
                               val last: String,
                               val emailAddrs: ImList<Email>)

    // Part 3 of 3 (continued)
    // Use the classes we made above to simplify the types and improve the toString implementations.
    @Test
    fun dataDefinitionExample3() {

        // Compare this type signature with the previous example.  Wow!
        val people = vec(Person("Jane", "Smith", vec(Email(HOME, "a@b.c"),
                                                     Email(WORK, "b@c.d"))),
                         Person("Fred", "Tase", vec(Email(HOME, "c@d.e"),
                                                    Email(WORK, "d@e.f"))))

        // Notice that the tuples are smart enough to take their new names, Person and Email instead
        // of Tuple3 and Tuple2.  This aids readability when debugging.
        assertEquals("PersistentVector(" +
                     "Person(first=Jane, last=Smith," +
                     " emailAddrs=PersistentVector(" +
                     "Email(mailType=HOME, address=a@b.c)," +
                     "Email(mailType=WORK, address=b@c.d)))," +
                     "Person(first=Fred, last=Tase," +
                     " emailAddrs=PersistentVector(" +
                     "Email(mailType=HOME, address=c@d.e)," +
                     "Email(mailType=WORK, address=d@e.f))))",
                     people.toString())

        // This type signature couldn't be simpler (or more descriptive):
        val jane = people[0]

        assertEquals("Person(first=Jane, last=Smith," +
                     " emailAddrs=PersistentVector(" +
                     "Email(mailType=HOME, address=a@b.c)," +
                     "Email(mailType=WORK, address=b@c.d)))",
                     jane.toString())

        // Let's use our new, descriptive field getter methods:
        assertEquals("Jane", jane.first)
        assertEquals("Smith", jane.last)

        val janesAddr = jane.emailAddrs[0]
        assertEquals(HOME, janesAddr.mailType)
        assertEquals("a@b.c", janesAddr.address)

        // Another simplified type signature.  Descriptive method names are much easier to read.
        val peopleByEmail = people.flatMap { person ->
            person.emailAddrs
                    .map { mail -> tup(mail.address, person) }
        }
                .toImMap<String, Person>(Fn1.identity())

        assertEquals("PersistentHashMap(" +
                     "Tuple2(\"d@e.f\"," +
                     "Person(first=Fred, last=Tase," +
                     " emailAddrs=PersistentVector(" +
                     "Email(mailType=HOME, address=c@d.e)," +
                     "Email(mailType=WORK, address=d@e.f))))," +
                     "Tuple2(\"a@b.c\"," +
                     "Person(first=Jane, last=Smith," +
                     " emailAddrs=PersistentVector(" +
                     "Email(mailType=HOME, address=a@b.c)," +
                     "Email(mailType=WORK, address=b@c.d))))," +
                     "Tuple2(\"b@c.d\"," +
                     "Person(first=Jane, last=Smith," +
                     " emailAddrs=PersistentVector(" +
                     "Email(mailType=HOME, address=a@b.c)," +
                     "Email(mailType=WORK, address=b@c.d))))," +
                     "Tuple2(\"c@d.e\"," +
                     "Person(first=Fred, last=Tase," +
                     " emailAddrs=PersistentVector(" +
                     "Email(mailType=HOME, address=c@d.e)," +
                     "Email(mailType=WORK, address=d@e.f)))))",
                     peopleByEmail.toString())

        // Now look Jane up by her address.
        assertEquals(jane, peopleByEmail["b@c.d"])

        // Conclusion:
        // Kotlin data classes make this example much easier to read
        //
        // You can begin with a quick and dirty proof of concept, then define types later to make
        // your code easy to maintain.
    }

    // Transormable/UnmodIterable interface example.  These provide a simpler version of something
    // like java.util.Stream, but wrapping checked exceptions and assuming immutability.
    @Test
    fun transformTest() {

        // These transformations do not change the underlying data.  They build a new collection by
        // chaining together the specified operations, then applying them in a single pass.  vec()
        // creates a vector (immutable list) of any length.
        val v = vec(4, 5)

        // Add more numbers at the beginning.  "precat" is short for "prepend version of
        // concatenate" or "add-to-beginning".  UnmodIterable is the primary bridge between Paguro
        // and traditional Java/Kotlin.  It extends Iterable, but adds transformations and deprecates the
        // remove() method of the iterator it provides.
        val u1 = v.precat(vec(1, 2, 3))

        // u1 now represents a bigger list of numbers
        assertEquals(vec(1, 2, 3, 4, 5), u1.toImList())

        // v is unchanged
        assertEquals(vec(4, 5), v)

        // Many people ask if these transformations are immutable.  Save off this transformation
        // at this point so we can check that it's unchanged later.
        var u2 = u1

        // Instead of updating in place, each change returns a new data structure which is an
        // extremely lightweight copy of the old because it shares as much as possible with the
        // previous structure.
        u2 = u2.concat(vec(6, 7, 8, 9))

        assertEquals(vec(1, 2, 3, 4, 5, 6, 7, 8, 9), u2.toImList())

        u2 = u2.filter { i -> i > 4 }

        assertEquals(vec(5, 6, 7, 8, 9), u2.toImList())

        u2 = u2.map { i -> i!! - 2 }

        assertEquals(vec(3, 4, 5, 6, 7), u2.toImList())

        // After a take, the subsequent items are not processed by the transformation.
        // If you had a billion items, this would only allow the first 5 to be processed.
        u2 = u2.take(4)

        assertEquals(vec(3, 4, 5, 6), u2.toImList())

        u2 = u2.drop(2)

        assertEquals(vec(5, 6), u2.toImList())

        // Let's see that again with the methods all chained together
        assertEquals(vec(5, 6),
                     vec(4, 5)                        //          4, 5
                             .precat(vec(1, 2, 3))    // 1, 2, 3, 4, 5
                             .concat(vec(6, 7, 8, 9)) // 1, 2, 3, 4, 5, 6, 7, 8, 9
                             .filter { i -> i > 4 }      //             5, 6, 7, 8, 9
                             .map { i -> i!! - 2 }         //       3, 4, 5, 6, 7
                             .take(4)                 //       3, 4, 5, 6
                             .drop(2)                 //             5, 6
                             .toImList())

        // u1 is unchanged
        assertEquals(vec(1, 2, 3, 4, 5), u1.toImList())

        // Conclusion:
        // Once you get used to them, Transformations are easier to write and read than their
        // traditional looping counterparts and immutable destination collections well.
    }
}
