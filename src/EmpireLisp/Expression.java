package EmpireLisp;

import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;

/**
 * The base class for all the values in this interpreter.
 * <p>
 * TODO: Fix the ExpressionPair.toString infinite recursion bug. Where the this.cdr = this
 * TODO: Fix a way to serialize all expressions. Keep in mind that references must also be serializable...
 *
 * @author Tyrerexus
 * @date 11/20/17
 */
@SuppressWarnings("JavaDoc")
public abstract class Expression {

    private static long ID_NUMBER = 1;

    @SuppressWarnings("WeakerAccess")
    public static final String ID_NUMBER_PREFIX = "_~";

    Expression() {
        expressionID = ID_NUMBER++;
    }

    long expressionID;

    /**
     * A helper function for serializeExpression. This function will generate define expressions.
     * However it will not do anything if the ID of the expression is already in the completedIDs.
     * On success, the ID of the expression is added to the completedIDs list.
     * Example: (define _~ID the_value)
     * @param completedIDs A list that keeps track of what IDs have been serialized.
     * @param to The destination to write to.
     * @param value The value of this registration. I.e. This expression serialized.
     * @throws IOException
     */
    void registerSelf(HashSet<Long> completedIDs, Writer to, String value) throws IOException {
        if (!completedIDs.contains(expressionID)) {
            to.write("(define " + ID_NUMBER_PREFIX + expressionID + " " + value + ")\n");
            completedIDs.add(expressionID);
        }
    }

    /**
     * Serializes the expression to real code.
     * @param output The code produced.
     * @throws IOException If any error occurred while trying to convert the expression to code.
     */
    public abstract void serializeCode(Writer output) throws IOException;

    /**
     * Serializes the expression to the output.
     * Example: (define _~ID the_value)
     *
     * @param output Where to write the expression.
     */
    public abstract void serializeExpression(HashSet<Long> completedIDs, Writer output) throws IOException;

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
    @SuppressWarnings("WeakerAccess")
    public boolean isNil() {
        return this instanceof ExpressionNil;
    }
}
