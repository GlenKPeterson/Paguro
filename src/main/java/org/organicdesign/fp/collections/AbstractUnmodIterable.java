package org.organicdesign.fp.collections;

import com.planbase.indented.IndentedStringable;
import org.jetbrains.annotations.NotNull;

import static com.planbase.indented.StringUtils.iterableToStr;

/**
 Implements equals and hashCode() methods compatible with all java.util collections (this
 algorithm is not order-dependent) and toString which takes the name of the sub-class.
 */
public abstract class AbstractUnmodIterable<T> implements IndentedStringable, UnmodIterable<T> {
    @Override public int hashCode() { return UnmodIterable.hash(this); }

    @Override
    public @NotNull String indentedStr(int indent, boolean singleLine) {
        return iterableToStr(indent, getClass().getSimpleName(), this, false, singleLine);
    }

    @Override
    public @NotNull String toString() {
        return indentedStr(0, true);
    }
}
