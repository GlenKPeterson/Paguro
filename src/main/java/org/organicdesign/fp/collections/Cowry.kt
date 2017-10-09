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

import java.util.Arrays

/**
 * Cowry is short for Copy On Write aRraY and contains utilities for doing this quickly and correctly.
 * It's package-private for now, final, and cannot be instantiated.
 * Created by gpeterso on 5/21/17.
 */
// We only one empty array and it makes the code simpler than pointing to null all the time.
// Have to time the difference between using this and null.  The only difference I can imagine
// is that this has an address in memory and null does not, so it could save a memory lookup
// in some places.
val EMPTY_ARRAY = arrayOfNulls<Any>(0)

// =================================== Array Helper Functions ==================================
// Helper function to avoid type warnings.
@Suppress("UNCHECKED_CAST")
fun <T> emptyArray() = EMPTY_ARRAY as Array<T>

//    // Thank you jeannicolas
//    // http://stackoverflow.com/questions/80476/how-can-i-concatenate-two-arrays-in-java
//    private static <T> T[] arrayGenericConcat(T[] a, T[] b) {
//        int aLen = a.length;
//        int bLen = b.length;
//
//        @SuppressWarnings("unchecked")
//        T[] c = (T[]) Array.newInstance(a.getClass().getComponentType(), aLen + bLen);
//        System.arraycopy(a, 0, c, 0, aLen);
//        System.arraycopy(b, 0, c, aLen, bLen);
//
//        return c;
//    }

// Helper function to avoid type warnings.
fun <T> singleElementArray(elem: T?): Array<T?> {
    @Suppress("UNCHECKED_CAST")
    return arrayOf<Any?>(elem as T) as Array<T?>
}

//// Helper function to avoid type warnings.
//fun <T> singleElementArray(elem: T, tClass: Class<T>): Array<out T> {
//    @Suppress("UNCHECKED_CAST")
//    val newItems = arrayOf<>(elem) as Array<T> //java.lang.reflect.Array.newInstance(tClass, 1) as Array<T>
//    newItems[0] = elem
//    return newItems
//}

fun <T> insertIntoArrayAt(item: T?, items: Array<T?>, idx: Int): Array<T?> {
    // Make an array that's one bigger.  It's too bad that the JVM bothers to
    // initialize this with nulls.

    @Suppress("UNCHECKED_CAST")
    val newItems: Array<T?> = arrayOfNulls<Any>(items.size + 1) as Array<T?>

    // If we aren't inserting at the first item, array-copy the items before the insert
    // point.
    if (idx > 0) {
        System.arraycopy(items, 0, newItems, 0, idx)
    }

    // Insert the new item.
    newItems[idx] = item

    // If we aren't inserting at the last item, array-copy the items after the insert
    // point.
    if (idx < items.size) {
        System.arraycopy(items, idx, newItems, idx + 1, items.size - idx)
    }

    return newItems
}

fun <T> insertIntoArrayAt(item: T, items: Array<out T>, idx: Int, tClass: Class<T>): Array<T> {
    // Make an array that's one bigger.  It's too bad that the JVM bothers to
    // initialize this with nulls.

    // Make an array that big enough.  It's too bad that the JVM bothers to
    // initialize this with nulls.
    @Suppress("UNCHECKED_CAST")
    val newItems: Array<T> = java.lang.reflect.Array.newInstance(tClass, items.size + 1) as Array<T>

    // If we aren't inserting at the first item, array-copy the items before the insert
    // point.
    if (idx > 0) {
        System.arraycopy(items, 0, newItems, 0, idx)
    }

    // Insert the new item.
    @Suppress("UNCHECKED_CAST")
    (newItems as Array<T?>).set(idx, item)

    // If we aren't inserting at the last item, array-copy the items after the insert
    // point.
    if (idx < items.size) {
        System.arraycopy(items, idx, newItems, idx + 1, items.size - idx)
    }

    return newItems
}

fun <T> arrayCopy(items: Array<T>, length: Int, tClass: Class<T>?): Array<T> {
    @Suppress("UNCHECKED_CAST")
    val newItems = if (tClass == null) {
        arrayOfNulls<Any>(length)
    } else {
        java.lang.reflect.Array.newInstance(tClass, length)
    } as Array<T>

    System.arraycopy(items, 0, newItems, 0,
                     if (items.size < length) items.size else length)
    return newItems
}

//    private static <T> T[] insertIntoArrayAt(T item, T[] items, int idx) {
//        return insertIntoArrayAt(item, items, idx, null);
//    }

/**
 * Called splice, but handles precat (idx = 0) and concat (idx = origItems.length).
 * @param insertedItems the items to insert
 * @param origItems the original items.
 * @param idx the index to insert new items at
 * @param tClass the class of the resulting new array
 * @return a new array with the new items inserted at the proper position of the old array.
 */
