package org.organicdesign.fp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;
import org.organicdesign.fp.collections.ImList;
import org.organicdesign.fp.collections.ImMap;
import org.organicdesign.fp.collections.RangeOfInt;
import org.organicdesign.fp.tuple.Tuple3;

import static org.junit.Assert.*;
import static org.organicdesign.fp.StaticImports.*;
import static org.organicdesign.fp.TradJavaStreamComparisonTest.ColorVal.*;

public class TradJavaStreamComparisonTest {

    // Declare a simple enum with a character mapping (eg. for storing in a database or passing
    // over a network).  We'll calculate and store the reverse-mapping three different ways,
    // Once with Paguro (3 lines), once using "Traditional" Java (6 lines plus closing braces),
    // and once using the new Java8 streams (9 lines, plus closing braces).
    public enum ColorVal {
        RED('R'),
        GREEN('G'),
        BLUE('B');
        private final Character ch;
        ColorVal(Character c) { ch = c; }
        public Character ch() { return ch; }

        // The Paguro way (3 lines of code):
        static final ImMap<Character,ColorVal> charToColorMapU =
                vec(values())
                        .toImMap(v -> tup(v.ch(), v));

        // Same thing in "traditional" Java (6 lines, plus closing braces):
        static final Map<Character,ColorVal> charToColorMapT;
        static {
            Map<Character,ColorVal> tempMap = new HashMap<>();
            for (ColorVal v : values()) {
                tempMap.put(v.ch(), v);
            }
            charToColorMapT = Collections.unmodifiableMap(tempMap);
        }

        // Same thing with Java 8's streams (3 lines thanks to @codepoetics on reddit)
        static final Map<Character,ColorVal> charToColorMap8 = Collections.unmodifiableMap(
                Stream.of(values())
                      .collect(Collectors.toMap(ColorVal::ch, Function.identity())));

        // If you were using a mutable map, you'd want to protect it with a method.
        // It's still a good idea to do that on public classes to defend against having to change
        // your public interface over time.
        public static ColorVal fromCharU(Character c) { return charToColorMapU.get(c); }
        public static ColorVal fromCharT(Character c) { return charToColorMapT.get(c); }
        public static ColorVal fromChar8(Character c) { return charToColorMap8.get(c); }
    }

    // Prove that all three reverse-mappings work.
    @Test public void testReverseMapping() {
        assertEquals(RED, ColorVal.fromCharU('R'));
        assertEquals(RED, ColorVal.fromCharT('R'));
        assertEquals(RED, ColorVal.fromChar8('R'));

        assertEquals(GREEN, ColorVal.fromCharU('G'));
        assertEquals(GREEN, ColorVal.fromCharT('G'));
        assertEquals(GREEN, ColorVal.fromChar8('G'));

        assertEquals(BLUE, ColorVal.fromCharU('B'));
        assertEquals(BLUE, ColorVal.fromCharT('B'));
        assertEquals(BLUE, ColorVal.fromChar8('B'));

        assertNull(ColorVal.fromCharU('x'));
        assertNull(ColorVal.fromCharT('x'));
        assertNull(ColorVal.fromChar8('x'));
    }

    // Prove that all three reverse-mappings cannot be changed accidentally

    // Notice that Paguro is the only one to give us a deprecation warning on this mutator
    // method because it always throws an exception.  It's not deprecated in the sense that the
    // method is ever going away.  But the warning indicates the coding error of ever trying to
    // call this method.
    @SuppressWarnings("deprecation")
    @Test(expected = UnsupportedOperationException.class)
    public void revMapExJ() { charToColorMapU.put('Z', RED); }

    // Traditional Java doesn't indicate that the put method always throws an exception.
    @Test(expected = UnsupportedOperationException.class)
    public void revMapExT() { charToColorMapT.put('Z', RED); }

    // Java8 doesn't indicate that the put method always throws an exception.
    @Test(expected = UnsupportedOperationException.class)
    public void revMapEx8() { charToColorMap8.put('Z', RED); }

