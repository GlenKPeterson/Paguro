// Copyright (c) 2014-03-06 PlanBase Inc. & Glen Peterson
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless r==uired by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.organicdesign.fp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import org.organicdesign.fp.function.Function0;

import static org.junit.Assert.*;

/**
 If these prove useful over time, they will be moved to the TestUtils project instead.
 */
public class TestUtilities {

    /**
     Tests if something is serializable.
     Note: Lambdas and anonymous classes are NOT serializable in Java 8.  Only enums and classes that implement
     Serializable are.  This might be the best reason to use enums for singletons.
     @param obj the item to serialize and deserialize
     @return whatever's left after serializing and deserializing the original item.  Sometimes things throw exceptions.
     */

    @SuppressWarnings("unchecked")
    public static <T> T serializeDeserialize(T obj) {

        // This method was started by sblommers.  Thanks for your help!
        // Mistakes are Glen's.
        // https://github.com/GlenKPeterson/Paguro/issues/10#issuecomment-242332099

        // Write
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);

            final byte[] data = baos.toByteArray();

            // Read
            ByteArrayInputStream baip = new ByteArrayInputStream(data);
            ObjectInputStream ois = new ObjectInputStream(baip);
            return (T) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     Call with two Iterators to test that they are equal
     @param control the reference iterator
     @param test the iterator under test.
     */
    public static <T> void compareIterators(Iterator<T> control, Iterator<? extends T> test) {
        int i = 0;
        while (control.hasNext()) {
            assertTrue("Control[" + i + "] had next, but test didn't", test.hasNext());
            T cNext = control.next();
            T tNext = test.next();
            assertEquals("Control[" + i + "]=" + cNext + " didn't equal test[" + i + "]=" + tNext,
                         cNext, tNext);
            i++;
        }
        assertFalse("Test[" + i + "] had extra elements", test.hasNext());
    }

    // TODO: Merge this with compareIterators above (I think the above is better).
    /**
     Call with two Iterators to test that they are equal
     @param a the reference iterator
     @param b the iterator under test.
     */
    public static <A,B> void iteratorTest(Iterator<A> a, Iterator<B> b) {
        while (a.hasNext()) {
            assertTrue("When a has a next, b should too", b.hasNext());
            assertEquals("a.next should equal b.next", a.next(), b.next());
        }
        assertFalse("When a has no next, b shouldn't either", b.hasNext());
    }

    public static <T extends Throwable> void assertEx(Function0<?> f, String beforeText,
                                                      Class<T> exType) {
        try {
            f.apply();
        } catch (Throwable t) {
            if (!exType.isInstance(t)) {
                fail("Expected " + beforeText + " to throw " + exType.getSimpleName() +
                     " but threw " + t);
            }
            return;
        }
        fail("Expected " + beforeText + " to throw " + exType.getSimpleName());
    }

    private static <A,B> void assertLiEq(ListIterator<A> a, ListIterator<B> b, String afterText) {
        assertEquals("a.hasNext should equal b.hasNext " + afterText,
                     a.hasNext(), b.hasNext());
        assertEquals("a.hasPrevious should equal b.hasPrevious " + afterText,
                     a.hasPrevious(), b.hasPrevious());
        assertEquals("a.nextIndex should equal b.nextIndex " + afterText,
                     a.nextIndex(), b.nextIndex());
        assertEquals("a.previousIndex should equal b.previousIndex " + afterText,
                     a.previousIndex(), b.previousIndex());
    }

    /**
     Call with two ListIterators to test that they are equal
     @param aList the reference iterator
     @param bList the iterator under test.
     */
    public static <A,B> void listIteratorTest(List<A> aList, List<B> bList) {

        assertEx(() -> aList.listIterator(-1), "aList.listIterator(-1)",
                 IndexOutOfBoundsException.class);
        assertEx(() -> bList.listIterator(-1), "bList.listIterator(-1)",
                 IndexOutOfBoundsException.class);

        assertEx(() -> aList.listIterator(aList.size() + 1), "aList.listIterator(aList.size() + 1)",
                 IndexOutOfBoundsException.class);
        assertEx(() -> bList.listIterator(aList.size() + 1), "bList.listIterator(aList.size() + 1)",
                 IndexOutOfBoundsException.class);

        for (int i = 0; i <= aList.size(); i++) {
            ListIterator<A> a = aList.listIterator(i);
            ListIterator<B> b = bList.listIterator(i);

            assertLiEq(a, b, "at start (i = " + i + ")");

            while (a.hasNext()) {
                assertTrue("When a has a next, b should too (started at " + i + ")", b.hasNext());

                assertEquals("a.next should equal b.next (started at " + i + ")",
                             a.next(), b.next());

                assertLiEq(a, b, "after calling next()");
            }
            assertFalse("When a has no next, b shouldn't either (started at " + i + ")",
                        b.hasNext());

            assertLiEq(a, b, "after the last item");

            assertEx(a::next, "a.next() after the last item", NoSuchElementException.class);
            assertEx(b::next, "b.next() after the last item", NoSuchElementException.class);

            while (a.hasPrevious()) {
                assertTrue("When a hasPrevious, b should too. (started at " + i + ")",
                           b.hasPrevious());

                assertEquals("a.previous should equal b.previous (started at " + i + ")",
                             a.previous(), b.previous());

                assertLiEq(a, b, "after calling previous()");
            }
            assertFalse("When a has no previous, b shouldn't either (started at " + i + ")",
                        b.hasPrevious());

            assertLiEq(a, b, "before first item");

            assertEx(a::previous, "a.previous() before first item", NoSuchElementException.class);
            assertEx(b::previous, "b.previous() before first item", NoSuchElementException.class);
        }

        // Check that indexing works when we start with the previous, then switch to the next()
        for (int i = 0; i <= aList.size(); i++) {
            ListIterator<A> a = aList.listIterator(i);
            ListIterator<B> b = bList.listIterator(i);

            assertLiEq(a, b, "at start (i = " + i + ")");

            while (a.hasPrevious()) {
                assertTrue("When a hasPrevious, b should too. (started at " + i + ")",
                           b.hasPrevious());

                assertEquals("a.previous should equal b.previous (started at " + i + ")",
                             a.previous(), b.previous());

                assertLiEq(a, b, "after calling previous()");
            }
            assertFalse("When a has no previous, b shouldn't either (started at " + i + ")",
                        b.hasPrevious());

            assertLiEq(a, b, "before first item");

            assertEx(a::previous, "a.previous()", NoSuchElementException.class);
            assertEx(b::previous, "b.previous()", NoSuchElementException.class);

            while (a.hasNext()) {
                assertTrue("When a has a next, b should too (started at " + i + ")", b.hasNext());

                assertEquals("a.next should equal b.next (started at " + i + ")",
                             a.next(), b.next());

                assertLiEq(a, b, "after calling next()");
            }
            assertFalse("When a has no next, b shouldn't either (started at " + i + ")",
                        b.hasNext());

            assertLiEq(a, b, "after the last item");

            assertEx(a::next, "a.next()", NoSuchElementException.class);
            assertEx(b::next, "b.next()", NoSuchElementException.class);
        }
    }
}
