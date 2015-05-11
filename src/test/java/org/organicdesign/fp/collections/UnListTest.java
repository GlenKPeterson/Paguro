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

package org.organicdesign.fp.collections;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.organicdesign.fp.StaticImports.un;

public class UnListTest {
    @Test public void indexOf() {
        assertEquals(-1, un(Arrays.asList("Along", "came", "a", "spider")).indexOf("hamster"));
        assertEquals(0, un(Arrays.asList("Along", "came", "a", "spider")).indexOf("Along"));
        assertEquals(2, un(Arrays.asList("Along", "came", "a", "spider")).indexOf("a"));
        assertEquals(3, un(Arrays.asList("Along", "came", "a", "spider")).indexOf("spider"));

        assertEquals(-1, un(Arrays.asList("Along", "came", "a", "spider")).lastIndexOf("hamster"));
        assertEquals(0, un(Arrays.asList("Along", "came", "a", "spider")).lastIndexOf("Along"));
        assertEquals(2, un(Arrays.asList("Along", "came", "a", "spider")).lastIndexOf("a"));
        assertEquals(3, un(Arrays.asList("Along", "came", "a", "spider")).lastIndexOf("spider"));

        assertEquals(5, un(Arrays.asList("Along", "came", "a", "spider", "and", "a", "poodle")).lastIndexOf("a"));
        assertEquals(5, un(Arrays.asList("Along", "came", "a", "spider", "and", "a")).lastIndexOf("a"));
        assertEquals(6, un(Arrays.asList("Along", "came", "a", "spider", "and", "a", "Along")).lastIndexOf("Along"));

        assertEquals(-1, UnList.empty().indexOf("hamster"));
        assertEquals(-1, UnList.empty().indexOf(39));
        assertEquals(-1, UnList.empty().lastIndexOf("hamster"));
        assertEquals(-1, UnList.empty().lastIndexOf(39));
    }

    @Test public void subList() {
        assertEquals(Arrays.asList("stones", "will", "break"),
                     un(Arrays.asList("Sticks", "and", "stones", "will", "break", "my", "bones", "but", "tests", "will", "never", "hurt", "me."))
                             .subList(2,5));
        assertEquals(Arrays.asList("Sticks", "and", "stones", "will", "break", "my", "bones", "but", "tests", "will", "never", "hurt", "me."),
                     un(Arrays.asList("Sticks", "and", "stones", "will", "break", "my", "bones", "but", "tests", "will", "never", "hurt", "me."))
                             .subList(0,13));
        assertEquals(Collections.emptyList(),
                     un(Arrays.asList("Sticks", "and", "stones", "will", "break", "my", "bones", "but", "tests",
                                      "will", "never", "hurt", "me."))
                             .subList(2, 2));
    }
}
