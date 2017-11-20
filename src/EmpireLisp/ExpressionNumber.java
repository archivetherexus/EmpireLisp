package EmpireLisp;

/**
 * @author Tyrerexus
 * @date 11/20/17
 */
public class ExpressionNumber extends Expression {

    public double number;

    public ExpressionNumber(double number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return Double.toString(number);
    }
}
