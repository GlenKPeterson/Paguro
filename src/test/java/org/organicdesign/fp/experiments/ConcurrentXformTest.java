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
    public void arrayCorrectness() {
        Long[] is = new Long[] { 1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L };
        IntRange range = IntRange.of(1, 9);
        assertArrayEquals(ConcurrentXform.of(1, range).toTypedArray(), is);

        assertArrayEquals(ConcurrentXform.of(2, range).toTypedArray(), is);
        assertArrayEquals(ConcurrentXform.of(3, range).toTypedArray(), is);
        assertArrayEquals(ConcurrentXform.of(4, range).toTypedArray(), is);
        assertArrayEquals(ConcurrentXform.of(5, range).toTypedArray(), is);
    }

    @Test
    public void linkedListCorrectness() {
        Long[] is = new Long[] { 1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L };
        IntRange range = IntRange.of(1, 9);
        assertArrayEquals(ConcurrentXform.of(1, range).toLinkedList().toTypedArray(), is);

        assertArrayEquals(ConcurrentXform.of(2, range).toLinkedList().toTypedArray(), is);
        assertArrayEquals(ConcurrentXform.of(3, range).toLinkedList().toTypedArray(), is);
        assertArrayEquals(ConcurrentXform.of(4, range).toLinkedList().toTypedArray(), is);
        assertArrayEquals(ConcurrentXform.of(5, range).toLinkedList().toTypedArray(), is);
    }

//    @Test
//    @Ignore
//    public void arraySpeed() {
//        System.out.println();
//        IntRange range = IntRange.of(-10000000, 10000000);
//        ConcurrentXform cx = ConcurrentXform.of(2, range);
//        long startTime = System.currentTimeMillis();
//        cx.toTypedArray();
//        System.out.println("Time: " + (System.currentTimeMillis() - startTime));
//    }

//    @Test
//    @Ignore
//    public void linkedListSpeed() {
//        // Test results (in seconds:
//        // Threads:    1      2   With Option<T> return type
//        //          11.1   11.3
//        //          11.0   11.4
//        //          10.0   11.4
//        // With null return type
//        //          12.3   12.1
//        //          12.0   12.2
//        //          12.3   12.3
//        // Back with option again
//        //          10.2   12.0
//        //          11.5   11.7
//        //          11.3   12.0
//        //
//        // Conclusions: Option is not slowing anything down.
//        // Question: Why is it that even with one thread, I can *see* two threads at over 95%
//        // on my system monitor for the entire duration of the test???  I've only got two
//        // processors, so it would be interesting to run on bigger hardware.
//        System.out.println();
//        IntRange range = IntRange.of(-10000000, 10000000);
//        ConcurrentXform cx = ConcurrentXform.of(2, range);
//        long startTime = System.currentTimeMillis();
//        cx.toLinkedList();
//        System.out.println("Time: " + (System.currentTimeMillis() - startTime));
//    }
}
