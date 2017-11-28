package EmpireLisp;

/**
 * @author Tyrerexus
 * @date 11/20/17
 */
@SuppressWarnings("JavaDoc")
public abstract class Expression {
    public abstract String toString();

    public abstract boolean equals(Expression other);

    public abstract void eval(Environment environment, IEvalCallback callback) throws LispException;

    @SuppressWarnings("WeakerAccess")
    public boolean isTrue() {
        return this instanceof ExpressionNumber && ((ExpressionNumber) this).number == 1;
    }

    public boolean isNil() {
        return this instanceof ExpressionNil;
    }
}
