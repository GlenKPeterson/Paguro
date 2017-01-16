package org.organicdesign.fp.collections;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.organicdesign.fp.collections.ComparisonContext.defCompCtx;
import static org.organicdesign.fp.collections.Equator.defaultComparator;
import static org.organicdesign.fp.collections.Equator.defaultEquator;

public class EquatorTest {
    @Test public void defaultEquatorTest() {
        assertEquals(0, defaultEquator().hash(null));
        assertEquals("".hashCode(), defaultEquator().hash(""));
        assertEquals("Telephone Pole".hashCode(), defaultEquator().hash("Telephone Pole"));

        assertEquals(Integer.valueOf(Integer.MIN_VALUE).hashCode(),
                     defaultEquator().hash(Integer.MIN_VALUE));
        assertEquals(Integer.valueOf(Integer.MIN_VALUE + 1).hashCode(),
                     defaultEquator().hash(Integer.MIN_VALUE + 1));
        assertEquals(Integer.valueOf(-1).hashCode(),
                     defaultEquator().hash(-1));
        assertEquals(Integer.valueOf(0).hashCode(),
                     defaultEquator().hash(0));
        assertEquals(Integer.valueOf(1).hashCode(),
                     defaultEquator().hash(1));
        assertEquals(Integer.valueOf(Integer.MAX_VALUE - 1).hashCode(),
                     defaultEquator().hash(Integer.MAX_VALUE - 1));
        assertEquals(Integer.valueOf(Integer.MAX_VALUE).hashCode(),
                     defaultEquator().hash(Integer.MAX_VALUE));

        assertTrue(defaultEquator().eq(null, null));

        Object a = new Object();
        assertFalse(defaultEquator().eq(null, a));
        assertFalse(defaultEquator().eq(a, null));
        assertFalse(defaultEquator().eq(null, "Hello"));
        assertFalse(defaultEquator().eq("Cumquat", null));

        assertTrue(defaultEquator().eq(a, a));
        assertTrue(defaultEquator().eq("Hello", "Hello"));
        assertTrue(defaultEquator().eq(Integer.MIN_VALUE, Integer.MIN_VALUE));
        assertTrue(defaultEquator().eq(Integer.MIN_VALUE + 3, Integer.MIN_VALUE + 3));

        // Same thing reversed for neq.
        assertTrue(defaultEquator().neq(null, a));
        assertTrue(defaultEquator().neq(a, null));
        assertTrue(defaultEquator().neq(null, "Hello"));
        assertTrue(defaultEquator().neq("Cumquat", null));

        assertFalse(defaultEquator().neq(a, a));
        assertFalse(defaultEquator().neq("Hello", "Hello"));
        assertFalse(defaultEquator().neq(Integer.MIN_VALUE, Integer.MIN_VALUE));
        assertFalse(defaultEquator().neq(Integer.MIN_VALUE + 3, Integer.MIN_VALUE + 3));
    }

    class ComparableToNull<T extends Comparable<T>> implements Comparable<T> {
        private final T item;
        ComparableToNull(T t) { item = t; }
        @Override public int compareTo(T o) {
            return o == null ? Integer.MAX_VALUE : item.compareTo(o);
        }
    }

    @SuppressWarnings({"StringBufferReplaceableByString", "RedundantStringConstructorCall"})
    @Test public void defaultComparatorTest() {
        assertEquals(0, defaultComparator().compare("Hello", "Hello"));
        assertEquals(0, defaultComparator().compare(new StringBuilder("Hell").append("o").toString(),
                                                    new String("Hello")));
        assertEquals("A".compareTo("B"), defaultComparator().compare("A", "B"));
        assertEquals("B".compareTo("A"), defaultComparator().compare("B", "A"));

        assertEquals(Integer.valueOf(3).compareTo(4), defaultComparator().compare(3, 4));
        assertEquals(Integer.valueOf(4).compareTo(3), defaultComparator().compare(4, 3));

        assertEquals(new ComparableToNull<>(3).compareTo(null),
                     defaultComparator().compare(new ComparableToNull<>(3), null));
        assertEquals(- new ComparableToNull<>(3).compareTo(null),
                     defaultComparator().compare(null, new ComparableToNull<>(3)));
    }

