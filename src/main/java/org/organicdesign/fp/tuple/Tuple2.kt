// Copyright 2016 PlanBase Inc. & Glen Peterson
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

package org.organicdesign.fp.tuple

import org.organicdesign.fp.FunctionUtils.stringify
import org.organicdesign.fp.collections.ImMap
import org.organicdesign.fp.collections.UnmodMap
import java.io.Serializable
import kotlin.collections.Map.Entry

/**
 Holds 2 items of potentially different types, and implements Map.Entry (and UnmodMap.UnEntry
 (there is no ImMap.ImEntry)).  Designed to let you easily create immutable subclasses (to give your
 data structures meaningful names) with correct equals(), hashCode(), and toString() methods.
 */
// Fields are protected so that sub-classes can make accessor methods with meaningful names.
open class Tuple2<A,B>(val _1:A, val _2:B) : Entry<A,B>, UnmodMap.UnEntry<A,B>, Serializable {

    // Inherited from Map.Entry
    /** Returns the first field of the tuple.  To implement Map.Entry. */
    override val key: A = _1
    /** Returns the second field of the tuple.  To implement Map.Entry. */
    override val value: B = _2

    /**
     Constructor is protected (not public) for easy inheritance.  Josh Bloch's "Item 1" says public
     static factory methods are better than constructors because they have names, they can return
     an existing object instead of a new one, and they can return a sub-type.  Therefore, you
     have more flexibility with a static factory as part of your public API then with a public
     constructor.
     */

    /**
     * Not compatible with immutability - use
     * [ImMap.assoc]
     * instead because it returns a new map.
     */
    @Deprecated("Mutation not allowed")
    override fun setValue(value: B): B {
        throw UnsupportedOperationException("Modification attempted")
    }

    override fun toString() :String =
            this::class.java.simpleName + "(" +
            stringify(_1) + "," +
            stringify(_2) + ")"


    override fun equals(other: Any?): Boolean {
        // Cheapest operation first...
        if (this === other) { return true; }
        return other is Entry<*,*> &&
               _1 == other.key &&
               _2 == other.value
    }

    // This is specified in java.util.Map as part of the map contract.
    override fun hashCode():Int =
                    (_1?.hashCode() ?: 0) xor
                    (_2?.hashCode() ?: 0)

    companion object {
        // For serializable.  Make sure to change whenever internal data format changes.
        private const val serialVersionUID = 20160906065000L

        /** Map.Entry factory method */
        fun <K,V> of(entry:Entry<K,V>) : Tuple2<K,V> {
            // Protect against multiple-instantiation
            if (entry is Tuple2) {
                return entry
            }
            return Tuple2(entry.key, entry.value)
        }
    }
}