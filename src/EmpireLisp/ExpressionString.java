package EmpireLisp;

/**
 * @author Tyrerexus
 * @date 11/24/17
 */
@SuppressWarnings("JavaDoc")
public class ExpressionString extends Expression implements ISequence {

    public String string;

    public ExpressionString(String string) {
        this.string = string;
    }

    @Override
    public String toString() {
        return '"' + string + '"';
    }

    @Override
    public boolean equals(Expression other) {
        if (other instanceof ExpressionString) {
            ExpressionString otherString = (ExpressionString) other;

            return string.equals(otherString.string);
        }
        else {
            return false;
        }
    }

    @Override
    public Expression eval(Environment environment) throws LispException {
        return this;
    }

    @Override
    public ExpressionNumber getLength() {
        return new ExpressionNumber(string.length()); // FIXME: Optimise this. We don't need a new copy each time!
    }

    @Override
    public Expression atIndex(ExpressionNumber index) {
        return new ExpressionNumber(string.charAt((int) index.number));
    }

    @Override
    public ISequence concatenate(ISequence other) {
        if (other instanceof ExpressionString) {
            ExpressionString otherString = (ExpressionString) other;

            return new ExpressionString(string + otherString.string);
        }
        else {
            throw new RuntimeException("ERROR: What to do?"); // TODO: Find the best solution. i.e returning a concatenation object for instance.
        }
    }
}
