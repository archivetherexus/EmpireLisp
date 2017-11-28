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
    public void eval(Environment environment, IEvalCallback callback) throws LispException {
        callback.evalCallback(this);
    }

    @Override
    public boolean equals(Expression other) {
        return other == this;
    }
}
