package org.organicdesign.fp.collections

/** This represents an iterator with a guaranteed ordering.  */
interface UnmodSortedIterator<E> : UnmodIterator<E> {
    companion object {
        class Wrapper<E> internal constructor(
                //        , Serializable {
                //        // For serializable.  Make sure to change whenever internal data format changes.
                //        private static final long serialVersionUID = 20160903174100L;

                private val iter: Iterator<E>) : UnmodSortedIterator<E> {

            override fun hasNext(): Boolean {
                return iter.hasNext()
            }

            override fun next(): E {
                return iter.next()
            }
        }
    }
}
