package org.organicdesign.fp.collections;

import java.lang.reflect.Array;
import java.util.Arrays;

import org.organicdesign.fp.tuple.Tuple2;

/**
 Cowry is short for Copy On Write aRraY and contains utilities for doing this quickly and correctly.
 It's package-private for now, final, and cannot be instantiated.
 Created by gpeterso on 5/21/17.
 */
final class Cowry {
    private Cowry() {
        throw new UnsupportedOperationException("Do not instantiate");
    }

    // We only one empty array and it makes the code simpler than pointing to null all the time.
    // Have to time the difference between using this and null.  The only difference I can imagine
    // is that this has an address in memory and null does not, so it could save a memory lookup
    // in some places.
    static final Object[] EMPTY_ARRAY = new Object[0];

    // =================================== Array Helper Functions ==================================
    // Helper function to avoid type warnings.
    @SuppressWarnings("unchecked")
    static <T> T[] emptyArray() { return (T[]) EMPTY_ARRAY; }

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
    @SuppressWarnings("unchecked")
    static <T> T[] singleElementArray(T elem) {
        return (T[]) new Object[] { elem };
    }

    static <T> T[] insertIntoArrayAt(T item, T[] items, int idx, Class<T> tClass) {
        // Make an array that's one bigger.  It's too bad that the JVM bothers to
        // initialize this with nulls.

        @SuppressWarnings("unchecked")
        // Make an array that big enough.  It's too bad that the JVM bothers to
                // initialize this with nulls.
                T[] newItems = (T[]) ((tClass == null) ? new Object[items.length + 1]
                                                       : Array.newInstance(tClass, items.length + 1) );

        // If we aren't inserting at the first item, array-copy the items before the insert
        // point.
        if (idx > 0) {
            System.arraycopy(items, 0, newItems, 0, idx);
        }

        // Insert the new item.
        newItems[idx] = item;

        // If we aren't inserting at the last item, array-copy the items after the insert
        // point.
        if (idx < items.length) {
            System.arraycopy(items, idx, newItems, idx + 1, items.length - idx);
        }

        return newItems;
    }

    static <T> T[] arrayCopy(T[] items, int length, Class<T> tClass) {
        @SuppressWarnings("unchecked")
        T[] newItems = (T[]) ((tClass == null) ? new Object[length]
                                               : Array.newInstance(tClass, length) );
        System.arraycopy(items, 0, newItems, 0,
                         items.length < length ? items.length
                                               : length);
        return newItems;
    }

//    private static <T> T[] insertIntoArrayAt(T item, T[] items, int idx) {
//        return insertIntoArrayAt(item, items, idx, null);
//    }

    /**
     Called splice, but handles precat (idx = 0) and concat (idx = origItems.length).
     @param insertedItems the items to insert
     @param origItems the original items.
     @param idx the index to insert new items at
     @param tClass the class of the resulting new array
     @return a new array with the new items inserted at the proper position of the old array.
     */
    static <A> A[] spliceIntoArrayAt(A[] insertedItems, A[] origItems, int idx,
                                             Class<A> tClass) {
        // Make an array that big enough.  It's too bad that the JVM bothers to
        // initialize this with nulls.
        @SuppressWarnings("unchecked")
        A[] newItems = tClass == null ? (A[]) new Object[insertedItems.length + origItems.length] :
                       (A[]) Array.newInstance(tClass, insertedItems.length + origItems.length);

        // If we aren't inserting at the first item, array-copy the items before the insert
        // point.
        if (idx > 0) {
            //               src,  srcPos, dest,destPos,length
            System.arraycopy(origItems, 0, newItems, 0, idx);
        }

        // Insert the new items
        //               src,      srcPos,     dest, destPos, length
        System.arraycopy(insertedItems, 0, newItems, idx, insertedItems.length);

        // If we aren't inserting at the last item, array-copy the items after the insert
        // point.
        if (idx < origItems.length) {
            System.arraycopy(origItems, idx, newItems, idx + insertedItems.length,
                             origItems.length - idx);
        }
        return newItems;
    }

//    private static int[] replaceInIntArrayAt(int replacedItem, int[] origItems, int idx) {
//        // Make an array that big enough.  It's too bad that the JVM bothers to
//        // initialize this with nulls.
//        int[] newItems = new int[origItems.length];
//        System.arraycopy(origItems, 0, newItems, 0, origItems.length);
//        newItems[idx] = replacedItem;
//        return newItems;
//    }

    @SuppressWarnings("unchecked")
    static <T> T[] replaceInArrayAt(T replacedItem, T[] origItems, int idx,
                                            Class<T> tClass) {
        // Make an array that big enough.  It's too bad that the JVM bothers to
        // initialize this with nulls.
        T[] newItems = (T[]) ( (tClass == null) ? new Object[origItems.length]
                                                : Array.newInstance(tClass, origItems.length) );
        System.arraycopy(origItems, 0, newItems, 0, origItems.length);
        newItems[idx] = replacedItem;
        return newItems;
    }

    /**
     Only call this if the array actually needs to be split (0 &lt; splitPoint &lt; orig.length).
     @param orig array to split
     @param splitIndex items less than this index go in the left, equal or greater in the right.
     @return a 2D array of leftItems then rightItems
     */
    static <T> Tuple2<T[],T[]> splitArray(T[] orig, int splitIndex) { //, Class<T> tClass) {
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
        return Tuple2.of(Arrays.copyOf(orig, splitIndex),
                         Arrays.copyOfRange(orig, splitIndex, orig.length));

//        // original array, offset, newArray, offset, length
//        System.arraycopy(orig, 0, split._1(), 0, splitIndex);
//
//        System.arraycopy(orig, splitIndex, split._2(), 0, rightLength);
//        return split;
    }

    /**
     Only call this if the array actually needs to be split (0 &lt; splitPoint &lt; orig.length).
     @param orig array to split
     @param splitIndex items less than this index go in the left, equal or greater in the right.
     @return a 2D array of leftItems then rightItems
     */
    static int[][] splitArray(int[] orig, int splitIndex) {
        // This function started an exact duplicate of the one above, but for ints.
//        if (splitIndex < 1) {
//            throw new IllegalArgumentException("Called split when splitIndex < 1");
//        }
//        if (splitIndex > orig.length - 1) {
//            throw new IllegalArgumentException("Called split when splitIndex > orig.length - 1");
//        }
        int rightLength = orig.length - splitIndex;
        int[][] split = new int[][] {new int[splitIndex],
                                     new int[rightLength]};
        // original array, offset, newArray, offset, length
        System.arraycopy(orig, 0, split[0], 0, splitIndex);
        System.arraycopy(orig, splitIndex, split[1], 0, rightLength);
        return split;
    }

    static <T> T[] truncateArray(T[] origItems, int newLength, Class<T> tClass) {
        if (origItems.length == newLength) {
            return origItems;
        }

        @SuppressWarnings("unchecked")
        T[] newItems = (T[]) ((tClass == null) ? new Object[newLength]
                                               : Array.newInstance(tClass, newLength) );

        //                      src, srcPos,    dest,destPos, length
        System.arraycopy(origItems, 0, newItems, 0, newLength);
        return newItems;
    }
}
