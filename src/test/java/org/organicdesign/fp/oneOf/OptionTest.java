package org.organicdesign.fp.oneOf;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.organicdesign.fp.TestUtilities.serializeDeserialize;
import static org.organicdesign.testUtils.EqualsContract.equalsDistinctHashCode;

public class OptionTest {

    @Test
    public void basics() {
        Option<Integer> o1a = Option.some(1);
        assertTrue(o1a.isSome());
        assertEquals(Integer.valueOf(1), o1a.get());
        assertEquals(Integer.valueOf(1), o1a.getOrElse(2));
        assertEquals("One", o1a.match(s -> "One",
                                      () -> "Two"));

        Option<Integer> z = Option.some(null);
        assertTrue(z.isSome());
        assertNull(z.get());
        assertNull(z.getOrElse(2));
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
        assertEquals(None.NONE, Option.someOrNullNoneOf(None.NONE));

        assertEquals(None.NONE, None.NONE);
        assertEquals(None.NONE, Option.someOrNullNoneOf(null));
    }

    @Test
    public void testExceptions() {
        IllegalStateException iSE;
        iSE = assertThrowsExactly(IllegalStateException.class,
                                  () -> Option.none().get());
        assertEquals("Called get on None", iSE.getMessage());

        iSE = assertThrowsExactly(IllegalStateException.class,
                                  () -> Option.someOrNullNoneOf(null).get());

        assertEquals("Called get on None", iSE.getMessage());
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
        Option<Integer> o1a = Option.some(1);
        @SuppressWarnings("UnnecessaryBoxing") Option<Integer> o1b = Option.some(Integer.valueOf(2 - 1));
        Option<Integer> o1c = Option.some(Integer.valueOf("1"));

        equalsDistinctHashCode(o1a, o1b, o1c, Option.some(0));

        equalsDistinctHashCode(o1a, serializeDeserialize(o1b), o1c, Option.some(0));

        equalsDistinctHashCode(o1a, o1b, o1c, Option.some(2));

        equalsDistinctHashCode(o1a, o1b, o1c, Option.some(null));

        equalsDistinctHashCode(o1a, o1b, o1c, Option.none());

        Option<Integer> weird = Option.some(1);

        equalsDistinctHashCode(o1a, o1b, weird, Option.none());

        equalsDistinctHashCode(Option.some(null), Option.some(null), Option.some(null), o1a);
        equalsDistinctHashCode(Option.some(null), serializeDeserialize(Option.some(null)),
                               Option.some(null), o1a);

        Option<Integer> z = Option.some(null);
        Option<Integer> n1 = Option.none();
        Option<Integer> n2 = Option.someOrNullNoneOf(null);
        Option<Integer> n3 = Option.none();

        assertSame(n1, n2);
        assertSame(n2, n3);

        assertNotEquals(n1, z);
        assertNotEquals(z, n1);
        assertNotEquals(n2, z);
        assertNotEquals(z, n2);
        assertNotEquals(n3, z);
        assertNotEquals(z, n3);

        assertNotEquals(z.hashCode(), n1.hashCode());
        assertNotEquals(z.hashCode(), n2.hashCode());
        assertNotEquals(z.hashCode(), n3.hashCode());

        assertEquals(n1.hashCode(), n2.hashCode());
        assertEquals(n2.hashCode(), n3.hashCode());

        assertEquals(n1, n2);
        assertEquals(n2, n1);

        equalsDistinctHashCode(o1a, o1b, o1c, n1);

        equalsDistinctHashCode(Option.some(null), Option.some(null), Option.some(null), n2);

        // None is a serializable singleton.
        assertSame(n1, serializeDeserialize(n1));
        assertEquals(n1, serializeDeserialize(n1));
    }

    @Test public void thenTest() {
        Option<Integer> o1 = Option.some(1);
        assertEquals(Option.none(), o1.then(i -> i > 3 ? Option.some("good")
                                                       : Option.none()));
        assertEquals(Option.some("good"), o1.then(i -> i < 3 ? Option.some("good")
                                                             : Option.none()));

        Option<Integer> o2 = Option.none();
        assertEquals(Option.none(), o2.then(i -> i < 3 ? Option.some("good")
                                                       : Option.none()));
    }

    @Test public void testToString() {
        assertEquals("Some(3)", Option.some(3).toString());
        assertEquals("Some(\"hi\")", Option.some("hi").toString());
        assertEquals("Some(7.5)", Option.some(7.5).toString());
    }
}