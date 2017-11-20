package EmpireLisp;

/**
 * @author Tyrerexus
 * @date 11/20/17
 */
public class ExpressionSymbol extends Expression {
    public String symbol;

    public ExpressionSymbol(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return symbol;
    }
}
