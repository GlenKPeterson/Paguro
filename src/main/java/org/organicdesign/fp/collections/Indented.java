package org.organicdesign.fp.collections;

/**
 Created by gpeterso on 5/21/17.
 */
public interface Indented {
    String indentedStr(int indent);

    // ========================================== STATIC ==========================================
    // Note, this is part of something completely different, but was especially useful for
    // debugging the above.  So much so, that I want to keep it when I'm done, but it needs
    // to move somewhere else before releasing.
    String[] SPACES = {
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

    int SPACES_LENGTH_MINUS_ONE = SPACES.length - 1;

    static StringBuilder indentSpace(int len) {
        StringBuilder sB = new StringBuilder();
        if (len < 1) { return sB; }
        while (len > SPACES_LENGTH_MINUS_ONE) {
            sB.append(SPACES[SPACES_LENGTH_MINUS_ONE]);
            len = len - SPACES_LENGTH_MINUS_ONE;
        }
        return sB.append(SPACES[len]);
    }

    static <T> String arrayString(T[] items) {
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

    // TODO: We need one of these for each type of primitive for pretty-printing without commas.
    static String arrayString(int[] items) {
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