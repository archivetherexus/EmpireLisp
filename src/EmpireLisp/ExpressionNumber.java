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
    public boolean equals(Expression other) {
        if (other instanceof ExpressionNumber) {
            ExpressionNumber otherNumber = (ExpressionNumber) other;
            return otherNumber.number == this.number;
        }
        else {
            return false;
        }
    }

    @Override
    public void eval(Environment environment, IEvalCallback callback) throws LispException {
        callback.evalCallback(this);
    }
}
