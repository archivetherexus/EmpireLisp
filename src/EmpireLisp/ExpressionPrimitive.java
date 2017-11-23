package EmpireLisp;

/**
 * @author Tyrerexus
 * @date 11/20/17
 */
@SuppressWarnings("JavaDoc")
public abstract class ExpressionPrimitive extends Expression implements IApplicable {
    @Override
    public String toString() {
        return "primitive-operator";
    }

    @Override
    public Expression eval(Environment environment) {
        return this;
    }
}
