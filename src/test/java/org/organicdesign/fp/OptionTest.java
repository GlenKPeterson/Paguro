package org.organicdesign.fp;

import org.junit.Test;
import org.organicdesign.fp.oneOf.Option;

import static org.junit.Assert.*;
import static org.organicdesign.fp.TestUtilities.serializeDeserialize;
import static org.organicdesign.testUtils.EqualsContract.equalsDistinctHashCode;

public class OptionTest {

    @Test public void basics() {
        Option<Integer> o1a = Option.of(1);
        assertTrue(o1a.isSome());
        assertEquals(Integer.valueOf(1), o1a.get());
        assertEquals(Integer.valueOf(1), o1a.getOrElse(2));
        assertEquals("One", o1a.match(s -> "One",
                                      () -> "Two"));

        Option<Integer> z = Option.of(null);
        assertTrue(z.isSome());
        assertEquals(null, z.get());
        assertEquals(null, z.getOrElse(2));
        assertEquals("One", z.match(s -> "One",
                                    () -> "Two"));

        Option<Integer> n = Option.none();
        assertFalse(n.isSome());
        assertEquals(Integer.valueOf(2), n.getOrElse(2));
        assertEquals("Two", n.match(s -> "One",
                                    () -> "Two"));

        Option<Integer> y = Option.someOrNullNoneOf(null);
        assertFalse(y.isSome());
        assertEquals(Integer.valueOf(2), y.getOrElse(2));
        assertEquals("Two", y.match(s -> "One",
                                    () -> "Two"));

        Option<String> os = Option.someOrNullNoneOf("Hello");
        assertTrue(os.isSome());
        assertEquals("Hello", os.get());
        assertEquals("Hello", os.getOrElse("Goodbye"));
        assertEquals(Integer.valueOf(1), o1a.match(s -> 1,
                                                   () -> 2));
        assertEquals(Option.NONE, Option.someOrNullNoneOf(Option.NONE));

        assertTrue(Option.NONE.equals(Option.NONE));
        assertTrue(Option.NONE.equals(Option.someOrNullNoneOf(null)));
    }

    @Test(expected = IllegalStateException.class) public void noneEx() {
        Option.none().get();
    }

    @Test(expected = IllegalStateException.class) public void nullNoneEx() {
        Option.someOrNullNoneOf(null).get();
    }
//
//    @Test public void iterationTest() {
//        Iterator<Integer> i1 = Option.of(1).iterator();
//        assertTrue(i1.hasNext());
//        assertEquals(Integer.valueOf(1), i1.next());
//        assertFalse(i1.hasNext());
//
//        Iterator<Integer> i2 = Option.<Integer>none().iterator();
//        assertFalse(i2.hasNext());
//    }
//
//    @Test(expected = NoSuchElementException.class)
//    public void iterEx1() {
//        Iterator<Integer> i1 = Option.of(1).iterator();
//        i1.next();
//        i1.next();
//    }
//
//    @Test(expected = NoSuchElementException.class)
//    public void iterEx2() {
//        Option.none().iterator().next();
//    }

    @Test public void equalsHash() {
        Option<Integer> o1a = Option.of(1);
        Option<Integer> o1b = Option.of(new Integer(2 - 1));
        Option<Integer> o1c = Option.of(Integer.valueOf("1"));

        equalsDistinctHashCode(o1a, o1b, o1c, Option.of(0));

        equalsDistinctHashCode(o1a, serializeDeserialize(o1b), o1c, Option.of(0));

        equalsDistinctHashCode(o1a, o1b, o1c, Option.of(2));

        equalsDistinctHashCode(o1a, o1b, o1c, Option.of(null));

        equalsDistinctHashCode(o1a, o1b, o1c, Option.none());

        equalsDistinctHashCode(Option.of(null), Option.of(null), Option.of(null), o1a);
        equalsDistinctHashCode(Option.of(null), serializeDeserialize(Option.of(null)),
                               Option.of(null), o1a);

        Option<Integer> z = Option.of(null);
        Option<Integer> n1 = Option.none();
        Option<Integer> n2 = Option.someOrNullNoneOf(null);
        Option n3 = Option.of(Option.NONE);

        assertTrue(n1 == n2);
        assertTrue(n2 == n3);

        assertFalse(n1.equals(z));
        assertFalse(z.equals(n1));
        assertFalse(n2.equals(z));
        assertFalse(z.equals(n2));
        assertFalse(n3.equals(z));
        assertFalse(z.equals(n3));

        assertNotEquals(z.hashCode(), n1.hashCode());
        assertNotEquals(z.hashCode(), n2.hashCode());
        assertNotEquals(z.hashCode(), n3.hashCode());

        assertEquals(n1.hashCode(), n2.hashCode());
        assertEquals(n2.hashCode(), n3.hashCode());

        assertTrue(n1.equals(n2));
        assertTrue(n2.equals(n1));

        equalsDistinctHashCode(o1a, o1b, o1c, n1);

        equalsDistinctHashCode(Option.of(null), Option.of(null), Option.of(null), n2);

        // None is a serializable singleton.
        assertTrue(n1 == serializeDeserialize(n1));
        assertEquals(n1, serializeDeserialize(n1));
    }

    @Test public void thenTest() {
        Option<Integer> o1 = Option.of(1);
        assertEquals(Option.none(), o1.then(i -> i > 3 ? Option.of("good")
                                                       : Option.none()));
        assertEquals(Option.of("good"), o1.then(i -> i < 3 ? Option.of("good")
                                                           : Option.none()));

        Option<Integer> o2 = Option.none();
        assertEquals(Option.none(), o2.then(i -> i < 3 ? Option.of("good")
                                                       : Option.none()));
    }
}