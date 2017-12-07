package EmpireLisp;

/**
 * The base class for all the values in this interpreter.
 *
 * @author Tyrerexus
 * @date 11/20/17
 */
@SuppressWarnings("JavaDoc")
public abstract class Expression {

    /**
     * Converts the Expression into its string representation.
     *
     * @return The string representation.
     */
    public abstract String toString();

    /**
     * Returns true if the other Expression is equal to this Expression.
     *
     * @param other The Expression to check against.
     * @return True if other's value is equal to this Expressions' value.
     */
    public abstract boolean equals(Expression other);

    /**
     * Evaluates the expression.
     *
     * @param evaluator   The evaluator. Mainly used to stash away values if necessary.
     * @param environment The environment in which the expression is evaluated in.
     * @param callback    The IEvalCallback to call when the evaluation is complete.
     * @throws LispException If an error occurred.
     */
    public abstract void eval(IEvaluator evaluator, Environment environment, IEvalCallback callback) throws LispException;

    /**
     * Returns true if the Expression has the value of the Expression "true".
     *
     * @return Returns true if the Expression has the value of the Expression "true".
     */
    @SuppressWarnings("WeakerAccess")
    public boolean isTrue() {
        return this instanceof ExpressionNumber && ((ExpressionNumber) this).number == 1;
    }

    /**
     * Returns true if the Expression has the value of the Expression "nil".
     *
     * @return Returns true if the Expression has the value of the Expression "nil".
     */
    public boolean isNil() {
        return this instanceof ExpressionNil;
    }
}
