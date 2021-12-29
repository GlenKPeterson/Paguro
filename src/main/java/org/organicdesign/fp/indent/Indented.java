package org.organicdesign.fp.indent;

import org.jetbrains.annotations.NotNull;

/**
 Created by gpeterso on 5/21/17.
 */
public interface Indented {
    /**
     Returns a string where line breaks extend the given amount of indentation.
     @param indent the amount of indent to start at.  Pretty-printed subsequent lines may have
     additional indent.
     @return a string with the given starting offset (in spaces) for every line.
     */
    @NotNull String indentedStr(int indent);
}