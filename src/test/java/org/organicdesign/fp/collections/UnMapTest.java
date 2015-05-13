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

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.organicdesign.fp.StaticImports.unMap;

public class UnMapTest {
    @Test public void containsValue() {
        UnMap<String,Integer> m = unMap("Hello", 3,
                                        "World", 2,
                                        "This", 1,
                                        "Is", 0,
                                        "A", -1,
                                        "test", -2);
        assertFalse(m.containsValue(Integer.MAX_VALUE));
        assertFalse(m.containsValue(4));
        assertTrue(m.containsValue(3));
        assertTrue(m.containsValue(0));
        assertTrue(m.containsValue(-2));
        assertFalse(m.containsValue(-3));
        assertFalse(m.containsValue(Integer.MIN_VALUE));
    }
}
