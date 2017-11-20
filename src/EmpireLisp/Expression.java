package EmpireLisp;

/**
 * @author Tyrerexus
 * @date 11/20/17
 */
public abstract class Expression {
    public abstract String toString();
    public abstract Expression eval(Environment environment);
}
