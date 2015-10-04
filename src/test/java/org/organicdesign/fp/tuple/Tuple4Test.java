package org.organicdesign.fp.tuple;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.organicdesign.fp.testUtils.EqualsContract.equalsDistinctHashCode;
import static org.organicdesign.fp.testUtils.EqualsContract.equalsSameHashCode;

public class Tuple4Test {
    @Test public void constructionAndAccess() {
        Tuple4<Double,Integer,String,Boolean> a = Tuple4.of(1.0, 3, "3rd", true);

        assertEquals(new Double(1.0), a._1());
        assertEquals(new Integer(3), a._2());
        assertEquals("3rd", a._3());
        assertTrue(a._4());

        equalsDistinctHashCode(a, Tuple4.of(new Double(1.0), 3, "3rd", true),
                               Tuple4.of(1.0, Integer.valueOf(3), "3rd", true),
                               Tuple4.of(1.0, 3, "3rd", false));

        equalsSameHashCode(a, Tuple4.of(new Double(1.0), 3, "3rd", true),
                           Tuple4.of(1.0, Integer.valueOf(3), "3rd", true),
                           Tuple4.of(3, 1.0, "3rd", true));

        assertEquals("Tuple4(1.0,3,3rd,true)", a.toString());
    }

}
