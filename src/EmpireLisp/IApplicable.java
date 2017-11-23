package EmpireLisp;

/**
 * @author Tyrerexus
 * @date 11/20/17
 */
@SuppressWarnings("JavaDoc")
public interface IApplicable {
    Expression apply(Environment environment, Expression arguments) throws LispException;
    boolean isLazyEval();
}