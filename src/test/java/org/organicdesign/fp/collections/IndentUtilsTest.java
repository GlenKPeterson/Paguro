package org.organicdesign.fp.collections;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.organicdesign.fp.indent.IndentUtils.indentSpace;

/**
 Created by gpeterso on 5/21/17.
 */
public class IndentUtilsTest {
    @Test public void testIndentSpace() {
        assertEquals("", indentSpace(Integer.MIN_VALUE).toString());
        assertEquals("", indentSpace(-1).toString());
        assertEquals("", indentSpace(0).toString());
        assertEquals(" ", indentSpace(1).toString());
        assertEquals("  ", indentSpace(2).toString());
        assertEquals("   ", indentSpace(3).toString());
        assertEquals("     ", indentSpace(5).toString());
        assertEquals("       ", indentSpace(7).toString());
        assertEquals("           ", indentSpace(11).toString());
        assertEquals("             ", indentSpace(13).toString());

        String spaces = "";
        for (int i = 0; i < 300; i++) {
            assertEquals(spaces, indentSpace(i).toString());
            spaces = spaces + " ";
        }
    }

}