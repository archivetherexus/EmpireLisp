package EmpireLisp;

/**
 * The nil value as an Expression.
 *
 * @author Tyrerexus
 * @date 11/28/17
 */
@SuppressWarnings("JavaDoc")
public class ExpressionNil extends ExpressionPair {

    public ExpressionNil() {
        super(null, null);
    }

    @Override
    public String toString() {
        return "nil";
    }

    @Override
    public boolean equals(Expression other) {
        return other instanceof ExpressionNil;
    }

    @Override
    public void eval(IEvaluator evaluator, Environment environment, IEvalCallback callback) throws LispException {
        callback.evalCallback(this);
    }
}
