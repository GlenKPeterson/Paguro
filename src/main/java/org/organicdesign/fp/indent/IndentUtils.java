package org.organicdesign.fp.indent;

import org.jetbrains.annotations.NotNull;

import static com.planbase.indented.StringUtils.indent;

public class IndentUtils {

    private IndentUtils() {
        throw new UnsupportedOperationException("No instantiation");
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
            sB.append(indent(0, item));
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
            sB.append(indent(0, item));
        }
        return sB.append("]").toString();
    }
}
