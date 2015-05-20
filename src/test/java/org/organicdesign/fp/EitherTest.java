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

import static org.junit.Assert.*;

public class EitherTest {
    @Test public void left() {
        Either<String,Integer> e = Either.left("Hello");
        assertTrue(e.isLeft());
        assertFalse(e.isRight());
        assertEquals("Hello", e.left());

        assertTrue(Either.patMatch(e,
                                   (l) -> "Hello".equals(l),
                                   (r) -> Boolean.FALSE));
    }

    @Test public void right() {
        Either<Integer,String> e = Either.right("Hello");
        assertFalse(e.isLeft());
        assertTrue(e.isRight());
        assertEquals("Hello", e.right());

        assertTrue(Either.patMatch(e,
                                   (l) -> Boolean.FALSE,
                                   (r) -> "Hello".equals(r)));
    }

    @Test (expected = UnsupportedOperationException.class)
    public void leftEx() {
        Either.left("Hello").right();
    }

    @Test (expected = UnsupportedOperationException.class)
    public void rightEx() {
        Either.right("Hello").left();
    }

    @Test (expected = IllegalArgumentException.class)
    public void patMatchNullEx() {
        Either.patMatch(null,
                        (l) -> Boolean.FALSE,
                        (r) -> "Hello".equals(r));
    }

    @Test public void equalsHashCodeToStr() {
        Either<String,Integer> l = Either.left("Hello");
        assertEquals(l.hashCode(), Either.left("Hello").hashCode());
        assertTrue(l.equals(Either.left("Hello")));

        Either<Integer,String> r = Either.right("Hello");
        assertEquals(r.hashCode(), Either.right("Hello").hashCode());
        assertTrue(r.equals(Either.right("Hello")));

        assertNotEquals(l.hashCode(), r.hashCode());
        assertNotEquals(l, r);

        assertEquals("Left(Hello)", l.toString());
        assertEquals("Right(Hello)", r.toString());
    }
}