    // Maybe your code has to inter-operate with a legacy system that instead of letters, uses
    // numbers to indicate the three primary colors:
    @Test public void testExtension() {

        // Paguro has methods that return new lightweight copies of the original map,
        // with the new value added.  This makes it a snap to extend existing immutable data
        // structures: 3 loc.
        final ImMap<Character,ColorVal> extendedMapU = charToColorMapU.assoc('1', RED)
                                                                      .assoc('2', GREEN)
                                                                      .assoc('3', BLUE);

        // To make the construction private, we declare a block in traditional Java.  7 loc.
        final Map<Character,ColorVal> extendedMapT;
        {
            Map<Character,ColorVal> tempMap = new HashMap<>();
            tempMap.putAll(charToColorMapT);
            tempMap.put('1', RED);
            tempMap.put('2', GREEN);
            tempMap.put('3', BLUE);
            extendedMapT = Collections.unmodifiableMap(tempMap);
        }

        // In Java8 we can use a lambda to make construction private.  8 loc, including a cast.
        // I had to look up the name of this functional class in the API docs because I thought it
        // was Producer.
        final Map<Character,ColorVal> extendedMap8 = ((Supplier<Map<Character,ColorVal>>) () -> {
            Map<Character,ColorVal> tempMap = new HashMap<>();
            tempMap.putAll(charToColorMap8);
            tempMap.put('1', RED);
            tempMap.put('2', GREEN);
            tempMap.put('3', BLUE);
            return Collections.unmodifiableMap(tempMap);
        }).get();

        // All three methods produce the same correct results.  The question is which code would
        // you rather write to achieve these results?
        assertEquals(RED, extendedMapU.get('R'));
        assertEquals(RED, extendedMapU.get('1'));
        assertEquals(RED, extendedMapT.get('R'));
        assertEquals(RED, extendedMapT.get('1'));
        assertEquals(RED, extendedMap8.get('R'));
        assertEquals(RED, extendedMap8.get('1'));

        assertEquals(GREEN, extendedMapU.get('G'));
        assertEquals(GREEN, extendedMapU.get('2'));
        assertEquals(GREEN, extendedMapT.get('G'));
        assertEquals(GREEN, extendedMapT.get('2'));
        assertEquals(GREEN, extendedMap8.get('G'));
        assertEquals(GREEN, extendedMap8.get('2'));

        assertEquals(BLUE, extendedMapU.get('B'));
        assertEquals(BLUE, extendedMapU.get('3'));
        assertEquals(BLUE, extendedMapT.get('B'));
        assertEquals(BLUE, extendedMapT.get('3'));
        assertEquals(BLUE, extendedMap8.get('B'));
        assertEquals(BLUE, extendedMap8.get('3'));

        assertNull(extendedMapU.get('x'));
        assertNull(extendedMapU.get('4'));
        assertNull(extendedMapT.get('x'));
        assertNull(extendedMapT.get('4'));
        assertNull(extendedMap8.get('x'));
        assertNull(extendedMap8.get('4'));
    }

    // ====================================== Second Example =====================================

    // java.awt.Color is outside of profile compact1, so I'm defining a simpler, but similar class
    static class Color extends Tuple3<Integer,Integer,Integer> {
        Color(int r, int g, int b) { super(r, g, b); }

        // Yes, throwing an Exception is contrived here.  Exceptions happen in real Java code, but
        // usually for more complex reasons.
        public int green() throws Exception { return _2; }
    }

    // The Paguro way: 3 loc, + 3 loc = 6 loc
    @Test
    public void colorSquareU() {
        ImList<Color> imgData = RangeOfInt.of(0, 256)
                .flatMap(i -> RangeOfInt.of(0, 256).map(j -> new Color(i, (i + j) / 2, 255)))
                .toImList();

        assertTrue(imgData.toString()
                          .startsWith("PersistentVector(" +
                                      "Color(0,0,255)," +
                                      "Color(0,0,255)," +
                                      "Color(0,1,255)," +
                                      "Color(0,1,255)," +
                                      "Color(0,2,255),"));

        ImMap<Integer,Integer> greenCounts = imgData
                .foldLeft(map(),
                          (accum, color) -> accum.assoc(color.green(),
                                                        accum.getOrElse(color.green(), 0) + 1));

        assertEquals(256, greenCounts.size());
        assertEquals(Integer.valueOf(3), greenCounts.get(0));
        assertEquals(Integer.valueOf(7), greenCounts.get(1));
        assertEquals(Integer.valueOf(11), greenCounts.get(2));
        assertEquals(Integer.valueOf(15), greenCounts.get(3));
    }

