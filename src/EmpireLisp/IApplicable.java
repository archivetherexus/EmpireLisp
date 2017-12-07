package EmpireLisp;

/**
 * Any object that implements this interface can be applied to an argument inside of an evaluation.
 *
 * @author Tyrerexus
 * @date 11/20/17
 */
@SuppressWarnings("JavaDoc")
public interface IApplicable {

    /**
     * Applies the object to an argument / some arguments.
     * @param evaluator The evaluator. Mainly used to stash away values if necessary.
     * @param environment The environment in which the expression is evaluated in.
     * @param arguments The argument / arguments to apply to this object.
     * @param callback The IEvalCallback to call when the evaluation is complete.
     * @throws LispException If an error occurred.
     */
    void apply(IEvaluator evaluator, Environment environment, ExpressionPair arguments, IEvalCallback callback) throws LispException;
}
