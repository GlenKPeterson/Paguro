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
        // https://github.com/GlenKPeterson/UncleJim/issues/10#issuecomment-242332099

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
}
