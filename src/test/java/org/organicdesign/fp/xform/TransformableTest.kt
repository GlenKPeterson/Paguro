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

package org.organicdesign.fp.xform

import java.util.*

import org.junit.Test
import org.organicdesign.fp.collections.ImMap
import org.organicdesign.fp.collections.ImSortedMap
import org.organicdesign.fp.collections.PersistentHashMap
import org.organicdesign.fp.collections.PersistentTreeMap
import org.organicdesign.fp.collections.UnmodSortedIterable
import org.organicdesign.fp.oneOf.Option
import org.organicdesign.fp.tuple.Tuple2

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.organicdesign.fp.FunctionUtils.ordinal
import kotlin.collections.Map.Entry

class TransformableTest {

    @Test
    @Throws(Exception::class)
    fun testToMutableList() {
        val control = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9)
        val trans = Xform.of(control)
        assertEquals(control, trans.toMutList())
    }

    @Test
    @Throws(Exception::class)
    fun testToImList() {
        val control = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9)
        val trans = Xform.of(control)
        assertEquals(control, trans.toImList())
    }

    @Test
    @Throws(Exception::class)
    fun testToMutableMap() {
        val control = HashMap<Int, String>()
        for (i in 1..11) {
            control.put(i, ordinal(i))
        }
        val trans = Xform.of<Entry<Int, String>>(control.entries)
        assertEquals(control, trans.toMutMap<Int, String> { x -> x })
    }

    @Test
    @Throws(Exception::class)
    fun testToMutableSortedMap() {
        val items:List<Int> = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9)
        val comp = Integer::compare
        val control = TreeMap<Int, String>(comp)
        for (i in items) {
            control.put(i, ordinal(i))
        }
        val trans = Xform.of(items)
        assertEquals(control, trans.toMutSortedMap(comp, { i -> Tuple2.of(i, ordinal(i)) }))
    }

    @Test
    @Throws(Exception::class)
    fun testToImMap() {
        var control: ImMap<Int, String> = PersistentHashMap.empty()
        for (i in 1..11) {
            control = control.assoc(i, ordinal(i))
        }
        val trans = Xform.of<Entry<Int, String>>(control)
        assertEquals(control, trans.toImMap<Int, String> { x -> x })

    }

    @Test
    @Throws(Exception::class)
    fun testToImSortedMap() {
        val items = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9)
        val comp = Integer::compare
        var control: ImSortedMap<Int, String> = PersistentTreeMap.empty(comp)
        for (i in items) {
            control = control.assoc(i, ordinal(i))
        }
        val trans = Xform.of(items)
        assertEquals(control, trans.toImSortedMap(comp) { i -> Tuple2.of(i, ordinal(i!!)) })
    }

    @Test
    @Throws(Exception::class)
    fun testToMutableSortedSet() {
        val control = TreeSet(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9))
        val trans = Xform.of(control)
        assertTrue(UnmodSortedIterable.equal(UnmodSortedIterable.castFromSortedSet(control),
                                             UnmodSortedIterable.castFromSortedSet(trans.toMutSortedSet { a, b -> a!! - b!! })))
    }

    @Test
    @Throws(Exception::class)
    fun testToImSet() {
        val items = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9)
        val control = HashSet(items)
        val trans = Xform.of(items)
        assertEquals(control, trans.toImSet())
    }

    @Test
    @Throws(Exception::class)
    fun testToImSortedSet() {
        val items = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9)
        val comp = Integer::compare
        val control = TreeSet<Int>(comp)
        control.addAll(items)
        val trans = Xform.of(items)
        assertEquals(control, trans.toImSortedSet(comp))
        assertTrue(UnmodSortedIterable.equal(UnmodSortedIterable.castFromSortedSet(control),
                                             trans.toImSortedSet(comp)))
    }

    @Test
    @Throws(Exception::class)
    fun testToMutableSet() {
        val control = HashSet(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9))
        val trans = Xform.of(control)
        assertEquals(control, trans.toMutSet())
    }

    @Test
    fun testHead() {
        // TODO: I'm not getting test coverage from this.  It seems to be using the UnmodIterable implementation instead (which is probably more efficient)
        val tint1 = Xform.of(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9))
        assertEquals(Option.some(1), (tint1 as Transformable<Int>).head())

        val tint2 = Xform.of(emptyList<Int>())
        assertEquals(Option.none<Any>(), (tint2 as Transformable<Int>).head())
    }
}