// Copyright 2016-05-28 PlanBase Inc. & Glen Peterson
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
package org.organicdesign.fp.collections

/**
 * Created by gpeterso on 5/21/17.
 */
interface Indented {
    /**
     * Returns a string where line breaks extend the given amount of indentation.
     * @param indent the amount of indent to start at.  Pretty-printed subsequent lines may have
     * additional indent.
     * @return a string with the given starting offset (in spaces) for every line.
     */
    fun indentedStr(indent: Int): String

    companion object {

        // ========================================== STATIC ==========================================
        // Note, this is part of something completely different, but was especially useful for
        // debugging the above.  So much so, that I want to keep it when I'm done, but it needs
        // to move somewhere else before releasing.
        val SPACES = arrayOf("",
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
                             "                                                ")

        val SPACES_LENGTH_MINUS_ONE = SPACES.size - 1

        /**
         * Creates a new StringBuilder with the given number of spaces and returns it.
         * @param length the number of spaces
         * @return a [StringBuilder] with the specificed number of initial spaces.
         */
        fun indentSpace(length: Int): StringBuilder {
            var len = length
            val sB = StringBuilder()
            if (len < 1) {
                return sB
            }
            while (len > SPACES_LENGTH_MINUS_ONE) {
                sB.append(SPACES[SPACES_LENGTH_MINUS_ONE])
                len -= SPACES_LENGTH_MINUS_ONE
            }
            return sB.append(SPACES[len])
        }

        /**
         * There is Arrays.toString, but this is intended to produce Cymling code some day.
         */
        fun <T> arrayString(items: Array<T>): String {
            val sB = StringBuilder("A[")
            var isFirst = true
            for (item in items) {
                if (isFirst) {
                    isFirst = false
                } else {
                    sB.append(" ")
                }
                if (item is String) {
                    sB.append("\"").append(item).append("\"")
                } else {
                    sB.append(item)
                }
            }
            return sB.append("]").toString()
        }

        /**
         * There is Arrays.toString, but this is intended to produce Cymling code some day.
         */
        // TODO: We need one of these for each type of primitive for pretty-printing without commas.
        fun arrayString(items: IntArray): String {
            val sB = StringBuilder("i[")
            var isFirst = true
            for (item in items) {
                if (isFirst) {
                    isFirst = false
                } else {
                    sB.append(" ")
                }
                sB.append(item)
            }
            return sB.append("]").toString()
        }
    }
}