    @SuppressWarnings({"StringBufferReplaceableByString", "RedundantStringConstructorCall"})
    @Test public void testCompCtx() {
        // This is copied from the above two tests.
        assertEquals(0, defCompCtx().hash(null));
        assertEquals("".hashCode(), defCompCtx().hash(""));
        assertEquals("Telephone Pole".hashCode(), defCompCtx().hash("Telephone Pole"));

        assertEquals(Integer.valueOf(Integer.MIN_VALUE).hashCode(),
                     defCompCtx().hash(Integer.MIN_VALUE));
        assertEquals(Integer.valueOf(Integer.MIN_VALUE + 1).hashCode(),
                     defCompCtx().hash(Integer.MIN_VALUE + 1));
        assertEquals(Integer.valueOf(-1).hashCode(),
                     defCompCtx().hash(-1));
        assertEquals(Integer.valueOf(0).hashCode(),
                     defCompCtx().hash(0));
        assertEquals(Integer.valueOf(1).hashCode(),
                     defCompCtx().hash(1));
        assertEquals(Integer.valueOf(Integer.MAX_VALUE - 1).hashCode(),
                     defCompCtx().hash(Integer.MAX_VALUE - 1));
        assertEquals(Integer.valueOf(Integer.MAX_VALUE).hashCode(),
                     defCompCtx().hash(Integer.MAX_VALUE));

        assertTrue(defCompCtx().eq(null, null));

        ComparableToNull<Integer> a = new ComparableToNull<>(3);
        assertFalse(defCompCtx().eq(null, a));
        assertFalse(defCompCtx().eq(a, null));
        assertFalse(defCompCtx().eq(null, new ComparableToNull<>("Hello")));
        assertFalse(defCompCtx().eq(new ComparableToNull<>("Cumquat"), null));

        assertTrue(defCompCtx().eq(a, a));
        assertTrue(defCompCtx().eq("Hello", "Hello"));
        assertTrue(defCompCtx().eq(Integer.MIN_VALUE, Integer.MIN_VALUE));
        assertTrue(defCompCtx().eq(Integer.MIN_VALUE + 3, Integer.MIN_VALUE + 3));


        assertEquals(0, defCompCtx().compare("Hello", "Hello"));
        assertEquals(0, defCompCtx().compare(new StringBuilder("Hell").append("o").toString(),
                                                    new String("Hello")));
        assertEquals("A".compareTo("B"), defCompCtx().compare("A", "B"));
        assertEquals("B".compareTo("A"), defCompCtx().compare("B", "A"));

        assertEquals(Integer.valueOf(3).compareTo(4), defCompCtx().compare(3, 4));
        assertEquals(Integer.valueOf(4).compareTo(3), defCompCtx().compare(4, 3));

        assertEquals(new ComparableToNull<>(3).compareTo(null),
                     defCompCtx().compare(new ComparableToNull<>(3), null));
        assertEquals(- new ComparableToNull<>(3).compareTo(null),
                     defCompCtx().compare(null, new ComparableToNull<>(3)));

        assertTrue(defCompCtx().lt(2, 3));
        assertFalse(defCompCtx().lt(3, 3));
        assertFalse(defCompCtx().lt(4, 3));

        assertTrue(defCompCtx().lte(2, 3));
        assertTrue(defCompCtx().lte(3, 3));
        assertFalse(defCompCtx().lte(4, 3));

        assertTrue(defCompCtx().gt(3, 2));
        assertFalse(defCompCtx().gt(3, 3));
        assertFalse(defCompCtx().gt(3, 4));

        assertTrue(defCompCtx().gte(3, 2));
        assertTrue(defCompCtx().gte(3, 3));
        assertFalse(defCompCtx().gte(3, 4));

    }
}