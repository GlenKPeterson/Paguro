// Copyright 2015 PlanBase Inc. & Glen Peterson
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.StringBuilder;

public class TupleGenerator {

    public static String ordinal(final int origI) {
        final int i = (origI < 0) ? -origI : origI;
        final int modTen = i % 10;
        if ( (modTen < 4) && (modTen > 0)) {
            int modHundred = i % 100;
            if ( (modHundred < 21) && (modHundred > 3) ) {
                return Integer.toString(origI) + "th";
            }
            switch (modTen) {
                case 1: return Integer.toString(origI) + "st";
                case 2: return Integer.toString(origI) + "nd";
                case 3: return Integer.toString(origI) + "rd";
            }
        }
        return Integer.toString(origI) + "th";
    }

    static String[] CHARS = new String[] {
            "A","B","C","D","E","F","G","H","I","J","K","L","M",
            "N","O","P","Q","R","S","T","U","V","W","X","Y","Z"
    };

    static String intToChar(int i) {
        return CHARS[i - 1];
    }

    static String types(int i) {
        StringBuilder sB = new StringBuilder();
        boolean isFirst = true;
        for (int l = 1; l <= i; l++) {
            if (isFirst) {
                isFirst = false;
            } else {
                sB.append(",");
            }
            sB.append(intToChar(l));
        }
        return sB.toString();
    }

    static String factoryParams(int i) {
        StringBuilder sB = new StringBuilder();
        boolean isFirst = true;
        for (int l = 1; l <= i; l++) {
            if (isFirst) {
                isFirst = false;
            } else {
                sB.append(", ");
            }
            sB.append(intToChar(l));
            sB.append(" ");
            sB.append(intToChar(l).toLowerCase());
        }
        return sB.toString();
    }

    public static void main(String... args) throws IOException {
        for (int i = 2; i < 27; i++) {
            FileWriter fr = new FileWriter("Tuple" + i + ".java");
            fr.write("// Copyright 2015 PlanBase Inc. & Glen Peterson\n" +
                     "//\n" +
                     "// Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
                     "// you may not use this file except in compliance with the License.\n" +
                     "// You may obtain a copy of the License at\n" +
                     "//\n" +
                     "// http://www.apache.org/licenses/LICENSE-2.0\n" +
                     "//\n" +
                     "// Unless required by applicable law or agreed to in writing, software\n" +
                     "// distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
                     "// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
                     "// See the License for the specific language governing permissions and\n" +
                     "// limitations under the License.\n" +
                     "\n" +
                     "package org.organicdesign.fp.tuple;\n" +
                     "\n" +
                     "import java.util.Objects;\n" +
                     "\n" +
                     "/**\n" +
                     " Holds " + i + " items of potentially different types.\n" +
                     " */\n" +
                     "public class Tuple" + i + "<");
            fr.write(types(i));
            fr.write("> {\n" +
                     "    // Fields are protected for easy inheritance\n");
            for (int l = 1; l <= i; l++) {
                fr.write("    protected final ");
                fr.write(intToChar(l));
                fr.write(" _");
                fr.write(String.valueOf(l));
                fr.write(";\n");
            }

            fr.write("\n" +
                     "    /** Constructor is protected for easy inheritance. */\n" +
                     "    protected Tuple" + i + "(");
            fr.write(factoryParams(i));
            fr.write(") {\n       ");
            for (int l = 1; l <= i; l++) {
                fr.write(" _");
                fr.write(String.valueOf(l));
                fr.write(" = ");
                fr.write(intToChar(l).toLowerCase());
                fr.write(";");
            }
            fr.write("\n" +
                     "    }\n" +
                     "\n" +
                     "    /** Public static factory method */\n" +
                     "    public static <");
            fr.write(types(i));
            fr.write("> Tuple" + i + "<");
            fr.write(types(i));
            fr.write("> of(");
            fr.write(factoryParams(i));
            fr.write(") {\n" +
                     "        return new Tuple" + i + "<>(");
            boolean isFirst = true;
            for (int l = 1; l <= i; l++) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    fr.write(", ");
                }
                fr.write(intToChar(l).toLowerCase());
            }
            fr.write(");\n" +
                     "    }\n");

            for (int l = 1; l <= i; l++) {
                fr.write("\n" +
                         "    /** Returns the " + ordinal(l) + " field of the tuple */\n" +
                         "    public ");
                fr.write(intToChar(l));
                fr.write(" _");
                fr.write(String.valueOf(l));
                fr.write("() { return _");
                fr.write(String.valueOf(l));
                fr.write("; }\n");
            }

            fr.write("\n" +
                     "    @Override\n" +
                     "    public String toString() {\n" +
                     "        return getClass().getSimpleName() + \"(\" +\n" +
                     "               _");
            isFirst = true;
            for (int l = 1; l <= i; l++) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    fr.write(" + \",\" + _");
                }
                fr.write(String.valueOf(l));
            }
            fr.write(" +\n" +
                     "               \")\";\n" +
                     "    }\n" +
                     "\n" +
                     "    @Override\n" +
                     "    public boolean equals(Object other) {\n" +
                     "        // Cheapest operation first...\n" +
                     "        if (this == other) { return true; }\n" +
                     "        if (!(other instanceof Tuple" + i + ")) { return false; }\n" +
                     "        // Details...\n" +
                     "        @SuppressWarnings(\"rawtypes\") final Tuple" + i + " that = (Tuple" + i + ") other;\n" +
                     "\n" +
                     "        return ");
            isFirst = true;
            for (int l = 1; l <= i; l++) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    fr.write(" &&\n" +
                             "               ");
                }
                fr.write("Objects.equals(this._" + l + ", that._" + l + "())");
            }
            fr.write(";\n" +
                     "    }\n" +
                     "\n" +
                     "    @Override\n" +
                     "    public int hashCode() {\n" +
                     "        // Has to match Tuple2 which implements java.util.Map.Entry as part of the map contract.\n" +
                     "        return  ( (_1 == null ? 0 : _1.hashCode()) ^\n" +
                     "                  (_2 == null ? 0 : _2.hashCode()) )");
            for (int l = 3; l <= i; l++) {
                fr.write(" +\n" +
                         "                (_" + l + " == null ? 0 : _" + l + ".hashCode())");
            }
            fr.write(";\n" +
                     "    }\n" +
                     "}");
            fr.flush();
            fr.close();
        }
        return;
    }
}