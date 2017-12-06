package EmpireLisp;

import java.util.Iterator;

/**
 * @author Tyrerexus
 * @date 11/24/17
 */
@SuppressWarnings("JavaDoc")
public interface ISequence {
    ExpressionNumber getLength();

    Expression atIndex(ExpressionNumber index) throws LispException;

    ISequence concatenate(ISequence other);

    Iterator<Expression> iterator();
}
