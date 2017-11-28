package EmpireLisp;

/**
 * @author Tyrerexus
 * @date 11/28/17
 */
@SuppressWarnings("JavaDoc")
public class ExpressionNil extends Expression {
    @Override
    public String toString() {
        return "nil";
    }

    @Override
    public boolean equals(Expression other) {
        return other instanceof ExpressionNil;
    }

    @Override
    public void eval(Environment environment, IEvalCallback callback) throws LispException {
        callback.evalCallback(this);
    }
}
