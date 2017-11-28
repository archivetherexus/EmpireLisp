package EmpireLisp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Tyrerexus
 * @date 11/20/17
 */
@SuppressWarnings("JavaDoc")
public class ExpressionPair extends Expression {
    @SuppressWarnings("WeakerAccess")
    public Expression left;

    @SuppressWarnings("WeakerAccess")
    public Expression right;

    @SuppressWarnings("WeakerAccess")
    public ExpressionPair(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public String toString() {
        if (left != null || right != null) {
            return "(" + (left != null ? left.toString() : "") + " . " + (right != null ? right.toString() : "") + ")";
        } else {
            return "()";
        }
    }

    @Override
    public boolean equals(Expression other) {
        if (other instanceof ExpressionPair) {
            ExpressionPair otherPair = (ExpressionPair) other;
            if (this.left == null && this.right == null) {
                return otherPair.left == null && otherPair.right == null;
            } else if (this.left == null) {
                return otherPair.left == null && this.right.equals(otherPair.right);
            } else if (this.right == null) {
                return this.left.equals(otherPair.left) && otherPair.right == null;
            } else {
                return this.left.equals(otherPair.left) && this.right.equals(otherPair.right);
            }
        } else {
            return false;
        }
    }

    @SuppressWarnings("Convert2Lambda")
    @Override
    public void eval(Environment environment, IEvalCallback callback) throws LispException {
        Expression operator = this.left;
        Expression operand = this.right;

        // Evaluate the operator. //
        operator.eval(environment, new IEvalCallback() {
            @Override
            public void evalCallback(Expression operator) throws LispException {
                if (operator instanceof IApplicable) {
                    ((IApplicable) operator).apply(environment, operand, callback);
                } else {
                    throw new LispException(LispException.ErrorType.NOT_APPLICABLE, "\"" + operator.toString() + "\" is not applicable");
                }
            }
        });
    }

    @SuppressWarnings("WeakerAccess")
    public List<Expression> toList() {
        List<Expression> result = new ArrayList<>();
        ExpressionPair list = this;
        while (list.left != null) {
            result.add(list.left);

            if (list.right instanceof ExpressionPair) {
                list = (ExpressionPair) list.right;
            } else {
                break;
            }
        }
        return result;
    }

    @SuppressWarnings("WeakerAccess")
    public Iterator<Expression> iterator() {
        ExpressionPair pair = this;
        class Head {
            ExpressionPair value = pair;
            boolean end = false;
        }
        Head head = new Head();
        return new Iterator<Expression>() {
            @Override
            public boolean hasNext() {
                return !head.end;
            }

            @Override
            public Expression next() {
                Expression value = head.value.left;
                if (head.value.right instanceof ExpressionPair) {
                    head.value = (ExpressionPair) head.value.right;
                    return value;
                } else if (head.value.right.isNil()) {
                    head.end = true;
                    return value;
                } else {
                    throw new RuntimeException("Invalid list structure!");
                }
            }
        };
    }
}
