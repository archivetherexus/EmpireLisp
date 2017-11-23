package EmpireLisp;

/**
 * @author Tyrerexus
 * @date 11/20/17
 */
@SuppressWarnings("JavaDoc")
public class ExpressionNumber extends Expression {

    @SuppressWarnings("WeakerAccess")
    public double number;

    @SuppressWarnings("WeakerAccess")
    public ExpressionNumber(double number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return Double.toString(number);
    }

    @Override
    public Expression eval(Environment environment) {
        return this;
    }
}
