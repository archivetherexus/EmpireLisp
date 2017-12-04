package EmpireLisp;

/**
 * @author Tyrerexus
 * @date 12/4/17
 */
public interface IEvaluator {
    boolean continueEvaluation();
    void stashEvaluation(Expression expression, Environment environment, IEvalCallback callback);
}
