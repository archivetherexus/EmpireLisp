package EmpireLisp;

/**
 * @author Tyrerexus
 * @date 11/20/17
 */
public interface IApplyable {
    Expression apply(Environment environment, Expression arguments);
    boolean isLazyEval();
}
