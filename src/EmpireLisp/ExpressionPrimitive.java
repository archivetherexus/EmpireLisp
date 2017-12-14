package EmpireLisp;

import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;

/**
 * A primitive-procedure as an Expression.
 *
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
    public void eval(IEvaluator evaluator, Environment environment, IEvalCallback callback) throws LispException {
        callback.evalCallback(this);
    }

    @Override
    public boolean equals(Expression other) {
        return other == this;
    }

    @SuppressWarnings("WeakerAccess")
    public ExpressionPrimitive() {
        expressionID = 0;
    }

    @Override
    public void serializeExpression(HashSet<Long> completedIDs, Writer output) throws IOException {
        registerSelf(completedIDs, output, toString());
    }

    @Override
    public void serializeCode(Writer output) throws IOException {
        output.write(toString());
    }
}
