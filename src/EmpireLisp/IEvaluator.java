package EmpireLisp;

/**
 * The evaluator provides an interface to the outer world.
 * Currently the only task of an IEvaluator is to answer if the evaluation should continue,
 * and to stash evaluation for later continuation.
 *
 * @author Tyrerexus
 * @date 12/4/17
 */
public interface IEvaluator {

    /**
     * This method is usually called by an eval() method.
     * The eval() method will cal stashEvaluation() if continueEvaluation() returns false.
     *
     * @return Return false if you wish to stash the evaluation.
     */
    boolean continueEvaluation();

    /**
     * Stash away the expression, the environment and the callback so that you can later continue the evaluation
     * if you wish to do so.
     *
     * @param expression  The expression which was being evaluated.
     * @param environment The environment in which the expression was being evaluated.
     * @param callback    The callback that would be called if the expression would be evaluated.
     */
    void stashEvaluation(Expression expression, Environment environment, IEvalCallback callback);
}
