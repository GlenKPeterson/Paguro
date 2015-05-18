package org.organicdesign.fp.experiments;

import java.util.Comparator;

import org.organicdesign.fp.tuple.Tuple2;

// TODO: This isn't anything yet.  Just an implementation of an immutable Red-Black tree, to try to make work like an RRB-Tree.
public class InsertableVector<E> {

    private static final class Array<T> {
        int size;
        Object[] items;
        private Array(int length) {
            items = new Object[length];
        }
    }

    private final Comparator comp;
    // So the Key here is a Long which is just the index into the list.
    // The value is a tuple of the item itself, plus the size.
    private final OkasakiRedBlackTree<Long,Tuple2<Array<E>,Integer>> tree;
    private InsertableVector(Comparator<? super E> c, OkasakiRedBlackTree<Long,Tuple2<Array<E>,Integer>>  t) {
        comp = c; tree = t;
    }



}
