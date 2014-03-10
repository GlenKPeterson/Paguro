// Copyright (c) 2014-03-08 PlanBase Inc. & Glen Peterson
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

package org.organicdesign.fp.experiments;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertArrayEquals;

@RunWith(JUnit4.class)
public class ConcurrentXformTest {

    @Test
    public void basics() {
        Long[] is = new Long[] { 1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L };
        IntRange range = IntRange.of(1, 9);
        assertArrayEquals(ConcurrentXform.of(1, range).toArray(), is);

        assertArrayEquals(ConcurrentXform.of(2, range).toArray(), is);
        assertArrayEquals(ConcurrentXform.of(3, range).toArray(), is);
        assertArrayEquals(ConcurrentXform.of(4, range).toArray(), is);
        assertArrayEquals(ConcurrentXform.of(5, range).toArray(), is);
    }

    @Test
    public void tryStuff() {
        System.out.println();
        IntRange range = IntRange.of(-10000000, 10000000);
        ConcurrentXform cx = ConcurrentXform.of(1, range);
        long startTime = System.currentTimeMillis();
        cx.toArray();
        System.out.println("Time: " + (System.currentTimeMillis() - startTime));
    }

}
