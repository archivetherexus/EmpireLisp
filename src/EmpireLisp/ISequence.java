package EmpireLisp;

import java.util.Iterator;

/**
 * Any object that implements this interface can be used by the standard sequence operators, such as:
 * map, filter, for-each, length, at, concat, etc.
 *
 * @author Tyrerexus
 * @date 11/24/17
 */
@SuppressWarnings("JavaDoc")
public interface ISequence {

    /**
     * Get the current length of the sequence.
     *
     * @return
     */
    ExpressionNumber getLength();

    /**
     * Get the value at the index from this sequence.
     *
     * @param index The index to use.
     * @return The value found.
     * @throws LispException May throw an "out-of-bounds" error, as well as similar errors.
     */
    Expression atIndex(ExpressionNumber index) throws LispException;

    /**
     * Concatenates another sequence with this sequence.
     *
     * @param other The other sequence to concatenate with.
     * @return The concatenated sequence which is the result of concatenating the old sequence with the other sequence.
     */
    ISequence concatenate(ISequence other);

    /**
     * Gets an iterator for this sequence. This is mainly used in the implementation of the standard sequence operators.
     *
     * @return The iterator for this sequence.
     */
    Iterator<Expression> iterator();
}
