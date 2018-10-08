package org.organicdesign.fp.collections

/** An unmodifiable ListIterator  */
//@PurelyImplements("java.util.ListIterator")
interface UnmodListIterator<E> : ListIterator<E>, UnmodSortedIterator<E> {


    // boolean	hasNext()
    // boolean	hasPrevious()
    // E	next()
    // int	nextIndex()
    // E	previous()

    // I think this is the only valid implementation of this method. You can override it if you
    // think otherwise.
    @JvmDefault
    override fun previousIndex(): Int = nextIndex() - 1

    // Methods inherited from interface java.util.Iterator
    // forEachRemaining
}
