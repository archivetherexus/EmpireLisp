package EmpireLisp;

import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;

/**
 * A symbol as an Expression.
 *
 * @author Tyrerexus
 * @date 11/20/17
 */
@SuppressWarnings("JavaDoc")
public class ExpressionSymbol extends Expression {

    @SuppressWarnings("WeakerAccess")
    public String symbol;

    @SuppressWarnings("WeakerAccess")
    public ExpressionSymbol(String symbol) {
        super();

        this.symbol = symbol.toLowerCase();
    }

    @Override
    public void serializeCode(Writer output) throws IOException {
        output.write(symbol);
    }

    @Override
    public void serializeExpression(HashSet<Long> completedIDs, Writer output) throws IOException {
        registerSelf(completedIDs, output, "(quote " + symbol + ")");
    }

    @Override
    public String toString() {
        return symbol;
    }

    @Override
    public boolean equals(Expression other) {
        if (other instanceof ExpressionSymbol) {
            ExpressionSymbol otherSymbol = (ExpressionSymbol) other;

            return otherSymbol.symbol.equals(this.symbol);
        } else {
            return false;
        }
    }

    @Override
    public void eval(IEvaluator evaluator, Environment environment, IEvalCallback callback) throws LispException {
        callback.evalCallback(environment.getVariable(symbol));
    }
}
