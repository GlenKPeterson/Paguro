package org.organicdesign.fp.collections

import org.junit.Test

import org.junit.Assert.*
import org.organicdesign.fp.collections.Indented.Companion.indentSpace

/**
 * Created by gpeterso on 5/21/17.
 */
class IndentedTest {
    @Test
    fun testIndentSpace() {
        assertEquals("", indentSpace(Integer.MIN_VALUE).toString())
        assertEquals("", indentSpace(-1).toString())
        assertEquals("", indentSpace(0).toString())
        assertEquals(" ", indentSpace(1).toString())
        assertEquals("  ", indentSpace(2).toString())
        assertEquals("   ", indentSpace(3).toString())
        assertEquals("     ", indentSpace(5).toString())
        assertEquals("       ", indentSpace(7).toString())
        assertEquals("           ", indentSpace(11).toString())
        assertEquals("             ", indentSpace(13).toString())

        var spaces = ""
        for (i in 0..299) {
            assertEquals(spaces, indentSpace(i).toString())
            spaces = spaces + " "
        }
    }

}