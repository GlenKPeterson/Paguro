package org.organicdesign.fp.collections;

import org.organicdesign.fp.function.Function1;
import org.organicdesign.fp.function.Function2;
import org.organicdesign.fp.xform.Transformable;
import org.organicdesign.fp.xform.Xform;

import java.util.Iterator;

/** An unmodifiable Iterable, without any guarantee about order. */
public interface UnmodIterable<T> extends Iterable<T>, Transformable<T> {
    // ==================================================== Static ====================================================

    // This hides the same method on all sub-interfaces!
//    static <E> UnmodIterable<E> empty() { return () -> UnmodIterator.empty(); }

    //    /**
//     Caution: this is a convenient optimization for immutable data structures and a nightmare waiting to happen to
//     mutable ones.  Don't go slapping this on immutable wrappers for mutable data.  If all the underlying
//     data is truly immutable, this allows you to compute the hashCode the first time it is needed, then return
//     that same code without re-computing it again.  It's the internal version of a memoizer.  Also, use this only
//     for decent sized collections.  If you only have 2 or 3 fields, this isn't buying you anything.
//     */
//    static Lazy.Int lazyHashCode(UnmodIterable iter) {
//        if (iter == null) { throw new IllegalArgumentException("Can't have a null iterable."); }
//        return Lazy.Int.of(() -> UnmodIterable.hashCode(iter));
//    }
//
//    /**
//     Caution: this is a convenient optimization for immutable data structures and a nightmare waiting to happen to
//     mutable ones.  Don't go slapping this on immutable wrappers for mutable data structures.  If all the underlying
//     data is truly immutable, this allows you to compute a reasonable toString() the first time it is needed, then
//     return that same String without re-computing it again.  It's the internal version of a memoizer.
//     */
//    static LazyRef<String> lazyToString(String name, UnmodIterable iter) {
//        if (name == null) { throw new IllegalArgumentException("Can't have a null name."); }
//        if (iter == null) { throw new IllegalArgumentException("Can't have a null iterable."); }
//        return LazyRef.of(() -> UnmodIterable.toString(name, iter));
//    }

//    /** Lets underlying compareTo method handle comparing nulls to non-null values. */
//    static <E extends Comparable<E>> int compareHelper(E e1, E e2) {
//        if (e1 == e2) { return 0; }
//        if (e1 == null) {
//            //noinspection ConstantConditions
//            return -e2.compareTo(e1);
//        }
//        return e1.compareTo(e2);
//    }
//
//    /** A default comparator for UnIterables comparable */
//    static <F extends Comparable<F>,E extends UnmodIterable<F>> Comparator<E> iterableComparator() {
//        return new Comparator<E>() {
//            @Override
//            public int compare(E o1, E o2) {
//                if (o1 == null) {
//                    if (o2 == null) {
//                        return 0;
//                    } else {
//                        //noinspection ConstantConditions
//                        return -compare(o2, o1);
//                    }
//                }
//                UnmodIterator<F> as = o1.iterator();
//                UnmodIterator<F> bs = o2.iterator();
//                while (as.hasNext() && bs.hasNext()) {
//                    int ret = compareHelper(as.next(), bs.next());
//                    if (ret != 0) {
//                        return ret;
//                    }
//                }
//                // If we run out of items in one, the longer one is considered greater, just like ordering words in a
//                // dictionary.
//                if (as.hasNext()) { return -1; }
//                if (bs.hasNext()) { return 1; }
//                // All items compare 0 and same number of items - these are sorted the same (and probably equal)
//                return 0;
//            }
//        };
//    }

    /**
     This is correct, but O(n).
     It also works regardless of the order of the items because a + b = b + a, even when an overflow occurs.
     */
    static int hashCode(Iterable is) {
        if (is == null) { throw new IllegalArgumentException("Can't have a null iteratable."); }
//        System.out.println("hashCode for: " + is);
        int ret = 0;
        for (Object t : is) {
            if (t != null) {
//                System.out.println("\tt: " + t + " hashCode: " + t.hashCode());
                ret = ret + t.hashCode();
            }
        }
        return ret;
    }

    /** Computes a reasonable to-string. */
    static String toString(String name, Iterable iterable) {
        if (name == null) { throw new IllegalArgumentException("Can't have a null name."); }
        if (iterable == null) { throw new IllegalArgumentException("Can't have a null iteratable."); }
        StringBuilder sB = new StringBuilder();
        sB.append(name).append("(");
        int i = 0;
        Iterator iter = iterable.iterator();
        while (iter.hasNext()) {
            Object item = iter.next();
            if (i > 0) { sB.append(","); }
            if (i > 4) { break; }
            sB.append(item);
            i++;
        }
        if (iter.hasNext()) {
            sB.append("...");
        }
        return sB.append(")").toString();
    }

    // ================================== Inherited from Iterable ==================================
    /** {@inheritDoc} */
    @Override UnmodIterator<T> iterator();

    // =============================== Inherited from Transformable ===============================

    /** {@inheritDoc} */
    @Override default Transformable<T> concat(Iterable<? extends T> list) {
        return Xform.of(this).concat(list);
    }

    /** {@inheritDoc} */
    @Override default Transformable<T> precat(Iterable<? extends T> list) {
        return Xform.of(this).precat(list);
    }

    /** {@inheritDoc} */
    @Override default Transformable<T> drop(long n) {
        return Xform.of(this).drop(n);
    }

    /** {@inheritDoc} */
    @Override default <B> B foldLeft(B ident, Function2<B,? super T,B> reducer) {
        return Xform.of(this).foldLeft(ident, reducer);
    }

    /** {@inheritDoc} */
    @Override default <B> B foldLeft(B ident, Function2<B,? super T,B> reducer,
                                    Function1<? super B,Boolean> terminateWhen) {
        return Xform.of(this).foldLeft(ident, reducer, terminateWhen);
    }

    /** {@inheritDoc} */
    @Override default Transformable<T> filter(Function1<? super T,Boolean> f) {
        return Xform.of(this).filter(f);
    }

    /** {@inheritDoc} */
    @Override default <B> Transformable<B> flatMap(Function1<? super T,Iterable<B>> f) {
        return Xform.of(this).flatMap(f);
    }

    /** {@inheritDoc} */
    @Override default <B> Transformable<B> map(Function1<? super T, ? extends B> f) {
        return Xform.of(this).map(f);
    }

    /** {@inheritDoc} */
    @Override default Transformable<T> take(long numItems) {
        return Xform.of(this).take(numItems);
    }

    /** {@inheritDoc} */
    @Override default Transformable<T> takeWhile(Function1<? super T,Boolean> f) {
        return Xform.of(this).takeWhile(f);
    }
}