fun <A> spliceIntoArrayAt(insertedItems: Array<out A>, origItems: Array<out A>, idx: Int,
                          tClass: Class<A>?): Array<A> {
    // Make an array that big enough.  It's too bad that the JVM bothers to
    // initialize this with nulls.
    @Suppress("UNCHECKED_CAST")
    val newItems = if (tClass == null) {
        arrayOfNulls<Any>(insertedItems.size + origItems.size)
    } else {
        java.lang.reflect.Array.newInstance(tClass, insertedItems.size + origItems.size)
    } as Array<A>

    // If we aren't inserting at the first item, array-copy the items before the insert
    // point.
    if (idx > 0) {
        //               src,  srcPos, dest,destPos,length
        System.arraycopy(origItems, 0, newItems, 0, idx)
    }

    // Insert the new items
    //               src,      srcPos,     dest, destPos, length
    System.arraycopy(insertedItems, 0, newItems, idx, insertedItems.size)

    // If we aren't inserting at the last item, array-copy the items after the insert
    // point.
    if (idx < origItems.size) {
        System.arraycopy(origItems, idx, newItems, idx + insertedItems.size,
                         origItems.size - idx)
    }
    return newItems
}

//    private static int[] replaceInIntArrayAt(int replacedItem, int[] origItems, int idx) {
//        // Make an array that big enough.  It's too bad that the JVM bothers to
//        // initialize this with nulls.
//        int[] newItems = new int[origItems.length];
//        System.arraycopy(origItems, 0, newItems, 0, origItems.length);
//        newItems[idx] = replacedItem;
//        return newItems;
//    }

fun <T> replaceInArrayAt(replacedItem: T?, origItems: Array<out T?>, idx: Int): Array<T?> {
    // Make an array that big enough.  It's too bad that the JVM bothers to
    // initialize this with nulls.
    @Suppress("UNCHECKED_CAST")
    val newItems = arrayOfNulls<Any>(origItems.size) as Array<out T?>

    System.arraycopy(origItems, 0, newItems, 0, origItems.size)

    @Suppress("UNCHECKED_CAST")
    (newItems as Array<T?>).set(idx, replacedItem)
    return newItems
}


fun <T> replaceInArrayAt(replacedItem: T, origItems: Array<out T>, idx: Int,
                         tClass: Class<T>): Array<T> {
    // Make an array that big enough.  It's too bad that the JVM bothers to
    // initialize this with nulls.
    @Suppress("UNCHECKED_CAST")
    val newItems = java.lang.reflect.Array.newInstance(tClass, origItems.size) as Array<T>

    System.arraycopy(origItems, 0, newItems, 0, origItems.size)

    @Suppress("UNCHECKED_CAST")
    (newItems as Array<T?>).set(idx, replacedItem)
    return newItems
}

/**
 * Only call this if the array actually needs to be split (0 &lt; splitPoint &lt; orig.length).
 * @param orig array to split
 * @param splitIndex items less than this index go in the left, equal or greater in the right.
 * @return a 2D array of leftItems then rightItems
 */
fun <T> splitArray(orig: Array<T>, splitIndex: Int): Pair<Array<T>, Array<T>> =
        Pair(Arrays.copyOf<T>(orig, splitIndex),
             Arrays.copyOfRange<T>(orig, splitIndex, orig.size))

//        if (splitIndex < 1) {
//            throw new IllegalArgumentException("Called split when splitIndex < 1");
//        }
//        if (splitIndex > orig.length - 1) {
//            throw new IllegalArgumentException("Called split when splitIndex > orig.length - 1");
//        }

// NOTE:
// I sort of suspect that generic 2D array creation where the two arrays are of a different
// length is not possible in Java, or if it is, it's not likely to be much faster than
// what we have here.  I'd just copy the Arrays.copyOf code everywhere this function is used
// if you want more speed.
//        int rightLength = orig.length - splitIndex;
//        Class<T> tClass = (Class<T>) orig.getClass().getComponentType();
//        Tuple2<T[],T[]> split = Tuple2.of((T[]) Array.newInstance(tClass, splitIndex),
//                                          (T[]) Array.newInstance(tClass, rightLength));
//
// Tuple2<T[],T[]> split =
//        // original array, offset, newArray, offset, length
//        System.arraycopy(orig, 0, split._1(), 0, splitIndex);
//
//        System.arraycopy(orig, splitIndex, split._2(), 0, rightLength);
//        return split;

/**
 * Only call this if the array actually needs to be split (0 &lt; splitPoint &lt; orig.length).
 * @param orig array to split
 * @param splitIndex items less than this index go in the left, equal or greater in the right.
 * @return a 2D array of leftItems then rightItems
 */
fun splitArray(orig: IntArray, splitIndex: Int): Array<IntArray> {
    // This function started an exact duplicate of the one above, but for ints.
    //        if (splitIndex < 1) {
    //            throw new IllegalArgumentException("Called split when splitIndex < 1");
    //        }
    //        if (splitIndex > orig.length - 1) {
    //            throw new IllegalArgumentException("Called split when splitIndex > orig.length - 1");
    //        }
    val rightLength = orig.size - splitIndex
    val split = arrayOf(IntArray(splitIndex), IntArray(rightLength))
    // original array, offset, newArray, offset, length
    System.arraycopy(orig, 0, split[0], 0, splitIndex)
    System.arraycopy(orig, splitIndex, split[1], 0, rightLength)
    return split
}

//    static <T> T[] truncateArray(T[] origItems, int newLength, Class<T> tClass) {
//        if (origItems.length == newLength) {
//            return origItems;
//        }
//
//        @SuppressWarnings("unchecked")
//        T[] newItems = (T[]) ((tClass == null) ? new Object[newLength]
//                                               : Array.newInstance(tClass, newLength) );
//
//        //                      src, srcPos,    dest,destPos, length
//        System.arraycopy(origItems, 0, newItems, 0, newLength);
//        return newItems;
//    }