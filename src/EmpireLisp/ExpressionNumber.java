package EmpireLisp;

import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;

/**
 * A number value as an Expression.
 *
 * @author Tyrerexus
 * @date 11/20/17
 */
@SuppressWarnings("JavaDoc")
public class ExpressionNumber extends Expression {

    @SuppressWarnings("WeakerAccess")
    public double number;

    @SuppressWarnings("WeakerAccess")
    public ExpressionNumber(double number) {
        super();

        this.number = number;
    }

    @Override
    public void serializeCode(Writer output) throws IOException {
        output.write(Double.toString(number));
    }

    @Override
    public void serializeExpression(HashSet<Long> completedIDs, Writer output) throws IOException {
        registerSelf(completedIDs, output, Double.toString(number));
    }

    @Override
    public String toString() {
        return Double.toString(number);
    }

    @Override
    public boolean equals(Expression other) {
        if (other instanceof ExpressionNumber) {
            ExpressionNumber otherNumber = (ExpressionNumber) other;
            return otherNumber.number == this.number;
        } else {
            return false;
        }
    }

    @Override
    public void eval(IEvaluator evaluator, Environment environment, IEvalCallback callback) throws LispException {
        callback.evalCallback(this);
    }
}
