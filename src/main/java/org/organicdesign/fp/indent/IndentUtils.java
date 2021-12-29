package org.organicdesign.fp.indent;

import org.jetbrains.annotations.NotNull;

public class IndentUtils {

    private IndentUtils() {
        throw new UnsupportedOperationException("No instantiation");
    }

    // ========================================== STATIC ==========================================
    // Note, this is part of something completely different, but was especially useful for
    // debugging the above.  So much so, that I want to keep it when I'm done, but it needs
    // to move somewhere else before releasing.
    private static final String[] SPACES = {
            "",
            " ",
            "  ",
            "   ",
            "    ",
            "     ",
            "      ",
            "       ",
            "        ",
            "         ",
            "          ",
            "           ",
            "            ",
            "             ",
            "              ",
            "               ",
            "                ",
            "                 ",
            "                  ",
            "                   ",
            "                    ",
            "                     ",
            "                      ",
            "                       ",
            "                        ",
            "                         ",
            "                          ",
            "                           ",
            "                            ",
            "                             ",
            "                              ",
            "                               ",
            "                                ",
            "                                 ",
            "                                  ",
            "                                   ",
            "                                    ",
            "                                     ",
            "                                      ",
            "                                       ",
            "                                        ",
            "                                         ",
            "                                          ",
            "                                           ",
            "                                            ",
            "                                             ",
            "                                              ",
            "                                               ",
            "                                                "};

    private static final int SPACES_LENGTH_MINUS_ONE = SPACES.length - 1;

    /**
     Creates a new StringBuilder with the given number of spaces and returns it.
     @param len the number of spaces
     @return a {@link StringBuilder} with the specificed number of initial spaces.
     */
    public static @NotNull StringBuilder indentSpace(int len) {
        StringBuilder sB = new StringBuilder();
        if (len < 1) { return sB; }
        while (len > SPACES_LENGTH_MINUS_ONE) {
            sB.append(SPACES[SPACES_LENGTH_MINUS_ONE]);
            len = len - SPACES_LENGTH_MINUS_ONE;
        }
        return sB.append(SPACES[len]);
    }

    /**
     There is Arrays.toString, but this is intended to produce Cymling code some day.
     */
    public static <T> @NotNull String arrayString(T @NotNull [] items) {
        StringBuilder sB = new StringBuilder("A[");
        boolean isFirst = true;
        for (T item : items) {
            if (isFirst) {
                isFirst = false;
            } else {
                sB.append(" ");
            }
            if (item instanceof String) {
                sB.append("\"").append(item).append("\"");
            } else {
                sB.append(item);
            }
        }
        return sB.append("]").toString();
    }

    /**
     There is Arrays.toString, but this is intended to produce Cymling code some day.
     */
    // TODO: We need one of these for each type of primitive for pretty-printing without commas.
    public static @NotNull String arrayString(int @NotNull [] items) {
        StringBuilder sB = new StringBuilder("i[");
        boolean isFirst = true;
        for (int item : items) {
            if (isFirst) {
                isFirst = false;
            } else {
                sB.append(" ");
            }
            sB.append(item);
        }
        return sB.append("]").toString();
    }
}
