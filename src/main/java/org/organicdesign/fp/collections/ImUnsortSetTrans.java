package org.organicdesign.fp.collections;

/**
 Declare your set as ImUnsortSetTrans, call asTransient(), build it, then call
 mySet = mySet.persistent() without having to declare a new variable.
 */
public interface ImUnsortSetTrans<E> extends ImUnsortSet<E> {

    /** {@inheritDoc} */
    @Override
    ImUnsortSetTrans<E> put(E val);

    /** {@inheritDoc} */
    @Override
    ImUnsortSetTrans<E> without(E key);

    /**
     Returns a persistent/immutable version of this transient set.
     Once you call persist() on it, you can't mutate the transient one anymore (or you'll get an
     exception).  This is because they share most of the underlying implementation.
     */
    ImUnsortSet<E> persistent();

}
