package org.organicdesign.fp.oneOf;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.organicdesign.testUtils.EqualsContract;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;

public class OneOf5Test {
    static class Str_Int_Float_Dub_Char extends OneOf5<String,Integer,Float,Double,Character> {

        // Constructor
        private Str_Int_Float_Dub_Char(@NotNull Object o, int sel) {
            super(o, String.class, Integer.class, Float.class, Double.class, Character.class, sel);
        }

        // Static factory methods
        static Str_Int_Float_Dub_Char of(@NotNull String o) { return new Str_Int_Float_Dub_Char(o, 0); }
        static Str_Int_Float_Dub_Char of(@NotNull Integer o) { return new Str_Int_Float_Dub_Char(o, 1); }
        static Str_Int_Float_Dub_Char of(@NotNull Float o) { return new Str_Int_Float_Dub_Char(o, 2); }
        static Str_Int_Float_Dub_Char of(@NotNull Double o) { return new Str_Int_Float_Dub_Char(o, 3); }
        static Str_Int_Float_Dub_Char of(@NotNull Character o) { return new Str_Int_Float_Dub_Char(o, 4); }
    }

    @Test
    public void testBasics() {
        Str_Int_Float_Dub_Char oo5 = Str_Int_Float_Dub_Char.of("right");
        assertEquals("right", oo5.match(s -> s,
                                         i -> "wrong",
                                         f -> "bad",
                                         d -> "evil",
                                         c -> "awful"));
        assertEquals("\"right\":String|Integer|Float|Double|Character", oo5.toString());

        oo5 = Str_Int_Float_Dub_Char.of(57);
        assertEquals(Integer.valueOf(57), oo5.match(s -> -99,
                                                    i -> i,
                                                    f -> 99,
                                                    d -> 2,
                                                    c -> 55));
        assertEquals("57:String|Integer|Float|Double|Character", oo5.toString());

        oo5 = Str_Int_Float_Dub_Char.of(57.2f);
        assertEquals(Float.valueOf(57.2f), oo5.match(s -> -99f,
                                                     i -> 99f,
                                                     f -> f,
                                                     d -> 2,
                                                     c -> 6));
        assertEquals("57.2:String|Integer|Float|Double|Character", oo5.toString());

        oo5 = Str_Int_Float_Dub_Char.of(17.2);
        assertEquals(Double.valueOf(17.2), oo5.match(s -> -99f,
                                                     i -> 99f,
                                                     f -> 2,
                                                     d -> d,
                                                     c -> -55f));
        assertEquals("17.2:String|Integer|Float|Double|Character", oo5.toString());

        oo5 = Str_Int_Float_Dub_Char.of('c');
        assertEquals(Character.valueOf('c'), oo5.match(s -> 's',
                                                       i -> 'i',
                                                       f -> 'f',
                                                       d -> 'd',
                                                       c -> c));
        assertEquals("c:String|Integer|Float|Double|Character", oo5.toString());
    }

    @Test public void testEquality() {
        assertEquals(0, Str_Int_Float_Dub_Char.of("").hashCode());
        assertEquals(1, Str_Int_Float_Dub_Char.of(0).hashCode());
        assertEquals(2, Str_Int_Float_Dub_Char.of(0.0f).hashCode());
        assertEquals(3, Str_Int_Float_Dub_Char.of(0.0).hashCode());
        assertEquals(36, Str_Int_Float_Dub_Char.of(' ').hashCode());
        assertNotEquals(3, Str_Int_Float_Dub_Char.of(-1.3).hashCode());

        assertFalse(Str_Int_Float_Dub_Char.of(5f).equals(Str_Int_Float_Dub_Char.of(5)));
        assertFalse(Str_Int_Float_Dub_Char.of(41).equals(Str_Int_Float_Dub_Char.of("A")));
        assertFalse(Str_Int_Float_Dub_Char.of("A").equals(Str_Int_Float_Dub_Char.of(41)));
        assertFalse(Str_Int_Float_Dub_Char.of(-19.3f).equals(Str_Int_Float_Dub_Char.of(-19.3)));

        assertFalse(Str_Int_Float_Dub_Char.of(65).equals(Str_Int_Float_Dub_Char.of("A")));
        assertFalse(Str_Int_Float_Dub_Char.of("A").equals(Str_Int_Float_Dub_Char.of(65)));
        assertFalse(Str_Int_Float_Dub_Char.of(65.0f).equals(Str_Int_Float_Dub_Char.of(65.0)));

        assertTrue(Str_Int_Float_Dub_Char.of(37).equals(Str_Int_Float_Dub_Char.of(37)));

        EqualsContract.equalsDistinctHashCode(Str_Int_Float_Dub_Char.of("one"), Str_Int_Float_Dub_Char.of("one"),
                                              Str_Int_Float_Dub_Char.of("one"),
                                              Str_Int_Float_Dub_Char.of("onf"));

        EqualsContract.equalsDistinctHashCode(Str_Int_Float_Dub_Char.of(97), Str_Int_Float_Dub_Char.of(97),
                                              Str_Int_Float_Dub_Char.of(97),
                                              Str_Int_Float_Dub_Char.of(-97));

        EqualsContract.equalsDistinctHashCode(Str_Int_Float_Dub_Char.of(17f), Str_Int_Float_Dub_Char.of(17f),
                                              Str_Int_Float_Dub_Char.of(17f),
                                              Str_Int_Float_Dub_Char.of(-17f));

        EqualsContract.equalsDistinctHashCode(Str_Int_Float_Dub_Char.of(31.7), Str_Int_Float_Dub_Char.of(31.7),
                                              Str_Int_Float_Dub_Char.of(31.7),
                                              Str_Int_Float_Dub_Char.of(-3333.7));
    }

