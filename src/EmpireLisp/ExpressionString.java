package EmpireLisp;

import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.Iterator;

/**
 * A string value as an Expression.
 *
 * @author Tyrerexus
 * @date 11/24/17
 */
@SuppressWarnings("JavaDoc")
public class ExpressionString extends Expression implements ISequence {

    public String string;
    private ExpressionNumber length;

    @SuppressWarnings("WeakerAccess")
    public ExpressionString(String string) {
        super();

        this.string = string;
        length = new ExpressionNumber(string.length());
    }

    @Override
    public void serializeCode(Writer output) throws IOException {
        output.write('"');
        output.write(string);
        output.write('"');
    }

    @Override
    public void serializeExpression(HashSet<Long> completedIDs, Writer output) throws IOException {
        registerSelf(completedIDs, output, "\"" + string + "\"");
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
        } else {
            return false;
        }
    }

    @Override
    public void eval(IEvaluator evaluator, Environment environment, IEvalCallback callback) throws LispException {
        callback.evalCallback(this);
    }

    @Override
    public ExpressionNumber getLength() {
        return length;
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
        } else {
            throw new RuntimeException("ERROR: What to do?"); // TODO: Find the best solution. i.e returning a concatenation object for instance.
        }
    }

    @Override
    public Iterator<Expression> iterator() {
        return new Iterator<Expression>() {
            int position = 0;

            @Override
            public boolean hasNext() {
                return position < string.length();
            }

            @Override
            public Expression next() {
                return new ExpressionNumber(string.charAt(position++));
            }
        };
    }
}
