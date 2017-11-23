package EmpireLisp;

import java.util.ArrayList;
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
        }
        else {
            return "()";
        }
    }

    @Override
    public boolean equals(Expression other) {
        if (other instanceof ExpressionPair) {
            ExpressionPair otherPair = (ExpressionPair) other;
            if (this.left == null && this.right == null) {
                return otherPair.left == null && otherPair.right == null;
            }
            else if (this.left == null) {
                return otherPair.left == null && this.right.equals(otherPair.right);
            }
            else if (this.right == null) {
                return this.left.equals(otherPair.left) && otherPair.right == null;
            }
            else {
                return this.left.equals(otherPair.left) && this.right.equals(otherPair.right);
            }
        }
        else {
            return false;
        }
    }

    @Override
    public Expression eval(Environment environment) throws LispException {
        if (this.right instanceof ExpressionPair) {
            Expression fun = this.left.eval(environment);
            ExpressionPair result;
            if (fun instanceof IApplicable) {
                if (!((IApplicable)fun).isLazyEval()) {
                    ExpressionPair list = (ExpressionPair) this.right;
                    result = new ExpressionPair(null, null);
                    ExpressionPair head = result;
                    while (list.left != null) {
                        head.left = list.left.eval(environment);
                        head.right = new ExpressionPair(null, null);
                        head = (ExpressionPair) head.right;

                        if (list.right instanceof ExpressionPair) {
                            list = (ExpressionPair) list.right;
                        } else {
                            break;
                        }
                    }
                }
                else {
                    result = (ExpressionPair) this.right;
                }

                return ((IApplicable)fun).apply(environment, result);
            }
            else {
                throw new LispException(LispException.ErrorType.NOT_APPLICABLE,"List is not applicable");
            }
        }
        else {
            return this;
        }
    }

    @SuppressWarnings("WeakerAccess")
    public List<Expression> toList() {
        List<Expression> result = new ArrayList<>();
        ExpressionPair list = this;
        while(list.left != null) {
            result.add(list.left);

            if (list.right instanceof  ExpressionPair) {
                list = (ExpressionPair) list.right;
            }
            else {
                break;
            }
        }
        return result;
    }
}
