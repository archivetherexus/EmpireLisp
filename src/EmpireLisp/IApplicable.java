package EmpireLisp;

/**
 * @author Tyrerexus
 * @date 11/20/17
 */
@SuppressWarnings("JavaDoc")
public interface IApplicable {
    void apply(IEvaluator evaluator, Environment environment, ExpressionPair arguments, IEvalCallback callback) throws LispException;
}
