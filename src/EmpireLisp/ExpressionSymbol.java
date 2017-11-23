package EmpireLisp;

/**
 * @author Tyrerexus
 * @date 11/20/17
 */
@SuppressWarnings("JavaDoc")
public class ExpressionSymbol extends Expression {

    @SuppressWarnings("WeakerAccess")
    public String symbol;

    @SuppressWarnings("WeakerAccess")
    public ExpressionSymbol(String symbol) {
        this.symbol = symbol;
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
        }
        else {
            return false;
        }
    }

    @Override
    public Expression eval(Environment environment) throws LispException {
        return environment.getVariable(symbol);
    }
}
