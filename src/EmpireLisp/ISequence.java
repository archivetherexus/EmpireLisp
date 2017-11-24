package EmpireLisp;

/**
 * @author Tyrerexus
 * @date 11/24/17
 */
@SuppressWarnings("JavaDoc")
public interface ISequence {
    ExpressionNumber getLength();
    Expression atIndex(ExpressionNumber index);
    ISequence concatenate(ISequence other);
}