    @Test(expected = IllegalArgumentException.class)
    public void subClassExa____n() { new Str_Int_Float_Dub_Char("hi", -1); }

    @Test(expected = ClassCastException.class)
    public void subClassEx_b___0() { new Str_Int_Float_Dub_Char(1, 0); }

    @Test(expected = ClassCastException.class)
    public void subClassEx__c__0() { new Str_Int_Float_Dub_Char(2f, 0); }

    @Test(expected = ClassCastException.class)
    public void subClassEx___d_0() { new Str_Int_Float_Dub_Char(3.0, 0); }

    @Test(expected = ClassCastException.class)
    public void subClassEx____e0() { new Str_Int_Float_Dub_Char('x', 0); }


    @Test(expected = ClassCastException.class)
    public void subClassExa____1() { new Str_Int_Float_Dub_Char("hi", 1); }

    @Test(expected = IllegalArgumentException.class)
    public void subClassEx_b___n() { new Str_Int_Float_Dub_Char(1, 5); }

    @Test(expected = ClassCastException.class)
    public void subClassEx__c__1() { new Str_Int_Float_Dub_Char(2f, 1); }

    @Test(expected = ClassCastException.class)
    public void subClassEx___d_1() { new Str_Int_Float_Dub_Char(3.0, 1); }

    @Test(expected = ClassCastException.class)
    public void subClassEx____e1() { new Str_Int_Float_Dub_Char('x', 1); }


    @Test(expected = ClassCastException.class)
    public void subClassExa____2() { new Str_Int_Float_Dub_Char("hi", 2); }

    @Test(expected = ClassCastException.class)
    public void subClassEx_b___2() { new Str_Int_Float_Dub_Char(1, 2); }

    @Test(expected = IllegalArgumentException.class)
    public void subClassEx__c__n() { new Str_Int_Float_Dub_Char(2f, -1); }

    @Test(expected = ClassCastException.class)
    public void subClassEx___d_2() { new Str_Int_Float_Dub_Char(3.0, 2); }

    @Test(expected = ClassCastException.class)
    public void subClassEx____e2() { new Str_Int_Float_Dub_Char('x', 2); }


    @Test(expected = ClassCastException.class)
    public void subClassExa____3() { new Str_Int_Float_Dub_Char("hi", 3); }

    @Test(expected = ClassCastException.class)
    public void subClassEx_b___3() { new Str_Int_Float_Dub_Char(1, 3); }

    @Test(expected = ClassCastException.class)
    public void subClassEx__c__3() { new Str_Int_Float_Dub_Char(2f, 3); }

    @Test(expected = IllegalArgumentException.class)
    public void subClassEx___d_n() { new Str_Int_Float_Dub_Char(3.0, 5); }

    @Test(expected = ClassCastException.class)
    public void subClassEx____e3() { new Str_Int_Float_Dub_Char('x', 3); }


    @Test(expected = ClassCastException.class)
    public void subClassExa____4() { new Str_Int_Float_Dub_Char("hi", 4); }

    @Test(expected = ClassCastException.class)
    public void subClassEx_b___4() { new Str_Int_Float_Dub_Char(1, 4); }

    @Test(expected = ClassCastException.class)
    public void subClassEx__c__4() { new Str_Int_Float_Dub_Char(2f, 4); }

    @Test(expected = ClassCastException.class)
    public void subClassEx___d_4() { new Str_Int_Float_Dub_Char(3.0, 4); }

    @Test(expected = IllegalArgumentException.class)
    public void subClassEx____en() { new Str_Int_Float_Dub_Char('x', -1); }
}