    // Same thing in "Traditional" Java: 4 loc + 2 brackets, 8 loc + 4 brackets =
    // 12 loc + 6 brackets
    // Note that colorSquareT() throws an Exception (because Color.green() throws it).
    @Test public void colorSquareT() throws Exception {
        java.util.List<Color> imgData = new ArrayList<>();
        for (int i = 0; i < 256; i++) {
            for (int j = 0; j < 256; j++) {
                imgData.add(new Color(i, (i + j) / 2, 255));
            }
        }

        assertTrue(imgData.toString()
                          .startsWith("[" +
                                      "Color(0,0,255), " +
                                      "Color(0,0,255), " +
                                      "Color(0,1,255), " +
                                      "Color(0,1,255), " +
                                      "Color(0,2,255), "));

        Map<Integer,Integer> greenCounts = new HashMap<>();
        for (Color color : imgData) {
            greenCounts.put(color.green(), greenCounts.getOrDefault(color.green(), 0) + 1);
        }

        assertEquals(256, greenCounts.size());
        assertEquals(Integer.valueOf(3), greenCounts.get(0));
        assertEquals(Integer.valueOf(7), greenCounts.get(1));
        assertEquals(Integer.valueOf(11), greenCounts.get(2));
        assertEquals(Integer.valueOf(15), greenCounts.get(3));
    }

    // Same thing with Java 8's Streams - I just used traditional Java to populate the list.
    // (4 loc + 2 brackets) + (8 loc + 1 bracket) = 12 loc + 3 brackets
    @Test public void colorSquare8() {
        java.util.List<Color> imgData = new ArrayList<>();
        for (int i = 0; i < 256; i++) {
            for (int j = 0; j < 256; j++) {
                imgData.add(new Color(i, (i + j) / 2, 255));
            }
        }
        assertTrue(imgData.toString()
                          .startsWith("[" +
                                      "Color(0,0,255), " +
                                      "Color(0,0,255), " +
                                      "Color(0,1,255), " +
                                      "Color(0,1,255), " +
                                      "Color(0,2,255), "));

        // If this didn't throw an exception, we could use (thanks to @codepoetics on reddit)
//        Map<Integer,Long> greenCounts = Collections.unmodifiableMap(
//                imgData.stream()
//                       .collect(Collectors.groupingBy(color -> color.green(),
//                                                      Collectors.counting())));

        // But green does throw an exception.  There are various ways of handling this, but I think
        // this is probably typical, and basically identical to what Paguro does for you.
        Map<Integer,Long> greenCounts = Collections.unmodifiableMap(
                imgData.stream()
                       .collect(Collectors.groupingBy(color -> {
                           try {
                               return color.green();
                           } catch (Exception e) {
                               throw new RuntimeException(e);
                           }
                       }, Collectors.counting())));

        // Conclusion:
        // Wrapping exceptions in lambdas is not the end of the world, but it's unnecessarily
        // distracting.  Also, why learn a whole Collectors library when you could fold into
        // a map and get exception wrapping for free?  Finally, what's returned here is just
        // another Collections.unmodifiableMap - you have to make a deep copy in order to change it.
        // If you really want to write code this way, at least use Paguro's
        // FunctionUtils.unmodMap() instead so that your IDE and compiler can warn you if you try to
        // call a deprecated method.

        assertEquals(256, greenCounts.size());
        assertEquals(Long.valueOf(3), greenCounts.get(0));
        assertEquals(Long.valueOf(7), greenCounts.get(1));
        assertEquals(Long.valueOf(11), greenCounts.get(2));
        assertEquals(Long.valueOf(15), greenCounts.get(3));
    }
}
