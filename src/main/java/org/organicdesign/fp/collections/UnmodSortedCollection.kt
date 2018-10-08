package org.organicdesign.fp.collections

import java.util.Spliterator
import java.util.Spliterators

interface UnmodSortedCollection<E> : UnmodCollection<E>, UnmodSortedIterable<E> {

    /** An unmodifiable ordered iterator {@inheritDoc}  */
    @JvmDefault
    override fun iterator(): UnmodSortedIterator<E>

    // Overrides kotlin.collections.Collection.size
    override val size: kotlin.Int

    /**
     * Overridden to avoid inheriting unrelated defaults between java.util.Collections and
     * kotlin.collections.Iterable. Copied implementation from Collections.spliterator() because
     * for a Collection the size is known.
     */
    @JvmDefault
    override fun spliterator(): Spliterator<E> =
            Spliterators.spliterator(this, Spliterator.SIZED or Spliterator.ORDERED)
}
