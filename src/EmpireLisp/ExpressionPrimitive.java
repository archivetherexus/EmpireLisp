package EmpireLisp;

/**
 * @author Tyrerexus
 * @date 11/20/17
 */
public abstract class ExpressionPrimitive extends Expression implements IApplyable {
    @Override
    public String toString() {
        return "primitive-operator";
    }

    @Override
    public Expression eval(Environment environment) {
        return this;
    }
}
